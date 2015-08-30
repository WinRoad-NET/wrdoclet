<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="css/stylesheet.css" title="Style" />
<SCRIPT type="text/javascript">
  window.onload=function(){
    top.frames["API_DETAIL_FRAME"].parent.frames["URL_INDEX_FRAME"].location = document.links[0].href;    
  };
</SCRIPT>
</head>

<body>
<h3>
分类
</h3>
<#list wrTags as wrTag>
<ul>
<li>
<a href="./tags/${wrTag}.html" target="URL_INDEX_FRAME">
${wrTag}
</a>
<#if taggedOpenAPIs??>
  (${taggedOpenAPIs[wrTag]?size})
</#if>
</li>
</ul>
</#list>
</body>
</html>