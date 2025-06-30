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
        /**
         * 删除关联文档后执行的事件。可以通过this.event获取删除的记录。
         * @event MWF.xApplication.process.Xform.AssociatedDocument#afterDeleteDocument
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        // /**
        //  * 打开关联文档前执行的事件。
        //  * @event MWF.xApplication.process.Xform.AssociatedDocument#openDocument
        //  * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
        //  */
        "moduleEvents": ["load", "queryLoad", "postLoad", "beforeLoadView", "loadView", "select", "unselect", "selectResult",
            "afterSelectResult", "deleteDocument","afterDeleteDocument","openDocument"]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);

        this.json = json;

        this.form = form;

        this.parentLine = null;
        //只是为了校验
        this.field = true;
        this.fieldModuleLoaded = false;
    },
	_loadUserInterface: function(){

        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });

        /**
         * @summary 当前组件关联的文档。
         * @member documentList {Array<Object>}
         * @memberOf MWF.xApplication.process.Xform.AssociatedDocument
         * @example
         *  //可以在脚本中获取该组件
         * var documentList = this.form.get("fieldId").documentList; //当前组件关联的文档
         */
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

            if( this.json.style === 'v10' ){
                this.button.addClass('form-content-button');
            }
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
                    if( this.dlg ){
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
                    }else if(this.dlg_mobile){
                        var toolbar = this.dlg_mobile.contentNode.querySelector('.mwf_selectView_action');
                        if(toolbar){
                            toolbar.querySelectorAll('oo-button').forEach((btn)=> !btn.hasClass('hide') && btn.addClass('hide'));
                            toolbar.querySelector('.mwf_selectView_action_close').removeClass('hide');
                        }
                    }

                    if( (json.data.failureList && json.data.failureList.length) || (json.data.successList && json.data.successList.length)  ){
                        this.showCreateResult(json.data.failureList, json.data.successList);
                    }
                    this.loadAssociatedDocument(function () {
                        this.fireEvent("afterSelectResult", [this.documentList]);
                        this.validationMode();
                    }.bind(this));
                }.bind(this));
            }else{
                this.status = "showResult";
                this.loadAssociatedDocument(function () {
                    this.fireEvent("afterSelectResult", [this.documentList]);
                    this.validationMode();
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
        if( this.json.style === 'v10' ){
            this.table = new Element('table', {
                "style": "width: 100%;border-collapse:collapse;",
                "border": "0",
                "cellPadding": "2",
                "cellSpacing": "0"
            }).inject(this.documentListNode);

            var lp = MWF.xApplication.process.Xform.LP;

            var hasCMS = this.documentList.some(function (d) { return d.targetType !== "processPlatform"; });
            var hasProcess = this.documentList.some(function (d) { return d.targetType === "processPlatform"; });

            var headerNode = new Element("tr").inject(this.table);
            var titles = [];
            var titlesWidth = ['', '', '', '6rem', '6rem', '20rem', ''];
            if( hasCMS && hasProcess ){
                titles = ['', lp.title, lp.documentType,
                    lp.processName+"/"+lp.categoryName,
                    lp.draftPerson+"/"+lp.publishPerson,
                    lp.draftTime+"/"+lp.publishTime, '' ];
            }else if(hasCMS){
                titles = ['', lp.title, lp.categoryName, lp.documentType, lp.publishPerson, lp.publishTime, '' ];
            }else if( hasProcess ){
                titles = ['', lp.title, lp.processName, lp.documentType, lp.draftPerson, lp.draftTime, ''];
            }
            titles.each(function (title, i){
                var th = new Element("th", {text: title}).inject(headerNode);
                if( titlesWidth[i] ){
                    th.setStyle('width', titlesWidth[i]);
                }
            })

            this.documentList.each(function (d) {
                this.loadDocumentListDefault_V10(d);
            }.bind(this));
        }else{
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
        }
    },
    loadDocumentListDefault_V10: function (d){
        var lp = MWF.xApplication.process.Xform.LP;
        var itemNode = new Element("tr").inject( this.table );
        var iconNode, textNode ,typeNode, categoryNode, personNode, timeNode;

        if( d.targetType === "processPlatform" ){
             iconNode = new Element("td.process-icon.ooicon-liucheng").inject(itemNode);
             textNode = new Element("td", {text: d.targetTitle}).inject(itemNode);
             categoryNode = new Element("td", {text: d.targetCategory}).inject(itemNode);
             typeNode = new Element("td", {text: lp.work}).inject(itemNode);
             personNode = new Element("td", {text: d.targetCreatorPersonCn}).inject(itemNode);
             timeNode = new Element("td", {text: d.targetStartTime}).inject(itemNode);
        }else{
            iconNode = new Element("td.doc-icon.ooicon-doc-cooperation").inject(itemNode);
            textNode = new Element("td", {text: d.targetTitle}).inject(itemNode);
            categoryNode = new Element("td", {text: d.targetCategory}).inject(itemNode);
            typeNode = new Element("td", {text: lp.document}).inject(itemNode);
            personNode = new Element("td", {text: d.targetCreatorPersonCn}).inject(itemNode);
            timeNode = new Element("td", {text: d.targetStartTime}).inject(itemNode);
        }
        var deleteNode = new Element("td.ooicon-delete").inject(itemNode);
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

        itemNode.addEvent('click', function (e) { this.openDoc(e, d); }.bind(this));
        itemNode.addEvent('mouseover', function (e) { itemNode.addClass(); }.bind(this));
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
                _self.fireEvent("afterDeleteDocument", [d]);
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

            var viewJsonList = [];
            this.viewJsonList = viewJsonList;

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

            if (layout.mobile && o2.version.dev===10){
                this.selectViewMobile(callback);
            }else{
                this.selectViewPc(callback);
            }

        }
    },
    loadViewers: function(viewNode, dlg){


        MWF.require("MWF.widget.Tab", null, false);

        this.viewTabArea = new Element('div.tabTitleArea').inject(viewNode);
        this.viewContentNode = new Element('div.tabContentArea').inject(viewNode);

        this.tab = new MWF.widget.Tab(this.viewTabArea, {
            "style": layout.mobile && o2.version.dev === 10 ? 'v10_mobile' : "script"
        });
        this.tab.load();
        this.tab.contentNodeContainer.inject(this.viewContentNode)

        // if( layout.mobile && o2.version.dev === 10 ){
        //     this.viewTabArea.setStyles({
        //         "border-top": "1px solid #ccc",
        //         "border-bottom": "0.624rem solid rgb(247, 247, 247)"
        //     });
        // }

        MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
            // this.view = new MWF.xApplication.query.Query.Viewer(dlg.content, viewJson, {
            //     "style": "select"
            // }, this.form.app, this.form.Macro );

            this.viewList = [];
            this.viewJsonList.each(function (viewJson, index) {
                var tabViewNode = Element("div", {"styles": {"height": "100%"}});
                var pageViewNode = new Element("div.pageViewNode").inject(tabViewNode);

                var viewPage = this.tab.addTab(tabViewNode, viewJson.viewName);

                var selectedBundles = this.selectedBundleMap[ viewJson.viewId ] || [];
                if( layout.mobile && o2.version.dev === 10 ){
                    pageViewNode.setStyle("height", '100%');
                }else{
                    var viewHeight = dlg.content.getSize().y - this.tab.tabNodeContainer.getSize().y - 1;
                    pageViewNode.setStyle("height", viewHeight);
                }

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
                }, this.form.app, this.form.Macro)

                if( layout.mobile && o2.version.dev === 10 ){
                    view.addEvent('selectRow', (row)=>{
                        row.node.addClass('selectedRow');
                    });
                    view.addEvent('unselectRow', (row)=>{
                        row.node.removeClass('selectedRow');
                    });
                }

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
    },
    selectViewPc: function(callback){
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
                "style": options.style || "v10_view",
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
                    this.loadViewers(dlg.content, dlg);
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
        }.bind(this));
    },
    selectViewMobile: function (callback){
        var _renderViewContainerMobile = this.form.Macro.environment._renderViewContainerMobile;
        var viewer = null;
        _renderViewContainerMobile(
            this.json.title || MWF.xApplication.process.Xform.LP.associatedDocument,
            (viewNode, o)=>{
                this.loadViewers(viewNode);
                this.dlg_mobile = o;
                // MWF.xDesktop.requireApp("query.Query", "Viewer", ()=>{
                //     viewer = new MWF.xApplication.query.Query.Viewer(viewNode, viewJson, viewOptions, _form.app, _form.Macro);
                //     viewer.addEvent('selectRow', (row)=>{
                //         row.node.addClass('selectedRow');
                //     });
                //     viewer.addEvent('unselectRow', (row)=>{
                //         row.node.removeClass('selectedRow');
                //     });
                // });
            },
            ()=>{
                this.afterSelectView( callback );
            },
            true
        );
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

        this.doResult(array);

        this.fireEvent("selectResult", [array]);
        if (callback) callback(array, dlg );
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
    },

    setData: function(){},
    getData: function(){
        return this.documentList;
    },
    getInputData: function (){
        return this.documentList;
    },
    createErrorNode: function(text){
        var node;
        if( this.form.json.errorStyle ){
            if( this.form.json.errorStyle.type === "notice" ){
                if( !this.form.errorNoticing ){ //如果是弹出
                    this.form.errorNoticing = true;
                    this.form.notice(text, "error", this.node, null, null, {
                        onClose : function () {
                            this.form.errorNoticing = false;
                        }.bind(this)
                    });
                }
            }else{
                node = new Element("div",{
                    "styles" : this.form.json.errorStyle.node,
                    "text": text
                });
                if( this.form.json.errorStyle.close ){
                    var closeNode = new Element("div",{
                        "styles" : this.form.json.errorStyle.close ,
                        "events": {
                            "click" : function(){
                                //this.destroy();
                                this.validationMode();
                            }.bind(this)
                        }
                    }).inject(node);
                }
            }
        }else{
            node = new Element("div");
            var iconNode = new Element("div", {
                "styles": {
                    "width": "20px",
                    "height": "20px",
                    "float": "left",
                    "background": "url("+"../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
                }
            }).inject(node);
            var textNode = new Element("div", {
                "styles": {
                    "height": "auto",
                    "line-height": "20px",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "keep-all"
                },
                "text": text
            }).inject(node);
        }
        return node;
    },
    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border-color", "red");

            this.errNode = this.createErrorNode(text);
            //if (this.iconNode){
            //    this.errNode.inject(this.iconNode, "after");
            //}else{
            this.errNode.inject(this.node, "after");
            //}
            this.showNotValidationMode(this.node);

            var parentNode = this.errNode;
            while( parentNode && parentNode.offsetParent === null ){
                parentNode = parentNode.getParent();
            }

            if ( parentNode && !parentNode.isIntoView()) parentNode.scrollIntoView(false);
        }
    },
    showNotValidationMode: function(node){
        var p = node.getParent("div");
        if (p){
            var mwftype = p.get("MWFtype") || p.get("mwftype");
            if (mwftype == "tab$Content"){
                if (p.getParent("div").getStyle("display")=="none"){
                    var contentAreaNode = p.getParent("div").getParent("div");
                    var tabAreaNode = contentAreaNode.getPrevious("div");
                    var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
                    var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
                    tabNode.click();
                    p = tabAreaNode.getParent("div");
                }
            }
            this.showNotValidationMode(p);
        }
    },
    validationMode: function(){
        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("borderStyle"));
            if (this.errNode){
                this.errNode.destroy();
                this.errNode = null;
            }
        }
    },
    validationConfigItem: function(routeName, data){
        var flag = (data.status==="all") ? true: (routeName === data.decision);
        if (flag){
            var n = this.getInputData();
            var v = (data.valueType==="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if (!v || (o2.typeOf(v)==="array" && !v.length)){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    },
    validationConfig: function(routeName, opinion){
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var data = this.json.validationConfig[i];
                    if (!this.validationConfigItem(routeName, data)) return false;
                }
            }
            return true;
        }
        return true;
    },

});
