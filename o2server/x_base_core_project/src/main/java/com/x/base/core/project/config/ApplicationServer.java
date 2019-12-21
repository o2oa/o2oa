package com.x.base.core.project.config;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class ApplicationServer extends ConfigObject {

	public static ApplicationServer defaultInstance() {
		return new ApplicationServer();
	}

	private static final Integer DEFAULT_PORT = 20020;
	private static final Integer DEFAULT_SCANINTERVAL = 0;
	private static final Integer DEFAULT_WEIGHT = 100;
	private static final Integer DEFAULT_SCHEDULEWEIGHT = 100;
	private static final Boolean DEFAULT_STATENABLE = true;
	private static final String DEFAULT_STATEXCLUSIONS = "*.js,*.gif,*.jpg,*.png,*.css,*.ico";
	private static final Integer DEFAULT_MAXFORMCONTENT = 20;

	public ApplicationServer() {
		this.enable = true;
		this.port = DEFAULT_PORT;
		this.sslEnable = false;
		this.proxyHost = "";
		this.proxyPort = DEFAULT_PORT;
		this.redeploy = true;
		this.scanInterval = DEFAULT_SCANINTERVAL;
		this.includes = new CopyOnWriteArrayList<String>();
		this.excludes = new CopyOnWriteArrayList<String>();
		this.weights = new CopyOnWriteArrayList<NameWeightPair>();
		this.scheduleWeights = new CopyOnWriteArrayList<NameWeightPair>();
		this.statEnable = DEFAULT_STATENABLE;
		this.statExclusions = DEFAULT_STATEXCLUSIONS;
		this.maxFormContent = DEFAULT_MAXFORMCONTENT;
	}

	@FieldDescribe("是否启用")
	private Boolean enable;
	@FieldDescribe("http/https端口,负责向前端提供数据访问接口.默认为20020端口.")
	private Integer port;
	@FieldDescribe("是否启用ssl传输加密,如果启用将使用config/keystore文件作为密钥文件.使用config/token.json文件中的sslKeyStorePassword字段为密钥密码,sslKeyManagerPassword为管理密码.")
	private Boolean sslEnable;
	@FieldDescribe("代理主机,当服务器是通过apache/nginx等代理服务器映射到公网或者通过路由器做端口映射,在这样的情况下需要设置此地址以标明公网访问地址.")
	private String proxyHost;
	@FieldDescribe("代理端口,当服务器是通过apache/nginx等代理服务器映射到公网或者通过路由器做端口映射,在这样的情况下需要设置此地址以标明公网访问端口.")
	private Integer proxyPort;
	@FieldDescribe("每次启动是否重载全部应用.")
	private Boolean redeploy;
	@FieldDescribe("应用reload扫描间隔,<0 表示不会reload应用,扫描到应用文件发生了变化.")
	private Integer scanInterval;
	@FieldDescribe("承载的应用,在集群环境下可以选择仅承载部分应用以降低服务器负载,可以使用*作为通配符.")
	private CopyOnWriteArrayList<String> includes;
	@FieldDescribe("选择不承载的应用,和includes的值配合使用可以选择或者排除承载的应用,可以使用*作为通配符.")
	private CopyOnWriteArrayList<String> excludes;
	@FieldDescribe("设置应用的Web访问权重,在集群环境中,一个应用可以部署多个实例提供负载均衡.通过合计占比来分配应用占比.")
	private CopyOnWriteArrayList<NameWeightPair> weights;
	@FieldDescribe("设置应用的定时任务权重,在集群环境中,一个应用可以部署多个实例提供负载均衡.通过合计占比来分配应用占比.")
	private CopyOnWriteArrayList<NameWeightPair> scheduleWeights;
	@FieldDescribe("启用统计,默认启用统计.")
	private Boolean statEnable;
	@FieldDescribe("统计忽略路径,默认忽略*.js,*.gif,*.jpg,*.png,*.css,*.ico")
	private String statExclusions;
	@FieldDescribe("最大提交数据限制(M),限制有所上传的内容大小,包括附件.")
	private Integer maxFormContent;

	public Integer getMaxFormContent() {
		return ((null == maxFormContent) || (maxFormContent < 1)) ? DEFAULT_MAXFORMCONTENT : maxFormContent;
	}

	public String getStatExclusions() {
		return (StringUtils.isEmpty(statExclusions) ? DEFAULT_STATEXCLUSIONS : this.statExclusions) + ",/druid/*";
	}

	public Boolean getStatEnable() {
		return BooleanUtils.isNotFalse(statEnable);
	}

	public Integer getScanInterval() {
		if (null != this.scanInterval && this.scanInterval > 0) {
			return this.scanInterval;
		}
		return DEFAULT_SCANINTERVAL;
	}

	public CopyOnWriteArrayList<NameWeightPair> getWeights() {
		if (null == this.weights) {
			this.weights = new CopyOnWriteArrayList<NameWeightPair>();
		}
		return this.weights;
	}

	public CopyOnWriteArrayList<NameWeightPair> getScheduleWeights() {
		if (null == this.scheduleWeights) {
			this.scheduleWeights = new CopyOnWriteArrayList<NameWeightPair>();
		}
		return this.scheduleWeights;
	}

	public Integer weight(Class<?> clazz) {
		NameWeightPair pair = this.weights.stream().filter(p -> StringUtils.equals(p.getName(), clazz.getName()))
				.findFirst().orElse(new NameWeightPair(clazz.getName(), DEFAULT_WEIGHT));
		return pair.getWeight();
	}

	public Integer scheduleWeight(Class<?> clazz) {
		NameWeightPair pair = this.scheduleWeights.stream()
				.filter(p -> StringUtils.equals(p.getName(), clazz.getName())).findFirst()
				.orElse(new NameWeightPair(clazz.getName(), DEFAULT_SCHEDULEWEIGHT));
		return pair.getWeight();
	}

	public class NameWeightPair {

		private String name;

		private Integer weight = DEFAULT_WEIGHT;

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
			return DEFAULT_WEIGHT;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0 && this.port < 65535) {
			return this.port;
		}
		return DEFAULT_PORT;
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public String getProxyHost() throws Exception {
		return StringUtils.isNotEmpty(this.proxyHost) ? this.proxyHost : "";
	}

	public Integer getProxyPort() {
		if (null != this.proxyPort && this.proxyPort > 0) {
			return this.proxyPort;
		}
		return this.getPort();
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

}
