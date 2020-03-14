# O2OA Web

O2平台Web端应用。

[![Build Status](https://travis-ci.com/huqi1980/o2oa_client_web.svg?branch=master)](https://travis-ci.org/o2oa/o2oa)
[![AGPL](https://img.shields.io/badge/license-AGPL-blue.svg)](https://github.com/o2oa/o2oa)
[![code-size](https://img.shields.io/github/languages/code-size/o2oa/o2oa.svg)](https://github.com/o2oa/o2oa)
[![last-commit](https://img.shields.io/github/last-commit/o2oa/o2oa.svg)](https://github.com/o2oa/o2oa)
---

## 简介

O2平台Web端应用，它将会随o2server一起编译。您也可以单独编译，并将它部署到任意的WEB服务器。

## 编译

先安装npm环境

    $ npm install
    $ npm i -g gulp-cli
   

使用一下命令编译：

    $ gulp

使用下面的参数可以将编译后直接通过FTP部署到web服务器：

	$ gulp --upload ftp --host ftp.server.com --user ftpuser --pass password --port 21 --remotePath /


> --upload : 可选值`local`、`ftp`、`sftp`
>
> --host : ftp或sftp服务器
>
> --user : 用户名，默认`anonymous`
>
> --pass : 密码，ftp或sftp密码，默认`@anonymous`
>
> --port : 端口，ftp默认21；sftp默认22
>
> --remotePath: 远程部署路径，默认“/”

## 配置

如果您将o2web部署到了其他WEB服务器，您需要手工修改下面的config.jon文件。

路径：/x_desktop/res/config/config.json

	{
	  "center": [		//可以配置多个中心服务器地址，系统会自动找到一个可用的服务器
	    {
	      "port": "20030",	//中心服务器端口
	      "host": ""		//中心服务器host
	    },
	    {
	      "port": "20030",		//中心服务器端口
	      "host": "127.0.0.1"	//中心服务器host
	    }
	  ],
	  "footer": "开发系统",		//系统页脚
	  "title": "o2oa开发平台",	//系统名称
	  "app_protocol": "auto",	//http，https 或 auto
	  "loginPage": {			//将一个portal页面作为登录页
	    "enable": false,
	    "portal": "",
	    "page": ""
	  }
	}

## 官方网站\:

官方网站 : [http://www.o2oa.net](http://www.o2oa.net)

oschina项目主页 : [https://www.oschina.net/p/o2oa](https://www.oschina.net/p/o2oa)

下载地址 : [http://www.o2oa.net](http://www.o2oa.net/download.html)


