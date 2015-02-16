<!DOCTYPE 
    html
    PUBLIC
    "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>

  <body>
    <div>
      ${openAPI.description}
    </div>
    <div>
      ${openAPI.requestMapping.url}
    </div>
    <div>
      请求
    </div>
    <div>
      ${openAPI.inParameter.name}
    </div>
    <div>
      ${openAPI.inParameter.description}
    </div>
    <#if openAPI.inParameter.fields?? >
      <table>
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
  </body>
</html>
