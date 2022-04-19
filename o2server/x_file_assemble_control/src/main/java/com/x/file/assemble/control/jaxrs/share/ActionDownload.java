package com.x.file.assemble.control.jaxrs.share;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;

import javax.ws.rs.core.StreamingOutput;

class ActionDownload extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDownload.class);

	private static final int CACHE_SIZE = 1024 * 1024 * 10;

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
				if (!Share.SHARE_TYPE_PASSWORD.equals(share.getShareType())) {
					if (!hasPermission(business, effectivePerson, share)) {
						throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
					}
				} else {
					if (StringUtils.isEmpty(password)) {
						throw new ExceptionFieldEmpty(Share.password_FIELDNAME);
					}
					if (!password.equalsIgnoreCase(share.getPassword())) {
						throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
					}
				}
			}
			Attachment2 attachment = emc.find(fileId, Attachment2.class);
			Folder2 folder = emc.find(fileId, Folder2.class);
			if(attachment == null && folder==null){
				throw new ExceptionAttachmentNotExist(shareId, fileId);
			}if(attachment!=null){
				if(!attachment.getPerson().equals(share.getPerson())){
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
				OriginFile originFile = emc.find(attachment.getOriginFile(), OriginFile.class);
				if (null == originFile) {
					throw new ExceptionAttachmentNotExist(shareId, fileId);
				}
				CacheCategory cacheCategory = new CacheCategory(Attachment2.class);
				CacheKey cacheKey = new CacheKey(this.getClass(), fileId);
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
				if (optional.isPresent()) {
					wo = (Wo) optional.get();
				} else {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
							originFile.getStorage());
					if (null == mapping) {
						throw new ExceptionStorageNotExist(originFile.getStorage());
					}

					StreamingOutput streamingOutput = output -> {
						try {
							originFile.readContent(mapping, output);
							output.flush();
						} catch (Exception e) {
							logger.warn("{}附件下载异常：{}", attachment.getName(), e.getMessage());
						}
					};
					String fastETag = attachment.getId()+attachment.getUpdateTime().getTime();
					wo = new Wo(streamingOutput, this.contentType(false, attachment.getName()),
							this.contentDisposition(false, attachment.getName()),
							originFile.getLength(), fastETag);
					if (originFile.getLength() < CACHE_SIZE) {
						CacheManager.put(cacheCategory, cacheKey, wo);
					}
				}
			}else{
				if(!folder.getPerson().equals(share.getPerson())){
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
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

		public Wo(StreamingOutput streamingOutput, String contentType, String contentDisposition, Long contentLength, String fastETag) {
			super(streamingOutput, contentType, contentDisposition, contentLength, fastETag);
		}

	}
}
