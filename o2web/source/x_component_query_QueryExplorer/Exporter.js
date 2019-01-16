MWF.xApplication.query.QueryExplorer.Exporter = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(app, data, options){
        this.setOptions(options);
        this.app = app;
        this.container = this.app.content;
        this.data = data;

        this.path = "/x_component_portal_PortalExplorer/$Exporter/";
        this.cssPath = "/x_component_portal_PortalExplorer/$Exporter/"+this.options.style+"/css.wcss";
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
        this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.app.lp.application.export}).inject(this.node);
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
        this.actions = this.app.restActions;

        //var listTitleNodeArea = new Element("div", {"styles": this.css.listTitleNodeArea}).inject(this.contentNode);
        //this.processSelectAction = new Element("div", {"styles": this.css.listTitleActionNode, "text": this.app.lp.application.select}).inject(listTitleNodeArea);
        //new Element("div", {"styles": this.css.listTitleNode, "text": this.app.lp.application.process}).inject(listTitleNodeArea);
        //this.processListNode = new Element("div", {"styles": this.css.listNode}).inject(this.contentNode);
        //
        listTitleNodeArea = new Element("div", {"styles": this.css.listTitleNodeArea}).inject(this.contentNode);
        this.viewSelectAction = new Element("div", {"styles": this.css.listTitleActionNode, "text": this.app.lp.application.select}).inject(listTitleNodeArea);
        new Element("div", {"styles": this.css.listTitleNode, "text": this.app.lp.application.view}).inject(listTitleNodeArea);
        this.viewListNode = new Element("div", {"styles": this.css.listNode}).inject(this.contentNode);
        //
        //listTitleNodeArea = new Element("div", {"styles": this.css.listTitleNodeArea}).inject(this.contentNode);
        //this.dictionarySelectAction = new Element("div", {"styles": this.css.listTitleActionNode, "text": this.app.lp.application.select}).inject(listTitleNodeArea);
        //new Element("div", {"styles": this.css.listTitleNode, "text": this.app.lp.application.dictionary}).inject(listTitleNodeArea);
        //this.dictionaryListNode = new Element("div", {"styles": this.css.listNode}).inject(this.contentNode);
        //
        listTitleNodeArea = new Element("div", {"styles": this.css.listTitleNodeArea}).inject(this.contentNode);
        this.statSelectAction = new Element("div", {"styles": this.css.listTitleActionNode, "text": this.app.lp.application.select}).inject(listTitleNodeArea);
        new Element("div", {"styles": this.css.listTitleNode, "text": this.app.lp.application.stat}).inject(listTitleNodeArea);
        this.statListNode = new Element("div", {"styles": this.css.listNode}).inject(this.contentNode);

        //this.listProcess();
        //this.listPage();
        //this.listDictionary();
        //this.listScript();
        this.listView();
        this.listStat();

        //this.processSelectAction.addEvent("click", function(){this.inverse(this.processListNode);}.bind(this));
        //this.formSelectAction.addEvent("click", function(){this.inverse(this.formListNode);}.bind(this));
        //this.dictionarySelectAction.addEvent("click", function(){this.inverse(this.dictionaryListNode);}.bind(this));
        //this.scriptSelectAction.addEvent("click", function(){this.inverse(this.scriptListNode);}.bind(this));

        this.viewSelectAction.addEvent("click", function(){this.inverse(this.viewListNode);}.bind(this));
        this.statSelectAction.addEvent("click", function(){this.inverse(this.statListNode);}.bind(this));
    },
    //listProcess: function(){
    //    this.actions.listProcess(this.data.id, function(json){
    //        if (json.data){
    //            json.data.each(function(process){
    //                this.createItem(process, this.processListNode);
    //            }.bind(this));
    //        }
    //    }.bind(this));
    //},
    // listPage: function(){
    //     this.actions.listPage(this.data.id, function(json){
    //         if (json.data){
    //             json.data.each(function(form){
    //                 this.createItem(form, this.formListNode);
    //             }.bind(this));
    //         }
    //     }.bind(this));
    // },
    //listDictionary: function(){
    //    this.actions.listDictionary(this.data.id, function(json){
    //        if (json.data){
    //            json.data.each(function(dic){
    //                this.createItem(dic, this.dictionaryListNode);
    //            }.bind(this));
    //        }
    //    }.bind(this));
    //},
    listView: function(){
        this.actions.listView(this.data.id, function(json){
            if (json.data){
                json.data.each(function(view){
                    this.createItem(view, this.viewListNode);
                }.bind(this));
            }
        }.bind(this));
    },
    listStat: function(){
        this.actions.listStat(this.data.id, function(json){
            if (json.data){
                json.data.each(function(stat){
                    this.createItem(stat, this.statListNode);
                }.bind(this));
            }
        }.bind(this));
    },

    inverse: function(node){
        var inputs = node.getElements("input");
        inputs.each(function(input){
            if (input.checked){
                input.set("checked", false);
            }else{
                input.set("checked", true);
            }
        });
    },


    createItem: function(data, node){
        var item = new Element("div", {"styles": this.css.processItem}).inject(node);
        var checkbox = new Element("input", {
            "styles": this.css.processItemCheckbox,
            "type": "checkbox",
            "checked": true
        }).inject(item);
        checkbox.store("itemData", data);
        var textNode = new Element("div", {"text": data.name, "styles": this.css.processItemText}).inject(item);
    },

    setEvent: function(){
        this.cancelButton.addEvent("click", function(e){
            this.close();
        }.bind(this));

        this.okButton.addEvent("click", function(e){
            this.exportApplication();
            //    this.close();
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
        //this.processListNode = null;
        //this.formListNode = null;
        //this.dictionaryListNode = null;
        //this.scriptListNode = null;

        this.viewListNode = null;
        this.statListNode = null;

        this.fireEvent("close");
    },
    exportApplication: function(){
        this.applicationJson = {
            "application": {},
            //"processList": [],
            "viewList": [],
            //"dictionaryList": [],
            "statList": []
        };

        this.createProgressBar();

        //var process = this.processListNode.getElements("input:checked");
        var views = this.viewListNode.getElements("input:checked");
        //var dics = this.dictionaryListNode.getElements("input:checked");
        var stats = this.statListNode.getElements("input:checked");

        this.status = {
            "count": views.length+stats.length+1,
            "complete": 0
        };
        this.exportProperty();
        //this.exportProcesses(process);
        //this.exportPages(pages);
        //this.exportDictionarys(dics);
        //this.exportScripts(scripts);
        this.exportViews(views);
        this.exportStats(stats);
    },
    exportProperty: function(){
        this.actions.getApplication(this.data.id, function(json){
            this.progressBarTextNode.set("text", "load Application Property ...");
            if (json.data){
                this.applicationJson.application = json.data;
            }
            this.checkExport();
        }.bind(this));
    },
    //exportProcesses: function(processes){
    //    processes.each(function(processCheckbox){
    //        var process = processCheckbox.retrieve("itemData");
    //        this.actions.getProcess(process.id, function(json){
    //            this.progressBarTextNode.set("text", "load Process \""+process.name+"\" ...");
    //            if (json.data){
    //                this.applicationJson.processList.push(json.data);
    //            }
    //            this.checkExport();
    //        }.bind(this));
    //    }.bind(this));
    //},
    // exportPages: function(forms){
    //     forms.each(function(formCheckbox){
    //         var form = formCheckbox.retrieve("itemData");
    //         this.actions.getPage(form.id, function(json){
    //             this.progressBarTextNode.set("text", "load Form \""+form.name+"\" ...");
    //             if (json.data){
    //                 this.applicationJson.pageList.push(json.data);
    //             }
    //             this.checkExport();
    //         }.bind(this));
    //     }.bind(this));
    // },
    //exportDictionarys: function(dics){
    //    dics.each(function(dicCheckbox){
    //        var dic = dicCheckbox.retrieve("itemData");
    //        this.actions.getDictionary(dic.id, function(json){
    //            this.progressBarTextNode.set("text", "load Dictionary \""+dic.name+"\" ...");
    //            if (json.data){
    //                this.applicationJson.dictionaryList.push(json.data);
    //            }
    //            this.checkExport();
    //        }.bind(this));
    //    }.bind(this));
    //},
    exportViews: function(views){
        views.each(function(viewCheckbox){
            var view = viewCheckbox.retrieve("itemData");
            this.actions.getView(view.id, function(json){
                this.progressBarTextNode.set("text", "load View \""+view.name+"\" ...");
                if (json.data){
                    this.applicationJson.viewList.push(json.data);
                }
                this.checkExport();
            }.bind(this));
        }.bind(this));
    },
    exportStats: function(stats){
        stats.each(function(statCheckbox){
            var stat = statCheckbox.retrieve("itemData");
            this.actions.getStat(stat.id, function(json){
                this.progressBarTextNode.set("text", "load Stat \""+stat.name+"\" ...");
                if (json.data){
                    this.applicationJson.statList.push(json.data);
                }
                this.checkExport();
            }.bind(this));
        }.bind(this));
    },
    checkExport: function(){
        this.status.complete = this.status.complete+1;
        var x = 358*(this.status.complete/this.status.count);
        this.progressBarPercent.setStyle("width", ""+x+"px");

        if (this.status.complete == this.status.count){
            this.saveApplicationToLocal();
        }
    },
    saveApplicationToLocal: function(){
        if (window.hasOwnProperty("ActiveXObject")){
            var win = window.open("", "_blank");
            win.document.write(JSON.encode(this.applicationJson));
        }else{
            this.downloadFile(this.data.name+".xapp", JSON.encode(this.applicationJson));
        }


        this.progressBarNode.destroy();
        this.progressBarNode = null;
        this.progressBarTextNode = null;
        this.progressBar = null;
        this.progressBarPercent = null;

        this.close();
    },

    downloadFile: function(fileName, content){
        var link = new Element("a", {"text": this.data.name}).inject(this.progressBarTextNode);
        var blob = new Blob([content]);
        link.download = fileName;
        link.href = URL.createObjectURL(blob);
        //link.href = "data:text/plain," + content;

        var evt = document.createEvent("HTMLEvents");
        evt.initEvent("click", false, false);
        link.dispatchEvent(evt);
        link.click();
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
