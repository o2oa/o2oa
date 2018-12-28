MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.QueryViewDesigner = MWF.xApplication.cms.QueryViewDesigner || {};
MWF.CMSQVD = MWF.xApplication.cms.QueryViewDesigner;

MWF.require("MWF.xScript.CMSMacro", null, false);

MWF.xDesktop.requireApp("cms.QueryViewDesigner", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("cms.QueryViewDesigner", "Property", null, false);
MWF.xDesktop.requireApp("process.ViewDesigner", "View", null, false);

MWF.xApplication.cms.QueryViewDesigner.View = new Class({
    Extends: MWF.xApplication.process.ViewDesigner.View,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "/x_component_cms_QueryViewDesigner/$View/view.html"
    },
    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "/x_component_process_ViewDesigner/$View/";
        this.cssPath = "/x_component_process_ViewDesigner/$View/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.designer = designer;
        this.data = data;
        if (!this.data.data) this.data.data = {};
        this.parseData();

        this.node = this.designer.designNode;
        //this.tab = this.designer.tab;

        this.areaNode = new Element("div", {"styles": {"height": "100%", "overflow": "auto"}});

        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.areaNode, {"distance": 100});
        //}.bind(this));


        this.propertyListNode = this.designer.propertyDomArea;
        //this.propertyNode = this.designer.propertyContentArea;

        if(this.designer.application){
            this.data.appId = this.designer.application.id;
            this.data.appName = this.designer.application.appName || this.designer.application.appName;
            if( !this.data.creatorPerson )this.data.creatorPerson = layout.desktop.session.user.distinguishedName;
        }

        this.isNewView = (this.data.id) ? false : true;

        this.items = [];
        this.view = this;

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.cms.QueryViewDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
                "path": this.options.propertyPath,
                "onPostLoad": function(){
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        }else{
            this.property.show();
        }
    },


    loadViewData: function(){
        if (this.data.id){
            this.saveSilence(function(){
                this.viewContentBodyNode.empty();
                this.viewContentTableNode = new Element("table", {
                    "styles": this.css.viewContentTableNode,
                    "border": "0px",
                    "cellPadding": "0",
                    "cellSpacing": "0"
                }).inject(this.viewContentBodyNode);

                this.designer.actions.loadQueryView(this.data.id, function(json){
                    var entries = {};
                    json.data.selectEntryList.each(function(entry){entries[entry.column] = entry;}.bind(this));

                    if (this.json.data.groupEntry.column){
                        if (json.data.groupGrid.length){
                            json.data.groupGrid.each(function(line, idx){
                                var groupTr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);
                                var colSpan = this.items.length;
                                var td = new Element("td", {"styles": this.css.viewContentGroupTdNode, "colSpan": colSpan}).inject(groupTr);
                                var groupAreaNode = new Element("div", {"styles": this.css.viewContentTdGroupNode}).inject(td);
                                var groupIconNode = new Element("div", {"styles": this.css.viewContentTdGroupIconNode}).inject(groupAreaNode);
                                var groupTextNode = new Element("div", {"styles": this.css.viewContentTdGroupTextNode}).inject(groupAreaNode);
                                groupTextNode.set("text", line.group);

                                var subtrs = [];
                                line.list.each(function(entry){
                                    var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);
                                    tr.setStyle("display", "none");
                                    var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                    Object.each(entry.data, function(d, k){
                                        if (k!=this.json.data.groupEntry.column){
                                            var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                            td.set("text", (entries[k].code) ? MWF.CMSMacro.exec(entries[k].code, {"value": d, "data": json.data}) : d);
                                        }
                                    }.bind(this));
                                    subtrs.push(tr)
                                }.bind(this));

                                groupAreaNode.store("subtrs", subtrs);

                                var _self = this;
                                groupAreaNode.addEvent("click", function(){
                                    var subtrs = this.retrieve("subtrs");
                                    var iconNode = groupAreaNode.getFirst("div");
                                    if (subtrs[0]){
                                        if (subtrs[0].getStyle("display")=="none"){
                                            subtrs.each(function(subtr){ subtr.setStyle("display", "table-row"); });
                                            iconNode.setStyle("background", "url("+"/x_component_process_ViewDesigner/$View/default/icon/down.png) center center no-repeat");
                                        }else{
                                            subtrs.each(function(subtr){ subtr.setStyle("display", "none"); });
                                            iconNode.setStyle("background", "url("+"/x_component_process_ViewDesigner/$View/default/icon/right.png) center center no-repeat");
                                        }
                                    }
                                    _self.setContentHeight();
                                });
                            }.bind(this));
                            this.setContentColumnWidth();
                            this.setContentHeight();
                        }

                    }else{
                        if (json.data.grid.length){
                            json.data.grid.each(function(line, idx){
                                var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);
                                Object.each(line.data, function(d, k){
                                    var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                    td.set("text", (entries[k].code) ? MWF.CMSMacro.exec(entries[k].code, {"value": d, "data": json.data}) : d);
                                }.bind(this));
                            }.bind(this));
                            this.setContentColumnWidth();
                            this.setContentHeight();
                        }
                    }
                }.bind(this));
            }.bind(this));
        }
    },

    addColumn: function(){
        MWF.require("MWF.widget.UUID", function(){
            var id = (new MWF.widget.UUID).id;
            var json = {
                "id": id,
                "column": id,
                "displayName": this.designer.lp.unnamed,
                "selectType": "attribute",
                "orderType": "original"
            };
            if (!this.json.data.selectEntryList) this.json.data.selectEntryList = [];
            this.json.data.selectEntryList.push(json);
            var column = new MWF.xApplication.cms.QueryViewDesigner.View.Column(json, this);
            this.items.push(column);
            column.selected();

            if (this.viewContentTableNode){
                var trs = this.viewContentTableNode.getElements("tr");
                trs.each(function(tr){
                    new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr)
                }.bind(this));
                //this.setContentColumnWidth();
            }
            this.setViewWidth();
            this.addColumnNode.scrollIntoView(true);

        }.bind(this));
        //new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 0}).toRight();
    },


    loadViewColumns: function(){
        //    for (var i=0; i<10; i++){
        if (this.json.data.selectEntryList) {
            this.json.data.selectEntryList.each(function (json) {
                this.items.push(new MWF.xApplication.cms.QueryViewDesigner.View.Column(json, this));

            }.bind(this));
        }
        //    }
    },


    createRootItem: function() {
        this.items.push(new MWF.xApplication.process.DictionaryDesigner.Dictionary.item("ROOT", this.data.data, null, 0, this, true));
    },

    saveSilence: function(callback){
        if (!this.data.name){
            this.designer.notice(this.designer.lp.notice.inputName, "error");
            return false;
        }
        if( this.isNewView ){
            this.data.isNewView = true
        }
        this.designer.actions.saveQueryView(this.data, function(json){
            this.isNewView = false;
            this.data.id = json.data.id;
            //this.page.textNode.set("text", this.data.name);
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
            }
            if (callback) callback();
        }.bind(this));
    },
    save: function(callback){
        //if (this.designer.tab.showPage==this.page){
        if (!this.data.name){
            this.designer.notice(this.designer.lp.notice.inputName, "error");
            return false;
        }
        //}
        if( this.isNewView ){
            this.data.isNewView = true
        }
        this.designer.actions.saveQueryView(this.data, function(json){
            this.isNewView = false;
            this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});

            this.data.id = json.data.id;
            //this.page.textNode.set("text", this.data.name);
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
            }
            if (callback) callback();
        }.bind(this));
    },
    saveAs: function(){
        var form = new MWF.xApplication.cms.QueryViewDesigner.View.NewName(this, {
            name : this.data.name + "_副本"
        }, {
            onSave : function( data, callback ){
                this._saveAs( data.name, callback );
            }.bind(this)
        }, {
            app: this.designer
        });
        form.edit()
    },
    clone : function( obj ){
        if (null == obj || "object" != typeof obj) return obj;

        if ( typeof obj.length==='number'){ //数组
            //print( "array" );
            var copy = [];
            for (var i = 0, len = obj.length; i < len; ++i) {
                copy[i] = this.clone(obj[i]);
            }
            return copy;
        }else{
            var copy = {};
            for (var attr in obj) {
                copy[attr] = this.clone(obj[attr]);
            }
            return copy;
        }
    },
    _saveAs : function( name , callback){
        var _self = this;

        var d = this.clone( this.data );

        d.isNewView = true;
        d.id = this.designer.actions.getUUID();
        d.name = name;
        d.alias = "";

        delete d[this.data.id+"viewFilterType"];
        d[d.id+"viewFilterType"]="custom";

        d.data.selectEntryList.each( function( entry ){
            entry.id = (new MWF.widget.UUID).id;
        }.bind(this));

        this.designer.actions.saveQueryView(d, function(json){
            this.designer.notice(this.designer.lp.notice.saveAs_success, "success", this.node, {"x": "left", "y": "bottom"});
            if (callback) callback();
        }.bind(this));
    }
});


