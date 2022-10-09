package com.x.general.assemble.control.jaxrs.office;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionHtmlToWord extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionHtmlToWord.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		byte[] bytes = this.local(wi);
		HtmlToWordResultObject resultObject = new HtmlToWordResultObject();

		resultObject.setBytes(bytes);
		resultObject.setName(wi.getFileName());
		resultObject.setPerson(effectivePerson.getDistinguishedName());

		String flag = StringTools.uniqueToken();
		CacheKey cacheKey = new CacheKey(flag);
		CacheManager.put(cacheCategory, cacheKey, resultObject);
		Wo wo = new Wo();
		wo.setId(flag);
		result.setData(wo);
		return result;
	}

	private byte[] local(Wi wi) throws IOException {
		String content = "<html><head></head><body>" + wi.getContent() + "</body></html>";
		try (POIFSFileSystem fs = new POIFSFileSystem();
				InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			fs.createDocument(is, "WordDocument");
			fs.writeFilesystem(out);
			return out.toByteArray();
		}
	}

	@Schema(name = "com.x.general.assemble.control.jaxrs.office.ActionHtmlToWord$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 2125735475191163975L;
	}

	@Schema(name = "com.x.general.assemble.control.jaxrs.office.ActionHtmlToWord$Wi")
	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -5654491825938383970L;

		@FieldDescribe("转换文件名.")
		@Schema(description = "转换文件名.")
		private String fileName;

		@FieldDescribe("内容.")
		@Schema(description = "内容.")
		private String content;

		public String getFileName() {
			return StringUtils.isEmpty(fileName) ? StringTools.uniqueToken() + ".doc" : fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

}