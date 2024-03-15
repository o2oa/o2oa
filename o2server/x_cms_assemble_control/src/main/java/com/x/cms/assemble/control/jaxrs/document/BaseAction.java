package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.service.*;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommentInfo;
import com.x.query.core.entity.Item;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseAction extends StandardJaxrsAction {

	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(Item.class, Document.class, DocumentCommentInfo.class);

	protected LogService logService = new LogService();
	protected DocumentViewRecordServiceAdv documentViewRecordServiceAdv = new DocumentViewRecordServiceAdv();
	protected DocumentPersistService documentPersistService = new DocumentPersistService();
	protected DocumentQueryService documentQueryService = new DocumentQueryService();

	protected DocCommendPersistService docCommendPersistService = new DocCommendPersistService();
	protected DocCommendQueryService docCommendQueryService = new DocCommendQueryService();

	protected FormServiceAdv formServiceAdv = new FormServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	protected FileInfoServiceAdv fileInfoServiceAdv = new FileInfoServiceAdv();
	protected PermissionQueryService permissionQueryService = new PermissionQueryService();

	protected boolean modifyDocStatus( String id, String stauts, String personName ) throws Exception{
		Business business = null;
		Document document = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);

			//进行数据库持久化操作
			emc.beginTransaction( Document.class );
			document = business.getDocumentFactory().get(id);
			if (null != document) {
				//修改文档状态
				document.setDocStatus( stauts );
				if(Document.DOC_STATUS_PUBLISH.equals(stauts)) {
					document.setPublishTime(new Date());
				}
				//保存文档信息
				emc.check( document, CheckPersistType.all);
			}
			emc.commit();
			return true;
		} catch (Exception th) {
			throw th;
		}
	}

	protected String getShortTargetFlag(String distinguishedName) {
		String target = null;
		if( StringUtils.isNotEmpty( distinguishedName ) ){
			String[] array = distinguishedName.split("@");
			StringBuffer sb = new StringBuffer();
			if( array.length == 3 ){
				target = sb.append(array[1]).append("@").append(array[2]).toString();
			}else if( array.length == 2 ){
				//2段
				target = sb.append(array[0]).append("@").append(array[1]).toString();
			}else{
				target = array[0];
			}
		}
		return target;
	}

	protected List<String> getShortTargetFlag(List<String> nameList) {
		List<String> targetList = new ArrayList<>();
		if( ListTools.isNotEmpty( nameList ) ){
			for(String distinguishedName : nameList) {
				String target = distinguishedName;
				String[] array = distinguishedName.split("@");
				StringBuffer sb = new StringBuffer();
				if (array.length == 3) {
					target = sb.append(array[1]).append("@").append(array[2]).toString();
				} else if (array.length == 2) {
					//2段
					target = sb.append(array[0]).append("@").append(array[1]).toString();
				} else {
					target = array[0];
				}
				targetList.add(target);
			}
		}
		return targetList;
	}

}
