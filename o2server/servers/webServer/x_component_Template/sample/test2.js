MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("cms.Module", "ExcelForm", null, false);

this.define("dipatchNumberToCity", function(){
    var ids = this.getSelectedId();
    if( ids.length == 0 ){
        this.form.app.notice("先选择号码","error");
        return;
    }
    var units = this.getLevel1Unit();
    var unitList = [];
    units.each( function( u ){
        unitList.push({
            name : u.name,
            id : u.distinguishedName
        })
    });
    MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false);
    var opt  = {
        "count": 1,
        "title": "选择分配的组织",
        "selectableItems" : unitList,
        "values": [],
        "onComplete": function( array ){
            if( !array || array.length == 0 )return;
            var unit = array[0].data.id;
            if( !unit )return;
            this.saveDocList( ids, unit, "", "" );
        }.bind(this)
    };
    var selector = new MWF.xApplication.Template.Selector.Custom(this.form.app.content, opt );
    selector.load();
}.bind(this));

this.define("dipatchNumberToCounty", function( city, range ){
    var ids = this.getSelectedId();
    if( ids.length == 0 ){
        this.form.app.notice("先选择号码","error");
        return;
    }

    var units = [];
    if( city ){
        var unit = this.org.listSubUnit( city , false );
        unit.each( function( u ){
            units.push({
                name : u.name,
                id : u.distinguishedName
            })
        });
    }
    if( city ){
        MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "selectableItems" : units,
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.id;
                this.saveDocList(ids, city, unit, ""  )
            }.bind(this)
        };
        var selector = new MWF.xApplication.Template.Selector.Custom(this.form.app.content, opt );
        selector.load();
    }else{
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "type" : "unit",
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.distinguishedName;
                debugger;
                var levelName = array[0].data.levelName;
                if( levelName.split("/").length != 2 ){
                    this.form.app.notice("请选择县级分公司", "error");
                    return false;
                }
                this.getAllUnit();
                var c = this.name_dnName[levelName.split("/")[0]];
                this.saveDocList(ids, c, unit, "")
            }.bind(this)
        };
        if( range )opt.units = [range];
        var selector = new MWF.O2Selector(this.form.app.content, opt );
    }
}.bind(this));

this.define("dipatchNumberToBranch", function( county, range ){
    var ids = this.getSelectedId();
    if( ids.length == 0 ){
        this.form.app.notice("先选择号码","error");
        return;
    }

    var units = [];
    if( county ){
        var unit = this.org.listSubUnit( county , false );
        unit.each( function( u ){
            units.push({
                name : u.name,
                id : u.distinguishedName
            })
        });
    }
    if( county ){
        MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "selectableItems" : units,
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.id;
                this.getAllUnit();
                var levelName = this.dnName_levelName[unit];
                if( levelName.split("/").length != 3 ){
                    this.form.app.notice("请选择网格", "error");
                    return false;
                }
                var c = this.name_dnName[levelName.split("/")[0]];
                this.saveDocList(ids, c, county, unit )
            }.bind(this)
        };
        var selector = new MWF.xApplication.Template.Selector.Custom(this.form.app.content, opt );
        selector.load();
    }else{
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "type" : "unit",
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.distinguishedName;
                debugger;
                this.getAllUnit();
                var levelName = this.dnName_levelName[unit];
                if( levelName.split("/").length != 3 ){
                    this.form.app.notice("请选择网格", "error");
                    return false;
                }
                var city2 = this.name_dnName[levelName.split("/")[0]];
                var county2 = this.name_dnName[levelName.split("/")[1]];
                this.saveDocList(ids, city2, county2, unit );
            }.bind(this)
        };
        if( range )opt.units = [range];
        var selector = new MWF.O2Selector(this.form.app.content, opt );
    }
}.bind(this));

