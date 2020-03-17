package com.x.file.assemble.control.jaxrs.share;

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
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Share;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

class ActionListShareToMe extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListShareToMe.class);
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String fileType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if (EMPTY_SYMBOL.equals(fileType)) {
				fileType = null;
			}
			List<String> shareIds = business.share().listWithShareUser1(effectivePerson.getDistinguishedName(), fileType);
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			for (String str : identities) {
				List<String> units = business.organization().unit().listWithIdentitySupNested(str);
				for(String unitName : units){
					Unit unit = business.organization().unit().getObject( unitName );
					if(unit!=null){
						List<String> ids = business.share().listWithShareOrg1(unit.getUnique(), fileType);
						logger.debug("{}根据组织{}查询分享结果：{}",effectivePerson.getDistinguishedName(),unit.getUnique(),ids+""+ids.size());
						shareIds = ListTools.add(shareIds,true,true,ids);
					}
				}
			}
			//去除设置屏蔽的共享文件
			List<String> shieldIds = business.share().listWithShieldUser1(effectivePerson.getDistinguishedName());
			shareIds = ListUtils.subtract(shareIds, shieldIds);
			List<Wo> wos = Wo.copier.copy(emc.list(Share.class, shareIds));
			for (Wo o : wos) {
				o.setContentType(this.contentType(false, o.getName()));
			}
			SortTools.desc(wos, false, "createTime");
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Share {

		private static final long serialVersionUID = -531053101150157872L;

		static WrapCopier<Share, Wo> copier = WrapCopierFactory.wo(Share.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("文件类型")
		private String contentType;

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

	}
}