MWF.xApplication.cms.QueryViewDesigner.View.Column = new Class({
    Extends : MWF.xApplication.process.ViewDesigner.View.Column,
    initialize: function(json, view, next){
        this.propertyPath = "/x_component_cms_QueryViewDesigner/$View/column.html";
        this.view = view;
        this.json = json;
        this.next = next;
        this.css = this.view.css;
        this.content = this.view.viewTitleTrNode;
        this.domListNode = this.view.domListNode;
        this.load();
    },

    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.cms.QueryViewDesigner.Property(this, this.view.designer.propertyContentArea, this.view.designer, {
                "path": this.propertyPath,
                "onPostLoad": function(){
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        }else{
            this.property.show();
        }
    }

});

MWF.xApplication.cms.QueryViewDesigner.View.NewName = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "blue",
        "width": 700,
        //"height": 300,
        "height": "220",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "新数据视图名称"
    },
    _createTableContent: function () {

        var html = "<table width='80%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin: 20px auto 0px auto; '>" +
            "<tr><td styles='formTableTitle' lable='name' width='25%'></td>" +
            "    <td styles='formTableValue' item='name' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data || {}, {
                isEdited: true,
                style: "cms",
                hasColon: true,
                itemTemplate: {
                    name: {text: "名称", notEmpty: true}
                }
            }, this.app);
            this.form.load();
        }.bind(this),null, true)

    },
    ok: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            this.fireEvent("save", [data , function(){
                this.close();
            }.bind(this)])
        }
    }
});


