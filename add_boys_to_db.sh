#!/bin/bash
# American boys names
cat boy.txt | tr [:upper:] [:lower:] > boys_list
for i in $( cat boys_list ); do echo $i.jpg >> path_boys; done

# variables 
DATE=$(date +%Y-%m-%d)
names="boys_list"
cesta="path_boys"

# mysql variables needed to connect to database
MYSQL_SERVER="127.0.0.1"
MYSQL_USER="root"
MYSQL_PW="jahodka;"
MYSQL_DB="images_1"
MYSQL="/usr/bin/mysql"

# reading two files where are searched strings inserted to input argumet - this is located into db to string column
# second file than is readed in loop until the file are processed and each line is inserted to new row in database
while read -r -u4  line2 && read -u5 line3; do {
echo "INSERT INTO img_dict(string, image, url, date, id, cesta) VALUES ('$line2', '', '','$DATE','', '$line3');" > /tmp/mysql1.sql
$MYSQL -h $MYSQL_SERVER -u $MYSQL_USER -p$MYSQL_PW $MYSQL_DB < /tmp/mysql1.sql
}; done 4< $names 5< $cesta
