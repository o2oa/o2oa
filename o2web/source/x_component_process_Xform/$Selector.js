o2.xDesktop.requireApp("process.Xform", "$Input", null, false);
/** @classdesc $Selector 组件类，此类为所有可选组件的父类
 * @class
 * @hideconstructor
 * @o2category FormComponents
 * @extends MWF.xApplication.process.Xform.$Input
 * @abstract
 */
MWF.xApplication.process.Xform.$Selector = MWF.APP$Selector = new Class(
    /** @lends MWF.xApplication.process.Xform.$Selector# */
{
    Extends: MWF.APP$Input,

    /**
     * 组件加载后触发。如果选项加载为异步，则异步处理完成后触发此事件
     * @event MWF.xApplication.process.Xform.$Selector#load
     * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
     */

    _showValue: function (node, value) {
        var optionItems = this.getOptions();
        if (optionItems && typeOf(optionItems.then) === "function") {
            optionItems.then(function (opt) {
                this.__showValue(node, value, opt)
            }.bind(this));
        } else {
            this.__showValue(node, value, optionItems)
        }
    },
    /**
     * @summary 刷新选择项，如果选择项是脚本，重新计算。
     * @example
     * this.form.get('fieldId').resetOption();
     */
    resetOption: function () {
        this.node.empty();
        this.setOptions();
        this.fireEvent("resetOption");
    },
    /**
     * @summary 获取选择项。
     * @return {Array | Promise} 返回选择项数组或Promise，如：<pre><code class='language-js'>[
     *  "女|female",
     *  "男|male"
     * ]</code></pre>
     * @example
     * this.form.get('fieldId').getOptions();
     * @example
     * //异步
     * var opt = this.form.get('fieldId').getOptions();
     * Promise.resolve(opt).then(function(options){
     *     //options为选择项数组
     * })
     */
    getOptions: function (async, refresh) {
        this.optionsCache = null;
        var opt = this._getOptions(async, refresh);
        if ((opt && typeOf(opt.then) === "function")) {
            var p = Promise.resolve(opt).then(function (option) {
                this.moduleSelectAG = null;
                this.optionsCache = (option || []);
                return this.optionsCache;
            }.bind(this));
            this.moduleSelectAG = p;
            return p;
        } else {
            this.optionsCache = (opt || []);
            return this.optionsCache;
        }
    },
    _getOptions: function (async, refresh) {
        debugger;
        switch (this.json.itemType) {
            case "values":
                return this.json.itemValues;
            case "script":
                return this.form.Macro.exec(((this.json.itemScript) ? this.json.itemScript.code : ""), this);
            default:
                break;
        }

        var opts, firstOpts = this.getFirstOption();
        switch (this.json.itemType) {
            case "dict":
                opts = this.getOptionsWithDict(async, refresh);
                break;
            case "view":
                opts = this.getOptionsWithView(async, refresh);
                break;
            case "statement":
                opts = this.getOptionsWithStatement(async, refresh);
                break;
        }
        if (opts && typeOf(opts.then) === "function") {
            return Promise.resolve(opts).then(function (opts) {
                return this._contactOption(firstOpts, opts);
            }.bind(this));
        } else {
            return this._contactOption(firstOpts, opts);
        }
        // if( (defaultOpts && typeOf(defaultOpts.then) === "function" ) || (opts && typeOf(opts.then) === "function" ) ){
        //     return Promise.all( [defaultOpts, opts] ).then(function (arr) {
        //         return this._contactOption( arr[0], arr[1] );
        //     }.bind(this));
        // }else{
        //     return this._contactOption( defaultOpts, opts );
        // }
    },
    _contactOption: function (opt1, opt2) {
        var optA, optB;
        if (!opt1) opt1 = [];
        if (!opt2) opt2 = [];
        optA = typeOf(opt1) !== "array" ? [opt1] : opt1;
        optB = typeOf(opt2) !== "array" ? [opt2] : opt2;
        optA.each(function (o) {
            if (o) optB.unshift(o);
        });
        return optB;
    },
    getFirstOption: function () {
        //return this.form.Macro.exec(((this.json.defaultOptionsScript) ? this.json.defaultOptionsScript.code : ""), this);
        if (!this.json.firstOptionEnable) return [];
        return [this.json.firstOption || "|"];
    },

    /**
     * @summary 获取整理后的选择项。
     * @param {Boolean} [refresh] 是否忽略缓存重新计算可选项。
     * @return {Object} 返回整理后的选择项数组或Promise，如：
     * <pre><code class='language-js'>{"valueList": ["","female","male"], "textList": ["","女","男"]}
     * </code></pre>
     * @example
     * var optionData = this.form.get('fieldId').getOptionsObj();
     * @example
     * //异步
     * var opt = this.form.get('fieldId').getOptionsObj(true);
     * Promise.resolve(opt).then(function(optionData){
     *     //optionData为选择项
     * })
     */
    getOptionsObj: function (refresh) {
        var optionItems = (refresh !== true && this.optionsCache) ? this.optionsCache : this.getOptions();
        if (optionItems && typeOf(optionItems.then) === "function") {
            return Promise.resolve(optionItems).then(function (optItems) {
                return this._getOptionsObj(optItems);
            }.bind(this));
        } else {
            return this._getOptionsObj(optionItems);
        }
    },
    _getOptionsObj: function (optItems) {
        var textList = [];
        var valueList = [];
        optItems.each(function (item) {
            var tmps = item.split("|");
            textList.push(tmps[0]);
            valueList.push(tmps[1] || tmps[0]);
        });
        return {textList: textList, valueList: valueList};
    },

    setOptions: function () {
        var optionItems = this.getOptions();
        this._setOptions(optionItems);
    },

    /**
     * @summary 获取选中项的value和text。
     * @return {Object} 返回选中项的value和text，如：
     * <pre><code class='language-js'>{"value": ["male"], "text": ["男"]}
     * {"value": [""], "text": [""]}
     * </code></pre>
     * @example
     * var data = this.form.get('fieldId').getTextData();
     * var text = data.text[0] //获取选中项的文本
     */
    getTextData: function () {
        var ops;
        if (this.isReadonly()) {
            ops = this.getOptionsObj();
            var data = this._getBusinessData();
            var d = typeOf(data) === "array" ? data : [data];
            if (ops && typeOf(ops.then) === "function") {
                return Promise.resolve(ops).then(function (opts) {
                    return this._getTextData(d, opts)
                }.bind(this));
            } else {
                return this._getTextData(d, ops)
            }
        } else {
            return this._getInputTextData();
        }
    },
    _getTextData: function (d, opts) {
        var value = [], text = [];
        d.each(function (v) {
            var idx = opts.valueList.indexOf(v);
            value.push(v || "");
            text.push(idx > -1 ? opts.textList[idx] : (v || ""));
        });
        if (!value.length) value = [""];
        if (!text.length) text = [""];
        return {"value": value, "text": text};
    },


    getOptionsWithDict: function (async, refresh) {
        if (!this.json.itemDict || !this.json.itemDict.length) return [];
        var obj = this.json.itemDict[0];
        var dict = new this.form.Macro.environment.Dict({
            "type": obj.appType,
            "application": obj.appId,
            "name": obj.id
        });

        var paths = (this.json.itemDictPath || "").split("/");
        paths.splice(0, 1); //第一个是root，删掉
        var path = paths.length ? paths.join(".") : null;

        var asy = o2.typeOf(async) === "boolean" ? async : (this.json.itemDictAsync !== false);
        var data = dict.get(path, null, null, asy, refresh === true);
        if (data && typeOf(data.then) === "function") {
            return data.then(function (data) {
                return this.parseDictOptions(data);
            }.bind(this));
        } else {
            return this.parseDictOptions(data);
        }
    },
    getString: function (d) {
        switch (o2.typeOf(d)) {
            case "null":
                return "";
            case "string":
                return d;
            case "boolean":
            case "number":
            case "date":
                return d.toString();
            default:
                return "";
        }
    },
    parseDictOptions: function (d) {
        var arr = [], value, text, valuekey = this.json.dictValueKey, textkey = this.json.dictTextKey;
        switch (o2.typeOf(d)) {
            case "array":
                d.each(function (i) {
                    switch (o2.typeOf(i)) {
                        case "object":
                            if (valuekey && textkey) {
                                value = this.getString(i[valuekey]);
                                text = this.getString(i[textkey]);
                                arr.push(text + "|" + value);
                            } else if (valuekey) {
                                arr.push(this.getString(i[valuekey]));
                            } else if (textkey) {
                                arr.push(this.getString(i[textkey]));
                            }
                            break;
                        case "null":
                            break;
                        default:
                            arr.push(i.toString());
                            break;
                    }
                }.bind(this));
                return arr;
            case "object":
                Object.each(d, function (i, key) {
                    switch (o2.typeOf(i)) {
                        case "object":
                            if (valuekey && textkey) {
                                value = this.getString(i[valuekey]);
                                text = this.getString(i[textkey]);
                                arr.push(value + "|" + text);
                            } else if (valuekey) {
                                arr.push(this.getString(i[valuekey]));
                            } else if (textkey) {
                                arr.push(this.getString(i[textkey]));
                            }
                            break;
                        case "null":
                            break;
                        default:
                            arr.push(i.toString() + "|" + key.toString());
                            break;
                    }
                }.bind(this))
                return arr;
            case "null":
                return [];
            default:
                return [d.toString()];
        }
    },
    getOptionsWithView: function (async, refresh) {
        if (!this.json.itemView) return [];
        var obj = this.json.itemView;

        var asy = o2.typeOf(async) === "boolean" ? async : (this.json.itemViewAsync !== false);

        var filter = [];
        if (this.json.viewFilterList && this.json.viewFilterList.length) {
            this.json.viewFilterList.each(function (entry) {
                entry.value = this.form.Macro.exec(entry.code.code, this);
                filter.push(entry);
            }.bind(this));
        }

        var data = this.form.Macro.environment.view.lookup({
            "view": obj.id,
            "application": obj.application,
            "filter": filter
        }, null, asy);
        if (data && typeOf(data.then) === "function") {
            return data.then(function (data) {
                return this.parseViewOptions(data);
            }.bind(this));
        } else {
            return this.parseViewOptions(data);
        }
    },
    parseViewOptions: function (json) {
        var arr = [], value, text, valuekey = this.json.viewValueColumn, textkey = this.json.viewTextColumn;
        json.grid.each(function (d) {
            var i = d.data || {};
            if (valuekey && textkey) {
                value = valuekey === "bundle" ? d.bundle : (this.getString(i[valuekey]));
                text = textkey === "bundle" ? d.bundle : (this.getString(i[textkey]));
                arr.push(text + "|" + value);
            } else if (valuekey) {
                arr.push(valuekey === "bundle" ? d.bundle : (this.getString(i[valuekey])));
            } else if (textkey) {
                arr.push(textkey === "bundle" ? d.bundle : (this.getString(i[textkey])));
            }
        }.bind(this))
        return arr.unique();
    },

    getOptionsWithStatement: function (async, refresh) {
        if (!this.json.itemStatement) return [];
        var obj = this.json.itemStatement;

        var asy = o2.typeOf(async) === "boolean" ? async : (this.json.itemViewAsync !== false);

        var filter = [];
        if (this.json.statementFilterList && this.json.statementFilterList.length) {
            this.json.statementFilterList.each(function (entry) {
                entry.value = this.form.Macro.exec(entry.code.code, this);
                filter.push(entry);
            }.bind(this));
        }

        var parameter = {};
        if(this.json.statementParameterList && this.json.statementParameterList.length) {
            this.json.statementParameterList.each(function (entry) {
                parameter[entry.parameter] = this.parseParameter(entry);
            }.bind(this));
        }

        var data = this.form.Macro.environment.statement.execute({
            "name": obj.name,
            "mode": "data",
            "page": 1, //（number）可选，当前页码，默认为1
            "pageSize": 1000, //（number）可选，每页的数据条数，默认为20
            "filter": filter,
            "parameter": parameter,
            "parameterList": this.json.parameterList
        }, null, asy);
        if (data && typeOf(data.then) === "function") {
            return data.then(function (data) {
                return this.parseStatementOptions(data);
            }.bind(this));
        } else {
            return this.parseStatementOptions(data);
        }
    },
    parseStatementOptions: function (json) {
        var arr = [], value, text, valuekey = this.json.statementValueColumn,
            textkey = this.json.statementTextColumn;
        json.data.each(function (d) {
            if (valuekey && textkey) {
                value = this.getDataByPath(d, valuekey);
                text = this.getDataByPath(d, textkey);
                arr.push(text + "|" + value);
            } else if (valuekey) {
                value = this.getDataByPath(d, valuekey);
                arr.push(value);
            } else if (textkey) {
                text = this.getDataByPath(d, textkey);
                arr.push(text);
            }
        }.bind(this));
        return arr.unique();
    },
    parseParameter: function (f) {
        var value = f.value;
        if (f.valueType === "script") {
            value = this.form.Macro.exec(f.valueScript ? f.valueScript.code : "", this);
        }
        if (typeOf(value) === "date") {
            value = value.format("db");
        }
        var user = layout.user;
        switch (value) {
            case "@year":
                value = (new Date().getFullYear()).toString();
                break;
            case "@season":
                var m = new Date().format("%m");
                if (["01", "02", "03"].contains(m)) {
                    value = "1"
                } else if (["04", "05", "06"].contains(m)) {
                    value = "2"
                } else if (["07", "08", "09"].contains(m)) {
                    value = "3"
                } else {
                    value = "4"
                }
                break;
            case "@month":
                value = new Date().format("%Y-%m");
                break;
            case "@time":
                value = new Date().format("db");
                break;
            case "@date":
                value = new Date().format("%Y-%m-%d");
                break;
            default:
        }

        if (f.formatType === "dateTimeValue" || f.formatType === "datetimeValue") {
            value = "{ts '" + value + "'}"
        } else if (f.formatType === "dateValue") {
            value = "{d '" + value + "'}"
        } else if (f.formatType === "timeValue") {
            value = "{t '" + value + "'}"
        }
        return value;
    },
    getDataByPath: function (obj, path, isUppcase) {
        var pathList = isUppcase ? path.toUpperCase().split(".") : path.split(".");
        for (var i = 0; i < pathList.length; i++) {
            var p = pathList[i];
            if ((/(^[1-9]\d*$)/.test(p))) p = p.toInt();
            if (obj[p]) {
                obj = obj[p];
            } else if (obj[p] === undefined || obj[p] === null) {
                if (!isUppcase && i === 0) {
                    return this.getDataByPath(obj, path, true);
                } else {
                    obj = "";
                }
                break;
            } else {
                obj = obj[p];
                break;
            }
        }
        return this.getString(obj);
    }

});
