#!/bin/bash

cat slovak_girls.txt | tr [:upper:] [:lower:] > zoznam_sk_dievcat.txt
for i in $( cat slovak_girls.txt ); do echo $i.jpg >> cesta_sk_dievcat.txt; done

DATE=$(date +%Y-%m-%d)
mena="zoznam_sk_dievcat.txt"
cesta="cesta_sk_dievcat.txt"

#mysql
MYSQL_SERVER="127.0.0.1"
MYSQL_USER="root"
MYSQL_PW="jahodka;"
MYSQL_DB="images_1"
MYSQL="/usr/bin/mysql"


while read -r -u4  line2 && read -u5 line3; do {
echo "INSERT INTO img_dict(string, image, url, date, id, cesta) VALUES ('$line2', '', '','$DATE','', '$line3');" > /tmp/mysql1.sql
$MYSQL -h $MYSQL_SERVER -u $MYSQL_USER -p$MYSQL_PW $MYSQL_DB < /tmp/mysql1.sql
}; done 4< $mena 5< $cesta

