package com.x.file.assemble.control;

import com.x.base.core.project.Context;
import com.x.collaboration.core.message.Collaboration;
import com.x.file.assemble.control.jaxrs.file.FileRemoveQueue;

public class ThisApplication {

	public static FileRemoveQueue fileRemoveQueue;

	public static Collaboration collaboration;

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			fileRemoveQueue = new FileRemoveQueue();
			context().startQueue(fileRemoveQueue);
			Collaboration.start(context());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			Collaboration.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
