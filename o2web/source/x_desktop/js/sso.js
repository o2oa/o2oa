window.layout = window.layout || {};
function getServiceAddress(config, callback){
    window.layout.config = config;
    if (config.configMapping && (config.configMapping[window.location.host] || config.configMapping[window.location.hostname])) {
        var mapping = config.configMapping[window.location.host] || config.configMapping[window.location.hostname];
        if (mapping.servers){
            window.layout.serviceAddressList = mapping.servers;
            if (mapping.center) center = (o2.typeOf(mapping.center)==="array") ? mapping.center[0] : mapping.center;
            window.layout.centerServer = center;
            if (callback) callback();
        }else{
            if (mapping.center) layout.config.center = (o2.typeOf(mapping.center)==="array") ? mapping.center : [mapping.center];
            getServiceAddressConfigArray(layout.config, callback);
        }
    }else{
        if (typeOf(config.center)=="object"){
            getServiceAddressConfigObject(callback);
        }else if (typeOf(config.center)=="array"){
            // var center = chooseCenter(config);
            // if (center){
            //     getServiceAddressConfigObject(callback, center);
            // }else{
            getServiceAddressConfigArray(config, callback);
            // }
        }
    }


}
function chooseCenter(config){
    var host = window.location.host;
    var center = null;
    for (var i=0; i<config.center.length; i++){
        var ct = config.center[i];
        if (ct.webHost==host){
            center = ct;
            break;
        }
    }
    return center;
}
function getServiceAddressConfigArray(config, callback) {
    var requests = [];
    config.center.each(function(center){
        requests.push(
            getServiceAddressConfigObject(function(){
                requests.each(function(res){
                    if (res.res && res.res.isRunning && res.res.isRunning()){res.res.cancel();}
                });
                if (callback) callback();
            }.bind(this), center)
        );
    }.bind(this));
}
function getServiceAddressConfigObject(callback, center){
    var centerConfig = center;
    var host = centerConfig.host || window.location.hostname;
    var port = centerConfig.port;
    var uri = "";
    var locate = window.location;
    var protocol = locate.protocol;
    if (!port || port=="80"){
        uri = protocol+"//"+host+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
    }else{
        uri = protocol+"//"+host+":"+port+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
    }
    var currenthost = window.location.hostname;
    uri = uri.replace(/{source}/g, currenthost);
    //var uri = "http://"+layout.config.center+"/x_program_center/jaxrs/distribute/assemble";
    return MWF.restful("get", uri, null, function(json){
        window.layout.serviceAddressList = json.data;
        window.layout.centerServer = center;

        // var serviceAddressList = json.data;
        // var addressObj = serviceAddressList["x_organization_assemble_authentication"];
        // var address = protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+"/z_sso_control/jaxrs/sso/smplogin";

        if (callback) callback();
    }.bind(this));
}
