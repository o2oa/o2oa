package com.x.program.center.timertask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import com.x.base.core.application.Application;
import com.x.base.core.utils.StringTools;
import com.x.program.center.ThisApplication;

public class ClearApplicationsTimerTask extends TimerTask {
	public void run() {
		try {
			boolean changed = false;
			List<String> removes = new ArrayList<>();
			for (Entry<String, CopyOnWriteArrayList<Application>> en : ThisApplication.applications.entrySet()) {
				clearApplication(en.getValue());
				if (en.getValue().isEmpty()) {
					if (removes.add(en.getKey())) {
						changed = true;
					}
				}
			}
			for (String str : removes) {
				ThisApplication.applications.remove(str);
			}
			if (changed) {
				ThisApplication.applications.setToken(StringTools.uniqueToken());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean clearApplication(CopyOnWriteArrayList<Application> list) throws Exception {
		List<Application> removeApplications = new ArrayList<>();
		Date now = new Date();
		for (Application application : list) {
			if ((now.getTime() - application.getReportDate().getTime()) > 40 * 1000) {
				removeApplications.add(application);
			}
		}
		return list.removeAll(removeApplications);
	}
}