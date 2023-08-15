MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xDesktop.requireApp("process.Work", "lp." + MWF.language, null, false);

MWF.xApplication.process.Work.Flow  = MWF.ProcessFlow = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        style: "default",
        processEnable: true,
        addTaskEnable: true,
        resetEnable: true,
        processOptions: {},
    },
    initialize: function (container, task, options, form) {
        this.setOptions(options);

        this.path = "../x_component_process_Work/$Flow/";
        this.cssPath = "../x_component_process_Work/$Flow/" + this.options.style + "/css.wcss";
        this._loadCss();

        this.task = task;
        this.container = $(container);
        this.selectedRouteNode = null;

        this.form = form;
        this.businessData = this.form.businessData;

        this.load();
    },
    load: function () {

        this.processEnable = this.options.processEnable && this.businessData.control["allowProcessing"];
        this.addTaskEnable = this.options.addTaskEnable && this.businessData.control["allowAddTask"];
        this.resetEnable = this.options.resetEnable && this.businessData.control["allowReset"];

        this.node = new Element("div.node", {
            styles: {
                overflow: "hidden"
            }
        }).inject(this.container);



        this.naviNode = new Element("div", {
            styles: {
                "float": "left",
                "width": "100px"
            }
        }).inject(this.node);
        this.contentNode = new Element("div.contentNode", {
            styles: {
                "margin-left": "100px",
                "width": "900px"
            }
        }).inject(this.node);

        this.oneKeySelect = new Element("select", {
            styles: {
                "padding": "6px",
                "border": "1px solid #ccc",
                "font-size": "14px",
                "width": "100%",
                "margin-bottom": "10px"
            }
        }).inject( this.contentNode );
        [
            "快速选择",
            "提交：选择[送办理]，意见：请办理，处理人：张三、李四...",
            "提交：选择[送核稿]，意见：请核稿，处理人：王五",
            "加签：选择[前加签]，意见：请处理，加签人：赵六",
            "重置：保留待办，意见：请处理，重置给：王五"
        ].each(function (t) {
            new Element("option", {
                text: t
            }).inject( this.oneKeySelect )
        }.bind(this))

        if( this.processEnable ){
            this.processorTitleNode = new Element("div", {
                text: "提交",
                events: {
                    click: function(){ this.changeAction("process"); }.bind(this)
                }
            }).inject( this.naviNode );
            this.processorContentNode = new Element("div").inject( this.contentNode );
            this.loadProcessor();
        }
        if( this.addTaskEnable ){
            this.addTaskTitleNode = new Element("div", {
                text: "加签",
                events: {
                    click: function(){ this.changeAction("addTask"); }.bind(this)
                }
            }).inject( this.naviNode );
            this.addTaskContentNode = new Element("div").inject( this.contentNode );
            this.addTaskContentNode.hide();
            this.loadAddTask();
        }
        if( this.resetEnable ){
            this.resetTitleNode = new Element("div", {
                text: "重置",
                events: {
                    click: function(){ this.changeAction("reset"); }.bind(this)
                }
            }).inject( this.naviNode );
            this.resetContentNode = new Element("div").inject( this.contentNode );
            this.resetContentNode.hide();
            this.loadReset();
        }
    },
    changeAction: function( action ){

        if(this.processorContentNode){
            this.processorContentNode[ action === "process" ? "show" : "hide" ]();
            this.processorTitleNode[ action === "process" ? "addClass" : "removeClass" ]("mainColor_color");
        }

        if(this.addTaskContentNode){
            this.addTaskContentNode[ action === "addTask" ? "show" : "hide" ]();
            this.addTaskTitleNode[ action === "addTask" ? "addClass" : "removeClass" ]("mainColor_color");
        }

        if(this.resetContentNode){
            this.resetContentNode[ action === "reset" ? "show" : "hide" ]();
            this.resetTitleNode[ action === "reset" ? "addClass" : "removeClass" ]("mainColor_color");
        }
    },
    loadProcessor: function () {
        var processOptions = this.options.processOptions;
        processOptions.onResize = function () {
            var size = this.processorContentNode.getSize();
            var naviSize = this.naviNode.getSize();
            this.container.setStyles({
                "height": size.y,
                "width": size.x + naviSize.x + this.getOffsetX( this.naviNode )
            });
            debugger;
            this.fireEvent("resize");
        }.bind(this);
        processOptions.inFlow = true;
        this.processor = new MWF.ProcessFlow.Processor(
            this.processorContentNode,
            this.task,
            processOptions,
            this.form
        );
    },
    loadReset: function(){
        this.reset = new MWF.ProcessFlow.Reset(
            this.resetContentNode,
            this.task,
            {},
            this.form
        );
    },
    loadAddTask: function(){
        this.addTask = new MWF.ProcessFlow.AddTask(
            this.addTaskContentNode,
            this.task,
            {},
            this.form
        );
    },
    destroy: function () {
        if( this.processor )this.processor.destroy();
        if( this.reset )this.reset.destroy();
        if( this.addTask )this.addTask.destroy();
    },
    getOffsetY : function(node){
        return (node.getStyle("margin-top").toInt() || 0 ) +
            (node.getStyle("margin-bottom").toInt() || 0 ) +
            (node.getStyle("padding-top").toInt() || 0 ) +
            (node.getStyle("padding-bottom").toInt() || 0 )+
            (node.getStyle("border-top-width").toInt() || 0 ) +
            (node.getStyle("border-bottom-width").toInt() || 0 );
    },
    getOffsetX : function(node){
        return (node.getStyle("margin-left").toInt() || 0 ) +
            (node.getStyle("margin-right").toInt() || 0 ) +
            (node.getStyle("padding-left").toInt() || 0 ) +
            (node.getStyle("padding-right").toInt() || 0 )+
            (node.getStyle("border-left-width").toInt() || 0 ) +
            (node.getStyle("border-right-width").toInt() || 0 );
    },
    filterOneKeyData: function(){
        var onekeyList = listOneKeyData();
        onekeyList.filter(function (d) {
            var flag = (d.action === "process" && this.processEnable) || (d.action === "reset" && this.resetEnable) || (d.action === "addTask" && this.addTaskEnable);
            if( !flag )return false;
            if( d.action === "process" && !this.getRouteConfig(d.data.routeName) )return false;
            return true;
        }.bind(this));
        this.onekeyList = onekeyList;
    },
    listOneKeyData: function () {
        return [
            {
                "process": "",
                "activity": "",
                "processName": "",
                "activityName": "",
                "person": "",
                "action": "",
                "data": {
                    "routeId": "",
                    "routeName": "",
                    "keepTask": true,
                    "idea": "",
                    "organizations": {}
                }
            }
        ];
    },
    getRouteConfigList: function () {
        if(this.routeConfigList)return this.routeConfigList;

        if (this.task.routeNameDisable){
            this.routeConfigList = [{
                "id": o2.uuid(),
                "asyncSupported": false,
                "soleDirect": false,
                "name": "继续流转",
                "alias": "",
                "selectConfigList": []
            }];
            return this.routeConfigList;
        }

        if( this.form && this.form.businessData && this.form.businessData.routeList ){
            this.routeConfigList = this.form.businessData.routeList;
        }
        if (!this.routeConfigList) {
            o2.Actions.get("x_processplatform_assemble_surface").listRoute({"valueList": this.task.routeList}, function (json) {
                this.routeConfigList = json.data;
            }.bind(this), null, false);
        }
        return this.routeConfigList;
    },
    getRouteConfig: function (routeName) {
        var routeList = this.getRouteConfigList();
        for (var i = 0; i < routeList.length; i++) {
            if (routeList[i].name === routeName) {
                return routeList[i];
            }
        }
    },

});

