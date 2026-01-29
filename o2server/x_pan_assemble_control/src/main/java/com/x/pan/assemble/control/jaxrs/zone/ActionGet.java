package com.x.pan.assemble.control.jaxrs.zone;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.ZonePermission;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Folder3 folder = emc.find(id, Folder3.class);
			if (null == folder || !Business.TOP_FOLD.equals(folder.getSuperior())) {
				throw new ExceptionFolderNotExist(id);
			}
			boolean isManager = business.controlAble(effectivePerson);
			if(!isManager){
				boolean isZoneReader = business.folder3().isZoneViewer(folder.getZoneId(), effectivePerson.getDistinguishedName());
				if(!isZoneReader) {
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}
			Wo wo = Wo.copier.copy(folder);
			boolean isZoneAdmin = business.folder3().isZoneAdmin(folder.getZoneId(), effectivePerson.getDistinguishedName());
			if(isManager || isZoneAdmin){
				List<WoZonePermission> zonePermissionList = emc.fetchEqual(ZonePermission.class, WoZonePermission.copier, ZonePermission.zoneId_FIELDNAME, folder.getZoneId());
				wo.setIsAdmin(true);
				wo.setIsEditor(true);
				wo.setZonePermissionList(zonePermissionList);
			} else {
				boolean isZoneEdit = business.folder3().isZoneEditor(folder.getZoneId(), effectivePerson.getDistinguishedName());
				wo.setIsEditor(isZoneEdit);
			}
			if(BooleanUtils.isTrue(wo.getIsEditor())){
				wo.setUsedCapacity(business.attachment3().statZoneCapacity(folder.getZoneId()));
				wo.setCapacity(wo.getCapacity());
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Zone {

		protected static WrapCopier<Folder3, Wo> copier = WrapCopierFactory.wo(Folder3.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
