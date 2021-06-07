o2.addReady(function () {
    // 是否ip
    var _isIp = function(ip) {
        var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
        return reg.test(ip);
    };
    //修改支持x-token
    var uri = new URI(window.location.href);
    var options = uri.get("data");
    if (options[o2.tokenName]) {
        // 删除
        Cookie.dispose(o2.tokenName);
        // 写入
        var host = window.location.host; // 域名 
        var domain = null;
        if (_isIp(host)) {
            domain = host;
        }else {
            if (host.indexOf(".") > 0) {
                domain = host.substring(host.indexOf(".")); // 上级域名 如 .o2oa.net
            }
        }
        if (domain) {
            Cookie.write(o2.tokenName, options[o2.tokenName], {domain: domain, path:"/"});
        }else {
            Cookie.write(o2.tokenName, options[o2.tokenName]);
        }
    }
    layout.load = function () {
        this.options = uri.get("data");
        if (!this.options.documentId) this.options.documentId = this.options.id
        this.options.name = "cms.Document";
        this.loadDocument(this.options);

        var appNames = "cms.Document";
        layout.openApplication(null, appName, this.options, null);
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
});
