<!DOCTYPE 
    html
    PUBLIC
    "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<#macro modifyHistoryMacro modificationRecordList>
    <thead>
      <tr>
        <th>
          修改人
        </th>
        <th>
          备注
        </th>
        <th>
          版本
        </th>
      </tr>
    </thead>
    <tbody>
      <#list modificationRecordList as record>
        <tr>
          <td>
            ${record.modifier}
          </td>
          <td>
            ${record.memo}
          </td>
          <td>
            ${record.version}
          </td>
        </tr>
      </#list>
    </tbody>
</#macro>

<#macro recurseFieldMacro field depth>
  <tr>
    <td>
      <#if (depth>0) >
        <#list 1..depth as i>
          &nbsp;&nbsp;
        </#list>
      </#if>
      <#if field.isParentTypeArgument()>
        <b>${field.name}</b>
      <#else>
        ${field.name}
      </#if>
    </td>
    <td>
      ${field.type?html}
    </td>
    <td>
      ${field.description}
    </td>
    <td>
      <#if field.parameterOccurs??>
        ${field.parameterOccurs}
      </#if>
    </td>
    <td>
      <#if field.history?? && field.history.modificationRecordList?size != 0 >
        <table class="innerTable">
           <@modifyHistoryMacro modificationRecordList=field.history.modificationRecordList />
        </table>
      </#if>
    </td>
  </tr>
  <#if field.fields??>
  <#list field.fields as child>
    <@recurseFieldMacro field=child depth=depth+1/>
  </#list>
  </#if>
</#macro>

<#macro paramTableMacro param isRequest>
  <#if isRequest>
    <table class="reqParamTable">
  <#else>
    <table class="resParamTable">
  </#if>
      <caption>
        <#if param.name??>
	      <span>
	        ${param.name}
	      </span>
        </#if>
        <#if param.description?? >
          <span>
            ${param.description}
          </span>
        </#if>
      </caption>
      <thead>
        <tr>
          <th>
            参数名
          </th>
          <th>
            参数类型
          </th>
          <th>
            描述
          </th>
          <th>
            是否必须
          </th>
          <th>
            修订记录
          </th>
        </tr>
      </thead>
      <tbody>
        <#list param.fields as field>
          <@recurseFieldMacro field=field depth=0/>
        </#list>
      </tbody>
    </table>
</#macro>

  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="../stylesheet.css" title="Style" />
  </head>
  <body>
    <h3>
      描述
    </h3>
    <p>
      ${openAPI.description}
    </p>
    <h3>
      地址
    </h3>
    <p>
      URL:
      <span class="lable label-method-type">
        ${openAPI.requestMapping.methodType}
      </span>
      ${openAPI.requestMapping.url}
    </p>
    <#if openAPI.modificationHistory?? && (openAPI.modificationHistory.modificationRecordList?size != 0)>
      <table class="modifyHistoryTable">
        <caption>
          修改历史
        </caption>
        <@modifyHistoryMacro modificationRecordList=openAPI.modificationHistory.modificationRecordList />
      </table>
    </#if>
    <h3>
      请求
    </h3>
    <#if openAPI.inParameter?? >
      <#if openAPI.inParameter.fields?? >
        <@paramTableMacro param=openAPI.inParameter isRequest=true />
      </#if>
    </#if>

    <h3>
      响应
    </h3>
    <#if openAPI.outParameter?? >
      <#if openAPI.outParameter.description?? >
        <div>
          ${openAPI.outParameter.description}
        </div>
      </#if>
      <#if openAPI.outParameter.fields?? >
        <@paramTableMacro param=openAPI.outParameter isRequest=false />
      </#if>
    </#if>
  </body>
</html>
