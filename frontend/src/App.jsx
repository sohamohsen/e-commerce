import { useEffect, useMemo, useState } from 'react'
import {
  getToken,
  readRole,
  readUsername,
  authApi,
  productApi,
  categoryApi,
  cartApi,
  orderApi,
  paymentApi,
  notificationApi,
  adminProductApi,
  adminCategoryApi,
  adminOrderApi,
  superAdminApi,
  adminAccountApi,
  chatApi,
} from './api'

const currency = new Intl.NumberFormat('en-EG', { style: 'currency', currency: 'EGP' })

const statusLabels = {
  PENDING: 'Pending Payment',
  PENDING_PAYMENT: 'Processing Payment',
  PAID: 'Paid',
  PREPARED: 'Preparing',
  SHIPPED: 'Shipped',
  DELIVERED: 'Delivered',
  FAILED: 'Payment Failed',
  CANCELLED: 'Cancelled',
  CANCELLATION_REQUESTED: 'Cancellation Requested',
}

function App() {
  const [token, setToken] = useState(() => getToken())
  const [role, setRole] = useState(() => readRole(getToken() || ''))
  const [username, setUsername] = useState(() => readUsername(getToken() || ''))
  const [page, setPage] = useState('shop')

  const [products, setProducts] = useState([])
  const [totalProducts, setTotalProducts] = useState(0)
  const [categories, setCategories] = useState([])
  const [cart, setCart] = useState(null)
  const [orders, setOrders] = useState([])
  const [notifications, setNotifications] = useState([])
  const [unreadCount, setUnreadCount] = useState(0)

  const [notice, setNotice] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  // Search & Filter State
  const [search, setSearch] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('')
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')
  const [sortBy, setSortBy] = useState('name')
  const [sortDir, setSortDir] = useState('asc')

  // Selected Item Modals
  const [selectedProduct, setSelectedProduct] = useState(null)
  const [selectedOrder, setSelectedOrder] = useState(null)

  const isAdmin = role === 'ADMIN' || role === 'SUPER_ADMIN'
  const isSuperAdmin = role === 'SUPER_ADMIN'

  const show = (message) => {
    setNotice(message)
    setError('')
    setTimeout(() => setNotice(''), 4000)
  }

  const fail = (message) => {
    setError(message)
    setNotice('')
    setTimeout(() => setError(''), 5000)
  }

  async function loadProducts() {
    setLoading(true)
    try {
      const data = await productApi.getProducts({
        page: 0,
        size: 30,
        name: search || undefined,
        categoryId: selectedCategory || undefined,
        minPrice: minPrice || undefined,
        maxPrice: maxPrice || undefined,
        sortBy,
        sortDir,
      })
      setProducts(data?.items || [])
      setTotalProducts(data?.totalElements || data?.items?.length || 0)
    } catch (e) {
      fail(e.message)
    } finally {
      setLoading(false)
    }
  }

  async function loadCategories() {
    try {
      const data = await categoryApi.getCategories({ size: 100 })
      setCategories(data?.items || [])
    } catch {
      // Optional fallback
    }
  }

  async function loadCart() {
    if (!token) return
    try {
      const data = await cartApi.getCart()
      setCart(data)
    } catch (e) {
      // Cart might be empty
    }
  }

  async function loadOrders() {
    if (!token) return
    try {
      const data = await orderApi.getMyOrders({ size: 50 })
      setOrders(Array.isArray(data?.items) ? data.items : [])
    } catch (e) {
      fail(e.message)
    }
  }

  async function loadNotifications() {
    if (!token) return
    try {
      const data = await notificationApi.getNotifications({ size: 20 })
      setNotifications(Array.isArray(data?.items) ? data.items : [])
    } catch {
      // Notification optional
    }
  }

  // Load ONLY unread count from backend - NO fallback calculation from notifications
  async function loadUnreadCount() {
    if (!token) {
      setUnreadCount(0)
      return
    }
    try {
      const response = await notificationApi.getUnreadCount()
      // The backend returns: { count: number } or just the number
      const count = response?.count || response?.unreadCount || response || 0
      setUnreadCount(count)
      console.log('📊 Unread count from backend:', count)
    } catch (error) {
      console.error('❌ Failed to load unread count:', error)
      // DO NOT fallback to calculating from notifications here
      // This prevents the count from being overwritten
      setUnreadCount(0)
    }
  }

  // Load initial data
  useEffect(() => {
    loadProducts()
    loadCategories()
  }, [search, selectedCategory, minPrice, maxPrice, sortBy, sortDir])

  // Load user-specific data when token changes
  useEffect(() => {
    if (token) {
      loadCart()
      loadNotifications()
      loadUnreadCount()
    } else {
      setUnreadCount(0)
    }
  }, [token])

  // REMOVED: The useEffect that was recalculating unreadCount from notifications
  // This was causing the count to change from 3 to 7

  const cartCount = useMemo(
      () => cart?.items?.reduce((sum, item) => sum + item.quantity, 0) || 0,
      [cart]
  )

  async function addToCart(productId) {
    if (!token) {
      setPage('login')
      show('Please log in first to add products to your cart.')
      return
    }
    try {
      await cartApi.addToCart(productId, 1)
      show('Product added to cart successfully!')
      loadCart()
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleCheckout() {
    try {
      const order = await orderApi.checkout()
      show(`Order #${order.orderId} created successfully!`)
      setCart(null)
      setPage('orders')
      loadOrders()
    } catch (e) {
      fail(e.message)
    }
  }

  async function handlePaymob(orderId) {
    try {
      const payment = await paymentApi.initiatePaymob(orderId)
      window.location.href = `https://eg.checkout.paymob.com/?publicKey=${encodeURIComponent(
          payment.publicKey
      )}&clientSecret=${encodeURIComponent(payment.clientSecret)}`
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleLogout() {
    try {
      await authApi.logout()
    } catch {
      // Ignore API logout error if token already expired
    }
    localStorage.removeItem('token')
    setToken(null)
    setRole('CUSTOMER')
    setUsername('Guest')
    setCart(null)
    setOrders([])
    setNotifications([])
    setUnreadCount(0)
    setPage('shop')
    show('Logged out successfully.')
  }

  return (
      <div className="app-shell">
        <header className="topbar">
          <button className="brand" onClick={() => setPage('shop')}>
            Nawl <small>STORE</small>
          </button>
          <nav>
            <button className={page === 'shop' ? 'active' : ''} onClick={() => setPage('shop')}>
              Products
            </button>
            {token && !isAdmin && (
                <button className={page === 'cart' ? 'active' : ''} onClick={() => { setPage('cart'); loadCart() }}>
                  Cart {cartCount > 0 && <span className="badge">{cartCount}</span>}
                </button>
            )}
            {token && !isAdmin && (
                <button className={page === 'orders' ? 'active' : ''} onClick={() => { setPage('orders'); loadOrders() }}>
                  My Orders
                </button>
            )}
            {token && (
                <button className={page === 'chat' ? 'active' : ''} onClick={() => setPage('chat')}>
                  {isAdmin ? 'Support Chat' : 'Support'}
                </button>
            )}
            {token && (
                <button
                    className={page === 'notifications' ? 'active' : ''}
                    onClick={() => {
                      setPage('notifications')
                      loadNotifications()
                      loadUnreadCount()
                    }}
                >
                  Notifications {unreadCount > 0 && <span className="badge">{unreadCount}</span>}
                </button>
            )}
            {isAdmin && (
                <button className={page === 'admin' ? 'active' : ''} onClick={() => setPage('admin')}>
                  Admin Panel
                </button>
            )}
            {isSuperAdmin && (
                <button className={page === 'superadmin' ? 'active' : ''} onClick={() => setPage('superadmin')}>
                  Super Admin
                </button>
            )}
          </nav>
          <div className="user-nav-group">
            {token ? (
                <>
                  <div className="user-badge">
                    <span>{username}</span>
                    <span className={`role-pill ${role}`}>{role.replace('_', ' ')}</span>
                  </div>
                  <button className="ghost" onClick={handleLogout}>
                    Logout
                  </button>
                </>
            ) : (
                <button className="primary" onClick={() => setPage('login')}>
                  Login
                </button>
            )}
          </div>
        </header>

        {(notice || error) && (
            <div className={`toast ${error ? 'error' : ''}`}>
              <span>{error || notice}</span>
              <button className="link" onClick={() => { setNotice(''); setError('') }} style={{ color: 'white' }}>✕</button>
            </div>
        )}

        <main>
          {page === 'shop' && (
              <ShopView
                  products={products}
                  totalProducts={totalProducts}
                  categories={categories}
                  loading={loading}
                  search={search}
                  setSearch={setSearch}
                  selectedCategory={selectedCategory}
                  setSelectedCategory={setSelectedCategory}
                  minPrice={minPrice}
                  setMinPrice={setMinPrice}
                  maxPrice={maxPrice}
                  setMaxPrice={setMaxPrice}
                  sortBy={sortBy}
                  setSortBy={setSortBy}
                  sortDir={sortDir}
                  setSortDir={setSortDir}
                  addToCart={addToCart}
                  onSelectProduct={(p) => setSelectedProduct(p)}
              />
          )}

          {page === 'login' && (
              <AuthView
                  onAuthenticated={(newToken, enable) => {
                    localStorage.setItem('token', newToken)
                    setToken(newToken)
                    const userRole = readRole(newToken)
                    setRole(userRole)
                    setUsername(readUsername(newToken))
                    if (enable === false) {
                      setPage('activate-account')
                      show('Account not activated yet. Please set a new password to activate your account.')
                    } else {
                      setPage(userRole === 'ADMIN' || userRole === 'SUPER_ADMIN' ? 'admin' : 'shop')
                      show('Welcome back!')
                    }
                  }}
                  fail={fail}
              />
          )}

          {page === 'activate-account' && (
              <ActivateAccountView
                  onActivated={() => {
                    setPage(role === 'ADMIN' || role === 'SUPER_ADMIN' ? 'admin' : 'shop')
                    show('Account activated and password updated successfully! Welcome.')
                  }}
                  fail={fail}
              />
          )}

          {page === 'cart' && !isAdmin && (
              <CartView cart={cart} refresh={loadCart} checkout={handleCheckout} fail={fail} show={show} />
          )}

          {page === 'orders' && !isAdmin && (
              <OrdersView
                  orders={orders}
                  refresh={loadOrders}
                  pay={handlePaymob}
                  onSelectOrder={(o) => setSelectedOrder(o)}
              />
          )}

          {page === 'notifications' && (
              <NotificationsView
                  notifications={notifications}
                  refresh={() => {
                    loadNotifications()
                    loadUnreadCount()
                  }}
                  show={show}
                  fail={fail}
                  onMarkRead={() => {
                    loadUnreadCount()
                  }}
              />
          )}

          {page === 'chat' && token && (
              <ChatView isAdmin={isAdmin} show={show} fail={fail} />
          )}

          {page === 'admin' && isAdmin && (
              <AdminDashboard
                  role={role}
                  categories={categories}
                  refreshCategories={loadCategories}
                  refreshProducts={loadProducts}
                  show={show}
                  fail={fail}
              />
          )}

          {page === 'superadmin' && isSuperAdmin && <SuperAdminDashboard show={show} fail={fail} />}

          {/* Access denied fallbacks */}
          {page === 'cart' && isAdmin && (
              <div className="empty">Cart is not available for admin accounts.</div>
          )}
          {page === 'orders' && isAdmin && (
              <div className="empty">Use the Admin Panel to manage orders.</div>
          )}
          {page === 'superadmin' && !isSuperAdmin && (
              <div className="empty">Access Denied — Super Admin privileges required.</div>
          )}
        </main>

        {/* Product Details Modal */}
        {selectedProduct && (
            <ProductDetailModal
                product={selectedProduct}
                onClose={() => setSelectedProduct(null)}
                addToCart={addToCart}
            />
        )}

        {/* Order Details Modal */}
        {selectedOrder && (
            <OrderDetailModal
                orderId={selectedOrder.id}
                onClose={() => setSelectedOrder(null)}
                pay={handlePaymob}
            />
        )}
      </div>
  )
}

/* ---------------------------------------------------- */
/* SHOP VIEW */
/* ---------------------------------------------------- */
function ShopView({
                    products,
                    totalProducts,
                    categories,
                    loading,
                    search,
                    setSearch,
                    selectedCategory,
                    setSelectedCategory,
                    minPrice,
                    setMinPrice,
                    maxPrice,
                    setMaxPrice,
                    sortBy,
                    setSortBy,
                    sortDir,
                    setSortDir,
                    addToCart,
                    onSelectProduct,
                  }) {
  return (
      <section>
        <div className="hero">
          <div>
            <p className="eyebrow">PREMIUM E-COMMERCE PLATFORM</p>
            <h1>
              Everything You Need,
              <br />
              In One Place.
            </h1>
            <p>
              Browse our carefully selected products and enjoy a fast, secure, and
              seamless shopping experience.
            </p>
          </div>
        </div>

        <div className="section-title">
          <h2>Product Store</h2>
          <span>
          Showing {products.length} of {totalProducts} products
        </span>
        </div>

        <div className="catalog-tools">
          <label>
            <span>Search Products</span>
            <input
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search by product name or description..."
            />
          </label>

          <label>
            <span>Category</span>
            <select
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
            >
              <option value="">All Categories</option>
              {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
              ))}
            </select>
          </label>

          <label>
            <span>Minimum Price</span>
            <input
                type="number"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
                placeholder="0"
            />
          </label>

          <label>
            <span>Maximum Price</span>
            <input
                type="number"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
                placeholder="Maximum price"
            />
          </label>

          <label>
            <span>Sort By</span>
            <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
            >
              <option value="name">Name</option>
              <option value="price">Price</option>
              <option value="createdAt">Date Added</option>
            </select>
          </label>

          <label>
            <span>Order</span>
            <select
                value={sortDir}
                onChange={(e) => setSortDir(e.target.value)}
            >
              <option value="asc">Ascending</option>
              <option value="desc">Descending</option>
            </select>
          </label>
        </div>

        {loading ? (
            <div className="empty">Loading products...</div>
        ) : (
            <div className="product-grid">
              {products.map((product) => (
                  <article className="product-card" key={product.id}>
                    <div
                        className="product-image"
                        onClick={() => onSelectProduct(product)}
                        style={{ cursor: "pointer" }}
                    >
                      {product.imageUrl ? (
                          <img src={product.imageUrl} alt={product.name} />
                      ) : (
                          <span>◈</span>
                      )}
                    </div>

                    <div className="product-body">
                <span className="category">
                  {product.categoryName || "General"}
                </span>

                      <h3
                          onClick={() => onSelectProduct(product)}
                          style={{ cursor: "pointer" }}
                      >
                        {product.name}
                      </h3>

                      <p>
                        {product.description ||
                            "A premium-quality product with exceptional design and craftsmanship."}
                      </p>

                      <div className="price-row">
                        <strong>{currency.format(product.price)}</strong>

                        <button
                            className="primary"
                            onClick={() => addToCart(product.id)}
                        >
                          + Add to Cart
                        </button>
                      </div>
                    </div>
                  </article>
              ))}
            </div>
        )}

        {!loading && !products.length && (
            <div className="empty">
              No products match your search criteria.
            </div>
        )}
      </section>
  )
}

/* ---------------------------------------------------- */
/* AUTH VIEW */
/* ---------------------------------------------------- */
function AuthView({ onAuthenticated, fail }) {
  const [mode, setMode] = useState('login')
  const [isAdminLogin, setIsAdminLogin] = useState(false)
  const [form, setForm] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    identifier: '',
  })

  const handleSubmit = async (e) => {
    e.preventDefault()

    try {
      if (mode === 'register') {
        await authApi.registerCustomer({
          name: form.name,
          email: form.email,
          phone: form.phone,
          password: form.password,
        })

        setMode('login')
        return
      }

      let res

      if (isAdminLogin) {
        res = await authApi.loginAdmin({
          email: form.email,
          password: form.password,
        })
      } else {
        res = await authApi.loginCustomer({
          identifier: form.identifier,
          password: form.password,
        })
      }

      onAuthenticated(res.token, res.enable)
    } catch (err) {
      fail(err.message)
    }
  }

  return (
      <section className="auth-card">
        <div>
          <p className="eyebrow">AUTHENTICATION</p>
          <h1>{mode === 'register' ? 'Create Account' : 'Sign In'}</h1>
        </div>

        <form onSubmit={handleSubmit}>
          {mode === 'register' ? (
              <>
                <label className="field">
                  Full Name
                  <input
                      value={form.name}
                      onChange={(e) =>
                          setForm({
                            ...form,
                            name: e.target.value,
                          })
                      }
                      required
                  />
                </label>

                <label className="field">
                  Email Address
                  <input
                      type="email"
                      value={form.email}
                      onChange={(e) =>
                          setForm({
                            ...form,
                            email: e.target.value,
                          })
                      }
                      required
                  />
                </label>

                <label className="field">
                  Phone Number
                  <input
                      value={form.phone}
                      onChange={(e) =>
                          setForm({
                            ...form,
                            phone: e.target.value,
                          })
                      }
                      required
                  />
                </label>
              </>
          ) : (
              <>
                <label className="check">
                  <input
                      type="checkbox"
                      checked={isAdminLogin}
                      onChange={(e) => setIsAdminLogin(e.target.checked)}
                  />
                  Sign in as Admin / Super Admin
                </label>

                <label className="field">
                  {isAdminLogin
                      ? 'Email Address'
                      : 'Email Address or Phone Number'}

                  <input
                      type={isAdminLogin ? 'email' : 'text'}
                      value={isAdminLogin ? form.email : form.identifier}
                      onChange={(e) =>
                          setForm({
                            ...form,
                            [isAdminLogin ? 'email' : 'identifier']:
                            e.target.value,
                          })
                      }
                      required
                  />
                </label>
              </>
          )}

          <label className="field">
            Password
            <input
                type="password"
                value={form.password}
                onChange={(e) =>
                    setForm({
                      ...form,
                      password: e.target.value,
                    })
                }
                required
            />
          </label>

          <button className="primary wide">
            {mode === 'register' ? 'Create Account' : 'Sign In'}
          </button>
        </form>

        <button
            className="link"
            onClick={() =>
                setMode(mode === 'login' ? 'register' : 'login')
            }
        >
          {mode === 'login'
              ? "Don't have an account? Create one"
              : 'Already have an account? Sign in'}
        </button>
      </section>
  )
}

