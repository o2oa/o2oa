/*
servercode	接入系统业务代码	String
time	消息发送时间，2011年12月12日 12:12:12格式举例:20111212121212	String
sign	签名，格式为MD5(servercode+serverpwd+time)
Serverpwd参数由【易和公司】,提供参数servicepwd由管理员提供；2、MD5算法是标准算法，可使用java内部实现；3、MD5后的值为32位并且小写。	String
srcnum	发送短号，由【易和公司】提供。	String
<10
desttype	目标号码类型
0:对方接收手机号码
1：对方登录名	String
dest	目标号名，当：
desttype=0,填写手机号码
当desttype=1,填写登录名	String
message	短信内容。	String
长度<500
messageid	短信流水号，如不需短信状态报告，该字段可以为空。	String
 */

var servercode = '';
var serverpwd = '';
var date = new Date();
var time = '' + date.getFullYear() + (date.getMonth() + 1) + date.getDate() + date.getHours() + date.getMinutes() + date.getSeconds();
var sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(servercode + serverpwd + time);
var desttype = 0;
var dest = '';
var message = '';
var messageid = '';
