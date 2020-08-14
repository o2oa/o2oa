package com.x.program.center.jaxrs.market;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.enums.CommonStatus;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.core.entity.Application;
import com.x.program.center.core.entity.Attachment;
import com.x.program.center.core.entity.InstallLog;

import java.util.ArrayList;
import java.util.List;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Application app = emc.find(id, Application.class);
			if (null == app) {
				throw new ExceptionEntityNotExist(id, Application.class);
			}
			Wo wo = Wo.copier.copy(app);
			wo.setAttList(emc.listEqual(Attachment.class, Attachment.application_FIELDNAME, wo.getId()));

			InstallLog installLog = emc.find(id, InstallLog.class);
			if(installLog!=null && CommonStatus.VALID.getValue().equals(installLog.getStatus())){
				wo.setInstalledVersion(installLog.getVersion());
			}else{
				wo.setInstalledVersion("");
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Application {

		private static final long serialVersionUID = -4000191514240350631L;
		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null, Wo.FieldsInvisible);

		@FieldDescribe("图片列表")
		private List<Attachment> attList = new ArrayList<>();

		@FieldDescribe("已安装的版本，空表示未安装")
		private String installedVersion;

		public List<Attachment> getAttList() {
			return attList;
		}

		public void setAttList(List<Attachment> attList) {
			this.attList = attList;
		}

		public String getInstalledVersion() {
			return installedVersion;
		}

		public void setInstalledVersion(String installedVersion) {
			this.installedVersion = installedVersion;
		}
	}
}