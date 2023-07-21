package com.x.message.assemble.communicate.jaxrs.mass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.message.assemble.communicate.message.QiyeweixinTextMessage;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.WeLink;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.message.assemble.communicate.Business;
import com.x.message.assemble.communicate.message.DingdingMessage;
import com.x.message.assemble.communicate.message.WeLinkMessage;
import com.x.message.assemble.communicate.message.ZhengwuDingdingMessage;
import com.x.message.core.entity.Mass;
import com.x.organization.core.entity.Person;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.Manager, OrganizationDefinition.MessageManager))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (ListTools.isEmpty(wi.getPersonList()) && ListTools.isEmpty(wi.getIdentityList())
					&& ListTools.isEmpty(wi.getGroupList()) && ListTools.isEmpty(wi.getUnitList())) {
				throw new ExceptionEmptyTarget();
			}
			if (StringUtils.isEmpty(wi.getBody())) {
				throw new ExceptionEmptyBody();
			}
			if ((!Config.qiyeweixin().getEnable()) && (!Config.dingding().getEnable())
					&& (!Config.zhengwuDingding().getEnable()) && (!Config.weLink().getEnable())) {
				throw new ExceptionDisable();
			}
			Mass mass = Wi.copier.copy(wi);
			mass.setCreatorPerson(effectivePerson.getDistinguishedName());
			List<String> people = business.organization().person().list(mass.getPersonList());
			people.addAll(business.organization().person().listWithIdentity(mass.getIdentityList()));
			/* 群组包含直接成员和递归成员 */
			people.addAll(business.organization().person().listWithUnitSubDirect(mass.getUnitList()));
			people.addAll(business.organization().person().listWithUnitSubNested(mass.getUnitList()));
			people.addAll(business.organization().person().listWithGroup(mass.getGroupList()));
			mass.setSendPersonList(ListTools.trim(people, true, true));
			List<List<String>> list = ListTools.batch(mass.getSendPersonList(), 500);
			if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())) {
				this.qiyeweixin(business, mass.getBody(), list);
				mass.setType(Mass.TYPE_QIYEWEIXIN);
			} else if (BooleanUtils.isTrue(Config.dingding().getEnable())) {
				this.dingding(business, mass.getBody(), list);
				mass.setType(Mass.TYPE_DINGDING);
			} else if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())) {
				this.zhengwuDingding(business, mass.getBody(), list);
				mass.setType(Mass.TYPE_ZHENGWUDINGDING);
			} else if (BooleanUtils.isTrue(Config.weLink().getEnable())
					&& BooleanUtils.isTrue(Config.weLink().getMessageEnable())) {
				this.welink(business, mass.getBody(), list);
				mass.setType(Mass.TYPE_WELINK);
			}
			emc.beginTransaction(Mass.class);
			emc.persist(mass, CheckPersistType.all);
			emc.commit();
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = Wo.copier.copy(mass);
			result.setData(wo);
			return result;
		}
	}

	private void qiyeweixin(Business business, String body, List<List<String>> list) throws Exception {
		for (List<String> os : list) {
			List<String> ids = ListTools.extractProperty(business.organization().person().listObject(os),
					Person.qiyeweixinId_FIELDNAME, String.class, true, true);
			QiyeweixinTextMessage m = new QiyeweixinTextMessage();
			m.setAgentid(Long.parseLong(Config.qiyeweixin().getAgentId(), 10));
			m.setTouser(StringUtils.join(ids, "|"));
			m.getText().setContent(body);
			String address = Config.qiyeweixin().getApiAddress() + "/cgi-bin/message/send?access_token="
					+ Config.qiyeweixin().corpAccessToken();
			QiyeweixinMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
					QiyeweixinMessageResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionQiyeweixinMessage(resp.getErrcode(), resp.getErrmsg());
			}
		}
	}

	private void dingding(Business business, String body, List<List<String>> list) throws Exception {
		for (List<String> os : list) {
			List<String> ids = ListTools.extractProperty(business.organization().person().listObject(os),
					Person.dingdingId_FIELDNAME, String.class, true, true);
			DingdingMessage m = new DingdingMessage();
			m.setAgent_id(Long.parseLong(Config.dingding().getAgentId(), 10));
			m.setUserid_list(StringUtils.join(ids, ","));
			m.getMsg().getText().setContent(body);
			String address = Config.dingding().getOapiAddress()
					+ "/topapi/message/corpconversation/asyncsend_v2?access_token="
					+ Config.dingding().corpAccessToken();
			DingdingMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
					DingdingMessageResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionDingdingMessage(resp.getErrcode(), resp.getErrmsg());
			}
		}
	}

	private void zhengwuDingding(Business business, String body, List<List<String>> list) throws Exception {
		for (List<String> os : list) {
			List<String> ids = ListTools.extractProperty(business.organization().person().listObject(os),
					Person.zhengwuDingdingId_FIELDNAME, String.class, true, true);
			ZhengwuDingdingMessage m = new ZhengwuDingdingMessage();
			m.setAgentId(Long.parseLong(Config.zhengwuDingding().getAgentId(), 10));
			m.setTouser(StringUtils.join(ids, ","));
			m.getMsg().getText().setContent(body);
			String address = Config.zhengwuDingding().getOapiAddress() + "/ent_message/send?access_token="
					+ Config.zhengwuDingding().appAccessToken();
			ZhengwuDingdingMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
					ZhengwuDingdingMessageResp.class);
			if (resp.getRetCode() != 0) {
				throw new ExceptionZhengwuDingdingMessage(resp.getRetCode(), resp.getRetMessage());
			}
		}
	}

	private void welink(Business business, String body, List<List<String>> list) throws Exception {
		for (List<String> os : list) {
			List<String> ids = ListTools.extractProperty(business.organization().person().listObject(os),
					Person.weLinkId_FIELDNAME, String.class, true, true);
			WeLinkMessage m = new WeLinkMessage();
			m.setToUserList(ids);
			m.setMsgRange("0");
			m.setMsgTitle("消息");
			m.setMsgContent(body);
			Date now = new Date();
			m.setCreateTime(now.getTime() + "");
			LOGGER.info("welink send body: " + m.toString());
			String address = Config.weLink().getOapiAddress() + "/messages/v3/send";
			LOGGER.info("welink send url: " + address);
			List<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair(WeLink.WeLink_Auth_Head_Key, Config.weLink().accessToken()));
			WeLinkMessageResp resp = HttpConnection.postAsObject(address, heads, m.toString(), WeLinkMessageResp.class);
			if (!"0".equals(resp.getCode())) {
				throw new ExceptionWeLinkMessage(resp.getCode(), resp.getMessage());
			}
		}

	}

	public static class WeLinkMessageResp {

		private String code;
		private String message;
		private List<String> failedUserId;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public List<String> getFailedUserId() {
			return failedUserId;
		}

		public void setFailedUserId(List<String> failedUserId) {
			this.failedUserId = failedUserId;
		}
	}

	public static class ZhengwuDingdingMessageResp {

		private Integer retCode;
		private String retMessage;
		private RetData retData;

		public static class RetData {
			private String invaliduser;
			private String invalidparty;
			private String errorparty;
			private String erroruser;

			public String getInvaliduser() {
				return invaliduser;
			}

			public void setInvaliduser(String invaliduser) {
				this.invaliduser = invaliduser;
			}

			public String getInvalidparty() {
				return invalidparty;
			}

			public void setInvalidparty(String invalidparty) {
				this.invalidparty = invalidparty;
			}

			public String getErrorparty() {
				return errorparty;
			}

			public void setErrorparty(String errorparty) {
				this.errorparty = errorparty;
			}

			public String getErroruser() {
				return erroruser;
			}

			public void setErroruser(String erroruser) {
				this.erroruser = erroruser;
			}

		}

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

		public RetData getRetData() {
			return retData;
		}

		public void setRetData(RetData retData) {
			this.retData = retData;
		}

	}

	public static class DingdingMessageResp {

		private Integer errcode;
		private String errmsg;
		private Long task_id;

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public Long getTask_id() {
			return task_id;
		}

		public void setTask_id(Long task_id) {
			this.task_id = task_id;
		}

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

	}

	public static class QiyeweixinMessageResp {

		private Integer errcode;
		private String errmsg;
		private String invaliduser;
		private String invalidparty;
		private String invalidtag;

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public String getInvaliduser() {
			return invaliduser;
		}

		public void setInvaliduser(String invaliduser) {
			this.invaliduser = invaliduser;
		}

		public String getInvalidparty() {
			return invalidparty;
		}

		public void setInvalidparty(String invalidparty) {
			this.invalidparty = invalidparty;
		}

		public String getInvalidtag() {
			return invalidtag;
		}

		public void setInvalidtag(String invalidtag) {
			this.invalidtag = invalidtag;
		}

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

	}

	public static class Wi extends Mass {

		private static final long serialVersionUID = -4648753208492517677L;

		static WrapCopier<Wi, Mass> copier = WrapCopierFactory.wi(Wi.class, Mass.class, null, JpaObject.FieldsUnmodify);
	}

	public static class Wo extends Mass {

		private static final long serialVersionUID = -2315413747596601116L;

		static WrapCopier<Mass, Wo> copier = WrapCopierFactory.wo(Mass.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}