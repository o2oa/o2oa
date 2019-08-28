#O2OA Android


O2平台Android端应用。

[![Build Status](https://travis-ci.com/huqi1980/o2oa_client_web.svg?branch=master)](https://travis-ci.org/o2oa/o2oa)
[![AGPL](https://img.shields.io/badge/license-AGPL-blue.svg)](https://github.com/o2oa/o2oa)
[![code-size](https://img.shields.io/github/languages/code-size/o2oa/o2oa.svg)](https://github.com/o2oa/o2oa)
[![last-commit](https://img.shields.io/github/last-commit/o2oa/o2oa.svg)](https://github.com/o2oa/o2oa)
---

## 简介

O2平台Android客户端，最低支持Android版本4.4 [**Android KitKat**]。

## 导入编译

请使用最新版本的`Android Studio`进行导入编译，编译的Android SDK版本是 26 [**Android O**] 。

#### SDK环境安装

安装Android Studio完成后，打开设置里面的SDK Manager工具。

![](http://img.muliba.net/post/20190106112546.png)

选择Android 8.0 ，安装SDK。然后选择SDK Tools 选项卡，

![](http://img.muliba.net/post/20190106112529.png)

勾选右下角的Show Package Details，然后选择Android SDK Build-Tools 下面的27.0.3版本进行安装。

#### 应用配置信息修改

导入项目后会生成 `local.properties` ,需要在这个文件中添加如下内容：

```properties
# 打包证书相关信息
signingConfig.keyAlias=别名
signingConfig.keyPassword=密码
signingConfig.storeFilePath=证书所在路径
signingConfig.storePassword=证书密码

# 下面是一些第三方SDK的key
# DEBUG版本的key
JPUSH_APPKEY_DEBUG=极光推送AppKey
PGY_APP_ID_DEBUG=蒲公英AppId
BAIDU_APPID_DEBUG=Baidu地图AppId
BAIDU_SECRET_DEBUG=Baidu地图Secret
BAIDU_APPKEY_DEBUG=百度地图Appkey
# RELEASE版本的key
JPUSH_APPKEY_RELEASE=极光推送AppKey
PGY_APP_ID_RELEASE=蒲公英AppId
BAIDU_APPID_RELEASE=Baidu地图AppId
BAIDU_SECRET_RELEASE=Baidu地图Secret
BAIDU_APPKEY_RELEASE=百度地图Appkey
# 腾讯Bugly AppId
BUGLY_APPID=腾讯Bugly AppId

JM_IM_USER_PASSWORD=极光IM的用户默认密码
```



## 替换应用logo图标、应用名称

Logo图标分两块，第一块是App的桌面图标，第二块是App内部看到的一些O2OA的图标。

### App的桌面图标

这个图标需要编译打包的时候打包进去的，在 `./app/src/main/res`目录下的`mipmap`目录下：

|                                                              |                                                    |
| :----------------------------------------------------------: | :------------------------------------------------: |
| ![http://img.muliba.net/post/20190105171759.png](http://img.muliba.net/post/20190105171759.png) | ![](http://img.muliba.net/post/20190105171908.png) |

把四个目录中的`logo.png`和`logo_round.png`都替换了。

### App内部的一些O2OA的图标

App内看到的一些O2OA相关的logo图标，可以不编译打包进App，我们服务端可以进行动态配置。用管理员进入我们O2OA的服务端，找到系统设置->移动办公配置->样式配置，就可以修改图标了：

![](http://img.muliba.net/post/20190105172349.png)



### 应用名称

应用桌面显示的名称也是编译打包前要修改好的，在strings资源文件中修改就行了：

路径：`./app/src/main/res/values/strings.xml`

![](http://img.muliba.net/post/20190105173144.png)













## 官方网站:

官方网站 : [http://www.o2oa.net](http://www.o2oa.net)

oschina项目主页 : [https://www.oschina.net/p/o2oa](https://www.oschina.net/p/o2oa)

下载地址 : [http://www.o2oa.net](http://www.o2oa.net/download.html)
