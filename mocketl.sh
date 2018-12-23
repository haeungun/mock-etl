#!/bin/bash
SQL_PATH=./src/main/resources

sqlite3 ${SQL_PATH}/mocketl.db < ${SQL_PATH}/mocketl.sql 
