MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Subform 子表单组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var subform = this.form.get("fieldId"); //获取组件
 * //方法2
 * var subform = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Subform = MWF.APPSubform = new Class(
    /** @lends MWF.xApplication.process.Xform.Subform# */
{
    Extends: MWF.APP$Module,

    _loadUserInterface: function () {
        this.node.empty();

        this.modules = [];
        this.moduleList = {};

        if (this.json.isDelay) {
            if (this.form.subformLoadedCount) {
                this.form.subformLoadedCount++;
            } else {
                this.form.subformLoadedCount = 1
            }
            this.form.checkSubformLoaded();
            this.checked = true;
        } else {

            this.getSubform(function () {
                this.loadSubform();
            }.bind(this));
        }
    },
    /**
     * @summary 当子表单被设置为延迟加载，通过active方法激活
     * @param {Function} callback
     * @example
     * var subform = this.form.get("fieldId");
     * subform.active(function(){
     *     //do someting
     * })
     */
    active: function (callback) {
        if (!this.loaded) {
            this.reload(callback)
        } else {
            if (callback) callback();
        }
    },
    /**
     * @summary 重新加载子表单
     * @param {Function} callback
     * @example
     * this.form.get("fieldId").reload(function(){
     *     //do someting
     * })
     */
    reload: function (callback) {
        this.clean();

        this.getSubform(function () {
            this.loadSubform();
            if (callback) callback();
        }.bind(this));
    },
    clean: function(){
        (this.modules || []).each(function(module){
            this.form.modules.erase(module);
        }.bind(this));

        Object.each(this.moduleList || {}, function (module, formKey) {
            delete this.form.json.moduleList[formKey];
        }.bind(this));

        if( this.subformData && this.subformData.json.id ){
            var id = this.subformData.json.id;
            if( this.form.subformLoaded && this.form.subformLoaded.length ){
                this.form.subformLoaded.erase(id);
            }
            if( this.parentformIdList && this.parentformIdList.length){
                this.parentformIdList.erase(id);
            }
        }

        this.modules = [];
        this.moduleList = {};

        this.node.empty();
    },
    loadCss: function () {
        if (this.subformData.json.css && this.subformData.json.css.code) {
            var cssText = this.form.parseCSS(this.subformData.json.css.code);
            var rex = new RegExp("(.+)(?=\\{)", "g");
            var match;
            var id = this.form.json.id.replace(/\-/g, "");
            while ((match = rex.exec(cssText)) !== null) {
                var prefix = ".css" + id + " ";
                var rule = prefix + match[0];
                cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                rex.lastIndex = rex.lastIndex + prefix.length;
            }

            var styleNode = $("style" + this.form.json.id);
            if (!styleNode) {
                var styleNode = document.createElement("style");
                styleNode.setAttribute("type", "text/css");
                styleNode.id = "style" + this.form.json.id;
                styleNode.inject(this.form.container, "before");
            }

            if (styleNode.styleSheet) {
                var setFunc = function () {
                    styleNode.styleSheet.cssText += cssText;
                };
                if (styleNode.styleSheet.disabled) {
                    setTimeout(setFunc, 10);
                } else {
                    setFunc();
                }
            } else {
                var cssTextNode = document.createTextNode(cssText);
                styleNode.appendChild(cssTextNode);
            }
        }
    },
    checkSubformNested: function (id) {
        if (!id) return true;
        if (this.parentformIdList) {
            return !this.parentformIdList.contains(id);
        } else {
            return ![this.form.json.id].contains(id);
        }
    },
    checkSubformUnique: function (id) {
        if (!id) return true;
        if (!this.form.subformLoaded) return true;
        return !this.form.subformLoaded.contains(id);
    },
    getParentformIdList: function () {
        var parentformIdList;
        if (this.parentformIdList) {
            parentformIdList = Array.clone(this.parentformIdList);
            parentformIdList.push(this.subformData.json.id)
        } else {
            parentformIdList = [this.form.json.id, this.subformData.json.id];
        }
        return parentformIdList;
    },
    loadSubform: function () {
        if (this.subformData) {
            if (!this.checkSubformNested(this.subformData.json.id)) {
                this.form.notice(MWF.xApplication.process.Xform.LP.subformNestedError, "error");
            } else if (!this.checkSubformUnique(this.subformData.json.id)) {
                this.form.notice(MWF.xApplication.process.Xform.LP.subformUniqueError, "error");
            } else {
                //this.form.addEvent("postLoad", function(){

                this.loadCss();

                this.modules = [];
                this.moduleList = {};

                this.node.set("html", this.subformData.html);
                Object.each(this.subformData.json.moduleList, function (module, key) {
                    var formKey = key;
                    if (this.form.json.moduleList[key]) {
                        formKey = this.json.id + "_" + key;
                        var moduleNode = this.node.getElement("#" + key);
                        if (moduleNode) moduleNode.set("id", formKey);
                        module.id = formKey;
                    }
                    this.form.json.moduleList[formKey] = module;
                    this.moduleList[formKey] = module;
                }.bind(this));

                var moduleNodes = this.form._getModuleNodes(this.node);
                moduleNodes.each(function (node) {
                    if (node.get("MWFtype") !== "form") {
                        var _self = this;
                        var json = this.form._getDomjson(node);
                        //if( json.type === "Subform" || json.moduleName === "subform" )this.form.subformCount++;
                        var module = this.form._loadModule(json, node, function () {
                            this.parentformIdList = _self.getParentformIdList();
                        });
                        this.form.modules.push(module);
                        this.modules.push(module);
                    }
                }.bind(this));

                this.form.subformLoaded.push(this.subformData.json.id);

                //}.bind(this));
            }
        }
        if (!this.checked) {
            if (this.form.subformLoadedCount) {
                this.form.subformLoadedCount++;
            } else {
                this.form.subformLoadedCount = 1
            }
            this.form.checkSubformLoaded();
        }
        //console.log( "add subformLoadedCount , this.form.subformLoadedCount = "+ this.form.subformLoadedCount)
        this.loaded = true;
        this.checked = true;
    },
    getSubform: function (callback) {
        var method = (this.form.json.mode !== "Mobile" && !layout.mobile) ? "getForm" : "getFormMobile";

        if (this.json.subformType === "script") {
            if (this.json.subformScript && this.json.subformScript.code) {
                var data = this.form.Macro.exec(this.json.subformScript.code, this);
                if (data) {
                    var formName, app;
                    if (typeOf(data) === "string") {
                        formName = data;
                    } else {
                        if (data.application) app = data.application;
                        if (data.subform) formName = data.subform;
                    }
                    if (formName) {
                        if (!app) app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                        MWF.Actions.get("x_processplatform_assemble_surface")[method](formName, app, function (json) {
                            this.getSubformData(json.data);
                            if (callback) callback();
                        }.bind(this));
                    } else {
                        if (callback) callback();
                    }
                } else {
                    if (callback) callback();
                }
            }
        } else {
            if (this.json.subformSelected && this.json.subformSelected !== "none") {
                var subformData = (this.form.app.relatedFormMap) ? this.form.app.relatedFormMap[this.json.subformSelected] : null;
                if (subformData) {
                    this.getSubformData({"data": subformData.data});
                    if (callback) callback();
                } else {
                    var app;
                    if (this.json.subformAppSelected) {
                        app = this.json.subformAppSelected;
                    } else {
                        app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                    }
                    MWF.Actions.get("x_processplatform_assemble_surface")[method](this.json.subformSelected, app, function (json) {
                        this.getSubformData(json.data);
                        if (callback) callback();
                    }.bind(this));
                }
            } else {
                if (callback) callback();
            }
        }
    },
    getSubformData: function (data) {
        if (!data || typeOf(data) !== "object") return;
        var subformDataStr = null;
        // if ( this.form.json.mode !== "Mobile" && !layout.mobile){
        //     subformDataStr = data.data;
        // }else{
        //     subformDataStr = data.mobileData;
        // }
        subformDataStr = data.data;
        this.subformData = null;
        if (subformDataStr) {
            var jsonStr = o2.bindJson(MWF.decodeJsonString(subformDataStr),  {"lp": MWF.xApplication.process.Xform.LP.form});
            this.subformData = JSON.decode(jsonStr);
            this.subformData.updateTime = data.updateTime;
        }
    }
});


