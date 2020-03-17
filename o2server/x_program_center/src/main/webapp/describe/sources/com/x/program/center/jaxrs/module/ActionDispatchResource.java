package com.x.program.center.jaxrs.module;

import com.x.base.core.project.Application;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.program.center.ThisApplication;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ActionDispatchResource extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDispatchResource.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, boolean asNew, String fileName, String filePath, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (StringUtils.isEmpty(fileName)) {
			fileName = this.fileName(disposition);
		}
		if(!fileName.toLowerCase().endsWith(".zip") && StringUtils.isEmpty(filePath)){
			throw new Exception("非zip文件的filePath属性不能为空");
		}
		if(StringUtils.isNotEmpty(filePath)){
			if(filePath.startsWith("o2_") || filePath.startsWith("x_")){
				throw new Exception("filePath can not start with 'o2_' or 'x_'!");
			}
		}
		if(bytes==null || bytes.length==0){
			throw new Exception("file must be not empty!");
		}

		Wo wo = new Wo();
		wo.setValueList(dispatch(asNew, fileName, filePath, bytes));
		result.setData(wo);
		return result;
	}

	private List<String> dispatch(boolean asNew, String fileName, String filePath, byte[] bytes) throws Exception{
		List<String> list = new ArrayList<>();
		Nodes nodes = Config.nodes();
		for (String node : nodes.keySet()){
			boolean flag = false;

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
						flag = true;
						list.add(node+":success");
					}else{
						list.add(node + ":failure");
					}
				}

			} catch (Exception ex) {
				list.add(node + ":failure-"+ex.getMessage());
				logger.print("socket dispatch resource to {}:{} error={}", node, nodes.get(node).nodeAgentPort(), ex.getMessage());
			}

			/*if(!flag) {
				Map.Entry<String, CenterServer> centerEntry = Config.nodes().centerServers().findByNode(node);
				if (centerEntry != null) {
					String url = Config.url_x_program_center_jaxrs(centerEntry, "sysresource", "upload", "resource", "as", "new", String.valueOf(asNew));
					Map<String, String> param = new HashMap<>();
					if(StringUtils.isNotEmpty(filePath)){
						param.put("filePath", filePath);
					}
					ActionResponse response = CipherConnectionAction.multiFormPost(false, url, fileName, bytes, param);
					logger.print("dispatch resource to {} result={}", url, XGsonBuilder.toJson(response));
					if (response.getType().equals(ActionResponse.Type.success)) {
						list.add(node + ":success");
						flag = true;
					}
				}
			}

			if(!flag) {
				Application app = ThisApplication.context().applications().findOneAppByNode(node);
				if (app != null) {
					String url = app.getUrlJaxrsRoot() + "sysresource/upload/resource/as/new/" + asNew;
					Map<String, String> param = new HashMap<>();
					if(StringUtils.isNotEmpty(filePath)){
						param.put("filePath", filePath);
					}
					ActionResponse response = CipherConnectionAction.multiFormPost(false, url, fileName, bytes, param);
					logger.print("dispatch to {} result={}", url, XGsonBuilder.toJson(response));
					if (response.getType().equals(ActionResponse.Type.success)) {
						list.add(node + ":success");
					} else {
						list.add(node + ":failure");
					}
				}else{
					list.add(node+":failure");
				}
			}*/
		}
		return list;
	}

	public static class Wo extends WrapStringList {

	}
}
