package com.x.server.console.action;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class EraseContentLog extends EraseContent {

    private static final Logger LOGGER = LoggerFactory.getLogger(EraseContentLog.class);

    @Override
    public boolean execute() {
        try {
            ClassLoader classLoader = EntityClassLoaderTools.concreteClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            this.init("log", null, classLoader);
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.element.FormVersion"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.element.ProcessVersion"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.element.ScriptVersion"));
            addClass(classLoader.loadClass("com.x.program.center.core.entity.PromptErrorLog"));
            addClass(classLoader.loadClass("com.x.program.center.core.entity.ScheduleLog"));
            addClass(classLoader.loadClass("com.x.program.center.core.entity.UnexpectedErrorLog"));
            addClass(classLoader.loadClass("com.x.program.center.core.entity.WarnLog"));
            this.run();
            return true;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }
}