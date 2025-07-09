package com.x.program.center.jaxrs.deploy;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.StringTools;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

/**
 * @author sword
 */
public class ActionDeployServerRes extends BaseAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeployServerRes.class);
	private static final Set<String> set = Set.of("war", "jar");
	private static final String CTL_JAR = "jar";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, Wi wi, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		if (BooleanUtils.isNotTrue(Config.general().getDeployWarEnable())) {
			throw new ExceptionDeployDisable();
		}
		String fileName = this.fileName(disposition);
		LOGGER.info("{}操作部署应用:{}.", effectivePerson::getDistinguishedName, () -> fileName);
		String ext = FilenameUtils.getExtension(fileName);
		String ctl = this.getFileCtl(fileName, ext);
		if(!StringTools.isFileName(fileName) || StringUtils.isBlank(ext) || !set.contains(ext) || StringUtils.isEmpty(ctl)){
			throw new ExceptionIllegalFile(fileName);
		}
		ActionResult<Wo> result = new ActionResult<>();
		List<String> list = Config.nodes().keySet().stream().filter(o -> {
			try {
				return !StringUtils.equals(o, Config.node());
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return false;
		}).collect(Collectors.toList());
		list.add(Config.node());
		List<String> resList = new ArrayList<>();
		for (String node : list) {
			try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
				resList.add(executeCommand(ctl, node, Config.nodes().get(node).nodeAgentPort(), byteArrayInputStream, fileName));
			}
		}
		Wo wo = new Wo();
		String message = StringUtils.join(resList);
		if(message.contains("success")){
			wi.setType("serverRes");
			wo.setId(saveLog(wi, effectivePerson));
		}else{
			throw new ExceptionDeployFail();
		}
		result.setData(wo);
		return result;
	}

	private String getFileCtl(String fileName, String ext) throws Exception {
		String ctl;
		List<String> storeWars = Arrays.asList(Objects.requireNonNull(Config.dir_store().list()));
		List<String> storeJars = Arrays.asList(
                Objects.requireNonNull(Config.dir_store_jars().list()));
		if(CTL_JAR.equals(ext)){
			if(storeJars.contains(fileName)){
				ctl = "storeJar";
			}else{
				ctl = "customJar";
			}
		}else{
			if(storeWars.contains(fileName)){
				ctl = "storeWar";
			}else{
				ctl = "customWar";
			}
		}
		return ctl;
	}

	public static class Wo extends WoId {
		private static final long serialVersionUID = 6597732235155964397L;

	}

}
