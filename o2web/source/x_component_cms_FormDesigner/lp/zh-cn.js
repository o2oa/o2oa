MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.FormDesigner.LP = Object.merge( {}, MWF.xApplication.process.FormDesigner.LP, {
    "selectApplication" : "选择应用",
    "formType": {
        "empty": "空白表单",
        "publishEdit": "发布类编辑表单",
        "publishRead": "发布类阅读表单",
        "dataInput": "数据录入类表单"
    },
    "validation" : {
        "publish" : "发布时"
    },
    "modules": {
        "reader": "读者",
        "author": "作者",
        "log": "阅读记录",
        "comment": "评论"
    },
    "formStyle":{
        "noneStyle": "空样式",
        "defaultStyle": "传统样式",
        "redSimple": "红色简洁",
        "blueSimple": "蓝色简洁",
        "defaultMobileStyle": "手机样式",
        "banner": "横幅",
        "title": "标题",
        "sectionTitle": "区段标题",
        "section": "区段"
    },
    "propertyTemplate": {
        "setPopular": "设置热点操作",

        "commentPerPage": "每页评论数",
        "tiao": "条",
        "allowModifyComment": "发表后允许修改",
        "allowComment": "允许发表评论",
        "editor": "编辑器",
        "editorTitle": "CKEditor Config 脚本",
        "editorConfigNote": "返回CKEditor的Config对象，用于编辑器初始化",
        "editorConfigLinkNote": "更多属性帮助请查看",

        "table": "表格",
        "text": "文本",
        "format": "格式",

        "validationSave": "保存校验",
        "validationPublish": "发布校验"
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