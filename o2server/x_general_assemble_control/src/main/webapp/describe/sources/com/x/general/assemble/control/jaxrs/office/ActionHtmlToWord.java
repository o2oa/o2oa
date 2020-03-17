package com.x.general.assemble.control.jaxrs.office;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.StringTools;

import net.sf.ehcache.Element;

class ActionHtmlToWord extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		byte[] bytes = this.local(wi);
		HtmlToWordResultObject resultObject = new HtmlToWordResultObject();

		resultObject.setBytes(bytes);
		resultObject.setName(wi.getFileName());
		resultObject.setPerson(effectivePerson.getDistinguishedName());

		String flag = StringTools.uniqueToken();
		cache.put(new Element(flag, resultObject));
		Wo wo = new Wo();
		wo.setId(flag);
		result.setData(wo);
		return result;
	}

	private byte[] local(Wi wi) throws Exception {
		String content = "<html><head></head><body>" + wi.getContent() + "</body></html>";
		try (POIFSFileSystem fs = new POIFSFileSystem();
				InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			fs.createDocument(is, "WordDocument");
			fs.writeFilesystem(out);
			return out.toByteArray();
		}
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("转换文件名.")
		private String fileName;
		@FieldDescribe("内容.")
		private String content;

		public String getFileName() throws Exception {
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