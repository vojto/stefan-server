#!/bin/bash
# dievocky

# priprava mien chlapcov + mena obrazkov podla mien chlapcov pre natlacenie do DB
#cat boy.txt | tr [:upper:] [:lower:] > /tmp/zoznam_chlapcov.txt
# uppercase to lower case
#for file in *.jpg ; do mv $file `echo $file | sed 's/./\l&/g'` ; done

# v adresari z vygenerovanymi menami chlapcov mi napise uplnu cestu, aby som ich mohla tlacit do DB
#for i in $( ls ); do echo "/home/cnluser/obrazky/"$i > cesta_k_obr_mien_chlapcov.txt ; done
#  for i in $( ls ); do echo "/home/cnluser/obrazky/"$i; done
# for i in $( ls ); do echo "/home/cnluser/obrazky/"$i >> cesta_k_obr_mien_chlapcov.txt; done
#for i in $( cat /tmp/zoznam_chlapcov.txt ); do echo "/home/cnluser/obrazky/"$i >> /tmp/cesta_boys.txt; done

# parsovanie dievcat
# cat girl.txt | sed 's/,/\n/g' | sed '/^$/d'
# for i in $( cat /tmp/zoznam_dievcat.txt ); do echo "/home/cnluser/obrazky/"$i.jpg >> /tmp/cesta_girls.txt; done

#cat girl.txt | sed 's/,/\n/g' | sed '/^$/d'
cat girl.txt | tr [:upper:] [:lower:] > zoznam_dievcat.txt
for i in $( cat zoznam_dievcat.txt ); do echo $i.jpg >> cesta_girls.txt; done

DATE=$(date +%Y-%m-%d)
mena="zoznam_dievcat.txt"
cesta="cesta_girls.txt"

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

