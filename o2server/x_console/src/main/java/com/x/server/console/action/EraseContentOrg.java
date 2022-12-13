package com.x.server.console.action;

import java.util.Objects;

import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class EraseContentOrg extends EraseContent {

	private static final Logger LOGGER = LoggerFactory.getLogger(EraseContentOrg.class);

	@Override
	public boolean execute() {
		try {
			ClassLoader classLoader = EntityClassLoaderTools.concreteClassLoader();
			Thread.currentThread().setContextClassLoader(classLoader);
			this.init("org", null, classLoader);
			try (ScanResult sr = new ClassGraph().addClassLoader(classLoader)
                    .acceptPackages("com.x.organization.core.entity")
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
//			addClass(classLoader.loadClass("com.x.organization.core.entity.Bind"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.Custom"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.Definition"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.Group"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.Identity"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.OauthCode"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.Person"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.PersonAttribute"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.PersonCard"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.Role"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.Unit"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.UnitAttribute"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.UnitDuty"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.accredit.Empower"));
//			addClass(classLoader.loadClass("com.x.organization.core.entity.accredit.EmpowerLog"));
			this.run();
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}
}