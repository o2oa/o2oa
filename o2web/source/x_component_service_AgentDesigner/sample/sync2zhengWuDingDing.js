/*
 * resources.getEntityManagerContainer() // 实体管理容器.
 * resources.getContext() //上下文根.
 * resources.getOrganization() //组织访问接口.
 * requestText //请求内容.
 * request //请求对象.
 */

var type = 'zhengwuDingdingMessage';
var resp = resources.getContext().applications().getQuery(com.x.base.core.project.x_message_assemble_communicate.class,
    'consume/list/' + type + '/count/100');
var messages = resp.getDataAsList(com.x.message.core.entity.Message.class);

for (var i in messages) {
    var message = messages[i];
    switch (message.getType()) {
        case 'task_create':
            if (com.x.base.core.project.config.Config.zhengwuDingding().getTaskToMessage()) {
                var person = resources.getOrganization().person().getObject(message.getPerson());
                var body = JSON.parse(message.getBody());
                if (body) {
                    if (person) {
                        if (person.getZhengwuDingdingId()) {
                            var txt = '您有新的待办需要处理:' + body['title'] + '.';
                            send('184707353', person.getZhengwuDingdingId(), txt);
                            print('发送政务钉钉待办消息,通知对象: ' + message.getPerson() + '(' + person.getZhengwuDingdingId() + '), 消息:' + txt + '.');
                        } else {
                            print('发送政务钉钉待办消息失败,通知对象' + message.getPerson() + ' 无法获取政务钉钉id.');
                        }
                    } else {
                        print('发送政务钉钉待办消息失败,通知对象' + message.getPerson() + ' 不存在.');
                    }
                } else {
                    print('发送政务钉钉待办消息失败,通知对象' + message.getPerson() + ' 无法获取消息对象.');
                }
            }
            break;
        case 'taskCompleted_create':
            if (com.x.base.core.project.config.Config.zhengwuDingding().getTaskCompletedToMessage()) {
                var person = resources.getOrganization().person().getObject(message.getPerson());
                var body = JSON.parse(message.getBody());
                if (body) {
                    if (person) {
                        if (person.getZhengwuDingdingId()) {
                            var txt = '您的待办已经处理完成:' + body['title'] + '.';
                            send('184707353', person.getZhengwuDingdingId(), txt);
                            print('发送政务钉钉已办消息,通知对象: ' + message.getPerson() + '(' + person.getZhengwuDingdingId() + '), 消息:' + txt + '.');
                        } else {
                            print('发送政务钉钉已办消息失败,通知对象' + message.getPerson() + ' 无法获取政务钉钉id.');
                        }
                    } else {
                        print('发送政务钉钉已办消息失败,通知对象' + message.getPerson() + ' 不存在.');
                    }
                } else {
                    print('发送政务钉钉待办消息失败,通知对象' + message.getPerson() + ' 无法获取消息对象.');
                }
            }
            break;
        case 'read_create':
            if (com.x.base.core.project.config.Config.zhengwuDingding().getReadToMessage()) {
                var person = resources.getOrganization().person().getObject(message.getPerson());
                var body = JSON.parse(message.getBody());
                if (body) {
                    if (person) {
                        if (person.getZhengwuDingdingId()) {
                            var txt = '您有新的待阅需要处理:' + body['title'] + '.';
                            send('184707353', person.getZhengwuDingdingId(), txt);
                            print('发送政务钉钉待阅消息,通知对象: ' + message.getPerson() + '(' + person.getZhengwuDingdingId() + '), 消息:' + txt + '.');
                        } else {
                            print('发送政务钉钉待阅消息失败,通知对象' + message.getPerson() + ' 无法获取政务钉钉id.');
                        }
                    } else {
                        print('发送政务钉钉待阅消息失败,通知对象' + message.getPerson() + ' 不存在.');
                    }
                } else {
                    print('发送政务钉钉待办消息失败,通知对象' + message.getPerson() + ' 无法获取消息对象.');
                }
            }
            break;
        case 'readCompleted_create':
            if (com.x.base.core.project.config.Config.zhengwuDingding().getReadCompletedToMessage()) {
                var person = resources.getOrganization().person().getObject(message.getPerson());
                var body = JSON.parse(message.getBody());
                if (body) {
                    if (person) {
                        if (person.getZhengwuDingdingId()) {
                            var txt = '您的待阅已经处理完成:' + body['title'] + '.';
                            send('184707353', person.getZhengwuDingdingId(), txt);
                            print('发送政务钉钉已阅消息,通知对象: ' + message.getPerson() + '(' + person.getZhengwuDingdingId() + '), 消息:' + txt + '.');
                        } else {
                            print('发送政务钉钉已阅消息失败,通知对象' + message.getPerson() + ' 无法获取政务钉钉id.');
                        }
                    } else {
                        print('发送政务钉钉已阅消息失败,通知对象' + message.getPerson() + ' 不存在.');
                    }
                } else {
                    print('发送政务钉钉待办消息失败,通知对象' + message.getPerson() + ' 无法获取消息对象.');
                }
            }
            break;
        default:
            system.print('未知的消息类型: ' + message.getType() + ' , title: ' + message.getTitle() + '.');
            break;
    }
    consume(message.getId(), type);
}

function send(agentId, user, message) {
    var body = '{"agentId":"agentId","touser":"' + 'touser' + '","toparty": "","msgtype":"text","context":"' + message + '"}';
    var address = com.x.base.core.project.config.Config.zhengwuDingding().getOapiAddress() + "/ent_message/send?access_token=" + com.x.base.core.project.config.Config.zhengwuDingding().appAccessToken();
    com.x.base.core.project.connection.HttpConnection.postAsString(address, null, body);
}

function consume(id, type) {
    resources.getContext().applications().getQuery(com.x.base.core.project.x_message_assemble_communicate.class,
        'consume/' + id + '/type/' + type);
}
