<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page language="java" import="java.util.ArrayList"%>
    <%@ page language="java" import="java.util.HashMap"%>
    <%@ page language="java" import="java.util.Date"%>
    <%@ page language="java" import="java.util.Calendar"%>
    <%@ page language="java" import="java.text.SimpleDateFormat"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Test Session</title>
</head>
<body>
<%
	HashMap<String,String> me = (HashMap<String,String>)session.getAttribute("me");
	ArrayList<Date> list = (ArrayList<Date>)session.getAttribute("days");
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
%>

Your IP :<%=session.getAttribute("ip")%>
<br>
About me:
<br>
Name:<%=me.get("name")%>;
Address:<%=me.get("address")%>;
E-Mail:<b><%=me.get("E-Mail")%></b>;
Blog:<b><%=me.get("Blog")%></b>;
<br>
The Next 7 Day:
<br>
<%for(int i=0;i<list.size(); i++){	%>
	<%String day = df.format(list.get(i)); %>
	Day<%=i+1%>:<%=day %>
	<br>
<% }%>
</body>
</html>