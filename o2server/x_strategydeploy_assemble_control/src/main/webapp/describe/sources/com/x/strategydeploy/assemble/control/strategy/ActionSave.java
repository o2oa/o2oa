package com.x.strategydeploy.assemble.control.strategy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.x.strategydeploy.assemble.control.strategy.exception.ExceptionStrategyNameEmpty;
import com.x.strategydeploy.assemble.control.strategy.exception.ExceptionWrapInConvert;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionSave extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	//出
	public static class Wo extends WoId {
	}

	//执行保存
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		StrategyDeploy strategydeploy = new StrategyDeploy();
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

		logger.info(wrapIn.getStrategydeploytitle());

		if (IsPass) {
			//标题不能为空
			if (null == wrapIn.getStrategydeploytitle() || wrapIn.getStrategydeploytitle().isEmpty()) {
				IsPass = false;
				Exception exception = new ExceptionStrategyNameEmpty();
				result.error(exception);
			}
		}

		if (IsPass) {
			//标题不能超过70
			if (wrapIn.getStrategydeploytitle().length() > 70) {
				IsPass = false;
				Exception exception = new ExceptionStrategyNameEmpty();
				result.error(exception);
			}
		}

		if (IsPass) {
			//年份
			if (null == wrapIn.getStrategydeployyear() || wrapIn.getStrategydeployyear().isEmpty()) {
				IsPass = false;
				Exception exception = new ExceptionMeasures("'strategydeployyear' can not be blank!");
				result.error(exception);
			}
		}

		if (IsPass) {
			//序号非空
			if (null == wrapIn.getSequencenumber() || null == wrapIn.getSequencenumber()) {
				IsPass = false;
				Exception e = new Exception("请填写序号");
				result.error(e);
			}
		}

		if (IsPass) {
			//序号合法
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				boolean isExist = false;
				isExist = business.strategyDeployFactory().IsExistById(wrapIn.getId());
				ActionVerifySequenceNumber VerifySequenceNumber = new ActionVerifySequenceNumber();
				//如果是新增，那么校验序号是否被占用。
				//如果是更新，那么1判断更新的序号和老序号是否相同，如果相同，那么通过，否则校验序号是否被占用
				if (isExist) {
					StrategyDeploy o = business.strategyDeployFactory().getById(wrapIn.getId());
					if (wrapIn.getSequencenumber() != o.getSequencenumber()) {
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
				logger.warn("strategydeploy ActionSave update/ get  a error!");
				result.error(e);
			}
		}

		if (IsPass) {
			//如果创建者为空，那么久默认当前用户
			if (null == wrapIn.getStrategydeploycreator() || wrapIn.getStrategydeploycreator().isEmpty()) {
				logger.info("effectivePerson.getDistinguishedName::" + effectivePerson.getDistinguishedName());
				wrapIn.setStrategydeploycreator(effectivePerson.getDistinguishedName());
			}
		}

		//根据创建人员，更新创建人员的组织。（非动态，创建或更新时候的所在组织情况）
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (IsPass) {
				List<String> _unitList = business.organization().unit().listWithPerson(effectivePerson.getDistinguishedName());
				wrapIn.setStrategydeployunit(String.join(",", _unitList));
			}
		} catch (Exception e) {
			logger.warn("strategydeploy ActionSave update/ get  a error!");
			throw e;
		}

		if (IsPass) {
			logger.info("wrapIn:" + wrapIn.getId() + "   " + wrapIn.getStrategydeploycreator());
			//strategydeploy = Wi.copier.copy(wrapIn);
			strategydeploy = strategyDeployOperationService.save(wrapIn);
			logger.info("strategydeploy:" + strategydeploy.getId());
			logger.info(strategydeploy.getId());
			Wo wo = new Wo();
			wo.setId(strategydeploy.getId());
			result.setData(wo);
			//result.setData(wrapOutId);
		}

		return result;
	}
}
