package com.x.cms.assemble.control.jaxrs.fileinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger(ExcuteDelete.class);

	protected ActionResult<WrapOutId> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		FileInfo fileInfo = null;
		Document document = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			// 先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			fileInfo = business.getFileInfoFactory().get(id);
			if (null == fileInfo) {
				throw new Exception("fileInfo{id:" + id + "} 文件信息不存在，无法继续删除.");
			}
			logger.debug("[delete]附件 fileinfo{'id':'" + id + "'}存在.");
			// 判断文档信息是否存在
			document = business.getDocumentFactory().get(fileInfo.getDocumentId());
			if (null == document) {
				throw new Exception("document{id:" + fileInfo.getDocumentId() + "} 文档信息不存在，无法继续删除.");
			}
			logger.debug("[delete]附件所属的文档 document{'id':'" + fileInfo.getDocumentId() + "'}存在.");
			// 如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.fileInfoEditAvailable(request, effectivePerson)) {
				throw new Exception("fileInfo{name:" + effectivePerson.getName() + "} ，用户没有内容管理应用信息操作的权限！");
			}
			// 删除文件，并且删除记录及文档的关联信息
			StorageMapping mapping = ThisApplication.storageMappings.get(FileInfo.class, fileInfo.getStorage());

			// 从FTP上删除文件
			fileInfo.deleteContent(mapping);
			emc.beginTransaction(FileInfo.class);
			emc.beginTransaction(Document.class);
			if (document != null && document.getAttachmentList() != null) {
				document.getAttachmentList().remove(fileInfo.getId());
			}
			emc.remove(fileInfo, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(FileInfo.class);
			ApplicationCache.notify(Document.class);
			// 成功删除一个附件信息
			logService.log(emc, effectivePerson.getName(), fileInfo.getName(), fileInfo.getAppId(), fileInfo.getId(),
					fileInfo.getDocumentId(), fileInfo.getId(), "FILE", "删除");

			wrap = new WrapOutId(fileInfo.getId());
			result.setData(wrap);

		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		return result;
	}

}