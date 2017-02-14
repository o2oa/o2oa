package com.x.server.console;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class CommandFactory {

	public static final Pattern start_pattern = Pattern
			.compile("^ {0,}start {0,}(data|storage|center|application|web|all|) {0,}$", Pattern.CASE_INSENSITIVE);

	public static final Pattern stop_pattern = Pattern
			.compile("^ {0,}stop {0,}(data|storage|center|application|web|all|) {0,}$", Pattern.CASE_INSENSITIVE);

	public static final Pattern dump_pattern = Pattern.compile("^ {0,}dump {0,}(data|storage) {0,}$",
			Pattern.CASE_INSENSITIVE);

	public static final Pattern restore_pattern = Pattern.compile(
			"^ {0,}restore {0,}(data|storage) {0,}([2][0][1-9][0-9][0-1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9])$",
			Pattern.CASE_INSENSITIVE);

	public static final Pattern help_pattern = Pattern.compile("^ {0,}help {0,}$", Pattern.CASE_INSENSITIVE);

	public static final Pattern exit_pattern = Pattern.compile("^ {0,}exit {0,}$", Pattern.CASE_INSENSITIVE);

	public static final Pattern update_pattern = Pattern.compile("^ {0,}update {0,}$", Pattern.CASE_INSENSITIVE);

	public static final Pattern version_pattern = Pattern.compile("^ {0,}version {0,}$", Pattern.CASE_INSENSITIVE);

	public static final Pattern config_pattern = Pattern.compile("^ {0,}config {0,}$", Pattern.CASE_INSENSITIVE);

	public static final Pattern log_pattern = Pattern.compile("^ {0,}log {0,}$", Pattern.CASE_INSENSITIVE);

	public static void printHelp(String base, String version) {
		String help = "";
		help += "server directory:" + base;
		help += StringUtils.LF;
		help += "version:" + version;
		help += StringUtils.LF;
		help += " help" + "\t\t\t\t\t" + "show useage message.";
		help += StringUtils.LF;
		help += " start|stop [all]" + "\t\t\t" + "start all server.";
		help += StringUtils.LF;
		help += " start|stop data" + "\t\t\t" + "start data server.";
		help += StringUtils.LF;
		help += " start|stop storage" + "\t\t\t" + "start storage server.";
		help += StringUtils.LF;
		help += " start|stop center" + "\t\t\t" + "start center server.";
		help += StringUtils.LF;
		help += " start|stop application" + "\t\t\t" + "start application server.";
		help += StringUtils.LF;
		help += " start|stop web" + "\t\t\t\t" + "start web server.";
		help += StringUtils.LF;
		help += " dump data" + "\t\t\t\t" + "dump data from database.";
		help += StringUtils.LF;
		help += " dump storage" + "\t\t\t\t" + "dump storage from database and file system.";
		help += StringUtils.LF;
		help += " restore data yyyyMMddHHmmss" + "\t\t" + "restore data to database.";
		help += StringUtils.LF;
		help += " restore storage yyyyMMddHHmmss" + "\t\t" + "restore storage to database and file system.";
		help += StringUtils.LF;
		help += " version " + "\t\t\t\t" + "show current server version.";
		help += StringUtils.LF;
		help += " update" + "\t\t\t\t\t" + "update server to available version.";
		help += StringUtils.LF;
		help += " config" + "\t\t\t\t\t" + "synchronize config from primary node.";
		help += StringUtils.LF;
		help += " exit" + "\t\t\t\t\t" + "stop and exit.";
		help += StringUtils.LF;
		System.out.println(help);
	}

}
