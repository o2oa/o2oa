package com.x.server.console.action;

import java.sql.DriverManager;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.store.fs.FileUtils;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.RunScript;
import org.h2.tools.Script;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.server.console.server.Servers;

public class CompactLocalH2 {

	private static Logger logger = LoggerFactory.getLogger(CompactLocalH2.class);

	private Date start =new Date();

	public boolean execute() throws Exception {
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
			logger.print("data server is running, must stop data server first.");
			return false;
		}
		DriverManager.registerDriver(new org.h2.Driver());
		logger.print("compact data start at {}.", DateTools.format(start));
		String dir = StringUtils.replace(Config.base(), "\\", "/") + "/local/repository/data";
		String url = "jdbc:h2:" + dir + "/X;FILE_LOCK=NO";
		String backup = dir + "/backup.sql";
		Script.process(url, "sa", Config.token().getPassword(), backup, "", "");
		DeleteDbFiles.execute(dir, "X", true);
		RunScript.execute(url, "sa", Config.token().getPassword(), backup, null, false);
		FileUtils.delete(backup);
		Date end = new Date();
		System.out.println(String.format("compact data completed at %s, elapsed:%dms.", DateTools.format(end),
				end.getTime() - start.getTime()));
		return true;
	}

}