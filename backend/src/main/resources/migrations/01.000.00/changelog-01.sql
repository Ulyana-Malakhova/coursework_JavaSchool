create table documents (
    id serial primary key,
    type text,
    organization text,
    date date,
    description text,
    patient text,
    state text
);