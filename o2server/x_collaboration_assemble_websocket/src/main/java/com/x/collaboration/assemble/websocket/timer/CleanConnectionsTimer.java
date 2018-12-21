package com.x.collaboration.assemble.websocket.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.websocket.Session;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.collaboration.assemble.websocket.ThisApplication;

public class CleanConnectionsTimer implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(CleanConnectionsTimer.class);

	public void run() {
		try {
			List<String> removes = new ArrayList<>();
			for (Entry<String, Session> entry : ThisApplication.connections.entrySet()) {
				if ((null == entry.getValue()) || (!entry.getValue().isOpen())) {
					removes.add(entry.getKey());
				}
			}
			for (String str : removes) {
				ThisApplication.connections.remove(str);
			}
			logger.info("clean {} websocket session.", removes.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}