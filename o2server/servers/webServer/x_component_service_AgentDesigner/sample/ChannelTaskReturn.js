/*
 * resources.getEntityManagerContainer() // 实体管理容器.
 * resources.getContext() //上下文根.
 * resources.getOrganization() //组织访问接口.
 * requestText //请求内容.
 * request //请求对象.
 */



printLog("运行代理渠道任务完成情况反馈");

load("nashorn:mozilla_compat.js");

var File = Java.type('java.io.File');

var Root_Dir = "D:"+File.separator+'FTPFile'+ File.separator + "ChannelTask" + File.separator + "Result";

var Root_Dir_Record = "D:"+File.separator+'FTPFile'+ File.separator + "ChannelTaskRecorder" + File.separator ;

function createRecordFloder(){
    var Date = Java.type( "java.util.Date" );
    var now = new Date();
    var recordPath = Root_Dir_Record +
        new java.text.SimpleDateFormat("yyyy").format(now) + File.separator +
        new java.text.SimpleDateFormat("MM").format(now) + File.separator +
        new java.text.SimpleDateFormat("dd").format(now) + File.separator + "Return";
    var recordDir = new File(recordPath);
    if (!recordDir.exists()) {
        if(!recordDir.mkdirs()){
            print( "创建记录文件夹失败："+ recordPath )
            return null;
        }
    }
    return recordPath;
}

function getFileList(){

    var path = Root_Dir;

    var file = new File(path );

    var ArrayList = Java.type('java.util.ArrayList');
    var wjList = new ArrayList();

    if (file.exists()) {
        var fileList = file.listFiles();
        for (var i = 0; i < fileList.length; i++) {
            if (fileList[i].isFile()) {//判断是否为文件
                var name = fileList[i].getName().split(".");
                if( name[ name.length - 1 ].toUpperCase().equals("REQ") ){
                    wjList.add(fileList[i]);
                }
            }
        }
    }else{
        printLog("目录不存在:"+path);
    }
    return wjList;
}

function printLog( text ){
    print( text );
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
        print("grid="+grid);
        var array = grid.getAsJsonArray()
        if( array.size() > 0 ){
            var workData = array.get(0);
            print("workData="+workData);
            if( workData && workData != null ){
                var d = workData.get("data");
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

function setWorkData( branchWorkId, dateStr, numberCount, finishCount ){
    var flag = true;
    var workCompletedId = getWorkCompltedId(branchWorkId);
    if( workCompletedId === null ){
        print("根据branchWorkId'"+branchWorkId+"'不能获取workCompletedId");
        return false;
    }else{
        print("根据branchWorkId'"+branchWorkId+"'获取workCompletedId为"+workCompletedId);
    }
    try {
        var webservicesClient = resources.getWebservicesClient();
        var finishedLog = webservicesClient.jaxrsGet('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/finishedLog" );
        print(finishedLog);
        var logJson;
        if( finishedLog != null && finishedLog !== "" ){
            var log = finishedLog.toString();
            logJson = JSON.parse(log);
            logJson.push( { "dateStr" : dateStr, "finishCount" : finishCount } )
        }else{
            logJson = [ { "dateStr" : dateStr, "finishCount" : finishCount } ];
        }
        webservicesClient.jaxrsPut('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/finishedLog", JSON.stringify(logJson) );

        webservicesClient.jaxrsPut('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/finishCount", finishCount );
        print( "branchWorkId='" + branchWorkId + "' workCompletedId='" + workCompletedId +"'的工作保存完毕" );
    }catch ( e ) {
        flag = false;
        e.printStackTrace();
    } finally {
        return flag;
    }
}

function readFileByLines( file ) {
    var flag = true;
    var FileInputStream = Java.type('java.io.FileInputStream');
    var InputStreamReader = Java.type('java.io.InputStreamReader');
    var BufferedReader = Java.type('java.io.BufferedReader');
    //var FileReader = Java.type('java.io.FileReader');

    var inStream = null;
    try {
        inStream = new InputStreamReader(new FileInputStream(file), "GBK");
    } catch ( e) {
        e.printStackTrace();
        flag = false;
        return flag;
    }

    var reader = null;
    try {
        print("正在读取文件"+ file.getName() +"：");
        reader = new BufferedReader(inStream);
        var tempString = null;
        var line = 1;
        // 一次读入一行，直到读入null为文件结束
        while ((tempString = reader.readLine()) != null) {
            // 显示行号 
            print("line " + line + ": " + tempString);
            var array = tempString.split("|");
            //20180717|6276d0e1-d6ec-4efc-bb2c-3cf1b31cd491|da4d0edd-3b24-48fa-9126-00f373771010|9|8|水家湖营销服务中心李集片区@9c5468ee-0e2d-4ea3-a7f1-edbbf695c71c@U
            if( array.length > 5 ){
                var branchWorkId = array[2];
                print("branchWorkId="+branchWorkId);
                if( ! setWorkData( branchWorkId, array[0], array[3], array[4] ) ){
                    flag = false;
                }
            }
            line++;
        }
        reader.close();
    } catch ( e ) {
        flag = false;
        e.printStackTrace();
    } finally {
        if (reader != null) {
            try {
                reader.close();
            } catch (e1) {
                return flag;
            }
        }
        return flag;
    }
}

function init(){
    var fileList = getFileList();
    var recordPath = null;
    if( fileList.size() > 0 ){
        recordPath = createRecordFloder();
    }
    for(var i = 0; i<fileList.size(); i++){
        try{
            var file = fileList.get(i);
            var flag = readFileByLines( file );
            if( flag && recordPath !== null ){
                print( "正在修改文件目录："+file.getName() );
                var newFilePath = recordPath + File.separator + file.getName();
                var newFile = new File( newFilePath );
                if (newFile.exists()) { // 如果已存在,删除旧文件 
                    newFile.delete();
                }
                if (file.renameTo( newFile )) {
                    //println("File is moved successful!");  
                    print( "文件移动到了:"+newFilePath );
                } else {
                    print( file.getName() + " is failed to move!");
                }
            }
        }catch( e ){
            e.printStackTrace();
        }

    }
}

init();
