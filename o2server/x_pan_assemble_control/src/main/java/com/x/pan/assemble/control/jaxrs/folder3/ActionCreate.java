package com.x.pan.assemble.control.jaxrs.folder3;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.ZonePermission;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author sword
 */
public class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionFolderNameEmpty();
			}
			if(StringUtils.isEmpty(wi.getSuperior())){
				throw new ExceptionFieldEmpty(Folder3.superior_FIELDNAME);
			}

			Folder3 superior = emc.find(wi.getSuperior(), Folder3.class);
			if(superior==null){
				throw new ExceptionFolderNotExist(wi.getSuperior());
			}

			if(!business.zoneEditableToCreate(effectivePerson, superior.getId())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			if (business.folder3().exist(wi.getName(), wi.getSuperior(), superior.getZoneId(), FileStatusEnum.VALID.getName(), null)) {
				throw new ExceptionFolderNameExist(wi.getName());
			}

			List<ZonePermission> permissionList = business.folder3().listZonePermission(superior.getId(), null);
			emc.beginTransaction(Folder3.class);
			emc.beginTransaction(ZonePermission.class);
			Folder3 folder = new Folder3(wi.getName(), effectivePerson.getDistinguishedName(), wi.getSuperior(), superior.getZoneId());
			emc.persist(folder, CheckPersistType.all);
			for (ZonePermission pp : permissionList){
				ZonePermission zonePermission = new ZonePermission(pp.getName(), pp.getRole(),
						folder.getId(), effectivePerson.getDistinguishedName());
				emc.persist(zonePermission, CheckPersistType.all);
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setId(folder.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Folder3 {

		static WrapCopier<Wi, Folder3> copier = WrapCopierFactory.wi(Wi.class, Folder3.class,
				ListTools.toList(Folder3.name_FIELDNAME, Folder3.superior_FIELDNAME), ListTools.toList(Folder3.capacity_FIELDNAME));

	}

	public static class Wo extends WoId {

	}

}
