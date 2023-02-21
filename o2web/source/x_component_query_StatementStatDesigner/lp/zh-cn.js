MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementStatDesigner = MWF.xApplication.query.StatementStatDesigner || {};
if(!MWF.APPDSMSD)MWF.APPDSMSD = MWF.xApplication.query.StatementStatDesigner;
MWF.xApplication.query.ViewDesigner = MWF.xApplication.query.ViewDesigner || {};
MWF.xDesktop.requireApp("query.StatementDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.query.StatementStatDesigner.LP = Object.merge( MWF.xApplication.query.StatementDesigner.LP, {
    "title": "查询统计",
    "newStatement": "新建查询统计",
    "unCategory": "未分类",
    "statement": "查询统计",
    "noStatNotice": "未创建统计，请先创建统计！",
    "stat": "统计",
    "propertyTemplate": {
        "chart":"图表",
        "bar": "柱状图",
        "pie":"饼状图",
        "line":"折线图",

        "category":"分类",
        "isGroup":"分类统计",
        "yes":"是",
        "no":"否",
        "categoryTitle":"分类标题",
        "isAmount":"合计",

        "categoryValue":"分类值",
        "categoryDisplay":"分类显示",
        "item":"根据列视图计算",
        "specified":"根据指定的分类值",
        "intersection":"列视图计算与指定分类值的交集",
        "sum1":"列视图计算与指定分类值的合集",
        "groupSpecifiedList":"指定分类值"
    }
});