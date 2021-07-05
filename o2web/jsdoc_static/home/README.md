# O2OA前台API {@o2version}

## 简介

* O2OA 是一个高度可定制化的企业级办公平台，您可以通过脚本语言扩展平台功能。

* O2OA 前端脚本基于您使用的浏览器的Javascript引擎，使用Javascript语法。

* 本API是O2OA前台脚本的编写说明，您可以通过脚本访问和操作平台内的各种对象。

## API总览

### Modules

* 为脚本封装的对象、方法和类

<table>
    <tr>
        <td><a href="module-data.html">业务数据 - data</a></td>
        <td><a href="module-workContext.html">流程实例 - workContext</a></td>
        <td><a href="module-documentContext.html">内容管理实例 - documentContext</a></td>
    </tr>
    <tr>
        <td><a href="module-org.html">组织查询 - org</a></td>
        <td><a href="module-form.html">流程及内容管理表单 - form</a></td>
        <td><a href="module-page.html">门户页面 - page</a></td>
    </tr>
    <tr>
        <td><a href="module-queryView.html">视图 - queryView</a></td>
        <td><a href="module-view.html">视图执行 - view</a></td>
        <td><a href="module-Dict.html">数据字典 - Dict</a></td>
    </tr>
    <tr>
        <td><a href="module-queryStatement.html">查询视图 - queryStatement</a></td>
        <td><a href="module-statement.html">查询视图执行 - statement</a></td>
        <td><a href="module-Actions.html">服务调用 - Actions</a></td>
    </tr>
    <tr>
        <td><a href="module-include.html">脚本引用 - include</a></td>
        <td><a href="module-define.html">方法定义 - define</a></td>
        <td><a href="module-session.html">当前用户 - session</a></td>
    </tr>
    <tr>
        <td><a href="module-o2m.html">平台移动APP API - o2m</a></td>
        <td><a href="module-wait.html">表单等待 - wait</a></td>
        <td><a href="module-importer.html">导入数据 - importer</a></td>
    </tr>
</table>

### FormComponent

* FormComponent为表单组件，可以通过`this.form.get("fieldId")`获取。

#### Process 流程表单组件
<table>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Form.html">表单 - Form</a></td>
        <td><a href="MWF.xApplication.process.Xform.Label.html">文本 - Label</a></td>
        <td><a href="MWF.xApplication.process.Xform.Textfield.html">文本字段 - Textfield</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Number.html">数字字段 - Number</a></td>
        <td><a href="MWF.xApplication.process.Xform.Org.html">人员组织 - Org</a></td>
        <td><a href="MWF.xApplication.process.Xform.Calendar.html">日期选择 - Calendar</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Textarea.html">多行文本 - Textarea</a></td>
        <td><a href="MWF.xApplication.process.Xform.Select.html">下拉框 - Select</a></td>
        <td><a href="MWF.xApplication.process.Xform.Radio.html">单选框 - Radio</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Checkbox.html">多选框 - Checkbox</a></td>
        <td><a href="MWF.xApplication.process.Xform.Combox.html">组合框 - Combox</a></td>
        <td><a href="MWF.xApplication.process.Xform.Opinion.html">意见框 - Opinion</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Button.html">按钮 - Button</a></td>
        <td><a href="MWF.xApplication.process.Xform.Address.html">地址 - Address</a></td>
        <td><a href="MWF.xApplication.process.Xform.Actionbar.html">操作条 - Actionbar</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Sidebar.html">侧边操作条 - Sidebar</a></td>
        <td><a href="MWF.xApplication.process.Xform.Image.html">图片 - Image</a></td>
        <td><a href="MWF.xApplication.process.Xform.ImageClipper.html">图片编辑 - ImageClipper</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Attachment.html">附件 - Attachment</a></td>
        <td><a href="MWF.xApplication.process.Xform.Div.html">容器 - Div</a></td>
        <td><a href="MWF.xApplication.process.Xform.Table.html">表格 - Table</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Table$Td.html">单元格 - Table$Td</a></td>
        <td><a href="MWF.xApplication.process.Xform.DatagridPC.html">数据网格PC端(过时) - DatagridPC</a></td>
        <td><a href="MWF.xApplication.process.Xform.DatagridMobile.html">数据网格移动端(过时) - DatagridMobile</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Subform.html">子表单 - Subform</a></td>
        <td><a href="MWF.xApplication.process.Xform.ViewSelector.html">选择视图 - ViewSelector</a></td>
        <td><a href="MWF.xApplication.process.Xform.View.html">嵌入视图 - View</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Stat.html">嵌入统计 - Stat</a></td>
        <td><a href="MWF.xApplication.process.Xform.Common.html">通用元素 - Common</a></td>
        <td><a href="MWF.xApplication.process.Xform.Tab.html">分页 - Tab</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Tree.html">树 - Tree</a></td>
        <td><a href="MWF.xApplication.process.Xform.Log.html">流程记录 - Log</a></td>
        <td><a href="MWF.xApplication.process.Xform.Monitor.html">流程监控 - Monitor</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Iframe.html">Iframe - Iframe</a></td>
        <td><a href="MWF.xApplication.process.Xform.Documenteditor.html">公文编辑器 - Documenteditor</a></td>
        <td><a href="MWF.xApplication.process.Xform.Htmleditor.html">HTML编辑器 - Htmleditor</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Office.html">Office控件 - Office</a></td>
        <td><a href="MWF.xApplication.process.Xform.StatementSelector.html">选择查询视图 - StatementSelector</a></td>
        <td><a href="MWF.xApplication.process.Xform.Statement.html">嵌入查询视图 - Statement</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Datatemplate.html">数据模板 - Datatemplate</a></td>
        <td><a href="MWF.xApplication.process.Xform.DatatablePC.html">数据表格PC端 - DatatablePC</a></td>
        <td><a href="MWF.xApplication.process.Xform.DatatableMobile.html">数据表格移动端 - DatatableMobile</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Importer.html">数据导入 - Importer</a></td>
        <td></td>
        <td></td>
    </tr>
