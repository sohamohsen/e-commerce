INSERT INTO category (name, created_by) VALUES
                                            ('Electronics', 1),
                                            ('Clothing', 1),
                                            ('Home & Kitchen', 1),
                                            ('Books', 1),
                                            ('Sports & Outdoors', 1);

INSERT INTO product (
    category_id,
    name,
    description,
    price,
    quantity,
    is_active,
    image_url,
    created_by
) VALUES
      ((SELECT id FROM category WHERE name = 'Electronics'), 'Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 15.99, 50, TRUE, NULL, 1),
      ((SELECT id FROM category WHERE name = 'Electronics'), 'Mechanical Keyboard', 'RGB backlit mechanical keyboard, blue switches', 49.99, 30, TRUE, NULL, 1),
      ((SELECT id FROM category WHERE name = 'Electronics'), 'USB-C Charging Cable', '1m braided USB-C cable', 7.50, 200, TRUE, NULL, 1),
      ((SELECT id FROM category WHERE name = 'Electronics'), 'Bluetooth Headphones', 'Over-ear noise cancelling headphones', 89.99, 0, TRUE, NULL, 1),

      ((SELECT id FROM category WHERE name = 'Clothing'), 'Cotton T-Shirt', 'Plain crew neck t-shirt, 100% cotton', 9.99, 100, TRUE, NULL, 1),
      ((SELECT id FROM category WHERE name = 'Clothing'), 'Denim Jacket', 'Classic blue denim jacket', 39.99, 25, TRUE, NULL, 1),
      ((SELECT id FROM category WHERE name = 'Clothing'), 'Running Shoes', 'Lightweight breathable running shoes', 59.99, 40, TRUE, NULL, 1),

      ((SELECT id FROM category WHERE name = 'Home & Kitchen'), 'Non-Stick Frying Pan', '28cm non-stick frying pan', 22.00, 15, TRUE, NULL, 1),
      ((SELECT id FROM category WHERE name = 'Home & Kitchen'), 'Electric Kettle', '1.7L stainless steel electric kettle', 27.50, 20, TRUE, NULL, 1),
      ((SELECT id FROM category WHERE name = 'Home & Kitchen'), 'Ceramic Mug Set', 'Set of 4 ceramic mugs', 14.00, 0, FALSE, NULL, 1),

      ((SELECT id FROM category WHERE name = 'Books'), 'Clean Code', 'A Handbook of Agile Software Craftsmanship', 34.99, 12, TRUE, NULL, 1),
      ((SELECT id FROM category WHERE name = 'Books'), 'Effective Java', 'Best practices for the Java platform', 42.00, 8, TRUE, NULL, 1),

      ((SELECT id FROM category WHERE name = 'Sports & Outdoors'), 'Yoga Mat', 'Non-slip 6mm yoga mat', 18.99, 35, TRUE, NULL, 1),
      ((SELECT id FROM category WHERE name = 'Sports & Outdoors'), 'Water Bottle 1L', 'BPA-free reusable water bottle', 8.99, 60, TRUE, NULL, 1);

INSERT INTO user (
    name,
    email,
    phone,
    password,
    role,
    enabled,
    failed_login_attempt,
    account_locked,
    created_by
)
VALUES (
    'Super Admin',
    'superadmin@ecommerce.local',
    '01000000000',
    '$2a$12$KbJukdn5Qt.3u29LcT/9HOJ2XtI1uc9tBV42ljv.QYyzz1ufnhpMq',
    'SUPER_ADMIN',
    true,
    0,
    false,
    1
);