MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Htmleditor = MWF.APPHtmleditor =  new Class({
	Extends: MWF.APP$Module,
    options: {
        "moduleEvents": ["load", "postLoad", "afterLoad"]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
    },
    load: function(){

        if (this.fireEvent("queryLoad")){
            this._queryLoaded();
            this._loadUserInterface();
            this._loadStyles();
            //this._loadEvents();

            this._afterLoaded();
            this.fireEvent("postLoad");
            this.fireEvent("load");
        }
    },

	_loadUserInterface: function(){
		this.node.empty();
        if (this.readonly){
            this.node.set("html", this._getBusinessData());
            this.node.setStyles({
                "-webkit-user-select": "text",
                "-moz-user-select": "text"
            });
        }else{
            var config = Object.clone(this.json.editorProperties);
            if (this.json.config){
                if (this.json.config.code){
                    var obj = MWF.Macro.exec(this.json.config.code, this);
                    Object.each(obj, function(v, k){
                        config[k] = v;
                    });
                }
            }
            this.loadCkeditor(config);
        }
    //    this._loadValue();
	},
    loadCkeditor: function(config){
        COMMON.AjaxModule.loadDom("ckeditor", function(){
            CKEDITOR.disableAutoInline = true;
            var editorDiv = new Element("div").inject(this.node);
            var htmlData = this._getBusinessData();
            if (htmlData){
                editorDiv.set("html", htmlData);
            }else if (this.json.templateCode){
                editorDiv.set("html", this.json.templateCode);
            }
            var height = this.node.getSize().y;
            var editorConfig = config || {};

            if (this.form.json.mode==="Mobile"){
                if (!editorConfig.toolbar && !editorConfig.toolbarGroups){
                    editorConfig.toolbar = [
                        { name: 'paragraph',   items: [ 'Bold', 'Italic', "-" , 'TextColor', "BGColor", 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', "-", 'Undo', 'Redo' ] },
                        { name: 'basicstyles', items: [ 'Styles', 'FontSize']}
                    ];
                }
            }

            editorConfig.localImageMaxWidth = 800;
            editorConfig.reference = this.form.businessData.work.job;
            editorConfig.referenceType = "processPlatformJob";
            editorConfig.extraPlugins = ['pagebreak'];
            
            // CKEDITOR.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/";
            // CKEDITOR.plugins.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/plugins/";
            this.editor = CKEDITOR.replace(editorDiv, editorConfig);

            this.editor.addCommand("ecnet", {
                exec:function(editor){
                    this.ecnet();
                }.bind(this)
            });
            this.editor.ui.add('ecnet', CKEDITOR.UI_BUTTON, {
                label:'智能纠错',
                icon: '/x_component_process_Xform/$Form/default/icon/ecnet.png',
                command:"ecnet"
            });

            this._loadEvents();
            //this.editor.on("loaded", function(){
            //    this._loadEvents();
            //}.bind(this));

            //this.setData(data)

            this.editor.on("change", function(){
                //this._setBusinessData(this.getData());
            }.bind(this));

            if (this.json.ecnet==="y"){
                // this.editor.on( "key", function( evt ) {
                //     // var char = evt.data.domEvent.$.char;
                //     // if ([".", ",", "?", ";", "'", " "].indexOf(char)!==-1){
                //     //     this.ecnet(evt.editor.getData());
                //     // }
                // }.bind(this));
                // this.editor.on("blur", function(){
                //     if (!this.notEcnetFlag) this.ecnet(this.getData());
                // }.bind(this));
            }


            //    this._loadEvents();
        }.bind(this));
    },
    getEcnetString: function(node, nodes){
        for (var i=0; i<node.childNodes.length; i++){
            if (node.childNodes[i].nodeType===Node.TEXT_NODE){
                var s = this.ecnetString.length;
                this.ecnetString += node.childNodes[i].nodeValue;
                var e = this.ecnetString.length;

                nodes.push({
                    "pnode": node,
                    "node": node.childNodes[i],
                    "start": s, "end": e
                });
            }else{
                this.getEcnetString(node.childNodes[i], nodes);
            }
        }
    },
    createEcnetNode: function(node){
        var newNode = node.node.ownerDocument.createElement("span");

        var increment = 0;
        var html = node.node.nodeValue;;
        node.ecnets.each(function(ecnet){
            var s = ecnet.begin+increment-node.start;
            var e = ecnet.end+increment-node.start;
            if (s<0) s=0;
            if (e>node.end+increment) e = node.end+increment;
            var length = html.length;

            var left = html.substring(0, s);
            var ecnetStr = html.substring(s, e);
            var right = html.substring(e, html.length);

            html = left+"<span class='o2_ecnet_item' style='color: red'><u>"+ecnetStr+"</u></span>"+right;
            increment += (html.length-length);

        }.bind(this));
        newNode.innerHTML = html;
        node.pnode.replaceChild(newNode, node.node);
        node.pnode.textNode = node.node;
        node.pnode.ecnetNode = newNode;

        var _self = this;
        var editorFrame = this.editor.document.$.defaultView.frameElement;
        var spans = newNode.getElementsByTagName("span");
        if (spans.length){
            for (var i = 0; i<spans.length; i++){
                var span = spans[i];
                if (span.className==="o2_ecnet_item"){
                    var ecnetNode = new Element("div", {"styles": {
                        "border": "1px solid #999999",
                        "box-shadow": "0px 0px 5px #999999",
                        "background-color": "#ffffff",
                        "position": "fixed",
                        "display": "none"
                    }}).inject(editorFrame, "after");
                    var correctNode = new Element("div", {
                        "styles": {
                            "padding": "3px 10px",
                            "font-weight": "bold",
                            "font-size": "12px",
                            "cursor": "pointer"
                        },
                        "text": node.ecnets[i].origin+"->"+node.ecnets[i].correct,
                        "events": {
                            "mouseover": function(){this.setStyle("background-color", "#dddddd")},
                            "mouseout": function(){this.setStyle("background-color", "#ffffff")},
                            "mousedown": function(){
                                var ecnetNode = this.getParent();
                                var node = ecnetNode.node;
                                var item = ecnetNode.node.ecnets[ecnetNode.idx];
                                var textNode = node.node.ownerDocument.createTextNode(item.correct);
                                ecnetNode.span.parentNode.replaceChild(textNode, ecnetNode.span);
                                ecnetNode.destroy();
                                node.node.nodeValue = node.pnode.ecnetNode.innerText;

                                node.ecnets.erase(item);
                                if (!node.ecnets.length){
                                    _self.ecnetNodes.erase(node);
                                }
                            }
                        }
                    }).inject(ecnetNode);
                    var ignoreNode = new Element("div", {
                        "styles": {
                            "padding": "3px 10px",
                            "font-size": "12px",
                            "cursor": "pointer"
                        },
                        "text": MWF.xApplication.process.Xform.LP.ignore,
                        "events": {
                            "mouseover": function(){this.setStyle("background-color", "#dddddd")},
                            "mouseout": function(){this.setStyle("background-color", "#ffffff")},
                            "mousedown": function(){
                                var ecnetNode = this.getParent();
                                var node = ecnetNode.node;
                                var item = ecnetNode.node.ecnets[ecnetNode.idx];
                                var textNode = node.node.ownerDocument.createTextNode(ecnetNode.span.innerText);
                                ecnetNode.span.parentNode.replaceChild(textNode, ecnetNode.span);
                                ecnetNode.destroy();
                                node.node.nodeValue = node.pnode.ecnetNode.innerText;

                                node.ecnets.erase(item);
                                if (!node.ecnets.length){
                                    _self.ecnetNodes.erase(node);
                                }
                            }
                        }
                    }).inject(ecnetNode);
                    ecnetNode.node = node;
                    ecnetNode.idx = i;

                    span.ecnetNode = ecnetNode;
                    ecnetNode.span = span;
                    span.addEventListener("click", function(){
                        var ecnetNode = this.ecnetNode;
                        ecnetNode.show();
                        var y = this.offsetTop;
                        var x = this.offsetLeft;
                        var w = this.offsetWidth;
                        var h = this.offsetHeight;
                        var p = editorFrame.getPosition();
                        var s = ecnetNode.getSize();
                        var top = y+p.y+h+5;
                        var left = x+p.x-((s.x-w)/2);

                        ecnetNode.style.left = ""+left+"px";
                        ecnetNode.style.top = ""+top+"px";

                        var _span = this;
                        var hideEcnetNode = function(){
                            ecnetNode.hide();
                            _span.ownerDocument.removeEventListener("mousedown", hideEcnetNode);
                        };
                        this.ownerDocument.addEventListener("mousedown", hideEcnetNode);

                    });

                }
            }
        }

        //node.pnode.ecnetInforNode = ecnetNode;

        // var spans = newNode.getElementsByTagName("span");
        // if (spans.length){
        //     var span = spans[0];
        //     span.addEventListener("click", function(){
        //         ecnetNode.style.display = "block";
        //         var y = span.offsetTop;
        //         var x = span.offsetLeft;
        //         var w = span.offsetWidth;
        //         var h = span.offsetHeight;
        //         var p = editorFrame.getPosition();
        //         var s = ecnetNode.getSize();
        //         var top = y+p.y+h+5;
        //         var left = x+p.x-((s.x-w)/2);
        //
        //         ecnetNode.style.left = ""+left+"px";
        //         ecnetNode.style.top = ""+top+"px";
        //     });
        //     span.addEventListener("mouseout", function(){});
        // }

    },
    clearEcnetNodes: function(){
        if (this.ecnetNodes && this.ecnetNodes.length){
            this.ecnetNodes.each(function(node){
                if (node.pnode.ecnetNode){
                    if (node.pnode.ecnetInforNode) node.pnode.ecnetInforNode.destroy();
                    node.pnode.ecnetInforNode = null;
                    node.pnode.replaceChild(node.pnode.textNode, node.pnode.ecnetNode);
                }
            }.bind(this));
            this.ecnetNodes = [];
        }
    },
    ecnet: function(data){
        //this.editor.document.$.body.innerText
        var editorFrame = this.editor.document.$.defaultView.frameElement;
        //var data = this.editor.getData();
        var body = this.editor.document.$.body;

        if (!this.ecnetNodes) this.ecnetNodes = [];
        if (this.ecnetNodes.length) this.clearEcnetNodes();

        var nodes = [];
        this.ecnetString = "";
        this.getEcnetString(body, nodes);

        MWF.Actions.get("x_general_assemble_control").ecnetCheck({"value": this.ecnetString}, function(json){
            if (json.data.itemList && json.data.itemList.length){

                nodes.each(function(node){
                    var items = [];
                    json.data.itemList.each(function(item){
                        if ((node.end<=item.end && node.end>item.begin) || (node.start>=item.begin && node.start<item.end) || (node.start<=item.begin && node.end>item.end)){
                            items.push(item);
                        }
                    }.bind(this));
                    if (items.length){
                        node.ecnets = items;
                        this.ecnetNodes.push(node);
                    }
                }.bind(this));


                this.ecnetNodes.each(function(node){
                    this.createEcnetNode(node);
                }.bind(this));


                // var item = json.data.itemList[0];
                // var left = data.substring(0, item.begin);
                // var ecnetStr = data.substring(item.begin, item.end);
                // var right = data.substring(item.end, data.length);
                //
                // var newData = left+"<span class='o2_ecnet_item' style='color:red' title='"+item.origin+"->"+item.correc+"'><u>"+ecnetStr+"</u></span>"+right;
                //this.editor.document.$.body.setSelectionRange(item.begin, item.end);
                //this.editor.setData(newData);

                // var iframe = editorFrame.clone();
                // iframe.inject(this.node);
                // iframe.position({
                //     "relativeTo": editorFrame,
                //     "position": 'upperLeft',
                //     "edge": 'upperLeft'
                // });
                // iframe.contentWindow.document.body.set("html", newData);
            }else{
                body = null;
                nodes = null;
            }
        }.bind(this));
    },
    _loadEvents: function(editorConfig){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                this.editor.on(key, function(event){
                    return this.form.Macro.fire(e.code, this, event);
                }.bind(this), this);
            }
        }.bind(this));

    },
    addModuleEvent: function(key, fun){
        this.editor.on(key, function(event){
            return (fun) ? fun(this, event) : null;
        }.bind(this), this);
    },
    _loadValue: function(){
        var data = this._getBusinessData();
    },
    resetData: function(){
        this.setData(this._getBusinessData());
    },
    getData: function(){
        this.clearEcnetNodes();
        return this.editor.getData();
    },
    setData: function(data){
        this._setBusinessData(data);
        if (this.editor) this.editor.setData(data);
    },
    createErrorNode: function(text){
        var node = new Element("div");
        var iconNode = new Element("div", {
            "styles": {
                "width": "20px",
                "height": "20px",
                "float": "left",
                "background": "url("+"/x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
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
    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border", "1px solid red");

            this.errNode = this.createErrorNode(text).inject(this.node, "after");
            this.showNotValidationMode(this.node);
            if (!this.node.isIntoView()) this.node.scrollIntoView();
        }
    },
    showNotValidationMode: function(node){
        var p = node.getParent("div");
        if (p){
            if (p.get("MWFtype") == "tab$Content"){
                if (p.getParent("div").getStyle("display")=="none"){
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
    validationMode: function(){
        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("borderStyle"));
            if (this.errNode){
                this.errNode.destroy();
                this.errNode = null;
            }
        }
    },

    validationConfigItem: function(routeName, data){
        var flag = (data.status=="all") ? true: (routeName == data.decision);
        if (flag){
            var n = this.getData();
            var v = (data.valueType=="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if (!v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    },
    validationConfig: function(routeName, opinion){
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var data = this.json.validationConfig[i];
                    if (!this.validationConfigItem(routeName, data)) return false;
                }
            }
            return true;
        }
        return true;
    },

    validation: function(routeName, opinion){
        if (!this.validationConfig(routeName, opinion))  return false;

        if (!this.json.validation) return true;
        if (!this.json.validation.code) return true;
        var flag = this.form.Macro.exec(this.json.validation.code, this);
        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    }
}); 