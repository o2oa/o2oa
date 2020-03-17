//package com.x.processplatform.assemble.designer.jaxrs.projection;
//
//import java.util.List;
//
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.entity.JpaObject;
//import com.x.base.core.project.annotation.FieldDescribe;
//import com.x.base.core.project.bean.WrapCopier;
//import com.x.base.core.project.bean.WrapCopierFactory;
//import com.x.base.core.project.exception.ExceptionAccessDenied;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.processplatform.assemble.designer.Business;
//import com.x.processplatform.core.entity.element.Projection;
//
//class ActionListPrev extends BaseAction {
//	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
//		ActionResult<List<Wo>> result = new ActionResult<>();
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			if (!business.editable(effectivePerson, null)) {
//				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
//			}
//		}
//		result = this.standardListPrev(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, null, null, null, null, null,
//				null, null, null, true, DESC);
//		return result;
//	}
//
//	public static class Wo extends Projection {
//
//		private static final long serialVersionUID = -7495725325510376323L;
//
//		public static WrapCopier<Projection, Wo> copier = WrapCopierFactory.wo(Projection.class, Wo.class,
//				JpaObject.singularAttributeField(Projection.class, true, true), null);
//
//		@FieldDescribe("排序号")
//		private Long rank;
//
//		public Long getRank() {
//			return rank;
//		}
//
//		public void setRank(Long rank) {
//			this.rank = rank;
//		}
//
//	}
//}
