
<html xmlns="http://www.w3.org/1999/xhtml">

<#include "modifyHisTmpl.ftl">
<#include "paramTableTmpl.ftl">

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="generator" content="wrdoclet" />
        <meta name="tags" content="${tags}" />
        <meta name="brief" content="${openAPI.brief}" />
    	<meta name="APIUrl" content="${openAPI.requestMapping.url}" />
    	<meta name="tooltip" content="${openAPI.requestMapping.tooltip}" />
    	<#if openAPI.requestMapping.methodType??>
    		<meta name="methodType" content="${openAPI.requestMapping.methodType}" />
    	<#else>
    		<meta name="methodType" content="" />
    	</#if>
    	<meta name="systemName" content="${systemName}" />
    	<meta name="branchName" content="${branchName}" />
    	<meta name="buildID" content="${buildID}" />
        <link rel="stylesheet" type="text/css" href="css/stylesheet.css" title="Style" />
    </head>
  <script  src="js/jquery-1.11.3.min.js"></script>
  <script  src="js/colResizable-1.5.min.js"></script>
  <script type="text/javascript">
	$(function(){
		$("#reqTable").colResizable({
			liveDrag:true, 
			gripInnerHtml:"<div class='grip'></div>", 
			draggingClass:"dragging"});
			
		$("#resTable").colResizable({
			liveDrag:true, 
			gripInnerHtml:"<div class='grip'></div>", 
			draggingClass:"dragging"});
	});	
  </script>


    <body>
    	<#if openAPI.deprecated>
    		<div class="deprecated">已废弃</div>
    	</#if>
        <#include "APIDetailTmpl.ftl">
		<div class="foot">
			<ul>
				<#if systemName?? && systemName != "">
				<li>
					© <strong>${systemName}</strong> |
				</li>
				</#if>
				<#if generatedTime??>
				<li>
					Generated on <strong>${generatedTime}</strong> |
				</li>
				</#if>
				<#if branchName?? && branchName != "">
				<li> Branch : <strong>${branchName}</strong> | </li>
				</#if>
				<li> Powered by <strong>winroad</strong></li>
			</ul>
		</div>
    </body>
</html>