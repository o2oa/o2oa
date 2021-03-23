MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.require("MWF.widget.O2Identity", null, false);
/** @class Org 人员组织组件。
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
 * field.resetData();  //重置字段的值为默认值或置空
 * @extends MWF.xApplication.process.Xform.$Input
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Org = MWF.APPOrg =  new Class(
    /** @lends MWF.xApplication.process.Xform.Org# */
    {
    Implements: [Events],
    Extends: MWF.APP$Input,
    options: {
        /**
         * 组件加载前触发。
         * @event MWF.xApplication.process.Xform.Org#queryLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 组件加载时触发.
         * @event MWF.xApplication.process.Xform.Org#load
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 组件加载后触发.
         * @event MWF.xApplication.process.Xform.Org#postLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 当组件值改变时触发。
         * @event MWF.xApplication.process.Xform.Org#change
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 当组件不允许输入（使用人员选择框）时，完成选择人员，并且给组件赋值后执行。
         * @event MWF.xApplication.process.Xform.Org#select
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "change", "select"],
        /**
         * 人员选择框事件：加载前执行。this.target指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#queryLoadSelector
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：加载后执行，由于选择项为异步加载，此时选择项并未加载完成。this.target指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#postLoadSelector
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：加载选择框容器节点前执行。this.target指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#queryLoadCategory
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：加载选择框容器节点后执行。this.target指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#postLoadContent
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */

        /**
         * 人员选择框事件：加载分类前执行。this.target指向分类，this.target.selector指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#queryLoadCategory
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：加载分类后执行。this.target指向分类，this.target.selector指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#postLoadCategory
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：选择分类后执行。this.target指向分类，this.target.selector指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#selectCategory
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：取消选择分类后执行。this.target指向分类，this.target.selector指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#unselectCategory
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：展开分类后执行。this.target指向分类，this.target.selector指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#expand
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：折叠分类后执行。this.target指向分类，this.target.selector指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#collapse
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */

        /**
         * 人员选择框事件：加载选择项前执行。this.target指向选择项，this.target.selector指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#queryLoadItem
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：加载选择项后执行。this.target指向选择项，this.target.selector指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#postLoadItem
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：选择选择项后执行。this.target指向选择项，this.target.selector指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#selectItem
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 人员选择框事件：取消选择选择项后执行。this.target指向选择项，this.target.selector指向人员选择框。
         * @event MWF.xApplication.process.Xform.Org#unselectItem
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "selectorEvents" : ["queryLoadSelector","postLoadSelector","queryLoadContent","postLoadContent","queryLoadCategory","postLoadCategory",
            "selectCategory", "unselectCategory","queryLoadItem","postLoadItem","selectItem", "unselectItem","change","expand","collapse"],
        "readonly": true
    },

    iconStyle: "orgIcon",

    getTextData: function(){
        //var value = this.node.get("value");
        //var text = this.node.get("text");
        var value = this.getValue();
        //var text = (this.node.getFirst()) ? this.node.getFirst().get("text") : this.node.get("text");
        var text = [];
        if( typeOf( value ) === "object" )value = [value];
        if( typeOf( value ) === "array" ){
            value.each(function(v){
                if( typeOf(v) === "string" ){
                    text.push(v);
                }else{
                    text.push(v.name+((v.unitName) ? "("+v.unitName+")" : ""));
                }
            }.bind(this));
            return {"value": value || "", "text": [text.join(",")]};
        }else{
            return {"value": [""], "text": [""]};
        }
    },

    loadDescription: function(){
        if (this.readonly || this.json.isReadonly)return;
        var v = this._getBusinessData();
        if (!v || !v.length){
            if (this.json.description){
                var size = this.node.getFirst().getSize();
                var w = size.x-3;
                if( this.json.showIcon!='no' && !this.form.json.hideModuleIcon ) {
                    if (COMMON.Browser.safari) w = w - 20;
                }
                this.descriptionNode = new Element("div", {"styles": this.form.css.descriptionNode, "text": this.json.description}).inject(this.node);
                this.descriptionNode.setStyles({
                    "width": ""+w+"px",
                    "height": ""+size.y+"px",
                    "line-height": ""+size.y+"px"
                });
                this.setDescriptionEvent();
            }
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            this.descriptionNode.addEvents({
                "mousedown": function( ev ){
                    this.descriptionNode.setStyle("display", "none");
                    this.clickSelect( ev );
                }.bind(this)
            });
        }
    },
    _loadNode: function(){
        this.field = true;
        if (this.readonly || this.json.isReadonly){
            this._loadNodeRead();
        }else{
            this._getOrgOptions();
            if (this.json.isInput){
                this._loadNodeInputEdit();
            }else{
                this._loadNodeEdit();
            }

        }
    },
    _getOrgOptions: function(){
        this.selectTypeList = typeOf( this.json.selectType ) == "array" ? this.json.selectType : [this.json.selectType];
        if( this.selectTypeList.contains( "identity" ) ) {
            this.identityOptions = new MWF.APPOrg.IdentityOptions(this.form, this.json);
        }
        if( this.selectTypeList.contains( "unit" ) ) {
            this.unitOptions = new MWF.APPOrg.UnitOptions(this.form, this.json);
        }
        if( this.selectTypeList.contains( "group" ) ){
            this.groupOptions = new MWF.APPOrg.GroupOptions( this.form, this.json );
        }

        //this.selectUnits = this.getSelectRange();
        //if (this.json.selectType=="identity"){
        //    this.selectDutys = this.getSelectRangeDuty();
        //}
    },

    _valueMerge: function(values, v){
        if (o2.typeOf(v)=="function"){
            return v.then(function(re){
                this._valueMerge(values, re)
            }.bind(this), function(){});
        }else{
            return values.concat(v);
        }
    },
    _computeValue: function(){

        var simple = this.json.storeRange === "simple";
        var values = [];
        if (this.json.identityValue) {
            this.json.identityValue.each(function(v){
                if (v) values.push(MWF.org.parseOrgData(v, true, simple))
            });
        }
        if (this.json.unitValue) {
            this.json.unitValue.each(function(v){ if (v) values.push(MWF.org.parseOrgData(v, true, simple))});
        }
        if (this.json.dutyValue) {
            var dutys = JSON.decode(this.json.dutyValue);
            var par;
            if (dutys.length){
                dutys.each(function(duty){
                    if (duty.code) par = this.form.Macro.exec(duty.code, this);
                    if (par){
                        var pars = (o2.typeOf(par)=="array") ? par : [par];
                        var promise = Promise.all(pars).then(function(p){
                            var uName = p.distinguishedName || p;
                            if (o2.typeOf(p)=="array") uName = p[0].distinguishedName || p[0];
                            var code = "return this.org.getDuty(\""+duty.name+"\", \""+uName+"\", true)";
                            var r = (!!uName) ? this.form.Macro.exec(code, this) : "";

                            var m = (o2.typeOf(r)=="array") ? "all" : "resolve";
                            return Promise[m](r).then(function(d){
                                if (typeOf(d)!=="array") d = (d) ? [d.toString()] : [];
                                var arr = [];
                                d.each(function(dd){
                                    if (dd) arr.push(MWF.org.parseOrgData(dd, true, simple));
                                });
                                return arr;
                            }.bind(this)).catch(function(){
                                console.log("catch error : can not get duty : " + duty.name, + "-" + uName);
                            });
                        }.bind(this), function(){});
                        values.push(promise);
                    }
                }.bind(this));
            }
        }
        if (this.json.defaultValue && this.json.defaultValue.code){
            var fd = this.form.Macro.exec(this.json.defaultValue.code, this);
            if (o2.typeOf(fd)=="array"){
                fd.each(function(v){values.push(v);});
            }else{
                values.push(fd);
            }

            // if (fd && fd.isAG){
            //     values.push(fd);
            // }else{
            //     if (typeOf(fd)!=="array") fd = (fd) ? [fd] : [];
            //     fd.each(function(fdd){
            //         if (fdd){
            //             if (typeOf(fdd)==="string"){
            //                 var data;
            //                 this.getOrgAction()[this.getValueMethod(fdd)](function(json){ data = MWF.org.parseOrgData(json.data, true, simple); }.bind(this), null, fdd, false);
            //                 values.push(data);
            //             }else{
            //                 values.push(fdd);
            //             }
            //         }
            //     }.bind(this));
            // }
        }
        // if (this.json.count>0){
        //     return values.slice(0, this.json.count);
        // }
        return values;
        //return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
    },
    __computeValue: function(){
        var simple = this.json.storeRange === "simple";
        var values = [];
        if (this.json.identityValue) {
            this.json.identityValue.each(function(v){
                if (v) values.push(MWF.org.parseOrgData(v, true, simple))
            });
        }
        if (this.json.unitValue) {
            this.json.unitValue.each(function(v){ if (v) values.push(MWF.org.parseOrgData(v, true, simple))});
        }
        if (this.json.dutyValue) {
            var dutys = JSON.decode(this.json.dutyValue);
            var par;
            if (dutys.length){
                dutys.each(function(duty){
                    if (duty.code) par = this.form.Macro.exec(duty.code, this);
                    if (par && par.isAG){
                        var ag = o2.AG.all(par).then(function(p){
                            var uName = "";
                            if (p && p.length) uName = p[0].distinguishedName || p[0];
                            var code = "return this.org.getDuty(\""+duty.name+"\", \""+uName+"\", true)";
                            var r = this.form.Macro.exec(code, this);

                            o2.AG.all(r).then(function(d) {
                                //var d = rd[0];
                                if (typeOf(d)!=="array") d = (d) ? [d.toString()] : [];
                                var arr = [];
                                d.each(function(dd){
                                    if (dd) arr.push(MWF.org.parseOrgData(dd, true, simple));
                                });
                                return arr;
                            }.bind(this));
                        }.bind(this));
                        values.push(ag);
                    }else{
                        var code = "return this.org.getDuty(\""+duty.name+"\", \""+par+"\", true)";
                        var r = this.form.Macro.exec(code, this);
                        var ag = o2.AG.all(r).then(function(d) {
                            //var d = rd[0];
                            if (typeOf(d)!=="array") d = (d) ? [d.toString()] : [];
                            var arr = [];
                            d.each(function(dd){
                                if (dd) arr.push(MWF.org.parseOrgData(dd, true, simple));
                            });
                            return arr;
                        }.bind(this));
                        values.push(ag);

                        // if (typeOf(d)!=="array") d = (d) ? [d.toString()] : [];
                        // d.each(function(dd){if (dd) values.push(MWF.org.parseOrgData(dd, true, simple));});
                    }

                }.bind(this));
            }
        }
        if (this.json.defaultValue && this.json.defaultValue.code){
            var fd = this.form.Macro.exec(this.json.defaultValue.code, this);

            if (fd && fd.isAG){
                // value.addResolve(function(v){
                //     this._setBusinessData(v);
                //     if (this.node.getFirst()) this.node.getFirst().set("value", v || "");
                //     if (this.readonly || this.json.isReadonly) this.node.set("text", v);
                // }.bind(this));
                values.push(fd);
                // fd.then(function(v){
                //     return this._valueMerge(values, v);
                // }.bind(this));
                // return fd;
            }else{
                if (typeOf(fd)!=="array") fd = (fd) ? [fd] : [];
                fd.each(function(fdd){
                    if (fdd){
                        if (typeOf(fdd)==="string"){
                            var data;
                            this.getOrgAction()[this.getValueMethod(fdd)](function(json){ data = MWF.org.parseOrgData(json.data, true, simple); }.bind(this), null, fdd, false);
                            values.push(data);
                        }else{
                            values.push(fdd);
                        }
                    }
                }.bind(this));
            }
        }
        if (this.json.count>0){
            return values.slice(0, this.json.count);
        }
        return values;
        //return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
    },

    getOrgAction: function(){
        if (!this.orgAction) this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        //if (!this.orgAction) this.orgAction = new MWF.xApplication.Selector.Actions.RestActions();
        return this.orgAction;
    },

    getOptions: function(){

        var _self = this;

        if( this.selectTypeList.length === 0 )return false;

        var values = this.getInputData() || [];

        var exclude = [];
        if( this.json.exclude ){
            var v = this.form.Macro.exec(this.json.exclude.code, this);
            exclude = typeOf(v)==="array" ? v : [v];
        }

        //var count = (this.json.count) ? this.json.count : 0;

        var identityOpt;
        if( this.identityOptions ){
            identityOpt = this.identityOptions.getOptions();
            if (this.json.identityRange!=="all"){
                if ( !identityOpt.noUnit && (!identityOpt.units || !identityOpt.units.length) ){
                    this.form.notice(MWF.xApplication.process.Xform.LP.noIdentitySelectRange, "error", this.node);
                    identityOpt.disabled = true;
                    // return false;
                }
            }
            if ( !identityOpt.noUnit && this.json.dutyRange && this.json.dutyRange!=="all"){
                if (!identityOpt.dutys || !identityOpt.dutys.length){
                    this.form.notice(MWF.xApplication.process.Xform.LP.noIdentityDutySelectRange, "error", this.node);
                    identityOpt.disabled = true;
                    // return false;
                }
            }
            identityOpt.values = (this.json.isInput) ? [] : values;
            identityOpt.exclude = exclude;
            if( this.form.json.selectorStyle )identityOpt = Object.merge( identityOpt, this.form.json.selectorStyle );
        }

        var unitOpt;
        if( this.unitOptions ){
            unitOpt = this.unitOptions.getOptions();
            if (this.json.unitRange!=="all"){
                if ( !unitOpt.units || !unitOpt.units.length){
                    this.form.notice(MWF.xApplication.process.Xform.LP.noUnitSelectRange, "error", this.node);
                    unitOpt.disabled = true;
                    // return false;
                }
            }
            unitOpt.values = (this.json.isInput) ? [] : values;
            unitOpt.exclude = exclude;
            if( this.form.json.selectorStyle )unitOpt = Object.merge( unitOpt, this.form.json.selectorStyle );
        }

        var groupOpt;
        if( this.groupOptions ){
            groupOpt = this.groupOptions.getOptions();
            groupOpt.values = (this.json.isInput) ? [] : values;
            groupOpt.exclude = exclude;
            if( this.form.json.selectorStyle )groupOpt = Object.merge( groupOpt, this.form.json.selectorStyle );
        }

        //var selectUnits = this.getSelectRange();
        //if (this.json.selectType=="identity"){
        //    var selectDutys = this.getSelectRangeDuty();
        //}

        //if (this.json.range!=="all"){
        //    if (!selectUnits.length){
        //        this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
        //        return false;
        //    }
        //}
        //if (this.json.selectType=="identity"){
        //    if ((this.json.dutyRange) && this.json.dutyRange!=="all"){
        //        if (!selectDutys || !selectDutys.length){
        //            this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
        //            return false;
        //        }
        //    }
        //}

        var defaultOpt = {};

        if( this.json.events && typeOf(this.json.events) === "object" ){
            Object.each(this.json.events, function(e, key){
                if (e.code){
                    if (this.options.selectorEvents.indexOf(key)!==-1){
                        if( key === "postLoadSelector" ) {
                            this.addEvent("loadSelector", function (selector) {
                                return this.form.Macro.fire(e.code, selector);
                            }.bind(this))
                        }else if( key === "queryLoadSelector"){
                            defaultOpt["onQueryLoad"] = function(target){
                                return this.form.Macro.fire(e.code, target);
                            }.bind(this)
                        }else{
                            defaultOpt["on"+key.capitalize()] = function(target){
                                return this.form.Macro.fire(e.code, target);
                            }.bind(this)
                        }
                    }
                }
            }.bind(this));
        }

        if( this.selectTypeList.length === 1 ){
            return Object.merge( {
                "type": this.selectTypeList[0],
                "onComplete": function(items){
                    this.selectOnComplete(items);
                }.bind(this),
                "onCancel": this.selectOnCancel.bind(this),
                // "onLoad": this.selectOnLoad.bind(this),
                "onLoad": function(){
                    //this 为 selector
                    _self.selectOnLoad(this, this.selector );
                },
                "onClose": this.selectOnClose.bind(this)
            }, defaultOpt, identityOpt || unitOpt || groupOpt )
        }else if( this.selectTypeList.length > 1 ){
            var options = {
                "type" : "",
                "types" : this.selectTypeList,
                "onComplete": function(items){
                    this.selectOnComplete(items);
                }.bind(this),
                "onCancel": this.selectOnCancel.bind(this),
                // "onLoad": this.selectOnLoad.bind(this),
                "onLoad": function(){
                    //this 为 selector
                    _self.selectOnLoad(this)
                },
                "onClose": this.selectOnClose.bind(this)
            };
            if( this.form.json.selectorStyle ){
                options = Object.merge( options, this.form.json.selectorStyle );
            }
            if( identityOpt )options.identityOptions = Object.merge( {}, defaultOpt, identityOpt );
            if( unitOpt )options.unitOptions =  Object.merge( {}, defaultOpt, unitOpt );
            if( groupOpt )options.groupOptions = Object.merge( {}, defaultOpt, groupOpt );
            return options;
        }

        //return {
        //    "type": this.json.selectType,
        //    "unitType": (this.json.selectUnitType==="all") ? "" : this.json.selectUnitType,
        //    "values": (this.json.isInput) ? [] : values,
        //    "count": count,
        //    "units": selectUnits,
        //    "dutys": (this.json.selectType=="identity") ? selectDutys : [],
        //    "exclude" : exclude,
        //    "expandSubEnable" : (this.json.expandSubEnable=="no") ? false : true,
        //    "categoryType": this.json.categoryType || "unit",
        //    "onComplete": function(items){
        //        this.selectOnComplete(items);
        //    }.bind(this),
        //    "onCancel": this.selectOnCancel.bind(this),
        //    "onLoad": this.selectOnLoad.bind(this),
        //    "onClose": this.selectOnClose.bind(this)
        //};
    },
    selectOnComplete: function(items){
        var array = [];
        items.each(function(item){
            array.push(item.data);
        }.bind(this));

        var simple = this.json.storeRange === "simple";

        this.checkEmpower( array, function( data ){
            var values = [];
            data.each(function(d){
                values.push(MWF.org.parseOrgData(d, true, simple));
            }.bind(this));

            if (this.json.isInput){
                this.addData(values);
            }else{
                this.setData(values);
            }
            //this._setBusinessData(values);
            this.validationMode();
            this.validation();
            var p = this.getValue();
            if (p.then){
                p.then(function(){
                    this.fireEvent("select");
                }.bind(this), function(){});
            }else{
                this.fireEvent("select");
            }

        }.bind(this))
    },
    selectOnCancel: function(){
        this.validation();
    },
    selectOnLoad: function( selector ){
        if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
        this.fireEvent("loadSelector", [selector])
    },
    selectOnClose: function(){
        v = this._getBusinessData();
        if (!v || !v.length) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
    },

    /**
     * @summary 弹出选择界面.
     * @example
     * this.form.get('org').clickSelect();
     */
    clickSelect: function( ev ){
        if (this.readonly)return;
        if( layout.mobile ){
            setTimeout( function(){ //如果有输入法界面，这个时候页面的计算不对，所以等100毫秒
                var options = this.getOptions();
                if(options){
                    if( this.selector && this.selector.loading ) {
                    }else if( this.selector && this.selector.selector && this.selector.selector.active ){
                    }else{
                        /**
                         * @summary 人员选择框package的对象
                         * @member {o2.O2Selector}
                         * @example
                         *  //可以在脚本中获取该组件
                         * var selector = this.form.get("fieldId").selector.selector; //获取人员选择框对象
                         * var options = selector.options; //获取人员选择框的选项
                         */
                        this.selector = new MWF.O2Selector(this.form.app.content, options);
                    }
                }
            }.bind(this), 100 )
        }else{
            var options = this.getOptions();
            if(options){
                if( this.selector && this.selector.loading ) {
                }else if( this.selector && this.selector.selector && this.selector.selector.active ){
                }else {
                    this.selector = new MWF.O2Selector(this.form.app.content, options);
                }
            }
        }
    },
    resetData: function(){

        var v = this.getValue();
        //this.setData((v) ? v.join(", ") : "");
        this.setData(v);
    },
    isEmpty: function(){
        var data = this.getData();
        if( typeOf(data) !== "array" )return true;
        if( data.length === 0 )return true;
        return false;
    },

    getInputData: function(){
        if (this.json.isInput){
            if (this.combox)return this.combox.getData();
            //return this._getBusinessData();
            return this.node.retrieve("data");
        }else{
            //return this._getBusinessData();
            return this.node.retrieve("data");
        }
    },
    _loadNodeRead: function(){
        this.node.empty();
        var node = new Element("div").inject(this.node);
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
    },
    _searchConfirmPerson: function(item){
        var inforNode = item.inforNode || new Element("div");

        var simple = this.json.storeRange === "simple";

        if (item.data){

            var data = item.data;
            if( this.selectTypeList.contains("identity") && this.json.identityResultType === "person"){
                var dn = data.distinguishedName || data;
                if( dn.substr( dn.length-1, 1).toLowerCase() === "i" ){
                    MWF.Actions.get("x_organization_assemble_express").listPersonWithIdentity({
                        identityList : [dn]
                    }, function(json){
                        if( json.data.length > 0 ){
                            if( data["person"] )json.data[0].id = data["person"];
                            item.data = MWF.org.parseOrgData( json.data[0], true, simple );
                            item.value = this.getDataText( item.data );
                            if(item.node)item.node.set("text", item.value);
                        }
                    }.bind(this), null, false)
                }
            }
            if( item.data && ( item.data.createTime || item.data.updateTime ) ){
                item.data = MWF.org.parseOrgData( item.data, true, simple );
            }

            var text = "";
            var flag = item.data.distinguishedName.substr(item.data.distinguishedName.length-2, 2);
            switch (flag.toLowerCase()){
                case "@i":
                    text = item.data.name+"("+item.data.unitName+")";
                    break;
                case "@p":
                    text = item.data.name+(item.data.employee ? "("+item.data.employee+")" : "");
                    break;
                case "@u":
                    text = item.data.levelName;
                    break;
                case "@g":
                    text = item.data.name;
                    break;
                default:
                    text = item.data.name;
            }
            inforNode.set({
                "styles": {"font-size": "14px", "color": ""},
                "text": text
            });
        }else{
            inforNode.set({
                "styles": {"font-size": "14px", "color": "#bd0000"},
                "text": MWF.xApplication.process.Xform.LP.noOrgObject
            });
        }
        if (!item.inforNode){
            new mBox.Tooltip({
                content: inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: item.node,
                transition: 'flyin'
            });
            item.inforNode = inforNode;
        }

    },
    getSearchOptions: function(){

        if( this.selectTypeList.length === 0 )return false;

        var identityOpt;
        if( this.identityOptions ){
            identityOpt = this.identityOptions.getSearchOptions();
            //if (this.json.identityRange!=="all"){
            //    if ( !identityOpt.units || !identityOpt.units.length){
            //        this.form.notice(MWF.xApplication.process.Xform.LP.noIdentitySelectRange, "error", this.node);
            //        return false;
            //    }
            //}
            //if ((this.json.dutyRange) && this.json.dutyRange!=="all"){
            //    if (!identityOpt.dutys || !identityOpt.dutys.length){
            //        this.form.notice(MWF.xApplication.process.Xform.LP.noIdentityDutySelectRange, "error", this.node);
            //        return false;
            //    }
            //}
        }

        var unitOpt;
        if( this.unitOptions ){
            unitOpt = this.unitOptions.getSearchOptions();
            //if (this.json.unitRange!=="all"){
            //    if ( !unitOpt.units || !unitOpt.units.length){
            //        this.form.notice(MWF.xApplication.process.Xform.LP.noUnitSelectRange, "error", this.node);
            //        return false;
            //    }
            //}
        }

        var groupOpt;
        if( this.groupOptions ){
            groupOpt = this.groupOptions.getOptions();
        }

        if( this.selectTypeList.length === 1 ){
            return Object.merge( {
                "type": this.selectTypeList[0]
            }, identityOpt || unitOpt || groupOpt )
        }else if( this.selectTypeList.length > 1 ){
            var options = {
                "type" : "",
                "types" : this.selectTypeList
            };
            if( identityOpt )options.identityOptions = identityOpt;
            if( unitOpt )options.unitOptions = unitOpt;
            if( groupOpt )options.groupOptions = groupOpt;
            return options;
        }

        //return {
        //    "type": this.json.selectType,
        //    "unitType": (this.json.selectUnitType==="all") ? "" : this.json.selectUnitType,
        //    "values": (this.json.isInput) ? [] : values,
        //    "count": count,
        //    "units": selectUnits,
        //    "dutys": (this.json.selectType=="identity") ? selectDutys : [],
        //    "exclude" : exclude,
        //    "expandSubEnable" : (this.json.expandSubEnable=="no") ? false : true,
        //    "categoryType": this.json.categoryType || "unit",
        //    "onComplete": function(items){
        //        this.selectOnComplete(items);
        //    }.bind(this),
        //    "onCancel": this.selectOnCancel.bind(this),
        //    "onLoad": this.selectOnLoad.bind(this),
        //    "onClose": this.selectOnClose.bind(this)
        //};
    },
    _searchOptions: function(value, callback){
        //var options = {
        //    "type": this.json.selectType,
        //    "unitType": (this.json.selectUnitType==="all") ? "" : this.json.selectUnitType,
        //    "units": this.selectUnits,
        //    "dutys": (this.json.selectType=="identity") ? this.selectDutys : []
        //};
        var options = this.getSearchOptions();
        if (!this.comboxFilter) this.comboxFilter = new MWF.O2SelectorFilter(value, options);
        this.comboxFilter.filter(value, function(data){
            data.map(function(d){
                var value = Object.clone(d);
                d.value = value;
                var flag = d.distinguishedName.substr(d.distinguishedName.length-2, 2);
                switch (flag.toLowerCase()){
                    case "@i":
                        d.text = d.name+"("+d.unitName+")";
                        break;
                    case "@p":
                        d.text = d.name+(d.employee ? "("+d.employee+")" : "");
                        break;
                    case "@u":
                        d.text = d.name;
                        break;
                    case "@g":
                        d.text = d.name;
                        break;
                    default:
                        d.text = d.name;
                }
            });
            if (callback) callback(data);
        });
    },

    _resetNodeInputEdit: function(){
        var node = new Element("div", {
            "styles": {
                "overflow": "hidden",
                //"position": "relative",
                "margin-right": "20px"
            }
        }).inject(this.node, "after");
        this.node.destroy();
        this.node = node;
    },
    _loadNodeInputEdit: function(){

        this.node.setStyle("overflow","visible");
        var input=null;
        MWF.require("MWF.widget.Combox", function(){
            this.combox = input = new MWF.widget.Combox({
                "count": this.json.count || 0,
                "splitShow": this.json.splitShow || ", ",
                "onCommitInput": function(item){
                    this._searchConfirmPerson(item);
                    //this.fireEvent("change");
                }.bind(this),
                "onChange": function(){
                    this.node.store("data", this.getInputData());
                    this._setBusinessData(this.getInputData());
                    this.fireEvent("change");
                }.bind(this),
                "optionsMethod": this._searchOptions.bind(this)

            });
        }.bind(this), false);
        input.setStyles({
            "background": "transparent",
            "border": "0px"
        });
        input.set(this.json.properties);

        if (!this.json.preprocessing) this._resetNodeInputEdit();

        this.node.empty();
        input.inject(this.node);
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
        if (this.json.showIcon!='no' && !this.form.json.hideModuleIcon) {
            this.iconNode = new Element("div", {
                "styles": this.form.css[this.iconStyle],
                "events": {
                    "click": function (ev) {
                        this.clickSelect(ev);
                    }.bind(this)
                    //this.clickSelect.bind(this)
                }
            }).inject(this.node, "before");
        }else if( this.form.json.nodeStyleWithhideModuleIcon ){
            this.node.setStyles(this.form.json.nodeStyleWithhideModuleIcon)
        }

        this.combox.addEvent("change", function(){
            this.validationMode();
            if (this.validation()){
                this.node.store("data", this.getInputData());
                this._setBusinessData(this.getInputData("change"));
            }
        }.bind(this));
    },

    _resetNodeEdit: function(){
        var input = new Element("div", {
            "styles": {
                "background": "transparent",
                "border": "0px",
                "min-height": "24px"
            }
        });
        var node = new Element("div", {"styles": {
                "overflow": "hidden",
                "position": "relative",
                "margin-right": "20px",
                "min-height": "24px"
            }}).inject(this.node, "after");
        input.inject(node);
        this.node.destroy();
        this.node = node;
    },
    _loadNodeEdit: function(){
        if (!this.json.preprocessing) this._resetNodeEdit();
        var input = this.node.getFirst();
        input.set(this.json.properties);
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type,
            "events": {
                "click": function (ev) {
                    this.clickSelect(ev);
                }.bind(this)
                //this.clickSelect.bind(this)
            }
        });
        if (this.json.showIcon!='no' && !this.form.json.hideModuleIcon) {
            this.iconNode = new Element("div", {
                "styles": this.form.css[this.iconStyle],
                "events": {
                    "click": function (ev) {
                        this.clickSelect(ev);
                    }.bind(this)
                    //this.clickSelect.bind(this)
                }
            }).inject(this.node, "before");
        }else if( this.form.json.nodeStyleWithhideModuleIcon ){
            this.node.setStyles(this.form.json.nodeStyleWithhideModuleIcon)
        }

        this.node.getFirst().setStyle("height", "auto");
        this.node.getFirst().addEvent("change", function(){
            this.validationMode();
            if (this.validation()){
                this.node.store("data", this.getInputData());
                this._setBusinessData(this.getInputData("change"));
            }
        }.bind(this));
    },
    getDataText: function(data){
        if (typeOf(data)=="string") return data;
        var text = "";
        var flag = data.distinguishedName.substr(data.distinguishedName.length-2, 2);
        switch (flag.toLowerCase()){
            case "@i":
                text = data.name+"("+data.unitName+")";
                break;
            case "@p":
                text = data.name+ (data.employee ? ("("+data.employee+")") : "");
                break;
            case "@u":
                text = data.name;
                break;
            case "@g":
                text = data.name;
                break;
            default:
                text = data.name;
        }
        return text;
    },
    addData: function(value){
        if (!value) return false;
        var simple = this.json.storeRange === "simple";
        value.each(function(v){
            var vtype = typeOf(v);
            if (vtype==="string"){
                var data;
                this.getOrgAction()[this.getValueMethod(v)](function(json){ data = MWF.org.parseOrgData(json.data, true, simple); }.bind(this), null, v, false);
                if (data) this.combox.addNewValue(this.getDataText(data), data);
            }
            if (vtype==="object"){
                var d = MWF.org.parseOrgData(v, true, simple);
                this.combox.addNewValue(this.getDataText(d), d);
            }
        }.bind(this));
    },
    checkChange: function(oldValues, values){
        var change = false;
        if (!values) values = [];
        if (oldValues.length && (values && values.length)){
            if (oldValues.length === values.length){
                for (var i=0; i<oldValues.length; i++){
                    if ((oldValues[i].distinguishedName!==values[i].distinguishedName) || (oldValues[i].name!==values[i].name) || (oldValues[i].unique!==values[i].unique)){
                        change = true;
                        break;
                    }
                }
            }else{
                change = true;
            }
        }else if (values.length || oldValues.length) {
            change = true;
        }
        if (change) this.fireEvent("change");
    },
    setData: function(value){
        if (!value) return false;
        var oldValues = this.getData();
        if (value.length==1 && !(value[0])) value=[];

        var promise = this._setValue(value);
        promise.then(function(values){
            o2.promiseAll(values).then(function(v){
                this.checkChange(oldValues, v)
            }.bind(this), function(){});

            // if (values && values.isAG){
            //     values.then(function(v){
            //         this.checkChange(oldValues, v)
            //     }.bind(this));
            // }else{
            //     this.checkChange(oldValues, values)
            // }
        }.bind(this), function(){});
    },
    // __setData: function(value){
    //     if (!value) return false;
    //     var oldValues = this.getData();
    //     var values = [];
    //     var comboxValues = [];
    //
    //     var simple = this.json.storeRange === "simple";
    //
    //     var type = typeOf(value);
    //     if (type==="array"){
    //         value.each(function(v){
    //             var vtype = typeOf(v);
    //             var data = null;
    //             if (vtype==="string"){
    //                 var error = (this.json.isInput) ? function(){ comboxValues.push(v); } : null;
    //                 this.getOrgAction()[this.getValueMethod(v)](function(json){ data = MWF.org.parseOrgData(json.data, true, simple); }.bind(this), error, v, false);
    //             }
    //             if (vtype==="object") {
    //                 data = MWF.org.parseOrgData(v, true, simple);
    //                 if(data.woPerson)delete data.woPerson;
    //             }
    //             if (data){
    //                 values.push(data);
    //                 comboxValues.push({"text": this.getDataText(data),"value": data});
    //             }
    //         }.bind(this));
    //     }
    //     if (type==="string"){
    //         var vData;
    //         var error = (this.json.isInput) ? function(){ comboxValues.push(value); } : null;
    //         this.getOrgAction()[this.getValueMethod(value)](function(json){ vData = MWF.org.parseOrgData(json.data, true, simple); }.bind(this), error, value, false);
    //         if (vData){
    //             values.push(vData);
    //             comboxValues.push({"text": this.getDataText(vData),"value": vData});
    //         }
    //     }
    //     if (type==="object"){
    //         var vData = MWF.org.parseOrgData(value, true, simple);
    //         if(vData.woPerson)delete vData.woPerson;
    //         values.push( vData );
    //         comboxValues.push({"text": this.getDataText(value),"value": vData});
    //     }
    //
    //     var change = false;
    //     if (oldValues.length && values.length){
    //         if (oldValues.length === values.length){
    //             for (var i=0; i<oldValues.length; i++){
    //                 if ((oldValues[i].distinguishedName!==values[i].distinguishedName) || (oldValues[i].name!==values[i].name) || (oldValues[i].unique!==values[i].unique)){
    //                     change = true;
    //                     break;
    //                 }
    //             }
    //         }else{
    //             change = true;
    //         }
    //     }else if (values.length || oldValues.length) {
    //         change = true;
    //     }
    //     this._setBusinessData(values);
    //     if (change) this.fireEvent("change");
    //
    //     if (this.json.isInput){
    //         if (this.combox){
    //             this.combox.clear();
    //             this.combox.addNewValues(comboxValues);
    //         }else{
    //             var node = this.node.getFirst();
    //             if (node){
    //                 node.empty();
    //                 comboxValues.each(function(v, i){
    //                     this.creteShowNode(v, (i===comboxValues.length-1)).inject(node);
    //                 }.bind(this));
    //             }
    //         }
    //     }else{
    //         if (this.node.getFirst()){
    //             var node = this.node.getFirst();
    //             node.empty();
    //             this.loadOrgWidget(values, node)
    //         }else{
    //             this.node.empty();
    //             this.loadOrgWidget(values, this.node);
    //         }
    //     }
    // },
    creteShowNode: function(data, islast){
        var nodeText = (data.text) ?  data.text : data;
        if (!islast) nodeText = nodeText + (this.json.splitShow || ", ");

        var node = new Element("div", {
            "styles": {
                "float": "left",
                "margin-right": "5px"
            },
            "text": nodeText
        });
        var text = "";
        if (data.value){
            var flag = data.value.distinguishedName.substr(data.value.distinguishedName.length-2, 2);
            switch (flag.toLowerCase()){
                case "@i":
                    text = data.value.name+"("+data.value.unitName+")";
                    break;
                case "@p":
                    text = data.value.name+ (data.value.employee ? "("+data.value.employee+")" : "");
                    break;
                case "@u":
                    text = data.value.levelName;
                    break;
                case "@g":
                    text = data.value.name;
                    break;
                default:
                    text = data.value.name;
            }
            var inforNode = new Element("div").set({
                "styles": {"font-size": "14px", "color": ""},
                "text": text
            });

            new mBox.Tooltip({
                content: inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: node,
                transition: 'flyin'
            });
        }
        return node;
    },
    _setValue: function(value){
        var values = [];
        var ags = [];
        var simple = this.json.storeRange === "simple";
        var flag = false;
        if (typeOf(value)!=="array") value = (!!value) ? [value] : [];
        //value = (value.flat) ? value.flat() : value.flatten();

        var p = o2.promiseAll(value).then(function(d){
            if (typeOf(d)!=="array") d = (!!d) ? [d] : [];
            d.each(function(da){
                if (typeOf(da)!=="array") da = (!!da) ? [da] : [];
                da.each(function(dd){
                    if (dd){
                        if (typeOf(dd)==="string"){
                            var pp = this.getOrgAction()[this.getValueMethod(dd)](function(json){
                                return MWF.org.parseOrgData(json.data, true, simple);
                            }.bind(this), null, dd, true).catch(function(e){
                                console.log("error:" + e);
                                console.log(e);
                            });
                            ags.push(pp);
                        }else{
                            values.push(dd);
                        }
                    }
                }.bind(this));
            }.bind(this));
            if (ags.length){
                return o2.promiseAll(ags).then(function(data){
                    values = values.concat(data);
                    flag = true;
                    this.__setValue(values);
                    return values;
                }.bind(this), function(){});
            }else{
                flag = true;
                this.__setValue(values);
                return values
            }
        }.bind(this), function(){});

        this.moduleValueAG = p;
        if (p && p.then) p.then(function(){
            this.moduleValueAG = null;
        }.bind(this), function(){
            this.moduleValueAG = null;
        }.bind(this));
        return p;

        // var ag = o2.AG.all(value).then(function(d) {
        //     if (typeOf(d)!=="array") d = (d) ? [d.toString()] : [];
        //
        //     d.each(function(dd){
        //         //if (dd) arr.push(MWF.org.parseOrgData(dd, true, simple));
        //         if (dd){
        //             if (typeOf(dd)==="string"){
        //                 ags.push(this.getOrgAction()[this.getValueMethod(dd)](function(json){
        //                     return MWF.org.parseOrgData(json.data, true, simple);
        //                 }.bind(this).ag(), null, dd, true));
        //             }else{
        //                 values.push(dd);
        //             }
        //         }
        //     }.bind(this));
        //     if (ags.length){
        //         return o2.AG.all(ags).then(function(data){
        //             values = values.concat(data);
        //             flag = true;
        //             this.__setValue(values);
        //             return values;
        //         }.bind(this));
        //     }else{
        //         flag = true;
        //         this.__setValue(values);
        //         return values
        //     }
        // }.bind(this));
        //
        // this.moduleValueAG = ag;
        // if (ag) ag.then(function(){
        //     this.moduleValueAG = null;
        // }.bind(this));
        // return ag;
    },
    __setValue: function(value){
        this.moduleValueAG = null;
        if (value.length==1 && !(value[0])) value=[];

        if (this.json.count>0){
            value = value.slice(0, this.json.count);
        }

        var values = [];
        var comboxValues = [];
        var type = typeOf(value);
        var simple = this.json.storeRange === "simple";
        if (type==="array"){
            value.each(function(v){
                var data=null;
                var vtype = typeOf(v);
                if (vtype==="string"){
                    var error = (this.json.isInput) ? function(){ comboxValues.push(v); } : null;
                    this.getOrgAction()[this.getValueMethod(v)](function(json){ data = MWF.org.parseOrgData(json.data, true, simple); }.bind(this), error, v, false);
                }
                if (vtype==="object") data = v;
                if (data){
                    var d = MWF.org.parseOrgData(data, true, simple)
                    values.push(d);
                    comboxValues.push({"text": this.getDataText(d),"value": d});
                }
            }.bind(this));
        }
        if (type==="string"){
            var vData;
            var error = (this.json.isInput) ? function(){ comboxValues.push(value); } : null;
            this.getOrgAction()[this.getValueMethod(value)](function(json){ vData = MWF.org.parseOrgData(json.data, true,simple); }.bind(this), error, value, false);
            if (vData){
                values.push(vData);
                comboxValues.push({"text": this.getDataText(vData),"value": vData});
            }
        }
        if (type==="object"){
            var v = MWF.org.parseOrgData(value, true, simple)
            values.push(v);
            comboxValues.push({"text": this.getDataText(v),"value": v});
        }

        this.node.store("data", values);
        this._setBusinessData(values);

        if (this.json.isInput){

            if (this.combox){
                this.combox.clear();
                this.combox.addNewValues(comboxValues);

                // values.each(function(v){
                //     if (typeOf(v)=="string"){
                //         this.combox.addNewValue(v);
                //     }else{
                //         this.combox.addNewValue(this.getDataText(v), v);
                //     }
                // }.bind(this));
            }else{
                var node = this.node.getFirst();
                if (node){
                    node.empty();
                    comboxValues.each(function(v, i){
                        this.creteShowNode(v, (i===comboxValues.length-1)).inject(node);
                    }.bind(this));
                }
            }

        }else{
            if (this.node.getFirst()){
                var node = this.node.getFirst();
                node.empty();
                this.loadOrgWidget(values, node)
            }else{
                this.node.empty();
                this.loadOrgWidget(values, this.node);
            }
        }

        //if (this.readonly) this.loadOrgWidget(values, this.node)
        //this.node.set("text", value);
    },
    getValueMethod: function(value){
        if (value){
            var flag = value.substr(value.length-2, 2);
            switch (flag.toLowerCase()){
                case "@i":
                    return "getIdentity";
                case "@p":
                    return "getPerson";
                case "@u":
                    return "getUnit";
                case "@g":
                    return "getGroup";
                default:
                    return (this.json.selectType==="unit") ? "getUnit" : "getIdentity";
            }
        }
        return (this.json.selectType==="unit") ? "getUnit" : "getIdentity";
    },
    loadOrgWidget: function(value, node){
        var disableInfor = layout.mobile ? true : false;
        if( this.json.showCard === "no" )disableInfor = true;
        var height = node.getStyle("height").toInt();
        if (node.getStyle("overflow")==="visible" && !height) node.setStyle("overflow", "hidden");
        if (value && value.length){
            value.each(function(data){
                var flag = data.distinguishedName.substr(data.distinguishedName.length-2, 2);
                var copyData = Object.clone(data);
                if( this.json.displayTextScript && this.json.displayTextScript.code ){
                    this.currentData = copyData;
                    var displayName = this.form.Macro.exec(this.json.displayTextScript.code, this);
                    if( displayName ){
                        copyData.displayName = displayName;
                    }
                    this.currentData = null;
                }

                var widget;
                switch (flag.toLowerCase()){
                    case "@i":
                        widget = new MWF.widget.O2Identity(copyData, node, {"style": "xform","lazy":true,"disableInfor" : disableInfor});
                        break;
                    case "@p":
                        widget = new MWF.widget.O2Person(copyData, node, {"style": "xform","lazy":true,"disableInfor" : disableInfor});
                        break;
                    case "@u":
                        widget = new MWF.widget.O2Unit(copyData, node, {"style": "xform","lazy":true,"disableInfor" : disableInfor});
                        break;
                    case "@g":
                        widget = new MWF.widget.O2Group(copyData, node, {"style": "xform","lazy":true,"disableInfor" : disableInfor});
                        break;
                    default:
                        widget = new MWF.widget.O2Other(copyData, node, {"style": "xform","lazy":true,"disableInfor" : disableInfor});
                }
                widget.field = this;
                if( layout.mobile ){
                    widget.node.setStyles({
                        "float" : "none"
                    })
                }
            }.bind(this));
        }
    },
    _loadStyles: function(){
        if (this.readonly || this.json.isReadonly){
            if (this.json.styles) this.node.setStyles(this.json.styles);
        }else{
            if (this.json.styles) this.node.setStyles(this.json.styles);
            if (this.json.inputStyles) if (this.node.getFirst()) this.node.getFirst().setStyles(this.json.inputStyles);
            if (this.iconNode && this.iconNode.offsetParent !== null ){
                var size = this.node.getSize();
                this.iconNode.setStyle("height", ""+size.y+"px");
            }
        }
    },

    checkEmpower : function( data, callback ){
        if( typeOf(data)==="array" && this.identityOptions && this.json.isCheckEmpower && this.json.identityResultType === "identity" ) {
            var empowerChecker = new MWF.APPOrg.EmpowerChecker(this.form, this.json);
            empowerChecker.load(data, callback);
        }else{
            if( callback )callback( data );
        }
    }

});

