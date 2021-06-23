var _worker = self;
var _action = {
    _checkRequest: function(s,f){
        if (this.request.readyState === XMLHttpRequest.DONE) {
            if (this.request.status === 200) {
                this._doneRequest(s);
            } else {
                this._errorRequest(f);
            }
        }
    },
    _createRequest: function(s, f){
        var request = new XMLHttpRequest();
        request.addEventListener("readystatechange",function(){
            if (this.readyState === XMLHttpRequest.DONE) {
                if (this.status === 200) {
                    var json = JSON.parse(this.responseText);
                    var xToken = this.getResponseHeader(_worker.findData.tokenName);
                    if (xToken){
                        json.xToken = xToken;
                    }
                    if (s) s(json);
                } else {
                    if (f) f(this, this.responseText);
                }
            }
        });
        return request
    },
    sendRequest: function(data){
        return new Promise(function(s, f){
            var request = this._createRequest(s, f);
            var method = data.method;
            var noCache = !!data.noCache;
            var async = !!data.loadAsync;
            var withCredentials = !!data.credentials;
            var url = data.address;
            var body = data.body;
            var debug = data.debug;
            var token = data.token;

            if (noCache) url = url+(((url.indexOf("?")!==-1) ? "&" : "?")+(new Date()).getTime());

            request.open(method, url, async);

            request.withCredentials = withCredentials;
            request.setRequestHeader("Content-Type", "application/json; charset=utf-8");
            request.setRequestHeader("Accept", "text/html,application/json,*/*");
            if (debug) request.setRequestHeader("x-debugger", "true");
            if (token){
                request.setRequestHeader(_worker.findData.tokenName, token);
                request.setRequestHeader("authorization", token);
            }

            request.send(body);
        }.bind(this));
    },

    _doneRequest: function(s){
        var json = JSON.parse(this.request.responseText);
        var xToken = this.request.getResponseHeader(_worker.findData.tokenName);
        if (xToken){
            json.xToken = xToken;
        }
        if (s) s(json);
        // _worker.postMessage({"type": "done", "data": json});
        // _worker.close();
    },
    _errorRequest: function(f){
        if (s) s(this.request, this.request.responseText);
        // _worker.postMessage({"type":"error", "data": {"status":  this.request.status, "statusText":  this.request.statusText, "responseText":this.request.responseText}});
        // _worker.close();
    }
};

_worker.action = _action;

_worker._receiveMessageReply = function(){
    _worker.postMessage({
        "type": "receive"
    });
};
_worker._readyMessageReply = function(){
    _worker.postMessage({
        "type": "ready",
        "count": this.filterOptionList.length
    });
};

_worker._getRequestOption = function(data, par){
    if (par){
        Object.keys(par).forEach(function(k){
            data.url = data.url.replace("{"+k+"}", par[k]);
        });
    }
    return {
        "method": data.method||"get",
        "noCache": false,
        "loadAsync": true,
        "credentials": true,
        "address": data.url,
        "body": data.body || "",
        "debug": data.debug || _worker.findData.debug,
        "token": data.token || _worker.findData.token
    };
};

_worker._createFilterOption = function(moduleType, appId, designerType, desingerId){
    var filterOption = JSON.parse(_worker.filterOptionTemplete);
    filterOption.moduleList.push({
        "moduleType": moduleType,
        "moduleAppList": [{"appId": appId, "designerList": [{"designerType": designerType, "designerIdList": [desingerId]}]}]
    });
    this.filterOptionList.push(filterOption);
    return filterOption;
};
_worker._getDesingerModule = function(id, restful, par, moduleType, designerType){
    var p = _worker.action.sendRequest(_worker._getRequestOption({"url": restful, "debug": _worker.findData.debug, "token": _worker.findData.token }, par));
    return p.then(function(json){
        list = json.data;
        if (list) list.forEach(function(designer){
            _worker._createFilterOption(moduleType, id, designerType, designer.id)
        });
    }, function(){
        return Promise.resolve("");
    });
};

_worker._getDesinger_processPlatform = function(id){
    var promiseArr = [];
    if (_worker.findData.filterOption.designerTypes.indexOf("script")!=-1){    //所有脚本
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listProcessScript, {"applicationId": id}, "processPlatform", "script"));
    }
    if (_worker.findData.filterOption.designerTypes.indexOf("form")!=-1){      //所有表单
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listProcessForm, {"applicationId": id}, "processPlatform", "form"));
    }
    if (_worker.findData.filterOption.designerTypes.indexOf("process")!=-1){   //所有流程
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listProcessProcess, {"applicationId": id}, "processPlatform", "process"));
    }
    return promiseArr;
};

_worker._getDesinger_cms = function(id){
    var promiseArr = [];
    if (_worker.findData.filterOption.designerTypes.indexOf("script")!=-1){    //所有脚本
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listCmsScript, {"flag": id}, "cms", "script"));
    }
    if (_worker.findData.filterOption.designerTypes.indexOf("form")!=-1){      //所有表单
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listCmsForm, {"appId": id}, "cms", "form"));
    }
    return promiseArr;
};

_worker._getDesinger_portal = function(id){
    var promiseArr = [];
    if (_worker.findData.filterOption.designerTypes.indexOf("script")!=-1){    //所有脚本
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listPortalScript, {"portalId": id}, "portal", "script"));
    }
    if (_worker.findData.filterOption.designerTypes.indexOf("page")!=-1){      //所有表单
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listPortalPage, {"portalId": id}, "portal", "page"));
    }
    if (_worker.findData.filterOption.designerTypes.indexOf("widget")!=-1){   //所有流程
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listPortalWidget, {"portalId": id}, "portal", "widget"));
    }
    return promiseArr;
};

_worker._getDesinger_query = function(id){
    var promiseArr = [];
    if (_worker.findData.filterOption.designerTypes.indexOf("view")!=-1){    //所有脚本
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listQueryView, {"flag": id}, "query", "view"));
    }
    if (_worker.findData.filterOption.designerTypes.indexOf("stat")!=-1){      //所有表单
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listQueryStat, {"flag": id}, "query", "stat"));
    }
    if (_worker.findData.filterOption.designerTypes.indexOf("statement")!=-1){   //所有流程
        promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listQueryStatement, {"flag": id}, "query", "statement"));
    }
    return promiseArr;
};

_worker._getDesinger_service = function(id){
    var promiseArr = [];
    if (_worker.findData.filterOption.designerTypes.indexOf("script")!=-1){    //所有脚本
        if (id=="invoke"){
            promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listInvoke, null, "service", "script"));
        }else{
            promiseArr.push(_worker._getDesingerModule(id, _worker.findData.actions.listAgent, null, "service", "script"));
        }
    }
    return promiseArr;
};


