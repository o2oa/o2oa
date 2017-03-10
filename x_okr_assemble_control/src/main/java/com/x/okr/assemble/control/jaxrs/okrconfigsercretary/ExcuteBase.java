package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.okr.assemble.control.service.OkrConfigSecretaryService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.entity.OkrConfigSecretary;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected BeanCopyTools<OkrConfigSecretary, WrapOutOkrConfigSecretary> wrapout_copier = BeanCopyToolsBuilder.create( OkrConfigSecretary.class, WrapOutOkrConfigSecretary.class, null, WrapOutOkrConfigSecretary.Excludes);
	protected OkrConfigSecretaryService okrConfigSecretaryService = new OkrConfigSecretaryService();
	protected OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected Ehcache cache = ApplicationCache.instance().getCache( OkrConfigSecretary.class );
	protected String catchNamePrefix = this.getClass().getName();
}
