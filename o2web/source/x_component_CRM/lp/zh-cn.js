MWF.xApplication.CRM.LP = {
	"title": "CRM",
	"confirm":{
		"customForm":{
			"replaceLocation":{
				"title":"信息确认",
				"content":"是否用定位地址替换详细地址?"
			}
		}
	},
	"main":{
		"title":"客户管理",
		"quickStart":"快速新建"
	},
	"customer":{
		"head":{
			"headTitle":"客户管理",
			"searchText":"搜索客户姓名",
			"search":"搜索",
			"create":"新建",
			"moreAction":"更多"
		},
		"customerForm":{
			"defaultSelect":"请选择",

			"title":"新增客户",

			"TCustomerName":"客户名称",
			"TCustomerType":"客户类型",
			"TCustomerLevel":"客户级别",
			"TSource":"来源",
			"TIndustryFirst":"行业",
			"TIndustrySecond":"行业",
			"TDistrict":"省、市、区",
			"TCountry":"国家",
			"TProvince":"省",
			"TCity":"市",
			"TArea":"区",
			"TStreet":"详细街道",
			"TLocation":"定位",
			"TLocationNotice":"输入街道，公司，写字楼等",
			"TLongitude":"经度",
			"TLatitude":"纬度",
			"TTelphone":"电话",
			"TFax":"传真",
			"TRemark":"备注",
			"TWebSite":"网址",
			"TEmail":"邮件",
			"TCustomerStatus":"客户状态",
			"TCustomerGrade":"客户分级",


			"saveSuccess":"新建成功",
			"actionConfirm":"确定",
			"actionCancel":"关闭"
		},
		"customerRead":{
			"mapLocation":"地图",
			"TCustomerName":"客户名称",
			"TCustomerNO":"客户编号",
			"TCustomerType":"客户类型",
			"TCustomerLevel":"客户级别",
			"TSource":"来源",
			"TIndustryFirst":"行业",
			"TIndustrySecond":"行业",
			"TDistrict":"省、市、区",
			"TCountry":"国家",
			"TProvince":"省",
			"TCity":"市",
			"TArea":"区",
			"TStreet":"详细街道",
			"TLocation":"定位",
			"TLocationNotice":"输入街道，公司，写字楼等",
			"TLongitude":"经度",
			"TLatitude":"纬度",
			"TTelphone":"电话",
			"TFax":"传真",
			"TRemark":"备注",
			"TWebSite":"网址",
			"TEmail":"邮件",
			"TCustomerStatus":"客户状态",
			"TCustomerGrade":"客户分级",
			"responsePerson":"负责人",
			"customerInfo":"基本信息",
			"editCustomer":"编辑",
			"navi":{
				"summary":"概要",
				"customerInfo":"客户信息",
				"address":"地址管理",
				"contact":"联系人",
				"chance":"商机",
				"clue":"线索",
				"bargain":"合同",
				"visitor":"拜访",
				"attachment":"附件"
			}
		},
		"customerEdit":{
			"customername":{
				"text":"客户名称",
				"notEmpty":true,
				"type":"text"
			},
			"level":{
				"text":"客户级别",
				"notEmpty":true,
				"type":"select",
				"value":"A（重点客户）,B（普通客户）,C（非优先客户）"
			},
			"industry":{
				"text":"客户行业",
				"notEmpty":true,
				"type":"select",
				"value":"IT/通信/电子/互联网,金融业,房地产,运输/物流,服务业,文化传媒,政府,其他"
			},
			"source":{
				"text":"客户来源",
				"notEmpty":true,
				"type":"select",
				"value":"促销活动,搜索引擎,广告,线上注册,预约上门,展会资源,其他"
			},
			"dealstatus":{
				"text":"成交状态",
				"notEmpty":true,
				"type":"select",
				"value":"成交,未成交"
			},
			"telephone":{
				"text":"电话",
				"notEmpty":true,
				"type":"text"
			},
			"website":{
				"text":"网址",
				"type":"text"
			},
			"nexttime":{
				"text":"下次联系时间",
				"notEmpty":true,
				"attr" : {id:"nexttime"},
				"type":"datetime"
			},
			"remark":{
				"text":"备注",
				"notEmpty":true,
				"type":"textarea"
			},
			"cellphone":{
				"text":"手机",
				"notEmpty":true,
				"type":"text"
			},
			"detailaddress":{
				"text":"地址",
				"type":"text"
			},
			"location":{
				"text":"区域",
				"type":"map"
			}
		},
		"industry":{
			"value":"IT/通信/电子/互联网,金融业,房地产,运输/物流,服务业,文化传媒,政府,其他"
		},
		"level":{
			"value":"A（重点客户）,B（普通客户）,C（非优先客户）"
		},
		"source":{
			"value":"促销活动,搜索引擎,广告,线上注册,预约上门,展会资源,其他"
		},
		"dealstatus":{
			"value":"成交,未成交"
		}
	},
	"customerView":{
		"fieldList":{
			"field1":{
				"field":"customername",
				"title":"客户名称",
				"width":165,
				"fixed":"left",
				"resize": true,
				"align": 'left',
				"sort":true,
				"formatter" :function (v, data, index) {return '<a style="color: #337ab7;" class="clueId" id="'+data.id+'">'+v+'</a>';}
			},
			"field2":{
				"field":"owneruser",
				"title":"负责人",
				"width":165,
				"align": 'left'
			},
			"field3":{
				"field":"industry",
				"title":"客户类型",
				"width":120,
				"align": 'left',
				"resize":true
			},
			"field4":{
				"field":"level",
				"title":"客户级别",
				"width":120,
				"resize":true,
				"align": 'left'
			},
			"field5":{
				"field":"updateTime",
				"title":"更新时间",
				"width":160,
				"align": 'left',
				"sort":true
			},
			"field6":{
				"field":"dealstatus",
				"title":"成交状态",
				"width":120,
				"align": 'left',
				"sort":true
			},
			"field7":{
				"field":"createTime",
				"title":"创建时间",
				"width":160,
				"align": 'left',
				"sort":true
			},
			"field8":{
				"field":"remark",
				"title":"备注",
				"align": 'left',
				"width":165
			},
			"field9":{
				"field":"nexttime",
				"title":"下次联系时间",
				"width":160,
				"align": 'left',
				"sort":true
			},
			"field10":{
				"field":"cellphone",
				"title":"手机",
				"width":165,
				"resize":true,
				"align": 'left'
			},
			"field11":{
				"field":"createuser",
				"title":"创建人",
				"width":120,
				"align": 'left',
				"sort":true
			},
			"field12":{
				"field":"telephone",
				"title":"电话",
				"minWidth":165,
				"resize":true,
				"align": 'left',
				"sort":true
			}
		},
		"title":"销售简报-新增客户",
		"searchText":"请输入客户名称/手机/电话",
		"sortField":"updateTime",
		"sortType":'desc'
	},
	"clueView":{
		"fieldList":{
			"field1":{
				"field":"name",
				"title":"线索名称",
				"width":165,
				"fixed":"left",
				"resize": true,
				"align": 'left',
				"sort":true,
				"formatter" :function (v, data, index) {return '<a style="color: #337ab7;" class="clueId" id="'+data.id+'">'+v+'</a>';}
			},
			"field2":{
				"field":"source",
				"title":"来源",
				"width":165,
				"align": 'left'
			},
			"field3":{
				"field":"telephone",
				"title":"电话",
				"minWidth":165,
				"resize":true,
				"align": 'left',
				"sort":true
			},
			"field4":{
				"field":"updateTime",
				"title":"更新时间",
				"minWidth":160,
				"resize":true,
				"align": 'left',
				"sort":true
			},
			"field5":{
				"field":"owneruser",
				"title":"负责人",
				"minWidth":120,
				"resize":true,
				"align": 'left',
				"sort":true
			},
			"field6":{
				"field":"cellphone",
				"title":"手机",
				"width":165,
				"resize":true,
				"align": 'left'
			},
			"field7":{
				"field":"industry",
				"title":"客户类型",
				"width":120,
				"align": 'left',
				"resize":true
			},
			"field8":{
				"field":"level",
				"title":"客户级别",
				"width":120,
				"resize":true,
				"align": 'left'
			},
			"field9":{
				"field":"address",
				"title":"地址",
				"align": 'left',
				"width":165
			},
			"field10":{
				"field":"nexttime",
				"title":"下次联系时间",
				"width":160,
				"align": 'left',
				"sort":true
			},
			"field11":{
				"field":"remark",
				"title":"备注",
				"align": 'left',
				"width":165
			}
			/*"field10":{
				"field":"opts",
				"title":"操作",
				"width":125,
				"fixed": "right",
				"align": "center",
				"resize": true,
				"formatter" :function () {return '<a class="rayui-btn rayui-btn-success" event="edit">编辑</a>';}
			}*/
		},
		"sortField":"updateTime",
		"sortType":'desc'
	},
	"clue":{
		"head":{
			"headTitle":"线索管理",
			"searchText":"请输入线索名称",
			"search":"搜索",
			"create":"新建",
			"moreAction":"更多"
		},
		"clueForm":{
			"defaultSelect":"请选择",

			"title":"新增线索",
			"name":"线索名称",
			"source":"来源",
			"telephone":"电话",
			"cellphone":"手机",
			"industry":"客户类型",
			"level":"客户级别",
			"address":"地址",
			"nexttime":"下次联系时间 ",
			"remark":"备注",


			"saveSuccess":"新建成功",
			"actionConfirm":"确定",
			"actionCancel":"关闭"
		},
		"clueRead":{
			"mapLocation":"地图",
			"TCustomerName":"线索名称",
			"TCustomerNO":"客户编号",
			"TCustomerType":"客户类型",
			"TCustomerLevel":"客户级别",
			"TSource":"来源",
			"TIndustryFirst":"行业",
			"TIndustrySecond":"行业",
			"TDistrict":"省、市、区",
			"TCountry":"国家",
			"TProvince":"省",
			"TCity":"市",
			"TArea":"区",
			"TStreet":"详细街道",
			"TLocation":"定位",
			"TLocationNotice":"输入街道，公司，写字楼等",
			"TLongitude":"经度",
			"TLatitude":"纬度",
			"TTelphone":"电话",
			"TFax":"传真",
			"TRemark":"备注",
			"TWebSite":"网址",
			"TEmail":"邮件",
			"TCustomerStatus":"客户状态",
			"TCustomerGrade":"客户分级",
			"responsePerson":"负责人",
			"customerInfo":"基本信息",
			"editCustomer":"编辑",
			"navi":{
				"summary":"概要",
				"customerInfo":"客户信息",
				"address":"地址管理",
				"contact":"联系人",
				"chance":"商机",
				"clue":"线索",
				"bargain":"合同",
				"visitor":"拜访",
				"attachment":"附件"
			}
		},
		"clueEdit":{
			"name":{
				"text":"线索名称",
				"notEmpty":true,
				"type":"text"
			},
			"source":{
				"text":"来源",
				"notEmpty":true,
				"type":"text"
			},
			"telephone":{
				"text":"电话",
				"notEmpty":true,
				"type":"text"
			},
			"cellphone":{
				"text":"手机",
				"notEmpty":true,
				"type":"text"
			},
			"industry":{
				"text":"客户类型",
				"notEmpty":true,
				"type":"select",
				"value":"IT/通信/电子/互联网,金融业,房地产,运输/物流,服务业,文化传媒,政府,其他"
			},
			"level":{
				"text":"客户级别",
				"notEmpty":true,
				"type":"select",
				"value":"A（重点客户）,B（普通客户）,C（非优先客户）"
			},
			"address":{
				"text":"地址",
				"notEmpty":true,
				"type":"text"
			},
			"nexttime":{
				"text":"下次联系时间",
				"notEmpty":true,
				"attr" : {id:"nexttime"},
				"type":"datetime"
			},
			"remark":{
				"text":"备注",
				"type":"textarea"
			}
		},
		"industry":{
			"value":"IT/通信/电子/互联网,金融业,房地产,运输/物流,服务业,文化传媒,政府,其他"
		},
		"level":{
			"value":"A（重点客户）,B（普通客户）,C（非优先客户）"
		}
	},
	"contact":{
		"head":{
			"headTitle":"联系人管理",
			"searchText":"请输入联系人名称",
			"search":"搜索",
			"create":"新建",
			"moreAction":"更多"
		},
		"contactEdit":{
			"contactsname":{
				"text":"线索名称",
				"notEmpty":true,
				"type":"text"
			},
			"customername":{
				"text":"客户名称",
				"notEmpty":true,
				"type":"openSelect"
			},
			"telephone":{
				"text":"电话",
				"notEmpty":true,
				"type":"text"
			},
			"cellphone":{
				"text":"手机",
				"type":"text"
			},
			"email":{
				"text":"电子邮箱",
				"type":"text"
			},
			"decision":{
				"text":"是否关键决策人",
				"notEmpty":true,
				"type":"select",
				"value":"是,否"
			},
			"post":{
				"text":"职务",
				"type":"text"
			},
			"sex":{
				"text":"性别",
				"notEmpty":true,
				"type":"select",
				"value":"男,女"
			},
			"detailaddress":{
				"text":"地址",
				"type":"text"
			},
			"nexttime":{
				"text":"下次联系时间",
				"notEmpty":true,
				"attr" : {id:"nexttime"},
				"type":"datetime"
			},
			"remark":{
				"text":"备注",
				"type":"textarea"
			}
		},
		"decision":{
			"value":"是,否"
		},
		"sex":{
			"value":"男,女"
		}
	},
	"contactsView":{
		"fieldList":{
			"field1":{
				"field":"contactsname",
				"title":"姓名",
				"width":165,
				"resize": true,
				"fixed":"left",
				"align": 'left',
				"formatter" :function (v, data, index) {return '<a style="color: #337ab7;" class="clueId" id="'+data.id+'">'+v+'</a>';}
			},
			"field2":{
				"field":"customer",
				"title":"客户名称",
				"width":165,
				"resize": true,
				"align": 'left',
				"formatter" :function (v, data, index) {return '<a style="color: #337ab7;" class="otherId" id="'+v.id+'">'+v.customername+'</a>';}
			},
			"field3":{
				"field":"cellphone",
				"title":"手机",
				"width":120,
				"align": 'left',
				"resize":true
			},
			"field4":{
				"field":"email",
				"title":"电子邮箱",
				"width":120,
				"resize":true,
				"align": 'left'
			},
			"field5":{
				"field":"post",
				"title":"职务",
				"width":160,
				"align": 'left',
				"sort":true
			},
			"field6":{
				"field":"detailaddress",
				"title":"地址",
				"width":120,
				"align": 'left',
			},
			"field7":{
				"field":"createuser",
				"title":"创建人",
				"width":120,
				"align": 'left',
			},
			"field8":{
				"field":"updateTime",
				"title":"更新时间",
				"align": 'left',
				"width":160
			},
			"field9":{
				"field":"createTime",
				"title":"创建时间",
				"width":160,
				"align": 'left'
			},
			"field10":{
				"field":"owneruser",
				"title":"负责人",
				"width":120,
				"resize":true,
				"align": 'left'
			},
			"field11":{
				"field":"createuser",
				"title":"创建人",
				"width":120,
				"align": 'left'
			},
			"field12":{
				"field":"remark",
				"title":"备注",
				"minWidth":165,
				"resize":true,
				"align": 'left'
			},
			"field13":{
				"field":"decision",
				"title":"是否关键决策人",
				"width":120,
				"align": 'left',
				"sort":true
			},
			"field14":{
				"field":"nexttime",
				"title":"下次联系时间",
				"width":120,
				"align": 'left'
			},
			"field15":{
				"field":"sex",
				"title":"性别",
				"width":120,
				"align": 'left'
			}
		},
		"title":"销售简报-新增联系人",
		"searchText":"请输入联系人名称/手机/电话",
		"sortField":"customerid",
		"sortType":'desc'
	},
    "index":{
        "head":{
            "headTitle":"本人及下属",
            "searchText":"搜索客户姓名",
            "search":"搜索",
            "create":"新建",
            "moreAction":"更多"
        },
		"searchDate":{
			"value":"今天,本周,本月,本季度,本年,自定义"
		}
    },
	"chance": {
		"head": {
			"headTitle": "商机管理",
			"searchText": "搜索商机名称",
			"search": "搜索",
			"create": "新建商机",
			"moreAction": "更多操作"
		},
		"chanceEdit":{
			"defaultSelect":"请选择",
			"title":"新建商机",
			"name":"商机名称",
			"customer":"客户名称",
			"money":"商机金额",
			"typeid":"商机状态组",
			"statusid":"商机阶段",
			"dealdate":"预计成交日期",
			"remark":"备注",
			"saveSuccess":"新建成功",
			"ok":"确定",
			"cancel":"关闭"
		},
		"title":"销售简报-新增商机",
		"typeid":{
			"value":"IT/通信/电子/互联网,金融业,房地产,运输/物流,服务业,文化传媒,政府,其他"
		},
		"statusid":{
			"value":"A（重点客户）,B（普通客户）,C（非优先客户）"
		}
	},
	"template":{
		"defaultSelect":"请选择"
	},
	"baiduMap":{

	}
};