package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.service.OkrConfigSecretaryService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.entity.OkrConfigSecretary;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	protected OkrConfigSecretaryService okrConfigSecretaryService = new OkrConfigSecretaryService();
	protected OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected Ehcache cache = ApplicationCache.instance().getCache( OkrConfigSecretary.class );
	protected String catchNamePrefix = this.getClass().getName();
}
