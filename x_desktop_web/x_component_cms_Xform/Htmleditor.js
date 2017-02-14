MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.xApplication.cms.Xform.Htmleditor = MWF.CMSHtmleditor =  new Class({
	Extends: MWF.CMS$Module,

    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
    },

	_loadUserInterface: function(){
		this.node.empty();
        if (this.readonly){
            this.node.set("html", this._getBusinessData());
            this.node.setStyles({
                "overflow" : "hidden",
                "-webkit-user-select": "text",
                "-moz-user-select": "text"
            });
        }else{
            var config = Object.clone(this.json.editorProperties);
            if (this.json.config){
                if (this.json.config.code){
                    var obj = MWF.CMSMacro.exec(this.json.config.code, this);
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
        _self = this;
        COMMON.AjaxModule.load("ckeditor", function(){
            var editorDiv = new Element("div").inject(this.node);
            var htmlData = this._getBusinessData();
            if (htmlData){
                editorDiv.set("html", htmlData);
            }else if (this.json.templateCode){
                editorDiv.set("html", this.json.templateCode);
            }

            var height = this.node.getSize().y;
            var editorConfig = config || {};

            if (this.form.json.mode=="Mobile"){
                if (!editorConfig.toolbar && !editorConfig.toolbarGroups){
                    editorConfig.toolbar = [
                        { name: 'paragraph',   items: [ 'Bold', 'Italic', "-" , 'TextColor', "BGColor", 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', "-", 'Undo', 'Redo' ] },
                        { name: 'basicstyles', items: [ 'Styles', 'FontSize']}
                    ];
                }
            }

            editorConfig.filebrowserCurrentDocumentImage = function( e, callback ){
                _self.selectCurrentDocumentImage( e, callback );
            }

            editorConfig.filebrowserFilesImage = function( e, callback ){
                _self.selectCloudFilesImage( e, callback );
            }

            // CKEDITOR.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/";
            // CKEDITOR.plugins.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/plugins/";
            this.editor = CKEDITOR.replace(editorDiv, editorConfig);

            //this.setData(data)

            this.editor.on("change", function(){
                this._setBusinessData(this.getData());
            }.bind(this));
            //    this._loadEvents();
        }.bind(this));
    },
    _loadEvents: function(editorConfig){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                this.editor.on(key, function(event){
                    return this.form.CMSMacro.fire(e.code, this, event);
                }.bind(this), this);
            }
        }.bind(this));

    },
    _loadValue: function(){
        var data = this._getBusinessData();
    },
    resetData: function(){
        this.setData(this._getBusinessData());
    },
    getData: function(){
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
                "background": "url("+"/x_component_cms_Xform/$Form/default/icon/error.png) center center no-repeat"
            }
        }).inject(node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "20px",
                "margin-left": "20px",
                "color": "red"
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
    validation: function(){
        if (!this.json.validation) return true;
        if (!this.json.validation.code) return true;
        var flag = this.form.CMSMacro.exec(this.json.validation.code, this);
        if (!flag) flag = MWF.xApplication.cms.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    },
    selectCurrentDocumentImage : function( e, callback ){
        var _self = this;
        MWF.xDesktop.requireApp("cms.Xform", "Attachment", function(){
            //_self.form.app.content
            _self.selector_doc = new MWF.xApplication.cms.Xform.Attachment( document.body , {}, _self.form, {})
            _self.selector_doc.loadAttachmentSelecter({
                "style" : "cms",
                "title": "选择本文档图片",
                "listStyle": "preview",
                "toBase64" : true,
                "selectType" : "images"
            }, function(url, data, base64Code){
                if(callback)callback(url, base64Code, data);
            });

        }, true);

    },
    selectCloudFilesImage : function( e, callback ){
        var _self = this;
        MWF.xDesktop.requireApp("File", "FileSelector", function(){
            //_self.form.app.content
            _self.selector_cloud = new MWF.xApplication.File.FileSelector( document.body ,{
                "style" : "default",
                "title": "选择云文件图片",
                "toBase64" : true,
                "listStyle": "preview",
                "selectType" : "images",
                "onPostSelectAttachment" : function(url, base64Code){
                    if(callback)callback(url, base64Code);
                }
            });
            _self.selector_cloud.load();
        }, true);

    }
});

