create table if not exists locations
(
    code         varchar(12)  not null
        primary key,
    city_name    varchar(128) not null,
    country_code varchar(2)   not null,
    country_name varchar(64)  not null,
    enabled      bit          null,
    region_name  varchar(128) null,
    trashed      bit          null
);
create table if not exists realtime_weather
(
    location_code varchar(12) not null
        primary key,
    humidity      int         not null,
    last_updated  datetime(6) null,
    precipitation int         not null,
    status        varchar(10) not null,
    temperature   int         not null,
    wind_speed    int         not null,
    constraint foreign key (location_code) references locations (code)
);

create table if not exists weather_hourly
(
    hour_of_day   int         not null,
    precipitation int         not null,
    status        varchar(12) not null,
    temperature   int         not null,
    location_code varchar(12) not null,
    primary key (hour_of_day, location_code),
    constraint
        foreign key (location_code) references locations (code)
);


