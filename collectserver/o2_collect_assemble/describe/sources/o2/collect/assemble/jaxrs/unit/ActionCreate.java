package o2.collect.assemble.jaxrs.unit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.wx.pwd.CheckStrength;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;

import o2.base.core.project.config.Config;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Unit;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionNameEmpty();
			}
			if (StringUtils.isEmpty(wi.getPassword())) {
				throw new ExceptionPasswordEmpty();
			}
			if (StringUtils.isEmpty(wi.getMobile())) {
				throw new ExceptionMobileEmpty();
			}
			if (!StringTools.isMobile(wi.getMobile())) {
				throw new ExceptionMobileInvalid(wi.getMobile());
			}
			if (null != unitExist(business, wi.getName(), null)) {
				throw new ExceptionNameExist(wi.getName());
			}
			if (effectivePerson.isNotManager()) {
				/* 管理员可以跳过这部分验证 */
				if (CheckStrength.checkPasswordStrength(wi.getPassword()) < 4) {
					throw new ExceptionInvalidPassword();
				}
				if (StringUtils.isEmpty(wi.getCodeAnswer())) {
					throw new ExceptionCodeAnswerEmpty();
				}
				if (!business.validateCode(wi.getMobile(), wi.getCodeAnswer(), null, true)) {
					throw new ExceptionInvalidCode();
				}
			}
			Unit unit = new Unit();
			Wi.copier.copy(wi, unit);
			unit.setPassword(Crypto.encrypt(wi.getPassword(), Config.token().getKey()));
			if (StringUtils.isNotEmpty(wi.getMobile())) {
				unit.setControllerMobileList(ListTools.toList(wi.getMobile()));
			}
			business.entityManagerContainer().beginTransaction(Unit.class);
			business.entityManagerContainer().persist(unit, CheckPersistType.all);
			business.entityManagerContainer().commit();
			if (effectivePerson.isNotManager()) {
				HttpToken httpToken = new HttpToken();
				EffectivePerson person = new EffectivePerson(unit.getName(), TokenType.user,
						Config.token().getCipher());
				httpToken.setToken(request, response, person);
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Unit {

		private static final long serialVersionUID = -7839216278338852396L;

		static WrapCopier<Wi, Unit> copier = WrapCopierFactory.wi(Wi.class, Unit.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Unit.pinyin_FIELDNAME, Unit.pinyinInitial_FIELDNAME));

		@FieldDescribe("手机号码")
		private String mobile;

		@FieldDescribe("验证码答案")
		private String codeAnswer;

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getCodeAnswer() {
			return codeAnswer;
		}

		public void setCodeAnswer(String codeAnswer) {
			this.codeAnswer = codeAnswer;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}
