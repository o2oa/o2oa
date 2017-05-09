package com.x.file.assemble.control.jaxrs.complex;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.utils.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.wrapout.WrapOutAttachment;
import com.x.file.assemble.control.wrapout.WrapOutFolder;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Folder;

@Path("complex")
public class ComplexAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "获取指定个人的顶层文件.", response = WrapOutComplex.class)
	@GET
	@Path("top")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTop(@Context HttpServletRequest request) {
		ActionResult<WrapOutComplex> result = new ActionResult<>();
		WrapOutComplex wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			List<WrapOutAttachment> wrapOutAttachments = new ArrayList<>();
			List<WrapOutFolder> wrapOutFolders = new ArrayList<>();
			BeanCopyTools<Attachment, WrapOutAttachment> attachmentCopier = BeanCopyToolsBuilder
					.create(Attachment.class, WrapOutAttachment.class, null, WrapOutAttachment.Excludes);
			for (Attachment o : emc.list(Attachment.class,
					business.attachment().listTopWithPerson(effectivePerson.getName()))) {
				WrapOutAttachment wrapOutAttachment = new WrapOutAttachment();
				attachmentCopier.copy(o, wrapOutAttachment);
				wrapOutAttachments.add(wrapOutAttachment);
			}
			BeanCopyTools<Folder, WrapOutFolder> folderCopier = BeanCopyToolsBuilder.create(Folder.class,
					WrapOutFolder.class, null, WrapOutFolder.Excludes);
			for (Folder o : emc.list(Folder.class, business.folder().listTopWithPerson(effectivePerson.getName()))) {
				WrapOutFolder wrapOutFolder = new WrapOutFolder();
				folderCopier.copy(o, wrapOutFolder);
				wrapOutFolders.add(wrapOutFolder);
			}
			SortTools.asc(wrapOutAttachments, false, "name");
			SortTools.asc(wrapOutFolders, false, "name");
			wrap = new WrapOutComplex();
			wrap.setAttachmentList(wrapOutAttachments);
			wrap.setFolderList(wrapOutFolders);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取个人在指定分类下的文件.", response = WrapOutComplex.class)
	@GET
	@Path("folder/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithFolderSubDirect(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutComplex> result = new ActionResult<>();
		WrapOutComplex wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Folder folder = emc.fetchAttribute(id, Folder.class, "person");
			if (null == folder) {
				throw new Exception("folder{id:" + id + "} not existed.");
			}
			if (!StringUtils.equals(folder.getPerson(), effectivePerson.getName())) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} access folder{id:" + folder.getId()
						+ "} was denied.");
			}
			Business business = new Business(emc);
			List<WrapOutAttachment> wrapOutAttachments = new ArrayList<>();
			List<WrapOutFolder> wrapOutFolders = new ArrayList<>();
			BeanCopyTools<Attachment, WrapOutAttachment> attachmentCopier = BeanCopyToolsBuilder
					.create(Attachment.class, WrapOutAttachment.class, null, WrapOutAttachment.Excludes);
			for (Attachment o : emc.list(Attachment.class, business.attachment().listWithFolder(folder.getId()))) {
				WrapOutAttachment wrapOutAttachment = new WrapOutAttachment();
				attachmentCopier.copy(o, wrapOutAttachment);
				wrapOutAttachments.add(wrapOutAttachment);
			}
			BeanCopyTools<Folder, WrapOutFolder> folderCopier = BeanCopyToolsBuilder.create(Folder.class,
					WrapOutFolder.class, null, WrapOutFolder.Excludes);
			for (Folder o : emc.list(Folder.class,
					business.folder().listWithPersonWithSuperior(effectivePerson.getName(), folder.getId()))) {
				WrapOutFolder wrapOutFolder = new WrapOutFolder();
				folderCopier.copy(o, wrapOutFolder);
				wrapOutFolders.add(wrapOutFolder);
			}
			SortTools.asc(wrapOutAttachments, false, "name");
			SortTools.asc(wrapOutFolders, false, "name");
			wrap = new WrapOutComplex();
			wrap.setAttachmentList(wrapOutAttachments);
			wrap.setFolderList(wrapOutFolders);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}