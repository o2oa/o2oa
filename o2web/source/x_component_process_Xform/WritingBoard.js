MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class WritingBoard 手写板组件。
 * @o2cn 手写板
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var writingBoard = this.form.get("name"); //获取组件
 * //方法2
 * var writingBoard = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.WritingBoard = MWF.APPWritingBoard = new Class(
    /** @lends MWF.xApplication.process.Xform.WritingBoard# */
    {
        Implements: [Events],
        Extends: MWF.APP$Module,
        options: {
            "moduleEvents": ["load", "queryLoad", "postLoad","change"]
        },
        initialize: function (node, json, form, options) {
            this.node = $(node);
            this.node.store("module", this);
            this.json = json;
            this.form = form;
            this.field = true;
            this.fieldModuleLoaded = false;
        },
        _loadUserInterface: function () {
            this.field = true;
            this.node.empty();

            if (!this.isReadonly()) {

                var actionNode = new Element("div").inject(this.node);
                actionNode.set({
                    //"id": this.json.id,
                    "text": this.json.name || this.json.id,
                    "styles": this._parseStyles(this.json.actionStyles || {})
                });
                actionNode.addEvent("click", function () {
                    this.validationMode();
                    var d = this._getBusinessData();
                    if (layout.mobile) {
                        window.setTimeout(function () {
                            this.handwriting(d);
                        }.bind(this), 100)
                    } else {
                        this.handwriting(d);
                    }
                }.bind(this));
            }


            var data = this._getBusinessData();
            if (data) {
                var img = new Element("img", {
                    src: MWF.xDesktop.getImageSrc(data)
                });
                if (this.json.imageStyles) {
                    img.setStyles(this._parseStyles(this.json.imageStyles));
                }
                if( !this.json.imageStyles || !this.json.imageStyles["max-width"] ){
                    img.setStyles({
                        "max-width": "90%"
                    })
                }
                img.inject(this.node);
            }

            this.fieldModuleLoaded = true;
        },
        getTextData: function () {
            var value = this._getBusinessData() || "";
            return {"value": [value], "text": [value]};
        },
        /**
         * @summary 判断组件值是否为空.
         * @example
         * if( this.form.get('fieldId').isEmpty() ){
         *     this.form.notice('请进行手写', 'warn');
         * }
         * @return {Boolean} 值是否为空.
         */
        isEmpty: function () {
            return !this.getData();
        },
        /**
         * 获取手写图片的ID。
         * @summary 获取手写图片的ID。
         * @example
         * var id = this.form.get('fieldId').getData(); //获取手写图片的ID
         * var url = MWF.xDesktop.getImageSrc( id ); //获取图片的url
         */
        getData: function (data) {
            return this._getBusinessData() || "";
        },
        setData: function (data) {
            this._setBusinessData(data);
            var img = this.node.getElement("img");
            if( !data ){
                if(img)img.destroy();
                return;
            }
            if (img){
                img.set("src", MWF.xDesktop.getImageSrc(data))
            }else{
                img = new Element("img", {
                    src: MWF.xDesktop.getImageSrc(data)
                }).inject(this.node);
            }
            if (this.json.imageStyles) {
                img.setStyles(this._parseStyles(this.json.imageStyles));
            }
            if( !this.json.imageStyles || !this.json.imageStyles["max-width"] ){
                img.setStyles({
                    "max-width": "90%"
                })
            }
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
            if(layout.mobile){
                var bodySize = $(document.body).getSize();
                x = bodySize.x;
                y = bodySize.y;
                this.json.tabletWidth = 0;
                this.json.tabletHeight = 0;
            }else{
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
            if( !layout.mobile ){
                this.handwritingActionNode = new Element("div", {
                    "styles": this.form.css.handwritingActionNode,
                    "text": MWF.xApplication.process.Work.LP.saveWrite
                }).inject(this.handwritingNode);
                var h = this.handwritingActionNode.getSize().y + this.handwritingActionNode.getStyle("margin-top").toInt() + this.handwritingActionNode.getStyle("margin-bottom").toInt();
                h = y - h;
                this.handwritingAreaNode.setStyle("height", "" + h + "px");
            }else{
                this.handwritingAreaNode.setStyle("height", "" + y + "px");
            }

            this.handwritingFile = {};
            MWF.require("MWF.widget.Tablet", function () {
                var id = this.getData();
                var opt = {
                    "style": "default",
                    "imageSrc": id ? MWF.xDesktop.getImageSrc( id ) : "",
                    "toolHidden": this.json.toolHidden || [],
                    "contentWidth": this.json.tabletWidth || 0,
                    "contentHeight": this.json.tabletHeight || 0,
                    "onSave": function (base64code, base64Image, imageFile) {
                        this.handwritingNode.hide();

                        if( this.tablet.isBlank() ){
                            this.setData("");
                            this.validation();
                            this.fireEvent("change");
                        }else{
                            this.upload(function (json) {
                                var data = json.data;
                                this.setData(data ? data.id : "");
                                this.validation();
                                this.fireEvent("change");
                            }.bind(this));
                        }


                    }.bind(this),
                    "onCancel": function () {
                        this.handwritingNode.hide();
                    }.bind(this)
                };
                if (layout.mobile) {
                    opt.tools = [
                        "save", "|",
                        "undo",
                        "redo", "|",
                        "reset", "|",
                        "cancel"
                    ]
                }

                /**
                 * @summary 手写板组件.
                 * @member {o2.widget.Tablet}
                 * @example
                 * var tablet = this.form.get("fieldId").tablet; //获取手写板
                 * tablet.cancel(); //关闭手写板
                 */
                this.tablet = new MWF.widget.Tablet(this.handwritingAreaNode, opt, null);
                this.tablet.load();
            }.bind(this));

            if(this.handwritingActionNode){
                this.handwritingActionNode.addEvent("click", function () {
                    //this.handwritingNode.hide();
                    if (this.tablet) this.tablet.save();
                }.bind(this));
            }
        },
        upload: function( callback ){
            var img = this.tablet.getImage( null, true );
            if(img)Promise.resolve( img ).then(function( image ){
                debugger;
                Promise.resolve( this.tablet.getFormData(image) ).then(function (formData) {
                    var fileName = "handwriting"+"_"+new Date().getTime();
                    if( image.type && image.type.contains("/") ) {
                        image.name = fileName + "." + image.type.split("/")[1];
                    }
                    o2.xDesktop.uploadImageByScale(
                        this.form.businessData.work.job,
                        "processPlatformJob",
                        0, //maxSize
                        formData,
                        image,
                        function (json) {
                            if (callback) callback(json);
                        }.bind(this),
                        function () {

                        }.bind(this)
                    );
                }.bind(this))
            }.bind(this))
        },
        createErrorNode: function (text) {
            var node = new Element("div");
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
                    "line-height": "20px",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "keep-all"
                },
                "text": text
            }).inject(node);
            return node;
        },
        notValidationMode: function (text) {
            if (!this.isNotValidationMode) {
                this.isNotValidationMode = true;
                this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
                this.node.setStyle("border", "1px solid red");

                this.errNode = this.createErrorNode(text).inject(this.node, "after");
                this.showNotValidationMode(this.node);
                if (!this.errNode.isIntoView()) this.errNode.scrollIntoView(false);
            }
        },
        showNotValidationMode: function (node) {
            var p = node.getParent("div");
            if (p) {
                if (p.get("MWFtype") == "tab$Content") {
                    if (p.getParent("div").getStyle("display") == "none") {
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
        validationMode: function () {
            if (this.isNotValidationMode) {
                this.isNotValidationMode = false;
                this.node.setStyles(this.node.retrieve("borderStyle"));
                if (this.errNode) {
                    this.errNode.destroy();
                    this.errNode = null;
                }
            }
        },
        validationConfigItem: function (routeName, data) {
            var flag = (data.status == "all") ? true : (routeName == data.decision);
            if (flag) {
                var n = this.getData();
                var v = (data.valueType == "value") ? n : n.length;
                switch (data.operateor) {
                    case "isnull":
                        if (!v) {
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
        validationConfig: function (routeName, opinion) {
            if (this.json.validationConfig) {
                if (this.json.validationConfig.length) {
                    for (var i = 0; i < this.json.validationConfig.length; i++) {
                        var data = this.json.validationConfig[i];
                        if (!this.validationConfigItem(routeName, data)) return false;
                    }
                }
                return true;
            }
            return true;
        },
        validation: function (routeName, opinion) {
            if( !this.isReadonly() ){
                if (!this.validationConfig(routeName, opinion)) return false;

                if (!this.json.validation) return true;
                if (!this.json.validation.code) return true;
                this.currentRouteName = routeName;
                var flag = this.form.Macro.exec(this.json.validation.code, this);
                this.currentRouteName = "";
                if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
                if (flag.toString() != "true") {
                    this.notValidationMode(flag);
                    return false;
                }
            }
            return true;
        },
        _parseStyles: function (styles) {
            Object.each(styles, function (value, key) {
                if ((value.indexOf("x_processplatform_assemble_surface") != -1 || value.indexOf("x_portal_assemble_surface") != -1 || value.indexOf("x_cms_assemble_control") != -1)) {
                    var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                    var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                    var host3 = MWF.Actions.getHost("x_cms_assemble_control");
                    if (value.indexOf("/x_processplatform_assemble_surface") !== -1) {
                        value = value.replace("/x_processplatform_assemble_surface", host1 + "/x_processplatform_assemble_surface");
                    } else if (value.indexOf("x_processplatform_assemble_surface") !== -1) {
                        value = value.replace("x_processplatform_assemble_surface", host1 + "/x_processplatform_assemble_surface");
                    }
                    if (value.indexOf("/x_portal_assemble_surface") !== -1) {
                        value = value.replace("/x_portal_assemble_surface", host2 + "/x_portal_assemble_surface");
                    } else if (value.indexOf("x_portal_assemble_surface") !== -1) {
                        value = value.replace("x_portal_assemble_surface", host2 + "/x_portal_assemble_surface");
                    }
                    if (value.indexOf("/x_cms_assemble_control") !== -1) {
                        value = value.replace("/x_cms_assemble_control", host3 + "/x_cms_assemble_control");
                    } else if (value.indexOf("x_cms_assemble_control") !== -1) {
                        value = value.replace("x_cms_assemble_control", host3 + "/x_cms_assemble_control");
                    }
                    value = o2.filterUrl(value);
                }
            }.bind(this));

            return styles;
        }

    });
