# O2OA : Java企业信息化系统

O2OA是基于J2EE架构，集成移动办公、智能办公，支持私有化部署，自适应负载能力的，能够很大程度上节约企业软件开发成本的基于AGPL协议开放源代码的企业信息化系统需求定制开发解决方案，对外提供专业的开发运维等技术服务。

![o2oa](https://static.oschina.net/uploads/space/2018/0918/200301_N9TG_3931542.png)

O2OA平台拥有流程管理、门户管理、信息管理、数据管理和服务管理五大核心能力。用户可以直接使用平台已有功能进行信息信息化建设，平台提供了完整的用户管理，权限管理，流程和信息管理体系，并且提供了大量的开发组件和开箱即用的应用，可以大大减化企业信息化建设成本和业务应用开发难度。


# 其主要能力如下：

流程管理：全功能流程引擎。基于任务驱动，开放式服务驱动，高灵活性、扩展性，事件定义丰富。包含人工、自动、拆分、合并、并行、定时、服务调用、子流程等功能。应用场景丰富，可轻松实现公文、合同、项目管理等复杂工作流应用。

信息管理：具有权限控制能力的内容管理平台。支持自定义栏目、分类，表格，表单，多级权限系统，能轻松实现知识管理、通知公司、规章制度、文件管理等内容发布系统。

门户管理：具体可视化表单编辑的，支持HTML直接导入的，支持各类数据源，外部应用集成能力的，所见即所得的门户管理平台。适用于实现企业信息化门户系统，可以轻松结合O2OA提供的认证设置与其他系统进行单点认证集成。

服务管理：可以在前端脚本的形式，开发和自定义web服务，实现与后端服务数据交互的能力。

数据中心：可以通过配置轻松实现数据透视图展示，数据统计、数据可视化图表开发等等功能。

智能办公：拥有语音办公、人脸识别、指纹认证、智能文档纠错、智能填表推荐等智能办公特色

移动办公：支持安卓\IOS手机APP办公，支持与企业微信和钉钉集成，支持企业私有化微信部署

开箱即用：O2OA还提供如考勤管理、日程管理、会议管理、脑图管理、便签、云文件、企业社区、执行力管理等开箱即用的应用供企业选择


# 产品特点\:

1. 代码全部开源，开发者可以下载源码进行任意，编译成自己的信息化平台。

2. 平台全功能免费，无任何功能和人数限制。

3. 支持私有化部署，下载软件安装包后可以安装在自己的服务器上，数据更安全。

4. 随时随地办公，平台支持兼容HTML5的浏览器，并且提供了原生的IOS/Android应用，并且支持钉钉和企业微信集成。

5. 高可扩展性，用户通过简单的学习后，可以自定义配置门户、流程应用、内容管理应用

更多的产品介绍、使用说明、下载、在线体验、API及讨论请移步至[http://www.o2oa.net/](http://www.o2oa.net/)


# 官方网站\:

开源主页 : https://www.oschina.net/p/o2oa

官方网站 : http://www.o2oa.net

Gitee : https://gitee.com/liyihz2008/O2OA

Github : https://github.com/o2oa/o2oa

GitBook : https://o2oa.gitbook.io/course/

脚本API：http://www.o2oa.net/x_desktop/portal.html?id=dcd8e168-2da0-4496-83ee-137dc976c7f6

O2OA开发相关教程天梯：https://my.oschina.net/o2oa/blog/3016363

# 关于正式环境数据安全相关的建议\:

O2OA自带的H2数据库是一个内嵌式的内存数据库，适合用于开发环境、功能演示环境，并不适合用作正式环境。

如果作为正式环境使用，建议您使用拥有更高性能，更加稳定的商用级别数据库。如Mysql8，Oracle12C，SQLServer 2012等。

另外，O2OA提供数据定期备份和恢复的能力，建议您开启正式环境的数据定期备份的功能，以确保数据库异常时可以进行数据恢复。


# 最新版本服务器安装包下载[o2server_V4.2235]\:

windows 64Bit : http://download.o2oa.net/download/o2server_20190828215118_windows.zip

Linux 64Bit : http://download.o2oa.net/download/o2server_20190828215118_linux.zip

MacOS : http://download.o2oa.net/download/o2server_20190828215118_macos.zip

AIX : http://download.o2oa.net/download/o2server_20190828215118_aix.zip

中标麒麟（龙芯）：http://download.o2oa.net/download/o2server_20190828215118_neokylin_loongson.zip


# 官方网盘下载\:

百度云盘：https://pan.baidu.com/s/1oBQ1atXGyXdLaYE5uAqF1w   提取码: pnk9

腾讯微云：https://share.weiyun.com/5krUMjj


# 最新版本 v4.2235\:

[2019-06-25]新增功能：[内容管理]增加文章评论后端服务。

[2019-06-25]新增功能：[流程引擎]流程引擎性能及表单展示性能优化。

[2019-06-25]新增功能：[内容管理]重写内容管理权限控制逻辑，提升文档查询效率。

[2019-06-25]新增功能：[内容管理]修复某些情况下栏目和分类权限控制无效的问题。

[2019-07-11]新增功能：[流程引擎]优化加签减签功能。

[2019-07-11]新增功能：[内容管理]移动端发布功能。

[2019-07-11]新增功能：[内容管理]添加评论、置顶服务。

[2019-07-11]新增功能：[内容管理]添加一个服务（根据栏目ID查询，指定的可见的栏目信息），移动端使用。

[2019-07-11]新增功能：[数据管理]优化数据视图统计功能，提供视图分页能力。

[2019-07-11]新增功能：[流程表单]增加流程中意见手写板的高度宽度设置，增加流程记录中手写意见的高度宽度设置。

[2019-07-18]新增功能：[数据统计]增加待办已办自动分页能力。

[2019-07-18]新增功能：[数据统计]增加数据视图自动分页能力。

[2019-07-18]新增功能：[平台日志]添加平台审计日志相关功能。

[2019-08-23]新增功能：[平台功能]增加303跳转的返回定义。

[2019-08-23]新增功能：[流程管理]增加管理员修改待阅,已阅,待办,已办意见接口。

[2019-08-23]新增功能：[流程管理]在待办已办中允许更长的流程意见。

[2019-08-23]新增功能：[流程管理]增加已完成工作的维护接口。

[2019-08-23]新增功能：[信息发布]信息发布设计中增加栏目分类。

[2019-08-23]新增功能：[会议管理]会议管理二维码签到功能。

[2019-07-11]系统优化：[内容管理]修改内容管理列表的批量操作方式。

[2019-07-11]系统优化：[前端代码]修改 mask 的 resize 问题。

[2019-07-18]系统优化：[服务器优化]优化数据库相关逻辑，修改数据库支持。

[2019-07-18]系统优化：[流程平台]增加title标题字段最大长度。

[2019-07-18]系统优化：[流程平台]删除流程参阅人员相关配置。

[2019-07-18]系统优化：[流程表单]优化手写板组件，增强配置能力。

[2019-07-18]系统优化：[内容管理]优化视图查询的效率，删除无效查询语句。

[2019-07-18]系统优化：[前端框架]优化整理前端代码，进一步提高展现效率。

[2019-08-23]系统优化：[信息发布]信息发布内嵌入视图功能优化。

[2019-08-23]系统优化：[表单设计]避免子表单的相互嵌套、重发插入子表单、字段的重复，以及子页面的重复嵌套。

[2019-08-23]系统优化：[系统服务]服务地址修改jaxrs/read/{id}/completed/manage改为jaxrs/read/{id}/processing/manage。

[2019-08-23]系统优化：[系统服务]服务地址修改jaxrs/task/{id}/completed/manage改为jaxrs/task/{id}/processing/manage。

[2019-08-23]系统优化：[系统服务]默认禁止列示目录。

[2019-08-23]系统优化：[系统登录]用户/密码提示信息改为'用户不存在或者密码错误'。

[2019-08-23]系统优化：[移动办公]移动端复合选人Tab页宽度问题。

[2019-08-26]系统优化：[移动办公]公文编辑器手机端显示更新。

[2019-08-26]系统优化：[表单设计]表单设计元素数据网格样式修改。

[2019-07-11]问题修复：[内容管理]修改草稿会显示在列表里的问题。

[2019-07-18]问题修复：[二次开发]修正定制模块类导入的问题。

[2019-07-18]问题修复：[二次开发]official模块默认路径被硬编码的问题。

[2019-07-18]问题修复：[内容管理]修复视图使用带权限查询时报错的问题。

[2019-07-18]问题修复：[内容管理]修复某些情况下栏目信息保存报错的问题。

[2019-08-23]问题修复：[系统报错]Error:Multiple concurrent threads attempted to access a single broker。

[2019-08-23]问题修复：[安全加固]XSS漏洞修复。

[2019-08-23]问题修复：[搜索]转义错误的BUG。

[2019-08-23]问题修复：[流程平台]修复已归档工作增加待阅问题问题。

[2019-08-23]问题修复：[信息发布]修复CMS新建文档时栏目选择列表为空的问题。

[2019-08-23]问题修复：[信息发布]修复未分类栏目无法显示的问题。

[2019-08-23]问题修复：[表单设计]修复视图组件添加条件的BUG。

[2019-08-26]问题修复：[系统问题]修复如果开启SSL，CenterAPI列表给出的URL都是HTTP协议的，无法访问的问题。

[2019-08-26]问题修复：[系统问题]修复服务API测试页面中某些情况下调用服务报JS异常的问题。


# 配置编译环境\:

强烈建议将项目Fork到自己的仓库里，Clone到本地后进行编译和打包，偶尔会发现下载zip文件后，commons/ext目录里的jar包不可用，全部只有1k大小。

下载源码建议安装 git lfs，然后 Clone, 这样获取的源码可以编译。https://github.com/o2oa/o2oa 仓库里的源码已经编译打包测试通过，无任何问题。



## 安装NodeJS

1、访问nodejs的官方网站的downdolad，网址：https://nodejs.org/en/download/，获取Linux Binaries (x64)安装包下载链接：

      wget https://nodejs.org/dist/v10.15.0/node-v10.15.0-linux-x64.tar.xz

2、解压安装：

	# yum search xz
	# yum install xz.i386
	# xz -d node-v10.15.0-linux-x64.tar.xz
	# tar -xf node-v10.15.0-linux-x64.tar
	# mv node-v10.15.0-linux-x64 node-v10.15.0

3、配置nodejs（略）

## 安装 Java8 及配置Java环境 

    略
	
## 安装 apache-maven-3.6.0 及配置maven环境 

    wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
	
    yum -y install apache-maven
	
	
## 安装 apache-ant-1.10.5 ant 	

    yum -y install ant
    

# 源码编译

1、打开命令提示工具，cd到o2oa/o2server目录

    cd /usr/o2oa/o2server

2、执行命令进行编译：mvn install，开始进行源码编译

    mvn install

3、打包安装程序： ant

    ant

## 关于编译错误
第一次使用maven进行项目编译执行 mvn install 可能会发生异常，原因是编译生成的META-INF/persistence.xml来不及加载。这种情况下，您可以再执行一次mvn install即可完成对项目的编译。

[ERROR] Failed to execute goal org.apache.openjpa:openjpa-maven-plugin:3.0.0:enhance(enhanncer) on project x_base_core_project: Exception enhancer of goal org.apache.openjpa:openjpa-maven-plugin:3.0.0:enhance failed: MetaDataFactory could not be configured (conf.newMetaDataFactoryInstance() returned null). This might mean that no configuration properties were fount. Ensured that you have a META-INF/persistence.xml file, that it is  available in your classpath, or that the properties file you are using for configuration is available. If you are using Ant, please see the <properties> or <propertiesFile> attributes of the task's nested <config> element. This can also occur if your OpenJPA distribution jars are corrupt, or if your security policy is overly strict. 

-> [Help 1][ERROR]......

如果在编译的时候遇到上述错误，直接重新再 mvn install 就可以了。

## 关于编译打包结果

o2oa/o2server/target目录下会有打包好的zip包，将此zip包Copy到其他目录解压（避免目录层级太深造成启动异常），然后启动服务即可。

服务器部署和启动相关的教程文档，请移步系列教程：https://my.oschina.net/u/3931542/blog/2209110


# 服务器部署

## 部署教程

开源中国技术博客：https://my.oschina.net/u/3931542

## windows部署步骤：

1.下载o2server_yyyyMMddHHmmss_windows.zip程序包。

2.解压下载后的压缩包到任意目录。

3.确认开通服务器的80、20020、20030端口。

4.打开o2server文件夹，选择start_windows.bat双击打开。

5.在命令行中输入"start" 回车,启动服务,等待相关服务启动完成。

6.启动完成后打开浏览器访问http://127.0.0.1。

7.输入用户名xadmin密码o2登陆系统。

O2OA自带的H2数据库是一个内嵌式的内存数据库，比适合用于开发环境、功能演示环境，并不适合用作正式环境使用。

如果作为正式环境使用，建议您使用拥有更高性能，更加稳定的商用级别数据库，如Mysql8，Oracle12C，SQLServer 2012等。

O2OA提供数据定期备份和恢复的能力，建议您开启正式环境的数据定期备份的功能，以确保数据库异常时可以进行数据恢复。