</table>



#### CMS 内容管理表单组件
<table>
    <tr>
        <td><a href="CMSForm.html">内容管理表单 - CMSForm</a></td>
        <td><a href="MWF.xApplication.process.Xform.Label.html">文本 - Label</a></td>
        <td><a href="MWF.xApplication.process.Xform.Textfield.html">文本字段 - Textfield</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Number.html">数字字段 - Number</a></td>
        <td><a href="MWF.xApplication.process.Xform.Org.html">人员组织 - Org</a></td>
        <td><a href="MWF.xApplication.cms.Xform.Reader.html">读者 - Reader</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.cms.Xform.Author.html">作者 - Author</a></td>
        <td><a href="MWF.xApplication.process.Xform.Calendar.html">日期选择 - Calendar</a></td>
        <td><a href="MWF.xApplication.process.Xform.Textarea.html">多行文本 - Textarea</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Select.html">下拉框 - Select</a></td>
        <td><a href="MWF.xApplication.process.Xform.Radio.html">单选框 - Radio</a></td>
        <td><a href="MWF.xApplication.process.Xform.Checkbox.html">多选框 - Checkbox</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Combox.html">组合框 - Combox</a></td>
        <td><a href="MWF.xApplication.process.Xform.Button.html">按钮 - Button</a></td>
        <td><a href="MWF.xApplication.process.Xform.Address.html">地址 - Address</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Actionbar.html">操作条 - Actionbar</a></td>
        <td><a href="MWF.xApplication.process.Xform.Image.html">图片 - Image</a></td>
        <td><a href="MWF.xApplication.process.Xform.ImageClipper.html">图片编辑 - ImageClipper</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Attachment.html">附件 - Attachment</a></td>
        <td><a href="MWF.xApplication.process.Xform.Div.html">容器 - Div</a></td>
        <td><a href="MWF.xApplication.process.Xform.Table.html">表格 - Table</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Table$Td.html">单元格 - Table$Td</a></td>
        <td><a href="MWF.xApplication.process.Xform.DatagridPC.html">数据网格PC端(过时) - DatagridPC</a></td>
        <td><a href="MWF.xApplication.process.Xform.DatagridMobile.html">数据网格移动端(过时) - DatagridMobile</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Subform.html">子表单 - Subform</a></td>
        <td><a href="MWF.xApplication.process.Xform.ViewSelector.html">选择视图 - ViewSelector</a></td>
        <td><a href="MWF.xApplication.process.Xform.View.html">嵌入视图 - View</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Stat.html">嵌入统计 - Stat</a></td>
        <td><a href="MWF.xApplication.process.Xform.Common.html">通用元素 - Common</a></td>
        <td><a href="MWF.xApplication.process.Xform.Tab.html">分页 - Tab</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Tree.html">树 - Tree</a></td>
        <td><a href="MWF.xApplication.process.Xform.Iframe.html">Iframe - Iframe</a></td>
        <td><a href="MWF.xApplication.process.Xform.Htmleditor.html">HTML编辑器 - Htmleditor</a></td>
    </tr>
    <tr>
        <td><a href="CMSLog.html">阅读记录 - CMSLog</a></td>
        <td><a href="MWF.xApplication.process.Xform.Office.html">Office控件 - Office</a></td>
        <td><a href="MWF.xApplication.cms.Xform.Comment.html">评论 - Comment</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.StatementSelector.html">选择查询视图 - StatementSelector</a></td>
        <td><a href="MWF.xApplication.process.Xform.Statement.html">嵌入查询视图 - Statement</a></td>
        <td><a href="MWF.xApplication.process.Xform.Datatemplate.html">数据模板 - Datatemplate</a></td>
     </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.DatatablePC.html">数据表格PC端 - DatatablePC</a></td>
        <td><a href="MWF.xApplication.process.Xform.DatatableMobile.html">数据表格移动端 - DatatableMobile</a></td>
        <td><a href="MWF.xApplication.process.Xform.Importer.html">数据导入 - Importer</a></td>
    </tr>
