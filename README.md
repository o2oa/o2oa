## 获取源码

[O2OA](https://www.o2oa.net/)是全开源的系统，完整的源码可以免费从gitee或者codechina获取。仓库地址是：

gitee： [https://gitee.com/o2oa/O2OA.git](https://gitee.com/o2oa/O2OA.git)

codechina: [https://codechina.csdn.net/O2OA/o2oa.git](https://codechina.csdn.net/O2OA/o2oa.git)

### gitee仓库

我们打开gitee上的[O2OA](https://www.o2oa.net/)仓库，可以看到源码：（操作：浏览器打开项目仓库主页）

![](https://www.o2oa.net/cms/static/upload/image/20220729/1659071350188071.png)

（操作：展开分支）

![](https://www.o2oa.net/cms/static/upload/image/20220729/1659071350477781.png)

我们来介绍一下几个主要有的分支：

<table class="ne-table"><tbody><tr class="firstRow"><td><p class="ne-p"><span class="ne-text">develop</span></p></td><td><p class="ne-p"><span class="ne-text">这是主分支，也是我们的开发分支，它有最新的源码，每天都会有大量的提交，最新的功能也会在此分支上。但它没有经过详细的测试，可能会有比较多的bug，适合希望了解<a href="https://www.o2oa.net/">O2OA</a>最新更新和希望研究源码的用户，但不建议在生产环境使用它。</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">master</span></p></td><td><p class="ne-p"><span class="ne-text">master分支是最新正式发布的O2OA稳定版源码，经过详细测试，编译后可在生产环境使用。</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">develop_java8</span></p></td><td><p class="ne-p"><span class="ne-text">O2OA已经升级到java11版本，为了兼容希望坚持使用java8版本的用户，我们创建了此分支，它与develop分支的更新内容保持一致。如果希望O2OA功能保持最新，但又要使用java8的用户，可以使用此分支编译服务器。</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">master_java8</span></p></td><td><p class="ne-p"><span class="ne-text">最后发布的java8稳定版，版本6.2.2。不再更新了，需要Java8版本的可以使用</span></p><p class="ne-p"><span class="ne-text">develop_java8分支。</span></p></td></tr></tbody></table>

如果你需要历史版本的源码，可以切换到对应的tag。（打开标签）

![](https://www.o2oa.net/cms/static/upload/image/20220729/1659071350185989.png)

### 克隆源码

建议您先Fork源码到您自己的空间。

![](https://www.o2oa.net/cms/static/upload/image/20220729/1659071350157185.png)

![](https://www.o2oa.net/cms/static/upload/image/20220729/1659071350323272.png)

然后进入自己fork的仓库，复制克隆地址:

![](https://www.o2oa.net/cms/static/upload/image/20220729/1659071350177069.png)

首先我们确保我们自己的机器上安装了git工具，如果没有的话：

windows系统可以到下面的地址下载：[https://git-scm.com/download/win](https://git-scm.com/download/win)

centos使用命令：

yum install -y git

Ubuntu使用命令：

sudo apt-get install git

然后我们进入自己机器需要获取源码的目录，启动终端或使用git bash，输入以下命令clone源码：

git clone https://xxxxx

红色部分替换为你自己Fork的仓库地址。如：

git clone https://gitee.com/o2oa/O2OA.git

![](https://www.o2oa.net/cms/static/upload/image/20220729/1659071350201220.png)

clone完成后，就可以看到O2OA的源码目录了。

![](https://www.o2oa.net/cms/static/upload/image/20220729/1659071351406718.png)

## 编译源码

获取了源码后，我们就可以将源码编译为可运行的O2OA服务器了。

### 准备编译环境

在编译源码之前，我们需要准备编译环境，O2OA编译需要以下环境：

<table class="ne-table"><tbody><tr class="firstRow"><td><p class="ne-p"><span class="ne-text">Node.js</span></p></td><td><p class="ne-p"><span class="ne-text">编译脚本运行环境，14.0.0以上版本</span></p><p class="ne-p"><span class="ne-text"><span>（建议使用<span>Node.js&nbsp;v16 LTS, v17及</span>以上版本的可能会报错）</span></span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">jdk</span></p></td><td><p class="ne-p"><span class="ne-text">根据您要编译的分支，决定使用JDK8 或者是<span> JDK11（推荐）</span></span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">maven</span></p></td><td><p class="ne-p"><span class="ne-text">3.6及以上版本</span></p></td></tr></tbody></table>

上述环境安装，在此就不赘述了。

### 检查当前分支是否是master分支

环境准备好后，我们打开终端或windows的命令行提示符，进入O2OA源码目录。默认情况先，现在是主分支，就是master分支，通过以下git命令检查分支

git status

![image.png](https://www.o2oa.net/cms/static/upload/image/20230816/1692170496624499.png "1692170496624499.png")

### 编译

切换到您需要的分支后，需要先使用npm安装编译脚本所需要的依赖包，使用以下命令安装：（安装过程可能需要几分钟）

npm install

![](https://www.o2oa.net/cms/static/upload/image/20220729/1659071351108873.png)

如果没有安装过gulp和jsdoc，需要先执行以下命令安装：

npm install -g gulp-cli
npm install -g jsdoc

然后就可以使用以下命令编译了：

npm run build\_ci

![](https://www.o2oa.net/cms/static/upload/image/20220729/1659071351185604.png)

整个编译过程大概需要30分钟。编译完成后，会在目录下生成target/o2server目录，这就是可运行的服务器目录。可以将它拷贝到服务器，运行相应start\_xxx命令就可以运行O2OA了。这个等会编译完成后，我们再看。

### 编译错误解决

如果编译的过程中出现下图错误，可通过修改maven源来解决

![b4320ee7d6ad5795ffcb363b88af7332.JPG](https://www.o2oa.net/cms/static/upload/image/20230816/1692174034167244.jpg "1692174034167244.jpg")

### 修改maven

### ![38e9e73236402200f49d59b7ca23bb64.JPG](https://www.o2oa.net/cms/static/upload/image/20230816/1692174100150826.jpg "1692174100150826.jpg")

<mirror>
      <id>alimaven</id>
      <name>aliyun maven</name>
      <url>https://maven.aliyun.com/repository/central</url>
      <mirrorOf>central</mirrorOf>
    </mirror>

优化编译

编译特定操作系统版本的O2OA

如果我们要编译指定操作系统版本的O2OA，可以使用以下命令：

**方法一：没有下载JVM和依赖包commons**

如果您没有手工下载JVM和依赖包commons，也没有关系，我们可以使用 “build\_ci:xxx” 命令：

推荐是使用此方式编译，这样可以保证编译程序找到合适的commons版本

#编译windows版本
npm run build\_ci:win

#编译linux x86版本
npm run build\_ci:linux
                
#编译aix版本
npm run build\_ci:aix

#编译linux arm版本
npm run build\_ci:arm

#编译windows版本
npm run build\_ci:macos

#编译linux mips版本
npm run build\_ci:mips

#编译树莓派版本
npm run build\_ci:rpi

编译脚本功能清单

编译脚本的功能清单如下：

<table class="ne-table"><tbody><tr class="firstRow"><td><p class="ne-p"><strong>命令</strong></p></td><td><p class="ne-p"><strong>运行</strong></p></td><td><p class="ne-p"><strong>说明</strong></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">build_ci</span></p></td><td><p class="ne-p"><span class="ne-text">npm run build_ci</span></p></td><td><p class="ne-p"><span class="ne-text">编译源码，自动下载JVM和依赖包，并构建可以运行在所有支持的操作系统的服务器。</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">build_ci:xxx</span></p></td><td><p class="ne-p"><span class="ne-text">npm run build_ci:win</span></p><p class="ne-p"><span class="ne-text">npm run build_ci:linux</span></p><p class="ne-p"><span class="ne-text">npm run build_ci:aix</span></p><p class="ne-p"><span class="ne-text">npm run build_ci:arm</span></p><p class="ne-p"><span class="ne-text">npm run build_ci:macos</span></p><p class="ne-p"><span class="ne-text">npm run build_ci:mips</span></p><p class="ne-p"><span class="ne-text">npm run build_ci:rpi</span></p></td><td><p class="ne-p"><span class="ne-text">编译源码，自动下载指定操作系统环境的JVM和依赖包，并构建可以运行在指定操作系统的服务器。</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">build</span></p></td><td><p class="ne-p"><span class="ne-text">npm run build</span></p></td><td><p class="ne-p"><span class="ne-text">预先下载JVM和依赖包，并放到o2server目录，</span></p><p class="ne-p"><span class="ne-text">编译源码，构建可以运行在所有支持的操作系统的服务器。</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">build:xxx</span></p></td><td><p class="ne-p"><span class="ne-text">npm run build:win</span></p><p class="ne-p"><span class="ne-text">npm run build:linux</span></p><p class="ne-p"><span class="ne-text">npm run build:aix</span></p><p class="ne-p"><span class="ne-text">npm run build:arm</span></p><p class="ne-p"><span class="ne-text">npm run build:macos</span></p><p class="ne-p"><span class="ne-text">npm run build:mips</span></p><p class="ne-p"><span class="ne-text">npm run build:rpi</span></p></td><td><p class="ne-p"><span class="ne-text">预先下载JVM和依赖包，并放到o2server目录，</span></p><p class="ne-p"><span class="ne-text">编译源码，构建可以运行在指定操作系统的服务器。</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">build_server</span></p></td><td><p class="ne-p"><span class="ne-text">npm run build_server</span></p></td><td><p class="ne-p"><span class="ne-text">单独编译O2OA服务端</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">build_web</span></p></td><td><p class="ne-p"><span class="ne-text">npm run build_web</span></p></td><td><p class="ne-p"><span class="ne-text">单独编译O2OA Web端</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">build_api</span></p></td><td><p class="ne-p"><span class="ne-text">npm run build_api</span></p></td><td><p class="ne-p"><span class="ne-text">生成O2OA脚本API文档</span></p></td></tr></tbody></table>

## 源码文件结构解析

趁服务器编译的过程，我们简单介绍一下源码文件结构。（打开gitee项目主页）

更目录下主要文件和目录：

<table class="ne-table"><tbody><tr class="firstRow"><td><p class="ne-p"><span class="ne-text">o2android/</span></p></td><td><p class="ne-p"><span class="ne-text">android端App源码，已经迁移到</span><span class="ne-text"> </span><a href="https://gitee.com/o2oa/o2oa-android" target="_blank" class="ne-link"><span class="ne-text">https://gitee.com/o2oa/o2oa-android</span></a><span class="ne-text"> 仓库中</span></p><p class="ne-p"><span class="ne-text">将在后续App编译课程中详细介绍</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">o2ios/</span></p></td><td><p class="ne-p"><span class="ne-text">ios端App源码，已经迁移到</span><span class="ne-text"> </span><a href="https://gitee.com/o2oa/o2oa-ios" target="_blank" class="ne-link"><span class="ne-text">https://gitee.com/o2oa/o2oa-ios</span></a><span class="ne-text"> 仓库中</span></p><p class="ne-p"><span class="ne-text">将在后续App编译课程中详细介绍</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">o2server/</span></p></td><td><p class="ne-p"><span class="ne-text">服务端源码目录</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">o2web/</span></p></td><td><p class="ne-p"><span class="ne-text">Web端源码目录</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">Jenkins*</span></p></td><td><p class="ne-p"><span class="ne-text">Jenkins流水线文件，我们在持续发布流程中的流水线文件，</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">build.xml</span></p></td><td><p class="ne-p"><span class="ne-text">maven打包文件</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">gulpfile.js</span></p></td><td><p class="ne-p"><span class="ne-text">打包脚本文件</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">...</span></p></td><td><p class="ne-p"><span class="ne-text">略</span></p></td></tr></tbody></table>

### o2web目录结构说明

前端的源码都放置在o2web目录，其中代码部分都在o2web/source/目录下，其他一些是相关的配置和打包脚本文件，所以我们简单介绍一下o2web/source/目录的内容

<table class="ne-table"><tbody><tr class="firstRow"><td><p class="ne-p"><span class="ne-text">o2_core/</span></p></td><td><p class="ne-p"><span class="ne-text">O2OA前端的平台的核心代码，如一些用户认证相关，平台框架相关，服务请求相关的脚本代码。</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">o2_lib/</span></p></td><td><p class="ne-p"><span class="ne-text">O2OA前端会引用到，或者可能会引用到的第三方框架或功能组件，如vue、mootools、ckeditor、echarts等</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">x_component_xxxxx</span></p></td><td><p class="ne-p"><span class="ne-text">每个x_component开头的目录就是一个O2OA平台的一个应用，如：</span></p><p class="ne-p"><span class="ne-text">x_component_Org-是组织管理应用；</span></p><p class="ne-p"><span class="ne-text">x_component_portal_Portal - 是展现门户的应用；</span></p><p class="ne-p"><span class="ne-text">x_component_process_TaskCenter - 是待办中心应用；</span></p><p class="ne-p"><span class="ne-text">x_component_Meeting - 是会议管理应用；</span></p><p class="ne-p"><span class="ne-text">……</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">o2_desktop</span></p></td><td><p class="ne-p"><span class="ne-text">主要放置各种html文件，以及载入前端应用的loader脚本代码</span></p></td></tr></tbody></table>

### o2server目录结构说明

<table class="ne-table"><tbody><tr class="firstRow"><td><p class="ne-p"><span class="ne-text">configSample</span><span class="ne-text">/</span></p></td><td><p class="ne-p"><span class="ne-text">存放</span><span class="ne-text">config</span><span class="ne-text">的配置样例</span><span class="ne-text">,</span><span class="ne-text">在编译过程中会根据注解自动生成默认配置文件</span><span class="ne-text">.</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">localSample</span><span class="ne-text">/</span></p></td><td><p class="ne-p"><span class="ne-text">存放</span><span class="ne-text">local</span><span class="ne-text">目录的配置文件</span><span class="ne-text">,</span><span class="ne-text">目前只有节点标识文件</span><span class="ne-text">node.cfg</span><span class="ne-text">.</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">x_console</span><span class="ne-text">/</span></p></td><td><p class="ne-p"><span class="ne-text">目录存放的是启动的主程序</span><span class="ne-text">.</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">x_*</span><span class="ne-text">_</span><span class="ne-text">core_entity</span><span class="ne-text">/</span></p></td><td><p class="ne-p"><span class="ne-text">实体类项目</span><span class="ne-text">.</span><span class="ne-text">编译后打包成</span><span class="ne-text">jar.</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">x_*</span><span class="ne-text">_</span><span class="ne-text">assemble_control</span></p></td><td><p class="ne-p"><span class="ne-text">业务装配模块</span><span class="ne-text">,</span><span class="ne-text">最终向前端提供业务服务</span><span class="ne-text">.</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">x_</span><span class="ne-text">*_</span><span class="ne-text">service_*/</span></p></td><td><p class="ne-p"><span class="ne-text">后台服务模块</span><span class="ne-text">,</span><span class="ne-text">仅对其他模块提供服务</span><span class="ne-text">,</span><span class="ne-text">不向前端开放</span><span class="ne-text">.</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">console_*.sh</span></p></td><td><p class="ne-p"><span class="ne-text">控制台进入脚本</span><span class="ne-text">.</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">restart_*.sh</span></p></td><td><p class="ne-p"><span class="ne-text">服务器重启脚本</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">service_windows.bat</span></p></td><td><p class="ne-p"><span class="ne-text">将服务器作为</span><span class="ne-text">windows</span><span class="ne-text">服务的生成脚本</span><span class="ne-text">.</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">start_*.sh</span></p></td><td><p class="ne-p"><span class="ne-text"></span><span class="ne-text">服务器启动脚本</span><span class="ne-text">.</span></p></td></tr><tr><td><p class="ne-p"><span class="ne-text">stop_*.sh</span></p></td><td><p class="ne-p"><span class="ne-text">服务器停止脚本</span><span class="ne-text">.</span></p></td></tr></tbody></table>

此时服务器应该编译打包完成，打开源码的target/o2server目录展示，通过命令可以启动服务器：

windows系统运行：start\_windows.bat

x86的linux系统运行： start\_linux.sh

arm的linux系统运行： start\_arm.sh

等等。