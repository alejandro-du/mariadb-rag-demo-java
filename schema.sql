CREATE TABLE IF NOT EXISTS products (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    url VARCHAR(2083), -- Maximum URL length as per standards
    final_price DECIMAL(10, 2),
    sku VARCHAR(100),
    currency VARCHAR(10),
    gtin VARCHAR(14), -- GTIN (UPC, EAN, etc.) is up to 14 digits
    specifications TEXT,
    image_urls TEXT, -- Large storage for multiple URLs
    top_reviews TEXT,
    rating_stars DECIMAL(3, 2),
    related_pages TEXT,
    available_for_delivery BOOLEAN,
    available_for_pickup BOOLEAN,
    brand VARCHAR(255),
    breadcrumbs TEXT,
    category_ids TEXT,
    review_count INT UNSIGNED,
    description TEXT,
    product_id VARCHAR(100),
    product_name VARCHAR(255),
    review_tags TEXT,
    category_url VARCHAR(2083),
    category_name VARCHAR(255),
    category_path TEXT,
    root_category_url VARCHAR(2083),
    root_category_name VARCHAR(255),
    upc VARCHAR(12), -- UPC is typically 12 digits
    tags TEXT,
    main_image VARCHAR(2083),
    rating DECIMAL(3, 2),
    unit_price DECIMAL(10, 2),
    unit VARCHAR(50),
    aisle VARCHAR(255),
    free_returns BOOLEAN,
    sizes TEXT,
    colors TEXT,
    seller VARCHAR(255),
    other_attributes JSON, -- Suitable for flexible data storage
    customer_reviews TEXT,
    ingredients TEXT,
    initial_price DECIMAL(10, 2),
    discount DECIMAL(5, 2),
    ingredients_full TEXT,
	categories TEXT,
	embedding VECTOR(1536)
) ENGINE=InnoDB;

LOAD DATA LOCAL INFILE '/walmart-products.csv'
INTO TABLE products
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(
    `timestamp`,
    `url`,
    `final_price`,
    `sku`,
    `currency`,
    `gtin`,
    `specifications`,
    `image_urls`,
    `top_reviews`,
    `rating_stars`,
    `related_pages`,
    `available_for_delivery`,
    `available_for_pickup`,
    `brand`,
    `breadcrumbs`,
    `category_ids`,
    `review_count`,
    `description`,
    `product_id`,
    `product_name`,
    `review_tags`,
    `category_url`,
    `category_name`,
    `category_path`,
    `root_category_url`,
    `root_category_name`,
    `upc`,
    `tags`,
    `main_image`,
    `rating`,
    `unit_price`,
    `unit`,
    `aisle`,
    `free_returns`,
    `sizes`,
    `colors`,
    `seller`,
    `other_attributes`,
    `customer_reviews`,
    `ingredients`,
    `initial_price`,
    `discount`,
    `ingredients_full`,
    `categories`
);
