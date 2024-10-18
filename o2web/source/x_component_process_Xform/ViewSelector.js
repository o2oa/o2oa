MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "Button", null, false);
/** @class ViewSelector 视图选择组件。
 * @o2cn 视图选择
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var sourceText = this.form.get("fieldId"); //获取组件
 * //方法2
 * var sourceText = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.Button
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.ViewSelector = MWF.APPViewSelector =  new Class({
	Implements: [Events],
	Extends: MWF.xApplication.process.Xform.Button,
    options: {
        /**
         * 视图参数（options）已经准备好，还未加载视图时执行。可以通过this.event得到视图参数，并可修改this.event修改视图的加载。
         * @event MWF.xApplication.process.Xform.ViewSelector#beforeLoadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 视图设计已经获取，容器也已经准备好。
         * @event MWF.xApplication.process.Xform.ViewSelector#loadViewLayout
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 异步加载视图后执行。
         * @event MWF.xApplication.process.Xform.ViewSelector#loadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 选中视图中的一条记录后执行。
         * @event MWF.xApplication.process.Xform.ViewSelector#select
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 打开视图中的一条记录后执行。
         * @event MWF.xApplication.process.Xform.ViewSelector#openDocument
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "beforeLoadView", "loadView", "queryLoad", "postLoad", "select", "openDocument"]
    },

	_loadUserInterface: function(){
        var button = this.node.getElement("button");
		if (!button) button = new Element("button");
		button.inject(this.node, "after");
		this.node.destroy();
		this.node = button;
		this.node.set({
			"id": this.json.id,
			"text": this.json.name || this.json.id,
			"styles": this.form.json.buttonStyle || this.form.css.buttonStyles,
			"MWFType": this.json.type
		});
        if(this.json.recoveryStyles){
            this.node.setStyles(this.json.recoveryStyles);
        }
        if( this.json.properties ){
            this.node.set(this.json.properties );
        }
        this.node.addEvent("click", function(){
            this.selectedData = null;
            this.selectView(function(data){
                this.doResult(data);
            }.bind(this));
        }.bind(this));
	},
    doResult: function(data){
        if (this.json.result === "script"){
            this.selectedData = data;
            return (this.json.selectedScript.code) ? this.form.Macro.exec(this.json.selectedScript.code, this) : "";
        }else{
            Object.each(this.json.selectedSetValues, function(v, k){
                var value = "";
                data.each(function(d, idx){
                    Object.each(d.data, function(dv, dk){
                        if (dk===v) value = (value) ? (value+", "+dv) : dv;
                    }.bind(this));
                }.bind(this));

                var field = this.form.all[k];
                if (field){
                    field.setData(value);
                    if (value){
                        if (field.descriptionNode) field.descriptionNode.setStyle("display", "none");
                    }else{
                        if (field.descriptionNode) field.descriptionNode.setStyle("display", "block");
                    }
                }
            }.bind(this));
        }
    },

    selectCMSView: function(callback){
        var viewData = this.json.cmsViewName;
        if (viewData){
            var filter = null;
            if (this.json.filterList && this.json.filterList.length){
                filter = [];
                this.json.filterList.each(function(entry){
                    entry.value = this.form.Macro.exec(entry.code.code, this);
                    //delete entry.code;
                    filter.push(entry);
                }.bind(this));
            }
            var viewJson = {
                "application": viewData.appId,
                "viewName": viewData.name,
                "isTitle": this.json.isTitle || "yes",
                "select": this.json.select || "single",
                "titleStyles": this.json.titleStyles,
                "itemStyles": this.json.itemStyles,
                "isExpand": this.json.isExpand || "no",
                "showActionbar" : this.json.actionbar === "show",
                "filter": filter
            };
            var options = {};
            var width = options.width || "800";
            var height = options.height || "450";

            var size;
            if (layout.mobile){
                size = document.body.getSize();
                width = size.x;
                height = size.y;
                options.style = "viewmobile";
            }
            width = width.toInt();
            height = height.toInt();

            size = this.form.app.content.getSize();
            var x = (size.x-width)/2;
            var y = (size.y-height)/2;
            if (x<0) x = 0;
            if (y<0) y = 0;
            if (layout.mobile){
                x = 20;
                y = 0;
            }

            var _self = this;
            MWF.require("MWF.xDesktop.Dialog", function(){
                var dlg = new MWF.xDesktop.Dialog({
                    "title": this.json.title || "select view",
                    "style": options.style || "view",
                    "top": y,
                    "left": x-20,
                    "fromTop":y,
                    "fromLeft": x-20,
                    "width": width,
                    "height": height,
                    "html": "<div></div>",
                    "maskNode": this.form.app.content,
                    "container": this.form.app.content,
                    "buttonList": [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function(){
                                //if (callback) callback(_self.view.selectedItems);
                                if (callback) callback(_self.view.getData());
                                this.close();
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function(){this.close();}
                        }
                    ]
                });
                dlg.show();

                if (layout.mobile){
                    var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                    var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                    if (backAction) backAction.addEvent("click", function(e){
                        dlg.close();
                    }.bind(this));
                    if (okAction) okAction.addEvent("click", function(e){
                        //if (callback) callback(this.view.selectedItems);
                        if (callback) callback(this.view.getData());
                        dlg.close();
                    }.bind(this));
                }

                // MWF.xDesktop.requireApp("process.Xform", "widget.CMSView", function(){
                //     this.view = new MWF.xApplication.process.Xform.widget.CMSView(dlg.content.getFirst(), viewJson, {"style": "select"});
                // }.bind(this));

                MWF.xDesktop.requireApp("process.Application", "Viewer", function(){
                    this.view = new MWF.xApplication.process.Application.Viewer(dlg.content, viewJson, {
                        "actions": {
                            "lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
                            "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
                        },
                        "actionRoot": "x_cms_assemble_control"
                    });
                }.bind(this));
            }.bind(this));
        }
    },
    selectProcessView: function(callback){
        var viewData = this.json.processViewName;
        if (viewData){
            var filter = null;
            if (this.json.filterList && this.json.filterList.length){
                filter = [];
                this.json.filterList.each(function(entry){
                    entry.value = this.form.Macro.exec(entry.code.code, this);
                    //delete entry.code;
                    filter.push(entry);
                }.bind(this));
            }

            var viewJson = {
                "application": viewData.application,
                "viewName": viewData.name,
                "isTitle": this.json.isTitle || "yes",
                "select": this.json.select || "single",
                "titleStyles": this.json.titleStyles,
                "itemStyles": this.json.itemStyles,
                "isExpand": this.json.isExpand || "no",
                "showActionbar" : this.json.actionbar === "show",
                "filter": filter
            };
            var options = {};
            var width = options.width || "800";
            var height = options.height || "600";

            var size;
            if (layout.mobile){
                size = document.body.getSize();
                width = size.x;
                height = size.y;
                options.style = "viewmobile";
            }
            width = width.toInt();
            height = height.toInt();

            size = this.form.app.content.getSize();
            var x = (size.x-width)/2;
            var y = (size.y-height)/2;
            if (x<0) x = 0;
            if (y<0) y = 0;
            if (layout.mobile){
                x = 20;
                y = 0;
            }

            var _self = this;
            MWF.require("MWF.xDesktop.Dialog", function(){
                var dlg = new MWF.xDesktop.Dialog({
                    "title": this.json.title || "select view",
                    "style": options.style || "view",
                    "top": y,
                    "left": x-20,
                    "fromTop":y,
                    "fromLeft": x-20,
                    "width": width,
                    "height": height,
                    "html": "",
                    "maskNode": this.form.app.content,
                    "container": this.form.app.content,
                    "buttonList": [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function(){
                                //if (callback) callback(_self.view.selectedItems);
                                if (callback) callback(_self.view.getData());
                                this.close();
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function(){this.close();}
                        }
                    ]
                });
                dlg.show();

                if (layout.mobile){
                    var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                    var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                    if (backAction) backAction.addEvent("click", function(e){
                        dlg.close();
                    }.bind(this));
                    if (okAction) okAction.addEvent("click", function(e){
                        //if (callback) callback(this.view.selectedItems);
                        if (callback) callback(this.view.getData());
                        dlg.close();
                    }.bind(this));
                }

                // MWF.xDesktop.requireApp("process.Xform", "widget.View", function(){
                //     this.view = new MWF.xApplication.process.Xform.widget.View(dlg.content.getFirst(), viewJson, {"style": "select"});
                // }.bind(this));

                MWF.xDesktop.requireApp("process.Application", "Viewer", function(){
                    this.view = new MWF.xApplication.process.Application.Viewer(dlg.content, viewJson);
                }.bind(this));
            }.bind(this));
        }
    },

    selectQueryView: function(callback){
        var viewData = this.json.queryView;

        if (viewData){
            var filter = null;
            if (this.json.filterList && this.json.filterList.length){
                filter = [];
                this.json.filterList.each(function(entry){
                    entry.value = this.form.Macro.exec(entry.code.code, this);
                    //delete entry.code;
                    filter.push(entry);
                }.bind(this));
            }

            var viewJson = {
                "application": viewData.appName,
                "viewName": viewData.name,
                "viewId": viewData.id,
                "isTitle": this.json.isTitle || "yes",
                "select": this.json.select || "single",
                "titleStyles": this.json.titleStyles,
                "itemStyles": this.json.itemStyles,
                "isExpand": this.json.isExpand || "no",
                "showActionbar" : this.json.actionbar === "show",
                "filter": filter,
                "defaultSelectedScript" : this.json.defaultSelectedScript ? this.json.defaultSelectedScript.code : null,
                "selectedAbleScript" : this.json.selectedAbleScript ? this.json.selectedAbleScript.code : null
            };

            this.fireEvent("beforeLoadView", [viewJson]);

            var options = {};
            // var width = options.width || "850";
            // var height = options.height || "700";
            var width = this.json.DialogWidth || "850";
            var height = this.json.DialogHeight || "700";

            if (layout.mobile){
                var size = document.body.getSize();
                width = size.x;
                height = size.y;
                options.style = "viewmobile";
            }
            width = width.toInt();
            height = height.toInt();

            var size = this.form.app.content.getSize();
            var x = (size.x-width)/2;
            var y = (size.y-height)/2;
            if (x<0) x = 0;
            if (y<0) y = 0;
            if (layout.mobile){
                x = 20;
                y = 0;
            }

            var _self = this;
            MWF.require("MWF.xDesktop.Dialog", function(){
                var dlg = new MWF.xDesktop.Dialog({
                    "title": this.json.title || "select view",
                    "style": options.style || "view",
                    "top": y,
                    "left": x-20,
                    "fromTop":y,
                    "fromLeft": x-20,
                    "width": width,
                    "height": height,
                    "html": "",
                    "maxHeightPercent": layout.mobile ? "100%" : "98%",
                    "maskNode": layout.mobile?$(document.body) : this.form.app.content,
                    "container": layout.mobile?$(document.body) : this.form.app.content,
                    "buttonList": [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function(){
                                //if (callback) callback(_self.view.selectedItems);
                                if (callback) callback(_self.view.getData());
                                this.close();
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function(){this.close();}
                        }
                    ],
                    "onPostShow": function(){
                        if(layout.mobile){
                            dlg.node.setStyle("z-index",200);
                        }
                        MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
                            // this.view = new MWF.xApplication.query.Query.Viewer(dlg.content, viewJson, {
                            //     "style": "select"
                            // }, this.form.app, this.form.Macro );
                            this.view = new MWF.xApplication.query.Query.Viewer(dlg.content, viewJson, {
                                "style": "select",
                                "onLoadLayout": function () {
                                    this.fireEvent("loadViewLayout");
                                }.bind(this),
                                "onLoadView": function(){
                                    this.fireEvent("loadView");
                                }.bind(this),
                                "onSelect": function(item){
                                    this.fireEvent("select", [item]);
                                }.bind(this),
                                "onUnselect": function(item){
                                    this.fireEvent("unselect", [item]);
                                }.bind(this),
                                "onOpenDocument": function(options, item){
                                    this.openOptions = {
                                        "options": options,
                                        "item": item
                                    };
                                    this.fireEvent("openDocument", [this.openOptions]);
                                    this.openOptions = null;
                                }.bind(this)
                            }, this.form.app, this.form.Macro);
                        }.bind(this));
                    }.bind(this)
                });
                dlg.show();

                if (layout.mobile){
                    var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                    var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                    if (backAction) backAction.addEvent("click", function(e){
                        dlg.close();
                    }.bind(this));
                    if (okAction) okAction.addEvent("click", function(e){
                        //if (callback) callback(this.view.selectedItems);
                        if (callback) callback(this.view.getData());
                        dlg.close();
                    }.bind(this));
                }

                // MWF.xDesktop.requireApp("process.Xform", "widget.View", function(){
                //     this.view = new MWF.xApplication.process.Xform.widget.View(dlg.content.getFirst(), viewJson, {"style": "select"});
                // }.bind(this));
                // MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
                //     this.view = new MWF.xApplication.query.Query.Viewer(dlg.content, viewJson, {"style": "select"});
                // }.bind(this));
            }.bind(this));
        }
    },
    selectView: function(callback){
        if (this.json.queryView){
            this.selectQueryView(callback);
        }else{
            if (this.json.selectViewType==="cms"){
                this.selectCMSView(callback);
            }else{
                this.selectProcessView(callback);
            }
        }
    }

});
