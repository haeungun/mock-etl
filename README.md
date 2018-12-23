[![Build Status](https://travis-ci.org/haeungun/mock-etl.svg?branch=master)](https://travis-ci.org/haeungun/mock-etl)

## MockETL
Let's implement Mock ETL project with mock message queue and mock database.

## Data source
Random generated user log from [mockaroo.com](https://mockaroo.com/)

## Doing what?
Retrive user log data from REST API and get statistics per given time
(ex: Sum of payment group by country, Sum of payment group by gender)

## Implemantation and limitation
| Origin     	| Mock            	| Implementation 	                        | Limitation           	|
|------------	|-----------------	|---------------------------------------	|----------------------	|
| Kafka      	| In-memory Queue 	| Multiple topics, producers, consumers 	| No broker, No offset 	|
| MySQL etc. 	| SQLite3         	| DBMS features  	                        | Different behavior   	|

Not yet support one topic with multiple consumer.

## Database init
``` bash
$> ./mocketl.sh
```

## Test & Build
``` bash
$> ./gradlew test
$> ./gradlew build
```
