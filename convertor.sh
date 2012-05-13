#!/bin/bash

## Checking of right number of arguments
##
if [ $# -lt 1  ]; then 
echo "Usage: $0 <input_file>"
exit 1
fi

## This variable represents filename of converted file
FILE=`file -i $1 | awk '{ print $2}' `

## Few most used filetypes are converted using special tools like pdftotext, docx2txt, catdoc other
## formats are converted by created Java softare 
case $FILE in
    # plain text
    'text/plain;')
    cat $1
;;
    'application/pdf;')
    # pdf file
    pdftotext $1
    tmp=`echo $1 | sed  's/.pdf/.txt/'  `
    cat $tmp
    rm $tmp
;;
    'application/msword;')
    # docx
    docx2txt $1
    tmp=`echo $1 | sed  's/.docx/.txt/'  `
    cat $tmp
    rm $tmp
;;
    # rtf file
    'text/rtf;')
    catdoc $1 
;;
    # doc file
    ';')
    catdoc $1
;;
    # Others filetypes conversion using Google Docs API 
    *)
echo "Javicka"
;;

esac


# Return value of main script
exit 0
