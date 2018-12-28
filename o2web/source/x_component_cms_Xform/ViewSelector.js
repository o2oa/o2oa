MWF.xDesktop.requireApp("process.Xform", "ViewSelector", null, false);
MWF.xApplication.cms.Xform.ViewSelector = MWF.CMSViewSelector =  new Class({
	Extends: MWF.APPViewSelector //,

    //selectView: function(callback){
    //    if (this.json.viewName){
    //        var viewJson = {
    //            "application": this.json.application || this.form.json.application,
    //            "viewName": this.json.viewName || "",
    //            "isTitle": this.json.isTitle || "yes",
    //            "select": this.json.select || "single"
    //        };
    //        var options = {};
    //        var width = options.width || "700";
    //        var height = options.height || "400";
    //
    //        if (layout.mobile){
    //            var size = document.body.getSize();
    //            width = size.x;
    //            height = size.y;
    //            options.style = "viewmobile";
    //        }
    //        width = width.toInt();
    //        height = height.toInt();
    //
    //        var size = this.form.app.content.getSize();
    //        var x = (size.x-width)/2;
    //        var y = (size.y-height)/2;
    //        if (x<0) x = 0;
    //        if (y<0) y = 0;
    //        if (layout.mobile){
    //            x = 20;
    //            y = 0;
    //        }
    //
    //        var _self = this;
    //        MWF.require("MWF.xDesktop.Dialog", function(){
    //            var dlg = new MWF.xDesktop.Dialog({
    //                "title": this.json.title || "select view",
    //                "style": options.style || "view",
    //                "top": y,
    //                "left": x-20,
    //                "fromTop":y,
    //                "fromLeft": x-20,
    //                "width": width,
    //                "height": height,
    //                "html": "<div></div>",
    //                "maskNode": this.form.app.content,
    //                "container": this.form.app.content,
    //                "buttonList": [
    //                    {
    //                        "text": MWF.LP.process.button.ok,
    //                        "action": function(){
    //                            //if (callback) callback(_self.view.selectedItems);
    //                            if (callback) callback(_self.view.getData());
    //                            this.close();
    //                        }
    //                    },
    //                    {
    //                        "text": MWF.LP.process.button.cancel,
    //                        "action": function(){this.close();}
    //                    }
    //                ]
    //            });
    //            dlg.show();
    //
    //            if (layout.mobile){
    //                var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
    //                var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
    //                if (backAction) backAction.addEvent("click", function(e){
    //                    dlg.close();
    //                }.bind(this));
    //                if (okAction) okAction.addEvent("click", function(e){
    //                    //if (callback) callback(this.view.selectedItems);
    //                    if (callback) callback(this.view.getData());
    //                    dlg.close();
    //                }.bind(this));
    //            }
    //
    //            MWF.xDesktop.requireApp("cms.Xform", "widget.View", function(){
    //                this.view = new MWF.xApplication.cms.Xform.widget.View(dlg.content.getFirst(), viewJson, {"style": "select"});
    //            }.bind(this));
    //        }.bind(this));
    //    }
    //}
	
}); 