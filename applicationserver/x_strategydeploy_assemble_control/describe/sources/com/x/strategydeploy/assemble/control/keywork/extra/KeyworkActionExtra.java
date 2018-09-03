package com.x.strategydeploy.assemble.control.keywork.extra;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.keywork.ActionListAllAndRelated;
import com.x.strategydeploy.assemble.control.keywork.ActionListAllAndRelated.WoKeyworkWithMeasures;
import com.x.strategydeploy.assemble.control.keywork.ActionListAllAndRelatedPeriodOfValidity;

@Path("keyworkextra")
@JaxrsDescribe("五项重点工作额外服务")
public class KeyworkActionExtra extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(KeyworkActionExtra.class);

	@JaxrsMethodDescribe(value = "根据年份列出五项重点工作的JSON", action = KeyworkActionExtra.class)
	@GET
	@Path("listbyyear/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listbyyear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year) {
		ActionResult<List<WoKeyworkWithMeasures>> result = new ActionResult<>();
		List<WoKeyworkWithMeasures> wos = new ArrayList<WoKeyworkWithMeasures>();
		//BaseAction.Wi wrapIn = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		boolean ispass = true;

		/*		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
							Business business = new Business(emc);
							wrapIn = this.convertToWrapIn(jsonElement, BaseAction.Wi.class);
						} catch (Exception e) {
							logger.warn("measuresactionextra listbyyear a error!");
							logger.error(e);
							result.error(e);
						}
						if (null == wrapIn.getKeyworkyear() || wrapIn.getKeyworkyear().isEmpty()) {
							Exception e = new Exception("keyworkyear can not be blank");
							result.error(e);
							ispass = false;
						}
		*/
		if (!StringUtils.isNumeric(year)) {
			Exception e = new Exception("year must be positive integer !");
			ispass = false;
			logger.info("year must be positive integer !");
		}

		if (ispass) {
			try {
				wos = new ActionListAllAndRelated().execute(request, effectivePerson, year);
				result.setData(wos);
			} catch (Exception e) {
				result.error(e);
				ispass = false;
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据年月日,列出有效期在此日期中的五项重点工作JSON", action = KeyworkActionExtra.class)
	@GET
	@Path("listbydate/year/{year}/month/{month}/day/{day}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listbydate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year, @JaxrsParameterDescribe("月份") @PathParam("month") String month, @JaxrsParameterDescribe("日期") @PathParam("day") String day) {
		ActionResult<List<ActionListAllAndRelatedPeriodOfValidity.WoKeyworkWithMeasures>> result = new ActionResult<List<ActionListAllAndRelatedPeriodOfValidity.WoKeyworkWithMeasures>>();
		List<ActionListAllAndRelatedPeriodOfValidity.WoKeyworkWithMeasures> wos = new ArrayList<ActionListAllAndRelatedPeriodOfValidity.WoKeyworkWithMeasures>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		boolean ispass = true;
		if (!StringUtils.isNumeric(year) || !StringUtils.isNumeric(month) || !StringUtils.isNumeric(day)) {
			Exception e = new Exception("year , month , day, must be positive integer !");
			ispass = false;
			logger.info("year , month , day, must be positive integer !");
			result.error(e);
		}

		if (ispass) {

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
				String dstr = year + "-" + month + "-" + day;
				Date date = sdf.parse(dstr);
				logger.info("date.toString():" + date.toString());
				//wos = new ActionListAllAndRelatedPeriodOfValidity().execute(request, effectivePerson, date);
				Integer month_i = Integer.valueOf(month);
				wos = new ActionListAllAndRelatedPeriodOfValidity().execute(request, effectivePerson, year, month_i);
				result.setData(wos);
			} catch (Exception e) {
				result.error(e);
				ispass = false;
			}
		}

		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据年月,列出有效期在此日期中的五项重点工作JSON", action = KeyworkActionExtra.class)
	@GET
	@Path("listbyyearandmonth/year/{year}/month/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listbyyearandmonth(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year, @JaxrsParameterDescribe("月份") @PathParam("month") String month) {
		ActionResult<List<ActionListAllAndRelatedPeriodOfValidity.WoKeyworkWithMeasures>> result = new ActionResult<List<ActionListAllAndRelatedPeriodOfValidity.WoKeyworkWithMeasures>>();
		List<ActionListAllAndRelatedPeriodOfValidity.WoKeyworkWithMeasures> wos = new ArrayList<ActionListAllAndRelatedPeriodOfValidity.WoKeyworkWithMeasures>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		boolean ispass = true;
		if (!StringUtils.isNumeric(year) || !StringUtils.isNumeric(month)) {
			Exception e = new Exception("year , month ,  must be positive integer !");
			ispass = false;
			logger.info("year , month , must be positive integer !");
			result.error(e);
		}

		if (ispass) {

			try {
				//自动默认日期为第一天。
				//String day = "1";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//小写的mm表示的是分钟  
				String dstr = year + "-" + month;
				Date date = sdf.parse(dstr);
				//logger.info("date.toString():" + date.toLocaleString());
				//wos = new ActionListAllAndRelatedPeriodOfValidity().execute(request, effectivePerson, date);
				Integer month_i = Integer.valueOf(month);
				wos = new ActionListAllAndRelatedPeriodOfValidity().execute(request, effectivePerson, year, month_i);
				result.setData(wos);
			} catch (Exception e) {
				result.error(e);
				ispass = false;
			}
		}

		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}
