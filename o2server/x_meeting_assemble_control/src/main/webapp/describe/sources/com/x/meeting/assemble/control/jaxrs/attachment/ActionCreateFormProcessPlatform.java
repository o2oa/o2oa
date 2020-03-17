package com.x.meeting.assemble.control.jaxrs.attachment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;

public class ActionCreateFormProcessPlatform extends BaseAction {

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Meeting meeting = emc.find(wi.getMeeting(), Meeting.class);
			if (null == meeting) {
				throw new ExceptionMeetingNotExist(wi.getMeeting());
			}
			business.meetingReadAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);

			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			for (String workAttachment : wi.getAttachmentList()) {
				String fileName = readAttachmentName(wi.getWork(), workAttachment);
				emc.beginTransaction(Attachment.class);
				byte[] bytes = readAttachmentContent(wi.getWork(), workAttachment);
				Attachment attachment = this.concreteAttachment(meeting, false);
				attachment.saveContent(mapping, bytes, fileName);
				attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
				attachment.setLastUpdateTime(new Date());
				emc.persist(attachment, CheckPersistType.all);
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private String readAttachmentName(String workId, String workAttachmentId) throws Exception {
		RespAttachmentName resp = ThisApplication.context().applications()
				.getQuery(x_processplatform_assemble_surface.class,
						Applications.joinQueryUri("attachment", workAttachmentId, "work", workId))
				.getData(RespAttachmentName.class);
		return resp.getName();
	}

	public static class RespAttachmentName {

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	private byte[] readAttachmentContent(String workId, String workAttachmentId) throws Exception {
		Application app = ThisApplication.context().applications()
				.randomWithWeight(x_processplatform_assemble_surface.class.getName());
		String address = app.getUrlJaxrsRoot() + "attachment/download/" + workAttachmentId + "/work/" + workId;
		HttpURLConnection connection = HttpConnection.prepare(address, CipherConnectionAction.cipher());
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		byte[] bytes = null;
		connection.connect();
		try (InputStream input = connection.getInputStream()) {
			bytes = IOUtils.toByteArray(input);
		}
		connection.disconnect();
		return bytes;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("会议标识")
		private String meeting;
		@FieldDescribe("工作标识")
		private String work;
		@FieldDescribe("附件标识")
		private List<String> attachmentList;

		public String getMeeting() {
			return meeting;
		}

		public void setMeeting(String meeting) {
			this.meeting = meeting;
		}

		public String getWork() {
			return work;
		}

		public void setWork(String work) {
			this.work = work;
		}

		public List<String> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<String> attachmentList) {
			this.attachmentList = attachmentList;
		}

	}

	public static class Wo extends WrapBoolean {

	}

}