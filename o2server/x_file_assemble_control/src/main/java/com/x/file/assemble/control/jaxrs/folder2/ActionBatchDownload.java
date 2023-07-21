package com.x.file.assemble.control.jaxrs.folder2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;

class ActionBatchDownload extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionBatchDownload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, List<String> attIds, List<String> folderIds) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			logger.debug("附件列表：{}#目录列表:{}",attIds,folderIds);
			List<Attachment2> attList = new ArrayList<>();
			if(attIds!=null && !attIds.isEmpty()){
				List<Attachment2> atts = emc.list(Attachment2.class, attIds);
				if(atts!=null){
					for(Attachment2 att : atts){
						if (StringUtils.equals(att.getPerson(), effectivePerson.getDistinguishedName())) {
							attList.add(att);
						}
					}
				}
			}
			List<Folder2> folderList = new ArrayList<>();
			if(folderIds!=null && !folderIds.isEmpty()){
				List<Folder2> folders = emc.list(Folder2.class, folderIds);
				if(folders!=null){
					for(Folder2 folder : folders){
						if (StringUtils.equals(folder.getPerson(), effectivePerson.getDistinguishedName())) {
							folderList.add(folder);
						}
					}
				}
			}
			if (attList.isEmpty() && folderList.isEmpty()) {
				throw new Exception("attIds or folderId can not empty or person{name:" + effectivePerson.getDistinguishedName() + "} access all denied.");
			}
			String zipName = effectivePerson.getName() + DateTools.format(new Date(),DateTools.formatCompact_yyyyMMddHHmmss) + ".zip";
			logger.info("down to {}，att size {}, folder size {}",zipName,attList.size(),folderList.size());
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				this.fileCommonService.downToZip(emc, attList, folderList, os);
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