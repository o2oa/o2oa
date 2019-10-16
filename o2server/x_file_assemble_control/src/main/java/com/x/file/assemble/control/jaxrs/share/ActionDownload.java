package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

class ActionDownload extends BaseAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Attachment2.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String shareId, String fileId, String password) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = null;
			/** 确定是否要用application/octet-stream输出 */
			Share share = emc.find(shareId, Share.class);
			if (null == share) {
				throw new ExceptionAttachmentNotExist(shareId);
			}
			/* 判断当前用户是否有权限访问该文件 */
			if(!effectivePerson.isManager() && !StringUtils.equals(effectivePerson.getDistinguishedName(), share.getPerson())) {
				if (!"password".equals(share.getShareType())) {
					if (!hasPermission(business, effectivePerson, share)) {
						throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
					}
				} else {
					if (StringUtils.isEmpty(password)) {
						throw new Exception("password can not be empty.");
					}
					if (!password.equalsIgnoreCase(share.getPassword())) {
						throw new Exception("invalid password.");
					}
				}
			}
			if("attachment".equals(share.getFileType())) {
				Attachment2 attachment = emc.find(fileId, Attachment2.class);
				if (null == attachment) {
					throw new ExceptionAttachmentNotExist(fileId);
				}
				OriginFile originFile = emc.find(attachment.getOriginFile(), OriginFile.class);
				if (null == originFile) {
					throw new ExceptionAttachmentNotExist(fileId, attachment.getOriginFile());
				}
				String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), fileId);
				Element element = cache.get(cacheKey);
				if ((null != element) && (null != element.getObjectValue())) {
					wo = (Wo) element.getObjectValue();
				} else {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
							originFile.getStorage());
					if (null == mapping) {
						throw new ExceptionStorageNotExist(originFile.getStorage());
					}
					try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
						originFile.readContent(mapping, os);
						byte[] bs = os.toByteArray();
						wo = new Wo(bs, this.contentType(false, attachment.getName()),
								this.contentDisposition(false, attachment.getName()));
						/**
						 * 对10M以下的文件进行缓存
						 */
						if (bs.length < (1024 * 1024 * 10)) {
							cache.put(new Element(cacheKey, wo));
						}
					}
				}
			}else{
				Folder2 folder = emc.find(fileId, Folder2.class);
				if (null == folder) {
					throw new ExceptionFolderNotExist(fileId);
				}
				String zipName = folder.getName() + ".zip";
				List<Folder2> folderList = new ArrayList<>();
				folderList.add(folder);
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					this.fileCommonService.downToZip(emc, null, folderList, os);
					byte[] bs = os.toByteArray();
					wo = new Wo(bs, this.contentType(false,zipName),
							this.contentDisposition(false, zipName));
					result.setData(wo);
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
}