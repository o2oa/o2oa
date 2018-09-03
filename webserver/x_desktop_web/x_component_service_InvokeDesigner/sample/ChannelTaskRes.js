/*
 * resources.getEntityManagerContainer() // 实体管理容器.
 * resources.getContext() //上下文根.
 * resources.getOrganization() //组织访问接口.
 * requestText //请求内容.
 * request //请求对象.
 */

print("远程调用：渠道任务接收拒绝实时接口");

var File = Java.type('java.io.File');
var Root_Dir_Record = "D:"+File.separator+'FTPFile'+ File.separator + "ChannelTaskRecorder" + File.separator ;
var recordFile = null;
var BranchWorkId = null;
var pw = null;

function createRecordFile(){
    if( BranchWorkId == null )return;
    var Date = Java.type( "java.util.Date" );
    var now = new Date();
    var recordPath = Root_Dir_Record +
        new java.text.SimpleDateFormat("yyyy").format(now) + File.separator +
        new java.text.SimpleDateFormat("MM").format(now) + File.separator +
        new java.text.SimpleDateFormat("dd").format(now) + File.separator + "Res";
    var recordDir = new File(recordPath);
    if (!recordDir.exists()) {
        if(!recordDir.mkdirs()){
            print( "创建记录文件夹失败："+ recordPath );
            recordDir = null;
        }
    }
    if( recordDir !== null ){
        var recordFilePath = recordPath + File.separator + BranchWorkId + ".txt";
        recordFile = new File(recordFilePath);
        if (recordFile.exists()) { // 如果已存在,删除旧文件
            recordFile.delete();
        }
        if(!recordFile.createNewFile()){
            print("不能记录文件:"+recordFilePath);
            recordFile = null;
        }else{
            print("创建记录文件:"+recordFilePath);
        }
    }
}

function printRecorder( text, notPrint ){
    if( !notPrint )print(text);
    if( BranchWorkId == null )return;
    if( recordFile === null )createRecordFile();
    if( recordFile === null )return;
    if( pw === null )pw = new java.io.PrintWriter(recordFile, "GBK");
    pw.print( text );
    pw.write(0x0d);
    pw.write(0x0a);
}

function getPureText( str ){
    if( str === null )return str;
    if( str.substr( 0 , 1 ) === "\"" ){
        str = str.substr( 1, str.length - 1 );
    }
    if( str.substr( str.length - 1 , 1 ) === "\"" ){
        str = str.substr( 0, str.length - 1 );
    }
    return str;
}

function getWorkCompltedId( branchWorkId ){
    var filterList = {"filterList": [{
        "logic":"and",
        "path": "branchWorkId",
        "title": "branchWorkId",
        "comparison":"equals",
        "comparisonTitle":"等于",
        "value": branchWorkId,
        "formatType":"textValue"
    }]};
    var json = resources.getWebservicesClient().jaxrsPut('x_query_assemble_surface', "view/flag/workCompletedByBranch/query/channelTask/execute", JSON.stringify( filterList ) );
    //var json = resources.getWebservicesClient().jaxrsGet('x_processplatform_assemble_surface', "data/workcompleted/e8db5cba-2d35-4b91-86a2-6c482ed7da55" );

    var obj = json.getAsJsonObject();
    var grid = obj.get("grid");
    if( grid ){
        printRecorder("grid="+grid);
        var workData = grid.getAsJsonArray();
        if( workData.size() > 0 ){
            printRecorder("workData="+workData);
            var data = workData.get(0);
            if( data && data != null ){
                var d = data.get("data");
                if( d && d!=null ){
                    var workCompletedId = d.get("workCompletedId");
                    if( workCompletedId && workCompletedId != null ){
                        workCompletedId = getPureText(workCompletedId.toString());
                        return workCompletedId;
                    }
                }
            }
        }
    }
    return null;
}

function setWorkData( branchWorkId, dateStr, Task_state ){
    var workCompletedId = getWorkCompltedId(branchWorkId);
    var text = null;
    if( workCompletedId === null ){
        text = "根据Sub_task_id'"+branchWorkId+"'不能获取文件";
        printRecorder(text);
        return text;
    }else{
        printRecorder("根据branchWorkId'"+branchWorkId+"'获取workCompletedId为"+workCompletedId);
        printRecorder( "文件URL：/x_desktop/work.html?workcompletedid="+workCompletedId );
    }
    var webservicesClient = resources.getWebservicesClient();

    try{
        webservicesClient.jaxrsPut('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/interfaceResTime", dateStr );
        webservicesClient.jaxrsPut('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/interfaceResStat", Task_state );
        printRecorder( "branchWorkId='" + branchWorkId + "' workCompletedId='" + workCompletedId +"'的工作保存完毕" );
        return null;
    }catch(e){
        return e.getMessage();
    }

}

function init(){
    //var re = {
    //    "head": {
    //        "sign" : {
    //            "service_name" : "ChannelTaskRes",
    //            "Trans_ido" : "20170724013901"
    //        }
    //    },
    //    "body" : {
    //        "msg" : {
    //            "Task_id" : "be4b9546-158b-4c3f-85a5-49d266c7df23",
    //            "Sub_task_id" : "be4b9546-158b-4c3f-85a5-49d266c7df23",
    //            "Task_ state" : "1",
    //            "Time" : "20170724013901"
    //        }
    //    }
    //}
    var text="";
    var Trans_ido="";
    var Response_code;
    try{
        print( "requestText="+requestText );

        var requestJson = JSON.parse(requestText);

        print( "type of requestJson = " + typeof( requestJson ));

        if( typeof(requestJson) === "string" ){
            requestJson = JSON.parse(requestJson);
        }

        Trans_ido = requestJson.head.sign.Trans_ido;
        var Sub_task_id = requestJson.body.msg.Sub_task_id;
        BranchWorkId = Sub_task_id;
        var Task_state = requestJson.body.msg.Task_state;
        var Time = requestJson.body.msg.Time;

        if( BranchWorkId && BranchWorkId != null && BranchWorkId != "" ){
            printRecorder( "requestText="+requestText, true );
        }

        text = setWorkData( Sub_task_id, Time, Task_state );
        if( text == null ){
            Response_code = "0000";
        }else{
            Response_code = "8888";
        }
    }catch(e){
        Response_code = "8888";
        e.printStackTrace();
        if( pw == null ){
            print(e.getMessage())
        }else{
            printRecorder(e.getMessage())
        }
        text = e.getMessage();
    }finally{
        var Response_desc = "成功";
        if( Response_code == "8888" ){
            Response_desc = text || "反馈错误"
        }
        var responseText = {
            "head": {
                "sign" : {
                    "service_name" : "ChannelTaskRes",
                    "Trans_ido" : Trans_ido
                }
            },
            "body" : {
                "msg" : {
                    "Response_code" : Response_code,
                    "Response_desc" : Response_desc
                }
            }
        };
        if( pw == null ){
            print("responseText="+JSON.stringify(responseText))
        }else{
            printRecorder("responseText="+JSON.stringify(responseText));
            pw.close();
        }
        return responseText;
    }
}

init();
