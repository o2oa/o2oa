package com.x.query.service.processing;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.core.express.Organization;
import com.x.query.service.processing.factory.ProcessFactory;
import com.x.query.service.processing.factory.QueryFactory;
import com.x.query.service.processing.factory.StateFactory;

public class Business {

	private static final Logger LOGGER = LoggerFactory.getLogger(Business.class);

	private EntityManagerContainer emc;

	public Business() {
	}

	private static URLClassLoader dynamicEntityClassLoader = null;

	public static ClassLoader getDynamicEntityClassLoader() throws IOException, URISyntaxException {
		if (null == dynamicEntityClassLoader) {
			refreshDynamicEntityClassLoader();
		}
		return dynamicEntityClassLoader;
	}

	public static synchronized void refreshDynamicEntityClassLoader() throws IOException, URISyntaxException {
		List<URL> urlList = new ArrayList<>();
		IOFileFilter filter = new WildcardFileFilter(DynamicEntity.JAR_PREFIX + "*.jar");
		for (File o : FileUtils.listFiles(Config.dir_dynamic_jars(true), filter, null)) {
			urlList.add(o.toURI().toURL());
		}
		URL[] urls = new URL[urlList.size()];
		if (null != dynamicEntityClassLoader) {
			dynamicEntityClassLoader.close();
		}
		dynamicEntityClassLoader = URLClassLoader.newInstance(urlList.toArray(urls),
				null != ThisApplication.context() ? ThisApplication.context().servletContext().getClassLoader()
						: Thread.currentThread().getContextClassLoader());
	}

	public static void reloadClassLoader() {
		try {
			EntityManagerContainerFactory.close();
			Business.refreshDynamicEntityClassLoader();
			ThisApplication.context().initDatas(true, Business.getDynamicEntityClassLoader());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	private QueryFactory query;

	public QueryFactory query() throws Exception {
		if (null == this.query) {
			this.query = new QueryFactory(this);
		}
		return query;
	}

	private StateFactory state;

	public StateFactory state() throws Exception {
		if (null == this.state) {
			this.state = new StateFactory(this);
		}
		return state;
	}

	public boolean isManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager() || (BooleanUtils.isTrue(this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.QueryManager, OrganizationDefinition.Manager)))) {
			return true;
		}
		return false;
	}

	public boolean isProcessManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager() || (BooleanUtils.isTrue(this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.ProcessPlatformManager, OrganizationDefinition.Manager)))) {
			return true;
		}
		return false;
	}

	public boolean isServiceManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager() || (BooleanUtils.isTrue(this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.ServiceManager, OrganizationDefinition.Manager)))) {
			return true;
		}
		return false;
	}

	public boolean isCmsManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.CMSManager,
				OrganizationDefinition.Manager)) {
			return true;
		}
		return false;
	}

	public boolean isPortalManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager() || (this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.PortalManager, OrganizationDefinition.Manager))) {
			return true;
		}
		return false;
	}

	private ProcessFactory process;

	public ProcessFactory process() throws Exception {
		if (null == this.process) {
			this.process = new ProcessFactory(this);
		}
		return process;
	}

}
