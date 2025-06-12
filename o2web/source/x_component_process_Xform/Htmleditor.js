MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Htmleditor HTML编辑器。
 * @o2cn HTML编辑器
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var htmlEditor = this.form.get("name"); //获取组件
 * //方法2
 * var htmlEditor = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Htmleditor = MWF.APPHtmleditor =  new Class(
    /** @lends MWF.xApplication.process.Xform.Htmleditor# */
    {
	Extends: MWF.APP$Module,
    options: {
        /**
         * 组件异步加载后触发.
         * @event MWF.xApplication.process.Xform.Htmleditor#afterLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["queryLoad", "load", "postLoad", "afterLoad"]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
        this.fieldModuleLoaded = false;
    },
    load: function(){
        this._loadModuleEvents();
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
        if (this.isReadonly()){
            // this.node.set("html", this._getBusinessData());
            this.node.setStyles({
                "-webkit-user-select": "text",
                "-moz-user-select": "text"
            });
            if( layout.mobile ){
                this.loadLazyImage(function () { //图片懒加载
                    var images = this.node.getElements("img");
                    //移动端设置图片宽度为100%
                    images.each( function( img ){
                        if( img.hasClass("lozad") ){
                            img.setStyles({
                                "max-width" : "100%"
                            });
                        }else{
                            img.setStyles({
                                "height": "auto",
                                "max-width" : "100%"
                            });
                        }
                    }.bind(this));
                    this.fireEvent("afterLoad");
                    this.fieldModuleLoaded = true;
                }.bind(this))
            }else{
                this.loadLazyImage(function () { //图片懒加载
                    if(this.json.enablePreview !== "n"){
                        this.loadImageViewer(); //PC端点击显示大图
                        this.fireEvent("afterLoad");
                        this.fieldModuleLoaded = true;
                    }
                }.bind(this));
            }
            this.node.loadCss("../o2_lib/htmleditor/ckeditor4161/contents_o2.css");
        }else{
            var config = Object.clone(this.json.editorProperties);
            if (this.json.config){
                if (this.json.config.code){
                    var obj = this.form.Macro.exec(this.json.config.code, this);
                    Object.each(obj, function(v, k){
                        config[k] = v;
                    });
                }
            }
            this.loadCkeditor(config);
        }
    //    this._loadValue();
	},
    loadLazyImage: function(callback){
        o2.require("o2.widget.ImageLazyLoader", function(){
            var loadder = new o2.widget.ImageLazyLoader(this.node, this._getBusinessData());
            loadder.load(function(){
                if(callback)callback();
            }.bind(this))
        }.bind(this));
    },
    loadImageViewer: function(){
        o2.require("o2.widget.ImageViewer", function(){
            var imageViewer = new o2.widget.ImageViewer(this.node);
            imageViewer.load();
        }.bind(this));
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
                        { name: 'basicstyles', items: [ 'Styles', 'FontSize']},
                        { name: 'insert', items : [ 'Image' ] }
                    ];
                }
            }

            editorConfig.base64Encode = !layout.mobile && (this.json.base64Encode === "y");
            editorConfig.enablePreview = (this.json.enablePreview !== "n");
            editorConfig.localImageMaxWidth = 2000;

            if(this.form.options.macro === "PageContext"){
                editorConfig.reference = this.form.json.id;
                editorConfig.referenceType = "portalPage";
            }else{
                editorConfig.reference = this.form.businessData.work.job;
                editorConfig.referenceType = "processPlatformJob";
            }

            if( editorConfig && editorConfig.extraPlugins ){
                var extraPlugins = editorConfig.extraPlugins;
                extraPlugins = typeOf( extraPlugins ) === "array" ? extraPlugins : extraPlugins.split(",");
                extraPlugins.push( 'lineheight' );
                extraPlugins.push( 'pagebreak' );
                extraPlugins.push( 'o2image' );
                extraPlugins.push( 'o2uploadimage' );
                extraPlugins.push('o2uploadremoteimage');
                editorConfig.extraPlugins = extraPlugins;
            }else{
                editorConfig.extraPlugins = ['lineheight','pagebreak', 'o2image','o2uploadimage', 'o2uploadremoteimage'];
            }

            if( editorConfig && editorConfig.removePlugins ){
                var removePlugins = editorConfig.removePlugins;
                removePlugins = typeOf( removePlugins ) === "array" ? removePlugins : removePlugins.split(",");
                editorConfig.removePlugins = removePlugins.concat(['image','easyimage','exportpdf','cloudservices']);
            }else{
                editorConfig.removePlugins = ['image','easyimage','exportpdf','cloudservices'];
            }



            // CKEDITOR.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/";
            // CKEDITOR.plugins.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/plugins/";
            this.editor = CKEDITOR.replace(editorDiv, editorConfig);

            this.editor.addCommand("ecnet", {
                exec:function(editor){
                    this.ecnet();
                }.bind(this)
            });
            this.editor.ui.add('ecnet', CKEDITOR.UI_BUTTON, {
                label:MWF.xApplication.process.Xform.LP.intelligentCorrection,
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
                //在数据模板和数据表格中时
                if( this.parentLine )this._setBusinessData(this.getData());
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

            this.fireEvent("afterLoad");
            this.fieldModuleLoaded = true;

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
        var html = node.node.nodeValue;
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
    // /**
    //  * @summary 重置组件的值为默认值或置空。
    //  *  @example
    //  * this.form.get('fieldId').resetData();
    //  */
    resetData: function(){
        this.setData(this._getBusinessData());
    },
    /**
     * @summary 判断组件值是否为空.
     * @example
     * if( this.form.get('fieldId').isEmpty() ){
     *     this.form.notice('HTML编辑器不能为空', 'warn');
     * }
     * @return {Boolean} 值是否为空.
     */
    isEmpty : function(){
        return !this.getData().trim();
    },
    /**
     * 当表单上没有对应组件的时候，可以使用this.data[fieldId]获取值，但是this.form.get('fieldId')无法获取到组件。
     * @summary 获取组件值。
     * @example
     * var data = this.form.get('fieldId').getData();
     * @example
     *  //如果无法确定表单上是否有组件，需要判断
     *  var data;
     *  if( this.form.get('fieldId') ){ //判断表单是否有无对应组件
     *      data = this.form.get('fieldId').getData();
     *  }else{
     *      data = this.data['fieldId']; //直接从数据中获取字段值
     *  }
     * @return 组件的数据.
     */
    getData: function(){
        this.clearEcnetNodes();
        return this.editor ? this.editor.getData() : this._getBusinessData();
    },
    /**
     * 当表单上没有对应组件的时候，可以使用this.data[fieldId] = data赋值。
     * @summary 为组件赋值。
     * @param data{String} .
     * @example
     *  this.form.get("fieldId").setData("test"); //赋文本值
     * @example
     *  //如果无法确定表单上是否有组件，需要判断
     *  if( this.form.get('fieldId') ){ //判断表单是否有无对应组件
     *      this.form.get('fieldId').setData( data );
     *  }else{
     *      this.data['fieldId'] = data;
     *  }
     */
    setData: function(data){
        this._setBusinessData(data);
        if (this.editor) this.editor.setData(data);
    },
    destroy: function(){
        if( this.editor )this.editor.destroy();
    },
    createErrorNode: function(text){
        node = new Element("div", {styles:{
            "margin-top": "0.3em"  
        }});
        var iconNode = new Element("div.ooicon-error", {
            "styles": {
                "width": "20px",
                "height": "1.2em",
                "float": "left",
                "display": "flex",
                "color": "red",
                "align-items": "center",
                "justify-content": "center"
                // "background": "url("+"../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
            }
        }).inject(node);
        var textNode = new Element("div", {
            "styles": {
                "height": "auto",
                "line-height": "1.2em",
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
            if (!this.errNode.isIntoView()) this.errNode.scrollIntoView(false);
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

        this.currentRouteName = routeName;
        var flag = this.form.Macro.exec(this.json.validation.code, this);
        this.currentRouteName = "";

        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    },


        getExcelData: function(){
            return this.getData();
        },
        setExcelData: function(data){
            if( typeOf(data) === "string" )data = data.replace(/&#10;/g,"<br>"); //excel字段换行是 &#10
            this.excelData = data;
            this.setData(data, true);
        },
        validationExcel: function () {
            if (!this.isReadonly()){
                var errorList = this.validationConfigExcel();
                if (errorList.length) return errorList;

                if (!this.json.validation) return [];
                if (!this.json.validation.code) return [];

                var flag = this.form.Macro.exec(this.json.validation.code, this);

                if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
                if (flag.toString() !== "true") {
                    return [flag];
                }
            }
            return [];
        },
        validationConfigExcel: function () {
            var errorList = [];
            if (this.json.validationConfig){
                if (this.json.validationConfig.length){
                    for (var i=0; i<this.json.validationConfig.length; i++) {
                        var flag = this.validationConfigItemExcel(this.json.validationConfig[i]);
                        if ( flag !== true ){
                            errorList.push( flag );
                        }
                    }
                }
            }
            return errorList;
        },
        validationConfigItemExcel: function(data){
            if ( data.status==="all"){
                var n = this._getBusinessData();
                var v = (data.valueType==="value") ? n : n.length;
                switch (data.operateor){
                    case "isnull":
                        if (!v)return data.prompt;
                        break;
                    case "notnull":
                        if (v)return data.prompt;
                        break;
                    case "gt":
                        if (v>data.value)return data.prompt;
                        break;
                    case "lt":
                        if (v<data.value)return data.prompt;
                        break;
                    case "equal":
                        if (v===data.value)return data.prompt;
                        break;
                    case "neq":
                        if (v!==data.value)return data.prompt;
                        break;
                    case "contain":
                        if (v.indexOf(data.value)!==-1) return data.prompt;
                        break;
                    case "notcontain":
                        if (v.indexOf(data.value)===-1)return data.prompt;
                        break;
                }
            }
            return true;
        }
}); 
