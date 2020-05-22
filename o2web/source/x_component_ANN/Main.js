o2.require("o2.widget.O2Identity", null, false);
o2.require("o2.widget.ace", null, false);
MWF.xApplication.ANN.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "ANN",
		"icon": "icon.png",
		"width": "1100",
		"height": "600",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.ANN.LP.title
	},
    initialize: function(desktop, options){
        this.setOptions(options);
        this.desktop = desktop;

        this.path = "../x_component_"+this.options.name.replace(/\./g, "_")+"/$Main/";
        this.options.icon = this.path+this.options.style+"/"+this.options.icon;

        //this.cssPath =this.path+this.options.style+"/css.wcss";
        this.stylePath = this.path+this.options.style+"/style.css";
        this.viewPath = this.path+this.options.style+"/view.html";
    },

	onQueryLoad: function(){
		this.lp = MWF.xApplication.ANN.LP;
		this.models = [];
	},
	loadApplication: function(callback){
        this.content.loadAll({css: this.stylePath, "html": this.viewPath}, {"bind": this}, function(){
        	this.node = this.content.getElement(".o2_ann_content");
            this.titleNode = this.content.getElement(".o2_ann_top");
            this.contentArea = this.content.getElement(".o2_ann_contentArea");
			this.listNode = this.content.getElement(".o2_ann_leftContent");
            this.contentNode = this.content.getElement(".o2_ann_rightContent");
            this.addAction = this.content.getElement(".o2_ann_topIcon");

            if (!o2.AC.isAdministrator()){
                this.addAction.destroy();
                this.addAction = null;
			}
			if (this.addAction) this.addAction.addEvent("click", this.createModel.bind(this));

            this.resizeFun = this.resize.bind(this);
            this.resizeFun();
            this.addEvent("resize", this.resizeFun);
			this.loadListModel();

            if (callback) callback();
		}.bind(this));
	},
	getDefaultData: function(){
		return {
            "neuralNetworkType": "mlp",
			"dataType": "processPlatform",
			"name": this.lp.unnamed,
			"description": "",
			"alias": "",
			"inValueScriptText": "",
			"outValueScriptText": "",
			"attachmentScriptText": "",
			"processList": [],
			"applicationList": [],
			"analyzeType": "default",
			"maxResult": 0,
			"propertyMap": {}
		}
	},
	checkSave: function(){
		if (this.currentModel && this.currentModel.context && (this.currentModel.context.status==="edit" || this.currentModel.context.status==="new")){
            this.notice(this.lp.checkSave, "error", this.contentNode, {"x": "left", "y": "top"});
            return false;
		}else{
			if (this.currentModel) this.currentModel.unselected();
            return true;
		}
	},

    createModel: function(){
        if (this.checkSave()) this.currentModel = new MWF.xApplication.ANN.Model(this.getDefaultData(), this);
	},
    resize: function(){
		var size = this.node.getSize();
        var titleSize = this.titleNode.getComputedSize();
        var h = size.y-titleSize.totalHeight;
        this.contentArea.setStyle("height", ""+h+"px");
	},
    loadListModel: function(){
		o2.Actions.get("x_query_assemble_designer").listModel(function(json){
			json.data.each(function(item){
                this.models.push(new MWF.xApplication.ANN.Model(item, this));
			}.bind(this));
		}.bind(this));
	}
});