/* ---------------------------------------------------- */
/* CART VIEW */
/* ---------------------------------------------------- */
function CartView({ cart, refresh, checkout, fail, show }) {
  async function updateQty(itemId, quantity) {
    try {
      await cartApi.updateCartItem(itemId, quantity)
      refresh()
    } catch (e) {
      fail(e.message)
    }
  }

  async function removeItem(itemId) {
    try {
      await cartApi.removeCartItem(itemId)
      show('Product removed from your cart.')
      refresh()
    } catch (e) {
      fail(e.message)
    }
  }

  return (
      <section>
        <div className="section-title">
          <h1>Shopping Cart</h1>
        </div>

        {!cart?.items?.length ? (
            <div className="empty">
              Your shopping cart is currently empty. Browse our products and add your favourites!
            </div>
        ) : (
            <div className="cart-layout">
              <div className="cart-items">
                {cart.items.map((item) => (
                    <div className="cart-item" key={item.id}>
                      <div>
                        <h3>{item.productName}</h3>
                        <span style={{ color: 'var(--text-muted)' }}>
                    Unit Price: {currency.format(item.unitPrice)}
                  </span>
                      </div>

                      <div className="quantity">
                        <button
                            onClick={() =>
                                updateQty(item.id, Math.max(1, item.quantity - 1))
                            }
                        >
                          −
                        </button>

                        <b>{item.quantity}</b>

                        <button
                            onClick={() =>
                                updateQty(item.id, item.quantity + 1)
                            }
                        >
                          +
                        </button>
                      </div>

                      <strong>{currency.format(item.subtotal)}</strong>

                      <button
                          className="link danger"
                          onClick={() => removeItem(item.id)}
                      >
                        Remove
                      </button>
                    </div>
                ))}
              </div>

              <aside className="summary">
                <h2>Order Summary</h2>

                <div>
                  <span>Total Amount:</span>
                  <strong>{currency.format(cart.totalAmount)}</strong>
                </div>

                <button
                    className="primary wide"
                    onClick={checkout}
                >
                  Place Order
                </button>
              </aside>
            </div>
        )}
      </section>
  )
}

