create table users
(
    id           varchar primary key,
    username     varchar unique,
    password     varchar unique,
    phone        varchar unique,
    email        varchar unique,
    name         varchar,
    active       boolean,
    activated_at timestamp,
    created_at   timestamp
);

create table events
(
    id           varchar primary key,
    user_id      varchar,
    type         varchar check (type in ('indoor', 'outdoor', 'remote')),
    finalised    boolean,
    finalised_at timestamp,
    create_at    timestamp
);