DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS status_bookings CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;


create sequence users_id_seq; --start 1 increment 1;
create sequence items_id_seq; --start 1 increment 1;
create sequence comments_id_seq;-- start 1 increment 1;
create sequence bookings_id_seq;-- start 1 increment 1;
create sequence status_bookings_id_seq;-- start 1 increment 1;




CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255)                            NOT NULL,
    description  VARCHAR(512)                            NOT NULL,
    is_available BOOLEAN                                 NOT NULL,
    owner_id     BIGINT                                  NOT NULL,
    CONSTRAINT pk_items PRIMARY KEY (id),
    CONSTRAINT fk_items_users FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE INDEX fk_items_index ON items (owner_id);

CREATE TABLE IF NOT EXISTS status_bookings
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(40) UNIQUE                      NOT NULL,
    CONSTRAINT pk_status_bookings PRIMARY KEY (id)
);

INSERT INTO status_bookings(title)
values ('APPROVED');
INSERT INTO status_bookings(title)
values ('REJECTED');
INSERT INTO status_bookings(title)
values ('WAITING');
INSERT INTO status_bookings(title)
values ('CANCELED');

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    booker_id  BIGINT                                  NOT NULL,
    status     VARCHAR(40)                             NOT NULL,
    CONSTRAINT pk_bookings PRIMARY KEY (id),
    CONSTRAINT fk_bookings_users FOREIGN KEY (booker_id) REFERENCES users (id),
    CONSTRAINT fk_bookings_items FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_bookings_status FOREIGN KEY (status) REFERENCES status_bookings (title) ON UPDATE CASCADE
);

CREATE INDEX fk_bookings_users_index ON bookings (booker_id);
CREATE INDEX fk_bookings_items_index ON bookings (item_id);
CREATE INDEX fk_bookings_status_index ON bookings (status);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(255)                            NOT NULL,
    item_id   BIGINT                                  NOT NULL,
    author_id BIGINT                                  NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_users FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_comments_items FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE INDEX fk_comments_users_index ON comments (author_id);
CREATE INDEX fk_comments_items_index ON comments (item_id);