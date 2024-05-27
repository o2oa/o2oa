# O2OA API {@o2version}

## 简介

* O2OA 是一个高度可定制化的企业级办公平台，您可以通过脚本语言扩展平台功能。

* O2OA 前端脚本运行在浏览器和移动端h5页面，基于您使用浏览器的Javascript引擎，使用Javascript语法。

* O2OA 后端脚本运行在服务器上。V9.0.0版本开始基于GraalVM，完全兼容 ECMAScript 2021 规范。9.0.0（不含）之前基于Nashorn，遵循ECMAScript 5.1规范。除本API的modules.server章节外，后端脚本还支持该文章所描述的特性：[https://www.o2oa.net/cms/service/531.html](https://www.o2oa.net/cms/service/531.html)。

* 本API是O2OA脚本的编写说明，您可以通过脚本访问和操作平台内的各种对象。

## API总览

### Namespace

* 平台中的全局命名空间

#### web
   {@o2IndexTable namespaces|o2category=web}


#### mobile
   * 移动端H5页面除了可以使用web章节以及Class章节描述的API，还可以使用下列API：
   
   {@o2IndexTable namespaces|o2category=mobile}

### Modules

* 为脚本封装的对象、方法和类。通过this[moduleName]调用。

 
#### web
  * 前台脚本（浏览器/移动端H5页面）可以使用的对象、方法和类。
  
   {@o2IndexTable modules|o2category=web}
 
  
#### server
 * 后台脚本（服务端）可以使用的对象、方法和类。
    
##### common 通用后台api
{@o2IndexTable modules|o2category=server.common}

##### process 流程后台api
  {@o2IndexTable modules|o2category=server.process}
  
##### service 服务管理api
  {@o2IndexTable modules|o2category=server.service}
 

### FormComponent

* FormComponent为表单页面组件，在前台脚本中可以使用。可以通过`this.form.get("fieldId")`获取。

#### Process 流程表单组件
   {@o2IndexTable classes|o2category=FormComponents|o2range=Process}


#### CMS 内容管理表单组件
   {@o2IndexTable classes|o2category=FormComponents|o2range=CMS}


#### Portal 门户页面组件
   {@o2IndexTable classes|o2category=FormComponents|o2range=Portal}

### Query

* Query为数据中心的应用，本章节包括视图、查询视图、导入模型的api。

#### QueryView 视图类
   {@o2IndexTable classes|o2category=QueryView|o2range=QueryView}
   
#### QueryStatement 查询视图类
   {@o2IndexTable classes|o2category=QueryStatement|o2range=QueryStatement}
   
#### QueryImporter 导入模型类
   {@o2IndexTable classes|o2category=QueryImporter|o2range=QueryImporter}
    
### 服务

* 服务是O2OA后台提供的一系列restful服务。

<table>
    <tr>
        <td>x_processplatform_assemble_surface</td>
        <td>流程平台相关服务</td>
    </tr>
    <tr>
         <td>x_portal_assemble_surface</td>
         <td>门户平台相关服务</td>
     </tr>
    <tr>
         <td>x_cms_assemble_control</td>
         <td>内容管理平台相关服务</td>
     </tr>
    <tr>
         <td>x_query_assemble_surface</td>
         <td>数据平台相关服务</td>
     </tr>
    <tr>
         <td>x_organization_assemble_express</td>
         <td>组织架构相关服务</td>
     </tr>
    <tr>
         <td>x_file_assemble_control</td>
         <td>云文件相关服务</td>
     </tr>
    <tr>
         <td>x_meeting_assemble_control</td>
         <td>会议管理相关服务</td>
     </tr>
     <tr>
          <td>x_bbs_assemble_control</td>
          <td>论坛相关服务</td>
      </tr>
     <tr>
          <td>x_calendar_assemble_control</td>
          <td>日程管理相关服务</td>
      </tr>
     <tr>
          <td>x_hotpic_assemble_control</td>
          <td>热点信息相关服务</td>
      </tr>
     <tr>
          <td>x_mind_assemble_control</td>
          <td>脑图模块相关服务</td>
      </tr>
     <tr>
          <td>x_organization_assemble_personal</td>
          <td>个人设置相关服务</td>
      </tr>
     <tr>
          <td>x_attendance_assemble_control</td>
          <td>考勤模块相关服务</td>
      </tr>
</table>

## 使用范围

O2OA 可在多个位置嵌入脚本代码，用于扩展平台和实现自定义功能。嵌入脚本代码分为前端执行代码和服务端执行代码。
 
 * 前端脚本使用范围：
 <table>
      <tr>
          <td>脚本库</td>
          <td>流程平台、门户平台和内容管理平台中，都有脚本设计元素，可以在此创建脚本库。</td>
      </tr>
      <tr>
           <td>表单、页面、视图、查询视图、导入模型及其组件事件</td>
           <td>流程平台和内容平台的表单、门户平台的页面、数据平台的视图和查询视图中，每个设计组件包含多种事件，包括DOM对象原生事件和O2平台扩展事件。</td>
       </tr>
      <tr>
           <td>表单、页面可编辑组件默认值</td>
           <td>流程平台和内容平台的表单和门户平台的页面中，可编辑组件或文本组件的默认值可以通过脚本指定。</td>
       </tr>
      <tr>
           <td>表单及可编辑组件校验</td>
           <td>流程平台和内容管理的表单中，可编辑的字段可以通过脚本进行有效性校验，校验通过返回true，不通过返回提示信息。</td>
       </tr>
      <tr>
           <td>流程设计</td>
           <td>流程设计主要是后台脚本，但也有除外，流程路由属性的扩展附签和选择附签下的脚本使用的是前台脚本。</td>
       </tr>
  </table>
  
  * 后端脚本使用范围：
  <table>
       <tr>
           <td>脚本库</td>
           <td>流程平台、门户平台和内容管理平台中，都有脚本设计元素，可以在此创建脚本库。</td>
       </tr>
       <tr>
            <td>服务管理</td>
            <td>服务管理的接口和定时代理。</td>
        </tr>
       <tr>
            <td>流程设计</td>
            <td>流程属性的所有脚本（流程事件、时效脚本等等）；流程活动的所有脚本（处理人、待阅人、阅读人脚本，拆分依据、时效脚本，活动事件，参数脚本、响应脚本、执行脚本等等）；流程路由的条件脚本。</td>
        </tr>
       <tr>
            <td>查询配置</td>
            <td>查询配置的查询语句和总数语句。</td>
        </tr>
       <tr>
            <td>视图列</td>
            <td>视图列的显示脚本。</td>
        </tr>
   </table>
   

### 前端脚本使用范围详情

#### 脚本库

流程平台、门户平台和内容管理平台中，都有脚本设计元素，可以在此创建自己的脚本库。如下图：
![脚本库](img/home/script.png)
<br/><br/>

#### 表单、页面、视图、查询视图及其组件事件

流程平台和内容平台的表单、门户平台的页面、数据平台的视图和查询视图中，每个设计组件包含多种事件，包括DOM对象原生事件和O2平台扩展事件。如下图：
![事件中的脚本](img/home/script_event.png)
<br/><br/>

#### 表单、页面可编辑组件默认值

流程平台和内容平台的表单和门户平台的页面中，可编辑组件或文本组件的默认值可以通过脚本指定。如下图：
![事件中的脚本](img/home/script_defaultvalue.png)
<br/><br/>

#### 表单、页面、视图、查询视图的部分属性

流程平台和内容平台的表单、门户平台的页面、数据平台的视图和查询视图中，有许多组件的相关属性可以通过脚本来定义。<br/>
如：下拉框、单选多选按钮的可选值、人员字段的选择范围、区段依据等。如下图：<br/>
![属性中的脚本](img/home/script_attribute.png)
<br/><br/>

#### 表单及可编辑组件校验

流程平台和内容管理的表单中，可编辑的字段可以通过脚本进行有效性校验，校验通过返回true，不通过返回提示信息。如下图：
![脚本](img/home/script_validation.png)
<br/><br/>

#### 流程路由属性的扩展附签和选择附签下的脚本
![扩展附签脚本](img/home/script_route_extend.png)
![选择附签脚本](img/home/script_route_select.png)
<br/><br/>


### 后端脚本使用范围详情

#### 脚本库
流程平台、门户平台和内容管理平台中，都有脚本设计元素，可以在此创建脚本库。
![脚本库](img/home/server_script.png)
<br/><br/>

#### 服务管理
服务管理的接口和定时代理。
![定时代理](img/home/server_agent.png)
![接口](img/home/server_interface.png)
<br/><br/>

#### 流程设计
流程属性的所有脚本（流程事件、时效脚本等等）。
![流程属性](img/home/server_process.png)

流程活动的所有脚本（处理人、待阅人、阅读人脚本，拆分依据、时效脚本，活动事件，参数脚本、响应脚本、执行脚本等等）
![流程活动](img/home/server_process_activity.png)

流程路由的条件脚本。
![流程路由](img/home/server_process_route.png)

#### 查询配置
查询配置的查询语句和总数语句。
![流程路由](img/home/server_statement.png)

#### 视图列
视图列的显示脚本，可以使用this.value获取本列的列值、this.entry.data获取本行的数据。
![视图列显示脚本](img/home/server_view_column.png)

## 样例
* 这是一个简单的样例，用于展现脚本如何编写和运行。
* 本例中我们要实现一个表单中，两个下拉框从配置数据中获取可选数据，以及实现联动。
* 我们需要创建一个流程应用，一个表单以及一个数据字典。在表单中创建两个下拉列表框。

1. 先在表单中创建两个下拉框
<br/>
![下拉框](img/home/example_1_1.png)
<br/><br/>

2. 在category1和category2两个下拉框的属性中，选择通过“脚本”编辑可选值。
<br/>
category1脚本:
<br/>
![下拉框](img/home/example_1_2_1.png)
<br/><br/>
category2脚本:
<br/>
![下拉框](img/home/example_1_2_2.png)
<br/><br/>

3. 在category1和category2两个下拉框的可选值脚本如下：<br/><br/>
category1的可选值脚本:
```
var dict = new this.Dict("category");   //获取名为category的数据字典 
var categoryList = dict.get();  //获取数据字典
var options = Object.keys(categoryList);    //获取大类，赋值给options变量
options.unshift("(请选择大类)|");   //在options数组首位插入提示选项，并将“”作为value，“(请选择大类)”作为text
return options; //返回列表，作为列表框的可选值 
```    
category2的可选值脚本:
```  
var dict = new this.Dict("category"); //获取名为category的数据字典 
var categoryList = dict.get(this.data.category1); //获取数据字典,以下拉框category1的值为关键字的数据值（数组） 
return categoryList; //返回列表，作为列表框的可选值 
```  

4. 在category1的change事件中添加如下代码：
```  
//获category2下拉框，并刷新可选项
this.form.get("category2").resetOption();
```  
5. 设计数据字典如下图，并命名为："分类配置"，别名为：“category”。
![下拉框](img/home/example_1_5.png)

6. 预览表单，即可看到大类下拉框中选项为数据字典中的第一层数据，选择不同的大类可与小类下拉框实现联动。