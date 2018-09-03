package o2.collect.assemble.jaxrs.collect;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.PmsMessage;
import com.x.base.core.project.tools.ListTools;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Device_;
import o2.collect.core.entity.Unit;

class ActionPushMessageTransfer extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPushMessageTransfer.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.setValue(false);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getTitle()) || StringUtils.isEmpty(wi.getAccount())
					|| StringUtils.isEmpty(wi.getUnit()) || StringUtils.isEmpty(wi.getPassword())) {
				logger.print("接收到推送消息发送请求,请求验证失败,内容:{}", wi.toString());
			} else {
				Unit unit = business.validateUnit(wi.getUnit(), wi.getPassword());
				if (null == unit) {
					throw new ExceptionValidateUnitError(wi.getAccount());
				}
				String accountId = business.account().getWithNameUnit(wi.getAccount(), unit.getId());
				if (StringUtils.isEmpty(accountId)) {
					throw new ExceptionAccountNotExist(wi.getAccount());
				}
				List<Device> devices = this.listDevice(business, accountId);
				logger.info("接收到推送消息发送请求,组织:{}, 账户:{}, 设备数量:{}, 标题:{}.", wi.getUnit(), wi.getAccount(), devices.size(),
						wi.getTitle());
				if (ListTools.isNotEmpty(devices)) {
					PushPayload pushPayload = PushPayload.newBuilder().setPlatform(Platform.all())
							.setAudience(Audience.registrationId(
									ListTools.extractProperty(devices, "name", String.class, true, true)))
							.setNotification(Notification.alert(wi.getTitle()))
							.setOptions(Options.newBuilder().setApnsProduction(true).build()).build();
					try {
						PushResult pushResult = jpushClient().sendPush(pushPayload);
						logger.info("{}", pushResult);
					} catch (APIConnectionException e) {
						// e.printStackTrace();
					} catch (APIRequestException e) {
						// e.printStackTrace();
					}
				}
				wo.setValue(true);
			}
			result.setData(wo);
			return result;
		}
	}

	/* 仅通知最近的两个设备 */
	private List<Device> listDevice(Business business, String accountId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Device.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Device> cq = cb.createQuery(Device.class);
		Root<Device> root = cq.from(Device.class);
		Predicate p = cb.equal(root.get(Device_.account), accountId);
		cq.select(root).where(p).orderBy(cb.desc(root.get(Device_.updateTime)));
		return em.createQuery(cq).setMaxResults(2).getResultList();
	}

	public static class Wi extends PmsMessage {
	}

	public static class Wo extends WrapBoolean {
	}

}
