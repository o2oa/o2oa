package com.x.cms.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

/**
 * 对文档附件文件信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class FileInfoServiceAdv {

		private FileInfoService fileInfoService = new FileInfoService();

		public FileInfo get(String id) throws Exception {
			if( StringUtils.isEmpty( id ) ){
				throw new Exception("id is null!");
			}
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				return fileInfoService.get( emc, id );
			} catch ( Exception e ) {
				throw e;
			}
		}

		public List<FileInfo> getAttachmentList( String documentId ) throws Exception {
			if( documentId == null ){
				throw new Exception("documentId is null!");
			}

			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);		
				List<String> ids = business.getFileInfoFactory().listAttachmentByDocument( documentId );
				if( ListTools.isEmpty( ids ) ){
					return null;
				}
				return emc.list( FileInfo.class, ids );
			} catch ( Exception e ) {
				throw e;
			}
		}
		
		public List<FileInfo> getAllPictureList( String documentId ) throws Exception {
			if( StringUtils.isEmpty( documentId ) ){
				throw new Exception("documentId is null!");
			}
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);		
				List<String> ids = business.getFileInfoFactory().listPictureByDocument( documentId );
				if( ListTools.isEmpty( ids ) ){
					return null;
				}
				return emc.list( FileInfo.class, ids );
			} catch ( Exception e ) {
				throw e;
			}
		}
		
		public List<FileInfo> getCloudPictureList( String documentId ) throws Exception {
			if( StringUtils.isEmpty( documentId ) ){
				throw new Exception("documentId is null!");
			}

			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);		
				List<String> ids = business.getFileInfoFactory().listCloudPictureByDocument( documentId );
				if( ListTools.isEmpty( ids ) ){
					return null;
				}
				return emc.list( FileInfo.class, ids );
			} catch ( Exception e ) {
				throw e;
			}
		}
		
		/**
		 * 只是删除一条文件附件信息
		 * @param id
		 * @throws Exception 
		 */
		public void deleteFileInfo( String id ) throws Exception {
			if( StringUtils.isEmpty( id ) ){
				throw new Exception("id is null!");
			}
			FileInfo file = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction( FileInfo.class );
				file = emc.find( id, FileInfo.class ); 
				if( file !=null ){
					emc.remove( file, CheckRemoveType.all );
					emc.commit();
				}
			} catch ( Exception e ) {
				throw e;
			}
		}

		/**
		 * 保存一个新的云图片信息
		 * @param cloudPicture
		 * @param document
		 * @param index 
		 * @throws Exception
		 */
		public void saveCloudPicture( String cloudPictureId, Document document, int index ) throws Exception {
			if( StringUtils.isEmpty( cloudPictureId ) ){
				throw new Exception("cloudPicture is null!");
			}
			FileInfo file = new FileInfo();
			file.setSeqNumber( index );
			file.setId( FileInfo.createId() );
			file.setAppId( document.getAppId() );
			file.setCategoryId( document.getCategoryId() );			
			file.setCreatorUid( document.getCreatorPerson() );
			file.setDocumentId( document.getId() );
			file.setCloudId( cloudPictureId );
			file.setCreateTime( new Date() );
			file.setFileHost( "" );
			file.setFileName( cloudPictureId );
			file.setFilePath( "x_file_assemble_control/servlet/file/download/" + cloudPictureId );
			file.setFileExtType( "PICTURE" );
			file.setFileType( "CLOUD" ); //文件类别：云文件（CLOUD） | 附件(ATTACHMENT)			
			file.setName( cloudPictureId );
			file.setSite( "content" );
			file.setUpdateTime( new Date() );
			file.setLastUpdateTime( new Date() );
			file.setStorage("cms");
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction( FileInfo.class );
				emc.persist( file, CheckPersistType.all );
				emc.commit();
			} catch ( Exception e ) {
				throw e;
			}
		}

		public void updatePictureIndex(String id, int index) throws Exception {
			if( StringUtils.isEmpty( id ) ){
				throw new Exception("cloudPicture is null!");
			}
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				FileInfo file = emc.find( id, FileInfo.class );
				if( file != null ){
					emc.beginTransaction( FileInfo.class );
					file.setSeqNumber( index );
					emc.check( file, CheckPersistType.all );
					emc.commit();
				}
			} catch ( Exception e ) {
				throw e;
			}
			
		}

		public FileInfo saveAttachment(String docId, FileInfo attachment) throws Exception {
			if( StringUtils.isEmpty( docId ) ){
				throw new Exception("docId is null!");
			}
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Document document = emc.find( docId, Document.class );
				if( document != null ){
					emc.beginTransaction( FileInfo.class );
					emc.persist( attachment, CheckPersistType.all );
					emc.commit();
				}
			} catch ( Exception e ) {
				throw e;
			}
			return attachment;
		}

		public FileInfo updateAttachment(String docId, String old_attId, FileInfo attachment, StorageMapping mapping) throws Exception {
			if( StringUtils.isEmpty( docId ) ){
				throw new Exception("docId is null!");
			}
			if( StringUtils.isEmpty( old_attId ) ){
				throw new Exception("old_attId is null!");
			}
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Document document = emc.find( docId, Document.class );
				FileInfo old_fileInfo = emc.find( old_attId, FileInfo.class );
				if( document != null ){
					emc.beginTransaction( FileInfo.class );				
					old_fileInfo.setLastUpdateTime( new Date() );
					old_fileInfo.setExtension( attachment.getExtension() );
					old_fileInfo.setName( attachment.getName() );
					old_fileInfo.setFileName( attachment.getFileName() );
					old_fileInfo.setStorage( mapping.getName() );
					old_fileInfo.setAppId( document.getAppId() );
					old_fileInfo.setCategoryId( document.getCategoryId() );
					old_fileInfo.setDocumentId( document.getId() );
					old_fileInfo.setCreatorUid( attachment.getCreatorUid() );
					old_fileInfo.setSite( attachment.getSite() );
					old_fileInfo.setFileHost( attachment.getFileHost() );
					old_fileInfo.setFileType("ATTACHMENT");
					old_fileInfo.setFileExtType( attachment.getFileExtType()  );
					old_fileInfo.setFilePath( attachment.getFilePath() );					
					emc.check( old_fileInfo, CheckPersistType.all );
					emc.commit();
				}
			} catch ( Exception e ) {
				throw e;
			}
			return attachment;
		}

		public List<String> listIdsWithDocId(String documentId) throws Exception {
			if( StringUtils.isEmpty( documentId ) ){
				throw new Exception("documentId is null!");
			}
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);		
				return business.getFileInfoFactory().listAttachmentByDocument( documentId );
			} catch ( Exception e ) {
				throw e;
			}
		}	
}
