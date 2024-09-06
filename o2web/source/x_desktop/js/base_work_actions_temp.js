var actionJson = {
  "authentication": {
    "uri": "/jaxrs/authentication",
    "method": "GET"
  },
  "login": {
    "uri": "/jaxrs/authentication",
    "method": "POST"
  },
  "loginAdmin": {
    "uri": "/jaxrs/authentication/administrator",
    "method": "POST"
  },
  "logout": {
    "uri": "/jaxrs/authentication",
    "method": "DELETE"
  },
  "safeLogout": {
    "uri": "/jaxrs/authentication/safe/logout",
    "method": "GET"
  },
  "getLoginMode": {
    "uri": "/jaxrs/authentication/mode"
  }, //图片验证码默认可用，mode获取扫描二维码和手机验证码是否可用
  "getAuthentication": {
    "uri": "/jaxrs/authentication"
  }, //获取当前登录用户
  "loginByPassword": {
    "uri": "/jaxrs/authentication",
    "method": "POST"
  }, //用户登录.credential=xxxx,password=xxxx
  "getLoginCaptcha": {
    "uri": "/jaxrs/authentication/captcha/width/{width}/height/{height}"
  }, //验证码
  "loginByCaptcha": {
    "uri": "/jaxrs/authentication/captcha",
    "method": "POST"
  },
  "createCredentialCode": {
    "uri": "/jaxrs/authentication/code/credential/{credential}"
  }, //发送短信验证码
  "checkCredential": {
    "uri": "/jaxrs/authentication/check/credential/{credential}"
  }, //检查用户名是否存在
  "loginByCode": {
    "uri": "/jaxrs/authentication/code",
    "method": "POST"
  }, //使用短信验证码登录
  "loginByTwoFactor": {
    "uri": "/jaxrs/authentication/two/factory/login",
    "method": "POST"
  }, //双因素登录认证
  "getLoginBind": {
    "uri": "/jaxrs/authentication/bind"
  }, //扫描的二维码
  "checkBindStatus": {
    "uri": "/jaxrs/authentication/bind/meta/{meta}"
  }, //通过二维码进行登录，轮询判断是否登录
  "getOauthServer": {
    "uri": "/jaxrs/authentication/oauth/name/{name}",
    "method": "GET"
  },
  "listOauthServer": {
    "uri": "/jaxrs/authentication/oauth/list",
    "method": "GET"
  },
  "qywxOauthServer": {
    "uri": "/jaxrs/authentication/oauth/qywx/config",
    "method": "GET"
  },
  "dingdingOauthServer": {
    "uri": "/jaxrs/authentication/oauth/dingding/config",
    "method": "GET"
  },
  "loginOauthServer": {
    "uri": "/jaxrs/authentication/oauth/login/name/{name}/code/{code}/redirecturi/{redirectUri}",
    "method": "GET"
  },
  "loginOauthQywxServer": {
    "uri": "/jaxrs/authentication/oauth/login/qywx/code/{code}",
    "method": "GET"
  },
  "loginOauthDingdingServer": {
    "uri": "/jaxrs/authentication/oauth/login/dingding/code/{code}",
    "method": "GET"
  },
  "oauthBind": {
    "uri": "/jaxrs/authentication/oauth/bind/name/{name}/code/{code}/redirecturi/{redirectUri}",
    "method": "GET"
  },
  "clazz": "x_organization_assemble_authentication"
}

