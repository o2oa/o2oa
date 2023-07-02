package com.x.processplatform.service.processing;

import com.x.base.core.container.EntityManagerContainer;
import com.x.processplatform.service.processing.processor.agent.AgentProcessor;
import com.x.processplatform.service.processing.processor.begin.BeginProcessor;
import com.x.processplatform.service.processing.processor.cancel.CancelProcessor;
import com.x.processplatform.service.processing.processor.choice.ChoiceProcessor;
import com.x.processplatform.service.processing.processor.delay.DelayProcessor;
import com.x.processplatform.service.processing.processor.embed.EmbedProcessor;
import com.x.processplatform.service.processing.processor.end.EndProcessor;
import com.x.processplatform.service.processing.processor.invoke.InvokeProcessor;
import com.x.processplatform.service.processing.processor.manual.ManualProcessor;
import com.x.processplatform.service.processing.processor.merge.MergeProcessor;
import com.x.processplatform.service.processing.processor.parallel.ParallelProcessor;
import com.x.processplatform.service.processing.processor.publish.PublishProcessor;
import com.x.processplatform.service.processing.processor.service.ServiceProcessor;
import com.x.processplatform.service.processing.processor.split.SplitProcessor;

public abstract class BaseProcessing {

	public abstract EntityManagerContainer entityManagerContainer();

	private AgentProcessor agent;

	public AgentProcessor agent() throws Exception {
		if (null == this.agent) {
			this.agent = new AgentProcessor(this.entityManagerContainer());
		}
		return agent;
	}

	private BeginProcessor begin;

	public BeginProcessor begin() throws Exception {
		if (null == this.begin) {
			this.begin = new BeginProcessor(this.entityManagerContainer());
		}
		return begin;
	}

	private CancelProcessor cancel;

	public CancelProcessor cancel() throws Exception {
		if (null == this.cancel) {
			this.cancel = new CancelProcessor(this.entityManagerContainer());
		}
		return cancel;
	}

	private ChoiceProcessor choice;

	public ChoiceProcessor choice() throws Exception {
		if (null == this.choice) {
			this.choice = new ChoiceProcessor(this.entityManagerContainer());
		}
		return choice;
	}

	private DelayProcessor delay;

	public DelayProcessor delay() throws Exception {
		if (null == this.delay) {
			this.delay = new DelayProcessor(this.entityManagerContainer());
		}
		return delay;
	}

	private EmbedProcessor embed;

	public EmbedProcessor embed() throws Exception {
		if (null == this.embed) {
			this.embed = new EmbedProcessor(this.entityManagerContainer());
		}
		return embed;
	}

	private EndProcessor end;

	public EndProcessor end() throws Exception {
		if (null == this.end) {
			this.end = new EndProcessor(this.entityManagerContainer());
		}
		return end;
	}

	private InvokeProcessor invoke;

	public InvokeProcessor invoke() throws Exception {
		if (null == this.invoke) {
			this.invoke = new InvokeProcessor(this.entityManagerContainer());
		}
		return invoke;
	}

	private ManualProcessor manual;

	public ManualProcessor manual() throws Exception {
		if (null == this.manual) {
			this.manual = new ManualProcessor(this.entityManagerContainer());
		}
		return manual;
	}

	private MergeProcessor merge;

	public MergeProcessor merge() throws Exception {
		if (null == this.merge) {
			this.merge = new MergeProcessor(this.entityManagerContainer());
		}
		return merge;
	}

	private ParallelProcessor parallel;

	public ParallelProcessor parallel() throws Exception {
		if (null == this.parallel) {
			this.parallel = new ParallelProcessor(this.entityManagerContainer());
		}
		return parallel;
	}

	private PublishProcessor publish;

	public PublishProcessor publish() throws Exception {
		if (null == this.publish) {
			this.publish = new PublishProcessor(this.entityManagerContainer());
		}
		return publish;
	}

	private ServiceProcessor service;

	public ServiceProcessor service() throws Exception {
		if (null == this.service) {
			this.service = new ServiceProcessor(this.entityManagerContainer());
		}
		return service;
	}

	private SplitProcessor split;

	public SplitProcessor split() throws Exception {
		if (null == this.split) {
			this.split = new SplitProcessor(this.entityManagerContainer());
		}
		return split;
	}

}
