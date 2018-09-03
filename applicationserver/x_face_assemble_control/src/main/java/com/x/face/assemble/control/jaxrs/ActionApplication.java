package com.x.face.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.face.assemble.control.jaxrs.face.FaceAction;
import com.x.face.assemble.control.jaxrs.faceset.FaceSetAction;
import com.x.face.assemble.control.jaxrs.search.SearchAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(FaceAction.class);
		classes.add(FaceSetAction.class);
		classes.add(SearchAction.class);
		//		classes.add(PersonAction.class);
		//		classes.add(PersonAttributeAction.class);
		//		classes.add(IdentityAction.class);
		//		classes.add(GroupAction.class);
		//		classes.add(RoleAction.class);
		//		classes.add(FunctionAction.class);
		//		classes.add(LoginRecordAction.class);
		//		classes.add(InputPersonAction.class);
		//		classes.add(UnitAction.class);
		//		classes.add(UnitAttributeAction.class);
		//		classes.add(UnitDutyAction.class);
		return classes;
	}

}
