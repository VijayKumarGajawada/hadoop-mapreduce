create table imdb(
color string,
year int,
score float,
title string,
movielikes int,
duration int,
criticreviews int,
userreviews int,
votedusers int,
director string,
directorlikes int,
actor1 string,
actor1likes int,
actor2 string,
actor2likes int,
actor3 string,
actor3likes int,
totalcastlikes int,
budget bigint,
gross bigint,
genres string,
facesinposter int,
plotkeywords string,
lang string,
country string,
contentrating string,
aspectratio float,
imdblink string)
row format delimited
fields terminated by ","
stored as textfile;
-----------------------------
load data local inpath '/home/hduser/Downloads/IMDBmovie.csv' overwrite into table imdb;
------------------------------


18. Top 10 B&W films highest number of critic reviews vs IMDB score

select title, criticreviews as c, score 
from imdb 
where color = " Black and White" 
order by c desc 
limit 10;

------------------------------

23. Top 20 FB likes – Movies with IMDB scores with plotkeywords

INSERT OVERWRITE LOCAL DIRECTORY '/home/hduser/Documents/hive queries/imdb/23'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
select movielikes as likes, title, score, plotkeywords 
from imdb 
order by likes desc 
limit 20;

-------------------------------
24. Top 10 FB Directorlikes with his movielikes and IMDB scores

INSERT OVERWRITE LOCAL DIRECTORY '/home/hduser/Documents/hive queries/imdb/24'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
select b.likes, a.director, a.year, a.title, a.movielikes, a.score 
from imdb a 
inner join (select distinct directorlikes as likes from imdb order by likes desc limit 10) b 
on b.likes = a.directorlikes 
where a.movielikes!= 0 
order by b.likes desc, a.movielikes desc; 

--------------------------------




