MWF.APPOrg.EmpowerChecker = new Class({
    Implements: [Events],
    initialize: function (form, json) {
        this.form = form;
        this.json = json;
        this.css = this.form.css;
        this.checkedAllItems = true;
    },
    load : function( data, callback, container ){
        if( typeOf(data)==="array" && this.json.isCheckEmpower && this.json.identityResultType === "identity" ){
            var array = [];
            data.each( function( d ){
                if( d.distinguishedName ){
                    var flag = d.distinguishedName.substr(d.distinguishedName.length-1, 1).toLowerCase();
                    if( flag === "i" ){
                        array.push( d.distinguishedName )
                    }
                }
            }.bind(this));
            if( array.length > 0 ){
                o2.Actions.get("x_organization_assemble_express").listEmpowerWithIdentity({
                    "application" : (this.form.businessData.work || this.form.businessData.workCompleted).application,
                    "process" : (this.form.businessData.work || this.form.businessData.workCompleted).process,
                    "work" : (this.form.businessData.work || this.form.businessData.workCompleted).id,
                    "identityList" : array
                }, function( json ){
                    var arr = [];
                    json.data.each( function(d){
                        if(d.fromIdentity !== d.toIdentity )arr.push(d);
                    });
                    if( arr.length > 0 ){
                        this.openSelectEmpowerDlg( arr, data, callback, container );
                    }else{
                        if( callback )callback( data );
                    }
                }.bind(this), function(){
                    if( callback )callback( data );
                }.bind(this))
            }else{
                if( callback )callback( data );
            }
        }else{
            if( callback )callback( data );
        }
    },
    getIgnoreEmpowerArray : function( callback ){
        var array = [];
        if( this.empowerSelectNodes && this.empowerSelectNodes.length ){
            this.empowerSelectNodes.each(function(node){
                if( !node.retrieve("isSelected") ){
                    var d = node.retrieve("data");
                    array.push( d.fromIdentity );
                }
            }.bind(this));
        }
        if( callback )callback( array );
        return array;
    },
    setIgnoreEmpowerFlag : function(data, callback){

        var ignoreList = this.getIgnoreEmpowerArray();
        for( var i=0; i<data.length; i++ ){
            var d = data[i];
            if( ignoreList.indexOf( d.distinguishedName ) > -1 ){
                d.ignoreEmpower = true;
            }else if( d.ignoreEmpower ){
                delete  d.ignoreEmpower;
            }
        }
        if( callback )callback( data );
    },
    replaceEmpowerIdentity : function(data, callback){

        var empowerData = {};
        this.empowerSelectNodes.each(function(node){
            if( node.retrieve("isSelected") ){
                var d = node.retrieve("data");
                empowerData[ d.fromIdentity ] = d;
            }
        }.bind(this));

        if( Object.keys(empowerData).length === 0 ){
            callback( data );
        }else{
            var identityList = [];
            for( var key in empowerData ){
                identityList.push( empowerData[key].toIdentity );
            }
            o2.Actions.get("x_organization_assemble_express").listIdentity({ "identityList" : identityList }, function(json){
                var newData = data.clone();
                var d = {};
                json.data.each( function(j){
                    d[j.distinguishedName] = j;
                });
                for( var i=0; i<newData.length; i++ ){
                    var nd = newData[i];
                    if( nd.distinguishedName && empowerData[nd.distinguishedName]){
                        if( d[empowerData[nd.distinguishedName].toIdentity] ){
                            newData[i] = d[empowerData[nd.distinguishedName].toIdentity]
                        }
                    }
                }
                callback( newData );
            },function(){
                callback( data );
            });
        }
    },
    openSelectEmpowerDlg : function( data, orginData, callback, container ){
        if( layout.mobile ){
            this.openSelectEmpowerDlg_mobile(data, orginData, callback, container);
        }else{
            this.openSelectEmpowerDlg_pc(data, orginData, callback, container);
        }
    },
    openSelectEmpowerDlg_mobile : function( data, orginData, callback, container ){



        var that = this;

        //处理json
        var subItemList = [];
        // var empowerMap = {};

        // var selectableItems = [];
        var valueList = [];

        data.each( function( d ){
            subItemList.push({
                "name" : d.fromIdentity.split("@")[0] + " "+MWF.xApplication.process.Xform.LP.empowerTo+" " + d.toIdentity.split("@")[0],
                "id" : d.fromIdentity + "#" + d.toIdentity
            })
            valueList.push({
                "name" : d.fromIdentity.split("@")[0] + " "+MWF.xApplication.process.Xform.LP.empowerTo+" " + d.toIdentity.split("@")[0],
                "id" : d.fromIdentity + "#" + d.toIdentity
            })
            // empowerMap[ d.fromIdentity ] = d.toIdentity;
        }.bind(this));

        if( subItemList.length === 0 ){
            callback();
            return;
        }
        var empowerList = Array.clone(subItemList);

        // selectableItems.push({
        //     "empowerMap" : empowerMap,
        //     "subItemList" : subItemList
        // });

        // if( selectableItems.length === 0 ){
        //     callback();
        //     return;
        // }
        // var empowerList = Array.clone(selectableItems);
        o2.xDesktop.requireApp("Template", "Selector.Custom", function () {
            var options = {
                "count": 0,
                "title": MWF.xApplication.process.Xform.LP.selectEmpower,
                "selectAllEnable" : true,
                "selectableItems": subItemList,
                "expand": false,
                "category": false,
                "values": valueList,
                "zIndex" : 3001,
                "closeOnclickOk" : true,
                "onComplete": function (items) {

                    var arr = [];
                    items.each(function (item) {
                        arr.push( item.data.id )
                    }.bind(this));

                    var ignoreList = [];
                    empowerList.each( function (obj) {
                        if( arr.indexOf( obj.id ) === -1 )ignoreList.push( obj.id.split("#")[0] )
                    });

                    for( var i=0; i<orginData.length; i++ ){
                        var d = orginData[i];
                        if( ignoreList.indexOf( d.distinguishedName ) > -1 ){
                            d.ignoreEmpower = true;
                        }else if( d.ignoreEmpower ){
                            delete  d.ignoreEmpower;
                        }
                    }
                    if( callback )callback( orginData );

                    // empowerList.each( function(obj){
                    // var list = obj.subItemList.filter(function(item, index){
                    //     return !arr.contains( item.id );
                    // }.bind(this));
                    // ignoreList = ignoreList.map(function(item,index){
                    //     return item.id.split("#")[1];
                    // })
                    // });

                    // empowerList.each( function(obj){
                    //     var org = that.orgItemsObject[obj.orgId];
                    //     // if( obj.ignoreList.length > 0 ){
                    //     var data = org.getData();
                    //     for( var i=0; i<data.length; i++ ){
                    //         var d = data[i];
                    //         if( obj.ignoreList.indexOf( d.distinguishedName ) > -1 ){
                    //             d.ignoreEmpower = true;
                    //         }else if( d.ignoreEmpower ){
                    //             delete d.ignoreEmpower;
                    //         }
                    //     }
                    //     org.setData( data );
                        // }

                        // if( obj.empowerMap ){
                        //     var data = org.getValue();
                        //     for( var i=0; i<data.length; i++ ){
                        //         var d = data[i];
                        //         if( obj.empowerMap[ d.distinguishedName ] ){
                        //             d.empowerToIdentity = obj.empowerMap[ d.distinguishedName ];
                        //         }
                        //     }
                        //     org.empowerData = Array.clone(data);
                        // }

                        // if(callback)callback();
                    // })
                }.bind(this)
            };
            if( this.form.json.selectorStyle ){
                Object.merge(options, this.form.json.selectorStyle );
            }
            options.flatCategory = false;
            var selector = new o2.xApplication.Template.Selector.Custom($(document.body), options);
            selector.load();
        }.bind(this))
    },
    openSelectEmpowerDlg_pc : function( data, orginData, callback, container ){
        var node = new Element("div", {"styles": this.css.empowerAreaNode});
        var html = "<div style=\"line-height: 20px; color: #333333; overflow: hidden\">"+MWF.xApplication.process.Xform.LP.empowerDlgText+"</div>";
        html += "<div style=\"margin-bottom:10px; margin-top:10px; overflow-y:auto;\"></div>";
        node.set("html", html);
        var itemNode = node.getLast();
        this.getEmpowerItems(itemNode, data);
        node.inject( container || this.form.app.content);

        var dlg = o2.DL.open({
            "title": MWF.xApplication.process.Xform.LP.selectEmpower,
            "style": this.form.json.dialogStyle || "user",
            "isResize": true,
            "content": node,
            "width": 630,
            //"height" : 500,
            "buttonList": [
                {
                    "type" : "ok",
                    "text": MWF.LP.process.button.ok,
                    "action": function(d, e){
                        //this.replaceEmpowerIdentity( orginData, callback ); //直接替换已授权的人，已废弃
                        this.setIgnoreEmpowerFlag( orginData, callback ); //然后设置忽略的人的标志
                        dlg.close();
                    }.bind(this)
                },
                {
                    "type" : "cancel",
                    "text": MWF.LP.process.button.cancel,
                    "action": function(){dlg.close();}
                }
            ],
            "onPostShow": function(){
                var selectNode = this.createSelectAllEmpowerNode();
                selectNode.inject( dlg.button );
                if( layout.mobile ){
                    dlg.node.inject( $(document.body) );
                    dlg.node.setStyles({
                        "width":"100%",
                        "height": "100%",
                        "top" : "0px",
                        "left" : "0px"
                    });
                    var y = dlg.node.getSize().y - 190;
                    itemNode.setStyle("height",y+"px");

                    dlg.options.contentHeight = dlg.node.getSize().y - 100 ; //+100;
                    dlg.setContentSize();
                }else{
                    dlg.content.setStyle("overflow","hidden");

                    var y = Math.min(300, itemNode.getSize().y);
                    var marginTop = itemNode.getStyle("margin-top");
                    var marginBottom = itemNode.getStyle("margin-bottom");
                    itemNode.setStyle("height",y+"px");

                    dlg.options.contentHeight = y + 30 + 20 ; //+100;
                    dlg.setContentSize();
                    dlg.node.setStyles({
                        "height": ""+(dlg.options.height) +"px"
                    });
                    dlg.reCenter();
                }

            }.bind(this)
        });
    },
    getEmpowerItems: function(itemNode, data){
        var _self = this;
        this.empowerSelectNodes = [];
        var count = 1;
        var node;
        data.each(function( d ){
            if(d.fromIdentity == d.toIdentity )return;
            if( count % 2 === 1 ){
                node = new Element("div", {"styles": this.css.empowerItemOddNode}).inject(itemNode);
                node.store("nodeType","Odd");
                //node.setStyle("margin-right","10px");
            }else{
                node = new Element("div", {"styles": this.css.empowerItemEvenNode}).inject(itemNode);
                node.store("nodeType","Even");
            }
            count++;
            this.empowerSelectNodes.push( node );
            node.store("data", d);
            var iconNode = new Element("div.empowerItemIconNode", {"styles": this.css.empowerItemIconNode}).inject(node);
            node.store("iconNode",iconNode);
            var contentNode = new Element("div.empowerItemContentNode", {"styles": this.css.empowerItemContentNode}).inject(node);

            var formIdentityNode = new Element("div.empowerItemPersonNode", {"styles": this.css.empowerItemPersonNode, text : d.fromIdentity.split("@")[0] }).inject(contentNode);
            var titleNode = new Element("div.empowerItemTitleNode", {"styles": this.css.empowerItemTitleNode, text : MWF.xApplication.process.Xform.LP.empowerTo }).inject(contentNode);
            var toIdentityNode = new Element("div.empowerItemPersonNode", {"styles": this.css.empowerItemPersonNode, text : d.toIdentity.split("@")[0] }).inject(contentNode);

            node.addEvents({
                "mouseover": function(){
                    var isSelected = this.retrieve("isSelected");
                    if (!isSelected){
                        this.setStyles(_self.css[ "empowerItem"+this.retrieve("nodeType")+"Node_over" ]);
                        if( _self.css.empowerItemIconNode_over ){
                            var iconNode = this.retrieve("iconNode");
                            if(iconNode)iconNode.setStyles(_self.css.empowerItemIconNode_over);
                        }
                    }
                },
                "mouseout": function(){
                    var isSelected = this.retrieve("isSelected");
                    if (!isSelected){
                        this.setStyles(_self.css[ "empowerItem"+this.retrieve("nodeType")+"Node" ])
                        if( _self.css.empowerItemIconNode_over ){
                            var iconNode = this.retrieve("iconNode");
                            if(iconNode)iconNode.setStyles(_self.css.empowerItemIconNode);
                        }
                    }
                },
                "click": function(){
                    var isSelected = this.retrieve("isSelected");
                    if (isSelected){
                        _self.unselectEmpowerItem( this)
                    }else{
                        _self.selectEmpowerItem(this)
                    }
                }
            });
            if( this.checkedAllItems )node.click();
        }.bind(this));
    },
    unselectEmpowerItem : function( itemNode ){
        itemNode.store("isSelected", false);
        itemNode.setStyles( this.css[ "empowerItem"+itemNode.retrieve("nodeType")+"Node" ] );
        itemNode.getElements("div").each( function( div ){
            var className = div.get("class");
            if( className && this.css[className] )div.setStyles( this.css[className] )
        }.bind(this))
    },
    selectEmpowerItem : function( itemNode ){
        itemNode.store("isSelected", true);
        itemNode.setStyles( this.css[ "empowerItem"+itemNode.retrieve("nodeType")+"Node_selected" ] );
        itemNode.getElements("div").each( function( div ){
            var className = div.get("class");
            if( className && this.css[className+"_selected"] )div.setStyles( this.css[className+"_selected"] )
        }.bind(this))
    },
    createSelectAllEmpowerNode : function(){
        var _self = this;
        var node = new Element("div", {
            styles : this.css.empowerSelectAllItemNode,
            text : MWF.xApplication.process.Xform.LP.selectAll
        });
        node.addEvents({
            "mouseover": function(){
                var isSelected = this.retrieve("isSelected");
                if (!isSelected) this.setStyles(_self.css.empowerSelectAllItemNode_over);
            },
            "mouseout": function(){
                var isSelected = this.retrieve("isSelected");
                if (!isSelected) this.setStyles(_self.css.empowerSelectAllItemNode)
            },
            "click": function(){
                var isSelected = this.retrieve("isSelected");
                if (isSelected){
                    this.store("isSelected", false);
                    this.setStyles( _self.css.empowerSelectAllItemNode );
                    _self.empowerSelectNodes.each( function(itemNode){
                        _self.unselectEmpowerItem(itemNode)
                    }.bind(this))
                }else{
                    this.store("isSelected", true);
                    this.setStyles( _self.css.empowerSelectAllItemNode_selected );
                    _self.empowerSelectNodes.each( function(itemNode){
                        _self.selectEmpowerItem(itemNode)
                    }.bind(this))
                }
            }
        });
        if( this.checkedAllItems ){
            node.store("isSelected", true);
            node.setStyles( _self.css.empowerSelectAllItemNode_selected );
        }
        return node;
    }
});

