package com.x.onlyofficefile.assemble.control;
import java.util.Timer;
import com.x.base.core.project.Context;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ThisApplication {
	private static Logger logger = LoggerFactory.getLogger(ThisApplication.class);

	protected static Context context;
	protected static Timer timer;
	public static Context context() {
		return context;
	}

	
	public static void init() {
		
	}

	public static void destroy() {
		try {

	    	} catch (Exception e) {
	    	 logger.error(e);
		 }
	 }
}
