package com.x.cms.assemble.search.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;

public class AppInfoServiceAdv {

	private AppInfoService appInfoService = new AppInfoService();

	public AppInfo get( String id ) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.get(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<AppInfo> listAll( String documentType ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAll(emc, documentType);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listAllIds(String documentType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAllIds(emc, documentType);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<AppInfo> list(List<String> app_ids) throws Exception {
		if (ListTools.isEmpty( app_ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.list(emc, app_ids);
		} catch (Exception e) {
			throw e;
		}
	}
}
