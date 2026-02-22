CREATE TABLE todo
(
    id         uuid         NOT NULL DEFAULT gen_random_uuid(),
    topic      varchar(255) NOT NULL,
    details    text,
    due_date   date,
    due_time   time,
    completed  boolean      NOT NULL DEFAULT false,
    created_at timestamptz  NOT NULL,
    updated_at timestamptz  NOT NULL,
    PRIMARY KEY (id)
);
