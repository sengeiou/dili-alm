<%if(depts != null){%>
<ul>
	<%for(item in depts){%>
	<li><a href="#">${item.name!}</a>
		<#department_listTree depts="${item.children!}" />
	</li>
	<%}%>
</ul>
<%}%>