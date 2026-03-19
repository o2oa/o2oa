package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.tools.StringTools;
import com.x.file.core.entity.open.OriginFile;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.assemble.control.util.LibreOfficeUtil;
import com.x.pan.core.entity.Attachment3;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Optional;

class ActionOfficePreview extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger( ActionOfficePreview.class );

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = null;
			/** 确定是否要用application/octet-stream输出 */
			Attachment3 attachment = emc.find(id, Attachment3.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			if(!business.zoneViewable(effectivePerson, attachment.getZoneId())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			if(!business.getSystemConfig().hasEnableOfficeReview()){
				throw new ExceptionOfficeHomeNotConfig();
			}
			OriginFile originFile = emc.find(attachment.getOriginFile(), OriginFile.class);
			if (null == originFile) {
				throw new ExceptionAttachmentNotExist(id,attachment.getOriginFile());
			}
			CacheCategory cacheCategory = new CacheCategory(Attachment3.class);
			CacheKey cacheKey = new CacheKey(this.getClass(), id);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
			} else {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
						originFile.getStorage());
				if (null == mapping) {
					throw new ExceptionStorageNotExist(originFile.getStorage());
				}
				File dirFile = new File(Config.dir_local_temp(), Business.TEMP_FOLD);
				FileTools.forceMkdir(dirFile);
				File docFile = new File(StringTools.uniqueToken() +"."+attachment.getExtension());
				try (OutputStream out = new FileOutputStream(docFile)){
					originFile.readContent(mapping, out);
				}
				byte[] bytesOfPdf = null;
				try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					boolean res = LibreOfficeUtil.convertOffice2PDFSync(docFile, out);
					if(res) {
						bytesOfPdf = out.toByteArray();
					}
					FileUtils.forceDelete(docFile);
				}
				String fastETag = attachment.getId()+attachment.getUpdateTime().getTime()+".pdf";
				if(bytesOfPdf!=null){
					wo = new Wo(bytesOfPdf, this.contentType(false, attachment.getName()+".pdf"),
							this.contentDisposition(false, attachment.getName()+".pdf"), fastETag);
					/**
					 * 对10M以下的文件进行缓存
					 */
					if (bytesOfPdf.length < (1024 * 1024 * 10)) {
						CacheManager.put(cacheCategory, cacheKey, wo);
					}
				}else{
					throw new ExceptionTransferError();
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition, String fastETag) {
			super(bytes, contentType, contentDisposition, fastETag);
		}

	}
}
