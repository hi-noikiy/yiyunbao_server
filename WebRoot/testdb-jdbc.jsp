<%@page contentType="text/html; charset=utf-8" %>
<%@page import="java.sql.*" %>
<%
//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
Class.forName("com.p6spy.engine.spy.P6SpyDriver").newInstance();
String url = "jdbc:mysql://localhost:3306/wuliu?characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull";
String user = "root";
String password = "12344321";
Connection conn = DriverManager.getConnection(url,user,password);
out.println(conn);
out.println("<br>数据库连接正常");
conn.setAutoCommit(false);
%>
	<div>Connection is connected.</div>
	<div>select version()</div>
	<%
	Statement stmt = conn.createStatement();
	ResultSet rs = stmt.executeQuery("select version()");
	while(rs.next()){
	%>
	<div><%=rs.getString(1) %></div>
	<%} %>
	<%conn.close();%>