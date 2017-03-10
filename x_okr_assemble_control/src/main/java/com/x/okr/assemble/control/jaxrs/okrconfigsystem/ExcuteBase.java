package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrConfigSystem;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	protected BeanCopyTools<OkrConfigSystem, WrapOutOkrConfigSystem> wrapout_copier = BeanCopyToolsBuilder.create( OkrConfigSystem.class, WrapOutOkrConfigSystem.class, null, WrapOutOkrConfigSystem.Excludes);
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected Ehcache cache = ApplicationCache.instance().getCache( OkrConfigSystem.class );
	protected String catchNamePrefix = this.getClass().getName();
	
}
