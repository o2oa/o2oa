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
MWF.xApplication.process.Xform.AssociatedDocument = MWF.APPAssociatedDocument =  new Class(
    /** @lends MWF.xApplication.process.Xform.AssociatedDocument# */
    {
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

        this.MIN_VIEW_CONTENT_HEIGHT = 300;
        this.MIN_SELECTED_AREA_HEIGHT = 200;
        switch (this.json.mode) {
            case "text":
            case "script":
                this.MIN_SELECTED_AREA_WIDTH = 500;
                break;
            case "default":
            default:
                this.MIN_SELECTED_AREA_WIDTH = this.json.style === 'v10' ?  760 : 500;
                break;
        }
    },
	_loadUserInterface: function(){

        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

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
                    this.dlg?.close();
                    // var d = data.map(function (d) {
                    //     return {
                    //         "type": d.type === "process" ? "processPlatform" : "cms",
                    //         "site": this.json.site || this.json.id,
                    //         "view": d.view,
                    //         "bundle": d.bundle
                    //     }
                    // }.bind(this));
                    // this.selectDocument(d);
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
    _updateAssociation: function (data, callback, async){
        var result;
        var p = o2.Actions.load("x_processplatform_assemble_surface").CorrelationAction.updateWithJob(
            this.getBundle(),
            this._parseUpdateDate(data),
            (json)=>{
                result = json;
                if(callback)callback(json);
                return json;
            },
            null,
            async !== false
        );
        return async === false ? result : p;
    },
    _createAssociation: function( data, async ){
        var result;
        var p = o2.Actions.load("x_processplatform_assemble_surface").CorrelationAction.createWithJob(
            this.getBundle(),
            { targetList: data },
            (json)=>{
                result = json;
                return json;
            },
            null,
            async !== false
        );
        return async === false ? result : p;
    },
    _cancelAssociated: function(ids, async){
        var result;
        var p =  o2.Actions.load("x_processplatform_assemble_surface").CorrelationAction.deleteWithJob(
            this.getBundle(),
            { idList: ids },
            (json)=>{
                result = json;
                return json;
            },
            null,
            async !== false
        );
        return async === false ? result : p;
    },
    _listAllAssociated: function(async, callback){
        var result;
        var p = o2.Actions.load("x_processplatform_assemble_surface").CorrelationAction.listWithJobWithSite(
            this.getBundle(),
            this.json.site || this.json.id,
            (json)=>{
                result = json;
                if(callback)callback(json);
                return json;
            },
            null,
            async !== false
        );
        return async === false ? result : p;
    },
    _parseUpdateDate: function(data){
          return {
              siteTargetList: [
                  {
                      site: this.json.site || this.json.id,
                      targetList: this._parseData(data)
                  }
              ]
          };
    },
    _parseData: function(data){
        !data && (data = []);
        o2.typeOf(data) !== 'array' && (data = [data]);
        return data.map(function(d){
            var obj = {
                "site": this.json.site || this.json.id,
                "view": d.view,
                "bundle": d.bundle
            };
            if(!d.type){
                obj.type = 'processPlatform';
            }else if( ["processPlatform", "cms"].includes(d.type) ){
                obj.type = d.type;
            }else{
                obj.type = d.type === "process" ? "processPlatform" : "cms";
            }
            return obj;
        }.bind(this));
    },
    getLp: function(){
        return MWF.xApplication.process.Xform.LP;
    },
    /**
     * @summary 设置关联文档，清空所有的已关联文档后再关联。
     * @param data{Object[]} .
     * @param [callback] {Function}
     * @param [async] {Boolean} 是否异步执行，默认为异步。如果组件未加载完成的时候执行该方法，强制为异步。
     * @return {Promise}
     * @example
     *  this.form.get("fieldId").set([{
     *      type: 'processPlatform', //类型，processPlatform表示流程，cms表示内容管理，如果为空默认为流程
     *      bundle: '', //流程实例的 job, 或者是内容管理文档的id
     *      view: '' //视图的id
     *  }], function(json){
     *      //json.data.failureList 关联失败列表
     *      //json.data.successList 关联成功列表
     *  }, true);
     * @example
     *  var p = this.form.get("fieldId").set([{
     *      type: 'processPlatform',
     *      bundle: '',
     *      view: ''
     *  }]);
     *  p.then(function(json){
     *      //json.data.failureList 关联失败列表
     *      //json.data.successList 关联成功列表
     *  })
     *  @example
     *  this.form.get('fieldId').set([])取消所有关联
     */
    set: function(data, callback, async){
        var after = function (json){
            if( json.data.failureList && json.data.failureList.length ){
                this.form.notice(this.getLp().associatedFailureMessage.replace('{count}', json.data.failureList.length), 'error');
            }
            return this.loadAssociatedDocument(()=>{
                this.validationMode();
                !!callback && callback(json);
                return json;
            }, async);
        }.bind(this);
        data = this._parseData(data);
        async = async !== false;
        var execute = function(){
            if( async ){
                // return Promise.resolve(
                //     this.cancelAllAssociated( null, true, true )
                // ).then(function(json){
                //     var p = !!data.length ? this._createAssociation(data) : {data: {}};
                //     return Promise.resolve(p).then( after );
                // }.bind(this));
                var p = this._updateAssociation(data);
                return Promise.resolve(p).then( (json)=>{
                    return after(json);
                });
            }else{
                // this.cancelAllAssociated( null, false, true);
                // var json = !!data.length ? this._createAssociation(data, false) : {data: {}};
                // after(json);
                // return json;
                var json = this._updateAssociation(data, callback, false);
                return after(json);
            }
        }.bind(this);

        return this.listPromise ? this.listPromise.then( execute ) : execute();
    },
    /**
     * @summary 根据传入的数据添加关联文档。
     * @param data {Object[]}
     * @param [toTop]{Boolean} 是否添加到已有文档前面，默认为否。
     * @param [keepOrder]{Boolean} 如果添加的已经存在，是否保留原有位置, 默认不保留。
     * @param [callback] {Function}
     * @param [async]{Boolean} 是否为异步，默认为异步。如果组件未加载完成的时候执行该方法，强制为异步。
     * @return {Promise|Object}
     * @example
     *  this.form.get("fieldId").add([{
     *      type: 'processPlatform', //类型，processPlatform表示流程，cms表示内容管理，如果为空默认为流程
     *      bundle: '', //流程实例的 job, 或者是内容管理文档的id
     *      view: '' //视图的id
     *  }],
     *  true,
     *  true,
     *  function(json){
     *      //json.data.failureList 关联失败列表
     *      //json.data.successList 关联成功列表
     *  }, true);
     * @example
     *  var p = this.form.get("fieldId").add([{
     *      type: 'processPlatform',
     *      bundle: '',
     *      view: ''
     *  }]);
     *  p.then(function(json){
     *      //json.data.failureList 关联失败列表
     *      //json.data.successList 关联成功列表
     *  })
     */
    add: function(data, toTop, keepOrder, callback, async){
        debugger;
        var execute = function(){
            data = this._parseData(data);
            var remains = this.documentList.map(function(d){
                return {
                    type: d.targetType,
                    bundle: d.targetBundle,
                    view: d.view
                };
            });
            var filter = (arr1, arr2) => {
                var bundles = arr1.map(function(d){ return d.bundle; });
                arr2 = arr2.filter(function(d){
                    return !bundles.contains( d.bundle );
                });
                return arr2;
            };
            if( !!keepOrder ){
                data = filter(remains, data);
            }else{
                remains = filter(data, remains);
            }
            data = !toTop ? remains.concat(data) : data.concat(remains);
            return this.set(data, callback, async);
        }.bind(this);

        return this.listPromise ? this.listPromise.then( execute ) : execute();
    },
    /**
     * @summary 取消指定的关联。
     * @param bundles {String[]}
     * @param [callback] {Function}
     * @param [async]{Boolean} 是否为异步，默认为异步。如果组件未加载完成的时候执行该方法，强制为异步。
     * @return {Promise}
     * @example
     *  this.form.get("fieldId").cancel([id1, id2]);
     */
    cancel: function(bundles, callback, async){
        var execute = function(){
            var remains = this.documentList.filter(function(d){
                return !bundles.contains(d.targetBundle);
            }).map(function(d){
                return {
                    type: d.targetType,
                    bundle: d.targetBundle,
                    view: d.view
                };
            });
            return this.set(remains, callback, async);
        }.bind(this);

        return this.listPromise ? this.listPromise.then( execute ) : execute();
    },
    /**
     * @summary 得到关联的列表。
     * @return {Promise|Object[]} 如果组件未加载完成的时候执行该方法，返回promise，否则返回对象数组。
     * <pre>
     * [
     *     {
     *         "site": "associatedDocument", //组件site，如果没有设置，则为组件id
     *         "type": "cms", //类型，processPlatform表示流程，cms表示内容管理
     *         "bundle": "909b78e1-3ec2-4c63-b756-200ff734c318", //流程实例的 job, 或者是内容管理文档的id
     *         "view": "b5bd7fae-239e-4cab-aa34-8c63350d9e97" //视图的id
     *     }
     * ]
     * </pre>
     * @example
     * var documentList = this.form.get('fieldId').get();
     * @example
     * var documentList = Promise.resolve( this.form.get('fieldId').get() )
     */
    get: function(){
        var execute = function(){
            return this.documentList.map(function(d){
                return {
                    site: d.site || this.json.site || this.json.id,
                    type: d.targetType,
                    bundle: d.targetBundle,
                    view: d.view
                };
            });
        }.bind(this);

        return this.listPromise ? this.listPromise.then( execute ) : execute();
    },
    getData: function(){
        return this.documentList;
    },
    selectDocument: function(data){
        this.cancelAllAssociated( function () {
            if( data && data.length ){
                var p = this._createAssociation(data);
                p.then(function (json){
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
    cancelAllAssociated: function( callback, async, force ){
	    var _self = this;
	    if( this.documentList.length ){
            var ids = [];
            if( !!force || this.json.reserve === false ){
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
            var p = this._cancelAssociated(ids, async);
            return p.then(function (json) {
                !!callback && callback();
            }.bind(this));
        }else{
            !!callback && callback();
            return Promise.resolve();
        }
    },
    loadAssociatedDocument: function( callback, async ){
        this.documentListNode.empty();
        var doCallback = (json)=>{
            this.documentList = json.data;
            this.showDocumentList();
            !!this.listPromise && (this.listPromise = null);
            if( !!callback ){
                return callback();
            }
        };
        if( async === false ){
            var json;
            this._listAllAssociated(false, (result)=>{json=result});
            return doCallback(json);
        }else{
            var p = this._listAllAssociated();
            this.listPromise = p;
            return p.then(function (json) {
                return doCallback(json);
            }.bind(this));
        }
    },
    showCreateResult: function(failureList, successList){
	    this.viewList.each(function (view) {
            view.showAssociatedDocumentResult(failureList, successList);
        });
    },
    // showDocumentList: function(array){
    //     this.documentList.each(function(d){
    //         if(d.targetCreatorPerson)d.targetCreatorPersonCn = d.targetCreatorPerson.split("@")[0];
    //     })
    //     this.documentListNode.empty();
	//     switch (this.json.mode) {
    //         case "text":
    //             this.loadDocumentListText();  break;
    //         case "script":
    //             this.loadDocumentListScript();  break;
    //         case "default":
    //         default:
    //             this.loadDocumentListDefault();  break;
    //     }
    // },
    showDocumentList: function(array, node, isAdd){
        if( !array )array = this.documentList;
        if( o2.typeOf(array) !== 'array' )array = [array];
        array.each(function(d){
            if(d.targetCreatorPerson)d.targetCreatorPersonCn = d.targetCreatorPerson.split("@")[0];
        });
        switch (this.json.mode) {
            case "text":
                this.loadDocumentListText(array, node);  break;
            case "script":
                this.loadDocumentListScript(array, node);  break;
            case "default":
            default:
                this.loadDocumentListDefault(array, node, isAdd);  break;
        }
    },
    loadDocumentListDefault: function(array, node, isAdd){
        if( this.json.style === 'v10' ){
            if( !isAdd || !this.table ){
                this.table = new Element('table', {
                    "style": "width: 100%;border-collapse:collapse;",
                    "border": "0",
                    "cellPadding": "2",
                    "cellSpacing": "0"
                }).inject(node || this.documentListNode);
            }

            var lp = MWF.xApplication.process.Xform.LP;

            var hasCMS = this.documentList.some(function (d) { return d.targetType !== "processPlatform"; });
            var hasProcess = this.documentList.some(function (d) { return d.targetType === "processPlatform"; });

            var headerNode = new Element("tr.title");
            var titles = [];
            var titlesWidth = [];
            if( hasCMS && hasProcess ){
                titlesWidth = ['', '', '', '4rem', '6rem', '13rem', '2rem'];
                titles = ['', lp.title,
                    lp.processName+"/"+lp.categoryName,
                    lp.documentType,
                    lp.creatorPerson,
                    lp.draftTime+"/"+lp.publishTime, '' ];
            }else{
                titlesWidth = ['', '', '', '4rem', '6rem', '13rem', '2rem'];
                if(hasCMS){
                    titles = ['', lp.title, lp.categoryName, lp.documentType, lp.publishPerson, lp.publishTime, '' ];
                }else if( hasProcess ){
                    titles = ['', lp.title, lp.processName, lp.documentType, lp.draftPerson, lp.draftTime, ''];
                }
            }
            titles.each(function (title, i){
                var th = new Element("th", {text: title}).inject(headerNode);
                if( titlesWidth[i] ){
                    th.setStyle('width', titlesWidth[i]);
                }else if(i > 0){
                    th.setStyle('min-width', '4rem');
                }
            })
            var titleTr = this.table.getElement('.title');
            if( titleTr ){
                headerNode.inject(titleTr, 'after');
                titleTr.destroy();
            }else{
                headerNode.inject(this.table);
            }

            (array || this.documentList).each(function (d) {
                this.loadDocumentListDefault_V10(d, node ? node.getElement('table') : this.table);
            }.bind(this));
        }else{
            (array || this.documentList).each(function (d) {
                var itemNode = new Element("div", {
                    styles:  this.form.css.associatedDocumentItem
                }).inject( node || this.documentListNode );
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
                this._loadDocument(d, itemNode, deleteNode);
            }.bind(this))
        }
    },
    loadDocumentListDefault_V10: function (d, node){
        var lp = MWF.xApplication.process.Xform.LP;
        var itemNode = new Element("tr").inject( node || this.table );
        itemNode.set('data-o2-bundle', d.targetBundle);
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
    loadDocumentListText: function(array, node){
        var lp = MWF.xApplication.process.Xform.LP;
        (array || this.documentList).each(function (d) {
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
            }).inject(node || this.documentListNode);
            var deleteNode = itemNode.getElement("[data-o2-action='delete']");
            if(!layout.mobile)deleteNode.hide();
            this._loadDocument(d, itemNode, deleteNode);
        }.bind(this))
    },
    loadDocumentListScript: function(array, node){
        if (this.json.displayScript && this.json.displayScript.code){
            var code = this.json.displayScript.code;
            (array || this.documentList).each(function(d){
                var itemNode = new Element("div", {
                    styles:  this.form.css.associatedDocumentItem,
                }).inject(node || this.documentListNode);

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
        itemNode.set('data-o2-bundle', d.targetBundle);
	    if( layout.mobile ){
            itemNode.addEvents({
                "click": function (e) {
                    this.openDoc(e, d);
                }.bind(this),
            });
        }else{
            itemNode.addEvents({
                "mouseover": function () {
                    if(deleteNode && !this.isReadonly())deleteNode.show();
                    itemNode.setStyles( this.form.css.associatedDocumentItem_over )
                }.bind(this),
                "mouseout": function () {
                    if(deleteNode && !this.isReadonly())deleteNode.hide();
                    itemNode.setStyles( this.form.css.associatedDocumentItem )
                }.bind(this),
                "click": function (e) {
                    this.openDoc(e, d);
                }.bind(this)
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
        var doCancel = (dlg)=>{
            _self.fireEvent("deleteDocument", [d]);

            var p = _self._cancelAssociated([d.id]);
            p.then(function (json) {
                itemNode.destroy();
                _self.documentList.erase(d);
                if( _self.selectMode ){
                    var dom = _self.documentListNode.querySelector('[data-o2-bundle="'+d.targetBundle+'"]');
                    if(dom)dom.destroy();

                    var vw = _self.viewList.filter((view)=>{
                        return view.json.id === d.view;
                    });
                    if(vw.length){
                        vw[0].cancelSelected([d.targetBundle], false);
                    }
                    _self.refreshSelectedCount();
                }
                _self.fireEvent("afterDeleteDocument", [d]);
                dlg?.close();
            }.bind(this));
        }
        if( this.selectMode ){
            doCancel();
        }else{
            this.form.confirm("warn", e, lp.cancelAssociatedTitle, lp.cancelAssociated.replace("{title}", o2.txt(d.targetTitle)), 370, 120, function () {
                doCancel(this)
            }, function () {
                this.close();
            }, null, null, this.form.json.confirmStyle);
        }
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
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.processName+": </div><div style='"+contentStyle+"'>"+o2.txt(d.targetCategory)+"</div></div>";
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.draftPerson +": </div><div style='"+contentStyle+"'>"+o2.txt(d.targetCreatorPersonCn)+"</div></div>";
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.draftTime +": </div><div style='"+contentStyle+"'>"+d.targetStartTime+"</div></div>";
        }else{
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.documentType+": </div><div style='"+contentStyle+"'>"+lp.document+"</div></div>";
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.categoryName+": </div><div style='"+contentStyle+"'>"+o2.txt(d.targetCategory)+"</div></div>";
            html += "<div style='"+lineStyle+"'><div style='"+titleStyle+"'>"+lp.publishPerson+": </div><div style='"+contentStyle+"'>"+o2.txt(d.targetCreatorPersonCn)+"</div></div>";
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

            debugger;

            if (layout.mobile && this.json.style === 'v10'){
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
            "style": layout.mobile && o2.version.dev === 10 ? 'v10_mobile' : "v10"
        });
        this.tab.load();
        this.tab.contentNodeContainer.inject(this.viewContentNode);

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

                if( layout.mobile  ){
                    if( this.json.style === 'v10' ){
                        pageViewNode.setStyle("height", '100%');
                    }else{
                        var selectedHeight = !!this.selectedArea.offsetParent ? this.selectedArea.getSize().y : 48;
                        var viewHeight = dlg.content.getSize().y - this.tab.tabNodeContainer.getSize().y - selectedHeight;
                        pageViewNode.setStyle("height", viewHeight);
                    }
                }else{
                    var viewHeight = dlg.content.getSize().y - this.tab.tabNodeContainer.getSize().y - 1;
                    if( this.json.style === 'v10' ){
                        viewHeight = viewHeight - 24;
                    }
                    if(this.selectedAreaDirection === 'vertical' || layout.mobile ){
                        viewHeight = viewHeight - this.selectedAreaHeight;
                    }else{

                    }
                    if( viewHeight < this.MIN_VIEW_CONTENT_HEIGHT ){
                        viewHeight = this.MIN_VIEW_CONTENT_HEIGHT;
                    }
                    pageViewNode.setStyle("height", viewHeight);
                }

                viewJson.defaultSelectedScript= function (item){
                    debugger;
                    var bundles = this.selectedBundleMap[viewJson.viewId] || [];
                    return bundles.contains(item.data.bundle);
                }.bind(this);

                var view = new MWF.xApplication.query.Query.Viewer(pageViewNode, viewJson, {
                    "isloadContent": this.status !== "showResult",
                    "isloadActionbar": this.status !== "showResult",
                    "isloadSearchbar": this.status !== "showResult",
                    "style": "select",
                    "defaultBundles": this.selectedBundleMap[viewJson.viewId] || [],
                    "onLoadView": function(){
                        this.fireEvent("loadView");
                    }.bind(this),
                    "onSelect": function(obj){
                        var bundles = this.documentList.map(function (doc) { return doc.targetBundle } )
                        var item = obj.item;
                        if( bundles.includes(item.data.bundle) ){
                            return;
                        }
                        var data = {
                            "type": item.view.json.type === "process" ? "processPlatform" : "cms",
                            "view": item.view.json.id,
                            "site": this.json.site || this.json.id,
                            "bundle": item.data.bundle
                        };
                        var selectFlag = item.view.getSelectFlag();
                        if( selectFlag === 'single' || this.documentList.length === 0 ){
                            this.selectedContentNode.empty();
                            this.set([data], ()=>{
                                this.refreshSelectedCount();
                                this.selectedContentNode.empty();
                                this.showDocumentList(this.documentList, this.selectedContentNode);
                            }, false);
                        }else{
                            this.add([data], false, true, ()=>{
                                //this.addDocumentInDialog();
                                this.refreshSelectedCount();
                                var list = this.documentList.filter(function (doc) {
                                    return doc.targetBundle === data.bundle;
                                });
                                this.showDocumentList(list, this.selectedContentNode, true);
                            }, false);
                        }
                        this.fireEvent("select", [item]);
                    }.bind(this),
                    "onUnselect": function(item){
                        selectedBundles.erase( item.data.bundle );
                        this.cancel([item.data.bundle], ()=>{
                            //this.cancelDocumentInDialog();
                            this.refreshSelectedCount();
                            var dom = this.selectedContentNode.querySelector('[data-o2-bundle="'+item.data.bundle+'"]');
                            if(dom)dom.destroy();
                        }, false);
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

                if( layout.mobile && this.json.style === 'v10' ){
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
        var height = this.json.DialogHeight || "900";

        width = width.toInt();
        height = height.toInt();

        var size = this.form.app.content.getSize();
        if (layout.mobile){
            var size = document.body.getSize();
            width = size.x;
            height = size.y;
            options.style = "viewmobile";
        }else{
            debugger;
            this.selectedAreaDirection = (size.x < width + this.MIN_SELECTED_AREA_WIDTH) ? "vertical" : "horizontal";
            if (this.selectedAreaDirection === "horizontal") {
                this.viewWidth = width;
                width = width + this.MIN_SELECTED_AREA_WIDTH;
            }else if(size.y > height + this.MIN_SELECTED_AREA_HEIGHT){
                this.selectedAreaHeight = this.MIN_SELECTED_AREA_HEIGHT;
                height = height + this.MIN_SELECTED_AREA_HEIGHT;
            }else{
                this.selectedAreaHeight = this.MIN_SELECTED_AREA_HEIGHT;
                height = size.y;
            }
        }

        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<0) y = 0;
        if (layout.mobile){
            x = 20;
            y = 0;
        }

        var _self = this;
        var lp = MWF.xApplication.process.Xform.LP;
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
                        "text": lp.associatedDocumentCompleted,
                        "action": function(){
                            //if (callback) callback(_self.view.selectedItems);

                            _self.afterSelectView( callback, dlg );
                            //this.close();
                        }
                    },
                    // {
                    //     "text": MWF.LP.process.button.cancel,
                    //     "action": function(){
                    //         _self.selectMode = false;
                    //         this.close();
                    //     }
                    // }
                ],
                "onQueryClose": function () {
                    this.selectMode = false;
                    this.dlg = null;
                }.bind(this),
                "onPostShow": function(){
                    this.selectedArea = new Element("div.associatedDocumentSelectedArea");
                    var viewNode;
                    if(layout.mobile){
                        viewNode = dlg.content;
                        dlg.node.setStyle("z-index",400);
                    }else{
                        viewNode = new Element('div').inject(dlg.content);
                        dlg.content.setStyle('display', 'flex');
                        if( this.selectedAreaDirection === 'horizontal' ){
                            viewNode.setStyles({
                                'flex-shrink': "0",
                                'width': this.viewWidth + 'px',
                                'display': 'flex',
                                'flex-direction': 'column'
                            });
                            this.selectedArea.setStyles({
                                'flex': 1,
                                'height': 'calc( 100% - 20px )',
                                'overflow': 'auto'
                            });
                        }else{
                            dlg.content.setStyles({
                                'flex-direction': 'column',
                                'flex-wrap': 'nowrap'
                            });
                            viewNode.setStyles({
                                'display': 'flex',
                                'flex-direction': 'column'
                            });
                            this.selectedArea.setStyles({
                                'height': this.selectedAreaHeight + 'px',
                                'overflow': 'auto'
                            });
                        }
                    }
                    this.loadViewers(viewNode, dlg);
                    if( layout.mobile ){
                        this.selectedArea.inject(dlg.content);
                        this.loadSelectedAreaMobile(this.selectedArea, dlg);
                    }else{
                        this.selectedArea.inject(dlg.content);
                        this.loadSelectedArea(this.selectedArea, dlg);
                    }
                }.bind(this)
            });
            this.dlg = dlg;
            dlg.show();
            this.selectMode = true;

            if (layout.mobile){
                if(dlg.title)dlg.title.addClass("mainColor_color");
                var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                if (backAction) {
                    backAction.set('text', MWF.xApplication.process.Xform.LP.completedEdit );
                    backAction.addEvent("click", function(e){
                        _self.afterSelectView( callback, dlg );
                        dlg.close();
                    }.bind(this));
                }
                if (okAction) {
                    okAction.hide();
                    // okAction.addEvent("click", function(e){
                    //     _self.afterSelectView( callback, dlg );
                    // }.bind(this));
                }
            }
        }.bind(this));
    },
    selectViewMobile: function (callback){
        var _renderViewContainerMobile = this.form.Macro.environment._renderViewContainerMobile;
        var viewer = null;
        _renderViewContainerMobile(
            this.json.title || MWF.xApplication.process.Xform.LP.associatedDocument,
            (viewNode, o)=>{

                o._selectCancel = o.selectCancel;
                o.selectCancel = (e)=>{
                    if( e.target.tagName.toLowerCase() === 'oo-button'){
                        o._selectCancel(e);
                    }else{
                        if( !this.selectedContentNode.offsetParent ){
                            o._selectCancel();
                        }else{
                            this.selectedContentNode.hide();
                            this.showSelectedContentNode.show();
                            this.hideSelectedContentNode.hide();
                        }
                    }
                };

                this.selectMode = true;
                window.setTimeout(
                    ()=>{
                        this.loadViewers(viewNode);
                    },
                    200
                );
                this.selectedArea = new Element("div.associatedDocumentSelectedArea").inject(viewNode, 'after');
                this.loadSelectedAreaMobile(this.selectedArea, o);
                this.dlg_mobile = o;

                o.contentNode.querySelector('.mwf_selectView_action_cancel').hide();
                o.contentNode.querySelector('.mwf_selectView_action_ok').set('text', MWF.xApplication.process.Xform.LP.associatedDocumentCompleted);

            },
            ()=>{
                this.afterSelectView( callback );
                this.selectMode = false;
            },
            false
        );
    },
    loadSelectedArea: function(container, dialog){
        var lp = MWF.xApplication.process.Xform.LP;
        this.selectedArea.setStyles(this.form.css.associatedDocumentSelectedArea);

        this.selectedTitleNode = new Element("div", {
            styles: this.form.css.associatedDocumentSelectedTitleNode
        }).inject(this.selectedArea);
        this.selectedCountNode = new Element("div", {
            styles: this.form.css.associatedDocumentSelectedCountNode,
            text: lp.associatedCount.replace('{count}', this.documentList.length)
        }).inject(this.selectedTitleNode);
        this.cancelAllNode = new Element("div", {
            styles: this.form.css.associatedDocumentSelectedCancelAllNode,
            text: lp.empty
        }).inject(this.selectedTitleNode);
        this.cancelAllNode.addEvent('click', (e)=>{
            this.selectedContentNode.empty();
            this.viewList.forEach((view)=>{
                view.cancelSelectedAll(false);
            })
            this.set([], ()=>{
                this.refreshSelectedCount();
                this.selectedContentNode.empty();
                this.showDocumentList(this.documentList, this.selectedContentNode);
            }, false);
        })

        this.selectedContentNode = new Element("div.MWFADContent", {}).inject(this.selectedArea);
        this.showDocumentList(this.documentList, this.selectedContentNode);
    },
    loadSelectedAreaMobile: function(container, dialog){
        var lp = MWF.xApplication.process.Xform.LP;
        this.selectedArea.setStyles(this.form.css.associatedDocumentSelectedArea);

        this.selectedTitleNode = new Element("div", {
            styles: this.form.css.associatedDocumentSelectedTitleNode
        }).inject(this.selectedArea);
        this.selectedTitleNode.addEvent('click', ()=>{
            if( !this.selectedContentNode.offsetParent ){
                this.selectedContentNode.show();
                // this.selectedMaskNode.show();
                this.showSelectedContentNode.hide();
                this.hideSelectedContentNode.show();
            }else{
                this.selectedContentNode.hide();
                this.showSelectedContentNode.show();
                this.hideSelectedContentNode.hide();
            }
        })
        this.selectedCountNode = new Element("div", {
            styles: this.form.css.associatedDocumentSelectedCountNode,
            text: lp.associatedCount.replace('{count}', this.documentList.length)
        }).inject(this.selectedTitleNode);

        this.showSelectedContentNode = new Element('i.ooicon-icon_arrow_up.mainColor_color').inject(this.selectedTitleNode);
        this.showSelectedContentNode.setStyles(this.form.css.associatedDocumentSelectedCancelAllNode);
        this.hideSelectedContentNode = new Element('i.ooicon-drop_down.mainColor_color').inject(this.selectedTitleNode);
        this.hideSelectedContentNode.setStyles(this.form.css.associatedDocumentSelectedCancelAllNode);
        this.hideSelectedContentNode.hide();

        this.selectedContentNode = new Element("div.MWFADContent", {}).inject(this.selectedArea);
        if( this.json.style === 'v10' ){
            this.selectedContentNode.setStyles(this.form.css.associatedDocumentSelectedContentNodeMobileV10);
        }else{
            this.selectedContentNode.setStyles(this.form.css.associatedDocumentSelectedContentNodeMobile);
        }
        this.selectedContentNode.hide();
        this.showDocumentList(this.documentList, this.selectedContentNode);
    },
    refreshSelectedCount: function(){
        var lp = MWF.xApplication.process.Xform.LP;
        this.selectedCountNode.set(
            "text",
           lp.associatedCount.replace('{count}', this.documentList.length)
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
            var code = this.json.selectedScript.code;
            if (!!code && !!code.trim()) {
                this.form.Macro.exec(this.json.selectedScript.code, this);
                this.form.saveFormData();
            }
        }else{
            var obj = this.json.selectedSetValues;
            if( obj && Object.keys(obj).length > 0 ){
                Object.each(obj, function(v, k){
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
                this.form.saveFormData();
            }
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
