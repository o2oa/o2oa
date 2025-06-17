package com.x.ai.assemble.control;

import com.google.gson.JsonObject;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.factory.CmsItemFactory;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.express.Organization;

/**
 * 应用业务服务类
 * @author sword
 */
public class Business {

	private static final Logger logger = LoggerFactory.getLogger(Business.class);

	/**
	 * 自定义应用配置文件名称，根据实际配置修改，文件要求为json格式
	 */
	public static final String CUSTOM_CONFIG_NAME = "o2oa_ai";

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	/**
	 * 获取配置
	 * @return
	 * @throws Exception
	 */
	public static synchronized AiConfig getConfig(){
		AiConfig aiConfig = null;
		try {
			JsonObject jsonObject = Config.customConfig(CUSTOM_CONFIG_NAME);
			if (jsonObject!=null){
				aiConfig = XGsonBuilder.convert(jsonObject, AiConfig.class);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		return aiConfig == null ? new AiConfig() : aiConfig;
	}

	/**
	 * 组织架构管理相关的工厂服务类
	 */
	private Organization organization;

	public Organization organization() {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	private CmsItemFactory cmsItem;

	public CmsItemFactory cmsItem() throws Exception {
		if (null == this.cmsItem) {
			this.cmsItem = new CmsItemFactory(this);
		}
		return cmsItem;
	}

}
