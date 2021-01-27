layout.addReady(function () {
    //修改支持x-token
    (function(layout){
        var uri = new URI(window.location.href);
        var options = uri.get("data");
        if (options["x-token"]) {
            Cookie.write("x-token", options["x-token"]);
        }
        var _load = function () {
            debugger;
            this.options = uri.get("data");
            if (!this.options.documentId) this.options.documentId = this.options.id;
            this.options.name = "cms.Document";
            // this.loadDocument(this.options);

            layout.openApplication(null, "cms.Document", this.options, null);
        };

        if (layout.session && layout.session.user){
            _load();
        }else{
            if (layout.sessionPromise){
                layout.sessionPromise.then(function(){
                    _load();
                },function(){});
            }
        }
    })(layout);
});
