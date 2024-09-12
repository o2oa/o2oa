MWF.xDesktop.requireApp("query.QueryManager", "package", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("process.ProcessManager", "", null, false);
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.QueryManager.Main = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Main,
	Implements: [Options, Events],

	options: {
        "application": null,
		"style": "default",
		"name": "query.QueryManager",
		"icon": "icon.png",
		"width": "1100",
		"height": "700",
		"title": MWF.xApplication.query.QueryManager.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.query.QueryManager.LP;
		this.currentContentNode = null;
        this.restActions = MWF.Actions.get("x_query_assemble_designer");
	},

    keyCopyItems: function(e){
        if (this.viewConfigurator){
            this.viewConfigurator.keyCopy(e);
        }
        if (this.statConfigurator){
            this.statConfigurator.keyCopy(e);
        }
        if (this.tableConfigurator){
            this.tableConfigurator.keyCopy(e);
        }
        if (this.statementConfigurator){
            this.statementConfigurator.keyCopy(e);
        }
        if (this.importerConfigurator){
            this.importerConfigurator.keyCopy(e);
        }
    },
    keyPasteItems: function(e) {
        var app = layout.desktop.currentApp || layout.desktop.app;
        if (app && app.appId === this.appId){
            if (this.viewConfigurator) {
                this.viewConfigurator.keyPaste(e);
            }
            if (this.statConfigurator) {
                this.statConfigurator.keyPaste(e);
            }
            if (this.tableConfigurator) {
                this.tableConfigurator.keyPaste(e);
            }
            if (this.statementConfigurator) {
                this.statementConfigurator.keyPaste(e);
            }
            if (this.importerConfigurator) {
                this.importerConfigurator.keyPaste(e);
            }
        }
    },
    loadStartMenu: function(callback){
        this.startMenuNode = new Element("div", {
            "styles": this.css.startMenuNode
        }).inject(this.node);

        this.menu = new MWF.xApplication.query.QueryManager.Menu(this, this.startMenuNode, {
            "onPostLoad": function(){
                if (this.status){
                    if (this.status.navi!=null){
                        this.menu.doAction(this.menu.startNavis[this.status.navi]);
                    }else{
                        this.menu.doAction(this.menu.startNavis[0]);
                    }
                }else{
                    this.menu.doAction(this.menu.startNavis[0]);
                }
            }.bind(this)
        });
        this.addEvent("resize", function(){
            if (this.menu) this.menu.onResize();
        }.bind(this));
    },
    clearContent: function(){
        //暂时没有启用---------------------
        if (this.selectConfiguratorContent){
            if (this.selectConfigurator) delete this.selectConfigurator;
            this.selectConfiguratorContent.destroy();
            this.selectConfiguratorContent = null;
        }
        if (this.revealConfiguratorContent){
            if (this.revealConfigurator) delete this.revealConfigurator;
            this.revealConfiguratorContent.destroy();
            this.revealConfiguratorContent = null;
        }
        //-------------------------------
        if (this.viewConfiguratorContent){
            if (this.viewConfigurator) delete this.viewConfigurator;
            this.viewConfiguratorContent.destroy();
            this.viewConfiguratorContent = null;
        }
        if (this.propertyConfiguratorContent){
            if (this.property) delete this.property;
            this.propertyConfiguratorContent.destroy();
            this.propertyConfiguratorContent = null;
        }
        if (this.statConfiguratorContent){
            if (this.statConfigurator) delete this.statConfigurator;
            this.statConfiguratorContent.destroy();
            this.statConfiguratorContent = null;
        }
        if (this.tableConfiguratorContent){
            if (this.tableConfigurator) delete this.tableConfigurator;
            this.tableConfiguratorContent.destroy();
            this.tableConfiguratorContent = null;
        }
        if (this.statementConfiguratorContent){
            if (this.statementConfigurator) delete this.statementConfigurator;
            this.statementConfiguratorContent.destroy();
            this.statementConfiguratorContent = null;
        }
        if (this.importerConfiguratorContent){
            if (this.importerConfigurator) delete this.importerConfigurator;
            this.importerConfiguratorContent.destroy();
            this.importerConfiguratorContent = null;
        }
    },

    queryProperty: function(){
        this.clearContent();
        this.propertyConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.property = new MWF.xApplication.query.QueryManager.QueryProperty(this, this.propertyConfiguratorContent);
        this.property.load();
    },

    selectConfig: function(){
        this.clearContent();
        this.selectConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadSelectConfig();
    },
    loadSelectConfig: function(){
        MWF.xDesktop.requireApp("query.QueryManager", "SelectExplorer", function(){
            this.selectConfigurator = new MWF.xApplication.query.QueryManager.SelectExplorer(this.selectConfiguratorContent, this.restActions);
            this.selectConfigurator.app = this;
            this.selectConfigurator.load();
        }.bind(this));
    },

    viewConfig: function(){
        this.clearContent();
        this.viewConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadViewConfig();
    },
    loadViewConfig: function(){
        MWF.xDesktop.requireApp("query.QueryManager", "ViewExplorer", function(){
            this.viewConfigurator = new MWF.xApplication.query.QueryManager.ViewExplorer(this.viewConfiguratorContent, this.restActions);
            this.viewConfigurator.app = this;
            this.viewConfigurator.load();
        }.bind(this));
    },

    statConfig: function(){
        this.clearContent();
        this.statConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadStatConfig();
    },
    loadStatConfig: function(){
        MWF.xDesktop.requireApp("query.QueryManager", "StatExplorer", function(){
            this.statConfigurator = new MWF.xApplication.query.QueryManager.StatExplorer(this.statConfiguratorContent, this.restActions);
            this.statConfigurator.app = this;
            this.statConfigurator.load();
        }.bind(this));
    },

    revealConfig: function(){
        this.clearContent();
        this.revealConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadRevealConfig();
    },
    loadRevealConfig: function(){
        MWF.xDesktop.requireApp("query.QueryManager", "RevealExplorer", function(){
            this.revealConfigurator = new MWF.xApplication.query.QueryManager.RevealExplorer(this.revealConfiguratorContent, this.restActions);
            this.revealConfigurator.app = this;
            this.revealConfigurator.load();
        }.bind(this));
    },

    tableConfig: function(){
        this.clearContent();
        this.tableConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadTableConfig();
    },
    loadTableConfig: function(){
        MWF.xDesktop.requireApp("query.QueryManager", "TableExplorer", function(){
            this.tableConfigurator = new MWF.xApplication.query.QueryManager.TableExplorer(this.tableConfiguratorContent, this.restActions);
            this.tableConfigurator.app = this;
            this.tableConfigurator.load();
        }.bind(this));
    },
    statementConfig: function(){
        this.clearContent();
        this.statementConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadStatementConfig();
    },
    loadStatementConfig: function(){
        MWF.xDesktop.requireApp("query.QueryManager", "StatementExplorer", function(){
            this.statementConfigurator = new MWF.xApplication.query.QueryManager.StatementExplorer(this.statementConfiguratorContent, this.restActions);
            this.statementConfigurator.app = this;
            this.statementConfigurator.load();
        }.bind(this));
    },
    importerConfig: function(){
        this.clearContent();
        this.importerConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadImporterConfig();
    },
    loadImporterConfig: function(){
        MWF.xDesktop.requireApp("query.QueryManager", "ImporterExplorer", function(){
            this.importerConfigurator = new MWF.xApplication.query.QueryManager.ImporterExplorer(this.importerConfiguratorContent, this.restActions);
            this.importerConfigurator.app = this;
            this.importerConfigurator.load();
        }.bind(this));
    }
});

