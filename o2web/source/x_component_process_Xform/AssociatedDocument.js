MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class AssociatedDocument 视图选择组件。
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
MWF.xApplication.process.Xform.AssociatedDocument = MWF.APPAssociatedDocument =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Module,
    options: {
        /**
         * 视图参数（options）已经准备好，还未加载视图时执行。可以通过this.event得到视图参数，并可修改this.event修改视图的加载。
         * @event MWF.xApplication.process.Xform.AssociatedDocument#beforeLoadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 异步加载视图后执行。
         * @event MWF.xApplication.process.Xform.AssociatedDocument#loadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 选中视图中的一条记录后执行。可以通过this.event获取该次选择的记录。
         * @event MWF.xApplication.process.Xform.AssociatedDocument#select
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 取消选中视图中的一条记录后执行。可以通过this.event获取该次取消选择的记录。
         * @event MWF.xApplication.process.Xform.AssociatedDocument#unselect
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 点击确定后执行的事件。可以通过this.event获取选择的记录列表。
         * @event MWF.xApplication.process.Xform.AssociatedDocument#selectResult
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 选择完成后，并且整理了关联文档数据后事件。可以通过this.event获取记录列表。
         * @event MWF.xApplication.process.Xform.AssociatedDocument#afterSelectResult
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 删除关联文档前执行的事件。可以通过this.event获取删除的记录。
         * @event MWF.xApplication.process.Xform.AssociatedDocument#deleteDocument
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        // /**
        //  * 打开关联文档前执行的事件。
        //  * @event MWF.xApplication.process.Xform.AssociatedDocument#openDocument
        //  * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
        //  */
        "moduleEvents": ["load", "queryLoad", "postLoad", "beforeLoadView", "loadView", "select", "unselect", "selectResult", "afterSelectResult", "deleteDocument","openDocument"]
    },

	_loadUserInterface: function(){
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });

        this.documentList = [];

        var button = this.node.getElement("button");
        if( this.isReadonly() ){
            if( button )button.hide();
        }else{
            if (!button) button = new Element("button");
            this.button = button;
            this.button.set({
                "text": this.json.buttonText,
                "styles": this.json.buttonStyles
            });

            this.button.addEvent("click", function(){
                debugger;
                this.selectedData = null;
                this.selectView(function(data){
                    // if(data.length === 0){
                    //     this.form.notice(MWF.xApplication.process.Xform.LP.selectDocNote, "info");
                    //     return;
                    // }
                    var d = data.map(function (d) {
                        return {
                            "type": d.type === "process" ? "processPlatform" : "cms",
                            "site": this.json.site || this.json.id,
                            "view": d.view,
                            "bundle": d.bundle
                        }
                    }.bind(this));
                    this.selectDocument(d);
                }.bind(this));
            }.bind(this));
        }


        if(this.json.recoveryStyles){
            this.node.setStyles(this.json.recoveryStyles);
        }

        this.documentListNode = this.node.getElement(".MWFADContent");
        this.documentListNode.setStyles( this.json.documentListNodeStyles || {} );

        this.loadAssociatedDocument();
	},
    selectDocument: function(data){
        this.cancelAllAssociated( function () {
            if( data && data.length ){
                o2.Actions.load("x_processplatform_assemble_surface").CorrelationAction.createWithJob(this.form.businessData.work.job, {
                    targetList: data
                }, function (json) {
                    this.status = "showResult";
                    if(this.dlg.titleText)this.dlg.titleText.set("text", MWF.xApplication.process.Xform.LP.associatedResult);
                    if( layout.mobile ){
                        var okAction = this.dlg.node.getElement(".MWF_dialod_Action_ok");
                        if (okAction) okAction.hide();
                    }else{
                        var okNode = this.dlg.button.getFirst();
                        if(okNode){
                            okNode.hide();
                            var cancelButton = okNode.getNext();
                            if(cancelButton)cancelButton.set("value", o2.LP.widget.close);
                        }
                    }
                    if( (json.data.failureList && json.data.failureList.length) || (json.data.successList && json.data.successList.length)  ){
                        this.showCreateResult(json.data.failureList, json.data.successList);
                    }
                    this.loadAssociatedDocument(function () {
                        this.fireEvent("afterSelectResult", [this.documentList]);
                    }.bind(this));
                }.bind(this));
            }else{
                this.status = "showResult";
                this.loadAssociatedDocument(function () {
                    this.fireEvent("afterSelectResult", [this.documentList]);
                }.bind(this));
                if( this.dlg )this.dlg.close();
            }
        }.bind(this));
    },
    cancelAllAssociated: function( callback ){
	    var _self = this;
	    if( this.documentList.length ){
            var ids = [];
            if( this.json.reserve === false ){
                ids = this.documentList.map(function (doc) {
                    return doc.id;
                });
            }else{
                var viewIds = (this.json.queryView || []).map(function (view) {
                   return view.id;
                });
                var docs = this.documentList.filter(function (doc) {
                    return viewIds.contains( doc.view );
                });
                ids = docs.map(function (doc) {
                    return doc.id;
                });
            }
            o2.Actions.load("x_processplatform_assemble_surface").CorrelationAction.deleteWithJob(this.getBundle(), {
                idList: ids
            },function (json) {
                //this.documentList = [];
                if(callback)callback();
            }.bind(this));
        }else{
	        if(callback)callback();
        }
    },
    loadAssociatedDocument: function( callback ){
        this.documentListNode.empty();
	    o2.Actions.load("x_processplatform_assemble_surface").CorrelationAction.listWithJobWithSite(this.form.businessData.work.job, (this.json.site || this.json.id), function (json) {
            this.documentList = json.data;
            this.showDocumentList();
            if(callback)callback();
        }.bind(this));
    },
    showCreateResult: function(failureList, successList){
	    this.viewList.each(function (view) {
            view.showAssociatedDocumentResult(failureList, successList);
        })
    },
    showDocumentList: function(){
        this.documentList.each(function(d){
            if(d.targetCreatorPerson)d.targetCreatorPersonCn = d.targetCreatorPerson.split("@")[0];
        })
        this.documentListNode.empty();
	    switch (this.json.mode) {
            case "text":
                this.loadDocumentListText();  break;
            case "script":
                this.loadDocumentListScript();  break;
            case "default":
            default:
                this.loadDocumentListDefault();  break;
        }
    },
    loadDocumentListDefault: function(){
        this.documentList.each(function (d) {
            var itemNode = new Element("div", {
                styles:  this.form.css.associatedDocumentItem
            }).inject( this.documentListNode );
            var iconNode = new Element("div", {
                styles:  this.form.css[ d.targetType === "processPlatform" ? "associatedDocumentWorkIcon" : "associatedDocumentCmsIcon" ]
            }).inject( itemNode );

            var deleteNode;
            if( !this.isReadonly() ){
                deleteNode = new Element("div", {
                    styles:  this.form.css.associatedDocumentDelete
                }).inject( itemNode );
                if(!layout.mobile)deleteNode.hide();
            }

            var textNode = new Element("div", {
                styles:  this.form.css.associatedDocumentText,
                text: d.targetTitle
            }).inject( itemNode );
            this._loadDocument(d, itemNode, deleteNode)
        }.bind(this))
    },
    loadDocumentListText: function(){
        var lp = MWF.xApplication.process.Xform.LP;
        this.documentList.each(function (d) {
            var html = this.json.textStyle;
            if (this.json.textStyleScript && this.json.textStyleScript.code) {
                this.form.Macro.environment.line = d;
                html = this.form.Macro.exec(this.json.textStyleScript.code, this);
            }
            html = html.replace(/\{targetTitle\}/g, o2.txt(d.targetTitle));
            html = html.replace(/\{targetStartTime\}/g, (d.targetType === "processPlatform") ? d.targetStartTime : d.targetStartTime);
            html = html.replace(/\{targetCreatorPersonCn\}/g, o2.txt((d.targetType === "processPlatform") ? d.targetCreatorPersonCn : d.targetCreatorPersonCn));
            html = html.replace(/\{targetType\}/g, o2.txt((d.targetType === "processPlatform") ? lp.work : lp.document));
            html = html.replace(/\{targetCategory\}/g, o2.txt((d.targetType === "processPlatform") ? d.targetCategory : d.targetCategory));
            var itemNode = new Element("div", {
                styles:  this.form.css.associatedDocumentItem,
                html: html
            }).inject(this.documentListNode);
            var deleteNode = itemNode.getElement("[data-o2-action='delete']");
            if(!layout.mobile)deleteNode.hide();
            this._loadDocument(d, itemNode, deleteNode);
        }.bind(this))
    },
    loadDocumentListScript: function(){
        if (this.json.displayScript && this.json.displayScript.code){
            var code = this.json.displayScript.code;
            this.documentList.each(function(d){
                var itemNode = new Element("div", {
                    styles:  this.form.css.associatedDocumentItem,
                }).inject(this.documentListNode);

                this.form.Macro.environment.line = d;
                var r = this.form.Macro.exec(code, this);
                var t = o2.typeOf(r);
                if (t==="string"){
                    itemNode.set("html", r);
                }else if (t==="element"){
                    r.inject(itemNode);
                }
                var deleteNode = itemNode.getElement("[data-o2-action='delete']");
                deleteNode.hide();
                this._loadDocument(d, itemNode, deleteNode);
            }.bind(this));
        }
    },
    _loadDocument: function(d, itemNode, deleteNode){
	    if( layout.mobile ){
            itemNode.addEvents({
                "click": function (e) {
                    this.openDoc(e, d);
                }.bind(this),
            });
        }else{
            itemNode.addEvents({
                "mouseover": function () {
                    if(deleteNode)deleteNode.show();
                    itemNode.setStyles( this.form.css.associatedDocumentItem_over )
                }.bind(this),
                "mouseout": function () {
                    if(deleteNode)deleteNode.hide();
                    itemNode.setStyles( this.form.css.associatedDocumentItem )
                }.bind(this),
                "click": function (e) {
                    this.openDoc(e, d);
                }.bind(this),
            });
        }
        if( deleteNode ){
            if( !this.isReadonly() ){
                deleteNode.addEvents({
                    "click": function (ev) {
                        this.cancelAssociated(ev, d, itemNode);
                        ev.stopPropagation();
                    }.bind(this)
                });
            }else{
                deleteNode.hide();
            }
        }
        if( this.json.showCard !== false ){
            this.createInforNode( itemNode, d );
        }
    },
    cancelAssociated: function(e, d, itemNode){
        var lp = MWF.xApplication.process.Xform.LP;
        var _self = this;
        this.form.confirm("warn", e, lp.cancelAssociatedTitle, lp.cancelAssociated.replace("{title}", o2.txt(d.targetTitle)), 370, 120, function () {
            _self.fireEvent("deleteDocument", [d]);
            o2.Actions.load("x_processplatform_assemble_surface").CorrelationAction.deleteWithJob(_self.form.businessData.work.job, {
                idList: [d.id]
            },function (json) {
                itemNode.destroy();
                _self.documentList.erase(d);
                this.close();
                //this.showDocumentList();
            }.bind(this));
        }, function () {
            this.close();
        }, null, null, this.form.json.confirmStyle);
    },
    createInforNode: function(itemNode, d){
        var lp = MWF.xApplication.process.Xform.LP;
        var inforNode = new Element("div");
        var html = "";
        var lineStyle = "clear: both; overflow:hidden";
        var titleStyle = "width:60px; float:left; font-weight: bold";
        var contentStyle = "width:120px; float:left; margin-left:10px";
        if( d.targetType === "processPlatform" ){
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.documentType+": </div><div style='"+contentStyle+"'>"+lp.work+"</div></div>";
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.processName+": </div><div style='"+contentStyle+"'>"+d.targetCategory+"</div></div>";
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.draftPerson +": </div><div style='"+contentStyle+"'>"+d.targetCreatorPersonCn+"</div></div>";
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.draftTime +": </div><div style='"+contentStyle+"'>"+d.targetStartTime+"</div></div>";
        }else{
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.documentType+": </div><div style='"+contentStyle+"'>"+lp.document+"</div></div>";
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.categoryName+": </div><div style='"+contentStyle+"'>"+d.targetCategory+"</div></div>";
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.publishPerson+": </div><div style='"+contentStyle+"'>"+d.targetCreatorPersonCn+"</div></div>";
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.publishTime+": </div><div style='"+contentStyle+"'>"+d.targetStartTime+"</div></div>";
        }

        inforNode.set("html", html);

        if (!layout.mobile){
            this.tooltip = new mBox.Tooltip({
                content: inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: itemNode,
                transition: 'flyin'
            });
        }
    },
    getBundle: function(){
	   return this.form.businessData.work.job;
    },
    selectView: function(callback){
        this.status = "select";
        var viewDataList = this.json.queryView;
        if( !viewDataList )return;
        viewDataList = typeOf(viewDataList) === "array" ? viewDataList : [viewDataList];
        if (viewDataList.length){

            var selectedJobs = this.documentList.map(function (d) {
                return d.targetBundle;
            });

            var disableSelectJobs = [];
            //var disableSelectJobs = Array.clone(selectedJobs);
            disableSelectJobs.push( this.getBundle() );

            debugger;

            var viewJsonList = [];

            this.selectedBundleMap = {};
            this.documentList.each(function (d) {
                var viewid = d.properties.view;
                if( !this.selectedBundleMap[viewid] )this.selectedBundleMap[viewid] = [];
                this.selectedBundleMap[viewid].push( d.targetBundle );
            }.bind(this));

            viewDataList.each(function (viewData) {
                var filter = null;
                var filterList = (this.json.viewFilterScriptList || []).filter(function (f) {
                    return f.id === viewData.id;
                });
                if( filterList.length ){
                    filter = this.form.Macro.exec(filterList[0].script.code, this);
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
                    //"defaultSelectedScript" : function (obj) {
                    //    return selectedJobs.contains(obj.data.bundle);
                    //},
                    "selectedAbleScript" : function (obj) {
                        return !disableSelectJobs.contains(obj.data.bundle);
                    }
                };
                viewJsonList.push( viewJson );
            }.bind(this));
            this.fireEvent("beforeLoadView", [viewDataList]);

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
                    "title": this.json.title || MWF.xApplication.process.Xform.LP.associatedDocument,
                    "style": options.style || "view",
                    "top": y,
                    "left": x-20,
                    "fromTop":y,
                    "fromLeft": x-20,
                    "width": width,
                    "height": height,
                    "html": "",
                    "maskNode": layout.mobile?$(document.body) : this.form.app.content,
                    "container": layout.mobile?$(document.body) : this.form.app.content,
                    "buttonList": [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function(){
                                //if (callback) callback(_self.view.selectedItems);

                                _self.afterSelectView( callback, dlg );
                                //this.close();
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function(){this.close();}
                        }
                    ],
                    "onQueryClose": function () {
                        this.dlg = null;
                    }.bind(this),
                    "onPostShow": function(){
                        if(layout.mobile){
                            dlg.node.setStyle("z-index",200);
                        }

                        MWF.require("MWF.widget.Tab", null, false);

                        this.tab = new MWF.widget.Tab(dlg.content, {"style": "script"});
                        this.tab.load();

                        MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
                            // this.view = new MWF.xApplication.query.Query.Viewer(dlg.content, viewJson, {
                            //     "style": "select"
                            // }, this.form.app, this.form.Macro );

                            this.viewList = [];
                            viewJsonList.each(function (viewJson, index) {
                                var tabViewNode = Element("div", {"styles": {"height": "100%"}});
                                var pageViewNode = new Element("div.pageViewNode").inject(tabViewNode);

                                var viewPage = this.tab.addTab(tabViewNode, viewJson.viewName);

                                var selectedBundles = this.selectedBundleMap[ viewJson.viewId ] || [];

                                //this.viewPage.showTabIm();
                                var viewHeight = dlg.content.getSize().y - this.tab.tabNodeContainer.getSize().y - 1;

                                pageViewNode.setStyle("height", viewHeight);

                                debugger;

                                var view = new MWF.xApplication.query.Query.Viewer(pageViewNode, viewJson, {
                                    "isloadContent": this.status !== "showResult",
                                    "isloadActionbar": this.status !== "showResult",
                                    "isloadSearchbar": this.status !== "showResult",
                                    "style": "select",
                                    "defaultBundles": this.selectedBundleMap[viewJson.viewId] || [],
                                    "onLoadView": function(){
                                        this.fireEvent("loadView");
                                    }.bind(this),
                                    "onSelect": function(item){
                                        this.fireEvent("select", [item]);
                                    }.bind(this),
                                    "onUnselect": function(item){
                                        selectedBundles.erase( item.data.bundle );
                                        this.fireEvent("unselect", [item]);
                                    }.bind(this),
                                    "onOpenDocument": function(options, item){
                                        this.openOptions = {
                                            "options": options,
                                            "item": item
                                        };
                                        this.fireEvent("openViewDocument", [this.openOptions]);
                                        this.openOptions = null;
                                    }.bind(this)
                                }, this.form.app, this.form.Macro);

                                viewPage.Viewer = view;
                                this.viewList.push(view);

                                viewPage.addEvent("postShow", function () {
                                    if( viewPage.Viewer && viewPage.Viewer.node ){
                                        viewPage.Viewer.setContentHeight();
                                    }
                                    // var viewHeight = dlg.content.getSize().y - this.tab.tabNodeContainer.getSize().y;
                                    // pageViewNode.setStyle("height", viewHeight);
                                }.bind(this));

                                if( index === 0 )viewPage.showTabIm();

                            }.bind(this));


                        }.bind(this));
                    }.bind(this)
                });
                this.dlg = dlg;
                dlg.show();

                if (layout.mobile){
                    if(dlg.title)dlg.title.addClass("mainColor_color");
                    var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                    var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                    if (backAction) backAction.addEvent("click", function(e){
                        dlg.close();
                    }.bind(this));
                    if (okAction) okAction.addEvent("click", function(e){
                        //if (callback) callback(this.view.getData());
                        _self.afterSelectView( callback, dlg );
                        //dlg.close();
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
    afterSelectView: function( callback, dlg ){
        var array = [];
        this.viewList.each(function (view) {

            var data = view.getData().map(function (d) {
                d.type = view.json.type;
                d.view = view.json.id;
                return d;
            }.bind(this));
            array = array.concat(data);
        }.bind(this));

        this.fireEvent("selectResult", [array]);
        if (callback) callback(array, dlg );
    },
    openDoc: function(e, d){
	    if( d.targetType === "processPlatform" ){
            o2.Actions.load("x_processplatform_assemble_surface").JobAction.findWorkWorkCompleted(d.targetBundle, function( json ){
                var workCompletedList = json.data.workCompletedList || [], workList = json.data.workList || [];
                if( !workCompletedList.length && !workList.length ){
                    this.form.notice(MWF.xApplication.process.Xform.LP.docDeleted, "info");
                }else{
                    this.form.Macro.environment.form.openJob(d.targetBundle, null, null, function ( app ) {
                        this.fireEvent("openDocument", [app]); //options 传入的事件
                    }.bind(this));
                }
            }.bind(this));
        }else{
            o2.Actions.load("x_cms_assemble_control").DocumentAction.query_get(d.targetBundle, function(){
                this.form.Macro.environment.form.openDocument(d.targetBundle);
            }.bind(this), function(){
                this.form.notice(MWF.xApplication.process.Xform.LP.docDeleted, "info");
                return true;
            }.bind(this))
        }
    }
	
}); 
