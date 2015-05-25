<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page language="java" import="java.util.ArrayList"%>
    <%@ page language="java" import="java.util.HashMap"%>
    <%@ page language="java" import="java.util.Date"%>
    <%@ page language="java" import="java.util.Calendar"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Test Session</title>
</head>
<body>
<% 	session.setAttribute("ip", request.getRemoteHost());%>
<% 	HashMap<String,String> map = new HashMap<String,String>();
	map.put("name", "zhanghua");
	map.put("address", "上海");
	map.put("E-Mail", "xtspring2011@126.com");
	map.put("Blog", "zh.jzg918.com");
	session.setAttribute("me", map);
%>
<% 	ArrayList<Date> list = new ArrayList<Date>();
	Date now = new Date();
	Calendar c = Calendar.getInstance();
	c.setTime(now);
	for(int i=1; i<=7; i++){
		c.add(Calendar.DAY_OF_MONTH, 1);
		list.add(c.getTime());
	}
	session.setAttribute("days", list);
	response.sendRedirect("test.jsp");
%>
</body>
</html>