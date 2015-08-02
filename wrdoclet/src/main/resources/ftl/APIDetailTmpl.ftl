<#include "modifyHisTmpl.ftl">
<#include "paramTableTmpl.ftl">
  <p class="qualifiedName">
    ☞&nbsp;${openAPI.qualifiedName}
  </p>
    <#if openAPI.description??>
	    <h3>
	      描述
	    </h3>
	    <p>
        ${openAPI.description}
	    </p>
    </#if>
    <h3>
      访问路径
    </h3>
    <p>
      <#if openAPI.requestMapping.methodType??>
        <span class="lable label-method-type">
          ${openAPI.requestMapping.methodType}
        </span>
      </#if>
      <#if openAPI.requestMapping.url??>
        ${openAPI.requestMapping.url}
      </#if>
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
    <#if openAPI.inParameters?? && (openAPI.inParameters?size != 0)>
        <@reqParamTableMacro params=openAPI.inParameters />
    </#if>

    <h3>
      响应
    </h3>
    <#if openAPI.returnCode?? && openAPI.returnCode != "">
      <span class='returnCode'>
      <b>响应码：</b>${openAPI.returnCode}
      </span>
    </#if>
    <#if openAPI.outParameter?? >
        <@resParamTableMacro param=openAPI.outParameter />
    </#if>