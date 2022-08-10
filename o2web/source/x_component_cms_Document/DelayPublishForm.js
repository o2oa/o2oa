MWF.xApplication.cms.Document = MWF.xApplication.cms.Document || {};
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);

MWF.xApplication.cms.Document.DelayPublishForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "580",
        "height": "220",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.cms.Document.LP.regularPublish, //"设置热点"
        "draggable": true,
        "closeAction": true,
        "publishTime": ""
    },
    _createTableContent: function () {
        this.formAreaNode.setStyle("z-index", 1002);
        this.formMaskNode.setStyle("z-index", 1002);
        this.formTableContainer.setStyles({
            "margin":"40px 40px 0px 40px"
        });
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle' style='font-size: 14px;width: 80px;'>"+this.lp.publishTime+":</td>" +
            "   <td styles='formTableValue' item='publishTime'></td>" +
            "</tr>"+
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "meeting",
                isEdited: true,
                itemTemplate: {
                    publishTime: {
                        text: this.lp.publishTime,
                        tType: "datetime",
                        notEmpty: true,
                        value: this.options.publishTime || "",
                        attr: {
                            "readonly":true
                        },
                        calendarOptions : {
                            "secondEnable": true,
                            "format": "db",
                           "onShow": function () {
                               this.container.setStyle("z-index", 1003 );
                           }
                        }
                    }
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },
    _createBottomContent: function () {

        this.closeActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.closeActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.lp.publish
        }).inject(this.formBottomNode);

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));


    },
    ok: function (e) {
        this.fireEvent("queryOk");

        var result = this.form.getResult(true, null);
        if( !result ){
            this.app.notice(this.lp.inputPublishTime, "error");
            return;
        }else if( new Date( result.publishTime ) <= new Date() ){
            this.app.notice(this.lp.inputPublishTime2, "error");
            return;
        }
        (this.formMaskNode || this.formMarkNode).destroy();
        this.formAreaNode.destroy();
        // this.app.notice(this.lp.setHotLinkSuccess, "success");
        this.fireEvent("postOk", result.publishTime);

    }
});
