window.layout = window.layout || {};
layout.desktop = layout;
layout.session = layout.session || {};
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
        if (config.center){
            if (typeOf(config.center)==="object"){
                getServiceAddressConfigObject(callback, config.center);
            }else if (typeOf(config.center)==="array"){
                getServiceAddressConfigArray(config, callback);
            }
        }else{
            getServiceAddressConfigObject(callback, {
                "host": window.location.hostname,
                "port": window.location.port || 80
            });
        }

    }


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

        var serviceAddressList = json.data;
        if (layout.config.proxyApplicationEnable){
            Object.keys(serviceAddressList).forEach(function(k){
                if (k!=="x_message_assemble_communicate") serviceAddressList[k].port = window.location.port || 80;
            });
        }
        window.layout.serviceAddressList = serviceAddressList;
        window.layout.centerServer = center;

        // var serviceAddressList = json.data;
        // var addressObj = serviceAddressList["x_organization_assemble_authentication"];
        // var address = protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+"/z_sso_control/jaxrs/sso/smplogin";

        if (callback) callback();
    }.bind(this));
}
