package com.x.teamwork.assemble.control.jaxrs.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.SystemConfig;

public class ActionGet extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		SystemConfig systemConfig = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionConfigIdEmpty();
				result.error( exception );
			}
		}

		if( check ){
			try {
				systemConfig = systemConfigQueryService.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionConfigInfoProcess( e, "系统根据ID查询指定考勤系统配置信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( systemConfig == null ) {
				try {
					systemConfig = systemConfigQueryService.getByCode( id );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionConfigInfoProcess( e, "系统根据ID查询指定考勤系统配置信息时发生异常.CODE:" + id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			try {
				wrap = Wo.copier.copy( systemConfig );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionConfigInfoProcess( e, "将所有查询到的考勤配置信息对象转换为可以输出的信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends SystemConfig  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<SystemConfig, Wo> copier = WrapCopierFactory.wo( SystemConfig.class, Wo.class, null,Wo.Excludes);
	}

}