MWF.xApplication.query.QueryManager.Menu = new Class({
    Extends: MWF.xApplication.process.ProcessManager.Menu,
    Implements: [Options, Events]
});

MWF.xApplication.query.QueryManager.QueryProperty = new Class({
    Extends: MWF.xApplication.process.ProcessManager.ApplicationProperty,
    load: function(){
        this.app.restActions.getApplication(this.app.options.application.id, function(json){
            this.data = json.data;
            this.propertyTitleBar = new Element("div", {
                "styles": this.app.css.propertyTitleBar,
                "text": this.data.name
            }).inject(this.node);

            this.contentNode =  new Element("div", {
                "styles": this.app.css.propertyContentNode
            }).inject(this.node);
            this.contentAreaNode =  new Element("div", {
                "styles": this.app.css.propertyContentAreaNode
            }).inject(this.contentNode);

            this.setContentHeight();
            this.setContentHeightFun = this.setContentHeight.bind(this);
            this.app.addEvent("resize", this.setContentHeightFun);
            MWF.require("MWF.widget.ScrollBar", function(){
                new MWF.widget.ScrollBar(this.contentNode, {"indent": false});
            }.bind(this));

            this.baseActionAreaNode = new Element("div", {
                "styles": this.app.css.baseActionAreaNode
            }).inject(this.contentAreaNode);

            this.baseActionNode = new Element("div", {
                "styles": this.app.css.propertyInforActionNode
            }).inject(this.baseActionAreaNode);
            this.baseTextNode = new Element("div", {
                "styles": this.app.css.baseTextNode,
                "text": this.app.lp.application.property
            }).inject(this.baseActionAreaNode);

            this.createEditBaseNode();

            this.createPropertyContentNode();

            this.createInterfaceNode();

            this.createIconContentNode();

            this.createAvailableNode();
            this.createControllerListNode();
        }.bind(this));
    },
    createInterfaceNode: function(){
        this.interfaceAreaNode = new Element("div", {
            "styles": this.app.css.baseActionAreaNode
        }).inject(this.contentAreaNode);
        this.interfaceAreaNode.setStyle("clear","both");

        this.interfaceActionNode = new Element("div", {
            "styles": this.app.css.propertyInforActionNode
        }).inject(this.interfaceAreaNode);
        this.interfaceTextNode = new Element("div", {
            "styles": this.app.css.baseTextNode,
            "text":  this.app.lp.interfaceConfig
        }).inject(this.interfaceAreaNode);

        this.interfaceContentNode = new Element("div", {"styles": {
                "overflow": "hidden",
                "-webkit-user-select": "text",
                "-moz-user-select": "text"
            }}).inject(this.contentAreaNode);


        var lp = this.app.lp;

        this.interfaceData = this.data.data ? JSON.parse(this.data.data) : "";

        var data = this.interfaceData || {
            viewShow: "true",
            viewNumber: 1,
            viewName: lp.viewName,
            statShow: "true",
            statNumber: 2,
            statName: lp.statName,
            statementShow: "true",
            statementNumber: 3,
            statementName: lp.statementName,
            importerShow: "true",
            importerNumber: 4,
            importerName: lp.importerName
        };
        // var viewStyle = "font-size:14px;color:#666;heigh:16px;margin-top:6px;margin-left:10px;";
        // var inputTextStyle = "float:right; width:120px; border:1px solid #ccc";


        var html = "<table cellspacing='0' cellpadding='0' border='0' align='left' style='margin-top: 20px;padding-left: 15%;width: 72%;'>";
        html += "<tr class='title'>" +
            "<td class='formTitle' style='width:150px'>"+lp.naviCategory+"</td>" +
            "<td class='formTitle' style='width:200px'>"+lp.isShow+"</td> " +
            "<td class='formTitle' style='width: calc( 100% - 360px )'>"+lp.showText+"</td></tr>";

        var view = {  index : data.viewNumber };
        view.html = "<tr class='view'>" +
            "<td class='formContent'><div class='sort'>↑</div>"+lp.viewName+"</td> " +
            "<td item='viewShow' class='formContent'></td> " +
            "<td item='viewName' class='formContent' style='padding:3px 0px;'></td></tr>";

        var stat = {  index : data.statNumber };
        stat.html = "<tr class='stat'><td class='formContent'><div class='sort'>↑</div>"+lp.statName+"</td>" +
            "<td item='statShow' class='formContent'></td>" +
            "<td item='statName' class='formContent' style='padding:3px 0px;'></td></tr>";

        var statement = { index : data.statementNumber };
        statement.html = "<tr class='statement'>" +
            "<td class='formContent'><div class='sort'>↑</div>"+lp.statementName+"</td>" +
            "<td item='statementShow' class='formContent'></td> " +
            "<td item='statementName' class='formContent' style='padding:3px 0px;'></td></tr>";

        var importer = {index : data.importerNumber};
        importer.html = "<tr class='importer'>" +
            "<td class='formContent'><div class='sort'>↑</div>"+lp.importerName+"</td> " +
            "<td item='importerShow' class='formContent'></td> " +
            "<td item='importerName' class='formContent' style='padding:3px 0px;'></td></tr>";

        var array = [view,stat,statement,importer];
        array.sort(function(a, b){
            return a.index - b.index;
        });
        array.each(function(a){
            html += a.html;
        });

        html += "</table>";

        this.interfaceContentNode.set("html", html);
        this.interfaceSortActions = this.interfaceContentNode.getElements("div.sort");
        this.interfaceSortActions.setStyles({
            "cursor":"pointer", "width":"20px", "font-size": "16px", "text-align":"center", "display": "none"
        }).set("title", lp.moveUp).addEvent("click", function(ev){
            var tr = ev.target.getParent("tr");
            var trBefore = tr.getPrevious("tr");
            if( !trBefore.hasClass("title") ){
                tr.inject(trBefore, "before");
            }
            var trs = tr.getParent("table").getElements("tr[class!='title']");
            trs.each(function(tr, i){
                this.interfaceForm.data[0][tr.get("class")+"Number"] = i+1;
            }.bind(this))
        }.bind(this));
        this.interfaceContentNode.getElements("td.formTitle").setStyles(this.app.css.propertyInterfaceTdTitle);
        this.interfaceContentNode.getElements("td.formContent").setStyles(this.app.css.propertyInterfaceTdContent);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.interfaceForm = new MForm(this.interfaceContentNode, data, {
                isEdited: false,
                style : "appproperty",
                itemTemplate: {
                    viewShow: { type:"radio", selectValue : ["true","false"], selectText: [lp.show, lp.hide], style: {"display":"inline"} },
                    statShow: { type:"radio", selectValue : ["true","false"], selectText: [lp.show, lp.hide], style: {"display":"inline"} },
                    statementShow: { type:"radio", selectValue : ["true","false"], selectText: [lp.show, lp.hide], style: {"display":"inline"} },
                    importerShow: { type:"radio", selectValue : ["true","false"], selectText: [lp.show, lp.hide], style: {"display":"inline"} },
                    statName: { event: {
                                focus: function(node){ node.setStyles(this.app.css.input_focus) }.bind(this),
                                blur: function(node){ node.setStyles(this.app.css.input) }.bind(this)
                            }
                     },
                    viewName: {  event: {
                            focus: function(node){ node.setStyles(this.app.css.input_focus) }.bind(this),
                            blur: function(node){ node.setStyles(this.app.css.input) }.bind(this)
                        }
                    },
                    statementName: {  event: {
                            focus: function(node){ node.setStyles(this.app.css.input_focus) }.bind(this),
                            blur: function(node){ node.setStyles(this.app.css.input) }.bind(this)
                        }
                    },
                    importerName: { event: {
                            focus: function(node){ node.setStyles(this.app.css.input_focus) }.bind(this),
                            blur: function(node){ node.setStyles(this.app.css.input) }.bind(this)
                        }
                    }
                }
            }, this);
            debugger;
            this.interfaceForm.load();

        }.bind(this), true);
    },
    createPropertyContentNode: function(){
        this.propertyContentNode = new Element("div", {"styles": {
            "overflow": "hidden",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        }}).inject(this.contentAreaNode);

        var html = "<table cellspacing='0' cellpadding='0' border='0' width='95%' align='center' style='margin-top: 20px'>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.name+"</td><td id='formApplicationName'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.alias+"</td><td id='formApplicationAlias'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.description+"</td><td id='formApplicationDescription'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.type+"</td><td id='formApplicationType'></td></tr>";
        // html += "<tr><td class='formTitle'>"+this.app.lp.application.firstPage+"</td><td id='formApplicationFirstPage'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.id+"</td><td id='formApplicationId'></td></tr>";
        //     html += "<tr><td class='formTitle'>"+this.app.lp.application.icon+"</td><td id='formApplicationIcon'></td></tr>";
        html += "</table>";
        this.propertyContentNode.set("html", html);
        this.propertyContentNode.getElements("td.formTitle").setStyles(this.app.css.propertyBaseContentTdTitle);

        this.nameInput = new MWF.xApplication.query.QueryManager.Input(this.propertyContentNode.getElement("#formApplicationName"), this.data.name, this.app.css.formInput);
        this.aliasInput = new MWF.xApplication.query.QueryManager.Input(this.propertyContentNode.getElement("#formApplicationAlias"), this.data.alias, this.app.css.formInput);
        this.descriptionInput = new MWF.xApplication.query.QueryManager.Input(this.propertyContentNode.getElement("#formApplicationDescription"), this.data.description, this.app.css.formInput);
        //2019年11月15日--ji 接口是queryCategory字段
        //this.typeInput = new MWF.xApplication.query.QueryManager.Input(this.propertyContentNode.getElement("#formApplicationType"), this.data.applicationCategory, this.app.css.formInput);
        this.typeInput = new MWF.xApplication.query.QueryManager.Input(this.propertyContentNode.getElement("#formApplicationType"), this.data.queryCategory, this.app.css.formInput);
        // this.firstPageInput = new MWF.xApplication.query.QueryManager.Select(this.propertyContentNode.getElement("#formApplicationFirstPage"), this.data.firstPage, this.app.css.formInput, function(){
        //     var pages = {};
        //     debugger;
        //     this.app.restActions.listPage(this.app.options.application.id, function(json){
        //         json.data.each(function(page) {
        //             pages[page.id] = page.name;
        //         }.bind(this));
        //     }.bind(this), null, false);
        //     return pages;
        // }.bind(this));
        this.idInput = new MWF.xApplication.query.QueryManager.Input(this.propertyContentNode.getElement("#formApplicationId"), this.data.id, this.app.css.formInput);
    },
    editMode: function(){
        this.nameInput.editMode();
        this.aliasInput.editMode();
        this.descriptionInput.editMode();
        this.typeInput.editMode();
        //this.firstPageInput.editMode();
        if(this.interfaceForm)this.interfaceForm.changeMode();
        if(this.interfaceSortActions)this.interfaceSortActions.setStyle("display","inline-block");
        this.isEdit = true;
    },
    readMode: function(){
        this.nameInput.readMode();
        this.aliasInput.readMode();
        this.descriptionInput.readMode();
        this.typeInput.readMode();
        //this.firstPageInput.readMode();
        if(this.interfaceForm)this.interfaceForm.changeMode( this.interfaceSaved );
        if(this.interfaceSortActions)this.interfaceSortActions.setStyle("display","none");
        this.interfaceSaved = false;
        this.isEdit = false;
    },
    save: function(callback, cancel) {
        this.data.name = this.nameInput.input.get("value");
        this.data.alias = this.aliasInput.input.get("value");
        this.data.description = this.descriptionInput.input.get("value");
        //2019年11月15日--ji 接口是queryCategory字段
        //this.data.applicationCategory = this.typeInput.input.get("value");
        this.data.queryCategory = this.typeInput.input.get("value");
        //this.data.firstPage = this.firstPageInput.input.get("value");

        if(this.interfaceForm){
            this.interfaceData = this.interfaceForm.getResult(true, ",", true, false, true );
            this.data.data = JSON.stringify(this.interfaceData);
        }
        this.interfaceSaved = true;

        this.app.restActions.saveApplication(this.data, function (json) {
            this.propertyTitleBar.set("text", this.data.name);
            this.data.id = json.data.id;
            this.nameInput.save();
            this.aliasInput.save();
            this.descriptionInput.save();
            this.typeInput.save();
            //this.firstPageInput.save();

            if (callback) callback();
        }.bind(this), function (xhr, text, error) {
            if (cancel) cancel(xhr, text, error);
        }.bind(this));
    }
});

