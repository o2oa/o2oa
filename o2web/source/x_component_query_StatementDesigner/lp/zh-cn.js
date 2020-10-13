MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementDesigner = MWF.xApplication.query.StatementDesigner || {};
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
    "inputStatementData": "请先编辑JPQL查询语句",

    "statementFormat": "如何创建语句：",
    "statementJpql": "直接编写JPQL创建语句",
    "statementScript": "通过脚本创建语句",

    "statementCategory": "访问对象类型",
    "scriptTitle": "通过脚本创建JPQL",

    "jpqlType": "JPQL类型",
    "jpqlFromResult": "查询开始条目",
    "jpqlMaxResult": "最大返回结果",
    "jpqlSelectTitle": "JPQL语句",
    "inputWhere": "您可以在下面的编辑框输入Where子句",
    "jpqlRunSuccess": "JPQL执行成功",
    "newLineSuccess": "插入数据成功",
    "newLineJsonError": "插入数据错误，数据格式有误",
    "queryStatement": "查询语句",
    "countStatement": "总数语句"
});