</table>

#### Portal 门户页面组件
<table>
    <tr>
        <td><a href="PortalPage.html">页面 - PortalPage</a></td>
        <td><a href="MWF.xApplication.process.Xform.Div.html">容器 - Div</a></td>
        <td><a href="MWF.xApplication.process.Xform.Label.html">文本 - Label</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Source.html">数据源 - Source</a></td>
        <td><a href="MWF.xApplication.process.Xform.SubSource.html">子数据源 - SubSource</a></td>
        <td><a href="MWF.xApplication.process.Xform.SourceText.html">数据文本 - SourceText</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Table.html">表格 - Table</a></td>
        <td><a href="MWF.xApplication.process.Xform.Table$Td.html">单元格 - Table$Td</a></td>
        <td><a href="MWF.xApplication.process.Xform.Tab.html">分页 - Tab</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Image.html">图片 - Image</a></td>
        <td><a href="MWF.xApplication.process.Xform.Button.html">按钮 - Button</a></td>
        <td><a href="MWF.xApplication.process.Xform.Iframe.html">Iframe - Iframe</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Common.html">通用元素 - Common</a></td>
        <td><a href="MWF.xApplication.process.Xform.Tree.html">树 - Tree</a></td>
        <td><a href="MWF.xApplication.process.Xform.View.html">嵌入视图 - View</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Stat.html">嵌入统计 - Stat</a></td>
        <td><a href="MWF.xApplication.process.Xform.Textfield.html">文本字段 - Textfield</a></td>
        <td><a href="MWF.xApplication.process.Xform.Org.html">人员组织 - Org</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Calendar.html">日期选择 - Calendar</a></td>
        <td><a href="MWF.xApplication.process.Xform.Textarea.html">多行文本 - Textarea</a></td>
        <td><a href="MWF.xApplication.process.Xform.Select.html">下拉框 - Select</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Radio.html">单选框 - Radio</a></td>
        <td><a href="MWF.xApplication.process.Xform.Checkbox.html">多选框 - Checkbox</a></td>
        <td><a href="MWF.xApplication.process.Xform.Widget.html">部件 - Widget</a></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.Statement.html">嵌入查询视图 - Statement</a></td>
        <td><a href="MWF.xApplication.process.Xform.Datatemplate.html">数据模板 - Datatemplate</a></td>
        <td></td>
    </tr>
    <tr>
        <td><a href="MWF.xApplication.process.Xform.DatatablePC.html">数据表格PC端 - DatatablePC</a></td>
        <td><a href="MWF.xApplication.process.Xform.DatatableMobile.html">数据表格移动端 - DatatableMobile</a></td>
        <td><a href="MWF.xApplication.process.Xform.Importer.html">数据导入 - Importer</a></td>
    </tr>
</table>



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

O2OA 可在多个位置嵌入脚本代码，用于扩展平台和实现自定义功能。嵌入脚本代码分为前端执行代码和服务端执行代码，两者语法一致，本文档指前台脚本。


### 脚本:

流程平台、门户平台和内容管理平台中，都有脚本设计元素，可以在此创建自己的脚本库。如下图：
![脚本](img/home/script.png)
<br/><br/>

### 表单、页面、视图、查询视图及其组件事件：

流程平台和内容平台的表单、门户平台的页面、数据平台的视图和查询视图中，每个设计组件包含多种事件，包括DOM对象原生事件和O2平台扩展事件。如下图：
![事件中的脚本](img/home/script_event.png)
<br/><br/>

### 表单、页面可编辑组件默认值：

流程平台和内容平台的表单和门户平台的页面中，可编辑组件或文本组件的默认值可以通过脚本指定。如下图：
![事件中的脚本](img/home/script_defaultvalue.png)
<br/><br/>

### 表单、页面、视图、查询视图的部分属性：

流程平台和内容平台的表单、门户平台的页面、数据平台的视图和查询视图中，有许多组件的相关属性可以通过脚本来定义。<br/>
如：下拉框、单选多选按钮的可选值、人员字段的选择范围、区段依据等。如下图：<br/>
![属性中的脚本](img/home/script_attribute.png)
<br/><br/>

### 表单及可编辑组件校验：

流程平台和内容管理的表单中，可编辑的字段可以通过脚本进行有效性校验，校验通过返回true，不通过返回提示信息。如下图：
![脚本](img/home/script_validation.png)
<br/><br/>

### 流程路由属性的扩展附签和选择附签下的脚本：
![扩展附签脚本](img/home/script_route_extend.png)
![选择附签脚本](img/home/script_route_select.png)
<br/><br/>



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