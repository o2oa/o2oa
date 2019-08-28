MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Log = MWF.APPLog =  new Class({
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");
        if (this.form.businessData){
            if (this.form.businessData.workLogList){
                this.workLog = this.form.businessData.workLogList;
                this.loadWorkLog();
            }
        }
	},

    loadWorkLog: function(){
        if( !this.json.category || this.json.category === "none" ){
            if (this.json.mode==="table"){
                this.loadWorkLogTable();
            }else if (this.json.mode==="text"){
                this.loadWorkLogText();
            }else if (this.json.mode==="media"){
                this.loadWorkLogMedia();
            }else{
                this.loadWorkLogDefault();
            }
        }else{
            this.loadCategoryList();
            if( !this.categoryList.length )return;
            this.expandCount = 0;
            if( this.json.expand && this.json.expand === "enable" ){
                this.expandCount = parseInt( this.json.expandCount );
            }
            this.table = new Element("table", this.json.tableProperties).inject( this.node );
            this.categoryList.each( function( key, idx ){
                var list = this.categoryJson[key];
                if( list && list.length ){
                    var tr = new Element("tr").inject( this.table );
                    if( this.expandCount && (idx + 1) > this.expandCount ){
                        tr.setStyle("display","none");
                    }
                    var text = key;
                    if( this.json.category === "unit" ){
                        text = key.split("@")[0];
                    }
                    new Element("td", {
                        styles : this.json.titleTdStyles,
                        text : text + MWF.xApplication.process.Xform.LP.idea
                    }).inject( tr );

                    var td = new Element("td", {
                        styles : this.json.contentTdStyles
                    }).inject( tr );

                    var div = new Element("div",{
                        styles : this.json.contentDivStyles || {}
                    }).inject( td );

                    if (this.json.mode==="table"){
                        this.loadWorkLogTable( list, div );
                    }else if (this.json.mode==="text"){
                        this.loadWorkLogText( list, div );
                    }else if (this.json.mode==="media"){
                        this.loadWorkLogMedia( list, div );
                    }else{
                        this.loadWorkLogDefault( list, div );
                    }

                    this._loadTableStyles();
                }
            }.bind(this));
            if( this.categoryList.length > this.expandCount ){
                this.loadExpandCollapseNode();
            }
        }
    },
    loadCategoryList : function(  ){
        var category;
        if( this.json.category === "activity" ){
            category = "fromActivityName";
            this._loadCategoryList( category );
        }else if( this.json.category === "unit" ){
            category = "unit";
            this._loadCategoryLitBySubData( category );
        }else if( this.json.category === "activityGroup" ){
            category = "fromOpinionGroup";
            this._loadCategoryList( category );
        }else{
            category = this.json.category;
            this._loadCategoryList( category );
        }

    },
    isNumber : function( d ){
        return parseFloat(d).toString() !== "NaN"
    },
    _loadCategoryList : function( category ){
        this.categoryList = [];
        this.categoryJson = {};
        if( category === "fromOpinionGroup" ){
            this.workLog.sort( function( a, b ){
              if( a.fromOpinionGroup && b.fromOpinionGroup ) {
                  var array1 = a.fromOpinionGroup.split("#");
                  var array2 = b.fromOpinionGroup.split("#");
                  for (var i = 0; i < array1.length; i++) {
                      var value1 = array1[i];
                      var value2 = array2[i];
                      if (this.isNumber(value1) && this.isNumber(value2)) {
                          if (parseFloat(value1) !== parseFloat(value2)) {
                              return parseFloat(value1) - parseFloat(value2);
                          }
                      } else if (!this.isNumber(value1) && !this.isNumber(value2)) {
                          return Date.parse(a.fromTime) - Date.parse(b.fromTime);
                      } else {
                          return this.isNumber(value1) ? -1 : 1;
                      }
                  }
                  return Date.parse(a.fromTime) - Date.parse(b.fromTime);
              }else if( a.fromOpinionGroup || b.fromOpinionGroup ){
                  return a.fromOpinionGroup ? -1 : 1;
              }else{
                  return Date.parse(a.fromTime) - Date.parse(b.fromTime);
              }
            }.bind(this))
        }
        this.workLog.each( function(log, idx){
            var key;
            if( this.json.category === "activityGroup" ){
                if( log.fromOpinionGroup ){
                    var arr = log.fromOpinionGroup.split("#");
                    key = arr[arr.length-1]
                }else{
                    key = log.fromActivityName;
                }
            }else{
                key = log[category];
            }

            if( key && this.checkShow(log)){
                var flag = false;

                for( var i=0; i< log.taskCompletedList.length; i++ ){
                    var taskCompleted = log.taskCompletedList[i];
                    flag = this.checkListShow(log, taskCompleted);
                    if( flag )break;
                }
                if (!flag && this.json.isTask){
                    for( var i=0; i< this.json.isTask.length; i++ ){
                        var task = log.taskList[i];
                        flag = this.checkListShow(log, task);
                        if( flag )break;
                    }
                }
                if(flag){
                    if( this.categoryList.indexOf( key ) === -1 ){
                        this.categoryList.push( key );
                    }
                    if( !this.categoryJson[key] )this.categoryJson[key] = [];
                    this.categoryJson[key].push( log );
                }
            }
        }.bind(this))
    },
    _loadCategoryLitBySubData : function( category ){
        this.categoryList = [];
        this.categoryJson = {};
        this.workLog.each( function(log, idx){
            var key = log[category];

            if( this.checkShow(log) ){
                var flag = false;

                for( var i=0; i< log.taskCompletedList.length; i++ ){
                    var taskCompleted = log.taskCompletedList[i];
                    flag = this.checkListShow(log, taskCompleted);
                    if( flag )break;
                }
                if (!flag && this.json.isTask){
                    for( var i=0; i< this.json.isTask.length; i++ ){
                        var task = log.taskList[i];
                        flag = this.checkListShow(log, task);
                        if( flag )break;
                    }
                }
                if(flag){
                    var log_copy = Object.clone(log);
                    log_copy.taskCompletedList = [];
                    log_copy.taskList = [];
                    log_copy.readCompletedList = [];
                    log_copy.readList = [];

                    var sub_categoryList = [];
                    var sub_categoryJson = {};

                    for( var i=0; i< log.taskCompletedList.length; i++ ){
                        var d = log.taskCompletedList[i];
                        var key = d[category];
                        if( key ){
                            if( sub_categoryList.indexOf( key ) === -1 ){
                                sub_categoryList.push( key );
                            }
                            if( !sub_categoryJson[key] )sub_categoryJson[key] = Object.clone(log_copy);
                            sub_categoryJson[key].taskCompletedList.push( d );
                        }
                    }

                    for( var i=0; i< log.taskList.length; i++ ){
                        var d = log.taskList[i];
                        var key = d[category];
                        if( key ){
                            if( sub_categoryList.indexOf( key ) === -1 ){
                                sub_categoryList.push( key );
                            }
                            if( !sub_categoryJson[key] )sub_categoryJson[key] = Object.clone(log_copy);
                            sub_categoryJson[key].taskList.push( d );
                        }
                    }

                    for( var i=0; i< log.readCompletedList.length; i++ ){
                        var d = log.readCompletedList[i];
                        var key = d[category];
                        if( key ){
                            if( sub_categoryList.indexOf( key ) === -1 ){
                                sub_categoryList.push( key );
                            }
                            if( !sub_categoryJson[key] )sub_categoryJson[key] = Object.clone(log_copy);
                            sub_categoryJson[key].readCompletedList.push( d );
                        }
                    }

                    for( var i=0; i< log.readList.length; i++ ){
                        var d = log.readList[i];
                        var key = d[category];
                        if( key ){
                            if( sub_categoryList.indexOf( key ) === -1 ){
                                sub_categoryList.push( key );
                            }
                            if( !sub_categoryJson[key] )sub_categoryJson[key] = Object.clone(log_copy);
                            sub_categoryJson[key].readList.push( d );
                        }
                    }

                    sub_categoryList.each( function(key){
                        if( this.categoryList.indexOf( key ) === -1 ){
                            this.categoryList.push( key );
                        }
                    }.bind(this));

                    for( var key in sub_categoryJson ){
                        if( !this.categoryJson[key] )this.categoryJson[key] = [];
                        this.categoryJson[key].push( sub_categoryJson[key] );
                    }
                }
            }

        }.bind(this))
    },
    _loadTableBorderStyle: function(){
        if (this.json.tableStyles.border){
            this.table.set("cellspacing", "0");
            this.table.setStyles({
                "border-top": this.json.tableStyles.border,
                "border-left": this.json.tableStyles.border
            });
            var ths = this.table.getElements("th");
            if( ths && ths.length ){
                ths.setStyles({
                    "border-bottom": this.json.tableStyles.border,
                    "border-right": this.json.tableStyles.border
                });
            }

            var tds = this.table.getElements("td");
            if( tds && tds.length ) {
                tds.setStyles({
                    "border-bottom": this.json.tableStyles.border,
                    "border-right": this.json.tableStyles.border
                });
            }
        }
    },
    _loadTableStyles: function(){
        Object.each(this.json.tableStyles, function(value, key){
            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                this.table.setStyle(key, value);
            }
        }.bind(this));
        this._loadTableBorderStyle();
    },
    loadExpandCollapseNode : function(){
        if( this.json.expand && this.json.expand === "enable" ){
            if( this.json.expandHTML ){
                this.expandNode = new Element("div",{
                    "html" : this.json.expandHTML
                }).inject( this.node, "bottom" );
                this.expandNode.addEvent("click", function(){
                    if( !this.json.category || this.json.category === "none" ){

                    }else{
                        this.table.getElements("tr").setStyle("display","");
                        this.expandNode.setStyle("display","none");
                        this.collapseNode.setStyle("display","");
                    }
                }.bind(this))
            }
            if( this.json.collapseHTML ){
                this.collapseNode = new Element("div",{
                    "html" : this.json.collapseHTML,
                    "styles" : { "display" : "none" }
                }).inject( this.node, "bottom" );
                this.collapseNode.addEvent("click", function(){
                    if( !this.json.category || this.json.category === "none" ){

                    }else{
                        var trs = this.table.getElements("tr");
                        for( var i=0; i<trs.length; i++ ){
                            if( i >= this.expandCount ){
                                trs[i].setStyle("display","none");
                            }
                        }
                        this.expandNode.setStyle("display","");
                        this.collapseNode.setStyle("display","none");
                    }
                }.bind(this))
            }
        }
    },


    loadWorkLogMedia: function(list, container){
        if( list ){
            list.each(function(log, idx){
                this.loadWorkLogLine_media(log, idx, container);
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkShow(log)) this.loadWorkLogLine_media(log, idx, container);
            }.bind(this));
        }

    },
    loadWorkLogLine_media: function(log, idx, container){
        if (log.taskCompletedList.length){
            log.taskCompletedList.each(function(taskCompleted){
                if (this.checkListShow(log, taskCompleted)) this.loadTaskLine_media(taskCompleted, log, container);
            }.bind(this));
        }
    },
    loadTaskLine_media: function(task, log, container){
        if (task.mediaOpinion){
            var mediaIds = task.mediaOpinion.split(",");
            var atts = [];
            if (this.form.businessData.attachmentList){
                this.form.businessData.attachmentList.each(function(att){
                    if (att.site==="$mediaOpinion"){
                        if (mediaIds.indexOf(att.id)!==-1) atts.push(att);
                    }
                }.bind(this));
            }
            if (atts.length) this.loadMediaOpinion_show(atts, task, container);
        }
    },
    loadMediaOpinion_show: function(atts, task, container){
        atts.each(function(att){
            //if (!att.contentType) att.contentType = "image";
            if (att.type){
                if (att.type.indexOf("image")!==-1){
                    this.loadMediaOpinion_image_show(att, task, container);
                }else if(att.type.indexOf("video")!==-1){
                    this.loadMediaOpinion_video_show(att, task, container);
                }else if(att.type.indexOf("audio")!==-1){
                    this.loadMediaOpinion_voice_show(att, task, container);
                }else{
                    this.loadMediaOpinion_voice_show(att, task, container);
                }
            }
        }.bind(this));
    },
    loadMediaOpinion_image_show: function(att, task, container){
        var url = this.getMediaOpinionUrl(att);
        var node = new Element("div", {"styles": {"overflow": "hidden"}}).inject( container || this.node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "28px",
                "height": "28px"
            },
            "text": task.person.substring(0, task.person.indexOf("@"))+"("+task.completedTime+")"
        }).inject(node);
        //var img = new Element("img", {"src": url, "styles": {"background-color": "#ffffff"}}).inject(node);
        //
        //var height = 200;
        //var width = 300;
        //if (layout.mobile){
        //    var size = img.getSize();
        //    width = 200;
        //    height = 200*(size.y/size.x);
        //}
        //img.setStyles({"width": ""+width+"px", "height": ""+height+"px"});


        var imgNode = new Element("div").inject(node);
        var width;
        if (layout.mobile){
            width = 200;
        }else{
            var pNode = node.getParent();
            var offset = imgNode.getPosition( pNode );
            //width = Math.min( pNode.getSize().x - offset.x - 2, 800 );
            width = pNode.getSize().x - offset.x - 42;
        }

        var img = new Element("img", {
            "src": url,
            "styles" : { width : width+"px" }, //最开始限定一下宽度，不要让页面抖动
            "events" : {
                load : function(ev){
                    var nh=ev.target.naturalHeight;
                    var nw = ev.target.naturalWidth;
                    if( !layout.mobile && ( this.isNumber( this.json.handwritingWidth ) || this.isNumber( this.json.handwritingHeight ) ) ){
                        var size = this.getImageSize( nw, nh );
                        img.setStyles(size);
                        imgNode.setStyles(size);
                    }else{
                        var x = Math.min(nw, width);
                        img.setStyles({"width": ""+ x +"px"}); //最终的宽度
                        imgNode.setStyles({"width": ""+ x +"px"});
                    }
                }.bind(this)
            }
        }).inject(imgNode);

        // var size = img.getSize();
        // var x_y = size.x/size.y;
        // if (size.y>260){
        //     var y = 260;
        //     var x = 260*x_y;
        //     img.setStyles({"width": ""+x+"px", "height": ""+y+"px"})
        // }
    },
    isNumber : function( d ){
        return parseFloat(d).toString() !== "NaN"
    },
    getImageSize : function(naturalWidth, naturalHeight ){
        var ww = this.json.handwritingWidth;
        var wh = this.json.handwritingHeight;
        if( this.isNumber(ww)  && !this.isNumber(wh) ){
            return {
                width : Math.min( naturalWidth, parseInt( ww )  ) + "px",
                height : "auto"
            }
        }else if( !this.isNumber(ww)  && this.isNumber(wh) ){
            return {
                width : "auto",
                height : Math.min( naturalHeight, parseInt( wh )  ) + "px"
            }
        }else if( this.isNumber(ww)  && this.isNumber(wh) ){
            var flag = ( naturalWidth / parseInt(ww) ) > ( naturalHeight / parseInt(wh) );
            if( flag ){
                return {
                    width : Math.min( naturalWidth, parseInt( ww )  ) + "px",
                    height : "auto"
                }
            }else{
                return {
                    width : "auto",
                    height : Math.min( naturalHeight, parseInt( wh )  ) + "px"
                }
            }
        }
    },
    loadMediaOpinion_video_show: function(att, task, container){

    },
    loadMediaOpinion_voice_show: function(att, task, container){
        //var node = new Element("audio").inject(this.node);
        var url = this.getMediaOpinionUrl(att);
        var div = new Element("div", {"styles": {"overflow": "hidden"}}).inject(container || this.node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "28px",
                "height": "28px"
            },
            "text": task.person.substring(0, task.person.indexOf("@"))+"("+task.completedTime+")"
        }).inject(div);

        var node = new Element("audio", {"loop": false, "controls": true}).inject(div);
        node.set("src", url);
        //this.audioNode.play();
    },

    loadWorkLogTable: function( list, container ){
        if( list ){
            list.each(function(log, idx){
                this.loadWorkLogLine_table(log, idx, container );
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkShow(log)) this.loadWorkLogLine_table(log, idx, container );
            }.bind(this));
        }
    },
    loadWorkLogLine_table: function(log, idx, container){
        if (!log.readList) log.readList = [];
        if (!log.readCompletedList) log.readCompletedList = [];
        if (log.taskCompletedList.length || log.readList.length || log.readCompletedList.length || (this.json.isTask && log.taskList.length)){
            var logActivityNode = new Element("div", {"styles": this.form.css.logActivityNode}).inject( container || this.node);
            var titleNode = new Element("div", {"styles": this.form.css.logActivityTitleNode}).inject(logActivityNode);
            var childNode = new Element("div", {"styles": this.form.css.logActivityChildNode}).inject(logActivityNode);

            var iconNode = new Element("div", {"styles": this.form.css.logActivityIconNode}).inject(titleNode);
            var fromAvtivityNode = new Element("div", {"styles": this.form.css.logActivityFromNode}).inject(titleNode);
            var arrowNode = new Element("div", {"styles": this.form.css.logActivityArrowNode}).inject(titleNode);
            var arrivedAvtivityNode = new Element("div", {"styles": this.form.css.logActivityArrivedNode}).inject(titleNode);

            var timeNode = new Element("div", {"styles": this.form.css.logActivityTimeNode}).inject(titleNode);

            if (log.connected){
                iconNode.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/ok14.png)");
            }else{
                iconNode.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
            }
            fromAvtivityNode.set("html", "<b>"+log.fromActivityName+"</b>");
            if (log.arrivedActivityName){
                arrowNode.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/right.png)");
                arrivedAvtivityNode.set("html", "<b>"+log.arrivedActivityName+"</b>");
                timeNode.set("html", "<b>"+MWF.xApplication.process.Xform.LP.begin+": </b>"+log.fromTime+"<br/><b>"+MWF.xApplication.process.Xform.LP.end+": </b>"+log.arrivedTime)

            }else{
                timeNode.set("html", "<b>"+MWF.xApplication.process.Xform.LP.begin+": </b>"+log.fromTime)
            }

            if ((idx % 2)===0){
                logActivityNode.setStyles(this.form.css.logActivityNode_even);
                titleNode.setStyles(this.form.css.logActivityTitleNode_even);
            }


            var taskTable = new Element("table", {
                "styles": this.form.css.logTableTask,
                "border": "0",
                "cellSpacing": "0",
                "cellpadding": "3px",
                "width": "100%"
            }).inject(childNode);
            var tr = taskTable.insertRow(0).setStyles(this.form.css.logTableTaskTitleLine);

            var td = tr.insertCell(0).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.person);
            td = tr.insertCell(1).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.department);
            td = tr.insertCell(2).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.startTime);
            td = tr.insertCell(3).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.completedTime);
            td = tr.insertCell(4).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.route);
            td = tr.insertCell(5).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.opinion);

            log.taskCompletedList.each(function(taskCompleted){
                if (this.checkListShow(log, taskCompleted)) this.loadTaskLine_table(taskCompleted, taskTable, log, false);
            }.bind(this));

            if (this.json.isShowRead!==false){
                var readNames = [];
                var readCompletedNames = [];
                if (log.readList && log.readList.length){
                    log.readList.each(function(read){
                        readNames.push(MWF.name.cn(read.person));
                    });
                }
                if (log.readCompletedList && log.readCompletedList.length){
                    log.readCompletedList.each(function(read){
                        readCompletedNames.push(MWF.name.cn(read.person));
                    });
                }
                this.loadReadLine_default(readNames, readCompletedNames, childNode);
            }

            if (this.json.isTask){
                log.taskList.each(function(task){
                    if (this.checkListShow(log, task)) this.loadTaskLine_table(task, taskTable, log, true);
                }.bind(this));
            }
        }

    },
    loadTaskLine_table: function(task, table, log, isTask){
        var style = "logTableTaskLine";
        if (isTask) style = "logTableTaskLine_task";
        var tr = table.insertRow(table.rows.length);
        var td = tr.insertCell(0).setStyles(this.form.css[style]);
        td.set("text", task.person.substring(0, task.person.indexOf("@")) || "");
        td = tr.insertCell(1).setStyles(this.form.css[style]);
        td.set("text", task.unit.substring(0, task.unit.indexOf("@")) || "");
        td = tr.insertCell(2).setStyles(this.form.css[style]);
        td.set("text", task.startTime || "");
        td = tr.insertCell(3).setStyles(this.form.css[style]);
        td.set("text", task.completedTime || "");
        td = tr.insertCell(4).setStyles(this.form.css[style]);
        td.set("text", task.routeName || "");
        td = tr.insertCell(5).setStyles(this.form.css[style]);
        td.set("html", "<div style='line-height: 28px; float:left'>" + task.opinion || ""+"</div>");

        if (task.mediaOpinion){
            var mediaIds = task.mediaOpinion.split(",");
            var atts = [];
            if (this.form.businessData.attachmentList){
                this.form.businessData.attachmentList.each(function(att){
                    if (att.site==="$mediaOpinion"){
                        if (mediaIds.indexOf(att.id)!==-1) atts.push(att);
                    }
                }.bind(this));
            }
            if (atts.length) this.loadMediaOpinion(atts, td.getFirst(), "table");
        }
    },



    loadWorkLogText: function(list, container){
        this.lineClass = "logTaskNode";
        if( list ){
            list.each(function(log, idx){
                this.loadWorkLogLine_text(log, idx, container);
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkShow(log)) this.loadWorkLogLine_text(log, idx, container);
            }.bind(this));
        }
    },
    loadWorkLogLine_text: function(log, idx, container){
        log.taskCompletedList.each(function(taskCompleted){
            if (this.checkListShow(log, taskCompleted)) this.loadTaskLine_text(taskCompleted, container || this.node, log, false);
        }.bind(this));

        if (this.json.isTask){
            log.taskList.each(function(task){
                if (this.checkListShow(log, task)) this.loadTaskLine_text(task, container || this.node, log, true);
            }.bind(this));
        }
    },
    loadTaskLine_text: function(task, node, log, isTask){
        this.loadTaskLine_default(task, node, log, isTask, "0px", false, true);
    },

    checkShow: function(log){
        var flag = true;
        if (this.json.filterScript && this.json.filterScript.code){
            this.form.Macro.environment.log = log;
            this.form.Macro.environment.list = null;
            flag = this.form.Macro.exec(this.json.filterScript.code, this);
        }else{
            if (this.json.filterActivity.length){
                filterActivitys = this.json.filterActivity;
                flag = (filterActivitys.indexOf(log.fromActivityName)!==-1);
            }
            if (this.json.filterActivityAlias){
                if (this.json.filterActivityAlias.length){
                    filterActivityAlias = this.json.filterActivityAlias;
                    flag = ((log.fromActivityAlias) && filterActivityAlias.indexOf(log.fromActivityAlias)!==-1);
                }
            }
            if (this.json.filterPerson.length){
                flag = false;
                filterPersons = this.json.filterPerson;
                var tmpTaskCompletedList = [];
                log.taskCompletedList.each(function(taskCompleted){
                    if ((filterPersons.indexOf(taskCompleted.person)!==-1) || (filterPersons.indexOf(taskCompleted.identity)!==-1)){
                        tmpTaskCompletedList.push(taskCompleted);
                    }
                }.bind(this));
                if (tmpTaskCompletedList.length){
                    //log.taskCompletedList = [];
                    //log.taskCompletedList = tmpTaskCompletedList;
                    flag = true;
                }
            }
            if (this.json.filterRoute.length){
                filterRoutes = this.json.filterRoute;
                flag = (filterRoutes.indexOf(log.routeName)!==-1);
            }
        }
        return flag;
    },
    checkListShow: function(log, list){
        var flag = true;
        if (this.json.filterScript && this.json.filterScript.code){
            this.form.Macro.environment.log = log;
            this.form.Macro.environment.list = list;
            flag = this.form.Macro.exec(this.json.filterScript.code, this);
        }else{
            if (this.json.filterPerson.length){
                flag = ((filterPersons.indexOf(list.person)!==-1)|| (filterPersons.indexOf(list.identity)!==-1));
            }
        }
        return flag;
    },

    loadWorkLogDefault: function(list, container){
        //var text = this.json.textStyle;
        if( list ){
            list.each(function(log, idx){
                this.loadWorkLogLine_default(log, idx, container);
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkShow(log)) this.loadWorkLogLine_default(log, idx, container);
            }.bind(this));
        }
    },
    loadWorkLogLine_default: function(log, idx, container){
        if (!log.readList) log.readList = [];
        if (!log.readCompletedList) log.readCompletedList = [];
        if (log.taskCompletedList.length || log.readList.length || log.readCompletedList.length || (this.json.isTask && log.taskList.length)) {
            var logActivityNode = new Element("div", {"styles": this.form.css.logActivityNode}).inject(container || this.node);
            var titleNode = new Element("div", {"styles": this.form.css.logActivityTitleNode}).inject(logActivityNode);
            var childNode = new Element("div", {"styles": this.form.css.logActivityChildNode}).inject(logActivityNode);

            var iconNode = new Element("div", {"styles": this.form.css.logActivityIconNode}).inject(titleNode);
            var fromAvtivityNode = new Element("div", {"styles": this.form.css.logActivityFromNode}).inject(titleNode);
            var arrowNode = new Element("div", {"styles": this.form.css.logActivityArrowNode}).inject(titleNode);
            var arrivedAvtivityNode = new Element("div", {"styles": this.form.css.logActivityArrivedNode}).inject(titleNode);

            var readActionNode = new Element("div", {"styles": this.form.css.logActivityReadActionNode}).inject(titleNode);

            var timeNode = new Element("div", {"styles": this.form.css.logActivityTimeNode}).inject(titleNode);

            if (log.connected) {
                iconNode.setStyle("background-image", "url(/x_component_process_Xform/$Form/" + this.form.options.style + "/icon/ok14.png)");
            } else {
                iconNode.setStyle("background-image", "url(/x_component_process_Xform/$Form/" + this.form.options.style + "/icon/rightRed.png)");
            }
            fromAvtivityNode.set("html", "<b>" + log.fromActivityName + "</b>");
            if (log.arrivedActivityName) {
                arrowNode.setStyle("background-image", "url(/x_component_process_Xform/$Form/" + this.form.options.style + "/icon/right.png)");
                arrivedAvtivityNode.set("html", "<b>" + log.arrivedActivityName + "</b>");
                timeNode.set("html", "<b>" + MWF.xApplication.process.Xform.LP.begin + ": </b>" + log.fromTime + "<br/><b>" + MWF.xApplication.process.Xform.LP.end + ": </b>" + log.arrivedTime)

            } else {
                timeNode.set("html", "<b>" + MWF.xApplication.process.Xform.LP.begin + ": </b>" + log.fromTime)
            }

            // if ((log.readList && log.readList.length) || (log.readCompletedList && log.readCompletedList.length)){
            //     readActionNode.set("text", MWF.xApplication.process.Xform.LP.worklogRead);
            // }

            if ((idx % 2) === 0) {
                logActivityNode.setStyles(this.form.css.logActivityNode_even);
                titleNode.setStyles(this.form.css.logActivityTitleNode_even);
            }

            log.taskCompletedList.each(function (taskCompleted) {
                if (this.checkListShow(log, taskCompleted)) this.loadTaskLine_default(taskCompleted, childNode, log, false);
            }.bind(this));

            if (this.json.isShowRead!==false){
                var readNames = [];
                var readCompletedNames = [];
                if (log.readList && log.readList.length){
                    log.readList.each(function(read){
                        readNames.push(MWF.name.cn(read.person));
                    });
                }
                if (log.readCompletedList && log.readCompletedList.length){
                    log.readCompletedList.each(function(read){
                        readCompletedNames.push(MWF.name.cn(read.person));
                    });
                }
                //if (readCompletedNames.length)
                this.loadReadLine_default(readNames, readCompletedNames, childNode);
            }

            if (this.json.isTask) {
                log.taskList.each(function (task) {
                    if (this.checkListShow(log, task)) this.loadTaskLine_default(task, childNode, log, true);
                }.bind(this));
            }
        }
    },
    loadReadLine_default: function(readNames, readCompletedNames, node){
        var html = "";
        var logReadNode = new Element("div", {"styles": this.form.css.logReadTextNode}).inject(node);
        if (readNames.length){
            var readStrTitle = readNames.join(", ");
            var readStr = (readNames.length>20) ? readNames.slice(0,20).join(", ") : readStrTitle;
            html = "<span style='color: #0000ff'>"+(this.json.showReadTitle || MWF.xApplication.process.Xform.LP.showReadTitle)+": </span>"+readStr+"<span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";

            //var logReadPersonNode = new Element("div", {"styles": this.form.css.logReadTextNode}).inject(logReadNode);
        }
        if (readCompletedNames.length){
            var readCompletedStrTitle = readCompletedNames.join(", ");
            var readCompletedStr = (readCompletedNames.length>20) ? readCompletedNames.slice(0,20).join(", ") : readCompletedStrTitle;
            html += "<span style='color: #0000ff'>"+(this.json.showReadCompletedTitle || MWF.xApplication.process.Xform.LP.showReadCompletedTitle)+": </span>"+readCompletedStr+"<span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        }
        if (html) logReadNode.set("html", html);
    },
    loadTaskLine_default: function(task, node, log, isTask, margin, isZebra, nodeStyle){
        var style = "logTaskNode";
        var textStyle = "logTaskFloatTextNode";
        if (nodeStyle){
            style = "logTaskTextNode";
            textStyle = "logTaskTextNode";
        }
        var logTaskNode = new Element("div", {"styles": this.form.css[style]}).inject(node);
        var iconNode = new Element("div", {"styles": this.form.css.logTaskIconNode}).inject(logTaskNode);
        var textNode = new Element("div", {"styles": this.form.css[textStyle]}).inject(logTaskNode);

        if (isZebra){
            logTaskNode.setStyles(this.form.css[this.lineClass]);
            if (this.lineClass === "logTaskNode"){
                this.lineClass = "logTaskNode_even";
            }else{
                this.lineClass = "logTaskNode";
            }
        }

        if (margin) iconNode.setStyle("margin-left", margin);
        var left = iconNode.getStyle("margin-left").toInt();
        left = left + 28;
        var html;
        var company = "";
        if (!isTask){
            company = (task.unitList) ? task.unitList[task.unitList.length-1] : "";

            var html = this.json.textStyle;
            if (this.json.textStyleScript && this.json.textStyleScript.code){
                this.form.Macro.environment.log = log;
                this.form.Macro.environment.list = null;
                html = this.form.Macro.exec(this.json.textStyleScript.code, this);
            }

            html = html.replace(/\{person\}/g, task.person.substring(0, task.person.indexOf("@")));
            html = html.replace(/\{department\}/g, task.unit.substring(0, task.unit.indexOf("@")));
            html = html.replace(/\{route\}/g, task.routeName);
            html = html.replace(/\{time\}/g, task.completedTime);
            html = html.replace(/\{date\}/g, new Date().parse(task.completedTime).format("%Y-%m-%d"));
            html = html.replace(/\{opinion\}/g, task.opinion);
            html = html.replace(/\{company\}/g, company.substring(0, company.indexOf("@")));
            html = html.replace(/\{startTime\}/g, task.startTime);
            html = html.replace(/\{startDate\}/g, new Date().parse(task.startTime).format("%Y-%m-%d"));
            html = html.replace(/\{activity\}/g, log.fromActivityName);
            html = html.replace(/\{arrivedActivity\}/g, log.arrivedActivityName);
            //var html = MWF.xApplication.process.Xform.LP.nextUser + task.person+"("+task.department+")" +", "+
            //    MWF.xApplication.process.Xform.LP.selectRoute + ": [" + task.routeName + "], " +
            //    MWF.xApplication.process.Xform.LP.submitAt + ": " + task.completedTime+ ", " +
            //    MWF.xApplication.process.Xform.LP.idea + ": <font style=\"color: #00F\">" + (task.opinion || "")+"</font>";
            textNode.set("html", html);
            if (task.mediaOpinion){
                var mediaIds = task.mediaOpinion.split(",");
                var atts = [];
                if (this.form.businessData.attachmentList){
                    this.form.businessData.attachmentList.each(function(att){
                        if (att.site==="$mediaOpinion"){
                            if (mediaIds.indexOf(att.id)!==-1) atts.push(att);
                        }
                    }.bind(this));
                }
                if (atts.length) this.loadMediaOpinion(atts, textNode, "default");
            }
        }else{
            //company = task.unitList[task.unitList.length-1];
            html = task.person.substring(0, task.person.indexOf("@"))+"("+task.unit.substring(0, task.unit.indexOf("@"))+")" + MWF.xApplication.process.Xform.LP.processing+", "+
                MWF.xApplication.process.Xform.LP.comeTime + ": " + task.startTime;
            textNode.set("html", html);
            iconNode.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
        }
    },
    loadMediaOpinion: function(atts, node, type){
        atts.each(function(att){
            if (!att.type) att.type = "image";
            if (att.type.indexOf("image")!==-1){
                if( type === "table" && !layout.mobile ){
                    this.loadMediaOpinion_image_tooltip(att, node);
                }else{
                    this.loadMediaOpinion_image(att, node);
                }
            }else if(att.type.indexOf("video")!==-1){
                this.loadMediaOpinion_video(att, node);
            }else if(att.type.indexOf("audio")!==-1){
                this.loadMediaOpinion_voice(att, node);
            }else{
                if( type === "table" && !layout.mobile ){
                    this.loadMediaOpinion_image_tooltip(att, node);
                }else{
                    this.loadMediaOpinion_image(att, node);
                }
            }
        }.bind(this));
    },
    getMediaOpinionUrl: function(att){
        var action = MWF.Actions.get("x_processplatform_assemble_surface");
        var url = action.action.actions["getAttachmentStream"].uri;
        if (this.form.businessData.workCompleted){
            url = action.action.actions["getWorkcompletedAttachmentStream"].uri;
            url = url.replace("{id}", att.id);
            url = url.replace("{workCompletedId}", this.form.businessData.workCompleted.id);
        }else{
            url = url.replace("{id}", att.id);
            url = url.replace("{workid}", this.form.businessData.work.id);
        }
        url = action.action.address+url;
        return url;
    },
    loadMediaOpinion_image_tooltip : function(att, node){
        var iconNode = new Element("div", {"styles": this.form.css.logMediaIcon}).inject(node.getParent());
        iconNode.setStyle("background-image", "url('"+this.form.path+this.form.options.style+"/icon/image.png')");
        iconNode.set("title", MWF.xApplication.process.Xform.LP.mediaOpinion_image);
        if( !this.MTooltipsLoaded )o2.xDesktop.requireApp("Template", "MTooltips", null, false);
        this.MTooltipsLoaded = true;
        var tooltip = new MTooltips(this.form.app.content, iconNode, this.form.app, null, {
            axis : "y", "delay" : 350,
            nodeStyles : {
                "max-width" : "800px"
            }
        });
        tooltip.contentNode = new Element("div",{
            styles : { width : "100%" , height : "100%" }
        });
        var img = new Element("img", {
            "src": this.getMediaOpinionUrl(att),
            "events" : {
                load : function(ev){
                    var nh = ev.target.naturalHeight;
                    var nw = ev.target.naturalWidth;
                    if( this.isNumber( this.json.handwritingWidth ) || this.isNumber( this.json.handwritingHeight ) ){
                        var size = this.getImageSize( nw, nh );
                        ev.target.setStyles(size);
                    }else{
                        var x = Math.min(nw, 800);
                        ev.target.setStyles({"width": ""+ x +"px"});
                    }
                }.bind(this)
            }
        }).inject(tooltip.contentNode);
    },
    loadMediaOpinion_image: function(att, node){
        var iconNode = new Element("div", {"styles": this.form.css.logMediaIcon}).inject(node.getParent());
        iconNode.setStyle("background-image", "url('"+this.form.path+this.form.options.style+"/icon/image.png')");
        iconNode.set("title", MWF.xApplication.process.Xform.LP.mediaOpinion_image);
        iconNode.addEvents({
            "click": function(e){
                if (e.target.mediaOpinionContentNode) return "";
                var url = this.getMediaOpinionUrl(att);
                // if (this.mediaOpinionContentNode){
                //     this.mediaOpinionContentNode.destroy();
                //     this.mediaOpinionContentNode = null;
                // }

                var imgNode = new Element("div", {"styles": this.form.css.logMediaOpinionContent}).inject(node.getParent(),"after");
                if (!layout.mobile) imgNode.setStyle("margin-left", "40px");
                //this.mediaOpinionContentNode = imgNode;
                e.target.mediaOpinionContentNode = imgNode;

                var imgCloseNode = new Element("div", {"styles": this.form.css.logMediaOpinionContentClose}).inject(imgNode);
                imgCloseNode.addEvent("click", function(){ imgNode.destroy(); e.target.mediaOpinionContentNode = "";});

                var imgAreaNode = new Element("div", {"styles": this.form.css.logMediaOpinionContentArea}).inject(imgNode);

                //var height = 260;
                //var width = 390;
                var width;
                if (layout.mobile){
                    width = node.getParent().getParent().getSize().x-2;
                    //height = width*2/3;
                }else{
                    var pNode = node.getParent().getParent();
                    var offset = imgNode.getPosition( pNode );
                    width = Math.min( pNode.getSize().x - offset.x - 42, 800 );
                }

                var img = new Element("img", {
                    "src": url,
                    "styles" : { width : width+"px" }, //最开始限定一下宽度，不要让页面抖动
                    "events" : {
                        load : function(ev){
                            var nh = ev.target.naturalHeight;
                            var nw = ev.target.naturalWidth;
                            if( !layout.mobile && ( this.isNumber( this.json.handwritingWidth ) || this.isNumber( this.json.handwritingHeight ) ) ){
                                var size = this.getImageSize( nw, nh );
                                img.setStyles(size);
                                imgNode.setStyles(size);
                            }else{
                                var x = Math.min(nw, width);
                                img.setStyles({"width": ""+ x +"px"}); //最终的宽度
                                imgNode.setStyles({"width": ""+ x +"px"});
                            }
                        }.bind(this)
                    }
                }).inject(imgAreaNode);

                //img.setStyles({"width": ""+width+"px"});
                //imgNode.setStyles({"width": ""+width+"px"});

                //img.setStyles({"width": ""+width+"px", "height": ""+height+"px"});
                //imgNode.setStyles({"width": ""+width+"px", "height": ""+height+"px"});


                // var size = img.getSize();
                // var x_y = size.x/size.y;
                //if (size.y>260){

                //}

                var p = iconNode.getPosition(this.form.app.content);
                var s = iconNode.getSize();
                var size = (layout.mobile) ? {"x": width, "y": height}: imgNode.getSize();
                var contentSize = this.form.app.content.getSize();
                var contentScroll = (layout.mobile) ? document.body.getFirst().getScroll() : {"x": 0, "y": 0};
                var y = p.y-size.y;
                var x = p.x+s.x/2-size.x/2;

                if (x<10) x = 10;
                if (x+size.x>contentSize.x-10) x = contentSize.x-size.x-20;
                if (y+size.y>contentSize.y-10) y = contentSize.y-size.y-20;
                if (y<10) y = 10;
                y=y+contentScroll.y;

                if (layout.mobile){
                    x = 0;
                }
                // imgNode.setStyles({
                //     "top": ""+y+"px",
                //     "left": ""+x+"px"
                // });



                // this.hideMediaOpinionNodeFun = this.hideMediaOpinionNode.bind(this);
                // this.form.node.addEvent("mousedown", this.hideMediaOpinionNodeFun);

                e.stopPropagation();
            }.bind(this)
        });
    },
    hideMediaOpinionNode: function(){
        if (this.mediaOpinionContentNode){
            this.mediaOpinionContentNode.destroy();
            this.mediaOpinionContentNode = null;
        }
        // if (this.hideMediaOpinionNodeFun) this.form.node.removeEvent("click", this.hideMediaOpinionNodeFun);
    },
    loadMediaOpinion_video: function(att, node){
        var iconNode = new Element("div", {"styles": this.form.css.logMediaIcon}).inject(node.getParent());
        iconNode.setStyle("background-image", "url('"+this.form.path+this.form.options.style+"/icon/video.png')");
        iconNode.set("title", MWF.xApplication.process.Xform.LP.mediaOpinion_video);
    },
    loadMediaOpinion_voice: function(att, node){
        // this.form.css.logMediaIcon.width = "60px";
        // var iconNode = new Element("audio", {
        //     "styles": this.form.css.logMediaIcon,
        //     "controls": true,
        //     "html": "<source src='"+url+"' type='audio/wav'></source>"
        // }).inject(node.getParent());

        var iconNode = new Element("div", {"styles": this.form.css.logMediaIcon}).inject(node.getParent());
        iconNode.setStyle("background-image", "url('"+this.form.path+this.form.options.style+"/icon/voice.png')");
        iconNode.set("title", MWF.xApplication.process.Xform.LP.mediaOpinion_voice);

        iconNode.addEvents({
            "click": function(e){
                var url = this.getMediaOpinionUrl(att);

                this.audioNode = new Element("audio", {"loop": false});
                this.audioNode.set("src", url);
                this.audioNode.play();

                e.stopPropagation();
            }.bind(this)
        });
    }
});