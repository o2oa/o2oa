//
//  O2URLContext.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/6.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
//用户登录请求URL
//#define ZL_LOGIN_MODULE_KEY @"x_organization_assemble_authentication"
//#define ZL_LOGIN_ACTION_QEURY @"jaxrs/authentication"
struct LoginContext{
    static let loginContextKey = "x_organization_assemble_authentication"
    static let loginActionQuery = "jaxrs/authentication"//校验token
    static let codeLoginActionQuery = "jaxrs/authentication/code/credential/##credential##"
    static let codeAuthActionQuery = "jaxrs/authentication/code"
    static let scanCodeAuthActionQuery = "jaxrs/authentication/bind/meta/##meta##"
}

////文件管理上下文
//#define  ZL_FILE_MANAGE_MODULE_KEY @"x_file_assemble_control"
//#define  ZL_FILE_MANAGE_FOLDER_TOP_QUERY @"jaxrs/complex/top"
//#define  ZL_FILE_MANAGE_FOLDER_QUERY @"jaxrs/complex/folder/p0"
//#define  ZL_FILE_MANAGE_FOLDER_DELETE_QUERY @"jaxrs/folder/p0"
//#define  ZL_FILE_MANAGE_FOLDER_RENAME_QUERY @"jaxrs/folder/p0"
//#define  ZL_FILE_DOWNLOAD_QUERY @"servlet/download/p0"
//#define  ZL_FILE_MANAGE_FOLDER_CREATE_QUERY @"jaxrs/folder"
//#define  ZL_FILE_MANAGE_FILE_UPLOAD_QUERY @"servlet/upload"
//#define  ZL_FILE_MANAGE_FILE_EDITOR_QUERY @"jaxrs/editor/list"
//#define  ZL_FILE_MANAGE_FILE_SHARE_QUERY @"jaxrs/share/list"
//#define  ZL_FILE_MANAGE_FILE_DELETE_QUERY @"jaxrs/attachment/p0"
//#define  ZL_FILE_MANAGE_FILE_RENAME_QUERY @"jaxrs/attachment/p0"
//#define  ZL_FILE_MANAGE_FILE_DOWNLOAD_QUERY @"servlet/download/p0/stream"
//#define  ZL_FILE_MANAGE_FILE_MY_SHARE_QUERY @"jaxrs/attachment/list/share/p0"
//#define  ZL_FILE_MANAGE_FILE_MY_EDITOR_QUERY @"jaxrs/attachment/list/editor/p0"

/**
 *  云盘相关
 */
struct FileContext {
    static let fileContextKey = "x_file_assemble_control"
    static let fileTopListQuery = "jaxrs/complex/top"
    static let fileFolderItemIdQuery = "jaxrs/complex/folder/##id##"
    static let fileFolderActionIdQuery = "jaxrs/folder/##id##"
    static let fileFolderCreateQuery = "jaxrs/folder"
    static let fileDownloadCMSIdQuery = "servlet/download/##id##/stream"
    static let fileDownloadItemIdQuery = "jaxrs/attachment/##id##/download/stream"
    static let fileDownloadPicItemIdQuery = "jaxrs/file/##id##/download/stream"
    static let fileUploadTopQuery = "jaxrs/attachment/upload/folder/(0)"
    static let fileUploadSubQuery = "jaxrs/attachment/upload/folder/##id##"
    static let fileUploadReference = "jaxrs/file/upload/referencetype/##referencetype##/reference/##reference##/scale/##scale##"
    static let fileEditorQuery = "jaxrs/editor/list"
    static let fileShareQuery = "jaxrs/share/list"
    static let fileDeleteQuery = "jaxrs/attachment/##id##"
    static let fileRenameQuery = "jaxrs/attachment/##id##"
    static let fileShareActionQuery = "jaxrs/attachment/##id##"
    static let fileMyShareListQuery = "jaxrs/attachment/list/share/##name##"
    static let fileMyEditorListQuery = "jaxrs/attachment/list/editor/##name##"
    
}

/**
 *  个人信息相关
 */
struct PersonContext {
    static let personContextKey = "x_organization_assemble_personal" //API上下文
    static let personInfoQuery = "jaxrs/person" //获得个人信息 提交个人信息
    static let personPasswordQuery = "jaxrs/password/decrypt" //获得明文口令
    static let personPasswordUpdateQuery = "jaxrs/password" //修改口令
    static let personIconUploadQuery  = "jaxrs/person/icon" //更改图像
}

/**
 *  通讯录相关
 */