/* ---------------------------------------------------- */
/* ORDERS VIEW */
/* ---------------------------------------------------- */
function OrdersView({ orders, refresh, pay, onSelectOrder }) {
  useEffect(() => {
    refresh()
  }, [])

  return (
      <section>
        <div className="section-title">
          <h1>My Orders</h1>

          <button
              className="ghost"
              onClick={refresh}
          >
            Refresh Orders
          </button>
        </div>

        {!orders.length ? (
            <div className="empty">
              You haven't placed any orders yet.
            </div>
        ) : (
            <div className="table-wrap">
              <table>
                <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Date</th>
                  <th>Total</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                {orders.map((order) => (
                    <tr key={order.id}>
                      <td>#{order.id}</td>

                      <td>
                        {order.createdAt
                            ? new Date(order.createdAt).toLocaleDateString('en-GB')
                            : '—'}
                      </td>

                      <td>{currency.format(order.totalAmount ?? 0)}</td>

                      <td>
                    <span className={`status ${order.status}`}>
                      {statusLabels[order.status] || order.status}
                    </span>
                      </td>

                      <td>
                        <div
                            style={{
                              display: 'flex',
                              gap: '0.5rem',
                            }}
                        >
                          <button
                              className="ghost"
                              onClick={() => onSelectOrder(order)}
                          >
                            View Details
                          </button>

                          {order.status === 'PENDING' && (
                              <button
                                  className="primary"
                                  onClick={() => pay(order.id)}
                              >
                                Pay Now
                              </button>
                          )}
                        </div>
                      </td>
                    </tr>
                ))}
                </tbody>
              </table>
            </div>
        )}
      </section>
  )
}

