package com.x.processplatform.assemble.surface.servlet.attachment;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

@WebServlet(urlPatterns = "/servlet/attachment/download/*")
public class DownloadServlet extends BaseServlet {

	private static Logger logger = LoggerFactory.getLogger(DownloadServlet.class);
	private static final long serialVersionUID = -4314532091497625540L;

	@HttpMethodDescribe(value = "下载附件 servlet/download/{id}/work/{workId}/stream , servlet/download/{id}/workcompleted/(workcompletedId}/stream 流文件 servlet/download/{id}/work/{workId} servlet/download/{id}/workcompleted/{workcompletedId} 输出contentType", response = WrapOutAttachment.class)
	// servlet/download/{id}/work/workId/stream
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		EffectivePerson effectivePerson = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			effectivePerson = this.effectivePerson(request);
			request.setCharacterEncoding("UTF-8");
			String part = this.getURIPart(request.getRequestURI(), "download");
			String id = StringUtils.substringBefore(part, "/");
			part = StringUtils.substringAfter(part, "/");
			String type = StringUtils.substringBefore(part, "/");
			part = StringUtils.substringAfter(part, "/");
			String refercenceId = StringUtils.substringBefore(part, "/");
			/* 确定是否要用application/octet-stream输出 */
			boolean streamContentType = StringUtils.endsWith(part, "/stream");
			Business business = new Business(emc);
			if (StringUtils.equals(type, "work")) {
				/* 后面要取AttachmentList，inheritedAttachmentList 必须整个取出 */
				Work work = emc.find(refercenceId, Work.class, ExceptionWhen.not_found);
				Control control = business.getControlOfWorkComplex(effectivePerson, work);
				if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
					throw new Exception("person access work{id:" + refercenceId + "} was deined.");
				}
				if (!work.getAttachmentList().contains(id)) {
					throw new Exception("work{id:" + refercenceId + "} not contains attachment{id:" + id + "}.");
				}
			} else if (StringUtils.equals(type, "workcompleted")) {
				/* 后面要取AttachmentList，必须整个取出 */
				WorkCompleted workCompleted = emc.find(refercenceId, WorkCompleted.class, ExceptionWhen.not_found);
				if (!workCompleted.getAttachmentList().contains(id)) {
					throw new Exception(
							"workCompleted{id:" + refercenceId + "} not contains attachment{id:" + id + "}.");
				}
				Control control = business.getControlOfWorkCompleted(effectivePerson, workCompleted);
				if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
					throw new Exception("person access workCompleted{id:" + refercenceId + "} was deined.");
				}
			} else {
				throw new Exception("unknown url:" + part + ".");
			}
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			StorageMapping mapping = ThisApplication.storageMappings.get(Attachment.class, attachment.getStorage());
			this.setResponseHeader(response, attachment, streamContentType);
			attachment.readContent(mapping, response.getOutputStream());
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}

}