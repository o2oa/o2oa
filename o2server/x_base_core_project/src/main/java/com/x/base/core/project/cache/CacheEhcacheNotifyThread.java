package com.x.base.core.project.cache;

import java.util.concurrent.LinkedBlockingQueue;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;

public class CacheEhcacheNotifyThread extends Thread {

	public CacheEhcacheNotifyThread(LinkedBlockingQueue<WrapClearCacheRequest> queue) {
		this.queue = queue;
	}

	private LinkedBlockingQueue<WrapClearCacheRequest> queue;

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				WrapClearCacheRequest wi = queue.take();
				String url = Config.url_x_program_center_jaxrs("cachedispatch");
				CipherConnectionAction.put(false, url, wi);
			} catch (InterruptedException e) {
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}