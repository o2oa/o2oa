MWF.xDesktop.requireApp("process.Xform", "Htmleditor", null, false);
MWF.xApplication.cms.Xform.Htmleditor = MWF.CMSHtmleditor =  new Class({
	Extends: MWF.APPHtmleditor,

	_loadUserInterface: function(){
		this.node.empty();
        if (this.readonly){
            this.node.set("html", this._getBusinessData());
            this.node.setStyles({
                "-webkit-user-select": "text",
                "-moz-user-select": "text"
            });
            if( layout.mobile ){
                this.node.getElements("img").each( function( img ){
                    //if( img.height )img.erase("height");
                    img.setStyles({
                        "height": "auto",
                        "max-width" : "100%"
                    });
                }.bind(this))
            }
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

            if (this.form.json.mode=="Mobile"){
                if (!editorConfig.toolbar && !editorConfig.toolbarGroups){
                    editorConfig.toolbar = [
                        { name: 'paragraph',   items: [ 'Bold', 'Italic', "-" , 'TextColor', "BGColor", 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', "-", 'Undo', 'Redo' ] },
                        { name: 'basicstyles', items: [ 'Styles', 'FontSize']}
                    ];
                }
            }
            // CKEDITOR.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/";
            // CKEDITOR.plugins.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/plugins/";

            //editorConfig.filebrowserCurrentDocumentImage = function( e, callback ){
            //    _self.selectCurrentDocumentImage( e, callback );
            //};

            //editorConfig.filebrowserFilesImage = function( e, callback ){
            //    _self.selectCloudFilesImage( e, callback );
            //};

            editorConfig.localImageMaxWidth = 800;
            editorConfig.reference = this.form.businessData.document.id;
            editorConfig.referenceType = "cmsDocument";

            if( editorConfig.skin )editorConfig.skin = "moono-lisa";
            this.editor = CKEDITOR.replace(editorDiv, editorConfig);
            this._loadEvents();

            //this.editor.on("loaded", function(){
            //    this._loadEvents();
            //}.bind(this));

            //this.setData(data)

            this.editor.on("change", function(){
                this._setBusinessData(this.getData());
            }.bind(this));
            //    this._loadEvents();
        }.bind(this));
    },
    getText : function(){
        return this.editor.document.getBody().getText();
    },
    getImages : function(){
        var result = [];
        var imgaes = this.editor.document.find("img");
        if( imgaes ){
            for( var i=0; i< imgaes.$.length; i++ ){
                result.push( imgaes.getItem(i).$ );
            }
        }
        return result;
    },
    getImageIds : function(){
        var result = [];
        var images = this.getImages();
        for( var i=0; i<images.length; i++ ){
            var img = images[i];
            if( img.getAttribute("data-id") ){
                result.push( img.getAttribute("data-id") )
            }
        }
        return result;
    },
    _loadStyles: function(){
        if (this.json.styles) this.node.setStyles(this.json.styles);
        this.node.setStyle("overflow","hidden");
    },
    //selectCurrentDocumentImage : function( e, callback ){
    //    var _self = this;
    //    MWF.xDesktop.requireApp("cms.Xform", "Attachment", function(){
    //        //_self.form.app.content
    //        _self.selector_doc = new MWF.xApplication.cms.Xform.Attachment( document.body , {}, _self.form, {})
    //        _self.selector_doc.loadAttachmentSelecter({
    //            "style" : "cms",
    //            "title": "选择本文档图片",
    //            "listStyle": "preview",
    //            "toBase64" : true,
    //            "selectType" : "images"
    //        }, function(url, data, base64Code){
    //            if(callback)callback(url, base64Code, data);
    //        });
    //
    //    }, true);
    //
    //},
    //selectCloudFilesImage : function( e, callback ){
    //    var _self = this;
    //    MWF.xDesktop.requireApp("File", "FileSelector", function(){
    //        //_self.form.app.content
    //        _self.selector_cloud = new MWF.xApplication.File.FileSelector( document.body ,{
    //            "style" : "default",
    //            "title": "选择云文件图片",
    //            "toBase64" : true,
    //            "listStyle": "preview",
    //            "selectType" : "images",
    //            "onPostSelectAttachment" : function(url, base64Code){
    //                if(callback)callback(url, base64Code);
    //            }
    //        });
    //        _self.selector_cloud.load();
    //    }, true);
    //
    //},
    validationConfigItem: function(routeName, data){
        var flag = (data.status=="all") ? true: (routeName == "publish");
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
    }
}); 