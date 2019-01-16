MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Template", "Minder", null, false);
MWF.xDesktop.requireApp("Report", "Common", null, false);

MWF.xApplication.Report.DepartmentMinder = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "id" : "",
        "year" : "",
        "style": "default",
        "template" : "default",
        "theme": "fresh-blue-compat" //"fresh-blue"
    },
    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Report/$DepartmentMinder/";
        this.cssPath = "/x_component_Report/$DepartmentMinder/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.lp = this.app.lp;

        this.actions = actions;
        this.node = $(node);
    },
    load: function(){
        this.departmentCN = this.options.department.split("@")[0];
        this.app.setTitle( this.options.year + "年" + this.departmentCN + "工作汇报");
        this.data = {
            root : {
                data : {
                    text : this.departmentCN,
                    isRoot : true,
                    watch : true
                },
                children : []
            }
        };
        this.app.strategyActions.listMeasureByYearDepartment( this.options.year, this.options.department, function( json ){
            var children = this.data.root.children;
            var arr = json.data || [];
            var loaded = 0;
            arr.each( function( d ){
                d.text = d.measuresinfotitle;
                d.note = " ";
                d.isMeasure = true;
                d.watch = true;
                var obj = {
                    data : d,
                    children : []
                };
                this.app.strategyActions.listPriorityByUnitMeasure( this.options.year, d.id, this.options.department, function( js ){

                    if( !js.data )js.data = [];
                    js.data.each( function( d ){
                        if(d.keyworktitle ){
                            d.text = d.keyworktitle;
                            d.note = " ";
                            d.isPriority = true;
                            d.watch = true;
                            obj.children.push( {
                                data : d,
                                children : [{
                                    data : { text : "加载中..." }
                                }]
                            })
                        }
                    });

                    loaded++;
                    if( loaded == arr.length ){
                        this.createMinder( this.data );
                    }
                }.bind(this));
                children.push( obj )
            }.bind(this));
        }.bind(this));

        this.refreshFun = this.refresh.bind(this);
        this.app.addEvent("resize", this.refreshFun);

        //new Element("button", {
        //    text : "整理布局",
        //    value : "整理布局",
        //    events : {
        //    click  : function(){
        //        //this.minder.km.execCommand('resetlayout')
        //
        //        this.minder.km.refresh();
        //    }.bind(this)
        //}}).inject( this.node )
    },

    destroy : function(){
        if(this.minder)this.minder.destroy() ;
        for( var key in this.tooltips ){
            this.tooltips[key].destroy();
        }
        if( this.refreshFun ){
            this.app.removeEvent("resize", this.refreshFun);
        }
        delete this;
    },
    refresh: function(){
        if(this.minder)this.minder.refresh();
    },
    reload: function(){
        if(this.minder)this.minder.destroy() ;
        this.node.empty();
        this.load();
    },
    createMinder: function( json ) {
        this.minder = new MWF.xApplication.Template.Minder(this.node, this.app, json, {
            "hasNavi" : false,
            "onPostLoad" : function(){
                this.minder.km.execCommand('ExpandToLevel', 2 ); //折叠到第一层
                this.minder.loadNavi( this.node );
            }.bind(this),
            "onPostLoadNode" : function( minderNode ){
                this.setMinderNode( minderNode );
            }.bind(this)
        });
        this.minder.load();
    },
    setMinderNode : function( minderNode ){
        var _self = this;
        if( !minderNode.getData().watch ){
        }else{
            var data = minderNode.getData();

            var iconRenderer = minderNode.getRenderer('NoteIconRenderer');
            if( iconRenderer && iconRenderer.getRenderShape() ){
                var icon = iconRenderer.getRenderShape();
                icon.addEventListener("mouseover", function ( ev ) {
                    _self.tooltipTimer = setTimeout(function() {
                        var c = this.getRenderBox('screen');
                        _self.loadTooltip( this.getData(), c );
                    }.bind(this), 300);
                }.bind(minderNode));

                icon.addEventListener("mouseout", function ( ev ) {
                    clearTimeout(_self.tooltipTimer);
                    _self.hideTooltip();
                }.bind(minderNode));
            }

            if( data.isPriority ){
                var expanderNode = minderNode.getRenderer('ExpanderRenderer').getRenderShape();
                if( expanderNode ){
                    expanderNode.addEventListener("mousedown", function(ev){
                        var data = this.getData();
                        if( !data.loaded ){
                            setTimeout( function(){
                                _self.expendPriority( this, data, function(){
                                }.bind(this))
                            }.bind(this), 100 )
                        }
                    }.bind(minderNode));
                }
            }

            var cNode = minderNode.getRenderContainer().node;
            cNode.addEventListener("click", function(ev){
                var data = this.getData();
                if( data.isPriority ){
                    if( !data.loaded ){
                        setTimeout( function(){
                            _self.expendPriority( this, data, function(){
                            }.bind(this))
                        }.bind(this), 100 )
                    }else{
                        var renderer = this.getRenderer('ExpanderRenderer');
                        renderer.expander.fire("mousedown");
                    }
                }else{
                    //if( !data.isRoot ){
                    //    var renderer = this.getRenderer('ExpanderRenderer');
                    //    renderer.expander.fire("mousedown");
                    //}
                }
            }.bind(minderNode));



            //cNode.addEventListener("dblclick", function ( ev ) {
            //    _self.openWork( this, this.getData() );
            //}.bind(minderNode));
        }
    },
    expendPriority : function( minderNode, data, callback ){
        var _self = this;
        if( this.isLoaddingChildren )return;
        this.isLoaddingChildren = true;
        this.actions.listWithPriority( this.options.year, {
            "workIds":[data.id],"unitList":[ this.options.department ] //,"reportObjType":"UNIT"
        }, function( json ){
            data.loaded = true;
            var obj = {
            };
            var array = json.data;
            array.sort( function(a, b){
                return  ( a.month  ).localeCompare( b.month  );
            });

            array.each( function( d ) {
                if (!obj[d.month])obj[d.month] = [];
                obj[d.month].push( d )
            });

            var j = {
                data : data,
                children : []
            };
            for( var key in obj ){
                var arr = [];
                obj[key].each( function( o ){
                    var a = [];
                    o.progList.each( function( prog ){
                        a.push(  this.app.common.splitWithLength( prog.targetPerson.split("@")[0] + "："  + prog.progressContent, 35 ) );
                    }.bind(this));
                    arr.push( a.join("\n") );
                }.bind(this));
                j.children.push( {
                    data : {
                        text : parseInt( key ) +"月"
                    },
                    children : [{
                        data : {  text : arr.join( "\n" ) }
                    }]
                })
            }

            while (minderNode.getChildren().length){
                var node = minderNode.getChildren()[0];
                this.minder.km.removeNode(node)
            }
            this.minder.km.importNode(minderNode, j );

            //this.proiorityLoadedLength ++;
            //if( this.proiorityLoadedLength == this.proiorityLength ){
                minderNode.expand();
                this.minder.km.refresh();

                //var nodes = departmentNode.getChildren();
                //nodes.forEach(function (node) {
                //    this.setMinderNode(node);
                //}.bind(this));

              this.isLoaddingChildren = false;
            //}

        }.bind(this))


    },
    loadTooltip: function( data, c ){
        if( !this.tooltips )this.tooltips = {};
        if( this.tooltips[ data.id ] ){
            var tooltip = this.currentTooltip = this.tooltips[ data.id ];
            tooltip.targetCoordinates = c;
            tooltip.load();
        }else{
            if( data.isRoot ){
                var tooltip = this.currentTooltip = new MWF.xApplication.Report.KeyWorkTooltip( this.node, null, this.app, data, {}, c);
            }else if( data.isMeasure ){
                var tooltip = this.currentTooltip = new MWF.xApplication.Report.MeasureTooltip( this.node, null, this.app, data, {}, c);
            }else if( data.isPriority ){
                var tooltip = this.currentTooltip = new MWF.xApplication.Report.PriorityTooltip( this.node, null, this.app, data, {}, c);
            }
            if( tooltip ){
                tooltip.load();
                this.tooltips[ data.id ] = tooltip;
            }
        }
    },
    hideTooltip: function(){
        if( this.currentTooltip ){
            this.currentTooltip.hide()
        }
    }
});
