package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.StringTools;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.wrapin.WrapInAuthentication;
import com.x.organization.assemble.authentication.wrapout.WrapOutAuthentication;
import com.x.organization.core.entity.Person;

class ActionCodeLogin extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCodeLogin.class);

	ActionResult<WrapOutAuthentication> execute(HttpServletRequest request, HttpServletResponse response,
			WrapInAuthentication wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutAuthentication> result = new ActionResult<>();
			Business business = new Business(emc);
			WrapOutAuthentication wrap = new WrapOutAuthentication();
			String credential = wrapIn.getCredential();
			String codeAnswer = wrapIn.getCodeAnswer();
			if (StringUtils.isEmpty(credential)) {
				throw new CredentialEmptyException();
			}
			if (StringUtils.isEmpty(codeAnswer)) {
				throw new CodeEmptyException();
			}
			if (Config.token().isInitialManager(credential)) {
				if (!StringUtils.equals(Config.token().getPassword(), codeAnswer)) {
					throw new InvalidPasswordException();
				}
				wrap = this.manager(request, response, business);
			} else {
				/* 普通用户登录,也有可能拥有管理员角色 */
				String id = business.person().getWithCredential(credential);
				if (StringUtils.isEmpty(id)) {
					throw new PersonNotExistedException(credential);
				}
				Person o = emc.find(id, Person.class);
				if (BooleanUtils.isTrue(Config.person().getSuperPermission())
						&& StringUtils.equals(Config.token().getPassword(), codeAnswer)) {
					/* 如果是管理员密码就直接登录 */
					logger.warn("user: {} use superPermission.", credential);
				} else {
					/* 普通用户登录 */
					if (!StringTools.isMobile(o.getMobile())) {
						throw new InvalidMobileException(o.getMobile());
					}
					if (!business.instrument().code().validate(o.getMobile(), codeAnswer)) {
						throw new InvalidCodeException();
					}
				}
				wrap = this.user(request, response, business, o);
			}
			result.setData(wrap);
			return result;
		}
	}

}