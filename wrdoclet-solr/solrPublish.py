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
#todo: path from command line
path = "D:/Git/wrdoclet/wrdoclet/target/doc/" # The directory of files to publish to solr
filelist = [ join(path,f) for f in listdir(path) if isfile(join(path,f)) and f.endswith('.html') and f != "index.html" ]
for file in filelist:
	with open(file, "r") as filecontent:
		soup = BeautifulSoup(filecontent) # Try to parse the HTML of the page
		if soup.html == None: # Check if there is an <html> tag
			print "Error: No HTML tag found at file: " + file
		#todo: invalid html handling
		#todo: summarize for documents submitted.
		tags = None
		if soup.html.head.find("meta", {"name":"tags"}) != None:
			tags = str(soup.html.head.find("meta", {"name":"tags"})['content']).decode("utf-8").split(", ")
		brief = None
		if soup.html.head.find("meta", {"name":"brief"}) != None:
			brief = str(soup.html.head.find("meta", {"name":"brief"})['content']).decode("utf-8")
		APIUrl = None
		if soup.html.head.find("meta", {"name":"APIUrl"}) != None:
			APIUrl = str(soup.html.head.find("meta", {"name":"APIUrl"})['content']).decode("utf-8")
		tooltip = None
		if soup.html.head.find("meta", {"name":"tooltip"}) != None:
			tooltip = str(soup.html.head.find("meta", {"name":"tooltip"})['content']).decode("utf-8")
		methodType = None
		if soup.html.head.find("meta", {"name":"methodType"}) != None:
			methodType = str(soup.html.head.find("meta", {"name":"methodType"})['content']).decode("utf-8")
		systemName = None
		if soup.html.head.find("meta", {"name":"systemName"}) != None:
			systemName = str(soup.html.head.find("meta", {"name":"systemName"})['content']).decode("utf-8")
		branchName = None
		if soup.html.head.find("meta", {"name":"branchName"}) != None:
			branchName = str(soup.html.head.find("meta", {"name":"branchName"})['content']).decode("utf-8")
		with open(file, "r") as fl:
			doc = { # just for print
				"id":str(systemName).decode("utf-8") + '_' + str(branchName).decode("utf-8") + '_' + str(APIUrl).decode("utf-8") + '_' + str(methodType).decode("utf-8"),
				"tags":tags, 
				"brief":brief, 			
				"APIUrl":APIUrl, 
				"tooltip":tooltip, 
				"methodType":methodType, 
				"systemName":systemName, 
				"branchName":branchName
				}
			print doc
			solrInstance.add(_id=str(systemName).decode("utf-8") + '_' + str(branchName).decode("utf-8") + '_' + str(APIUrl).decode("utf-8") + '_' + str(methodType).decode("utf-8"), 
				tags=tags,
				brief=brief, 			
				APIUrl=APIUrl, 
				tooltip=tooltip, 
				methodType=methodType, 
				systemName=systemName,
				branchName=branchName,
				pageContent=fl.read() # instead of using soup.html, because there is a bug that it will modify some special characters.
				)
			solrInstance.commit()
