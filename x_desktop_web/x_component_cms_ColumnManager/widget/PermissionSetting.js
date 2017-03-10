MWF.xApplication.cms.ColumnManager = MWF.xApplication.cms.ColumnManager || {};
MWF.xApplication.cms.ColumnManager.PermissionSetting = new Class({
    Implements: [Options],
    options : {
        objectId : "", //对象或分类的ID
        objectType : "APPINFO", //CATEGORY
        permission : "VIEW" //PUBLISH
    },
    initialize: function(app, lp, node, options){
        this.app = app;
        this.node = $(node);
        this.lp = lp;
        this.setOptions( options );

        this.data = [];
        this.personList = [];
        this.departmentList = [];
        this.companyList = [];

        //this.lp = this.app.lp.application.availableSetting;
    },
    load: function(){
        this.listData( function( json ){
            json.data = json.data || [];
            this.data = json.data;
            json.data.each(function( d ){
                if(d.usedObjectType == "USER" ){
                    this.personList.push( d.usedObjectName )
                }else if(d.usedObjectType == "DEPARTMENT"){
                    this.departmentList.push( d.usedObjectName )
                }else{
                    this.companyList.push( d.usedObjectName )
                }
            }.bind(this));
            this.createNode();
        }.bind(this));
    },
    createNode: function(){
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();

        this.titleNode = new Element("div", {
            "styles": this.app.css.availableTitleNode,
            "text": this.lp.title
        }).inject(this.node);

        this.contentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);
        this.itemsContentNode = new Element("div", {"styles": this.app.css.availableItemsContentNode}).inject(this.contentNode);
        this.actionAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);

        var changeIdentityList = new Element("div", {
            "styles": this.app.css.selectButtonStyle,
            "text": this.lp.setPerson
        }).inject(this.actionAreaNode);
        changeIdentityList.addEvent("click", function(){
            this.changeIdentitys();
        }.bind(this));

        var changeDepartmentList = new Element("div", {
            "styles": this.app.css.selectButtonStyle,
            "text": this.lp.setDepartment
        }).inject(this.actionAreaNode);
        changeDepartmentList.addEvent("click", function(){
            this.changeDepartments();
        }.bind(this));

        var changeCompanyList = new Element("div", {
            "styles": this.app.css.selectButtonStyle,
            "text":this.lp.setCompany
        }).inject(this.actionAreaNode);
        changeCompanyList.addEvent("click", function(){
            this.changeCompanys();
        }.bind(this));

        this.setItems();
    },
    setItems: function(){
        var explorer = {
            "actions": this.personActions,
            "app": {
                "lp": this.app.lp
            }
        }
        if (this.personList){
            this.personList.each(function(name){
                if (name) new MWF.widget.Person({"name": name}, this.itemsContentNode, explorer, false, null, {"style": "application"});
            }.bind(this));
        }
        if (this.departmentList){
            this.departmentList.each(function(name){
                if (name) new MWF.widget.Department({"name": name}, this.itemsContentNode, explorer, false, null, {"style": "application"});
            }.bind(this));
        }
        if (this.companyList){
            this.companyList.each(function(name){
                if (name) new MWF.widget.Company({"name": name}, this.itemsContentNode, explorer, false, null, {"style": "application"});
            }.bind(this));
        }
    },

    changeIdentitys: function(){
        var explorer = {
            "actions": this.personActions,
            "app": {
                "lp": this.app.lp
            }
        };
        var options = {
            "type": "person",
            "title": this.lp.setPerson,
            "names": this.personList || [],
            "onComplete": function(items){
                var personList = [];

                items.each(function(item){
                    personList.push(item.data.name);
                }.bind(this));

                personList.each(function(item){
                    if( !this.personList.contains( item ) ){
                        var object = {
                            "objectType": this.options.objectType,
                            "objectId": this.options.objectId,
                            "usedObjectType": "USER",
                            "usedObjectCode": item,
                            "usedObjectName": item,
                            "permission" : this.options.permission
                        };
                        this.saveData(object, function(json){
                            object.id = json.data.id;
                            this.data.push( object );
                        }.bind(this), null, false);
                    }
                }.bind(this));

                this.personList.each(function(item){
                    if( !personList.contains( item ) ){
                        var ad = null;
                        var id = "";
                        this.data.each(function(data){
                            if( data.usedObjectName == item ){
                                ad = data;
                                id = data.id;
                            }
                        }.bind(this));
                        this.removeData(id, function(json){
                            this.data.erase( ad )
                        }.bind(this));
                    }
                }.bind(this))

                this.personList = personList;
                this.itemsContentNode.empty();
                this.setItems();

                this.app.notice(  this.lp.setIdentitySuccess , "success");
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.app.content, options);
    },
    changeDepartments: function(){
        var explorer = {
            "actions": this.personActions,
            "app": {
                "lp": this.app.lp
            }
        };
        var options = {
            "type": "department",
            "title": this.lp.setDepartment,
            "names": this.departmentList || [],
            "onComplete": function(items){
                var departmentList = [];

                items.each(function(item){
                    departmentList.push(item.data.name);
                }.bind(this));

                departmentList.each(function(item){
                    if( !this.departmentList.contains( item ) ){
                        var object = {
                            "objectType": this.options.objectType,
                            "objectId": this.options.objectId,
                            "usedObjectType": "DEPARTMENT",
                            "usedObjectCode": item,
                            "usedObjectName": item,
                            "permission" : this.options.permission
                        };
                        this.saveData(object, function(json){
                            object.id = json.data.id;
                            this.data.push( object );
                        }.bind(this));
                    }
                }.bind(this));

                this.departmentList.each(function(item){
                    if( !departmentList.contains( item ) ){
                        var ad = null;
                        var id = "";
                        this.data.each(function(data){
                            if( data.usedObjectName == item ){
                                ad = data;
                                id = data.id;
                            }
                        }.bind(this));
                        this.removeData(id, function(json){
                            this.data.erase( ad )
                        }.bind(this));
                    }
                }.bind(this));

                this.departmentList = departmentList;
                this.itemsContentNode.empty();
                this.setItems();

                this.app.notice(  this.lp.setDepartmentSuccess , "success");
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.app.content, options);
    },
    changeCompanys: function(){
        var explorer = {
            "actions": this.personActions,
            "app": {
                "lp": this.app.lp
            }
        };
        var options = {
            "type": "company",
            "title": this.lp.setCompany,
            "names": this.companyList || [],
            "onComplete": function(items) {
                var companyList = [];

                items.each(function (item) {
                    companyList.push(item.data.name);
                }.bind(this));

                companyList.each(function (item) {
                    if (!this.companyList.contains(item)) {
                        var object = {
                            "objectType": this.options.objectType,
                            "objectId": this.options.objectId,
                            "usedObjectType": "COMPANY",
                            "usedObjectCode": item,
                            "usedObjectName": item,
                            "permission" : this.options.permission
                        };
                        this.saveData(object, function (json) {
                            object.id = json.data.id;
                            this.data.push( object );
                        }.bind(this));
                    }
                }.bind(this));

                this.companyList.each(function (item) {
                    if (!companyList.contains(item)) {
                        var ad = null;
                        var id = "";
                        this.data.each(function (data) {
                            if (data.usedObjectName == item) {
                                ad = data;
                                id = data.id;
                            }
                        }.bind(this));
                        this.removeData(id, function (json) {
                            this.data.erase(ad)
                        }.bind(this) );
                    }
                }.bind(this));

                this.companyList = companyList;
                this.itemsContentNode.empty();
                this.setItems();


                this.app.notice(  this.lp.setCompanySuccess , "success");
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.app.content, options);
    },
    listData: function( callback ){
        this.app.restActions.listColumnPermission(this.options.objectId, function(json){
            if( callback )callback( json );
        }.bind(this), null ,false)
    },
    removeData: function( id, callback ){
        this.app.restActions.removePermission(id, function (json) {
            if( callback )callback( json );
        }.bind(this), null, false);
    },
    saveData: function( data, callback ){
        debugger;
        this.app.restActions.savePermission(data, function (json) {
            if( callback )callback( json );
        }.bind(this), null, false);
    }
});
