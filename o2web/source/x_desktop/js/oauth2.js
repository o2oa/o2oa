layout.addReady(function(){
    (function(layout){
        var uri = new URI(window.location.href);

        var _load = function(){
            var host = o2.Actions.getHost("x_organization_assemble_authentication");
            var url = host+"/x_organization_assemble_authentication/jaxrs/oauth/auth";
            var toUri = new URI(o2.filterUrl(url));
            toUri.setData(uri.getData());
            toUri.go();
        };
        _load();
    })(layout);
});
