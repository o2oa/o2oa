package com.x.pan.assemble.control.jaxrs.folder3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.Folder3;

import java.io.ByteArrayOutputStream;
import java.util.*;

class ActionBatchDownload extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionBatchDownload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, List<String> attIds, List<String> folderIds) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			logger.debug("附件列表：{}#目录列表:{}",attIds,folderIds);
			Set<String> zoneIdSet = new HashSet<>();
			List<Attachment3> attList = new ArrayList<>();
			if(ListTools.isNotEmpty(attIds)){
				List<Attachment3> atts = emc.list(Attachment3.class, attIds);
				for(Attachment3 att : atts){
					String zoneId = business.getSystemConfig().getReadPermissionDown() ? att.getFolder() : att.getZoneId();
					if(zoneIdSet.contains(zoneId)){
						attList.add(att);
					}else if(business.zoneReadable(effectivePerson, zoneId)){
						attList.add(att);
						zoneIdSet.add(zoneId);
					}
				}
			}

			List<Folder3> folderList = new ArrayList<>();
			if(ListTools.isNotEmpty(folderIds)){
				List<Folder3> folders = emc.list(Folder3.class, folderIds);
				for(Folder3 folder : folders){
					String zoneId = business.getSystemConfig().getReadPermissionDown() ? folder.getId() : folder.getZoneId();
					if(zoneIdSet.contains(zoneId)){
						folderList.add(folder);
					}else if(business.zoneReadable(effectivePerson, zoneId)){
						folderList.add(folder);
						zoneIdSet.add(zoneId);
					}
				}
			}
			if (attList.isEmpty() && folderList.isEmpty()) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			String zipName = effectivePerson.getName() + DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss) + ".zip";
			logger.info("down to {}，att size {}, folder size {}",zipName,attList.size(),folderList.size());
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				this.fileCommonService.downToZip2(emc, effectivePerson, attList, folderList, os);
				byte[] bs = os.toByteArray();
				Wo wo = new Wo(bs, this.contentType(false,zipName),
						this.contentDisposition(false, zipName));
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
}
