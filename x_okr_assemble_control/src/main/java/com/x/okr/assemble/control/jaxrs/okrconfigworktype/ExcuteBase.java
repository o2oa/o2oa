package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrConfigWorkTypeService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkPersonSearchService;
import com.x.okr.assemble.control.service.OkrWorkPersonService;
import com.x.okr.entity.OkrConfigWorkType;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected BeanCopyTools<OkrConfigWorkType, WrapOutOkrConfigWorkType> wrapout_copier = BeanCopyToolsBuilder.create( OkrConfigWorkType.class, WrapOutOkrConfigWorkType.class, null, WrapOutOkrConfigWorkType.Excludes);
	protected OkrConfigWorkTypeService okrConfigWorkTypeService = new OkrConfigWorkTypeService();
	protected OkrWorkPersonSearchService okrWorkPersonSearchService = new OkrWorkPersonSearchService();
	protected OkrCenterWorkQueryService okrCenterWorkQueryService = new OkrCenterWorkQueryService();
	protected OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected Ehcache cache = ApplicationCache.instance().getCache( OkrConfigWorkType.class );
	protected String catchNamePrefix = this.getClass().getName();
	
}
