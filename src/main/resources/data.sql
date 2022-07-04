INSERT INTO address (id, address_description, city, country, phone_number, postal_code) VALUES (1, 'Benjamin Street nr 14, second apartment', 'New York City', 'US', '+002412312', 'A42AFGA');
INSERT INTO supplier (id, email, name, url, address_id) VALUES (1, 'ceo@siemens.com', 'Siemens', 'www.siemens.com', 1);
INSERT INTO product_spec (id, color, capacity) VALUES (1, 'Candy Red', '256 GB');
INSERT INTO product (product_name, product_spec_id, price, description, supplier_id) VALUES ('iPhone 13 Pro Max', 1, 250, 'A very fast 120hz display phone', 1);