<#-- @implicitly included -->
<#-- @ftlvariable name="data" type="com.wikiparser.IndexData" -->



<html>
    <head>
     <link rel="stylesheet" href="/css/index.css">
    </head>
    <body>
        <ul>
        <#list data.items as item>
            <li>${item}</li>
        </#list>
        </ul>
    </body>
</html>
