
/**
 * 微信公众号单点登录
 * 目前这个页面有两个功能 一个是绑定账号 一个是单点登录
 */
layout.addReady(function(){
    (function(layout){
        layout.mobile = true;
        var href = locate.href;
        if (href.indexOf("debugger") !== -1) layout.debugger = true;

        var _loginSuccess =  function (toUri) {
            if (toUri) {
                history.replaceState(null, "page", toUri);
                toUri.toURI().go();
            } else {
                history.replaceState(null, "page", "../x_desktop/appMobile.html?app=process.TaskCenter");
                "appMobile.html?app=process.TaskCenter".toURI().go();
            }
        };
        var _bindWeixin2UserAndGo = function (openid, toUri) {
            o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.bindOpenId(openid, function (json) {
                console.log("绑定成功", json);
                _loginSuccess(toUri);
            }, function (err) {
                console.error(err);
                _loginSuccess(toUri);
            })
        };
        
        var uri = href.toURI();
        var redirect = uri.getData("redirect"); //登录成功后跳转地址
        var code = uri.getData("code"); //微信code
        console.log("code：" + code)
        var type = uri.getData("type"); // bind 是绑定
        console.log("type：" + type)

        o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.loginWithCode(code, function (json) {
            if (json.data && json.data.unbind === true) { // 未绑定
                // 登录并绑定
                if (layout.session && layout.session.user){
                    _bindWeixin2UserAndGo(json.data.mpwxopenId, redirect);
                }else{
                    if (layout.sessionPromise){
                        layout.sessionPromise.then(function(){
                            _bindWeixin2UserAndGo(json.data.mpwxopenId, redirect);
                        },function(){});
                    }
                }
            } else {
                layout.session.user = json.data;
                _loginSuccess(redirect)
            }
        }, function (err) {
            console.error(err);
            //绑定成功
            const box = new Element("div", { "style": "text-align: center;" }).inject($("appContent"));
            new Element("h2", { "text": "单点失败！" }).inject(box);
        });

    })(layout);
});