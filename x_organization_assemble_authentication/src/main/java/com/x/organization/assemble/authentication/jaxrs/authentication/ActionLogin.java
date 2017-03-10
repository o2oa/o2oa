package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.Crypto;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.wrap.in.WrapInAuthentication;
import com.x.organization.assemble.authentication.wrap.out.WrapOutAuthentication;
import com.x.organization.core.entity.Person;

class ActionLogin extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionLogin.class);

	ActionResult<WrapOutAuthentication> execute(HttpServletRequest request, HttpServletResponse response,
			WrapInAuthentication wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutAuthentication> result = new ActionResult<>();
			Business business = new Business(emc);
			WrapOutAuthentication wrap = new WrapOutAuthentication();
			String credential = wrapIn.getCredential();
			logger.debug("user:{}, try to login.", credential);
			String password = wrapIn.getPassword();
			if (StringUtils.isEmpty(credential)) {
				throw new CredentialEmptyException();
			}
			if (StringUtils.isEmpty(password)) {
				throw new PasswordEmptyException();
			}
			if (Config.token().isInitialManager(credential)) {
				if (!StringUtils.equals(Config.token().getPassword(), password)) {
					throw new InvalidPasswordException();
				}
				wrap = this.manager(request, response, business);
			} else {
				/* 普通用户登录,也有可能拥有管理员角色 */
				String personId = business.person().getWithCredential(credential);
				if (StringUtils.isEmpty(personId)) {
					throw new PersonNotExistedException(credential);
				}
				Person o = emc.find(personId, Person.class);
				/* 先判断是否使用superPermission登录 */
				if (BooleanUtils.isTrue(Config.person().getSuperPermission())
						&& StringUtils.equals(Config.token().getPassword(), password)) {
					logger.warn("user: {} use superPermission.", credential);
				} else if (!StringUtils.equals(Crypto.encrypt(password, Config.token().getKey()), o.getPassword())) {
					/* 普通用户认证密码 */
					throw new InvalidPasswordException();
				}
				wrap = this.user(request, response, business, o);
			}
			result.setData(wrap);
			return result;
		}
	}
}