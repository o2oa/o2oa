package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wx.pwd.CheckStrength;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.StringTools;
import com.x.organization.assemble.personal.Business;
import com.x.organization.assemble.personal.wrapin.WrapInRegist;
import com.x.organization.core.entity.GenderType;
import com.x.organization.core.entity.Person;

class ActionCreate extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<WrapOutBoolean> execute(WrapInRegist wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutBoolean> result = new ActionResult<>();
			WrapOutBoolean wrap = new WrapOutBoolean();
			Business business = new Business(emc);
			String name = wrapIn.getName();
			String password = wrapIn.getPassword();
			String mobile = wrapIn.getMobile();
			GenderType genderType = wrapIn.getGenderType();
			String codeAnswer = wrapIn.getCodeAnswer();
			String captcha = wrapIn.getCaptcha();
			String captchaAnswer = wrapIn.getCaptchaAnswer();
			if (StringUtils.equals(com.x.base.core.project.server.Person.REGISTER_TYPE_DISABLE,
					Config.person().getRegister())) {
				throw new DisableRegistException();
			}
			if (StringUtils.isEmpty(name) || (!StringTools.isSimply(name))) {
				throw new InvalidNameException(name);
			}
			if (this.nameExisted(emc, name)) {
				throw new NameExistedException(name);
			}
			if (!StringTools.isMobile(mobile)) {
				throw new InvalidMobileException(mobile);
			}
			if (this.mobileExisted(emc, mobile)) {
				throw new MobileExistedException(mobile);
			}
			if (CheckStrength.checkPasswordStrength(password) < 4) {
				throw new InvalidPasswordException();
			}
			if (null == genderType) {
				throw new InvalidGenderTypeException();
			}
			if (StringUtils.equals(com.x.base.core.project.server.Person.REGISTER_TYPE_CODE,
					Config.person().getRegister())) {
				if (BooleanUtils.isNotTrue(business.instrument().code().validate(mobile, codeAnswer))) {
					throw new InvalidCodeException();
				}
			}
			if (StringUtils.equals(com.x.base.core.project.server.Person.REGISTER_TYPE_CAPTCHA,
					Config.person().getRegister())) {
				if (!business.instrument().captcha().validate(captcha, captchaAnswer)) {
					throw new InvalidCaptchaException();
				}
			}
			this.register(business, name, password, genderType, mobile);
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

	private void register(Business business, String name, String password, GenderType genderType, String mobile)
			throws Exception {
		Person o = new Person();
		o.setName(name);
		business.person().setPassword(o, password);
		o.setGenderType(genderType);
		o.setMobile(mobile);
		business.entityManagerContainer().beginTransaction(Person.class);
		business.entityManagerContainer().persist(o, CheckPersistType.all);
		business.entityManagerContainer().commit();
	}

}