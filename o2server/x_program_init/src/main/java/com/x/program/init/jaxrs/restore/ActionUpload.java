package com.x.program.init.jaxrs.restore;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import com.itextpdf.io.codec.Base64.InputStream;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ZipTools;
import com.x.program.init.MissionRestore;
import com.x.program.init.ThisApplication;

class ActionUpload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpload.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, final FormDataBodyPart part) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		String name = DateTools.format(new Date(), DateTools.format_yyyyMMddHHmmss);
		Path path = Config.dir_local_temp().toPath().resolve(DateTools.format(new Date(), name + ".zip"));

		Files.copy(part.getValueAs(InputStream.class), path, StandardCopyOption.REPLACE_EXISTING);
		ZipTools.unZip(path.toFile(), null, Config.path_local_dump(true).resolve("dumpData_" + name).toFile(), true,
				StandardCharsets.UTF_8);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		MissionRestore missionRestore = new MissionRestore();
		missionRestore.setName(name);
		ThisApplication.setMissionRestore(missionRestore);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7892218945591687635L;

	}

}