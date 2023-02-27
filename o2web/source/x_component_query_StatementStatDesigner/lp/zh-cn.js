MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementStatDesigner = MWF.xApplication.query.StatementStatDesigner || {};
if(!MWF.APPDSMSD)MWF.APPDSMSD = MWF.xApplication.query.StatementStatDesigner;
MWF.xApplication.query.ViewDesigner = MWF.xApplication.query.ViewDesigner || {};
MWF.xDesktop.requireApp("query.StatementDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.query.StatementStatDesigner.LP = Object.merge( MWF.xApplication.query.StatementDesigner.LP, {
    "title": "查询统计",
    "newStatementStat": "新建查询统计",
    "unCategory": "未分类",
    "statement": "查询配置",
    "statementDetail": "语句详情",
    "statementStat": "查询统计",
    "addStatement": "创建查询配置",
    "reload": "刷新",
    "selectStatement": "选择查询配置",
    "add": "创建",
    "select": "选择",
    "or": "或者",
    "please": "请",
    "testStatement": "测试语句",
    "noStatementNote": "查询统计基于查询配置",
    "noStatNotice": "未创建统计，请先创建统计！",
    "noStatementDetailNote": "请先创建或选择查询配置",
    "deleteStatmentTitle": "移除查询配置确认",
    "deleteStatment": "确定将查询配置从该统计移除？",
    "editStatement": "编辑查询语句",
    "updateSuccess": "保存成功",
    "createSuccess": "创建成功",
    "stat": "统计",
    "addCategory": "添加分类",
    "addColumn": "添加列",
    "unnamedCategory": "无标题分类",
    "deleteCategoryTitle": "删除分类确认",
    "deleteCategory": "是否确定删除当前分类？",
    "propertyTemplate": {
        "chart":"图表",
        "bar": "柱状图",
        "pie":"饼状图",
        "line":"折线图",

        "display": "展现",
        "category":"分类",
        "isGroup":"分类统计",
        "yes":"是",
        "no":"否",
        "categoryTitle":"分类标题",
        "isAmount":"合计",

        "categoryValue":"分类值",
        "categoryDisplay":"分类显示",
        "item":"根据查询语句计算结果",
        "specified":"根据指定的分类值",
        "intersection":"查询语句计算结果与指定分类值的交集",
        "sum1":"查询语句计算结果与指定分类值的合集",
        "groupSpecifiedList":"指定分类值"
    }
});