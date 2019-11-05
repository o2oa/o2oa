# O2OA相关教程

[安装管理及配置](https://o2oa.gitbook.io/course/ping-tai-shi-shi)

[业务开发与设计](https://o2oa.gitbook.io/course/liu-cheng-guan-li)

[平台内置应用介绍](https://o2oa.gitbook.io/course/xi-tong-ying-yong-jie-shao)

[源码的编译及管理](https://o2oa.gitbook.io/course/yuan-ma-de-bian-yi-ji-guan-li)

***

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

Gitee : https://gitee.com/o2oa/O2OA

Github : https://github.com/o2oa/o2oa

GitBook : https://o2oa.gitbook.io/course/

脚本API：http://www.o2oa.net/x_desktop/portal.html?id=dcd8e168-2da0-4496-83ee-137dc976c7f6

O2OA开发相关教程天梯：https://my.oschina.net/o2oa/blog/3016363

# 关于正式环境数据安全相关的建议\:

O2OA自带的H2数据库是一个内嵌式的内存数据库，适合用于开发环境、功能演示环境，并不适合用作正式环境。

如果作为正式环境使用，建议您使用拥有更高性能，更加稳定的商用级别数据库。如Mysql8，Oracle12C，SQLServer 2012等。

另外，O2OA提供数据定期备份和恢复的能力，建议您开启正式环境的数据定期备份的功能，以确保数据库异常时可以进行数据恢复。


# 最新版本服务器安装包下载[o2server_V4.2698]\:

windows 64Bit : http://download.o2oa.net/download/o2server_20191103191129_windows.zip

Linux 64Bit : http://download.o2oa.net/download/o2server_20191103191129_linux.zip

MacOS : http://download.o2oa.net/download/o2server_20191103191129_macos.zip

AIX : http://download.o2oa.net/download/o2server_20191103191129_aix.zip

中标麒麟（龙芯）：http://download.o2oa.net/download/o2server_20191103191129_neokylin_loongson.zip

raspberrypi(树莓派)：http://download.o2oa.net/download/o2server_20191103191129_raspberrypi.zip



# 官方网盘下载\:

百度云盘：https://pan.baidu.com/s/1oBQ1atXGyXdLaYE5uAqF1w   提取码: pnk9

腾讯微云：https://share.weiyun.com/5krUMjj


# 最新版本 v4.2698\:

新增功能：[文档发布]新增GitBook，并且持续更新：https://o2oa.gitbook.io/course/。

新增功能：[流程管理]在线编辑，增加符合国家党政机关公文格式GB/T 9704-2012版式文件。

新增功能：[流程管理]引擎强化，增加流程处理授权功能。

新增功能：[流程管理]引擎强化，增加拆分任务合并的业务场景。

新增功能：[流程管理]引擎强化，增加支持不对称的拆分合并应用场景。

新增功能：[流程管理]引擎强化，实现添加流程环节额外处理人业务场景，临时添加处理人。

新增功能：[流程管理]引擎强化，工作流转日志增加身份的输出。

新增功能：[流程管理]引擎强化，添加路由类型，通过路由添加处理人，实现转派的业务场景。

新增功能：[流程管理]功能强化，流程平台提交界面中增加选择人员的功能。

新增功能：[平台能力]集群强化，消除Center单点，支持多Center集群结构。

新增功能：[平台能力]文件存储，支持外部存储，支持webdav。

新增功能：[平台能力]在线编辑，支持Html转Word功能。

新增功能：[平台能力]在线编辑，支持odf格式版式文件。

新增功能：[平台能力]系统支持，新增加树莓派版本，支持3B+以上版本。

新增功能：[平台能力]数据中心，增加Projection映射功能，可以自定义数据到表的映射。

新增功能：[平台能力]组织架构，实现多组织隔离，可以在同一台服务器上运行多个不同的组织。

新增功能：[平台能力]消息管理，增加webSocket的心跳信号，解决Jetty关闭webSocket问题。

新增功能：[服务管理]定时任务，触发添加锁定，防止耗时长的任务反复执行。

新增功能：[服务管理]定时任务，添加定时任务执行日志，自动记录运行失败的任务。

新增功能：[源码结构]结构优化，支持自定义web程序模块开发。

新增功能：[门户管理]前端组件增加，门户管理中加入部件设计，增加设计成果的复用度。

新增功能：[内容管理]前端组件增加，内容管理增加评论组件。

新增功能：[内容管理]新增CMS服务：getControl，根据ID获取人员对CMS文档的访问控制信息

新增功能：[内容管理]更新文档保存方法，支持录入发布时间，支持使用published状态把文档设置为发布状态

新增功能：[内容管理]CMS新增通过表单标识和栏目标识获取表单对象的服务

新增功能：[移动办公IOS]切换了新的企业证书，需要重新下载IOS客户端，否则无法正常登录使用。

新增功能：[移动办公IOS]添加JSAPI，通讯录选择器功能，可以支持流程、门户调用IOS源生的选择器，人员、组织、身份、群组单个或者复合选择

新增功能：[移动办公IOS]添加会议管理扫码签到功能

新增功能：[移动办公IOS]添加登录方式切换功能

新增功能：[移动办公Android]添加JSAPI，通讯录选择器功能，可以支持流程、门户调用Android源生的选择器，人员、组织、身份、群组单个或者复合选择。

新增功能：[移动办公Android]添加会议管理扫码签到功能。

新增功能：[移动办公Android]添加登录方式切换功能。

系统优化：[流程管理]引擎优化，合并节点由保留等待合并改为优先删除。

系统优化：[平台能力]源码依赖调整，Apache Tika升级到1.22。

系统优化：[平台能力]源码依赖调整，POI降级版本4.10->4.01以匹配Tika版本。

系统优化：[源码结构]结构优化，修改编译pom，取消编译时ant的使用,编译不再需要ant支持。

系统优化：[内容管理]提交代码cms_express

系统优化：[内容管理]优化CMS附件权限控制服务

系统优化：[内容管理]优化附件管理，为CMS的附件添加xtype和xtext列

系统优化：[内容管理]将CMS所有的action服务响应统一换成了asyncResponse

系统优化：[内容管理]取消CMS文档获取服务返回数据中的attachmentList，由前端采用单独的服务异步获取。

系统优化：[内容管理]内容管理和流程平台的选人/组织进行了整合。

系统优化：[系统能力]erase content CMS加入新增的Review相关数据表

系统优化：[内容管理]CMS视图适应scopeType="全部"

系统优化：[内容管理]CMS添加附件权限控制服务，优化附件相关缓存设计

系统优化：[移动办公IOS]优化会议管理选择会议室、选择人员的界面

系统优化：[移动办公Android]首页界面整体调整和IOS双端尽量一致

系统优化：[移动办公Android]优化会议管理选择会议室、选择人员的界面

问题修复：[流程管理]修复通过data update 接口更新数据导致workId,workCompletedId,completed字段被覆盖的问题。

问题修复：[流程管理]修复拆分合并不匹配的情况下无法正常合并的问题。

问题修复：[平台能力]调整模块启动顺序，修复服务器启动后立即访问报错的问题。

问题修复：[平台能力]修正钉钉同步人员没有列入白名单错误提示。

问题修复：[平台能力]修正自定义数据表增加草稿后无法正常编译的问题。

问题修复：[平台能力]修正使用神经网络数据集找不到的问题。

问题修复：[平台能力]修复某些情况下H2数据自动创建数据库失败的问题。

问题修复：平台能力]修复Center节点选举升序的问题。

问题修复：[平台能力]修正某些情况下piped read end导致的服务器CPU异常高占用。

问题修复：[数据中心]修复某些情况下视图根据日期范围选择数据无效的问题。

问题修复：[服务管理]修复后台代理添加个人属性权限不足的问题。

问题修复：[考勤管理]修复考勤打卡分析的问题。

问题修复：[社区管理]修复投票贴缓存引起的无法投票的问题。

问题修复：[社区管理]修复BBS分区和版块可见权限保存后，内容仍显示为空的问题。

问题修复：[内容管理]修复CMS文档置顶标识未写入item导致视图里的$document.isTop一直是false的问题。

问题修复：[内容管理]修复发布文档时的问题，修复API测试页面，Post的时候data为空时的报错。

问题修复：[内容管理]修复CMS中栏目可见范围在某些情况下不生效的问题。

问题修复：[内容管理]修复CMS定时任务运行时产生的的一个错误。

问题修复：[内容管理]修复CMS栏目和分类有管理权限的组织内人员无法管理文档的问题。

问题修复：[内容管理]修复CMS在某些情况下权限不正确的问题。

问题修复：[移动办公IOS]修复某些情况下会议管理的关联流程失败的问题。

问题修复：[移动办公IOS]修复某些机型下IM聊天选择图片闪退的问题。

问题修复：[移动办公Android]修复某些情况下会议管理的关联流程失败的问题。

问题修复：[移动办公Android]修复结束工作下载附件失败的问题。

问题修复：[移动办公Android]修复某些情况下IM聊天选人的问题。

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



