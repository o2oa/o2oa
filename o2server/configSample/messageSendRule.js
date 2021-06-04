/**
 * 统一消息推送执行脚本，使用在messages.json配置文件
 * 方法返回boolean类型，true表示满足发送条件且接受对body消息内容的修改，false表示不发送
 * 变量message表示消息体，原始消息的格式{type:'',person,:'',title,'',body:null}，是一个obj对象
 * 变量body表示消息内容，每个消息类型的消息体可能不同，是一个Gson的JsonObject对象
 * 以下excute方法表示拟稿状态的待办不发送消息;excute1方法表示变更或者添加body对象中的modifyFlag参数;excute2方法示例获取message对象的属性值
 */
function excute() {
    if(body.has("first") && body.has("workCreateType")){
        if (body.get("first").getAsBoolean() && "surface".equals(body.get("workCreateType").getAsString())){
            return false;
        }
    }
    return true;
}
function excute1() {
    body.addProperty("modifyFlag","1");
    return true;
}
function excute2() {
    print(message.getType());
    print(message.getTitle());
    return true;
}
