package com.x.base.core.project.logger;

import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import com.x.base.core.project.config.Config;

public class Audit {

	private Date start;

	private String className = "";

	private String person = "";

	private String remoteAddress = "";
	private String uri = "";
	private String userAgent = "";

	private String userId = "";
	private String userName = "";

	protected Audit(String person, String remoteAddress, String uri, String userAgent, String className) {
		this.start = new Date();
		this.person = Objects.toString(person, "");
		if(person==null || person.indexOf("@") == -1){
			userId = Objects.toString(person, "");
			userName = Objects.toString(person, "");
		}else{
			String persons[] = person.split("@");
			userId = persons[1];
			userName = persons[0];
		}
		this.remoteAddress = Objects.toString(remoteAddress, "");
		this.uri = Objects.toString(uri, "");
		this.userAgent = Objects.toString(userAgent, "");
		this.className = Objects.toString(className, "");

	}

	public void log1() throws Exception {
		this.log1("");
	}

	/**
	 * 审计日志
	 * @param message
	 * @param os
	 * @throws Exception
	 */
	public void log1(String message, Object... os){
		try {
			if (Config.logLevel().audit().enable()) {
				Date end = new Date();
				long elapsed = end.getTime() - start.getTime();
				PrintStream stream = (PrintStream) Config.resource(Config.RESOURCE_AUDITLOGPRINTSTREAM);
				stream.printf("%tF %tT,,,%d,,,%s,,,%s,,,%s,,,%s,,,%s,,,%s%n", end, end, elapsed, this.person,
						this.remoteAddress, this.uri, this.userAgent, this.className,
						MessageFormatter.arrayFormat(Objects.toString(message, ""), os).getMessage());
			}
		} catch (Exception e) {
			System.out.println("审计日志打印异常"+e.getMessage());
		}
	}

	/**
	 * 移动集团审计日志格式：
	 * 默认时间戳之类的信息|日志版本号|请求ID|请求深度|请求链|OA账号（OA&合同不为空）|员工工号（报账不为空）
	 * |系统归属|系统名称|模块名称|表单类型|操作名称|是否统计上报|扩展信息|时间戳|耗时|请求结果|主机IP
	 * |主机名|终端类型|终端IP |终端型号|终端IMEI|错误堆栈
	 * @throws Exception
	 */
	public void log(String person, String op){
		try {
			if (Config.logLevel().audit().enable()) {
				if(person!=null){
					this.person = person;
					if(person.indexOf("@") == -1){
						userId = person;
						userName = person;
					}else{
						String persons[] = person.split("@");
						userId = persons[1];
						userName = persons[0];
					}
				}
				Date end = new Date();
				long elapsed = end.getTime() - start.getTime();
				InetAddress addr = InetAddress.getLocalHost();
				String hostAddress = addr.getHostAddress();
				String hostName = addr.getHostName();
				String system = Objects.toString(Config.logLevel().audit().getSystem(), "OA");
				String systemName = Objects.toString(Config.logLevel().audit().getSystemName(), "OA系统");
				String companycode = Objects.toString(Config.logLevel().audit().getCompanycode(), "");
				String mode = this.uri;
				if(this.uri!=null) {
					String[] uris = this.uri.split("/");
					if (uris.length > 1) {
						mode = uris[1];
					}
				}
				PrintStream stream = (PrintStream) Config.resource(Config.RESOURCE_AUDITLOGPRINTSTREAM);
				stream.printf("%tF %tT|2.0||1||%s|%s|%s|%s|%s||%s|true|%s|%d|%d|true|%s|%s|%s|%s|%s||", end, end, this.userId,
						this.userId, systemName, system, mode, op, this.getParameter(op, system, companycode), end.getTime(), elapsed, hostAddress, hostName,
						getTerminal(), this.remoteAddress, this.userAgent);
				stream.println();
			}
		} catch (Exception e) {
			System.out.println("审计日志打印异常"+e.getMessage());
		}
	}

	public String getTerminal(){
		if(StringUtils.isNotBlank(this.userAgent)) {
			String userAgent = this.userAgent.toLowerCase();
			if (userAgent.indexOf("micromessenger") != -1) {
				//微信
				return "MOA";
			} else if (userAgent.indexOf("android") != -1) {
				//安卓
				return "MOA";
			} else if (userAgent.indexOf("iphone") != -1 || userAgent.indexOf("ipad") != -1 || userAgent.indexOf("ipod") != -1) {
				//苹果
				return "MOA";
			} else {
				//电脑
				return "PC";
			}
		}
		return "PC";
	}

	public String getParameter(String op,String system,String companycode){
		StringBuffer parameter =new StringBuffer();
		if("登录".equals(op)){
			parameter.append("LOG_RESULT=0&interfacename=").append(system).append("_")
					.append(getTerminal()).append("_LOGIN&errorCode=ok&companycode=").append(companycode).append("&LOGIN_ENTRY=0");
		}else if("注销".equals(op)){
			parameter.append("LOG_RESULT=0&interfacename=").append(system).append("_")
					.append(getTerminal()).append("_LOGOUT&errorCode=ok&companycode=").append(companycode).append("&LOGOUT_ENTRY=0");
		}else{
			parameter.append("LOG_RESULT=0&interfacename=").append(system).append("_")
					.append(getTerminal()).append("_OPERATION&errorCode=ok&companycode=").append(companycode);
		}
		return parameter.toString();
	}
}
