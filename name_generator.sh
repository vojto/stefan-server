#!/bin/bash                	#
#				#
# 	NAME GENERATOR 		#
#				#
#################################


## Checking number of argument
if [ $# -ne 3 ]; then 
echo "Usage $0 <names_file> <template_img> <output_directory>"
exit 1
fi


## First argument represets file with names delimited by newlines
NAMES_FILE=$1

## Template is picture, where are generated Names
TEMPLATE=$2

## Directory where are picture saved after generation
OUTPUT_DIRECTORY=$3

## Maximal font size
FONT_MAX=80

## Testing whether exist output directory, if not, then output directory is created 
test -d $OUTPUT_DIRECTORY || mkdir $OUTPUT_DIRECTORY


## Main loop 
for name in `cat $NAMES_FILE`; do

## Length of name string
LENGTH=${#name}

## Lenght of font's character
CHAR_LENGTH=30   

## Current font size
FONT_SIZE=80 

name=`echo $name | tr -d ','` 

## Counting of center position of written text into picture
POSITION=`expr \( 370 - \(  \( $CHAR_LENGTH  \* $LENGTH \)   \) \) / 2`

## 
if [ $LENGTH -gt 8 ]; then 
FONT_SIZE=70
fi


## Command to insert name text into the template picture and save to the output directory
convert -pointsize $FONT_SIZE -gravity South -font Comic-Sans-MS-Bold -fill black -draw 'text 0,0 '$name' ' $TEMPLATE $OUTPUT_DIRECTORY/$name.jpg

done


## return value of main generating script
exit 0
