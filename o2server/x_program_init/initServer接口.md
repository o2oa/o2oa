
# initServer

## externalDataSources

get /jaxrs/externaldatasources/check
检查是否可以设置外部数据源

get /jaxrs/externaldatasources/list
列示可用的外部数据源配置样例.

get /jaxrs/externaldatasources/set
设置外部数据源.

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

get /jaxrs/restore/cancel
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

get /jaxrs/server/stop
停止init服务器
