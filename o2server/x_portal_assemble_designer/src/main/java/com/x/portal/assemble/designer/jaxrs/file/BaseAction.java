package com.x.portal.assemble.designer.jaxrs.file;

import java.util.Date;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.URLTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.File;

abstract class BaseAction extends StandardJaxrsAction {

	protected void updateCreator(File file,EffectivePerson effectivePerson){
		file.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		file.setLastUpdateTime(new Date());
	}

	String getShortUrlCode(Business business, File file, int length) throws Exception{
		String code = URLTools.shortUrl(file.getId(), length);
		if (business.entityManagerContainer().duplicateWithFlags(file.getId(), File.class, code)) {
			length++;
			return getShortUrlCode(business, file, length);
		}else{
			return code;
		}
	}

}
