package com.x.cms.assemble.control.jaxrs.queryview;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.QueryView;


public class ActionFlag extends BaseAction {

	public ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			QueryView queryView = business.queryViewFactory().pick( flag );
			if( queryView == null ){
				Exception exception = new ExceptionQueryViewNotExists( flag );
				result.error( exception );
			}else{
				if (!business.queryViewFactory().allowRead( effectivePerson, queryView )) {
					Exception exception = new ExceptionInsufficientPermissions( flag );
					result.error( exception );
				}else{
					Wo wrap = Wo.copier.copy( queryView );
					result.setData(wrap);
				}
			}
			return result;
		}
	}

	public static class Wo extends QueryView {

		private static final long serialVersionUID = 2886873983211744188L;
		
		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		public static WrapCopier<QueryView, Wo> copier = WrapCopierFactory.wo( QueryView.class, Wo.class, null, JpaObject.FieldsInvisible );
	}

}