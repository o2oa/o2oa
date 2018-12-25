package com.x.okr.assemble.control.jaxrs.okrtask;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.dataadapter.wftask.WfReadCountGetter;
import com.x.okr.assemble.control.dataadapter.wftask.WfTaskCountGetter;
import com.x.okr.assemble.control.service.OkrUserManagerService;

/**
 * 获取指定人员的OKR待办数量
 * @author O2LEE
 *
 */
public class ActionCountMyTask extends BaseAction {

	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private static  Logger logger = LoggerFactory.getLogger( ActionCountMyTask.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String flag, String callbackName ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<String> notInTaskTypeList = new ArrayList<String>();
		String personName = null;
		Long taskCount = 0L;
		Boolean check = true;
		
		try {
			personName = okrUserManagerService.getPersonNameWithFlag( flag );
			if( StringUtils.isEmpty( personName )) {
				check = false;
				logger.warn("系统未能根据用户标识查询到指定的用户，标识(flag)：" + flag);
			}
		} catch ( Exception e ) {
			check = false;
			e.printStackTrace();
		}
		
		if(check && StringUtils.isNotEmpty( personName ) ) {
			notInTaskTypeList.add( "工作汇报" );
			try{
				taskCount = okrTaskService.getTaskCountByUserName( null, notInTaskTypeList, personName );
			}catch(Exception e){
				e.printStackTrace();
			}
			
			//获取该用户指定应用的待办数量
			try{
				taskCount += new WfTaskCountGetter().countWithProcess(personName);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			//获取该用户指定应用的待阅数量
			try{
				taskCount += new WfReadCountGetter().countWithProcess(personName);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		Wo woText = new Wo(callbackName + "("+taskCount+")") ;
		result.setData( woText );
		return result;
	}
	
	public static class Wo extends WoText {
		public Wo( String text ) {
			this.setText( text );
		}
	}
}