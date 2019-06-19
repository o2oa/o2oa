package com.x.cms.assemble.control.jaxrs.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.fileinfo.ExceptionFileInfoContentRead;
import com.x.cms.assemble.control.jaxrs.fileinfo.ExceptionResponseHeaderSet;
import com.x.cms.common.image.compression.ImageResizeAndCompression;
import com.x.cms.core.entity.FileInfo;

public class ActionImageChangeSize extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionImageChangeSize.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, 
			String fileId, Integer width, Integer height ) {
		ActionResult<Wo> result = new ActionResult<>();
		ByteArrayOutputStream baos = null;
		FileInfo fileInfo = null;
		StorageMapping mapping = null;
		ByteArrayInputStream bais = null;
		BufferedImage image = null;
		byte[] byteArray = null;
		Boolean check = true;
		
		if (check) {
			try {
				// 校验文件和文档是正存在
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					fileInfo = emc.find(fileId, FileInfo.class);
					if (null == fileInfo) {
						throw new Exception("fileInfo{id" + fileId + "} not existed.");
					}
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				mapping = ThisApplication.context().storageMappings().get( FileInfo.class, fileInfo.getStorage() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionResponseHeaderSet(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				baos = new ByteArrayOutputStream();
				fileInfo.readContent( mapping, baos );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFileInfoContentRead(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if( baos != null ){
				try{
					bais = new ByteArrayInputStream( baos.toByteArray() );
					image = ImageIO.read( bais );
					if( width != 0 && height != 0 ){
						image = ImageResizeAndCompression.cut( image, width, height);
					}
					ImageIO.write( image, "png", baos );
					byteArray = baos.toByteArray();
					baos.flush();
					
					Wo wo = new Wo(byteArray, 
							this.contentType(false, fileInfo.getFileName()), 
							this.contentDisposition(false, fileInfo.getName()));
					
					result.setData( wo );
				}catch( Exception e ){
					e.printStackTrace();
				}finally{
					try{ 
						baos.close();
					}catch( Exception e ){
						e.printStackTrace();
					}
					try{ 
						bais.close();
					}catch( Exception e ){
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}
	
	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}
}
