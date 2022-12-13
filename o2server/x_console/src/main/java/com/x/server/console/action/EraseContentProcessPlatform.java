package com.x.server.console.action;

import java.util.Objects;

import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class EraseContentProcessPlatform extends EraseContent {

    private static final Logger LOGGER = LoggerFactory.getLogger(EraseContentProcessPlatform.class);

    public boolean execute() {
        try {
            ClassLoader classLoader = EntityClassLoaderTools.concreteClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            this.init("processPlatform", ItemCategory.pp, classLoader);
            try (ScanResult sr = new ClassGraph().addClassLoader(classLoader)
                    .acceptPackages("com.x.processplatform.core.entity.content",
                            "com.x.processplatform.core.entity.message")
                    .enableAnnotationInfo().scan()) {
                for (ClassInfo info : sr.getClassesWithAnnotation(ContainerEntity.class.getName())) {
                    Class<?> cls = classLoader.loadClass(info.getName());
                    ContainerEntity containerEntity = cls.getAnnotation(ContainerEntity.class);
                    if (Objects.equals(containerEntity.type(), ContainerEntity.Type.content)
                            || Objects.equals(containerEntity.type(), ContainerEntity.Type.log)) {
                        addClass(cls);
                    }
                }
            }
            addClass(classLoader.loadClass("com.x.query.core.entity.Item"));
            this.run();
            return true;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }
}