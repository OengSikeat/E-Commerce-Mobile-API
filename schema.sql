CREATE TABLE app_users
(
    user_id        serial PRIMARY KEY NOT NULL,
    full_name VARCHAR(50)        NOT NULL,
    email     VARCHAR(50)        NOT NULL,
    password  VARCHAR(255)       NOT NULL
);

CREATE TABLE app_roles
(
    role_id        serial PRIMARY KEY NOT NULL,
    name VARCHAR(50)        NOT NULL
);

CREATE TABLE app_user_role
(
    user_id INT NOT NULL REFERENCES app_users (user_id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES app_roles (role_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);
CREATE TABLE blacklisted_tokens(
    token_id serial PRIMARY KEY NOT NULL,
    token VARCHAR(500) NOT NULL,
    expiry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null
)