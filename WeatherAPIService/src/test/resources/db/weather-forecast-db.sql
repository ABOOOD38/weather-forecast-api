insert into locations (code, city_name, country_code, country_name, enabled, region_name, trashed)
values ('NYC_USA', 'New York City', 'US', 'United States of America', true, 'New York', false),
       ('LACA_USA', 'los Angeles', 'US', 'United States of America', true, 'California', false),
       ('DELHI_IN', 'New Delhi', 'IN', 'India', true, 'Delhi', false),
       ('MBMH_IN', 'Mumbai', 'IN', 'India', true, 'Maharashtra', false),
       ('AMMAN_JO', 'AMMAN', 'JO', 'Jordan', true, 'Jordan', true);


insert into realtime_weather(location_code, humidity, last_updated, precipitation, status, temperature, wind_speed)
VALUES ('DELHI_IN', 60, '2023-04-15T12:22:23.692224415', 70, 'Sunny', 10, 10),
       ('NYC_USA', 30, '2023-04-15T12:22:23.8', 40, 'Snowy', -1, 15);

insert into weather_hourly (hour_of_day, location_code, precipitation, status, temperature)
values (1, 'MBMH_IN', 60, 'Cloudy', 20),
       (2, 'MBMH_IN', 61, 'Cloudy', 21),
       (3, 'MBMH_IN', 62, 'Cloudy', 22),
       (4, 'MBMH_IN', 63, 'Sunny', 23),
       (11, 'NYC_USA', 50, 'Snowy', -5),
       (12, 'NYC_USA', 52, 'Snowy', -7),
       (13, 'NYC_USA', 49, 'Snowy', -2),
       (14, 'NYC_USA', 54, 'Snowy', 0);
