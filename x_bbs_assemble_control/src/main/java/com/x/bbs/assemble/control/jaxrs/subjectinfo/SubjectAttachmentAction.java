package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.math.NumberUtils;
import org.imgscalr.Scalr;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.WrapOutSectionInfo;
import com.x.bbs.assemble.control.service.BBSSubjectInfoServiceAdv;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;

@Path("subjectattach")
public class SubjectAttachmentAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger(SubjectAttachmentAction.class);
	private BBSSubjectInfoServiceAdv subjectInfoServiceAdv = new BBSSubjectInfoServiceAdv();
	private BeanCopyTools<BBSSubjectAttachment, WrapOutSubjectAttachment> wrapout_copier = BeanCopyToolsBuilder.create(
			BBSSubjectAttachment.class, WrapOutSubjectAttachment.class, null, WrapOutSubjectAttachment.Excludes);

	@HttpMethodDescribe(value = "根据指定ID获取附件信息.", response = WrapOutSectionInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutSubjectAttachment> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutSubjectAttachment wrap = null;
		BBSSubjectAttachment attachmentInfo = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new AttachmentIdEmptyException();
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}
		}

		if (check) {
			try {
				attachmentInfo = subjectInfoServiceAdv.getAttachment(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttachmentQueryByIdException(e, id);
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}
		}

		if (check) {
			if (attachmentInfo != null) {
				try {
					wrap = wrapout_copier.copy(attachmentInfo);
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					Exception exception = new AttachmentWrapOutException(e);
					result.error(exception);
					logger.error(exception, currentPerson, request, null);
				}
			} else {
				Exception exception = new AttachmentNotExistsException(id);
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}
		}

		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除BBSSubjectAttachment数据对象.", response = WrapOutSubjectAttachment.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutSubjectAttachment> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectAttachment subjectAttachment = null;
		BBSSubjectInfo subjectInfo = null;
		StorageMapping mapping = null;
		boolean check = true;
		if (id == null || id.isEmpty()) {
			check = false;
			Exception exception = new AttachmentIdEmptyException();
			result.error(exception);
			logger.error(exception, currentPerson, request, null);
		}
		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				subjectAttachment = emc.find(id, BBSSubjectAttachment.class);
				if (null == subjectAttachment) {
					check = false;
					Exception exception = new AttachmentNotExistsException(id);
					result.error(exception);
					logger.error(exception, currentPerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttachmentQueryByIdException(e, id);
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}
		}
		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				subjectInfo = emc.find(subjectAttachment.getSubjectId(), BBSSubjectInfo.class);
				if (null == subjectInfo) {
					logger.warn("subjectInfo{id:" + subjectAttachment.getSubjectId()
							+ "} is not exists, anyone can delete the attachments.");
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectQueryByIdException(e, subjectAttachment.getSubjectId());
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (subjectAttachment != null) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					mapping = ThisApplication.storageMappings.get(BBSSubjectAttachment.class,
							subjectAttachment.getStorage());
					// 对文件进行删除
					subjectAttachment.deleteContent(mapping);
					// 对数据库记录进行删除
					subjectAttachment = emc.find(id, BBSSubjectAttachment.class);
					subjectInfo = emc.find(subjectAttachment.getSubjectId(), BBSSubjectInfo.class);
					emc.beginTransaction(BBSSubjectAttachment.class);
					emc.beginTransaction(BBSSubjectInfo.class);
					if (subjectInfo != null && subjectInfo.getAttachmentList() != null) {
						subjectInfo.getAttachmentList().remove(subjectAttachment.getId());
						emc.check(subjectInfo, CheckPersistType.all);
					}
					emc.remove(subjectAttachment, CheckRemoveType.all);
					emc.commit();
				} catch (Exception e) {
					check = false;
					Exception exception = new AttachmentDeleteException(e, subjectAttachment.getSubjectId());
					result.error(exception);
					logger.error(exception, currentPerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据主题ID获取BBSSubjectAttachment列表.", response = WrapOutSubjectAttachment.class)
	@GET
	@Path("list/subject/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByWorkId(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<List<WrapOutSubjectAttachment>> result = new ActionResult<List<WrapOutSubjectAttachment>>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutSubjectAttachment> wrapOutSubjectAttachmentList = null;
		List<BBSSubjectAttachment> fileInfoList = null;
		BBSSubjectInfo subjectInfo = null;
		if (id == null || id.isEmpty()) {
			Exception exception = new SubjectIdEmptyException();
			result.error(exception);
			logger.error(exception, currentPerson, request, null);
		} else {
			try {
				subjectInfo = subjectInfoServiceAdv.get(id);
				if (subjectInfo != null) {
					if (subjectInfo.getAttachmentList() != null && subjectInfo.getAttachmentList().size() > 0) {
						fileInfoList = subjectInfoServiceAdv.listAttachmentByIds(subjectInfo.getAttachmentList());
					} else {
						fileInfoList = new ArrayList<BBSSubjectAttachment>();
					}
					wrapOutSubjectAttachmentList = wrapout_copier.copy(fileInfoList);
				} else {
					Exception exception = new SubjectNotExistsException(id);
					result.error(exception);
					logger.error(exception, currentPerson, request, null);
				}
				if (wrapOutSubjectAttachmentList == null) {
					wrapOutSubjectAttachmentList = new ArrayList<WrapOutSubjectAttachment>();
				}
				result.setData(wrapOutSubjectAttachmentList);
			} catch (Throwable th) {
				Exception exception = new SubjectQueryByIdException(th, id);
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "将图片附件转为base64编码.", response = WrapOutString.class)
	@GET
	@Path("{id}/binary/base64/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response imageToBase64(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("size") String size) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutString wrap = null;
		BBSSubjectAttachment fileInfo = null;
		Integer sizeNum = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new AttachmentIdEmptyException();
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (size != null && !size.isEmpty()) {
				if (NumberUtils.isNumber(size)) {
					sizeNum = Integer.parseInt(size);
				} else {
					check = false;
					Exception exception = new SizeFormatException(size);
					result.error(exception);
					logger.error(exception, currentPerson, request, null);
				}
			} else {
				sizeNum = 800;
			}
		}
		if (check) {
			try {
				fileInfo = subjectInfoServiceAdv.getAttachment(id);
				if (fileInfo == null) {
					check = false;
					Exception exception = new AttachmentNotExistsException(id);
					result.error(exception);
					logger.error(exception, currentPerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttachmentQueryByIdException(e, id);
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}

		}
		if (check) {
			if (!isImage(fileInfo)) {
				check = false;
				Exception exception = new AttachmentNotImageException(id);
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}
		}
		BufferedImage image = null;
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output_for_ftp = new ByteArrayOutputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		if (check) {
			try {
				StorageMapping mapping = ThisApplication.storageMappings.get(BBSSubjectAttachment.class,
						fileInfo.getStorage());
				fileInfo.readContent(mapping, output_for_ftp);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttachmentContentReadException(e, id);
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}
		}
		if (check) {
			input = new ByteArrayInputStream(output_for_ftp.toByteArray());
			if (input != null) {
				try {
					image = ImageIO.read(input);
				} catch (IOException e) {
					check = false;
					Exception exception = new AttachmentContentReadException(e, id);
					result.error(exception);
					logger.error(exception, currentPerson, request, null);
				}
			}
		}
		if (check) {
			try {
				int width = image.getWidth();
				int height = image.getHeight();
				if (sizeNum > 0) {
					if (width * height > sizeNum * sizeNum) {
						image = Scalr.resize(image, sizeNum);
					}
				}
				ImageIO.write(image, "png", output);
				wrap = new WrapOutString();
				wrap.setValue(Base64.encodeBase64String(output.toByteArray()));
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ImageEncodeBase64Exception(e, id);
				result.error(exception);
				logger.error(exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private boolean isImage(BBSSubjectAttachment fileInfo) {
		if (fileInfo == null || fileInfo.getExtension() == null || fileInfo.getExtension().isEmpty()) {
			return false;
		}
		if ("jpg".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("png".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("jpeg".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("tiff".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("gif".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("bmp".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		}
		return false;
	}
}