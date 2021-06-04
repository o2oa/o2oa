MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.require("MWF.widget.Tree", null, false);
//MWF.require("MWF.widget.Toolbar", null, false);

/** @class Actionbar 操作条组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var actionbar = this.form.get("name"); //获取操作条
 * //方法2
 * var actionbar = this.target; //在操作条和操作本身的事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Actionbar = MWF.APPActionbar =  new Class(
    /** @lends MWF.xApplication.process.Xform.Actionbar# */
    {
        Extends: MWF.APP$Module,
        options: {
            /**
             * 组件加载前触发。
             * @event MWF.xApplication.process.Xform.Actionbar#queryLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            /**
             * 组件加载时触发。
             * @event MWF.xApplication.process.Xform.Actionbar#load
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            /**
             * 组件加载后事件.由于加载过程中有异步处理，这个时候操作条有可能还未生成。
             * @event MWF.xApplication.process.Xform.Actionbar#postLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            /**
             * 组件加载后事件。这个时候操作条已生成
             * @event MWF.xApplication.process.Xform.Actionbar#afterLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "moduleEvents": ["load", "queryLoad", "postLoad", "afterLoad"]
        },
        /**
         * @summary 重新加载操作条.
         * @example
         * this.form.get("name").reload(); //显示操作条
         */
        reload : function(){
            this._loadUserInterface();
        },
        _loadUserInterface: function(){
            // if (this.form.json.mode == "Mobile"){
            //     this.node.empty();
            // }else if (COMMON.Browser.Platform.isMobile){
            //     this.node.empty();
            // }else{
            this.toolbarNode = this.node.getFirst("div");
            if(!this.toolbarNode)return;

            this.toolbarNode.empty();

            MWF.require("MWF.widget.Toolbar", function(){
                /**
                 * @summary Toolbar组件，平台使用该组件生成操作条。
                 * @member {o2.widget.Toolbar}
                 * @example
                 *  //可以在脚本中获取该组件
                 * var toolbarWidget = this.form.get("fieldId").toolbarWidget; //获取组件对象
                 */
                this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {
                    "style": this.json.style,
                    "onPostLoad" : function(){
                        this.fireEvent("afterLoad");
                    }.bind(this)
                }, this);
                if (this.json.actionStyles) this.toolbarWidget.css = this.json.actionStyles;
                //alert(this.readonly)

                if( this.json.multiTools ){ //自定义操作和系统操作混合的情况，用 system : true 来区分系统和自定义
                    var addReadActionFlag = !this.json.hideSystemTools && !this.json.hideReadedAction; //是否需要增加已阅
                    var jsonStr = JSON.stringify(this.json.multiTools);
                    jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.process.Xform.LP.form});
                    this.json.multiTools = JSON.parse(jsonStr);
                    this.json.multiTools.each( function (tool) {
                        if( tool.system ){
                            if( !this.json.hideSystemTools ){
                                if( tool.id === "action_readed" )addReadActionFlag = false;
                                this.setToolbars([tool], this.toolbarNode, this.readonly);
                            }
                        }else{
                            this.setCustomToolbars([tool], this.toolbarNode);
                        }
                    }.bind(this));
                    if( addReadActionFlag ){
                        var addActions = [
                            {
                                "type": "MWFToolBarButton",
                                "img": "read.png",
                                "title": MWF.xApplication.process.Xform.LP.setReaded,
                                "action": "readedWork",
                                "text": MWF.xApplication.process.Xform.LP.readed,
                                "id": "action_readed",
                                "control": "allowReadProcessing",
                                "condition": "",
                                "read": true
                            }
                        ];
                        this.setToolbars(addActions, this.toolbarNode, this.readonly);
                    }
                    this.toolbarWidget.load();
                }else{
                    if (this.json.hideSystemTools){
                        this.setCustomToolbars(this.json.tools, this.toolbarNode);
                        this.toolbarWidget.load();
                    }else{
                        if (this.json.defaultTools){
                            var addActions = [
                                {
                                    "type": "MWFToolBarButton",
                                    "img": "read.png",
                                    "title": MWF.xApplication.process.Xform.LP.setReaded,
                                    "action": "readedWork",
                                    "text": MWF.xApplication.process.Xform.LP.readed,
                                    "id": "action_readed",
                                    "control": "allowReadProcessing",
                                    "condition": "",
                                    "read": true
                                }
                            ];
                            //this.form.businessData.control.allowReflow =

                            //this.json.defaultTools.push(o);
                            this.setToolbars(this.json.defaultTools, this.toolbarNode, this.readonly);
                            if( !this.json.hideReadedAction ){
                                this.setToolbars(addActions, this.toolbarNode, this.readonly);
                            }

                            this.setCustomToolbars(this.json.tools, this.toolbarNode);
                            this.toolbarWidget.load();
                        }else{
                            MWF.getJSON(this.form.path+"toolbars.json", function(json){
                                this.setToolbars(json, this.toolbarNode, this.readonly, true);
                                this.setCustomToolbars(this.json.tools, this.toolbarNode);
                                this.toolbarWidget.load();
                            }.bind(this), null);
                        }
                    }
                }
            }.bind(this));
            // }
        },

        setCustomToolbars: function(tools, node){
            var path = "../x_component_process_FormDesigner/Module/Actionbar/";
            var iconPath = "";
            if( this.json.customIconStyle ){
                iconPath = this.json.customIconStyle+"/";
            }
            tools.each(function(tool){
                var flag = true;
                if (this.readonly){
                    flag = tool.readShow;
                }else{
                    flag = tool.editShow;
                }
                if (flag){
                    flag = true;
                    if (tool.control){
                        flag = this.form.businessData.control[tool.control]
                    }
                    if (tool.condition){
                        var hideFlag = this.form.Macro.exec(tool.condition, this);
                        flag = !hideFlag;
                    }
                    if (flag){
                        var actionNode = new Element("div", {
                            "id": tool.id,
                            "MWFnodetype": tool.type,
                            "MWFButtonImage": path+""+this.form.options.style+"/custom/"+iconPath+tool.img,
                            "title": tool.title,
                            "MWFButtonAction": "runCustomAction",
                            "MWFButtonText": tool.text
                        }).inject(node);
                        if( this.json.customIconOverStyle ){
                            actionNode.set("MWFButtonImageOver" , path+""+this.form.options.style +"/custom/"+this.json.customIconOverStyle+ "/" +tool.img );
                        }
                        if( tool.properties ){
                            actionNode.set(tool.properties);
                        }
                        if (tool.actionScript){
                            actionNode.store("script", tool.actionScript);
                        }
                        if (tool.sub){
                            var subNode = node.getLast();
                            this.setCustomToolbars(tool.sub, subNode);
                        }
                    }
                }
            }.bind(this));
        },

        setToolbarItem: function(tool, node, readonly, noCondition){
            var path = "../x_component_process_FormDesigner/Module/Actionbar/";
            var flag = true;
            if (tool.control){
                flag = this.form.businessData.control[tool.control]
            }
            if (!noCondition) if (tool.condition){
                var hideFlag = this.form.Macro.exec(tool.condition, this);
                flag = flag && (!hideFlag);
            }
            // if (tool.id == "action_processWork"){
            //     if (!this.form.businessData.task){
            //         flag = false;
            //     }
            // }
            if (tool.id == "action_downloadAll" || tool.id == "action_print"){
                if (!this.form.businessData.work.startTime){
                    flag = false;
                }
            }
            if (tool.id == "action_delete"){
                if (!this.form.businessData.work || !this.form.businessData.work.id){
                    flag = false;
                }
            }


            if (tool.id == "action_rollback") tool.read = true;
            if (readonly) if (!tool.read) flag = false;
            if (flag){
                var actionNode = new Element("div", {
                    "id": tool.id,
                    "MWFnodetype": tool.type,
                    //"MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img,
                    "MWFButtonImage": path+(this.options.style||"default") +"/tools/"+ (this.json.style || "default") +"/"+tool.img,
                    "title": tool.title,
                    "MWFButtonAction": tool.action,
                    "MWFButtonText": tool.text
                }).inject(node);
                if( this.json.iconOverStyle ){
                    actionNode.set("MWFButtonImageOver" , path+""+(this.options.style||"default")+"/tools/"+( this.json.iconOverStyle || "default" )+"/"+tool.img );
                }
                if( tool.properties ){
                    actionNode.set(tool.properties);
                }
                if (tool.sub){
                    var subNode = node.getLast();
                    this.setToolbars(tool.sub, subNode, readonly, noCondition);
                }
            }
        },
        /**
         * @summary 根据操作id获取操作，该方法在操作条的afterLoad事件中有效，操作的操作脚本有效。
         *  @param {String} id - 必选，操作id.
         *  @return {o2.widget.ToolbarButton} 操作
         *  @example
         *  var actionbar = this.form.get("name"); //获取操作条
         *  var item = actionbar.getItem( "action_delete" ); //获取删除操作
         *  item.node.hide(); //隐藏删除操作的节点
         *  item.node.click(); //触发操作的click事件
         */
        getItem : function( id ){
            if( this.toolbarWidget && id ){
                return this.toolbarWidget.items[id]
            }
        },
        /**
         * @summary 获取所有操作，该方法在操作条的afterLoad事件中有效，操作的操作脚本有效。
         *  @return {Array} 操作数组
         *  @example
         *  var actionbar = this.form.get("name"); //获取操作条
         *  var itemList = actionbar.getAllItem(); //获取操作数组
         *  itemList[1].node.hide(); //隐藏第一个操作
         */
        getAllItem : function(){
            return this.toolbarWidget ? this.toolbarWidget.childrenButton : [];
        },
        setToolbars: function(tools, node, readonly, noCondition){
            tools.each(function(tool){
                this.setToolbarItem(tool, node, readonly, noCondition);
            }.bind(this));
        },
        runCustomAction: function(bt){
            var script = bt.node.retrieve("script");
            this.form.Macro.exec(script, this);
        },
        saveWork: function(){
            debugger;
            this.form.saveWork();
        },
        closeWork: function(){
            this.form.closeWork();
        },
        processWork: function(){
            this.form.processWork();
        },
        resetWork: function(){
            this.form.resetWork();
        },
        retractWork: function(e, ev){
            this.form.retractWork(e, ev);
        },
        rerouteWork: function(e, ev){
            this.form.rerouteWork(e, ev);
        },
        deleteWork: function(){
            this.form.deleteWork();
        },
        printWork: function(){
            this.form.printWork();
        },
        readedWork: function(b,e){
            this.form.readedWork(e);
        },
        addSplit: function(e){
            this.form.addSplit(e);
        },
        rollback: function(e){
            this.form.rollback(e);
        },
        downloadAll: function(e){
            this.form.downloadAll(e);
        },
        pressWork: function(e){
            this.form.pressWork(e);
        },
        pauseTask: function(e){
            var p = this.form.pauseTask(e);
            if (p){
                p.then(function(){
                    e.setText(MWF.xApplication.process.Xform.LP.resume);
                    e.options.action = "resumeTask";

                    var img = e.picNode.getElement("img");
                    var src = img.get("src");
                    src = src.substr(0, src.lastIndexOf("/"));
                    src = src+"/resume.png";
                    img.set("src", src);

                }.bind(this), function(){});
            }
        },
        resumeTask: function(e){
            var p = this.form.resumeTask(e);
            if (p){
                p.then(function(){
                    e.setText( MWF.xApplication.process.Xform.LP.pause);
                    e.options.action = "pauseTask";

                    var img = e.picNode.getElement("img");
                    var src = img.get("src");
                    src = src.substr(0, src.lastIndexOf("/"));
                    src = src+"/pause.png";
                    img.set("src", src);

                }.bind(this), function(){});
            }
        }

    });
