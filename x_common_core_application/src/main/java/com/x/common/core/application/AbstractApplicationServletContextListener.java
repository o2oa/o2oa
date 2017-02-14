package com.x.common.core.application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.openjpa.enhance.PCRegistry;

import com.x.base.core.entity.JpaObject;
import com.x.common.core.application.cache.ApplicationCache;
import com.x.common.core.application.configuration.application.Applications;
import com.x.common.core.application.configuration.storage.StorageMappings;
import com.x.common.core.application.task.ReportTask;
import com.x.common.core.container.DataMappings;
import com.x.common.core.container.factory.EntityManagerContainerFactory;

public abstract class AbstractApplicationServletContextListener implements ServletContextListener {

	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	protected String webApplicationDirectory;
	protected String context;
	protected String displayName;

	public void contextInitialized(ServletContextEvent sce) {
		this.webApplicationDirectory = sce.getServletContext().getRealPath("");
		this.context = sce.getServletContext().getContextPath();
		this.displayName = context + " under " + webApplicationDirectory;
		try {
			System.out.println(displayName + "starting.");
			this.initThisApplicationClass();
			System.out.println(displayName + "loading applications.");
			AbstractThisApplication.applications = AbstractThisApplication.loadCenterObject("applications",
					Applications.class);
			if (this.loadDatas()) {
				System.out.println(displayName + "loading datas.");
				DataMappings dataMappings = AbstractThisApplication.loadCenterObject("datamappings",
						DataMappings.class);
				EntityManagerContainerFactory.init(webApplicationDirectory, dataMappings);
			}
			if (this.loadStorages()) {
				System.out.println(displayName + "loading storages.");
				AbstractThisApplication.storageMappings = AbstractThisApplication.loadCenterObject("storagemappings",
						StorageMappings.class);
			}
			System.out.println(displayName + "init other.");
			this.initOther();
			/* 必须先将ThisApplication.initialized = true，否则Center无法getAvaliable */
			AbstractThisApplication.initialized = true;
			/* 启动报告任务 */
			scheduler.scheduleWithFixedDelay(new ReportTask(), 10, 20, TimeUnit.SECONDS);
			System.out.println(displayName + "start completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void scheduleWithFixedDelay(Runnable runnable, Long initialDelay, Long delay) {
		this.scheduler.scheduleWithFixedDelay(runnable, initialDelay, delay, TimeUnit.SECONDS);
	}

	protected void schedule(Runnable runnable, Long delay) {
		this.scheduler.schedule(runnable, delay, TimeUnit.SECONDS);
	}

	public abstract Class<?> getThis();

	public abstract boolean loadDatas();

	public abstract boolean loadApplications();

	public abstract boolean loadStorages();

	public abstract void initOther() throws Exception;

	public abstract void destroyOther();

	public void contextDestroyed(ServletContextEvent sce) {
		try {
			System.out.println(displayName + " start to destory.");
			this.destroyOther();
			scheduler.shutdownNow();
			ApplicationCache.shutdown();
			EntityManagerContainerFactory.close();
			// PCRegistry.deRegister(sce.getClass().getClassLoader());
			PCRegistry.deRegister(JpaObject.class.getClassLoader());
			Thread.sleep(1000);
			System.out.println(displayName + " destory completed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initThisApplicationClass() throws Exception {
		ThisApplicationClass thisApplicationClass = this.getClass().getAnnotation(ThisApplicationClass.class);
		Class<?> clz = thisApplicationClass.value();
		MethodUtils.invokeStaticMethod(clz, "init", this.getThis(), this.webApplicationDirectory, this.context);
	}
}