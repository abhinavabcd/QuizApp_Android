
import urllib
import re
import random
from lxml.html import parse, tostring, clean,etree
from lxml.etree import strip_tags
import pprint
import StringIO

import yajl as json
import socket
import time
import urllib2
import logging
import zlib 
import itertools
import os
import json
import codecs
from StringIO import StringIO

url_loader=urllib2.build_opener(urllib2.HTTPCookieProcessor(),urllib2.ProxyHandler())
urllib2.install_opener(url_loader)

def get_data(url,post=None,headers={}):
    headers['Accept-encoding'] ='gzip'
    req=urllib2.Request(url,post,headers)
    ret = url_loader.open(req) 
    if ret.info().get('Content-Encoding') == 'gzip':
        return StringIO(zlib.decompress(ret.read(),16+zlib.MAX_WBITS))
    return codecs.decode(ret, 'utf-8')

uniqueId = 1
links={}
tmpRef = {}

def checkAndSetLinkRef(link):
    global uniqueId,links,tmpRef
    ret = links.get(link,None)
    if(ret==None):
        if tmpRef.get(link,None) != None:
            links[link] = tmpRef.get(link,None)
            return links[link]
        links[link]=uniqueId
        uniqueId = uniqueId + 1
        return links[link]
    return None

def getLinkRefOrTmpRef(link):
    global uniqueId,links,tmpRef
    if not links.get(link,None):
        if not tmpRef.get(link,None):
            tmpRef[link]=uniqueId
            uniqueId = uniqueId + 1
            return tmpRef[link]
        return tmpRef.get(link,None)
    return links.get(link,None)

def insertCombinations(catalog,listItem):
    liobjs = []
    if len(catalog)==0:
        try:
            liobjs.append(ListItem.CreateNew(save=True,**listItem))
        except:
            pass
        return liobjs
    combis = list(itertools.product(*catalog.values()))
    listItem['searchTags'].append("dummy")
    for combi in combis:
        tmp = ' '.join(map(lambda x: str(x),combi))
        name = listItem['group_Type'] +' '+ tmp
        listItem['name'] = name
        listItem['boUid'] = name
        listItem['searchTags'].pop()
        listItem['searchTags'].append(tmp)
        listItem['unitPrice'] = listItem['unitPrice']+random.randint(0,1000)
        listItem['unitFakePrice'] = listItem['unitPrice'] + 100
        try:
            tmp = ListItem.CreateNew(save=True,**listItem)
            liobjs.append(tmp)
        except:
            pass
    return liobjs

PAGES = 81
bo = [User.CreateNew("user1", "21212121", {}, [], Menu.CreateNew("{}"),True),
      User.CreateNew("user2", "22222222", {}, [], Menu.CreateNew("{}"),True),
      User.CreateNew("user3", "23232323", {}, [], Menu.CreateNew("{}"),True),
      User.CreateNew("user4", "24242424", {}, [], Menu.CreateNew("{}"),True)]

if os.path.isfile("links.json"):
    with open("links.json") as json_file:
        links = json.load(json_file)

for i in range(1930,PAGES):

    #http://en.wikipedia.org/wiki/List_of_Telugu-language_films
    root = parse(get_data('http://en.wikipedia.org/wiki/List_of_Telugu_films_of_'+str(i))).getroot()
#    matches = root.cssselect("div#mw-content-text ul a")
    columns = root.cssselect("table.wikitable th")
    rows = root.cssselect("table.wikitable tr")
    print matches
    break
    for k in map(lambda x: 'http://www.themobilestore.in'+re.sub("#.*","",str(x.attrib['href'])), matches):
        if(checkAndSetLinkRef(k)!=None):# if link is not processed before setLinkRef returns ref else 0
#            k = "http://www.themobilestore.in/mobiles-tablet/apple-iphone-5s/p-31103-39415583508-cat.html#variant_id=31103-15785258407"
            itemData = parse(get_data(k)).getroot() #urllib.urlopen
            images = map(lambda x: x.attrib['data-medium-url'] , itemData.cssselect('ul.thumbnails a'))
