<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta charset="utf-8" />
    <title>寿光电子结算管理平台</title>
    <meta name="description" content="overview &amp; stats" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
    <#css/>
    <#js/>
    
    <%
    if(has(pageCss) && pageCss=="true"){
    %>
    <#pageCss/>
    <%
    }
    %>
    
    <% if(has(pageJs) && pageJs=="true"){%>
    <#pageJs/>
    <%}%>
    <script type="text/javascript">
        var contextPath = '${contextPath}';
    </script>
</head>
<body>
${tag.body}
</body>
</html>
