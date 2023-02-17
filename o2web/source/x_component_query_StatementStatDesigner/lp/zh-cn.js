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

    "propertyTemplate": {

    }
});