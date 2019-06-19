package com.x.server.console.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionShowDataSource extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionShowDataSource.class);

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	public boolean execute(Integer interval, Integer repeat) {
		try {
			this.init();
			final Integer interval_adjust = Math.min(Math.max(interval, 1), 20);
			final Integer repeat_repeat = Math.min(Math.max(repeat, 1), 200);
			List<BasicDataSource> os = this.dataSources();
			new Thread() {
				public void run() {
					try {
						for (int i = 0; i < repeat_repeat; i++) {
							for (int j = 0; j < os.size(); j++) {
								BasicDataSource o = os.get(j);
								System.out.println("show dataSource process dataSource[" + j + "] " + o + " url: "
										+ o.getUrl() + ", numActive: " + o.getNumActive() + ", numIdle: "
										+ o.getNumIdle() + ".");
							}
							Thread.sleep(interval_adjust * 1000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();

		} catch (

		Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private List<BasicDataSource> dataSources() throws Exception {
		List<BasicDataSource> list = new ArrayList<>();
		if (Config.externalDataSources().enable()) {
			for (String name : Config.externalDataSources().names()) {
				list.add((BasicDataSource) Config.resource_jdbc(name));
			}
		} else {
			for (String name : Config.nodes().dataServers().names()) {
				list.add((BasicDataSource) Config.resource_jdbc(name));
			}
		}
		return list;
	}

}