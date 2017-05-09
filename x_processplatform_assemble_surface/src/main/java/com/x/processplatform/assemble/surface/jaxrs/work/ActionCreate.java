package com.x.processplatform.assemble.surface.jaxrs.work;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.DefaultCharset;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.TokenType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.organization.core.express.Organization;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.builder.WorkLogBuilder;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTask;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkLog;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionCreate extends ActionBase {

	ActionResult<List<WrapOutWorkLog>> execute(EffectivePerson effectivePerson, String processFlag,
			JsonElement jsonElement) throws Exception {
		String workId = "";
		WrapIdentity identity = null;
		List<WrapOutWorkLog> wraps = new ArrayList<>();
		ActionResult<List<WrapOutWorkLog>> result = new ActionResult<>();
		WrapInWork wrapIn = this.convertToWrapIn(jsonElement, WrapInWork.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			identity = this.decideCreatorIdentity(business, effectivePerson, wrapIn);
			Process process = business.process().pick(processFlag);
			if (null == process) {
				throw new ProcessNotExistedException(processFlag);
			}
			Application application = business.application().pick(process.getApplication());
			List<String> roles = business.organization().role().listNameWithPerson(effectivePerson.getName());
			List<String> identities = business.organization().identity().listNameWithPerson(effectivePerson.getName());
			List<String> departments = business.organization().department()
					.listNameWithPersonSupNested(effectivePerson.getName());
			List<String> companies = business.organization().company()
					.listNameWithPersonSupNested(effectivePerson.getName());
			if (!business.application().allowRead(effectivePerson, roles, identities, departments, companies,
					application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getId());
			}
			WrapOutId wrapOutId = ThisApplication.context().applications()
					.postQuery(x_processplatform_service_processing.class,
							"work/process/" + URLEncoder.encode(process.getId(), DefaultCharset.name), wrapIn.getData())
					.getData(WrapOutId.class);
			workId = wrapOutId.getId();
		}
		/* 设置Work信息 并返回job信息 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Organization organization = business.organization();
			emc.beginTransaction(Work.class);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new WorkNotExistedException(workId);
			}
			work.setTitle(wrapIn.getTitle());
			work.setCreatorIdentity(identity.getName());
			work.setCreatorPerson(organization.person().getWithIdentity(identity.getName()).getName());
			work.setCreatorDepartment(organization.department().getWithIdentity(identity.getName()).getName());
			work.setCreatorCompany(organization.company().getWithIdentity(identity.getName()).getName());
			// if ((null != wrapIn.getData()) &&
			// wrapIn.getData().isJsonObject()) {
			// DataHelper dataHelper = new DataHelper(emc, work);
			// dataHelper.update(wrapIn.getData());
			// }
			emc.commit();
		}
		/* 驱动工作 */
		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				"work/" + URLEncoder.encode(workId, DefaultCharset.name) + "/processing", null);
		/* 拼装返回结果 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new WorkNotExistedException(workId);
			}
			List<String> ids = business.workLog().listWithFromActivityTokenForwardNotConnected(work.getActivityToken());
			/* 先取得没有结束的WorkLog */
			wraps = WorkLogBuilder.complex(business, emc.list(WorkLog.class, ids));
			/* 标识当前用户的待办 */
			for (WrapOutWorkLog o : wraps) {
				o.setCurrentTaskIndex(-1);
				for (int i = 0; i < o.getTaskList().size(); i++) {
					WrapOutTask t = o.getTaskList().get(i);
					if (StringUtils.equals(effectivePerson.getName(), t.getPerson())) {
						o.setCurrentTaskIndex(i);
					}
				}
			}
		}
		result.setData(wraps);
		return result;
	}

	private WrapIdentity decideCreatorIdentity(Business business, EffectivePerson effectivePerson, WrapInWork wrapIn)
			throws Exception {
		if (TokenType.cipher.equals(effectivePerson.getTokenType())) {
			return business.organization().identity().getWithName(wrapIn.getIdentity());
		} else if (StringUtils.isNotEmpty(wrapIn.getIdentity())) {
			List<WrapIdentity> identities = business.organization().identity()
					.listWithPerson(effectivePerson.getName());
			if (identities.size() == 0) {
				throw new NoneIdentityException(effectivePerson.getName());
				// throw new Exception("can not get identity of person:" +
				// effectivePerson.getName() + ".");
			} else if (identities.size() == 1) {
				return identities.get(0);
			} else {
				/* 有多个身份需要逐一判断是否包含. */
				for (WrapIdentity o : identities) {
					if (StringUtils.equals(o.getName(), wrapIn.getIdentity())) {
						return o;
					}
				}
			}
		} else {
			List<WrapIdentity> list = business.organization().identity().listWithPerson(effectivePerson.getName());
			if (!list.isEmpty()) {
				return list.get(0);
			}
		}
		throw new Exception("decideCreatorIdentity error:" + wrapIn.toString());
	}

}