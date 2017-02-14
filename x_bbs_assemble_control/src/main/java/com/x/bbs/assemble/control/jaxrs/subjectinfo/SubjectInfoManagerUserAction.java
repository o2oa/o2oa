package com.x.bbs.assemble.control.jaxrs.subjectinfo;

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
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectVoteService;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSVoteOption;



@Path("user/subject")
public class SubjectInfoManagerUserAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( SubjectInfoManagerUserAction.class );
	private BBSSubjectVoteService subjectVoteService = new BBSSubjectVoteService();
	private BBSSubjectInfoServiceAdv subjectInfoServiceAdv = new BBSSubjectInfoServiceAdv();
	private BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	private BeanCopyTools< BBSSubjectAttachment, WrapOutSubjectAttachment > attachmentWrapout_copier = BeanCopyToolsBuilder.create( BBSSubjectAttachment.class, WrapOutSubjectAttachment.class, null, WrapOutSubjectAttachment.Excludes);
	private BeanCopyTools< BBSSubjectInfo, WrapOutSubjectInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSSubjectInfo.class, WrapOutSubjectInfo.class, null, WrapOutSubjectInfo.Excludes);
	private BeanCopyTools<WrapInSubjectInfo, BBSSubjectInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInSubjectInfo.class, BBSSubjectInfo.class, null, WrapInSubjectInfo.Excludes );
	private BeanCopyTools< BBSVoteOption, WrapOutBBSVoteOption > voteOptionWrapout_copier = BeanCopyToolsBuilder.create( BBSVoteOption.class, WrapOutBBSVoteOption.class, null, WrapOutBBSVoteOption.Excludes);
	
	@HttpMethodDescribe(value = "根据指定ID获取主题具体信息.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		List<WrapOutSubjectAttachment> wrapSubjectAttachmentList = null;
		List<BBSSubjectAttachment> subjectAttachmentList = null;
		String subjectVoteResult = null;
		String optionBinaryContent = null;
		List<BBSVoteOption> voteOptionList = null;
		List<WrapOutBBSVoteOption> wrapOutSubjectVoteOptionList = null;
		WrapOutSubjectInfo wrap = null;
		BBSSubjectInfo subjectInfo = null;
		String subjectContent = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}

		if (check) {
			if ( subjectInfo == null ) {
				check = false;
				result.error( new Exception( "根据传入的主题ID未能查询到任何主题信息！" ) );
				result.setUserMessage( "根据传入的主题ID未能查询到任何主题信息！" );
			}else{//查到了主题信息
				try {
					wrap = wrapout_copier.copy( subjectInfo );
					//根据附件ID列表查询附件信息
					if( wrap.getAttachmentList() != null && wrap.getAttachmentList().size() > 0 ){
						subjectAttachmentList = subjectInfoServiceAdv.listAttachmentByIds( wrap.getAttachmentList() );
						if( subjectAttachmentList != null && subjectAttachmentList.size() > 0 ){
							wrapSubjectAttachmentList = attachmentWrapout_copier.copy( subjectAttachmentList );
							wrap.setSubjectAttachmentList( wrapSubjectAttachmentList );
						}
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage("系统在转换对象为输出格式时发生异常！");
					logger.error("system copy subject info to wrap got an exceptin. id:" + id, e);
				}
			}			
		}
		if (check) {
			if( wrap != null ){
				//填充主题的内容信息
				try {
					subjectContent = subjectInfoServiceAdv.getSubjectContent( id );
					if( subjectContent != null ){
						wrap.setContent( subjectContent );
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage("系统在根据ID查询主题的内容时发生异常！");
					logger.error("system query subjec content with id got an exceptin. id:" + id, e);
				}
			}
		}
		if (check) {
			if( wrap != null ){//获取该主题的投票选项
				try {
					voteOptionList = subjectVoteService.listVoteOption( id );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage("系统在根据主题ID查询主题所有投票选项信息列表时发生异常！");
					logger.error("system query all vote options for subject with id got an exceptin. id:" + id, e);
				}
			}
		}
		if (check) {
			if( voteOptionList != null  && !voteOptionList.isEmpty() ){
				try {
					wrapOutSubjectVoteOptionList = voteOptionWrapout_copier.copy( voteOptionList );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage("系统转换投票信息列表为输出格式时发生异常！");
					logger.error("system wrap vote options got an exceptin. id:" + id, e);
				}
			}
		}
		if (check) {
			if( wrapOutSubjectVoteOptionList != null  && !wrapOutSubjectVoteOptionList.isEmpty() ){
				for( WrapOutBBSVoteOption option : wrapOutSubjectVoteOptionList ){
					//获取图片编码
					try {
						optionBinaryContent = subjectVoteService.getOptionBinaryContent( option.getId() );
						option.setOptionBinary( optionBinaryContent );
					} catch (Exception e) {
						check = false;
						result.error( e );
						result.setUserMessage("系统在根据选项ID查询选项的二进制内容时发生异常！");
						logger.error("system query subjec content with id got an exceptin. id:" + id, e);
					}
				}
			}
		}
		if (check) {
			if( wrapOutSubjectVoteOptionList != null  && !wrapOutSubjectVoteOptionList.isEmpty() ){
				wrap.setVoteOptionList( wrapOutSubjectVoteOptionList );
			}
		}
		if ( check ) {
			if( wrap != null ){
				//获取该主题的投票结果
				try {
					subjectVoteResult = subjectVoteService.getVoteResult( id );
					wrap.setVoteResult( subjectVoteResult );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage("系统在根据主题ID查询主题投票结果时发生异常！");
					logger.error("system query subjec vote result with id got an exceptin. id:" + id, e);
				}
			}
		}
		result.setData( wrap );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "设置为精华主题.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("setCream/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setCream( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setCream( id, true, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题设置精华主题时发生异常！");
				logger.error("system set subject info for cream with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消精华主题.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("nonCream/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonCream( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setCream( id, false, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题取消精华主题时发生异常！");
				logger.error("system set subject info for non cream with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "设置为原创主题.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("setOriginal/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setOriginal( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setOriginal( id, true, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题设置原创主题时发生异常！");
				logger.error("system set subject info for original with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消原创主题.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("nonOriginal/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonOriginal( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setOriginal( id, false, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题取消原创主题时发生异常！");
				logger.error("system set subject info for non original with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "锁定主题: 状态修改为'启用', 属性stopReply = false.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("unlock/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unlock( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.lock( id, false, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题解除锁定时发生异常！");
				logger.error( "system unlock subject info with id got an exceptin. id:" + id, e );
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "锁定主题: 状态修改为'已锁定', 属性stopReply = true.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("lock/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response lock( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.lock( id, true, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题锁定时发生异常！");
				logger.error( "system lock subject info with id got an exceptin. id:" + id, e );
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消完成主题: 属性isCompleted = false.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("uncomplete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unComplete( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.complete( id, true, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题取消完成时发生异常！");
				logger.error( "system unComplete subject info with id got an exceptin. id:" + id, e );
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "完成主题: 属性isCompleted = true.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("complete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response complete( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.complete( id, true, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题设置完成时发生异常！");
				logger.error( "system complete subject info with id got an exceptin. id:" + id, e );
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消问题贴采纳回复.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("unacceptreply/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unAcceptReply( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.acceptReply( id, "", currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题取消回复采纳时发生异常！");
				logger.error( "system unAcceptReply subject got an exceptin. id:" + id, e );
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "问题贴采纳回复.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("acceptreply/{id}/{replyId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response acceptReply( @Context HttpServletRequest request, @PathParam("id") String id, @PathParam("replyId") String replyId ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.acceptReply( id, replyId, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题设置完成时发生异常！");
				logger.error( "system acceptReply subject got an exceptin. id:" + id, e );
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "版块置顶.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("topToSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response topToSection( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setTopToSection( id, true, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题设置版块置顶时发生异常！");
				logger.error("system set subject info for top to section with id got an exceptin. id:" + id, e);
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消版块置顶.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("nonTopToSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonTopToSection( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setTopToSection( id, false, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题取消版块置顶时发生异常！");
				logger.error("system set subject info for non top to section with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "主版块置顶.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("topToMainSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response topToMainSection( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setTopToMainSection( id, true, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题设置主版块置顶时发生异常！");
				logger.error("system set subject info for top to main section with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消主版块置顶.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("nonTopToMainSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonTopToMainSection( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setTopToMainSection( id, false, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题取消版主块置顶时发生异常！");
				logger.error("system set subject info for non top to main section with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "论坛置顶.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("topToForum/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response topToForum( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setTopToForum( id, true, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题设置论坛置顶时发生异常！");
				logger.error("system set subject info for top to forum with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消论坛置顶.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("nonTopToForum/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonTopToForum( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setTopToForum( id, false, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题取消论坛置顶时发生异常！");
				logger.error("system set subject info for non top to forum with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "全局置顶.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("topToBBS/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response topToBBS( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setTopToBBS( id, true, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题设置全局置顶时发生异常！");
				logger.error("system set subject info for top to BBS with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "推荐到BBS首页.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("setRecommendToBBSIndex/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setRecommendToBBSIndex( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.recommendToBBSIndex( id, true, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题推荐到BBS首页时发生异常！");
				logger.error("system set subject info for recommend to BBSIndex with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消推荐到BBS首页.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("nonRecommendToBBSIndex/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonRecommendToBBSIndex( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.recommendToBBSIndex( id, false, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题推荐到BBS首页时发生异常！");
				logger.error("system set subject info for recommend to BBSIndex with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消全局置顶.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("nonTopToBBS/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonTopToBBS( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数主题ID为空，无法继续查询主题信息！" ) );
				result.setUserMessage( "传入的参数主题ID为空，无法继续查询主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据ID查询主题信息时发生异常！");
				logger.error("system query subject info with id got an exceptin. id:" + id, e);
			}
		}
		
		if (check) {
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception( "主题不存在，无法继续进行操作！" ) );
				result.setUserMessage("主题不存在，无法继续进行操作！");
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.setTopToBBS( id, false, currentPerson.getName() );
				result.setUserMessage( id );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("对主题取消全局置顶时发生异常！");
				logger.error("system set subject info for non top to BBS with id got an exceptin. id:" + id, e);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
    
	@HttpMethodDescribe(value = "创建新的主题信息或者更新主题信息.", request = WrapInSubjectInfo.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInSubjectInfo wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		BBSSectionInfo sectionInfo = null;
		BBSSubjectInfo subjectInfo = null;
		EffectivePerson currentPerson = this.effectivePerson( request );
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法保存主题信息！" ) );
				result.setUserMessage( "传入的参数为空，无法保存主题信息！" );
			}
		}
		
		if( check ){
			wrapIn.setHostIp( request.getRemoteHost() );
			if( wrapIn.getTitle() == null ){
				check = false;
				result.error( new Exception("传入的主题标题为空，无法保存主题信息！" ) );
				result.setUserMessage( "传入的主题标题为空，无法保存主题信息！" );
			}
		}
		
		if( check ){
			if( wrapIn.getType() == null ){
				check = false;
				result.error( new Exception("传入的主题类别为空，无法保存主题信息！" ) );
				result.setUserMessage( "传入的主题类别为空，无法保存主题信息！" );
			}
		}
		
		if( check ){
			if( wrapIn.getContent() == null ){
				check = false;
				result.error( new Exception("传入的主题内容为空，无法保存主题信息！" ) );
				result.setUserMessage( "传入的主题内容为空，无法保存主题信息！" );
			}
		}
		if( check ){
			if( wrapIn.getSectionId() == null || wrapIn.getSectionId().isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数版块ID为空，无法保存主题信息！" ) );
				result.setUserMessage( "传入的参数版块ID为空，无法保存主题信息！" );
			}
		}
		
		//查询版块信息是否存在
		if( check ){
			try {
				sectionInfo = sectionInfoServiceAdv.get( wrapIn.getSectionId() );
			} catch (Exception e) {
				check = false;
				result.error( new Exception("根据版块ID查询版块信息发生异常，无法保存主题信息！" ) );
				result.setUserMessage( "根据版块ID查询版块信息发生异常，无法保存主题信息！" );
				logger.error( "system query section info with id got an exceptin. id:" + wrapIn.getSectionId(), e );
			}
		}
		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				result.error( new Exception("根据传入的版块ID未能查询到任何版块信息，无法保存主题信息！id:"+ wrapIn.getSectionId() ) );
				result.setUserMessage( "根据传入的版块ID未能查询到任何版块信息，无法保存主题信息！id:"+ wrapIn.getSectionId() );
			}
		}
		if( check ){
			if( wrapIn.getTypeCatagory() == null || !wrapIn.getTypeCatagory().isEmpty() ){
				wrapIn.setTypeCatagory( "信息" );
			}else{
				if( sectionInfo.getTypeCatagory() == null || sectionInfo.getTypeCatagory().isEmpty() ){
					wrapIn.setTypeCatagory( "信息" );
				}else{
					//判断TypeCatagory是否合法
					String[] catagories = sectionInfo.getTypeCatagory().split("\\|");
					Boolean catagoryValid = false;
					if( catagories != null && catagories.length > 0 ){
						for( String catagory : catagories ){
							if( catagory.equals( wrapIn.getTypeCatagory() )){
								catagoryValid = true;
							}
						}
						if( !catagoryValid ){
							check = false;
							result.error( new Exception("type catagory is invalid in section！catagory:"+ wrapIn.getTypeCatagory() ) );
							result.setUserMessage( "主题贴类别分类在版块允许分类中不允许发布！catagory:"+ wrapIn.getTypeCatagory() );
						}
					}else{
						wrapIn.setTypeCatagory( "信息" );
					}
				}
			}				
		}
		if( check ){
			if( wrapIn.getType() == null || !wrapIn.getType().isEmpty() ){
				wrapIn.setType( "未知类别" );
			}else{
				if( sectionInfo.getSubjectType() == null || sectionInfo.getSubjectType().isEmpty() ){
					wrapIn.setType( "未知类别" );
				}else{
					//判断Type是否合法
					String[] types = sectionInfo.getSubjectType().split("\\|");
					Boolean typeValid = false;
					if( types != null && types.length > 0 ){
						for( String type : types ){
							if( type.equals( wrapIn.getType() )){
								typeValid = true;
							}
						}
						if( !typeValid ){
							check = false;
							result.error( new Exception("subject type is invalid in section！type:"+ wrapIn.getType() ) );
							result.setUserMessage( "主题类别在版块允许分类中无效！type:"+ wrapIn.getType() );
						}
					}else{
						wrapIn.setTypeCatagory( "未知类别" );
					}
				}
			}				
		}
		if( check ){
			subjectInfo = new BBSSubjectInfo();
			try {
				subjectInfo = wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "将传入的参数转换为主题对象发生异常，无法保存主题信息！" );
				logger.error( "system copy wrap into bbssubjectinfo object got an exceptin. " , e );
			}
		}
		
		if( check ){
			subjectInfo.setForumId( sectionInfo.getForumId() );
			subjectInfo.setForumName( sectionInfo.getForumName() );
			subjectInfo.setMainSectionId(sectionInfo.getMainSectionId());
			subjectInfo.setMainSectionName(sectionInfo.getMainSectionName());
			subjectInfo.setSectionId(sectionInfo.getId());
			subjectInfo.setSectionName(sectionInfo.getSectionName());
			subjectInfo.setCreatorName( currentPerson.getName() );
			subjectInfo.setLatestReplyTime( new Date() );
			subjectInfo.setTypeCatagory( wrapIn.getTypeCatagory() );
		}
		
		if( check ){
			subjectInfo.setMachineName( wrapIn.getSubjectMachineName() );
			subjectInfo.setSystemType( wrapIn.getSubjectSystemName() );
			try {
				subjectInfo = subjectInfoServiceAdv.save( subjectInfo, wrapIn.getContent(), wrapIn.getPictureBase64() );
				result.setUserMessage( subjectInfo.getId() );
				result.setData( new WrapOutId(subjectInfo.getId()));
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "保存主题信息到数据库时发生异常！" );
				logger.error( "system save bbssubjectinfo object got an exceptin. " , e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除指定的主题信息.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		BBSSubjectInfo subjectInfo = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数为空，无法根据ID删除主题！" ) );
				result.setUserMessage( "传入的参数为空，无法根据ID删除主题！" );
			}
		}		
		//判断主题信息是否存在
		if( check ){
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( new Exception("根据ID获取主题信息时发生异常！" ) );
				result.setUserMessage( "根据ID获取主题信息时发生异常！" );
				logger.error( "system query subject info with id got an exceptin.", e );
			}
		}		
		if( check ){
			if( subjectInfo == null ){
				check = false;
				result.error( new Exception("根据ID未能获取到任何主题信息！" ) );
				result.setUserMessage( "根据ID未能获取到任何主题信息！" );
			}
		}		
		try {
			subjectInfoServiceAdv.delete( id );//删除主题同时要将所有的回复内容全部删除
			result.setData( new WrapOutId(id) );
			//记录操作日志
			operationRecordService.subjectOperation( currentPerson.getName(), subjectInfo, "DELETE", hostIp, hostName );
		} catch (Exception e) {
			check = false;
			result.error( new Exception("根据ID删除主题信息时发生异常！" ) );
			result.setUserMessage( "根据ID删除主题信息时发生异常！" );
			logger.error( "system delete subject info with id got an exceptin.", e );
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示我发布的主题.", response = WrapOutSubjectInfo.class, request = WrapInFilter.class)
	@PUT
	@Path("my/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMySubjectForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		List<WrapOutSubjectInfo> wraps = new ArrayList<>();
		List<BBSSubjectInfo> subjectInfoList = null;
		List<BBSSubjectInfo> subjectInfoList_out = new ArrayList<BBSSubjectInfo>();
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法查询主题信息！" ) );
				result.setUserMessage( "传入的参数为空，无法查询主题信息！" );
			}
		}
		if( check ){
			if( page == null ){
				page = 1;
			}
		}
		if( check ){
			if( count == null ){
				count = 20;
			}
		}
		if( check ){
			try{
				total = subjectInfoServiceAdv.countUserSubjectForPage( wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getNeedPicture(), wrapIn.getWithTopSubject(), currentPerson.getName() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "根据ID信息查询登录者的主题信息数量时发生异常！" );
				logger.error( "system count my subject info with section info got an exceptin.", e );
			}
		}
		if( check ){
			if( total > 0 ){
				try{
					subjectInfoList = subjectInfoServiceAdv.listUserSubjectForPage( wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getNeedPicture(), wrapIn.getWithTopSubject(), page*count, currentPerson.getName() );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "根据ID信息查询登录者的主题信息时发生异常！" );
					logger.error( "system query my subject info with section info got an exceptin.", e );
				}
			}
		}
		if( check ){
			if( page <= 0 ){
				page = 1;
			}
			if( count <= 0 ){
				count = 20;
			}
			int startIndex = ( page - 1 ) * count;
			int endIndex = page * count;
			for( int i=0; subjectInfoList != null && i< subjectInfoList.size(); i++ ){
				if( i < subjectInfoList.size() && i >= startIndex && i < endIndex ){
					subjectInfoList_out.add( subjectInfoList.get( i ) );
				}
			}
			if( subjectInfoList_out != null && !subjectInfoList_out.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( subjectInfoList_out );
					result.setData( wraps );
					result.setCount( total );
				} catch (Exception e) {
					check = false;
					result.error( new Exception("将主题转换为输出格式发生异常！" ) );
					result.setUserMessage( "将主题转换为输出格式发生异常！" );
					logger.error( "system copy subject info list to wrapout got an exceptin.", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
}