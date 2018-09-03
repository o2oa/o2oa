MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Office = MWF.APPOffice =  new Class({
	Extends: MWF.APP$Module,
	isActive: false,
    options:{
        "version": "5,0,3,1",
        "MakerCaption": "浙江兰德纵横网络技术股份有限公司",
        "MakerKey": "E138DABB4AC26C2D8E09FAE59AB3BDE87AFB9D7B",
        "ProductCaption": "O2",
        "ProductKey": "EDCC626CB85C9A1D3E0D7BDDDC2637753C596725",
        "clsid": "A64E3073-2016-4baf-A89D-FFE1FAA10EC0"
    },

	_loadUserInterface: function(){
		this.node.empty();
		this.node.setStyles({
			"min-height": "100px"
		});
        // this.isActive = true;
		if (Browser.name==="ie"){
            this.isActive = true;
            this.file = null;
            if (!this.form.officeList) this.form.officeList=[];
            this.form.officeList.push(this);
        }
	},
	
	_afterLoaded: function(){
		if (!this.isActive){
			this.loadOfficeNotActive();
		}else{
			this.loadOffice();
		}
	},
    getProgID: function(){
        switch (this.json.officeType){
            case "word":
                return "Word.Document";
            case "excel":
                return "Excel.Sheet";
            case "ppt":
                return "PowerPoint.Show";
        }
        return "Word.Document"
    },
	defaultParam: function(readonly){
		return {
			"ProductCaption": this.json.productCaption || this.options.ProductCaption,
            "ProductKey": this.json.productKey || this.options.ProductKey,
            "MakerCaption": this.options.MakerCaption,
            "MakerKey": this.options.MakerKey,
			"Titlebar": "0",
			"Menubar": "0",
			"ToolBars": (readonly) ? "0" : "1",
			"Statusbar": "0",
			"BorderStyle": (readonly) ? "0" : "0",
			"IsNoCopy": "0",
            "IsResetToolbarsOnOpen": "1",
            "FileNew": "0",
            "FileOpen": "0",
            "FileClose": "0",
            "FileSave": "0",
            "FileProperties": "0"
		}
	},
	loadOffice: function(){
	    if (this.node.getSize().y<800) this.node.setStyle("height", "800px");

        if (!layout.desktop.offices) layout.desktop.offices = {};
        layout.desktop.offices[this.getOfficeObjectId()] = this;

        if (this.readonly){
            this.loadOfficeRead();
        }else if (this.json.isReadonly){
            this.readonly  = true;
            this.loadOfficeRead();
        }else{
            if (this.json.readScript && this.json.readScript.code){
                var flag = this.form.Macro.exec(this.json.readScript.code, this);
                if (flag){
                    this.readonly = true;
   