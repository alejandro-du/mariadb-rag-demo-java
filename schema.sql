CREATE TABLE IF NOT EXISTS products (
	id SERIAL PRIMARY KEY,
    asin VARCHAR(20),
    title VARCHAR(255),
    img_url VARCHAR(255),
    product_url VARCHAR(255),
    stars DECIMAL(2, 1),
    reviews INT,
    price DECIMAL(10, 2),
    list_price DECIMAL(10, 2),
    category_name VARCHAR(100),
    is_best_seller BOOLEAN,
    bought_in_last_month INT,
	embedding VECTOR(768)
);
