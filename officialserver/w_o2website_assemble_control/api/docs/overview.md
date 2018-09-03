# 概述

- **O2Platform** 是一个高度可定制化的企业级办公平台，您可以通过脚本语言扩展平台功能。

- **O2Platform** 脚本基于Javascript语法，您可以通过脚本访问和操作允许的DOM对象；也可以通过平台定义的API操作业务数据、组织、视图等各种对象。脚本可以在后端（服务器端）和前端（浏览器端）执行。定义在流程中的脚本在后端执行；定义在表单等其他地方的脚本在前端执行。

- 后端脚本基于JDK8标准的Nashorn，遵循ECMAScript 5.1规范。前端脚本基于您使用的浏览器的Javascript引擎。 


----------

# 嵌入脚本
　　在进行流程设计时，您可以在流程的事件，活动的事件以及活动的所有者等允许嵌入脚本的位置添加脚本。流程设计中的脚本，会在流程实例运行时，在服务器端基于Nashorn引擎执行；在表单设计时，您可以在表单元素的事件，默认值等属性中添加脚本，此脚本会在访问表单的过程中，适时基于浏览器Javascript引擎运行。
<div style="float: right; margi0n-right: 0px">
<div style="background-color: #999999; color:#fff; font-size: 16px; height:38px; line-height: 38px; margin: 5px 0px; text-align: center; font-weight: bold">Alt+/ 脚本提示</div>
<image width="306px" height="300px" src="http://www.o2oa.io/api/image/t4.png"/>
</div>
<div style="float: left; margin-right: 5px; margin-left: 30px">
<div style="background-color: #999999; color:#fff; font-size: 16px; height:38px; line-height: 38px; margin: 5px 0px; text-align: center; font-weight: bold">属性窗格可嵌入脚本</div>
<image width="306px" height="300px" src="http://www.o2oa.io/api/image/t1.png"/>
</div>
<div style="float: left; margin-right: 5px">
<div style="background-color: #999999; color:#fff; font-size: 16px; height:38px; line-height: 38px; margin: 5px 0px; text-align: center; font-weight: bold">单击进入脚本编辑器</div>
<image width="306px" height="300px" src="http://www.o2oa.io/api/image/t2.png"/>
</div>
　　
----------

# API总览 #
## 流程平台 ##
- 流程实例（workContext）
- 业务数据（data）
- 表单（form）（仅前端）
- 视图（view）
- 数据字典（Dict）
- 组织（org）
- 服务（service）
- 引用（include）
- 方法定义（define）
## 内容管理 ##
- 内容文档（documentContext）
- 业务数据（data）
- 表单（form）
- 视图（view）
- 数据字典（Dict）
- 组织（org）
- 服务（service）
- 引用（include）
- 方法定义（define）
## 门户 ##
- 页面（page）
- 视图（view）
- 数据字典（Dict）
- 组织（org）
- 服务（service）
- 引用（include）
- 方法定义（define）


----------
# 例子 #
- 本列中，在表单上设计了一个选择城市的下拉选择框，根据表单上另外一个省份选择框的值，来改变城市选择框的可选值。 

![](http://www.o2oa.io/api/image/t5.png)

在选择框属性窗格中，可选择属性选择“脚本”，在脚本编辑器中输入以下代码。 

    var dict = new this.Dict("country"); //获取名为country的数据字典 
	var cityList = dict.get(this.data.province); //获取数据字典中，以表单输入框province的值为关键字的数据值（数组） 
	return cityList; //返回数组，作为列表框的可选值 

数据字典“country”设计如下：

![](http://www.o2oa.io/api/image/t6.png)