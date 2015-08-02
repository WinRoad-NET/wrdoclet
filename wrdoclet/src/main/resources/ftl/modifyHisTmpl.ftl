
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
            <#if record.modifier??>
              ${record.modifier}
            </#if>
          </td>
          <td>
            <#if record.memo??>
              ${record.memo}
            </#if>  
          </td>
          <td>
            <#if record.version??>
              ${record.version}
            </#if>
          </td>
        </tr>
      </#list>
    </tbody>
</#macro>
