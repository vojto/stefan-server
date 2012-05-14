# -*- coding: utf-8 -*-
"""
oCreated on Sat May 12 20:05:39 2012

@author: Peter Gonda, Vojtech Rinik
"""

# junk = "is are the in of into from when where why what i he she we has a was me that this then than they your once had themselves there them to were through you do with every have and by for but all over my very times went behind to how take and out lot more often together ".split(" ")
f = open('preposition.txt')
junk = f.readlines()
junk = [x.strip() for x in junk]

numbers = "one two three four five six seven eight nine ten"

from flask import Flask
from flask import request
from string import strip
from datetime import timedelta
from flask import make_response, request, current_app
from functools import update_wrapper
import subprocess
import json
import re
import hashlib
import os


def crossdomain(origin=None, methods=None, headers=None,
                max_age=21600, attach_to_all=True,
                automatic_options=True):
    if methods is not None:
        methods = ', '.join(sorted(x.upper() for x in methods))
    if headers is not None and not isinstance(headers, basestring):
        headers = ', '.join(x.upper() for x in headers)
    if not isinstance(origin, basestring):
        origin = ', '.join(origin)
    if isinstance(max_age, timedelta):
        max_age = max_age.total_seconds()

    def get_methods():
        if methods is not None:
            return methods

        options_resp = current_app.make_default_options_response()
        return options_resp.headers['allow']

    def decorator(f):
        def wrapped_function(*args, **kwargs):
            if automatic_options and request.method == 'OPTIONS':
                resp = current_app.make_default_options_response()
            else:
                resp = make_response(f(*args, **kwargs))
            if not attach_to_all and request.method != 'OPTIONS':
                return resp

            h = resp.headers

            h['Access-Control-Allow-Origin'] = origin
            h['Access-Control-Allow-Methods'] = get_methods()
            h['Access-Control-Max-Age'] = str(max_age)
            if headers is not None:
                h['Access-Control-Allow-Headers'] = headers
            return resp

        f.provide_automatic_options = False
        return update_wrapper(wrapped_function, f)
    return decorator



DATABASE_SCRIPT="/home/cnluser/images.sh"
CONVERT_SCRIPT="/root/server/convertor.sh"

app = Flask(__name__)
app.debug=True
@app.route("/")
@crossdomain(origin='*')
def hello():
    return "OK"

@app.route("/phrase/<phrase>")
@crossdomain(origin='*')
def phrase(phrase):
    # print(phrase)
    print "Incoming query!"
    print phrase
    phrase = re.sub("[\.,]", "", phrase)
    words = phrase.lower().split(' ')
    print "Tokens: " + (" ".join(words))
    
    groups = []
    current_group = []
    
    last_word = ""
    for word in words:
        if (word in junk):
            # Vytvorit novu skupinu
            if len(current_group) != 0: groups.append(current_group)
            current_group = []
        elif (word in numbers):
            if len(current_group) != 0: groups.append(current_group)
            groups.append([word])
            current_group = []
            
        else:
            # Pridat do aktualnej skupiny
            current_group.append(word)
        last_word = word
    if len(current_group) != 0: groups.append(current_group)
    
    print "Groups: "
    print groups

    joined_groups = []    
    
    for group in groups:
        joined_groups.append ("_".join(group))
    
    urls = {}
    for group in joined_groups:
        print "Querying " + group
        url = query_to_database(group)
        key = group.lower()
        key = re.sub("_", " ", key)
        key = re.sub("\.", "", key)
        urls[key] = url

    print urls

    return json.dumps(urls) 


def query_to_database(input_string):
    result = subprocess.check_output(DATABASE_SCRIPT + " " +input_string, shell=True)
    name = re.sub("\s+", "", result)
    names = []
    for i in range(3):
        names.append(name + "-" + str(i) + ".jpg")
    return names
        


#phrase("that stupid dog has shitted all over my green grass")


@app.route("/rate/inc/<picture>")
@crossdomain(origin='*')
def increment(picture):
    
    return "picture"


@app.route("/rate/dec/<picture>")
@crossdomain(origin='*')
def decrement(picture):
    
    return "picture"


uploaded_files = {}

@app.route('/upload', methods=['POST'])
@crossdomain(origin='*')
def upload_file():
    name = request.files.keys()[0]
    item = request.files[name]
    # hashsum = hashlib.sha1(name).hexdigest()
    print "Name: " + name
    path = os.path.join('.', 'files', name)
    item.save(path)
    print "Calling: " + CONVERT_SCRIPT + " " + path
    result = subprocess.check_output(CONVERT_SCRIPT + " " + path, shell = True)
    uploaded_files[name] = result
    return result
        # if file and allowed_file(file.filename):
        #     filename = secure_filename(file.filename)
        #     file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
        # return "somrina"
    
@app.route('/uploaded')
@crossdomain(origin='*')
def uploaded():
    return json.dumps(uploaded_files)

if __name__ == "__main__":
    app.run(host = '0.0.0.0')