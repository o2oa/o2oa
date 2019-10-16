package com.x.base.core.project.logger;

import java.io.PrintStream;
import java.util.Date;
import java.util.Objects;

import org.slf4j.helpers.MessageFormatter;

import com.x.base.core.project.config.Config;

public class Audit {

	private Date start;

	private String className = "";

	private String person = "";

	private String remoteAddress = "";
	private String uri = "";
	private String userAgent = "";

	protected Audit(String person, String remoteAddress, String uri, String userAgent, String className) {
		this.start = new Date();
		this.person = Objects.toString(person, "");
		this.remoteAddress = Objects.toString(remoteAddress, "");
		this.uri = Objects.toString(uri, "");
		this.userAgent = Objects.toString(userAgent, "");
		this.className = Objects.toString(className, "");
	}

	public void log() throws Exception {
		this.log("");
	}

	public void log(String message, Object... os) throws Exception {
		if (Config.logLevel().audit().enable()) {
			Date end = new Date();
			long elapsed = end.getTime() - start.getTime();
			PrintStream stream = (PrintStream) Config.resource(Config.RESOURCE_AUDITLOGPRINTSTREAM);
			stream.printf("%tF %tT,,,%d,,,%s,,,%s,,,%s,,,%s,,,%s,,,%s%n", end, end, elapsed, this.person,
					this.remoteAddress, this.uri, this.userAgent, this.className,
					MessageFormatter.arrayFormat(Objects.toString(message, ""), os).getMessage());
		}
	}
}
