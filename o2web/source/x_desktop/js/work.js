layout.addReady(function(){
    (function(layout){
        var uri = new URI(window.location.href);
        var options = uri.get("data");
        var appNames = "process.Work";
        var statusObj = null;

        var _load = function(){
            // layout.message = new MWF.xDesktop.MessageMobile();
            // layout.message.load();
            layout.apps = [];
            //layout.node = $("layout");
            layout.node = $("layout") || $("appContent") || document.body;
            var appName=appNames, m_status=statusObj, option=options;

            var topWindow = window.opener;
            if (topWindow){
                try{
                    if (!appName) appName = topWindow.layout.desktop.openBrowserApp;
                    if (!m_status) m_status = topWindow.layout.desktop.openBrowserStatus;
                    if (!option)  option = topWindow.layout.desktop.openBrowserOption;
                }catch(e){}
            }

            if (options.job && !options.workid && !options.draftid && !options.draft){
                var workData = null;
                o2.Actions.get("x_processplatform_assemble_surface").listWorkByJob(options.job, function(json){
                    if (json.data) workData = json.data;
                }.bind(this), null, false);

                if (workData){
                    var len = workData.workList.length + workData.workCompletedList.length;
                    if (len){
                        if (len>1 && options.choice){
                            layout.node.empty();
                            layout.node.setStyle("background", "#f1f1f1");
                            var node = new Element("div", {
                                "styles": {"font-size": "18px", "text-align": "center", "font-weight": "bold", "margin": "auto", "height": "30px", "width": "90%", "max-width": "600px", "padding": "40px 0px 20px 20px"},
                                "text": o2.LP.widget.choiceWork
                            }).inject(layout.node);
                            var node = new Element("div", {"styles": {"margin": "auto", "padding": "0px 20px", "width": "90%", "max-width": "600px"}}).inject(layout.node);
                            workData.workList.each(function(work){
                                var workNode = new Element("div", {
                                    "styles": {
                                        "background": "#ffffff",
                                        "border-radius": "10px",
                                        "clear": "both",
                                        "margin-bottom": "10px",
                                        "height": "40px",
                                        "padding": "10px 10px"
                                    }
                                }).inject(node);
                                var html = "<div style='height: 40px; width: 40px; float: left; background: url(../x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
                                    "<div style='height: 40px; width: 40px; float: right'><div class='MWFAction' style='height: 20px; width: 40px; margin-top: 10px; border: 1px solid #999999; border-radius: 5px;text-align: center; cursor: pointer'>"+o2.LP.widget.open+"</div></div>"+
                                    "<div style='height: 20px; line-height: 20px; margin: 0px 40px'>"+work.title+"</div>" +
                                    "<div style='margin: 0px 40px'><div style='color:#999999; float: left; margin-right: 10px'>"+work.activityName+"</div>" +
                                    "<div style='color:#999999; float: left; margin-right: 10px'>"+work.activityArrivedTime+"</div>" +
                                    "<div style='color:#999999; float: left; margin-right: 10px'>"+(work.manualTaskIdentityText || "")+"</div></div>";
                                workNode.set("html", html);
                                var action = workNode.getElement(".MWFAction");
                                action.store("work", work);
                                action.addEvent("click", function(e){
                                    var work = e.target.retrieve("work");
                                    options.workId = work.id;
                                    layout.node.empty();
                                    layout.openApplication(null, appName, option||{}, m_status);
                                    //if (work) this.openWork(work.id, null, work.title, options);

                                }.bind(this));

                            }.bind(this));
                            workData.workCompletedList.each(function(work){
                                var workNode = new Element("div", {
                                    "styles": {
                                        "background": "#ffffff",
                                        "border-radius": "10px",
                                        "clear": "both",
                                        "margin-bottom": "10px",
                                        "height": "40px",
                                        "padding": "10px 10px"
                                    }
                                }).inject(node);
                                var html = "<div style='height: 40px; width: 40px; float: left; background: url(../x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
                                    "<div style='height: 40px; width: 40px; float: right'><div class='MWFAction' style='height: 20px; width: 40px; margin-top: 10px; border: 1px solid #999999; border-radius: 5px;text-align: center; cursor: pointer'>"+o2.LP.widget.open+"</div></div>"+
                                    "<div style='height: 20px; line-height: 20px; margin: 0px 40px'>"+work.title+"</div>" +
                                    "<div style='margin: 0px 40px'><div style='color:#999999; float: left; margin-right: 10px'>+o2.LP.widget.workcompleted+</div>" +
                                    "<div style='color:#999999; float: left; margin-right: 10px'>"+work.completedTime+"</div>";
                                workNode.set("html", html);
                                var action = workNode.getElement(".MWFAction");
                                action.store("work", work);
                                action.addEvent("click", function(e){
                                    var work = e.target.retrieve("work");
                                    options.workCompletedId = work.id;
                                    layout.node.empty();
                                    layout.openApplication(null, appName, option||{}, m_status);
                                }.bind(this));

                            }.bind(this));

                        }else{
                            if (workData.workList.length){
                                options.workId =  workData.workList[0].id;
                            }else{
                                options.workCompletedId =  workData.workCompletedList[0].id;
                            }
                            layout.openApplication(null, appName, option||{}, m_status);
                        }
                    }
                }
            }else{
                if (options.draft) options.draft = JSON.parse(options.draft);
                layout.openApplication(null, appName, option||{}, m_status);
            }
        };
        _load();
    })(layout);
});
