package com.x.processplatform.service.processing.processor.embed;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.organization.core.express.Organization;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.organization.core.express.wrap.WrapPerson;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.EmbedCreatorType;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AbstractProcessor;

public class EmbedProcessor extends AbstractProcessor {

	private static Logger logger = LoggerFactory.getLogger(EmbedProcessor.class);

	public EmbedProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		return work;
	}

	@Override
	protected List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity) throws Exception {
		Embed embed = (Embed) activity;
		String uri = "work/process/" + URLEncoder.encode(embed.getTargetProcess(), "UTF-8");
		Data pushData = null;
		if (BooleanUtils.isTrue(embed.getInheritData())) {
			pushData = data;
		}
		WrapOutId wrapOutId = ThisApplication.applications.postQuery(x_processplatform_service_processing.class, uri,
				pushData, WrapOutId.class);
		/* 创建处于新建状态的work之后暂时不推动，由下个环节进行推动 */
		this.updateTarget(wrapOutId.getId(), attributes, work, pushData, embed);
		work.setEmbedTargetWork(wrapOutId.getId());
		List<Work> results = new ArrayList<>();
		results.add(work);
		return results;
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		/* 驱动上个环节新产生的work */
		ThisApplication.applications.putQuery(x_processplatform_service_processing.class,
				"work/" + URLEncoder.encode(work.getEmbedTargetWork(), "UTF-8") + "/processing", null);
		List<Route> results = new ArrayList<>();
		results.add(routes.get(0));
		return results;
	}

	private void updateTarget(String id, ProcessingAttributes attributes, Work work, Data data, Embed embed)
			throws Exception {
		Work target = this.entityManagerContainer().find(id, Work.class, ExceptionWhen.not_found);
		this.updateTargetWorkTitle(target, attributes, work, data, embed);
		this.updateTargetWorkCreator(target, attributes, work, data, embed);
	}

	private void updateTargetWorkTitle(Work target, ProcessingAttributes attributes, Work work, Data data, Embed embed)
			throws Exception {
		String value = "";
		if (this.hasTitleScript(embed)) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data, embed);
			value = scriptHelper.evalAsString(work.getApplication(), embed.getTitleScript(),
					embed.getTitleScriptText());
		}
		if (StringUtils.isEmpty(value)) {
			value = embed.getName() + ":" + work.getTitle();
			target.setTitle(value);
		}
	}

	private void updateTargetWorkCreator(Work target, ProcessingAttributes attributes, Work work, Data data,
			Embed embed) throws Exception {
		EmbedCreatorType type = embed.getEmbedCreatorType();
		if (null == type) {
			type = EmbedCreatorType.creator;
		}
		String value = "";
		switch (type) {
		case identity:
			value = embed.getTargetIdentity();
			break;
		case lastIdentity:
			value = this.findLastIdentity(work);
			break;
		default:
			value = work.getCreatorIdentity();
			break;
		}
		if (this.hasTargetIdentityScript(embed)) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data, embed,
					new BindingPair("identity", value));
			value = scriptHelper.evalAsString(work.getApplication(), embed.getTargetIdentityScript(),
					embed.getTargetIdentityScriptText());
		}
		if (StringUtils.isEmpty(value)) {
			/** 没有设定被调用流程的启动者. */
			this.business().work().addHint(work, "没有找到被调用流程的启动者,强制设置被调用流程的启动者为创建者");
		}
		this.updateTargetCreatorIdentity(target, value);
	}

	private String findLastIdentity(Work work) throws Exception {
		EntityManagerContainer emc = this.entityManagerContainer();
		String id = this.business().taskCompleted().getLastWithWork(work.getId());
		if (StringUtils.isEmpty(id)) {
			return work.getCreatorIdentity();
		} else {
			TaskCompleted o = emc.find(id, TaskCompleted.class);
			return o.getId();
		}
	}

	private void updateTargetCreatorIdentity(Work target, String identity) throws Exception {
		Organization org = this.business().organization();
		WrapIdentity wrapIdentity = org.identity().getWithName(identity);
		target.setCreatorIdentity(wrapIdentity.getName());
		WrapDepartment wrapDepartment = org.department().getWithIdentity(wrapIdentity.getName());
		target.setCreatorDepartment(wrapDepartment.getName());
		WrapCompany wrapCompany = org.company().getWithIdentity(wrapIdentity.getName());
		target.setCreatorCompany(wrapCompany.getName());
		WrapPerson wrapPerson = org.person().getWithIdentity(wrapIdentity.getName());
		target.setCreatorPerson(wrapPerson.getName());
	}

	private boolean hasTitleScript(Embed embed) throws Exception {
		return StringUtils.isNotEmpty(embed.getTitleScript()) || StringUtils.isNotEmpty(embed.getTitleScriptText());
	}

	private boolean hasTargetIdentityScript(Embed embed) throws Exception {
		return StringUtils.isNotEmpty(embed.getTargetIdentityScript())
				|| StringUtils.isNotEmpty(embed.getTargetIdentityScriptText());
	}

}