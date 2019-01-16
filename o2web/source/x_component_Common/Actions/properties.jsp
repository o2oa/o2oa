<%@ page language="java" contentType="application/json; charset=UTF-8" import="java.io.FileInputStream, java.util.Properties"%>
<%
	String propertiesPath = this.getClass().getClassLoader().getResource("slot_prop.properties").getPath();
	FileInputStream fis = new FileInputStream(propertiesPath);
	Properties prop = new Properties();
	prop.load(fis);
%>
{
	"slotHost": "<%=prop.getProperty("platform_slot_host") %>",
	"listAddress": "<%=prop.getProperty("platform_slot_list_address") %>",
	"getAddress": "<%=prop.getProperty("platform_slot_get_address") %>",
	"getId": "<%=prop.getProperty("platform_uuid") %>"
}
<% fis.close(); %>