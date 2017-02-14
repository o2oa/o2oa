package com.x.base.core.project;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.reflect.MethodUtils;

public abstract class AbstractApplicationServletContextListener implements ServletContextListener {

	protected String path;
	protected String context;

	public void contextInitialized(ServletContextEvent sce) {
		this.path = sce.getServletContext().getRealPath("");
		this.context = sce.getServletContext().getContextPath();
		System.out.println(context + " in " + path + " is starting.");
		try {
			this.initBeforeApplicationClass();
			this.initThisApplicationClass();
			System.out.println(context + " in " + path + " is started.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract Class<?> getThis();

	public void contextDestroyed(ServletContextEvent sce) {
		try {
			System.out.println(context + " in " + path + " start to destory.");
			this.destroyThisApplicationClass();
			this.destroyAfterThisApplicationClass();
			System.out.println(context + " destory completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initBeforeApplicationClass() throws Exception {
		ThisApplicationClass thisApplicationClass = this.getClass().getAnnotation(ThisApplicationClass.class);
		Class<?> clz = thisApplicationClass.value();
		MethodUtils.invokeStaticMethod(clz, "initBefore", this.getThis(), this.context, this.path);
	}

	private void initThisApplicationClass() throws Exception {
		ThisApplicationClass thisApplicationClass = this.getClass().getAnnotation(ThisApplicationClass.class);
		Class<?> clz = thisApplicationClass.value();
		MethodUtils.invokeStaticMethod(clz, "init");
	}

	private void destroyThisApplicationClass() throws Exception {
		ThisApplicationClass thisApplicationClass = this.getClass().getAnnotation(ThisApplicationClass.class);
		Class<?> clz = thisApplicationClass.value();
		MethodUtils.invokeStaticMethod(clz, "destroy");
	}

	private void destroyAfterThisApplicationClass() throws Exception {
		ThisApplicationClass thisApplicationClass = this.getClass().getAnnotation(ThisApplicationClass.class);
		Class<?> clz = thisApplicationClass.value();
		MethodUtils.invokeStaticMethod(clz, "destroyAfter");
	}
}