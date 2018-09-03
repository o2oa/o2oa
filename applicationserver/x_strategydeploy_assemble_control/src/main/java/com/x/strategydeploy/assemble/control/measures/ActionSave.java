package com.x.strategydeploy.assemble.control.measures;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.measures.exception.ExceptionMeasures;
import com.x.strategydeploy.assemble.control.measures.exception.ExceptionMeasuresInfoIdEmpty;
import com.x.strategydeploy.assemble.control.measures.exception.ExceptionMeasuresNameEmpty;
import com.x.strategydeploy.assemble.control.measures.exception.ExceptionWrapInConvert;
import com.x.strategydeploy.core.entity.MeasuresInfo;

public class ActionSave extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(ActionSave.class);

	//出
	public static class Wo extends WoId {
	}

	//执行
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		MeasuresInfo measuresinfo = new MeasuresInfo();
		//Wo wrapOutId = null;
		Boolean IsPass = true;
		Wi wrapIn = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			IsPass = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (IsPass) {
			if (null == wrapIn.getMeasuresinfotitle() || wrapIn.getMeasuresinfotitle().isEmpty()) {
				IsPass = false;
				Exception exception = new ExceptionMeasuresInfoIdEmpty();
				result.error(exception);
			}
		}

		if (IsPass) {
			if (wrapIn.getMeasuresinfotitle().length() > 70) {
				IsPass = false;
				Exception exception = new ExceptionMeasuresNameEmpty();
				result.error(exception);
			}
		}

		if (IsPass) {
			if (null == wrapIn.getMeasuresinfoparentid() || wrapIn.getMeasuresinfoparentid().isEmpty()) {
				IsPass = false;
				Exception exception = new ExceptionMeasures("请选择公司重点工作，Measuresinfoparentid 不能为空");
				result.error(exception);
			}
		}

		if (IsPass) {
			if (null == wrapIn.getMeasuresinfoyear() || wrapIn.getMeasuresinfoyear().isEmpty()) {
				IsPass = false;
				Exception exception = new ExceptionMeasures("'measuresinfoyear' can not be blank!");
				result.error(exception);
			}
		}

		if (IsPass) {
			//序号非空
			//if (null == wrapIn.getSequencenumber() || wrapIn.getSequencenumber() <= 0) {
			if (null == wrapIn.getSequencenumber()) {
				IsPass = false;
				Exception e = new Exception("请填写序号,编号为从1.1开始");
				result.error(e);
			}
		}

		if (IsPass) {
			//序号合法
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				boolean isExist = false;
				isExist = business.measuresInfoFactory().IsExistById(wrapIn.getId());
				ActionVerifySequenceNumber VerifySequenceNumber = new ActionVerifySequenceNumber();
				//如果是新增，那么校验序号是否被占用。
				//如果是更新，那么1判断更新的序号和老序号是否相同，如果相同，那么通过，否则校验序号是否被占用
				if (isExist) {
					MeasuresInfo o = business.measuresInfoFactory().getById(wrapIn.getId());
					//if (wrapIn.getSequencenumber() != o.getSequencenumber()) {
					if (!StringUtils.equalsIgnoreCase(wrapIn.getSequencenumber(), o.getSequencenumber()) ) {
						ActionResult<ActionVerifySequenceNumber.Wo> ActionResultSnWo = VerifySequenceNumber.excute(wrapIn);
						if (ActionResultSnWo.getType() == Type.error) {
							IsPass = false;
							Exception e = new Exception(ActionResultSnWo.getMessage());
							result.error(e);
						}
					}
				} else {
					ActionResult<ActionVerifySequenceNumber.Wo> ActionResultSnWo = VerifySequenceNumber.excute(wrapIn);
					if (ActionResultSnWo.getType() == Type.error) {
						IsPass = false;
						Exception e = new Exception(ActionResultSnWo.getMessage());
						result.error(e);
					}
				}
			} catch (Exception e) {
				logger.warn("measuresInfo ActionSave update/ get  a error!");
				result.error(e);
			}
		}

		if (IsPass) {
			//如果创建者为空，那么久默认当前用户
			if (null == wrapIn.getMeasuresinfocreator() || wrapIn.getMeasuresinfocreator().isEmpty()) {
				logger.info("effectivePerson.getDistinguishedName::" + effectivePerson.getDistinguishedName());
				wrapIn.setMeasuresinfocreator(effectivePerson.getDistinguishedName());
			}
		}

		if (IsPass) {
			measuresinfo = this.convertToWrapIn(jsonElement, MeasuresInfo.class);
			measuresinfo = measuresInfoOperationService.save(wrapIn);
			Wo wo = new Wo();
			wo.setId(measuresinfo.getId());
			result.setData(wo);
			//result.setData(wrapOutId);
		} else {

			logger.info("not check pass !!!");
		}

		return result;
	}
}
