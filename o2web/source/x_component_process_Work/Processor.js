MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xDesktop.requireApp("process.Work", "lp." + MWF.language, null, false);

MWF.xApplication.process.Work.Processor = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "mediaNode": null,
        "opinion": "",
        "tabletWidth": 0,
        "tabletHeight": 0,
        "orgHeight": 276,
        "maxOrgCountPerline": 2,
        "isManagerProcess": false //是否为管理员提交
    },

    initialize: function (node, task, options, form) {
        this.setOptions(options);

        this.path = "../x_component_process_Work/$Processor/";
        this.cssPath = "../x_component_process_Work/$Processor/" + this.options.style + "/css.wcss";
        this._loadCss();

        this.task = task;
        this.node = $(node);
        this.selectedRoute = null;

        this.form = form;

        this.load();
    },
    load: function () {
        if (layout.mobile) {
            this.content = new Element("div").inject(this.node);
        } else {
            this.content = this.node;
        }
        if (!this.form && this.options.isManagerProcess) {
            this.managerProcessNoticeNode = new Element("div", {
                "styles": this.css.managerProcessNoticeNode,
                "html": MWF.xApplication.process.Work.LP.managerProcessNotice
            }).inject(this.content);
            this.managerLoginNode = new Element("div", {
                "styles": this.css.managerLoginNode,
                "text": MWF.xApplication.process.Work.LP.managerLogin
            }).inject(this.content);

            this.managerLoginNode.addEvent("click", function (ev) {
                this.managerLogin(ev);
            }.bind(this));

            //var text = MWF.xApplication.process.Work.LP.managerLoginReturn.replace( "{user}", layout.session.user.name );
            //this.managerLoginReturnNode = new Element("div", {"styles": this.css.managerLoginNode, "text": text }).inject(this.content);
            //this.managerLoginReturnNode.hide();
            //this.managerPerson = layout.session.user.distinguishedName;
            //this.managerLoginReturnNode.addEvent("click", function(ev){
            //    this.managerLoginReturn(ev);
            //}.bind(this))
        }

        this.routeOpinionTile = new Element("div", {
            "styles": this.css.routeOpinionTile,
            "text": MWF.xApplication.process.Work.LP.inputOpinion
        }).inject(this.content);
        this.routeOpinionArea = new Element("div", {"styles": this.css.routeOpinionArea}).inject(this.content);

        this.setOpinion();

        if (this.form) {
            if (layout.mobile) {
                this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.content);
                this.orgsTile = new Element("div", {
                    "styles": this.css.orgsTitle,
                    "text": MWF.xApplication.process.Work.LP.selectPerson
                }).inject(this.orgsArea);
                this.orgsArea.hide();
            } else {
                this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.content);
                this.orgsTile = new Element("div", {
                    "styles": this.css.orgsTitle,
                    "text": MWF.xApplication.process.Work.LP.selectPerson
                }).inject(this.orgsArea);
            }
        }

        if (layout.mobile) {
            this.buttonsArea = new Element("div", {"styles": this.css.buttonsArea}).inject(this.node);
        } else {
            this.buttonsArea = new Element("div", {"styles": this.css.buttonsArea}).inject(this.content);
        }
        this.setButtons();

        if (this.form) {
            if (layout.mobile) {
                this.getRouteGroupList();
                if (this.hasDecisionOpinion) {
                    this.routeContainer = new Element("div", {
                        "styles": this.css.routeContainer
                    }).inject(this.routeOpinionTile, "before");
                    this.routeGroupTitle = new Element("div", {
                        "styles": this.css.routeSelectorTile,
                        "text": MWF.xApplication.process.Work.LP.selectRouteGroup
                    }).inject(this.routeContainer);
                    this.routeGroupArea = new Element("div", {"styles": this.css.routeSelectorArea}).inject(this.routeContainer);

                    this.routeSelectorTile = new Element("div", {
                        "styles": this.css.routeSelectorTile,
                        "text": MWF.xApplication.process.Work.LP.selectRoute
                    }).inject(this.routeContainer);
                    this.routeSelectorArea = new Element("div", {"styles": this.css.routeSelectorArea}).inject(this.routeContainer);

                    this.setRouteGroupList();
                } else {
                    this.routeSelectorTile = new Element("div", {
                        "styles": this.css.routeSelectorTile,
                        "text": MWF.xApplication.process.Work.LP.selectRoute
                    }).inject(this.routeOpinionTile, "before");
                    this.routeSelectorArea = new Element("div", {"styles": this.css.routeSelectorArea}).inject(this.routeSelectorTile, "after");
                    this.setRouteList();
                }
            } else {
                this.getRouteGroupList();
                if (this.hasDecisionOpinion) {
                    //if( this.getMaxOrgLength() > 1 ){
                    this.routeContainer = new Element("div", {
                        "styles": this.css.routeContainer
                    }).inject(this.routeOpinionTile, "before");

                    this.routeLeftWarper = new Element("div", {
                        "styles":
                            this.getMaxOrgLength() > 1 ? this.css.routeLeftWarper : this.css.routeLeftWarper_single
                    }).inject(this.routeContainer);
                    this.routeGroupTitle = new Element("div", {
                        "styles": this.css.routeSelectorTile,
                        "text": MWF.xApplication.process.Work.LP.selectRouteGroup
                    }).inject(this.routeLeftWarper);
                    this.routeGroupArea = new Element("div", {"styles": this.css.routeSelectorArea_hasGroup}).inject(this.routeLeftWarper);

                    this.routeRightWarper = new Element("div", {
                        "styles":
                            this.getMaxOrgLength() > 1 ? this.css.routeRightWarper : this.css.routeRightWarper_single
                    }).inject(this.routeContainer);
                    this.routeSelectorTile = new Element("div", {
                        "styles": this.css.routeSelectorTile,
                        "text": MWF.xApplication.process.Work.LP.selectRoute
                    }).inject(this.routeRightWarper);
                    this.routeSelectorArea = new Element("div", {"styles": this.css.routeSelectorArea_hasGroup}).inject(this.routeRightWarper);
                    this.setRouteGroupList();
                    //}else{
                    //    this.routeGroupTile = new Element("div", {"styles": this.css.routeSelectorTile, "text": MWF.xApplication.process.Work.LP.selectRoute }).inject(this.routeOpinionTile, "before");
                    //    this.routeGroupArea = new Element("div", {"styles": this.css.routeSelectorArea_hasGroup_wide }).inject(this.routeGroupTile, "after");
                    //    this.setRouteGroupList();
                    //}
                } else {
                    this.routeSelectorTile = new Element("div", {
                        "styles": this.css.routeSelectorTile,
                        "text": MWF.xApplication.process.Work.LP.selectRoute
                    }).inject(this.routeOpinionTile, "before");
                    this.routeSelectorArea = new Element("div", {"styles": this.css.routeSelectorArea}).inject(this.routeSelectorTile, "after");
                    this.setRouteList();
                }
            }
        } else { //快速处理
            this.routeSelectorTile = new Element("div", {
                "styles": this.css.routeSelectorTile,
                "text": MWF.xApplication.process.Work.LP.selectRoute
            }).inject(this.routeOpinionTile, "before");
            this.routeSelectorArea = new Element("div", {"styles": this.css.routeSelectorArea}).inject(this.routeSelectorTile, "after");
            this.setRouteList_noform();
            this.setSize_noform();
        }

        this.fireEvent("postLoad");
    },
    getRouteGroupList: function () {
        if (this.routeGroupObject) return this.routeGroupObject;
        this.routeGroupObject = {};
        this.routeGroupNameList = [];
        this.hasDecisionOpinion = false;
        var routeList = this.getRouteDataList();
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
                this.hasDecisionOpinion = true;
                var decisionOpinionList = route.decisionOpinion.split("#");
                decisionOpinionList.each(function (decisionOption) {
                    this.routeGroupNameList.combine([decisionOption]);
                    var d = this.splitByStartNumber(decisionOption);
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
        var _self = this;
        //var keys = Object.keys( this.routeGroupObject );
        //var length = keys.length;
        //var sortArray = MWF.xApplication.process.Work.LP.routeGroupOrderList;
        //keys.sort( function( a, b ){
        //    var aIdx = sortArray.indexOf(a);
        //    var bIdx = sortArray.indexOf(b);
        //    if( aIdx === -1 )aIdx = sortArray.length;
        //    if( bIdx === -1 )aIdx = sortArray.length;
        //    return aIdx - bIdx;
        //});

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

        var flag = false;
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
            } else {
                flag = true;
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
                //this.selectedRouteGroup.setStyles(this.css.routeNode);
                //this.selectedRouteGroup.getFirst().setStyles(this.css.routeIconNode);
                //this.selectedRouteGroup.getLast().setStyles(this.css.routeTextNode);
                //
                //this.selectedRouteGroup = null;
            }
        } else {
            this.selectedRouteGroup = node;
            node.setStyles(this.css.routeGroupNode_selected);
            //this.selectedRouteGroup.addClass("mainColor_bg");

            var routeList = this.selectedRouteGroup.retrieve("routeList");
            this.setRouteList(routeList);
        }
        this.routeGroupArea.setStyle("background-color", "#FFF");
    },
    setRouteList_noform: function (routeList) {
        var _self = this;
        this.routeSelectorArea.empty();
        this.selectedRoute = null;

        //this.task.routeNameList = ["送审核", "送办理", "送公司领导阅"];
        if (!routeList) routeList = this.getRouteDataList();
        routeList.each(function (route, i) {
            var routeName = route.name;
            var routeNode = new Element("div", {
                "styles": this.css.routeNode,
                "text": routeName
            }).inject(this.routeSelectorArea);
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
                    _self.selectRoute_noform(this);
                }
            });

            if (routeList.length == 1 || route.sole) {
                this.selectRoute_noform(routeNode);
            }

        }.bind(this));
    },
    setRouteList: function (routeList) {
        var _self = this;
        //if( this.hasDecisionOpinion && this.getMaxOrgLength() === 1 ){
        //    if( this.routeSelectorArea )this.routeSelectorArea.destroy();
        //    this.routeSelectorArea = new Element("div", { styles : this.css.routeSelectorArea_hasGroup_single }).inject( this.selectedRouteGroup, "after" );
        //}else{
        this.routeSelectorArea.empty();
        //}
        this.selectedRoute = null;
        //this.task.routeNameList = ["送审核", "送办理", "送公司领导阅"];
        if (!routeList) routeList = this.getRouteDataList();
        //this.task.routeNameList.each(function(route, i){
        var isSelected = false;
        routeList.each(function (route, i) {
            if (route.hiddenScriptText && this.form && this.form.Macro) {
                if (this.form.Macro.exec(route.hiddenScriptText, this).toString() === "true") return;
            }
            var routeName = route.name;
            if (route.displayNameScriptText && this.form && this.form.Macro) {
                routeName = this.form.Macro.exec(route.displayNameScriptText, this);
            }
            var routeNode = new Element("div", {
                "styles": this.css.routeNode,
                "text": routeName
            }).inject(this.routeSelectorArea);
            //var routeIconNode = new Element("div", {"styles": this.css.routeIconNode}).inject(routeNode);
            //var routeTextNode = new Element("div", {"styles": this.css.routeTextNode, "text": routeName}).inject(routeNode);
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

            if (routeList.length == 1 || route.sole) { //sole表示优先路由
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
    selectRoute_noform: function (node) {
        if (this.selectedRoute) {
            if (this.selectedRoute.get("text") != node.get("text")) {
                this.selectedRoute.setStyles(this.css.routeNode);
                this.selectedRoute.removeClass("mainColor_bg");

                this.selectedRoute = node;
                node.setStyles(this.css.routeNode_selected);
                node.addClass("mainColor_bg");
                node.removeClass("lightColor_bg");

            } else {
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
    selectRoute: function (node) {
        if (this.selectedRoute) {
            if (this.selectedRoute.get("text") != node.get("text")) {
                this.selectedRoute.setStyles(this.css.routeNode);
                this.selectedRoute.removeClass("mainColor_bg");
                //this.selectedRoute.getFirst().setStyles(this.css.routeIconNode);
                //this.selectedRoute.getLast().setStyles(this.css.routeTextNode);

                this.selectedRoute = node;
                node.setStyles(this.css.routeNode_selected);
                node.addClass("mainColor_bg");
                node.removeClass("lightColor_bg");
                //node.setStyle("background-color", "#da7429");
                //node.getFirst().setStyle("background-image", "url("+"../x_component_process_Work/$Processor/default/checked.png)");
                //node.getLast().setStyle("color", "#FFF");

            } else {
                this.selectedRoute.setStyles(this.css.routeNode);
                this.selectedRoute.addClass("lightColor_bg");
                this.selectedRoute.removeClass("mainColor_bg");
                //this.selectedRoute.getFirst().setStyles(this.css.routeIconNode);
                //this.selectedRoute.getLast().setStyles(this.css.routeTextNode);

                this.selectedRoute = null;
            }
        } else {
            this.selectedRoute = node;
            node.setStyles(this.css.routeNode_selected);
            node.addClass("mainColor_bg");
            node.removeClass("lightColor_bg");
            //node.setStyle("background-color", "#da7429");
            //node.getFirst().setStyle("background-image", "url("+"../x_component_process_Work/$Processor/default/checked.png)");
            //node.getLast().setStyle("color", "#FFF");
        }
        this.routeSelectorArea.setStyle("background-color", "#FFF");
        if (layout.mobile) {
            this.loadOrgs_mobile(this.selectedRoute ? this.selectedRoute.retrieve("route") : "");
        } else {
            this.loadOrgs(this.selectedRoute ? this.selectedRoute.retrieve("route") : "");
        }

        //临时添加
        if (this.form.data.json.events && this.form.data.json.events.afterSelectRoute) {
            this.form.Macro.exec(this.form.data.json.events.afterSelectRoute.code, node);
        }

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

        this.mediaActionArea = new Element("div", {"styles": this.css.inputOpinionMediaActionArea}).inject(this.inputOpinionNode);
        this.handwritingAction = new Element("div", {
            "styles": this.css.inputOpinionHandwritingAction,
            "text": MWF.xApplication.process.Work.LP.handwriting
        }).inject(this.mediaActionArea);
        this.handwritingAction.addEvent("click", function () {
            if (layout.mobile) {
                window.setTimeout(function () {
                    this.handwriting();
                }.bind(this), 100)
            } else {
                this.handwriting();
            }
        }.bind(this));

        // if (navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia){
        //     this.audioRecordAction = new Element("div", {"styles": this.css.inputOpinionAudioRecordAction, "text": MWF.xApplication.process.Work.LP.audioRecord}).inject(this.mediaActionArea);
        //     this.audioRecordAction.addEvent("click", function(){
        //         this.audioRecord();
        //     }.bind(this));
        // }

        if (layout.mobile) {
            this.selectIdeaNode.inject(this.routeOpinionArea, "after");
        }

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
        if (layout.mobile) {
            this.handwritingNode.setStyles({
                "top": "0px",
                "left": "0px"
            });
        } else {
            this.handwritingNode.position({
                "relativeTo": this.options.mediaNode || this.node,
                "position": "center",
                "edge": "center"
            });
        }
    },
    createHandwriting: function () {
        this.handwritingNodeMask = new Element("div.handwritingMask", {"styles": this.css.handwritingMask}).inject(this.node);

        this.handwritingNode = new Element("div.handwritingNode", {"styles": this.css.handwritingNode}).inject(this.node, "after");
        //var size = (this.options.mediaNode || this.node).getSize();
        //var y = size.y;
        //var x = size.x;
        //兼容以前的默认高宽
        var x = 600;
        var y = 320;
        if (!layout.mobile) {

            x = Math.max(this.options.tabletWidth || x, 500);
            y = Math.max(this.options.tabletHeight ? (parseInt(this.options.tabletHeight) + 110) : y, 320);

            //y = Math.max(size.y, 320);
            //x = Math.max(size.x, 480);
        } else {
            var bodySize = $(document.body).getSize();
            x = bodySize.x;
            y = bodySize.y;
            this.options.tabletWidth = 0;
            this.options.tabletHeight = 0;
        }
        // for (k in this.node.style){
        //     if (this.node.style[k]) this.handwritingNode.style[k] = this.node.style[k];
        // }
        var zidx = this.node.getStyle("z-index");
        this.handwritingNode.setStyles({
            "height": "" + y + "px",
            "width": "" + x + "px",
            "z-index": zidx + 1
        });
        if (layout.mobile) {
            debugger;
            this.handwritingNode.addEvent('touchmove', function (e) {
                e.preventDefault();
            });
            this.handwritingNode.setStyles({
                "top": "0px",
                "left": "0px"
            });
            //this.handwritingNode.position({
            //    "relativeTo": this.node,
            //    "position": "center",
            //    "edge": "center"
            //});
        } else {
            this.handwritingNode.position({
                "relativeTo": this.options.mediaNode || this.node,
                "position": "center",
                "edge": "center"
            });
        }
        this.handwritingAreaNode = new Element("div", {"styles": this.css.handwritingAreaNode}).inject(this.handwritingNode);
        this.handwritingActionNode = new Element("div", {
            "styles": this.css.handwritingActionNode,
            "text": MWF.xApplication.process.Work.LP.saveWrite
        }).inject(this.handwritingNode);
        var h = this.handwritingActionNode.getSize().y + this.handwritingActionNode.getStyle("margin-top").toInt() + this.handwritingActionNode.getStyle("margin-bottom").toInt();
        h = y - h;
        this.handwritingAreaNode.setStyle("height", "" + h + "px");

        MWF.require("MWF.widget.Tablet", function () {
            var handWritingOptions = {
                "style": "default",
                "contentWidth": this.options.tabletWidth || 0,
                "contentHeight": this.options.tabletHeight || 0,
                "onSave": function (base64code, base64Image, imageFile) {
                    this.handwritingFile = imageFile;
                    this.handwritingNode.hide();
                    this.handwritingNodeMask.hide();
                    // this.page.get("div_image").node.set("src",base64Image);

                }.bind(this),
                "onCancel": function () {
                    this.handwritingFile = null;
                    this.handwritingNode.hide();
                    this.handwritingNodeMask.hide();
                }.bind(this)
            };
            if (layout.mobile) {
                handWritingOptions.tools = [
                    "undo",
                    "redo", "|",
                    "reset", "|",
                    "size",
                    "cancel"
                ]
            }
            this.tablet = new MWF.widget.Tablet(this.handwritingAreaNode, handWritingOptions, null);
            this.tablet.load();
        }.bind(this));

        this.handwritingActionNode.addEvent("click", function () {
            //this.handwritingNode.hide();
            if (this.tablet) this.tablet.save();
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
            if (layout.mobile) {
                this.submit_mobile(ev)
            } else {
                this.submit_pc(ev)
            }
        }.bind(this));
    },
    submit_mobile: function (ev) {
        if (this.hasDecisionOpinion && !this.selectedRouteGroup) {
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

        if (!this.selectedRoute) {
            this.routeSelectorArea.setStyle("background-color", "#ffe9e9");
            new mBox.Notice({
                type: "error",
                position: {"x": "center", "y": "top"},
                move: false,
                target: this.routeSelectorArea,
                delayClose: 6000,
                content: MWF.xApplication.process.Work.LP.mustSelectRoute
            });
            return false;
        }
        var routeName = this.selectedRoute.retrieve("routeName") || this.selectedRoute.get("text");
        var opinion = this.inputTextarea.get("value");
        if (opinion === MWF.xApplication.process.Work.LP.inputText) opinion = "";
        var medias = [];
        if (this.handwritingFile) medias.push(this.handwritingFile);
        if (this.soundFile) medias.push(this.soundFile);
        if (this.videoFile) medias.push(this.videoFile);

        var currentRouteId = this.selectedRoute.retrieve("route");
        var routeData = this.getRouteData(currentRouteId);
        if (!opinion && medias.length === 0) {
            if (routeData.opinionRequired == true) {
                this.inputTextarea.setStyle("background-color", "#ffe9e9");
                new mBox.Notice({
                    type: "error",
                    position: {"x": "center", "y": "top"},
                    move: false,
                    target: this.inputTextarea,
                    delayClose: 6000,
                    content: MWF.xApplication.process.Work.LP.opinionRequired
                });
                return false;
            }
        }

        if (routeData.validationScriptText) {
            var validation = this.form.Macro.exec(routeData.validationScriptText, this);
            if (!validation || validation.toString() !== "true") {
                if (typeOf(validation) === "string") {
                    new mBox.Notice({
                        type: "error",
                        position: {"x": "center", "y": "top"},
                        move: false,
                        target: this.node,
                        delayClose: 6000,
                        content: validation
                    });
                    return false;
                } else {
                    //"路由校验失败"
                    new mBox.Notice({
                        type: "error",
                        position: {"x": "center", "y": "top"},
                        move: false,
                        target: this.node,
                        delayClose: 6000,
                        content: MWF.xApplication.process.Work.LP.routeValidFailure
                    });
                    return false;
                }
            }
        }

        //var array = [routeName, opinion, medias];
        //this.node.mask({
        //    "inject": {"where": "bottom", "target": this.node},
        //    "destroyOnHide": true,
        //    "style": {
        //        "background-color": "#999",
        //        "opacity": 0.3,
        //        "z-index":600
        //    }
        //});
        //this.fireEvent("submit", array );

        var appendTaskOrgItem;
        if (routeData.type === "appendTask" && routeData.appendTaskIdentityType === "select") {
            if (!this.orgItems || this.orgItems.length === 0) {
                new mBox.Notice({
                    type: "error",
                    position: {"x": "center", "y": "top"},
                    move: false,
                    target: this.orgsArea,
                    delayClose: 6000,
                    content: MWF.xApplication.process.Work.LP.noAppendTaskIdentityConfig //"没有配置转交人，请联系管理员"
                });
                return false;
            } else {
                appendTaskOrgItem = this.orgItems[0]
            }
        }

        if (!this.saveOrgs()) return false;

        //this.saveOrgsWithCheckEmpower( function(){
        var appandTaskIdentityList;
        if (appendTaskOrgItem) {
            appandTaskIdentityList = appendTaskOrgItem.getData();
            if (!appandTaskIdentityList || appandTaskIdentityList.length === 0) {
                new mBox.Notice({
                    type: "error",
                    position: {"x": "center", "y": "top"},
                    move: false,
                    target: this.orgsArea,
                    delayClose: 6000,
                    content: MWF.xApplication.process.Work.LP.selectAppendTaskIdentityNotice //"请选择转交人"
                });
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
        //}.bind(this))
    },
    submit_pc: function (ev) {
        if (this.hasDecisionOpinion && !this.selectedRouteGroup) {
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

        if (!this.selectedRoute) {
            this.routeSelectorArea.setStyle("background-color", "#ffe9e9");
            //new mBox.Notice({
            //    type: "error",
            //    position: {"x": "center", "y": "top"},
            //    move: false,
            //    target: this.routeSelectorArea,
            //    delayClose: 6000,
            //    content: MWF.xApplication.process.Work.LP.mustSelectRoute
            //});
            MWF.xDesktop.notice(
                "error",
                {"x": "center", "y": "top"},
                MWF.xApplication.process.Work.LP.mustSelectRoute,
                this.routeSelectorArea,
                null,  //{"x": 0, "y": 30}
                {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
            );
            return false;
        }
        var routeName = this.selectedRoute.retrieve("routeName") || this.selectedRoute.get("text");
        var opinion = this.inputTextarea.get("value");
        if (opinion === MWF.xApplication.process.Work.LP.inputText) opinion = "";
        var medias = [];
        if (this.handwritingFile) medias.push(this.handwritingFile);
        if (this.soundFile) medias.push(this.soundFile);
        if (this.videoFile) medias.push(this.videoFile);

        var currentRouteId = this.selectedRoute.retrieve("route");
        var routeData = this.getRouteData(currentRouteId);
        if (!opinion && medias.length === 0) {
            if (routeData.opinionRequired == true) {
                this.inputTextarea.setStyle("background-color", "#ffe9e9");
                //new mBox.Notice({
                //    type: "error",
                //    position: {"x": "center", "y": "top"},
                //    move: false,
                //    target: this.inputTextarea,
                //    delayClose: 6000,
                //    content: MWF.xApplication.process.Work.LP.opinionRequired
                //});
                MWF.xDesktop.notice(
                    "error",
                    {"x": "center", "y": "top"},
                    MWF.xApplication.process.Work.LP.opinionRequired,
                    this.inputTextarea,
                    null,  //{"x": 0, "y": 30}
                    {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
                );
                return false;
            }
        }

        var appendTaskOrgItem = "";
        if (routeData.type === "appendTask" && routeData.appendTaskIdentityType === "select") {
            if (!this.orgItems || this.orgItems.length === 0) {
                //new mBox.Notice({
                //    type: "error",
                //    position: {"x": "center", "y": "top"},
                //    move: false,
                //    target: this.orgsArea,
                //    delayClose: 6000,
                //    content: MWF.xApplication.process.Work.LP.noAppendTaskIdentityConfig //"没有配置转交人，请联系管理员"
                //});
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
                    //new mBox.Notice({
                    //    type: "error",
                    //    position: {"x": "center", "y": "top"},
                    //    move: false,
                    //    target: this.orgsArea,
                    //    delayClose: 6000,
                    //    content:  MWF.xApplication.process.Work.LP.selectAppendTaskIdentityNotice //"请选择转交人"
                    //});
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
        this.node.empty();
        delete this.task;
        delete this.node;
        delete this.routeSelectorTile;
        delete this.routeSelectorArea;
        delete this.routeOpinionTile;
        delete this.routeOpinionArea;
        delete this.buttonsArea;
        delete this.inputOpinionNode;
        delete this.inputTextarea;
        delete this.cancelButton;
        delete this.okButton;
    },
    getRouteDataList: function () {
        debugger;
        if(this.routeDataList)return this.routeDataList;
        if( this.form && this.form.businessData && this.form.businessData.routeList ){
            this.form.businessData.routeList.sort( function(a, b){
                var aIdx = parseInt(a.orderNumber || "9999999");
                var bIdx = parseInt(b.orderNumber || "9999999");
                return aIdx - bIdx;
            }.bind(this));
            this.form.businessData.routeList.each( function(d){
                d.selectConfigList = JSON.parse(d.selectConfig || "[]");
            }.bind(this));
            this.routeDataList = this.form.businessData.routeList;
        }
        if (!this.routeDataList) {
            o2.Actions.get("x_processplatform_assemble_surface").listRoute({"valueList": this.task.routeList}, function (json) {
                json.data.sort(function(a, b){
                    var aIdx = parseInt(a.orderNumber || "9999999");
                    var bIdx = parseInt(b.orderNumber || "9999999");
                    return aIdx - bIdx;
                }.bind(this));
                json.data.each(function (d) {
                    d.selectConfigList = JSON.parse(d.selectConfig || "[]");
                }.bind(this));
                this.routeDataList = json.data;
            }.bind(this), null, false);
        }
        return this.routeDataList;
    },
    getRouteData: function (routeId) {
        var routeList = this.getRouteDataList();
        for (var i = 0; i < routeList.length; i++) {
            if (routeList[i].id === routeId) {
                return routeList[i];
            }
        }
    },
    getMaxOrgLength: function () {
        var routeList = this.getRouteDataList();
        var length = 0;
        routeList.each(function (route) {
            if (route.hiddenScriptText) { //如果隐藏路由，返回
                if (this.form.Macro.exec(route.hiddenScriptText, this).toString() === "true") return;
            }
            length = Math.max(length, route.selectConfigList.length);
        }.bind(this));
        return length;
    },
    getOrgData: function (routeId) {
        var routeList = this.getRouteDataList();
        for (var i = 0; i < routeList.length; i++) {
            if (routeList[i].id === routeId) {
                return routeList[i].selectConfigList;
            }
        }
    },
    getVisableOrgData: function (routeId) {
        var selectConfigList = this.getOrgData(routeId);
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

    loadOrgs_mobile: function (route) {
        debugger;
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
            this.showOrgs_mobile(route);
        } else {
            this.createOrgs_mobile(route)
        }
    },
    showOrgs_mobile: function (route) {
        this.orgItemMap = this.orgItemsMap[route] || {};
        var dataVisable = this.getVisableOrgData(route);
        if (dataVisable.length) {
            if (this.isSameArray(Object.keys(this.orgItemMap), dataVisable.map(function (d) {
                return d.name
            }))) {
                this.orgTableObject[route].show();
                this.orgItems = this.orgItemsObject[route] || [];
                this.setSize(dataVisable.length);
            } else {
                this.loadOrgTable_mobile(route);
            }
        } else {
            this.orgsArea.hide();
            this.orgItemMap = {};
            this.orgItems = [];
            this.setSize(0);
        }
    },
    createOrgs_mobile: function (route) {
        var dataVisable = this.getVisableOrgData(route);
        if (dataVisable.length) {
            this.loadOrgTable_mobile(route);
        } else {
            this.setSize(dataVisable.length);
            this.orgItemMap = {};
            this.orgItems = [];
            this.orgsArea.hide();
        }
    },
    loadOrgTable_mobile: function (route) {
        var dataVisable = this.getVisableOrgData(route);
        this.setSize(dataVisable.length);

        this.orgsArea.show();

        var table_old = this.orgTableObject[route];
        var divsMap_old = {};
        if (table_old) {
            var divs = table_old.getChildren("div");
            divs.each(function (div) {
                divsMap_old[div.retrieve("orgName")] = div;
            });
        }

        var orgItems_old = this.orgItemsObject[route] || [];
        var orgItemMap_old = this.orgItemsMap[route] || {};

        this.orgItemsObject[route] = [];
        this.orgItemsMap[route] = {};

        this.orgItems = this.orgItemsObject[route];
        this.orgItemMap = this.orgItemsMap[route];

        var routeOrgTable = new Element("div", {
            "styles": this.css.routeOrgTable
        }).inject(this.orgsArea);
        this.orgTableObject[route] = routeOrgTable;

        var ignoreFirstOrgOldData = false

        dataVisable.each(function (config, i) {
            var sNode = new Element("div", {
                "styles": this.css.routeOrgTr
            }).inject(routeOrgTable);
            sNode.store("orgName", config.name);
            if (orgItemMap_old[config.name]) {
                var org = orgItemMap_old[config.name];
                this.orgItems.push(org);
                this.orgItemMap[config.name] = org;

                var div = divsMap_old[config.name];
                div.getChildren().inject(sNode);
            } else {
                this.loadOrg_mobile(sNode, config, ignoreFirstOrgOldData && i == 0)
            }
        }.bind(this));
        if (table_old) table_old.destroy();

    },
    // loadOrgs_mobile: function (route) {
    //     if (!this.form || !route) {
    //         this.orgsArea.hide();
    //         return;
    //     } else {
    //         this.orgsArea.show();
    //     }
    //     if (!this.orgTableObject) this.orgTableObject = {};
    //     if (!this.orgItemsObject) this.orgItemsObject = {};
    //     if (!this.orgItemsMap) this.orgItemsMap = {};
    //
    //     var isLoaded = false;
    //     for (var key in this.orgTableObject) {
    //         if (route === key) {
    //             this.orgTableObject[key].show();
    //             this.orgItems = this.orgItemsObject[key] || [];
    //             var data = this.getOrgData(route);
    //             isLoaded = true;
    //         } else {
    //             this.orgTableObject[key].hide();
    //         }
    //     }
    //     if (isLoaded) return;
    //
    //     this.orgItems = [];
    //     this.orgItemsObject[route] = this.orgItems;
    //
    //     var data = this.getOrgData(route);
    //     var routeConfig = this.getRouteData(route);
    //     var ignoreFirstOrgOldData = false; //(routeConfig.type === "appendTask" && routeConfig.appendTaskIdentityType === "select");
    //     this.setSize(data.length);
    //     if (data.length) {
    //         this.orgsArea.show();
    //
    //         var routeOrgTable = new Element("div", {
    //             "styles": this.css.routeOrgTable
    //         }).inject(this.orgsArea);
    //         this.orgTableObject[route] = routeOrgTable;
    //
    //         data.each(function (config, i) {
    //             var sNode = new Element("div", {
    //                 "styles": this.css.routeOrgTr
    //             }).inject(routeOrgTable);
    //             this.loadOrg_mobile(sNode, config, ignoreFirstOrgOldData && i == 0)
    //         }.bind(this))
    //     } else {
    //         this.orgsArea.hide();
    //     }
    // },
    loadOrg_mobile: function (container, json, ignoreOldData) {
        var titleNode = new Element("div.selectorTitle", {
            "styles": this.css.selectorTitle
        }).inject(container);
        var titleTextNode = new Element("div.selectorTitleText", {
            "text": json.title,
            "styles": this.css.selectorTitleText
        }).inject(titleNode);

        var contentNode = new Element("div.selectorContent", {
            "styles": this.css.selectorContent
        }).inject(container);

        var errorNode = new Element("div.selectorErrorNode", {
            "styles": this.css.selectorErrorNode
        }).inject(container);

        var org = new MWF.xApplication.process.Work.Processor.Org(contentNode, this.form, json, this, {
            onSelect: function (items, data) {
                if (!data || !data.length) {
                    contentNode.setStyles(this.css.selectorContent_noItem);
                } else {
                    contentNode.setStyles(this.css.selectorContent);
                }
            }.bind(this)
        });
        org.ignoreOldData = ignoreOldData;
        org.errContainer = errorNode;
        org.summitDlalog = this;
        this.orgItems.push(org);
        this.orgItemMap[json.name] = org;

        titleNode.addEvent("click", function () {
            this.load();
        }.bind(org));
        contentNode.addEvent("click", function () {
            this.load();
        }.bind(org));

        var defaultValue = org.getValue();
        org.loadOrgWidget(defaultValue, contentNode);
        if (!defaultValue || defaultValue.length == 0) {
            contentNode.setStyles(this.css.selectorContent_noItem);
        }

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
        var dataVisable = this.getVisableOrgData(route);
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
        var dataVisable = this.getVisableOrgData(route);
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
        var data = this.getOrgData(route);
        var dataVisable = this.getVisableOrgData(route);
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

        debugger;

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

        // var routeConfig = this.getRouteData( route );
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
        var org = new MWF.xApplication.process.Work.Processor.Org(contentNode, this.form, json, this);
        org.ignoreOldData = ignoreOldData;
        org.errContainer = errorNode;
        org.summitDlalog = this;
        org.load();
        this.orgItems.push(org);
        this.orgItemMap[json.name] = org;

    },
    showOrgsByRoute: function (route) {
        //debugger;
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
        var currentRoute = this.selectedRoute ? this.selectedRoute.retrieve("route") : "";
        var orgList = this.orgItemsObject[currentRoute];
        if (!orgList) return [];
        orgList.each(function (org) {
            if (org.selector && org.selector.selector) {
                selectorList.push(org.selector.selector);
            }
        }.bind(this))
        return selectorList;
    },
    getCurrentRouteOrgList: function () {
        var currentRoute = this.selectedRoute ? this.selectedRoute.retrieve("route") : "";
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
    setSize_noform: function () {
        var height = 0;
        if (this.managerProcessNoticeNode) height = height + this.getOffsetY(this.managerProcessNoticeNode) + this.managerProcessNoticeNode.getStyle("height").toInt();
        if (this.managerLoginNode) height = height + this.getOffsetY(this.managerLoginNode) + this.managerLoginNode.getStyle("height").toInt();
        if (this.routeSelectorTile) height = height + this.getOffsetY(this.routeSelectorTile) + this.routeSelectorTile.getStyle("height").toInt();
        if (this.routeSelectorArea) height = height + this.getOffsetY(this.routeSelectorArea) + this.routeSelectorArea.getStyle("height").toInt();
        if (this.routeOpinionTile) height = height + this.getOffsetY(this.routeOpinionTile) + this.routeOpinionTile.getStyle("height").toInt();
        if (this.routeOpinionArea) height = height + this.getOffsetY(this.routeOpinionArea) + this.routeOpinionArea.getStyle("height").toInt();
        this.node.setStyle("height", height);
        this.fireEvent("resize");
    },
    setSize: function (currentOrgLength, flag) {
        if (layout.mobile) {
            this.setSize_mobile();
        } else {
            this.setSize_pc(currentOrgLength, flag);
        }
        //this.node.store("width", this.node.getStyle("width").toInt() + ( flag ? 20 : 0 ));
        if (!flag) this.fireEvent("resize");
    },
    setSize_mobile: function () {
        if (this.buttonsArea) {
            debugger;
            var bodySize = $(document.body).getSize();
            var nodeHeight = bodySize.y - this.getOffsetY(this.node);
            this.node.setStyles({
                "overflow-y": "hidden",
                "height": nodeHeight
            });
            var buttonsAreaSize = this.buttonsArea.getSize();
            this.content.setStyles({
                "height": nodeHeight - buttonsAreaSize.y - this.getOffsetY(this.buttonsArea) - this.getOffsetY(this.content),
                "overflow-y": "auto"
            })
        }
    },
    setSize_pc: function (currentOrgLength, flag) {
        var lines = ((currentOrgLength + 1) / 2).toInt();

        var height = 0;
        if (this.routeSelectorTile) height = height + this.getOffsetY(this.routeSelectorTile) + this.routeSelectorTile.getStyle("height").toInt();
        if (this.routeSelectorArea) height = height + this.getOffsetY(this.routeSelectorArea) + this.routeSelectorArea.getStyle("height").toInt();
        if (this.routeOpinionTile) height = height + this.getOffsetY(this.routeOpinionTile) + this.routeOpinionTile.getStyle("height").toInt();
        if (this.routeOpinionArea) height = height + this.getOffsetY(this.routeOpinionArea) + this.routeOpinionArea.getStyle("height").toInt();
        //if( this.buttonsArea )height = height + this.getOffsetY(this.buttonsArea) +  this.buttonsArea.getStyle("height").toInt();

        if (lines > 0) {
            if (this.orgsArea) this.orgsArea.show();

            if (flag) {
                // if( this.orgsTile )height = height + this.getOffsetY(this.orgsTile) +  this.orgsTile.getStyle("height").toInt();
                this.orgsArea.getChildren().each(function (el) {
                    height += el.getSize().y + this.getOffsetY(el);
                }.bind(this))
                this.node.setStyle("height", height);
            } else {
                if (this.orgsTile) height = height + this.getOffsetY(this.orgsTile) + this.orgsTile.getStyle("height").toInt();
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
            this.node.setStyles(this.css.node_wide);
            this.inputOpinionNode.setStyles(this.css.inputOpinionNode_wide);
            this.inputTextarea.setStyles(this.css.inputTextarea_wide);
            this.inputTextareaStyle = this.css.inputTextarea_wide;
            this.selectIdeaNode.setStyles(this.css.selectIdeaNode_wide);

        } else {
            this.node.setStyles(this.css.node);
            this.inputOpinionNode.setStyles(this.css.inputOpinionNode);
            this.inputTextarea.setStyles(this.css.inputTextarea);
            this.inputTextareaStyle = this.css.inputTextarea;
            this.selectIdeaNode.setStyles(this.css.selectIdeaNode);
        }
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
        debugger;
        var currentRoute = this.selectedRoute ? this.selectedRoute.retrieve("route") : "";

        var visableOrg = this.getVisableOrgData( currentRoute || this.selectedRouteId || "" );
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
        if (layout.mobile) {
            if (callback) callback();
            return true;
        } else {
            if (!this.isOrgsHasEmpower()) {
                if (callback) callback();
                return true;
            }
            //this.checkEmpowerMode = true;
            this.showEmpowerDlg(callback);
        }
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

//兼容快速流转，所以需要判断
if (MWF.xApplication.process.Xform && MWF.xApplication.process.Xform.Form) {
    MWF.xDesktop.requireApp("process.Xform", "Org", null, false);

    MWF.xApplication.process.Work.Processor.Org = new Class({
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
            if (layout.mobile) {
                setTimeout(function () { //如果有输入法界面，这个时候页面的计算不对，所以等100毫秒
                    var options = this.getOptions();
                    if (options) {
                        //this.selector = new MWF.O2Selector(this.container, options);
                        this.selector = new MWF.O2Selector($(document.body), options);
                    }
                }.bind(this), 100)
            } else {
                var options = this.getOptions();
                if (options) {
                    this.selector = new MWF.O2Selector(this.container, options);
                }
            }
        },
        _getOrgOptions: function () {
            this.selectTypeList = typeOf(this.json.selectType) == "array" ? this.json.selectType : [this.json.selectType];
            if (this.selectTypeList.contains("identity")) {
                this.identityOptions = new MWF.xApplication.process.Work.Processor.IdentityOptions(this.form, this.json);
            }
            if (this.selectTypeList.contains("unit")) {
                this.unitOptions = new MWF.xApplication.process.Work.Processor.UnitOptions(this.form, this.json);
            }
            //if( this.selectTypeList.contains( "group" ) ){
            //    this.groupOptions = new MWF.APPOrg.GroupOptions( this.form, this.json );
            //}
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

            //var groupOpt;
            //if( this.groupOptions ){
            //    groupOpt = this.groupOptions.getOptions();
            //    groupOpt.values = (this.json.isInput) ? [] : values;
            //    groupOpt.exclude = exclude;
            //}

            var defaultOpt;
            if (layout.mobile) {
                defaultOpt = {
                    "style": "default",
                    "zIndex": 3000
                };
            } else {
                defaultOpt = {
                    "style": "process",
                    "width": "auto",
                    "height": "240",
                    "embedded": true,
                    "hasLetter": false, //字母
                    "hasTop": true //可选、已选的标题
                };
            }

            if (this.json.events && typeOf(this.json.events) === "object") {
                Object.each(this.json.events, function (e, key) {
                    if (e.code) {
                        if (this.options.moduleEvents.indexOf(key) !== -1) {
                            //this.addEvent(key, function(event){
                            //    return this.form.Macro.fire(e.code, this, event);
                            //}.bind(this));
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

            var mobileEvents = {
                "onComplete": function (items) {
                    this.selectOnComplete(items);
                }.bind(this),
                "onCancel": this.selectOnCancel.bind(this),
                "onClose": this.selectOnClose.bind(this)
            };

            if (this.selectTypeList.length === 1) {
                return Object.merge(
                    defaultOpt,
                    {
                        "type": this.selectTypeList[0],
                        "onLoad": function () {
                            //this 为 selector
                            _self.selectOnLoad(this, this.selector)
                        }
                        //"onComplete": function(items){
                        //    this.selectOnComplete(items);
                        //}.bind(this),
                        //"onCancel": this.selectOnCancel.bind(this),
                        //"onClose": this.selectOnClose.bind(this)
                    },
                    layout.mobile ? mobileEvents : {},
                    identityOpt || unitOpt
                )
            } else if (this.selectTypeList.length > 1) {
                var options = {
                    "type": "",
                    "types": this.selectTypeList,
                    "onLoad": function () {
                        //this 为 selector
                        _self.selectOnLoad(this)
                    }
                    //"onComplete": function(items){
                    //    this.selectOnComplete(items);
                    //}.bind(this),
                    //"onCancel": this.selectOnCancel.bind(this),
                    //"onClose": this.selectOnClose.bind(this)
                };
                if (identityOpt) {
                    options.identityOptions = Object.merge(
                        defaultOpt,
                        layout.mobile ? mobileEvents : {},
                        identityOpt
                    );
                }
                if (unitOpt) {
                    options.unitOptions = Object.merge(
                        defaultOpt,
                        layout.mobile ? mobileEvents : {},
                        unitOpt
                    );
                }
                //if( groupOpt )options.groupOptions = groupOpt;
                return options;
            }
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
        selectOnCancel: function () { //移动端才执行
            //this.validation();
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
                    if (layout.mobile) {
                        //widget.node.setStyles({
                        //    "float" : "none"
                        //})
                    }
                }.bind(this));
            }
        },

        hasEmpowerIdentity: function () {
            var data = this.getData();
            if (!this.empowerChecker) this.empowerChecker = new MWF.xApplication.process.Work.Processor.EmpowerChecker(this.form, this.json, this.processor);
            return this.empowerChecker.hasEmpowerIdentity(data);
        },
        checkEmpower: function (data, callback, container, selectAllNode) {
            if (typeOf(data) === "array" && this.identityOptions && this.json.isCheckEmpower && this.json.identityResultType === "identity") {
                if (!this.empowerChecker) this.empowerChecker = new MWF.xApplication.process.Work.Processor.EmpowerChecker(this.form, this.json, this.processor);
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

        //saveWithCheckEmpower: function( isValid, callback ){
        //    var checkEmpowerData = function(){
        //        var array = this.getData();
        //        this.checkEmpower( array, function( data ){
        //            var values = [];
        //            data.each(function(d){
        //                values.push(MWF.org.parseOrgData(d, true));
        //            }.bind(this));
        //            this.setData( values );
        //            if( callback )callback(values)
        //        }.bind(this), container, selectAllNode)
        //    }.bind(this)
        //    if( isValid ){
        //        if( this.validation() ){
        //            checkEmpowerData( function(){
        //                if(callback)callback();
        //            }.bind(this));
        //            return true;
        //        }else{
        //            return false;
        //        }
        //    }else{
        //        //this.setData( this.getData() );
        //        checkEmpowerData( function(){
        //            if(callback)callback();
        //        }.bind(this));
        //        return true;
        //    }
        //},

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
            if (this.selector && !layout.mobile) {
                return this.getSelectedData();
            } else {
                return this.getValue();
            }
        },
        getSelectedData: function () {
            if (layout.mobile) {
                return this.getValue();
            } else {
                var simple = this.json.storeRange === "simple";
                var data = [];
                if (this.selector && this.selector.selector) {
                    this.selector.selector.selectedItems.each(function (item) {
                        data.push(MWF.org.parseOrgData(item.data, true, simple));
                    })
                }
                return data;
            }
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
                        }.bind(this), error, v, false);
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
                }.bind(this), error, value, false);
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
                debugger;
                if (!layout.mobile && errorSize.y > 26) {
                    this.errorHeightOverflow = true;
                }

                //this.showNotValidationMode(this.node);
                //if (!this.node.isIntoView()) this.node.scrollIntoView();
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

    MWF.xApplication.process.Work.Processor.EmpowerChecker = new Class({
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
                            if (layout.mobile) {
                                this.openSelectEmpowerDlg(arr, data, callback, container);
                            } else {
                                this.openSelectEmpowerDlg_embedded(arr, data, callback, container);
                            }
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
        openSelectEmpowerDlg_embedded: function (data, orgData, callback, container) {
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

    MWF.xApplication.process.Work.Processor.UnitOptions = new Class({
        Extends: MWF.APPOrg.UnitOptions
    });

    MWF.xApplication.process.Work.Processor.IdentityOptions = new Class({
        Extends: MWF.APPOrg.IdentityOptions
    });
}
