package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionCopyToWork extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCopyToWork.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workId, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			if (effectivePerson.isNotManager()) {
				WoWorkControl workControl = business.getControl(effectivePerson, work, WoWorkControl.class);
				if (!workControl.getAllowProcessing()) {
					throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
							work.getId());
				}
			}
			Req req = new Req();
			if (ListTools.isNotEmpty(wi.getAttachmentList())) {
				for (WiAttachment w : wi.getAttachmentList()) {
					Attachment o = emc.find(w.getId(), Attachment.class);
					if (null == o) {
						throw new ExceptionAttachmentNotExist(w.getId());
					}
					if (!business.readableWithJob(effectivePerson, o.getJob())) {
						throw new ExceptionAccessDenied(effectivePerson, o.getJob());
					}
					ReqAttachment qa = new ReqAttachment();
					qa.setId(o.getId());
					qa.setName(w.getName());
					qa.setSite(w.getSite());
					req.getAttachmentList().add(qa);
				}
			}
			if (ListTools.isNotEmpty(req.getAttachmentList())) {
				wos = ThisApplication.context().applications()
						.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
								"attachment/copy/work/" + URLEncoder.encode(work.getId(), DefaultCharset.name), req)
						.getDataAsList(Wo.class);
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("附件对象")
		private List<WiAttachment> attachmentList = new ArrayList<>();

		public List<WiAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WiAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

	}

	public static class ReqAttachment extends GsonPropertyObject {

		private String id;
		private String name;
		private String site;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

	}

	public static class Req extends GsonPropertyObject {

		List<ReqAttachment> attachmentList = new ArrayList<>();

		public List<ReqAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<ReqAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

	}

	public static class WiAttachment extends GsonPropertyObject {

		private String id;
		private String name;
		private String site;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

	}

	public static class Wo extends WoId {

	}

	public static class WoWorkControl extends WorkControl {
	}
}