#http://bigbasket.com/product/facet/get-page/?sid=E3dMAoOiY2OnNDg5fDQ5MKFjA6Jhb8I%3D&page=4
#http://bigbasket.com/product/facet/get-page/?page=100

from BaseApplication import *
import urllib
import re
import random
from lxml.html import parse, tostring, clean,etree,document_fromstring
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
import itertools
import os
import json
import codecs
from StringIO import StringIO

url_loader=urllib2.build_opener(urllib2.HTTPCookieProcessor(),urllib2.ProxyHandler())
urllib2.install_opener(url_loader)

def get_data_initial(url,post=None,headers={}):
#    headers['Accept-encoding'] ='gzip'
#    req=urllib2.Request(url,post,headers)
#    ret = url_loader.open(req)
    response = urllib2.urlopen(url)
    data = json.load(response)   
#    print data["products"]
#    if ret.info().get('Content-Encoding') == 'gzip':
#        return StringIO(zlib.decompress(ret.read(),16+zlib.MAX_WBITS))
    return data

def charsetDecoder(func):
    def wrapper(*args,**kwargs):
        ret = func(*args,**kwargs)
        return StringIO(ret.read().decode('iso-8859-1'))
    return wrapper

@charsetDecoder
def get_data(url,post=None,headers={}):
    headers['Accept-encoding'] ='gzip'
    req=urllib2.Request(url,post,headers)
    ret = url_loader.open(req) 
    if ret.info().get('Content-Encoding') == 'gzip':
        return StringIO(zlib.decompress(ret.read(),16+zlib.MAX_WBITS))
    return ret

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

def printDict(dic):
    for key in dic.keys():
        print key+"  :  "
        print dic[key]
#        if not dic[key]:
#            print "None\n"
#        else:
#            print dic[key].encode('utf-8','replace')+"\n"

PAGES = 276
bo = [User.CreateNew("user1", "21212121", {}, [], Menu.CreateNew("{}"),True),
      User.CreateNew("user2", "22222222", {}, [], Menu.CreateNew("{}"),True),
      User.CreateNew("user3", "23232323", {}, [], Menu.CreateNew("{}"),True),
      User.CreateNew("user4", "24242424", {}, [], Menu.CreateNew("{}"),True)]

if os.path.isfile("links.json"):
    with open("links.json") as json_file:
        links = json.load(json_file)

for i in range(259,PAGES):
#    root = document_fromstring(get_data_initial('http://bigbasket.com/product/facet/get-page/?sid=E3dMAoOiY2OnNDg5fDQ5MKFjA6Jhb8I%3D&page='+str(i))["products"])#.getroot()
    try:
        root = document_fromstring(get_data_initial('http://bigbasket.com/product/facet/get-page/?page='+str(i))["products"])#.getroot()
    except:
        root = document_fromstring(get_data_initial('http://bigbasket.com/product/facet/get-page/?page='+str(i))["products"])#.getroot()
#    root = parse(StringIO(get_data('http://bigbasket.com/product/facet/get-page/?sid=E3dMAoOiY2OnNDg5fDQ5MKFjA6Jhb8I%3D&page='+str(i))["products"])).getroot()
    print i
    tmp = map(lambda x: str(x.attrib['href']) , root.cssselect("div.uiv2-list-box-img-block a"))
    tmp = ['http://bigbasket.com'+val for val in tmp if val.find('#')<0]
    for k in tmp:
        if(checkAndSetLinkRef(k)!=None):# if link is not processed before setLinkRef returns ref else 0
            print k
#            k = "http://www.themobilestore.in/mobiles-tablet/apple-iphone-5s/p-31103-39415583508-cat.html#variant_id=31103-15785258407"
            try:
                itemData = parse(get_data(k)).getroot()
            except:
                itemData = parse(get_data(k)).getroot()
            images = map(lambda x: x.attrib['src'].lstrip('/') , itemData.cssselect('div.uiv2-product-large-img-container img.uiv2-product-large-img-display'))