this.define("saveDocList", function( ids, city, county, branch ){

    ids.each( function(id){
        debugger;
        var oldData = this.form.selectedItemJson[id];
        var newData = { docStatus : "published", city : city, county : county , branch : branch };
        if( !this.form.statJson ){
            this.form.statJson = new StatJson(this);
        }
        this.form.statJson.changeData( newData, oldData, oldData.batch );
        this.form.statJson.submit();
    }.bind(this));
    if( this.form.currentView.docStatus == "error" ){
        var changeCount = 0;
        ids.each( function( id ){
            this.saveDoc( id, city, county, branch, function(){
                changeCount++;
                if( changeCount == ids.length ){
                    this.setUploadedUnit( function(){
                        this.form.app.notice("分配成功","");
                        this.createImportBatchDiv();
                        this.loadStatTable( this.statTableOptions ? this.statTableOptions.container : this.form.get("statContaienr").node );
                        this.form.view.reload();
                        this.form.view.selectedItems = [];
                        if( this.form.view_error ){
                            this.form.view_error.reload();
                            this.form.view_error.selectedItems = [];
                        }
                    }.bind(this));
                }
            }.bind(this))
        }.bind(this))
    }else{
        this.saveDcc(ids, ["city","county","branch"], [city,county,branch], function(){
            this.setUploadedUnit( function(){
                this.form.app.notice("分配成功","");
                this.createImportBatchDiv();
                this.loadStatTable( this.statTableOptions ? this.statTableOptions.container : this.form.get("statContaienr").node  );
                this.form.currentView.reload();
                this.form.currentView.selectedItems = [];
            }.bind(this));
        }.bind(this))
    }
}.bind(this));

this.define("saveDoc", function( id, city, county, branch, callback ){
    MWF.Actions.get("x_cms_assemble_control").getDocument(id, function( json ){
        var docData = json.data;
        docData.data.city = city;
        docData.data.county = county;
        docData.data.branch = branch;

        docData.data.errorText = "";
        docData.data.docStatus = "published";
        docData.data.status = "成功";
        docData.data.title = docData.data.subject;

        delete docData.data.$document;
        delete docData.document.viewCount;
        delete docData.document.publishTime;
        delete docData.document.hasIndexPic;
        delete docData.document.readPersonList;
        delete docData.document.readUnitList;
        delete docData.document.readGroupList;
        delete docData.document.authorPersonList;
        delete docData.document.authorUnitList;
        delete docData.document.authorGroupList;
        delete docData.document.managerList;
        delete docData.document.pictureList;

        delete docData.documentLogList;
        delete docData.isAppAdmin;
        delete docData.isCategoryAdmin;
        delete docData.isManager;
        delete docData.isCreator;
        delete docData.isEditor;

        docData.document.docData = docData.data;
        delete docData.data;

        docData.document.docStatus = "published";
        docData.document.subject = docData.document.title;

        MWF.Actions.get("x_cms_assemble_control").updateDocument( docData.document , function(){
            if( callback )callback();
        }.bind(this));
    }.bind(this))
}.bind(this));

this.define("dipatchNumber", function(){
    // var flag = (this.workContext.getControl().allowSave  && this.workContext.getActivity().alias == "draft") ;
    // if( !flag ){
    //     this.form.app.notice( "发起节点才能分配号码","error" );
    //     return;
    // }
    var ids = this.getSelectedId();
    if( ids.length == 0 ){
        this.form.app.notice("先选择号码","error");
        return;
    }
    var units = this.getSubUnit();
    if( units ){
        MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "selectableItems" : units,
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.id;
                this.setUnit(ids, unit )
            }.bind(this)
        };
        var selector = new MWF.xApplication.Template.Selector.Custom(this.form.app.content, opt );
        selector.load();
    }else{
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "type" : "unit",
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.distinguishedName;
                debugger;
                this.setUnit(ids, unit)
            }.bind(this)
        };
        var selector = new MWF.O2Selector(this.form.app.content, opt );
    }

});

this.define("getSelectedId", function(){
    var ids = [];
    if( !this.form.currentView ){
        this.form.currentView = this.form.view;
    }
    this.form.selectedItemJson = {};
    this.form.currentView.selectedItems.each( function( item ){
        ids.push( item.data.bundle );
        this.form.selectedItemJson[ item.data.bundle ] = {
            batch : item.data.data.batch,
            city : item.data.data.city,
            county : item.data.data.county,
            branch : item.data.data.branch,
            docStatus : this.form.currentView.docStatus || "published"
        }
    }.bind(this));
    return ids;
});

this.define("getSubUnit", function(){
    var units = this.data.currentUnit;
    if( units ){
        var unit = this.org.listSubUnit( units , false );
    }else if( !this.data.newFlag ){
        var unit = this.getLevel1Unit(); //this.workContext.getWork().creatorUnitLevelName.split("/")[0];
    }else{
        return null;
    }
    //unit = un