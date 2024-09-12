MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementDesigner = MWF.xApplication.query.StatementDesigner || {};
if(!MWF.APPDSMD)MWF.APPDSMD = MWF.xApplication.query.StatementDesigner;
MWF.xApplication.query.ViewDesigner = MWF.xApplication.query.ViewDesigner || {};
MWF.xDesktop.requireApp("query.ViewDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.query.StatementDesigner.LP = Object.merge( MWF.xApplication.query.ViewDesigner.LP, {
    "title": "查询设计",
    "newStatement": "新建查询配置",
    "unCategory": "未分类",
    "statement": "查询配置",
    "property": "属性",
    "run": "运行",
    "runTest": "测试语句",
    "statementType": "语句类型",
    "statementTable": "数据表",
    "selectTable": "选择数据表",
    "save_success": "查询配置保存成功！",
    "inputStatementName": "请输入查询配置名称",
    "saveStatementNotice" : "请先保存！",
    "cannotDisabledViewNotice": "视图未启用",
    "noViewNotice" : "未创建视图，请先创建视图！",
    "previewNotSelectStatementNotice" : "只有语句类型为‘Select’才可以预览",
    "field" : "字段",
    "fileldSelectNote" : "-选择后在语句中插入字段-",

    "statementFormat": "语句格式：",
    "statementJpql": "JPQL",
    "statementScript": "JPQL脚本",
    "nativeSql": "原生SQL",
    "nativeSqlScript": "原生SQL脚本",

    "queryParameter": "查询参数",
    "filterList": "过滤条件",
    "pageNo": "页码",
    "perPage": "每页",
    "size": "条",

    "statementCategory": "访问对象类型",
    "scriptTitle": "通过脚本创建JPQL",
    "sqlScriptTitle": "通过脚本创建SQL",
    "countMethod": "总数语句",

    "jpqlType": "JPQL类型",
    "jpqlFromResult": "查询开始条目",
    "jpqlMaxResult": "最大返回结果",
    "jpqlSelectTitle": "JPQL语句",
    "inputWhere": "您可以在下面的编辑框输入Where子句",
    "jpqlRunSuccess": "JPQL执行成功",
    "newLineSuccess": "插入数据成功",
    "newLineJsonError": "插入数据错误，数据格式有误",
    "queryStatement": "查询语句",
    "countStatement": "总数语句",

    "currentPerson":"当前人",
    "currentIdentity":"当前身份",
    "currentPersonDirectUnit":"当前人所在直接组织",
    "currentPersonAllUnit":"当前人所在所有组织",
    "currentPersonGroupList": "当前人所在群组",
    "currentPersonRoleList": "当前人所拥有角色",
    "defaultCondition": "自动赋值条件：",

    "ignore": "忽略",
    "auto": "自动",
    "assign": "指定",

    "mastInputParameter" : "请输入参数",
    "pathExecption" : "路径的写法是\"表别名.字段名\",格式不正确",

    "modifyViewFilterNote": "语句格式已经改变，请修改视图的过滤条件",

    "systemTable":"系统表",
    "customTable":"自建数据表",

    "taskInstance": "待办(Task)",
    "taskCompletedInstance": "已办(TaskCompleted)",
    "readInstance": "待阅(Read)",
    "readedInstance": "已阅(ReadCompleted)",
    "workInstance": "流程实例(Work)",
    "workCompletedInstance": "已完成流程实例(WorkCompleted)",
    "reviewInstance": "可阅读(Review)",
    "documentInstance": "内容管理文档(Document)",
    "cmsReviewInstance": "内容管理可阅读(com.x.cms.core.entity.Review)",
    "documentViewRecord": "内容管理阅读记录(DocumentViewRecord)",
    "documentCommentInfo": "内容管理评论记录(DocumentCommentInfo)",

    "taskInstanceSql":"待办(PP_C_TASK)",
    "taskCompletedInstanceSql": "已办(PP_C_TASKCOMPLETED)",
    "readInstanceSql":"待阅(PP_C_READ)",
    "readedInstanceSql":"已阅(PP_C_READCOMPLETED)",
    "workInstanceSql":"流程实例(PP_C_WORK)",
    "workCompletedInstanceSql":"已完成流程实例(PP_C_WORKCOMPLETED)",
    "reviewInstanceSql":"可阅读(PP_C_REVIEW)",
    "documentInstanceSql":"内容管理文档(CMS_DOCUMENT)",
    "cmsReviewInstanceSql":"内容管理可阅读(CMS_REVIEW)",
    "documentViewRecordSql":"内容管理阅读记录(CMS_DOCUMENT_VIEWRECORD)",
    "documentCommentInfoSql":"内容管理评论记录(CMS_DOCUMENT_COMMENTINFO)",

    "autoAddColumns": "根据数据表生成列",

    "propertyTemplate": {
        // "statementFormat": "如何创建语句：",
        // "statementJpql": "直接编写JPQL创建语句",
        // "statementScript": "通过脚本创建语句",
        // "statementCategory": "访问对象类型",

        "idPath":  "id路径",
        "idPathNote": "注：指Id（cms文档id/流程work id）相对单条数据的路径，用于打开文档。",
        "selectPath": "选择路径",
        "selectPathNote": "注：正确填写了查询语句，再测试语句或刷新视图数据可显示(刷新)选择路径。",
        "dataPathNote": "注：指该列相对单条数据的路径。比如 0，title，或 0.title",
        "executionAuthority": "执行权限",
        "anonymousAccess": "匿名访问",
        "allowed": "允许",
        "disAllowed": "不允许",
        "executePerson": "执行人",
        "executeUnit": "执行组织",

        "hidden": "隐藏",
        "orderNumber":"排序号",

        // "systemTable":"系统表",
        // "customTable":"自建数据表",
        // "taskInstance":"待办",
        // "taskCompletedInstance": "已办",
        // "readInstance":"待阅",
        // "readedInstance":"已阅",
        // "workInstance":"流程实例",
        // "workCompletedInstance":"已完成流程实例",
        // "reviewInstance":"可阅读",
        // "documentInstance":"内容管理文档",

        "parameter":"参数",
        "parameterNote":"注：对应查询语句和总数语句中的参数；\n如\":field\"的where条件，填写\"field\"；\n如\"?1\"的where条件，填写\"?1\"。",
        "pathNote":"注：路径的写法是\"表别名.字段名\"，如：o.title",
        "userInput":"用户输入",

        "export": "导出",
        "exportWidth": "宽度",
        "exportEnable": "允许导出",
        "isTime": "时间类型",
        "isNumber": "数字类型",
        "viewEnable": "启用视图",
        "forceClearCustomViewStyle": "切换时清除自定义样式",

        "total": "合计",
        "notNeeded": "不需要",
        "totalValue": "数值",
        "totalCount": "数量",

        "headText": "表头文本",
        "headStyle": "表头样式",
        "columnTitleStyle": "列标题样式",
        "columnContentStyle": "列内容样式"
    },
    "formToolbar":{
        "save": "保存",
        "autoSave": "自动保存",
        "preview": "预览",
        "help": "帮助",
        "add": "新建",
        "gotoApp": "打开所在应用"
    },

    "sorkKeyNote": "-排序-",
    "createTime": "创建时间",
    "updateTime": "更新时间",
    "asc": "正序",
    "desc": "倒序",
    "searchPlacholder": "输入名称/别名/id搜索",
    "searchAndSort": "排序和搜索"

});