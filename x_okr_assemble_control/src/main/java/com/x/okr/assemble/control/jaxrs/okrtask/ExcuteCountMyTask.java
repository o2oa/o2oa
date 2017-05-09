package com.x.okr.assemble.control.jaxrs.okrtask;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.PersonNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.PersonQueryByFlagException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.TaskCountQueryException;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.organization.core.express.wrap.WrapPerson;

public class ExcuteCountMyTask extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteCountMyTask.class );
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String flag ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		List<String> notInTaskTypeList = new ArrayList<String>();
		WrapPerson person = null;
		Long taskCount = 0L;
		boolean check = true;
		
		if( check ){
			try {
				person = okrUserManagerService.getUserByUserNumber( flag );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new PersonQueryByFlagException( e, flag );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( person != null ){
				notInTaskTypeList.add( "工作汇报" );
				try{
					taskCount = okrTaskService.getTaskCountByUserName( null, notInTaskTypeList, person.getName() );
				}catch(Exception e){
					Exception exception = new TaskCountQueryException( e, flag );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				Exception exception = new PersonNotExistsException( flag );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		result.setCount( taskCount );
		return result;
	}
	
}