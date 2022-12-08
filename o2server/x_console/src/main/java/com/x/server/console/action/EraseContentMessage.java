package com.x.server.console.action;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class EraseContentMessage extends EraseContent {

    private static final Logger LOGGER = LoggerFactory.getLogger(EraseContentMessage.class);

    @Override
    public boolean execute() {
        try {
            ClassLoader classLoader = EntityClassLoaderTools.concreteClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            this.init("message", null, classLoader);
            addClass(classLoader.loadClass("com.x.message.core.entity.IMConversation"));
            addClass(classLoader.loadClass("com.x.message.core.entity.IMConversationExt"));
            addClass(classLoader.loadClass("com.x.message.core.entity.IMMsg"));
            addClass(classLoader.loadClass("com.x.message.core.entity.Instant"));
            addClass(classLoader.loadClass("com.x.message.core.entity.Mass"));
            addClass(classLoader.loadClass("com.x.message.core.entity.Message"));
            this.run();
            return true;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }
}