if (!o2.xAction.RestActions.Action["x_organization_assemble_authentication"]) o2.xAction.RestActions.Action["x_organization_assemble_authentication"] = new Class({Extends: o2.xAction.RestActions.Action});
o2.Actions.actions["x_organization_assemble_authentication"] = new o2.xAction.RestActions.Action["x_organization_assemble_authentication"]("x_organization_assemble_authentication", actionJson);
var actionJson = {
  "getApplication": {"uri": "/jaxrs/application/{id}"},
  "listApplication": {"uri": "/jaxrs/application/list"},
  "listApplications": {"uri": "/jaxrs/application/list"},
  "getApplicationIcon": {"uri": "/jaxrs/application/{id}/icon"},

  "listWorkNext": {"uri": "/jaxrs/work/list/{id}/next/{count}/application/{applicationId}"},
  "listWorkPrev": {"uri": "/jaxrs/work/list/{id}/prev/{count}/application/{applicationId}"},
  "listProcessCount": {"uri": "/jaxrs/work/list/count/application/{applicationId}/process"},
  "listWorkNextManage": {"uri": "/jaxrs/work/list/{id}/next/{count}/application/{applicationId}/manage"},
  "listWorkPrevManage": {"uri": "/jaxrs/work/list/{id}/prev/{count}/application/{applicationId}/manage"},
  "listProcessCountManage": {"uri": "/jaxrs/work/list/count/application/{applicationId}/process/manage"},
  "listProcessManage": {"uri": "/jaxrs/work/list/count/application/{applicationId}/process/manage"},

  "listWorkCreator": {"uri": "/jaxrs/work/list/{id}/next/{count}/creator/current"},
  "listWorkCreatorFilter": {"uri": "/jaxrs/work/list/{id}/next/{count}/creator/current/filter", "method": "POST"},

  "listAssignments": {"uri": "/jaxrs/work/{id}/assignment/manage"},
  "listRelatives": {"uri": "/jaxrs/work/{id}/relative/manage"},
  "listWorkByJob": {"uri": "/jaxrs/job/{job}/find/work/workcompleted"},
  "listAssignmentByWork": {"uri": "/jaxrs/work/{id}/assignment/manage"},

  "listWorkFilter": {"uri": "/jaxrs/work/list/{id}/next/{count}/application/{applicationId}/filter", "method": "POST"},
  "listFilterAttribute": {"uri": "/jaxrs/work/filter/attribute/application/{applicationId}"},
  "listWorkFilterManage": {"uri": "/jaxrs/work/list/{id}/next/{count}/application/{applicationId}/filter/manage", "method": "POST"},
  "listFilterAttributeManage": {"uri": "/jaxrs/work/filter/attribute/application/{applicationId}/manage"},

  "listWorkCompletedNext": {"uri": "/jaxrs/workcompleted/list/{id}/next/{count}/application/{applicationId}"},
  "listWorkCompletedPrev": {"uri": "/jaxrs/workcompleted/list/{id}/prev/{count}/application/{applicationId}"},
  "listWorkCompletedProcess": {"uri": "/jaxrs/workcompleted/list/count/application/{applicationId}/process"},

  "listWorkCompletedNextManage": {"uri": "/jaxrs/workcompleted/list/{id}/next/{count}/application/{applicationId}/manage"},
  "listWorkCompletedPrevManage": {"uri": "/jaxrs/workcompleted/list/{id}/prev/{count}/application/{applicationId}/manage"},
  "listWorkCompletedProcessManage": {"uri": "/jaxrs/workcompleted/list/count/application/{applicationId}/process/manage"},

  "listWorkCompletedFilter": {"uri": "/jaxrs/workcompleted/list/{id}/next/{count}/application/{applicationId}/filter", "method": "POST"},
  "listWorkCompletedFilterAttribute": {"uri": "/jaxrs/workcompleted/filter/attribute/application/{applicationId}"},
  "listWorkCompletedFilterManage": {"uri": "/jaxrs/workcompleted/list/{id}/next/{count}/application/{applicationId}/filter/manage", "method": "POST"},
  "listWorkCompletedFilterAttributeManage": {"uri": "/jaxrs/workcompleted/filter/attribute/application/{applicationId}/manage"},

  "listWorkCompletedAssignments": {"uri": "/jaxrs/workcompleted/{id}/assignment/manage"},

  "removeAllWork": {"uri": "/jaxrs/work/{id}/relative/manage", "method": "DELETE"},
  "removeWork": {"uri": "/jaxrs/work/{id}/single/manage", "method": "DELETE"},
  "removeWorkCompleted": {"uri": "/jaxrs/workcompleted/{id}/delete/manage", "method": "DELETE"},

  "resetTask": {"uri": "/jaxrs/task/{id}/reset/manage", "method": "PUT"},
  "flowTask": {"uri": "/jaxrs/task/{id}/processing/manage", "method": "PUT"},

  "resetRead": {"uri": "/jaxrs/read/{id}/reset/manage", "method": "PUT"},
  "flagRead": {"uri": "/jaxrs/read/{id}/processing/manage", "method": "PUT"},

  "flowWork": {"uri": "/jaxrs/work/{id}/processing", "method": "PUT"},

  "getWork": {"uri": "/jaxrs/work/{id}/manage"},
  "getWorkCompleted": {"uri": "/jaxrs/workcompleted/{id}/manage"},
  "getWorkContent": {"uri": "/jaxrs/work/{id}"},
  "getWorkInfor": {"uri": "/jaxrs/work/{id}"},


  "checkDraft": {"uri": "/jaxrs/work/{id}/close/check", "method": "GET"},

  "removeTask": {"uri": "/jaxrs/task/{id}/manage", "method": "DELETE"},
  "removeDone": {"uri": "/jaxrs/taskcompleted/{id}/manage", "method": "DELETE"},
  "removeRead": {"uri": "/jaxrs/read/{id}/manage", "method": "DELETE"},
  "removeReaded": {"uri": "/jaxrs/readcompleted/{id}/manage", "method": "DELETE"},

  "listDictionary": {"uri": "/jaxrs/applicationdict/list/application/{application}"},
  "getDictionary": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{applicationFlag}"},

  "updataDictionary": {"uri": "/jaxrs/applicationdict/{applicationDictFlag}/application/{applicationFlag}","method": "PUT"},

  "getDictRoot": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/data"},
  "getDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data"},
  "setDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "PUT"},
  "addDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "POST"},
  "deleteDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "DELETE"},

  "listSerialNumber": {"uri": "/jaxrs/serialnumber/list/application/{application}"},
  "getSerialNumber": {"uri": "/jaxrs/serialnumber/{id}"},
  "deleteSerialNumber": {"uri": "/jaxrs/serialnumber/{id}","method": "DELETE"},
  "updateSerialNumber": {"uri": "/jaxrs/serialnumber/{id}","method": "PUT"},

  "getRerouteTo": {"uri": "/jaxrs/process/{flag}/allowrerouteto"},
  "rerouteWork": {"uri": "/jaxrs/work/{id}/reroute/activity/{activityId}/activitytype/{type}", "method": "PUT"},

  "listView": {"uri": "/jaxrs/queryview/list/application/flag/{application}"},
  "loadView": {"uri": "/jaxrs/queryview/flag/{flag}/application/flag/{application}/execute", "method": "PUT"},
  "exportView": {"uri": "/jaxrs/queryview/flag/{flag}/application/flag/{applicationFlag}/excel", "method": "PUT"},
  "exportViewResult": {"uri": "/jaxrs/queryview/excel/result/{flag}"},


  "listStat": {"uri": "/jaxrs/querystat/list/application/flag/{application}"},
  "loadStat": {"uri": "/jaxrs/querystat/flag/{flag}/application/flag/{application}/execute", "method": "PUT"},

  "press": {"uri": "/jaxrs/taskcompleted/press/work/{work}"},

  "pauseTask": {"uri": "/jaxrs/task/v2/{id}/pause"},
  "resumeTask": {"uri": "/jaxrs/task/v2/{id}/resume"},

  "getCount": {"uri": "/jaxrs/work/count/{credential}"},

  "listApplicationStartable": {"uri": "/jaxrs/application/list/complex"},

  "listWorkByCreator": {"uri": "/jaxrs/work/list/creator"},

  "startWork": {"uri": "/jaxrs/work/process/{processId}", "method": "POST"},
  "processWork": {"uri": "/jaxrs/work/{id}/processing", "method": "PUT"},

  "createtWork": {"uri": "/jaxrs/work/process/{processId}", "method": "POST"},

  "draw": {"uri": "/jaxrs/draft/process/{processFlag}", "method": "POST"},
  "saveDraft": {"uri": "/jaxrs/draft", "method": "PUT"},
  "getDraft": {"uri": "/jaxrs/draft/{id}"},
  "listDraftNext": {"uri": "/jaxrs/draft/list/{id}/next/{count}"},
  "listDraftPrev": {"uri": "/jaxrs/draft/list/{id}/prev/{count}"},
  "listDraftPage": {"uri": "/jaxrs/draft/list/my/paging/{page}/size/{size}", "method": "POST"},
  "listDraftApplication": {"uri": "/jaxrs/draft/list/count/application"},
  "startDraft": {"uri": "/jaxrs/draft/{id}/start"},
  "deleteDraftWork": {"uri":  "/jaxrs/draft/{id}", "method": "DELETE"},

  "getTask": {"uri": "/jaxrs/task/{id}"},
  "saveTask": {"uri": "/jaxrs/task/{id}", "method": "POST"},
  "deleteTask": {"uri": "/jaxrs/task/{id}", "method": "DELETE"},
  "listTask": {"uri": "/jaxrs/task/list"},
  "listTaskNext": {"uri": "/jaxrs/task/list/{id}/next/{count}"},
  "listTaskNextByApp": {"uri": "/jaxrs/task/list/{id}/next/{count}/application/{application}"},
  "listTaskFilterCount": {"uri": "/jaxrs/task/filter/attribute"},
  "listTaskFilterCountFilter": {"uri": "/jaxrs/task/filter/attribute/filter", "method": "POST"},

  "listTaskMyFilterPaging": {"uri": "/jaxrs/task/list/my/filter/{page}/size/{size}", "method": "POST"},
  "listTaskMyPaging": {"uri": "/jaxrs/task/list/my/paging/{page}/size/{size}"},




  "getTaskCountFilter": {"uri": "/jaxrs/task/count/filter", "method": "POST"},
  "getTaskCount": {"uri": "/jaxrs/task/count/{name}"},
  "listTaskFilter": {"uri": "/jaxrs/task/list/{id}/next/{count}/filter", "method": "POST"},


  "listTaskPrev": {"uri": "/jaxrs/task/list/{id}/prev/{count}"},
  "processTask": {"uri": "/jaxrs/task/{id}/processing", "method": "POST"},

  "listTaskCompletedNext": {"uri": "/jaxrs/taskcompleted/list/{id}/next/{count}"},
  "listTaskCompletedNextByApp": {"uri": "/jaxrs/taskcompleted/list/{id}/next/{count}/application/{application}"},

  "listTaskCompletedPrev": {"uri": "/jaxrs/taskcompleted/list/{id}/prev/{count}"},

  "getSimpleJobByTask": {"uri": "/jaxrs/task/{id}/reference"},

  "listTaskApplication": {"uri": "/jaxrs/task/list/count/application"},
  "listTaskCompletedApplication": {"uri": "/jaxrs/taskcompleted/list/count/application"},

  "listTaskCompletedFilterCount": {"uri": "/jaxrs/taskcompleted/filter/attribute"},
  "listTaskCompletedFilterCountFilter": {"uri": "/jaxrs/taskcompleted/filter/attribute/filter", "method": "POST"},
  "listTaskCompletedMyFilterPaging": {"uri": "/jaxrs/taskcompleted/list/my/filter/{page}/size/{size}", "method": "POST"},
  "listTaskCompletedMyPaging": {"uri": "/jaxrs/taskcompleted/list/my/paging/{page}/size/{size}"},

  "listTaskCompletedFilter": {"uri": "/jaxrs/taskcompleted/list/{id}/next/{count}/filter", "method": "POST"},

  "getSimpleJobByTaskCompleted": {"uri": "/jaxrs/taskcompleted/{id}/reference"},
  "getTaskCompleted": {"uri": "/jaxrs/taskcompleted/{id}"},

  "listReadApplication": {"uri": "/jaxrs/read/list/count/application"},
  "listReadFilter": {"uri": "/jaxrs/read/list/{id}/next/{count}/filter", "method": "POST"},
  "listReadNext": {"uri": "/jaxrs/read/list/{id}/next/{count}"},
  "getSimpleJobByRead": {"uri": "/jaxrs/read/{id}/reference"},
  "listReadFilterCount": {"uri": "/jaxrs/read/filter/attribute"},
  "listReadFilterCountFilter": {"uri": "/jaxrs/read/filter/attribute/filter", "method": "POST"},
  "listReadMyFilterPaging": {"uri": "/jaxrs/read/list/my/filter/{page}/size/{size}", "method": "POST"},
  "listReadMyPaging": {"uri": "/jaxrs/read/list/my/paging/{page}/size/{size}"},

  "getReadCount": {"uri": "/jaxrs/read/count/{name}"},
  "setReaded": {"uri": "/jaxrs/read/{id}/processing", "method": "POST"},
  "getRead": {"uri": "/jaxrs/read/{id}"},

  "listReadedApplication": {"uri": "/jaxrs/readcompleted/list/count/application"},
  "listReadedFilter": {"uri": "/jaxrs/readcompleted/list/{id}/next/{count}/filter", "method": "POST"},
  "listReadedNext": {"uri": "/jaxrs/readcompleted/list/{id}/next/{count}"},
  "getSimpleJobByReaded": {"uri": "/jaxrs/readcompleted/{id}/reference"},
  "getReaded": {"uri": "/jaxrs/readcompleted/{id}"},
  "listReadedFilterCount": {"uri": "/jaxrs/readcompleted/filter/attribute"},
  "listReadedFilterCountFilter": {"uri": "/jaxrs/readcompleted/filter/attribute/filter", "method": "POST"},
  "listReadedMyFilterPaging": {"uri": "/jaxrs/readcompleted/list/my/filter/{page}/size/{size}", "method": "POST"},
  "listReadedMyPaging": {"uri": "/jaxrs/readcompleted/list/my/paging/{page}/size/{size}"},


  "listReviewApplication": {"uri": "/jaxrs/review/list/count/application"},
  "listReviewFilter": {"uri": "/jaxrs/review/list/{id}/next/{count}/filter", "method": "POST"},
  "listReviewNext": {"uri": "/jaxrs/review/list/{id}/next/{count}"},
  "getSimpleJobByReview": {"uri": "/jaxrs/review/{id}/reference"},
  "getReview": {"uri": "/jaxrs/review/{id}"},
  "listReviewFilterCount": {"uri": "/jaxrs/review/filter/attribute"},
  "listReviewFilterCountFilter": {"uri": "/jaxrs/review/filter/attribute/filter", "method": "POST"},
  "listReviewMyFilterPaging": {"uri": "/jaxrs/review/list/my/filter/{page}/size/{size}", "method": "POST"},
  "listReviewMyPaging": {"uri": "/jaxrs/review/list/my/paging/{page}/size/{size}"},
  "listReviewByJob": {"uri": "/jaxrs/review/list/job/{job}"},

  "manageDeleteReview": {"uri": "/jaxrs/review/{id}/application/{applicationFlag}/manage", "method": "DELETE"},
  "addWorkCompletedReview": {"uri": "/jaxrs/review/create/workcompleted", "method": "POST"},
  "addWorkReview": {"uri": "/jaxrs/review/create/work", "method": "POST"},

  "getJobByTask": {"uri": "/jaxrs/work/task/{id}/complex"},
  "getJobByWork": {"uri": "/jaxrs/work/{id}/complex"},
  "getJobByWorkMobile": {"uri": "/jaxrs/work/{id}/complex/mobile"},

  "getLogWithWork":  {"uri": "/jaxrs/worklog/list/work/{workId}"},


  "getWorkData": {"uri": "/jaxrs/data/work/{id}", "method": "GET"},
  "getWorkDataByPath": {"uri": "/jaxrs/data/work/{id}/{path}", "method": "GET"},
  "getWorkcompletedDataByPath": {"uri": "/jaxrs/data/workcompleted/{id}/{path}", "method": "GET"},
  "getJobDataByPath": {"uri": "/jaxrs/data/job/{id}/{path}", "method": "GET"},

  "getWorkcompletedData": {"uri": "/jaxrs/data/workcompleted/{id}", "method": "GET"},

  "saveData": {"uri": "/jaxrs/data/work/{id}", "method": "PUT"},
  "saveSectionData": {"uri": "/jaxrs/data/section/work/{id}", "method": "PUT"},
  "saveWorkCompletedData": {"uri": "/jaxrs/data/workcompleted/{id}", "method": "PUT"},

  "getJobByWorkCompleted": {"uri": "/jaxrs/workcompleted/{id}/complex"},
  "getJobByWorkCompletedMobile": {"uri": "/jaxrs/workcompleted/{id}/complex/mobile"},

  "uploadAttachment": {"uri": "/jaxrs/attachment/upload/work/{id}", "method": "POST", "enctype": "formData"},
  "uploadAttachmentByWorkCompleted": {"uri": "/jaxrs/attachment/upload/workcompleted/{id}", "method": "POST", "enctype": "formData"},
  "V2UploadWorkOrWorkCompleted": {"uri": "/jaxrs/attachment/v2/upload/workorworkcompleted/{workOrWorkCompleted}", "method": "POST", "enctype": "formData"},


  "replaceAttachment": {"uri": "/jaxrs/attachment/update/{id}/work/{workid}", "method": "POST", "enctype": "formData"},

  "getAttachmentData": {"uri": "/jaxrs/attachment/download/{id}/work/{workid}", "method": "GET"},
  "getWorkcompletedAttachmentData": {"uri": "/jaxrs/attachment/download/{id}/workcompleted/{workCompletedId}", "method": "GET"},

  "getAttachmentStream": {"uri": "/jaxrs/attachment/download/{id}/work/{workid}/stream", "method": "GET"},
  "getWorkcompletedAttachmentStream": {"uri": "/jaxrs/attachment/download/{id}/workcompleted/{workCompletedId}/stream", "method": "GET"},

  "getAttachment": {"uri": "/jaxrs/attachment/{id}/work/{workid}"},
  "getAttachmentWorkcompleted": {"uri": "/jaxrs/attachment/{id}/workcompleted/{workCompletedId}"},
  "deleteAttachment": {"uri": "/jaxrs/attachment/{id}/work/{workid}", "method": "DELETE"},
  "deleteWorkCompletedAttachment": {"uri": "/jaxrs/attachment/{id}/workcompleted/{workCompletedId}", "method": "DELETE"},

  "configAttachment": {"uri": "/jaxrs/attachment/edit/{id}/work/{workId}", "method": "PUT"},
  "getAttachmentOCR": {"uri": "/jaxrs/attachment/{id}/work/{workId}/text"},
  "setAttachmentOCR": {"uri": "/jaxrs/attachment/edit/{id}/work/{workId}/text", "method": "PUT"},

  "listWorkAttachments": {"uri": "/jaxrs/attachment/list/work/{workId}"},
  "listWorkCompletedAttachments": {"uri": "/jaxrs/attachment/list/workcompleted/{workCompletedId}"},

  "retractWork": {"uri": "/jaxrs/work/{id}/retract", "method": "PUT"},
  "resetWork": {"uri": "/jaxrs/task/{id}/reset", "method": "PUT"},

  "deleteWork": {"uri": "/jaxrs/work/{id}", "method": "DELETE"},
  "abandoned": {"uri": "/jaxrs/snap/work/{workId}/type/abandoned"},


  "getJobByWorkAssignForm": {"uri": "/jaxrs/work/{id}/complex/appoint/form/{formTag}"},
  "getJobByWorkAssignFormMobile": {"uri": "/jaxrs/work/{id}/complex/appoint/form/{formTag}/mobile"},
  "getJobByWorkCompletedAssignForm": {"uri": "/jaxrs/workcompleted/{id}/complex/appoint/form/{formTag}"},
  "getJobByWorkCompletedAssignFormMobile": {"uri": "/jaxrs/workcompleted/{id}/complex/appoint//form/{formTag}/mobile"},

  "lookupView": {"uri": "/jaxrs/view/{id}"},
  "getView": {"uri": "/jaxrs/view/{id}"},

  "getForm": {"uri": "/jaxrs/form/{id}/application/{applicationFlag}"},
  "getFormMobile": {"uri": "/jaxrs/form/{id}/application/{applicationFlag}/mobile"},
  "getScript": {"uri": "/jaxrs/script/{flag}/application/{applicationFlag}"},
  "getScriptByName": {"uri": "/jaxrs/script/{name}/application/{applicationId}","method": "POST"},
  "getFormV2": {"uri": "/jaxrs/form/v2/{id}?t={tag}"},
  "getFormV2Mobile": {"uri": "/jaxrs/form/v2/{id}/mobile?t={tag}"},
  "getScriptByNameV2": {"uri": "/jaxrs/script/{flag}/application/{applicationFlag}/imported"},

  "sendReaderByWork" : {"uri":"/jaxrs/read/work/{workId}", "method": "POST"},
  "sendReaderByWorkCompleted" : {"uri":"/jaxrs/read/workcompleted/{workCompletedId}", "method": "POST"},
  "copyAttachmentToWork" : {"uri":"/jaxrs/attachment/copy/work/{workId}", "method":"POST"},

  "listProcess": {"uri": "/jaxrs/process/list/application/{applicationFlag}"},
  "getProcess": {"uri": "/jaxrs/process/{id}/complex"},
  "getProcessByName": {"uri": "/jaxrs/process/{flag}/application/{applicationFlag}"},

  "readFile": {"uri": "/jaxrs/file/{flag}/application/{applicationFlag}/content"},
  "listFile": {"uri": "/jaxrs/file/list/application/{applicationFlag}"},

  "changeSite": {"uri": "/jaxrs/attachment/{id}/work/{workId}/change/site/{site}"},

  "addSplit": {"uri": "/jaxrs/work/{id}/add/split", "method": "PUT"},

  "loadWork": {"uri": "/jaxrs/work/workorworkcompleted/{workOrWorkCompleted}"},
  "getWorkLog": {"uri": "/jaxrs/worklog/list/workorworkcompleted/{workOrWorkCompleted}"},
  "getRecordLog": {"uri": "/jaxrs/record/list/workorworkcompleted/{workOrWorkCompleted}"},
  "listAttachments": {"uri": "/jaxrs/attachment/list/workorworkcompleted/{workOrWorkCompleted}"},
  "listTaskcompleted": {"uri": "/jaxrs/taskcompleted/list/workorworkcompleted/{workOrWorkCompleted}"},
  "listReadcompleted": {"uri": "/jaxrs/readcompleted/list/workorworkcompleted/{workOrWorkCompleted}"},
  "listRead": {"uri": "/jaxrs/read/list/workorworkcompleted/{workOrWorkCompleted}"},
  "getWorkControl": {"uri": "/jaxrs/control/workorworkcompleted/{workOrWorkCompleted}"},
  "getWorkForm": {"uri": "/jaxrs/form/workorworkcompleted/{workOrWorkCompleted}"},
  "getWorkFormMobile": {"uri": "/jaxrs/form/workorworkcompleted/{workOrWorkCompleted}/mobile"},

  "loadWorkV2": {"uri": "/jaxrs/work/v2/workorworkcompleted/{workOrWorkCompleted}"},
  "lookupFormWithWork": {"uri": "/jaxrs/form/v2/lookup/workorworkcompleted/{workOrWorkCompleted}"},
  "lookupFormWithWorkMobile": {"uri": "/jaxrs/form/v2/lookup/workorworkcompleted/{workOrWorkCompleted}/mobile"},

  "listTaskByJob": {"uri": "/jaxrs/task/list/job/{job}"},
  "listTaskCompletedByJob": {"uri": "/jaxrs/taskcompleted/list/job/{job}"},
  "listReadByJob": {"uri": "/jaxrs/read/list/job/{job}"},
  "listReadCompletedByJob": {"uri": "/jaxrs/readcompleted/list/job/{job}"},

  "listTaskByWork": {"uri": "/jaxrs/task/list/work/{work}"},
  "listTaskCompletedByWork": {"uri": "/jaxrs/taskcompleted/list/work/{work}"},
  "listReadByWork": {"uri": "/jaxrs/read/list/work/{work}"},
  "listReadCompletedByWork": {"uri": "/jaxrs/readcompleted/list/work/{work}"},

  "listTaskCompletedByWorkOrWorkCompleted": {"uri": "/jaxrs/taskcompleted/list/workorworkcompleted/{workOrWorkCompleted}"},
  "listReadByWorkOrWorkCompleted": {"uri": "/jaxrs/read/list/workorworkcompleted/{workOrWorkCompleted}"},
  "listReadCompletedByWorkOrWorkCompleted": {"uri": "/jaxrs/readcompleted/list/workorworkcompleted/{workOrWorkCompleted}"},

  "rollback": {"uri": "/jaxrs/work/{id}/rollback", "method": "PUT"},
  "rollbackWorkcompleted": {"uri": "/jaxrs/workcompleted/{flag}/rollback", "method": "PUT"},

  "setTaskOpinion": {"uri": "/jaxrs/task/{id}/opinion/manage", "method": "PUT"},
  "setTaskCompletedOpinion": {"uri": "/jaxrs/taskcompleted/{id}/opinion/manage", "method": "PUT"},
  "setReadOpinion": {"uri": "/jaxrs/read/{id}/opinion/manage", "method": "PUT"},
  "setReadCompletedOpinion": {"uri": "/jaxrs/readcompleted/{id}/opinion/manage", "method": "PUT"},

  "manageListTaskFilterByPage": {"uri": "/jaxrs/task/list/filter/{page}/size/{pageSize}/manage", "method": "POST"},
  "manageListTaskDoneFilterByPage": {"uri": "/jaxrs/taskcompleted/list/filter/{page}/size/{pageSize}/manage", "method": "POST"},
  "manageListReadFilterByPage": {"uri": "/jaxrs/read/list/filter/{page}/size/{pageSize}/manage", "method": "POST"},
  "manageListReadDoneFilterByPage": {"uri": "/jaxrs/readcompleted/list/filter/{page}/size/{pageSize}/manage", "method": "POST"},

  "manageListWorkFilterByPage": {"uri": "/jaxrs/work/list/filter/{page}/size/{pageSize}/manage", "method": "POST"},
  "manageListWorkCompletedFilterByPage": {"uri": "/jaxrs/workcompleted/list/filter/{page}/size/{pageSize}/manage", "method": "POST"},

  "docToWord": {"uri": "/jaxrs/attachment/doc/to/word/workorworkcompleted/{workId}", "method": "POST"},

  "getRouteSelectConfig" : {"uri":"/jaxrs/route/{id}/selectconfig"},
  "listRoute" : {"uri":"/jaxrs/route/list", "method":"PUT"},
  "getRoute" : {"uri":"/jaxrs/route/{id}"},

  "getReadRecord" : {"uri":"/jaxrs/readrecord/list/workorworkcompleted/{workOrWorkCompleted}"},



  "clazz": "x_processplatform_assemble_surface"

}

