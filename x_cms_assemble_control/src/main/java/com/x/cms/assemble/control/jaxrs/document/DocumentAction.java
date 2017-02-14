package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpAttribute;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.CatagoryInfoFactory;
import com.x.cms.assemble.control.factory.DocumentFactory;
import com.x.cms.assemble.control.jaxrs.log.WrapOutLog;
import com.x.cms.assemble.control.service.CatagoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.CatagoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.content.DataItem;
import com.x.cms.core.entity.content.tools.DataHelper;
import com.x.cms.core.entity.query.ScopeType;
import com.x.organization.core.express.wrap.WrapIdentity;

@Path("document")
public class DocumentAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( DocumentAction.class );
	private LogService logService = new LogService();
	private DocumentInfoServiceAdv documentInfoServiceAdv = new DocumentInfoServiceAdv();
	private CatagoryInfoServiceAdv catagoryInfoServiceAdv = new CatagoryInfoServiceAdv();
	private BeanCopyTools<Document, WrapOutDocument> wrapout_copier = BeanCopyToolsBuilder.create(Document.class, WrapOutDocument.class, null, WrapOutDocument.Excludes);
	private BeanCopyTools<WrapInDocument, Document> wrapin_copier = BeanCopyToolsBuilder.create( WrapInDocument.class, Document.class, null, WrapInDocument.Excludes );
	private BeanCopyTools<FileInfo, WrapOutDocumentComplexFile> wrapout_file_copier = BeanCopyToolsBuilder.create(FileInfo.class, WrapOutDocumentComplexFile.class, null, WrapOutDocumentComplexFile.Excludes);
		
	@HttpMethodDescribe(value = "根据ID获取document对象详细信息，包括附件列表，数据信息.", response = WrapOutDocument.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutDocumentComplex> result = new ActionResult<>();
		WrapOutDocumentComplex wrap = new WrapOutDocumentComplex();
		WrapOutDocument wrapOutDocument  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		CatagoryInfo catagoryInfo = null;
		Document document = null;
		List<FileInfo> attachmentList = null;
		Boolean check = true;
		
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception("query parameter id can not null!") );
			result.setUserMessage( "系统未获取到传入的参数id." );
		}
		if( check ){
			try {
				document = documentInfoServiceAdv.view( id, currentPerson );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据ID获取文档信息时发生异常." );
				logger.error( "system query document info with id got an exception.id:" + id , e  );
			}
		}
		if( check ){
			if( document != null ){
				try {
					wrapOutDocument = wrapout_copier.copy( document );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统转换文档信息为输出格式时发生异常." );
					logger.error( "system copy documemnt to wrap out got an exception." , e  );
				}
			}
		}
		if( check ){
			if( wrapOutDocument != null ){
				try {
					catagoryInfo = catagoryInfoServiceAdv.get( document.getCatagoryId() );
					wrapOutDocument.setCatagoryName( catagoryInfo.getCatagoryName());
					wrapOutDocument.setCatagoryAlias( catagoryInfo.getCatagoryAlias());
					wrap.setDocument( wrapOutDocument );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统根据分类ID获取分类信息时发生异常." );
					logger.error( "system query catagory with id got an exception.id:" + document.getCatagoryId() , e  );
				}
			}
		}
		if( check ){
			if( wrapOutDocument != null ){
				try {
					wrap.setData( documentInfoServiceAdv.getDocumentData( document ) );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在获取文档内容时发生异常." );
					logger.error( "system query data for document got an exception.id:" + wrapOutDocument.getId() , e  );
				}
			}
		}
		if( check ){
			try {
				attachmentList = documentInfoServiceAdv.getAttachmentList( document );
				if( attachmentList != null && !attachmentList.isEmpty() ){
					wrap.setAttachmentList( wrapout_file_copier.copy( attachmentList ) );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在获取文档附件信息列表时发生异常." );
				logger.error( "system query attachment list for document got an exception.id:" + wrapOutDocument.getId() , e  );
			}
		}
		if( check ){
			wrap.setDocumentLogList( new ArrayList< WrapOutLog >() );
		}
		result.setData( wrap );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	
	@HttpMethodDescribe(value = "根据ID发布文档信息.", response = WrapOutId.class)
	@PUT
	@Path("publish/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response publish(@Context HttpServletRequest request, @PathParam("id") String id) {		
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		List<DataItem> dataItemList = null;
		EffectivePerson currentPerson = this.effectivePerson(request);	
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = business.getDocumentFactory().get(id);
			if ( null == document ) {
				throw new Exception("[publish]document{id:" + id + "} not existed.");
			}
			try {
				modifyDocStatus( id, "published" );
			} catch (Exception e) {
				logger.error( "[publish]用户[" + currentPerson.getName() + "]发布文档操作失败。", e );
			}			
			//删除与该文档有关的所有数据信息
			dataItemList = business.getDataItemFactory().listWithDocIdWithPath( id );
			if( dataItemList != null && !dataItemList.isEmpty() ){
				emc.beginTransaction( DataItem.class );
				for( DataItem dataItem : dataItemList ){
					dataItem.setDocStatus( ScopeType.published.toString() );
					dataItem.setPublishTime( new Date() );
					emc.check( dataItem, CheckPersistType.all );
				}
				emc.commit();
			}
			logger.debug("[publish]System trying to save log......" );
			emc.beginTransaction( Log.class);
			logService.log( emc, currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功发布一个文档信息", document.getAppId(), document.getCatagoryId(), document.getId(), "", "DOCUMENT", "删除" );
			emc.commit();
			
			wrap = new WrapOutId( document.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID取消文档信息发布.", response = WrapOutId.class)
	@PUT
	@Path("publish/{id}/cancel")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response publishCancel(@Context HttpServletRequest request, @PathParam("id") String id) {		
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);	
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			
			Document document = business.getDocumentFactory().get(id);
			if ( null == document ) {
				throw new Exception("[publishCancel]document{id:" + id + "} not existed.");
			}
			try {
				modifyDocStatus(id, "canceled");
			} catch (Exception e) {
				logger.error( "[publishCancel]用户[" + currentPerson.getName() + "]取消文档发布状态操作失败。", e );
			}
			logService.log( emc, currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功取消一个文档信息的发布状态", document.getAppId(), document.getCatagoryId(), document.getId(), "", "DOCUMENT", "取消发布" );			
			wrap = new WrapOutId( document.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID取消文档信息发布.", response = WrapOutId.class)
	@PUT
	@Path("achive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response achive(@Context HttpServletRequest request, @PathParam("id") String id) {		
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);	
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			
			Document document = business.getDocumentFactory().get(id);
			if ( null == document ) {
				throw new Exception("[achive]document{id:" + id + "} not existed.");
			}
			
			logger.debug("[achive]用户[" + currentPerson.getName() + "] 尝试归档文档信息......" );
			try {
				modifyDocStatus(id, "archived");
			} catch (Exception e) {
				logger.error( "[achive]用户[" + currentPerson.getName() + "]归档文档操作失败。", e );
			}
			
			logger.debug("[achive]System trying to save log......" );
			emc.beginTransaction( Log.class);
			logService.log( emc, currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功归档一个文档信息", document.getAppId(), document.getCatagoryId(), document.getId(), "", "DOCUMENT", "删除" );
			emc.commit();
			
			wrap = new WrapOutId( document.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID将文档信息保存为草稿.", response = WrapOutId.class)
	@PUT
	@Path("draft/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response draft(@Context HttpServletRequest request, @PathParam("id") String id) {		
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);	
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			
			Document document = business.getDocumentFactory().get( id );
			if ( null == document ) {
				throw new Exception("[draft]document{id:" + id + "} not existed.");
			}
			try {
				modifyDocStatus(id, "draft");
			} catch (Exception e) {
				logger.error( "[draft]用户[" + currentPerson.getName() + "]修改文档为草稿操作失败。", e );
			}
			emc.beginTransaction( Log.class);
			logService.log( emc, currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功将一个文档信息存为草稿", document.getAppId(), document.getCatagoryId(), document.getId(), "", "DOCUMENT", "删除" );
			emc.commit();
			
			wrap = new WrapOutId( document.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	private boolean modifyDocStatus( String id, String stauts ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);			
			//先判断需要操作的文档信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			Document document = business.getDocumentFactory().get(id);
			if (null == document) {
				throw new Exception("[delete]document{id:" + id + "} not existed.");
			}		
			//进行数据库持久化操作
			emc.beginTransaction( Document.class );
			//修改状态
			document.setDocStatus( stauts );
			document.setPublishTime( null );
			//保存文档信息
			emc.check( document, CheckPersistType.all);			
			emc.commit();			
			return true;
		} catch (Exception th) {
			throw th;
		}
	}
	
	@HttpMethodDescribe(value = "创建Document文档信息对象.", request = WrapInDocument.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest request, WrapInDocument wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		List<WrapIdentity> identities = null;
		WrapIdentity wrapIdentity = null;
		String identity = wrapIn.getIdentity();
		//获取到当前用户信息
		EffectivePerson currentPerson = this.effectivePerson( request );
		boolean isXAdmin = false;
		
		logger.debug("[create]user[" + currentPerson.getName() + "] try to save document......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			isXAdmin = business.isXAdmin(request, currentPerson);
			if( !"xadmin".equalsIgnoreCase( currentPerson.getName()) ){
				//先查询用户所有的身份，再根据身份查询用户的部门信息
				identities = business.organization().identity().listWithPerson(currentPerson.getName());
				if (identities.size() == 0) {//该员工目前没有分配身份
					throw new Exception("[create]can not get identity of person:" + currentPerson.getName() + ".");
				} else if (identities.size() == 1) {
					wrapIdentity = identities.get(0);
					logger.debug("[create]find user identity[ "+identity+" ] for user{'person':'"+currentPerson.getName()+"'}." );
				} else {
					//有多个身份需要逐一判断是否包含. 
					wrapIdentity = this.findIdentity(identities, identity);
					logger.debug("[create]find "+identities.size()+" user identity for user{'person':'"+currentPerson.getName()+"'}." );
					if ( null == wrapIdentity ) {
						throw new Exception( "person{name:" + currentPerson.getName() + "} not contians identity{name:" + identity + "}.");
					}
				}
			}			
		} catch (Exception e) {
			logger.error( "[create]用户在保存文档信息时发生异常。", e );
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = new Document();
			try {
				document = wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				logger.error("[create]根据传入的参数创建文档对象发生异常！", e);
			}
			if( wrapIn.getId() != null && wrapIn.getId().length() > 10){
				document.setId( wrapIn.getId() );
			}
			if( wrapIdentity != null){
				logger.debug( "[create]wrapIdentity.getName() = " + wrapIdentity.getName() );
				document.setCreatorIdentity( business.organization().identity().getWithName( wrapIdentity.getName() ).getName() );
				document.setCreatorPerson( business.organization().person().getWithIdentity( wrapIdentity.getName() ).getName() );
				document.setCreatorDepartment( business.organization().department().getWithIdentity( wrapIdentity.getName() ).getName() );
				document.setCreatorCompany( business.organization().company().getWithIdentity( wrapIdentity.getName() ).getName() );
			}else{
				if( "xadmin".equalsIgnoreCase( currentPerson.getName()) ){
					document.setCreatorIdentity( "xadmin" );
					document.setCreatorPerson( "xadmin" );
					document.setCreatorDepartment( "xadmin" );
					document.setCreatorCompany( "xadmin" );
				}else{
					logger.error("[create]系统无法获取用户[" + currentPerson.getName() + "]身份，无法进行文档数据保存!");
					result.error(new Exception("[create]系统无法获取用户身份，无法进行文档数据保存!"));
				}
			}
			emc.beginTransaction( Document.class );
			emc.persist( document, CheckPersistType.all );
			emc.commit();
			logService.log( emc, currentPerson.getName(), "user[" + currentPerson.getName() + "]成功创建一个文档信息", document.getAppId(), document.getCatagoryId(), document.getId(), "", "DOCUMENT", "新增" );
			wrap = new WrapOutId( document.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除Document文档信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {		
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		DataHelper dataHelper = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[delete]用户[" + currentPerson.getName() + "] 尝试删除文档信息......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );
			
			//先判断需要操作的文档信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			Document document = business.getDocumentFactory().get(id);
			if (null == document) {
				throw new Exception("[delete]document{id:" + id + "} not existed.");
			}
			logger.debug("[delete]System trying to beginTransaction to delete document......" );
			
			//进行数据库持久化操作
			emc.beginTransaction( Document.class );
			emc.beginTransaction( DataItem.class );
			
			//删除与该文档有关的所有数据信息
			dataHelper = new DataHelper( business.entityManagerContainer(), document.getAppId(), document.getCatagoryId(), document.getId() );
			dataHelper.remove();
			//删除文档信息
			logger.debug("[delete]System try to delete document info......" );
			emc.remove( document, CheckRemoveType.all );
			
			emc.commit();
			logger.debug("System delete document success." );
			//成功删除一个文档信息
			
			logger.debug("[delete]System trying to save log......" );
			emc.beginTransaction( Log.class);
			logService.log( emc, currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功删除一个文档信息", document.getAppId(), document.getCatagoryId(), document.getId(), "", "DOCUMENT", "删除" );
			emc.commit();
			wrap = new WrapOutId( document.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新Document文档信息对象.", request = WrapInDocument.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInDocument wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[put]user[" + currentPerson.getName() + "] try to save document......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			
			//先判断需要操作的文档信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			Document document = business.getDocumentFactory().get(id);
			if ( null == document ) {
				throw new Exception("[put]document{id:" + id + "} not existed.");
			}
			//进行数据库持久化操作
			BeanCopyTools<WrapInDocument, Document> copier = BeanCopyToolsBuilder.create(WrapInDocument.class, Document.class, null, WrapInDocument.Excludes);
			logger.debug("[put]System trying to beginTransaction to update document......" );
			emc.beginTransaction( Document.class);
			copier.copy( wrapIn, document );
			emc.check( document, CheckPersistType.all);			
			emc.commit();
			logger.debug("[put]System update document success......" );
			//成功更新一个文档信息
			logger.debug("[put]System trying to save log......" );
			emc.beginTransaction( Log.class);
			logService.log( emc, currentPerson.getName(), "[put]user[" + currentPerson.getName() + "]成功更新一个文档信息", document.getAppId(), document.getCatagoryId(), document.getId(), "", "DOCUMENT", "更新" );
			emc.commit();
			wrap = new WrapOutId( document.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 查询下一页的草稿，可以根据基础条件进行过滤
	 * @param request
	 * @param id
	 * @param count
	 * @param wrapIn - 过滤的参数
	 * 	      appId, catagoryId, creatorPerson, form, createDate(创建年份月份)
	 * @return
	 */
	@HttpMethodDescribe(value = "列示草稿根据过滤条件的Document,下一页.", response = WrapOutDocument.class, request = WrapInFilter.class)
	@PUT
	@Path("draft/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutDocument>> result = new ActionResult<>();
		List<WrapOutDocument> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<NameValueCountPair> statusList = new ArrayList<NameValueCountPair>();
		List<NameValueCountPair> creatorList = new ArrayList<NameValueCountPair>();
		List<Document> documentList = null;
		NameValueCountPair parameter = null;
		DocumentFactory documentFactory = null;
		CatagoryInfo catagoryInfo = null;
		boolean isXAdmin = false;
		logger.debug("user[" + currentPerson.getName() + "] try to list draft for nextpage, last docuId=" + id );
		try {			
			EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			Business business = new Business(emc);
			isXAdmin = business.isXAdmin(request, currentPerson );
			documentFactory = business.getDocumentFactory();
			
			//向参数条件数据列表里添加状态是草稿的限制
			parameter = new NameValueCountPair();
			parameter.setName("docStatus");
			parameter.setValue("draft");
			statusList.add(parameter);
			wrapIn.setStatusList(statusList);
			
			if( !isXAdmin ){
				//控制权限，creatorPerson
				parameter = new NameValueCountPair();
				parameter.setName("creatorPerson");
				parameter.setValue(currentPerson.getName());
				creatorList.add(parameter);
				wrapIn.setCreatorList(creatorList);
			}
			
			//查询出ID对应的记录的sequence
			Object sequence = null;
			logger.debug( "传入的ID=" + id );
			if( id == null || "(0)".equals(id) || id.isEmpty() ){
				logger.debug( "第一页查询，没有id传入" );
			}else{
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty(emc.find(id, Document.class, ExceptionWhen.not_found), "sequence");
				}
			}
			
			//从数据库中查询符合条件的一页数据对象
			documentList = documentFactory.listIdsNextWithFilter( id, count, sequence, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = documentFactory.getCountWithFilter( wrapIn );

			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = wrapout_copier.copy( documentList );
			
			//根据document的目录ID，查询目录的基本信息放到返回结果里
			CatagoryInfoFactory catagoryInfoFactory = business.getCatagoryInfoFactory();
			for( WrapOutDocument wrapout : wraps ){
				catagoryInfo = catagoryInfoFactory.get( wrapout.getCatagoryId() );
				wrapout.setCatagoryName( catagoryInfo.getCatagoryName());
				wrapout.setCatagoryAlias( catagoryInfo.getCatagoryAlias());
			}
			
			//对查询的列表进行排序
			result.setCount( total );
			result.setData(wraps);

		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Document,上一页.", response = WrapOutDocument.class, request = WrapInFilter.class)
	@PUT
	@Path("draft/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutDocument>> result = new ActionResult<>();
		List<WrapOutDocument> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<NameValueCountPair> statusList = new ArrayList<NameValueCountPair>();
		List<NameValueCountPair> creatorList = new ArrayList<NameValueCountPair>();
		NameValueCountPair parameter = null;
		List<Document> documentList = null;
		DocumentFactory documentFactory = null;
		CatagoryInfo catagoryInfo = null;
		boolean isXAdmin = false;
		logger.debug("user[" + currentPerson.getName() + "] try to list draft for nextpage, last docuId=" + id );
		try {
			EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			Business business = new Business(emc);
			isXAdmin = business.isXAdmin(request, currentPerson );
			
			documentFactory = business.getDocumentFactory();
			
			//向参数条件数据列表里添加状态是草稿的限制
			parameter = new NameValueCountPair();
			parameter.setName("docStatus");
			parameter.setValue("draft");
			statusList.add(parameter);
			wrapIn.setStatusList(statusList);
			
			//控制权限，creatorPerson			
			if( !isXAdmin ){
				//控制权限，creatorPerson
				parameter = new NameValueCountPair();
				parameter.setName("creatorPerson");
				parameter.setValue(currentPerson.getName());
				creatorList.add(parameter);
				wrapIn.setCreatorList(creatorList);
			}
			
			//查询出ID对应的记录的sequence
			Object sequence = null;
			logger.debug( "传入的ID=" + id );
			if( id == null || "(0)".equals(id) || id.isEmpty() ){
				logger.debug( "第一页查询，没有id传入" );
			}else{
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty(emc.find(id, Document.class, ExceptionWhen.not_found), "sequence");
				}
			}
			//从数据库中查询符合条件的一页数据对象
			documentList = documentFactory.listIdsPrevWithFilter( id, count, sequence, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = documentFactory.getCountWithFilter( wrapIn );

			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = wrapout_copier.copy( documentList );
			
			//根据document的目录ID，查询目录的基本信息放到返回结果里
			CatagoryInfoFactory catagoryInfoFactory = business.getCatagoryInfoFactory();
			for( WrapOutDocument wrapout : wraps ){
				catagoryInfo = catagoryInfoFactory.get( wrapout.getCatagoryId() );
				wrapout.setCatagoryName( catagoryInfo.getCatagoryName());
				wrapout.setCatagoryAlias( catagoryInfo.getCatagoryAlias());
			}
			
			//对查询的列表进行排序
			result.setCount( total );
			result.setData(wraps);

		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的Document,下一页.", response = WrapOutDocument.class, request = WrapInFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutDocument>> result = new ActionResult<>();
		List<WrapOutDocument> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<Document> documentList = null;
		DocumentFactory documentFactory = null;
		CatagoryInfo catagoryInfo = null;
		logger.debug("user[" + currentPerson.getName() + "] try to list document for nextpage, last docuId=" + id );
		try {		
			EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			Business business = new Business(emc);
			documentFactory = business.getDocumentFactory();
			
			//查询出ID对应的记录的sequence
			Object sequence = null;
			if( id == null || "(0)".equals(id) || id.isEmpty() ){
				logger.debug( "第一页查询，没有id传入" );
			}else{
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty(emc.find( id, Document.class, ExceptionWhen.not_found), "sequence");
				}
			}
			
			//从数据库中查询符合条件的一页数据对象
			documentList = documentFactory.listIdsNextWithFilter( id, count, sequence, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = documentFactory.getCountWithFilter( wrapIn );

			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = wrapout_copier.copy( documentList );
			
			//根据document的目录ID，查询目录的基本信息放到返回结果里
			CatagoryInfoFactory catagoryInfoFactory = business.getCatagoryInfoFactory();
			for( WrapOutDocument wrapout : wraps ){
				catagoryInfo = catagoryInfoFactory.get( wrapout.getCatagoryId() );
				wrapout.setCatagoryName( catagoryInfo.getCatagoryName());
				wrapout.setCatagoryAlias( catagoryInfo.getCatagoryAlias());
			}
			
			//对查询的列表进行排序
			result.setCount( total );
			result.setData(wraps);

		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Document,上一页.", response = WrapOutDocument.class, request = WrapInFilter.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutDocument>> result = new ActionResult<>();
		List<WrapOutDocument> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<Document> documentList = null;
		DocumentFactory documentFactory = null;
		CatagoryInfo catagoryInfo = null;
		logger.debug("user[" + currentPerson.getName() + "] try to list document for nextpage, last docuId=" + id );
		try {		
			EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			Business business = new Business(emc);
			documentFactory = business.getDocumentFactory();
			
			/**
			if("XAdmin".equalsIgnoreCase(business.getDocumentListViewPermission(request, person))){
				//是平台管理员，看到全部
			}else if("Company".equalsIgnoreCase(business.getDocumentListViewPermission(request, person))){
				//是公司管理员，看到全部
			}else if("Department".equalsIgnoreCase(business.getDocumentListViewPermission(request, person))){
				//是部门管理员，看到全部
			}else{
				//只以看到个人的
				//控制权限，creatorPerson
				parameter = new NameValueCountPair();
				parameter.setName("creatorPerson");
				parameter.setValue(person);
				creatorList.add(parameter);
				wrapIn.setCreatorList(creatorList);
			}
			**/
			
			//查询出ID对应的记录的sequence
			Object sequence = null;
			logger.debug( "传入的ID=" + id );
			if( id == null || "(0)".equals(id) || id.isEmpty() ){
				logger.debug( "第一页查询，没有id传入" );
			}else{
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty(emc.find(id, Document.class, ExceptionWhen.not_found), "sequence");
				}
			}		
			
			//从数据库中查询符合条件的一页数据对象
			documentList = documentFactory.listIdsPrevWithFilter( id, count, sequence, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = documentFactory.getCountWithFilter( wrapIn );

			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = wrapout_copier.copy( documentList );
			
			//根据document的目录ID，查询目录的基本信息放到返回结果里
			CatagoryInfoFactory catagoryInfoFactory = business.getCatagoryInfoFactory();
			for( WrapOutDocument wrapout : wraps ){
				catagoryInfo = catagoryInfoFactory.get( wrapout.getCatagoryId() );
				wrapout.setCatagoryName( catagoryInfo.getCatagoryName());
				wrapout.setCatagoryAlias( catagoryInfo.getCatagoryAlias());
			}
			
			//对查询的列表进行排序
			result.setCount( total );
			result.setData(wraps);

		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/* 查找在List<WrapIdentity>中是否有指定名称的身份。 */
	private WrapIdentity findIdentity( List<WrapIdentity> identities, String name ) throws Exception {
		if( name != null && !name.isEmpty() && !"null".equals(name)){
			if( identities != null && !identities.isEmpty() ){
				for (WrapIdentity o : identities) {
					if (StringUtils.equals(o.getName(), name)) {
						return o;
					}
				}
			}
		}else{
			if( identities != null && !identities.isEmpty() ){
				return identities.get(0);
			}
		}
		return null;
	}
	
/*
	@HttpMethodDescribe(value = "发送通知.", response = WrapOutDocument.class)
	@GET
	@Path("notification/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response notification(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutDocument> result = new ActionResult<>();
		WrapOutDocument wrap = null;
		
		EffectivePerson currentPerson = this.effectivePerson(request);
		Business business = null;
		Document document = null;

		logger.debug("[notification]user[" + currentPerson.getName() + "] try to notification document{'id':'"+id+"'}......" );
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			document = business.getDocumentFactory().get(id);
			//判断文档是否存在
			if ( null == document ) {
				throw new Exception("[notification]document{id:" + id + "} not existed.");
			}

			//6、记录访问日志
			logger.debug("System trying to save log......" );
			emc.beginTransaction( Log.class);
			logService.log( emc, person, "用户[" + currentPerson.getName() + "]发送了文档通知", document.getAppId(), document.getCatagoryId(), document.getId(), "", "DOCUMENT", "通知" );
			emc.commit();
			result.setData( wrap );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	protected void sendReadMessage( Read read ) {
		
		try {
			if (null != read) {
				try {
					ReadMessage readMessage = new ReadMessage();
					readMessage.setTitle(read.getTitle());
					readMessage.setPersonList(new ArrayList<String>());
					readMessage.getPersonList().add(read.getPerson());
					readMessage.setRead(read.getId());
					readMessage.setActivityName(read.getActivityName());
					readMessage.setApplicationName(read.getApplicationName());
					readMessage.setProcessName(read.getProcessName());
					Collaboration.send(readMessage);
				} catch (Exception e) {
					throw new Exception("read{id:" + read.getId() + "} post readMessage error.", e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/

	
/*	@HttpMethodDescribe(value = "获取指定分类下所有的文档信息列表", response = WrapOutDocument.class)
	@GET
	@Path("list/catagory/{catagoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyCatagory( @Context HttpServletRequest request, @PathParam("catagoryId") String catagoryId ) {		
		ActionResult<List<WrapOutDocument>> result = new ActionResult<>();
		List<WrapOutDocument> wraps = null;
		CatagoryInfo catagoryInfo = null;
		List<Document> documentList = null;
		Boolean check = true;
		
		if( catagoryId == null || catagoryId.isEmpty() ){
			check = false;
			result.error( new Exception("catagoryId can not be null!") );
			result.setUserMessage( "系统未获取到查询的参数分类ID[catagoryId]!" );
		}
		if( check ){
			try {
				catagoryInfo = catagoryInfoServiceAdv.get( catagoryId );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据分类ID查询分类信息对象时发生异常。catagoryId:"+ catagoryId );
				logger.error( "system query catagory info with id got an exception.catagoryid:"+catagoryId, e );
			}
		}
		if( check ){
			try {
				documentList = documentInfoServiceAdv.listByCatagoryId( catagoryId );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据分类ID查询分类下的文档信息列表时发生异常。catagoryId:"+ catagoryId );
				logger.error( "system query document ids with catagory id got an exception.catagoryid:"+catagoryId, e );
			}
		}
		if( check ){
			if( documentList != null ){
				try {
					wraps = wrapout_copier.copy( documentList );
					Collections.sort( wraps );			
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统转换列表为输出格式时发生异常!" );
					logger.error( "system copy document list to wrap out got an exception.", e );
				}
			}
		}
		if( check ){
			if( wraps != null && catagoryInfo != null ){
				for( WrapOutDocument wrapout : wraps ){
					wrapout.setCatagoryName( catagoryInfo.getCatagoryName());
					wrapout.setCatagoryAlias( catagoryInfo.getCatagoryAlias());
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}*/
	
}