MWF.xApplication.ANN.Model = new Class({
    initialize: function(data, app){
        this.data = data;
        this.app = app;
        this.lp = app.lp;
        this.listNode = this.app.listNode;
        this.load();
    },
    reload: function(){
        o2.Actions.get("x_query_assemble_designer").getModel(this.data.id, function(json){
            this.data = json.data;
            if (this.context) this.context.data = this.data;
            this.node.getElement(".o2_ann_model_nodeContentName").set("text", this.data.name);
            this.node.getElement(".o2_ann_model_nodeContentDate").set("text", this.data.updateTime);
            var rightIcon = this.node.getElement(".o2_ann_model_nodeRight");
            rightIcon.set("title", this.lp.status[this.data.status||'idle']);
            rightIcon.setStyle("background-image", "url(../x_component_ANN/$Main/default/icon/"+(this.data.status || 'idle')+".png")
        }.bind(this));
    },
	load: function(){
    	if (this.data.id){
    		this.loadList(function(){
                this.loadEvent();
			}.bind(this));
		}else{
    		this.loadCreate();
		}
	},
    loadList: function(callback){
        var viewPath = this.app.path+this.app.options.style+"/model.html";
        this.listNode.loadHtml(viewPath, {"bind": {"lp": this.lp, "data": this.data}}, function(){
			this.node = this.listNode.getLast();
            // var rightIcon = this.node.getElement(".o2_ann_model_nodeRight");
            // rightIcon.set("title", this.lp.status[this.data.status||'idle']);
            // rightIcon.setStyle("background-image", "url(../x_component_ANN/$Main/default/icon/"+(this.data.status || 'idle')+".png")
			if (callback) callback();
		}.bind(this));
	},
    loadCreate: function(){
		this.loadList(function(){
            this.loadEvent();
			this.selected();
		}.bind(this));
	},
    loadEvent: function(){
        this.node.addEvents({
			"mouseover": function(){if (!this.isSelected) this.node.addClass("o2_ann_model_node_over")}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.removeClass("o2_ann_model_node_over")}.bind(this),
            "click": function(){
                if (!this.isSelected){
                    this.selected();
                }else{
                    if (this.context) this.context.reload();
                }
            }.bind(this)
		});
	},
    getData: function(callback){
        o2.Actions.get("x_query_assemble_designer").getModel(this.data.id, function(json){
            this.data = json.data;
            if (callback) callback();
        }.bind(this));
    },
    selected: function(){
    	if (this.app.checkSave()){
            this.node.addClass("o2_ann_model_node_selected");
            this.isSelected = true;
            this.app.currentModel = this;
    		this.openModel();
		}
	},
    unselected: function(){
        this.isSelected = false;
        this.app.currentModel = null;
        this.node.removeClass("o2_ann_model_node_selected");
        this.node.removeClass("o2_ann_model_node_over");
        if (this.context) this.context.destroy();
	},
    openModel: function(){
		this.context = new MWF.xApplication.ANN.Model.Context(this);
	},
    destroy: function(){
        this.node.destroy();
        o2.release(this);
    }
});