if (!o2.xAction.RestActions.Action["x_processplatform_assemble_surface"]) o2.xAction.RestActions.Action["x_processplatform_assemble_surface"] = new Class({Extends: o2.xAction.RestActions.Action});
o2.Actions.actions["x_processplatform_assemble_surface"] = new o2.xAction.RestActions.Action["x_processplatform_assemble_surface"]("x_processplatform_assemble_surface", actionJson);
var actionJson = {
  //返回人员的所有服务---------------------------------------------
  //人员增删改查
  "addPerson": {"uri": "/jaxrs/person", "method": "POST"},
  "getPerson": {"uri": "/jaxrs/person/{id}"},
  "updatePerson": {"uri": "/jaxrs/person/{id}", "method": "PUT"},
  "removePerson": {"uri": "/jaxrs/person/{id}", "method": "DELETE"},
  "deletePerson": {"uri": "/jaxrs/person/{id}", "method": "DELETE"},

  //列出指定群组所包含的人员（直接包含 嵌套包含）
  "listPersonDirect": {"uri": "/jaxrs/person/list/group/{id}/sub/direct"},
  "listPersonNested": {"uri": "/jaxrs/person/list/group/{id}/sub/nested"},
  //列出指定角色包含的人员
  //"listPersonByRole": {"uri": "/jaxrs/person/list/role/{id}"},

  //按拼音搜索（全拼 首字母）
  "listPersonByPinyin": {"uri": "/jaxrs/person/list/like/pinyin", "method": "PUT"},
  "listPersonByPinyininitial": {"uri": "/jaxrs/person/list/pinyininitial", "method": "PUT"},
  //按关键字搜索
  "listPersonByKey": {"uri": "/jaxrs/person/list/like", "method": "PUT"},

  //更换头像
  "changePersonIcon": {"uri": "/jaxrs/person/{id}/icon", "method": "PUT", "enctype": "formData"},
  //获取头像
  "getPersonIcon": {"uri": "/jaxrs/person/{flag}/icon"},

  //列出所有人员
  "listPersonNext": {"uri": "/jaxrs/person/list/{id}/next/{count}"},
  "listPersonPrev": {"uri": "/jaxrs/person/list/{id}/prev/{count}"},

  //修改密码
  "changePassword": {"uri": "/jaxrs/person/{name}/set/password", "method": "PUT"},
  //重置密码
  "resetPassword": {"uri": "/jaxrs/person/{flag}/reset/password"},

  //-------------------------------------------------------------

  //返回人员属性的所有服务------------------------------------------
  //人员属性增删改查
  "addPersonAttribute": {"uri": "/jaxrs/personattribute", "method": "POST"},
  "getPersonAttribute": {"uri": "/jaxrs/personattribute/{id}"},
  "updatePersonAttribute": {"uri": "/jaxrs/personattribute/{id}", "method": "PUT"},
  "removePersonAttribute": {"uri": "/jaxrs/personattribute/{id}", "method": "DELETE"},
  "deletePersonAttribute": {"uri": "/jaxrs/personattribute/{id}", "method": "DELETE"},

  //列出指定人员的所有属性
  "listPersonAttribute": {"uri": "/jaxrs/personattribute/list/person/{id}"},
  //-------------------------------------------------------------

  //返回身份的所有服务---------------------------------------------
  //身份增删改查
  "addIdentity": {"uri": "/jaxrs/identity", "method": "POST"},
  "getIdentity": {"uri": "/jaxrs/identity/{id}"},
  "updateIdentity": {"uri": "/jaxrs/identity/{id}", "method": "PUT"},
  "removeIdentity": {"uri": "/jaxrs/identity/{id}", "method": "DELETE"},
  "deleteIdentity": {"uri": "/jaxrs/identity/{id}", "method": "DELETE"},

  //身份排序
  "orderIdentity": {"uri": "/jaxrs/identity/{flag}/order/before/{followFlag}"},

  //列出指定组织的所有身份
  //"listIdentity": {"uri": "/jaxrs/identity/list/department/{id}"},

  //按拼音搜索（全拼 首字母）
  "listIdentityByPinyin": {"uri": "/jaxrs/identity/list/like/pinyin", "method": "PUT"},
  "listIdentityByPinyininitial": {"uri": "/jaxrs/identity/list/pinyininitial", "method": "PUT"},
  //按关键字搜索
  "listIdentityByKey": {"uri": "/jaxrs/identity/list/like", "method": "PUT"},

  //列出指定人的所有身份
  "listIdentityByPerson": {"uri": "/jaxrs/identity/list/person/{id}"},
  //列出指定人的所有身份、及身份所在组织和职务等信息。。。
  //"listIdentityComplexByPerson": {"uri": "/jaxrs/identity/complex/list/person/{id}"},
  //列出指定组织的身份
  "listIdentityWithUnit": {"uri": "/jaxrs/identity/list/unit/{unit}"},
  //列出指定职务的所有身份
  "listIdentityWithDuty": {"uri": "/jaxrs/identity/list/unitduty/name/{flag}"},

  //列出所有身份
  "listIdentityNext": {"uri": "/jaxrs/identity/list/{id}/next/{count}"},
  "listIdentityPrev": {"uri": "/jaxrs/identity/list/{id}/prev/{count}"},

  "listIdentityWidthUnitWithDutyName": {"uri": "/jaxrs/identity/list/{unit}/unitduty/name/{duty}"},

  //-------------------------------------------------------------

  //返回角色的所有服务---------------------------------------------
  "addRole": {"uri": "/jaxrs/role", "method": "POST"},
  "getRole": {"uri": "/jaxrs/role/{id}"},
  "updateRole": {"uri": "/jaxrs/role/{id}", "method": "PUT"},
  "removeRole": {"uri": "/jaxrs/role/{id}", "method": "DELETE" },
  "deleteRole": {"uri": "/jaxrs/role/{id}", "method": "DELETE" },

  //按拼音搜索（全拼 首字母）
  "listRoleByPinyin": {"uri": "/jaxrs/role/list/like/pinyin", "method": "PUT"},
  "listRoleByPinyininitial": {"uri": "/jaxrs/role/list/pinyininitial", "method": "PUT"},
  //按关键字搜索
  "listRoleByKey": {"uri": "/jaxrs/role/list/like", "method": "PUT"},

  //列出指定群组拥有的角色
  "listRoleByGroup": {"uri": "/jaxrs/role/list/group/{id}"},
  //列出指定人员拥有的角色
  "listRoleByPerson": {"uri": "/jaxrs/role/list/person/{id}"},

  //列出所有角色
  "listRoleNext": {"uri": "/jaxrs/role/list/{id}/next/{count}"},
  "listRolePrev": {"uri": "/jaxrs/role/list/{id}/prev/{count}"},
  //-------------------------------------------------------------

  //返回群组的所有服务---------------------------------------------
  //群组增删改查
  "addGroup": {"uri": "/jaxrs/group", "method": "POST"},
  "getGroup": {"uri": "/jaxrs/group/{id}"},
  "updateGroup": {"uri": "/jaxrs/group/{id}", "method": "PUT"},
  "removeGroup": {"uri": "/jaxrs/group/{id}", "method": "DELETE"},
  "deleteGroup": {"uri": "/jaxrs/group/{id}", "method": "DELETE"},

  //根据拼音搜索（全拼 首字母）
  "listGroupByPinyin": {"uri": "/jaxrs/group/list/like/pinyin", "method": "PUT"},
  "listGroupByPinyininitial": {"uri": "/jaxrs/group/list/pinyininitial", "method": "PUT"},
  //根据关键字搜索
  "listGroupByKey": {"uri": "/jaxrs/group/list/like", "method": "PUT"},

  //列出人员所在群组（直接包含群组  嵌套群组）
  "listGroupDirectByPerson": {"uri": "/jaxrs/group/list/person/{id}/sup/direct"},
  "listGroupNestedByPerson": {"uri": "/jaxrs/group/list/person/{id}/sup/nested"},

  //列出指定群组的上级和下级群组（直接 和 所有）
  "listSubGroupDirect": {"uri": "/jaxrs/group/list/{id}/sub/direct"},
  "listSubGroupNested": {"uri": "/jaxrs/group/list/{id}/sub/nested"},
  "listSupGroupDirect": {"uri": "/jaxrs/group/list/{id}/sup/direct"},
  "listSupGroupNested": {"uri": "/jaxrs/group/list/{id}/sup/nested"},
  //列出指定角色包含的群组
  //"listGroupByRole": {"uri": "/jaxrs/group/list/role/{id}"},

  //列出所有群组
  "listGroupNext": {"uri": "/jaxrs/group/list/{id}/next/{count}"},
  "listGroupPrev": {"uri": "/jaxrs/group/list/{id}/prev/{count}"},
  //-------------------------------------------------------------

  //返回组织的所有接口
  "addUnit": {"uri": "/jaxrs/unit", "method": "POST"},
  "getUnit": {"uri": "/jaxrs/unit/{id}"},
  "updateUnit": {"uri": "/jaxrs/unit/{id}", "method": "PUT"},
  "removeUnit": {"uri": "/jaxrs/unit/{id}", "method": "DELETE"},
  "deleteUnit": {"uri": "/jaxrs/unit/{id}", "method": "DELETE"},

  //根据拼音搜索（全拼 首字母）
  "listUnitByPinyin": {"uri": "/jaxrs/unit/list/like/pinyin", "method": "PUT"},
  "listUnitByPinyininitial": {"uri": "/jaxrs/unit/list/pinyininitial", "method": "PUT"},
  //根据关键字搜索
  "listUnitByKey": {"uri": "/jaxrs/unit/list/like", "method": "PUT"},

  //列出顶层组织
  "listTopUnit": {"uri": "/jaxrs/unit/list/top"},
  //根据指定的类型，列出组织
  "listUnitByType": {"uri": "/jaxrs/unit/list/unit/type", "method": "PUT"},
  //列出组织类型
  "listUnitType": {"uri": "/jaxrs/unit/list/type"},
  //列出所有组织
  "listUnitNext": {"uri": "/jaxrs/unit/list/{id}/next/{count}"},
  "listUnitPrev": {"uri": "/jaxrs/unit/list/{id}/prev/{count}"},

  //列出指定组织的上级和下级组织（直接 和 所有）
  "listSubUnitDirect": {"uri": "/jaxrs/unit/list/{id}/sub/direct"},
  "listSubUnitNested": {"uri": "/jaxrs/unit/list/{id}/sub/nested"},
  "listSubUnitNestedByType": {"uri": "/jaxrs/unit/list/{flag}/sub/direct/type/{type}"},

  "getSupUnitDirect": {"uri": "/jaxrs/unit/{id}/sup/direct"},
  "listSupUnitNested": {"uri": "/jaxrs/unit/list/{id}/sup/nested"},
  "listSupUnitNestedByType": {"uri": "/jaxrs/unit/list/{flag}/sup/nested/type/{type}"},

  "getUnitWithIdentityWithLevel": {"uri": "/jaxrs/unit/identity/{id}/level/{level}"},
  "getUnitWithIdentityWithType": {"uri": "/jaxrs/unit/identity/{id}/type/{type}"},
  //-------------------------------------------------------------

  //返回组织属性的所有接口
  "addUnitattribute": {"uri": "/jaxrs/unitattribute", "method": "POST"},
  "getUnitattribute": {"uri": "/jaxrs/unitattribute/{id}"},
  "updateUnitattribute": {"uri": "/jaxrs/unitattribute/{id}", "method": "PUT"},
  "removeUnitattribute": {"uri": "/jaxrs/unitattribute/{id}", "method": "DELETE"},
  "deleteUnitattribute": {"uri": "/jaxrs/unitattribute/{id}", "method": "DELETE"},

  //列出组织属性
  "listUnitattribute": {"uri": "/jaxrs/unitattribute/list/unit/{id}"},

  //列出所有组织属性
  "listUnitattributeNext": {"uri": "/jaxrs/unitattribute/list/{id}/next/{count}"},
  "listUnitattributePrev": {"uri": "/jaxrs/unitattribute/list/{id}/prev/{count}"},
  //-------------------------------------------------------------

  //返回组织职务的所有接口
  "addUnitduty": {"uri": "/jaxrs/unitduty", "method": "POST"},
  "getUnitduty": {"uri": "/jaxrs/unitduty/{id}"},
  "updateUnitduty": {"uri": "/jaxrs/unitduty/{id}", "method": "PUT"},
  "removeUnitduty": {"uri": "/jaxrs/unitduty/{id}", "method": "DELETE"},
  "deleteUnitduty": {"uri": "/jaxrs/unitduty/{id}", "method": "DELETE"},

  //获取去重的职务名称
  "listUnitdutyName": {"uri": "/jaxrs/unitduty/distinct/name"},
  //搜索职务名称
  "listUnitdutyNameByKey": {"uri": "/jaxrs/unitduty/distinct/name/like/{key}"},

  //根据关键字搜索
  "listUnitdutyByKey": {"uri": "/jaxrs/unitduty/distinct/name/like/{key}"},
  //列出指定身份的所有职务
  "listUnitdutyByIdentity": {"uri": "/jaxrs/unitduty/list/identity/{flag}"},

  //列出指定组织的所有职务
  "listUnitdutyByUnit": {"uri": "/jaxrs/unitduty/list/unit/{flag}"},
  "listUnitdutyByName": {"uri": "/jaxrs/unitduty/list/name/{name}"},

  //列出所有组织职务
  "listUnitdutyNext": {"uri": "/jaxrs/unitduty/list/{id}/next/{count}"},
  "listUnitdutyPrev": {"uri": "/jaxrs/unitduty/list/{id}/prev/{count}"},
  //-------------------------------------------------------------

  "getImportPersonTemplate": {"uri": "/jaxrs/inputperson/template"},
  "getImportPersonResault": {"uri": "/jaxrs/inputperson/result/flag/{flag}"},
  "importPerson": {"uri": "/jaxrs/inputperson", "method": "POST", "enctype": "formData"},
  "wipeAll": {"uri": "/jaxrs/inputperson/wipe"},

  "clazz": "x_organization_assemble_control"
}
if (!o2.xAction.RestActions.Action["x_organization_assemble_control"]) o2.xAction.RestActions.Action["x_organization_assemble_control"] = new Class({Extends: o2.xAction.RestActions.Action});
o2.Actions.actions["x_organization_assemble_control"] = new o2.xAction.RestActions.Action["x_organization_assemble_control"]("x_organization_assemble_control", actionJson);
var actionJson = {
  "getQuery": {"uri": "/jaxrs/query/{flag}"},
  "listQuery": {"uri": "/jaxrs/query/list"},
  "listQueryByKey": {"uri": "/jaxrs/query/list/key/{key}"},

  "getStatById": {"uri": "/jaxrs/stat/{id}"},
  "getStat": {"uri": "/jaxrs/stat/flag/{flag}/query/{queryFlag}"},
  "listStat": {"uri": "/jaxrs/stat/list/query/{queryFlag}"},
  "loadStatById": {"uri": "/jaxrs/stat/{id}/execute", "method": "PUT"},
  "loadStat": {"uri": "/jaxrs/stat/flag/{flag}/query/{queryFlag}/execute", "method": "PUT"},

  "getViewById": {"uri": "/jaxrs/view/{id}"},
  "getView": {"uri": "/jaxrs/view/flag/{flag}/query/{queryFlag}"},
  "listView": {"uri": "/jaxrs/view/list/query/{queryFlag}"},
  "loadViewById": {"uri": "/jaxrs/view/{id}/execute", "method": "PUT"},
  "loadView": {"uri": "/jaxrs/view/flag/{flag}/query/{queryFlag}/execute", "method": "PUT"},
  "bundleView": {"uri": "/jaxrs/view/{id}/bundle", "method": "PUT"},

  "getTableById" : { "uri": "/jaxrs/table/{id}" },

  "exportViewWithQuery": {"uri": "/jaxrs/view/flag/{flag}/query/{queryFlag}/excel", "method": "PUT"},
  "exportView": {"uri": "/jaxrs/view/{id}/excel", "method": "PUT"},
  "getViewExcel": {"uri": "/jaxrs/view/excel/result/{flag}"},

  "search": {"uri": "/jaxrs/segment/key/{key}"},
  "listSearchEntry": {"uri": "/jaxrs/segment/list/entry", "method": "POST"},

  "executeStatement" : { "uri": "/jaxrs/statement/{flag}/execute/page/{page}/size/{size}", "method": "POST" },

  "listRowNext" : { "uri" : "/jaxrs/table/list/{tableFlag}/row/{id}/next/{count}" },
  "listRowPrev" : { "uri" : "/jaxrs/table/list/{tableFlag}/row/{id}/prev/{count}" },
  "listRowSelectWhere" : { "uri" : "/jaxrs/table/list/{tableFlag}/row/select/where/{where}" }, //通过where 获取表中的数据,格式为jpql语法,o.name='zhangsan'
  "listRowCountWhere" : { "uri" : "/jaxrs/table/{tableFlag}/row/count/where/{where}" },
  "getRow" : { "uri": "/jaxrs/table/{tableFlag}/row/{id}" }, //获取表中某一行数据
  "updateRow":{ "uri": "/jaxrs/table/{tableFlag}/row/{id}", "method": "PUT" }, //更新指定表中指定行数据.
  "insertRow":{ "uri": "/jaxrs/table/{tableFlag}/row", "method": "POST" },//插入一行
  "countRowWhere" : { "uri": "/jaxrs/table/{tableFlag}/row/count/where/{where}"},//通过where 统计数量
  "deleteRow" : { "uri": "/jaxrs/table/{tableFlag}/row/{id}", "method": "DELETE" }, //更新指定表中指定行数据.
  "deleteAllRow" : { "uri": "/jaxrs/table/{tableFlag}/row/delete/all", "method": "DELETE"}, //通过where 统计数量

  "calculateNeural": {"uri": "/jaxrs/neural/list/calculate/model/{modelFlag}/work/{workId}"},

  "executImportModel": { "uri" : "/jaxrs/importmodel/{id}/execute", "method": "POST"},
  "listImportModel": { "uri" : "/jaxrs/importmodel/list/query/{queryFlag}"},
  "getImportModelById": { "uri" : "/jaxrs/importmodel/{id}"},
  "getImportModel": { "uri" : "/jaxrs/importmodel/flag/{flag}/query/{queryFlag}"},

  "getUUID": { "uri" : "/jaxrs/importmodel/uuid"},

  "getImportModelRecord": { "uri" : "/jaxrs/importmodel/record/{recordId}"},
  "getImportModelRecordStatus": { "uri" : "/jaxrs/importmodel/record/{recordId}/status"},
  "listImportModelRecord": {"uri": "/jaxrs/importmodel/list/record/paging/{page}/size/{size}", "method": "POST"},
  "listImportModelRecordItem": { "uri" : "/jaxrs/importmodel/list/record/item/paging/{page}/size/{size}", "method": "POST"},

  "clazz": "x_query_assemble_surface"
}

