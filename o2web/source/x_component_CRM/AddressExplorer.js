MWF.xDesktop.requireApp("CRM", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xApplication.CRM.AddressExplorer = new Class({
    Extends: MWF.xApplication.CRM.Explorer,
    Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_CRM/$AddressExplorer/";
        this.cssPath = "/x_component_CRM/$AddressExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
    },
    reload: function(){
        this.node.empty();
        this.load();
    },
    load: function(){
        this.loadToolbar();
        this.loadContentNode();

        this.loadContent();
        this.setNodeScroll();

    },
    destroy: function(){
        if( this.baiduMap ){
            this.baiduMap.map.clearOverlays();
            this.elementContentNode.destroy();
        }
        this.node.empty();
        delete  this;
    },
    loadContent : function( filterData ){
        this.elementContentNode.empty();
        /*this.view = new MWF.xApplication.CRM.AddressExplorer.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        this.view.filterData = filterData;
        this.view.load();*/

        /*this.actions.listWorkplace( function(json){
            json.data = json.data || [];
            this.wpContent = this.toolbarNode.getElements("[name=wpContent]")[0];
            this.createList( json.data );


            this.baiduMap = new MWF.xApplication.CRM.AddressExplorer.BaiduMap( this.elementContentNode, this.app, this, {} );
            this.baiduMap.load( json.data );
            this.setContentSize();
        }.bind(this));*/
        var jdata =  [];
        //this.wpContent = this.toolbarNode.getElements("[name=wpContent]")[0];
        //this.createList( jdata );


        this.baiduMap = new MWF.xApplication.CRM.AddressExplorer.BaiduMap( this.elementContentNode, this.app, this, {} );
        this.baiduMap.load( jdata );
        this.setContentSize();

    },
    reloadList: function(){
        this.actions.listWorkplace( function(json){
            this.wpContent.empty();
            this.createList( json.data || [] );
        }.bind(this))
    },
    createList: function( data ){
        this.wdList = new Element("div", {
            styles : this.css.wdList
        }).inject( this.wpContent );
        this.wdList.setStyle( "width" , this.toolbarNode.getSize().x - 200 + "px" );

        data.each( function( d ){
            var placeItem = new Element( "div", {
                styles : this.css.toolbarContentItem,
                text : d.placeName
            }).inject( this.wdList );
            placeItem.addEvent( "click" , function(e){
                this.obj.baiduMap.gotoMarker( this.data );
                e.stopPropagation();
            }.bind({ obj : this, data : d }) )
        }.bind(this) );

        this.arrow = "up";
        if( this.wdList.getScrollSize().y > this.wpContent.getSize().y ){

            this.wdList.addEvent("click",function(e){
                if( this.arrow != "down" ){
                    this.openList( e );
                }else{
                    this.closeList( e )
                }
            }.bind(this));

            this.arrowNode = new Element("div.arrowNode",{
                "styles" : this.css.arrowNode
            }).inject( this.wpContent, "top" );
            this.arrowNode.addEvents({
                "mouseover" : function(){
                    this.arrowNode.setStyles( this.categoryArrow != "down" ? this.css.arrowNode_over : this.css.arrowNode_down_over);
                }.bind(this),
                "mouseout" : function(){
                    this.arrowNode.setStyles( this.categoryArrow != "down" ? this.css.arrowNode : this.css.arrowNode_down);
                }.bind(this),
                "click" : function( e ){
                    if( this.arrow != "down" ){
                        this.openList( e );
                    }else{
                        this.closeList( e )
                    }
                }.bind(this)
            });
        }
    },
    _setContentSize: function(){
        //this.wdList.setStyle( "width" , this.toolbarNode.getSize().x - 200 + "px" );
    },
    openList : function( e ){
        this.arrow = "down";
        //this.arrowNode.setStyles(this.css.arrowNode_down_over );
        this.arrowNode.setStyle("display","none");
        this.wdList.setStyles(this.css.wdList_all);
        window.closeList = this.closeList.bind(this);
        this.app.content.addEvent("click", window.closeList );
        e.stopPropagation();
    },
    closeList : function( e ){
        this.arrow = "up";
        //this.arrowNode.setStyles(this.css.arrowNode );
        this.arrowNode.setStyle("display","");
        this.wdList.setStyles(this.css.wdList);
        this.app.content.removeEvent("click" , window.closeList );
        e.stopPropagation();
    },
    createDocument: function(){
        this.baiduMap.createMarker();
    }
});

