MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xDesktop.requireApp("process.Work", "lp." + MWF.language, null, false);

MWF.xApplication.process.Work.Flow = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        style: "default",
        processOptions: {},
    },
    initialize: function (container, task, options, form) {
        this.setOptions(options);

        this.path = "../x_component_process_Work/$Flow/";
        this.cssPath = "../x_component_process_Work/$Flow/" + this.options.style + "/css.wcss";
        this._loadCss();

        this.task = task;
        this.container = $(container);
        this.selectedRoute = null;

        this.form = form;
        this.businessData = this.form.businessData;

        this.load();
    },
    load: function () {
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
                "margin-left": "100px"
            }
        }).inject(this.node);

        if( this.businessData.control["allowProcessing"] ){
            this.processorTitleNode = new Element("div", {
                text: "提交",
                events: {
                    click: function(){ this.changeAction("process") }.bind(this)
                }
            }).inject( this.naviNode );
            this.processorContentNode = new Element("div").inject( this.contentNode );
            this.loadProcessor();
        }
        if( this.businessData.control["allowAddTask"] ){
            this.addTaskTitleNode = new Element("div", {
                text: "加签",
                events: {
                    click: function(){ this.changeAction("addTask") }.bind(this)
                }
            }).inject( this.naviNode );
            this.addTaskContentNode = new Element("div").inject( this.contentNode );
            this.addTaskContentNode.hide();
            this.loadAddTask();
        }
        if( this.businessData.control["allowReset"] ){
            this.resetTitleNode = new Element("div", {
                text: "重置",
                events: {
                    click: function(){ this.changeAction("reset") }.bind(this)
                }
            }).inject( this.naviNode );
            this.resetContentNode = new Element("div").inject( this.contentNode );
            this.resetContentNode.hide();
            this.loadReset();
        }
    },
    changeAction: function( action ){
        switch (action) {
            case "process":
                this.processorContentNode.show();
                this.addTaskContentNode.hide();
                this.resetContentNode.hide();
                break;
            case "addTask":
                this.processorContentNode.hide();
                this.addTaskContentNode.show();
                this.resetContentNode.hide();
                break;
            case "reset":
                this.processorContentNode.hide();
                this.addTaskContentNode.hide();
                this.resetContentNode.show();
                break;
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
        this.processor = new MWF.xApplication.process.Work.Flow.Processor(
            this.processorContentNode,
            this.task,
            processOptions,
            this.form
        );
    },
    loadReset: function(){
        this.reset = new MWF.xApplication.process.Work.Flow.Reset(
            this.resetContentNode,
            this.task,
            {},
            this.form
        );
    },
    loadAddTask: function(){
        this.addTask = new MWF.xApplication.process.Work.Flow.AddTask(
            this.addTaskContentNode,
            this.task,
            {},
            this.form
        );
    },
    destroy: function () {
        if( this.processor )this.processor.destroy();
        if( this.reset )this.reset.destroy();
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
    }
});

MWF.xApplication.process.Work.Flow.Processor = new Class({
    Extends: MWF.xApplication.process.Work.Processor
});