if (!o2.xAction.RestActions.Action["x_query_assemble_surface"]) o2.xAction.RestActions.Action["x_query_assemble_surface"] = new Class({Extends: o2.xAction.RestActions.Action});
o2.Actions.actions["x_query_assemble_surface"] = new o2.xAction.RestActions.Action["x_query_assemble_surface"]("x_query_assemble_surface", actionJson);
var actionJson = {
  "listApplication": {"uri": "/jaxrs/appinfo/list/user/view/all" },
  "listCMSApplication": {"uri": "/jaxrs/appinfo/list/user/view/all" },
  "getApplication": {"uri": "/jaxrs/appinfo/{id}"},
  "getCMSApplication": {"uri": "/jaxrs/appinfo/{id}"},

  "listAllAppType" : {"uri":"/jaxrs/appinfo/list/appType"},
  "listAllAppTypeByManager" : {"uri":"/jaxrs/appinfo/list/appType/manager"},
  "listWhatICanManageWithAppType" : {"uri":"/jaxrs/appinfo/list/manage/type/{appType}"},
  "listWhatICanViewWithAppType" : {"uri":"/jaxrs/appinfo/list/user/view/all/type/{appType}"},

  "listCMSCategory" : {"uri": "/jaxrs/categoryinfo/list/view/app/{appId}/all" },
  "getCMSForm" : {"uri": "/jaxrs/form/{id}" },

  "listApplicationView": {"uri": "/jaxrs/queryview/list/all"},
  "listCMSApplicationView": {"uri": "/jaxrs/queryview/list/all"},

  "getId" : {"uri":"/jaxrs/uuid/random"},

  "listColumn": {"uri": "/jaxrs/appinfo/list/user/view/all" }, //"/jaxrs/appinfo/list/all"},
  "listColumnByPublish": {"uri": "/jaxrs/appinfo/list/user/publish" },
  "listColumnByAdmin": {"uri": "/jaxrs/appinfo/list/admin" },
  "getColumn": {"uri": "/jaxrs/appinfo/{id}"},
  "getColumnByName": {"uri": "/jaxrs/appinfo/{id}"},
  "addColumn": {"uri": "/jaxrs/appinfo","method": "POST"},
  "removeColumn": {"uri": "/jaxrs/appinfo/{id}","method": "DELETE"},
  "updateColumn": {"uri": "/jaxrs/appinfo","method": "POST"},
  "getColumnIcon":{"uri":"/jaxrs/appinfo/{id}/icon"},
  "updataColumnIcon": {
    "uri": "/jaxrs/appinfo/{id}/icon/size/72",
    "method": "POST",
    "enctype": "formData"
  },

  "listAllCategory" : {"uri":"/jaxrs/categoryinfo/list/all"},
  "listCategory": {"uri": "/jaxrs/categoryinfo/list/view/app/{appId}/all" },
  "listCategoryByPublisher": {"uri": "/jaxrs/categoryinfo/list/publish/app/{appId}" },
  "addCategory" : {"uri":"/jaxrs/categoryinfo","method":"POST"},
  "getCategory" : {"uri":"/jaxrs/categoryinfo/{id}"},
  "updateCategory": {"uri": "/jaxrs/categoryinfo","method": "POST"},
  "removeCategory": {"uri": "/jaxrs/categoryinfo/{id}","method": "DELETE"},
  "saveCategoryExtContent" : {"uri": "/jaxrs/categoryinfo/extContent","method": "POST"},
  "saveCategoryImportView" : {"uri": "/jaxrs/categoryinfo/bind/{categoryId}/view","method": "PUT"},

  "listAllForm" : {"uri":"/jaxrs/form/list/all"},
  "listForm": {"uri": "/jaxrs/form/list/app/{appId}"},
  "getForm": {"uri": "/jaxrs/form/{id}"},
  "getFormWithColumn": {"uri": "/jaxrs/form/{formFlag}/appinfo/{appFlag}"},
  "addForm": {"uri": "/jaxrs/form","method": "POST"},
  "removeForm": {"uri": "/jaxrs/form/{id}","method": "DELETE"},
  "updataForm": {"uri": "/jaxrs/form/{id}","method": "PUT"},
  "getFormByAnonymous": {"uri": "/jaxrs/anonymous/form/{id}"},
  "listFormFieldWithForm" : {"uri": "/jaxrs/form/list/{id}/formfield"},
  "listFormFieldWithColumn" : {"uri": "/jaxrs/form/list/formfield/appInfo/{appId}"},


  "getFormV2": {"uri": "/jaxrs/form/v2/{id}?t={tag}"},
  "getFormMobileV2": {"uri": "/jaxrs/form/v2/{id}/mobile?t={tag}"},
  "getFormAnonymousV2": {"uri": "/jaxrs/anonymous/form/v2/{id}?t={tag}"},
  "getFormMobileAnonymousV2": {"uri": "/jaxrs/anonymous/form/v2/{id}/mobile?t={tag}"},

  "lookupFormWithDocV2":  {"uri": "/jaxrs/form/v2/lookup/document/{docId}"},
  "lookupFormWithDocMobileV2":  {"uri": "/jaxrs/form/v2/lookup/document/{docId}/mobile"},
  "lookupFormWithDocAnonymousV2":  {"uri": "/jaxrs/anonymous/form/v2/lookup/document/{docId}"},
  "lookupFormWithDocMobileAnonymousV2":  {"uri": "/jaxrs/anonymous/form/v2/lookup/document/{docId}/mobile"},


  "getFormTemplate": {"uri": "/jaxrs/templateform/{id}"},
  "deleteFormTemplate": {"uri": "/jaxrs/templateform/{id}","method": "DELETE"},
  "addFormTemplate": {"uri": "/jaxrs/templateform","method": "POST"},
  "listFormTemplate": {"uri": "/jaxrs/templateform/list"},
  "listFormTemplateCategory": {"uri": "/jaxrs/templateform/list/category"},
  "listFormTemplatByCategory": {"uri": "/jaxrs/templateform/list/category","method": "PUT"},

  "listDictionary": {"uri": "/jaxrs/design/appdict/list/appInfo/{appId}"},
  "getDictionary": {"uri": "/jaxrs/design/appdict/{appDictId}"},
  "addDictionary": {"uri": "/jaxrs/design/appdict", "method": "POST"},
  "removeDictionary": {"uri": "/jaxrs/design/appdict/{appDictId}","method": "DELETE"},
  "updataDictionary": {"uri": "/jaxrs/design/appdict/{appDictId}","method": "PUT"},

  "getDictRoot": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/data"},
  "getDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data"},
  "setDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "PUT"},
  "addDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "POST"},
  "deleteDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "DELETE"},

  "getDictRootAnonymous" : {"uri": "/jaxrs/anonymous/surface/appdict/{appDictId}/appInfo/{appId}/data"},
  "getDictDataAnonymous" : {"uri": "/jaxrs/anonymous/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data"},

  "listScript": {"uri": "/jaxrs/script/list/app/{appId}"},
  "getScript": {"uri": "/jaxrs/script/{id}"},
  "addScript": {"uri": "/jaxrs/script", "method": "POST"},
  "removeScript": {"uri": "/jaxrs/script/{id}", "method": "DELETE"},
  "deleteScript": {"uri": "/jaxrs/script/{id}", "method": "DELETE"},
  "updataScript": {"uri": "/jaxrs/script/{id}", "method": "PUT"},
  "getScriptByName": {"uri": "/jaxrs/script/list/app/{appId}/name/{name}"},
  "getScriptByNameV2": {"uri": "/jaxrs/script/{uniqueName}/app/{flag}/imported"},


