MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.ViewDesigner = MWF.xApplication.process.ViewDesigner || {};
MWF.xDesktop.requireApp("process.ViewDesigner", "lp."+MWF.language, null, false);
MWF.CMSQVD.LP = MWF.xApplication.cms.QueryViewDesigner.LP = Object.merge( MWF.xApplication.process.ViewDesigner.LP, {
    "ok": "确定",
    "cancel": "取消",
    "notice" : {
        "saveAs_success" : "数据视图另存成功，您可以在栏目配置中打开新视图！"
    }
});