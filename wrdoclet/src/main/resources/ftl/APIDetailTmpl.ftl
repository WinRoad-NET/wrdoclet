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
    <div class="accessPath">
      <#if openAPI.requestMapping.methodType??>
        <span class="lable label-method-type">
          ${openAPI.requestMapping.methodType}
        </span>
      </#if>
      <#if openAPI.authNeeded??>
      	<#if openAPI.authNeeded>
			<img title="需要认证" class="authIcon" src="data:image/png;base64,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"/>
      	<#else>
      		<img title="不需要认证" class="authIcon" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAASsSURBVEhLhZbHSmxBEIZrxpxzwICCaSm4EsSNLtS9C30FH0vcufERXIgbFVRQUMEcEcw5zT1f6X/oO3ecW1B0n+rq+it190l8fn6mEomEwVAqlXL++PjwUVRQUODj6empvb+/+xp7amtrraSkxL6+viyZTLqOCB3kEPYiLJ9Diefn51Rubq7BEIsoyRnkjJubm7axsWENDQ2uJ6P39/dWWlpqQ0NDLk8nBYGz2MI++xKPj4+pnJwcg1l4e3tz5by8PN9IFHNzcx5Va2ur60EYQ5fx/Pzc946MjPhaSIC8vr76iE0BJ/WBAQQYUFoBnZ2dtZaWFuvp6XH51dWVR/ny8uLAGC0vL7f19XU7PDz0fb+RygMliQDw/Pz8OL2+EIHu7Oy48crKStve3raZmRlbWVmxk5MTu7u7c8aJ29tbq6urs+XlZd8bkkoCDiPfkHeD0hd6BOFQe3u77e/v2/X1tY2NjdnAwIA3FFkKmX0XFxe+Dzshq+nQ4RuKZElfEIdUU1Nje3t7Xtvu7m7fRJSkFzAahjFqUI9GhJ6+GTVHXlhY6IH+jZRG1F0NR00FhBxmjhNQGBnEHED0mLMuRp4VGCI9AmGUMUUsZ9Bra2uL94QMGFEyEgSUFViehgBslBPInp6e4vQPDw//7LR4D8BqLHoGxm5WYJR06AWKMY3UFjmgExMTro+jYbScFuTUVqDsyQrMuUUREKVZTM0xQMTNzc1+5JCLiBJQ9ivdjOj8t7lQ4Iq8ublxEDkQ3XYeNSnmcuF4iTAejhBzgeOIR/6z9iv19vb6hQEorBsL8LKyMj/nOPfw8OBGs5EcQC+rJtHxAEBEDRi3FLWtqqqyxsZGq6io8LkuD4jIfqMY/Oc7I9FY0Pj4eFzr+vp66+josKKiIquurvaaYYg7fG1tzSPn+3+UiLz7xz2MAYQRbi5GXidqSpphou7q6vKs0ESkm4zwUJCJzs5Ot4V51TakjMDQwsJCXEcdfnUzYHQ8RwTim+yQAdJ+dHTkR6y/v9/XFUhIGYF5gYiUu5plqYQpZC5nMMro5zOaNzU1eYZWV1dtdHTUHWQdkgMZa3x2dubNRDo5p4yai8kGIxnQSA9onZTTiEtLS24zve5/ARMBADyDnDUBYDSbE6GcPtjd3fX0FxcX2/HxsTsEcAieJD0wDQMw4+XlpW/EY0UkDiMNGVDW2AMQziPjHWdOzVUOL49qKKE6FuP8XXBM6GqMIk8HY8QoYERLPZFjS2s0G3b5dtCozhF/Z1sviKJiA8yGEFRyrYnDdUZlCPs4BbOGQwTqXQ0gCxBncnp62rtaTv3TGN8ex3LmAOM8x8wNR2vMudn4H+vr63MZJ4Wj56nGEz5gvJ+cnIzTQq1hPA1ZEWgdxrBKhjMEgR5znArvhCSK+g+C6WY2TE1N+VygOCSjkskxpRKD6IUZOTg48NeLO1/n2TMSGfDbQWkT8Y3C4uKizc/Px3cwzmUiupk3GUfYBwHCBTI4OBg7JUpEDZDSnwGEsrwi9QCR0q2tLf+3pkOJDh0cITvUkUcDGd/cWvwc8JgQqTKCLdl3YEAFopQpMqUsnUg19FsGRNii8SAwvjOSsj/kQPF2isQR/wAAAABJRU5ErkJggg==">
      	</#if>
      </#if>
      <#if openAPI.requestMapping.url??>
      	<div class="openAPIUrl">
        	${openAPI.requestMapping.url}
        </div>
      </#if>
    </div>
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