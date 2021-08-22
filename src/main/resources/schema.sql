create table users
(
    id    varchar primary key,
    phone varchar unique
);

create table contacts
(
    id            varchar primary key,
    user_id       varchar,
    contact_name  varchar,
    contact_phone varchar
);

create table events
(
    id      varchar primary key,
    user_id varchar
);

create table invitations
(
    id         varchar primary key,
    event_id   varchar,
    user_id    varchar,
    invitee_id varchar,
    accepted   boolean
);

create table members
(
    id       varchar primary key,
    event_id varchar,
    user_id  varchar,
    role     varchar check (role in ('host', 'guest'))
);

create table suggestions
(
    id       varchar primary key,
    event_id varchar,
    user_id  varchar,
    type     varchar check (type in ('time', 'place', 'equipment')),
    value    varchar
);

create table reactions
(
    id            varchar primary key,
    event_id      varchar,
    suggestion_id varchar,
    user_id       varchar,
    accepted      boolean
);

create table messages
(
    id         varchar primary key,
    event_id   varchar,
    user_id    varchar,
    content    varchar,
    created_at timestamp
);

create table photos
(
    id         varchar primary key,
    event_id   varchar,
    user_id    varchar,
    path       varchar,
    created_at timestamp
);

create table voices
(
    id        varchar primary key,
    event_id  varchar,
    user_id   varchar,
    path      varchar,
    create_at timestamp
);

create table receipts
(
    id         varchar primary key,
    event_id   varchar,
    user_id    varchar,
    path       varchar,
    sum        varchar,
    created_at timestamp
);
