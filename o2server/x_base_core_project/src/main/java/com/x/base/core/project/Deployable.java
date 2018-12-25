package com.x.base.core.project;

public abstract class Deployable extends Compilable {

	protected static final String druid_servlet = "<servlet><servlet-name>DruidStatView</servlet-name><servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class></servlet>";
	protected static final String druid_servlet_mapping = "<servlet-mapping><servlet-name>DruidStatView</servlet-name><url-pattern>/druid/*</url-pattern></servlet-mapping>";
	protected static final String druid_filter = "<filter><filter-name>DruidWebStatFilter</filter-name><filter-class>com.alibaba.druid.support.http.WebStatFilter</filter-class><init-param><param-name>exclusions</param-name><param-value>*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*</param-value></init-param></filter>";
	protected static final String druid_filter_mapping = "<filter-mapping><filter-name>DruidWebStatFilter</filter-name><url-pattern>/*</url-pattern></filter-mapping>";

	public abstract String pack(String distPath, String repositoryPath) throws Exception;

}