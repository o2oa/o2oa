MWF.xApplication.IM = MWF.xApplication.IM || {};
MWF.xApplication.IM.contextRoot = "x_component_IM";
MWF.xApplication.IM.Actions = MWF.xApplication.IM.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
// MWF.xApplication.IM.Actions.RestActions = new Class({
// 	initialize: function(){
// 		this.controlAction = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_control", MWF.xApplication.IM.contextRoot);
//         this.socketAction = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_collaboration_assemble_websocket", MWF.xApplication.IM.contextRoot);
//
//
//         //this.expressAction = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_express", MWF.xApplication.IM.contextRoot);
// 	},
//
//     listTopCompany: function(success, failure, async){
//         this.controlAction.invoke({"name": "listTopCompany","async": async,	"success": success,	"failure": failure});
//     },
//     listCompanySub: function(success, failure, id, async){
//         this.controlAction.invoke({"name": "listCompanySub","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
//     },
//     listDepartmentSub: function(success, failure, id, async){
//         this.controlAction.invoke({"name": "listDepartmentSub","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
//     },
//     getPersonComplex: function(success, failure, id, async){
//         this.controlAction.invoke({"name": "getPersonComplex","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
//     },
//     getPerson: function(success, failure, id, async){
//         this.controlAction.invoke({"name": "getPerson","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
//     },
//
//     listPersonByKey: function(success, failure, key, async){
//         this.controlAction.invoke({"name": "listPersonByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
//     },
//
//     personOnline: function(success, failure, person, async){
//         this.socketAction.invoke({"name": "personOnline","async": async, "parameter": {"person": person},	"success": success,	"failure": failure});
//     },
//
//     listChat: function(success, failure, person, async){
//         this.socketAction.invoke({"name": "listChat","async": async, "success": success, "failure": failure});
//     }
//
// });

MWF.xApplication.IM = MWF.xApplication.IM || {};
MWF.xApplication.IM.Actions = MWF.xApplication.IM.Actions || {};
MWF.xDesktop.requireApp("Org", "Actions.RestActions", null, false);
MWF.xApplication.IM.Actions.RestActions = new Class({
    Extends: MWF.xApplication.Org.Actions.RestActions,
    initialize: function(){
        this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_control", "x_component_Org");
        this.socketAction = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_collaboration_assemble_websocket", MWF.xApplication.IM.contextRoot);
    },
    personOnline: function(success, failure, person, async){
        this.socketAction.invoke({"name": "personOnline","async": async, "parameter": {"person": person},	"success": success,	"failure": failure});
    },

    listChat: function(success, failure, person, async){
        this.socketAction.invoke({"name": "listChat","async": async, "success": success, "failure": failure});
    }
});