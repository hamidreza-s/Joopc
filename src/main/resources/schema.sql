create table users
(
    id           varchar primary key,
    username     varchar unique,
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

create table invitations
(
    id         varchar primary key,
    event_id   varchar,
    user_id    varchar,
    invitee_id varchar,
    rsvp       varchar check (rsvp in ('yes', 'maybe', 'no')),
    note       varchar,
    valid      boolean,
    expired_at timestamp,
    create_at  timestamp
);

create table members
(
    id        varchar primary key,
    event_id  varchar,
    user_id   varchar,
    role      varchar check (role in ('owner', 'host', 'guest')),
    create_at timestamp
);

create table polls
(
    id           varchar primary key,
    event_id     varchar,
    type         varchar check (type in ('time', 'place')),
    value        varchar,
    finalised    boolean,
    finalised_at timestamp,
    create_at    timestamp
);

create table votes
(
    id        varchar primary key,
    event_id  varchar,
    poll_id   varchar,
    user_id   varchar,
    vote      varchar check (vote in ('up', 'down')),
    note      varchar,
    create_at timestamp
);

create table checklists
(
    id          varchar primary key,
    event_id    varchar,
    user_id     varchar,
    item        varchar,
    assignee_id varchar,
    note        varchar,
    create_at   timestamp
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
