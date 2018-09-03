MWF.xApplication.Forum.IS_LOGIN = false;
MWF.xApplication.Forum.ForumSetting = null;
MWF.xApplication.Forum.RoleInfoList = [];
MWF.xApplication.Forum.PermissionInfoList = [];
MWF.xApplication.Forum.isBBSSystemAdmin = false;

MWF.xApplication.Forum.Access = new Class({
    //SECTION_SUBJECT_MANAGEMENT_  主题管理
    //SECTION_REPLY_MANAGEMENT_  回帖管理
    //SECTION_SECTION_MANAGER_ 子板块增删改
    //SECTION_PERMISSION_MANAGEMENT_ 权限管理
    //SECTION_CONFIG_MANAGEMENT_ 参数配置
    //"SECTION_GUEST_"; //查看范围
    //"SECTION_SUBJECT_PUBLISHER_"; //发帖范围
    //"SECTION_SUBJECT_AUDITOR_"; //发帖审核
    //"SECTION_REPLY_PUBLISHER_"; //回复范围
    //"SECTION_REPLY_AUDITOR_"; //回复审核
    //"SECTION_RECOMMENDER_"; //推荐
    //"SECTION_SUBJECT_STICK_"; //置顶
    //"SECTION_SUBJECT_CREAM_"; //精华
    initialize: function ( actions, lp, userName ) {
        this.actions = actions;
        this.lp = lp;
        this.userName = userName || layout.desktop.session.user.distinguishedName || "";
        this.anonymous = layout.desktop.session.user.name == "anonymous";
        if( this.anonymous ){
            MWF.Actions.get("x_organization_assemble_personal").getRegisterMode(function(json){
                this.signUpMode = json.data.value
            }.bind(this), null ,false)
        }

        if( !MWF.xApplication.Forum.ForumSetting ){
            MWF.xApplication.Forum.ForumSetting = {};
            this.actions.listCategoryAll(function (json) {
                if( !json.data )json.data = [];
                json.data.each(function (d) {
                    MWF.xApplication.Forum.ForumSetting[d.id] = d;
                }.bind(this))
            }.bind(this))
        }


    },
    login : function( callback ){
        if( !MWF.xApplication.Forum.IS_LOGIN ){
            this.actions.login( {}, function( json ){
                MWF.xApplication.Forum.RoleInfoList = json.data ? json.data.roleInfoList : [];
                MWF.xApplication.Forum.PermissionInfoList = json.data ? json.data.permissionInfoList : [];
                MWF.xApplication.Forum.IS_LOGIN = true;
                MWF.xApplication.Forum.isBBSSystemAdmin = json.data ?　json.data.isBBSSystemAdmin : false;
                if( callback )callback();
            }.bind(this))
        }else{
            if( callback )callback();
        }
    },
    isAnonymous : function(){
      return this.anonymous;
    },
    isAnonymousDynamic : function(){
      var flag = true;
        MWF.Actions.get("x_organization_assemble_authentication").authentication( function( json ){
            if (json.data.tokenType == "anonymous"){
                this.userName = layout.desktop.session.user.distinguishedName = "anonymous";
                flag = true;
            }else{
                this.userName = layout.desktop.session.user.distinguishedName = json.data.distinguishedName;
                flag = false;
            }
        }.bind(this), null, false );
        return flag;
    },
    isAdmin : function(){
        return MWF.AC.isBBSManager() || MWF.xApplication.Forum.isBBSSystemAdmin; //(layout.desktop.session.user.roleList.indexOf("BBSSystemAdmin")!=-1);
    },
    inArray : function( managers, callback, username ){
        var flag = false;
        if( typeOf(managers) == "string" )managers = managers.split(",");
        if( managers.indexOf( username || this.userName ) != -1 )flag = true;
        if( callback )callback( flag );
        return flag;
    },
    isForumViewer : function( forumIdOrData ){
        var flag = true;
        if( this.hasForumAdminAuthority( forumIdOrData, null, null, false ) ){
            return true;
        }
        this.getCategoryData( forumIdOrData, function( data ){
            if( data.forumVisible != this.lp.allPerson ){
                if( MWF.xApplication.Forum.RoleInfoList.indexOf( "FORUM_GUEST_" + data.id ) == -1 ){
                    flag = false
                }
            }
        });
        return flag;
    },
    isSectionViewer : function( sectionIdOrData ){
        var flag = true;
        if( this.hasSectionAdminAuthority( sectionIdOrData, null, null, false ) ){
            return true;
        }
        if( typeOf( sectionIdOrData ) == "string" ){
            this.actions.getSection( sectionIdOrData, function( json ){
                var data = json.data;
                if( data.sectionVisible != this.lp.allPerson ){
                    if( MWF.xApplication.Forum.PermissionInfoList.indexOf( "SECTION_VIEW_" + data.id ) == -1 ){
                        flag = false
                    }
                }
            }.bind(this), null, async)
        }else{
            var data = sectionIdOrData;
            if( data.sectionVisible != this.lp.allPerson ){
                if( MWF.xApplication.Forum.PermissionInfoList.indexOf( "SECTION_VIEW_" + data.id ) == -1 ){
                    flag = false
                }
            }
        }
        return flag;
    },
    isRecommender : function( sectionIdOrData ){
        var flag = false;
        if( typeOf( sectionIdOrData ) == "string" ){
            this.actions.getSection( sectionIdOrData, function( json ){
                var data = json.data;
                //if( data.sectionVisible == this.lp.allPerson && data.indexRecommendable == true ){
                if( MWF.xApplication.Forum.RoleInfoList.indexOf( "SECTION_RECOMMENDER_" + data.id ) != -1 ){
                    flag = true
                }else if( this.hasSectionAdminAuthority( data, null, null, false ) ){ //如果是管理员
                    flag = true;
                }
                //}
            }.bind(this), null, async)
        }else{
            var data = sectionIdOrData;
            //if( data.sectionVisible == this.lp.allPerson && data.indexRecommendable == true ){
            if( MWF.xApplication.Forum.RoleInfoList.indexOf( "SECTION_RECOMMENDER_" + data.id ) != -1 ){
                flag = true
            }else if( this.hasSectionAdminAuthority( data, null, null, false ) ){ //如果是管理员
                flag = true;
            }
            //}
        }
        return flag;
    },
    isSubjectPublisher : function( sectionIdOrData ){
        var flag = true;
        if( this.hasSectionAdminAuthority( sectionIdOrData, null, null, false ) ){
            return true;
        }
        if( typeOf( sectionIdOrData ) == "string" ){
            this.actions.getSection( sectionIdOrData, function( json ){
                var data = json.data;
                if( data.subjectPublishAble != this.lp.allPerson ){
                    if( MWF.xApplication.Forum.RoleInfoList.indexOf( "SECTION_SUBJECT_PUBLISHER_" + data.id ) == -1 ){
                        flag = false
                    }
                }
            }.bind(this), null, async)
        }else{
            var data = sectionIdOrData;
            if( data.subjectPublishAble != this.lp.allPerson ){
                if( MWF.xApplication.Forum.RoleInfoList.indexOf( "SECTION_SUBJECT_PUBLISHER_" + data.id ) == -1 ){
                    flag = false
                }
            }
        }
        return flag;
    },
    isReplyPublisher : function( sectionIdOrData ){
        var flag = true;
        if( this.hasSectionAdminAuthority( sectionIdOrData, null, null, false ) ){
            return true;
        }
        if( typeOf( sectionIdOrData ) == "string" ){
            this.actions.getSection( sectionIdOrData, function( json ){
                var data = json.data;
                if( data.replyPublishAble != this.lp.allPerson ){
                    if( MWF.xApplication.Forum.RoleInfoList.indexOf( "SECTION_REPLY_PUBLISHER_" + data.id ) == -1 ){
                        flag = false
                    }
                }
            }.bind(this), null, false)
        }else{
            var data = sectionIdOrData;
            if( data.replyPublishAble != this.lp.allPerson ){
                if( MWF.xApplication.Forum.RoleInfoList.indexOf( "SECTION_REPLY_PUBLISHER_" + data.id ) == -1 ){
                    flag = false
                }
            }
        }
        return flag;
    },
    getCategoryData : function( forumIdOrData , callback ){
        if( typeOf( forumIdOrData ) == "string" ){
            if( MWF.xApplication.Forum.ForumSetting[ forumIdOrData ] ){
                if( callback )callback( MWF.xApplication.Forum.ForumSetting[ forumIdOrData ] );
            }else{
                this.actions.getCategory( forumIdOrData, function( json ){
                    MWF.xApplication.Forum.ForumSetting[ forumIdOrData ] = json.data;
                    if( callback )callback( json.data );
                }.bind(this), null, false )
            }
        }else{
            if( !MWF.xApplication.Forum.ForumSetting[ forumIdOrData.id  ] ){
                MWF.xApplication.Forum.ForumSetting[ forumIdOrData ] = forumIdOrData;
            }
            if( callback )callback( forumIdOrData );
        }
    },
    isForumManager : function( forumIdOrData, callback, username, async ){  //是否分区管理员
        var flag = false;
        this.getCategoryData( forumIdOrData, function( forumData ){
            flag = this.inArray( forumData.forumManagerName , callback, username );
        }.bind(this) );
        return flag;
    },
    isSectionManager : function( sectionIdOrData , callback, username , async){  //是否板块管理员
        var flag = false;
        if( typeOf( sectionIdOrData ) == "string" ){
            this.actions.getSection( sectionIdOrData, function( json ){
                flag = this.inArray( json.data.moderatorNames, callback, username );
            }.bind(this), null, async)
        }else{
            flag = this.inArray( sectionIdOrData.moderatorNames , callback, username );
        }
        return flag;
    },
    hasForumAdminAuthority  : function( forumIdOrData, callback, username, async ) { //具有分区管理权限
        if (!username && this.isAdmin()){
            if( callback )callback( true );
            return true;
        }
        var flag = false;
        flag = this.isForumManager( forumIdOrData, function( flag ){
            if( callback )callback( flag );
        }.bind(this), username , async);
        return flag;
    },
    hasSectionAdminAuthority : function( sectionIdOrData , callback, username, async ){ //具有板块管理权限
        if (!username && this.isAdmin()) {
            if (callback)callback(true);
            return true;
        }

        var flag = false;
        var fun = function( data ){
            var managers = data.moderatorNames;
            if( typeOf(managers) == "string" )managers = managers.split(",");
            if( managers.indexOf( username || this.userName ) != -1 )flag = true;
            if( flag ){
                if( callback )callback( flag );
            }else{
                flag = this.isForumManager( data.forumId, function( flag ){
                    if( callback )callback( flag );
                }.bind(this), username , async)
            }
        }.bind(this);

        if( typeOf( sectionIdOrData ) == "string" ){
            this.actions.getSection( sectionIdOrData, function( json ){
                fun( json.data );
            }.bind(this), null, async)
        }else{
            fun( sectionIdOrData );
        }
        return flag;
    }


}); 