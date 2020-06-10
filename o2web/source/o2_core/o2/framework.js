layout.addReady(function(){
    MWF.require("MWF.xScript.Environment", null, false);
    MWF.require("MWF.xScript.PageEnvironment", null, false);

    var page = {
        "businessData": {},
        "json": {
            "application": ""
        },
        "options": {}
    };
    var environment = {
        "form": page,
        "forms": page.forms,
        "all": page.all,
        "data": page.businessData.data,
        "status": page.businessData.status,
        "pageInfor": page.businessData.pageInfor,
        "target": null,
        "event": null
    };
    o2.env = new MWF.xScript.PageEnvironment(environment);
});
