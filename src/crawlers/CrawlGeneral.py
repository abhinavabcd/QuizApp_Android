

from BaseApplication import *
import urllib
import re
from lxml.html import parse, tostring, clean
from lxml.etree import strip_tags
import pprint
import StringIO
#from Levenshtein import ratio
import yajl as json
import socket
import time
import urllib2
import logging
import zlib 
from StringIO import StringIO
url_loader=urllib2.build_opener(urllib2.HTTPCookieProcessor(),urllib2.ProxyHandler())
urllib2.install_opener(url_loader)

def get_data(url,post=None,headers={}):
    headers['Accept-encoding'] ='gzip'
    req=urllib2.Request(url,post,headers)
    ret = url_loader.open(req) 
    if ret.info().get('Content-Encoding') == 'gzip':
        return StringIO(zlib.decompress(ret.read(),16+zlib.MAX_WBITS))
    return ret

itemData = parse(get_data('http://en.wikipedia.org/wiki/List_of_mobile_phone_number_series_by_country')).getroot()



images = map(lambda x: x.attrib['href'] , itemData.cssselect('span.zoom-gallery a'))
print images
properties = {}
for col in itemData.cssselect('div.feature .col'):
    key = unicode(col.cssselect('label')[0].text_content()).split("\n")[0]
    val = unicode(col.cssselect('span')[0].text_content()).split("\n")[0]
    
    properties[key]=val

for col in itemData.cssselect('table.detl-table tr'):
    tds = col.cssselect("td")
    if(len(tds)==2):
        key = unicode(tds[0].text_content()).split("\n")[0]
        val = unicode(tds[1].text_content()).split("\n")[0]
        properties[key] = val
print properties

description = None
for descDiv in itemData.cssselect('.fluid-box'):
    if(descDiv.cssselect("h3").text_content().strip(" \n").lower()=="product description"):
        description = tostring(descDiv)
        
price , fakePrice = map(lambda x:x.text_content().strip("     \n") , itemData.cssselect(".price"))[:2]
searchTags = []
group = None
groupType =  None
name = itemData.cssselect('.prcdt-overview')[0].cssselect(".title")[0].text_content().strip("     \n")
print price , fakePrice
