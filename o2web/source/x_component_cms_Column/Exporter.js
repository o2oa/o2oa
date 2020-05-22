//MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", null, false);
MWF.xApplication.cms.Column.Exporter = new Class({
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

        this.path = "../x_component_cms_Column/$Exporter/";
        this.cssPath = "../x_component_cms_Column/$Exporter/"+this.options.style+"/css.wcss";
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
        this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.app.lp.export}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.buttonAreaNode = new Element("div", {"styles": this.css.buttonAreaNode}).inject(this.node);

        this.cancelButton = new Element("div", {"styles": this.css.button, "text": this.app.lp.export_cancel}).inject(this.buttonAreaNode);
        this.okButton = new Element("div", {"styles": this.css.okButton, "text": this.app.lp.export_ok}).inject(this.buttonAreaNode);

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

        var listTitleNodeArea = new Element("div", {"styles": this.css.listTitleNodeArea}).inject(this.contentNode);
        this.categorySelectAction = new Element("div", {"styles": this.css.listTitleActionNode, "text": this.app.lp.inverse}).inject(listTitleNodeArea);
        new Element("div", {"styles": this.css.listTitleNode, "text": this.app.lp.cate}).inject(listTitleNodeArea);
        this.categoryListNode = new Element("div", {"styles": this.css.listNode}).inject(this.contentNode);

        listTitleNodeArea = new Element("div", {"styles": this.css.listTitleNodeArea}).inject(this.contentNode);
        this.formSelectAction = new Element("div", {"styles": this.css.listTitleActionNode, "text": this.app.lp.inverse}).inject(listTitleNodeArea);
        new Element("div", {"styles": this.css.listTitleNode, "text": this.app.lp.form}).inject(listTitleNodeArea);
        this.formListNode = new Element("div", {"styles": this.css.listNode}).inject(this.contentNode);

        listTitleNodeArea = new Element("div", {"styles": this.css.listTitleNodeArea}).inject(this.contentNode);
        this.listSelectAction = new Element("div", {"styles": this.css.listTitleActionNode, "text": this.app.lp.inverse}).inject(listTitleNodeArea);
        new Element("div", {"styles": this.css.listTitleNode, "text": this.app.lp.list}).inject(listTitleNodeArea);
        this.listListNode = new Element("div", {"styles": this.css.listNode}).inject(this.contentNode);

        listTitleNodeArea = new Element("div", {"styles": this.css.listTitleNodeArea}).inject(this.contentNode);
        this.queryViewSelectAction = new Element("div", {"styles": this.css.listTitleActionNode, "text": this.app.lp.inverse}).inject(listTitleNodeArea);
        new Element("div", {"styles": this.css.listTitleNode, "text": this.app.lp.queryView}).inject(listTitleNodeArea);
        this.queryViewListNode = new Element("div", {"styles": this.css.listNode}).inject(this.contentNode);

        listTitleNodeArea = new Element("div", {"styles": this.css.listTitleNodeArea}).inject(this.contentNode);
        this.dictionarySelectAction = new Element("div", {"styles": this.css.listTitleActionNode, "text": this.app.lp.inverse}).inject(listTitleNodeArea);
        new Element("div", {"styles": this.css.listTitleNode, "text": this.app.lp.dictionary}).inject(listTitleNodeArea);
        this.dictionaryListNode = new Element("div", {"styles": this.css.listNode}).inject(this.contentNode);

        listTitleNodeArea = new Element("div", {"styles": this.css.listTitleNodeArea}).inject(this.contentNode);
        this.scriptSelectAction = new Element("div", {"styles": this.css.listTitleActionNode, "text": this.app.lp.inverse}).inject(listTitleNodeArea);
        new Element("div", {"styles": this.css.listTitleNode, "text": this.app.lp.script}).inject(listTitleNodeArea);
        this.scriptListNode = new Element("div", {"styles": this.css.listNode}).inject(this.contentNode);

        this.listCategory();
        this.listForm();
        this.listList();
        this.listQueryView();
        this.listDictionary();
        this.listScript();

        this.categorySelectAction.addEvent("click", function(){this.inverse(this.categoryListNode);}.bind(this));
        this.formSelectAction.addEvent("click", function(){this.inverse(this.formListNode);}.bind(this));
        this.listSelectAction.addEvent("click", function(){this.inverse(this.listListNode);}.bind(this));
        this.queryViewSelectAction.addEvent("click", function(){this.inverse(this.queryViewListNode);}.bind(this));
        this.dictionarySelectAction.addEvent("click", function(){this.inverse(this.dictionaryListNode);}.bind(this));
        this.scriptSelectAction.addEvent("click", function(){this.inverse(this.scriptListNode);}.bind(this));
    },
    listCategory: function(){
        this.actions.listCategory(this.data.id, function(json){
            if (json.data){
                json.data.each(function(category){
                    this.createItem(category, this.categoryListNode);
                }.bind(this));
            }
        }.bind(this));
    },
    listForm: function(){
        this.actions.listForm(this.data.id, function(json){
            if (json.data){
                json.data.each(function(form){
                    this.createItem(form, this.formListNode);
                }.bind(this));
            }
        }.bind(this));
    },
    listList: function(){
        this.actions.listView(this.data.id, function(json){
            if (json.data){
                json.data.each(function(list){
                    this.createItem(list, this.listListNode);
                }.bind(this));
            }
        }.bind(this));
    },
    listQueryView: function(){
        this.actions.listQueryView(this.data.id, function(json){
            if (json.data){
                json.data.each(function(queryview){
                    this.createItem(queryview, this.queryViewListNode);
                }.bind(this));
            }
        }.bind(this));
    },
    listDictionary: function(){
        this.actions.listDictionary(this.data.id, function(json){
            if (json.data){
                json.data.each(function(dic){
                    this.createItem(dic, this.dictionaryListNode);
                }.bind(this));
            }
        }.bind(this));
    },
    listScript: function(){
        this.actions.listScript(this.data.id, function(json){
            if (json.data){
                json.data.each(function(script){
                    this.createItem(script, this.scriptListNode);
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
        var item = new Element("div", {"styles": this.css.categoryItem}).inject(node);
        var checkbox = new Element("input", {
            "styles": this.css.categoryItemCheckbox,
            "type": "checkbox",
            "checked": true
        }).inject(item);
        checkbox.store("itemData", data);
        var textNode = new Element("div", {"text": data.name, "styles": this.css.categoryItemText}).inject(item);
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
        this.categoryListNode = null;
        this.formListNode = null;
        this.listListNode = null;
        this.queryViewListNode = null;
        this.dictionaryListNode = null;
        this.scriptListNode = null;

        this.fireEvent("close");
    },
    exportApplication: function(){
        this.applicationJson = {
            "application": {},
            "categoryList": [],
            "formList": [],
            "listList": [],
            "queryViewList": [],
            "dictionaryList": [],
            "scriptList": []
        };

        this.createProgressBar();

        var category = this.categoryListNode.getElements("input:checked");
        var forms = this.formListNode.getElements("input:checked");
        var lists = this.listListNode.getElements("input:checked");
        var queryViews = this.queryViewListNode.getElements("input:checked");
        var dics = this.dictionaryListNode.getElements("input:checked");
        var scripts = this.scriptListNode.getElements("input:checked");

        this.status = {
            "count": category.length+forms.length+  lists.length + queryViews.length + dics.length+scripts.length+1,
            "complete": 0
        };
        this.exportProperty();
        this.exportCategoryes(category);
        this.exportForms(forms);
        this.exportLists(lists);
        this.exportQueryViews(queryViews);
        this.exportDictionarys(dics);
        this.exportScripts(scripts);
    },
    exportProperty: function(){
        this.actions.getColumn(this.data.id, function(json){
            this.progressBarTextNode.set("text", "load Application Property ...");
            if (json.data){
                this.applicationJson.application = json.data;
            }
            this.checkExport();
        }.bind(this));
    },
    exportCategoryes: function(categoryes){
        categoryes.each(function(categoryCheckbox){
            var category = categoryCheckbox.retrieve("itemData");
            this.actions.getCategory(category.id, function(json){
                this.progressBarTextNode.set("text", "load Category \""+category.name+"\" ...");
                if (json.data){
                    this.applicationJson.categoryList.push(json.data);
                }
                this.checkExport();
            }.bind(this));
        }.bind(this));
    },
    exportForms: function(forms){
        forms.each(function(formCheckbox){
            var form = formCheckbox.retrieve("itemData");
            this.actions.getForm(form.id, function(json){
                this.progressBarTextNode.set("text", "load Form \""+form.name+"\" ...");
                if (json.data){
                    this.applicationJson.formList.push(json.data);
                }
                this.checkExport();
            }.bind(this));
        }.bind(this));
    },
    exportLists: function(lists){
        lists.each(function(listCheckbox){
            var list = listCheckbox.retrieve("itemData");
            this.actions.getView(list.id, function(json){
                this.progressBarTextNode.set("text", "load Form \""+list.name+"\" ...");
                if (json.data){
                    this.applicationJson.listList.push(json.data);
                }
                this.checkExport();
            }.bind(this));
        }.bind(this));
    },
    exportQueryViews: function(queryViews){
        queryViews.each(function(queryViewCheckbox){
            var queryView = queryViewCheckbox.retrieve("itemData");
            this.actions.getQueryView(queryView.id, function(json){
                this.progressBarTextNode.set("text", "load Form \""+queryView.name+"\" ...");
                if (json.data){
                    this.applicationJson.queryViewList.push(json.data);
                }
                this.checkExport();
            }.bind(this));
        }.bind(this));
    },
    exportDictionarys: function(dics){
        dics.each(function(dicCheckbox){
            var dic = dicCheckbox.retrieve("itemData");
            this.actions.getDictionary(dic.id, function(json){
                this.progressBarTextNode.set("text", "load Dictionary \""+dic.name+"\" ...");
                if (json.data){
                    this.applicationJson.dictionaryList.push(json.data);
                }
                this.checkExport();
            }.bind(this));
        }.bind(this));
    },
    exportScripts: function(scripts){
        scripts.each(function(scriptCheckbox){
            var script = scriptCheckbox.retrieve("itemData");
            this.actions.getScript(script.id, function(json){
                this.progressBarTextNode.set("text", "load Script \""+script.name+"\" ...");
                if (json.data){
                    this.applicationJson.scriptList.push(json.data);
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
