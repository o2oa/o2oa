this.define("deleteWork", function(){
    var that = this;
    this.confirm("infor", "注销文件确认", "您正在注销此文档，注销后文档无法找回，请您确认要注销此文件吗？", 380, 120, function () {
        // MWF.require("MWF.widget.Mask", function () {
        var _self = that.form.getApp().appForm;
        // _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
        // _self.mask.loadNode(_self.app.content);

        _self.fireEvent("beforeDelete");
        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeDelete");

        _self.doDeleteWork(function () {
            _self.fireEvent("afterDelete");
            if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterDelete");
            // if (_self.mask) { _self.mask.hide(); _self.mask = null; }
            _self.app.notice(MWF.xApplication.process.Xform.LP.workDelete + ": “" + _self.businessData.work.title + "”", "success");
            _self.app.close();
            this.close();
        }.bind(this), function (xhr, text, error) {
            var errorText = error + ":" + text;
            if (xhr) errorText = xhr.responseText;
            _self.app.notice("request json error: " + errorText, "error", dlg.node);
            // if (_self.mask) { _self.mask.hide(); _self.mask = null; }
        }.bind(this));
        // }.bind(this));
    }, function () {
        this.close();
    }, null);
})