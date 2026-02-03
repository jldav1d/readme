INSERT INTO users (username, password, role) VALUES
 ('alice_reads', 'hash_pass_1', 'user'),
 ('bob_builder', 'hash_pass_2', 'user'),
 ('charlie_admin', 'hash_pass_3', 'user'),
 ('bookworm_dana', 'hash_pass_4', 'user'),
 ('eddie_lit', 'hash_pass_5', 'user');

INSERT INTO books (title, author, description, slug, price, stock, published_at) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', 'A classic tale of wealth and love.', 'the-great-gatsby', 15.99, 45, '1992-04-10 09:00:00'),
('1984', 'George Orwell', 'Dystopian future society.', '1984-orwell', 12.50, 30, '1987-06-08 09:00:00'),
('The Hobbit', 'J.R.R. Tolkien', 'A fantasy adventure.', 'the-hobbit', 18.00, 25, '1972-09-21 09:00:00'),
('Atomic Habits', 'James Clear', 'Small changes, remarkable results.', 'atomic-habits', 22.00, 100, '2018-10-16 09:00:00'),
('Project Hail Mary', 'Andy Weir', 'Space survival sci-fi.', 'project-hail-mary', 25.00, 12, '2021-05-04 09:00:00');


INSERT INTO categories (name) VALUES
('Fiction'),
('Sci-Fi'),
('Non-Fiction'),
('Fantasy'),
('Classic');

INSERT INTO book_categories (category_id, book_id) VALUES
(5, 1),
(1, 2),
(4, 3),
(3, 4),
(2, 5);

INSERT INTO cart (user_id) VALUES (1), (2), (3), (4), (5);

INSERT INTO cart_items (cart_id, book_id, quantity) VALUES
(1, 4, 1),
(2, 3, 2),
(4, 5, 1),
(5, 1, 1),
(5, 2, 1);

INSERT INTO orders (user_id, total_price) VALUES
(1, 15.99),
(2, 25.00),
(4, 40.00),
(1, 12.50),
(5, 18.00);

INSERT INTO order_items (order_id, book_id, purchased_price, quantity) VALUES
(1, 1, 15.99, 1),
(2, 5, 25.00, 1),
(3, 4, 22.00, 1),
(3, 3, 18.00, 1),
(4, 2, 12.50, 1);