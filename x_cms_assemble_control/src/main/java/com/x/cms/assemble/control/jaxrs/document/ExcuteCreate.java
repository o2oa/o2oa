package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.organization.core.express.wrap.WrapIdentity;

public class ExcuteCreate extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteCreate.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, WrapInDocument wrapIn, EffectivePerson effectivePerson) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		List<WrapIdentity> identities = null;
		WrapIdentity wrapIdentity = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Boolean check = true;
		String identity = wrapIn.getIdentity();
		
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {			
				Business business = new Business(emc);
				if( !"xadmin".equalsIgnoreCase( effectivePerson.getName()) ){
					//先查询用户所有的身份，再根据身份查询用户的部门信息
					identities = business.organization().identity().listWithPerson( effectivePerson.getName() );
					if ( identities.size() == 0 ) {//该员工目前没有分配身份
						check = false;
						Exception exception = new PersonHasNoIdentityException( effectivePerson.getName() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					} else if ( identities.size() == 1 ) {
						wrapIdentity = identities.get(0);
					} else {
						wrapIdentity = this.findIdentity( identities, identity );
						if ( null == wrapIdentity ) {
							check = false;
							Exception exception = new PersonIdentityInvalidException( identity );
							result.error( exception );
							logger.error( exception, effectivePerson, request, null);
						}
					}
				}			
			} catch (Exception e) {
				check = false;
				Exception exception = new PersonIdentityQueryException( e, identity );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
				check = false;
				Exception exception = new DocumentTitleEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getCategoryId() == null || wrapIn.getCategoryId().isEmpty() ){
				check = false;
				Exception exception = new DocumentCategoryIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				categoryInfo = categoryInfoServiceAdv.get( wrapIn.getCategoryId() );
				if( categoryInfo == null ){
					check = false;
					Exception exception = new CategoryInfoNotExistsException( wrapIn.getCategoryId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}catch(Exception e){
				check = false;
				Exception exception = new CategoryInfoQueryByIdException( e, wrapIn.getCategoryId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}

		if( check ){
			try {
				document = WrapTools.document_wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				check = false;
				Exception exception = new DocumentWrapInException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getId() != null && wrapIn.getId().length() > 10){
				document.setId( wrapIn.getId() );
			}
			document.setAppId( categoryInfo.getAppId() );
			document.setCategoryName( categoryInfo.getCategoryName() );
			document.setCategoryId( categoryInfo.getId() );
		}
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				if( wrapIdentity != null){
					document.setCreatorIdentity( business.organization().identity().getWithName( wrapIdentity.getName() ).getName() );
					document.setCreatorPerson( business.organization().person().getWithIdentity( wrapIdentity.getName() ).getName() );
					document.setCreatorDepartment( business.organization().department().getWithIdentity( wrapIdentity.getName() ).getName() );
					document.setCreatorCompany( business.organization().company().getWithIdentity( wrapIdentity.getName() ).getName() );
				}else{
					if( "xadmin".equalsIgnoreCase( effectivePerson.getName()) ){
						document.setCreatorIdentity( "xadmin" );
						document.setCreatorPerson( "xadmin" );
						document.setCreatorDepartment( "xadmin" );
						document.setCreatorCompany( "xadmin" );
					}else{
						Exception exception = new PersonHasNoIdentityException( effectivePerson.getName() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}			
				emc.beginTransaction( Document.class );
				emc.persist( document, CheckPersistType.all );
				emc.commit();
				ApplicationCache.notify( Document.class );
				
				logService.log( emc, effectivePerson.getName(), document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "新增" );
				wrap = new WrapOutId( document.getId() );
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}
	
}