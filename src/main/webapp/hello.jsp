<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.HashMap"%>
hello world!
<BR>
My Blog:zh.jzg918.com
<% 	
	Object me = session.getAttribute("me");
	if(me != null){
		HashMap<String,String> map = (HashMap<String,String>)me;
		map.put("name", "zhanghua_change");
		session.setAttribute("me", map);
	}

%>