MWF.xApplication.process.Work.Flow.Reset = new Class({
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
        if (layout.mobile) {
            this.content = new Element("div").inject(this.container);
        } else {
            this.content = this.container;
        }

        this.routeOpinionTile = new Element("div", {
            "styles": this.css.routeOpinionTile,
            "text": "重置意见"
        }).inject(this.content);
        this.routeOpinionArea = new Element("div", {"styles": this.css.routeOpinionArea}).inject(this.content);

        this.setOpinion();

        if (layout.mobile) {
            this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.content);
            this.orgsTile = new Element("div", {
                "styles": this.css.orgsTitle,
                "text": "重置给"
            }).inject(this.orgsArea);
            this.orgsArea.hide();
        } else {
            this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.content);
            this.orgsTile = new Element("div", {
                "styles": this.css.orgsTitle,
                "text": "重置给"
            }).inject(this.orgsArea);
        }

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
        if (this.selectedRoute) {
            if (this.selectedRoute.get("text") != node.get("text")) {
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
        if (this.selectedRoute) {
            if (this.selectedRoute.get("text") != node.get("text")) {
                node.setStyles(this.css.routeNode);
                node.removeClass("lightColor_bg");
            }
        } else {
            node.setStyles(this.css.routeNode);
            node.removeClass("lightColor_bg");
        }
    },
    selectRoute: function (node) {
        if (this.selectedRoute) {
            if (this.selectedRoute.get("text") != node.get("text")) { //选中其他路由
                this.selectedRoute.setStyles(this.css.routeNode);
                this.selectedRoute.removeClass("mainColor_bg");

                node.setStyles(this.css.routeNode_selected);
                node.addClass("mainColor_bg");
                node.removeClass("lightColor_bg");

            } else { //取消选中当前路由
                if( this.options.useDefaultOpinion ) {
                    if (this.inputTextarea.get("value") === this.getDefaultOpinion(this.selectedRoute)) {
                        this.lastDefaultOpinion = "";
                        this.inputTextarea.set("value", MWF.xApplication.process.Work.LP.inputText || "");
                    }
                }
                this.selectedRoute.setStyles(this.css.routeNode);
                this.selectedRoute.addClass("lightColor_bg");
                this.selectedRoute.removeClass("mainColor_bg");
                this.selectedRoute = null;
            }
        } else {
            this.selectedRoute = node;
            node.setStyles(this.css.routeNode_selected);
            node.addClass("mainColor_bg");
            node.removeClass("lightColor_bg");
        }
        this.routeSelectorArea.setStyle("background-color", "#FFF");

    },
    setOpinion: function () {
        this.selectIdeaNode = new Element("div", {"styles": this.css.selectIdeaNode}).inject(this.routeOpinionArea);
        this.selectIdeaScrollNode = new Element("div", {"styles": this.css.selectIdeaScrollNode}).inject(this.selectIdeaNode);
        this.selectIdeaAreaNode = new Element("div", {
            "styles": {
                "overflow": "hidden"
            }
        }).inject(this.selectIdeaScrollNode);

        this.inputOpinionNode = new Element("div", {"styles": this.css.inputOpinionNode}).inject(this.routeOpinionArea);
        this.inputTextarea = new Element("textarea", {
            "styles": this.css.inputTextarea,
            "value": this.options.opinion || MWF.xApplication.process.Work.LP.inputText
        }).inject(this.inputOpinionNode);
        this.inputTextarea.setStyle("resize", "none");
        this.inputTextarea.addEvents({
            "focus": function () {
                if (this.get("value") == MWF.xApplication.process.Work.LP.inputText) this.set("value", "");
            },
            "blur": function () {
                if (!this.get("value")) this.set("value", MWF.xApplication.process.Work.LP.inputText);
            },
            "keydown": function () {
                this.inputTextarea.setStyles(this.inputTextareaStyle || this.css.inputTextarea);
            }.bind(this)
        });

        MWF.require("MWF.widget.ScrollBar", function () {
            new MWF.widget.ScrollBar(this.selectIdeaScrollNode, {
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
                        if (_self.inputTextarea.get("value") == MWF.xApplication.process.Work.LP.inputText) {
                            _self.inputTextarea.set("value", this.get("text"));
                        } else {
                            _self.inputTextarea.set("value", _self.inputTextarea.get("value") + ", " + this.get("text"));
                        }
                    },
                    "dblclick": function () {
                        if (_self.inputTextarea.get("value") == MWF.xApplication.process.Work.LP.inputText) {
                            _self.inputTextarea.set("value", this.get("text"));
                        } else {
                            _self.inputTextarea.set("value", _self.inputTextarea.get("value") + ", " + this.get("text"));
                        }
                    },
                    "mouseover": function () {
                        this.setStyles(_self.css.selectIdeaItemNode_over);
                    },
                    "mouseout": function () {
                        this.setStyles(_self.css.selectIdeaItemNode);
                    }
                }
            }).inject(this.selectIdeaAreaNode);
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
            if (!this.form && this.options.isManagerProcess) {
                this.submit_withoutForm(ev)
            }else if (layout.mobile) {
                this.submit_mobile(ev)
            } else {
                this.submit_pc(ev)
            }
        }.bind(this));
    },
    loadOrg: function(){
        this.getSelectorOptions( function (options) {
            this.selector = new MWF.O2Selector(this.orgsArea, options)
        }.bind(this) );
    },
    getSelectorOptions: function( callback ){
        o2.Actions.get("x_processplatform_assemble_surface").listTaskByWork(this.businessData.work.id, function(json){
            var identityList = [];
            json.data.each(function(task){
                identityList.push(task.identity);
            });
            this._getSelectorOptions(identityList, callback);
        }.bind(this))
    },
    _getSelectorOptions: function (exclude, callback) {
        var options = this.getDefaultOptions();

        var range = this.businessData.activity.resetRange || "department";
        var selectorOptions;
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

MWF.xApplication.process.Work.Flow.AddTask = new Class({
    Extends: MWF.xApplication.process.Work.Flow.Reset,
    load: function(){
        if (layout.mobile) {
            this.content = new Element("div").inject(this.container);
        } else {
            this.content = this.container;
        }

        this.routeOpinionTile = new Element("div", {
            "styles": this.css.routeOpinionTile,
            "text": "加签意见"
        }).inject(this.content);
        this.routeOpinionArea = new Element("div", {"styles": this.css.routeOpinionArea}).inject(this.content);

        this.setOpinion();

        if (layout.mobile) {
            this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.content);
            this.orgsTile = new Element("div", {
                "styles": this.css.orgsTitle,
                "text": "加签人"
            }).inject(this.orgsArea);
            this.orgsArea.hide();
        } else {
            this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.content);
            this.orgsTile = new Element("div", {
                "styles": this.css.orgsTitle,
                "text": "加签人"
            }).inject(this.orgsArea);
        }

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