_worker._listApplication = function(type){
    switch (type){
        case "processPlatform":
            return this.action.sendRequest(_worker._getRequestOption({"url": this.findData.actions.listProcess, "debug": this.findData.debug, "token": this.findData.token }));
        case "cms":
            return this.action.sendRequest(_worker._getRequestOption({"url": this.findData.actions.listCms, "debug": this.findData.debug, "token": this.findData.token }));
        case "portal":
            return this.action.sendRequest(_worker._getRequestOption({"url": this.findData.actions.listPortal, "debug": this.findData.debug, "token": this.findData.token }));
        case "query":
            return this.action.sendRequest(_worker._getRequestOption({"url": this.findData.actions.listQuery, "debug": this.findData.debug, "token": this.findData.token }));
    }

};
_worker._parseFindModule = function(moduleList){
    var promiseArr = [];
    moduleList.forEach(function(module){
        if (module.moduleType==="service"){
            if (!module.flagList || !module.flagList.length){

                var filterOption = JSON.parse(_worker.filterOptionTemplete);
                filterOption.moduleList.push({
                    "moduleType": module.moduleType,
                    "moduleAppList": [{"appId": "invoke"}]
                });
                this.filterOptionList.push(filterOption);

                var filterOption = JSON.parse(_worker.filterOptionTemplete);
                filterOption.moduleList.push({
                    "moduleType": module.moduleType,
                    "moduleAppList": [{"appId": "agent"}]
                });
                this.filterOptionList.push(filterOption);

                promiseArr.push(Promise.resolve(""));
                //promiseArr = promiseArr.concat(_worker["_getDesinger_"+module.moduleType]("invoke"));
                //promiseArr = promiseArr.concat(_worker["_getDesinger_"+module.moduleType]("agent"));
            }else{
                module.flagList.forEach(function(flag){
                    if (!flag.designerList || !flag.designerList.length){
                        promiseArr = promiseArr.concat(_worker["_getDesinger_"+module.moduleType](flag.id));
                    }else{
                        flag.designerList.forEach(function(designer){
                            var filterOption = _worker._createFilterOption(module.moduleType, flag.id, designer.designerType, designer.id)
                            promiseArr = promiseArr.concat(Promise.resolve(filterOption));
                        });
                    }
                });
            }
        }else{
            if (!module.flagList || !module.flagList.length){
                var p = _worker._listApplication(module.moduleType);
                promiseArr.push(p.then(function(json){

                    //按应用进行搜索
                    if (json && json.data) json.data.forEach(function(app){
                        var filterOption = JSON.parse(_worker.filterOptionTemplete);
                        filterOption.moduleList.push({
                            "moduleType": module.moduleType,
                            "moduleAppList": [{"appId": app.id}]
                        });
                        this.filterOptionList.push(filterOption);
                    });
                    return Promise.resolve("");

                    //按设计元素进行搜索
                    // var pArr = [];
                    // json.data.forEach(function(app){
                    //     pArr = pArr.concat(_worker["_getDesinger_"+module.moduleType](app.id));
                    // });
                    // return Promise.all(pArr);
                }, function(){
                    return Promise.resolve("");
                }));

            }else{
                module.flagList.forEach(function(flag){
                    if (!flag.designerList || !flag.designerList.length){
                        promiseArr = promiseArr.concat(_worker["_getDesinger_"+module.moduleType](flag.id));
                    }else{
                        flag.designerList.forEach(function(designer){
                            var filterOption = _worker._createFilterOption(module.moduleType, flag.id, designer.designerType, designer.id)
                            promiseArr = promiseArr.concat(Promise.resolve(filterOption));
                        });
                    }
                });
            }
        }
    }.bind(this));
    return promiseArr;
}

_worker._findMessageReply = function(data, option){
    //_worker.setTimeout(function(){
        _worker.postMessage({
            "type": "find",
            "data": data,
            "option": option
        });
    //}, 10);
};
_worker._findOptionReply = function(){
    _worker.postMessage({
        "type": "done"
    });
};
_worker._findCompletedReply = function(data, option){
    _worker.setTimeout(function(){
        _worker.postMessage({
            "type": "completed"
        });
    }, 100);
};



_worker._createFindMessageReplyData = function(module, designer, aliase, pattern){
    return {
        "module": module,
        "appId": designer.appId,
        "appName": designer.appName,
        "designerId": designer.designerId,
        "designerName": designer.designerName,
        "designerType": designer.designerType,
        "designerAliase": aliase,

        "pattern": pattern
    };
};

_worker._setFilterOptionRegex = function(){
    var keyword = _worker.findData.filterOption.keyword;
    keyword = keyword.replace("[", "\\[").replace("]", "\\]").replace("(", "\\(").replace(")", "\\)").replace("{", "\\{").replace("}", "\\}")
        .replace("^", "\\^").replace("$", "\\$").replace(".", "\\.").replace("?", "\\?").replace("+", "\\+").replace("*", "\\*").replace("|", "\\|");

    if (_worker.findData.filterOption.matchRegExp){
        var flag = (_worker.findData.filterOption.caseSensitive) ? "gm" : "gmi";
        this.keywordRegexp =  new RegExp(keyword, flag);
    }else{
        var flag = (_worker.findData.filterOption.caseSensitive) ? "gm" : "gmi";
        keyword = (_worker.findData.filterOption.matchWholeWord) ? "\\b"+keyword+"\\b" : keyword;
        this.keywordRegexp = new RegExp(keyword, flag);
    }
};

_worker._findProcessPlatformParse_script = function(designer, option, module){
    if (designer.patternList && designer.patternList.length){
        // var action = this.findData.actions.getProcessScript;
        //
        // var p = _worker.action.sendRequest(_worker._getRequestOption({"url": action}, {"id": designer.designerId}));
        // p.then(function(json){
            designer.patternList.forEach(function(pattern){
                if (pattern.property=="text"){
                    //var scriptLines = json.data.text.split(/\n/);
                    pattern.lines.forEach(function(line){
                        var scriptText = line.lineValue;
                        _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                            "property": pattern.property,
                            "value": scriptText,
                            "line": line.line
                        }), option);

                        // var scriptText = scriptLines[line-1];
                        // while ((arr = this.keywordRegexp.exec(scriptText)) !== null) {
                        //     var col = arr.index;
                        //     var key = arr[0];
                        //     var value = arr.input;
                        //     _worker._findMessageReply(_worker._createFindMessageReplyData("processPlatform", designer, json.data.aliase, {
                        //         "property": pattern.property,
                        //         "value": value,
                        //         "line": line,
                        //         "column": col,
                        //         "key": key
                        //     }), option);
                        // }
                    });
                }else{
                    // _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                    //     "property": pattern.property,
                    //     "value": pattern.propertyValue
                    // }), option);
                }
            });
        //}, function(){});
    }
};

