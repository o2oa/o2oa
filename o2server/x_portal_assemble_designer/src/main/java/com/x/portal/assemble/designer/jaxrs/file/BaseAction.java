package com.x.portal.assemble.designer.jaxrs.file;

import java.util.Date;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.URLTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.File;
import java.util.concurrent.locks.ReentrantLock;

abstract class BaseAction extends StandardJaxrsAction {
	private static final ReentrantLock lock = new ReentrantLock();
	protected void updateCreator(File file,EffectivePerson effectivePerson){
		file.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		file.setLastUpdateTime(new Date());
	}

	String getShortUrlCode(Business business, File file, int length) throws Exception{
		lock.lock();
		try {
			String code = URLTools.shortUrl(file.getId(), length);
			if (business.entityManagerContainer().duplicateWithFlags(file.getId(), File.class, code)) {
				length++;
				return getShortUrlCode(business, file, length);
			}else{
				return code;
			}
		} finally {
			lock.unlock();
		}
	}

}
