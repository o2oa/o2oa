package com.x.organization.assemble.authentication.jaxrs.dingding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

class ActionLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String code) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// String url =  Config.dingding().getOapiAddress() + "/user/getuserinfo?access_token="
			// 		+ Config.dingding().corpAccessToken() + "&code=" + code;
			// v2 版本 api 地址
		  String url =  Config.dingding().getOapiAddress() + "/topapi/v2/user/getuserinfo?access_token="
					+ Config.dingding().corpAccessToken();
			GetUserInfoWi wi = new GetUserInfoWi();
			wi.setCode(code);
			String value = HttpConnection.postAsString(url, null, wi.toString());
			if (logger.isDebugEnabled()) {
				logger.debug("钉钉单点获取用户 userid  返回 ： {}", value);
			}
			Type type = new TypeToken<DingdingResponse<GetUserInfoWo>> () {}.getType();
			DingdingResponse<GetUserInfoWo> resp = gson.fromJson(value, type);

			if (resp.getErrcode() != 0 ) {
				throw new ExceptionDingding(resp.getErrcode(), resp.getErrmsg());
			}
			if (resp.getResult() == null) {
				throw new ExceptionDingding(-1, " 钉钉返回的 result 为空");
			}
			String userId = resp.getResult().getUserid();
			if (StringUtils.isEmpty(userId)) {
				throw new ExceptionDingding(-1, "钉钉返回的 userId 为空");
			}
			Business business = new Business(emc);
			String personId = business.person().getWithCredential(userId);
			if (StringUtils.isEmpty(personId)) {
				throw new ExceptionPersonNotExist(userId);
			}
			Person person = emc.find(personId, Person.class);
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


	public static class GetUserInfoWo extends GsonPropertyObject {

		private static final long serialVersionUID = -6591396003538955999L;
		private String userid;
		private String device_id; // 
		private Boolean sys; // 是否管理员
		private Integer sys_level; // 级别。 1：主管理员 2：子管理员 100：老板 0：其他（如普通员工）
		private String associated_unionid; // 用户关联的unionId。
		private String unionid; // 用户unionId。
		private String name; // 用户名字
    public String getUserid() {
      return userid;
    }
    public void setUserid(String userid) {
      this.userid = userid;
    }
    public String getDevice_id() {
      return device_id;
    }
    public void setDevice_id(String device_id) {
      this.device_id = device_id;
    }
    public Boolean getSys() {
      return sys;
    }
    public void setSys(Boolean sys) {
      this.sys = sys;
    }
    public Integer getSys_level() {
      return sys_level;
    }
    public void setSys_level(Integer sys_level) {
      this.sys_level = sys_level;
    }
    public String getAssociated_unionid() {
      return associated_unionid;
    }
    public void setAssociated_unionid(String associated_unionid) {
      this.associated_unionid = associated_unionid;
    }
    public String getUnionid() {
      return unionid;
    }
    public void setUnionid(String unionid) {
      this.unionid = unionid;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }

		
	}

	public static class GetUserInfoWi extends GsonPropertyObject {
		private static final long serialVersionUID = -3438949020059197043L;
		private String code;

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

		
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

}