_worker.findScriptLineValue = function(result, code, preLine, preIndex, len){
    var lineRegexp = /\r\n|\n|\r/g;
    var preText = code.substring(preIndex, result.index);
    var m = preText.match(lineRegexp);
    preLine += (m) ? m.length : 0;

    var value = result[0];

    var n = result.index-1;
    var char = code.charAt(n);
    while (!lineRegexp.test(char) && n>=0){
        value = char+value;
        n--;
        char = code.charAt(n);
    }
    n =  this.keywordRegexp.lastIndex;
    char = code.charAt(n);
    while (!lineRegexp.test(char) && n<len){
        value = value+char;
        n++;
        char = code.charAt(n);
    }
    preIndex = this.keywordRegexp.lastIndex = n;
    return {"value": value, "preLine": preLine, "preIndex": preIndex};
};

_worker.findInDesigner_script = function(formData, key, module, designer, propertyDefinition, option, mode, path){
    if (formData[key].hasOwnProperty("code")) path.push("code");
    var code = formData[key].code || formData[key];
    if (code){
        this.keywordRegexp.lastIndex = 0;
        var len = code.length;

        var preLine = 0;
        var preIndex = 0;
        var result;
        while ((result = this.keywordRegexp.exec(code)) !== null){
            var obj = _worker.findScriptLineValue(result, code, preLine, preIndex, len);
            preLine = obj.preLine;
            preIndex = obj.preIndex;

            _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                "type": formData.type,
                "propertyType": propertyDefinition.type || "text",
                "propertyName": propertyDefinition.name,
                "name": formData.name || formData.id,
                "id": formData.id,
                "key": key,
                "value": obj.value,
                "line": preLine+1,
                "mode": mode,
                "path": path
            }), option);
        }
    }
};




_worker.findInDesigner_events = function(formData, key, module, designer, propertyDefinition, option, mode, path){
    var eventObj = formData[key];
    Object.keys(eventObj).forEach(function(evkey){
        var code = eventObj[evkey].code;
        if (code){
            this.keywordRegexp.lastIndex = 0;
            var len = code.length;

            var preLine = 0;
            var preIndex = 0;
            var result;
            while ((result = this.keywordRegexp.exec(code)) !== null){
                var obj = _worker.findScriptLineValue(result, code, preLine, preIndex, len);
                preLine = obj.preLine;
                preIndex = obj.preIndex;

                _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                    "type": formData.type,
                    "propertyType": propertyDefinition.type || "text",
                    "propertyName": propertyDefinition.name,
                    "name": formData.name || formData.id,
                    "id": formData.id,
                    "key": key,
                    "evkey": evkey,
                    "value": obj.value,
                    "line": preLine+1,
                    "mode": mode,
                    "path": (path) ? path.concat([evkey, "code"]) : ""
                }), option);
            }
        }
    });
};
_worker.findInDesigner_map = function(formData, key, module, designer, propertyDefinition, option, mode, path){
    var map = formData[key];
    Object.keys(map).forEach(function(evkey) {
        this.keywordRegexp.lastIndex = 0;
        var text = map[evkey];
        if (text){
            if ((typeof text)=="string") {
                if (this.keywordRegexp.test(text)) {
                    _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                        "type": formData.type,
                        "propertyType": propertyDefinition.type || "text",
                        "propertyName": propertyDefinition.name,
                        "name": formData.name || formData.id,
                        "id": formData.id,
                        "key": key,
                        "value": evkey + ": " + text,
                        "mode": mode,
                        "path": path
                    }), option);
                }
            }else{
                Object.keys(text).forEach(function (stylekey){
                    this.keywordRegexp.lastIndex = 0;

                    if (this.keywordRegexp.test(text[stylekey])) {
                        _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                            "type": formData.type,
                            "propertyType": propertyDefinition.type || "text",
                            "propertyName": propertyDefinition.name+"-"+evkey,
                            "name": formData.name || formData.id,
                            "id": formData.id,
                            "key": key,
                            "value": stylekey + ": " + text[stylekey],
                            "mode": mode,
                            "path": path.concat(evkey)
                        }), option);
                    }
                });
            }
        }
    });
};
_worker.findInDesigner_array = function(formData, key, module, designer, propertyDefinition, option, mode, path){
    var arr = formData[key];
    arr.forEach(function(v, i) {
        this.keywordRegexp.lastIndex = 0;
        var text = v.toString();
        if (this.keywordRegexp.test(text)){
            _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                "type": formData.type,
                "propertyType": propertyDefinition.type || "text",
                "propertyName": propertyDefinition.name,
                "name": formData.name || formData.id,
                "id": formData.id,
                "line": i+1,
                "key": key,
                "value": text,
                "mode": mode,
                "path": path
            }), option);
        }
    });
};
_worker.findInDesigner_objectArray = function(formData, key, module, designer, propertyDefinition, option, mode, path){
    var arr = formData[key];
    arr.forEach(function(map, i) {
        var p = path.concat(i);
        Object.keys(map).forEach(function(evkey) {
            this.keywordRegexp.lastIndex = 0;
            var text = map[evkey];
            if (this.keywordRegexp.test(text)){
                _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                    "type": formData.type,
                    "propertyType": propertyDefinition.type || "text",
                    "propertyName": propertyDefinition.name,
                    "name": formData.name || formData.id,
                    "id": formData.id,
                    "line": i+1,
                    "key": key,
                    "value": evkey+": "+text,
                    "mode": mode,
                    "path": p.concat(evkey)
                }), option);
            }
        });
    });
};

_worker.findInDesigner_duty = function(formData, key, module, designer, propertyDefinition, option, mode, path){
    var text = formData[key];
    if (text){
        var json = JSON.parse(text);
        json.forEach(function(duty, i) {
            var p = path.concat(i);
            this.keywordRegexp.lastIndex = 0;
            var text = duty.name;
            if (text) if (this.keywordRegexp.test(text)){
                _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                    "type": formData.type,
                    "propertyType": propertyDefinition.type || "text",
                    "propertyName": propertyDefinition.name,
                    "name": formData.name || formData.id,
                    "id": formData.id,
                    "key": key,
                    "line": i+1,
                    "valueKey": "name",
                    "value": "name:"+text,
                    "mode": mode,
                    "path": p.concat("name")
                }), option);
            }

            var code = duty.code;
            if (code){
                this.keywordRegexp.lastIndex = 0;
                var len = code.length;
                var idx = i+1;
                var preLine = 0;
                var preIndex = 0;
                var result;
                while ((result = this.keywordRegexp.exec(code)) !== null){
                    var obj = _worker.findScriptLineValue(result, code, preLine, preIndex, len);
                    preLine = obj.preLine;
                    preIndex = obj.preIndex;


                    _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                        "type": formData.type,
                        "propertyType": propertyDefinition.type || "text",
                        "propertyName": propertyDefinition.name+"(code)&nbsp;"+idx+"."+duty.name,
                        "name": formData.name || formData.id,
                        "id": formData.id,
                        "key": key,
                        "valueKey": "code",
                        "idx": i,
                        "value": obj.value,
                        "line": preLine+1,
                        "mode": mode,
                        "path": p.concat("code")
                    }), option);
                }
            }
        });
    }
};