MWF.ProcessFlow.Reset = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options:{
        style: "process"
    },
    initialize: function (container, task, options, form) {
        this.setOptions(options);

        this.path = "../x_component_process_Work/$Processor/";
        this.cssPath = "../x_component_process_Work/$Processor/" + this.options.style + "/css.wcss";
        this._loadCss();


        this.task = task;
        this.container = $(container);

        this.form = form;
        this.businessData = this.form.businessData;

        this.load();
    },
    load: function(){
        this.content = this.container;

        this.opinionTitle = new Element("div", {
            "styles": this.css.opinionTitle,
            "text": "重置意见"
        }).inject(this.content);
        this.opinionArea = new Element("div", {"styles": this.css.opinionArea}).inject(this.content);

        this.setOpinion();

        this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.content);
        this.orgsTitle = new Element("div", {
            "styles": this.css.orgsTitle,
            "text": "重置给"
        }).inject(this.orgsArea);

        this.loadOrg();


        var _self = this;
        var routeNode = new Element("div", {
            "styles": this.css.routeNode,
            "text": "保留我的待办"
        }).inject(this.content);

        routeNode.addEvents({
            "mouseover": function (e) {
                _self.overRoute(this);
            },
            "mouseout": function (e) {
                _self.outRoute(this);
            },
            "click": function (e) {
                _self.selectRoute(this);
            }
        });
        this.fireEvent("postLoad");
    },
    overRoute: function (node) {
        if (this.selectedRouteNode) {
            if (this.selectedRouteNode.get("text") != node.get("text")) {
                node.setStyles(this.css.routeNode_over);
                node.addClass("lightColor_bg");
                //node.setStyle("background-color", "#f7e1d0");
            }
        } else {
            node.setStyles(this.css.routeNode_over);
            node.addClass("lightColor_bg");
        }
    },
    outRoute: function (node) {
        if (this.selectedRouteNode) {
            if (this.selectedRouteNode.get("text") != node.get("text")) {
                node.setStyles(this.css.routeNode);
                node.removeClass("lightColor_bg");
            }
        } else {
            node.setStyles(this.css.routeNode);
            node.removeClass("lightColor_bg");
        }
    },
    selectRoute: function (node) {
        if (this.selectedRouteNode) {
            if (this.selectedRouteNode.get("text") != node.get("text")) { //选中其他路由
                this.selectedRouteNode.setStyles(this.css.routeNode);
                this.selectedRouteNode.removeClass("mainColor_bg");

                node.setStyles(this.css.routeNode_selected);
                node.addClass("mainColor_bg");
                node.removeClass("lightColor_bg");

            } else { //取消选中当前路由
                if (this.opinionTextarea.get("value") === this.getDefaultOpinion(this.selectedRouteNode)) {
                    this.lastDefaultOpinion = "";
                    this.opinionTextarea.set("value", MWF.xApplication.process.Work.LP.inputText || "");
                }

                this.selectedRouteNode.setStyles(this.css.routeNode);
                this.selectedRouteNode.addClass("lightColor_bg");
                this.selectedRouteNode.removeClass("mainColor_bg");
                this.selectedRouteNode = null;
            }
        } else {
            this.selectedRouteNode = node;
            node.setStyles(this.css.routeNode_selected);
            node.addClass("mainColor_bg");
            node.removeClass("lightColor_bg");
        }
        this.routeArea.setStyle("background-color", "#FFF");

    },
    setOpinion: function () {
        this.selectOpinionNode = new Element("div", {"styles": this.css.selectIdeaNode}).inject(this.opinionArea);
        this.selectOpinionScrollNode = new Element("div", {"styles": this.css.selectIdeaScrollNode}).inject(this.selectOpinionNode);
        this.selectOpinionAreaNode = new Element("div", {
            "styles": {
                "overflow": "hidden"
            }
        }).inject(this.selectOpinionScrollNode);

        this.opinionNode = new Element("div", {"styles": this.css.inputOpinionNode}).inject(this.opinionArea);
        this.opinionTextarea = new Element("textarea", {
            "styles": this.css.inputTextarea,
            "value": this.options.opinion || MWF.xApplication.process.Work.LP.inputText
        }).inject(this.opinionNode);
        this.opinionTextarea.setStyle("resize", "none");
        this.opinionTextarea.addEvents({
            "focus": function () {
                if (this.get("value") == MWF.xApplication.process.Work.LP.inputText) this.set("value", "");
            },
            "blur": function () {
                if (!this.get("value")) this.set("value", MWF.xApplication.process.Work.LP.inputText);
            },
            "keydown": function () {
                this.opinionTextarea.setStyles(this.opinionTextareaStyle || this.css.inputTextarea);
            }.bind(this)
        });

        MWF.require("MWF.widget.ScrollBar", function () {
            new MWF.widget.ScrollBar(this.selectOpinionScrollNode, {
                "style": "small",
                "where": "before",
                "distance": 30,
                "friction": 4,
                "indent": false,
                "axis": {"x": false, "y": true}
            });
        }.bind(this));

        MWF.require("MWF.widget.UUID", function () {
            MWF.UD.getDataJson("idea", function (json) {
                if (json) {
                    if (json.ideas) {
                        this.setIdeaList(json.ideas);
                    }
                } else {
                    MWF.UD.getPublicData("idea", function (pjson) {
                        if (pjson) {
                            if (pjson.ideas) {
                                this.setIdeaList(pjson.ideas);
                            }
                        }
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    },
    setIdeaList: function (ideas) {
        var _self = this;
        ideas.each(function (idea) {
            if (!idea) return;
            new Element("div", {
                "styles": this.css.selectIdeaItemNode,
                "text": idea,
                "events": {
                    "click": function () {
                        if (_self.opinionTextarea.get("value") == MWF.xApplication.process.Work.LP.inputText) {
                            _self.opinionTextarea.set("value", this.get("text"));
                        } else {
                            _self.opinionTextarea.set("value", _self.opinionTextarea.get("value") + ", " + this.get("text"));
                        }
                    },
                    "dblclick": function () {
                        if (_self.opinionTextarea.get("value") == MWF.xApplication.process.Work.LP.inputText) {
                            _self.opinionTextarea.set("value", this.get("text"));
                        } else {
                            _self.opinionTextarea.set("value", _self.opinionTextarea.get("value") + ", " + this.get("text"));
                        }
                    },
                    "mouseover": function () {
                        this.setStyles(_self.css.selectIdeaItemNode_over);
                    },
                    "mouseout": function () {
                        this.setStyles(_self.css.selectIdeaItemNode);
                    }
                }
            }).inject(this.selectOpinionAreaNode);
        }.bind(this));
    },
    setButtons: function () {
        this.cancelButton = new Element("div", {"styles": this.css.cancelButton}).inject(this.buttonsArea);
        var iconNode = new Element("div", {"styles": this.css.cancelIconNode}).inject(this.cancelButton);
        var textNode = new Element("div", {
            "styles": this.css.cancelTextNode,
            "text": MWF.xApplication.process.Work.LP.cancel
        }).inject(this.cancelButton);

        this.okButton = new Element("div", {"styles": this.css.okButton}).inject(this.buttonsArea);
        var iconNode = new Element("div", {"styles": this.css.okIconNode}).inject(this.okButton);
        var textNode = new Element("div", {
            "styles": this.css.okTextNode,
            "text": MWF.xApplication.process.Work.LP.ok
        }).inject(this.okButton);

        this.cancelButton.addEvent("click", function () {
            this.destroy();
            this.fireEvent("cancel");
        }.bind(this));

        this.okButton.addEvent("click", function (ev) {

        }.bind(this));
    },
    loadOrg: function(){
        this.getSelOptions( function (options) {
            this.selector = new MWF.O2Selector(this.orgsArea, options)
        }.bind(this) );
    },
    getSelOptions: function( callback ){
        o2.Actions.get("x_processplatform_assemble_surface").listTaskByWork(this.businessData.work.id, function(json){
            var identityList = [];
            json.data.each(function(task){
                identityList.push(task.identity);
            });
            this._getSelOptions(identityList, callback);
        }.bind(this))
    },
    _getSelOptions: function (exclude, callback) {
        var options = this.getDefaultOptions();

        var range = this.businessData.activity.resetRange || "department";
        switch (range) {
            case "unit":
                options.units = this.businessData.task.unit ? [this.businessData.task.unit] : [];
                options.exclude = exclude;
                callback( options );
                break;
            case "topUnit":
                MWF.require("MWF.xScript.Actions.UnitActions", function () {
                    orgActions = new MWF.xScript.Actions.UnitActions();
                    var data = { "unitList": [this.businessData.task.unit] };
                    orgActions.listUnitSupNested(data, function (json) {
                        options.units = json.data[0] ? [json.data[0]]: [];
                        options.exclude = exclude;
                        callback( options );
                    }.bind(this));
                }.bind(this));
                break;
            case "script":
                o2.Actions.load("x_processplatform_assemble_surface").ProcessAction.getActivity(this.businessData.work.activity, "manual", function (activityJson) {
                    var scriptText = activityJson.data.activity.resetRangeScriptText;
                    if (!scriptText) return;
                    var resetRange = this.Macro.exec(activityJson.data.activity.resetRangeScriptText, this);
                    options.noUnit = true;
                    options.include = typeOf(resetRange) === "array" ? resetRange : [resetRange];
                    options.exclude = exclude;
                    callback( options );
                }.bind(this))
                break;
            default:
                callback( options );
        }
    },
    getDefaultOptions: function(){
        var defaultOpt;
        if (layout.mobile) {
            defaultOpt = {
                "type": "identity",
                "style": "default",
                "zIndex": 3000,
                "count": this.businessData.activity.resetCount || 0
            };
        } else {
            defaultOpt = {
                "type": "identity",
                "style": "process",
                "width": "auto",
                "height": "240",
                "count": this.businessData.activity.resetCount || 0,
                "embedded": true,
                "hasLetter": false, //字母
                "hasTop": true //可选、已选的标题
            };
        }
        if (this.form.json.selectorStyle) {
            defaultOpt = Object.merge(Object.clone(this.form.json.selectorStyle), defaultOpt);
            if (this.form.json.selectorStyle.style) defaultOpt.style = this.form.json.selectorStyle.style;
        }
        return defaultOpt;
    },


    doResetWork: function () {
        var names = this.identityList || [];
        if (!names.length) {
            this.app.notice(MWF.xApplication.process.Xform.LP.inputResetPeople, "error", this.node);
            return false;
        }
        var opinion = $("resetWork_opinion").get("value");
        var checkbox = this.content.getElement(".resetWork_keepOption");
        var keep = (checkbox.checked);

        var nameText = [];
        names.each(function (n) { nameText.push(MWF.name.cn(n)); });
        if (!opinion) {
            opinion = MWF.xApplication.process.Xform.LP.resetTo + ": " + nameText.join(", ");
        }

        MWF.require("MWF.widget.Mask", function () {
            this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
            this.mask.loadNode(this.app.content);

            this.fireEvent("beforeReset");
            if (this.app && this.app.fireEvent) this.app.fireEvent("beforeReset");

            this.resetWorkToPeson(names, opinion, keep, function (workJson) {
                //this.workAction.loadWork(function (workJson) {
                this.fireEvent("afterReset");
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterReset");
                this.addResetMessage(workJson.data);
                //this.app.notice(MWF.xApplication.process.Xform.LP.resetOk + ": " + MWF.name.cns(names).join(", "), "success");
                if (!this.app.inBrowser) this.app.close();
                //}.bind(this), null, this.businessData.work.id);
                dlg.close();
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this), function (xhr, text, error) {
                var errorText = error + ":" + text;
                if (xhr) errorText = xhr.responseText;
                this.app.notice("request json error: " + errorText, "error", dlg.node);
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this));
        }.bind(this));
    },
    resetWorkToPeson: function (identityList, opinion, keep, success, failure) {
        var data = {
            "opinion": opinion,
            "routeName": MWF.xApplication.process.Xform.LP.reset,
            "identityList": identityList,
            "keep": !!keep
        };
        this.saveFormData(
            function (json) {
                o2.Actions.load("x_processplatform_assemble_surface").TaskAction.V2Reset(
                    //this.workAction.resetWork(
                    function (json) {
                        if (success) success(json);
                    }.bind(this),
                    function (xhr, text, error) {
                        if (failure) failure(xhr, text, error);
                    },
                    this.businessData.task.id, data
                );
            }.bind(this),
            function (xhr, text, error) {
                if (failure) failure(xhr, text, error);
            }, true, null, true
        );
    },
    destroy: function () {
        if (this.orgItem && this.orgItem.clearTooltip){
            this.orgItem.clearTooltip();
        }
        if (this.node) this.node.empty();
        // delete this.task;
        // delete this.node;
    },
});

MWF.ProcessFlow.AddTask = new Class({
    Extends: MWF.ProcessFlow.Reset,
    load: function(){
        this.content = this.container;

        this.opinionTitle = new Element("div", {
            "styles": this.css.opinionTitle,
            "text": "加签意见"
        }).inject(this.content);
        this.opinionArea = new Element("div", {"styles": this.css.opinionArea}).inject(this.content);

        this.setOpinion();

        this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.content);
        this.orgsTitle = new Element("div", {
            "styles": this.css.orgsTitle,
            "text": "加签人"
        }).inject(this.orgsArea);

        this.loadOrg();


        var _self = this;
        ["前加签","后加签"].each(function (text) {
            var routeNode = new Element("div", {
                "styles": this.css.routeNode,
                "text": text
            }).inject(this.content);

            routeNode.addEvents({
                "mouseover": function (e) {
                    _self.overRoute(this);
                },
                "mouseout": function (e) {
                    _self.outRoute(this);
                },
                "click": function (e) {
                    _self.selectRoute(this);
                }
            });
        }.bind(this))
        this.fireEvent("postLoad");
    },
})

MWF.ProcessFlow.Processor = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "mediaNode": null,
        "opinion": "",
        "defaultRoute": "",
        "isHandwriting": true,
        "tabletToolHidden": [],
        "tabletWidth": 0,
        "tabletHeight": 0,
        "orgHeight": 276,
        "inFlow": false,
        "maxOrgCountPerline": 2
    },

    initialize: function (node, task, options, form) {
        this.setOptions(options);

        this.path = "../x_component_process_Work/$Processor/";
        this.cssPath = "../x_component_process_Work/$Processor/" + this.options.style + "/css.wcss";
        this._loadCss();

        this.task = task;
        this.node = $(node);
        this.selectedRouteNode = null;

        this.form = form;

        this.load();
    },
    load: function () {
        this.content = this.node;

        this.opinionTitle = new Element("div", {
            "styles": this.css.opinionTitle,
            "text": MWF.xApplication.process.Work.LP.inputOpinion
        }).inject(this.content);
        this.opinionArea = new Element("div", {"styles": this.css.opinionArea}).inject(this.content);

        this.setOpinion();

        this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.content);
        this.orgsTitle = new Element("div", {
            "styles": this.css.orgsTitle,
            "text": MWF.xApplication.process.Work.LP.selectPerson
        }).inject(this.orgsArea);

        this.buttonsArea = new Element("div", {"styles": this.css.buttonsArea}).inject(this.content);

        this.setButtons();

        this.getRouteGroupList();
        if (this.hasRouteGroup) {
            //if( this.getMaxOrgLength() > 1 ){
            this.routeContainer = new Element("div", {
                "styles": this.css.routeContainer
            }).inject(this.opinionTitle, "before");

            this.routeLeftWarper = new Element("div").inject(this.routeContainer);
            if( this.getMaxOrgLength() > 1 ){
                this.routeLeftWarper.setStyles( this.options.inFlow ? this.css.routeLeftWarper_flow : this.css.routeLeftWarper );
            }else{
                this.routeLeftWarper.setStyles( this.css.routeLeftWarper_single );
            }
            this.routeGroupTitle = new Element("div", {
                "styles": this.css.routeSelectorTile,
                "text": MWF.xApplication.process.Work.LP.selectRouteGroup
            }).inject(this.routeLeftWarper);
            this.routeGroupArea = new Element("div", {"styles": this.css.routeSelectorArea_hasGroup}).inject(this.routeLeftWarper);

            this.routeRightWarper = new Element("div").inject(this.routeContainer);
            if( this.getMaxOrgLength() > 1 ){
                this.routeLeftWarper.setStyles( this.options.inFlow ? this.css.routeRightWarper_flow : this.css.routeRightWarper );
            }else{
                this.routeLeftWarper.setStyles( this.css.routeRightWarper_single );
            }
            this.routeTitleNode = new Element("div", {
                "styles": this.css.routeSelectorTile,
                "text": MWF.xApplication.process.Work.LP.selectRoute
            }).inject(this.routeRightWarper);
            this.routeArea = new Element("div", {"styles": this.css.routeSelectorArea_hasGroup}).inject(this.routeRightWarper);
            this.setRouteGroupList();
        } else {
            this.routeTitleNode = new Element("div", {
                "styles": this.css.routeSelectorTile,
                "text": MWF.xApplication.process.Work.LP.selectRoute
            }).inject(this.opinionTitle, "before");
            this.routeArea = new Element("div", {"styles": this.css.routeSelectorArea}).inject(this.routeTitleNode, "after");
            this.setRouteList();
        }

        this.fireEvent("postLoad");
    },
    getRouteGroupList: function () {
        if (this.routeGroupObject) return this.routeGroupObject;
        this.routeGroupObject = {};
        this.routeGroupNameList = [];
        this.hasRouteGroup = false;
        var routeList = this.getRouteConfigList();
        routeList.each(function (route, i) {

            if (route.hiddenScriptText && this.form && this.form.Macro) { //如果隐藏路由，返回
                if (this.form.Macro.exec(route.hiddenScriptText, this).toString() === "true") return;
            }

            if (route.displayNameScriptText && this.form && this.form.Macro) { //如果有显示名称公式
                route.displayName = this.form.Macro.exec(route.displayNameScriptText, this);
            } else {
                route.displayName = route.name;
            }

            if (route.decisionOpinion) {
                this.hasRouteGroup = true;
                route.decisionOpinion.split("#").each(function (rg) {
                    this.routeGroupNameList.combine([rg]);
                    var d = this.splitByStartNumber(rg);
                    if (!this.routeGroupObject[d.name]) this.routeGroupObject[d.name] = [];
                    this.routeGroupObject[d.name].push(route);
                }.bind(this))
            } else {
                var defaultName = MWF.xApplication.process.Work.LP.defaultDecisionOpinionName;
                this.routeGroupNameList.combine([defaultName]);
                if (!this.routeGroupObject[defaultName]) this.routeGroupObject[defaultName] = [];
                this.routeGroupObject[defaultName].push(route);
            }
        }.bind(this));
        return this.routeGroupObject;
    },
    splitByStartNumber: function (str) {
        var obj = {
            name: "",
            order: ""
        };
        for (var i = 0; i < str.length; i++) {
            if (parseInt(str.substr(i, 1)).toString() !== "NaN") {
                obj.order = obj.order + str.substr(i, 1);
            } else {
                obj.name = str.substr(i, str.length);
                break;
            }
        }
        return obj;
    },
    setRouteGroupList: function () {
        debugger;
        var _self = this;

        var keys = this.routeGroupNameList;
        keys.sort(function (a, b) {
            var aIdx = parseInt(this.splitByStartNumber(a).order || "9999999");
            var bIdx = parseInt(this.splitByStartNumber(b).order || "9999999");
            return aIdx - bIdx;
        }.bind(this));

        var list = [];
        keys.each(function (k) {
            list.push(this.splitByStartNumber(k).name)
        }.bind(this));

        var flag = true;
        var matchRoutes = [];
        list.each(function (routeGroupName) {
            var routeList = this.routeGroupObject[routeGroupName];
            var routeGroupNode = new Element("div", {
                "styles": this.css.routeGroupNode,
                "text": routeGroupName
            }).inject(this.routeGroupArea);
            routeGroupNode.store("routeList", routeList);
            routeGroupNode.store("routeGroupName", routeGroupName);

            routeGroupNode.addEvents({
                "mouseover": function (e) {
                    _self.overRouteGroup(this);
                },
                "mouseout": function (e) {
                    _self.outRouteGroup(this);
                },
                "click": function (e) {
                    _self.selectRouteGroup(this);
                }
            });

            if (keys.length === 1) {
                this.selectRouteGroup(routeGroupNode);
                flag = false;
            }else if( matchRoutes.length === 0 && this.options.defaultRoute ){
                matchRoutes = routeList.filter(function(r){ return r.id === this.options.defaultRoute || r.name === this.options.defaultRoute; }.bind(this));
                if( matchRoutes.length ){
                    this.selectRouteGroup(routeGroupNode);
                }
                flag = false;
            }
        }.bind(this))
        if (flag) {
            this.setSize(0);
        }
    },
    overRouteGroup: function (node) {
        if (this.selectedRouteGroup) {
            if (this.selectedRouteGroup.get("text") != node.get("text")) {
                node.setStyles(this.css.routeGroupNode_over);
            }
        } else {
            node.setStyles(this.css.routeGroupNode_over);
        }
    },
    outRouteGroup: function (node) {
        if (this.selectedRouteGroup) {
            if (this.selectedRouteGroup.get("text") != node.get("text")) {
                node.setStyles(this.css.routeGroupNode);
            }
        } else {
            node.setStyles(this.css.routeGroupNode);
        }
    },
    selectRouteGroup: function (node) {
        if (this.selectedRouteGroup) {
            if (this.selectedRouteGroup.get("text") != node.get("text")) {
                this.selectedRouteGroup.setStyles(this.css.routeGroupNode);
                //this.selectedRouteGroup.removeClass("mainColor_bg");

                this.selectedRouteGroup = node;
                this.selectedRouteGroup.setStyles(this.css.routeGroupNode_selected);
                //this.selectedRouteGroup.addClass("mainColor_bg");

                var routeList = this.selectedRouteGroup.retrieve("routeList");
                this.setRouteList(routeList);

            } else {

            }
        } else {
            this.selectedRouteGroup = node;
            node.setStyles(this.css.routeGroupNode_selected);

            var routeList = this.selectedRouteGroup.retrieve("routeList");
            this.setRouteList(routeList);
        }
        this.routeGroupArea.setStyle("background-color", "#FFF");
    },
    setRouteList: function (routeList) {
        var _self = this;
        this.routeArea.empty();
        this.selectedRouteNode = null;
        //this.task.routeNameList = ["送审核", "送办理", "送公司领导阅"];
        if (!routeList) routeList = this.getRouteConfigList();
        //this.task.routeNameList.each(function(route, i){
        var isSelected = false;
        var isSelectedDefault = false;
        routeList.each(function (route, i) {
            if ( route.hiddenScriptText ) {
                if (this.form.Macro.exec(route.hiddenScriptText, this).toString() === "true") return;
            }
            var routeName = route.name;
            if (route.displayNameScriptText && this.form && this.form.Macro) {
                routeName = this.form.Macro.exec(route.displayNameScriptText, this);
            }
            var routeNode = new Element("div", {
                "styles": this.css.routeNode,
                "text": routeName
            }).inject(this.routeArea);

            routeNode.store("route", route.id);
            routeNode.store("routeName", route.name);

            routeNode.addEvents({
                "mouseover": function (e) {
                    _self.overRoute(this);
                },
                "mouseout": function (e) {
                    _self.outRoute(this);
                },
                "click": function (e) {
                    _self.selectRoute(this);
                }
            });

            if( route.id === this.options.defaultRoute || route.name === this.options.defaultRoute) {
                this.selectRoute(routeNode);
                isSelected = true;
                isSelectedDefault = true;
            }else if ( !isSelectedDefault && (routeList.length == 1 || route.sole )) { //sole表示优先路由
                this.selectRoute(routeNode);
                isSelected = true;
            }
        }.bind(this));
        if (!isSelected) {
            this.setSize(0);
            if( this.orgsArea )this.orgsArea.hide();
        }
    },
    overRoute: function (node) {
        if (this.selectedRouteNode) {
            if (this.selectedRouteNode.get("text") != node.get("text")) {
                node.setStyles(this.css.routeNode_over);
                node.addClass("lightColor_bg");
            }
        } else {
            node.setStyles(this.css.routeNode_over);
            node.addClass("lightColor_bg");
        }
    },
    outRoute: function (node) {
        if (this.selectedRouteNode) {
            if (this.selectedRouteNode.get("text") != node.get("text")) {
                node.setStyles(this.css.routeNode);
                node.removeClass("lightColor_bg");
            }
        } else {
            node.setStyles(this.css.routeNode);
            node.removeClass("lightColor_bg");
        }
    },
    getDefaultOpinion: function( node ){
        var routeId = node.retrieve("route");
        var routeDate = this.getRouteConfig( routeId );
        return routeDate.opinion || "";
    },
    selectRoute: function (node) {
        if (this.selectedRouteNode) {
            if (this.selectedRouteNode.get("text") != node.get("text")) { //选中其他路由
                this.selectedRouteNode.setStyles(this.css.routeNode);
                this.selectedRouteNode.removeClass("mainColor_bg");

                if( this.opinionTextarea.get("value") === ( this.lastDefaultOpinion || "" ) ||
                    this.opinionTextarea.get("value") === (MWF.xApplication.process.Work.LP.inputText || "")
                ){
                    this.lastDefaultOpinion = this.getDefaultOpinion(node) || "";
                    this.opinionTextarea.set("value", this.lastDefaultOpinion || (MWF.xApplication.process.Work.LP.inputText || "") );
                }

                this.selectedRouteNode = node;
                node.setStyles(this.css.routeNode_selected);
                node.addClass("mainColor_bg");
                node.removeClass("lightColor_bg");

            } else { //取消选中当前路由
                if (this.opinionTextarea.get("value") === this.getDefaultOpinion(this.selectedRouteNode)) {
                    this.lastDefaultOpinion = "";
                    this.opinionTextarea.set("value", MWF.xApplication.process.Work.LP.inputText || "");
                }

                this.selectedRouteNode.setStyles(this.css.routeNode);
                this.selectedRouteNode.addClass("lightColor_bg");
                this.selectedRouteNode.removeClass("mainColor_bg");

                this.selectedRouteNode = null;
            }
        } else {
            if ( (this.opinionTextarea.get("value") === (MWF.xApplication.process.Work.LP.inputText || "")) ||
                (this.opinionTextarea.get("value") === this.lastDefaultOpinion )
            ) {
                this.lastDefaultOpinion = this.getDefaultOpinion(node) || "";
                if (this.lastDefaultOpinion) this.opinionTextarea.set("value", this.lastDefaultOpinion);
            }

            this.selectedRouteNode = node;
            node.setStyles(this.css.routeNode_selected);
            node.addClass("mainColor_bg");
            node.removeClass("lightColor_bg");
        }
        this.routeArea.setStyle("background-color", "#FFF");
        this.loadOrgs(this.selectedRouteNode ? this.selectedRouteNode.retrieve("route") : "");

        //临时添加
        if (this.form.data.json.events && this.form.data.json.events.afterSelectRoute) {
            this.form.Macro.exec(this.form.data.json.events.afterSelectRoute.code, node);
        }

    },

    setOpinion: function () {
        this.selectOpinionNode = new Element("div", {"styles": this.css.selectIdeaNode}).inject(this.opinionArea);
        this.selectOpinionScrollNode = new Element("div", {"styles": this.css.selectIdeaScrollNode}).inject(this.selectOpinionNode);
        this.selectOpinionAreaNode = new Element("div", {
            "styles": {
                "overflow": "hidden"
            }
        }).inject(this.selectOpinionScrollNode);

        this.opinionNode = new Element("div", {"styles": this.css.inputOpinionNode}).inject(this.opinionArea);
        this.opinionTextarea = new Element("textarea", {
            "styles": this.css.inputTextarea,
            "value": this.options.opinion || MWF.xApplication.process.Work.LP.inputText
        }).inject(this.opinionNode);
        this.opinionTextarea.setStyle("resize", "none");
        this.opinionTextarea.addEvents({
            "focus": function () {
                if (this.get("value") == MWF.xApplication.process.Work.LP.inputText) this.set("value", "");
            },
            "blur": function () {
                if (!this.get("value")) this.set("value", MWF.xApplication.process.Work.LP.inputText);
            },
            "keydown": function () {
                this.opinionTextarea.setStyles(this.opinionTextareaStyle || this.css.inputTextarea);
            }.bind(this)
        });

        if( this.options.isHandwriting ){
            this.mediaActionArea = new Element("div", {"styles": this.css.inputOpinionMediaActionArea}).inject(this.opinionNode);
            this.handwritingAction = new Element("div", {
                "styles": this.css.inputOpinionHandwritingAction,
                "text": MWF.xApplication.process.Work.LP.handwriting
            }).inject(this.mediaActionArea);
            this.handwritingAction.addEvent("click", function () {
                this.handwriting();
            }.bind(this));
        }


        MWF.require("MWF.widget.ScrollBar", function () {
            new MWF.widget.ScrollBar(this.selectOpinionScrollNode, {
                "style": "small",
                "where": "before",
                "distance": 30,
                "friction": 4,
                "indent": false,
                "axis": {"x": false, "y": true}
            });
        }.bind(this));

        MWF.require("MWF.widget.UUID", function () {
            MWF.UD.getDataJson("idea", function (json) {
                if (json) {
                    if (json.ideas) {
                        this.setIdeaList(json.ideas);
                    }
                } else {
                    MWF.UD.getPublicData("idea", function (pjson) {
                        if (pjson) {
                            if (pjson.ideas) {
                                this.setIdeaList(pjson.ideas);
                            }
                        }
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    },
    audioRecord: function () {
        if (!this.audioRecordNode) this.createAudioRecord();
        this.audioRecordNode.show();
        this.audioRecordNode.position({
            "relativeTo": this.options.mediaNode || this.node,
            "position": "center",
            "edge": "center"
        });

        MWF.require("MWF.widget.AudioRecorder", function () {
            this.audioRecorder = new MWF.widget.AudioRecorder(this.audioRecordNode, {
                "onSave": function (blobFile) {
                    this.soundFile = blobFile;
                    this.audioRecordNode.hide();
                    // this.page.get("div_image").node.set("src",base64Image);
                }.bind(this),
                "onCancel": function () {
                    this.soundFile = null;
                    this.audioRecordNode.hide();
                }.bind(this)
            }, null);
        }.bind(this));
    },
    createAudioRecord: function () {
        this.audioRecordNode = new Element("div", {"styles": this.css.handwritingNode}).inject(this.node, "after");
        var size = (this.options.mediaNode || this.node).getSize();
        // var y = Math.max(size.y, 320);
        // var x = Math.max(size.x, 400);

        // for (k in this.node.style){
        //     if (this.node.style[k]) this.audioRecordNode.style[k] = this.node.style[k];
        // }
        var zidx = this.node.getStyle("z-index");
        this.audioRecordNode.setStyles({
            "height": "" + size.y + "px",
            "width": "" + size.x + "px",
            "z-index": zidx + 1
        });
    },

    handwriting: function () {
        if (!this.handwritingNode) this.createHandwriting();
        if (this.handwritingNodeMask) this.handwritingNodeMask.show();
        this.handwritingNode.show();
        this.handwritingNode.position({
            "relativeTo": this.options.mediaNode || this.node,
            "position": "center",
            "edge": "center"
        });
    },
    createHandwriting: function () {
        this.handwritingNodeMask = new Element("div.handwritingMask", {"styles": this.css.handwritingMask}).inject(this.node);

        this.handwritingNode = new Element("div.handwritingNode", {"styles": this.css.handwritingNode}).inject(this.node, "after");

        var bodySize = $(document.body).getSize();
        x = bodySize.x;
        y = bodySize.y;
        this.options.tabletWidth = 0;
        this.options.tabletHeight = 0;
        var zidx = this.node.getStyle("z-index");
        this.handwritingNode.setStyles({
            "height": "" + y + "px",
            "width": "" + x + "px",
            "z-index": zidx + 1
        });
        this.handwritingNode.position({
            "relativeTo": this.options.mediaNode || this.node,
            "position": "center",
            "edge": "center"
        });
        this.handwritingAreaNode = new Element("div", {"styles": this.css.handwritingAreaNode}).inject(this.handwritingNode);
        this.handwritingAreaNode.setStyle("height", "" + y + "px");

        MWF.require("MWF.widget.Tablet", function () {
            var handWritingOptions = {
                "style": "default",
                "toolHidden": this.options.tabletToolHidden || [],
                "contentWidth": this.options.tabletWidth || 0,
                "contentHeight": this.options.tabletHeight || 0,
                "onSave": function (base64code, base64Image, imageFile) {
                    if( !this.tablet.isBlank() ){
                        this.handwritingFile = imageFile;
                        this.handwritingAction.setStyles( this.css.inputOpinionHandwritingOkAction )
                    }else{
                        this.handwritingFile = null
                        this.handwritingAction.setStyles( this.css.inputOpinionHandwritingAction );
                    }
                    this.handwritingNode.hide();
                    this.handwritingNodeMask.hide();
                    // this.page.get("div_image").node.set("src",base64Image);

                }.bind(this),
                "onCancel": function () {
                    this.handwritingFile = null;
                    this.handwritingAction.setStyles( this.css.inputOpinionHandwritingAction );
                    this.handwritingNode.hide();
                    this.handwritingNodeMask.hide();
                }.bind(this)
            };

            this.tablet = new MWF.widget.Tablet(this.handwritingAreaNode, handWritingOptions, null);
            this.tablet.load();
        }.bind(this));

        if(this.handwritingActionNode) {
            this.handwritingActionNode.addEvent("click", function () {
                //this.handwritingNode.hide();
                if (this.tablet) this.tablet.save();
            }.bind(this));
        }
    },

    setIdeaList: function (ideas) {
        var _self = this;
        ideas.each(function (idea) {
            if (!idea) return;
            new Element("div", {
                "styles": this.css.selectIdeaItemNode,
                "text": idea,
                "events": {
                    "click": function () {
                        if (_self.opinionTextarea.get("value") == MWF.xApplication.process.Work.LP.inputText) {
                            _self.opinionTextarea.set("value", this.get("text"));
                        } else {
                            _self.opinionTextarea.set("value", _self.opinionTextarea.get("value") + ", " + this.get("text"));
                        }
                    },
                    "dblclick": function () {
                        if (_self.opinionTextarea.get("value") == MWF.xApplication.process.Work.LP.inputText) {
                            _self.opinionTextarea.set("value", this.get("text"));
                        } else {
                            _self.opinionTextarea.set("value", _self.opinionTextarea.get("value") + ", " + this.get("text"));
                        }
                    },
                    "mouseover": function () {
                        this.setStyles(_self.css.selectIdeaItemNode_over);
                    },
                    "mouseout": function () {
                        this.setStyles(_self.css.selectIdeaItemNode);
                    }
                }
            }).inject(this.selectOpinionAreaNode);
        }.bind(this));
    },
    setButtons: function () {
        this.cancelButton = new Element("div", {"styles": this.css.cancelButton}).inject(this.buttonsArea);
        var iconNode = new Element("div", {"styles": this.css.cancelIconNode}).inject(this.cancelButton);
        var textNode = new Element("div", {
            "styles": this.css.cancelTextNode,
            "text": MWF.xApplication.process.Work.LP.cancel
        }).inject(this.cancelButton);

        this.okButton = new Element("div", {"styles": this.css.okButton}).inject(this.buttonsArea);
        var iconNode = new Element("div", {"styles": this.css.okIconNode}).inject(this.okButton);
        var textNode = new Element("div", {
            "styles": this.css.okTextNode,
            "text": MWF.xApplication.process.Work.LP.ok
        }).inject(this.okButton);

        this.cancelButton.addEvent("click", function () {
            this.destroy();
            this.fireEvent("cancel");
        }.bind(this));

        this.okButton.addEvent("click", function (ev) {
            this.submit(ev);
        }.bind(this));
    },
    submit: function (ev) {
        if (this.hasRouteGroup && !this.selectedRouteGroup) {
            this.routeGroupArea.setStyle("background-color", "#ffe9e9");
            MWF.xDesktop.notice(
                "error",
                {"x": "center", "y": "top"},
                MWF.xApplication.process.Work.LP.mustSelectRouteGroup,
                this.routeGroupArea,
                null,  //{"x": 0, "y": 30}
                {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
            );
            return false;
        }

        if (!this.selectedRouteNode) {
            this.routeArea.setStyle("background-color", "#ffe9e9");
            MWF.xDesktop.notice(
                "error",
                {"x": "center", "y": "top"},
                MWF.xApplication.process.Work.LP.mustSelectRoute,
                this.routeArea,
                null,  //{"x": 0, "y": 30}
                {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
            );
            return false;
        }
        var routeName = this.selectedRouteNode.retrieve("routeName") || this.selectedRouteNode.get("text");
        var opinion = this.opinionTextarea.get("value");
        if (opinion === MWF.xApplication.process.Work.LP.inputText) opinion = "";
        var medias = [];
        if (this.handwritingFile) medias.push(this.handwritingFile);
        if (this.soundFile) medias.push(this.soundFile);
        if (this.videoFile) medias.push(this.videoFile);

        var currentRouteId = this.selectedRouteNode.retrieve("route");
        var routeData = this.getRouteConfig(currentRouteId);
        if (!opinion && medias.length === 0) {
            if (routeData.opinionRequired == true) {
                this.opinionTextarea.setStyle("background-color", "#ffe9e9");
                MWF.xDesktop.notice(
                    "error",
                    {"x": "center", "y": "top"},
                    MWF.xApplication.process.Work.LP.opinionRequired,
                    this.opinionTextarea,
                    null,  //{"x": 0, "y": 30}
                    {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
                );
                return false;
            }
        }

        var appendTaskOrgItem = "";
        if (routeData.type === "appendTask" && routeData.appendTaskIdentityType === "select") {
            if (!this.orgItems || this.orgItems.length === 0) {
                MWF.xDesktop.notice(
                    "error",
                    {"x": "center", "y": "center"},
                    MWF.xApplication.process.Work.LP.noAppendTaskIdentityConfig,
                    this.node,
                    null,  //{"x": 0, "y": 30}
                    {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
                );
                return false;
            } else {
                appendTaskOrgItem = this.orgItems[0]
            }
        }

        this.saveOrgsWithCheckEmpower(function () {
            var appandTaskIdentityList;
            if (appendTaskOrgItem) {
                appandTaskIdentityList = appendTaskOrgItem.getData();
                if (!appandTaskIdentityList || appandTaskIdentityList.length === 0) {
                    MWF.xDesktop.notice(
                        "error",
                        {"x": "center", "y": "center"},
                        MWF.xApplication.process.Work.LP.selectAppendTaskIdentityNotice,
                        this.node,
                        {"x": 0, "y": 30},
                        {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
                    );
                    return;
                }
            }

            if (routeData.validationScriptText) {
                var validation = this.form.Macro.exec(routeData.validationScriptText, this);
                if (!validation || validation.toString() !== "true") {
                    if (typeOf(validation) === "string") {
                        MWF.xDesktop.notice(
                            "error",
                            {"x": "center", "y": "center"},
                            validation,
                            this.node,
                            {"x": 0, "y": 30},
                            {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
                        );
                        return false;
                    } else {
                        //"路由校验失败"
                        MWF.xDesktop.notice(
                            "error",
                            {"x": "center", "y": "center"},
                            MWF.xApplication.process.Work.LP.routeValidFailure,
                            this.node,
                            {"x": 0, "y": 30},
                            {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
                        );
                        return false;
                    }
                }
            }

            this.node.mask({
                "inject": {"where": "bottom", "target": this.node},
                "destroyOnHide": true,
                "style": {
                    "background-color": "#999",
                    "opacity": 0.3,
                    "z-index": 600
                }
            });


            var array = [routeName, opinion, medias, appandTaskIdentityList, this.orgItems, function () {
                if (appendTaskOrgItem) appendTaskOrgItem.setData([]);
            }];

            this.fireEvent("submit", array);
        }.bind(this))
    },

    destroy: function () {
        if (this.orgItems && this.orgItems.length){
            this.orgItems.each(function (org) {
                if(org.clearTooltip)org.clearTooltip();
            })
        }
        if (this.node) this.node.empty();
        delete this.task;
        delete this.node;
        delete this.routeTitleNode;
        delete this.routeArea;
        delete this.opinionTitle;
        delete this.opinionArea;
        delete this.buttonsArea;
        delete this.opinionNode;
        delete this.opinionTextarea;
        delete this.cancelButton;
        delete this.okButton;
    },
    getRouteConfigList: function () {
        if(this.routeConfigList)return this.routeConfigList;

        if (this.task.routeNameDisable){
            this.routeConfigList = [{
                "id": o2.uuid(),
                "asyncSupported": false,
                "soleDirect": false,
                "name": "继续流转",
                "alias": "",
                "selectConfigList": []
            }];
            return this.routeConfigList;
        }

        if( this.form && this.form.businessData && this.form.businessData.routeList ){
            this.form.businessData.routeList.sort( function(a, b){
                var aIdx = parseInt(a.orderNumber || "9999999");
                var bIdx = parseInt(b.orderNumber || "9999999");
                return aIdx - bIdx;
            }.bind(this));
            this.form.businessData.routeList.each( function(d){
                d.selectConfigList = JSON.parse(d.selectConfig || "[]");
            }.bind(this));
            this.routeConfigList = this.form.businessData.routeList;
        }
        if (!this.routeConfigList) {
            o2.Actions.get("x_processplatform_assemble_surface").listRoute({"valueList": this.task.routeList}, function (json) {
                json.data.sort(function(a, b){
                    var aIdx = parseInt(a.orderNumber || "9999999");
                    var bIdx = parseInt(b.orderNumber || "9999999");
                    return aIdx - bIdx;
                }.bind(this));
                json.data.each(function (d) {
                    d.selectConfigList = JSON.parse(d.selectConfig || "[]");
                }.bind(this));
                this.routeConfigList = json.data;
            }.bind(this), null, false);
        }
        return this.routeConfigList;
    },
    getRouteConfig: function (routeId) {
        var routeList = this.getRouteConfigList();
        for (var i = 0; i < routeList.length; i++) {
            if (routeList[i].id === routeId) {
                return routeList[i];
            }
        }
    },
    getMaxOrgLength: function () {
        var routeList = this.getRouteConfigList();
        var length = 0;
        routeList.each(function (route) {
            if (route.hiddenScriptText) { //如果隐藏路由，返回
                if (this.form.Macro.exec(route.hiddenScriptText, this).toString() === "true") return;
            }
            length = Math.max(length, route.selectConfigList.length);
        }.bind(this));
        return length;
    },
    getOrgConfig: function (routeId) {
        var routeList = this.getRouteConfigList();
        for (var i = 0; i < routeList.length; i++) {
            if (routeList[i].id === routeId) {
                return routeList[i].selectConfigList;
            }
        }
    },
    getVisableOrgConfig: function (routeId) {
        var selectConfigList = this.getOrgConfig(routeId);
        var list = [];
        (selectConfigList || []).each(function (config) {
            if (!this.isOrgHidden(config)) {
                list.push(config);
            }
        }.bind(this));
        return list;
    },
    isOrgHidden: function (d) {
        if (d.hiddenScript && d.hiddenScript.code) { //如果隐藏路由，返回
            var hidden = this.form.Macro.exec(d.hiddenScript.code, this);
            if (hidden && hidden.toString() === "true") return true;
        }
        return false;
    },

    isSameArray: function (arr1, arr2) {
        if (arr1.length !== arr2.length) return false;
        for (var i = 0; i < arr1.length; i++) {
            if (arr1[i] !== arr2[i]) return false;
        }
        return true;
    },
    loadOrgs: function (route) {
        if (!this.form || !route) {
            this.orgsArea.hide();
            this.setSize(0);
            return;
        } else {
            this.orgsArea.show();
        }
        if (!this.orgTableObject) this.orgTableObject = {};
        if (!this.orgItemsObject) this.orgItemsObject = {};
        if (!this.orgItemsMap) this.orgItemsMap = {};
        var isLoaded = false;
        for (var key in this.orgTableObject) {
            if (route === key) {
                isLoaded = true;
            } else {
                this.orgTableObject[key].hide();
            }
        }
        if (isLoaded) {
            this.showOrgs(route);
        } else {
            this.createOrgs(route)
        }
    },
    showOrgs: function (route) {
        this.orgItemMap = this.orgItemsMap[route] || {};
        var dataVisable = this.getVisableOrgConfig(route);
        if (dataVisable.length) {
            if (this.isSameArray(Object.keys(this.orgItemMap), dataVisable.map(function (d) { return d.name }))) {
                this.orgTableObject[route].show();
                this.orgItems = this.orgItemsObject[route] || [];
                this.setSize(dataVisable.length);
            } else {
                this.loadOrgTable(route);
            }
        } else {
            this.orgsArea.hide();
            this.orgItemMap = {};
            this.orgItems = [];
            this.setSize(0);
        }
    },
    createOrgs: function (route) {
        var dataVisable = this.getVisableOrgConfig(route);
        if (dataVisable.length) {
            this.loadOrgTable(route);
        } else {
            this.setSize(dataVisable.length);
            this.orgItemMap = {};
            this.orgItems = [];
            this.orgsArea.hide();
        }
    },
    loadOrgTable: function (route) {
        var data = this.getOrgConfig(route);
        var dataVisable = this.getVisableOrgConfig(route);
        this.setSize(dataVisable.length);

        this.orgsArea.show();

        var table_old = this.orgTableObject[route];
        var tdsMap_old = {};
        if (table_old) {
            var tds = table_old.getElements("td");
            tds.each(function (td) {
                tdsMap_old[td.retrieve("orgName")] = td;
            });
        }

        var orgItems_old = this.orgItemsObject[route] || [];
        var orgItemMap_old = this.orgItemsMap[route] || {};

        this.orgItemsObject[route] = [];
        this.orgItemsMap[route] = {};

        this.orgItems = this.orgItemsObject[route];
        this.orgItemMap = this.orgItemsMap[route];

        var len = dataVisable.length;

        var routeOrgTable = new Element("table", {
            "cellspacing": 0, "cellpadding": 0, "border": 0, "width": "100%",
            "styles": this.css.routeOrgTable
        }).inject(this.orgsArea);
        this.orgTableObject[route] = routeOrgTable;

        var lines = ((len + 1) / 2).toInt();
        for (var n = 0; n < lines; n++) {
            var tr = new Element("tr").inject(routeOrgTable);
            new Element("td", {"width": "50%", "valign": "bottom", "styles": this.css.routeOrgOddTd}).inject(tr);
            new Element("td", {"width": "50%", "valign": "bottom", "styles": this.css.routeOrgEvenTd}).inject(tr);
        }

        var trs = routeOrgTable.getElements("tr");

        var ignoreFirstOrgOldData = false; //(routeConfig.type === "appendTask" && routeConfig.appendTaskIdentityType === "select");

        dataVisable.each(function (config, i) {
            var sNode;
            var width;
            if (i + 1 == len && (len % 2 === 1)) {
                sNode = trs[trs.length - 1].getFirst("td");
                sNode.set("colspan", 2);
                trs[trs.length - 1].getLast("td").destroy();
                sNode.setStyle("border", "0px");
                sNode.set("width", "100%");
                sNode.store("orgName", config.name);

                if (orgItemMap_old[config.name]) {
                    var org = orgItemMap_old[config.name];
                    this.orgItems.push(org);
                    this.orgItemMap[config.name] = org;

                    var td = tdsMap_old[config.name];
                    td.getChildren().inject(sNode);
                } else {
                    this.loadOrg(sNode, config, "all", ignoreFirstOrgOldData && i == 0)
                }
            } else {
                var row = ((i + 2) / 2).toInt();
                var tr = trs[row - 1];
                sNode = (i % 2 === 0) ? tr.getFirst("td") : tr.getLast("td");
                sNode.store("orgName", config.name);

                if (orgItemMap_old[config.name]) {
                    var org = orgItemMap_old[config.name];
                    this.orgItems.push(org);
                    this.orgItemMap[config.name] = org;

                    var td = tdsMap_old[config.name];
                    td.getChildren().inject(sNode);
                } else {
                    this.loadOrg(sNode, config, (i % 2 === 0) ? "left" : "right", ignoreFirstOrgOldData && i == 0)
                }
            }
        }.bind(this));
        if (table_old) table_old.destroy();
    },
    loadOrg: function (container, json, position, ignoreOldData) {
        var titleNode = new Element("div.selectorTitle", {
            "styles": this.css.selectorTitle
        }).inject(container);
        var titleTextNode = new Element("div.selectorTitleText", {
            "text": json.title,
            "styles": this.css.selectorTitleText
        }).inject(titleNode);

        var errorNode = new Element("div.selectorErrorNode", {
            "styles": this.css.selectorErrorNode
        }).inject(titleNode);

        var contentNode = new Element("div.selectorContent", {
            "styles": this.css.selectorContent
        }).inject(container);
        var org = new MWF.ProcessFlow.Processor.Org(contentNode, this.form, json, this);
        org.ignoreOldData = ignoreOldData;
        org.errContainer = errorNode;
        org.summitDlalog = this;
        org.load();
        this.orgItems.push(org);
        this.orgItemMap[json.name] = org;

    },
    showOrgsByRoute: function (route) {
        this.loadOrgs(route);
    },
    clearAllOrgs: function () {
        //清空组织所选人
        for (var key in this.orgItemsObject) {
            var orgItems = this.orgItemsObject[key] || [];
            orgItems.each(function (org) {
                org.setDataToOriginal();
            })
        }
        //
        this.orgTableObject = {};
        this.orgItemsObject = {};
        this.orgItemsMap = {};
        this.orgsArea.empty();
    },
    getCurrentRouteSelectorList: function () {
        var selectorList = [];
        var currentRoute = this.selectedRouteNode ? this.selectedRouteNode.retrieve("route") : "";
        var selectedRouteNode = this.orgItemsObject[currentRoute];
        if (!orgList) return [];
        orgList.each(function (org) {
            if (org.selector && org.selector.selector) {
                selectorList.push(org.selector.selector);
            }
        }.bind(this))
        return selectorList;
    },
    getCurrentRouteOrgList: function () {
        var currentRoute = this.selectedRouteNode ? this.selectedRouteNode.retrieve("route") : "";
        var orgList = this.orgItemsObject[currentRoute];
        return orgList || [];
    },
    getSelectorSelectedData: function (filedName) {
        var data = [];
        var orgList = this.getCurrentRouteOrgList();
        for (var i = 0; i < orgList.length; i++) {
            var org = orgList[i];
            if (org.json.name === filedName) {
                var selector = org.selector.selector;
                selector.selectedItems.each(function (item) {
                    data.push(item.data)
                })
            }
        }
        return data;
    },
    getOffsetY: function (node) {
        return (node.getStyle("margin-top").toInt() || 0) +
            (node.getStyle("margin-bottom").toInt() || 0) +
            (node.getStyle("padding-top").toInt() || 0) +
            (node.getStyle("padding-bottom").toInt() || 0) +
            (node.getStyle("border-top-width").toInt() || 0) +
            (node.getStyle("border-bottom-width").toInt() || 0);
    },
    setSize: function (currentOrgLength, flag) {
        var lines = ((currentOrgLength + 1) / 2).toInt();

        var height = 0;
        if (this.routeTitleNode) height = height + this.getOffsetY(this.routeTitleNode) + this.routeTitleNode.getStyle("height").toInt();
        if (this.routeArea) height = height + this.getOffsetY(this.routeArea) + this.routeArea.getStyle("height").toInt();
        if (this.opinionTitle) height = height + this.getOffsetY(this.opinionTitle) + this.opinionTitle.getStyle("height").toInt();
        if (this.opinionArea) height = height + this.getOffsetY(this.opinionArea) + this.opinionArea.getStyle("height").toInt();
        //if( this.buttonsArea )height = height + this.getOffsetY(this.buttonsArea) +  this.buttonsArea.getStyle("height").toInt();

        if (lines > 0) {
            if (this.orgsArea) this.orgsArea.show();

            if (flag) {
                // if( this.orgsTitle )height = height + this.getOffsetY(this.orgsTitle) +  this.orgsTitle.getStyle("height").toInt();
                this.orgsArea.getChildren().each(function (el) {
                    height += el.getSize().y + this.getOffsetY(el);
                }.bind(this))
                this.node.setStyle("height", height);
            } else {
                if (this.orgsTitle) height = height + this.getOffsetY(this.orgsTitle) + this.orgsTitle.getStyle("height").toInt();
                height = height + lines * this.options.orgHeight + this.getOffsetY(this.orgsArea);
                this.node.setStyle("height", height);
            }
        } else {
            if (this.orgsArea) this.orgsArea.hide();
            this.node.setStyle("height", height);
            //this.node.store("height", 401 );
        }
        debugger;
        if (this.getMaxOrgLength() > 1) {
            if( this.options.inFlow ){
                this.node.setStyles(this.css.node_wide_flow);
                this.opinionNode.setStyles(this.css.inputOpinionNode_wide_flow);
                this.opinionTextarea.setStyles(this.css.inputTextarea_wide_flow);
                this.opinionTextareaStyle = this.css.inputTextarea_wide_flow;
                this.selectOpinionNode.setStyles(this.css.selectIdeaNode_wide_flow);
            }else{
                this.node.setStyles(this.css.node_wide);
                this.opinionNode.setStyles(this.css.inputOpinionNode_wide);
                this.opinionTextarea.setStyles(this.css.inputTextarea_wide);
                this.opinionTextareaStyle = this.css.inputTextarea_wide;
                this.selectOpinionNode.setStyles(this.css.selectIdeaNode_wide);
            }

        } else {
            this.node.setStyles(this.css.node);
            this.opinionNode.setStyles(this.css.inputOpinionNode);
            this.opinionTextarea.setStyles(this.css.inputTextarea);
            this.opinionTextareaStyle = this.css.inputTextarea;
            this.selectOpinionNode.setStyles(this.css.selectIdeaNode);
        }
        if (!flag) this.fireEvent("resize");
    },
    isErrorHeightOverflow: function () {
        var hasOverflow = false;
        (this.orgItems || []).each(function (item) {
            if (item.errorHeightOverflow) {
                hasOverflow = true;
            }
        }.bind(this));
        return hasOverflow;
    },
    checkErrorHeightOverflow: function (force) {
        if (force || this.isErrorHeightOverflow()) {
            this.setSize(this.orgItems.length, true);
        }
    },
    errorHeightChange: function () {
        debugger;
        this.checkErrorHeightOverflow(true)
    },
    validationOrgs: function () {
        if (!this.orgItems || !this.orgItems.length) return true;
        var flag = true;
        this.orgItems.each(function (item) {
            if (!item.validation()) flag = false;
        }.bind(this));
        this.checkErrorHeightOverflow();
        return flag;
    },
    isOrgsHasEmpower: function () {
        if (!this.orgItems || !this.orgItems.length) return true;
        var flag = false;
        this.needCheckEmpowerOrg = [];
        this.orgItems.each(function (item) {
            if (item.hasEmpowerIdentity()) {
                this.needCheckEmpowerOrg.push(item);
                flag = true;
            }
        }.bind(this));
        return flag;
    },
    saveOrgs: function (keepSilent) {
        if (!this.orgItems || !this.orgItems.length) return true;
        var flag = true;
        this.orgItems.each(function (item) {
            if (!item.save(!keepSilent)) flag = false;
        }.bind(this));
        return flag;
    },
    saveOrgsWithCheckEmpower: function (callback) {
        var currentRoute = this.selectedRouteNode ? this.selectedRouteNode.retrieve("route") : "";

        var visableOrg = this.getVisableOrgConfig( currentRoute || this.selectedRouteId || "" );
        var needOrgLength = visableOrg.length;

        var loadedOrgLength = 0;
        if ( this.orgItems && this.orgItems.length)loadedOrgLength = this.orgItems.length;

        if( needOrgLength !== loadedOrgLength ){
            MWF.xDesktop.notice(
                "error",
                {"x": "center", "y": "center"},
                MWF.xApplication.process.Work.LP.loadedOrgCountUnexpected,
                this.node,
                {"x": 0, "y": 30},
                {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
            );
            return false;
        }

        if (!this.orgItems || !this.orgItems.length) {
            if (callback) callback();
            return true;
        }
        if (!this.validationOrgs()) return false;

        if (!this.isOrgsHasEmpower()) {
            if (callback) callback();
            return true;
        }
        //this.checkEmpowerMode = true;
        this.showEmpowerDlg(callback);
    },
    showEmpowerDlg: function (callback) {
        //this.empowerMask = new Element("div", {"styles": this.css.handwritingMask}).inject(this.node);

        //this.needCheckEmpowerOrg.each( function(org){
        //    org.saveCheckedEmpowerData();
        //}.bind(this));

        var empowerNode = new Element("div.empowerNode", {"styles": this.css.empowerNode});
        var empowerTitleNode = new Element("div", {
            text: MWF.xApplication.process.Xform.LP.empowerDlgText,
            styles: this.css.empowerTitleNode
        }).inject(empowerNode);

        var orgs = this.needCheckEmpowerOrg;
        var len = orgs.length;
        var lines = ((len + 1) / 2).toInt();

        var empowerTable = new Element("table", {
            "cellspacing": 0, "cellpadding": 0, "border": 0, "width": "100%",
            "styles": this.css.empowerTable
        }).inject(empowerNode);

        for (var n = 0; n < lines; n++) {
            var tr = new Element("tr").inject(empowerTable);
            new Element("td", {"width": "50%", "styles": this.css.empowerOddTd}).inject(tr);
            new Element("td", {"width": "50%", "styles": this.css.empowerEvenTd}).inject(tr);
        }

        var trs = empowerTable.getElements("tr");
        orgs.each(function (org, i) {
            var sNode;
            var width;
            if (i + 1 == len && (len % 2 === 1)) {
                sNode = trs[trs.length - 1].getFirst("td");
                sNode.set("colspan", 2);
                trs[trs.length - 1].getLast("td").destroy();
                width = "50%";
            } else {
                var row = ((i + 2) / 2).toInt();
                var tr = trs[row - 1];
                sNode = (i % 2 === 0) ? tr.getFirst("td") : tr.getLast("td");
            }

            var titleNode = new Element("div.empowerAreaTitle", {
                "styles": this.css.empowerAreaTitle
            }).inject(sNode);

            var titleTextNode = new Element("div.empowerAreaTitleText", {
                "text": org.json.title,
                "styles": this.css.empowerAreaTitleText
            }).inject(titleNode);

            var selectAllNode = new Element("div", {
                styles: {
                    float: "right"
                }
            }).inject(titleNode);

            var contentNode = new Element("div.empowerAreaContent", {
                "styles": this.css.empowerAreaContent
            }).inject(sNode);

            org.loadCheckEmpower(null, contentNode, selectAllNode);

        }.bind(this));

        empowerNode.setStyle("height", lines * this.options.orgHeight + 20);
        //var dlgHeight = Math.min( Math.floor( this.form.app.content.getSize().y * 0.9) , lines*this.options.orgHeight + 151 );

        //var width = this.node.retrieve("width");
        //empowerNode.setStyle( "width", width );
        var width = 840;
        //if( len > 1 ){
        //    width = "840"
        //}else{
        //    width = "420"
        //}
        empowerNode.setStyle("width", width + "px");

        this.node.getParent().mask({
            "style": this.css.mask
        });
        this.empowerDlg = o2.DL.open({
            "title": MWF.xApplication.process.Xform.LP.selectEmpower,
            "style": this.form.json.dialogStyle || "user",
            "isResize": false,
            "content": empowerNode,
            //"container" : this.node,
            "width": width + 40, //600,
            "height": "auto", //dlgHeight,
            "mark": false,
            "onPostLoad": function () {
                if (this.nodeWidth) {
                    this.node.setStyle("width", this.nodeWidth + "px");
                }
                if (this.nodeHeight) {
                    this.node.setStyle("height", this.nodeHeight + "px");
                }
            },
            "buttonList": [
                {
                    "type": "ok",
                    "text": MWF.LP.process.button.ok,
                    "action": function (d, e) {
                        //if (this.empowerDlg) this.empowerDlg.okButton.click();

                        orgs.each(function (org, i) {
                            org.saveCheckedEmpowerData(function () {
                                if (i === orgs.length - 1) {
                                    if (callback) callback();
                                    this.node.getParent().unmask();
                                    this.empowerDlg.close();
                                }
                            }.bind(this))
                        }.bind(this))
                    }.bind(this)
                },
                {
                    "type": "cancel",
                    "text": MWF.LP.process.button.cancel,
                    "action": function () {
                        this.node.getParent().unmask();
                        this.empowerDlg.close();
                    }.bind(this)
                }
            ]
        });
    },
    managerLogin: function (e) {
        debugger;
        var _self = this;
        var user = (this.task.identityDn || this.task.identity).split("@")[0];
        var text = MWF.xApplication.process.Work.LP.managerLoginConfirmContent.replace("{user}", user);
        MWF.xDesktop.confirm("infor", e, MWF.xApplication.process.Work.LP.managerLoginConfirmTitle, text, 450, 120, function () {
            o2.Actions.load("x_organization_assemble_authentication").AuthenticationAction.switchUser({"credential": (_self.task.personDn || _self.task.person)}, function () {
                var text = MWF.xApplication.process.Work.LP.managerLoginSuccess.replace("{user}", user);
                MWF.xDesktop.notice("success", {x: "right", y: "top"}, text);
                window.open(o2.filterUrl("../x_desktop/work.html?workid=" + _self.task.work));
            }.bind(this));
            this.close();
        }, function () {
            this.close();
        }, null, null);
    }

});

MWF.xDesktop.requireApp("process.Xform", "Org", null, false);

MWF.ProcessFlow.Processor.Org = new Class({
    Implements: [Options, Events],
    options: {
        moduleEvents: ["queryLoadSelector", "postLoadSelector", "postLoadContent", "queryLoadCategory", "postLoadCategory",
            "selectCategory", "unselectCategory", "queryLoadItem", "postLoadItem", "selectItem", "unselectItem", "change"]
    },
    initialize: function (container, form, json, processor, options) {
        this.form = form;
        this.json = json;
        this.processor = processor;
        this.container = $(container);
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        this.setOptions(options);
    },
    load: function () {
        var options = this.getOptions();
        if (options) {
            this.selector = new MWF.O2Selector(this.container, options);
        }
    },
    clearTooltip: function(){
        if( this.selector && this.selector.selector && this.selector.selector.clearTooltip ){
            this.selector.selector.clearTooltip();
        }
    },
    _getOrgOptions: function () {
        this.selectTypeList = typeOf(this.json.selectType) == "array" ? this.json.selectType : [this.json.selectType];
        if (this.selectTypeList.contains("identity")) {
            this.identityOptions = new MWF.ProcessFlow.Processor.IdentityOptions(this.form, this.json);
        }
        if (this.selectTypeList.contains("unit")) {
            this.unitOptions = new MWF.ProcessFlow.Processor.UnitOptions(this.form, this.json);
        }
        if( this.selectTypeList.contains( "group" ) ){
            this.groupOptions = new MWF.ProcessFlow.Processor.GroupOptions( this.form, this.json );
        }
    },
    getDefaultOptions: function(){
        return {
            "style": "process",
            "width": "auto",
            "height": "240",
            "embedded": true,
            "hasLetter": false, //字母
            "hasTop": true //可选、已选的标题
        };
    },
    getOptionEvents: function(){
        return {};
    },
    getOptions: function () {
        var _self = this;
        this._getOrgOptions();
        if (this.selectTypeList.length === 0) return false;
        var exclude = [];
        if (this.json.exclude) {
            var v = this.form.Macro.exec(this.json.exclude.code, this);
            exclude = typeOf(v) === "array" ? v : [v];
        }

        var identityOpt;
        if (this.identityOptions) {
            identityOpt = this.identityOptions.getOptions();
            if (this.json.identityRange !== "all") {
                if (!identityOpt.noUnit && (!identityOpt.units || !identityOpt.units.length)) {
                    this.form.notice(MWF.xApplication.process.Xform.LP.noIdentitySelectRange, "error", this.node);
                    identityOpt.disabled = true;
                    // return false;
                }
            }
            if (!identityOpt.noUnit && this.json.dutyRange && this.json.dutyRange !== "all") {
                if (!identityOpt.dutys || !identityOpt.dutys.length) {
                    this.form.notice(MWF.xApplication.process.Xform.LP.noIdentityDutySelectRange, "error", this.node);
                    identityOpt.disabled = true;
                    // return false;
                }
            }
            if (this.ignoreOldData) {
                identityOpt.values = this._computeValue() || [];
            } else {
                identityOpt.values = this.getValue() || [];
            }
            identityOpt.exclude = exclude;
        }

        var unitOpt;
        if (this.unitOptions) {
            unitOpt = this.unitOptions.getOptions();
            if (this.json.unitRange !== "all") {
                if (!unitOpt.units || !unitOpt.units.length) {
                    this.form.notice(MWF.xApplication.process.Xform.LP.noUnitSelectRange, "error", this.node);
                    unitOpt.disabled = true;
                    // return false;
                }
            }
            if (this.ignoreOldData) {
                unitOpt.values = this._computeValue() || [];
            } else {
                unitOpt.values = this.getValue() || [];
            }
            unitOpt.exclude = exclude;
        }

        var groupOpt;
        if( this.groupOptions ){
            groupOpt = this.groupOptions.getOptions();
            if (this.ignoreOldData) {
                groupOpt.values = this._computeValue() || [];
            } else {
                groupOpt.values = this.getValue() || [];
            }
            groupOpt.exclude = exclude;
        }

        var defaultOpt = this.getDefaultOptions();

        if (this.json.events && typeOf(this.json.events) === "object") {
            Object.each(this.json.events, function (e, key) {
                if (e.code) {
                    if (this.options.moduleEvents.indexOf(key) !== -1) {
                        if (key === "postLoadSelector") {
                            this.addEvent("loadSelector", function (selector) {
                                return this.form.Macro.fire(e.code, selector);
                            }.bind(this))
                        } else if (key === "queryLoadSelector") {
                            defaultOpt["onQueryLoad"] = function (target) {
                                return this.form.Macro.fire(e.code, target);
                            }.bind(this)
                        } else {
                            defaultOpt["on" + key.capitalize()] = function (target) {
                                return this.form.Macro.fire(e.code, target);
                            }.bind(this)
                        }
                    }
                }
            }.bind(this));
        }

        if (this.needValid()) {
            defaultOpt["onValid"] = function (selector) {
                this.validOnSelect();
            }.bind(this);
        }

        if (this.form.json.selectorStyle) {
            defaultOpt = Object.merge(Object.clone(this.form.json.selectorStyle), defaultOpt);
            if (this.form.json.selectorStyle.style) defaultOpt.style = this.form.json.selectorStyle.style;
        }

        var events = this.getOptionEvents();

        if (this.selectTypeList.length === 1) {
            var opts = Object.merge(
                defaultOpt,
                {
                    "type": this.selectTypeList[0],
                    "onLoad": function () {
                        //this 为 selector
                        _self.selectOnLoad(this, this.selector)
                    }
                },
                events,
                identityOpt || unitOpt || groupOpt
            )
            return this.filterOptionValues( opts, this.selectTypeList[0] );
        } else if (this.selectTypeList.length > 1) {
            var options = {
                "type": "",
                "types": this.selectTypeList,
                "onLoad": function () {
                    //this 为 selector
                    _self.selectOnLoad(this)
                }
            };
            if (identityOpt) {
                options.identityOptions = Object.merge(
                    defaultOpt, events, identityOpt
                );
            }
            if (unitOpt) {
                options.unitOptions = Object.merge(
                    defaultOpt,  events, unitOpt
                );
            }
            if (groupOpt) {
                options.groupOptions = Object.merge(
                    defaultOpt, events, groupOpt
                );
            }
            return options;
        }
    },
    filterOptionValues: function( options, type ){
        var suffix;
        switch (type) {
            case "identity": suffix = "I"; break;
            case "unit": suffix = "U"; break;
            case "group": suffix = "G"; break;
        }
        options.values = (options.values || []).filter(function (v) {
            if( typeOf(v) === "string" ){
                if( v.contains("@") ){
                    return v.split("@").getLast().toUpperCase() === suffix;
                }else{
                    return true;
                }
            }else if( typeOf(v) === "object" ){
                if( v.distinguishedName ){
                    return v.distinguishedName.split("@").getLast().toUpperCase() === suffix;
                }else{
                    return false;
                }
            }
            return false;
        }.bind(this));
        return options;
    },
    selectOnComplete: function (items) { //移动端才执行
        var array = [];
        items.each(function (item) {
            array.push(item.data);
        }.bind(this));

        var simple = this.json.storeRange === "simple";

        this.checkEmpower(array, function (data) {
            var values = [];
            data.each(function (d) {
                values.push(MWF.org.parseOrgData(d, true, simple));
            }.bind(this));

            this.setData(values);

            //this.validationMode();
            //this.validation();

            this.container.empty();
            this.loadOrgWidget(values, this.container);

            this.selector = null;

            this.fireEvent("select", [items, values]);
        }.bind(this))
    },
    selectOnLoad: function (selector) {
        //if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
        this.fireEvent("loadSelector", [selector])
    },
    selectOnClose: function () {
        var v = this._getBusinessData();
        //if (!v || !v.length) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
    },
    loadOrgWidget: function (value, node) {
        var height = node.getStyle("height").toInt();
        if (node.getStyle("overflow") === "visible" && !height) node.setStyle("overflow", "hidden");
        if (value && value.length) {
            value.each(function (data) {
                if( typeOf(data) === "string" ){
                    data = { distinguishedName : data, name : o2.name.cn(data) };
                }
                var flag = data.distinguishedName.substr(data.distinguishedName.length - 1, 1);
                var copyData = Object.clone(data);
                if (this.json.displayTextScript && this.json.displayTextScript.code) {
                    this.currentData = copyData;
                    var displayName = this.form.Macro.exec(this.json.displayTextScript.code, this);
                    if (displayName) {
                        copyData.displayName = displayName;
                    }
                    this.currentData = null;
                }

                var widget;
                switch (flag.toLowerCase()) {
                    case "i":
                        widget = new MWF.widget.O2Identity(copyData, node, {"style": "xform", "lazy": true});
                        break;
                    case "p":
                        widget = new MWF.widget.O2Person(copyData, node, {"style": "xform", "lazy": true});
                        break;
                    case "u":
                        widget = new MWF.widget.O2Unit(copyData, node, {"style": "xform", "lazy": true});
                        break;
                    case "g":
                        widget = new MWF.widget.O2Group(copyData, node, {"style": "xform", "lazy": true});
                        break;
                    default:
                        widget = new MWF.widget.O2Other(copyData, node, {"style": "xform", "lazy": true});
                }
                widget.field = this;
            }.bind(this));
        }
    },

    hasEmpowerIdentity: function () {
        var data = this.getData();
        if (!this.empowerChecker) this.empowerChecker = new MWF.ProcessFlow.Processor.EmpowerChecker(this.form, this.json, this.processor);
        return this.empowerChecker.hasEmpowerIdentity(data);
    },
    checkEmpower: function (data, callback, container, selectAllNode) {
        if (typeOf(data) === "array" && this.identityOptions && this.json.isCheckEmpower && this.json.identityResultType === "identity") {
            if (!this.empowerChecker) this.empowerChecker = new MWF.ProcessFlow.Processor.EmpowerChecker(this.form, this.json, this.processor);
            this.empowerChecker.selectAllNode = selectAllNode;
            this.empowerChecker.load(data, callback, container);
        } else {
            if (callback) callback(data);
        }
    },

    loadCheckEmpower: function (callback, container, selectAllNode) {
        this.checkEmpower(this.getData(), callback, container, selectAllNode)
    },
    saveCheckedEmpowerData: function (callback) {
        var data = this.getData();
        var simple = this.json.storeRange === "simple";
        //this.empowerChecker.replaceEmpowerIdentity(data, function( newData ){
        this.empowerChecker.setIgnoreEmpowerFlag(data, function (newData) {
            var values = [];
            newData.each(function (d) {
                values.push(MWF.org.parseOrgData(d, true, simple));
            }.bind(this));
            this.setData(values);
            if (callback) callback(values)
        }.bind(this))
    },

    save: function (isValid) {
        if (isValid) {
            if (this.validation()) {
                return true;
            } else {
                this.processor.checkErrorHeightOverflow();
                return false;
            }
        } else {
            this.setData(this.getData());
            return true;
        }
    },

    resetSelectorData: function () {
        if (this.selector && this.selector.selector) {
            this.selector.selector.emptySelectedItems();
            this.selector.selector.options.values = this.getValue() || [];
            this.selector.selector.setSelectedItem();
        }
    },
    setDataToOriginal: function () {
        var v = this._computeValue();
        this.setData(v || "");
    },
    resetData: function () {
        var v = this.getValue() || [];
        //this.setData((v) ? v.join(", ") : "");
        this.setData(v);
    },
    getData: function () {
        return this.getValue();
    },
    getSelectedData: function () {
        var simple = this.json.storeRange === "simple";
        var data = [];
        if (this.selector && this.selector.selector) {
            this.selector.selector.selectedItems.each(function (item) {
                data.push(MWF.org.parseOrgData(item.data, true, simple));
            })
        }
        return data;
    },
    getValue: function () {
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
        return value || "";
    },
    _computeValue: function () {
        var values = [];
        if (this.json.identityValue) {
            this.json.identityValue.each(function (v) {
                if (v) values.push(v)
            });
        }
        if (this.json.unitValue) {
            this.json.unitValue.each(function (v) {
                if (v) values.push(v)
            });
        }
        // if (this.json.groupValue) {
        //     this.json.groupValue.each(function (v) {
        //         if (v) values.push(v)
        //     });
        // }
        if (this.json.dutyValue) {
            var dutys = JSON.decode(this.json.dutyValue);
            var par;
            if (dutys.length) {
                dutys.each(function (duty) {
                    if (duty.code) par = this.form.Macro.exec(duty.code, this);
                    var code = "return this.org.getDuty(\"" + duty.name + "\", \"" + par + "\")";

                    var d = this.form.Macro.exec(code, this);
                    if (typeOf(d) !== "array") d = (d) ? [d.toString()] : [];
                    d.each(function (dd) {
                        if (dd) values.push(dd);
                    });

                }.bind(this));
            }
        }
        if (this.json.defaultValue && this.json.defaultValue.code) {
            var fd = this.form.Macro.exec(this.json.defaultValue.code, this);
            if (typeOf(fd) !== "array") fd = (fd) ? [fd] : [];
            fd.each(function (fdd) {
                if (fdd) {
                    if (typeOf(fdd) === "string") {
                        var data;
                        this.getOrgAction()[this.getValueMethod(fdd)](function (json) {
                            data = json.data
                        }.bind(this), null, fdd, false);
                        values.push(data);
                    } else {
                        values.push(fdd);
                    }
                }
            }.bind(this));
        }
        if (this.json.count > 0) {
            return values.slice(0, this.json.count);
        }
        return values;
        //return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
    },
    getOrgAction: function () {
        if (!this.orgAction) this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        //if (!this.orgAction) this.orgAction = new MWF.xApplication.Selector.Actions.RestActions();
        return this.orgAction;
    },
    setData: function (value) {

        if (!value) return false;
        var oldValues = this.getValue();
        var values = [];

        var simple = this.json.storeRange === "simple";

        var type = typeOf(value);
        if (type === "array") {
            value.each(function (v) {
                var vtype = typeOf(v);
                var data = null;
                if (vtype === "string") {
                    this.getOrgAction()[this.getValueMethod(v)](function (json) {
                        data = MWF.org.parseOrgData(json.data, true, simple);
                    }.bind(this), null, v, false);
                }
                if (vtype === "object") {
                    data = MWF.org.parseOrgData(v, true, simple);
                    if (data.woPerson) delete data.woPerson;
                }
                if (data) values.push(data);
            }.bind(this));
        }
        if (type === "string") {
            var vData;
            this.getOrgAction()[this.getValueMethod(value)](function (json) {
                vData = MWF.org.parseOrgData(json.data, true, simple);
            }.bind(this), null, value, false);
            if (vData) values.push(vData);
        }
        if (type === "object") {
            var vData = MWF.org.parseOrgData(value, true, simple);
            if (vData.woPerson) delete vData.woPerson;
            values.push(vData);
        }

        var change = false;
        if (oldValues.length && values.length) {
            if (oldValues.length === values.length) {
                for (var i = 0; i < oldValues.length; i++) {
                    if ((oldValues[i].distinguishedName !== values[i].distinguishedName) || (oldValues[i].name !== values[i].name) || (oldValues[i].unique !== values[i].unique)) {
                        change = true;
                        break;
                    }
                }
            } else {
                change = true;
            }
        } else if (values.length || oldValues.length) {
            change = true;
        }
        this._setBusinessData(values);
        if (change) this.fireEvent("change");
    },

    getValueMethod: function (value) {
        if (value) {
            var flag = value.substr(value.length - 1, 1);
            switch (flag.toLowerCase()) {
                case "i":
                    return "getIdentity";
                case "p":
                    return "getPerson";
                case "u":
                    return "getUnit";
                case "g":
                    return "getGroup";
                default:
                    return (this.json.selectType === "unit") ? "getUnit" : "getIdentity";
            }
        }
        return (this.json.selectType === "unit") ? "getUnit" : "getIdentity";
    },

    _getBusinessData: function () {
        if (this.json.section == "yes") {
            return this._getBusinessSectionData();
        } else {
            if (this.json.type === "Opinion") {
                return this._getBusinessSectionDataByPerson();
            } else {
                return this.form.businessData.data[this.json.name] || "";
            }
        }
    },
    _getBusinessSectionData: function () {
        switch (this.json.sectionBy) {
            case "person":
                return this._getBusinessSectionDataByPerson();
            case "unit":
                return this._getBusinessSectionDataByUnit();
            case "activity":
                return this._getBusinessSectionDataByActivity();
            case "splitValue":
                return this._getBusinessSectionDataBySplitValue();
            case "script":
                return this._getBusinessSectionDataByScript(this.json.sectionByScript.code);
            default:
                return this.form.businessData.data[this.json.name] || "";
        }
    },
    _getBusinessSectionDataByPerson: function () {
        this.form.sectionListObj[this.json.name] = layout.desktop.session.user.id;
        var dataObj = this.form.businessData.data[this.json.name];
        return (dataObj) ? (dataObj[layout.desktop.session.user.id] || "") : "";
    },
    _getBusinessSectionDataByUnit: function () {
        this.form.sectionListObj[this.json.name] = "";
        var key = (this.form.businessData.task) ? this.form.businessData.task.unit : "";
        if (key) this.form.sectionListObj[this.json.name] = key;
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByActivity: function () {
        this.form.sectionListObj[this.json.name] = "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";
        if (key) this.form.sectionListObj[this.json.name] = key;
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataBySplitValue: function () {
        this.form.sectionListObj[this.json.name] = "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
        if (key) this.form.sectionListObj[this.json.name] = key;
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByScript: function (code) {
        this.form.sectionListObj[this.json.name] = "";
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        var key = this.form.Macro.exec(code, this);
        if (key) this.form.sectionListObj[this.json.name] = key;
        return (key) ? (dataObj[key] || "") : "";
    },

    loadPathData: function (path) {
        var data = null;
        this.form.workAction.getJobDataByPath(this.form.businessData.work.job, path, function (json) {
            data = json.data || null;
        }, null, false);
        return data;
    },

    _setBusinessData: function (v) {
        if (this.json.section == "yes") {
            // var d = this.loadPathData(this.json.name);
            // if (d) this.form.businessData.data[this.json.name] = d;
            this._setBusinessSectionData(v);
        } else {
            if (this.json.type === "Opinion") {
                // var d = this.loadPathData(this.json.name);
                // if (d) this.form.businessData.data[this.json.name] = d;
                this._setBusinessSectionDataByPerson(v);
            } else {
                if (this.form.businessData.data[this.json.name]) {
                    this.form.businessData.data[this.json.name] = v;
                } else {
                    this.form.businessData.data[this.json.name] = v;
                    this.form.Macro.environment.setData(this.form.businessData.data);
                }
                if (this.json.isTitle) this.form.businessData.work.title = v;
            }
        }
    },
    _setBusinessSectionData: function (v) {
        switch (this.json.sectionBy) {
            case "person":
                this._setBusinessSectionDataByPerson(v);
                break;
            case "unit":
                this._setBusinessSectionDataByUnit(v);
                break;
            case "activity":
                this._setBusinessSectionDataByActivity(v);
                break;
            case "splitValue":
                this._setBusinessSectionDataBySplitValue(v);
                break;
            case "script":
                this._setBusinessSectionDataByScript(this.json.sectionByScript.code, v);
                break;
            default:
                if (this.form.businessData.data[this.json.name]) {
                    this.form.businessData.data[this.json.name] = v;
                } else {
                    this.form.businessData.data[this.json.name] = v;
                    this.form.Macro.environment.setData(this.form.businessData.data);
                }
        }
    },
    _setBusinessSectionDataByPerson: function (v) {
        var resetData = false;
        var key = layout.desktop.session.user.id;
        this.form.sectionListObj[this.json.name] = key;

        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) {
            dataObj = {};
            this.form.businessData.data[this.json.name] = dataObj;
            resetData = true;
        }
        if (!dataObj[key]) resetData = true;
        dataObj[key] = v;

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByUnit: function (v) {
        var resetData = false;
        var key = (this.form.businessData.task) ? this.form.businessData.task.unit : "";

        if (key) {
            this.form.sectionListObj[this.json.name] = key;
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj) {
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByActivity: function (v) {
        var resetData = false;
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";

        if (key) {
            this.form.sectionListObj[this.json.name] = key;
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj) {
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataBySplitValue: function (v) {
        var resetData = false;
        var key = (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";

        if (key) {
            this.form.sectionListObj[this.json.name] = key;
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj) {
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByScript: function (code, v) {
        var resetData = false;
        var key = this.form.Macro.exec(code, this);

        if (key) {
            this.form.sectionListObj[this.json.name] = key;
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj) {
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },

    createErrorNode: function (text) {
        var _self = this;
        var node;
        if (this.processor.css.errorContentNode) {
            node = new Element("div", {
                "styles": this.processor.css.errorContentNode,
                "text": text
            });
            if (this.processor.css.errorCloseNode) {
                var closeNode = new Element("div", {
                    "styles": this.processor.css.errorCloseNode,
                    "events": {
                        "click": function () {
                            this.destroy();
                            if (_self.errorHeightOverflow) {
                                _self.errorHeightOverflow = false;
                                _self.processor.errorHeightChange();
                            }
                        }.bind(node)
                    }
                }).inject(node);
            }
        } else {
            node = new Element("div");
            var iconNode = new Element("div", {
                "styles": {
                    "width": "20px",
                    "height": "20px",
                    "float": "left",
                    "background": "url(" + "../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
                }
            }).inject(node);
            var textNode = new Element("div", {
                "styles": {
                    "height": "auto",
                    "min-height": "20px",
                    "line-height": "20px",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "break-all"
                },
                "text": text
            }).inject(node);
        }
        return node;
    },
    notValidationMode: function (text) {
        if (!this.isNotValidationMode) {
            //this.isNotValidationMode = true;
            //this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            //this.node.setStyle("border-color", "red");

            this.errNode = this.createErrorNode(text);
            if (this.errContainer) {
                this.errContainer.empty();
                this.errNode.inject(this.errContainer);
            } else {
                this.errNode.inject(this.container, "after");
            }
            var errorSize = this.errNode.getSize();
            if (!layout.mobile && errorSize.y > 26) {
                this.errorHeightOverflow = true;
            }
        }
    },
    needValid: function () {
        return ((this.json.validationCount && typeOf(this.json.validationCount.toInt()) === "number") ||
            (this.json.validation && this.json.validation.code));
    },
    validOnSelect: function () {
        if (!this.errNode) return true;
        var flag = true;
        if (this.json.validationCount && typeOf(this.json.validationCount.toInt()) === "number") {
            if (this.selector.selector.selectedItems.length < this.json.validationCount.toInt()) {
                flag = MWF.xApplication.process.Xform.LP.selectItemCountNotice.replace("{count}", this.json.validationCount);
            }
        }
        if (flag === true) {
            if (this.json.validation && this.json.validation.code) {
                var data = this.getData();
                this.setData(data);
                flag = this.form.Macro.exec(this.json.validation.code, this);
                if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
            }
        }
        if (flag.toString() != "true") {
            this.notValidationMode(flag);
            this.processor.errorHeightChange();
            return false;
        } else if (this.errNode) {
            this.errNode.destroy();
            this.errNode = null;
            if (this.errorHeightOverflow) {
                this.errorHeightOverflow = false;
                this.processor.errorHeightChange();
            }
        }
        return true;
    },
    validation: function () {
        var data = this.getData();
        this.setData(data);
        var flag = true;
        if (this.json.validationCount && typeOf(this.json.validationCount.toInt()) === "number") {
            if (data.length < this.json.validationCount.toInt()) {
                //"请至少选择" + this.json.validationCount + "项"
                flag = MWF.xApplication.process.Xform.LP.selectItemCountNotice.replace("{count}", this.json.validationCount);
            }
        }

        if (flag === true) {
            if (this.json.validation && this.json.validation.code) {
                flag = this.form.Macro.exec(this.json.validation.code, this);
                if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
            }
        }

        if (flag.toString() != "true") {
            this.notValidationMode(flag);
            return false;
        } else if (this.errNode) {
            this.errNode.destroy();
            this.errNode = null;
        }
        return true;
    }
});

MWF.ProcessFlow.Processor.EmpowerChecker = new Class({
    Extends: MWF.APPOrg.EmpowerChecker,
    initialize: function (form, json, processor) {
        this.form = form;
        this.json = json;
        this.processor = processor;
        this.css = this.processor.css;
        this.checkedAllItems = true;
    },
    load: function (data, callback, container) {
        if (typeOf(data) === "array" && this.json.isCheckEmpower && this.json.identityResultType === "identity") {
            var array = [];
            data.each(function (d) {
                if (d.distinguishedName) {
                    var flag = d.distinguishedName.substr(d.distinguishedName.length - 1, 1).toLowerCase();
                    if (flag === "i") {
                        array.push(d.distinguishedName)
                    }
                }
            }.bind(this));
            if (array.length > 0) {
                o2.Actions.get("x_organization_assemble_express").listEmpowerWithIdentity({
                    "application": (this.form.businessData.work || this.form.businessData.workCompleted).application,
                    "process": (this.form.businessData.work || this.form.businessData.workCompleted).process,
                    "work": (this.form.businessData.work || this.form.businessData.workCompleted).id,
                    "identityList": array
                }, function (json) {
                    var arr = [];
                    json.data.each(function (d) {
                        if (d.fromIdentity !== d.toIdentity) arr.push(d);
                    });
                    if (arr.length > 0) {
                        this.openSelectEmpower(arr, data, callback, container);
                    } else {
                        if (callback) callback(data);
                    }
                }.bind(this), function () {
                    if (callback) callback(data);
                }.bind(this))
            } else {
                if (callback) callback(data);
            }
        } else {
            if (callback) callback(data);
        }
    },
    hasEmpowerIdentity: function (data) {
        var flag = false;
        if (typeOf(data) === "array" && this.json.isCheckEmpower && this.json.identityResultType === "identity") {
            var array = [];
            data.each(function (d) {
                if (d.distinguishedName) {
                    var flag = d.distinguishedName.substr(d.distinguishedName.length - 1, 1).toLowerCase();
                    if (flag === "i") array.push(d.distinguishedName)
                }
            }.bind(this));
            if (array.length > 0) {
                o2.Actions.get("x_organization_assemble_express").listEmpowerWithIdentity({
                    "application": (this.form.businessData.work || this.form.businessData.workCompleted).application,
                    "process": (this.form.businessData.work || this.form.businessData.workCompleted).process,
                    "work": (this.form.businessData.work || this.form.businessData.workCompleted).id,
                    "identityList": array
                }, function (json) {
                    var arr = [];
                    json.data.each(function (d) {
                        if (d.fromIdentity !== d.toIdentity)
                            arr.push(d);
                    });
                    if (arr.length > 0) {
                        flag = true;
                    }
                }.bind(this), null, false)
            }
        }
        return flag;
    },
    openSelectEmpower: function (data, orgData, callback, container) {
        var node = new Element("div", {"styles": this.css.empowerAreaNode});
        //var html = "<div style=\"line-height: 30px; color: #333333; overflow: hidden\">"+MWF.xApplication.process.Xform.LP.empowerDlgText+"</div>";
        var html = "<div style=\"margin-bottom:10px; margin-top:10px; overflow-y:auto;\"></div>";
        node.set("html", html);
        var itemNode = node.getLast();
        this.getEmpowerItems(itemNode, data);
        node.inject(container || this.form.app.content);

        if (this.selectAllNode) {
            var selectNode = this.createSelectAllEmpowerNode();
            selectNode.inject(this.selectAllNode);
            if (this.checkedAllItems) {
                selectNode.store("isSelected", true);
                selectNode.setStyles(this.css.empowerSelectAllItemNode_selected);
            }
        }
    },
    getSelectedData: function (callback) {
        var json = {};
        this.empowerSelectNodes.each(function (node) {
            if (node.retrieve("isSelected")) {
                var d = node.retrieve("data");
                json[d.fromIdentity] = d;
            }
        }.bind(this));
        if (callback) callback(json);
    }
});

MWF.ProcessFlow.Processor.UnitOptions = new Class({
    Extends: MWF.APPOrg.UnitOptions
});

MWF.ProcessFlow.Processor.IdentityOptions = new Class({
    Extends: MWF.APPOrg.IdentityOptions
});

MWF.ProcessFlow.Processor.GroupOptions = new Class({
    Extends: MWF.APPOrg.GroupOptions
});

