MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.FormDesigner.LP = Object.merge( MWF.xApplication.process.FormDesigner.LP, {
    "validation" : {
        "publish" : "发布时"
    },
    "propertyTemplate": {

    },
    "actionBar": {
        "close":"关闭",
        "closeTitle": "关闭文档",
        "edit": "编辑",
        "editTitle": "编辑文档",
        "save": "保存",
        "saveTitle": "保存文档",
        "publish": "发布",
        "publishTitle": "发布文档",
        "saveDraft": "保存草稿",
        "saveDraftTitle": "保存草稿",
        "popular": "设置热点",
        "popularTitle": "设置热点",
        "delete": "删除",
        "deleteTitle": "删除文档",
        "print": "打印",
        "printTitle": "打印文档"
    }
});