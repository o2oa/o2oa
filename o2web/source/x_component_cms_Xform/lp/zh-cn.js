MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Xform = MWF.xApplication.process.Xform || {};
MWF.xDesktop.requireApp("process.Xform", "lp."+MWF.language, null, false);
MWF.xApplication.cms.Xform.LP = Object.merge( MWF.xApplication.process.Xform.LP, {
    "dataSaved": "数据保存成功",
    "documentPublished" : "发布成功" ,


    "noSelectRange": "无法确定选择范围",

    "begin": "起",
    "end": "止",
    "none": "无",

    "person": "人员名称",
    "department": "单位",
    "firstDate": "首次阅读时间",
    "readDate": "最近阅读时间",
    "readCount" : "阅读次数",
    "startTime": "收到时间",
    "completedTime": "处理时间",
    "opinion": "意见",

    "systemProcess": "系统处理",

    "deleteAttachmentTitle":"删除附件确认",
    "deleteAttachment": "是否确定要删除您选中的附件？",

    "replaceAttachmentTitle":"替换附件确认",
    "replaceAttachment": "是否确定要替换您选中的附件？",
    "uploadMore": "您最多只允许上传 {n} 个附件",
    "notValidation": "数据校验未通过",

    "deleteDocumentTitle": "删除文件确认",
    "deleteDocumentText": {"html": "<div style='color: red;'>注意：您正在删除此文档，删除后文档无法找回，您确认要删除此文件？</div>"},
    "documentDelete": "已经删除文件",

    "readerFieldNotice" : "不选则全员可见",

    "readedLogTitle" : "阅读记录",
    "readedCountText" : "共{person}人、{count}次阅读",
    "defaultReadedLogText" : "<font style='color:#00F;'>{person}</font>（{department}） 阅于<font style='color:#00F'>{date}</font>，共<font style='color:#00F'>{count}</font>次",

    "reply" : "评论",
    "commentTitle" : "评论区域",
    "commentCountText" : "共{count}次评论",

    "saveComment" : "发表评论",
    "saveCommentSuccess" : "发布评论成功",
    "deleteCommentTitle" : "删除评论确认",
    "deleteCommentText" : "删除评论后不能恢复，您确定要删除此评论？",
    "deleteCommentSuccess" : "删除评论成功",
    "commentFormTitle" : "编辑评论",
    "createCommentSuccess" : "创建评论成功",
    "updateSuccess" : "更新成功",
    "save" : "保存"

    //"at" : "阅于",
    //"readdDocument" : "，",
    //"historyRead" : "共",
    //"times" : "次"
});