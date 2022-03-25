package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionUnsupportedMediaType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DocumentTools;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.FileInfo;


class ActionPreviewPdf extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPreviewPdf.class);

	private final static List<String> keys = Arrays.asList("doc", "docx", "ppt", "pptx", "xls", "xlsx");

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String fileName) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Cache.CacheCategory cacheCategory = new Cache.CacheCategory(FileInfo.class);
		Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), id, fileName);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			Wo wo = (Wo) optional.get();
			result.setData(wo);
		} else {
			FileInfo attachment = fileInfoServiceAdv.get(id);
			if (null == attachment) {
				throw new Exception("附件不存在。id:" + id);
			} else {
				String type = FilenameUtils.getExtension(attachment.getName());
				if (!keys.contains(type.toLowerCase())) {
					throw new ExceptionUnsupportedMediaType(type);
				}
				StorageMapping mapping = ThisApplication.context().storageMappings().get(FileInfo.class, attachment.getStorage());
				byte[] bytes = DocumentTools.toPdf(attachment.getName(), attachment.readContent(mapping), "");
				if (StringUtils.isBlank(fileName)) {
					fileName = FilenameUtils.getBaseName(attachment.getName()) + ".pdf";
				} else{
					fileName = fileName + ".pdf";
				}
				Wo wo = new Wo(bytes,
						this.contentType(false, fileName),
						this.contentDisposition(false, fileName));
				/**
				 * 对10M以下的文件进行缓存
				 */
				if (bytes.length < (1024 * 1024 * 10)) {
					CacheManager.put(cacheCategory, cacheKey, wo);
				}
				result.setData(wo);
			}
		}
		return result;
	}

	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

	public static void main(String[] args) {
		System.out.println(keys.equals("doc"));
	}


}
