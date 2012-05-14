# encoding: utf-8                                                                                                      

# Author: Vojtech Rinik

# ruby google-search API engine for searching our images                                                               
require "rubygems"
require "google-search"

query = ARGV[0]
seq = ARGV[1].to_i
query = query.gsub '_', ' '
# puts "[Ruby] Searching #{query} (#{seq})"                                                                            
result = Google::Search::Image.new(:query => query, :image_size => :medium, :file_type => :jpg)
items = result.all_items

# we need 3 image results to give opportunity users of application to choose which image is best choice for me\
aning of          word                                                                                                 
image1 = items[0]
image2 = items[1]
image3 = items[2]

# print URL image that we found and we need to download to it                                                  
puts image1.uri
puts image2.uri
puts image3.uri