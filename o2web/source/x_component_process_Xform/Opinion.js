MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
MWF.xDesktop.requireApp("process.Work", "lp." + o2.language, null, false);
/** @class Opinion 意见输入框。
 * @o2cn 意见输入框
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("fieldId"); //获取组件对象
 * //方法2
 * var field = this.target; //在组件本身的脚本中获取，比如事件脚本、默认值脚本、校验脚本等等
 *
 * var data = field.getData(); //获取值
 * field.setData("字符串值"); //设置值
 * field.hide(); //隐藏字段
 * var id = field.json.id; //获取字段标识
 * var flag = field.isEmpty(); //字段是否为空
 * @extends MWF.xApplication.process.Xform.$Input
 * @o2category FormComponents
 * @o2range {Process}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Opinion = MWF.APPOpinion = new Class(
    /** @lends MWF.xApplication.process.Xform.Opinion# */
    {
        Implements: [Events],
        Extends: MWF.APP$Input,

        _loadUserInterface: function () {

            this._loadNode();
            if (this.json.compute == "show") {
                this._setValue(this._computeValue());
            } else {
                this._loadValue();
            }
        },
        _loadNode: function () {
            if (this.readonly) {
                this._loadNodeRead();
            } else {
                this._loadNodeEdit();
            }
        },
        _loadNodeRead: function () {
            this.node.empty();
            this.node.set({
                "nodeId": this.json.id,
                "MWFType": this.json.type
            });
            this.node.setStyle("display", "none");
        },
        validationConfigItem: function (routeName, data) {
            var flag = (data.status === "all") ? true : (routeName === data.decision);
            if (flag) {
                var n = this.getInputData();
                var v = (data.valueType === "value") ? n : n.length;
                switch (data.operateor) {
                    case "isnull":
                        if (!v && !(this.handwritingFile && this.handwritingFile[layout.session.user.distinguishedName])) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "notnull":
                        if (v) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "gt":
                        if (v > data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "lt":
                        if (v < data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "equal":
                        if (v == data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "neq":
                        if (v != data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "contain":
                        if (v.indexOf(data.value) != -1) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "notcontain":
                        if (v.indexOf(data.value) == -1) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                }
            }
            return true;
        },
        _resetNodeEdit: function () {
            var input = new Element("textarea", {
                "styles": {
                    "background": "transparent",
                    "width": "100%",
                    "border": "0px"
                }
            });
            input.set(this.json.properties);
            this.textarea = input;

            var node = new Element("div", {
                "styles": {
                    "ovwrflow": "hidden",
                    "position": "relative",
                    "padding-right": "2px"
                }
            }).inject(this.node, "after");
            input.inject(node);
            this.node.destroy();
            this.node = node;
        },
        _loadNodeEdit: function () {
            if (!this.json.preprocessing) {
                this._resetNodeEdit();
            }else{
                this.textarea = this.node.getElement("textarea");
            }
            var input = this.node.getFirst();
            if( !input && this.nodeHtml ){
                this.node.set("html", this.nodeHtml);
                input = this.node.getFirst();
            }
            input.set(this.json.properties);

            //this.node = input;
            this.node.set({
                "id": this.json.id,
                "MWFType": this.json.type
            });
            this.input = input;

            if( this.json.isSelectIdea === "yes" ){
                this.selectIdeaNode = new Element("div", {"styles": this.form.css.selectIdeaNode}).inject(this.node);
                this.underLineNode = new Element("div", {"styles": {
                        "border-top": "1px solid #ccc",
                        "clear": "both"
                 }}).inject(this.node);
                this.loadSelectIdea()
            }

            this.mediaActionArea = new Element("div", {"styles": this.form.css.inputOpinionMediaActionArea}).inject(this.node);

            if (this.json.isHandwriting !== "no") {
                /**
                 * @summary 手写意见按钮按钮。
                 * @member {Element}
                 */
                this.handwritingAction = new Element("div", {
                    "styles": this.form.css.inputOpinionHandwritingAction,
                    "text": MWF.xApplication.process.Work.LP.handwriting
                }).inject(this.mediaActionArea);
                this.handwritingAction.addEvent("click", function () {
                    this.handwriting();
                }.bind(this));
            }

            if (this.json.isAudio !== "no") {
                if (navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia) {
                    /**
                     * @summary 音频按钮.在浏览器支持HTML5的getUserMedia才可用。
                     * @member {Element}
                     */
                    this.audioRecordAction = new Element("div", {
                        "styles": this.form.css.inputOpinionAudioRecordAction,
                        "text": MWF.xApplication.process.Work.LP.audioRecord
                    }).inject(this.mediaActionArea);
                    this.audioRecordAction.addEvent("click", function () {
                        this.audioRecord();
                    }.bind(this));
                }
            }

            this.node.addEvent("change", function () {
                this._setBusinessData(this.getInputData());
                this.fireEvent("change");
            }.bind(this));

            this.node.getFirst().addEvent("blur", function () {
                this.validation();
                this.hideSelectOpinionNode();
            }.bind(this));
            this.node.getFirst().addEvent("keyup", function () {
                this.validationMode();
            }.bind(this));

            this.node.getFirst().addEvent("keydown", function (e) {
                if (this.selectOpinionNode && (this.selectOpinionNode.getStyle("display") != "none") && this.selectOpinionNode.getFirst()) {
                    if (e.code == 38) { //up
                        if (this.selectedOpinionNode) {
                            var node = this.selectedOpinionNode.getPrevious();
                            if (!node) node = this.selectOpinionNode.getLast();
                            this.unselectedOpinion(this.selectedOpinionNode);
                            this.selectedOpinion(node)
                        } else {
                            node = this.selectOpinionNode.getLast();
                            this.selectedOpinion(node)
                        }
                    }
                    if (e.code == 40) { //down
                        if (this.selectedOpinionNode) {
                            var node = this.selectedOpinionNode.getNext();
                            if (!node) node = this.selectOpinionNode.getFirst();
                            this.unselectedOpinion(this.selectedOpinionNode);
                            this.selectedOpinion(node)
                        } else {
                            node = this.selectOpinionNode.getFirst();
                            this.selectedOpinion(node)
                        }
                    }
                    if (e.code == 27) {  //esc
                        this.hideSelectOpinionNode();
                        e.preventDefault();
                    }
                    if (e.code == 32 || e.code == 13) {  //space
                        if (this.selectedOpinionNode) {
                            this.setOpinion(this.selectedOpinionNode.get("text"));
                            e.preventDefault();
                        }
                    }
                }
            }.bind(this));

            if (!this.form.json.notLoadUserOpinion) {
                MWF.UD.getDataJson("userOpinion", function (json) {
                    this.userOpinions = json;
                }.bind(this), false);
            }
            this.node.getFirst().addEvent("input", function (e) {
                this.startSearchOpinion();
            }.bind(this));
            this.node.getFirst().addEvent("focus", function () {
                this.startSearchOpinion();
            }.bind(this));
        },
        _afterLoaded: function(){
            if (!this.isReadonly()){
                this.setNodeSize();
                this.loadDescription();
            }
        },
        setNodeSize: function(){
            if( this.textarea ){
                var x = 0;
                if( this.selectIdeaNode ){
                    var size = this.selectIdeaNode.getSize();
                    x = size.x + 5;
                    this.textarea.setStyles({
                        "height": ( size.y - 6 ) + "px",
                        "resize":"none",
                        "border-bottom": "0px",
                        "padding-right": "0px",
                        "width": "calc( 100% - "+ x +"px )"
                    });
                    this.node.setStyles({
                        "min-height": size.y + "px",
                        "height": "auto",
                        "overflow":"hidden",
                        "width": "100%"
                    });
                    this.mediaActionArea.setStyle("right", x+"px");
                }
            }
        },
        loadSelectIdea: function(){
            this.selectIdeaScrollNode = new Element("div", {"styles": this.form.css.selectIdeaScrollNode}).inject(this.selectIdeaNode);
            this.selectIdeaAreaNode = new Element("div", {
                "styles": {
                    "overflow": "hidden"
                }
            }).inject(this.selectIdeaScrollNode);
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
        },
        setIdeaList: function (ideas) {
            var _self = this;
            ideas.each(function (idea) {
                if (!idea) return;
                new Element("div", {
                    "styles": this.form.css.selectIdeaItemNode,
                    "text": idea,
                    "events": {
                        "click": function () {
                            if(_self.descriptionNode)_self.descriptionNode.setStyle("display", "none");
                            if ( !_self.textarea.get("value") ) {
                                _self.textarea.set("value", this.get("text"));
                            } else {
                                _self.textarea.set("value", _self.textarea.get("value") + ", " + this.get("text"));
                            }
                        },
                        "mouseover": function () {
                            this.setStyles(_self.form.css.selectIdeaItemNode_over);
                        },
                        "mouseout": function () {
                            this.setStyles(_self.form.css.selectIdeaItemNode);
                        }
                    }
                }).inject(this.selectIdeaAreaNode);
            }.bind(this));
        },

        audioRecord: function () {
            if (!this.audioRecordNode) this.createAudioRecordNode();
            this.audioRecordNode.show();
            this.audioRecordNode.position({
                "relativeTo": this.node,
                "position": "center",
                "edge": "center"
            });
            var p = this.audioRecordNode.getPosition(this.form.app.content);
            var top = p.y;
            var left = p.x;
            if (p.y < 0) top = 10;
            if (p.x < 0) left = 10;
            this.audioRecordNode.setStyles({
                "top": "" + top + "px",
                "left": "" + left + "px"
            });

            this.soundFile = {};
            MWF.require("MWF.widget.AudioRecorder", function () {
                /**
                 * @summary 音频意见组件.
                 * @member {o2.widget.AudioRecorder}
                 */
                this.audioRecorder = new MWF.widget.AudioRecorder(this.audioRecordNode, {
                    "onSave": function (audioFile) {
                        this.soundFile[layout.session.user.distinguishedName] = audioFile;
                        if (this.previewNode) {
                            this.previewNode.destroy();
                            this.previewNode = null;
                        }
                        this.previewNode = new Element("audio", {
                            "controls": true,
                            "src": window.URL.createObjectURL(audioFile)
                        }).inject(this.node);
                        this.audioRecordNode.hide();
                        // this.page.get("div_image").node.set("src",base64Image);
                    }.bind(this),
                    "onCancel": function () {
                        this.soundFile[layout.session.user.distinguishedName] = null;
                        delete this.soundFile[layout.session.user.distinguishedName];
                        if (this.previewNode) {
                            this.previewNode.destroy();
                            this.previewNode = null;
                        }
                        this.audioRecordNode.hide();
                    }.bind(this)
                }, null);
            }.bind(this));
        },
        createAudioRecordNode: function () {
            this.audioRecordNode = new Element("div", {"styles": this.form.css.handwritingNode}).inject(this.node, "after");
            var size = this.node.getSize();
            var y = Math.max(size.y, 320);
            var x = Math.max(size.x, 400);
            x = Math.min(x, 600);
            y = 320;
            x = 500;
            var zidx = this.node.getStyle("z-index").toInt() || 0;
            zidx = (zidx < 1000) ? 1000 : zidx;
            this.audioRecordNode.setStyles({
                "height": "" + y + "px",
                "width": "" + x + "px",
                "z-index": zidx + 1
            });
            // this.audioRecordNode.position({
            //     "relativeTo": this.node,
            //     "position": "center",
            //     "edge": "center"
            // });
        },
        /**
         * @summary 弹出手写板.
         * @example
         * this.form.get("fieldId").handwriting();
         */
        handwriting: function () {
            if (!this.handwritingNode) this.createHandwriting();
            this.handwritingNode.show();
            if (layout.mobile) {
                this.handwritingNode.setStyles({
                    "top": "0px",
                    "left": "0px"
                });

            } else {
                this.handwritingNode.position({
                    "relativeTo": this.form.app.content || this.form.container,
                    "position": "center",
                    "edge": "center"
                });
                //var p = this.handwritingNode.getPosition(this.form.app.content);
                var p = this.handwritingNode.getPosition(this.handwritingNode.getOffsetParent());
                var top = p.y;
                var left = p.x;
                if (p.y < 0) top = 10;
                if (p.x < 0) left = 10;
                this.handwritingNode.setStyles({
                    "top": "" + top + "px",
                    "left": "" + left + "px"
                });
            }
        },
        createHandwriting: function () {
            /**
             * @summary 手写板容器.
             * @member {Element}
             */
            this.handwritingNode = new Element("div", {"styles": this.form.css.handwritingNode}).inject(this.node, "after");
            var x, y;
            if (layout.mobile) {
                var bodySize = $(document.body).getSize();
                x = bodySize.x;
                y = bodySize.y;
                this.json.tabletWidth = 0;
                this.json.tabletHeight = 0;
            } else {
                var size = this.node.getSize();
                x = Math.max(this.json.tabletWidth || size.x, 600);
                this.json.tabletWidth = x;
                y = Math.max(this.json.tabletHeight ? (parseInt(this.json.tabletHeight) + 110) : size.y, 320);
            }

            var zidx = this.node.getStyle("z-index").toInt() || 0;
            zidx = (zidx < 1000) ? 1000 : zidx;
            this.handwritingNode.setStyles({
                "height": "" + y + "px",
                "width": "" + x + "px",
                "z-index": zidx + 1
            });
            if (layout.mobile) {
                this.handwritingNode.addEvent('touchmove', function (e) {
                    e.preventDefault();
                });
                this.handwritingNode.setStyles({
                    "top": "0px",
                    "left": "0px"
                }).inject($(document.body));
            } else {
                this.handwritingNode.position({
                    "relativeTo": this.node,
                    "position": "center",
                    "edge": "center"
                });
            }
            this.handwritingAreaNode = new Element("div", {"styles": this.form.css.handwritingAreaNode}).inject(this.handwritingNode);
            if (!layout.mobile) {
                this.handwritingActionNode = new Element("div", {
                    "styles": this.form.css.handwritingActionNode,
                    "text": MWF.xApplication.process.Work.LP.saveWrite
                }).inject(this.handwritingNode);
                var h = this.handwritingActionNode.getSize().y + this.handwritingActionNode.getStyle("margin-top").toInt() + this.handwritingActionNode.getStyle("margin-bottom").toInt();
                h = y - h;
                this.handwritingAreaNode.setStyle("height", "" + h + "px");
            } else {
                this.handwritingAreaNode.setStyle("height", "" + y + "px");
            }
            this.handwritingFile = {};
            MWF.require("MWF.widget.Tablet", function () {
                /**
                 * @summary 手写板组件.
                 * @member {o2.widget.Tablet}
                 */
                this.tablet = new MWF.widget.Tablet(this.handwritingAreaNode, {
                    "style": "default",
                    "toolHidden": this.json.toolHidden || [],
                    "contentWidth": this.json.tabletWidth || 0,
                    "contentHeight": this.json.tabletHeight || 0,
                    "onSave": function (base64code, base64Image, imageFile) {
                        this.handwritingFile[layout.session.user.distinguishedName] = imageFile;
                        if (this.previewNode) {
                            this.previewNode.destroy();
                            this.previewNode = null;
                        }
                        if (this.json.isHandwritingPreview !== "no") {
                            this.previewNode = new Element("img", {"src": base64Image}).inject(this.node);
                            this.previewNode.setStyles({
                                "max-width": "90%"
                            })
                        }
                        this.handwritingNode.hide();

                        this.validation();
                        this.fireEvent("change");
                        // this.page.get("div_image").node.set("src",base64Image);
                    }.bind(this),
                    "onCancel": function () {
                        this.handwritingFile[layout.session.user.distinguishedName] = null;
                        delete this.handwritingFile[layout.session.user.distinguishedName];
                        if (this.previewNode) {
                            this.previewNode.destroy();
                            this.previewNode = null;
                        }
                        this.handwritingNode.hide();
                    }.bind(this)
                }, null);
                this.tablet.load();
            }.bind(this));
            if (layout.mobile) {
                opt.tools = [
                    "save", "|",
                    "undo",
                    "redo", "|",
                    "reset", "|",
                    "cancel"
                ]
            }
            if (this.handwritingActionNode) {
                this.handwritingActionNode.addEvent("click", function () {
                    //this.handwritingNode.hide();
                    if (this.tablet) this.tablet.save();
                }.bind(this));
            }
        },

        unselectedOpinion: function (node) {
            node.setStyle("background-color", "#ffffff");
            this.selectedOpinionNode = null;
        },
        selectedOpinion: function (node) {
            node.setStyle("background-color", "#d2ddf5");
            this.selectedOpinionNode = node;
        },
        startSearchOpinion: function () {
            var t = this.input.get("value");
            var arr = t.split(/(,\s*){1}|(;\s*){1}|\s+/g);
            t = arr[arr.length - 1];
            if (t.length) {
                this.clearSearcheOpinionId();
                this.searcheOpinionId = window.setTimeout(function () {
                    this.searchOpinions(t);
                }.bind(this), 500);
            } else {
                this.clearSearcheOpinionId();
            }
        },
        clearSearcheOpinionId: function () {
            if (this.searcheOpinionId) {
                window.clearTimeout(this.searcheOpinionId);
                this.searcheOpinionId = "";
            }
        },
        searchOpinions: function (t) {
            var value = this.input.get("value");
            var arr = value.split(/[\n\r]/g);
            lines = arr.length;
            value = arr[arr.length - 1];
            var offsetValue = value;
            //var offsetValue = value.substr(0, value.length-t.length);

            if (this.userOpinions) {
                var ops = this.userOpinions.filter(function (v, i) {
                    return v.contains(t) && (v != t);
                }.bind(this));
                if (ops.length) {
                    this.showSelectOpinionNode(ops, offsetValue, lines);
                } else {
                    this.hideSelectOpinionNode(ops);
                }
            }
        },
        hideSelectOpinionNode: function () {
            if (this.selectOpinionNode) this.selectOpinionNode.setStyle("display", "none");
        },
        showSelectOpinionNode: function (ops, offsetValue, lines) {
            if (!this.selectOpinionNode) this.createSelectOpinionNode();
            this.selectOpinionNode.empty();
            ops.each(function (op) {
                this.createSelectOpinionOption(op);
            }.bind(this));

            var inputSize = this.input.getSize();
            var size = MWF.getTextSize(offsetValue, this.json.inputStyles);
            var offY = ((size.y - 3) * lines) + 3;
            if (offY > inputSize.y) offY = inputSize.y;

            this.selectOpinionNode.setStyle("display", "block");
            this.selectOpinionNode.position({
                "relativeTo": this.node,
                "position": "leftTop",
                "edge": "leftTop",
                "offset": {"x": size.x, "y": offY}
            });
        },
        createSelectOpinionNode: function () {
            this.selectOpinionNode = new Element("div", {"styles": this.form.css.opinionSelectNode}).inject(this.node);
        },
        createSelectOpinionOption: function (op) {
            var option = new Element("div", {
                "styles": this.form.css.opinionSelectOption,
                "text": op
            }).inject(this.selectOpinionNode);
            if (this.json.selectItemStyles) option.setStyles(this.json.selectItemStyles);
            option.addEvents({
                "mouseover": function () {
                    this.setStyle("background-color", "#d2ddf5")
                },
                "mouseout": function () {
                    this.setStyle("background-color", "#ffffff")
                },
                "mousedown": function () {
                    this.setOpinion(op)
                }.bind(this)
            });
        },
        setOpinion: function (op) {
            var v = this.input.get("value");
            var arr = v.split(/(,\s*){1}|(;\s*){1}|\s+/g);
            t = arr[arr.length - 1];
            var leftStr = v.substr(0, v.length - t.length);
            this.input.set("value", leftStr + op);
            this.hideSelectOpinionNode();
            this._setBusinessData(this.getInputData());
        },

        loadDescription: function () {
            if (this.isReadonly()) return;
            var v = this._getBusinessData();
            if (!v) {
                if (this.json.description) {
                    var size = this.node.getFirst().getSize();
                    var w = size.x - 3
                    if (this.json.showIcon != 'no' && !this.form.json.hideModuleIcon) {
                        w = size.x - 23;
                    }
                    if( this.handwritingAction ){
                        w = w - this.handwritingAction.getSize().x
                    }
                    if( this.audioRecordAction ){
                        w = w - this.audioRecordAction.getSize().x
                    }
                    this.descriptionNode = new Element("div", {
                        "styles": this.form.css.descriptionNode,
                        "text": this.json.description
                    }).inject(this.node);
                    this.descriptionNode.setStyles({
                        "width": "" + w + "px",
                        "height": "" + size.y + "px",
                        "line-height": "" + size.y + "px"
                    });
                    this.setDescriptionEvent();
                }
            }
        },
        setDescriptionEvent: function () {
            if (this.descriptionNode) {
                if (COMMON.Browser.Platform.name === "ios") {
                    this.descriptionNode.addEvents({
                        "click": function () {
                            this.descriptionNode.setStyle("display", "none");
                            this.node.getFirst().focus();
                        }.bind(this)
                    });
                } else if (COMMON.Browser.Platform.name === "android") {
                    this.descriptionNode.addEvents({
                        "click": function () {
                            this.descriptionNode.setStyle("display", "none");
                            this.node.getFirst().focus();
                        }.bind(this)
                    });
                } else {
                    this.descriptionNode.addEvents({
                        "click": function () {
                            this.descriptionNode.setStyle("display", "none");
                            this.node.getFirst().focus();
                        }.bind(this)
                    });
                }
                this.node.getFirst().addEvents({
                    "focus": function () {
                        this.descriptionNode.setStyle("display", "none");
                    }.bind(this),
                    "blur": function () {
                        if (!this.node.getFirst().get("value")) this.descriptionNode.setStyle("display", "block");
                    }.bind(this)
                });
            }
        }

    });
