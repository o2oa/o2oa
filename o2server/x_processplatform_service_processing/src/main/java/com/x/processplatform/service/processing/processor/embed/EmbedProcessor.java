package com.x.processplatform.service.processing.processor.embed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.EmbedCreatorType;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWi.WiAttachment;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWo;
import com.x.processplatform.service.processing.Business;
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
		// 清理标识
		aeiObjects.getWork().setEmbedCompleted("");
		aeiObjects.getWork().setEmbedTargetWork("");
		aeiObjects.getWork().setEmbedTargetJob("");
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
		List<Work> results = new ArrayList<>();
		int blinker = blinker(BooleanUtils.isTrue(embed.getWaitUntilCompleted()),
				aeiObjects.getWork().getEmbedTargetWork(), aeiObjects.getWork().getEmbedCompleted());
		switch (blinker) {
		case 0:// waitUntilCompleted:false, embedTargetWork:false, embedCompleted:false
		case 1:// waitUntilCompleted:false, embedTargetWork:false, embedCompleted:true X
			embed(aeiObjects, embed);
			results.add(aeiObjects.getWork());
			break;
		case 2:// waitUntilCompleted:false, embedTargetWork:true, embedCompleted:false
		case 3:// waitUntilCompleted:false, embedTargetWork:true, embedCompleted:true X
			results.add(aeiObjects.getWork());
			break;
		case 4:// waitUntilCompleted:true, embedTargetWork:false, embedCompleted:false
			embed(aeiObjects, embed);
			break;
		case 5:// waitUntilCompleted:true, embedTargetWork:false, embedCompleted:true
				// impossible
			results.add(aeiObjects.getWork());
			break;
		case 6:// waitUntilCompleted:true, embedTargetWork:true, embedCompleted:false
				// wait
			break;
		case 7:// waitUntilCompleted:true, embedTargetWork:true, embedCompleted:true
			results.add(aeiObjects.getWork());
			break;
		default:
			break;
		}
		return results;
	}

	private int blinker(boolean waitUntilCompleted, String embedTargetWork, String embedCompleted) {
		int value = 0;
		if (waitUntilCompleted) {
			value += 4;
		}
		if (StringUtils.isNotBlank(embedTargetWork)) {
			value += 2;
		}
		if (StringUtils.isNotBlank(embedCompleted)) {
			value += 1;
		}
		return value;
	}

	private void embed(AeiObjects aeiObjects, Embed embed) throws Exception {
		ActionAssignCreateWi assignData = new ActionAssignCreateWi();
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
			List<Attachment> os = aeiObjects.getAttachments();
			for (Attachment attachment : os) {
				WiAttachment wiAttachment = new WiAttachment();
				attachment.copyTo(wiAttachment, true);
				assignData.getAttachmentList().add(wiAttachment);
			}
		}
		String targetIdentity = this.targetIdentity(aeiObjects, embed);
		targetIdentity = this.business().organization().identity().get(targetIdentity);
		if (StringUtils.isEmpty(targetIdentity)) {
			throw new ExceptionEmptyTargetIdentity(embed.getName());
		}
		assignData.setIdentity(targetIdentity);
		assignData.setProcessing(true);
		assignData.setParentWork(aeiObjects.getWork().getId());
		assignData.setParentJob(aeiObjects.getWork().getJob());
		assignData.setTitle(this.targetTitle(aeiObjects, embed, assignData));
		if (this.hasAssignDataScript(embed)) {
			WrapScriptObject wrap = new WrapScriptObject();
			wrap.set(gson.toJson(assignData));
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_ASSIGNDATA, wrap);
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					embed, Business.EVENT_EMBEDTARGETASSIGNDATA);
			ActionAssignCreateWi returnData = GraalvmScriptingFactory.eval(source, bindings,
					ActionAssignCreateWi.class);
			if (null != returnData) {
				assignData = returnData;
			} else {
				assignData = gson.fromJson(wrap.get(), ActionAssignCreateWi.class);
			}
		}
		LOGGER.debug("embed:{}, process:{} try to embed application:{}, process:{}, assignData:{}.", embed::getName,
				embed::getProcess, embed::getTargetApplication, embed::getTargetProcess, assignData::toString);
		EmbedExecutor executor = new EmbedExecutor();
		ActionAssignCreateWo wo = executor.execute(assignData);
		aeiObjects.getWork().setEmbedTargetWork(wo.getId());
		aeiObjects.getWork().setEmbedTargetJob(wo.getJob());
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Embed embed, List<Work> works) throws Exception {
		// nothing
	}

	public static class WoWorkId extends WoId {

		private static final long serialVersionUID = 7931241930072510113L;

	}

	@Override
	protected Optional<Route> inquiring(AeiObjects aeiObjects, Embed embed) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.embedInquire(aeiObjects.getWork().getActivityToken(), embed));
		return aeiObjects.getRoutes().stream().findFirst();
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
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings();
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					embed, Business.EVENT_EMBEDTARGETIDENTITY);
			List<String> os = GraalvmScriptingFactory.evalAsDistinguishedNames(source, bindings);
			os = ListTools.trim(os, true, false);
			if (ListTools.isEmpty(os)) {
				value = "";
			} else {
				value = os.get(0);
			}
		}
		return value;
	}

	private String targetTitle(AeiObjects aeiObjects, Embed embed, ActionAssignCreateWi assignData) throws Exception {
		String value = "";
		if (this.hasTitleScript(embed)) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					embed, Business.EVENT_EMBEDTARGETTITLE);
			WrapScriptObject wrap = new WrapScriptObject();
			wrap.set(gson.toJson(assignData));
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_ASSIGNDATA, wrap);
			Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, bindings);
			if (opt.isPresent()) {
				value = opt.get();
			}
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
