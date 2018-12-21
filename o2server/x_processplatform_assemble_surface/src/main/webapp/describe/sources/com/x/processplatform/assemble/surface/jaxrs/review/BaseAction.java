package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutReview;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

public class BaseAction extends StandardJaxrsAction {

	protected static WrapCopier<Review, WrapOutReview> reviewOutCopier = WrapCopierFactory.wo(Review.class,
			WrapOutReview.class, null, JpaObject.FieldsInvisible);

	protected static WrapCopier<Work, WrapOutWork> workOutCopier = WrapCopierFactory.wo(Work.class, WrapOutWork.class,
			null, WrapOutWork.Excludes);

	protected static WrapCopier<WorkCompleted, WrapOutWorkCompleted> workCompletedOutCopier = WrapCopierFactory
			.wo(WorkCompleted.class, WrapOutWorkCompleted.class, null, WrapOutWorkCompleted.Excludes);

}