MWF.APPOrg.GroupOptions = new Class({
    Implements: [Events],
    initialize: function (form, json) {
        this.form = form;
        this.json = json;
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
    },
    getOptions: function(){
        var count = (this.json.groupCount) ? this.json.groupCount : 0;
        if( this.json.groupRange==="group" ){
            return {
                "count": count,
                "storeRange" : this.json.storeRange,
                "include": this.getSelectRange( true )
            }
        }else{
            return {
                "count": count,
                "storeRange" : this.json.storeRange
            }
        }
    },
    getSearchOptions : function(){
        return {};
    },
    getSelectRange : function( refresh ){
        if( !this.selectRange || refresh){
            this.selectRange = this._getSelectRange();
        }
        return this.selectRange;
    },
    _getSelectRange : function(){
        var rangeValues = [];
        if (this.json.groupRangeKey && this.json.groupRangeKey.code){
            var v = this.form.Macro.exec(this.json.groupRangeKey.code, this);
            if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
            rangeValues = v;
            //v.each(function(d){
            //    if (d){
            //        if (typeOf(d)==="string"){
            //            var data;
            //            this.orgAction.getGroup(function(json){ data = json.data }.bind(this), null, d, false);
            //            rangeValues.push(data);
            //        }else{
            //            rangeValues.push(d);
            //        }
            //    }
            //}.bind(this));
        }
        return rangeValues;
    }
});

