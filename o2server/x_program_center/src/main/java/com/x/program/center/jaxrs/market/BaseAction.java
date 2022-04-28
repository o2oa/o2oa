package com.x.program.center.jaxrs.market;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.WrapModule;

import java.util.Date;

abstract class BaseAction extends StandardJaxrsAction {

	protected static String COLLECT_MARKET_CATEGORY = "/o2_collect_assemble/jaxrs/application2/list/category";
	protected static String COLLECT_MARKET_LIST_INFO = "/o2_collect_assemble/jaxrs/application2/list/paging/{page}/size/{size}";
	protected static String COLLECT_MARKET_INFO = "/o2_collect_assemble/jaxrs/application2/";
	protected static String COLLECT_UNIT_IS_VIP = "/o2_collect_assemble/jaxrs/unit/is/vip";

	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(InstallData.class);

	public boolean hasAuth(EffectivePerson effectivePerson, String person){
		if(effectivePerson.isManager()){
			return true;
		}
		if(effectivePerson.getDistinguishedName().equals(person)){
			return true;
		}
		return false;
	}

	public static class InstallData extends GsonPropertyObject {
		private WrapModule WrapModule;

		private String staticResource;

		private String customApp;

		public WrapModule getWrapModule() {
			return WrapModule;
		}

		public void setWrapModule(WrapModule wrapModule) {
			WrapModule = wrapModule;
		}

		public String getStaticResource() {
			return staticResource;
		}

		public void setStaticResource(String staticResource) {
			this.staticResource = staticResource;
		}

		public String getCustomApp() {
			return customApp;
		}

		public void setCustomApp(String customApp) {
			this.customApp = customApp;
		}
	}

	public static class Application2 extends GsonPropertyObject{

		@FieldDescribe("主键.")
		private String id;

		@FieldDescribe("名称.必填")
		private String name;

		@FieldDescribe("分类.必填")
		private String category;

		@FieldDescribe("子分类.")
		private String subCategory;

		@FieldDescribe("版本.必填")
		private String version;

		@FieldDescribe("价格.")
		private Double price;

		@FieldDescribe("状态：draft|audit|publish|invalid.")
		private String status;

		@FieldDescribe("宣传图片url链接.")
		private String broadcastPic;

		@FieldDescribe("封面图片url链接.")
		private String indexPic;

		@FieldDescribe("视频url链接.")
		private String video;

		@FieldDescribe("依赖中间件(如：onlyOffice)")
		private String middleware;

		@FieldDescribe("适配O2的版本(向上兼容)")
		private String o2Version;

		@FieldDescribe("配置文件配置地址(web端)")
		private String configPath;

		@FieldDescribe("描述.必填")
		private String describe;

		@FieldDescribe("应用简介.必填")
		private String abort;

		@FieldDescribe("应用安装步骤.必填")
		private String installSteps;

		@FieldDescribe("发布者.")
		private String publisher;

		@FieldDescribe("发布时间")
		private Date publishTime;

		@FieldDescribe("排序号,升序排列,为空在最后")
		private Integer orderNumber;

		@FieldDescribe("推荐指数")
		private Integer recommend;

		@FieldDescribe("下载次数")
		private Integer downloadCount;

		@FieldDescribe("最后更新时间")
		private Date lastUpdateTime;

		@FieldDescribe("安装后是否需要重启")
		private Boolean restart = false;

		@FieldDescribe("是否是VIP应用")
		private Boolean vipApp = false;

		@FieldDescribe("创建时间.")
		private Date createTime;

		@FieldDescribe("修改时间.")
		private Date updateTime;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getSubCategory() {
			return subCategory;
		}

		public void setSubCategory(String subCategory) {
			this.subCategory = subCategory;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getBroadcastPic() {
			return broadcastPic;
		}

		public void setBroadcastPic(String broadcastPic) {
			this.broadcastPic = broadcastPic;
		}

		public String getIndexPic() {
			return indexPic;
		}

		public void setIndexPic(String indexPic) {
			this.indexPic = indexPic;
		}

		public String getVideo() {
			return video;
		}

		public void setVideo(String video) {
			this.video = video;
		}

		public String getMiddleware() {
			return middleware;
		}

		public void setMiddleware(String middleware) {
			this.middleware = middleware;
		}

		public String getO2Version() {
			return o2Version;
		}

		public void setO2Version(String o2Version) {
			this.o2Version = o2Version;
		}

		public String getConfigPath() {
			return configPath;
		}

		public void setConfigPath(String configPath) {
			this.configPath = configPath;
		}

		public String getDescribe() {
			return describe;
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

		public String getAbort() {
			return abort;
		}

		public void setAbort(String abort) {
			this.abort = abort;
		}

		public String getInstallSteps() {
			return installSteps;
		}

		public void setInstallSteps(String installSteps) {
			this.installSteps = installSteps;
		}

		public String getPublisher() {
			return publisher;
		}

		public void setPublisher(String publisher) {
			this.publisher = publisher;
		}

		public Date getPublishTime() {
			return publishTime;
		}

		public void setPublishTime(Date publishTime) {
			this.publishTime = publishTime;
		}

		public Integer getOrderNumber() {
			return orderNumber;
		}

		public void setOrderNumber(Integer orderNumber) {
			this.orderNumber = orderNumber;
		}

		public Integer getRecommend() {
			return recommend;
		}

		public void setRecommend(Integer recommend) {
			this.recommend = recommend;
		}

		public Integer getDownloadCount() {
			return downloadCount;
		}

		public void setDownloadCount(Integer downloadCount) {
			this.downloadCount = downloadCount;
		}

		public Date getLastUpdateTime() {
			return lastUpdateTime;
		}

		public void setLastUpdateTime(Date lastUpdateTime) {
			this.lastUpdateTime = lastUpdateTime;
		}

		public Boolean getRestart() {
			return restart;
		}

		public void setRestart(Boolean restart) {
			this.restart = restart;
		}

		public Boolean getVipApp() {
			return vipApp;
		}

		public void setVipApp(Boolean vipApp) {
			this.vipApp = vipApp;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public Date getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}
	}

}
