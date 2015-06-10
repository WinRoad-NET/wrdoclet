<#include "modifyHisTmpl.ftl">
<#include "paramTableTmpl.ftl">
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
      <#if openAPI.requestMapping.methodType??>
        <span class="lable label-method-type">
          ${openAPI.requestMapping.methodType}
        </span>
      </#if>
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
    <#if openAPI.inParameters?? && (openAPI.inParameters?size != 0)>
        <@reqParamTableMacro params=openAPI.inParameters />
    </#if>

    <h3>
      响应
    </h3>
    <#if openAPI.returnCode?? >
      <span class='returnCode'>
      <b>响应码：</b>${openAPI.returnCode}
      </span>
    </#if>
    <#if openAPI.outParameter?? >
      <#if openAPI.outParameter.description?? >
        <div>
          ${openAPI.outParameter.description}
        </div>
      </#if>
        <@resParamTableMacro param=openAPI.outParameter />
    </#if>