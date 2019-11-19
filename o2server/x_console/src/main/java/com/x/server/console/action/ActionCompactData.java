package com.x.server.console.action;

import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.store.fs.FileUtils;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.RunScript;
import org.h2.tools.Script;

import com.x.base.core.container.factory.SlicePropertiesBuilder;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.server.console.server.Servers;

public class ActionCompactData {

	private static Logger logger = LoggerFactory.getLogger(ActionCompactData.class);

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	public boolean execute(String password) throws Exception {
		this.init();
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			logger.print("password not match.");
			return false;
		}
		DataServer server = Config.currentNode().getData();
		if (null == server) {
			logger.print("not config dataServer.");
			return false;
		}
		if (!BooleanUtils.isTrue(server.getEnable())) {
			logger.print("data server not enable.");
			return false;
		}
		if (Servers.dataServerIsRunning()) {
			logger.print("data server is running.");
			return false;
		}
		//Class.forName(SlicePropertiesBuilder.driver_h2).newInstance();
		logger.print("compact data start at {}.", DateTools.format(start));
		String dir = StringUtils.replace(Config.base(), "\\", "/") + "/local/repository/data";
		String url = "jdbc:h2:" + dir + "/X;FILE_LOCK=NO";
		String backup = dir + "/backup.sql";
		Script.process(url, "sa", Config.token().getPassword(), backup, "", "");
		DeleteDbFiles.execute(dir, "X", true);
		RunScript.execute(url, "sa", Config.token().getPassword(), backup, null, false);
		FileUtils.delete(backup);
		Date end = new Date();
		System.out.println("compact data completed at " + DateTools.format(end) + ", elapsed:"
				+ (end.getTime() - start.getTime()) + "ms.");
		return true;
	}

}