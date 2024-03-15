package com.x.processplatform.service.processing.processor.cancel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class CancelProcessor extends AbstractCancelProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CancelProcessor.class);

	public CancelProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.cancelArrive(aeiObjects.getWork().getActivityToken(), cancel));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		// nothing
	}

	@Override
	public List<Work> executing(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.cancelExecute(aeiObjects.getWork().getActivityToken(), cancel));
		// 唯一work处理
		if (aeiObjects.getWorks().size() > 1) {
			aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
			aeiObjects.getTasks().stream().filter(o -> StringUtils.equals(o.getWork(), aeiObjects.getWork().getId()))
					.forEach(o -> aeiObjects.getDeleteTasks().add(o));
		} else {
			aeiObjects.getTasks().stream().forEach(o -> aeiObjects.getDeleteTasks().add(o));
			aeiObjects.getTaskCompleteds().stream().forEach(o -> aeiObjects.getDeleteTaskCompleteds().add(o));
			aeiObjects.getReads().stream().forEach(o -> aeiObjects.getDeleteReads().add(o));
			aeiObjects.getReadCompleteds().stream().forEach(o -> aeiObjects.getDeleteReadCompleteds().add(o));
			aeiObjects.getReviews().stream().forEach(o -> aeiObjects.getDeleteReviews().add(o));
			aeiObjects.getDocumentVersions().stream().forEach(o -> aeiObjects.getDeleteDocumentVersions().add(o));
			aeiObjects.getDocSignScrawls().stream().forEach(o -> aeiObjects.getDeleteDocSignScrawls().add(o));
			aeiObjects.getDocSigns().stream().forEach(o -> aeiObjects.getDeleteDocSigns().add(o));
			aeiObjects.getRecords().stream().forEach(o -> aeiObjects.getDeleteRecords().add(o));
			aeiObjects.getWorkLogs().stream().forEach(o -> aeiObjects.getDeleteWorkLogs().add(o));
			// 附件删除单独处理,需要删除Attachment的二进制文件
			aeiObjects.getAttachments().stream().forEach(o -> aeiObjects.getDeleteAttachments().add(o));
			// 如果只有一份数据，没有拆分，那么删除Data
			aeiObjects.getWorkDataHelper().remove();
			aeiObjects.getWorks().stream().forEach(o -> aeiObjects.getDeleteWorks().add(o));
			// cancel 可以不删除快照
		}
		// 需要返回work,否则事件无法执行.
		List<Work> results = new ArrayList<>();
		results.add(aeiObjects.getWork());
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Cancel cancel, List<Work> works) throws Exception {
		// 删除后再次检查，如果存在多个副本，且都已经在End状态，那么试图推动一个
		if (!aeiObjects.getWorks().isEmpty()) {
			Predicate<Work> p = o -> Objects.equals(ActivityType.end, o.getActivityType());
			if (aeiObjects.getWorks().stream().allMatch(p)) {
				touchWork(aeiObjects.getWorks().get(0).getId());
			}
		}
		tryUpdateParentWork(aeiObjects);
	}

	private void tryUpdateParentWork(AeiObjects aeiObjects) {
		if (StringUtils.isNotBlank(aeiObjects.getWork().getParentWork())) {
			try {
				Work parent = aeiObjects.entityManagerContainer().find(aeiObjects.getWork().getParentWork(),
						Work.class);
				if ((null != parent) && Objects.equals(parent.getActivityType(), ActivityType.embed)) {
					Embed embed = (Embed) aeiObjects.business().element().get(parent.getActivity(),
							parent.getActivityType());
					if ((null != embed) && BooleanUtils.isTrue(embed.getWaitUntilCompleted())) {
						updateParentWork(aeiObjects, parent, embed);
					}
				}
			} catch (Exception e) {
				LOGGER.error(new ExceptionUpdateParentWork(e, aeiObjects.getWork().getId(),
						aeiObjects.getWork().getParentWork()));
			}
		}
	}

	private void updateParentWork(AeiObjects aeiObjects, Work parent, Embed embed) throws Exception {
		// 先把状态值注入,这样脚本执行时可以取得到值.
		parent.setEmbedCompleted(ActivityType.cancel.toString());
		AeiObjects embedAeiObjects = new AeiObjects(aeiObjects.business(), parent, embed,
				aeiObjects.getProcessingAttributes());
		embedAeiObjects.entityManagerContainer().beginTransaction(Work.class);
		if (this.hasEmbedCompletedScript(embed) || this.hasEmbedCompletedCancelScript(embed)) {
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_EMBEDDATA, aeiObjects.getData());
			if (this.hasEmbedCompletedScript(embed)) {
				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
						embed, Business.EVENT_EMBEDCOMPLETED);
				GraalvmScriptingFactory.eval(source, bindings);
			}
			if (this.hasEmbedCompletedCancelScript(embed)) {
				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
						embed, Business.EVENT_EMBEDCOMPLETEDCANCEL);
				GraalvmScriptingFactory.eval(source, bindings);
			}
		}
		embedAeiObjects.commit();
		touchWork(parent.getId());
	}

	/**
	 * 触发在等待状态的父文档
	 * 
	 * @param parentWorkId
	 */
	private void touchWork(String workId) {
		new Thread(CancelProcessor.class.getName() + "_touchWork") {
			@Override
			public void run() {
				try {
					new Processing(new ProcessingAttributes()).processing(workId);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
		}.start();
	}

	@Override
	protected Optional<Route> inquiring(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.cancelInquire(aeiObjects.getWork().getActivityToken(), cancel));
		return Optional.empty();
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		// nothing
	}

}