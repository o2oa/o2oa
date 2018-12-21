MWF.xApplication.OnlineMeeting = MWF.xApplication.OnlineMeeting || {};
MWF.xApplication.OnlineMeeting.Actions = MWF.xApplication.OnlineMeeting.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.OnlineMeeting.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_meeting_assemble_control", "x_component_OnlineMeeting");
        this.actionAuth = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_authentication", "x_component_OnlineMeeting");
	},
    getOpenMeeting: function(success, failure, async){
        this.action.invoke({"name": "getOpenMeeting","async": async, "success": function(json){
            var data = json.data;
            var par = json.data.httpProtocol || layout.config.app_protocol;
            par = par+"://";
            par = (data.host) ? (par+data.host) : (par+window.location.host);
            par = (!data.port || data.port==80) ? this.roomHost : par+":"+data.port;
            this.roomHost = par;

            if (success) success(json);
        }.bind(this),	"failure": failure});
    },
    listRoom: function(success, failure, async){
        this.action.invoke({"name": "listRoom","async": async, "success": success,	"failure": failure});
    },

    addRoom: function(data, success, failure, async){
        this.action.invoke({"name": "addRoom", "async": async, "data": data, "success": success, "failure": failure});
    },

    deleteRoom: function(id, success, failure, async){
        this.action.invoke({"name": "removeRoom", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    getLoginUri: function(data){
        this.actionAuth.getActions();
        var action = this.actionAuth.actions["login"];
        var uri = this.actionAuth.address+action.uri;
        //
        //var par = "http://";
        //par = (data.host) ? (par+data.host) : (par+window.location.host);
        //this.roomHost = par;
        debugger;
        par = this.roomHost+"/openmeetings/signin?oauthid="+data.oauth2Id;
        return uri.replace(/{uri}/, escape(par));
    },
    getRoomUri: function(data){
        var par = this.roomHost+this.actionAuth.actions["room"].uri;
        debugger;
        return par.replace(/{id}/, data.id);
    }
});