package com.x.organization.assemble.authentication.jaxrs.sso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.Crypto;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.TokenType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

@Path("sso")
public class SsoAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "使用token进行Sso登陆。格式加密后的unique#1970年毫秒数", response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpServletRequest request, @Context HttpServletResponse response,
			WrapInSso wrapIn) {
		ActionResult<WrapOutSso> result = new ActionResult<>();
		WrapOutSso wrap = new WrapOutSso();
		try {
			String token = wrapIn.getToken();
			if (StringUtils.isEmpty(token)) {
				throw new Exception("token is empty.");
			}
			String content = null;
			try {
				byte[] bs = Crypto.decrypt(Base64.decodeBase64(token), Config.token().getSso().getBytes());
				content = new String(bs, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("can not read token.");
			}
			if (StringUtils.isEmpty(content)) {
				throw new Exception("read empty token.");
			}
			String unique = StringUtils.substringBefore(content, "#");
			String timeString = StringUtils.substringAfter(content, "#");
			if (StringUtils.isEmpty(unique)) {
				throw new Exception("read empty token.");
			}
			Date date = new Date(Long.parseLong(timeString));
			Date now = new Date();
			if (Math.abs((now.getTime() - date.getTime())) >= (60000 * 60 * 12)) {
				throw new Exception("token is long time ago.");
			}
			if (StringUtils.equalsIgnoreCase(unique, Config.administrator().getName())) {
				throw new Exception("can not sso admin.");
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String personId = business.person().getWithUnique(unique);
				if (StringUtils.isEmpty(personId)) {
					throw new Exception("person{unique:" + unique + "} not exist.");
				}
				Person person = emc.find(personId, Person.class);
				person.copyTo(wrap);
				List<String> roles = new ArrayList<>();
				for (Role o : emc.fetchAttribute(business.role().listWithPerson(person.getId()), Role.class, "name")) {
					roles.add(o.getName());
				}
				wrap.setRoleList(roles);
				wrap.setAuthentication(true);
			}
			HttpToken httpToken = new HttpToken();
			EffectivePerson effectivePerson = new EffectivePerson(wrap.getName(), TokenType.user,
					Config.token().getCipher());
			httpToken.setToken(request, response, effectivePerson);
			wrap.setToken(effectivePerson.getToken());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}