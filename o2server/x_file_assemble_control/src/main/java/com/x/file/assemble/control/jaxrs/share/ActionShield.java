package com.x.file.assemble.control.jaxrs.share;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Share;

class ActionShield extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Share share = emc.find(id, Share.class);
			if (null == share) {
				throw new ExceptionAttachmentNotExist(id);
			}
			/* 判断当前用户是否有权限访问该文件 */
			if (!"password".equals(share.getShareType())) {
				if(!effectivePerson.isManager() && !StringUtils.equals(effectivePerson.getDistinguishedName(), share.getPerson())) {
					if (!hasPermission(business, effectivePerson, share)) {
						throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
					}
				}
				emc.beginTransaction(Share.class);
				List<String> list = new ArrayList<>();
				list.add(effectivePerson.getDistinguishedName());
				if(share.getShieldUserList()==null){
					share.setShieldUserList(list);
				}else{
					share.setShieldUserList(ListTools.add(share.getShieldUserList(),true,true,list));
				}
				emc.commit();
			}

			Wo wo = new Wo();
			wo.setId(share.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}