package com.x.processplatform.assemble.surface.servlet.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

@Deprecated
@WebServlet(urlPatterns = "/servlet/attachment/upload/work/*")
@MultipartConfig
public class UploadServlet extends AbstractServletAction {

	private static Logger logger = LoggerFactory.getLogger(UploadServlet.class);

	private static final long serialVersionUID = 5628571943877405247L;

	@HttpMethodDescribe(value = "创建Attachment对象.", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<List<WrapOutId>> result = new ActionResult<>();
		List<WrapOutId> wraps = new ArrayList<>();
		EffectivePerson effectivePerson = null;
		try {
			this.setCharacterEncoding(request, response);
			if (!this.isMultipartContent(request)) {
				throw new Exception("not mulit part request.");
			}
			effectivePerson = this.effectivePerson(request);
			String workId = this.getURIPart(request.getRequestURI(), "work");
			Work work = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				/* 后面要重新保存 */
				work = emc.find(workId, Work.class, ExceptionWhen.not_found);
				/* 统计待办数量判断用户是否可以上传附件 */
				Control control = business.getControlOfWorkComplex(effectivePerson, work);
				if (BooleanUtils.isNotTrue(control.getAllowProcessing())) {
					throw new Exception("person{name:" + effectivePerson.getName() + "} access work{id:" + workId
							+ "} was denied.");
				}
			}
			/* 附件分类信息 */
			String site = null;
			FileItemIterator fileItemIterator = this.getItemIterator(request);
			List<Attachment> attachments = new ArrayList<>();
			// List<AttachmentLog> attachmentLogs = new ArrayList<>();
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				String name = item.getFieldName();
				try (InputStream input = item.openStream()) {
					if (item.isFormField()) {
						String str = Streams.asString(input);
						if (StringUtils.equals(name, "site")) {
							site = str;
						}
					} else {
						StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
						Attachment attachment = this.concreteAttachment(work, effectivePerson, site);
						/** 禁止不带扩展名的文件上传 */
						if (StringUtils.isEmpty(FilenameUtils.getExtension(item.getName()))) {
							throw new EmptyExtensionException(item.getName());
						}
						attachment.saveContent(mapping, input, FilenameUtils.getName(item.getName()));
						attachments.add(attachment);
						// AttachmentLog attachmentLog =
						// this.concreteAttachmentLog(attachment);
						// attachmentLog.setAttachmentLogType(AttachmentLogType.create);
						// attachmentLogs.add(attachmentLog);
					}
				}
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.beginTransaction(Work.class);
				emc.beginTransaction(Attachment.class);
				// emc.beginTransaction(AttachmentLog.class);
				work = emc.find(workId, Work.class);
				for (Attachment o : attachments) {
					o.setSite(site);
					emc.persist(o, CheckPersistType.all);
					work.getAttachmentList().add(o.getId());
					wraps.add(new WrapOutId(o.getId()));
				}
				// for (AttachmentLog o : attachmentLogs) {
				// emc.persist(o, CheckPersistType.all);
				// }
				emc.commit();
				ApplicationCache.notify(Attachment.class);
			}
			result.setData(wraps);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		this.result(response, result);
	}

	private Attachment concreteAttachment(Work work, EffectivePerson effectivePerson, String site) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setCompleted(false);
		attachment.setPerson(effectivePerson.getName());
		attachment.setLastUpdatePerson(effectivePerson.getName());
		attachment.setSite(site);
		/** 用于判断目录的值 */
		attachment.setWorkCreateTime(work.getCreateTime());
		attachment.setApplication(work.getApplication());
		attachment.setProcess(work.getProcess());
		attachment.setJob(work.getJob());
		attachment.setActivity(work.getActivity());
		attachment.setActivityName(work.getActivityName());
		attachment.setActivityToken(work.getActivityToken());
		attachment.setActivityType(work.getActivityType());
		return attachment;
	}

}