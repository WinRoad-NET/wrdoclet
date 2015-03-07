
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