/* ---------------------------------------------------- */
/* NOTIFICATIONS VIEW */
/* ---------------------------------------------------- */
function NotificationsView({ notifications, refresh, show, fail, onMarkRead }) {
  async function markAsRead(id) {
    try {
      await notificationApi.markAsRead(id)
      show('Notification marked as read.')
      refresh()
      if (onMarkRead) onMarkRead()
    } catch (e) {
      fail(e.message)
    }
  }

  async function markAllAsRead() {
    try {
      await notificationApi.markAllAsRead()
      show('All notifications marked as read.')
      refresh()
      if (onMarkRead) onMarkRead()
    } catch (e) {
      fail(e.message)
    }
  }

  // Calculate unread count from the loaded notifications for display
  const unreadCount = notifications.filter(n => !n.isRead).length

  return (
      <section>
        <div className="section-title">
          <div>
            <h1>Notifications</h1>
            {unreadCount > 0 && (
                <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
              {unreadCount} unread
            </span>
            )}
          </div>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            {unreadCount > 0 && (
                <button className="ghost" onClick={markAllAsRead}>
                  Mark All as Read
                </button>
            )}
            <button className="ghost" onClick={refresh}>
              Refresh
            </button>
          </div>
        </div>

        {!notifications.length ? (
            <div className="empty">No notifications available.</div>
        ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {notifications.map((n) => (
                  <div
                      key={n.id}
                      style={{
                        background: n.isRead
                            ? 'var(--bg-card)'
                            : 'rgba(99, 102, 241, 0.15)',
                        border: '1px solid var(--border-color)',
                        borderRadius: 'var(--radius-md)',
                        padding: '1.2rem 1.5rem',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                      }}
                  >
                    <div>
                      <h3
                          style={{
                            fontSize: '1.1rem',
                            marginBottom: '0.3rem',
                          }}
                      >
                        {n.title || 'Notification'}
                        {!n.isRead && (
                            <span style={{
                              marginLeft: '0.5rem',
                              fontSize: '0.7rem',
                              color: '#6366f1',
                              background: 'rgba(99, 102, 241, 0.2)',
                              padding: '0.2rem 0.5rem',
                              borderRadius: '4px'
                            }}>
                      NEW
                    </span>
                        )}
                      </h3>

                      <p style={{ color: 'var(--text-muted)' }}>
                        {n.message}
                      </p>

                      <small style={{ color: 'var(--text-dim)' }}>
                        {n.createdAt
                            ? new Date(n.createdAt).toLocaleString('en-GB')
                            : ''}
                      </small>
                    </div>

                    {!n.isRead && (
                        <button
                            className="ghost"
                            onClick={() => markAsRead(n.id)}
                        >
                          Mark as Read
                        </button>
                    )}
                  </div>
              ))}
            </div>
        )}
      </section>
  )
}


