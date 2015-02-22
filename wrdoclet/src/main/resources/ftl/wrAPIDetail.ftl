<!DOCTYPE 
    html
    PUBLIC
    "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">


<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="../stylesheet.css" title="Style">

  </head>

  <body>
    <div>
      ${openAPI.description}
    </div>
    <div>
      URL:
    </div>
    <div>
      ${openAPI.requestMapping.url}
    </div>
    <#if openAPI.modificationHistory?? && (openAPI.modificationHistory.modificationRecordList?size != 0)>
      <table border="1" cellpadding="3" cellspacing="1">
        <caption>
          修改历史
        </caption>
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
          <#list openAPI.modificationHistory.modificationRecordList as record>
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
        </table>
      </#if>
      <div>
        请求
      </div>
      <#if openAPI.inParameter?? >
        <#if openAPI.inParameter.fields?? >
          <table class="paramSummary" border="1" cellpadding="3" cellspacing="1">
            <caption>
              <span>
                ${openAPI.inParameter.name}
              </span>
              <#if openAPI.inParameter.description?? >
                <span>
                  ${openAPI.inParameter.description}
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
              </tr>
            </thead>
            <tbody>
              <#list openAPI.inParameter.fields as field>
                <tr>
                  <td>
                    ${field.name}
                  </td>
                  <td>
                    ${field.type}
                  </td>
                  <td>
                    ${field.description}
                  </td>
                  <td>
                    <#if field.parameterOccurs??>
                      ${field.parameterOccurs}
                    </#if>
                  </td>
                </tr>
              </#list>
            </tbody>
          </table>
        </#if>
      </#if>

      <div>
        响应
      </div>
      <#if openAPI.outParameter?? >
        <#if openAPI.outParameter.description?? >
          <div>
            ${openAPI.outParameter.description}
          </div>
        </#if>
        <#if openAPI.outParameter.fields?? >
          <table class="paramSummary" border="1" cellpadding="3" cellspacing="1">
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
              </tr>
            </thead>
            <tbody>
              <#list openAPI.outParameter.fields as field>
                <tr>
                  <td>
                    ${field.name}
                  </td>
                  <td>
                    ${field.type}
                  </td>
                  <td>
                    ${field.description}
                  </td>
                  <td>
                    <#if field.parameterOccurs??>
                      ${field.parameterOccurs}
                    </#if>
                  </td>
                </tr>
              </#list>
            </tbody>
          </table>
        </#if>
      </#if>
    </body>
  </html>
