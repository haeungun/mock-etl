[![Build Status](https://travis-ci.org/haeungun/mock-etl.svg?branch=master)](https://travis-ci.org/haeungun/mock-etl)

## MockETL
Let's implement Mock ETL project with mock message queue and mock database.

## Data source
Random generated user log from [mockaroo.com](https://mockaroo.com/)

#### Json sample
```json
[  
   {  
      "country":"China",
      "gender":"Male",
      "last_name":"McLeese",
      "payment":364000,
      "id":98,
      "ip_address":"122.133.242.12",
      "first_name":"Alix",
      "age":57,
      "email":"amcleese2p@shop-pro.jp",
      "user_agent":"Mozilla/5.0 (Windows; U; Windows NT 5.2; en-US) AppleWebKit/534.17 (KHTML, like Gecko) Chrome/11.0.652.0 Safari/534.17"
   },
   {  
      "country":"China",
      "gender":"Male",
      "last_name":"Kopke",
      "payment":142600,
      "id":99,
      "ip_address":"100.45.224.212",
      "first_name":"Clayson",
      "age":40,
      "email":"ckopke2q@timesonline.co.uk",
      "user_agent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_7) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.790.0 Safari/535.1"
   },
   {  
      "country":"China",
      "gender":"Male",
      "last_name":"Shingler",
      "payment":196800,
      "id":100,
      "ip_address":"225.8.15.195",
      "first_name":"Guillermo",
      "age":33,
      "email":"gshingler2r@vk.com",
      "user_agent":"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/534.36 (KHTML, like Gecko) Chrome/13.0.766.0 Safari/534.36"
   }
]
```
**Mockaroo API has limitation with free plan which is can be called 1000 times per a day.**
If you want to test in your local machine, please add a new API endpoint on mockaroo.

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

