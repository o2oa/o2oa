MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.widget.Panel", null, false);
MWF.xApplication.process.FormDesigner.Preview = MWF.FCPreview = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
        "previewPath": "/x_desktop/preview.html",
        "size": null,
        "mode": "pc"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
        var href = window.location.href;
        if (href.indexOf("debugger")!=-1) this.options.previewPath = "/x_desktop/preview.html?debugger";

        this.path = "/x_component_process_FormDesigner/$Preview/";
        this.cssPath = "/x_component_process_FormDesigner/$Preview/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.form = form;
		this.data = Object.clone((form._getFormData) ? form._getFormData() : form._getPageData());

        //this.data = Object.clone(data);
	},

	load: function(){
        var p = this.getPanelPostion();
        this.createPreviewNodes();
        this.createPanel(p);
        this.setEvent();
        this.loadForm();
        this.addActions();
	},
    getPanelPostion: function(){
        var size = this.form.designer.node.getSize();
        var x = size.x*0.7;
        var y = size.y*0.8;
        if (this.options.size){
            x = this.options.size.x;
            y = this.options.size.y;
        }

        var top = ((size.y-y)/2)*0.8;
        var left = (size.x-x)/2;
        return {"x": x, "y": y, "top": top, "left": left};
    },
    createPreviewNodes: function(){
        this.node = new Element("div", {"styles": this.css.contentNode});
        this.topActionAreaNode = new Element("div", {"styles": this.css.topActionAreaNode}).inject(this.node);
        this.formFrameNode = new Element("iframe", {"styles": this.css.formFrameNode}).inject(this.node);
        this.formJsonNode = new Element("div", {"styles": this.css.formJsonNode}).inject(this.node);
    },
    addActions: function(){
        this.showJsonAction = new Element("div", {
            "styles": this.css.actionButton,
            "text": "show json"
        }).inject(this.topActionAreaNode);

        this.showJsonAction.addEvent("click", function(){
            this.showJson();
        }.bind(this));

        this.showFormAction = new Element("div", {
            "styles": this.css.actionButton,
            "text": "show form"
        }).inject(this.topActionAreaNode);
        this.showFormAction.setStyle("display", "none");
        this.showFormAction.addEvent("click", function(){
            this.showForm();
        }.bind(this));

    },
    showForm: function(){
        this.formJsonNode.empty();
        this.formFrameNode.setStyle("display", "block");
        this.formJsonNode.setStyle("display", "none");
        this.showJsonAction.setStyle("display", "block");
        this.showFormAction.setStyle("display", "none");
    },
    showJson: function(){
        this.showJsonAction.setStyle("display", "none");
        this.showFormAction.setStyle("display", "block");
        this.formFrameNode.setStyle("display", "none");
        this.formJsonNode.setStyle("display", "block");

        var layout = this.formFrameNode.contentWindow.layout;

        MWF.require("MWF.widget.JsonParse", function(){

            this.json = new MWF.widget.JsonParse(layout.appForm.getData(), this.formJsonNode, null);
            this.json.load();
        }.bind(this));
    },
    createPanel: function(p){
        //alert(p.x);
        //alert(p.y);
        this.panel = new MWF.widget.Panel(this.node, {
            "style": "form",
            "title": this.data.json.name,
            "width": p.x,
            "height": p.y,
            "top": p.top,
            "left": p.left,
            "isExpand": false,
            "target": this.form.designer.node,
            "onQueryClose": function(){
                this.destroy();
            }.bind(this)
        });
        this.panel.load();
    },
    destroy: function(){
        this.node.empty();
        o2.release(this);
    },
    setEvent: function(){
        this.setFormFrameSize();
        this.panel.addEvent("resize", this.setFormFrameSize.bind(this));
    },

    setFormFrameSize: function(){
        var size = this.panel.content.getSize();
        var topSize = this.topActionAreaNode.getSize();
        var y = size.y-topSize.y-8;
        var x = size.x-8;
        this.formFrameNode.setStyle("height", ""+y+"px");
        this.formFrameNode.setStyle("width", ""+x+"px");
        this.formJsonNode.setStyle("height", ""+y+"px");
        this.formJsonNode.setStyle("width", ""+x+"px");
    },

    loadForm: function(){
        this.formFrameNode.store("preview",this);

        this.formFrameNode.set("src", this.options.previewPath+"?mode="+this.options.mode);
        //window.open(this.options.previewPath);
        //this.formDocument = this.formFrameNode.contentDocument;
        //this.formWindow = this.formFrameNode.contentWindow;

        //this.formFrameNode.preview = this;


    },
    loadFormData: function(node){
        MWF.getJSON("/x_desktop/res/preview/work.json", function(json){
            MWF.xDesktop.requireApp("process.Xform", "Form", function(){
                this.appForm = new MWF.APPForm(node, this.data);

                // this.appForm.businessData = {
                //     "data": json.data,
                //     "taskList": json.taskList,
                //     "work": json.work,
                //     "workCompleted": json.workCompleted,
                //     "control": json.control,
                //     "activity": json.activity,
                //     "task": json.currentTask,
                //     "workLogList": json.workLogList,
                //     "attachmentList": json.attachmentList,
                //     "status": {
                //         //"readonly": (this.options.readonly) ? true : false
                //         "readonly": json.readonly
                //     }
                // };

                this.appForm.app = this.form.designer;
                this.appForm.load();
            }.bind(this));
        }.bind(this));
    }
});