MWF.APPOrg.UnitOptions = new Class({
    Implements: [Events],
    initialize : function( form, json  ){
        this.form = form;
        this.json = json;
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
    },
    getOptions: function(){

        var count = (this.json.unitCount) ? this.json.unitCount : 0;
        var selectUnits = this.getSelectRange( true );

        return {
            "count": count,
            "units": selectUnits,
            "unitType": (this.json.selectUnitType==="all") ? "" : this.json.selectUnitType,
            "storeRange" : this.json.storeRange,
            "expandSubEnable" : (this.json.unitExpandSubEnable=="no") ? false : true
        };
    },
    getSearchOptions : function(){
        var selectUnits = this.getSelectRange( true );
        return {
            "units": selectUnits,
            "unitType": (this.json.selectUnitType==="all") ? "" : this.json.selectUnitType
        };
    },
    getSelectRange : function( refresh ){
        if( !this.selectRange || refresh){
            this.selectRange = this._getSelectRange();
        }
        return this.selectRange;
    },
    _getSelectRange : function(){
        if (this.json.unitRange==="unit"){
            return this.getScriptSelectUnit();
        }
        if (this.json.unitRange==="draftUnit"){
            var dn = (this.form.businessData.work || this.form.businessData.workCompleted).creatorIdentityDn;
            if (!dn){
                if ( layout.session.user.identityList && layout.session.user.identityList.length){
                    var ids = [];
                    layout.session.user.identityList.each(function(id){ ids.push(id.id); });
                    return this.getNextSelectUnit(ids);
                }else{
                    return [];
                }
            }else{
                return this.getNextSelectUnit((this.form.businessData.work || this.form.businessData.workCompleted).creatorIdentityDn);
            }
        }
        if (this.json.unitRange==="currentUnit"){
            if (this.form.app.currentTask){
                return this.getNextSelectUnit(this.form.app.currentTask.identityDn);
            }else{
                if (this.form.app.taskList && this.form.app.taskList.length){
                    var ids = [];
                    this.form.app.taskList.each(function(task){ ids.push(task.identity); });
                    return this.getNextSelectUnit(ids);
                }else{
                    if ( layout.session.user.identityList && layout.session.user.identityList.length){
                        var ids = [];
                        layout.session.user.identityList.each(function(id){ ids.push(id.id); });
                        return this.getNextSelectUnit(ids);
                    }else{
                        return [];
                    }
                }
            }
        }
        return [];
    },
    getScriptSelectUnit: function(){
        var rangeValues = [];
        if (this.json.unitRangeUnit && this.json.unitRangeUnit.length){
            this.json.unitRangeUnit.each(function(unit){
                //var unitFlag = unit.distinguishedName || unit.id || unit.unique || unit.levelName;
                //if (unitFlag) rangeValues.push(unitFlag);
                rangeValues.push(unit);
            }.bind(this));
        }
        if (this.json.unitRangeField && this.json.unitRangeField.length){
            this.json.unitRangeField.each(function(field){
                var n = (typeOf(field)=="object") ? field.name : field;
                var v = this.form.businessData.data[n];
                if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
                v.each(function(d){
                    if (d){
                        // if (typeOf(d)==="string"){
                        //     var data;
                        //     this.orgAction.getUnit(function(json){ data = json.data }.bind(this), null, d, false);
                        //     rangeValues.push(data);
                        // }else{
                            rangeValues.push(d);
                        // }
                    }
                }.bind(this));
            }.bind(this));
        }
        if (this.json.unitRangeKey && this.json.unitRangeKey.code){
            var v = this.form.Macro.exec(this.json.unitRangeKey.code, this);
            if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
            v.each(function(d){
                if (d){
                    // if (typeOf(d)==="string"){
                    //     var data;
                    //     this.orgAction.getUnit(function(json){ data = json.data }.bind(this), null, d, false);
                    //     rangeValues.push(data);
                    // }else{
                        rangeValues.push(d);
                    // }
                }
            }.bind(this));
        }
        return rangeValues;
    },
    getNextSelectUnit: function(id){
        var ids = typeOf(id)==="array" ? id : [id];
        var data;
        var units = [];
        ids.each( function(i){
            if (this.json.unitRangeNext === "direct"){
                this.orgAction.getIdentity(function(json){ data = json.data }.bind(this), function(){data={"woUnit": null}}, i, false);
                if (data && data.woUnit) units.push(data.woUnit);
            }else if(this.json.unitRangeNext==="level"){
                this.orgAction.getUnitWithIdentityWithLevel(i, this.json.unitRangeNextLevel, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
                if (data) units.push(data);
            }else if (this.json.unitRangeNext==="type"){
                if (this.json.unitRangeNextUnitType==="all"){
                    this.orgAction.getUnitWithIdentityWithLevel(i, 1, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
                }else{
                    this.orgAction.getUnitWithIdentityWithType(i, this.json.unitRangeNextUnitType, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
                }
                if (data) units.push(data);
            }
            data = null;
        }.bind(this));
        return units;
        //if (this.json.unitRangeNext === "direct"){
        //    if (typeOf(id)==="array"){
        //        var units = [];
        //        id.each(function(i){
        //            this.orgAction.getIdentity(function(json){ data = json.data }.bind(this), function(){data={"woUnit": null}}, i, false);
        //            if (data && data.woUnit) units.push(data.woUnit);
        //            data = null;
        //        }.bind(this));
        //        return units;
        //    }else{
        //        this.orgAction.getIdentity(function(json){ data = json.data }.bind(this), function(){data={"woUnit": null}}, id, false);
        //        return (data.woUnit) ? [data.woUnit] : [];
        //    }
        //}
        //if (this.json.unitRangeNext==="level"){
        //    this.orgAction.getUnitWithIdentityWithLevel(id, this.json.unitRangeNextLevel, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
        //    //this.json.rangeNextLevel
        //    return (data) ? [data] : [];
        //}
        //if (this.json.unitRangeNext==="type"){
        //    if (this.json.unitRangeNextUnitType==="all"){
        //        this.orgAction.getUnitWithIdentityWithLevel(id, 1, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
        //    }else{
        //        this.orgAction.getUnitWithIdentityWithType(id, this.json.unitRangeNextUnitType, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
        //    }
        //
        //    return (data) ? [data] : [];
        //}

    }
});

MWF.APPOrg.IdentityOptions = new Class({
    Implements: [Events],
    initialize : function( form, json  ){
        this.form = form;
        this.json = json;
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
    },
    getOptions: function(){
        var count = (this.json.identityCount) ? this.json.identityCount : 0;
        if( this.json.identityOnlyUseInclude ){
            return {
                "noUnit" : true,
                "count": count,
                "resultType" : this.json.identityResultType,
                "storeRange" : this.json.storeRange,
                "include" : this._getInclude()
            };
        }else{
            var selectUnits = this.getSelectRange( true );
            var selectDutys = this.getSelectRangeDuty( true );
            return {
                "count": count,
                "units": selectUnits,
                "dutys": selectDutys,
                "expandSubEnable" : (this.json.identityExpandSubEnable=="no") ? false : true,
                "resultType" : this.json.identityResultType,
                "categoryType": this.json.categoryType || "unit",
                "dutyUnitLevelBy" : this.json.dutyUnitLevelBy || "duty",
                "storeRange" : this.json.storeRange,
                "include" : this._getInclude()
            };
        }
    },
    getSearchOptions : function(){
        var selectUnits = this.getSelectRange( true );
        var selectDutys = this.getSelectRangeDuty( true );
        return {
            "units": selectUnits,
            "dutys": selectDutys,
            "resultType" : this.json.identityResultType
        };
    },
    getSelectRange : function( refresh ){
        if( !this.selectRange || refresh ){
            this.selectRange = this._getSelectRange();
        }
        return this.selectRange;
    },
    _getInclude : function(){
        var include = [];
        if( this.json.identityIncludeKey ){
            var v = this.form.Macro.exec(this.json.identityIncludeKey.code, this);
            if( v )include = typeOf(v)==="array" ? v : [v];
        }
        return include;
    },
    _getSelectRange : function(){
        if (this.json.identityRange==="unit"){
            return this.getScriptSelectUnit();
        }
        if (this.json.identityRange==="draftUnit"){
            var dn = (this.form.businessData.work || this.form.businessData.workCompleted).creatorIdentityDn;
            if (!dn){
                if ( layout.session.user.identityList && layout.session.user.identityList.length){
                    var ids = [];
                    layout.session.user.identityList.each(function(id){ ids.push(id.id); });
                    return this.getNextSelectUnit(ids);
                }else{
                    return [];
                }
            }else{
                return this.getNextSelectUnit((this.form.businessData.work || this.form.businessData.workCompleted).creatorIdentityDn);
            }
        }
        if (this.json.identityRange==="currentUnit"){
            if (this.form.app.currentTask){
                return this.getNextSelectUnit(this.form.app.currentTask.identityDn);
            }else{
                if (this.form.app.taskList && this.form.app.taskList.length){
                    var ids = [];
                    this.form.app.taskList.each(function(task){ ids.push(task.identity); });
                    return this.getNextSelectUnit(ids);
                }else{
                    if ( layout.session.user.identityList && layout.session.user.identityList.length){
                        var ids = [];
                        layout.session.user.identityList.each(function(id){ ids.push(id.id); });
                        return this.getNextSelectUnit(ids);
                    }else{
                        return [];
                    }
                }
            }
        }
        return [];
    },
    getScriptSelectUnit: function(){
        var rangeValues = [];
        if (this.json.identityRangeUnit && this.json.identityRangeUnit.length){
            this.json.identityRangeUnit.each(function(unit){
                //var unitFlag = unit.distinguishedName || unit.id || unit.unique || unit.levelName ;
                //if (unitFlag) rangeValues.push(unitFlag);
                rangeValues.push(unit);
            }.bind(this));
        }
        if (this.json.identityRangeField && this.json.identityRangeField.length){
            this.json.identityRangeField.each(function(field){
                var n = (typeOf(field)=="object") ? field.name : field;
                var v = this.form.businessData.data[n];
                if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
                v.each(function(d){
                    if (d){
                        // if (typeOf(d)==="string"){
                        //     var data;
                        //     this.orgAction.getUnit(function(json){ data = json.data }.bind(this), null, d, false);
                        //     rangeValues.push(data);
                        // }else{
                            rangeValues.push(d);
                        // }
                    }
                }.bind(this));
            }.bind(this));
        }
        if (this.json.identityRangeKey && this.json.identityRangeKey.code){
            var v = this.form.Macro.exec(this.json.identityRangeKey.code, this);
            if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
            v.each(function(d){
                if (d){
                    // if (typeOf(d)==="string"){
                    //     var data;
                    //     this.orgAction.getUnit(function(json){ data = json.data }.bind(this), null, d, false);
                    //     rangeValues.push(data);
                    // }else{
                        rangeValues.push(d);
                    // }
                }
            }.bind(this));
        }
        return rangeValues;
    },
    getNextSelectUnit: function(id){
        var ids = typeOf(id)==="array" ? id : [id];
        var data;
        var units = [];
        ids.each(function(i){
            if (this.json.identityRangeNext === "direct"){
                this.orgAction.getIdentity(function(json){ data = json.data }.bind(this), function(){data={"woUnit": null}}, i, false);
                if (data && data.woUnit) units.push(data.woUnit);
            }else if (this.json.identityRangeNext==="level"){
                this.orgAction.getUnitWithIdentityWithLevel(i, this.json.identityRangeNextLevel, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
                if (data) units.push(data);
            }else if (this.json.identityRangeNext==="type"){
                if (this.json.identityRangeNextUnitType==="all"){
                    this.orgAction.getUnitWithIdentityWithLevel(i, 1, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
                }else{
                    this.orgAction.getUnitWithIdentityWithType(i, this.json.identityRangeNextUnitType, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
                }
                if (data) units.push(data);
            }
            data = null;
        }.bind(this));
        return units;

        //if (this.json.identityRangeNext === "direct"){
        //if (typeOf(id)==="array"){
        //    var units = [];
        //    id.each(function(i){
        //        this.orgAction.getIdentity(function(json){ data = json.data }.bind(this), function(){data={"woUnit": null}}, i, false);
        //        if (data && data.woUnit) units.push(data.woUnit);
        //        data = null;
        //    }.bind(this));
        //    return units;
        //}else{
        //    this.orgAction.getIdentity(function(json){ data = json.data }.bind(this), function(){data={"woUnit": null}}, id, false);
        //    return (data.woUnit) ? [data.woUnit] : [];
        //}
        //}
        //if (this.json.identityRangeNext==="level"){
        //    this.orgAction.getUnitWithIdentityWithLevel(id, this.json.identityRangeNextLevel, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
        //    return (data) ? [data] : [];
        //    //this.json.rangeNextLevel
        //}
        //if (this.json.identityRangeNext==="type"){
        //    if (this.json.identityRangeNextUnitType==="all"){
        //        this.orgAction.getUnitWithIdentityWithLevel(id, 1, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
        //    }else{
        //        this.orgAction.getUnitWithIdentityWithType(id, this.json.identityRangeNextUnitType, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
        //    }
        //    return (data) ? [data] : [];
        //}

    },

    getSelectRangeDuty: function( refresh ){
        if( !this.selectRangeDuty || refresh ){
            this.selectRangeDuty = this._getSelectRangeDuty();
        }
        return this.selectRangeDuty;
    },
    _getSelectRangeDuty: function(){
        if (this.json.dutyRange==="duty"){
            return this.getScriptSelectDuty();
        }
        return [];
    },
    getScriptSelectDuty: function(){
        var rangeValues = [];
        if( this.json.rangeDuty && this.json.rangeDuty.length ){
            var rangeDuty = this.json.rangeDuty;
            if( typeOf(rangeDuty) === "string" ){
                rangeDuty = JSON.parse( rangeDuty );
            }
            if( typeOf(rangeDuty) === "array" ){
                rangeDuty.each(function(unit){
                    var unitFlag = typeOf(unit) === "string" ? unit : (unit.id || unit.name);
                    if (unitFlag) rangeValues.push(unitFlag);
                }.bind(this));
            }
        }
        //if (this.json.rangeDuty && this.json.rangeDuty.length){
        //    this.json.rangeDuty.each(function(unit){
        //        var unitFlag = unit.id || unit.name;
        //        if (unitFlag) rangeValues.push(unitFlag);
        //    }.bind(this));
        //}
        if (this.json.rangeDutyField && this.json.rangeDutyField.length){
            this.json.rangeDutyField.each(function(field){
                var n = (typeOf(field)=="object") ? field.name : field;
                var v = this.form.businessData.data[n];
                if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
                v.each(function(d){
                    if (d) rangeValues.push(d);
                }.bind(this));
            }.bind(this));
        }
        if (this.json.rangeDutyKey && this.json.rangeDutyKey.code){
            var v = this.form.Macro.exec(this.json.rangeDutyKey.code, this);
            if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
            v.each(function(d){
                if (d) rangeValues.push(d);
            }.bind(this));
        }
        return rangeValues;
    }
});
