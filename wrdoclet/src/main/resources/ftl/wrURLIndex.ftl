<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="../stylesheet.css" title="Style" />
<SCRIPT type="text/javascript">
  window.onload=function(){
    top.API_DETAIL_FRAME.location = document.links[0].href;
  };
</SCRIPT>
</head>

<body>
<#list urls as url>
<ul>
<li>
<a href="../APIs/${url.filename}" target="API_DETAIL_FRAME">${url.index}</a>
</li>
</ul>
</#list>
</body>
</html>