"listAppByManager" : {"uri":"/jaxrs/appinfo/list/manage"},
  "isAppInfoManager" : {"uri":"/jaxrs/permission/appInfo/{id}/manageable"},

  "isCategoryInfoManagers" : {"uri":"/jaxrs/permission/categoryInfo/{id}/manageable"},

  "listAppInfoManagers" : {"uri":"/jaxrs/permission/appInfo/{id}/managers"},
  "listAppInfoPublishers" : {"uri":"/jaxrs/permission/appInfo/{id}/publishers"},
  "listAppInfoViewers" : {"uri":"/jaxrs/permission/appInfo/{id}/viewers"},

  "listCategoryInfoManagers" : {"uri":"/jaxrs/permission/category/{id}/managers"},
  "listCategoryInfoPublishers" : {"uri":"/jaxrs/permission/category/{id}/publishers"},
  "listCategoryInfoViewers" : {"uri":"/jaxrs/permission/category/{id}/viewers"},

  "saveAppInfoManager" : {"uri":"/jaxrs/permission/manager/appInfo/{id}", "method":"POST" },
  "saveAppInfoPublisher" : {"uri":"/jaxrs/permission/publisher/appInfo/{id}", "method":"POST" },
  "saveAppInfoViewer" : {"uri":"/jaxrs/permission/viewer/appInfo/{id}", "method":"POST" },

  "saveCategoryInfoManager" : {"uri":"/jaxrs/permission/manager/categoryInfo/{id}", "method":"POST" },
  "saveCategoryInfoPublisher" : {"uri":"/jaxrs/permission/publisher/categoryInfo/{id}", "method":"POST" },
  "saveCategoryInfoViewer" : {"uri":"/jaxrs/permission/viewer/categoryInfo/{id}", "method":"POST" },

  "listView": {"uri" : "/jaxrs/view/list/app/{appId}"},
  "listViewByCategory": {"uri" : "/jaxrs/view/list/category/{categoryId}"},
  "listViewByForm": {"uri" : "/jaxrs/view/list/form/{formId}"},
  "getView" : {"uri" : "/jaxrs/view/{id}"},
  "addView": {"uri" : "/jaxrs/view", "method": "POST"},
  "updateView" : {"uri":"/jaxrs/view/{id}","method":"PUT"},
  "deleteView" : {"uri":"/jaxrs/view/{id}","method": "DELETE"},

  "listViewColumn": {"uri" : "/jaxrs/viewfieldconfig/list/view/{viewId}"},
  "getViewColumn" : {"uri" : "/jaxrs/viewfieldconfig/{id}"},
  "addViewColumn" : {"uri":"/jaxrs/viewfieldconfig", "method": "POST"},
  "updateViewColumn" : {"uri":"/jaxrs/viewfieldconfig/{id}","method":"PUT"},
  "deleteViewColumn" : {"uri":"/jaxrs/viewfieldconfig/{id}","method": "DELETE"},

  "listCategoryViewByView": {"uri" : "/jaxrs/viewcategory/list/view/{viewId}"},
  "listCategoryViewByCategory": {"uri" : "/jaxrs/viewcategory/list/category/{categoryId}"},
  "getCategoryView" : {"uri" : "/jaxrs/viewfieldconfig/{id}"},
  "addCategoryView" : {"uri":"/jaxrs/viewcategory", "method": "POST"},
  "updateCategoryView" : {"uri":"/jaxrs/viewcategory/{id}","method":"PUT"},
  "deleteCategoryView" : {"uri":"/jaxrs/viewcategory/{id}","method": "DELETE"},

  "listQueryView": {"uri": "/jaxrs/queryview/design/list/application/{applicationId}"},
  "listQueryViewNextPage": {"uri": "/jaxrs/queryview/design/list/{id}/next/{count}"},
  "listQueryViewPrevPage": {"uri": "/jaxrs/queryview/design/list/{id}/prev/{count}"},
  "getQueryView": {"uri": "/jaxrs/queryview/design/{id}"},
  "addQueryView": {"uri": "/jaxrs/queryview/design", "method": "POST"},
  "updateQueryView": {"uri": "/jaxrs/queryview/design/{id}","method": "PUT"},
  "deleteQueryView": {"uri": "/jaxrs/queryview/design/{id}","method": "DELETE"},
  "getQueryViewContent": {"uri": "/jaxrs/queryview/design/flag/{flag}"}, //获取QueryView内容.
  "loadQueryView": {"uri": "/jaxrs/queryview/design/flag/{flag}/simulate","method": "PUT"},



  //---document--
  "getDocument" : {"uri" : "/jaxrs/document/{id}"},
  "getDocumentControl" : {"uri" : "/jaxrs/document/{id}/control"},
  "viewDocument" : {"uri" : "/jaxrs/document/{id}/view"},
  "getDocumentByAnonymous" : {"uri" : "/jaxrs/anonymous/document/{id}/view"},
  "addDocument": {"uri" : "/jaxrs/document", "method": "POST"},
  "updateDocument" : {"uri":"/jaxrs/document","method":"POST"},
  "removeDocument" : {"uri":"/jaxrs/document/{id}","method": "DELETE"},
  "publishDocument" : {"uri":"/jaxrs/document/publish/{id}","method":"PUT"},  //发布文档信息
  "cancelPublishDocument" : {"uri":"/jaxrs/document/publish/{id}/cancel","method":"PUT"}, //取消发布文档信息
  "archiveDocument" : {"uri":"/jaxrs/document/achive/{id}","method":"PUT"}, //归档文档信息
  "redraftDocument" : {"uri":"/jaxrs/document/draft/{id}","method":"PUT"}, //恢复为草稿
  "publishDocumentComplex" : {  "uri":"/jaxrs/document/publish/content","method":"PUT" },  //直接一次性发布
  "moveDocumentToCategory" : { "uri" : "/jaxrs/document/category/change" , "method":"PUT" },

  //"getCategory" : {"uri":"/jaxrs/categoryinfo/{id}"},

  //"getForm": {"uri": "/jaxrs/form/{id}"},

  "getData": {"uri": "/jaxrs/data/document/{id}"},
  "addData": {"uri": "/jaxrs/data/document/{id}", "method": "POST"},
  "updateData": {"uri": "/jaxrs/data/document/{id}", "method": "PUT"},

  //attachment为文档中的附件
  "listAttachment" : {"uri":"/jaxrs/fileinfo/list/document/{documentid}"},
  "listAttachmentByAnonymous" : {"uri":"/jaxrs/anonymous/fileinfo/list/document/{documentId}"},
  "getAttachment": {"uri": "/jaxrs/fileinfo/{id}/document/{documentid}"},
  "getAttachmentByAnonymous": {"uri": "/jaxrs/anonymous/fileinfo/{id}/document/{documentid}"},
  "deleteAttachment": {"uri": "/jaxrs/fileinfo/{id}", "method": "DELETE"},

  "configAttachment": {"uri": "/jaxrs/fileinfo/edit/{id}/doc/{docId}", "method": "PUT"},

  "uploadAttachment": {"uri": "/jaxrs/fileinfo/upload/document/{id}", "method": "POST", "enctype": "formData"},
  "replaceAttachment": {"uri": "/jaxrs/fileinfo/update/document/{documentid}/attachment/{id}", "method": "POST", "enctype": "formData"},
  //"getAttachmentData": {"uri": "/servlet/download/{id}/document/{documentid}", "method": "GET"},
  "getAttachmentData": {"uri": "/jaxrs/fileinfo/download/document/{id}", "method": "GET"}, //document/{documentid}/
  "getAttachmentDataByAnonymous": {"uri": "/jaxrs/anonymous/fileinfo/download/document/{id}", "method": "GET"}, //document/{documentid}/
  "getAttachmentStream": {"uri": "/jaxrs/fileinfo/download/document/{id}/stream", "method": "GET"},
  "getAttachmentStreamByAnonymous": {"uri": "/jaxrs/anonymous/fileinfo/download/document/{id}/stream", "method": "GET"},

  //获取互联网URL指向的图片的base64编码[Jaxrs],必填, 需要获取的图片URL地址
  //  url- 必填, 需要获取的图片URL地址
  //  size - 转换图片压缩, size为最大宽高限制, 如 size=64 为 64*64
  //  注意,如果附件本身不是图片格式,则系统不会进行编码,并且给出异常, 扩展名限制:BMP、JPG、JPEG、PNG、GIF、TIFF
  //  访问文件无权限限制
  "getInternetImageBaseBase64" : {"uri":"/jaxrs/image/encode/base64", "method": "POST"},
  //上传一个图片,直接转换为一个base64编码[Servlet]
  "convertLocalImageToBase64": {"uri": "/servlet/image/encode/base64/size/{size}", "method": "POST", "enctype": "formData"},
  //将用户已经上传的图片附件直接转换为一个base64编码[Jaxrs]
  "getSubjectAttachmentBase64" : {"uri":"/jaxrs/fileinfo/{id}/binary/base64/{size}"},

  //file为CMS平台的附件
  "listFile" : {"uri":"/jaxrs/file/list/appInfo/{applicationFlag}"},
  "getFile": {"uri": "/jaxrs/file/{id}"},
  "addFile": {"uri": "/jaxrs/file", "method": "POST"},
  "removeFile": {"uri": "/jaxrs/file/{id}", "method": "DELETE"},
  "deleteFile": {"uri": "/jaxrs/file/{id}", "method": "DELETE"},
  "updataFile": {"uri": "/jaxrs/file/{id}", "method": "PUT"},
  "getFileByName": {"uri": "/jaxrs/file/appInfo/{applicationId}/name/{name}"},
  "downloadFile": {"uri": "/jaxrs/file/{id}/download"},
  "downloadWithApp": {"uri": "/jaxrs/file/{flag}/appInfo/{applicationFlag}/download"},
  "readFileById": {"uri": "/jaxrs/file/{id}/content"},
  "uploadFile": {"uri": "/jaxrs/file/{id}/upload", "method": "POST", "enctype": "formData"},
  "copyFile": {"uri": "/jaxrs/file/{flag}/appInfo/{appInfoFlag}"},

  "readFile": {"uri": "/jaxrs/file/{flag}/appInfo/{applicationFlag}/content"},
