package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceAdmin;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		AttendanceAdmin attendanceAdmin = null;

		Business business = new Business(null);
		if(!business.isManager(effectivePerson)){
			throw new ExceptionAccessDenied(effectivePerson);
		}

		if (wrapIn.getUnitName() == null || wrapIn.getUnitName().isEmpty()) {
			String topUnitName = userManagerService.getTopUnitNameWithPersonName( effectivePerson.getDistinguishedName() );
			wrapIn.setUnitName(topUnitName);
		}

		attendanceAdmin = new AttendanceAdmin();
		wrapIn.copyTo( attendanceAdmin, JpaObject.FieldsUnmodify );
		if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
			attendanceAdmin.setId(wrapIn.getId());
		}

		if( StringUtils.isNotEmpty( attendanceAdmin.getAdmin()) ){
			Person person = userManagerService.getPersonObjByName(attendanceAdmin.getAdminName());
			if( person != null ){
				attendanceAdmin.setAdminName( person.getName() );
			}
		}

		if( StringUtils.isNotEmpty( attendanceAdmin.getAdminName()) ){
			if( StringUtils.isEmpty( attendanceAdmin.getAdmin()) ){
				Person person = userManagerService.getPersonObjByName(attendanceAdmin.getAdminName());
				if( person != null ){
					attendanceAdmin.setAdmin( person.getDistinguishedName() );
				}
			}
		}

		attendanceAdmin = attendanceAdminServiceAdv.save(attendanceAdmin);
		result.setData(new Wo(attendanceAdmin.getId()));
		return result;
	}

	public static class Wi extends AttendanceAdmin {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
