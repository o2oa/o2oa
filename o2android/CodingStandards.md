##规范性、单一性、简洁性 3个基础原则

###命名：
    ID: 控件_模块_功能
        控件：
            TextView tv
            EditText edit
            ListView list
            RecyclerView rv
            GridView grid
            LinearLayout linear
            RelativeLayout relative
            ScrollView scroll
            等。。
        范围：当前使用的activity、fragment或者业务模块的名称
        功能：当前id所在的控件的功能
        例子：edit_login_password ，表示登录界面的密码EditText输入框


    文件：
        java：驼峰命名 模块+功能+类型    如： BBSPublishActivity.java
        xml：类型_模块_功能   如：fragment_bbs_publish.xml



###存储：
    所有文件都存储在Constants.BASE_FILE_PATH （ZONE_XBPM），里面按功能新建目录存储各种文件

####测试
这个是测试的文字
另外一段呢。。。。

这是才另外一段


###项目结构：
    项目采用MVP模式

* api——服务端相关的代码
* app——将界面层按照模块分配包
   * 模块
     * view
     * presenter
   * ...
* config——Application、Activity、Fragment、Presenter等的顶级父类,常量表等
* model——数据层，按照模块分配包
    * vo——前端列表使用的对象
    * persistence——持久化存储对象
    * bo
        * api
        * ...
* utils——工具集合
* widgets——各个可复用View集合


###主题配置







