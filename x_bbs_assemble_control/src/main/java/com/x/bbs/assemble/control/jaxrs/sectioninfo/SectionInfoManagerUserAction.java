package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectInfoServiceAdv;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.organization.core.express.wrap.WrapPerson;



@Path("user/section")
public class SectionInfoManagerUserAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( SectionInfoManagerUserAction.class );
	private BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	private BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	private BBSSubjectInfoServiceAdv subjectInfoServiceAdv = new BBSSubjectInfoServiceAdv();
	private BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	private BeanCopyTools< BBSSectionInfo, WrapOutSectionInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSSectionInfo.class, WrapOutSectionInfo.class, null, WrapOutSectionInfo.Excludes);
	private BeanCopyTools<WrapInSectionInfo, BBSSectionInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInSectionInfo.class, BBSSectionInfo.class, null, WrapInSectionInfo.Excludes );
	
	@HttpMethodDescribe(value = "获取所有版块的信息列表.", response = WrapOutSectionInfo.class)
	@GET
	@Path("all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllSection( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		List<WrapOutSectionInfo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		Boolean check = true;
		if( check ){
			//从数据库查询主版块列表
			try {
				sectionInfoList = sectionInfoServiceAdv.listAll();
				if (sectionInfoList == null) {
					sectionInfoList = new ArrayList<BBSSectionInfo>();
				}
			} catch (Exception e) {
				result.error(e);
				result.setUserMessage("系统在查询所有主版块信息时发生异常");
				logger.error("system query all main section info got an exception!", e);
			}
		}
		if( check ){
			if( sectionInfoList != null && sectionInfoList.size() > 0 ){
				try {
					wraps = wrapout_copier.copy( sectionInfoList );
					result.setData(wraps);
				} catch (Exception e) {
					result.error(e);
					result.setUserMessage("系统在将版块信息列表转换为输出格式时发生异常");
					logger.error("system copy section list to wraps got an exception!", e);
				}		
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据论坛ID获取所有主版块的信息列表(管理).", response = WrapOutSectionInfo.class)
	@GET
	@Path("forum/{forumId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithForum( @Context HttpServletRequest request, @PathParam("forumId") String forumId ) {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		List<WrapOutSectionInfo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		BBSForumInfo forumInfo = new BBSForumInfo();
		Boolean check = true;
		if( check ){
			if( forumId == null || forumId.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数ID为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数ID为空，无法继续进行查询" );
			}
		}
		if( check ){ //查询论坛信息是否存在
			try{
				forumInfo = forumInfoServiceAdv.get(forumId);
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询论坛信息时发生异常！" );
				logger.error( "system query forum info with id got an exception!id:" + forumId, e );
			}
		}
		if( check ){
			if( forumInfo == null ){
				check = false;
				result.error( new Exception("论坛信息不存在，无法继续进行查询操作！ID=" + forumId ) );
				result.setUserMessage( "论坛信息不存在，无法继续进行查询操作！" );
			}
		}
		if( check ){
			//从数据库查询主版块列表
			try {
				sectionInfoList = sectionInfoServiceAdv.listMainSectionByForumId( forumId );
				if (sectionInfoList == null) {
					sectionInfoList = new ArrayList<BBSSectionInfo>();
				}
			} catch (Exception e) {
				result.error(e);
				result.setUserMessage("系统在查询所有主版块信息时发生异常");
				logger.error("system query all main section info got an exception!", e);
			}
		}
		if( check ){
			if( sectionInfoList != null && sectionInfoList.size() > 0 ){
				try {
					wraps = wrapout_copier.copy( sectionInfoList );
					result.setData(wraps);
				} catch (Exception e) {
					result.error(e);
					result.setUserMessage("系统在将版块信息列表转换为输出格式时发生异常");
					logger.error("system copy section list to wraps got an exception!", e);
				}		
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据主版块ID查询所有的子版块信息列表(管理).", response = WrapOutSectionInfo.class)
	@GET
	@Path("sub/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubSectionByMainSectionId( @Context HttpServletRequest request, @PathParam("sectionId") String sectionId ) {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		List<WrapOutSectionInfo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		BBSSectionInfo sectionInfo = new BBSSectionInfo();
		Boolean check = true;	
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数sectionId为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数sectionId为空，无法继续进行查询" );
			}
		}		
		if( check ){
			try{
				sectionInfo = sectionInfoServiceAdv.get( sectionId );
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询版块信息时发生异常！" );
				logger.error( "system query section info with id got an exception!id:" + sectionId, e );
			}
		}		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				result.error( new Exception("版块信息不存在，无法继续进行查询操作！ID=" + sectionId ) );
				result.setUserMessage( "版块信息不存在，无法继续进行查询操作！" );
			}
		}		
		if( check ){
			try {
				sectionInfoList = sectionInfoServiceAdv.listSubSectionByMainSectionId( sectionId );
				if (sectionInfoList == null) {
					sectionInfoList = new ArrayList<BBSSectionInfo>();
				}
			} catch (Exception e) {
				result.error(e);
				result.setUserMessage("系统在查询所有主版块信息时发生异常");
				logger.error("system query sub section info with main section id got an exception!", e);
			}
		}		
		if( check ){
			if( sectionInfoList != null && sectionInfoList.size() > 0 ){
				try {
					wraps = wrapout_copier.copy( sectionInfoList );
					result.setData(wraps);
				} catch (Exception e) {
					result.error(e);
					result.setUserMessage("系统在将版块信息列表转换为输出格式时发生异常");
					logger.error("system copy section list to wraps got an exception!", e);
				}		
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "创建新的版块信息或者更新版块信息.", request = WrapInSectionInfo.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInSectionInfo wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		Boolean check = true;
		String[] names = null;
		String[] typeCatagory = null;
		String[] types = null;
		WrapPerson person = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo_old = null;
		BBSSectionInfo sectionInfo_sub = null;
		BBSSectionInfo sectionInfo = new BBSSectionInfo();
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception("系统传入的对象为空，无法进行数据保存！") );
			result.setUserMessage( "系统传入的对象为空，无法进行数据保存！" );
		}
		//校验版块名称
		if( check ){
			if( wrapIn.getSectionName() == null || wrapIn.getSectionName().isEmpty() ){
				check = false;
				result.error( new Exception("系统传入的[版块名称]为空，无法进行数据保存！") );
				result.setUserMessage( "系统传入的[版块名称]为空，无法进行数据保存！" );
			}
		}
		//校验论坛信息是否有效，并且补充论坛名称
		if (check) {
			if ( wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty() ) {
				check = false;
				result.error(new Exception("系统传入的[论坛ID]为空，无法进行数据保存！"));
				result.setUserMessage("系统传入的[论坛ID]为空，无法进行数据保存！");
			}
		}
		if (check) {
			try{
				forumInfo = forumInfoServiceAdv.get( wrapIn.getForumId() );
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据论坛ID查询论坛信息时发生异常" );
				logger.error( "system query forum with forum id got an exception!id:" + wrapIn.getForumId() , e );
			}
		}
		if (check) {
			if( forumInfo == null ){
				//论坛信息不存在
				check = false;
				result.error( new Exception("论坛信息不存在，ID="+ wrapIn.getForumId() ) );
				result.setUserMessage( "论坛信息不存在，ID="+ wrapIn.getForumId() );
			}else{
				//补充论坛名称
				wrapIn.setForumName( forumInfo.getForumName() );
			}
		}
		//判断论坛是否允许用户创建版块
		if (check) {
			if ( !forumInfo.getSectionCreateAble() ) {
				check = false;
				result.error( new Exception("论坛["+forumInfo.getForumName()+"]不允许创建版块。" ) );
				result.setUserMessage( "论坛["+forumInfo.getForumName()+"]不允许创建版块。" );
			}
		}
		//判断用户是否是论坛管理员
		if ( check ) {
			if( !currentPerson.getName().equals( forumInfo.getForumManagerName() )){
				check = false;
				result.error( new Exception("操作用户不是论坛["+forumInfo.getForumName()+"]管理员，无法创建版块。" ) );
				result.setUserMessage( "操作用户不是论坛["+forumInfo.getForumName()+"]管理员，无法创建版块。" );
			}
		}
		if( check ){
			if( wrapIn.getSubjectType() == null || wrapIn.getSubjectType().isEmpty() ){
				wrapIn.setSubjectType( forumInfo.getSubjectType() );
			}
			if( wrapIn.getSubjectType() == null || wrapIn.getSubjectType().isEmpty() ){
				wrapIn.setSubjectType( "新闻|讨论" );
			}
		}
		if( check ){
			if( wrapIn.getTypeCatagory() != null && !wrapIn.getTypeCatagory().isEmpty() ){
				typeCatagory =  wrapIn.getTypeCatagory().split("\\|");
				if( typeCatagory != null && typeCatagory.length > 0 ){
					for( String catagory : typeCatagory ){
						if( !"信息".equals( catagory ) && !"问题".equals( catagory ) && !"投票".equals( catagory )){
							check = false;
							result.error( new Exception("typeCatagory is invalid.catagory:" + catagory ) );
							result.setUserMessage( "系统传入的[主题分类]不合法，无法进行数据保存！分类:" + catagory );
						}
					}
				}
			}else{
				wrapIn.setTypeCatagory( forumInfo.getTypeCatagory() );
			}
		}
		//校验版块管理员（版主）是否存在
		if( check ){
			if( wrapIn.getModeratorNames() != null && !wrapIn.getModeratorNames().isEmpty() ){
				//判断指定的用户是否存在
				names = wrapIn.getModeratorNames().split( "," );
				for( String name : names ){
					try {
						person = userManagerService.getUserByFlag( name );
						if( person == null ){
							check = false;
							result.error( new Exception( "指定的版主信息不存在，姓名：" + name ) );
							result.setUserMessage( "指定的版主信息不存在，姓名：" + name );
							break;
						}
					} catch (Exception e) {
						check = false;
						result.error( e );
						result.setUserMessage( "系统在根据人员姓名查询人员信息时发生异常！NAME:" + name );
						logger.error( "system get user by flag got an exception!name:" + name, e );
						break;
					}
				}				
			}else{
				wrapIn.setModeratorNames( currentPerson.getName() );
			}
		}
		if( check ){
			wrapIn.setCreatorName( currentPerson.getName() );
		}
		if( check ){
			try {
				wrapin_copier.copy( wrapIn, sectionInfo );
				if( sectionInfo.getId() == null || sectionInfo.getId().isEmpty() ){
					sectionInfo.setId( BBSSectionInfo.createId() );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在COPY传入的对象时发生异常！" );
				logger.error( "system copy wrapIn to sectionInfo got an exception!", e );
			}
		}
		
		if( check ){
			try{
				sectionInfo_old = sectionInfoServiceAdv.get( sectionInfo.getId() );
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询版块信息时发生异常！" );
				logger.error( "system query section info with id got an exception!id:" + sectionInfo.getId(), e );
			}
		}
		if( check ){
			//如果主版的ID为空，或者主版ID与当前ID一致，则版块为一级版块，主版块
			if( sectionInfo.getMainSectionId() == null || sectionInfo.getMainSectionId().isEmpty()  || sectionInfo.getId().equals( sectionInfo.getMainSectionId() ) ){
				sectionInfo.setMainSectionId( sectionInfo.getId() );
				sectionInfo.setMainSectionName( sectionInfo.getSectionName() );
				sectionInfo.setSectionLevel( "主版块" );
			}else{
				try{
					sectionInfo_sub = sectionInfoServiceAdv.get( sectionInfo.getMainSectionId() );
				}catch( Exception e ){
					check = false;
					result.error( e );
					result.setUserMessage( "系统在根据主版块ID查询主版块信息时发生异常！" );
					logger.error( "system query main section info with id got an exception!id:" + sectionInfo.getId(), e );
				}
				if( check ){
					if( sectionInfo_sub != null ){
						sectionInfo.setMainSectionId( sectionInfo_sub.getId() );
						sectionInfo.setMainSectionName( sectionInfo_sub.getSectionName() );
						sectionInfo.setSectionLevel( "子版块" );
					}else{
						check = false;
						result.error( new Exception("根据主版块ID未查询到任何版块信息。") );
						result.setUserMessage( "根据主版块ID未查询到任何版块信息。！" );
						logger.error( "section info is not exsits! id:" + sectionInfo.getId() );
					}
				}
			}
		}
		
		if( check ){
			try {
				sectionInfo = sectionInfoServiceAdv.save( sectionInfo );
				wrap = new WrapOutId( sectionInfo.getId() );
				result.setData( wrap );
				result.setUserMessage( "版块信息保存成功！" );
				if( sectionInfo_old != null ){
					operationRecordService.sectionOperation( currentPerson.getName(), sectionInfo, "MODIFY", hostIp, hostName );
				}else{
					operationRecordService.sectionOperation( currentPerson.getName(), sectionInfo, "CREATE", hostIp, hostName );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存版块信息时发生异常！" );
				logger.error( "system save section info got an exception!", e );
			}
		}
		
		if( check ){
			try {
				//论坛信息添加成功，继续添加权限和角色信息
				permissionInfoService.createSectionPermission( sectionInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在创建论坛权限信息时发生异常！" );
				logger.error( "system create forum permission info got an exception!", e );
			}
		}
		
		if( check ){
			try {
				//论坛信息添加成功，继续添加权限和角色信息
				roleInfoService.createSectionRole( sectionInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在创建论坛角色信息时发生异常！" );
				logger.error( "system create section role info got an exception!", e );
			}
		}
		if( check ){
			if( sectionInfo.getMainSectionId() != null && !sectionInfo.getMainSectionId().equalsIgnoreCase( sectionInfo.getId() )){
				try {
					//如果该版块是子版块，那么主版块的角色也需要重新维护
					roleInfoService.createSectionRole( sectionInfo.getMainSectionId() );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在创建论坛角色信息时发生异常！" );
					logger.error( "system create main section role info got an exception!", e );
				}
			}
		}
		if( check ){
			try {
				//该版块所处的论坛的角色需要重新维护
				roleInfoService.createForumRole( sectionInfo.getForumId() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在创建论坛角色信息时发生异常！" );
				logger.error( "system create forum role info got an exception!", e );
			}
		}
		if( check ){//检查版主权限的设置
			try {
				sectionInfoServiceAdv.checkSectionManager( sectionInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在为版主绑定角色信息时发生异常！" );
				logger.error( "system bind role for section manager got an exception!", e );
			}
		}
		if( check ){
			if( sectionInfo.getMainSectionId() != null && !sectionInfo.getMainSectionId().equalsIgnoreCase( sectionInfo.getId() )){
				try {
					sectionInfoServiceAdv.checkSectionManager( sectionInfo.getMainSectionId() );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在为版主绑定角色信息时发生异常！" );
					logger.error( "system bind role for section manager got an exception!", e );
				}
			}
		}
		if( check ){
			try {
				forumInfoServiceAdv.checkForumManager(forumInfo);
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在为论坛管理员绑定角色信息时发生异常！" );
				logger.error( "system bind role for forum manager got an exception!", e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "根据ID删除指定的版块信息，如果版块里有贴子，则不允许删除.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo  = null;
		Long subjectCount = 0L;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		EffectivePerson currentPerson = this.effectivePerson( request );
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception( "传入的参数ID为空，无法继续进行删除操作！" ) );
				result.setUserMessage( "传入的参数ID为空，无法继续进行删除操作" );
			}
		}
		
		//查询版块信息，判断版块信息是否存在
		if( check ){
			try{
				sectionInfo = sectionInfoServiceAdv.get(id);
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询版块信息时发生异常！" );
				logger.error( "system query section info with id got an exception!id:" + id, e );
			}
		}
		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				result.error( new Exception("版块信息不存在，无法继续进行删除操作！ID：" + id) );
				result.setUserMessage( "版块信息不存在，无法继续进行删除操作！");
			}
		}
		
		if (check) {
			try{
				forumInfo = forumInfoServiceAdv.get( sectionInfo.getForumId() );
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据论坛ID查询论坛信息时发生异常" );
				logger.error( "system query forum with forum id got an exception!id:" + sectionInfo.getForumId() , e );
			}
		}
		
		if (check) {
			if( forumInfo == null ){
				//论坛信息不存在
				check = false;
				result.error( new Exception( "论坛信息不存在，ID="+ sectionInfo.getForumId() ) );
				result.setUserMessage( "论坛信息不存在，ID="+ sectionInfo.getForumId() );
			}
		}
		
		//判断用户是否是论坛管理员
		if ( check ) {
			if ( !currentPerson.getName().equals( forumInfo.getForumManagerName() ) ) {
				check = false;
				result.error( new Exception("操作用户不是论坛[" + forumInfo.getForumName() + "]管理员，无法删除版块。") );
				result.setUserMessage( "操作用户不是论坛[" + forumInfo.getForumName() + "]管理员，无法删除版块。" );
			}
		}
		
		//判断版块内是否含有主题
		if (check) {
			try{
				subjectCount = subjectInfoServiceAdv.countByMainAndSubSectionId( id, true );
			}catch( Exception e){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据版块ID查询主题信息数量时发生异常！" );
				logger.error( "system count subject info with section id got an exception!id:" + id, e );
			}
		}
		
		if (check) {
			if( subjectCount > 0 ){
				check = false;
				result.error( new Exception( "版块["+sectionInfo.getSectionName()+"]中仍存在"+ subjectCount +"个主题，无法直接进行版块删除操作！" ) );
				result.setUserMessage( "版块["+sectionInfo.getSectionName()+"]中仍存在"+ subjectCount +"个主题，无法直接进行版块删除操作！" );
			}
		}
				
		if( check ){
			try {
				sectionInfoServiceAdv.delete( id );
				wrap = new WrapOutId( id );
				result.setData( wrap );
				result.setUserMessage( "版块信息删除成功！" );
				operationRecordService.sectionOperation( currentPerson.getName(), sectionInfo, "DELETE", hostIp, hostName );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在删除版块信息时发生异常" );
				logger.error( "system delete section info got an exception!", e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除指定的版块信息，如果版块里有贴子，则全部删除.", response = WrapOutId.class)
	@DELETE
	@Path("force/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteForce(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		Boolean check = true;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo  = null;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		EffectivePerson currentPerson = this.effectivePerson( request );
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception( "传入的参数ID为空，无法继续进行删除操作！" ) );
				result.setUserMessage( "传入的参数ID为空，无法继续进行删除操作" );
			}
		}		
		if( check ){
			try{
				sectionInfo = sectionInfoServiceAdv.get(id);
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询版块信息时发生异常！" );
				logger.error( "system query section info with id got an exception!id:" + id, e );
			}
		}		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				result.error( new Exception("版块信息不存在，无法继续进行删除操作！ID：" + id) );
				result.setUserMessage( "版块信息不存在，无法继续进行删除操作！");
			}
		}		
		if (check) {
			try{
				forumInfo = forumInfoServiceAdv.get( sectionInfo.getForumId() );
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据论坛ID查询论坛信息时发生异常" );
				logger.error( "system query forum with forum id got an exception!id:" + sectionInfo.getForumId() , e );
			}
		}		
		if (check) {
			if( forumInfo == null ){
				//论坛信息不存在
				check = false;
				result.error( new Exception( "论坛信息不存在，ID="+ sectionInfo.getForumId() ) );
				result.setUserMessage( "论坛信息不存在，ID="+ sectionInfo.getForumId() );
			}
		}		
		//判断用户是否是论坛管理员
		if ( check ) {
			if ( !currentPerson.getName().equals( forumInfo.getForumManagerName() ) ) {
				check = false;
				result.error( new Exception("操作用户不是论坛[" + forumInfo.getForumName() + "]管理员，无法删除版块。") );
				result.setUserMessage( "操作用户不是论坛[" + forumInfo.getForumName() + "]管理员，无法删除版块。" );
			}
		}		
		if( check ){
			try {
				sectionInfoServiceAdv.delete( id );				
				wrap = new WrapOutId( id );
				result.setData( wrap );
				result.setUserMessage( "版块信息删除成功！" );
				operationRecordService.sectionOperation( currentPerson.getName(), sectionInfo, "DELETE", hostIp, hostName );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在删除版块信息时发生异常" );
				logger.error( "system delete section info got an exception!", e );
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}