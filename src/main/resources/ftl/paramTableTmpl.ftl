
<#macro recurseFieldMacro field depth>
  <tr>
    <td>
      <#if (depth>
        0) >
        <#list 1..depth as i>
          &nbsp;&nbsp;
        </#list>
      </#if>
      <#if field.name??>
        <#if field.isParentTypeArgument()>
          <b>
            ${field.name}
          </b>
          <#else>
            ${field.name}
          </#if>
        </#if>
      </td>
      <td>
        <#if field.type??>
          ${field.type?html}
        </#if>
      </td>
      <td>
        <#if field.description??>
          ${field.description}
        </#if>
      </td>
      <td style="text-align: center;">
        <#if field.parameterOccurs??>
          <#if field.parameterOccurs == "REQUIRED">
          	是
          <#elseif field.parameterOccurs == "OPTIONAL">
          	否
          </#if>
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


  <#macro reqParamTableMacro params>
    <table id="reqTable" class="reqParamTable" width="100%" border="0" cellpadding="0" cellspacing="0">
      <thead>
        <tr>
          <th style="width: 20%;"></span>
            参数名
          </th>
          <th style="width: 22%;"></span>
            参数类型
          </th>
          <th style="width: 20%;"></span>
            描述
          </th>
          <th style="width: 8%;"></span>
            是否必须
          </th>
          <th style="width: 30%;">
            修订记录
          </th>
        </tr>
      </thead>
      <tbody>
        <#list params as param>
          <@recurseFieldMacro field=param depth=0/>
        </#list>
      </tbody>
    </table>
  </#macro>

  <#macro resParamTableMacro param>
    <table id="resTable" class="resParamTable" width="100%" border="0" cellpadding="0" cellspacing="0">
      <thead>
        <tr>
          <th style="width: 20%;"></span>
            参数名
          </th>
          <th style="width: 22%;"></span>
            参数类型
          </th>
          <th style="width: 20%;"></span>
            描述
          </th>
          <th style="width: 8%;"></span>
            是否必须
          </th>
          <th style="width: 30%;">
            修订记录
          </th>
        </tr>
      </thead>
      <tbody>
        <@recurseFieldMacro field=param depth=0/>
      </tbody>
    </table>
  </#macro>

