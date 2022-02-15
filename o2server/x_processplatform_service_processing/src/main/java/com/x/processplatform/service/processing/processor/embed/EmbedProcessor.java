package com.x.processplatform.service.processing.processor.embed;

import java.util.ArrayList;
import java.util.List;

import javax.script.CompiledScript;
import javax.script.ScriptContext;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.JsonScriptingExecutor;
import com.x.base.core.project.scripting.ScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.EmbedCreatorType;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WrapScriptObject;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class EmbedProcessor extends AbstractEmbedProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbedProcessor.class);

	public EmbedProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Embed embed) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.embedArrive(aeiObjects.getWork().getActivityToken(), embed));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Embed embed) throws Exception {
		// nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Embed embed) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.embedExecute(aeiObjects.getWork().getActivityToken(), embed));
		AssignData assignData = new AssignData();
		String targetApplication = embed.getTargetApplication();
		String targetProcess = embed.getTargetProcess();
		if (StringUtils.isEmpty(targetApplication)) {
			throw new ExceptionEmptyTargetApplication(embed.getName());
		}
		if (StringUtils.isEmpty(targetProcess)) {
			throw new ExceptionEmptyTargetProcess(embed.getName());
		}
		assignData.setApplication(targetApplication);
		assignData.setProcess(embed.getTargetProcessName());
		if (BooleanUtils.isTrue(embed.getInheritData())) {
			assignData.setData(aeiObjects.getData());
		}
		if (BooleanUtils.isTrue(embed.getInheritAttachment())) {
			List<Attachment> os = this.business().entityManagerContainer().list(Attachment.class,
					this.business().attachment().listWithJob(aeiObjects.getWork().getJob()));
			assignData.setAttachmentList(os);
		}
		String targetIdentity = this.targetIdentity(aeiObjects, embed);
		targetIdentity = this.business().organization().identity().get(targetIdentity);
		if (StringUtils.isEmpty(targetIdentity)) {
			throw new ExceptionEmptyTargetIdentity(embed.getName());
		}
		assignData.setIdentity(targetIdentity);
		assignData.setTitle(this.targetTitle(aeiObjects, embed));
		assignData.setProcessing(true);
		if (this.hasAssignDataScript(embed)) {
			WrapScriptObject wrap = new WrapScriptObject();
			wrap.set(gson.toJson(assignData));
			ScriptContext scriptContext = aeiObjects.scriptContext();
			scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put(ScriptingFactory.BINDING_NAME_ASSIGNDATA, wrap);
			CompiledScript cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					embed, Business.EVENT_EMBEDTARGETASSIGNDATA);
			AssignData returnData = JsonScriptingExecutor.eval(cs, scriptContext, AssignData.class);
			if (null != returnData) {
				assignData = returnData;
			} else {
				assignData = gson.fromJson(wrap.get(), AssignData.class);
			}
		}
		LOGGER.debug("embed:{}, process:{} try to embed application:{}, process:{}, assignData:{}.", embed::getName,
				embed::getProcess, embed::getTargetApplication, embed::getTargetProcess, assignData::toString);
		if (BooleanUtils.isTrue(embed.getAsync())) {
			ThisApplication.syncEmbedQueue.send(assignData);
		} else {
			EmbedExecutor executor = new EmbedExecutor();
			String embedWorkId = executor.execute(assignData);
			aeiObjects.getWork().setEmbedTargetWork(embedWorkId);
		}

		List<Work> results = new ArrayList<>();
		results.add(aeiObjects.getWork());
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Embed embed, List<Work> works) throws Exception {
		// nothing
	}

	public static class WoWorkId extends WoId {

		private static final long serialVersionUID = 7931241930072510113L;

	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Embed embed) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.embedInquire(aeiObjects.getWork().getActivityToken(), embed));
		// 驱动上个环节新产生的work
		List<Route> results = new ArrayList<>();
		results.add(aeiObjects.getRoutes().get(0));
		return results;
	}

	private String targetIdentity(AeiObjects aeiObjects, Embed embed) throws Exception {
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
			value = this.findLastIdentity(aeiObjects.getWork());
			break;
		default:
			value = aeiObjects.getWork().getCreatorIdentity();
			break;
		}
		if (this.hasIdentityScript(embed)) {
			ScriptContext scriptContext = aeiObjects.scriptContext();
			CompiledScript compiledScript = aeiObjects.business().element().getCompiledScript(
					aeiObjects.getWork().getApplication(), embed, Business.EVENT_EMBEDTARGETIDENTITY);
			List<String> os = JsonScriptingExecutor.evalDistinguishedNames(compiledScript, scriptContext);
			os = ListTools.trim(os, true, false);
			if (ListTools.isEmpty(os)) {
				value = "";
			} else {
				value = os.get(0);
			}
		}
		return value;
	}

	private String targetTitle(AeiObjects aeiObjects, Embed embed) throws Exception {
		String value = "";
		if (this.hasTitleScript(embed)) {
			CompiledScript compiledScript = aeiObjects.business().element()
					.getCompiledScript(aeiObjects.getWork().getApplication(), embed, Business.EVENT_EMBEDTARGETTITLE);
			value = JsonScriptingExecutor.evalString(compiledScript, aeiObjects.scriptContext());
		}
		if (StringUtils.isEmpty(value)) {
			value = embed.getName() + ":" + aeiObjects.getWork().getTitle();
		}
		return value;
	}

	private String findLastIdentity(Work work) throws Exception {
		EntityManagerContainer emc = this.entityManagerContainer();
		String id = this.business().taskCompleted().getLastWithWork(work.getId());
		if (StringUtils.isEmpty(id)) {
			return work.getCreatorIdentity();
		} else {
			TaskCompleted o = emc.find(id, TaskCompleted.class);
			return o.getIdentity();
		}
	}

	private boolean hasTitleScript(Embed embed) {
		return StringUtils.isNotEmpty(embed.getTargetTitleScript())
				|| StringUtils.isNotEmpty(embed.getTargetTitleScriptText());
	}

	private boolean hasIdentityScript(Embed embed) {
		return StringUtils.isNotEmpty(embed.getTargetIdentityScript())
				|| StringUtils.isNotEmpty(embed.getTargetIdentityScriptText());
	}

	private boolean hasAssignDataScript(Embed embed) {
		return StringUtils.isNotEmpty(embed.getTargetAssginDataScript())
				|| StringUtils.isNotEmpty(embed.getTargetAssginDataScriptText());
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Embed embed) throws Exception {
		// nothing
	}

}