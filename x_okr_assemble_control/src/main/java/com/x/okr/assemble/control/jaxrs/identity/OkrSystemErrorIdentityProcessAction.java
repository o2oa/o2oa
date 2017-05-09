package com.x.okr.assemble.control.jaxrs.identity;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.application.jaxrs.MemberTerms;
import com.x.base.core.application.jaxrs.NotEqualsTerms;
import com.x.base.core.application.jaxrs.NotInTerms;
import com.x.base.core.application.jaxrs.NotMemberTerms;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.identity.entity.ErrorIdentityRecords;
import com.x.okr.assemble.control.jaxrs.identity.exception.FilterIdentityEmptyException;
import com.x.okr.assemble.control.jaxrs.identity.exception.IdentityCheckException;
import com.x.okr.assemble.control.jaxrs.identity.exception.NewIdentityEmptyException;
import com.x.okr.assemble.control.jaxrs.identity.exception.NewIdentityNotExistsException;
import com.x.okr.assemble.control.jaxrs.identity.exception.OldIdentityEmptyException;
import com.x.okr.assemble.control.jaxrs.identity.exception.RecordIdEmptyException;
import com.x.okr.assemble.control.jaxrs.identity.exception.RecordTypeEmptyException;
import com.x.okr.assemble.control.jaxrs.identity.exception.TableNameEmptyException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.InsufficientPermissionsException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.OkrSystemAdminCheckException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.WrapInConvertException;
import com.x.okr.assemble.control.service.OkrSystemIdentityOperatorService;
import com.x.okr.assemble.control.service.OkrSystemIdentityQueryService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.entity.OkrErrorIdentityRecords;
import com.x.okr.entity.OkrErrorSystemIdentityInfo;
import com.x.organization.core.express.Organization;

@Path("error/identity")
public class OkrSystemErrorIdentityProcessAction extends StandardJaxrsAction {
	private Logger logger = LoggerFactory.getLogger(OkrSystemErrorIdentityProcessAction.class);
	private OkrSystemIdentityOperatorService okrSystemIdentityOperatorService = new OkrSystemIdentityOperatorService();
	private OkrSystemIdentityQueryService okrSystemIdentityQueryService = new OkrSystemIdentityQueryService();
	private OkrUserManagerService userManagerService = new OkrUserManagerService();

