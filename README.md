

O2OA
==========
## 简介
O2OA 是一套办公平台。她有以下特点\:    
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
  前台程序位于github的x_desktop_web目录  
  
#### 目录规范
*   每个应用都是以 `x_component_{APPLICATION_NAME}`方式来命名，如x_component_Attendance表示考勤的目录    
*   应用中至少包括下列文件及目录
```bash
x_component_{APPLICATION_NAME}
    Main.js                 //应用主程序  
    $Main                   //主程序用到的资源包  
        appicon.png         //应用图标，在桌面上显示  
        default             //样式包，可以创建其他名称的样式包，并在options传入到Main.js以改变页面风格  
          css.wcss          //样式文件，以json格式编写  
    lp                      //语言包，目前支持中文  
        zh-cn.js        
    Actions                 
        action.json         //后台服务的url和方法，本系统使用JAX-RS 方式的 RESTful Web Service
        RestAction.js       //应用程序中直接使用此类的方法进行后台交互
```
