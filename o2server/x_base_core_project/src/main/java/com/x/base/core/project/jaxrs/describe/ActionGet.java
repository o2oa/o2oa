package com.x.base.core.project.jaxrs.describe;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.File;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;

public class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	private static Wo wo;

	private static final String KEY = "xplatfor";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext context) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(get(context));
		return result;
	}

	protected Wo get(ServletContext context) throws Exception {
		synchronized (ActionGet.class) {
			if (null == wo) {
				String realPath = context.getRealPath("/describe/api.json");
				File file = new File(realPath);
				if (file.exists()) {
					String json = FileUtils.readFileToString(file, DefaultCharset.charset);
					wo = new Wo();
					wo.setData(Crypto.encrypt(json, KEY, "DES"));
				}
			}
			return wo == null ? new Wo() : wo;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 6772463745917569315L;

		@Schema(description = "数据.")
		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
	}
}