_worker.findInDesigner_actions = function(formData, key, module, designer, propertyDefinition, option, mode, path){
    var arr = formData[key];
    arr.forEach(function(action, i) {
        var p = path.concat(i);
        this.keywordRegexp.lastIndex = 0;
        var text = action.text;
        if (text) if (this.keywordRegexp.test(text)){
            _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                "type": formData.type,
                "propertyType": propertyDefinition.type || "text",
                "propertyName": propertyDefinition.name,
                "name": formData.name || formData.id,
                "id": formData.id,
                "key": key,
                "line": i+1,
                "value": "text:"+text,
                "mode": mode,
                "path": p.concat("text")
            }), option);
        }

        var code = action.actionScript;
        if (code){
            this.keywordRegexp.lastIndex = 0;
            var len = code.length;
            var idx = i+1;
            var preLine = 0;
            var preIndex = 0;
            var result;
            while ((result = this.keywordRegexp.exec(code)) !== null){
                var obj = _worker.findScriptLineValue(result, code, preLine, preIndex, len);
                preLine = obj.preLine;
                preIndex = obj.preIndex;


                _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                    "type": formData.type,
                    "propertyType": propertyDefinition.type || "text",
                    "propertyName": propertyDefinition.name+"(actionScript)&nbsp;"+action.text,
                    "name": formData.name || formData.id,
                    "id": formData.id,
                    "key": key,
                    "value": obj.value,
                    "line": preLine+1,
                    "mode": mode,
                    "path": p.concat("actionScript")
                }), option);
            }
        }
        code = action.condition;
        if (code){
            this.keywordRegexp.lastIndex = 0;
            var len = code.length;
            var idx = i+1;
            var preLine = 0;
            var preIndex = 0;
            var result;
            while ((result = this.keywordRegexp.exec(code)) !== null){
                var obj = _worker.findScriptLineValue(result, code, preLine, preIndex, len);
                preLine = obj.preLine;
                preIndex = obj.preIndex;

                _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                    "type": formData.type,
                    "propertyType": propertyDefinition.type || "text",
                    "propertyName": propertyDefinition.name+"(condition)&nbsp;"+action.text,
                    "name": formData.name || formData.id,
                    "id": formData.id,
                    "key": key,
                    "value": obj.value,
                    "line": preLine+1,
                    "mode": mode,
                    "path": p.concat("condition")
                }), option);
            }
        }
    });
};

_worker.findInDesigner_filter = function(formData, key, module, designer, propertyDefinition, option, mode){
    var arr = formData[key];
    arr.forEach(function(filter, i) {
        this.keywordRegexp.lastIndex = 0;
        var text = filter.path;
        if (text) if (this.keywordRegexp.test(text)){
            _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                "type": formData.type,
                "propertyType": propertyDefinition.type || "text",
                "propertyName": propertyDefinition.name,
                "name": formData.name || formData.id,
                "id": formData.id,
                "key": key,
                "line": i+1,
                "value": "path:"+text,
                "mode": mode
            }), option);
        }

        var value = filter.value;
        if (value) if (this.keywordRegexp.test(value)){
            _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                "type": formData.type,
                "propertyType": propertyDefinition.type || "text",
                "propertyName": propertyDefinition.name,
                "name": formData.name || formData.id,
                "id": formData.id,
                "key": key,
                "line": i+1,
                "value": "value:"+value,
                "mode": mode
            }), option);
        }

        var otherValue = filter.otherValue;
        if (otherValue) if (this.keywordRegexp.test(otherValue)){
            _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                "type": formData.type,
                "propertyType": propertyDefinition.type || "text",
                "propertyName": propertyDefinition.name,
                "name": formData.name || formData.id,
                "id": formData.id,
                "key": key,
                "line": i+1,
                "value": "otherValue:"+otherValue,
                "mode": mode
            }), option);
        }


        var code = (filter.code) ? filter.code.code: "";
        if (code){
            this.keywordRegexp.lastIndex = 0;
            var len = code.length;
            var idx = i+1;
            var preLine = 0;
            var preIndex = 0;
            var result;
            while ((result = this.keywordRegexp.exec(code)) !== null){
                var obj = _worker.findScriptLineValue(result, code, preLine, preIndex, len);
                preLine = obj.preLine;
                preIndex = obj.preIndex;


                _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                    "type": formData.type,
                    "propertyType": propertyDefinition.type || "text",
                    "propertyName": propertyDefinition.name+"(code)&nbsp;"+filter.path,
                    "name": formData.name || formData.id,
                    "id": formData.id,
                    "key": key,
                    "value": obj.value,
                    "line": preLine+1,
                    "mode": mode
                }), option);
            }
        }

        code = (filter.valueScript) ? filter.valueScript.code : "";
        if (code){
            this.keywordRegexp.lastIndex = 0;
            var len = code.length;
            var idx = i+1;
            var preLine = 0;
            var preIndex = 0;
            var result;
            while ((result = this.keywordRegexp.exec(code)) !== null){
                var obj = _worker.findScriptLineValue(result, code, preLine, preIndex, len);
                preLine = obj.preLine;
                preIndex = obj.preIndex;


                _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                    "type": formData.type,
                    "propertyType": propertyDefinition.type || "text",
                    "propertyName": propertyDefinition.name+"(valueScript)&nbsp;"+filter.path,
                    "name": formData.name || formData.id,
                    "id": formData.id,
                    "key": key,
                    "value": obj.value,
                    "line": preLine+1,
                    "mode": mode
                }), option);
            }
        }

    });
};