struct ContactContext {
    static let contactsContextKey = "x_organization_assemble_express"
    static let contactsContextKeyV2 = "x_organization_assemble_control"
    static let topLevelUnitByIdentity = "jaxrs/unit/identity/level/object" // x_organization_assemble_express 上下文下的
    static let personInfoByNameQuery = "jaxrs/person/##name##"
    static let personIconByNameQuery = "servlet/icon/##name##" //获取图像
    static let personIconByNameQueryV2 = "jaxrs/person/##name##/icon"
    static let personIdentityByNameQuery = "jaxrs/identity/list/person/##name##"
    static let personOfDepartmentByNameQuery = "jaxrs/department/list/person/##name##"
    static let personOfGroupByNameQuery = "jaxrs/group/list/person/##name##/sup/nested"
    static let personSearchByKeyQuery = "jaxrs/person/list/like/##key##"
    static let personSearchByKeyQueryV2 = "jaxrs/person/list/like"
    static let topCompanyQuery = "jaxrs/complex/list/company/top"
    static let topUnitQuery = "jaxrs/unit/list/top"
    static let subGroupByNameQuery = "jaxrs/group/list/##name##/sub/direct"
    static let subCompanyByNameQuery = "jaxrs/complex/company/##name##"
    static let companySearchByKeyQuery = "jaxrs/company/list/like/##key##"
    static let subDepartByNameQuery = "jaxrs/complex/department/##name##"
    static let subUnitByNameQuery = "jaxrs/unit/list/##name##/sub/direct"
    static let subPersonByNameQuery = "jaxrs/complex/list/identity/person/##name##"
    static let subIdentityByNameQuery = "jaxrs/identity/list/unit/##name##"
    static let departmentSearchByKeyQuery = "jaxrs/department/list/like/##key##"
    static let subPersonInfoByNameQuery = "jaxrs/complex/person/##name##"
    static let personNextListQuery = "jaxrs/person/list/##id##/next/##count##"
    static let personPrevListQuery = "jaxrs/person/list/##id##/prev/##count##"
    static let personDirectManageQuery = "jaxrs/personattribute/##attribute##/person/##name##"
    static let OrgContextKey = "x_organization_assemble_control"
}


struct ApplicationContext {
    static let applicationContextKey = "x_processplatform_assemble_surface"
    static let applicationContextKey2 = "x_portal_assemble_surface"
    static let applicationListQuery = "jaxrs/application/list/complex"
    static let applicationOnlyListQuery = "jaxrs/application/list"
    static let applicationOnlyFlowByAppQuery = "jaxrs/process/list/application/##applicationId##"
    static let applicationListQuery2 = "application.json"
    static let applicationListQueryForPortal = "jaxrs/portal/list"
    static let applicationIconQuery = "jaxrs/portal/##applicationId##/icon"
}

//首页图片
struct HotpicContext {
    static let hotpicContextKey = "x_hotpic_assemble_control"
    static let hotpicImageByIdQuery = "servlet/picture/##id##"
    static let hotpicImageSizeByIdQuery = "servlet/picture/##id##/size/##size##"
    static let hotpicAllListQuery = "jaxrs/user/hotpic/filter/list/page/##page##/count/##count##"
}

/**
 *  工作任务相关
 */
struct TaskContext {
    static let taskContextKey = "x_processplatform_assemble_surface"
    static let taskDataContextKey = "x_processplatform_assemble_surface"
    static let taskDataSaveQuery = "jaxrs/data/work/##id##"
    static let taskWorkDeleteQuery = "jaxrs/work/##id##"
    static let todoTaskSaveAndSubmitQuery = "jaxrs/task/##id##/processing"
    static let todoTaskListQuery = "jaxrs/task/list/##id##/next/##count##"
    static let todoTaskListFilterQuery = "jaxrs/task/list/##id##/next/##count##/filter"
    static let todoTaskSubmitQuery = "jaxrs/task/##id##"
    static let todoTaskGetAttachmentInfoQuery = "jaxrs/attachment/##attachmentId##/work/##workId##"
    static let todoTaskGetAttachmentQuery = "jaxrs/attachment/download/##attachmentId##/work/##workId##"
    static let todoTaskUpReplaceAttachmentQuery = "jaxrs/attachment/update/##attachmentId##/work/##workId##"
    static let todoTaskUploadAttachmentQuery = "jaxrs/attachment/upload/work/##workId##"
    static let todoCreateAvaiableIdentityByIdQuery = "jaxrs/process/list/available/identity/process/##processId##"
}

