#O2OA IOS


O2平台IOS端应用。

[![Build Status](https://travis-ci.com/huqi1980/o2oa_client_web.svg?branch=master)](https://travis-ci.org/o2oa/o2oa)
[![AGPL](https://img.shields.io/badge/license-AGPL-blue.svg)](https://github.com/o2oa/o2oa)
[![code-size](https://img.shields.io/github/languages/code-size/o2oa/o2oa.svg)](https://github.com/o2oa/o2oa)
[![last-commit](https://img.shields.io/github/last-commit/o2oa/o2oa.svg)](https://github.com/o2oa/o2oa)
---

## 简介

O2平台IOS客户端，最低支持IOS版本10 。

## 导入编译

当前项目使用的Xcode10，Swift4.2 。Xcode10需要开启`Legacy Build System` , 在Xcode菜单 File -> Workspace Settings. -> Per-User Workspace Settings: -> Build System  选择`Legacy Build System` 。

项目使用了Cocoapods，所以下载项目代码后在项目根目录先执行：

```shell
pod install
```

然后打开 `O2Platform.xcworkspace`



## 替换应用logo图标、应用名称

Logo图标分两块，第一块是App的桌面图标，第二块是App内部看到的一些O2OA的图标。

### App的桌面图标

这个图标需要编译打包的时候打包进去的，在Assets.xcassets文件中AppIcon ：

![](http://img.muliba.net/post/20190105185049.png)



### App内部的一些O2OA的图标

App内看到的一些O2OA相关的logo图标，可以不编译打包进App，我们服务端可以进行动态配置。用管理员进入我们O2OA的服务端，找到系统设置->移动办公配置->样式配置，就可以修改图标了：

![](http://img.muliba.net/post/20190105172349.png)



### 应用名称

应用桌面显示的名称也是编译打包前要修改好的，Info.plist文件中修改DisplayName 就行了。







## 官方网站:

官方网站 : [http://www.o2oa.net](http://www.o2oa.net)

oschina项目主页 : [https://www.oschina.net/p/o2oa](https://www.oschina.net/p/o2oa)

下载地址 : [http://www.o2oa.net](http://www.o2oa.net/download.html)