<p align="center">
	<a target="_blank" href="https://github.com/o2oa/o2oa/blob/develop/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/o2oa/o2oa"></a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img src="https://img.shields.io/badge/JDK-11-green" />
	</a>
	<a target="_blank" href="https://gitee.com/o2oa/O2OA/stargazers">
		<img src="https://gitee.com/o2oa/O2OA/badge/star.svg?theme=dark" alt='gitee star'/>
	</a>
	<a target="_blank" href="https://github.com/o2oa/o2oa/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/o2oa/o2oa?style=social"></a>
</p>


----------------------------------------------------------------------------

# O2OA（翱途）开发平台（100%开源的OA与协同办公解决方案）

O2OA（翱途）低代码开发平台，100%开源企业协同办公定制平台，提供完整的前后端API和模块定制能力。平台基于JavaEE分布式架构，具备流程引擎、表单定制、页面定制能力及业务数据服务能力，支持跨平台移动办公，有效提升工作效率。

平台通过国产信创认证，支持白标二次开发，提供高度灵活的协同办公解决方案，低成本满足企业办公需求。低代码开发大幅降低技术门槛，加速系统构建与定制，赋能企业快速响应业务实现，确保信息安全与自主可控。

![o2oa](https://www.o2oa.net/v3/img/home/pic_zonghe@2x.png)



# 官方网站:

开源主页 : https://www.oschina.net/p/o2oa

官方网站 : http://www.o2oa.net

官方论坛 : https://www.o2oa.net/forum/


# 关于正式环境数据安全相关的建议:

O2OA自带的H2数据库是一个内嵌式的内存数据库，适合用于开发环境、功能演示环境，并不适合用作正式环境。

如果作为正式环境使用，建议您使用拥有更高性能，更加稳定的商用级别数据库。如Mysql8，Oracle12C，SQLServer 2012等。

另外，O2OA提供数据定期备份和恢复的能力，建议您开启正式环境的数据定期备份的功能，以确保数据库异常时可以进行数据恢复。



# 支持操作系统：

```shell
Windows 64Bit, Linux 64Bit[CentOS, RedHat, Ubuntu等], MacOS, AIX, Raspberrypi(树莓派), ARM_Linux, MIPS_Linux, UOS,麒麟等国产操作系统
```



# 支持数据库：

O2OA通过openjpa默认支持以下数据库:

| Database Name                     | Database Version            | JDBC Driver Name                  | JDBC Driver Version |
| --------------------------------- | --------------------------- | --------------------------------- | ------------------- |
| Apache Derby                      | 10.1.2.1                    | Apache Derby Embedded JDBC Driver | 10.1.2.1            |
| Borland Interbase                 | 7.1.0.202                   | Interclient                       | 4.5.1               |
| Borland JDataStore                | 6.0                         | Borland JDataStore                | 6.0                 |
| DB2                               | 8.1                         | IBM DB2 JDBC Universal Driver     | 1.0.581             |
| Empress                           | 8.62                        | Empress Category 2 JDBC Driver    | 8.62                |
| Firebird                          | 1.5                         | JayBird JCA/JDBC driver           | 1.0.1               |
| H2 Database Engine                | 1.0                         | H2                                | 1.0                 |
| Hypersonic Database Engine        | 1.8.0                       | Hypersonic                        | 1.8.0               |
| Informix Dynamic Server           | 9.30.UC10                   | Informix JDBC driver              | 2.21.JC2            |
| InterSystems Cache                | 5.0                         | Cache JDBC Driver                 | 5.0                 |
| Microsoft Access                  | 9.0 (a.k.a. "2000")         | DataDirect SequeLink              | 5.4.0038            |
| Microsoft SQL Server              | 9.00.1399 (SQL Server 2005) | SQLServer                         | 1.0.809.102         |
| Microsoft Visual FoxPro           | 7.0                         | DataDirect SequeLink              | 5.4.0038            |
| MySQL                             | 3.23.43-log                 | MySQL Driver                      | 3.0.14              |
| MySQL                             | 5.0.26                      | MySQL Driver                      | 3.0.14              |
| Oracle                            | 8.1,9.2,10.1                | Oracle JDBC driver                | 10.2.0.1.0          |
| Pointbase                         | 4.4                         | Pointbase JDBC driver             | 4.4 (4.4)           |
| PostgreSQL                        | 7.2.1                       | PostgreSQL Native Driver          | 8.1                 |
| PostgreSQL                        | 8.1.5                       | PostgreSQL Native Driver          | 8.1                 |
| Sybase Adaptive Server Enterprise | 12.5                        | jConnect                          | 5.5 (5.5)           |

主流数据库都包含在内.



# 国产数据库支持

对与国产数据库的支持是对不同的数据库编写不同的适配方言来实现的，也就是通过定制DBDictionary来实现对接。

目前我们已经成功适配的国产数据库如下：

| 数据库                 | 方言                                                   |
| ---------------------- | ------------------------------------------------------ |
| 达梦                   | com.x.base.core.openjpa.jdbc.sql.DMDictionary          |
| 南大通用8s             | com.x.base.core.openjpa.jdbc.sql.GBaseDictionary       |
| 南大通用华库(mysql5)   | com.x.base.core.openjpa.jdbc.sql.GBaseMySQL5Dictionary |
| 南大通用华库(mysql8)   | com.x.base.core.openjpa.jdbc.sql.GBaseMySQLDictionary  |
| 人大金仓V7             | com.x.base.core.openjpa.jdbc.sql.KingbaseDictionary    |
| 人大金仓V8             | com.x.base.core.openjpa.jdbc.sql.Kingbase8Dictionary   |
| 人大金仓V8R6           | com.x.base.core.openjpa.jdbc.sql.Kingbase8R6Dictionary |
| 神通数据库             | com.x.base.core.openjpa.jdbc.sql.OscarDictionary       |
| 各种PostgreSQL改造版本 | org.apache.openjpa.jdbc.sql.PostgresDictionary         |



# 平台使用手册：

https://www.o2oa.net/handbook.html



# 源码编译教程:

https://www.o2oa.net/cms/source/335.html



# 服务器部署教程：

Windows环境：https://www.o2oa.net/cms/serverdeployment/467.html

Linux环境：https://www.o2oa.net/cms/serverdeployment/468.html



# How to Start

## windows

1.下载o2server. yyyyMMddHHmmss_ windows.zip程序包。

2.解压下载后的压缩包到任意目录。

3.确认开通服务器的80、20020、20030端口。

4.打开o2server文件夹,选择start_ windows.bat双击打开。

5.启动服务,等待相关服务自动完成。

6.自动完成后打开浏览器访问http://127.0.0.1。

7.输入用户名xadmin密码o2oa@2022登陆系统。

## linux

1.下载o2server. yyyyMMddHHmmss_linux.zip程序包。

2.确认开通服务器的80、20020、20030端口。

3."unzip o2server. syyyMMddHHmmss linux.zip" 解压程序包。

4."cd o2server. yyyMMddHHmmss_ linux" 进入解压目录。

5."cd o2server"进入程序目录。

6."./start. linux.sh" 回车启动服务器控制台。

7.启动服务,等待相关服务自动完成。

8.自动完成后打开浏览器访问http://127.0.0.1。

9.输入用户名xadmin密码o2oa@2022登陆系统。



#### 若开发者学习研究O2OA，企业在O2OA应用开发平台上建设内部使用的办公系统，不闭源分发版本，不参与商业项目的使用行为不会构成侵权风险。

#### 如果需要进行转售，闭源分发或者在商业项目中作为项目的一部分使用，请主动联系兰德网络公司购买商用许可。

商用许可与支持服务：https://www.o2oa.net/service/gs.html



# 协议

[AGPL-3.0 开源协议。](./LICENSE)



# 关于

[![img](https://www.o2oa.net/v3/img/common/logo_all@2x.png)](./assets/O2OA-logo.jpg)

浙江兰德纵横网络技术股份有限 公司成立于1999年，是浙江省高新技术企业、浙江省信息产业厅认定的软件企业、浙江省信息产业重点企业、浙江省软件业五强企业， 总部拥有5300多平方米的办公场所，员工总数超过600人，在北京、上海、深圳、天津、南京、合肥、郑州、重庆、沈阳、长春、哈尔滨、呼和浩特、济南、南昌等省会城市设立办事处， 服务网络覆盖中国及东南亚部分国家和地区。公司已通过ISO9001：2008国际质量体系认证，并取得了国家系统集成三级资质认证和CMI三级认证的证书。

兰德网络是信息技术应用与服务提供商，为客户的计算机信息系统、企业级通信系统的建设提供解决方案和专业化服务。 业务包括：项目咨询、方案设计、应用系统开发、企业信息系统等服务。

兰德网络在电信运营企业中拥有良好的品牌形象，我们的通信软件系列产品已广泛应用于中国电信、中国移动、中国联通和中国网通等广大电信运营商的通信系统。 兰德网络已成为中国无线信息服务产业中优秀的商业机构及系统提供商代表，并将成为信息产业、互联网和数据通信服务产业中杰出的系统与服务提供商代表。

面向未来，兰德网络的愿景是“致力于更加美好的协作”。我们相信人类的协作一定会有更加美好的图景，等待我们去探索和描绘！



O2OA（翱途）开发平台是由 **浙江兰德纵横网路技术股份有限公司** 建立和维护的。O2OA（翱途）的名字和标志是属于 **浙江兰德纵横网路技术股份有限公司** 的注册商标。

我们 ❤️ 开源软件！看一下[我们的其他开源项目](https://github.com/o2oa)，瞅一眼[我们的博客](https://my.oschina.net/o2oa)。


