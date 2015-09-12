#! /usr/bin/env python27
# -*- coding:utf-8 -*-
""" Index links from a sitemap to a SOLR instance"""
import sys
import solr
import uuid
from os import listdir
from os.path import isfile, join
from BeautifulSoup import BeautifulSoup
from xml.etree.ElementTree import parse

default_encoding = 'utf-8'
if sys.getdefaultencoding() != default_encoding:
    reload(sys)
    sys.setdefaultencoding(default_encoding)
	
solrUrl = 'http://127.0.0.1:8080/solr/apidocs' # The URL of the solr instance
solrInstance = solr.SolrConnection(solrUrl) # Solr Connection object
path = "D:/Git/wrdoclet/wrdoclet/target/doc/APIs/" # The directory of files to publish to solr
filelist = [ join(path,f) for f in listdir(path) if isfile(join(path,f)) ]
for file in filelist:
	with open(file, "r") as filecontent:
		soup = BeautifulSoup(filecontent) # Try to parse the HTML of the page
		if soup.html == None: # Check if there is an <html> tag
			print "Error: No HTML tag found at file: " + file
		tags = None
		if soup.html.head.find("meta", {"name":"tags"}) != None:
			tags = str(soup.html.head.find("meta", {"name":"tags"})['content']).decode("utf-8").split(", ")
		filePath = None
		if soup.html.head.find("meta", {"name":"filePath"}) != None:
			filePath = str(soup.html.head.find("meta", {"name":"filePath"})['content']).decode("utf-8")
		APIUrl = None
		if soup.html.head.find("meta", {"name":"APIUrl"}) != None:
			APIUrl = str(soup.html.head.find("meta", {"name":"APIUrl"})['content']).decode("utf-8")
		systemName = None
		if soup.html.head.find("meta", {"name":"systemName"}) != None:
			systemName = str(soup.html.head.find("meta", {"name":"systemName"})['content']).decode("utf-8")
		branchName = None
		if soup.html.head.find("meta", {"name":"branchName"}) != None:
			branchName = str(soup.html.head.find("meta", {"name":"branchName"})['content']).decode("utf-8")
		doc = {
			"id":str(filePath).decode("utf-8")+str(APIUrl).decode("utf-8")+str(systemName).decode("utf-8")+str(branchName).decode("utf-8"),
			"tags":tags, 
			"filePath":filePath, 
			"APIUrl":APIUrl, 
			"systemName":systemName, 
			"branchName":branchName
			}
		print doc
		solrInstance.add(_id=str(filePath).decode("utf-8")+str(APIUrl).decode("utf-8")+str(systemName).decode("utf-8")+str(branchName).decode("utf-8"), 
			tags=tags,
			filePath=filePath,
			APIUrl=APIUrl, 
			systemName=systemName,
			branchName=branchName,
			pageContent=str(soup.html)
			)
		solrInstance.commit()