#            print images
#            print name
            tmpPrice = re.sub("\D","",itemData.cssselect('span.m-w')[0].text_content())
            if tmpPrice == "":
                unitPrice = int(random.random()*10000) 
            else: 
                unitPrice = int(tmpPrice)
            unitFakePrice = unitPrice + 100
            discreteFactor = 1
            isContinuous = False
            isUnique = False
#            print unitPrice
            catalog = {}
            tmp = itemData.cssselect('div#catalog-options')
#            print len(tmp)
            tmp1 = map(lambda x: x.text_content().strip().strip(':'), tmp[0].cssselect('b'))
#            print tmp1
            tmp2 = tmp[0].cssselect('ul.cat-options')
#            print len(tmp1),len(tmp2)
            if len(tmp1)==len(tmp2):
                for i in range(len(tmp2)):
                    catalog[re.sub("[\$\.]","",tmp1[i])] = map(lambda x: x.text_content().strip(), tmp2[i].cssselect('span.catalog-option-title'));
#            print catalog
#            relatedItems = map(lambda x: str(getLinkRefOrTmpRef('http://www.themobilestore.in'+str(x.attrib['href']))) , root.cssselect("div.variant-image a"))
            relatedItems = map(lambda x: str('http://www.themobilestore.in'+str(x.attrib['href'])) , itemData.cssselect("div.variant-image a"))
#            print colors
            searchTags = map(lambda x: x.text_content(), itemData.cssselect("div.bread-crumbs span"))
            if len(searchTags)>0:
                searchTags.pop(0)
#            print searchTags
            name = itemData.cssselect("div#catalog-title h1")[0].text_content() #searchTags[-1] 
            description = itemData.cssselect("div#description")[0].text_content()
#            print description
            selectionMinMax = [1,1]
            isEndPoint = True
            isSearchNode = True
            groupType = None
            properties = {}
            properties["relatedItems"] = relatedItems
            properties.update(catalog)

            tmp = itemData.cssselect("div#feature_groups tr")
            for tmpchild in tmp:
                try:
                    tt = map(lambda x: x.text_content(),tmpchild.cssselect("td"))
                except:
                    tt = []
                if len(tt)==2:
                    properties[re.sub("[\$\.]","",tt[0].strip().strip(':'))] = tt[1].strip().strip(':')
#            print relatedItems
            uid = itemData.cssselect("div#catalog-title h1")[0].text_content()
            listItem = {
                    "_rangeList": [[0,5]],
                    "parentListItemRef" : None, 
                    "rootListItemRef" : None,
                    "parentPath" : None,
                    "childLists" : [],
                    "linkedLists" : [], # linked ListItems , they are not childs of this list nor consuming these will destroy the parent  
                    
                    "name": name,#required=True)# this is unique for a business operator for all his items accross , and users this name in the api #TODO if required , change to bo_given_name
                    "description" : description,
                    "images" : images,
                    "discreteFactor" : 1,
                    "isContinuous" : False,
                    "isUnique" : False,
                    "unitPrice" : unitPrice,
                    "unitFakePrice" : unitFakePrice,
                    "isUserAuth" : True,
                    "isTime" : False,
                    "ownerUser" : bo[int(random.random()*len(bo))],
                    "boUid" : uid, #"VC0001",# to use in business opearator api
                    "group_Type" : searchTags[-1],
                    "selectionType" : SELECT_ONE,
                    "selectionMinMax" : [1,1],
                    "viewSpan" : 10,
                    "isNeeded" : True,
                    "expandForEach" : False,
                    "isEndPoint" : True,
                    "uiRenderRef" : "quantitySelect",
                    "searchTags" : searchTags,
                    "isSearchNode" : True,
                    "indexToItemMap" : None,
                    "discountConditions" : None,
                    "afterCartConditions" : None,
                    "isApprovalNeeded" : False,
                    "properties" : properties,
                    "commonUserTextInput" : None,
                    "commonUserFileInput" : None,
                    "commonUserSelectInput" : None,
                    "commonUserRadioInput" : None,
                    
                    "userTextInput" : None,
                    "userFileInput" : None,
                    "userSelectInput" : None,
                    "userRadioInput" : None
            }
            liobjs = insertCombinations(catalog,listItem)
            print k
            fp = open('links.json', 'w')
            json.dump(links, fp)
            fp.close()
        