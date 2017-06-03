<!DOCTYPE 
    html
    PUBLIC
    "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">



<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="css/stylesheet.css" title="Style" />

  </head>
  <body>
  <h1><center>文档</center></h1>
  <p>
    <#list tagedOpenAPIs?keys as tag>
      <#assign openAPIs = tagedOpenAPIs[tag]>
        <p class="tag-paragraph">
        <h2>
          ${tag}
        </h2>
        <#list openAPIs as openAPI>
 <#include "APIDetailTmpl.ftl">
        </#list>
        </p>
      </#list>
    </p>
    </body>
  </html>
