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

脚本API：http://www.o2oa.net/x_desktop/portal.html?id=dcd8e168-2da0-4496-83ee-137dc976c7f6

O2OA开发相关教程天梯：https://my.oschina.net/o2oa/blog/3016363


# 最新版本服务器安装包下载[o2server_20190426125713]\:

windows 64Bit : http://download.o2oa.net/download/versions/o2server_20190426125713_windows.zip

Linux 64Bit : http://download.o2oa.net/download/versions/o2server_20190426125713_linux.zip

MacOS : http://download.o2oa.net/download/versions/o2server_20190426125713_macos.zip

AIX : http://download.o2oa.net/download/versions/o2server_20190426125713_aix.zip


# 官方网盘下载\:

百度云盘：https://pan.baidu.com/s/1oBQ1atXGyXdLaYE5uAqF1w   提取码: pnk9

腾讯微云：https://share.weiyun.com/5krUMjj


# 最新版本 v4.1660\:

[2019-04-26]新增功能：[用户认证]可以通过配置设置登录二维码的logo图片。

[2019-04-26]新增功能：[流程引擎]增加可以针对多级拆分状态下的增加任意分支功能。

[2019-04-26]新增功能：[用户管理]增加登录错误次数限制以及锁定时间限制。

[2019-04-26]新增功能：[系统能力]增加自定义数据表和数据服务的能力。

[2019-04-26]新增功能：[内容管理API]增加根据指定时间获取最新发布的信息数量和列表服务。

[2019-04-26]系统优化：[流程引擎API]取得当前工作待办getTaskList()，取得当前任务待办(包括所有分支)getJobTaskList()。

[2019-04-26]系统优化：[流程引擎API]取得当前工作已办getTaskCompletedList()，取得当前任务待办(包括所有分支)getJobTaskCompletedList()。

[2019-04-26]系统优化：[流程引擎API]取得当前工作待阅getReadList()，取得当前任务待办(包括所有分支)getJobReadList()。

[2019-04-26]系统优化：[流程引擎API]取得当前工作已阅getReadCompletedList()，取得当前任务待办(包括所有分支)getJobReadCompletedList()。

[2019-04-26]系统优化：[用户管理]手机号码从默认支持大陆和香港改为默认支持大陆港澳台。

[2019-04-26]系统优化：[信息管理]增加清空栏目和清空分类文档信息接口。

[2019-04-26]系统优化：[表单设计]修改移动端H5提交页面样式。

[2019-04-26]系统优化：[表单设计]修改移动端H5人员选择界面样式。

[2019-04-26]系统优化：[表单设计]优化表单定位方法，支持在左侧结构中进行组件移动。

[2019-04-26]系统优化：[系统安全]session相关优化。

[2019-04-26]系统优化：[系统优化]系统页面展现效率相关优化。

[2019-04-26]BUG修复：[流程引擎]自动启动的工作被误判为草稿删除的BUG。

[2019-04-26]BUG修复：[流程引擎]撤回按钮设置无效的BUG。

[2019-04-26]BUG修复：[组织管理]通过EXCEL导入人员重名判断为重复用户的问题。

[2019-04-26]BUG修复：[表单设计]打印表单只打印第一页的问题。

[2019-04-26]BUG修复：[Office编辑器]修复套红事件无法使用的问题。

[2019-04-26]BUG修复：[人员选择]修复人员选择器会弹出对话框'1'的问题。

[2019-04-26]BUG修复：[流程服务]修复COPY流程附件到另一个流程时没有的权限问题。


# 配置编译环境\:

强烈建议将项目Fork到自己的仓库里，Clone到本地后进行编译和打包，偶尔会发现下载zip文件后，commons/ext目录里的jar包不可用，全部只有1k大小。

https://github.com/o2oa/o2oa 仓库里的源码已经编译打包测试通过，无任何问题。


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


# 关于授权协议\:

o2oa软件遵守双重协议，一个是AGPL授权协议，一个是商用授权协议。

1、o2oa是开源软件，您可以修改源码及免费使用；这时需遵守AGPL协议。  

2、当使用者使用o2oa软件提供收费服务，或者对o2oa进行分发、销售时需进行商业授权。

   具体请查看：[http://www.o2oa.net/product.html](http://www.o2oa.net/product.html)。  

3、使用者下载本软件即表示愿遵守此项协议。  


## 什么是商业授权？

商业授权是软件开发者授权用户将软件用于商业用途的凭证（商业使用权利）。

## 开源软件为什么还要购买商业授权？

开源不等于免费，公开源码是为了方便用户二次开发，便于学习和交流。

未获商业授权之前，不得将本软件用于商业用途（包括但不限于政府办公系统、企业门户平台、经营性项目、以营利为目或实现盈利的项目）。

任何情况下都不得对O2OA办公平台的商业授权进行出租、出售、抵押或发放子许可证。

## 哪些用户需要购买商业版权？

1）直接将O2OA进行定制化售卖。

2）将O2OA或者一部分功能集成到定制项目或者产品内，完成项目的部分功能或者提升产品能力。

3）将O2OA或者一部分功能转赠并且以其他形式获得利益（比如为了获得其他项目而免费赠送OA产品）。

## 一份商业授权可以用于多个项目吗？

可以。O2OA商业授权是按年来授权的，在授权期限内，您可以无限制地使用O2OA进行任何合法的商业活动。

## 购买商业授权除了能使用O2OA进行商业活动还有什么好处？

购买了商业授权后，您和您的企业将会成为O2OA注册合作伙伴。除了使用O2OA进行商业活动之外，合作伙伴还有可能获得O2OA推送的商业项目机会。

## 商业授权和软件版本有关联吗？

商业授权与软件版本无关，商业授权在有效期内可以无限制地进行版本升级，所有的O2OA版本均无版本、用户数、数据量等限制。

## 商业授权过期后对已经完成的项目会不会有影响？

商业授权是按年收费的，当所购买的商业授权过期后，您将无法再使用O2OA进行任何商业活动，但是您已经出售的O2OA软件或者已经使用O2OA完成后商业项目不会受到影响，仍可以继续使用和正常升级。当您需要再次使用O2OA进行商业活动的时候，只需要再次购买O2OA商业授权即可。



