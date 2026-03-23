package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.file.core.entity.personal.Attachment2;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.AttachmentVersion;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

class ActionListHistory extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionListHistory.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Attachment3 attachment = emc.find(id, Attachment3.class);
			if (null == attachment) {
				Attachment2 attachment2 = emc.find(id, Attachment2.class);
				if(attachment2 == null) {
					throw new ExceptionAttachmentNotExist(id);
				}else{
					if (!business.controlAble(effectivePerson) && !StringUtils.equals(effectivePerson.getDistinguishedName(), attachment2.getPerson())) {
						throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
					}
				}
			}else{
				String zoneId = business.getSystemConfig().getReadPermissionDown() ? attachment.getFolder() : attachment.getZoneId();
				if(!business.zoneViewable(effectivePerson, zoneId)){
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}

			List<Wo> woList = emc.fetchEqual(AttachmentVersion.class, Wo.copier, AttachmentVersion.attachmentId_FIELDNAME, id);
			SortTools.asc(woList, AttachmentVersion.fileVersion_FIELDNAME);
			result.setData(woList);
			return result;
		}
	}

	public static class Wo extends AttachmentVersion {

		private static final long serialVersionUID = -4527691019226002575L;

		static WrapCopier<AttachmentVersion, Wo> copier = WrapCopierFactory.wo(AttachmentVersion.class, Wo.class,
				JpaObject.singularAttributeField(AttachmentVersion.class, true, true),
				null);

	}
}
