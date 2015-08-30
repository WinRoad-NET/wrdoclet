
<html xmlns="http://www.w3.org/1999/xhtml">

<#include "modifyHisTmpl.ftl">
<#include "paramTableTmpl.ftl">

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link rel="stylesheet" type="text/css" href="../css/stylesheet.css" title="Style" />
    </head>
  <script  src="../js/jquery.js"></script>
  <script  src="../js/colResizable-1.5.min.js"></script>
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
        <#include "APIDetailTmpl.ftl">
		<div class="foot">
			<ul>
				<li>
					<#if generatedTime??>
						Generated on <strong>${generatedTime}</strong> |
					</#if></li>
				<li> Powered by <strong>winroad</strong></li>
			</ul>
		</div>
    </body>
</html>