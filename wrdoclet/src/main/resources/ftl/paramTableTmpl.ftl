
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
