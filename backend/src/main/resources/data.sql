
INSERT INTO MENU_ITEM (NAME, HAS_SIZE, PRICE_REGULAR, PRICE_LARGE, IS_AVAILABLE, IS_DELETED) VALUES
('Americano', true, 1.50, 2.00, true, false),
('Americano with milk', true, 2.00, 2.50, true, false),
('Latte', true, 2.50, 3.00, true, false),
('Cappuccino', true, 2.50, 3.00, true, false),
('Hot Chocolate', true, 2.00, 2.50, true, false),
('Mocha', true, 2.50, 3.00, true, false),
('Mineral Water', false, 1.00, 0.00, true, false);

INSERT INTO BUSINESS_HOUR (DAY_OF_WEEK, OPEN_TIME, CLOSE_TIME, CLOSED) VALUES
('Monday', '06:30', '19:00', false),
('Tuesday', '06:30', '19:00', false),
('Wednesday', '06:30', '19:00', false),
('Thursday', '06:30', '19:00', false),
('Friday', '06:30', '19:00', false),
('Saturday', '07:00', '18:00', false),
('Sunday', null, null, true);

INSERT INTO STATION_SETTING (STATION_NAME, KIOSK_NAME) VALUES
('Cramlington Station', 'Whistlestop Coffee Hut');

INSERT INTO STAFF (name, email, password)
VALUES ('Admin', 'admin@coffee.com', '1234');