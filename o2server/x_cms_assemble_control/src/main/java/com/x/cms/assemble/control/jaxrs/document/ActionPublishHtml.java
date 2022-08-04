package com.x.cms.assemble.control.jaxrs.document;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.cms.core.entity.Document;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档发送html文本到webServer下
 * @author sword
 */
public class ActionPublishHtml extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionPublishHtml.class);
	private static final String PUBLISH_PATH = "cms_publish";

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );

		Document document = documentQueryService.get(id);
		if(document == null){
			throw new ExceptionEntityNotExist(id);
		}
		if(StringUtils.isBlank(wi.getHtmlContent())){
			throw new ExceptionFieldEmpty("htmlContent");
		}
		String htmlContent = wi.getHtmlContent();
		try {
			htmlContent =  URLDecoder.decode(htmlContent, StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			logger.warn("docContent URLDecoder error:" + e.getMessage());
		}
		this.dispatch(true, id+".html", PUBLISH_PATH, htmlContent.getBytes(StandardCharsets.UTF_8));

		result.setData(new Wo(true));
		return result;
	}

	private void dispatch(boolean asNew, String fileName, String filePath, byte[] bytes) throws Exception{
		List<String> list = new ArrayList<>();
		Nodes nodes = Config.nodes();
		for (String node : nodes.keySet()){
			try (Socket socket = new Socket(node, nodes.get(node).nodeAgentPort())) {
				socket.setKeepAlive(true);
				socket.setSoTimeout(10000);
				try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
					 DataInputStream dis = new DataInputStream(socket.getInputStream())){
					Map<String, Object> commandObject = new HashMap<>();
					commandObject.put("command", "uploadResource:"+fileName);
					commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));

					Map<String, Object> param = new HashMap<>();
					param.put("fileName", fileName);
					param.put("asNew", asNew);
					if(StringUtils.isNotEmpty(filePath)){
						param.put("filePath", filePath);
					}
					commandObject.put("param", param);
					dos.writeUTF(XGsonBuilder.toJson(commandObject));
					dos.flush();
					dos.writeInt(bytes.length);
					dos.flush();

					try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)){
						byte[] onceBytes = new byte[1024];
						int length = 0;
						while((length = bis.read(onceBytes, 0, onceBytes.length)) != -1) {
							dos.write(onceBytes, 0, length);
							dos.flush();
						}
					}

					String result = dis.readUTF();
					logger.print("socket dispatch resource {} to {}:{} result={}", fileName, node, nodes.get(node).nodeAgentPort(), result);
					if("success".equals(result)){
						list.add(node+":success");
					}else{
						list.add(node + ":failure");
					}
				}
			} catch (Exception ex) {
				list.add(node + ":failure-"+ex.getMessage());
				logger.print("socket dispatch resource to {}:{} error={}", node, nodes.get(node).nodeAgentPort(), ex.getMessage());
			}

		}
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("html内容，发布为o2server/webServer/cms_publish/{id}.html")
		private String htmlContent;

		public String getHtmlContent() {
			return htmlContent;
		}

		public void setHtmlContent(String htmlContent) {
			this.htmlContent = htmlContent;
		}
	}

	public static class Wo extends WrapBoolean {
		public Wo(Boolean value){
			super(value);
		}
	}
}
