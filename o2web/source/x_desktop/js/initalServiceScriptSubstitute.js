var resources = {
        "getEntityManagerContainer": function(){ return {}; },
        "getContext": function(){ return {}; },
        "getApplications": function(){ return {}; },
        "getOrganization": function(){
                return {
                        group: function(){ return {}; },
                        identity: function(){ return {}; },
                        person: function(){ return {}; },
                        personAttribute: function(){ return {}; },
                        role: function(){ return {}; },
                        unit: function(){ return {}; },
                        unitAttribute: function(){ return {}; },
                        unitDuty: function(){ return {}; }
                };
        },
        "getWebservicesClient": function(){ return {}; }
};
var effectivePerson = this.effectivePerson = {};