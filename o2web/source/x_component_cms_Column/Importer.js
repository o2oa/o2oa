//MWF.xDesktop.requireApp("cms.Column", "Actions.RestActions", null, false);
MWF.xApplication.cms.Column.Importer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(app, e, options){
        this.setOptions(options);
        this.app = app;
        this.container = this.app.content;
        this.actions = this.app.restActions;
        this.event = e;

        this.path = "/x_component_cms_Column/$Importer/";
        this.cssPath = "/x_component_cms_Column/$Importer/"+this.options.style+"/css.wcss";
        this._loadCss();
    },
    load: function(){
        this.container.mask({
            "destroyOnHide": true,
            "style": {
                "background-color": "#666",
                "opacity": 0.6
            }
        });
        this.node = new Element("div", {"styles": this.css.content});
        this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.app.lp.application.import}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.buttonAreaNode = new Element("div", {"styles": this.css.buttonAreaNode}).inject(this.node);

        this.cancelButton = new Element("div", {"styles": this.css.button, "text": this.app.lp.application.export_cancel}).inject(this.buttonAreaNode);
        this.okButton = new Element("div", {"styles": this.css.okButton, "text": this.app.lp.application.export_ok}).inject(this.buttonAreaNode);

        this.loadContent();

        this.setEvent();

        this.node.inject(this.container);
        this.node.position({
            relativeTo: this.container,
            position: 'center',
            edge: 'center'
        });
    },
    loadContent: function(){
        this.textarea = new Element("textarea", {"styles": this.css.textarea}).inject(this.contentNode);
    },

    setEvent: function(){
        this.cancelButton.addEvent("click", function(e){
            this.close();
        }.bind(this));

        this.okButton.addEvent("click", function(e){
            this.importApplication();
        }.bind(this));
    },

    close: function(){
        this.container.unmask();
        this.node.destroy();

        this.cancelButton = null;
        this.okButton = null;
        this.buttonAreaNode = null;
        this.contentNode = null;
        this.titleNode = null;
        this.node = null;

        this.fireEvent("close");
    },
    importApplication: function(){
        var str = this.textarea.get("value");
        if (str){
            this.applicationJson = JSON.decode(str);

            var name = this.applicationJson.application.name;
            this.actions.listApplicationSummary("", function(json){
                var flag = "create";
                var overApplication = null;
                if (json.data){
                    for (var i=0; i<json.data.length; i++){
                        if (json.data[i].name == name){
                            overApplication = json.data[i];
                            flag = "overwrite";
                            break;
                        }
                    }
                }
                if (flag == "overwrite"){
                    var _self = this;
                    this.app.confirm("infor", this.event, this.app.lp.application.import_confirm_title, this.app.lp.application.import_confirm, 400, 180, function(){
                        _self.doImportOverwriteApplication(overApplication);
                        this.close();
                    }, function(){
                        this.close();
                    })
                }else{
                    this.doImportApplication();
                }
            }.bind(this));
        }
    },
    readyProrressBar: function(){
        this.createProgressBar();
        this.status = {
            "count": this.applicationJson.processList.length+this.applicationJson.formList.length+this.applicationJson.dictionaryList.length+this.applicationJson.scriptList.length+1,
            "complete": 0
        }
    },
    doImportApplication: function(){
        this.readyProrressBar();

        this.actions.action.invoke({"name": "addApplication","data": this.applicationJson.application,"success": function(){
            this.progressBarTextNode.set("text", "Import Application Property ...");
            this.checkExport();

            this.importOverwriteProcessList();
            this.importOverwriteFormList();
            this.importOverwriteDictionaryList();
            this.importOverwriteScriptList();

        }.bind(this)});
    },
    doImportOverwriteApplication: function(overApplication){
        this.readyProrressBar();
        this.applicationJson.application.id = overApplication.id;
        this.actions.saveApplication(this.applicationJson.application, function(){
            this.progressBarTextNode.set("text", "Import Application Property ...");
            this.checkExport();
        }.bind(this));

        this.importOverwriteProcessList();
        this.importOverwriteFormList();
        this.importOverwriteDictionaryList();
        this.importOverwriteScriptList();
    },
    importOverwriteProcessList: function() {
        this.actions.listProcess(this.applicationJson.application.id, function(json){
            var processList = json.data || [];

            this.applicationJson.processList.each(function(process){
                var id = "";
                process.application = this.applicationJson.application.id;
                for (var i=0; i<processList.length; i++){
                    if (processList[i].name==process.name){
                        id = processList[i].id;
                        break;
                    }
                }

                if (id) {
                    process.id = id;
                    process.begin.process = id;
                    process.agentList.each(function(a){a.process = id;});
                    process.cancelList.each(function(a){a.process = id;});
                    process.choiceList.each(function(a){a.process = id;});
                    process.embedList.each(function(a){a.process = id;});
                    process.endList.each(function(a){a.process = id;});
                    process.invokeList.each(function(a){a.process = id;});
                    process.manualList.each(function(a){a.process = id;});
                    process.mergeList.each(function(a){a.process = id;});
                    process.messageList.each(function(a){a.process = id;});
                    process.routeList.each(function(a){a.process = id;});
                    process.parallelList.each(function(a){a.process = id;});
                    process.serviceList.each(function(a){a.process = id;});
                    process.splitList.each(function(a){a.process = id;});

                    this.actions.updateProcess(process, function(){
                        this.progressBarTextNode.set("text", "Import Process \""+process.name+"\" ...");
                        this.checkExport();
                    }.bind(this));
                }else{


                    this.actions.addProcess(process, function(){
                        this.progressBarTextNode.set("text", "Import Process \""+process.name+"\" ...");
                        this.checkExport();
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    },



    importOverwriteFormList: function(){
        this.actions.listForm(this.applicationJson.application.id, function(json){
            var formList = json.data || [];
            this.applicationJson.formList.each(function(form){
                form.application = this.applicationJson.application.id;
                var id = "";
                for (var i=0; i<formList.length; i++){
                    if (formList[i].name==form.name){
                        id = formList[i].id;
                        break;
                    }
                }
                if (id) {
                    form.id = id;
                    this.actions.action.invoke({"name": "updataForm","data": form,"parameter": {"id": form.id},"success": function(){
                        this.progressBarTextNode.set("text", "Import Form \""+form.name+"\" ...");
                        this.checkExport();
                    }.bind(this)});
                }else{
                    this.actions.action.invoke({"name": "addForm","data": form,"parameter": {"id": form.id},"success": function(){
                        this.progressBarTextNode.set("text", "Import Form \""+form.name+"\" ...");
                        this.checkExport();
                    }.bind(this)});
                }

            }.bind(this));
        }.bind(this));
    },
    importOverwriteDictionaryList: function(){
        this.actions.listDictionary(this.applicationJson.application.id, function(json){
            var dicList = json.data || [];
            this.applicationJson.dictionaryList.each(function(dictionary){
                dictionary.application = this.applicationJson.application.id;
                var id = "";
                for (var i=0; i<dicList.length; i++){
                    if (dicList[i].name==dictionary.name){
                        id = dicList[i].id;
                        break;
                    }
                }
                if (id){
                    dictionary.id = id;
                    this.actions.updateDictionary(dictionary, function(){
                        this.progressBarTextNode.set("text", "Import Process \""+dictionary.name+"\" ...");
                        this.checkExport();
                    }.bind(this));
                }else{
                    this.actions.addDictionary(dictionary, function(){
                        this.progressBarTextNode.set("text", "Import Process \""+dictionary.name+"\" ...");
                        this.checkExport();
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    },
    importOverwriteScriptList: function(){
        this.actions.listScript(this.applicationJson.application.id, function(json){
            var scriptList = json.data || [];
            this.applicationJson.scriptList.each(function(script){
                script.application = this.applicationJson.application.id;
                var id = "";
                for (var i=0; i<scriptList.length; i++){
                    if (scriptList[i].name==script.name){
                        id = scriptList[i].id;
                        break;
                    }
                }
                if (id){
                    script.id = id;
                    this.actions.updateScript(script, function(){
                        this.progressBarTextNode.set("text", "Import Process \""+script.name+"\" ...");
                        this.checkExport();
                    }.bind(this));
                }else{
                    this.actions.addScript(script, function(){
                        this.progressBarTextNode.set("text", "Import Process \""+script.name+"\" ...");
                        this.checkExport();
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    },


    checkExport: function(){
        this.status.complete = this.status.complete+1;
        var x = 358*(this.status.complete/this.status.count);
        this.progressBarPercent.setStyle("width", ""+x+"px");

        if (this.status.complete == this.status.count){
            this.progressBarNode.destroy();
            this.progressBarNode = null;
            this.progressBarTextNode = null;
            this.progressBar = null;
            this.progressBarPercent = null;

            this.close();
        }
    },
    createProgressBar: function(){
        this.node.hide();
        this.progressBarNode = new Element("div", {"styles": this.css.progressBarNode});
        this.progressBarNode.inject(this.container);
        this.progressBarNode.position({
            relativeTo: this.container,
            position: 'center',
            edge: 'center'
        });

        this.progressBarTextNode = new Element("div", {"styles": this.css.progressBarTextNode}).inject(this.progressBarNode);
        this.progressBar = new Element("div", {"styles": this.css.progressBar}).inject(this.progressBarNode);
        this.progressBarPercent = new Element("div", {"styles": this.css.progressBarPercent}).inject(this.progressBar);

    }

});
