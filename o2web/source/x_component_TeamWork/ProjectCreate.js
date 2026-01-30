MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.ProjectCreate = new Class({
    Extends: MWF.xApplication.TeamWork.Common.Popup,
    options:{
        "closeByClickMask" : false
    },
    open: function (e) { 
        
        //设置css 和 lp等
        var css = this.css;
        this.path = "../x_component_TeamWork/$ProjectCreate/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();
        if(css) this.css = Object.merge(  css, this.css );

        this.lp = this.app.lp.projectCreate;

        this.fireEvent("queryOpen");
        this.isNew = false;
        this.isEdited = false;
        this._open();
        this.fireEvent("postOpen");
    },
    projectClose:function(){
        this.close()
    },
    // getData:function(callback){
    //     var id = this.data.id;
    //     if(this.data.id){
    //         this.rootActions.ProjectAction.get(id,function(json){
    //             this.data = json.data;
    //             if(callback)callback()
    //         }.bind(this))
    //     }else{
    //         this.data.name = "";
    //         this.data.source = "";
    //         if(callback)callback()
    //     }
        
    // },
    selectPerson: function( type,count,value,callback ) { 
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        this.fireEvent("querySelect", this);
        var options = {
            "type": type,
            "title": "选人",
            "count": count,
            "values": value || [],
            "onComplete": function (items) { 
                var personList = [];
                items.each(function (item) {
                    personList.push(item.data.distinguishedName);

                });
                if(callback)callback(personList);
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.app.content, options);
    },
    blurValue:function(e){
        var _name = e.currentTarget.get('data-o2-element');
        var _value = e.currentTarget.get('value');
        this.data[_name] = _value;
        if(_value!=""){
            e.currentTarget.setStyles({"border":"1px solid #cccccc"})
        }
    },
    projectCreate:function(){ 
        var flag = true;
        
        if(this.title.get('value') ==""){
            this.title.setStyles({
                "border":'1px solid #ff0000'
            })
            flag = false
        }
        
        if(flag){
            //创建
            //var data = {}
            var _valueList = ['title','source','objective','description'];
            _valueList.forEach(function(v){
                this.data[v] = this[v].get("value")
            }.bind(this));

            var _data = Object.clone(this.data);
            _data.startTime = _data.startTime + " 00:00:00"

            // if(this.data.groups.length>0){
            //     _data.groups = [];
            //     this.data.groups.forEach(function(d){
            //         _data.groups.push(d.id)
            //     })
            // }

            this.rootActions.ProjectAction.save(_data,function(json){ 
                this.close(json);
            }.bind(this));
        }
    },
    
    _createTableContent: function () {
        
        //初始化值
        this.data.title = '';
        this.data.source = '';
        this.data.objective = '';
        this.data.description = '';
        this.data.participantList = [];
        //this.data.groups = [];
        this.data.endTime = '';

        var now = new Date()
        this.data.startTime = this.app.formatDateV2(now,"date");
        this.data.endTime = "";
        var url = this.path+this.options.style+"/view.html";
        this.formTableArea.setStyles({"width":"100%","height":"100%"});
        this.formTableArea.loadAll({"html":url,css:this.path + this.options.style  + "/" + "style.css"},{"bind": {"lp": this.lp,"data":this.data}, "module": this},function(){ 

            //参与人
            if(this.data.participantList){
                var _person = [];
                this.data.participantList.forEach(function(p){
                    _person.push(p.split("@")[0])
                })
                this.participantList.set("text",_person.join(','));
            }
            this.participantList.addEvents({ 
                click:function(){ 
                    this.selectPerson("person",0,this.data.participantList,function(personList){
                        this.data.participantList = personList;
                        var _person = [];
                        this.data.participantList.forEach(function(p){
                            _person.push(p.split("@")[0])
                        })
                        this.participantList.set("text",_person.join(','));
                    }.bind(this))
                }.bind(this)
            });
            
            
            this.endTime.addEvents({
                click:function(){
                    var opt = {
                        type:"date"
                    };
                    this.app.selectCalendar(this.endTime,this.container,opt,function(json){
                        
                        
                        if(json.action == "ok"){
                            this.endTime.set("text",json.dateString);
                            this.endTime.setStyles({"border":"1px solid #cccccc"})
                            this.data.endTime = json.dateString + " 23:59:59"
                        }else if(json.action == "clear"){
                            this.endTime.set("text","");
                            this.data.endTime = ''
                        }
                        

                    }.bind(this))
                }.bind(this)
            });

            
            
        }.bind(this))
        
    }
    
});
