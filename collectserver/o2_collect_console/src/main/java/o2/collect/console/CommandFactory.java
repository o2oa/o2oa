package o2.collect.console;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


public class CommandFactory {

	public static final Pattern start_pattern = Pattern.compile("^ {0,}start {0,}$", Pattern.CASE_INSENSITIVE);

	public static final Pattern stop_pattern = Pattern.compile("^ {0,}stop {0,}$", Pattern.CASE_INSENSITIVE);

	public static final Pattern help_pattern = Pattern.compile("^ {0,}help {0,}$", Pattern.CASE_INSENSITIVE);

	public static final Pattern exit_pattern = Pattern.compile("^ {0,}exit {0,}$", Pattern.CASE_INSENSITIVE);

	public static void printHelp() {
		String help = "";
		help += "usage: O2Paltform";
		help += StringUtils.LF;
		help += " help" + "\t\t\t\t" + "show useage message.";
		help += StringUtils.LF;
		help += " start|stop" + "\t\t\t" + "start o2 select server.";
		help += StringUtils.LF;
		help += " exit" + "\t\t\t\t" + "stop and exit.";
		help += StringUtils.LF;
		System.out.println(help);
	}

	@Test
	public void test() {
		printHelp();
	}

}
