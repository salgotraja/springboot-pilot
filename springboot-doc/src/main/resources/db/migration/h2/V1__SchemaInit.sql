create sequence user_id_seq start with 1 increment by 50;
create sequence bookmark_id_seq start with 1 increment by 50;
create sequence refresh_token_seq start with 1 increment by 50;

create table users
(
    id       bigint DEFAULT nextval('user_id_seq') not null,
    email    varchar(255)                          not null,
    password varchar(255)                          not null,
    role     varchar(20)                           not null,
    name     varchar(255)                          not null,
    primary key (id)
);

create table bookmarks
(
    id    bigint DEFAULT nextval('bookmark_id_seq') not null,
    title varchar(255)                              not null,
    url   varchar(255)                              not null,
    primary key (id)
);

CREATE TABLE refresh_token (
                               id BIGINT DEFAULT nextval('refresh_token_seq') PRIMARY KEY,
                               token VARCHAR(255),
                               expiry_date TIMESTAMP,
                               user_id BIGINT,
                               CONSTRAINT fk_user
                                   FOREIGN KEY(user_id)
                                       REFERENCES users(id)
);