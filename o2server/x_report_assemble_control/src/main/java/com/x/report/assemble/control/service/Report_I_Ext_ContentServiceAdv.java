package com.x.report.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_I_Ext_Content;
import com.x.report.core.entity.Report_I_Ext_ContentDetail;


/**
 * 汇报扩展信息服务
 * @author O2LEE
 *
 */
public class Report_I_Ext_ContentServiceAdv{

	private Report_I_Ext_ContentService report_I_Ext_ContentService = new Report_I_Ext_ContentService();

	public Report_I_Ext_Content get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( id, Report_I_Ext_Content.class );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<Report_I_Ext_ContentDetail> listDetail(String contentId, String contentType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.report_I_Ext_ContentDetailFactory().getWithContentAndType(contentId, contentType);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<Report_I_Ext_Content> listWithReportId( String reportId, String infoLevel, String targetPerson ) throws Exception {
		if (reportId == null || reportId.isEmpty()) {
			throw new Exception("reportId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_Ext_ContentService.listWithReportId(emc, reportId, infoLevel, targetPerson );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<Report_I_Ext_Content> listWithReportId( String reportId ) throws Exception {
		if (reportId == null || reportId.isEmpty()) {
			throw new Exception("reportId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_Ext_ContentService.listWithReportId(emc, reportId );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> listWithReportIds( List<String> reportIds, String infoLevel, String targetPerson ) throws Exception {
		if ( ListTools.isEmpty( reportIds )) {
			throw new Exception("reportIds is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_Ext_ContentService.listWithReportIds(emc, reportIds, infoLevel, targetPerson );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<Report_I_Ext_Content> listWithInfoLevel( String infoLevel ) throws Exception {
		if (infoLevel == null || infoLevel.isEmpty()) {
			throw new Exception("infoLevel is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_Ext_ContentService.listWithInfoLevel(emc, infoLevel );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<Report_I_Ext_ContentDetail> listDetailWithContentId(String contentId) throws Exception {
		if (contentId == null || contentId.isEmpty()) {
			throw new Exception("contentId is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_Ext_ContentService.listDetailWithContentId(emc, contentId );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Report_I_Ext_Content save( Report_I_Ext_Content report_I_Ext_Content, List<Report_I_Ext_ContentDetail> details ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc);
			Report_I_Ext_Content contentEntity = null;
			Boolean exists = true;
			contentEntity = emc.find( report_I_Ext_Content.getId(), Report_I_Ext_Content.class );
			emc.beginTransaction( Report_I_Ext_Content.class );
			if( contentEntity == null ) {
				exists = false;
				contentEntity = new Report_I_Ext_Content();
			}
			report_I_Ext_Content.copyTo( contentEntity, JpaObject.FieldsUnmodify );
			if( StringUtils.isEmpty( contentEntity.getId() )) {
				contentEntity.setId( Report_I_Ext_ContentDetail.createId()  );	
			}
			if( contentEntity.getCreateTime() == null ) {
				contentEntity.setCreateTime( new Date());
			}
			if(exists) {
				emc.check( contentEntity, CheckPersistType.all );
			}else {
				emc.persist( contentEntity, CheckPersistType.all );
			}
			if( ListTools.isNotEmpty( details )) {
				emc.beginTransaction( Report_I_Ext_ContentDetail.class );
				Report_I_Ext_ContentDetail detailEntity = null;
				List<Report_I_Ext_ContentDetail> oldDetails = null;
				for( Report_I_Ext_ContentDetail detail : details ) {
					exists = true;
					//每个content里type相同的信息只有一个才对
					//detailEntity = emc.find( detail.getId(), Report_I_Ext_ContentDetail.class );
					oldDetails = business.report_I_Ext_ContentDetailFactory().getWithContentAndType(contentEntity.getId(), detail.getContentType() );
					if(ListTools.isEmpty(oldDetails)) {
						exists = false;
						detailEntity = new Report_I_Ext_ContentDetail();
					}
					//存在数据，有可能多余一条
					for( int i = 0; i<oldDetails.size(); i++) {
						if( i == 0 ) {
							detailEntity = oldDetails.get( i );
						}else {
							emc.remove( oldDetails.get( i ), CheckRemoveType.all );
						}
					}
					detail.copyTo( detailEntity, JpaObject.FieldsUnmodify );
					if( StringUtils.isEmpty( detailEntity.getId() )) {
						detailEntity.setId( Report_I_Ext_ContentDetail.createId() );	
					}
					detailEntity.setContentId( contentEntity.getId() );
					detailEntity.setProfileId(contentEntity.getProfileId());
					detailEntity.setReportId(contentEntity.getReportId());
					if( detailEntity.getCreateTime() == null ) {
						detailEntity.setCreateTime( new Date());
					}
					if(exists) {
						emc.check( detailEntity, CheckPersistType.all );
					}else {
						emc.persist( detailEntity, CheckPersistType.all );
					}
				}
			}
			emc.commit();
			return contentEntity;
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Report_I_Ext_ContentDetail saveDetail(Report_I_Ext_ContentDetail report_I_Ext_ContentDetail) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_Ext_ContentService.saveDetail( emc, report_I_Ext_ContentDetail );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete(String id, EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Report_I_Ext_Content entity =  emc.find( id, Report_I_Ext_Content.class );
			List<Report_I_Ext_ContentDetail> details = report_I_Ext_ContentService.listDetailWithContentId(emc, id);
			emc.beginTransaction( Report_I_Ext_Content.class );
			emc.beginTransaction( Report_I_Ext_ContentDetail.class );
			//删除所有的Detail
			if( ListTools.isNotEmpty( details ) ) {
				for( Report_I_Ext_ContentDetail detail : details ) {
					emc.remove( detail, CheckRemoveType.all );
				}
			}
			if( entity != null ) {
				emc.remove( entity, CheckRemoveType.all );
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Report_I_Ext_Content> list(List<String> ids) throws Exception {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_Ext_ContentService.list(emc, ids );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Boolean updateOrderNumber(String id, Integer orderNumber) throws Exception {
		if (StringUtils.isEmpty( id )) {
			throw new Exception("content id is null.");
		}
		if ( orderNumber == null ) {
			throw new Exception("orderNumber is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Report_I_Ext_Content entity = emc.find( id, Report_I_Ext_Content.class );
			if ( entity != null ) {
				emc.beginTransaction( Report_I_Ext_Content.class );
				entity.setOrderNumber(orderNumber);
				emc.check( entity, CheckPersistType.all );
				emc.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}	
}
