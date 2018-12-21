package com.x.strategydeploy.assemble.control.keywork;

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
import com.x.strategydeploy.assemble.control.keywork.exception.ExceptionKeyWorkInfoIdEmpty;
import com.x.strategydeploy.assemble.control.keywork.exception.ExceptionKeyWorkInfoTitleEmpty;
import com.x.strategydeploy.assemble.control.keywork.exception.ExceptionWrapInConvert;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class ActionSave extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	//出
	public static class Wo extends WoId {
	}

	//执行
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		KeyworkInfo keyworkinfo = new KeyworkInfo();
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
			if (null == wrapIn.getKeyworktitle() || wrapIn.getKeyworktitle().isEmpty()) {
				IsPass = false;
				Exception exception = new ExceptionKeyWorkInfoIdEmpty();
				result.error(exception);
			}
		}

		if (IsPass) {
			if (wrapIn.getKeyworktitle().length() > 70) {
				IsPass = false;
				Exception exception = new ExceptionKeyWorkInfoTitleEmpty();
				result.error(exception);
			}
		}

		if (IsPass) {
			if (null == wrapIn.getKeyworkyear() || wrapIn.getKeyworkyear().isEmpty()) {
				IsPass = false;
				Exception exception = new Exception("'keyworkyear' can not be blank!");
				result.error(exception);
			}
		}

		if (IsPass) {
			if (null == wrapIn.getKeyworkunit() || wrapIn.getKeyworkunit().isEmpty()) {
				IsPass = false;
				Exception exception = new Exception("'keyworkunit' can not be blank!");
				result.error(exception);
			}

		}

		if (IsPass) {
			//序号非空
			if (null == wrapIn.getSequencenumber() || wrapIn.getSequencenumber() <= 0) {
				IsPass = false;
				Exception e = new Exception("请填写序号,序号为从1开始的正整数.");
				result.error(e);
			}
		}
		
		if (IsPass) {
			//序号合法
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				boolean isExist = false;
				isExist = business.keyworkInfoFactory().IsExistById(wrapIn.getId());
				ActionVerifySequenceNumber VerifySequenceNumber = new ActionVerifySequenceNumber();
				//如果是新增，那么校验序号是否被占用。
				//如果是更新，那么1判断更新的序号和老序号是否相同，如果相同，那么通过，否则校验序号是否被占用
				if (isExist) {
					KeyworkInfo o = business.keyworkInfoFactory().getById(wrapIn.getId());
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
				logger.warn("KeyworkInfo ActionSave update/ get  a error!");
				result.error(e);
			}
		}		

		if (IsPass) {
			//如果创建者为空，那么久默认当前用户
			if (null == wrapIn.getKeyworkcreator() || wrapIn.getKeyworkcreator().isEmpty()) {
				logger.info("effectivePerson.getDistinguishedName::" + effectivePerson.getDistinguishedName());
				wrapIn.setKeyworkcreator(effectivePerson.getDistinguishedName());
			}
		}

		if (IsPass) {
			keyworkinfo = this.convertToWrapIn(jsonElement, KeyworkInfo.class);
			keyworkinfo = keyWorkOperationService.save(wrapIn);
			Wo wo = new Wo();
			wo.setId(keyworkinfo.getId());
			result.setData(wo);
			//result.setData(wrapOutId);
		} else {
			logger.info("not check pass !!!");
		}

		return result;
	}
}
