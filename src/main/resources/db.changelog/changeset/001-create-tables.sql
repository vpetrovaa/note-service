--liquibase formatted sql

--changeset vpetrovaa:create_note_tables

create schema if not exists notes_schema;
set schema 'notes_schema';

create table if not exists users
(
    id bigserial,
	email varchar(45) not null unique,
	first_name varchar(45) not null,
	last_name varchar(45) not null,
	primary key (id)
);

create table if not exists notes
(
    id bigserial,
	description varchar(200) not null,
	theme varchar(100) not null,
    tag varchar(100) not null,
	user_id bigint not null,
    foreign key (user_id) references users (id) on update cascade on delete cascade,
	primary key (id)
);