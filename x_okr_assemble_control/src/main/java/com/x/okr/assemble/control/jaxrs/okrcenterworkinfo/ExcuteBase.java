package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrConfigWorkTypeService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkPersonSearchService;
import com.x.okr.assemble.control.service.OkrWorkProcessIdentityService;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteBase.class );
	protected BeanCopyTools<OkrCenterWorkInfo, WrapOutOkrCenterWorkInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrCenterWorkInfo.class, WrapOutOkrCenterWorkInfo.class, null, WrapOutOkrCenterWorkInfo.Excludes);
	protected BeanCopyTools<OkrCenterWorkInfo, WrapOutOkrCenterWorkViewInfo> wrapout_view_copier = BeanCopyToolsBuilder.create( OkrCenterWorkInfo.class, WrapOutOkrCenterWorkViewInfo.class, null, WrapOutOkrCenterWorkViewInfo.Excludes);
	protected OkrWorkProcessIdentityService okrWorkProcessIdentityService = new OkrWorkProcessIdentityService();
	protected OkrCenterWorkQueryService okrCenterWorkQueryService = new OkrCenterWorkQueryService();
	protected OkrWorkPersonSearchService okrWorkPersonSearchService = new OkrWorkPersonSearchService();
	protected OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	protected OkrConfigWorkTypeService okrConfigWorkTypeService = new OkrConfigWorkTypeService();
	protected DateOperation dateOperation = new DateOperation();
	
	protected OkrUserCache checkUserLogin(String name) {
		OkrUserCache okrUserCache = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( name );
		} catch (Exception e) {
			logger.warn( "system get login indentity with person name got an exception" );
			logger.error(e);
			return null;
		}
		if(  okrUserCache == null || okrUserCache.getLoginIdentityName() == null ){
			return null;
		}
		if( okrUserCache.getLoginUserName() == null ){
			return null;
		}
		if( okrUserCache.getLoginUserOrganizationName() == null ){
			return null;
		}
		if( okrUserCache.getLoginUserCompanyName() == null ){
			return null;
		}
		return okrUserCache;
	}
}
