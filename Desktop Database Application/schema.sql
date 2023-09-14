create table users(
    id char(9),
    rank varchar(15),
    hasAccess char(1),
    primary key (id)
);

create table access(
    id char(9),
    type numeric(1),
    date char(10),
    time char(8),
    primary key (id, date, time),
    foreign key (id) references users
);