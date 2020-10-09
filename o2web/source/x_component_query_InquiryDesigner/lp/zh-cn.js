MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.InquiryDesigner = MWF.xApplication.query.InquiryDesigner || {};
MWF.xApplication.query.ViewDesigner = MWF.xApplication.query.ViewDesigner || {};
MWF.xDesktop.requireApp("process.ViewDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.query.InquiryDesigner.LP = Object.merge( MWF.xApplication.query.ViewDesigner.LP, {
    "title" : "查询设计"
});