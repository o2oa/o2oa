package com.x.file.assemble.control.jaxrs.share;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Share;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Share share = emc.find(id, Share.class);
			if (null == share) {
				throw new ExceptionAttachmentNotExist(id);
			}
			/* 判断文件的所有者是否是当前用户 */
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), share.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			EntityManager em = emc.beginTransaction(Share.class);
			emc.delete(Share.class, share.getId());
			em.getTransaction().commit();
			if(!"password".equals(share.getShareType())){
				List<String> shareCancels = new ArrayList<>();
				shareCancels.addAll(share.getShareUserList());
				if(!share.getShareOrgList().isEmpty()){
					shareCancels.addAll(business.organization().person().listWithUnitSubNested( share.getShareOrgList() ));
				}
				ListOrderedSet<String> set = new ListOrderedSet<>();
				set.addAll(shareCancels);
				shareCancels = set.asList();
				/* 发送取消共享通知 */
				for (String str : shareCancels) {
					this.message_send_attachment_shareCancel(share, str);
				}
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}