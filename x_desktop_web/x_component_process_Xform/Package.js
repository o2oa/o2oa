MWF.xApplication.process.Xform = MWF.xApplication.process.Xform || {};
MWF.require("MWF.xScript.Macro", null, false);
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "Label", null, false);
MWF.xDesktop.requireApp("process.Xform", "Textfield", null, false);
MWF.xDesktop.requireApp("process.Xform", "Number", null, false);
MWF.xDesktop.requireApp("process.Xform", "Personfield", null, false);
MWF.xDesktop.requireApp("process.Xform", "Calendar", null, false);
MWF.xDesktop.requireApp("process.Xform", "Textarea", null, false);
MWF.xDesktop.requireApp("process.Xform", "Select", null, false);
MWF.xDesktop.requireApp("process.Xform", "Radio", null, false);
MWF.xDesktop.requireApp("process.Xform", "Checkbox", null, false);
MWF.xDesktop.requireApp("process.Xform", "Button", null, false);

MWF.xApplication.process.Xform.Div = MWF.APPDiv =  new Class({
	Extends: MWF.APP$Module
});
MWF.xApplication.process.Xform.Image = MWF.APPImage =  new Class({
	Extends: MWF.APP$Module
});

MWF.xDesktop.requireApp("process.Xform", "Table", null, false);
MWF.xDesktop.requireApp("process.Xform", "Datagrid", null, false);

MWF.xApplication.process.Xform.Html = MWF.APPHtml =  new Class({
	Extends: MWF.APP$Module,
	load: function(){
		this.node.insertAdjacentHTML("beforebegin", this.json.text);
		this.node.destroy();
	}
});

MWF.xDesktop.requireApp("process.Xform", "Tab", null, false);

MWF.xApplication.process.Xform.tab$Page = MWF.APPTab$Page =  new Class({
	Extends: MWF.APP$Module
});
MWF.xApplication.process.Xform.tab$Content = MWF.APPTab$Content =  new Class({
	Extends: MWF.APP$Module
});

MWF.xDesktop.requireApp("process.Xform", "Tree", null, false);

MWF.xDesktop.requireApp("process.Xform", "Iframe", null, false);
MWF.xDesktop.requireApp("process.Xform", "Htmleditor", null, false);
MWF.xDesktop.requireApp("process.Xform", "Office", null, false);
MWF.xDesktop.requireApp("process.Xform", "Attachment", null, false);
MWF.xDesktop.requireApp("process.Xform", "Actionbar", null, false);
MWF.xDesktop.requireApp("process.Xform", "Log", null, false);
MWF.xDesktop.requireApp("process.Xform", "Monitor", null, false);
MWF.xDesktop.requireApp("process.Xform", "View", null, false);
MWF.xDesktop.requireApp("process.Xform", "ViewSelector", null, false);
MWF.xDesktop.requireApp("process.Xform", "ImageClipper", null, false);
MWF.xDesktop.requireApp("process.Xform", "View", null, false);
MWF.xDesktop.requireApp("process.Xform", "View", null, false);
MWF.xDesktop.requireApp("process.Xform", "ViewSelector", null, false);
MWF.xDesktop.requireApp("process.Xform", "ImageClipper", null, false);
