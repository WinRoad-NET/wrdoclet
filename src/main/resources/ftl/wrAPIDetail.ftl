
<html xmlns="http://www.w3.org/1999/xhtml">

<#include "modifyHisTmpl.ftl">
<#include "paramTableTmpl.ftl">

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="generator" content="wrdoclet" />
        <meta name="tags" content="${tags}" />
        <meta name="brief" content="${openAPI.brief}" />
		<meta name="APIUrl" content="${openAPI.requestMapping.url!}${openAPI.requestMapping.headers!}${openAPI.requestMapping.params!}${openAPI.requestMapping.consumes!}" />
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
    OASV3 = ${OASV3};
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
	//http://jsfiddle.net/doktormolle/RR7RK/
    function fx(frmAction,//url the form has to be sended to
                frmMethod,//post||get
                winName,//name used for window and form-target
                winOpts,//options for window.open
                jsonName,//the name of the json inside _POST
                json//the object to send
    )
    {
        //open the window
        var win=window.open('about:blank',winName,winOpts);
        win.focus();
        //create form & input and append it to the body
        var f=document.createElement('form');
        f.setAttribute('action',frmAction);
        f.style.display='none';
        f.setAttribute('target',winName);
        f.setAttribute('method',frmMethod);
        var e=document.createElement('input');
        e.setAttribute('name',jsonName);
        e.setAttribute('value',JSON.stringify(json).replace(/</g, "&lt;"));
        f.appendChild(e);
        document.body.appendChild(f);
        //send the form
        f.submit();
        //remove the form after a moment
        setTimeout(function(){f.parentNode.removeChild(f);},1000);
        return false;
    }
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
				<li> Powered by <strong>winroad</strong> | </li>
				<li> <a href="http://www.winroad.net/donate" target="_blank">打赏</a> </li>
			</ul>
		</div>
    </body>
</html>