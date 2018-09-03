package o2.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class DemoSite extends GsonPropertyObject {

	public static DemoSite defaultInstance() {
		return new DemoSite();
	}

	public DemoSite() {
		this.name = default_name;
		this.site = default_site;
		this.ssoClient = default_ssoClient;
		this.ssoKey = default_ssoKey;
		this.ssoPerson = default_ssoPerson;
		this.registEnable = default_registEnable;
		this.registUnit = default_registUnit;
	}

	private static final String default_name = "演示站点";
	private static final String default_site = "https://demo.o2oa.io:24420";
	private static final String default_ssoClient = "collect";
	private static final String default_ssoKey = "thisistheway";
	private static final String default_ssoPerson = "周睿";
	private static final Boolean default_registEnable = true;
	private static final String default_registUnit = "人力资源部";

	private String name;

	private String site;

	private String ssoClient;

	private String ssoKey;

	private String ssoPerson;

	private Boolean registEnable;

	private String registUnit;

	public String getName() {
		return StringUtils.isEmpty(this.name) ? default_name : this.name;
	}

	public String getRegistUnit() {
		return StringUtils.isEmpty(this.registUnit) ? default_registUnit : this.registUnit;
	}

	public Boolean getRegistEnable() {
		return BooleanUtils.isTrue(this.registEnable);
	}

	public String getSite() {
		return StringUtils.isEmpty(this.site) ? default_site : this.site;
	}

	public String getSsoClient() {
		return StringUtils.isEmpty(this.ssoClient) ? default_ssoClient : this.ssoClient;
	}

	public String getSsoKey() {
		return StringUtils.isEmpty(this.ssoKey) ? default_ssoKey : this.ssoKey;
	}

	public String getSsoPerson() {
		return StringUtils.isEmpty(this.ssoPerson) ? default_ssoPerson : this.ssoPerson;
	}

	public void setSsoClient(String ssoClient) {
		this.ssoClient = ssoClient;
	}

	public void setSsoKey(String ssoKey) {
		this.ssoKey = ssoKey;
	}

	public void setSsoPerson(String ssoPerson) {
		this.ssoPerson = ssoPerson;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void setRegistEnable(Boolean registEnable) {
		this.registEnable = registEnable;
	}

	public void setRegistUnit(String registUnit) {
		this.registUnit = registUnit;
	}

	public void setName(String name) {
		this.name = name;
	}

}
