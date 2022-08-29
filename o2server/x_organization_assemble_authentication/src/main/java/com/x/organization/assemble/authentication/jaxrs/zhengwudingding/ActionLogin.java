package com.x.organization.assemble.authentication.jaxrs.zhengwudingding;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ZhengwuDingding;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

class ActionLogin extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String code) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			LOGGER.debug("execute:{}, code:{}.", effectivePerson::getDistinguishedName, () -> code);
			String dingUserId = this.getDingUserId(code);
			String userId = this.getUserIdByDingUserId(dingUserId);
			Business business = new Business(emc);
			Person person = this.get(business, userId);
			if (null == person) {
				throw new ExceptionCanNotFindPerson(userId);
			}
			Wo wo = Wo.copier.copy(person);
			List<String> roles = business.organization().role().listWithPerson(person.getDistinguishedName());
			wo.setRoleList(roles);
			EffectivePerson effective = new EffectivePerson(wo.getDistinguishedName(), TokenType.user,
					Config.token().getCipher(), Config.person().getEncryptType());
			wo.setToken(effective.getToken());
			HttpToken httpToken = new HttpToken();
			httpToken.setToken(request, response, effective);
			result.setData(wo);
		}
		return result;
	}

	private String getDingUserId(String code) throws Exception {
		String address = ZhengwuDingding.default_oapiAddress + "/user/getuserinfo?access_token="
				+ Config.zhengwuDingding().corpAccessToken() + "&code=" + code;
		GetDingUserIdResp resp = HttpConnection.getAsObject(address, null, GetDingUserIdResp.class);
		if (resp.getErrcode() != 0) {
			throw new ExceptionGetDingUserId(resp.getErrcode(), resp.getErrmsg());
		}
		return resp.getUserid();
	}

	private String getUserIdByDingUserId(String dingId) throws Exception {
		String address = Config.zhengwuDingding().getOapiAddress() + "/user/singleGetUserIdByDingId?access_token="
				+ Config.zhengwuDingding().appAccessToken() + "&dingUserId=" + dingId;
		GetUserIdByDingUserIdResp resp = HttpConnection.postAsObject(address, null, null,
				GetUserIdByDingUserIdResp.class);
		if (resp.getRetCode() != 0) {
			throw new ExceptionGetDingUserId(resp.getRetCode(), resp.getRetMessage());
		}
		return resp.getRetData();
	}

	public static class Wo extends Person {

		private static final long serialVersionUID = 4901269474728548509L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static {
			Excludes.add("password");
		}

		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class, null, Excludes);

		private String token;
		private List<String> roleList;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}
	}

	public static class GetDingUserIdResp extends GsonPropertyObject {

		private Integer errcode;
		private String errmsg;
		private String userid;

		public String getUserid() {
			return userid;
		}

		public void setUserid(String userid) {
			this.userid = userid;
		}

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

	}

	public static class GetUserIdByDingUserIdResp extends GsonPropertyObject {

		private Integer retCode;
		private String retMessage;
		private String retData;

		public Integer getRetCode() {
			return retCode;
		}

		public void setRetCode(Integer retCode) {
			this.retCode = retCode;
		}

		public String getRetMessage() {
			return retMessage;
		}

		public void setRetMessage(String retMessage) {
			this.retMessage = retMessage;
		}

		public String getRetData() {
			return retData;
		}

		public void setRetData(String retData) {
			this.retData = retData;
		}

	}

	public Person get(Business business, String userId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.zhengwuDingdingId), userId);
		cq.select(root).where(p);
		List<Person> list = em.createQuery(cq).getResultList();
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

}
