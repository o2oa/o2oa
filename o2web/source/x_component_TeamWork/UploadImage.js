MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.require("MWF.widget.ImageClipper", null, false);

MWF.xApplication.TeamWork.UploadImage = new Class({
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
        "title": "上传封面",
        "draggable": true,
        "closeAction": true,
        "toMain" : true,
        "documentId" : ""
    },
    initialize: function (explorer, data, options, para) {
        this.setOptions(options);
        this.explorer = explorer;
        if( para ){
            if( this.options.relativeToApp ){
                this.app = para.app || this.explorer.app;
                this.container = para.container || this.app.content;
                this.lp = para.lp || this.explorer.lp || this.app.lp;
                this.css = para.css || this.explorer.css || this.app.css;
                this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = para.container;
                this.lp = para.lp || this.explorer.lp;
                this.css = para.css || this.explorer.css;
                this.actions = para.actions || this.explorer.actions;
            }
        }else{
            if( this.options.relativeToApp ){
                this.app = this.explorer.app;
                this.container = this.app.content;
                this.lp = this.explorer.lp || this.app.lp;
                this.css = this.explorer.css || this.app.css;
                this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = window.document.body;
                this.lp = this.explorer.lp;
                this.css = this.explorer.css;
                this.actions = this.explorer.actions;
            }
        }
        this.data = data || {};

        this.css = {};
        this.cssPath = "/x_component_TeamWork/$UploadImage/"+this.options.style+"/css.wcss";

        this.load();
        this.lp = this.app.lp.uploadImage
    },

    _createTableContent: function () {
        this.actions = MWF.Actions.get("x_file_assemble_control");
        // this.actions.getHotPic("CMS", this.options.documentId , function( json ){
        //     if( json.data && json.data.length > 0 ){
        //         this.isNew = false;
        //     }else{
        //         this.isNew = true;
        //     }
        //     this.hotPicData = (json.data && json.data.length > 0) ? json.data[0] : {};

            var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
                "<tr>" +
                //"   <td styles='formTableTitle' lable='hotPicture'></td>" +
                "   <td styles='formTableValue' item='hotPictureArea'></td>" +
                "</tr>"+
                "<tr>" +
                //"   <td styles='formTableTitle'></td>" +
                "   <td></td>" +
                "</tr>"+
                //"<tr>" +
                //"   <td styles='formTableTitle'></td>" +
                //"   <td styles='formTableValue' item='hotPictureActionArea'></td>" +
                //"</tr>"
                "</table>";
            this.formTableArea.set("html", html);

            MWF.xDesktop.requireApp("Template", "MForm", function () {
                this.form = new MForm(this.formTableArea, this.data, {
                    style: "default",
                    isEdited: true,
                    itemTemplate: {
                        hotPicture: { text: this.lp.hotPicture }
                    }
                }, this.app, this.css);
                this.form.load();
                this.createIconNode();
            }.bind(this), true);

        // }.bind(this), null, false);
    },
    createIconNode: function(){
        var hotPictureArea = this.formTableArea.getElements("[item='hotPictureArea']")[0];
        //this.iconNode = new Element("img",{
        //    "styles" : this.css.iconNode
        //}).inject(hotPictureArea);
        //if (this.hotPicData.pictureBase64){
        //    this.iconNode.set("src", this.hotPicData.pictureBase64);
        //}
        MWF.require("MWF.widget.ImageClipper", function () {
            this.clipper = new MWF.widget.ImageClipper(hotPictureArea, {
                aspectRatio : 2,
                fromFileEnable : false,
                imageUrl : "",
                reference : this.options.documentId,
                referenceType : "teamworkProject"
            });
            this.clipper.load();
        }.bind(this));

    },

    _createBottomContent: function () {

        this.closeActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.closeBtn
        }).inject(this.formBottomNode);

        this.closeActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));


        // if( !this.isNew ){
        //     this.cancelHotActionNode = new Element("div.formOkActionNode", {
        //         "styles": this.css.cancelHotPicture,
        //         "text": this.lp.cancelHotPicture
        //     }).inject(this.formBottomNode);
        //
        //     this.cancelHotActionNode.addEvent("click", function (e) {
        //         this.cancelHotPic(e);
        //     }.bind(this));
        // }

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.lp.okBtn
        }).inject(this.formBottomNode);

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));

    },

    ok: function (e) {
        this.fireEvent("queryOk");

        var pictureBase64 = this.clipper.getBase64Image();
        if( !pictureBase64 || pictureBase64 == "" ){
            this.app.notice(this.lp.unselectHotPic, "error");
            return;
        }
        this.clipper.uploadImage( function( json ){
            this.fireEvent("postOk", json.data.id);
            this.cancel();
        }.bind(this) );

    },
    _close: function(){
        this.clipper.close();
    }
});
