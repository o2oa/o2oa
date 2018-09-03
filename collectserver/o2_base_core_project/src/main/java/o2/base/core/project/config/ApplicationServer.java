package o2.base.core.project.config;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.Host;

public class ApplicationServer extends GsonPropertyObject {

	public static ApplicationServer defaultInstance() {
		return new ApplicationServer();
	}

	public ApplicationServer() {
		this.enable = true;
		this.port = default_port;
		this.sslEnable = false;
		this.proxyHost = "";
		this.proxyPort = default_port;
		this.redeploy = true;
		this.scanInterval = default_scanInterval;
		this.includes = new CopyOnWriteArrayList<String>();
		this.excludes = new CopyOnWriteArrayList<String>();
		this.weights = new CopyOnWriteArrayList<NameWeightPair>();

	}

	private static final Integer default_port = 20080;
	private static final Integer default_scanInterval = 0;
	public static final Integer default_weight = 100;

	private Boolean enable;
	private Integer port;
	private Boolean sslEnable;
	private String proxyHost;
	private Integer proxyPort;
	private Boolean redeploy;
	private Integer scanInterval;
	private CopyOnWriteArrayList<String> includes;
	private CopyOnWriteArrayList<String> excludes;

	private CopyOnWriteArrayList<NameWeightPair> weights;

	public Integer getScanInterval() {
		if (null != this.scanInterval && this.scanInterval > 0) {
			return this.scanInterval;
		}
		return default_scanInterval;
	}

	public class NameWeightPair {

		private String name;

		private Integer weight = default_weight;

		public NameWeightPair() {
		}

		public NameWeightPair(String name, Integer weight) {
			this.name = name;
			this.weight = weight;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getWeight() {
			if ((null != this.weight) && (this.weight > 0)) {
				return this.weight;
			}
			return default_weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0) {
			return this.port;
		}
		return default_port;
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public String getProxyHost() {
		if (StringUtils.isNotEmpty(this.proxyHost)) {
			return this.proxyHost;
		}
		return Host.ROLLBACK_IPV4;
	}

	public Integer getProxyPort() {
		if (null != this.proxyPort && this.proxyPort > 0) {
			return this.proxyPort;
		}
		return default_port;
	}

	public Boolean getRedeploy() {
		return BooleanUtils.isTrue(this.redeploy);
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setScanInterval(Integer scanInterval) {
		this.scanInterval = scanInterval;
	}

	public void setRedeploy(Boolean redeploy) {
		this.redeploy = redeploy;
	}

	public CopyOnWriteArrayList<String> getIncludes() {
		return includes;
	}

	public void setIncludes(CopyOnWriteArrayList<String> includes) {
		this.includes = includes;
	}

	public CopyOnWriteArrayList<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(CopyOnWriteArrayList<String> excludes) {
		this.excludes = excludes;
	}

	public CopyOnWriteArrayList<NameWeightPair> getWeights() {
		return weights;
	}

	public void setWeights(CopyOnWriteArrayList<NameWeightPair> weights) {
		this.weights = weights;
	}

	public Integer weight(Class<?> clazz) {
		NameWeightPair pair = this.weights.stream().filter(p -> StringUtils.equals(p.getName(), clazz.getName()))
				.findFirst().orElse(new NameWeightPair(clazz.getName(), default_weight));
		return pair.getWeight();
	}

}