_worker.findInDesigner_serial = function(formData, key, module, designer, propertyDefinition, option, mode){
    var text = formData[key];
    if (text) {
        var json = JSON.parse(text);

        json.forEach(function(serial, i) {
            this.keywordRegexp.lastIndex = 0;
            switch (serial.key){
                case "text":
                case "unitAttribute":
                    if (this.keywordRegexp.test(serial.value)){
                        _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                            "type": formData.type,
                            "propertyType": propertyDefinition.type || "text",
                            "propertyName": propertyDefinition.name+"&nbsp"+serial.key+"&nbsp",
                            "name": formData.name || formData.id,
                            "id": formData.id,
                            "key": key,
                            "value": serial.value
                        }), option);
                    }
                    break;
                case "script":
                    var code = serial.value;
                    if (serial.value){
                        if (code){
                            this.keywordRegexp.lastIndex = 0;
                            var len = code.length;
                            var idx = i+1;
                            var preLine = 0;
                            var preIndex = 0;
                            var result;
                            while ((result = this.keywordRegexp.exec(code)) !== null){
                                var obj = _worker.findScriptLineValue(result, code, preLine, preIndex, len);
                                preLine = obj.preLine;
                                preIndex = obj.preIndex;

                                _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                                    "type": formData.type,
                                    "propertyType": propertyDefinition.type || "text",
                                    "propertyName": propertyDefinition.name+"&nbsp"+serial.key+"&nbsp",
                                    "name": formData.name || formData.id,
                                    "id": formData.id,
                                    "key": key,
                                    "value": obj.value,
                                    "line": preLine+1
                                }), option);
                            }
                        }
                    }
                    break;
                default:
                    //nothing
            }
        });
    }
};


_worker.findInDesigner_projection = function(formData, key, module, designer, propertyDefinition, option, mode){
    var text = formData[key];
    if (text) {
        var json = JSON.parse(text);
        json.forEach(function(projection, i) {
            this.keywordRegexp.lastIndex = 0;
            var text = projection.path+"->"+projection.name;
            if (this.keywordRegexp.test(text)){
                _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                    "type": formData.type,
                    "propertyType": propertyDefinition.type || "text",
                    "propertyName": propertyDefinition.name,
                    "name": formData.name || formData.id,
                    "id": formData.id,
                    "key": key,
                    "value": text,
                    "line": i
                }), option);
            }
        });
    }
};

_worker.findInDesigner_selectConfig_script = function(formData, key, module, designer, propertyDefinition, option, mode, code, propertyName){
    if (code){
        this.keywordRegexp.lastIndex = 0;
        var len = code.length;
        var preLine = 0;
        var preIndex = 0;
        var result;
        while ((result = this.keywordRegexp.exec(code)) !== null){
            var obj = _worker.findScriptLineValue(result, code, preLine, preIndex, len);
            preLine = obj.preLine;
            preIndex = obj.preIndex;

            _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                "type": formData.type,
                "propertyType": propertyDefinition.type || "text",
                "propertyName": propertyName,
                "name": formData.name || formData.id,
                "id": formData.id,
                "key": key,
                "value": obj.value,
                "line": preLine+1
            }), option);
        }
    }
};
_worker.findInDesigner_selectConfig_events = function(formData, key, module, designer, propertyDefinition, option, mode, eventObj, propertyName){
    Object.keys(eventObj).forEach(function(evkey){
        var code = eventObj[evkey].code;
        this.keywordRegexp.lastIndex = 0;
        var len = code.length;

        var preLine = 0;
        var preIndex = 0;
        var result;
        while ((result = this.keywordRegexp.exec(code)) !== null){
            var obj = _worker.findScriptLineValue(result, code, preLine, preIndex, len);
            preLine = obj.preLine;
            preIndex = obj.preIndex;

            _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                "type": formData.type,
                "propertyType": propertyDefinition.type || "text",
                "propertyName": propertyName,
                "name": formData.name || formData.id,
                "id": formData.id,
                "key": key,
                "evkey": evkey,
                "value": obj.value,
                "line": preLine+1,
                "mode": mode
            }), option);
        }
    });
};

_worker.findInDesigner_selectConfig = function(formData, key, module, designer, propertyDefinition, option, mode){
    var text = formData[key];
    if (text) {
        var json = JSON.parse(text);
        json.forEach(function(config, i) {
            var idx = i+1;
            var propertyName = propertyDefinition.name+"&nbsp"+idx+"-"+config.name+"&nbsp默认值脚本(defaultValue)&nbsp";
            _worker.findInDesigner_selectConfig_script(formData, key, module, designer, propertyDefinition, option, mode, config.defaultValue.code, propertyName);

            propertyName = propertyDefinition.name+"&nbsp"+idx+"-"+config.name+"&nbsp校验脚本(validation)&nbsp";
            _worker.findInDesigner_selectConfig_script(formData, key, module, designer, propertyDefinition, option, mode, config.validation.code, propertyName);

            propertyName = propertyDefinition.name+"&nbsp"+idx+"-"+config.name+"&nbsp隐藏脚本(hiddenScript)&nbsp";
            _worker.findInDesigner_selectConfig_script(formData, key, module, designer, propertyDefinition, option, mode, config.hiddenScript.code, propertyName);

            propertyName = propertyDefinition.name+"&nbsp"+idx+"-"+config.name+"&nbsp身份选择组织范围脚本(identityRangeKey)&nbsp";
            _worker.findInDesigner_selectConfig_script(formData, key, module, designer, propertyDefinition, option, mode, config.identityRangeKey.code, propertyName);

            propertyName = propertyDefinition.name+"&nbsp"+idx+"-"+config.name+"&nbsp职务范围脚本(rangeDutyKey)&nbsp";
            _worker.findInDesigner_selectConfig_script(formData, key, module, designer, propertyDefinition, option, mode, config.rangeDutyKey.code, propertyName);

            propertyName = propertyDefinition.name+"&nbsp"+idx+"-"+config.name+"&nbsp身份添加脚本(identityIncludeKey)&nbsp";
            _worker.findInDesigner_selectConfig_script(formData, key, module, designer, propertyDefinition, option, mode, config.identityIncludeKey.code, propertyName);

            propertyName = propertyDefinition.name+"&nbsp"+idx+"-"+config.name+"&nbsp组织选择组织范围脚本(unitRangeKey)&nbsp";
            _worker.findInDesigner_selectConfig_script(formData, key, module, designer, propertyDefinition, option, mode, config.unitRangeKey.code, propertyName);

            propertyName = propertyDefinition.name+"&nbsp"+idx+"-"+config.name+"&nbsp排除范围脚本(exclude)&nbsp";
            _worker.findInDesigner_selectConfig_script(formData, key, module, designer, propertyDefinition, option, mode, config.exclude.code, propertyName);

            var arr = config.rangeDuty;
            if (arr && arr.length) arr.forEach(function(v, i) {
                this.keywordRegexp.lastIndex = 0;
                var text = v.toString();
                if (this.keywordRegexp.test(text)){

                    propertyName = propertyDefinition.name+"&nbsp"+idx+"-"+config.name+"&nbsp职务范围(rangeDuty)&nbsp";

                    _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                        "type": formData.type,
                        "propertyType": propertyDefinition.type || "text",
                        "propertyName": propertyName,
                        "name": formData.name || formData.id,
                        "id": formData.id,
                        "line": i+1,
                        "key": key,
                        "value": text,
                        "mode": mode
                    }), option);
                }
            });

            propertyName = propertyDefinition.name+"&nbsp"+idx+"-"+config.name+"&nbsp事件(events)&nbsp";
            _worker.findInDesigner_selectConfig_events(formData, key, module, designer, propertyDefinition, option, mode, config.events, propertyName);
        });
    }
};

