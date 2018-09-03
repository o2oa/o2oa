package com.x.strategydeploy.assemble.control.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("strategyimport")
@JaxrsDescribe("战略管理，战略信息配置导入 【'year':'2018'】")
public class StrategyImportAction extends StandardJaxrsAction {
	private static  Logger logger = LoggerFactory.getLogger(StrategyImportAction.class);

	@JaxrsMethodDescribe(value = "战略管理，战略信息配置导入 【'year':'2018'】.", action = StandardJaxrsAction.class)
	@POST
	//@Consumes({ "multipart/form-data", "application/json" })
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("strategy")
	public void input(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @FormDataParam(FILE_FIELD) final byte[] bytes, @JaxrsParameterDescribe("Excel文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition,
			@JaxrsParameterDescribe("年份") @FormDataParam("year") String year) {
		ActionResult<ActionImportExcelXLSX.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		boolean isPass = true;

		String eL = "[1-9]{1}[0-9]{3}";
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(year);
		isPass = m.matches();
		if (isPass) {
			try {
				result = new ActionImportExcelXLSX().execute(effectivePerson, bytes, disposition, year);
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
				result.error(e);
			}
		} else {
			isPass = false;
			Exception e = new Exception("Please Input Correct date format.eg:2017");
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}

		/*
				JsonObject jsonObj = null;
				jsonObj = jsonElement.getAsJsonObject();//转换成Json对象
				String _year = jsonObj.get("year").getAsString();
				logger.info("上传的年份：" + _year);
		*/

		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}
