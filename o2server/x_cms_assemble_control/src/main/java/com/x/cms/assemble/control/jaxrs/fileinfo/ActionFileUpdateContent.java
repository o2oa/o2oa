package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 附件信息修改
 * @author O2LEE
 *
 */
class ActionFileUpdateContent extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionFileUpdateContent.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		logger.debug("receive id:{}, jsonElement:{}.", id, jsonElement);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setId(id);
		result.setData(wo);
		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );
		if(StringUtils.isBlank(wi.getName()) && StringUtils.isBlank(wi.getSite())){
			return result;
		}
		Document doc;
		AppInfo appInfo;
		CategoryInfo categoryInfo;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			FileInfo file = emc.find(id, FileInfo.class);
			if(file == null){
				throw new ExceptionEntityNotExist(id);
			}
			doc = emc.find(file.getDocumentId(), Document.class);
			if(doc == null){
				throw new ExceptionDocumentNotExists(file.getDocumentId());
			}
			appInfo = emc.find(file.getAppId(), AppInfo.class);
			categoryInfo = emc.find(file.getCategoryId(), CategoryInfo.class);
		}

		if ( !documentQueryService.getFileInfoManagerAssess( effectivePerson, doc, categoryInfo, appInfo ) ) {
			throw new ExceptionDocumentAccessDenied(effectivePerson.getDistinguishedName(), doc.getTitle(), doc.getId());
		}

		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			FileInfo file = emc.find(id, FileInfo.class);
			if(StringUtils.isNotBlank(wi.getSite())){
				file.setSite(wi.getSite());
			}
			if(StringUtils.isNotBlank(wi.getName())){
				String ext = FilenameUtils.getExtension( wi.getName() );
				if(!StringTools.isFileName(wi.getName()) || !ext.equalsIgnoreCase(file.getExtension())){
					throw new ExceptionChangeNameDenied(wi.getName());
				}
				file.setName(wi.getName());
			}
			emc.beginTransaction(FileInfo.class);
			emc.commit();
		}

		return result;
	}

	public static class Wi extends FileInfo {

		private static final long serialVersionUID = -7323267705275836793L;
		static final WrapCopier<Wi, FileInfo> copier = WrapCopierFactory.wi(Wi.class, FileInfo.class,
				Arrays.asList(FileInfo.name_FIELDNAME, FileInfo.site_FIELDNAME),
				null);

	}

	public static class Wo extends WoId {

	}
}