MWF.xApplication.ANN.Model.Context = new Class({
    initialize: function(model){
        this.model = model;
        this.data = model.data;
        this.app = model.app;
        this.lp = model.lp;
        this.contentNode = this.app.contentNode;
        this.getData(this.load.bind(this));
    },
    getData: function(callback){
        if (this.data.id){
            o2.Actions.get("x_query_assemble_designer").getModel(this.data.id, function(json){
                this.data = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    reload: function(){
        this.contentNode.empty();
        this.getData(this.load.bind(this));
    },
	load: function(){
        this.status = "open";
        if (!this.data.id) this.status = "new";
        var viewPath = this.app.path+this.app.options.style+"/content.html";
        this.contentNode.loadHtml(viewPath, {"bind": {"lp": this.lp, "data": this.data}}, function(){
            this.node = this.contentNode.getFirst();
            this.toolbarNode = this.node.getFirst();
            this.content = this.node.getLast();
            this.lines = this.content.getElements(".o2_ann_modelContext_line");

            o2.widget.ace.load(function(){
                o2.load("../o2_lib/ace/src-min-noconflict/ext-static_highlight.js", function(){
                    this.loadContent();
                    this.loadToolbar();

                    this.resizeFun = this.resize.bind(this);
                    this.resizeFun();
                    this.app.addEvent("resize", this.resizeFun);

                    if (this.status==="new") this.edit();
                }.bind(this));
            }.bind(this));
        }.bind(this));
	},
    loadContent: function(){
        this.loadProcessList();
        this.loadApplicationList();
        this.loadCode(6, JSON.stringify(this.data.propertyMap,null,2));
        this.loadCode(7);
        this.loadCode(8);
        this.loadCode(9);

        this.loadStatus();
    },
    loadStatus: function(){
        this.statusNode = this.node.getElement(".o2_ann_modelContext_status");

    },
    loadProcessList: function(){
        if (this.data.processList){
            var node = this.lines[3].getLast().getFirst();
            this.data.processList.each(function(process){
                new o2.widget.O2Process({id: process}, node)
            }.bind(this));
        }
    },
    loadCode: function(i, text){
        var node = this.lines[i].getLast();
        if (text) node.set("text", text);
        var highlight = ace.require("ace/ext/static_highlight");
        highlight(node, {mode: "ace/mode/javascript", theme: "ace/theme/tomorrow", "fontSize": 16});
    },
    loadApplicationList: function(){
        if (this.data.applicationList){
            var node = this.lines[4].getLast();
            this.data.applicationList.each(function(application){
                new o2.widget.O2Application({id: application}, node)
            }.bind(this));
        }
    },
    destroy: function(){
        if (this.resizeFun) this.app.removeEvent("resize", this.resizeFun);
        this.contentNode.empty();
        o2.release(this);
    },
    "close": function(el,e){
        if (this.status!=="open"){
            var _self = this;
            this.app.confirm("infor", e, this.lp.closeTitle, this.lp.closeInfor, 380, 100, function(){
                if (_self.data.id){
                    _self.model.unselected();
                }else{
                    var model = _self.model;
                    _self.model.unselected();
                    model.destroy();
                }
                this.close();
            }, function(){this.close();});
        }else{
            this.model.unselected();
        }

	},
    loadToolbar: function(){
        MWF.require("MWF.widget.Toolbar", function() {
            this.toolbar = new MWF.widget.Toolbar(this.toolbarNode, {"style": "simple"}, this);
            this.toolbar.load();
            this.setToolbarDisabled();
        }.bind(this));
    },
    setToolbarDisabled: function(){
        if (this.toolbar){
            if (this.status==="edit"){
                this.toolbar.childrenButton[0].node.hide();
                this.toolbar.childrenButton[1].node.show();
                this.toolbar.childrenButton[6].node.show();

                if (this.data.status) {
                    this.toolbar.childrenButton[2].node.hide();
                    this.toolbar.childrenButton[3].node.hide();
                }else{
                    this.toolbar.childrenButton[2].node.show();
                    this.toolbar.childrenButton[3].node.show();
                    this.toolbar.childrenButton[8].node.show();
                }
                if (this.data.status && this.data.status==="generating"){
                    this.toolbar.childrenButton[4].node.show();
                    this.toolbar.childrenButton[8].node.hide();
                }else{
                    this.toolbar.childrenButton[4].node.hide();
                }
                if (this.data.status && this.data.status==="learning"){
                    this.toolbar.childrenButton[5].node.show();
                    this.toolbar.childrenButton[8].node.hide();
                }else{
                    this.toolbar.childrenButton[5].node.hide();
                }
            }else if (this.status==="new"){
                this.toolbar.childrenButton[0].node.hide();
                this.toolbar.childrenButton[1].node.show();
                this.toolbar.childrenButton[6].node.hide();

                this.toolbar.childrenButton[2].node.hide();
                this.toolbar.childrenButton[3].node.hide();
                this.toolbar.childrenButton[4].node.hide();
                this.toolbar.childrenButton[5].node.hide();
            }else{
                this.toolbar.childrenButton[0].node.show();
                this.toolbar.childrenButton[1].node.hide();
                this.toolbar.childrenButton[6].node.show();

                if (this.data.status) {
                    this.toolbar.childrenButton[2].node.hide();
                    this.toolbar.childrenButton[3].node.hide();
                }else{
                    this.toolbar.childrenButton[2].node.show();
                    this.toolbar.childrenButton[3].node.show();
                    this.toolbar.childrenButton[8].node.show();
                }
                if (this.data.status && this.data.status==="generating"){
                    this.toolbar.childrenButton[4].node.show();
                    this.toolbar.childrenButton[8].node.hide();
                }else{
                    this.toolbar.childrenButton[4].node.hide();
                }
                if (this.data.status && this.data.status==="learning"){
                    this.toolbar.childrenButton[5].node.show();
                    this.toolbar.childrenButton[8].node.hide();
                }else{
                    this.toolbar.childrenButton[5].node.hide();
                }
            }
        }
    },
    resize: function(){
        var size = this.contentNode.getSize();
        var toolbarSize = this.toolbarNode.getComputedSize();
        var h = size.y - toolbarSize.totalHeight;
        this.content.setStyle("height", ""+h+"px");
	},
    edit: function(){
        this.status = (!this.data.id) ? "new" : "edit";
        this.setToolbarDisabled();

        this.nameInput = this.editInput(0, "name");
        this.descriptionInput = this.editInput(1, "description");
        this.aliasInput = this.editInput(2, "alias");

        this.editProcessList(3);
        this.editApplicationList(4);

        this.analyzeInput = this.editSelect(5, ["default", "full"]);

        o2.require("o2.widget.ScriptArea", function(){
            this.propertyMapInput = this.editCode(6, "propertyMap");
            this.inValueScriptInput = this.editCode(7, "inValueScriptText");
            this.outValueScriptInput = this.editCode(8, "outValueScriptText");
            this.attachmentScriptInput = this.editCode(9), "attachmentScriptText";
        }.bind(this));
    },
    editInput: function(i, text){
        var node = this.lines[i].getLast();
        var value = node.get("text");
        node.empty();
        var input = new Element("input.o2_ann_modelContext_input", {
            "type": "text",
            "value": value,
            "placeholder": this.lp.placeholder[text]
        }).inject(node);
        return input;
    },

    editProcessList: function(i){
        var node = this.lines[i].getLast();
        var action = new Element("button.o2_ann_modelContext_button", {"text": this.lp.selectProcess}).inject(node);
        action.addEvent("click", function(e){
            MWF.xDesktop.requireApp("Selector", "package", function(){
                new MWF.O2Selector(this.app.content, {
                    "type": "process", "values": this.data.processList,
                    "onComplete": function(items){
                        e.target.getPrevious().empty();
                        this.data.processList = [];
                        items.each(function(item){
                            this.data.processList.push(item.data.id);
                        }.bind(this));
                        this.loadProcessList();
                    }.bind(this)
                });
            }.bind(this));
        }.bind(this));
    },
    editApplicationList: function(i){
        var node = this.lines[i].getLast();
        var action = new Element("button.o2_ann_modelContext_button", {"text": this.lp.selectApplication}).inject(node);
        action.addEvent("click", function(e){
            MWF.xDesktop.requireApp("Selector", "package", function(){
                new MWF.O2Selector(this.app.content, {
                    "type": "application", "values": this.data.applicationList,
                    "onComplete": function(items){
                        e.target.getPrevious().empty();
                        this.data.applicationList = [];
                        items.each(function(item){
                            this.data.applicationList.push(item.data.id);
                        }.bind(this));
                        this.loadApplicationList();
                    }.bind(this)
                });
            }.bind(this));
        }.bind(this));
    },

    editSelect: function(i, arr){
        var node = this.lines[i].getLast();
        var value = node.get("text");
        node.empty();
        var select = new Element("select").inject(node);
        arr.each(function(v){
            new Element("option", {"value": v, "text": v, "checked": (value===v)}).inject(select);
        });
        return select;
    },
    editCode: function(i, key){
        var node = this.lines[i].getLast();
        var value = (key==="propertyMap") ? JSON.stringify(this.data.propertyMap,null,2) : this.data[key];
        node.empty();
        var editor = new o2.widget.ScriptArea(node, {"title": "","maxObj": this.app.content, "isbind": false, "isload": true});
        editor.load({"code": value});
        return editor;
    },
    getModelData: function(){
        this.data.name = this.nameInput.get("value");
        this.data.description = this.descriptionInput.get("value");
        this.data.alias = this.aliasInput.get("value");
        this.data.analyzeType = this.analyzeInput.options[this.analyzeInput.selectedIndex].get("value");

        this.data.propertyMap = JSON.parse(this.propertyMapInput.editor.getValue());
        this.data.inValueScriptText = this.inValueScriptInput.editor.getValue();
        this.data.outValueScriptText = this.outValueScriptInput.editor.getValue();
        this.data.attachmentScriptText = this.attachmentScriptInput.editor.getValue();

        if (this.data.propertyMap) this.data.propertyMap = {};
    },
    save: function(){
        debugger;
        this.getModelData();
        if (!this.data.id){
            o2.Actions.get("x_query_assemble_designer").createModel(this.data, function(json){
                this.data.id = json.data.id;
                this.app.notice(this.lp.generate, "success");
                this.model.reload();
                this.reload();
            }.bind(this));
        }else{
            o2.Actions.get("x_query_assemble_designer").updateModel(this.data.id, this.data, function(){
                this.app.notice(this.lp.saveSuccess, "success");
                this.model.reload();
                this.reload();
            }.bind(this));
        }
    },
    generate: function(){
        o2.Actions.get("x_query_assemble_designer").generate(this.data.id,function(){
            this.app.notice(this.lp.generate, "success");
            this.model.reload();
            this.reload();
        }.bind(this));
    },
    learn: function(){
        o2.Actions.get("x_query_assemble_designer").learn(this.data.id, function(){
            this.app.notice(this.lp.learn, "success");
            this.model.reload();
            this.reload();
        }.bind(this));
    },
    stopGenerate: function(el, e){
        var _self = this;
        this.app.confirm("infor", e, this.lp.stopGenerateTitle, this.lp.stopGenerateInfor, 380, 100, function(){
            o2.Actions.get("x_query_assemble_designer").stopGenerating(_self.data.id, function(){
                this.app.notice(this.lp.stopGenerate, "success");
                this.model.reload();
                this.reload();
            }.bind(_self));
            this.close();
        }, function(){this.close();});

    },
    stopLearn: function(el, e){
        var _self = this;
        this.app.confirm("infor", e, this.lp.stopLearningTitle, this.lp.stopLearningInfor, 380, 100, function(){
            o2.Actions.get("x_query_assemble_designer").stopLearning(_self.data.id, function(){
                this.app.notice(this.lp.stopLearn, "success");
                this.model.reload();
                this.reload();
            }.bind(_self));
            this.close();
        }, function(){this.close();});
    },
    reset: function(el, e){
        var _self = this;
        this.app.confirm("infor", e, this.lp.resetStatusTitle, this.lp.resetStatusInfor, 380, 100, function(){
            o2.Actions.get("x_query_assemble_designer").resetStatus(_self.data.id, function(){
                this.app.notice(this.lp.resetStatus, "success");
                this.model.reload();
                this.reload();
            }.bind(_self));
            this.close();
        }, function(){this.close();});
    },
    remove: function(el, e){
        var _self = this;
        this.app.confirm("infor", e, this.lp.removeTitle, this.lp.removeInfor, 380, 100, function(){
            o2.Actions.get("x_query_assemble_designer").deleteModel(_self.data.id, function(){
                var text = this.lp.deleteModel.replace("{name}", this.data.name);
                this.app.notice(text, "success");
                var model = this.model;
                model.unselected();
                model.destroy();
            }.bind(_self));
            this.close();
        }, function(){this.close();});
    }
});