//  "listFile": {"uri": "/jaxrs/file/list/application/{applicationFlag}"},


  //id                   -- ID
  //  documentId           --文档ID
  //  description          --信息说明（size:255）
  //  base64               --图片Base64编码后的文本（size:1MB）
  "saveImage" : {"uri" : "/jaxrs/document/pic", "method": "POST" },
  "getImage" : {"uri":"/jaxrs/document/pic/{id}"},
  "listImage" :{"uri":"/jaxrs/document/pic/doc/{documentid}"},
  "removeImage" : {"uri":"/jaxrs/document/pic/{id}","method": "DELETE"},
  //4、获取指定文档的ID获取文档大图的base64编码，如果有多个则获取第一个，如果没有则返回“0”
  "getImageByDocument" : {"uri":"/servlet/document/{documentid}/mainpic", "method": "PUT"},

  "getReadedCount" : {"uri":"/jaxrs/document/{id}/view/count"},
  "listReadedLog" : {"uri":"/jaxrs/viewrecord/document/{docId}/filter/list/{id}/next/{count}","method":"GET"},

  //--index----
  "listDocumentAll": {"uri" : "/jaxrs/document/list/category/{categoryId}"},
  "listDocumentFilterNext": {"uri" : "/jaxrs/document/filter/list/{id}/next/{count}", "method": "PUT"},
  "listDocumentFilterPrev": {"uri" : "/jaxrs/document/filter/list/{id}/prev/{count}", "method": "PUT"},

  "listViewDataNext": {"uri" : "/jaxrs/view/viewdata/list/{id}/next/{count}", "method": "POST"},

  "listDraftNext": {"uri" : "/jaxrs/document/draft/list/{id}/next/{count}", "method": "PUT"},
  "listDraftPrev": {"uri" : "/jaxrs/document/draft/list/{id}/prev/{count}", "method": "PUT"},

  "listDraftFilterAttribute": {"uri" : "/jaxrs/searchfilter/list/draft/filter"},
  "listPublishFilterAttribute": {"uri" : "/jaxrs/searchfilter/list/publish/filter"},
  "listArchiveFilterAttribute": {"uri" : "/jaxrs/searchfilter/list/archive/filter"},

  "listCategoryDraftFilterAttribute": {"uri" : "/jaxrs/searchfilter/list/draft/filter/category/{categoryId}"},
  "listCategoryPublishFilterAttribute": {"uri" : "/jaxrs/searchfilter/list/publish/filter/category/{categoryId}"},
  "listCategoryArchiveFilterAttribute": {"uri" : "/jaxrs/searchfilter/list/archive/filter/category/{categoryId}"},

  //---module---
  "achiveDocument" : {"uri":"/jaxrs/document/achive/{id}","method":"PUT"}, //归档文档信息

  "importDocumentFormExcel": {"uri" : "/jaxrs/document/import/category/{categoryId}", "method": "POST", "enctype": "formData"},
  "deleteDocumentWithBatchName" :  {"uri":"/jaxrs/document/batch/{batchId}","method":"DELETE"},
  "checkImportStatus" : {"uri" : "/jaxrs/document/batch/{batchName}/status"},
  "checkAllImportStatus" : {"uri" : "/jaxrs/document/batch/status"},

  "getColumnByAlias": {"uri": "/jaxrs/appinfo/alias/{alias}"},
  "getCategoryByAlias" : {"uri":"/jaxrs/categoryinfo/alias/{alias}"},

  //{  docIds : docIds, dataChanges : dataChanges }
  "batchModifyData" :  {"uri":"/jaxrs/document/batch/data/modify","method":"PUT"}, //批量修改数据

  //评论
  "listCommentNextWithFilter" : {"uri":"/jaxrs/comment/list/{id}/next/{count}","method":"PUT"},
  "listCommentPageWithFilter" : {"uri":"/jaxrs/comment/list/{page}/size/{size}","method":"PUT"},
  "listCommentPrevWithFilter" : {"uri":"/jaxrs/comment/list/{id}/prev/{count}","method":"PUT"},
  "deleteComment" :  {"uri":"/jaxrs/comment/{id}","method":"DELETE"},
  "saveComment" :  {"uri":"/jaxrs/comment","method":"POST"},

  "clazz": "x_cms_assemble_control"
}

