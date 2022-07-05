INSERT INTO address (id, address_description, city, country, phone_number, postal_code) VALUES (111, 'Benjamin Street nr 14, second apartment', 'New York City', 'US', '+002412312', 'A42AFGA');
INSERT INTO address (id, address_description, city, country, phone_number, postal_code) VALUES (222, 'Calea Manastur 2-6', 'Cluj-Napoca', 'Romania', '+40722250000', '400372');
INSERT INTO address (id, address_description, city, country, phone_number, postal_code) VALUES (333, 'Oak Wood Street 3-6 buildings', 'San Jose', 'US', '+12931721', '120938129');

INSERT INTO supplier (id, email, name, url, address_id) VALUES (111, 'ceo@siemens.com', 'Siemens', 'www.siemens.com', 111);
INSERT INTO supplier (id, email, name, url, address_id) VALUES (222, 'relatii-clienti@emag.ro', 'Emag', 'www.emag.ro', 222);
INSERT INTO supplier (id, email, name, url, address_id)
VALUES (333, 'bruce@samsung.com', 'Samsung', 'www.samsung.com', 333);

INSERT INTO product_spec (id, color, capacity)
VALUES (111, 'Candy Red', '256GB');
INSERT INTO product_spec (id, color, capacity)
VALUES (222, 'Exquisite Green', '1TB');
INSERT INTO product_spec (id, color, capacity)
VALUES (555, 'Pearl White', '9 kgs');
INSERT INTO product_spec (id, color, capacity)
VALUES (666, 'Pristine Blue', '300 liters');
INSERT INTO product_spec (id, color, capacity)
VALUES (777, 'Plain Black', '500 liters');

INSERT INTO product (id, product_name, product_spec_id, price, description, supplier_id)
VALUES (111, 'iPhone 13 Pro Max', 111, 850, 'A very fast 120hz display phone', 222);
INSERT INTO product (id, product_name, product_spec_id, price, description, supplier_id)
VALUES (222, 'Samsung S22 Ultra', 222, 750, 'Latest Samsung Flagship', 222);
INSERT INTO product (id, product_name, product_spec_id, price, description, supplier_id)
VALUES (333, 'Samsung Fold-able', 222, 850, 'Mindblowing fold-able phone', 333);
INSERT INTO product (id, product_name, product_spec_id, price, description, supplier_id)
VALUES (444, 'Samsung Fold-able', 222, 700, 'Budget friendly fold-able with lower capacity', 333);
INSERT INTO product (id, product_name, product_spec_id, price, description, supplier_id)
VALUES (555, 'Budget Friendly Refrigerator', 666, 500, 'Fancy blue Refrigerator', 111);
INSERT INTO product (id, product_name, product_spec_id, price, description, supplier_id)
VALUES (666, 'Refrigerator', 777, 1000, 'Spacious refrigerator', 111);
INSERT INTO product (id, product_name, product_spec_id, price, description, supplier_id)
VALUES (777, 'Washing Machine', 555, 2500, 'Sparkling clean clothes', 111);


INSERT INTO category (id, category_name, description, image_url)
VALUES (111, 'Phones', 'Best Phones on Earth', 'some-cool-icon.gif');
INSERT INTO category (id, category_name, description, image_url)
VALUES (222, 'Home appliance', 'Whatever you need for your home!', 'some-cool-icon.gif');
INSERT INTO category (id, category_name, description, image_url) VALUES (333, 'Budget items', 'Best prices', 'some-cool-icon.gif');


INSERT INTO product_category (product_id, category_id) VALUES (111, 111);
INSERT INTO product_category (product_id, category_id) VALUES (222, 111);
INSERT INTO product_category (product_id, category_id) VALUES (333, 111);
INSERT INTO product_category (product_id, category_id) VALUES (444, 111);
INSERT INTO product_category (product_id, category_id) VALUES (555, 222);
INSERT INTO product_category (product_id, category_id) VALUES (666, 222);
INSERT INTO product_category (product_id, category_id) VALUES (777, 222);
INSERT INTO product_category (product_id, category_id) VALUES (444, 333);
INSERT INTO product_category (product_id, category_id) VALUES (555, 333);