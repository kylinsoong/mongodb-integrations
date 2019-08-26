#!/bin/bash

# Path to the directory that contains DBGEN binaries. e.g. dbgen 
DBGEN=~/tpch-dbgen

# Path to the jar file with JDBC driver of your Teiid Server
MDB_USER=root
MDB_PASSWD=root

#The following three variables need not to be changed 
QUERY_TEMPLATES=$(pwd)/query-templates
GENERATED_QUERIES=$(pwd)/generated-queries
GENERATED_DATA=$(pwd)/generated-data