MWF.xApplication.CRM.AddressExplorer.BaiduMap = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, app, explorer, options) {
        this.container = container;
        this.explorer = explorer;
        this.app = app;
        this.actions = explorer.actions;
        this.setOptions(options);
        this.markers = {};
        this.markerInfoWindows = {};
    },
    load : function( markerData ){
        this.markerData = markerData;
        this.mapNode = new Element("div", {styles : {
            width : "100%",
            height : "99%"
        }}).inject(this.container);
        setTimeout( function(){
            this.loadResource( );
        }.bind(this) , 200 )
    },
    loadResource: function (callback) {
        window.BMap_loadScriptTime = (new Date).getTime();
        //var apiPath = "http://api.map.baidu.com/api?v=2.0&ak=Qac4WmBvHXiC87z3HjtRrbotCE3sC9Zg";
        var apiPath = "http://api.map.baidu.com/getscript?v=2.0&ak=Qac4WmBvHXiC87z3HjtRrbotCE3sC9Zg&services=&t=20161219171637";
        if( !window.BDMapApiLoaded ){
            COMMON.AjaxModule.loadDom(apiPath, function () {
                window.BDMapApiLoaded = true;
                if( !window.BDMarkerToolLoaded ){
                    COMMON.AjaxModule.load( "/x_component_CRM/BDMarkerTool.js", function(){
                        window.BDMarkerToolLoaded = true;
                        this._loadMap();
                        if (callback)callback();
                    }.bind(this) );
                }else{
                    this._loadMap();
                    if (callback)callback();
                }
            }.bind(this));
        }else{
            this._loadMap();
            if (callback)callback();
        }
    },
    _loadMap: function(){
        if (navigator.geolocation){
            try{
                navigator.geolocation.getCurrentPosition(this.loadMap.bind(this), this.loadMap.bind(this));
            }catch( e ){
                this.loadMap();
            }
        }else{
            this.loadMap();
        }
    },
    loadMap: function(position){
        this.createMap( position );
        this.addMapControl();
        if( this.markerData ){
            this.addMarkerArray( this.markerData );
        }
    },
    createMap: function( position ){
        var point = null;
        if (position && position.coords){
            point = new BMap.Point(position.coords.longitude, position.coords.latitude);
        }
        if( !point ){
            debugger
            if( this.markerData && this.markerData.length > 0){
                var json = this.markerData[0];
                point = new BMap.Point(json.longitude, json.latitude);
            }else{
                //point = new BMap.Point(116.404, 39.915);
                point = new BMap.Point(120.122, 30.298);//默认城市的坐标
            }
        }
        var map = this.map = new BMap.Map(this.mapNode);    // 创建Map实例
        map.centerAndZoom(point, 12);  // 初始化地图,设置中心点坐标和地图级别
//		map.centerAndZoom(new BMap.Point(116.404, 39.915), 11);  // 初始化地图,设置中心点坐标和地图级别
//		map.setCurrentCity("杭州市");          // 设置地图显示的城市 此项是必须设置的
        map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
    },
    addMapControl: function(){
        //向地图中添加缩放控件
        var ctrl_nav = new BMap.NavigationControl({anchor:BMAP_ANCHOR_TOP_RIGHT,type:BMAP_NAVIGATION_CONTROL_LARGE});
        this.map.addControl(ctrl_nav);
        //向地图中添加缩略图控件
        var ctrl_ove = new BMap.OverviewMapControl({anchor:BMAP_ANCHOR_BOTTOM_RIGHT,isOpen:1});
        this.map.addControl(ctrl_ove);
        //向地图中添加比例尺控件
        var ctrl_sca = new BMap.ScaleControl({anchor:BMAP_ANCHOR_BOTTOM_LEFT});
        this.map.addControl(ctrl_sca);
        //map.addControl(new this.mapWindow.BMap.MapTypeControl());   //添加地图类型控件

        this.addCityListControl();
    },
    addCityListControl : function(){
        this.map.addControl(new BMap.CityListControl({
            anchor: BMAP_ANCHOR_TOP_LEFT,
            offset: new BMap.Size(10, 20),
            // 切换城市之间事件
             onChangeBefore: function( ){
             },
            // 切换城市之后事件
             onChangeAfter:function( ){
             }
        }));
    },
    getInfoWindowHtml: function( json ){
        json = json || {};
        //拼接infowindow内容字串
        var html = [];
        html.push('<br/>');
        html.push('<table border="0" cellpadding="1" cellspacing="1" id="markerTable" docId="'+ (json.id || "") +'" style="font-size:12px;">');
        html.push('  <tr>');
        html.push('      <td style="width:50px" align="left" class="common">名称：</td>');
        html.push('      <td style="width: 300px"><input type="text"  size="40" value="'+(json.placeName || "") +'" id="placeName" ></td>');
        html.push('	     <td style="width: 10px" valign="top"><span style="color:#ff0000">*</span></td>');
        html.push('  </tr>');
        html.push('  <tr>');
        html.push('      <td  align="left" class="common">别名：</td>');
        html.push('      <td><input type="text" maxlength="300px" size="40"  value="'+(json.placeAlias || "") +'" id="placeAlias" ></td>');
        html.push('	     <td valign="top"></td>');
        html.push('  </tr>');
        html.push('  <tr>');
        html.push('      <td  align="left" class="common">范围：</td>');
        html.push('      <td><input type="text" maxlength="300px" size="40"  value="'+(json.errorRange || "") +'" id="errorRange" ></td>');
        html.push('	     <td valign="top">米</td>');
        html.push('  </tr>');
        html.push('  <tr>');
        html.push('      <td align="left" class="common">备注：</td>');
        html.push('      <td><textarea rows="4" cols="31"  id="description">'+ (json.description || "") +'</textarea></td>');
        html.push('	     <td valign="top"></td>');
        html.push('  </tr>');
        html.push('  <tr>');
        html.push('	     <td  align="center" colspan="3">');
        html.push('          <input type="button" name="btnOK"  id="submitPlace" value="保存">&nbsp;&nbsp;');
        if( json.id ){
            html.push('		     <input type="button" name="btnMove" id="enableMovePlace" value="允许移动">&nbsp;&nbsp;');
        }
        html.push('		     <input type="button" name="btnClear" id="cancelPlace" value="删除">');
        html.push('	     </td>');
        html.push('  </tr>');
        html.push('</table>');
        return html.join("");
    },
    createMarker: function(){
        var _self = this;
        var mkrTool = new BMapLib.MarkerTool(this.map, {autoClose: true});
        mkrTool.addEventListener("markend", function(evt){
            var pt = evt.marker.point;
            var gc = new BMap.Geocoder();
            //var addrsssObj = _self.app.rightContentDiv.getElementById("detailaddress");
            gc.getLocation(pt, function (rs) {
                var addComp = rs.addressComponents;
                var province =  (addComp.province).replace("省","").replace("市","").replace("自治区","").replace("回族","").replace("壮族","").replace("维吾尔","")
                var city = addComp.city;
                var area = addComp.district;
                jQuery("#detailaddress").val(rs.address);
                jQuery("#detailaddress").attr("location",province+","+city+","+area);
                jQuery("#detailaddress").attr("lng",rs.point.lng);
                jQuery("#detailaddress").attr("lat",rs.point.lat);
                jQuery("#detailaddress").attr("province",province);
                jQuery("#detailaddress").attr("city",city);

            });
            var mkr = evt.marker;
            mkr.addEventListener("dragend",function(){
                var pt = mkr.point;
                var gc = new BMap.Geocoder();
                gc.getLocation(pt, function (ts) {
                    var addComp = ts.addressComponents;
                    var province =  (addComp.province).replace("省","").replace("市","").replace("自治区","").replace("回族","").replace("壮族","").replace("维吾尔","");
                    var city = addComp.city;
                    var area = addComp.district;
                    debugger
                    jQuery("#detailaddress").val(ts.address);
                    jQuery("#detailaddress").attr("location",province+","+city+","+area);
                    jQuery("#detailaddress").attr("lng",ts.point.lng);
                    jQuery("#detailaddress").attr("lat",ts.point.lat);
                    jQuery("#detailaddress").attr("province",province);
                    jQuery("#detailaddress").attr("city",city);


                });
            });
            /*var infoWin = new BMap.InfoWindow(this.getInfoWindowHtml(), {offset: new BMap.Size(0, -10)});
            infoWin.addEventListener("open",function(){
                var table = document.id("markerTable");
                var submitPlace = table.getElements("[id='submitPlace']");
                submitPlace.addEvent("click", function(){
                    this.obj.ok( this.mkr , this.table );
                }.bind({ obj : this, mkr : mkr, table : table }));

                var cancelPlace = table.getElements("[id='cancelPlace']");
                cancelPlace.addEvent("click", function(){
                    this.obj.cancel( this.mkr, this.table );
                }.bind({ obj : this, mkr : mkr, table : table }));
            }.bind( _self ));
            var mkr = evt.marker;
            mkr.addEventListener("click",function(){
                this.openInfoWindow(infoWin);
            });
            mkr.addEventListener("dragend",function(){
                this.openInfoWindow(infoWin);
            });
            mkr.openInfoWindow(infoWin);
            */
        }.bind(this));

        mkrTool.open(); //打开工具
        var icon = BMapLib.MarkerTool.SYS_ICONS[14]; //设置工具样式，使用系统提供的样式BMapLib.MarkerTool.SYS_ICONS[0] -- BMapLib.MarkerTool.SYS_ICONS[23]
        mkrTool.setIcon(icon);
    },
    //创建marker
    addMarkerArray : function ( markerArr ){
        for(var i=0;i<markerArr.length;i++){
            var json = markerArr[i];
            this.addMarker( json );
        }
    },
    addMarker : function( json ){
        var _self = this;
        var point = new BMap.Point(json.longitude, json.latitude);
        var iconImg = BMapLib.MarkerTool.SYS_ICONS[8];
        var marker = new BMap.Marker(point,{
            icon:iconImg,
            enableDragging : false
        });

        var label = new BMap.Label(json.placeName,{"offset":new BMap.Size(0,-20)});

        marker.setLabel(label);
        this.map.addOverlay(marker);
        label.setStyle({
            borderColor:"#808080",
            color:"#333",
            cursor:"pointer"
        });

        (function(){
            var _iw = new BMap.InfoWindow(this.getInfoWindowHtml( json ), {offset: new BMap.Size(0, -10)});
            var _marker = marker;
            var _json = json;
            _marker.addEventListener("click",function(){
                this.openInfoWindow(_iw);
            });
            _marker.addEventListener("dragend",function(){
                this.openInfoWindow(_iw);
            });
            _iw.addEventListener("open",function(){
                _marker.getLabel().hide();

                var table = document.id("markerTable");
                var enableMovePlace = table.getElements("[id='enableMovePlace']");
                enableMovePlace.addEvent("click", function(){
                    this.obj.enableMove( this.mkr );
                }.bind({ obj : this, mkr : _marker, table : table, id : _json.id }));

                var submitPlace = table.getElements("[id='submitPlace']");
                submitPlace.addEvent("click", function(){
                    this.obj.ok( this.mkr , this.table, this.id );
                }.bind({ obj : this, mkr : _marker, table : table, id : _json.id }));

                var cancelPlace = table.getElements("[id='cancelPlace']");
                cancelPlace.addEvent("click", function(){
                    this.obj.cancel( this.mkr, this.table, this.id );
                }.bind({ obj : this, mkr : _marker, table : table, id : _json.id }))

            }.bind( _self ));
            _iw.addEventListener("close",function(){
                _marker.getLabel().show();
            });
            label.addEventListener("click",function(){
                _marker.openInfoWindow(_iw);
            });

            this.markers[ json.id ] = marker;
            this.markerInfoWindows[ json.id ] = _iw;
        }.bind(this))();
    },
    enableMove: function( mrk ){
        mrk.closeInfoWindow();
        mrk.enableDragging();
    },
    gotoMarker : function( json ){
        var marker = this.markers[ json.id ];
        this.map.centerAndZoom(marker.point, 15);
        marker.openInfoWindow( this.markerInfoWindows[ json.id ] );
    },
    ok: function( mkr, table, id ){
        //var id = table.get("docId");
        var placeName = table.getElements("[id='placeName']")[0].get("value");
        if( placeName.trim() == "" ){
            this.app.notice( "工作场所不能为空", "error" );
            return false;
        }

        var placeAlias = table.getElements("[id='placeAlias']")[0].get("value");
        var description = table.getElements("[id='description']")[0].get("value");
        var errorRange = table.getElements("[id='errorRange']")[0].get("value");
        var data = {
            placeName : placeName,
            placeAlias : placeAlias,
            errorRange : errorRange,
            description : description,
            longitude : mkr.point.lng,
            latitude : mkr.point.lat
        };
        if(id)data.id = id;
        this.actions.saveWorkplace( data, function( json ){
            data.id = json.data.message;
            mkr.closeInfoWindow();
            mkr.remove();
            this.addMarker( data );
            this.explorer.reloadList();
        }.bind(this) )
    },
    cancel: function( mkr, table, id ){
        if( id ){
            this.actions.deleteWorkplace( id, function(){
                mkr.closeInfoWindow();
                var label = mkr.getLabel();
                if( label )label.remove();
                mkr.remove();
                this.explorer.reloadList();
            }.bind(this) )
        }else{
            mkr.closeInfoWindow();
            var label = mkr.getLabel();
            if( label )label.remove();
            mkr.remove();
        }
    }
});