/* ---------------------------------------------------- */
/* LIVE CHAT */
/* ---------------------------------------------------- */
function ChatView({ isAdmin, show, fail }) {
  const [conversation, setConversation] = useState(null)
  const [waitingConversations, setWaitingConversations] = useState([])
  const [messages, setMessages] = useState([])
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(true)
  const [sending, setSending] = useState(false)

  async function loadCurrent() {
    try {
      const current = await chatApi.getCurrentConversation()
      setConversation(current)
      return current
    } catch (e) {
      fail(e.message)
      return null
    } finally {
      setLoading(false)
    }
  }

  async function loadWaiting() {
    if (!isAdmin) return
    try {
      setWaitingConversations(await chatApi.getWaitingConversations())
    } catch (e) {
      fail(e.message)
    }
  }

  async function loadMessages(id) {
    try {
      setMessages(await chatApi.getMessages(id))
    } catch (e) {
      fail(e.message)
    }
  }

  useEffect(() => {
    loadCurrent()
    loadWaiting()
    const refresh = window.setInterval(() => {
      loadCurrent().then((current) => {
        if (current?.id) loadMessages(current.id)
      })
      loadWaiting()
    }, 3000)
    return () => window.clearInterval(refresh)
  }, [isAdmin])

  useEffect(() => {
    if (conversation?.id) loadMessages(conversation.id)
    else setMessages([])
  }, [conversation?.id])

  async function startConversation() {
    try {
      const current = await chatApi.startConversation()
      setConversation(current)
      show('Your support request has been created. An agent will join shortly.')
    } catch (e) {
      fail(e.message)
    }
  }

  async function acceptConversation(id) {
    try {
      const current = await chatApi.acceptConversation(id)
      setConversation(current)
      setWaitingConversations((items) => items.filter((item) => item.id !== id))
      show('Conversation accepted.')
    } catch (e) {
      fail(e.message)
    }
  }

  async function sendMessage(event) {
    event.preventDefault()
    const content = message.trim()
    if (!content || !conversation?.id) return
    setSending(true)
    try {
      const sent = await chatApi.sendMessage(conversation.id, content)
      setMessages((items) => [...items, sent])
      setMessage('')
    } catch (e) {
      fail(e.message)
    } finally {
      setSending(false)
    }
  }

  async function closeConversation() {
    if (!conversation || !window.confirm('Close this support conversation?')) return
    try {
      await chatApi.closeConversation(conversation.id)
      setConversation(null)
      setMessages([])
      show('Conversation closed.')
    } catch (e) {
      fail(e.message)
    }
  }

  if (loading) return <div className="empty">Loading support chat…</div>

  return (
      <section className="chat-layout">
        <aside className="chat-sidebar">
          <p className="eyebrow">LIVE SUPPORT</p>
          <h2>{isAdmin ? 'Support queue' : 'Need help?'}</h2>
          {isAdmin ? (
              <>
                <p className="muted">Accept one waiting customer to begin helping them.</p>
                {conversation && <div className="chat-current">You are helping customer #{conversation.customerId}.</div>}
                <div className="chat-queue">
                  {waitingConversations.length === 0 ? <p className="muted">No customers are waiting.</p> : waitingConversations.map((item) => (
                      <div className="chat-queue-item" key={item.id}>
                        <span>Customer #{item.customerId}</span>
                        <button className="primary small" disabled={Boolean(conversation)} onClick={() => acceptConversation(item.id)}>Accept</button>
                      </div>
                  ))}
                </div>
              </>
          ) : !conversation ? (
              <>
                <p className="muted">Start a conversation and our support team will be notified.</p>
                <button className="primary" onClick={startConversation}>Start support chat</button>
              </>
          ) : (
              <div className={`chat-state ${conversation.status.toLowerCase()}`}>
                {conversation.status === 'WAITING' ? 'Waiting for a support agent…' : 'A support agent is connected.'}
              </div>
          )}
        </aside>

        <div className="chat-panel">
          {!conversation ? (
              <div className="empty">{isAdmin ? 'Choose a customer from the queue.' : 'Start a chat whenever you need help.'}</div>
          ) : (
              <>
                <div className="chat-header">
                  <div><strong>{isAdmin ? `Customer #${conversation.customerId}` : 'Customer Support'}</strong><span>{conversation.status === 'WAITING' ? 'Waiting' : 'Active'}</span></div>
                  <button className="ghost small" onClick={closeConversation}>Close chat</button>
                </div>
                <div className="chat-messages">
                  {messages.length === 0 && <p className="muted">No messages yet. Say hello to get started.</p>}
                  {messages.map((item) => (
                      <div className="chat-message" key={item.id}>
                        <span className="chat-sender">{String(item.senderId) === String(conversation.customerId) ? 'Customer' : 'Support'}</span>
                        <p>{item.content}</p>
                        <time>{item.createdAt ? new Date(item.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''}</time>
                      </div>
                  ))}
                </div>
                <form className="chat-compose" onSubmit={sendMessage}>
                  <input value={message} onChange={(event) => setMessage(event.target.value)} maxLength="2000" disabled={conversation.status !== 'ACTIVE'} placeholder={conversation.status === 'ACTIVE' ? 'Write a message…' : 'Waiting for an agent…'} />
                  <button className="primary" disabled={sending || conversation.status !== 'ACTIVE'}>{sending ? 'Sending…' : 'Send'}</button>
                </form>
              </>
          )}
        </div>
      </section>
  )
}

/* ---------------------------------------------------- */
/* ADMIN DASHBOARD */
/* ---------------------------------------------------- */
function AdminDashboard({
                          categories,
                          refreshCategories,
                          refreshProducts,
                          show,
                          fail,
                        }) {
  const [tab, setTab] = useState('products')

  const [adminProducts, setAdminProducts] = useState([])
  const [adminCategories, setAdminCategories] = useState([])
  const [adminOrders, setAdminOrders] = useState([])

  const [productForm, setProductForm] = useState({
    name: '',
    description: '',
    price: '',
    quantity: '',
    categoryId: '',
  })

  const [createImageFile, setCreateImageFile] = useState(null)
  const [editProductModal, setEditProductModal] = useState(null)
  const [editImageFile, setEditImageFile] = useState(null)
  const [categoryForm, setCategoryForm] = useState({
    name: '',
    description: '',
  })

  const [stockModal, setStockModal] = useState(null)
  const [cancelModal, setCancelModal] = useState(null)

  async function fetchAdminProducts() {
    try {
      const res = await adminProductApi.getProducts({ size: 100 })
      setAdminProducts(res?.items || [])
    } catch (e) {
      fail(e.message)
    }
  }

  async function fetchAdminCategories() {
    try {
      const res = await adminCategoryApi.getCategories({ size: 100 })
      setAdminCategories(res?.items || [])
    } catch (e) {
      fail(e.message)
    }
  }

  async function fetchAdminOrders() {
    try {
      const res = await adminOrderApi.getOrders({ size: 100 })
      setAdminOrders(res?.items || [])
    } catch (e) {
      fail(e.message)
    }
  }

  useEffect(() => {
    if (tab === 'products') fetchAdminProducts()
    if (tab === 'categories') fetchAdminCategories()
    if (tab === 'orders') fetchAdminOrders()
  }, [tab])

  async function handleCreateProduct(e) {
    e.preventDefault()

    try {
      const productData = {
        name: productForm.name,
        description: productForm.description,
        price: Number(productForm.price),
        quantity: Number(productForm.quantity),
        categoryId: Number(productForm.categoryId),
        isActive: true,
      }

      await adminProductApi.createProduct(
          productData,
          createImageFile
      )

      show('Product created successfully.')
      setProductForm({
        name: '',
        description: '',
        price: '',
        quantity: '',
        categoryId: '',
      })

      setCreateImageFile(null)
      fetchAdminProducts()
      refreshProducts()
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleUpdateProduct(e) {
    e.preventDefault()

    if (!editProductModal) return

    try {
      const productData = {
        name: editProductModal.name,
        description: editProductModal.description,
        price: Number(editProductModal.price),
        quantity: Number(editProductModal.quantity),
        categoryId: Number(editProductModal.categoryId),
        isActive:
            editProductModal.isActive !== undefined
                ? editProductModal.isActive
                : true,
      }

      await adminProductApi.updateProduct(
          editProductModal.id,
          productData,
          editImageFile
      )

      show('Product updated successfully.')
      setEditProductModal(null)
      setEditImageFile(null)

      fetchAdminProducts()
      refreshProducts()
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleToggleStatus(productId) {
    try {
      await adminProductApi.toggleStatus(productId)
      show('Product status updated successfully.')
      fetchAdminProducts()
      refreshProducts()
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleUpdateStock() {
    if (!stockModal) return

    try {
      await adminProductApi.updateStock(stockModal.productId, {
        quantity: Number(stockModal.quantity),
      })

      show('Stock updated successfully.')
      setStockModal(null)

      fetchAdminProducts()
      refreshProducts()
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleDeleteProduct(productId) {
    if (!window.confirm('Are you sure you want to delete this product?'))
      return

    try {
      await adminProductApi.deleteProduct(productId)
      show('Product deleted successfully.')
      fetchAdminProducts()
      refreshProducts()
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleCreateCategory(e) {
    e.preventDefault()

    try {
      await adminCategoryApi.createCategory(categoryForm)

      show('Category created successfully.')
      setCategoryForm({
        name: '',
        description: '',
      })

      fetchAdminCategories()
      refreshCategories()
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleDeleteCategory(catId) {
    if (!window.confirm('Are you sure you want to delete this category?'))
      return

    try {
      await adminCategoryApi.deleteCategory(catId)
      show('Category deleted successfully.')

      fetchAdminCategories()
      refreshCategories()
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleOrderStatusChange(orderId, newStatus) {
    try {
      await adminOrderApi.updateStatus(orderId, newStatus)
      show('Order status updated successfully.')
      fetchAdminOrders()
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleCancelOrder() {
    if (!cancelModal || !cancelModal.reason.trim()) return

    try {
      await adminOrderApi.cancelOrder(
          cancelModal.orderId,
          cancelModal.reason
      )

      show('Order cancellation request submitted successfully.')
      setCancelModal(null)
      fetchAdminOrders()
    } catch (e) {
      fail(e.message)
    }
  }

  return (
      <section>
        <div className="section-title">
          <div>
            <p className="eyebrow">ADMINISTRATION</p>
            <h1>Store Management Dashboard</h1>
          </div>
        </div>

        <div className="admin-tabs">
          <button
              className={tab === 'products' ? 'active' : ''}
              onClick={() => setTab('products')}
          >
            Products
          </button>

          <button
              className={tab === 'categories' ? 'active' : ''}
              onClick={() => setTab('categories')}
          >
            Categories
          </button>

          <button
              className={tab === 'orders' ? 'active' : ''}
              onClick={() => setTab('orders')}
          >
            Orders
          </button>
        </div>

        {/* PRODUCTS TAB */}
        {tab === 'products' && (
            <div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', marginTop: '2rem' }}>
                {/* Create Product Form */}
                <form className="admin-form" onSubmit={handleCreateProduct}>
                  <h2>Create New Product</h2>

                  <label className="field">
                    Product Name
                    <input
                        value={productForm.name}
                        onChange={(e) => setProductForm({ ...productForm, name: e.target.value })}
                        required
                    />
                  </label>

                  <label className="field">
                    Description
                    <textarea
                        value={productForm.description}
                        onChange={(e) => setProductForm({ ...productForm, description: e.target.value })}
                        rows="3"
                    />
                  </label>

                  <label className="field">
                    Price (EGP)
                    <input
                        type="number"
                        step="0.01"
                        value={productForm.price}
                        onChange={(e) => setProductForm({ ...productForm, price: e.target.value })}
                        required
                    />
                  </label>

                  <label className="field">
                    Quantity
                    <input
                        type="number"
                        value={productForm.quantity}
                        onChange={(e) => setProductForm({ ...productForm, quantity: e.target.value })}
                        required
                    />
                  </label>

                  <label className="field">
                    Category
                    <select
                        value={productForm.categoryId}
                        onChange={(e) => setProductForm({ ...productForm, categoryId: e.target.value })}
                        required
                    >
                      <option value="">Select Category</option>
                      {categories.map((c) => (
                          <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                  </label>

                  <label className="field">
                    Product Image
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setCreateImageFile(e.target.files[0])}
                    />
                  </label>

                  <button className="primary wide">Create Product</button>
                </form>

                {/* Products List */}
                <div>
                  <h2>Existing Products</h2>
                  <div className="table-wrap">
                    <table>
                      <thead>
                      <tr>
                        <th>Name</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Status</th>
                        <th>Actions</th>
                      </tr>
                      </thead>
                      <tbody>
                      {adminProducts.map((p) => (
                          <tr key={p.id}>
                            <td>{p.name}</td>
                            <td>{currency.format(p.price)}</td>
                            <td>{p.quantity}</td>
                            <td>
                          <span className={`status ${p.isActive ? 'active' : 'inactive'}`}>
                            {p.isActive ? 'Active' : 'Inactive'}
                          </span>
                            </td>
                            <td>
                              <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                                <button
                                    className="ghost"
                                    onClick={() => setEditProductModal(p)}
                                >
                                  Edit
                                </button>
                                <button
                                    className="ghost"
                                    onClick={() => setStockModal({ productId: p.id, quantity: p.quantity })}
                                >
                                  Stock
                                </button>
                                <button
                                    className="ghost"
                                    onClick={() => handleToggleStatus(p.id)}
                                >
                                  {p.isActive ? 'Deactivate' : 'Activate'}
                                </button>
                                <button
                                    className="link danger"
                                    onClick={() => handleDeleteProduct(p.id)}
                                >
                                  Delete
                                </button>
                              </div>
                            </td>
                          </tr>
                      ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
        )}

        {/* CATEGORIES TAB */}
        {tab === 'categories' && (
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', marginTop: '2rem' }}>
              <form className="admin-form" onSubmit={handleCreateCategory}>
                <h2>Create New Category</h2>

                <label className="field">
                  Category Name
                  <input
                      value={categoryForm.name}
                      onChange={(e) => setCategoryForm({ ...categoryForm, name: e.target.value })}
                      required
                  />
                </label>

                <label className="field">
                  Description
                  <textarea
                      value={categoryForm.description}
                      onChange={(e) => setCategoryForm({ ...categoryForm, description: e.target.value })}
                      rows="3"
                  />
                </label>

                <button className="primary wide">Create Category</button>
              </form>

              <div>
                <h2>Existing Categories</h2>
                <div className="table-wrap">
                  <table>
                    <thead>
                    <tr>
                      <th>Name</th>
                      <th>Description</th>
                      <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {adminCategories.map((c) => (
                        <tr key={c.id}>
                          <td>{c.name}</td>
                          <td>{c.description || '—'}</td>
                          <td>
                            <button
                                className="link danger"
                                onClick={() => handleDeleteCategory(c.id)}
                            >
                              Delete
                            </button>
                          </td>
                        </tr>
                    ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
        )}

        {/* ORDERS TAB */}
        {tab === 'orders' && (
            <div className="table-wrap" style={{ marginTop: '2rem' }}>
              <table>
                <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Customer</th>
                  <th>Date</th>
                  <th>Total</th>
                  <th>Status</th>
                  <th>Change Status</th>
                  <th>Cancel</th>
                </tr>
                </thead>
                <tbody>
                {adminOrders.map((o) => (
                    <tr key={o.id}>
                      <td>#{o.id}</td>
                      <td>{o.customerEmail || o.userId || 'Customer'}</td>
                      <td>
                        {o.createdAt
                            ? new Date(o.createdAt).toLocaleDateString('en-GB')
                            : '—'}
                      </td>
                      <td>{currency.format(o.totalAmount ?? 0)}</td>
                      <td>
                    <span className={`status ${o.status}`}>
                      {statusLabels[o.status] || o.status}
                    </span>
                      </td>
                      <td>
                        <select
                            value={o.status}
                            onChange={(e) =>
                                handleOrderStatusChange(o.id, e.target.value)
                            }
                            style={{
                              background: 'var(--bg-glass)',
                              border: '1px solid var(--border-color)',
                              color: 'white',
                              padding: '4px 8px',
                              borderRadius: '4px',
                            }}
                        >
                          <option value="PENDING">PENDING</option>
                          <option value="PAID">PAID</option>
                          <option value="PREPARED">PREPARED</option>
                          <option value="SHIPPED">SHIPPED</option>
                          <option value="DELIVERED">DELIVERED</option>
                          <option value="CANCELLED">CANCELLED</option>
                        </select>
                      </td>
                      <td>
                        {['PENDING', 'PENDING_PAYMENT', 'PAID', 'PREPARED'].includes(
                            o.status
                        ) && (
                            <button
                                className="danger-button"
                                onClick={() =>
                                    setCancelModal({
                                      orderId: o.id,
                                      reason: '',
                                    })
                                }
                            >
                              Cancel
                            </button>
                        )}
                      </td>
                    </tr>
                ))}
                </tbody>
              </table>
            </div>
        )}

        {/* Edit Product Modal */}
        {editProductModal && (
            <div className="modal-backdrop">
              <div className="modal">
                <h2>Edit Product</h2>
                <form onSubmit={handleUpdateProduct}>
                  <label className="field">
                    Product Name
                    <input
                        value={editProductModal.name}
                        onChange={(e) => setEditProductModal({ ...editProductModal, name: e.target.value })}
                        required
                    />
                  </label>

                  <label className="field">
                    Description
                    <textarea
                        value={editProductModal.description}
                        onChange={(e) => setEditProductModal({ ...editProductModal, description: e.target.value })}
                        rows="3"
                    />
                  </label>

                  <label className="field">
                    Price (EGP)
                    <input
                        type="number"
                        step="0.01"
                        value={editProductModal.price}
                        onChange={(e) => setEditProductModal({ ...editProductModal, price: e.target.value })}
                        required
                    />
                  </label>

                  <label className="field">
                    Quantity
                    <input
                        type="number"
                        value={editProductModal.quantity}
                        onChange={(e) => setEditProductModal({ ...editProductModal, quantity: e.target.value })}
                        required
                    />
                  </label>

                  <label className="field">
                    Category
                    <select
                        value={editProductModal.categoryId}
                        onChange={(e) => setEditProductModal({ ...editProductModal, categoryId: e.target.value })}
                        required
                    >
                      <option value="">Select Category</option>
                      {categories.map((c) => (
                          <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                  </label>

                  <label className="field">
                    Update Image (optional)
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setEditImageFile(e.target.files[0])}
                    />
                  </label>

                  <div className="modal-actions">
                    <button className="ghost" onClick={() => setEditProductModal(null)}>
                      Cancel
                    </button>
                    <button className="primary">Update Product</button>
                  </div>
                </form>
              </div>
            </div>
        )}

        {/* Update Stock Modal */}
        {stockModal && (
            <div className="modal-backdrop">
              <div className="modal">
                <h2>Update Product Stock</h2>

                <label className="field">
                  New Stock Quantity
                  <input
                      type="number"
                      value={stockModal.quantity}
                      onChange={(e) =>
                          setStockModal({
                            ...stockModal,
                            quantity: e.target.value,
                          })
                      }
                  />
                </label>

                <div className="modal-actions">
                  <button
                      className="ghost"
                      onClick={() => setStockModal(null)}
                  >
                    Cancel
                  </button>

                  <button
                      className="primary"
                      onClick={handleUpdateStock}
                  >
                    Save Stock
                  </button>
                </div>
              </div>
            </div>
        )}

        {/* Cancel Order Modal */}
        {cancelModal && (
            <div className="modal-backdrop">
              <div className="modal">
                <h2>Cancel Order #{cancelModal.orderId}</h2>

                <label className="field">
                  Cancellation Reason
                  <input
                      value={cancelModal.reason}
                      onChange={(e) =>
                          setCancelModal({
                            ...cancelModal,
                            reason: e.target.value,
                          })
                      }
                      placeholder="Enter the cancellation reason..."
                  />
                </label>

                <div className="modal-actions">
                  <button
                      className="ghost"
                      onClick={() => setCancelModal(null)}
                  >
                    Cancel
                  </button>

                  <button
                      className="danger-button"
                      onClick={handleCancelOrder}
                  >
                    Confirm Cancellation
                  </button>
                </div>
              </div>
            </div>
        )}
      </section>
  )
}

/* ---------------------------------------------------- */
/* SUPER ADMIN DASHBOARD */
/* ---------------------------------------------------- */
function SuperAdminDashboard({ show, fail }) {
  const [adminForm, setAdminForm] = useState({
    name: '',
    email: '',
    password: '',
    phone: '',
  })

  const [lockUserId, setLockUserId] = useState('')

  async function handleCreateAdmin(e) {
    e.preventDefault()

    try {
      await superAdminApi.createAdmin(adminForm)

      show('Admin account created successfully!')

      setAdminForm({
        name: '',
        email: '',
        password: '',
        phone: '',
      })
    } catch (e) {
      fail(e.message)
    }
  }

  async function handleToggleLock(e) {
    e.preventDefault()

    if (!lockUserId) return

    try {
      await superAdminApi.toggleLockAccount(Number(lockUserId))

      show('Account lock status updated successfully!')

      setLockUserId('')
    } catch (e) {
      fail(e.message)
    }
  }

  return (
      <section>
        <div className="section-title">
          <div>
            <p className="eyebrow">SUPER ADMIN CONTROL</p>
            <h1>Admin & Account Management</h1>
          </div>
        </div>

        <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
              gap: '2rem',
            }}
        >
          <form
              className="admin-form"
              onSubmit={handleCreateAdmin}
          >
            <h2>Create New Admin Account</h2>

            <label className="field">
              Full Name
              <input
                  value={adminForm.name}
                  onChange={(e) =>
                      setAdminForm({
                        ...adminForm,
                        name: e.target.value,
                      })
                  }
                  required
              />
            </label>

            <label className="field">
              Email Address
              <input
                  type="email"
                  value={adminForm.email}
                  onChange={(e) =>
                      setAdminForm({
                        ...adminForm,
                        email: e.target.value,
                      })
                  }
                  required
              />
            </label>

            <label className="field">
              Password
              <input
                  type="password"
                  value={adminForm.password}
                  onChange={(e) =>
                      setAdminForm({
                        ...adminForm,
                        password: e.target.value,
                      })
                  }
                  required
              />
            </label>

            <label className="field">
              Phone Number
              <input
                  value={adminForm.phone}
                  onChange={(e) =>
                      setAdminForm({
                        ...adminForm,
                        phone: e.target.value,
                      })
                  }
                  required
              />
            </label>

            <button className="primary wide">
              Create Admin Account
            </button>
          </form>

          <form
              className="admin-form"
              onSubmit={handleToggleLock}
          >
            <h2>Lock / Unlock User Account</h2>

            <label className="field">
              User ID
              <input
                  type="number"
                  value={lockUserId}
                  onChange={(e) => setLockUserId(e.target.value)}
                  required
                  placeholder="Example: 12"
              />
            </label>

            <button
                className="danger-button wide"
                style={{ marginTop: '1.2rem' }}
            >
              Change Lock Status
            </button>
          </form>
        </div>
      </section>
  )
}

/* ---------------------------------------------------- */
/* PRODUCT DETAIL MODAL */
/* ---------------------------------------------------- */
function ProductDetailModal({ product, onClose, addToCart }) {
  return (
      <div className="modal-backdrop" onClick={onClose}>
        <div
            className="modal"
            onClick={(e) => e.stopPropagation()}
            style={{ maxWidth: '600px' }}
        >
          <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
              }}
          >
          <span className="eyebrow">
            {product.categoryName || 'Product'}
          </span>

            <button className="ghost" onClick={onClose}>
              ✕
            </button>
          </div>

          <h2>{product.name}</h2>

          {product.imageUrl && (
              <img
                  src={product.imageUrl}
                  alt={product.name}
                  style={{
                    width: '100%',
                    maxHeight: '250px',
                    objectFit: 'cover',
                    borderRadius: 'var(--radius-md)',
                  }}
              />
          )}

          <p style={{ color: 'var(--text-muted)' }}>
            {product.description ||
                'No additional description is available for this product.'}
          </p>

          <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
              }}
          >
            <strong style={{ fontSize: '1.4rem' }}>
              {currency.format(product.price)}
            </strong>

            <button
                className="primary"
                onClick={() => {
                  addToCart(product.id)
                  onClose()
                }}
            >
              Add to Cart
            </button>
          </div>
        </div>
      </div>
  )
}

/* ---------------------------------------------------- */
/* ORDER DETAIL MODAL */
/* ---------------------------------------------------- */
function OrderDetailModal({ orderId, onClose, pay }) {
  const [details, setDetails] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    orderApi
        .getOrderDetails(orderId)
        .then(setDetails)
        .finally(() => setLoading(false))
  }, [orderId])

  return (
      <div className="modal-backdrop" onClick={onClose}>
        <div
            className="modal"
            onClick={(e) => e.stopPropagation()}
            style={{ maxWidth: '650px' }}
        >
          <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
              }}
          >
            <h2>Order Details #{orderId}</h2>

            <button className="ghost" onClick={onClose}>
              ✕
            </button>
          </div>

          {loading ? (
              <div>Loading order details...</div>
          ) : !details ? (
              <div>Unable to load order details.</div>
          ) : (
              <div
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '1rem',
                  }}
              >
                <div
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                    }}
                >
              <span>
                Status:{' '}
                <b className={`status ${details.status}`}>
                  {statusLabels[details.status] || details.status}
                </b>
              </span>

                  <span>
                Total:{' '}
                    <b>
                  {currency.format(
                      details.totalAmount ?? details.total ?? 0
                  )}
                </b>
              </span>
                </div>

                <h3>Order Items</h3>

                <div className="table-wrap">
                  <table>
                    <thead>
                    <tr>
                      <th>Product</th>
                      <th>Quantity</th>
                      <th>Unit Price</th>
                      <th>Total</th>
                    </tr>
                    </thead>

                    <tbody>
                    {(details.items || details.orderItems || []).map(
                        (item, idx) => (
                            <tr key={idx}>
                              <td>
                                {item.productName ||
                                    item.product?.name ||
                                    'Product'}
                              </td>

                              <td>{item.quantity}</td>

                              <td>
                                {currency.format(
                                    item.price || item.unitPrice || 0
                                )}
                              </td>

                              <td>
                                {currency.format(
                                    (item.quantity || 1) *
                                    (item.price || item.unitPrice || 0)
                                )}
                              </td>
                            </tr>
                        )
                    )}
                    </tbody>
                  </table>
                </div>

                {details.status === 'PENDING' && (
                    <button
                        className="primary wide"
                        onClick={() => {
                          pay(orderId)
                          onClose()
                        }}
                    >
                      Proceed to Secure Online Payment (Paymob)
                    </button>
                )}
              </div>
          )}
        </div>
      </div>
  )
}

/* ---------------------------------------------------- */
/* ACTIVATE ACCOUNT VIEW */
/* ---------------------------------------------------- */
function ActivateAccountView({ onActivated, fail }) {
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] =
      useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (password.length < 8) {
      fail('Password must be at least 8 characters long.')
      return
    }

    if (password !== confirmPassword) {
      fail('Passwords do not match.')
      return
    }

    try {
      await adminAccountApi.activateAccount(password)
      onActivated()
    } catch (err) {
      fail(err.message)
    }
  }

  return (
      <section className="auth-card">
        <div>
          <p className="eyebrow">ACCOUNT ACTIVATION</p>

          <h1>Activate Admin Account</h1>

          <p
              style={{
                color: 'var(--text-muted)',
                fontSize: '0.9rem',
                marginTop: '0.5rem',
              }}
          >
            This account is currently inactive. Please set a
            new password to activate your account and gain
            access to the administration dashboard.
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          <label className="field">
            New Password (minimum 8 characters)

            <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                minLength={8}
            />
          </label>

          <label className="field">
            Confirm New Password

            <input
                type="password"
                value={confirmPassword}
                onChange={(e) =>
                    setConfirmPassword(e.target.value)
                }
                required
                minLength={8}
            />
          </label>

          <button className="primary wide">
            Activate Account
          </button>
        </form>
      </section>
  )
}

export default App
