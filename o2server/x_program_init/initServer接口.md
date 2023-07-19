
# initServer

## externalDataSources

get /jaxrs/externaldatasources/check
检查是否可以设置外部数据源

get /jaxrs/externaldatasources/list
列示可用的外部数据源配置样例.

post /jaxrs/externaldatasources/set
设置外部数据源.
{
"externalDataSources": [{
"enable": true,
"url": "jdbc:sqlserver://127.0.0.1:1433;DatabaseName\u003dX;selectMethod\u003dcursor;sendStringParametersAsUnicode\u003dfalse",
"username": "sa",
"password": "password",
"includes": [],
"excludes": [],
"logLevel": "ERROR",
"autoCommit": false,
"schema": "X"
}]
}


post /jaxrs/externaldatasources/validate
检查数据源是否可以连接.
{
"externalDataSources": [{
"enable": true,
"url": "jdbc:sqlserver://127.0.0.1:1433;DatabaseName\u003dX;selectMethod\u003dcursor;sendStringParametersAsUnicode\u003dfalse",
"username": "sa",
"password": "password",
"includes": [],
"excludes": [],
"logLevel": "ERROR",
"autoCommit": false,
"schema": "X"
}]
}

get /jaxrs/externaldatasources/set/cancel
取消设置外部数据源.

## h2

get /jaxrs/h2/check
检查h2服务服务器是否需要升级,jarVersion程序h2版本,localRepositoryDataH2Version本地文件版本,dataBaseFileExists数据库文件是否存在,needUpgrade是否需要升级

get /jaxrs/h2/upgrade
确认需要进行升级

get /jaxrs/h2/upgrade/cancel
取消升级

## restore

post /jaxrs/restore/upload
上传zip格式数据包,字段名file

get /jaxrs/restore/upload/cancel
取消数据导入

## secret

get /jaxrs/secret/check
检查密码是否为空

post /jaxrs/secret/set
设置初始管理员密码 {"secret":"o2oa@2022"}

get /jaxrs/secret/cancel
取消设置初始管理员密码

## server


get /jaxrs/server/execute
执行服务器任务,执行完成后将停止init服务器,随后正常启动.

get /jaxrs/server/execute/status
获取初始服务器任务执行状态 {"status":"waiting|running|success|failure", "messages":[""],"failureMessage":""}

get /jaxrs/server/stop
停止init服务器
