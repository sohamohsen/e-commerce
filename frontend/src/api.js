const API_BASE = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '')

export const getToken = () => localStorage.getItem('token')

export async function api(path, { method = 'GET', body, query, auth = true, isFormData = false } = {}) {
  const headers = {}
  if (!isFormData) {
    headers['Content-Type'] = 'application/json'
  }

  if (auth && getToken()) {
    headers.Authorization = `Token ${getToken()}`
  }

  let url = `${API_BASE}${path}`
  if (query) {
    const params = new URLSearchParams()
    Object.entries(query).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params.append(key, value)
      }
    })
    const queryString = params.toString()
    if (queryString) {
      url += (url.includes('?') ? '&' : '?') + queryString
    }
  }

  const options = {
    method,
    headers,
  }

  if (body) {
    options.body = isFormData ? body : JSON.stringify(body)
  }

  const response = await fetch(url, options)

  if (response.status === 204) return null

  const payload = await response.json().catch(() => ({}))

  if (!response.ok) {
    const errorMsg = payload.message || payload.error || 'An unexpected error occurred while connecting to the server.'
    throw new Error(errorMsg)
  }

  return payload.data !== undefined ? payload.data : payload
}

export function parseJwt(token) {
  if (!token) return null
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
        atob(base64)
            .split('')
            .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
            .join('')
    )
    return JSON.parse(jsonPayload)
  } catch {
    return null
  }
}

export function readRole(token) {
  const payload = parseJwt(token)
  return payload?.role || 'CUSTOMER'
}

export function readUsername(token) {
  const payload = parseJwt(token)
  return payload?.sub || payload?.email || payload?.username || 'User'
}

// Authentication API endpoints
export const authApi = {
  registerCustomer: (data) => api('/auth/customer/register', { method: 'POST', body: data, auth: false }),
  loginCustomer: (data) => api('/auth/customer/login', { method: 'POST', body: data, auth: false }),
  loginAdmin: (data) => api('/auth/admin/login', { method: 'POST', body: data, auth: false }),
  logout: () => api('/user/logout', { method: 'POST' }),
}

// Product API endpoints
export const productApi = {
  getProducts: (query) => api('/product', { query, auth: false }),
  getProductById: (id) => api(`/product/${id}`, { auth: false }),
}

// Category API endpoints
export const categoryApi = {
  getCategories: (query) => api('/category', { query, auth: false }),
  getCategoryById: (id) => api(`/category/${id}`, { auth: false }),
}

// Shopping Cart API endpoints
export const cartApi = {
  getCart: () => api('/cart'),
  addToCart: (productId, quantity = 1) => api('/cart/items', { method: 'POST', body: { productId, quantity } }),
  updateCartItem: (cartItemId, quantity) => api(`/cart/items/${cartItemId}`, { method: 'PATCH', body: { quantity } }),
  removeCartItem: (cartItemId) => api(`/cart/items/${cartItemId}`, { method: 'DELETE' }),
}

// Order API endpoints
export const orderApi = {
  checkout: () => api('/orders/checkout', { method: 'POST' }),
  getMyOrders: (query) => api('/orders', { query }),
  getOrderDetails: (id) => api(`/orders/${id}`),
}

// Payment API endpoints
export const paymentApi = {
  initiatePaymob: (orderId) => api(`/payment/orders/${orderId}/pay`, { method: 'POST' }),
}

// Notification API endpoints
export const notificationApi = {
  getNotifications: (query) => api('/notifications', { query }),
  getUnreadCount: () => api('/notifications/count'), // This should return ONLY unread count
  markAsRead: (id) => api(`/notifications/${id}/read`, { method: 'PATCH' }),
  markAllAsRead: () => api('/notifications/read-all', { method: 'PATCH' }),
}

// Admin Product API endpoints
export const adminProductApi = {
  getProducts: (query) => api('/admin-products', { query }),
  getProductById: (id) => api(`/admin-products/${id}`),
  createProduct: (productData, imageFile) => {
    const formData = new FormData()
    const jsonBlob = new Blob([JSON.stringify(productData)], { type: 'application/json' })
    formData.append('request', jsonBlob)
    if (imageFile) {
      formData.append('image', imageFile)
    }
    return api('/admin-products', { method: 'POST', body: formData, isFormData: true })
  },
  updateProduct: (id, productData, imageFile) => {
    const formData = new FormData()
    const jsonBlob = new Blob([JSON.stringify(productData)], { type: 'application/json' })
    formData.append('request', jsonBlob)
    if (imageFile) {
      formData.append('image', imageFile)
    }
    return api(`/admin-products/${id}`, { method: 'PATCH', body: formData, isFormData: true })
  },
  updateStock: (id, data) => api(`/admin-products/${id}/stock`, { method: 'PATCH', body: data }),
  toggleStatus: (id) => api(`/admin-products/${id}/status`, { method: 'PATCH' }),
  deleteProduct: (id) => api(`/admin-products/${id}`, { method: 'DELETE' }),
}

// Admin Category API endpoints
export const adminCategoryApi = {
  getCategories: (query) => api('/admin-categories', { query }),
  getCategoryById: (id) => api(`/admin-categories/${id}`),
  createCategory: (data) => api('/admin-categories', { method: 'POST', body: data }),
  updateCategory: (id, data) => api(`/admin-categories/${id}`, { method: 'PATCH', body: data }),
  deleteCategory: (id) => api(`/admin-categories/${id}`, { method: 'DELETE' }),
}

// Admin Order API endpoints
export const adminOrderApi = {
  getOrders: (query) => api('/admin-orders', { query }),
  getOrderDetails: (id) => api(`/admin-orders/${id}`),
  updateStatus: (id, status) => api(`/admin-orders/${id}/status`, { method: 'PATCH', body: { status } }),
  cancelOrder: (id, reason) => api(`/admin-orders/${id}/cancel`, { method: 'POST', body: { reason } }),
}

// Super Admin API endpoints
export const superAdminApi = {
  createAdmin: (data) => api('/super-admin/admin', { method: 'POST', body: data }),
  toggleLockAccount: (userId) => api(`/super-admin/lock-account/${userId}`, { method: 'POST' }),
}

// Admin Account API endpoints
export const adminAccountApi = {
  activateAccount: (password) => api('/admin/enable', { method: 'POST', body: { password } }),
}

// Customer support chat API endpoints
export const chatApi = {
  startConversation: () => api('/chat/conversation', { method: 'POST' }),
  getCurrentConversation: () => api('/chat/conversations/current'),
  getWaitingConversations: () => api('/chat/conversations/waiting'),
  acceptConversation: (id) => api(`/chat/conversations/${id}/accept`, { method: 'POST' }),
  getMessages: (id) => api(`/chat/conversations/${id}/messages`),
  sendMessage: (id, content) => api(`/chat/conversations/${id}/messages`, { method: 'POST', body: { content } }),
  closeConversation: (id) => api(`/chat/conversations/${id}/close`, { method: 'POST' }),
}
