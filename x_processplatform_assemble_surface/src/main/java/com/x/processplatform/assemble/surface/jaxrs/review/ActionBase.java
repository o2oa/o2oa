package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutReview;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

public class ActionBase extends StandardJaxrsAction {

	protected static BeanCopyTools<Review, WrapOutReview> reviewOutCopier = BeanCopyToolsBuilder.create(Review.class,
			WrapOutReview.class);

	protected static BeanCopyTools<Work, WrapOutWork> workOutCopier = BeanCopyToolsBuilder.create(Work.class,
			WrapOutWork.class, null, WrapOutWork.Excludes);

	protected static BeanCopyTools<WorkCompleted, WrapOutWorkCompleted> workCompletedOutCopier = BeanCopyToolsBuilder
			.create(WorkCompleted.class, WrapOutWorkCompleted.class, null, WrapOutWorkCompleted.Excludes);

}