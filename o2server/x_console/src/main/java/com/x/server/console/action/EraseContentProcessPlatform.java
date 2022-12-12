package com.x.server.console.action;

import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class EraseContentProcessPlatform extends EraseContent {

    private static final Logger LOGGER = LoggerFactory.getLogger(EraseContentProcessPlatform.class);

    public boolean execute() {
        try {
            ClassLoader classLoader = EntityClassLoaderTools.concreteClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            this.init("processPlatform", ItemCategory.pp, classLoader);
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.Attachment"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.DocSign"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.DocSignScrawl"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.DocumentVersion"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.Draft"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.KeyLock"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.Read"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.ReadCompleted"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.Record"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.Review"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.SerialNumber"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.Snap"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.Task"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.TaskCompleted"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.Work"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.WorkCompleted"));
            addClass(classLoader.loadClass("com.x.processplatform.core.entity.content.WorkLog"));
            addClass(classLoader.loadClass("com.x.query.core.entity.Item"));
            this.run();
            return true;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }
}