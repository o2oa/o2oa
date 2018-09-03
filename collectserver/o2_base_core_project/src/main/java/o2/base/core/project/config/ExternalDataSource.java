package o2.base.core.project.config;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ExternalDataSource extends GsonPropertyObject {

	public ExternalDataSource() {

	}

	private String url;
	private String username;
	private String password;
	private List<String> includes;
	private List<String> excludes;
	private Boolean enable;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}