struct ReadContext {
    static let readContextKey = "x_processplatform_assemble_surface"
    static let readSubmitQuery = "jaxrs/read/##id##"
    static let readProcessing = "jaxrs/read/##id##/processing"
    static let readListByPageSizeQuery = "jaxrs/read/list/##id##/next/##count##"
    static let readListByPageSizeFilterQuery = "jaxrs/read/list/##id##/next/##count##/filter"
}

struct TaskedContext {
    static let taskedContextKey = "x_processplatform_assemble_surface"
    static let taskedListByPageSizeQuery = "jaxrs/taskcompleted/list/##id##/next/##count##"
    static let taskedListByPageSizeFilterQuery = "jaxrs/taskcompleted/list/##id##/next/##count##/filter"
    static let taskedDataByIdQuery = "jaxrs/taskcompleted/##id##/reference"
    static let taskedRetractQuery = "jaxrs/work/##work##/retract"
    static let taskedGetAttachmentInfoQuery = "jaxrs/attachment/##attachmentId##/work/##workcompletedId##"
    static let taskedGetAttachmentQuery = "/jaxrs/attachment/download/##attachmentId##/workcompleted/##workcompletedId##"
}


struct ReadedContext {
    static let readedContextKey = "x_processplatform_assemble_surface"
    static let readedListByPageSizeQuery = "jaxrs/readcompleted/list/##id##/next/##count##"
    static let readedListByPageSizeFilterQuery = "jaxrs/readcompleted/list/##id##/next/##count##/filter"
    
}

struct WorkContext {
    static let workContextKey = "x_processplatform_assemble_surface"
    static let workCreateQuery = "jaxrs/work/process/##id##"
    
}

/**
 *  会议相关
 */
struct MeetingContext {
    static let meetingContextKey = "x_meeting_assemble_control"
    static let buildCreateQuery = "jaxrs/building"
    static let buildSearchPinYinQuery = "jaxrs/building//list/like/pinyin/##pinyin##"
    static let buildListQuery = "jaxrs/building/list"
    static let buildListSearchQuery = "jaxrs/building/list/like/##searchkey##"
    static let buildListPinYinInitialSearchQuery = "jaxrs/building/list/pinyininitial/##pinyininitial##"
    static let buildListStartAndCompletedQuery = "jaxrs/building/list/start/##start##/completed/##completed##"
    static let buildItemIdQuery = "jaxrs/building/##id##"
    static let roomCreateQuery = "jaxrs/room"
    static let roomSearchPinYinQuery = "jaxrs/room//list/like/pinyin/##pinyin##"
    static let roomListQuery = "jaxrs/room/list"
    static let roomListSearchQuery = "jaxrs/room/list/like/##searchkey##"
    static let roomLIstPinYinInitialSearchQuery = "jaxrs/room/list/pinyininitial/##pinyininitial##"
    static let roomItemIdQuery = "jaxrs/room/##id##"
    static let meetingCreateQuery = "jaxrs/meeting"
    static let meetingListDayCountQuery = "jaxrs/meeting/list/coming/day/##day##"
    static let meetingListMonthCountQuery = "jaxrs/meeting/list/coming/month/##month##"
    static let meetingListAcceptQuery = "jaxrs/meeting/list/wait/accept"
    static let meetingListConfirmQuery = "jaxrs/meeting/list/wait/confirm"
    static let meetingListYearMonthQuery  = "jaxrs/meeting/list/year/##year##/month/##month##"
    static let meetingListYearMonthDayQuery = "jaxrs/meeting/list/year/##year##/month/##month##/day/##day##"
    static let meetingItemIdQuery = "jaxrs/meeting/##id##"
    static let meetingItemAcceptIdQuery = "jaxrs/meeting/##id##/accept"
    static let meetingItemAddInviteIdQuery = "jaxrs/meeting/##id##/add/invite"
    static let meetingItemConfirmAllowIdQuery = "jaxrs/meeting/##id##/confirm/allow"
    static let meetingItemConfirmDenyIdQuery = "jaxrs/meeting/##id##/confirm/deny"
    static let meetingItemCompletedIdQuery = "jaxrs/meeting/##id##/manual/completed"
    static let meetingItemRejectIdQuery = "jaxrs/meeting/##id##/reject"

}

