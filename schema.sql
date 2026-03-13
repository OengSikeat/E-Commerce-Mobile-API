CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255) NOT NULL,
                       profile VARCHAR(500),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(10,2) NOT NULL,
                          image_url VARCHAR(500),
                          size_options VARCHAR(255),
                          category VARCHAR(100) DEFAULT 'OTHERS',
                          discount_percentage DECIMAL(5,2) DEFAULT 0,
                          created_by INTEGER,
                          on_promotion BOOLEAN DEFAULT FALSE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_products_created_by
                              FOREIGN KEY (created_by)
                                  REFERENCES users(id)
                                  ON DELETE SET NULL
);

CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        user_id INTEGER NOT NULL,
                        product_id INTEGER NOT NULL,
                        quantity INTEGER DEFAULT 1,
                        total_amount DECIMAL(10,2) NOT NULL,
                        status VARCHAR(20) DEFAULT 'pending',
                        address VARCHAR(255),
                        city VARCHAR(100),
                        state VARCHAR(100),
                        country VARCHAR(100),
                        postcode VARCHAR(20),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_orders_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id)
                                ON DELETE CASCADE,

                        CONSTRAINT fk_orders_product
                            FOREIGN KEY (product_id)
                                REFERENCES products(id)
                                ON DELETE CASCADE,

                        CONSTRAINT chk_order_status
                            CHECK (status IN ('pending', 'shipped', 'delivered'))
);

CREATE TABLE waitlists (
                           id SERIAL PRIMARY KEY,
                           user_id INTEGER NOT NULL,
                           product_id INTEGER NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_waitlists_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT fk_waitlists_product
                               FOREIGN KEY (product_id)
                                   REFERENCES products(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT uq_waitlists_user_product
                               UNIQUE (user_id, product_id)
);
