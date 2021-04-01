MWF.xApplication.ForumDocument = MWF.xApplication.ForumDocument || {};
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.require("MWF.widget.ImageClipper", null, false);

MWF.xApplication.ForumDocument.HotLinkForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "660",
        "height": "520",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Forum.LP.setHotPicture,
        "draggable": true,
        "closeAction": true,
        "toMain" : true,
        "documentId" : ""
    },
    _createTableContent: function () {
        this.actions = MWF.Actions.get("x_hotpic_assemble_control");
        this.actions.getHotPic("BBS", this.options.documentId , function( json ){
            if( json.data && json.data.length > 0 ){
                this.isNew = false;
            }else{
                this.isNew = true;
            }
            this.hotPicData = (json.data && json.data.length > 0) ? json.data[0] : {};

            var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
                "<tr>" +
                //"   <td styles='formTableTitle' lable='hotPicture'></td>" +
                "   <td styles='formTableValue' item='hotPictureArea'></td>" +
                "</tr>"+
                "<tr>" +
                //"   <td styles='formTableTitle'></td>" +
                "   <td>"+this.lp.hotLinkDescription+"</td>" +
                "</tr>"+
                //"<tr>" +
                //"   <td styles='formTableTitle'></td>" +
                //"   <td styles='formTableValue' item='hotPictureActionArea'></td>" +
                //"</tr>"
            "</table>";
            this.formTableArea.set("html", html);

            MWF.xDesktop.requireApp("Template", "MForm", function () {
                this.form = new MForm(this.formTableArea, this.data, {
                    style: "cms",
                    isEdited: true,
                    itemTemplate: {
                        hotPicture: { text: this.lp.hotPicture }
                    }
                }, this.app, this.css);
                this.form.load();
                this.createIconNode();
            }.bind(this), true);

        }.bind(this), null, false);
    },
    createIconNode: function(){
        var hotPictureArea = this.formTableArea.getElements("[item='hotPictureArea']")[0];
        //this.iconNode = new Element("img",{
        //    "styles" : this.css.hotIconNode
        //}).inject(hotPictureArea);
        //if (this.hotPicData.pictureBase64){
            //this.iconNode.set("src", this.hotPicData.pictureBase64.substr( 0, 10 ) == 'data:image' ? this.hotPicData.pictureBase64 : ('data:image/png;base64,'+this.hotPicData.pictureBase64));
        //}

        MWF.require("MWF.widget.ImageClipper", function () {
            this.clipper = new MWF.widget.ImageClipper(hotPictureArea, {
                aspectRatio : 2,
                fromFileEnable : true,
                imageUrl : this.hotPicData.picId ?  MWF.xDesktop.getImageSrc( this.hotPicData.picId ) : "",
                reference : this.options.documentId,
                referenceType : "forumDocument"
            });
            this.clipper.load();
        }.bind(this));

        //var hotPictureActionArea = this.formTableArea.getElements("[item='hotPictureActionArea']")[0];
        //var changeIconActionNode = new Element("div", {
        //    "styles": this.css.changeIconActionNode,
        //    "text": this.lp.selectDocPicture
        //}).inject(hotPictureActionArea);
        //changeIconActionNode.addEvent("click", function () {
        //    this.selectDocPicture();
        //}.bind(this));
        //
        //
        //var changeIconActionNode = new Element("div", {
        //    "styles": this.css.changeIconActionNode,
        //    "text": this.lp.selectFilePicture
        //}).inject(hotPictureActionArea);
        //changeIconActionNode.addEvent("click", function () {
        //    this.selectFilePicture();
        //}.bind(this));

    },
    //selectDocPicture: function(){
    //    MWF.xDesktop.requireApp("Forum", "Attachment", null, false);
    //    this.actions.listAttachment( this.options.documentId, function( json ){
    //        this.selector_doc = new MWF.xApplication.Forum.Attachment(document.body, this, this.actions, this.lp, {
    //            //documentId : this.data ? this.data.id : "",
    //            isNew : false,
    //            isEdited : false,
    //            "onUpload" : function( attData ){
    //                this.attachment.attachmentController.addAttachment(attData);
    //                this.attachment.attachmentController.checkActions();
    //            }.bind(this)
    //        })
    //        this.selector_doc.data = json.data || [];
    //        this.selector_doc.loadAttachmentSelecter({
    //            "style": "cms",
    //            "title": "选择本文档图片",
    //            "listStyle": "preview",
    //            "toBase64" : true,
    //            "selectType": "images"
    //        }, function (url, data, base64Code) {
    //            //if (callback)callback(url, data);
    //            this.iconNode.set("src", base64Code || url);
    //            this.hotPicData.pictureBase64 = base64Code || url;
    //        }.bind(this));
    //    }.bind(this) )
    //},
    //selectFilePicture: function () {
    //    var _self = this;
    //    MWF.xDesktop.requireApp("File", "FileSelector", function(){
    //        _self.selector_cloud = new MWF.xApplication.File.FileSelector( document.body ,{
    //            "style" : "default",
    //            "title": "选择云文件图片",
    //            "toBase64" : true,
    //            "listStyle": "preview",
    //            "selectType" : "images",
    //            "onPostSelectAttachment" : function(url, base64Code){
    //                _self.iconNode.set("src", base64Code || url);
    //                _self.hotPicData.pictureBase64 = base64Code || url;
    //            }
    //        });
    //        _self.selector_cloud.load();
    //    }, true);
    //},
    _createBottomContent: function () {

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.lp.setHotPicture
        }).inject(this.formBottomNode);

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));

        if( !this.isNew ){
            this.cancelHotActionNode = new Element("div.formOkActionNode", {
                "styles": this.css.cancelHotPicture,
                "text": this.lp.cancelHotPicture
            }).inject(this.formBottomNode);

            this.cancelHotActionNode.addEvent("click", function (e) {
                this.cancelHotPic(e);
            }.bind(this));
        }

        this.closeActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.closeActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

    },
    cancelHotPic: function(e){
        var _self = this;
        this.app.confirm("warn", e, this.lp.cancelHotPicComfirmTitle, this.lp.cancelHotPicComfirmContent, 350, 120, function(){
            _self._cancelHotPic(_self.data, false);
            this.close();
        }, function(){
            this.close();
        });
    },
    _cancelHotPic :  function(){
        this.actions.removeHotPic( this.hotPicData.id , function( json ) {
            if (json.type == "error") {
                this.app.notice(json.message, "error");
            } else {
                this.formMaskNode.destroy();
                this.formAreaNode.destroy();
                this.app.notice(this.lp.cancelHotLinkSuccess, "success");
            }
        }.bind(this))
    },
    ok: function (e) {
        this.fireEvent("queryOk");
        //var data = this.form.getResult(true, ",", true, false, true);
        var pictureBase64 = this.clipper.getBase64Image();
        if( !pictureBase64 || pictureBase64 == "" ){
            this.app.notice(this.lp.unselectHotPic, "error");
            return;
        }
        this.hotPicData.pictureBase64 = pictureBase64;
        //var pictureBase64 =this.hotPicData.pictureBase64.replace("http://","").split("\/");
        //pictureBase64.shift();
        //this.hotPicData.pictureBase64 = "/"+ pictureBase64.join("/");
        this.clipper.uploadImage( function( json ){

            //this.hotPicData.pictureBase64 = pictureBase64;
            this.hotPicData.infoId = this.options.documentId;
            this.hotPicData.url = MWF.xDesktop.getImageSrc( json.id );
            this.hotPicData.picId = json.id;
            this.hotPicData.title = this.data.title;
            this.hotPicData.application = "BBS";
            this.hotPicData.creator = layout.desktop.session.user.distinguishedName;
            this.hotPicData.summary = this.options.summary;

            this._ok(this.hotPicData, function (json) {
                if (json.type == "error") {
                    this.app.notice(json.message, "error");
                } else {
                    this.formMaskNode.destroy();
                    this.formAreaNode.destroy();
                    this.app.notice(this.lp.setHotLinkSuccess, "success");
                    this.fireEvent("postOk", json.data.id);
                }
            }.bind(this))

        }.bind(this) );
    },
    _ok: function (data, callback) {
        this.actions.saveHotPic( data, function(json){
            if( callback )callback(json);
        }.bind(this));
    },
    _close: function(){
      this.clipper.close();
    }
});