	@HttpMethodDescribe(value = "根据ID获取OkrTask对象.", response = WrapOutString.class)
	@GET
	@Path("check")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response check(@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			WrapOutString wrapOutString = new WrapOutString();
			okrSystemIdentityOperatorService.checkAllAbnormalIdentityInSystem();
			result.setData(wrapOutString);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system check identity got an exception.");
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据要求将所有数据记录从一个身份转换到另一个身份.", response = WrapInErrorIdentity.class, request = JsonElement.class)
	@PUT
	@Path("change")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeIdentity(@Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapInErrorIdentity wrapIn = null;
		Boolean identityExists = false;
		Boolean check = true;

		if (check) {
			try {
				wrapIn = this.convertToWrapIn(jsonElement, WrapInErrorIdentity.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new WrapInConvertException(e, jsonElement);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (wrapIn.getOldIdentity() == null || wrapIn.getOldIdentity().isEmpty()) {
				check = false;
				Exception exception = new OldIdentityEmptyException();
				result.error(exception);
			}
		}

		if (check) {
			if (wrapIn.getNewIdentity() == null || wrapIn.getNewIdentity().isEmpty()) {
				check = false;
				Exception exception = new NewIdentityEmptyException();
				result.error(exception);
			}
		}

		if (check) {
			if (wrapIn.getRecordType() == null || wrapIn.getRecordType().isEmpty()) {
				check = false;
				Exception exception = new RecordTypeEmptyException();
				result.error(exception);
			}
		}

		if (check) {
			if (wrapIn.getTableName() == null || wrapIn.getTableName().isEmpty()) {
				check = false;
				Exception exception = new TableNameEmptyException();
				result.error(exception);
			}
		}

		if (check) {
			if (wrapIn.getRecordId() == null || wrapIn.getRecordId().isEmpty()) {
				check = false;
				Exception exception = new RecordIdEmptyException();
				result.error(exception);
			}
		}

		if (check) {
			try {
				identityExists = userManagerService.isUserIdentityExsits(wrapIn.getNewIdentity());
				if (!identityExists) {
					check = false;
					Exception exception = new NewIdentityNotExistsException(wrapIn.getNewIdentity());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new IdentityCheckException(e, wrapIn.getNewIdentity());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				WrapOutString wrapOutString = new WrapOutString();
				okrSystemIdentityOperatorService.changeUserIdentity(wrapIn.getOldIdentity(), wrapIn.getNewIdentity(),
						wrapIn.getRecordType(), wrapIn.getTableName(), wrapIn.getRecordId());
				okrSystemIdentityOperatorService.checkAllAbnormalIdentityInSystem(wrapIn.getOldIdentity(), null);
				wrapOutString.setValue("数据身份信息变更完成");
				result.setData(wrapOutString);
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error(e);
				logger.warn("system check identity got an exception.");
				logger.error(e, effectivePerson, request, null);
			}
		}

		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据要求将所有数据记录从一个身份转换到另一个身份.", response = WrapOutOkrErrorSystemIdentityInfo.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response errorInfoFilterListNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, JsonElement jsonElement) {
		BeanCopyTools<OkrErrorSystemIdentityInfo, WrapOutOkrErrorSystemIdentityInfo> wrapout_copier = BeanCopyToolsBuilder
				.create(OkrErrorSystemIdentityInfo.class, WrapOutOkrErrorSystemIdentityInfo.class, null,
						WrapOutOkrErrorSystemIdentityInfo.Excludes);
		ActionResult<List<WrapOutOkrErrorSystemIdentityInfo>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Organization organization = new Organization(ThisApplication.context());
		Boolean hasPermission = false;
		WrapInQueryErrorIdentity wrapIn = null;
		Boolean check = true;

		if (check) {
			try {
				wrapIn = this.convertToWrapIn(jsonElement, WrapInQueryErrorIdentity.class);
			} catch (Exception e) {
				wrapIn = new WrapInQueryErrorIdentity();
			}
		}
		if (check) {
			try {
				hasPermission = organization.role().hasAny(currentPerson.getName(), "OkrSystemAdmin");
				if (!hasPermission) {
					check = false;
					Exception exception = new InsufficientPermissionsException(currentPerson.getName(),
							"OkrSystemAdmin");
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new OkrSystemAdminCheckException(e, currentPerson.getName());
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}

		if (check) {
			if (wrapIn.getIdentity() != null && !wrapIn.getIdentity().isEmpty()) {
				equalsMap.put("identity", wrapIn.getIdentity());
			}
		}

		if (check) {
			sequenceField = wrapIn.getSequenceField();
			try {
				result = this.standardListNext(wrapout_copier, id, count, sequenceField, equalsMap, notEqualsMap,
						likesMap, insMap, notInsMap, membersMap, notMembersMap, false, wrapIn.getOrder());
			} catch (Exception e) {
				result.error(e);
				logger.error(e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "根据要求将所有数据记录从一个身份转换到另一个身份.", response = WrapOutOkrErrorSystemIdentityInfo.class, request = JsonElement.class)
	@PUT
	@Path("detail")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getErrorRecords(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<List<ErrorIdentityRecords>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Organization organization = new Organization(ThisApplication.context());
		Boolean hasPermission = false;
		OkrErrorIdentityRecords errorIdentityRecords = new OkrErrorIdentityRecords();
		String identity = null;
		String content = null;
		List<ErrorIdentityRecords> errorRecordsList = null;
		WrapInQueryErrorIdentity wrapIn = null;
		Boolean check = true;

		if (check) {
			try {
				wrapIn = this.convertToWrapIn(jsonElement, WrapInQueryErrorIdentity.class);
			} catch (Exception e) {
				wrapIn = new WrapInQueryErrorIdentity();
			}
		}
		if (check) {
			try {
				hasPermission = organization.role().hasAny(currentPerson.getName(), "OkrSystemAdmin");
				if (!hasPermission) {
					check = false;
					Exception exception = new InsufficientPermissionsException(currentPerson.getName(),
							"OkrSystemAdmin");
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new OkrSystemAdminCheckException(e, currentPerson.getName());
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			identity = wrapIn.getIdentity();
			if (identity == null || identity.isEmpty()) {
				check = false;
				Exception exception = new FilterIdentityEmptyException();
				result.error(exception);
			}
		}
		if (check) {
			try {
				errorIdentityRecords = okrSystemIdentityQueryService.getErrorIdentityRecords(identity);
				if (errorIdentityRecords != null) {
					content = errorIdentityRecords.getRecordsJson();
				} else {
					content = "{}";
				}
				if (content != null && !"{}".equals(content)) {
					Gson gson = XGsonBuilder.pureGsonDateFormated();
					errorRecordsList = (List<ErrorIdentityRecords>) gson.fromJson(content,
							new TypeToken<List<ErrorIdentityRecords>>() {
							}.getType());
				}
				result.setData(errorRecordsList);
			} catch (Exception e) {
				result.error(e);
				logger.error(e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据要求将所有数据记录从一个身份转换到另一个身份.", response = WrapOutOkrErrorSystemIdentityInfo.class, request = JsonElement.class)
	@PUT
	@Path("errorrecords/filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getOkrErrorIdentityRecords(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, JsonElement jsonElement) {
		BeanCopyTools<OkrErrorSystemIdentityInfo, WrapOutOkrErrorSystemIdentityInfo> wrapout_copier = BeanCopyToolsBuilder
				.create(OkrErrorSystemIdentityInfo.class, WrapOutOkrErrorSystemIdentityInfo.class, null,
						WrapOutOkrErrorSystemIdentityInfo.Excludes);
		ActionResult<List<WrapOutOkrErrorSystemIdentityInfo>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Organization organization = new Organization(ThisApplication.context());
		Boolean hasPermission = false;
		WrapInQueryErrorIdentity wrapIn = null;
		Boolean check = true;

		if (check) {
			try {
				wrapIn = this.convertToWrapIn(jsonElement, WrapInQueryErrorIdentity.class);
			} catch (Exception e) {
				wrapIn = new WrapInQueryErrorIdentity();
			}
		}
		if (check) {
			try {
				hasPermission = organization.role().hasAny(currentPerson.getName(), "OkrSystemAdmin");
				if (!hasPermission) {
					check = false;
					Exception exception = new InsufficientPermissionsException(currentPerson.getName(),
							"OkrSystemAdmin");
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new OkrSystemAdminCheckException(e, currentPerson.getName());
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}

		if (check) {
			if (wrapIn.getIdentity() != null && !wrapIn.getIdentity().isEmpty()) {
				equalsMap.put("identity", wrapIn.getIdentity());
			}
		}

		if (check) {
			sequenceField = wrapIn.getSequenceField();
			try {
				result = this.standardListNext(wrapout_copier, id, count, sequenceField, equalsMap, notEqualsMap,
						likesMap, insMap, notInsMap, membersMap, notMembersMap, false, wrapIn.getOrder());
			} catch (Exception e) {
				result.error(e);
				logger.error(e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
