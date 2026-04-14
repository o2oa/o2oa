package com.x.pan.assemble.control.jaxrs.zone;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.ZonePermission;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 重置共享区所有子目录权限
 * @author sword
 */
class ActionResetPermission extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger( ActionResetPermission.class );

	ActionResult<Wo> execute(final EffectivePerson effectivePerson, String id) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		final List<ZonePermission> permissionList;
		List<String> list;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Folder3 folder = emc.find(id, Folder3.class);
			if (null == folder || !Business.TOP_FOLD.equals(folder.getSuperior())) {
				throw new ExceptionFolderNotExist(id);
			}
			Business business = new Business(emc);
			if(!business.controlAble(effectivePerson)){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			logger.info("{}操作共享区【{}|{}】所有子目录的权限重置", effectivePerson.getDistinguishedName(), folder.getName(), id);
			permissionList = business.folder3().listZonePermission(id, null);
			list = business.folder3().listSubNested(id, null);
		}
		list.stream().forEach(folderId -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Folder3 folder = emc.find(folderId, Folder3.class);
				emc.beginTransaction(ZonePermission.class);
				emc.deleteEqual(ZonePermission.class, ZonePermission.zoneId_FIELDNAME, folderId);
				for (ZonePermission pp : permissionList){
					ZonePermission zonePermission = new ZonePermission(pp.getName(), pp.getRole(),
							folder.getId(), effectivePerson.getDistinguishedName());
					emc.persist(zonePermission, CheckPersistType.all);
				}
				folder.setHasSetPermission(false);
				emc.commit();
			}catch (Exception e){
				logger.error(e);
			}
		});
		CacheManager.notify(ZonePermission.class);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}
