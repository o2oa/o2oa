
/**
 * 微信公众号单点登录
 * 目前这个页面有两个功能 一个是绑定账号 一个是单点登录
 */
layout.addReady(function(){
    (function(layout){
        layout.mobile = true;
        var href = locate.href;
        if (href.indexOf("debugger") != -1) layout.debugger = true;
 
        var _bindWeixin2User = function () {
            var uri = href.toURI();
            var code = uri.getData("code"); //微信code
            if (code) {
                // layout.showLoading();
                o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.bindWithCode(code, function (json) {
                    // layout.hideLoading();
                    //绑定成功
                    var box = new Element("div", { "style": "text-align: center;" }).inject($("appContent"));
                    new Element("h2", { "text": "绑定成功！" }).inject(box);
                }, function (err) {
                    // layout.hideLoading();
                    console.log(err);
                    // layout.notice('绑定账号失败', 'error');
                })
            } else {
                // layout.hideLoading();
                console.log('没有传入微信code无法绑定');
                // layout.notice('没有传入微信code无法绑定', 'error');
            }
        };
        
        var uri = href.toURI();
        var redirect = uri.getData("redirect"); //登录成功后跳转地址
        var code = uri.getData("code"); //微信code
        console.log("code：" + code)
        var type = uri.getData("type"); // bind 是绑定
        console.log("type：" + type)
        if (type && type === "bind") { // 绑定要登录
            if (layout.session && layout.session.user){
                _bindWeixin2User();
            }else{
                if (layout.sessionPromise){
                    layout.sessionPromise.then(function(){
                        _bindWeixin2User();
                    },function(){});
                }
            }
        } else { //code 单点登录
            // layout.showLoading();
            o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.loginWithCode(code, function (json) {
                // layout.hideLoading();
                layout.session.user = json.data;
                if (redirect) {
                    history.replaceState(null, "page", redirect);
                    redirect.toURI().go();
                } else {
                    history.replaceState(null, "page", "../x_desktop/appMobile.html?app=process.TaskCenter");
                    "appMobile.html?app=process.TaskCenter".toURI().go();
                }
            }, function (err) {
                // layout.hideLoading();
                console.log(err)
                // layout.notice('单点登录失败，请先绑定用户', 'error');
            });
        }

    })(layout);
});