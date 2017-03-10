MWF.xApplication.Organization = MWF.xApplication.Organization || {};
MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};

MWF.xApplication.Organization.Selector.Selector = MWF.OrgSelector = new Class({
    Implements: [Options],
    options: {
        "type": "person", //单个选择时的类型
        "types" : [], //多重选择时的类型，可选值为 person, group, company, department, identity，其他未测试
        "count": 0,
        "title": "Select Person",
        "groups": [], //选person, group, role 时的范围
        "roles": [], //选选person, group, role 时的范围
        "companys": [], //选 company, department, duty, identity 时的范围
        "departments": [], //选 company, department, duty, identity 时的范围

        "values": [], //单个选择时的已选id
        "names": [], //单个选择时的已选名称

        "multipleValues" : {}, //多重选择时已选id， 和 groupValues，companyValues，departmentValues，identityValues，personValues 二选一， 样例 { "group" : ["xx群组id"], "department" : ["xx部门1id":"xx部门2id"] ...  }
        "groupValues" : [], // 多重选择时group 的已选id
        "companyValues" : [], // 多重选择时company 的已选id
        "departmentValues" : [], // 多重选择时department 的已选id
        "identityValues" : [], // 多重选择时identity 的已选id
        "personValues" : [], // 多重选择时person 的已选id,

        "multipleNames": {}, //多重选择时的已选name， 和 groupNames，companyNames，departmentNames，identityNames，personNames 二选一， 样例 { "group" : ["xx群组"], "department" : ["xx部门1":"xx部门2"] ...  }
        "groupNames" : [], // 多重选择时group 的已选选值
        "companyNames" : [], // 多重选择时company 的已选名称
        "departmentNames" : [], // 多重选择时department 的已选名称
        "identityNames" : [], // 多重选择时identity 的已选名称
        "personNames" : [], // 多重选择时person 的已选名称

    },
    initialize: function(container, options){
        MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
        this.setOptions(options);
        this.container = container;
        var type;
        if( !this.options.types || this.options.types.length == 0 ){
            type = this.options.type.capitalize();
        }else if( this.options.types.length == 1 ){
            type = this.options.types[0].capitalize();
        }
        if ( type ){
            MWF.xDesktop.requireApp("Organization", "Selector."+type, function(){
                this.selector = new MWF.xApplication.Organization.Selector[type](this.container, options);
                this.selector.load();
            }.bind(this));
        }else{
            MWF.xDesktop.requireApp("Organization", "Selector.MultipleSelector", function() {
                this.selector = new MWF.xApplication.Organization.Selector.MultipleSelector(this.container, this.options );
                this.selector.load();
            }.bind(this));
        }
    }
});