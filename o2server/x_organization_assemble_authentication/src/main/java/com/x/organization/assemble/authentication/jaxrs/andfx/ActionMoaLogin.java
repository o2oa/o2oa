package com.x.organization.assemble.authentication.jaxrs.andfx;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.*;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

class ActionMoaLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionMoaLogin.class);

	private static final String SUCCESS_CODE = "103000";

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String token, String enterId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		logger.info("enterId:{}", enterId);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			String url =  Config.andFx().getSsoApi();
			Map<String, Object> map = new TreeMap<>();
			map.put("version", "1.0");
			map.put("id", Config.andFx().getSourceId());
			map.put("idtype", "0");
			map.put("msgid", System.currentTimeMillis()+StringTools.randomNumber4());
			map.put("token", token);
			map.put("systemtime", DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmssSSS));
			map.put("key", Config.andFx().getSourceKey());
			map.put("apptype", "3");
			String enStr = MapTools.mapToString(map);
			String sign = MD5Tool.md5(enStr);

			Req req = this.joinRes(map, sign);
			String value = HttpConnection.postAsString(url, null, gson.toJson(req));
			logger.info("andfx sso token:{} resp:{}", token, value);
			Resp resp = gson.fromJson(value, Resp.class);

			if (!SUCCESS_CODE.equals(resp.getHeader().getResultcode())) {
				throw new ExceptionAndFx(resp.getHeader().getResultcode(), resp.getHeader().resultdesc);
			}

			String mobile = Crypto.decodeAES(resp.getBody().getMsisdn(), Config.andFx().getSourceKey());
			Business business = new Business(emc);
			String personId = business.person().getWithCredential(mobile);
			if (StringUtils.isEmpty(personId)) {
				throw new ExceptionPersonNotExist(mobile);
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
			logger.info("用户：{},认证凭据：{}认证成功");
			result.setData(wo);
		}
		return result;
	}

	private Req joinRes(Map<String, Object> map, String sign){
		Req req = new Req();
		ReqBody body = new ReqBody();
		body.setToken(String.valueOf(map.get("token")));
		req.setBody(body);
		ReqHeader header = new ReqHeader();
		header.setApptype(String.valueOf(map.get("apptype")));
		header.setId(String.valueOf(map.get("id")));
		header.setIdtype(String.valueOf(map.get("idtype")));
		header.setMsgid(String.valueOf(map.get("msgid")));
		header.setSign(sign);
		header.setSystemtime(String.valueOf(map.get("systemtime")));
		header.setVersion(String.valueOf(map.get("version")));
		req.setHeader(header);
		return req;
	}

	public static class Req {
		private ReqHeader header;
		private ReqBody body;

		public ReqHeader getHeader() {
			return header;
		}

		public void setHeader(ReqHeader header) {
			this.header = header;
		}

		public ReqBody getBody() {
			return body;
		}

		public void setBody(ReqBody body) {
			this.body = body;
		}
	}

	public static class ReqHeader {
		private String version;
		private String msgid;
		private String systemtime;
		private String id;
		private String idtype;
		private String apptype;
		private String sign;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getMsgid() {
			return msgid;
		}

		public void setMsgid(String msgid) {
			this.msgid = msgid;
		}

		public String getSystemtime() {
			return systemtime;
		}

		public void setSystemtime(String systemtime) {
			this.systemtime = systemtime;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getIdtype() {
			return idtype;
		}

		public void setIdtype(String idtype) {
			this.idtype = idtype;
		}

		public String getApptype() {
			return apptype;
		}

		public void setApptype(String apptype) {
			this.apptype = apptype;
		}

		public String getSign() {
			return sign;
		}

		public void setSign(String sign) {
			this.sign = sign;
		}
	}

	public static class ReqBody {
		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}

	public static class Resp {
		private RespBody body;
		private RespHeader header;


		public RespBody getBody() {
			return body;
		}

		public void setBody(RespBody body) {
			this.body = body;
		}

		public RespHeader getHeader() {
			return header;
		}

		public void setHeader(RespHeader header) {
			this.header = header;
		}
	}

	public static class RespBody {
		private String usessionid;
		private String msisdnmask;
		private String msisdn;

		public String getUsessionid() {
			return usessionid;
		}

		public void setUsessionid(String usessionid) {
			this.usessionid = usessionid;
		}

		public String getMsisdnmask() {
			return msisdnmask;
		}

		public void setMsisdnmask(String msisdnmask) {
			this.msisdnmask = msisdnmask;
		}

		public String getMsisdn() {
			return msisdn;
		}

		public void setMsisdn(String msisdn) {
			this.msisdn = msisdn;
		}
	}

	public static class RespHeader {
		private String resultcode;
		private String resultdesc;

		public String getResultcode() {
			return resultcode;
		}

		public void setResultcode(String resultcode) {
			this.resultcode = resultcode;
		}

		public String getResultdesc() {
			return resultdesc;
		}

		public void setResultdesc(String resultdesc) {
			this.resultdesc = resultdesc;
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
