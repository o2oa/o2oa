O2OA
==========## 简介
O2OA 是一套现代办公平台。她有以下特点\:    
1. 核心代码开源    
2. 全功能免费    
3. 私有化部署，下载软件后可以安装在自己的服务器上    
4. 随时随地办公，平台支持兼容HTML5的浏览器，并且开发了源生的IOS/Android应用    
5. 高可扩展性，用户通过简单的学习后，可以自定义配置门户、流程应用、内容管理应用    

更多的产品介绍、下载、在线体验、API及讨论请移步至[http://o2oa.io/](http://www.o2oa.io/)

## 安装
建议 `fork` 本仓库后进行二次开发   
`fork` 操作完成后，会在您的 github 账户下创建一个 o2oa 的副本。接下来可以克隆到本地。  
```bash  
cd {YOUR_WORKING_DIRECTORY}
git clone https://github.com/{YOUR_GITHUB_USERNAME}/o2oa.git  
```
## 前台开发
在前台开发前，请先了解`mootools`, 我们基于 `mootools`[https://mootools.net/](https://mootools.net/) 架构创建了产品的js类库。

### 目录
  前台程序位于github的x_desktop_web目录。  
 
#### 目录说明
```bash
x_desktop                                //桌面资源、平台基础资源和通用工具类
    config                                //系统配置目录
    css                                
    framework                            //系统用到的开源框架
        ace                                //脚本编辑器框架
        d3                                //图表框架
        echarts                            //考勤用到的图表框架
        htmleditor                        //富文本编辑器
        kityminder                        //脑图
        mootools                        //模块化、面对对象的JS Web应用框架
        raphael                            //矢量图形框架，在流程图配置中用到
    js                                
    mwf4                                //通用资源和工具
        package
            lp                            //系统桌面语言包
            widget                        //通用工具类
            xAction                        //登录页和桌面用到的后台交互类
            xDesktop                    //系统桌面工具类
            xScript                        //流程引擎和内容管理自定义脚本的运行环境
        MWF.js                            //底层方法JS库，对mootools的补充
    preview                                //预览界面模拟json
    common.js                            //底层方法、通用方法、框架引用JS库
    index.html                            //首页HTML
    app.html                            //在新窗口打开应用时的HTML
    forum.html                            //在新窗口打开论坛的HTML
    cmsdocMobile.html                    //手机端打开内容管理文档页面的HTML
    {NAME}.html                            //其他在新窗口打开的HTML
x_component_Attendance                    //考勤应用
x_component_Chat                        //在线交流应用
x_component_cms_Column                    //CMS（内容管理）栏目的列式、增删配置
x_component_cms_ColumnManager            //CMS栏目管理设置（分类、表单、脚本、数据字典的列式、增删）
x_component_cms_Document                //CMS文档
x_component_cms_FormDesigner            //CMS表单设计
x_component_cms_Index                    //CMS首页
x_component_cms_Module                    //CMS栏目页面
x_component_cms_QueryViewDesigner        //CMS查询视图设计（嵌入在文档中）
x_component_cms_ScriptDesigner            //CMS脚本设计
x_component_cms_ViewDesigner            //CMS列式视图设计
x_component_cms_Xform                    //CMS文档中表单的实现
x_component_Execution                    //OKR应用
x_component_ExeManager                    //OKR文档的管理界面
x_component_File                        //云文件
x_component_Forum                        //论坛首页
x_component_ForumCategory                //论坛分类页面
x_component_ForumDocument                //论坛帖子页面
x_component_ForumPerson                    //论坛个人中心
x_component_ForumSearch                    //论坛搜索结果页面
x_component_ForumSection                //论坛版块界面
x_component_HotArticle                    //热点图片管理界面
x_component_Meeting                        //会议室管理
x_component_Note                        //桌面记事本插件
x_component_OnlineMeeting                //在线会议应用
x_component_OnlineMeetingRoom            //在线会议室
x_component_Organization                //人员、部门、组织管理以及选择
x_component_portal_PageDesigner            //门户页面设计
x_component_portal_Portal                //门户展现
x_component_portal_portalExplorer
x_component_portal_PortalManager        //门户管理设置（页面、脚本的列式、增删）
x_component_portal_ScriptDesigner        //门户脚本设计
x_component_portal_XPage                //门户页面实现
x_component_process_Application            //流程应用
x_component_process_ApplicationExplorer    
x_component_process_DictionaryDesigner    //流程数据字典
x_component_process_FormDesigner        //流程表单设计
x_component_process_ProcessDesigner        //流程图设计
x_component_process_ProcessManager        //流程管理设置（流程图、表单、脚本、数据字典的列式、增删）
x_component_process_ScriptDesigner        //流程脚本设计
x_component_process_StatDesigner        //流程统计、流程监控设计
x_component_process_TaskCenter            //办公中心，普通用户发起流程和查看待办
x_component_process_ViewDesigner        //流程查询视图设计
x_component_process_Work                //流程任务的展现
x_component_process_Xfrom                //流程表单的实现
x_component_Profile                        //用户个人设置界面
x_component_Template                    //列式、弹出页接口类
```

 
#### 应用目录规范
*   每个应用都是以 `x_component_{APPLICATION_NAME}`方式来命名，如x_component_Attendance表示考勤的目录。    
*   应用中至少包括下列文件及目录\:
```bash
x_component_{APPLICATION_NAME}
    Main.js                 //应用主程序  
    $Main                   //主程序用到的资源包  
        appicon.png         //应用图标，在桌面上显示  
        default             //样式包，可以创建其他名称的样式包，并在options传入到Main.js以改变页面风格  
          css.wcss          //样式文件，以json格式编写  
    lp                      //语言包，目前支持中文  
        zh-cn.js        
    Actions                 
        action.json         //后台服务的url和方法，本系统使用JAX-RS 方式的 RESTful Web Service
        RestAction.js       //应用程序中直接使用此类的方法进行后台交互
```