#            print images
#            print name
            tmp = itemData.cssselect('div.uiv2-product-detail-content')[0]
            tmpPrice = re.sub("\D|0+$","",tmp.cssselect('div.uiv2-price')[0].text_content())
            if tmpPrice == "":
                unitPrice = int(random.random()*10000) 
            else: 
                unitPrice = int(tmpPrice)
            tmp1 = tmp.cssselect('div.uiv2-savings span#uiv2-mrp')
            if len(tmp1)>0:
                unitFakePrice = re.sub("\D|0+$","",tmp1[0].text_content())
            else:
                unitFakePrice = unitPrice + 100
                
            # yet to do from here
            discreteFactor = 1
            isContinuous = False
            isUnique = False
#            print unitPrice
            properties = {}
            properties["sizes"] = map(lambda x: x.text_content().strip(), itemData.cssselect('div.uiv2-size-variants label'))
#            relatedItems = map(lambda x: str(getLinkRefOrTmpRef('http://www.themobilestore.in'+str(x.attrib['href']))) , root.cssselect("div.variant-image a"))
            tmp = itemData.cssselect("div.uiv2-title-2")
#            print etree.tostring(tmp[0])
#            print tmp[0].text_content()
            relatedTypes = map(lambda x: re.sub("[\n\s]+"," ",x.text_content().strip()) , itemData.cssselect("div.uiv2-title-2"))
            tmp = itemData.cssselect("div.product-slider div.uiv2-carousel-holder")
#            print relatedTypes
#            print len(tmp)
            if len(relatedTypes)==len(tmp):
                for i in range(len(tmp)):
                    properties[re.sub("[\$\.]","",relatedTypes[i].strip().strip(':'))] = map(lambda x: str('http://bigbasket.com'+str(x.attrib['href'])) , tmp[i].cssselect("div.uiv2-list-box-img-block a"))
#            print colors
            searchTags = map(lambda x: x.text_content(), itemData.cssselect("div.uiv2-shopping-list-bredcom div.breadcrumb-item span"))
            if len(searchTags)>0:
                searchTags.pop(0)
#            print searchTags
            name = itemData.cssselect("div.uiv2-product-name h1")[0].text_content().strip('\n').strip()
            try:
                description = itemData.cssselect("div.uiv2-boxsty p")[0].text_content().strip('\n').strip()
            except:
                description = ""
#            print description
            selectionMinMax = [1,1]
            isEndPoint = True
            isSearchNode = True
            group_Type = searchTags[-1] if not len(searchTags)==0 else None

#            print relatedItems
            uid = itemData.cssselect("div.uiv2-product-name h1")[0].text_content()
            tmp = itemData.cssselect("div.uiv2-brand-name a")
            if len(tmp)>0:
                properties["brandname"] = tmp[0].text_content()
            listItem = {
                    "_rangeList": [[0,10]],
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
                    "group_Type" : group_Type,
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
#            printDict(listItem)
            liobjs = insertCombinations([],listItem)
            fp = open('links.json', 'w')
            json.dump(links, fp)
            fp.close()
#            liobj = ListItem.CreateNew(save=True,**listItem)
#            print searchTags
#            print liobj.getSearchTags()
#            print listItem
#            print map(lambda x: etree.tostring(x),itemData.cssselect("div#feature_groups tr"))

#            etree.tostring
#        break
            #ListItem.CreateNew(quantity=5, _rangeList=None, parentListItemRef=None, rootListItemRef=None, parentPath=None, childLists=None, linkedLists, name, description, images, uid, discreteFactor, isContinuous, isUnique, unitPrice, unitFakePrice, isUserAuth, boGroupUid, selectionType, selectionMinMax, viewSpan, isNeeded, expandForEach, isEndPoint, ownerUser, uiRenderRef, searchTags, isSearchNode, indexToItemMap, discountConditions, afterCartConditions, listItemHeadRef, isApprovalNeeded, properties, commonUserTextInput, commonUserFileInput, commonUserSelectInput, commonUserRadioInput, userTextInput, userFileInput, userSelectInput, userRadioInput, group_Type, isTime, save)
        
#    break
        