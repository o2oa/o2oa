package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

public class ActionPublish extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPublish.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<FileInfo> cloudPictures = null;
		Document document = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if (check) {
			try {
				document = documentInfoServiceAdv.get(id);
				if (null == document) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists(id);
					result.error(exception);
					throw exception;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,
						"文档信息获取操作时发生异常。Id:" + id + ", Name:" + effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				modifyDocStatus(id, "published", effectivePerson.getDistinguishedName());
				document.setDocStatus("published");
				document.setPublishTime(new Date());
				
				document = documentInfoServiceAdv.refreshDocInfoData( document );
				
				Wo wo = new Wo();
				wo.setId( document.getId() );
				result.setData( wo );
				
				//检查是否需要删除热点图片
				try {
					ThisApplication.queueDocumentUpdate.send( document );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
				
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统将文档状态修改为发布状态时发生异常。Id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
				throw exception;
			}
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				logService.log(emc, effectivePerson.getDistinguishedName(),
						document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(),
						document.getCategoryId(), document.getId(), "", "DOCUMENT", "发布");
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		// 处理文档的云文档图片信息
		if (check) {
			try {
				cloudPictures = fileInfoServiceAdv.getCloudPictureList(document.getId());
				if (cloudPictures == null) {
					cloudPictures = new ArrayList<>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在查询文档云图片信息时发生异常！ID:" + document.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			// 检查是否有需要删除的图片
			if (cloudPictures != null && !cloudPictures.isEmpty()) {
				boolean isExists = false;
				for (FileInfo picture : cloudPictures) {
					isExists = false;
					if (wi.getCloudPictures() != null && !wi.getCloudPictures().isEmpty()) {
						for (String cloudPictureId : wi.getCloudPictures()) {
							if (picture.getCloudId() != null && picture.getCloudId().equalsIgnoreCase(cloudPictureId)) {
								isExists = true;
							}
						}
					}
					if (!isExists) {
						try {
							fileInfoServiceAdv.deleteFileInfo(picture.getId());
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionDocumentInfoProcess(e,
									"系统在删除文档云图片信息时发生异常！ID:" + picture.getId());
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				}
			}
		}

		if (check) {
			// 检查是否有需要新添加的云图片信息
			if ( wi.getCloudPictures() != null && !wi.getCloudPictures().isEmpty() ) {
				boolean isExists = false;
				int index = 0;
				for (String cloudPictureId : wi.getCloudPictures()) {
					index++;
					isExists = false;
					for (FileInfo picture : cloudPictures) {
						if (picture.getCloudId() != null && picture.getCloudId().equalsIgnoreCase(cloudPictureId)) {
							isExists = true;
							fileInfoServiceAdv.updatePictureIndex(picture.getId(), index);
						}
					}
					if (!isExists) {
						try {
							// 说明原来的文件中不存在，需要添加一个新的云图片
							fileInfoServiceAdv.saveCloudPicture(cloudPictureId, document, index);
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionDocumentInfoProcess(e,
									"系统在新增文档云图片信息时发生异常！CLOUD_ID:" + cloudPictureId);
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				}
			}
		}

		//将读者以及作者信息持久化到数据库中
		try {
			documentInfoServiceAdv.refreshDocumentPermission( id, wi.getReaderList(), wi.getAuthorList() );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess(e, "系统在核对文档访问管理权限信息时发生异常！");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		ApplicationCache.notify(Document.class);

		return result;
	}

	public static class Wi extends Document {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier<Wi, Document> copier = WrapCopierFactory.wi( Wi.class, Document.class, null, JpaObject.FieldsUnmodify );
		
		private String identity = null;
		
		private String[] dataPaths = null;
		
		private String wf_jobId = null;
		
		private String wf_workId = null;
		
		private String[] wf_attachmentIds = null;	
		
		private Map<?, ?> docData = null;
		
		private List<PermissionInfo> readerList = null;
		
		private List<PermissionInfo> authorList = null;
		
		private List<String> cloudPictures = null;
		
		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public List<PermissionInfo> getReaderList() {
			return readerList;
		}

		public List<PermissionInfo> getAuthorList() {
			return authorList;
		}

		public void setReaderList(List<PermissionInfo> readerList) {
			this.readerList = readerList;
		}

		public void setAuthorList(List<PermissionInfo> authorList) {
			this.authorList = authorList;
		}

		public String[] getDataPaths() {
			if( dataPaths != null && dataPaths.length == 1 && dataPaths[0].equals("null")){
				return null;
			}
			return dataPaths;
		}

		public void setDataPaths(String[] dataPaths) {
			this.dataPaths = dataPaths;
		}

		public Map<?, ?> getDocData() {
			return docData;
		}

		public void setDocData(Map<?, ?> docData) {
			this.docData = docData;
		}

		public String getWf_jobId() {
			return wf_jobId;
		}

		public String getWf_workId() {
			return wf_workId;
		}

		public String[] getWf_attachmentIds() {
			return wf_attachmentIds;
		}

		public void setWf_jobId(String wf_jobId) {
			this.wf_jobId = wf_jobId;
		}

		public void setWf_workId(String wf_workId) {
			this.wf_workId = wf_workId;
		}

		public void setWf_attachmentIds(String[] wf_attachmentIds) {
			this.wf_attachmentIds = wf_attachmentIds;
		}

		public List<String> getCloudPictures() {
			return cloudPictures;
		}

		public void setCloudPictures(List<String> cloudPictures) {
			this.cloudPictures = cloudPictures;
		}

	}
	
	public static class Wo extends WoId {

	}
}