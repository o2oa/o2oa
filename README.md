# O2OA : Java企业信息化系统,开源OA openSource OA Platform

O2OA应用开发平台是兰德纵横网络技术股份有限公司发布和维护的开源产品，是使用JavaEE技术栈，分布式架构设计的一款真正全代码开源的企业应用定制化开发平台。适用于企业OA、协同办公类信息化系统的建设和开发。

![o2oa](https://static.oschina.net/uploads/space/2018/0918/200301_N9TG_3931542.png)

O2OA平台拥有流程管理、门户管理、信息管理、数据管理和服务管理五大核心能力。用户可以直接使用平台已有功能进行信息信息化建设，平台提供了完整的用户管理，权限管理，流程和信息管理体系，并且提供了大量的开发组件和开箱即用的应用，可以大幅度减化企业信息化建设成本和业务应用开发难度。

### 若开发者学习研究O2OA，企业在O2OA应用开发平台上建设内部使用的办公系统，不闭源分发版本，不参与商业项目的使用行为不会构成侵权风险。

### 如果需要进行转售，闭源分发或者在商业项目中作为项目的一部分使用，请主动联系兰德网络公司购买商用许可。

商用许可说明：https://www.o2oa.net/license.html

技术支持服务：https://www.o2oa.net/support.html

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

语雀文档 : https://www.o2oa.net/course

脚本API：http://www.o2oa.net/api/


# 关于正式环境数据安全相关的建议\:

O2OA自带的H2数据库是一个内嵌式的内存数据库，适合用于开发环境、功能演示环境，并不适合用作正式环境。

如果作为正式环境使用，建议您使用拥有更高性能，更加稳定的商用级别数据库。如Mysql8，Oracle12C，SQLServer 2012等。

另外，O2OA提供数据定期备份和恢复的能力，建议您开启正式环境的数据定期备份的功能，以确保数据库异常时可以进行数据恢复。


# 最新版本服务器安装包下载地址\: https://www.o2oa.net/download.html

## 支持操作系统：

    Windows 64Bit, Linux 64Bit[CentOS, RedHat, Ubuntu等], MacOS, AIX, Raspberrypi(树莓派),

    ARM_Linux,
    
    MIPS_Linux,
    
    UOS,麒麟等国产操作系统

## 支持数据库：

    自带H2数据库.

    第三方数据库: Apache Derby, Borland Interbase, Borland JDataStore, DB2, Empress, Firebird, H2 Database Engine, 
                  Hypersonic Database Engine, Informix Dynamic Server, Ingres Database, InterSystems Cache,
                  Microsoft Access, Microsoft SQL Server, Microsoft Visual FoxPro, MySQL, Oracle, Pointbase,
                  PostgreSQL, IBM solidDB, Sybase Adaptive Server Enterprise, 达梦DM7+, 人大金仓, 南大通用, 神通数据库


# 官方网盘下载\:

百度云盘：https://pan.baidu.com/s/1oBQ1atXGyXdLaYE5uAqF1w   提取码: pnk9


# 最新源码编译教程\:http://www.o2oa.net/course/ng5iqb.html


# 服务器部署教程

    Windows环境：https://www.o2oa.net/course/qq5gny.html
    
    Linux环境：https://www.o2oa.net/course/yto8af.html


# 最新版本 v6.2.x\:

功能新增

[前端通用]新增了服务器中自带前端API文档的功能

[前端通用]前端API文档增加了表单组件的说明

[前端通用]前端API文档增加了Promise的说明

[前端通用]前端API文档中workContext、documentContext、org的样例根据后台最新数据获取

[移动办公]移动端原生能力支持的JSAPI文档

[数据中心]新增了自建表数据导出到Excel文件、从Excel文件中导入数据到自建表的功能

[平台主页]新增了任务栏tab页的“全部关闭”和“关闭其他”等功能

[平台主页]新增了应用菜单的排序和分组功能

[内容管理]新增了发布文档允许上传其他cms文档的附件的功能

[内容管理]新增了根据url上传附件接口

[流程平台]新增了根据job获取工作附件列表的接口

[流程平台]新增了批量上传附件到多个work的接口

[流程平台]新增了根据url上传附件接口

功能优化

[移动办公]IM聊天消息功能结构优化

[移动办公]云盘分享功能改进

[移动办公]附件缓存功能优化

[系统配置]系统设置中配置修改实时生效，无需重启server（除了node配置修改）

[人员组织]根据职务和组织查询身份同时返回关联组织的排序号

[用户认证]兼容支持用户密码为md5加密的密码登录

[数据中心]视图支持数组查询和展现

[内容管理]表单结构优化，前端加载速度优化

[人员组织]补充微信、钉钉等单点认证审计日志

问题修复

[移动办公]修复了钉钉扫码功能

[流程平台]修复了待阅打开附件报权限错误的问题

[流程平台]修复了处理转交的授权没有record流转记录的问题

[流程平台]修复了回溯到拟稿环节没有待办的问题

[内容管理]修复了内容管理编辑者无法编辑文档的问题

[数据中心]修复了自建表不能修改可读或可编辑人员组织的问题

[平台架构]修复了服务请求的Promise rejection错误的问题

[平台架构]修复了double数据类型数据失真的问题

# 协议

[AGPL-3.0 开源协议。](./LICENSE)



# 关于

[![img](./assets/O2OA-logo.jpg)](./assets/O2OA-logo.jpg)



O2OA开发平台是由 **浙江兰德纵横网路技术股份有限公司** 建立和维护的。O2OA 的名字和标志是属于 **浙江兰德纵横网路技术股份有限公司** 的注册商标。

我们 ❤️ 开源软件！看一下[我们的其他开源项目](https://github.com/o2oa)，瞅一眼[我们的博客](https://my.oschina.net/o2oa)。