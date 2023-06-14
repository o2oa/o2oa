package com.x.program.init.jaxrs.data;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.h2.tools.RunScript;

import com.itextpdf.io.codec.Base64.InputStream;
import com.nimbusds.jose.util.StandardCharset;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ZipTools;

class ActionUpload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpload.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, final FormDataBodyPart part) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		FormDataContentDisposition fileDetail = part.getFormDataContentDisposition();

		String name = DateTools.format(new Date(), DateTools.format_yyyyMMddHHmmss);
		Path path = Config.dir_local_temp().toPath().resolve(DateTools.format(new Date(), name + ".zip"));

		Files.copy(part.getValueAs(InputStream.class), path, StandardCopyOption.REPLACE_EXISTING);
		ZipTools.unZip(path.toFile(), null, Config.path_local_dump(true).resolve("dumpData_" + name).toFile(), true,
				StandardCharsets.UTF_8);

		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7892218945591687635L;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -5726130517002102825L;

		private String secret;

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

	}

}