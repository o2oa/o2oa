package com.x.collaboration.assemble.websocket.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.project.Context;
import com.x.base.core.project.clock.ClockTimerTask;
import com.x.collaboration.assemble.websocket.ThisApplication;

public class CleanupConnectionsTimer extends ClockTimerTask {

	public CleanupConnectionsTimer(Context context) {
		super(context);
	}

	private static Logger logger = LoggerFactory.getLogger(CleanupConnectionsTimer.class);

	public void execute() {
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