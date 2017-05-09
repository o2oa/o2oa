package com.x.crm.assemble.control.jaxrs.crmbaseconfig;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.WrapCrmTools;
import com.x.crm.assemble.control.wrapin.WrapInCrmRegion;
import com.x.crm.assemble.control.wrapout.WrapOutRegion;
import com.x.crm.core.entity.CrmRegion;

@Path("region")
public class RegionConfigAction extends StandardJaxrsAction {
	private Logger logger = LoggerFactory.getLogger(RegionConfigAction.class);

	@HttpMethodDescribe(value = "测试", response = WrapOutString.class)
	@GET
	@Path("iswork")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void iswork(@Suspended final AsyncResponse asyncResponse) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = null;
		wrap = new WrapOutString();
		wrap.setValue("region/iswork is work!!");
		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "初始化", response = WrapOutString.class)
	@POST
	@Path("init")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void initdata(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonArray jsonArray) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//JsonArray jsonArray = new JsonParser().parse(jsonData).getAsJsonArray();
			logger.error("jsonArray:" + jsonArray.size());
			for (JsonElement jsonElement : jsonArray) {
				//logger.error(jsonElement2.getAsJsonObject().get("Name").getAsString());
				CrmRegion crmRegion = new CrmRegion();

				WrapInCrmRegion wrapInCrmRegion = new WrapInCrmRegion();
				wrapInCrmRegion = this.convertToWrapIn(jsonElement, WrapInCrmRegion.class);
				crmRegion = WrapCrmTools.CrmRegionInCopier.copy(wrapInCrmRegion);

				crmRegion.setCityid(jsonElement.getAsJsonObject().get("ID").getAsString());
				crmRegion.setCityname(jsonElement.getAsJsonObject().get("Name").getAsString());
				crmRegion.setParentid(jsonElement.getAsJsonObject().get("ParentId").getAsString());
				crmRegion.setShortname(jsonElement.getAsJsonObject().get("ShortName").getAsString());
				crmRegion.setLeveltype(jsonElement.getAsJsonObject().get("LevelType").getAsString());

				if (null == jsonElement.getAsJsonObject().get("CityCode")) {
					crmRegion.setCitycode("");
				} else {
					crmRegion.setCitycode(jsonElement.getAsJsonObject().get("CityCode").getAsString());
				}

				if (null == jsonElement.getAsJsonObject().get("ZipCode")) {
					crmRegion.setZipcode("");
				} else {
					crmRegion.setZipcode(jsonElement.getAsJsonObject().get("ZipCode").getAsString());
				}

				crmRegion.setMergername(jsonElement.getAsJsonObject().get("MergerName").getAsString());
				crmRegion.setLng(jsonElement.getAsJsonObject().get("lng").getAsString());
				crmRegion.setLat(jsonElement.getAsJsonObject().get("Lat").getAsString());
				if (null == jsonElement.getAsJsonObject().get("Pinyin")) {
					crmRegion.setCitypinyin("");
				} else {
					crmRegion.setCitypinyin(jsonElement.getAsJsonObject().get("Pinyin").getAsString());
				}

				emc.beginTransaction(CrmRegion.class);
				emc.persist(crmRegion);
				emc.commit();
			}

			//http://blog.csdn.net/wyb112233/article/details/50179659
			WrapOutString wrap = null;
			wrap = new WrapOutString();
			wrap.setValue("" + jsonArray.size());
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "省份列表", response = WrapOutString.class)
	@PUT
	@Path("provincelist")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void provinceList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<WrapOutRegion>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			//			EffectivePerson effectivePerson = this.effectivePerson(request);
			//			String _loginPersonName = effectivePerson.getName();
			Business business = new Business(emc);
			//			boolean isPassTest = true;

			List<CrmRegion> CrmRegionList = new ArrayList<CrmRegion>();
			List<WrapOutRegion> wrapOutRegionList = new ArrayList<WrapOutRegion>();

			CrmRegionList = business.regionFactory().listProvince();

			wrapOutRegionList = WrapCrmTools.CrmRegionOutCopier.copy(CrmRegionList);
			result.setCount((long) wrapOutRegionList.size());
			result.setData(wrapOutRegionList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "根据cityid，获取城市列表", response = WrapOutString.class)
	@PUT
	@Path("citylist")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void cityList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<List<WrapOutRegion>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			EffectivePerson effectivePerson = this.effectivePerson(request);
			String _loginPersonName = effectivePerson.getName();
			Business business = new Business(emc);
			boolean isPassTest = true;

			List<CrmRegion> CrmRegionList = new ArrayList<CrmRegion>();
			List<WrapOutRegion> wrapOutRegionList = new ArrayList<WrapOutRegion>();

			if (null == jsonElement.getAsJsonObject().get("pid") || StringUtils.isBlank(jsonElement.getAsJsonObject().get("pid").getAsString())) {
				Exception exception = new RegionSuperiorPidException();
				result.error(exception);
				isPassTest = false;
			}

			if (isPassTest) {
				String pid = jsonElement.getAsJsonObject().get("pid").getAsString();
				logger.error("pid:" + pid);
				CrmRegionList = business.regionFactory().listCity(pid);

				wrapOutRegionList = WrapCrmTools.CrmRegionOutCopier.copy(CrmRegionList);
				result.setCount((long) wrapOutRegionList.size());
				result.setData(wrapOutRegionList);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "根据parentid，获取区县列表", response = WrapOutString.class)
	@PUT
	@Path("countylist")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void countyList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<List<WrapOutRegion>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			EffectivePerson effectivePerson = this.effectivePerson(request);
			String _loginPersonName = effectivePerson.getName();
			Business business = new Business(emc);
			boolean isPassTest = true;

			List<CrmRegion> CrmRegionList = new ArrayList<CrmRegion>();
			List<WrapOutRegion> wrapOutRegionList = new ArrayList<WrapOutRegion>();

			if (null == jsonElement.getAsJsonObject().get("pid") || StringUtils.isBlank(jsonElement.getAsJsonObject().get("pid").getAsString())) {
				Exception exception = new RegionSuperiorPidException();
				result.error(exception);
				isPassTest = false;
			}
			if (isPassTest) {
				String pid = jsonElement.getAsJsonObject().get("pid").getAsString();
				logger.error("pid:" + pid);
				CrmRegionList = business.regionFactory().listCounty(pid);

				wrapOutRegionList = WrapCrmTools.CrmRegionOutCopier.copy(CrmRegionList);
				result.setCount((long) wrapOutRegionList.size());
				result.setData(wrapOutRegionList);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}
