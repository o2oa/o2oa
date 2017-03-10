package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutAgent;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutBegin;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutCancel;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutChoice;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutCondition;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutDelay;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutEmbed;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutEnd;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutInvoke;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutManual;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutMerge;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutMessage;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutParallel;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutProcess;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutRoute;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutService;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutSplit;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Condition;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

abstract class ActionBase {
	
	static BeanCopyTools<Process, WrapOutProcess> processCopier = BeanCopyToolsBuilder.create(Process.class,
			WrapOutProcess.class, null, WrapOutProcess.Excludes);
	static BeanCopyTools<Agent, WrapOutAgent> agentCopier = BeanCopyToolsBuilder.create(Agent.class, WrapOutAgent.class,
			null, WrapOutAgent.Excludes);
	static BeanCopyTools<Begin, WrapOutBegin> beginCopier = BeanCopyToolsBuilder.create(Begin.class, WrapOutBegin.class,
			null, WrapOutBegin.Excludes);
	static BeanCopyTools<Cancel, WrapOutCancel> cancelCopier = BeanCopyToolsBuilder.create(Cancel.class,
			WrapOutCancel.class, null, WrapOutCancel.Excludes);
	static BeanCopyTools<Choice, WrapOutChoice> choiceCopier = BeanCopyToolsBuilder.create(Choice.class,
			WrapOutChoice.class, null, WrapOutChoice.Excludes);
	static BeanCopyTools<Condition, WrapOutCondition> conditionCopier = BeanCopyToolsBuilder.create(Condition.class,
			WrapOutCondition.class, null, WrapOutCondition.Excludes);
	static BeanCopyTools<Delay, WrapOutDelay> delayCopier = BeanCopyToolsBuilder.create(Delay.class, WrapOutDelay.class,
			null, WrapOutDelay.Excludes);
	static BeanCopyTools<Embed, WrapOutEmbed> embedCopier = BeanCopyToolsBuilder.create(Embed.class, WrapOutEmbed.class,
			null, WrapOutEmbed.Excludes);
	static BeanCopyTools<End, WrapOutEnd> endCopier = BeanCopyToolsBuilder.create(End.class, WrapOutEnd.class, null,
			WrapOutEnd.Excludes);
	static BeanCopyTools<Invoke, WrapOutInvoke> invokeCopier = BeanCopyToolsBuilder.create(Invoke.class,
			WrapOutInvoke.class, null, WrapOutInvoke.Excludes);
	static BeanCopyTools<Manual, WrapOutManual> manualCopier = BeanCopyToolsBuilder.create(Manual.class,
			WrapOutManual.class, null, WrapOutManual.Excludes);
	static BeanCopyTools<Merge, WrapOutMerge> mergeCopier = BeanCopyToolsBuilder.create(Merge.class, WrapOutMerge.class,
			null, WrapOutMerge.Excludes);
	static BeanCopyTools<Message, WrapOutMessage> messageCopier = BeanCopyToolsBuilder.create(Message.class,
			WrapOutMessage.class, null, WrapOutMessage.Excludes);
	static BeanCopyTools<Parallel, WrapOutParallel> parallelCopier = BeanCopyToolsBuilder.create(Parallel.class,
			WrapOutParallel.class, null, WrapOutParallel.Excludes);
	static BeanCopyTools<Service, WrapOutService> serviceCopier = BeanCopyToolsBuilder.create(Service.class,
			WrapOutService.class, null, WrapOutService.Excludes);
	static BeanCopyTools<Split, WrapOutSplit> splitCopier = BeanCopyToolsBuilder.create(Split.class, WrapOutSplit.class,
			null, WrapOutSplit.Excludes);
	static BeanCopyTools<Route, WrapOutRoute> routeCopier = BeanCopyToolsBuilder.create(Route.class, WrapOutRoute.class,
			null, WrapOutRoute.Excludes);
}