_worker.findInDesigner_text = function(formData, key, module, designer, propertyDefinition, option, mode){
    this.keywordRegexp.lastIndex = 0;
    var text = formData[key];
    if (this.keywordRegexp.test(text)){
        _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
            "type": formData.type,
            "propertyType": propertyDefinition.type || "text",
            "propertyName": propertyDefinition.name,
            "name": formData.name || formData.id,
            "id": formData.id,
            "key": key,
            "value": text,
            "mode": mode
        }), option);
    }
};

_worker.findInDesignerProperty = function(key, propertyDefinition, formData, option, module, designer, mode, path){
    if (propertyDefinition){
        switch (propertyDefinition.type){
            case "html":
                _worker.findInDesigner_script(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "script":
            case "css":
            case "sql":
                _worker.findInDesigner_script(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "events":
                _worker.findInDesigner_events(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "map":
                _worker.findInDesigner_map(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "array":
                _worker.findInDesigner_array(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "object-array":
                _worker.findInDesigner_objectArray(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "duty":
                _worker.findInDesigner_duty(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "actions":
                _worker.findInDesigner_actions(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "filter":
                _worker.findInDesigner_filter(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "serial":
                _worker.findInDesigner_serial(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "projection":
                _worker.findInDesigner_projection(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            case "selectConfig":
                _worker.findInDesigner_selectConfig(formData, key, module, designer, propertyDefinition, option, mode, path);
                break;
            default:
                _worker.findInDesigner_text(formData, key, module, designer, propertyDefinition, option, mode, path);
        }
    }
}

_worker.findInDesigner = function(formData, option, module, designer, mode, path){
    Object.keys(formData).forEach(function(key){
        var propertyDefinition = this.designerPropertysData.form[key];
        _worker.findInDesignerProperty(key, propertyDefinition, formData, option, module, designer, mode, path.concat(key));
    });
};

_worker.decodeJsonString = function(str){
    var tmp = "[\""+str+"\"]";
    var dataObj = (JSON.parse(tmp));
    return dataObj[0];
};

_worker._getDesignerData = function(designer, module){
    var action = "";

    switch (designer.designerType){
        case "form":
            if (module=="processPlatform") action = this.findData.actions.getProcessForm
            else if (module=="cms") action = this.findData.actions.getCmsForm;
            break;
        case "process":
            action = this.findData.actions.getProcessProcess;
            break;
        case "page":
            action = this.findData.actions.getPortalPage;
            break;
        case "widget":
            action = this.findData.actions.getPortalWidget;
            break;
        case "view":
            action = this.findData.actions.getQueryView;
            break;
        case "statement":
            action = this.findData.actions.getQueryStatement;
            break;
    }

    if (action){
        var formPromise = _worker.action.sendRequest(_worker._getRequestOption({"url": action}, {"id": designer.designerId}));
        if (!this.designerPropertysData) this.designerPropertysData = _worker.action.sendRequest(_worker._getRequestOption({"url": "../x_component_FindDesigner/propertys.json"}));
        return Promise.all([formPromise, this.designerPropertysData]);
    }
};

_worker._findInDesigner_form = function(formData, designer, option, module, path){
    var mode = formData.json.mode;
    path.push("json");
    _worker.findInDesigner(formData.json, option, module, designer, mode, path);

    path.push("moduleList");
    for (key in formData.json.moduleList){
        if (formData.json.moduleList[key].recoveryStyles) formData.json.moduleList[key].styles = formData.json.moduleList[key].recoveryStyles;
        if (formData.json.moduleList[key].recoveryInputStyles) formData.json.moduleList[key].inputStyles = formData.json.moduleList[key].recoveryInputStyles;

        _worker.findInDesigner(formData.json.moduleList[key], option, module, designer, mode, path.concat(key));
    }
};

_worker._findProcessPlatformParse_form = function(designer, option, module){
    if (designer.patternList && designer.patternList.length){
        var p = _worker._getDesignerData(designer, module);
        if (p){
            var patternPropertys = designer.patternList.map(function(a){return a.property;});
            p.then(function(arr){
                var formJson = arr[0];
                this.designerPropertysData = arr[1];
                if (patternPropertys.indexOf("data")!=-1){
                    var formData = JSON.parse(_worker.decodeJsonString(formJson.data.data));
                    _worker._findInDesigner_form(formData, designer, option, module, ["data"]);
                }
                if (patternPropertys.indexOf("mobileData")!=-1){
                    var formData = JSON.parse(_worker.decodeJsonString(formJson.data.mobileData));
                    _worker._findInDesigner_form(formData, designer, option, module, ["mobileData"])
                }
            }, function(){});
        }
    }
};

_worker._findProcessPlatformParse_view = function(designer, option, module){
    debugger;
    if (designer.patternList && designer.patternList.length){
        if (!this.designerPropertysData) this.designerPropertysData = _worker.action.sendRequest(_worker._getRequestOption({"url": "../x_component_FindDesigner/propertys.json"}));
        var patternPropertys = designer.patternList.map(function(a){return a.property;});
        if (patternPropertys.indexOf("data")!=-1){

            var p = _worker._getDesignerData(designer, module);
            if (p) {
                p.then(function (arr) {
                    var viewJson = arr[0].data;
                    this.designerPropertysData = arr[1];

                    var viewData = JSON.parse(viewJson.data);

                    viewData.type = "View";
                    viewData.name = viewJson.name;
                    Object.keys(viewData).forEach(function(key){
                        if (key!=="where" && key!=="selectList" && key!=="actionbarList" && key!=="pagingList" && key!=="type" && key!=="name" ){
                            var propertyDefinition = this.designerPropertysData.view[key];
                            _worker.findInDesignerProperty(key, propertyDefinition, viewData, option, module, designer, null, ["data", key]);
                        }
                    });

                    if (viewData.where) {
                        viewData.where.type = "View";
                        viewData.where.name = viewJson.name;
                        Object.keys(viewData.where).forEach(function (key) {
                            if (key !== "type" && key !== "name") {
                                var propertyDefinition = this.designerPropertysData.view[key];
                                _worker.findInDesignerProperty(key, propertyDefinition, viewData.where, option, module, designer, null, ["data", "where", key]);
                            }
                        });
                    }

                    if (viewData.selectList) {
                        viewData.selectList.forEach(function (col, i) {
                            col.type = "column";
                            col.name = col.displayName;
                            Object.keys(col).forEach(function (key) {
                                if (key !== "name" && key !== "type") {
                                    var propertyDefinition = this.designerPropertysData.view[key];
                                    _worker.findInDesignerProperty(key, propertyDefinition, col, option, module, designer, null, ["data", "selectList", i, key]);
                                }
                            });
                        });
                    }

                    if (viewData.actionbarList) {
                        viewData.actionbarList.forEach(function (item, i) {
                            item.type = "actionbar";
                            item.name = item.type;
                            Object.keys(item).forEach(function (key) {
                                if (key !== "name" && key !== "type") {
                                    var propertyDefinition = this.designerPropertysData.view[key];
                                    _worker.findInDesignerProperty(key, propertyDefinition, item, option, module, designer, null, ["data", "actionbarList", i, key]);
                                }
                            });
                        });
                    }

                    if (viewData.pagingList) {
                        viewData.pagingList.forEach(function (item, i) {
                            item.type = "paging";
                            item.name = item.type;
                            Object.keys(item).forEach(function (key) {
                                if (key !== "name" && key !== "type") {
                                    var propertyDefinition = this.designerPropertysData.view[key];
                                    _worker.findInDesignerProperty(key, propertyDefinition, item, option, module, designer, null, ["data", "pagingList", i, key]);
                                }
                            });
                        });
                    }

                }, function () {
                });
            }
        }else{
            Promise.resolve(this.designerPropertysData).then(function(data){
                this.designerPropertysData = data;
                designer.patternList.forEach(function(pattern){
                    var propertyDefinition = this.designerPropertysData.view[pattern.property];
                    if (propertyDefinition){
                        _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
                            "type": "View",
                            "propertyType": propertyDefinition.type || "text",
                            "propertyName": propertyDefinition.name,
                            "name": designer.designerName,
                            "key": pattern.property,
                            "path": [pattern.property],
                            "value": pattern.propertyValue
                        }), option);
                    }
                });
            }, function(){});
        }
    }
}

_worker._findProcessPlatformParse_statement = function(designer, option, module){
    if (designer.patternList && designer.patternList.length){
        if (!this.designerPropertysData) this.designerPropertysData = _worker.action.sendRequest(_worker._getRequestOption({"url": "../x_component_FindDesigner/propertys.json"}));
        // var patternPropertys = designer.patternList.map(function(a){return a.property;});
        // if (patternPropertys.indexOf("view")!=-1){
debugger;
            var p = _worker._getDesignerData(designer, module);
            if (p) {
                p.then(function (arr) {
                    var statementJson = arr[0].data;
                    this.designerPropertysData = arr[1];

                    designer.patternList.forEach(function(pattern){
                        if (pattern.property=="view"){
                            if ((typeof statementJson.view)=="string"){
                                statementJson.view = JSON.parse(statementJson.view);
                            }
                            var viewData = statementJson.view.data;
                            if (viewData){
                                viewData.type = "View";
                                viewData.name = statementJson.name;
                                Object.keys(viewData).forEach(function(key){
                                    if (key!=="where" && key!=="selectList" && key!=="actionbarList" && key!=="pagingList" && key!=="type" && key!=="name" ){
                                        var propertyDefinition = this.designerPropertysData.view[key];
                                        _worker.findInDesignerProperty(key, propertyDefinition, viewData, option, module, designer, null, ["view", "data", key]);
                                    }
                                });

                                if (viewData.where) {
                                    viewData.where.type = "View";
                                    viewData.where.name = viewJson.name;
                                    Object.keys(viewData.where).forEach(function(key){
                                        if ( key!=="type" && key!=="name"){
                                            var propertyDefinition = this.designerPropertysData.view[key];
                                            _worker.findInDesignerProperty(key, propertyDefinition, viewData.where, option, module, designer, null, ["view", "data", "where", key]);
                                        }
                                    });
                                }

                                if (viewData.selectList) {
                                    viewData.selectList.forEach(function (col, i) {
                                        col.type = "View-column";
                                        col.name = col.displayName;
                                        Object.keys(col).forEach(function (key) {
                                            if (key !== "name" && key !== "type") {
                                                var propertyDefinition = this.designerPropertysData.view[key];
                                                _worker.findInDesignerProperty(key, propertyDefinition, col, option, module, designer, null, ["view", "data", "selectList", i, key]);
                                            }
                                        });
                                    });
                                }

                                if (viewData.actionbarList) {
                                    viewData.actionbarList.forEach(function (item, i) {
                                        item.type = "View-actionbar";
                                        item.name = item.type;
                                        Object.keys(item).forEach(function (key) {
                                            if (key !== "name" && key !== "type") {
                                                var propertyDefinition = this.designerPropertysData.view[key];
                                                _worker.findInDesignerProperty(key, propertyDefinition, item, option, module, designer, null, ["view", "data", "actionbarList", i, key]);
                                            }
                                        });
                                    });
                                }

                                if (viewData.pagingList) {
                                    viewData.pagingList.forEach(function (item, i) {
                                        item.type = "View-paging";
                                        item.name = item.type;
                                        Object.keys(item).forEach(function (key) {
                                            if (key !== "name" && key !== "type") {
                                                var propertyDefinition = this.designerPropertysData.view[key];
                                                _worker.findInDesignerProperty(key, propertyDefinition, item, option, module, designer, null, ["view", "data", "pagingList", i, key]);
                                            }
                                        });
                                    });
                                }
                            }
                        }else{
                            var propertyDefinition = this.designerPropertysData.statement[pattern.property];
                            if (propertyDefinition){
                                _worker.findInDesignerProperty(pattern.property, propertyDefinition, statementJson, option, module, designer, null, [pattern.property]);
                            }
                        }
                    });


                    // if (statementJson.view){
                    //     var viewJson = JSON.parse(statementJson.view);
                    //     var viewData = viewJson.data;
                    //
                    // }
                }, function () {});
            }
        // }else{
        //     Promise.resolve(this.designerPropertysData).then(function(data){
        //         this.designerPropertysData = data;
        //         designer.patternList.forEach(function(pattern){
        //             var propertyDefinition = this.designerPropertysData.statement[pattern.property];
        //             if (propertyDefinition){
        //                 _worker._findMessageReply(_worker._createFindMessageReplyData(module, designer, "", {
        //                     "type": "Statement",
        //                     "propertyType": propertyDefinition.type || "text",
        //                     "propertyName": propertyDefinition.name,
        //                     "name": designer.designerName,
        //                     "key": pattern.property,
        //                     "mode": null,
        //                     "path": [pattern.property],
        //                     "value": pattern.propertyValue
        //                 }), option);
        //             }
        //         });
        //     }, function(){});
        // }
    }
};


_worker._findProcessPlatformParse_process = function(designer, option, module){
    if (designer.patternList && designer.patternList.length){
        var p = _worker._getDesignerData(designer, module);
        if (p){
            p.then(function(arr){
                var processData = arr[0].data;
                this.designerPropertysData = arr[1];

                designer.patternList.forEach(function(pattern){
                    if (pattern.elementType === "process"){
                        debugger;
                        var propertyDefinition = this.designerPropertysData.process[pattern.property];
                        processData.type = pattern.elementType;
                        _worker.findInDesignerProperty(pattern.property, propertyDefinition, processData, option, module, designer, null, [pattern.property]);
                    }else if (pattern.elementType === "route") {
                        for (var i=0; i<processData.routeList.length; i++){
                            if (processData.routeList[i].id===pattern.elementId) break;
                        }
                        Object.keys(processData.routeList[i]).forEach(function(key){
                            var propertyDefinition = this.designerPropertysData.process[key];
                            processData.routeList[i].type = pattern.elementType;
                            _worker.findInDesignerProperty(key, propertyDefinition, processData.routeList[i], option, module, designer, null,["routeList", i, key]);
                        }.bind(this));


                    }else{
                        if (pattern.elementType=="begin" && pattern.elementId===processData.begin.id){
                            Object.keys(processData.begin).forEach(function(key){
                                var propertyDefinition = this.designerPropertysData.process[key];
                                processData.begin.type = pattern.elementType;
                                _worker.findInDesignerProperty(key, propertyDefinition, processData.begin, option, module, designer, null,["begin", key]);
                            }.bind(this));
                        }else{
                            var arrKey = pattern.elementType+"List";
                            for (var i=0; i<processData[arrKey].length; i++){
                                if (processData[arrKey][i].id===pattern.elementId) break;
                            }
                            Object.keys(processData[arrKey][i]).forEach(function(key){
                                var propertyDefinition = this.designerPropertysData.process[key];
                                processData[arrKey][i].type = pattern.elementType;
                                _worker.findInDesignerProperty(key, propertyDefinition, processData[arrKey][i], option, module, designer, null,[arrKey, i, key]);
                            }.bind(this));
                        }
                    }
                });
            }, function(){});
        }
    }
};

_worker._findProcessPlatformParse = function(resultList, option, module){
    resultList.forEach(function(designer){
        switch (designer.designerType){
            case "script":
                _worker._findProcessPlatformParse_script(designer, option, module);
                break;
            case "form":
            case "page":
            case "widget":
                debugger;
                _worker._findProcessPlatformParse_form(designer, option, module);
                break;
            case "process":
                _worker._findProcessPlatformParse_process(designer, option, module);
                break;
            case "view":
                _worker._findProcessPlatformParse_view(designer, option, module);
                break;
            case "statement":
                _worker._findProcessPlatformParse_statement(designer, option, module);
                break;
        }
    });
};




_worker._doFindDesigner = function(option, idx){

    var option = this.filterOptionList[idx];

        var res = _worker._getRequestOption({
            "method": "post",
            "url": this.findData.actions.findAction,
            "body": JSON.stringify(option),
            "debug": this.findData.debug,
            "token": this.findData.token
        });
        this.action.sendRequest(res).then(function(json){
            if (json.data.processPlatformList && json.data.processPlatformList.length){

                // var worker = new Worker("../x_component_FindDesigner/PatternWorker.js");
                // worker.onmessage = function(e) {
                //     if (e.data) _worker._findMessageReply(e.data, option);
                // }.bind(this);
                // worker.postMessage({
                //     "parser": "_findProcessPlatformParse",
                //     "actions": _worker.findData.actions,
                //     "option": option,
                //     "pattern": json.data.processPlatformList
                // });

               _worker._findProcessPlatformParse(json.data.processPlatformList, option, "processPlatform");
            }
            if (json.data.cmsList && json.data.cmsList.length){
                _worker._findProcessPlatformParse(json.data.cmsList, option, "cms");
            }
            if (json.data.portalList && json.data.portalList.length){
                _worker._findProcessPlatformParse(json.data.portalList, option, "portal");
            }
            if (json.data.queryList && json.data.queryList.length){
                _worker._findProcessPlatformParse(json.data.queryList, option, "query");
            }
            if (json.data.serviceList && json.data.serviceList.length){
                _worker._findProcessPlatformParse(json.data.serviceList, option, "service");
            }
            _worker._findOptionReply();

            idx++;
            if (this.filterOptionList.length>idx){
                _worker._doFindDesigner(null, idx);
            }else{
                debugger;
                _worker._findCompletedReply();
            }
            //this.filterOptionList[idx];

            //_worker._findMessageReply(json.data, option);
        }, function(xhr){
            _worker._findOptionReply(null);
            _worker._findMessageReply(null);

            idx++;
            if (this.filterOptionList.length>idx){
                _worker._doFindDesigner(null, idx);
            }else{
                _worker._findCompletedReply();
            }
        });

},

_worker._doFindDesignerFromFilterOption = function(){
    var idx = 0;
    //this.filterOptionList.forEach(function(option){
    //    _worker._doFindDesigner(option, idx);
    //});

    _worker._doFindDesigner(null, idx);
},
onmessage = function(e) {
    this.findData = e.data;
    var moduleList = this.findData.filterOption.moduleList;
    this.findData.filterOption.moduleList = [];
    _worker._setFilterOptionRegex();

    this.filterOptionTemplete = JSON.stringify(this.findData.filterOption);
    this.filterOptionList = [];

    Promise.all(_worker._parseFindModule(moduleList)).then(function(){
        // this.filterOptionList[0].moduleList=[];
        // //_worker._doFindDesigner(this.filterOptionList[0]);
        // this.filterOptionList = [this.filterOptionList[0]];
        _worker._readyMessageReply();
        debugger;
        _worker._doFindDesignerFromFilterOption();
    });

    //"moduleType": "cms", "flagList": [];
    // designerTypes
    // flagList = [{
    //     "id": "dddd",
    //     "desingerList": [
    //         {
    //             "desingerType": "脚本(script)表单(form)流程(process)页面(page)部件(widget)视图(view)脚本(statement)统计(stat",
    //             "id": ""
    //         },
    //         {
    //             "desingerType": "脚本(script)表单(form)流程(process)页面(page)部件(widget)视图(view)脚本(statement)统计(stat",
    //             "id": ""
    //         }
    //     ]
    // }]




    _worker._receiveMessageReply();



    //_worker.action.sendRequest(e.data);
}
