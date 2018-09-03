package com.x.crm.assemble.control.jaxrs.crmbaseconfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.WrapCrmTools;
import com.x.crm.assemble.control.wrapin.WrapInCrmBaseConfig;
import com.x.crm.assemble.control.wrapout.WrapOutCrmBaseConfig;
import com.x.crm.assemble.control.wrapout.WrapOutCustomerBaseConfig;
import com.x.crm.core.entity.CrmBaseConfig;

@JaxrsDescribe(value="CRM基础配置服务")
@Path("baseconfig")
public class CrmBaseConfigAction extends StandardJaxrsAction {
	private Logger logger = LoggerFactory.getLogger(CrmBaseConfigAction.class);

	//@HttpMethodDescribe(value = "测试", response = WrapOutString.class)
	@GET
	@Path("iswork")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void iswork(@Suspended final AsyncResponse asyncResponse) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = null;
		wrap = new WrapOutString();
		wrap.setValue("baseconfig/iswork is work!!");
		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	//@HttpMethodDescribe(value = "创建数据表，创建数据", request = JsonElement.class, response = WrapOutString.class)
	@POST
	@Path("create")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CrmBaseConfig crmbaseconfig = new CrmBaseConfig();
			WrapInCrmBaseConfig wrapInCrmbaseconfig = new WrapInCrmBaseConfig();
			wrapInCrmbaseconfig = this.convertToWrapIn(jsonElement, WrapInCrmBaseConfig.class);
			crmbaseconfig = WrapCrmTools.CrmBaseConfigInCopier.copy(wrapInCrmbaseconfig);
			emc.beginTransaction(CrmBaseConfig.class);
			emc.persist(crmbaseconfig);
			emc.commit();
			WrapOutString wrap = null;
			wrap = new WrapOutString();
			wrap.setValue(crmbaseconfig.getId());
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value="根据配置类型，获取配置数据",action=StandardJaxrsAction.class)
	//@HttpMethodDescribe(value = "根据配置类型，获取配置数据", request = JsonElement.class, response = WrapOutString.class)
	@PUT
	@Path("getconfig/{type}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getConfigBytype(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement,@JaxrsParameterDescribe("配置类型") @PathParam("type") String type) {
		ActionResult<WrapOutCustomerBaseConfig> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			WrapOutCustomerBaseConfig data = new WrapOutCustomerBaseConfig();

			//List<WrapOutCrmBaseConfig> wraps = new ArrayList<>();
			List<String> ids = business.crmBaseConfigFactory().getIdListByBaseconfigtype(type);

			long configCount = 0L;
			for (String _id : ids) {
				WrapOutCrmBaseConfig _RootConfig = WrapCrmTools.CrmBaseConfigOutCopier.copy(emc.find(_id, CrmBaseConfig.class));
				//List<WrapOutCrmBaseConfig> RootWraps = WrapCrmTools.CrmBaseConfigOutCopier.copy(business.crmBaseConfigFactory().getConfigListByTypByParentIdOrderByOrdernumber(type, _id));
				List<WrapOutCrmBaseConfig> firstWraps = WrapCrmTools.CrmBaseConfigOutCopier.copy(business.crmBaseConfigFactory().getConfigListByTypByParentIdOrderByOrdernumber(type, _id));

				List<WrapOutCrmBaseConfig> firstLevelWraps = new ArrayList<>();
				for (WrapOutCrmBaseConfig firstWrap : firstWraps) {
					List<WrapOutCrmBaseConfig> secondWraps = WrapCrmTools.CrmBaseConfigOutCopier.copy(business.crmBaseConfigFactory().getConfigListByTypByParentIdOrderByOrdernumber(type, firstWrap.getId()));
					firstWrap.setChildNodes(secondWraps);
					firstLevelWraps.add(firstWrap);
					configCount++;
				}

				_RootConfig.setChildNodes(firstLevelWraps);

				String _configvalue = _RootConfig.getConfigvalue();
				logger.error(new Exception("_configvalue:" + _configvalue));
				if (StringUtils.equalsIgnoreCase(_configvalue, "customertype")) {
					data.setCustomertype_config(_RootConfig);
				}

				if (StringUtils.equalsIgnoreCase(_configvalue, "level")) {
					data.setLevel_config(_RootConfig);
				}

				if (StringUtils.equalsIgnoreCase(_configvalue, "source")) {
					data.setSource_config(_RootConfig);
				}

				if (StringUtils.equalsIgnoreCase(_configvalue, "industry")) {
					data.setIndustry_config(_RootConfig);
				}

				if (StringUtils.equalsIgnoreCase(_configvalue, "state")) {
					data.setState_config(_RootConfig);
				}
				if (StringUtils.equalsIgnoreCase(_configvalue, "customerrank")) {
					data.setCustomerrank_config(_RootConfig);
				}

			}
			result.setCount(configCount);
			result.setData(data);

		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	//@HttpMethodDescribe(value = "根据配置类型，获取配置数据", request = JsonElement.class, response = WrapOutString.class)
	@PUT
	@Path("getbytype/{type}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getByBaseconfigtype(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement, @PathParam("type") String type) {
		ActionResult<List<WrapOutCrmBaseConfig>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<WrapOutCrmBaseConfig> wraps = new ArrayList<>();
			//List<String> ids = business.crmBaseConfigFactory().getIdListByBaseconfigtype(type);

			/*			for (String _id : ids) {
							WrapOutCrmBaseConfig _firsconfig = WrapCrmTools.CrmBaseConfigOutCopier.copy(emc.find(_id, CrmBaseConfig.class));
							List<String> _secondLevelIds = business.crmBaseConfigFactory().getIdListByBaseconfigtypeByParentId(type, _id);
							List<WrapOutCrmBaseConfig> secondwraps = WrapCrmTools.CrmBaseConfigOutCopier.copy(emc.list(CrmBaseConfig.class, _secondLevelIds));
							if (_secondLevelIds.size() > 0) {
								_firsconfig.setParentconfigLsit(secondwraps);
							}
							wraps.add(_firsconfig);
						}*/

			//			for (String _id : ids) {
			//				//WrapOutCrmBaseConfig _firsconfig = WrapCrmTools.CrmBaseConfigOutCopier.copy(emc.find(_id, CrmBaseConfig.class));
			//				CrmBaseConfig parentNode = business.crmBaseConfigFactory().recursiveTree(type, _id);
			//				WrapOutCrmBaseConfig WrapparentNode = WrapCrmTools.CrmBaseConfigOutCopier.copy(parentNode);
			//				wraps.add(WrapparentNode);
			//			}

			wraps = business.crmBaseConfigFactory().rootNodeListByType(type);

			//			for (WrapOutCrmBaseConfig wrap : wraps) {
			//				CrmBaseConfig parentNode = business.crmBaseConfigFactory().recursiveTree(type, wrap.getId(), wrap);
			//				WrapOutCrmBaseConfig WrapparentNode = WrapCrmTools.CrmBaseConfigOutCopier.copy(parentNode);
			//				wraps.add(WrapparentNode);
			//			}

			//			Iterator<WrapOutCrmBaseConfig> iterator = wraps.iterator();
			//			while (iterator.hasNext()) {
			//				WrapOutCrmBaseConfig wrap = iterator.next();
			//				if (wrap == iterator.next()) {
			//					iterator.remove();
			//				}
			//				CrmBaseConfig parentNode = business.crmBaseConfigFactory().recursiveTree(type, wrap.getId(), wrap);
			//				WrapOutCrmBaseConfig WrapparentNode = WrapCrmTools.CrmBaseConfigOutCopier.copy(parentNode);
			//				wraps.add(WrapparentNode);
			//			}

			List<WrapOutCrmBaseConfig> thingsToBeAdd = new ArrayList<WrapOutCrmBaseConfig>();

			for (Iterator<WrapOutCrmBaseConfig> iterator = wraps.iterator(); iterator.hasNext();) {
				WrapOutCrmBaseConfig wrap = iterator.next();
				logger.error(new Exception("Iterator：" + wrap.getId()));

				WrapOutCrmBaseConfig parentNode = business.crmBaseConfigFactory().recursiveTree(type, wrap.getId(), wrap);
				logger.error(new Exception("parentNode：" + parentNode.getId()));
				//				if (null != wrap) {
				//					if (wrap.iscFlag()) {
				//						logger.debug(type);
				//						WrapOutCrmBaseConfig parentNode = business.crmBaseConfigFactory().recursiveTree(type, wrap.getId(), wrap);
				//						WrapOutCrmBaseConfig WrapparentNode = WrapCrmTools.CrmBaseConfigOutCopier.copy(parentNode);
				//						thingsToBeAdd.add(WrapparentNode);
				//						wrap.setcFlag(false);
				//					}
				//				}
			}
			wraps.addAll(thingsToBeAdd);

			//						for (String _id : ids) {
			//							logger.error("一级："+_id);
			//							List<String> idlist = business.crmBaseConfigFactory().listSubNested(_id);
			////							for (String string : idlist) {
			////								List<String> idlist3 = business.crmBaseConfigFactory().listSubNested(string);
			////								for (String string2 : idlist3) {
			////									//logger.error("一级："+_id+" 二级："+string+" 三级："+string2);
			////								}
			////								//logger.error("一级："+_id+" 二级："+string);
			////							}
			//						}

			//			List<String> idlist = business.crmBaseConfigFactory().listSubNested("102e8821-ce72-4410-9d83-856d39d7bccd");
			//			List<CrmBaseConfig> res = emc.list(CrmBaseConfig.class, idlist);
			//			wraps =  WrapCrmTools.CrmBaseConfigOutCopier.copy(res);
			//			//wraps.add(_firsconfig);
			//wraps.add(_firsconfig);
			result.setData(wraps);
		} catch (Throwable th) {
			// TODO Auto-generated catch block
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}
