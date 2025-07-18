package com.x.program.center.jaxrs.deploy;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.core.entity.DeployLog;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

	protected String saveLog(Wi wi, EffectivePerson effectivePerson) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			DeployLog log = Wi.copier.copy(wi);
			log.setInstallPerson(effectivePerson.getDistinguishedName());
			log.setInstallTime(new Date());
			if(StringUtils.isBlank(log.getVersion())){
				log.setVersion(Config.version());
			}
			emc.beginTransaction(DeployLog.class);
			emc.persist(log);
			emc.commit();
			return log.getId();
		}
	}

	protected String executeCommand(String ctl, String nodeName, int nodePort, InputStream inputStream,
			String fileName)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, URISyntaxException {
		try (Socket socket = new Socket(nodeName, nodePort);
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				DataInputStream dis = new DataInputStream(socket.getInputStream())) {
			socket.setKeepAlive(true);
			socket.setSoTimeout(30000);
			Map<String, Object> commandObject = new HashMap<>();
			commandObject.put("command", "redeploy:" + ctl);
			commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
			dos.writeUTF(XGsonBuilder.toJson(commandObject));
			dos.flush();
			dos.writeUTF(fileName);
			dos.flush();
			byte[] bytes = new byte[1024];
			int length;
			while ((length = inputStream.read(bytes, 0, bytes.length)) != -1) {
				dos.write(bytes, 0, length);
				dos.flush();
			}
			socket.shutdownOutput();
			return dis.readUTF();
		}
	}

	public static class Wi extends GsonPropertyObject {
		static WrapCopier<Wi, DeployLog> copier = WrapCopierFactory.wi(Wi.class, DeployLog.class, null,
				ListTools.toList("asNew", "filePath"));
		private boolean asNew;
		private String filePath;
		private String name;
		private String title;
		private String version;
		private String remark;
		private String type;

		public boolean getAsNew() {
			return asNew;
		}

		public void setAsNew(boolean asNew) {
			this.asNew = asNew;
		}

		public boolean isAsNew() {
			return asNew;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

}
