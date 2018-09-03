package o2.collect.assemble.jaxrs.unit;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.StringTools;

import o2.base.core.project.config.Config;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Account_;
import o2.collect.core.entity.Unit;

class ActionListWithAccountName extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithAccountName.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String mobile, String code) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (StringUtils.isEmpty(mobile)) {
				throw new ExceptionMobileEmpty();
			}
			Business business = new Business(emc);
			if (effectivePerson.isNotManager()) {
				/* 管理员可以跳过这部分检查 */
				/* 不删除短信验证码,后面在device bind 的时候再用一次 */
				if (!business.validateCode(mobile, code, null, false)) {
					throw new ExceptionInvalidCode();
				}
			}
			List<Wo> wos = Wo.copier.copy(this.listWithAccount(business, mobile));
			/* 没有找到任何组织,那么在demo中进行注册 */
			if (wos.isEmpty()) {
				/* 如果启用了演示注册 */
				if (BooleanUtils.isTrue(Config.demoSite().getRegistEnable())) {
					logger.print("注册用户 {} 到演示服务器.", mobile);
					String token = this.ssoLogin();
					this.registPerson(token, mobile);
					this.createIdentity(token, mobile);
					Unit unit = emc.flag(Config.demoSite().getName(), Unit.class);
					if (null != unit) {
						/** 将当前账号注入到demo */
						emc.beginTransaction(Account.class);
						Account account = new Account();
						account.setName(mobile);
						account.setUnit(unit.getId());
						emc.persist(account, CheckPersistType.all);
						emc.commit();
						wos.add(Wo.copier.copy(unit));
					}
				}
			} else if (wos.size() > 1) {
				/* 有两个以上的的站点,过滤掉demo */
				wos = wos.stream().filter(o -> {
					return !StringUtils.equals("演示站点", o.getName());
				}).collect(Collectors.toList());

			}
			if (wos.isEmpty()) {
				logger.print("用户 {} 找不到任何组织.", mobile);
			}
			wos = wos.stream().sorted(Comparator.comparing(Wo::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}

	}

	private String ssoLogin() throws Exception {
		String address = StringTools.url(Config.demoSite().getSite(), "x_organization_assemble_authentication", "jaxrs",
				"sso");
		SsoReq ssoReq = new SsoReq();
		ssoReq.setClient(Config.demoSite().getSsoClient());
		String token = URLEncoder.encode(Config.demoSite().getSsoPerson(), "UTF-8") + "#" + (new Date()).getTime();
		token = Crypto.encrypt(token, Config.demoSite().getSsoKey());
		ssoReq.setToken(token);
		String respValue = HttpConnection.postAsString(address, null, ssoReq.toString());
		SsoResp ssoResp = gson.fromJson(respValue, SsoResp.class);
		if ((null != ssoResp) && (null != ssoResp.getData())) {
			return ssoResp.getData().getToken();
		}
		throw new ExceptionSso();
	}

	private void registPerson(String token, String mobile) throws Exception {
		String address = StringTools.url(Config.demoSite().getSite(), "x_organization_assemble_control", "jaxrs",
				"person");
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(new NameValuePair(HttpToken.X_Token, token));
		RegistPersonReq registPersonReq = new RegistPersonReq();
		registPersonReq.setGenderType(GenderType.d);
		registPersonReq.setMobile(mobile);
		registPersonReq.setName("DEMO" + mobile);
		registPersonReq.setEmployee("EMPLOYEE" + mobile);
		HttpConnection.postAsString(address, heads, registPersonReq.toString());

	}

	private void createIdentity(String token, String mobile) throws Exception {
		String address = StringTools.url(Config.demoSite().getSite(), "x_organization_assemble_control", "jaxrs",
				"identity");
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(new NameValuePair(HttpToken.X_Token, token));
		CreateIdentityReq createIdentityReq = new CreateIdentityReq();
		createIdentityReq.setName("DEMO" + mobile);
		createIdentityReq.setPerson("DEMO" + mobile);
		createIdentityReq.setUnit(Config.demoSite().getRegistUnit());
		HttpConnection.postAsString(address, heads, createIdentityReq.toString());
	}

	private List<Unit> listWithAccount(Business business, String mobile) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Account> root = cq.from(Account.class);
		Predicate p = cb.equal(root.get(Account_.name), mobile);
		cq.select(root.get(Account_.unit)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return business.entityManagerContainer().list(Unit.class, list);
	}

	public static class Wo extends Unit {

		private static final long serialVersionUID = -4855784604415258419L;

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class RegistPersonReq extends GsonPropertyObject {

		private String name;

		private String mobile;

		private String employee;

		private GenderType genderType;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public GenderType getGenderType() {
			return genderType;
		}

		public void setGenderType(GenderType genderType) {
			this.genderType = genderType;
		}

		public String getEmployee() {
			return employee;
		}

		public void setEmployee(String employee) {
			this.employee = employee;
		}
	}

	public static class CreateIdentityReq extends GsonPropertyObject {

		private String name;

		private String person;

		private String unit;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class SsoReq extends GsonPropertyObject {

		private String client;

		private String token;

		public String getClient() {
			return client;
		}

		public void setClient(String client) {
			this.client = client;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

	}

	public static class SsoResp extends GsonPropertyObject {

		private Data data;

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public static class Data {
			private String token;

			public String getToken() {
				return token;
			}

			public void setToken(String token) {
				this.token = token;
			}
		}

	}

}