//考勤管理
//#define ZL_ATTENDANCE_MODULE_KEY @"x_attendance_assemble_control"
//#define ZL_ATTENDANCE_DETAIL_LIST_QUERY @"jaxrs/attendancedetail/filter/list/p0/next/p1"
//#define ZL_ATTENDANCE_DETAIL_MONTH_PIECHART_QUERY @"jaxrs/attendancedetail/filter/list/user"
//#define ZL_ATTENDANCE_SETTING_APPEALABLE_QUERY @"jaxrs/attendancesetting/code/APPEALABLE"
//#define ZL_ATTENDANCE_ATTENDANCE_INFO_QUERY @"jaxrs/attendanceappealInfo/appeal/p0"
//#define ZL_ATTENDANCE_ATTENDANCE_CHECK_LIST_QUERY @"jaxrs/attendanceappealInfo/filter/list/p0/next/p1"
//#define ZL_ATTENDANCE_ATTENDANCE_CHECK_SUBMIT_QUERY @"jaxrs/attendanceappealInfo/process/p0"
struct icContext {
    static let icContextKey = "x_attendance_assemble_control"
    static let detailListQuery = "jaxrs/attendancedetail/filter/list/##id##/next/##count##"
    static let detailMonthPieChartQuery = "jaxrs/attendancedetail/filter/list/user"
    static let settingAppealableQuery = "jaxrs/attendancesetting/code/APPEALABLE"
    static let attendanceInfoQuery = "jaxrs/attendanceappealInfo/appeal/##id##"
    static let attendanceCheckListQuery = "jaxrs/attendanceappealInfo/filter/list/##id##/next/##count##"
    static let attendanceCheckSubmitQuery = "jaxrs/attendanceappealInfo/process/##id##"
}

struct CMSContext {
    static let cmsContextKey = "x_cms_assemble_control"
    static let cmsCategoryQuery  = "jaxrs/appinfo/list/user/view"
    static let cmsCanPublishCategoryQuery  = "jaxrs/appinfo/get/user/publish/##appId##" //GET 查询app下当前用户能发布的category，返回的是app对象
    static let cmsCategoryListQuery = "jaxrs/categoryinfo/list/publish/app/##appId##"
    static let cmsCategoryDetailQuery = "jaxrs/document/filter/list/##id##/next/##count##"
    static let cmsDocumentDraftQuery = "jaxrs/document/draft/list/##id##/next/##count##" //PUT 查询草稿 {"categoryIdList":["36783507-3109-4701-a1bd-487e12340af5"],"creatorList":["楼国栋@louguodong@P"],"documentType":"全部"}
    static let cmsDocumentPost = "jaxrs/document" //保存修改文档 POST
    static let cmsAttachmentListQuery = "jaxrs/fileinfo/list/document/##documentId##"
    static let cmsAttachmentGET = "jaxrs/fileinfo/##attachId##/document/##documentId##" //附件对象获取
    static let cmsAttachmentDownloadQuery = "servlet/download/##id##/stream"
    static let cmsAttachmentDownloadNewQuery = "jaxrs/fileinfo/download/document/##attachId##" //下载附件 GET
    static let cmsAttachmentUpload = "jaxrs/fileinfo/upload/document/##docId##" //上传附件POST
    static let cmsAttachmentReplace = "jaxrs/fileinfo/update/document/##docId##/attachment/##attachId##" //替换附件 POST
}


//BBS
struct BBSContext {
    static let bbsContextKey = "x_bbs_assemble_control"
    static let getCategoryAndSectionQuery = "jaxrs/mobile/view/all" //所有分区及所有子板块
    static let getSectionItemQuery = "jaxrs/section/##id##" //板块详细信息列表
    static let sectionTopItemQuery = "jaxrs/subject/top/##id##" //板块内的置顶帖
    static let subjectByIdQuery = "jaxrs/subject/view/##id##" //获得具体帖子
    static let subjectFromSectionByPageQuery = "jaxrs/subject/filter/list/page/##pageNumber##/count/##pageSize##" //板块帖子分页查询
    static let uploadImageQuery = "servlet/upload/subject" //上传图片
    static let imageDisplayQuery = "servlet/download/subjectattachment/##id##/stream" //图片显示地址
    static let itemCreateQuery = "jaxrs/user/subject"//发帖
    static let itemReplyQuery = "jaxrs/user/reply" //回帖
    static let bbsSectionIconQuery = "servlet/section/##id##/icon"
    
}


struct DesktopContext {
    static let DesktopContextKey = "x_desktop"
    static let todoDesktopQuery = "workmobile.html?workid=##workid##"
    static let todoedDestopQuery = "workmobile.html?workCompletedId=##workCompletedId##"
    static let bbsItemDetailQuery  = "forumdocMobile.html?id=##subjectId##"
    static let cmsItemDetailQuery = "cmsdocMobile.html?id=##documentId##"
    static let appDetailQuery = "appMobile.html?app=portal.Portal&status=##status##"
}

