package o2.collect.assemble.jaxrs.unit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;

import o2.base.core.project.config.Config;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Unit;

class ActionUpdateWithCode extends BaseAction {

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String name, String mobile, String code, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(mobile)) {
				throw new ExceptionMobileEmpty();
			}
			if (!StringTools.isMobile(mobile)) {
				throw new ExceptionMobileInvalid(mobile);
			}
			Unit unit = this.unitExist(business, name, null);
			if (null == unit) {
				throw new ExceptionUnitNotExist(name);
			}
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionNameEmpty();
			}
			if (null != this.unitExist(business, wi.getName(), unit.getId())) {
				throw new ExceptionUnitNameExist(wi.getName());
			}
			for (String str : wi.getControllerMobileList()) {
				if (!StringTools.isMobile(str)) {
					throw new ExceptionMobileInvalid(str);
				}
			}
			if (effectivePerson.isNotManager()) {
				if (!ListTools.contains(unit.getControllerMobileList(), mobile)) {
					throw new ExceptionMobileNotInControllerMobileList(mobile);
				}
				if (!business.validateCode(mobile, code, null, true)) {
					throw new ExceptionInvalidCode();
				}
			}
			Wi.copier.copy(wi, unit);
			emc.beginTransaction(Unit.class);
			emc.check(unit, CheckPersistType.all);
			emc.commit();
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

		private static final long serialVersionUID = -4855784604415258419L;

		static WrapCopier<Wi, Unit> copier = WrapCopierFactory.wi(Wi.class, Unit.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, JpaObject.password_FIELDNAME));

	}

	public static class Wo extends WrapBoolean {
	}
}