MWF.xApplication.process.Xform.SubmitForm = MWF.APPSubmitform = new Class({
    Extends: MWF.APPSubform,
    _loadUserInterface: function () {
        // this.node.empty();
        this.getSubform(function () {
            this.loadSubform();
        }.bind(this));
    },
    reload: function () {
        // this.node.empty();
        this.getSubform(function () {
            this.loadSubform();
        }.bind(this));
    },
    show: function () {
        if (this.json.submitScript && this.json.submitScript.code) {
            this.form.Macro.exec(this.json.submitScript.code, this);
        }
        // this.fireSubFormEvent("load");
    },
    // fireSubFormEvent : function( name ){
    //     var events = this.subformData.json.events;
    //     if( events && events[name] && events[name]["code"] ){
    //         this.form.Macro.exec(events[name]["code"], this);
    //     }
    // },
    loadSubform: function () {
        if (this.subformData) {
            if (!this.checkSubformUnique(this.subformData.json.id)) { //如果提交表单已经嵌入到表单中，那么把这个表单弹出来
                // this.form.notice(MWF.xApplication.process.Xform.LP.subformUniqueError, "error");
                this.isEmbedded = true;
                this.fireEvent("afterModulesLoad");
            } else if (!this.checkSubformNested(this.subformData.json.id)) {
                this.form.notice(MWF.xApplication.process.Xform.LP.subformNestedError, "error");
            } else {
                //this.form.addEvent("postLoad", function(){

                // this.fireSubFormEvent("queryLoad");
                this.loadCss();

                this.node.set("html", this.subformData.html);
                Object.each(this.subformData.json.moduleList, function (module, key) {
                    var formKey = key;
                    if (this.form.json.moduleList[key]) {
                        formKey = this.json.id + "_" + key;
                        var moduleNode = this.node.getElement("#" + key);
                        if (moduleNode) moduleNode.set("id", formKey);
                        module.id = formKey;
                    }
                    this.form.json.moduleList[formKey] = module;
                }.bind(this));

                var moduleNodes = this.form._getModuleNodes(this.node);
                moduleNodes.each(function (node) {
                    if (node.get("MWFtype") !== "form") {
                        var _self = this;
                        var json = this.form._getDomjson(node);
                        //if( json.type === "Subform" || json.moduleName === "subform" )this.form.subformCount++;
                        var module = this.form._loadModule(json, node, function () {
                            this.parentformIdList = _self.getParentformIdList();
                        });
                        this.form.modules.push(module);
                    }
                }.bind(this));

                this.form.subformLoaded.push(this.subformData.json.id);
                this.fireEvent("afterModulesLoad");
                // this.fireSubFormEvent("postLoad");
                // this.fireSubFormEvent("load");
                // this.fireSubFormEvent("afterLoad");
            }
        }
        // if( this.form.subformLoadedCount ){
        //     this.form.subformLoadedCount++;
        // }else{
        //     this.form.subformLoadedCount = 1
        // }
        // this.form.checkSubformLoaded();
    },
    getSubform: function (callback) {
        var method = (this.form.json.mode !== "Mobile" && !layout.mobile) ? "getForm" : "getFormMobile";
        if (this.json.submitFormType === "script") {
            if (this.json.submitFormScript && this.json.submitFormScript.code) {
                var data = this.form.Macro.exec(this.json.submitFormScript.code, this);
                if (data) {
                    var formName, app;
                    if (typeOf(data) === "string") {
                        formName = data;
                    } else {
                        if (data.application) app = data.application;
                        if (data.form) formName = data.form;
                    }
                    if (formName) {
                        if (!app) app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                        MWF.Actions.get("x_processplatform_assemble_surface")[method](formName, app, function (json) {
                            this.getSubformData(json.data);
                            if (callback) callback();
                        }.bind(this));
                    } else {
                        if (callback) callback();
                    }
                } else {
                    if (callback) callback();
                }
            }
        } else {
            if (this.json.submitFormSelected && this.json.submitFormSelected !== "none") {
                var app;
                if (this.json.submitFormAppSelected) {
                    app = this.json.submitFormAppSelected;
                } else {
                    app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                }
                MWF.Actions.get("x_processplatform_assemble_surface")[method](this.json.submitFormSelected, app, function (json) {
                    this.getSubformData(json.data);
                    if (callback) callback();
                }.bind(this));
            } else {
                if (callback) callback();
            }
        }
    }
});