if (!o2.xAction.RestActions.Action["x_cms_assemble_control"]) o2.xAction.RestActions.Action["x_cms_assemble_control"] = new Class({Extends: o2.xAction.RestActions.Action});
o2.Actions.actions["x_cms_assemble_control"] = new o2.xAction.RestActions.Action["x_cms_assemble_control"]("x_cms_assemble_control", actionJson);
var actionJson = {
  "collectConnected": {"uri": "/jaxrs/collect/connect"},
  "getCollectConfig": {"uri": "/jaxrs/collect"},
  "disconnect": {"uri": "/jaxrs/collect/disconnect"},
  "collectValidate": {"uri": "/jaxrs/collect/validate"},

  "updateCollect": {"uri": "/jaxrs/collect", "method": "PUT"},
  "updateUnitCollect": {"uri": "/jaxrs/collect/updateUnit", "method": "PUT"},

  "deleteCollect": {"uri": "/jaxrs/collect/name/{name}/mobile/{mobile}/code/{code}", "method": "DELETE"},
  "createCollect": {"uri": "/jaxrs/collect", "method": "POST"},
  "collectValidateInput": {"uri": "/jaxrs/collect/validate/direct", "method": "PUT"},

  "getCode": {"uri": "/jaxrs/collect/code/mobile/{mobile}"},
  "codeValidate": {"uri": "/jaxrs/collect/validate/codeanswer", "method": "PUT"},
  "resetPassword": {"uri": "/jaxrs/collect/resetpassword", "method": "PUT"},

  "nameExist": {"uri": "/jaxrs/collect/name/{name}/exist"},
  "passwordValidate": {"uri": "/jaxrs/collect/validate/password", "method": "PUT"},

  "listModuleCategory": {"uri": "/jaxrs/module/list/category"},
  "listModule": {"uri": "/jaxrs/module/list", "method": "PUT"},
  "compareModule": {"uri": "/jaxrs/module/{id}/compare"},
  "importModule": {"uri": "/jaxrs/module/write/{flag}", "method": "PUT"},
  "compareUpload": {"uri": "/jaxrs/module/compare/upload", "method": "PUT", "enctype": "formData"},

  "outputStructure": {"uri": "/jaxrs/module/output/structure"},
  "removeStructure": {"uri": "/jaxrs/module/remove/structure/{id}", "method": "DELETE"},
  "outputCreate": {"uri": "/jaxrs/module/output", "method": "PUT"},
  "output": {"uri": "/jaxrs/module/output", "method": "PUT"},
  "download": {"uri": "/jaxrs/module/output/{flag}/file"},
  "listStructure": {"uri": "/jaxrs/module/output/list/structure"},

  "getCollect": {"uri": "/jaxrs/config/collect"},
  "getPerson": {"uri": "/jaxrs/config/person"},
  "getToken": {"uri": "/jaxrs/config/token"},
  "getPortal": {"uri": "/jaxrs/config/portal"},
  "setCollect": {"uri": "/jaxrs/config/collect", "method": "PUT"},
  "setPerson": {"uri": "/jaxrs/config/person", "method": "PUT"},
  "setToken": {"uri": "/jaxrs/config/token", "method": "PUT"},
  "setPortal": {"uri": "/jaxrs/config/portal", "method": "PUT"},

  "getCenterServer": {"uri": "/jaxrs/config/centerserver"},
  "setCenterServer": {"uri": "/jaxrs/config/centerserver", "method": "PUT"},
  "getProxy": {"uri": "/jaxrs/config/proxy"},
  "setProxy": {"uri": "/jaxrs/config/proxy", "method": "PUT"},

  "mobile_defaultStyle": {"uri": "/jaxrs/appstyle/default/style"},
  "mobile_currentStyle": {"uri": "/jaxrs/appstyle/current/style"},


  "imageLaunchLogo": {"uri": "/jaxrs/appstyle/image/launch/logo", "method": "PUT", "enctype": "formData"},
  "imageLaunchLogoErase": {"uri": "/jaxrs/appstyle/image/launch/logo/erase"},

  "imageLoginAvatar": {"uri": "/jaxrs/appstyle/image/login/avatar", "method": "PUT", "enctype": "formData"},
  "imageLoginAvatarErase": {"uri": "/jaxrs/appstyle/image/login/avatar/erase"},

  "imageMenuLogoBlur": {"uri": "/jaxrs/appstyle/image/menu/logo/blur", "method": "PUT", "enctype": "formData"},
  "imageMenuLogoBlurErase": {"uri": "/jaxrs/appstyle/image/menu/logo/blur/erase"},

  "imageMenuLogoFocus": {"uri": "/jaxrs/appstyle/image/menu/logo/focus", "method": "PUT", "enctype": "formData"},
  "imageMenuLogoFocusErase": {"uri": "/jaxrs/appstyle/image/menu/logo/focus/erase"},

  "imagePeopleAvatarDefault": {"uri": "/jaxrs/appstyle/image/people/avatar/default", "method": "PUT", "enctype": "formData"},
  "imagePeopleAvatarDefaultErase": {"uri": "/jaxrs/appstyle/image/people/avatar/default/erase"},

  "imageProcessDefault": {"uri": "/jaxrs/appstyle/image/process/default", "method": "PUT", "enctype": "formData"},
  "imageProcessDefaultErase": {"uri": "/jaxrs/appstyle/image/process/default/erase"},

  "imageSetupAboutLogo": {"uri": "/jaxrs/appstyle/image/setup/about/logo", "method": "PUT", "enctype": "formData"},
  "imageSetupAboutLogoErase": {"uri": "/jaxrs/appstyle/image/setup/about/logo/erase"},

  "imageApplicationTop": {"uri": "/jaxrs/appstyle/image/application/top", "method": "PUT", "enctype": "formData"},
  "imageApplicationTopErase": {"uri": "/jaxrs/appstyle/image/application/top/erase"},

  "setAppStyle": {"uri": "/jaxrs/appstyle", "method": "PUT"},

  "listAgent": {"uri": "/jaxrs/agent"},
  "createAgent" : {"uri": "/jaxrs/agent", "method": "POST"},
  "updateAgent" : {"uri": "/jaxrs/agent/{flag}", "method": "PUT"},
  "updateAgentByUploadFile" : {"uri": "/jaxrs/agent/{flag}/file", "method": "PUT", "enctype": "formData"},
  "deleteAgent" : {"uri": "/jaxrs/agent/{flag}", "method": "DELETE"},
  "getAgent" : {"uri": "/jaxrs/agent/{flag}"},
  "disableAgent" : {"uri": "/jaxrs/agent/{flag}/disable"},
  "enableAgent" : {"uri": "/jaxrs/agent/{flag}/enable"},
  "executeAgent" : {"uri": "/jaxrs/agent/{flag}/execute"},

  "listInvoke": {"uri": "/jaxrs/invoke"},
  "createInvoke" : {"uri": "/jaxrs/invoke", "method": "POST"},
  "updateInvoke" : {"uri": "/jaxrs/invoke/{flag}", "method": "PUT"},
  "updateInvokeByUploadFile" : {"uri": "/jaxrs/invoke/{flag}/file", "method": "PUT", "enctype": "formData"},
  "deleteInvoke" : {"uri": "/jaxrs/invoke/{flag}", "method": "DELETE"},
  "getInvoke" : {"uri": "/jaxrs/invoke/{flag}"},
  "executeInvoke" : {"uri": "/jaxrs/invoke/{flag}/execute", "method": "POST"},
  "executeToken" : {"uri": "/jaxrs/invoke/{flag}/client/{client}/token/{token}/execute", "method": "POST"},

  "listPromptErrorLog" : {"uri": "/jaxrs/prompterrorlog/list/{id}/next/{count}"},
  "listPromptErrorLogWithDate" : {"uri": "/jaxrs/prompterrorlog/list/{id}/next/{count}/date/{date}"},
  "listUnexpectedErrorLog" : {"uri": "/jaxrs/unexpectederrorlog/list/{id}/next/{count}"},
  "listUnexpectedErrorLogWithDate" : {"uri": "/jaxrs/unexpectederrorlog/list/{id}/next/{count}/date/{date}"},
  "listWarnLog" : {"uri": "/jaxrs/warnlog/list/{id}/next/{count}"},
  "listWarnLogWithDate" : {"uri": "/jaxrs/warnlog/list/{id}/next/{count}/date/{date}"},
  "listSystemLog" : {"uri": "/jaxrs/warnlog/view/system/log/tag/{tag}"},
  "echo" : {"uri": "/jaxrs/echo"},


  "listDictionaryPaging": {"uri": "/jaxrs/dict/list/paging/{page}/size/{size}"},
  "listDictionary": {"uri": "/jaxrs/dict/list"},
  "getDictionary": {"uri": "/jaxrs/dict/{id}"},
  "addDictionary": {"uri": "/jaxrs/dict", "method": "POST"},
  "updateDictionary": {"uri": "/jaxrs/dict/{id}","method": "PUT"},
  "removeDictionary": {"uri": "/jaxrs/dict/{id}","method": "DELETE"},

  "getDictRoot": {"uri": "/jaxrs/dict/{dictFlag}/data"},
  "getDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data"}, //path:路径(多层路径以.分割，如person.name)
  "setDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "PUT"},
  "addDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "POST"},
  "deleteDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "DELETE"},

  "listScriptPaging": {"uri": "/jaxrs/script/list/paging/{page}/size/{size}", "method": "POST"},
  "listScript": {"uri": "/jaxrs/script/list"},
  "getScript": {"uri": "/jaxrs/script/{id}"},
  "addScript": {"uri": "/jaxrs/script", "method": "POST"},
  "removeScript": {"uri": "/jaxrs/script/{id}", "method": "DELETE"},
  "updateScript": {"uri": "/jaxrs/script/{id}", "method": "PUT"},
  "getScriptByName": {"uri": "/jaxrs/script/name/{name}/imported"}
}

