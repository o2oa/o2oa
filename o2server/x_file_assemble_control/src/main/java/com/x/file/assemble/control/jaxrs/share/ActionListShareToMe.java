package com.x.file.assemble.control.jaxrs.share;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Group;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Share;

class ActionListShareToMe extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListShareToMe.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String fileType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if (EMPTY_SYMBOL.equals(fileType)) {
				fileType = null;
			}
			List<String> shareIds = new ArrayList<>();
			shareIds.addAll(business.share().listWithShareUser1(effectivePerson.getDistinguishedName(), fileType));
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			for (String str : identities) {
				List<String> units = business.organization().unit().listWithIdentitySupNested(str);
				for(String unitName : units){
					Unit unit = business.organization().unit().getObject( unitName );
					if(unit!=null){
						shareIds.addAll(business.share().listWithShareOrg1(unit.getUnique(), fileType));
					}
				}
			}
			List<String> groupIds = business.organization().group().listWithPersonReference(
					ListTools.toList(effectivePerson.getDistinguishedName()),true,true, false);
			if(ListTools.isNotEmpty(groupIds)) {
				List<Group> groupList = business.organization().group().listObject(groupIds);
				for(Group group : groupList) {
					shareIds.addAll(business.share().listWithShareGroup(group.getUnique(), fileType));
				}
			}
			shareIds = ListTools.trim(shareIds, true, true);
			//去除设置屏蔽的共享文件
			List<String> shieldIds = business.share().listWithShieldUser1(effectivePerson.getDistinguishedName());
			shareIds = ListUtils.subtract(shareIds, shieldIds);
			List<Wo> wos = Wo.copier.copy(emc.list(Share.class, shareIds));
			for (Wo o : wos) {
				Attachment2 sourcefile = emc.find(o.getFileId(), Attachment2.class);
				if(sourcefile != null){
					o.setDescription(sourcefile.getDescription());
				}
				o.setContentType(this.contentType(false, o.getName()));
			}
			SortTools.desc(wos, false, Share.createTime_FIELDNAME);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Share {

		private static final long serialVersionUID = -531053101150157872L;

		static final WrapCopier<Share, Wo> copier = WrapCopierFactory.wo(Share.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("文件类型")
		private String contentType;

		@FieldDescribe("文件描述")
		private String description;

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}
}
