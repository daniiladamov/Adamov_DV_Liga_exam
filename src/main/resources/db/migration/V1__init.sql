create table boxes (id int8 generated by default as identity, close time default '20:00', open time default '08:00',
ratio float8 not null, primary key (id));

create table employees (id int8 generated by default as identity, box_id int8 not null, user_id int8 not null,
discount_max int4, discount_min int4, primary key (id));

create table order_operation (order_id int8 not null, operation_id int8 not null, primary key (order_id, operation_id));

create table orders (id int8 generated by default as identity, order_status varchar(255) not null default 'ACTIVE',
"date" date,end_time time, start_time time, box_id int8 not null, user_id int8 not null, cost numeric(19, 2) not null,
confirm boolean default false,primary key (id));

create table operations (id int8 generated by default as identity, cost numeric(19, 2) not null, duration int4 not null,
name varchar(255), primary key (id));

create table users (id int8 generated by default as identity, first_name varchar(255),
last_name varchar(255), password varchar(255), "role" varchar(255) not null, surname varchar(255), username varchar(255),
uuid varchar(255), primary key (id));

alter table if exists users add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);
alter table if exists operations add constraint UKsd356iaf9aasdgdsfh135gsdfg unique ("name");
alter table if exists bills add constraint FK2s1iwv6bgsmh8u9awhdd1aela foreign key (order_id) references orders;
alter table if exists employees add constraint FKhi50wt4fqcn5pn04b7q1ltqpo foreign key (box_id) references boxes;
alter table if exists employees add constraint FKl0l2t3xcxfbxi28r945vto8us foreign key (user_id) references users;
alter table if exists order_operation add constraint FKgc67yeomlx546t08eep9k3sc8 foreign key (operation_id) references operations;
alter table if exists order_operation add constraint FKq63mqdm9abe2kw0hsa1qh4pdw foreign key (order_id) references orders;
alter table if exists orders add constraint FK1cci2xp4x6vjsrgipxr7fa4eu foreign key (box_id) references boxes;
alter table if exists orders add constraint FK32ql8ubntj5uh44ph9659tiih foreign key (user_id) references users;


insert into boxes(ratio) values (1.1), (1.2), (0.8);

insert into users( first_name, last_name, password, "role", surname, username) values
('Адамов', 'Даниил','$2a$08$J55DI/.0P1CZZDuQa7dqXO1Zsbpl7CEApX2sFG9PUi.yV7MHE0K4e','ROLE_ADMIN','Васильевич','admin'),
('Работающий', 'Работяга','$2a$08$J55DI/.0P1CZZDuQa7dqXO1Zsbpl7CEApX2sFG9PUi.yV7MHE0K4e','ROLE_EMPLOYEE',null,'empl'),
('Заказывающий', 'Заказчик','$2a$08$J55DI/.0P1CZZDuQa7dqXO1Zsbpl7CEApX2sFG9PUi.yV7MHE0K4e','ROLE_USER',null,'user'),
('Заказывалов', 'Заказ','$2a$08$J55DI/.0P1CZZDuQa7dqXO1Zsbpl7CEApX2sFG9PUi.yV7MHE0K4e','ROLE_USER',null,'user1'),
('Работающий', 'Работяга','$2a$08$J55DI/.0P1CZZDuQa7dqXO1Zsbpl7CEApX2sFG9PUi.yV7MHE0K4e','ROLE_EMPLOYEE',null,'empl1'),
('Работающий', 'Работяга','$2a$08$J55DI/.0P1CZZDuQa7dqXO1Zsbpl7CEApX2sFG9PUi.yV7MHE0K4e','ROLE_EMPLOYEE',null,'empl2');

insert into employees(box_id, user_id)values (1, 2), (2,5), (3,6);

insert into operations(cost, duration, name) values (300, 15,'мойка базовая'), (1200, 25,'химчистка салона'),
(200, 10,'мойка двигателя'), (250, 8,'сушка'), (400,16,'полировка');

insert into orders ("date",start_time,end_time,user_id,box_id,cost)
values (current_date,'15:00:00','15:15:00',3,1,200),(current_date,'15:10:00','15:25:00',4,2,300),
       (current_date,'15:10:00','15:35:00',3,3,250),(current_date,'13:00:00','13:15:00',4,1,200),
       (current_date,'13:10:00','13:25:00',4,2,300),(current_date,'13:10:00','13:35:00',3,3,250);