if (!o2.xAction.RestActions.Action["x_program_center"]) o2.xAction.RestActions.Action["x_program_center"] = new Class({Extends: o2.xAction.RestActions.Action});
o2.Actions.actions["x_program_center"] = new o2.xAction.RestActions.Action["x_program_center"]("x_program_center", actionJson);
var actionJson = {
  "changePassword": {"uri": "/jaxrs/person/password", "method": "PUT"},
  "getPerson": {"uri": "/jaxrs/person"},
  "updatePerson": {"uri": "/jaxrs/person", "method": "PUT"},
  "changeIcon": {"uri": "/jaxrs/person/icon", "method": "PUT", "enctype": "formData"},
  "checkPassword" : {"uri":"/jaxrs/reset/check/password/{password}"},
  "getPersonIcon": {"uri": "/jaxrs/person/icon"},

  "getIcon": {"uri": "/jaxrs/icon/{person}"},

  //disable 不允许注册，captcha 图片验证码, code 手机验证码 x_organization_assemble_personal
  "getRegisterMode" : { "uri" : "/jaxrs/regist/mode" },
  "getRegisterCaptcha" : {"uri":"/jaxrs/regist/captcha/width/{width}/height/{height}"},
  "createRegisterCode" : {"uri":"/jaxrs/regist/code/mobile/{mobile}"},
  "checkRegisterName" : {"uri":"/jaxrs/regist/check/name/{name}"},
  "checkRegisterPassword" : {"uri":"/jaxrs/regist/check/password/{password}"},
  "checkRegisterMobile" : {"uri":"/jaxrs/regist/check/mobile/{mobile}"},
  "register" : {"uri":"/jaxrs/regist", "method": "POST"},

  "resetPassword" : {"uri":"/jaxrs/reset", "method": "PUT"},
  "checkCredentialOnResetPassword" : {"uri":"/jaxrs/reset/check/credential/{credential}"},
  "checkPasswordOnResetPassword" : {"uri":"/jaxrs/reset/check/password/{password}"},
  "createCodeOnResetPassword" :  {"uri":"/jaxrs/reset/code/credential/{credential}"},

  "setPasswordAnonymous" : {"uri":"/jaxrs/reset/password/anonymous", "method": "POST"},


  "getUserData": {"uri": "/jaxrs/custom/{name}"},
  "putUserData": {"uri": "/jaxrs/custom/{name}", "method": "PUT"},
  "deleteUserData": {"uri": "/jaxrs/custom/{name}", "method": "DELETE"},

  "getPublicUserData": {"uri": "/jaxrs/definition/{name}"},
  "putPublicUserData": {"uri": "/jaxrs/definition/{name}", "method": "PUT"},
  "deletePublicUserData": {"uri": "/jaxrs/definition/{name}", "method": "DELETE"},
  //外出授权
  "getMyEmPower": {"uri": "/jaxrs/empower/list/currentperson", "method": "GET"},
  "getReceiveEmPower": {"uri": "/jaxrs/empower/list/to", "method": "GET"},
  "createEmPower": {"uri": "/jaxrs/empower", "method": "POST"},
  "editEmPower": {"uri": "/jaxrs/empower/{id}", "method": "PUT"},
  "deleteEmPower": {"uri": "/jaxrs/empower/{id}", "method": "DELETE"},

  //2020年1月8日新版无这2个方法
  "getMyEmPowerLog": {"uri": "/jaxrs/empowerlog/list/currentperson", "method": "GET"},
  "getReceiveEmPowerLog": {"uri": "/jaxrs/empowerlog/list/to", "method": "GET"},
  "listToCurrentPersonPaging": {"uri": "/jaxrs/empowerlog/list/to/currentperson/paging/{page}/size/{size}", "method": "POST"},
  "listWithCurrentPersonPaging": {"uri": "/jaxrs/empowerlog/list/currentperson/paging/{page}/size/{size}", "method": "POST"},

  "clazz": "x_organization_assemble_personal"
}
if (!o2.xAction.RestActions.Action["x_organization_assemble_personal"]) o2.xAction.RestActions.Action["x_organization_assemble_personal"] = new Class({Extends: o2.xAction.RestActions.Action});
o2.Actions.actions["x_organization_assemble_personal"] = new o2.xAction.RestActions.Action["x_organization_assemble_personal"]("x_organization_assemble_personal", actionJson);