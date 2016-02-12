#! /usr/bin/env python27
# -*- coding:utf-8 -*-
""" Index links from a sitemap to a SOLR instance"""
import sys
import getopt
import solr
import uuid
from os import listdir
from os.path import isfile, join, isdir
from BeautifulSoup import BeautifulSoup
from xml.etree.ElementTree import parse

default_encoding = 'utf-8'
if sys.getdefaultencoding() != default_encoding:
    reload(sys)
    sys.setdefaultencoding(default_encoding)

class PublishException(Exception):
	def __init__(self, error, publishedCount, skippedCount):
		Exception.__init__(self, error, publishedCount, skippedCount)
		self.error = error
		self.publishedCount = publishedCount
		self.skippedCount = skippedCount

def usage():
	print 'solrPublish.py -i <inputpath> -s <solraddr> -b <buildid>'

def printSummary(publishedCount, skippedCount):
	print "-------------Publish Summary---------------"
	print "Published API count: " + str(publishedCount)
	print "Skipped files count to publish: " + str(skippedCount)

def publishToSolr(solrInstance, path, buildIDToPublish):
	publishedCount = 0
	skippedCount = 0
	processedSet = set()

	filelist = [ join(path,f) for f in listdir(path) if isfile(join(path,f)) and f.endswith('.html') and f != "index.html" ]
	for file in filelist:
		with open(file, "r") as filecontent:
			soup = BeautifulSoup(filecontent) # Try to parse the HTML of the page
			if soup.html == None: # Check if there is an <html> tag
				print "Error: No HTML tag found at file: " + file
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
			buildID = None
			if soup.html.head.find("meta", {"name":"buildID"}) != None:
				buildID = str(soup.html.head.find("meta", {"name":"buildID"})['content']).decode("utf-8")
			if tags == None or brief == None or APIUrl == None or tooltip == None or methodType == None or systemName == None or branchName == None:
				raise PublishException("Invalid API detail html page: " + file, publishedCount)
			if buildID != None and buildID != buildIDToPublish:
				print "Found doc '" + file + "' with buildID:'" + buildID + "' not equal to the one to publish:'" + buildIDToPublish + "'!"
				skippedCount += 1
			else:
				try:
					with open(file, "r") as fl:
						doc = { # just for print
							"id":str(systemName).decode("utf-8") + '_' + str(branchName).decode("utf-8") + '_' + str(APIUrl).decode("utf-8") + '_' + str(methodType).decode("utf-8"),
							"tags":tags, 
							"brief":brief, 			
							"APIUrl":APIUrl, 
							"tooltip":tooltip, 
							"methodType":methodType, 
							"systemName":systemName, 
							"branchName":branchName,
							"buildID":buildIDToPublish
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
							buildID=buildIDToPublish,
							pageContent=fl.read() # instead of using soup.html, because there is a bug that it will modify some special characters.
							)
						solrInstance.commit()
						publishedCount += 1
						processedSet.add((systemName, branchName))
				except Exception,data: 
					raise PublishException(data, publishedCount, skippedCount)
	return (publishedCount, skippedCount, processedSet)

def cleanupDocs(solrInstance, systemName, branchName, buildID):
	query = "-buildID:" + buildID + " AND systemName:" + systemName + " AND branchName:" + branchName
	#query = "*:*"
	print "Cleaning up docs by query: '" + query + "'"
	solrInstance.delete_query(query)
	solrInstance.commit()

def main(argv):
	#solrUrl = 'http://127.0.0.1:8080/solr/apidocs' # The URL of the solr instance
	#path = "D:/Git/wrdoclet/wrdoclet/target/doc/" # The directory of files to publish to solr
	solrUrl = ''
	path = ''
	buildID = ''

	try:
		opts, agrs = getopt.getopt(argv, "hi:s:b:", ["inputpath=", "solraddr=", "buildid="])
	except getopt.GetoptError:
		usage()
		sys.exit(2)
	for opt, arg in opts:
		if opt == "-h":
			usage()
			sys.exit()
		elif opt in ("-i", "--inputpath"):
			path = arg
		elif opt in ("-s", "--solraddr"):
			solrUrl = arg
		elif opt in ("-b", "--buildid"):
			buildID = arg

	if solrUrl == '' or path == '' or buildID == '':
		usage()
		sys.exit()
		
	print "input path of docs: ", path
	print "solr address url: ", solrUrl
	print "doc build ID: ", buildID

	solrInstance = solr.SolrConnection(solrUrl) # Solr Connection object
	publishedCount = 0
	skippedCount = 0
	processedSet = set()

	try:
		pCount, sCount, pSet = publishToSolr(solrInstance, path, buildID)
		publishedCount += pCount
		skippedCount += sCount
		processedSet |= pSet
		#just for migration of old type users, official wrdoclet user will not hit this code path.
		pathlist = [ join(path,f) for f in listdir(path) if isdir(join(path,f)) and f.startswith('detail') ]
		for p in pathlist:
			pCount, sCount, pSet = publishToSolr(solrInstance, p, buildID)
			publishedCount += pCount
			skippedCount += sCount
			processedSet |= pSet
	except PublishException, ex: 
		printSummary(publishedCount + ex.publishedCount, skippedCount + ex.skippedCount)
		raise
	except:
		printSummary(publishedCount, skippedCount)
		raise
	printSummary(publishedCount, skippedCount)

	for processedSystemBranch in processedSet:
		cleanupDocs(solrInstance, processedSystemBranch[0], processedSystemBranch[1], buildID)


if __name__ == "__main__":
   main(sys.argv[1:])