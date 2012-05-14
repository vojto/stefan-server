#!/bin/bash

# Author: Veronika Klauzova

######################################
#   Image google-search engine       #
######################################

# control input strings
if [ $# -lt 1 ] ; then
    exit 1;
fi

# each input string are converted to lowercase strings
SEARCH_STRING=$(echo $1 | tr [:upper:] [:lower:])

# Variables
# Whole path to RUBY binary file
RUBY="/usr/bin/ruby"

IMAGE_SEQ_OUTPUT="1"

# Temporary searching file
IMAGE_SEARCH_RESULTS="/tmp/image_search"

# search first 3 matches on google-search images
function search_google_image() {
    $RUBY /home/cnluser/phrases.rb "$SEARCH_STRING" $IMAGE_SEQ_OUTPUT > $IMAGE_SEARCH_RESULTS
}


# Variables
SAVED_IMAGES=`echo $IMG | sed  's/\-.*//'`
IMG=`ls /home/cnluser/obrazky/ | grep "$SEARCH_STRING"`
SAVED_IMAGES=`echo $IMG | sed  's/\-.*//'`

# Path to output directory for donwloaded pictures
IMAGES="/home/cnluser/obrazky"

# Path to temporary urls of pictures
IMAGE_URL="/tmp/url"

# Date format
DATE=$(date +%Y-%m-%d)

# Path tp MySQL connector binary
MYSQL="/usr/bin/mysql"

# IP addres of MySQL server
MYSQL_SERVER="127.0.0.1"

# MySQL user 
MYSQL_USER="root"

# MySQL password
MYSQL_PW="jahodka;"

# MySQL database
MYSQL_DB="images_1"


## Function for searching strings and files of pictures on the disk
function searched_string_and_files_on_disk () {

# check if searched string is in our database    
    if [ "$SAVED_IMAGES" = "$SEARCH_STRING" ];
            then    
                # true branch
                echo "$SEARCH_STRING"
                exit 0
    else 
                # false branch
                # google images function calling
                search_google_image

                # Matchign of URL's of pictures
                cat $IMAGE_SEARCH_RESULTS | grep http > $IMAGE_URL

                # download images when we don't have it in "cache" inside mysql database
                wget_images    

                # insert images into database
                echo "INSERT INTO img_dict(string, image, url, date, id, cesta) VALUES ('$SEARCH_STRING', '', '`cat $IMAGE_URL`','$DATE','', '$SEARCH_STRING.jpg');" > /tmp/mysql.sql
                # connection to mysql server
                 $MYSQL -h $MYSQL_SERVER -u $MYSQL_USER -p$MYSQL_PW $MYSQL_DB < /tmp/mysql.sql

                exit 0
        fi
}

## Function which downloading pictures
function wget_images(){
        VAR=0;

        # for each URL address we printed search-string name for jpg file
        echo "$SEARCH_STRING"
        for file in `cat $IMAGE_URL`
            do
                wget --timeout=1 --user-agent=firefox -O "$IMAGES/$SEARCH_STRING"-$VAR.jpg $file 2> /dev/null
                VAR=$[VAR + 1];
            done
}

# calling searched function
searched_string_and_files_on_disk 

# return value of script
exit 0