package com.x.program.center.jaxrs.command;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionUploadFile extends BaseAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUploadFile.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String ctl, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		LOGGER.debug("execute:{}, ctl:{}.", effectivePerson::getDistinguishedName, () -> ctl);
		if (BooleanUtils.isNotTrue(Config.general().getDeployWarEnable())) {
			throw new ExceptionDeployDisable();
		}
		ActionResult<Wo> result = new ActionResult<>();
		List<String> list = Config.nodes().keySet().stream().filter(o -> {
			try {
				return StringUtils.equals(o, Config.node());
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return false;
		}).collect(Collectors.toList());
		list.add(Config.node());
		for (String node : list) {
			try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
				executeCommand(ctl, node, Config.nodes().get(node).nodeAgentPort(), byteArrayInputStream, disposition);
			}
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private void executeCommand(String ctl, String nodeName, int nodePort, InputStream fileInputStream,
			FormDataContentDisposition disposition)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, URISyntaxException {
		try (Socket socket = new Socket(nodeName, nodePort);
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				DataInputStream dis = new DataInputStream(socket.getInputStream())) {
			socket.setKeepAlive(true);
			socket.setSoTimeout(5000);
			Map<String, Object> commandObject = new HashMap<>();
			commandObject.put("command", "redeploy:" + ctl);
			commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
			dos.writeUTF(XGsonBuilder.toJson(commandObject));
			dos.flush();
			dos.writeUTF(disposition.getFileName());
			dos.flush();
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
				dos.write(bytes, 0, length);
				dos.flush();
			}
		}
	}

	@Schema(name = "com.x.program.center.jaxrs.command.ActionUploadFile$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 6597732235155964397L;

	}

}