MWF.xApplication.query.QueryManager.Input = new Class({
    Extends: MWF.xApplication.process.ProcessManager.Input,
    Implements: [Events]
});
MWF.xApplication.query.QueryManager.Select = new Class({
    Extends: MWF.xApplication.process.ProcessManager.Input,
    Implements: [Events],
    initialize: function(node, value, style, select){
        this.node = $(node);
        this.value = (value) ? value: "";
        this.style = style;
        this.select = select;
        this.selectList = null;;
        this.load();
    },
    getSelectList: function(){
        if (this.select){
            return this.select();
        }
        return [];
    },
    getText: function(value){
        if (value){
            if (this.selectList){
                return this.selectList[value] || "";
            }
        }
        return "";
    },
    load: function(){
        this.selectList = this.getSelectList();
        this.content = new Element("div", {
            "styles": this.style.content,
            "text": this.getText(this.value)
        }).inject(this.node);
    },
    editMode: function(){
        this.content.empty();
        this.input = new Element("select",{
            //"styles": this.style.input,
            //"value": this.value
        }).inject(this.content);

        Object.each(this.selectList, function(v, k){
            new Element("option", {
                "value": k,
                "text": v,
                "selected": (this.value==v)
            }).inject(this.input);
        }.bind(this));

        //this.input.addEvents({
        //    //"focus": function(){
        //    //    this.input.setStyles(this.style.input_focus);
        //    //}.bind(this),
        //    //"blur": function(){
        //    //    this.input.setStyles(this.style.input);
        //    //}.bind(this),
        //    //"change": function(){
        //    //    this.input.setStyles(this.style.input);
        //    //}.bind(this)
        //});
    },
    readMode: function(){
        this.content.empty();
        this.input = null;
        this.content.set("text", this.getText(this.value));
    },
    save: function(){
        if (this.input) this.value = this.input.options[this.input.selectedIndex].get("value");
        return this.value;
    }
});