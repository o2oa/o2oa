package o2.base.core.project.config;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Update extends GsonPropertyObject {

	public static Update defaultInstance() {
		return new Update();
	}

	public Update() {
	}

	private static final String default_site = "http://update.o2oa.io";

	private String site;

	public String site() {
		return StringUtils.isEmpty(this.site) ? default_site : this.site;
	}

}
