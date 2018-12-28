o2.addReady(function(){
    COMMON = {
        "setContentPath": function(path){
            COMMON.contentPath = path;
        },
        "JSON": o2.JSON,
        "Browser": Browser,
        "Class": o2.Class,
        "XML": o2.xml,
        "AjaxModule": {
            "load": function(urls, callback, async, reload){
                o2.load(urls, callback, reload, document);
            },
            "loadDom":  function(urls, callback, async, reload){
                o2.load(urls, callback, reload, document);
            },
            "loadCss":  function(urls, callback, async, reload, sourceDoc){
                o2.loadCss(urls, document.body, callback, reload, sourceDoc);
            }
        },
        "Request": Request,
        "typeOf": o2.typeOf
    };
    MWF = o2;
    MWF.getJSON = o2.JSON.get;
    MWF.getJSONP = o2.JSON.getJsonp;
    MWF.defaultPath = o2.session.path;
});
