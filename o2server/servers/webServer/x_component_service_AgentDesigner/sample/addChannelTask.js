/*
 * resources.getEntityManagerContainer() // 实体管理容器.
 * resources.getContext() //上下文根.
 * resources.getOrganization() //组织访问接口.
 * requestText //请求内容.
 * request //请求对象.
 */


print("运行渠道任务工单任务同步实时接口");

var File = Java.type('java.io.File');
var Root_Dir_Record = "D:"+File.separator+'FTPFile'+ File.separator + "ChannelTaskRecorder" + File.separator ;
var recordFile = null;
var workcompletedid = "";
var pw = null;

function createRecordFile(){
    var Date = Java.type( "java.util.Date" );
    var now = new Date();
    var recordPath = Root_Dir_Record +
        new java.text.SimpleDateFormat("yyyy").format(now) + File.separator +
        new java.text.SimpleDateFormat("MM").format(now) + File.separator +
        new java.text.SimpleDateFormat("dd").format(now) + File.separator + "Add";
    var recordDir = new File(recordPath);
    if (!recordDir.exists()) {
        if(!recordDir.mkdirs()){
            print( "创建记录文件夹失败："+ recordPath );
            recordDir = null;
        }
    }
    if( recordDir !== null ){
        var recordFilePath = recordPath + File.separator + workcompletedid + ".txt";
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

function printRecorder( text ){
    print(text);
    if( recordFile == null )createRecordFile();
    if( recordFile == null )return;
    if( pw == null )pw = new java.io.PrintWriter(recordFile, "GBK");
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

function getWorkCompleteIds(){
    var ArrayList = Java.type('java.util.ArrayList');
    var idList = new ArrayList();

    var filterList = {"filterList": [{
        "logic":"and",
        "path": "interfaceStatus",
        "title": "接口状态",
        "comparison":"equals",
        "comparisonTitle":"等于",
        "value": "detailDone",
        "formatType":"textValue"
    }]};
    var json = resources.getWebservicesClient().jaxrsPut('x_query_assemble_surface', "view/flag/workCompletedByBranch/query/channelTask/execute", JSON.stringify( filterList ) );

    var obj = json.getAsJsonObject();
    var grid = obj.get("grid");
    if( grid ){
        var workData = grid.getAsJsonArray();

        var iter = workData.iterator();
        while(iter.hasNext()) {
            //如果存在，则调用next实现迭代
            var data = iter.next();  //把Object型强转成int型
            if( data && data != null ){
                var d = data.get("data");
                if( d && d!=null ){
                    var workCompletedId = d.get("workCompletedId");
                    if( workCompletedId && workCompletedId != null ){
                        workCompletedId = getPureText(workCompletedId.toString());
                        idList.add( workCompletedId )
                    }
                }
            }
        }
    }
    return idList;
}

//function getWorkCompleteId(){
//    var filterList = {"filterList": [{
//        "logic":"and",
//        "path": "interfaceStatus",
//        "title": "接口状态",
//        "comparison":"equals",
//        "comparisonTitle":"等于",
//        "value": "detailDone",
//        "formatType":"textValue"
//    }]};
//    var json = resources.getWebservicesClient().jaxrsPut('x_query_assemble_surface', "view/flag/workCompletedByBranch/query/channelTask/execute", JSON.stringify( filterList ) );
//
//    var obj = json.getAsJsonObject();
//    var grid = obj.get("grid");
//    if( grid ){
//        var workData = grid.getAsJsonArray();
//        if( workData.size() > 0 ){
//            var data = workData.get(0);
//            if( data && data != null ){
//                var d = data.get("data");
//                if( d && d!=null ){
//                    var workCompletedId = d.get("workCompletedId");
//                    if( workCompletedId && workCompletedId != null ){
//                        workCompletedId = getPureText(workCompletedId.toString());
//                        return workCompletedId;
//                    }
//                }
//            }
//        }
//    }
//    return null;
//}

function getIdo(){
    var Random = Java.type("java.util.Random");
    var random = new Random();
    var result="";
    for (var i=0; i< 14; i++){
        result += random.nextInt(10);
    }
    return result;
}


function getXml( data ){
    var Date = Java.type( "java.util.Date" );
    var now = new Date();
    var nowStr = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(now);

    var xml = '<?xml version=\"1.0\" encoding=\"UTF-8\"?>';
    xml += '<xml>';
    xml += '<head>';
    xml +=  '<sign>';
    xml +=      '<service_name>addChannelTask</service_name>';
    xml +=      '<Trans_ido>'+ getIdo() +'</Trans_ido>';
    xml +=  '</sign>';
    xml += '</head>';
    xml += '<body>';
    xml +=  '<msg>';
    xml +=      '<Task_id>'+ ( data.provinceWorkId || data.cityWorkId || data.countyWorkId || data.branchWorkId ) +'</Task_id>';
    xml +=      '<Sub_task_id>'+data.branchWorkId+'</Sub_task_id>';
    xml +=      '<Task_name>'+data.subject+'</Task_name>';
    xml +=      '<Region_code>'+ data.cityBSSId +'</Region_code>';
    xml +=      '<Group_id>' + data.branchBSSId + '</Group_id>';
    xml +=      '<Task_type>'+data.taskType+'</Task_type>';
    xml +=      '<Task_target></Task_target>';
    xml +=      '<Task_dev_num>'+ data.numberCount +'</Task_dev_num>';
    xml +=      '<prize>'+ (data.reward_branch || data.reward_county || data.reward_city || data.reward)  +'</prize>';
    xml +=      '<File_Name>'+ data.File_Name +'</File_Name>';
    xml +=      '<Eff_date>'+ data.taskStartDate.replace(/-/g,'') +'000000</Eff_date>';
    xml +=      '<Exp_date>'+ data.taskEndDate.replace(/-/g,'') +'235959</Exp_date>';
    xml +=      '<Send_time>'+ nowStr +'</Send_time>';
    xml +=  '</msg>';
    xml += '</body>';
    xml += '</xml>';

    printRecorder("请求xml="+xml);

    return xml;
}

function sendData( xml ) {
    var addr = 'http://130.30.6.38:12007/adapter';

    var ArrayList = Java.type('java.util.ArrayList');
    var heads = new ArrayList();
    var NameValuePair = Java.type('com.x.base.core.project.bean.NameValuePair');
    //var p1 = new NameValuePair('Content-Type', 'application/x-www-form-urlencoded; charset=utf-8');
    var p1 = new NameValuePair('Content-Type', 'text/xml; charset=utf-8');
    heads.add(p1);
    //var parameters = '&data=' + encodeURIComponent(xml);

    var HttpConnectionClass = Java.type('com.x.base.core.project.connection.HttpConnection');

    var resp = HttpConnectionClass.postAsString(addr, heads, xml);
    //print(resp);
    return resp;

}

function getText(root, tag){
    var list = root.getElementsByTagName(tag);
    if( list == null )return null;
    var node = list.item(0);
    if( node == null )return null;
    return node.getTextContent();
}

function setWorkFlag( workCompletedId, json ){
    if( json.Response_code === "0000" ){
        resources.getWebservicesClient().jaxrsPut('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/interfaceStatus", "syncDone" );
    }else{
        resources.getWebservicesClient().jaxrsPut('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/interfaceStatus2", "syncError" );
    }
    if( json.Response_desc ){
        resources.getWebservicesClient().jaxrsPut('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/interfaceResponseDesc", json.Response_desc );
    }
    printRecorder( "workCompletedId为"+workCompletedId+"的工单设置标志位成功" );
}

function parseResp( xmlStr ){
    // var resp = '<?xml version="1.0" encoding="UTF-8"?>';
    //     resp+='<head>';
    //     resp+=	'<sign>';
    //     resp+=	    '<service_name>addChannelTask</service_name>';
    //     resp+=	    '<Trans_ido></Trans_ido>';
    //     resp+=	'</sign>';
    //     resp+='</head>';
    //     resp+='<body>';
    //     resp+=	'<msg>';
    //     resp+=		'<Response_code>0000</Response_code>';
    //     resp+=		'<Response_desc>success</Response_desc>';
    //     resp+=	'</msg>';
    //     resp+='</body>';

    // printRecorder( "BSS端返回" + resp );

    // var title = resp.substr( 0, resp.indexOf('?>')+2 );
    // var root = resp.substr( resp.indexOf('?>')+2, resp.length );
    // var xmlStr = title + "<root>" + root + "</root>";
    //printRecorder( "xmlStr="+xmlStr );

    var DocumentBuilderFactory = Java.type('javax.xml.parsers.DocumentBuilderFactory');
    var StringReader = Java.type('java.io.StringReader');
    var InputSource = Java.type('org.xml.sax.InputSource');

    var dbf = DocumentBuilderFactory.newInstance();
    var db = dbf.newDocumentBuilder();
    var sr = new StringReader(xmlStr);
    var is = new InputSource(sr);
    var document = db.parse(is);
    var root = document.getDocumentElement();

    var Response_code = getText(root,"Response_code");
    var Response_desc = getText(root,"Response_desc");

    printRecorder( "Response_code=" + Response_code );
    printRecorder( "Response_desc=" + Response_desc );

    return {
        "Response_code" : Response_code,
        "Response_desc" : Response_desc
    }

}

function init(){
    var idList = getWorkCompleteIds();
    print( "idList="+idList );
    if( idList.size() === 0 ){
        print("未找到需要同步的工单");
    }
    for(var i = 0; i<idList.size(); i++){
        try{
            var id = idList.get(i);
            workcompletedid = id;
            printRecorder("处理工单，workCompletedId="+id );

            var workData = resources.getWebservicesClient().jaxrsGet( "x_processplatform_assemble_surface", "data/workcompleted/"+id );
            //printRecorder("workData="+workData);

            var data = JSON.parse( workData.toString() );
            var xml = getXml( data );
            var resp = sendData( xml );
            printRecorder( "BSS端返回 " + resp );
            var json = parseResp( resp );
            setWorkFlag( id, json );
            printRecorder( "workCompletedId为"+id+"的工单同步结束" );
            printRecorder( "文件URL：/x_desktop/work.html?workcompletedid="+id );
            if( pw !==null ){
                pw.close();
                pw = null
            }
            if( recordFile != null ){
                recordFile = null
            }
        }catch(e){
            e.printStackTrace();
        }
    }
}

//function init(){
//    var id = getWorkCompleteId();
//    workcompletedid = id;
//    if( id === null ){
//        print("未找到需要同步的工单")
//    }else{
//        printRecorder("找到工单，workCompletedId="+id );
//
//        var workData = resources.getWebservicesClient().jaxrsGet( "x_processplatform_assemble_surface", "data/workcompleted/"+id );
//        //printRecorder("workData="+workData);
//
//        var data = JSON.parse( workData.toString() );
//        var xml = getXml( data );
//        var resp = sendData( xml );
//        printRecorder( "BSS端返回 " + resp );
//        var json = parseResp( resp );
//        setWorkFlag( id, json );
//        printRecorder( "workCompletedId为"+id+"的工单同步结束" );
//        printRecorder( "文件URL：/x_desktop/work.html?workcompletedid="+id );
//        if( pw !==null )pw.close();
//    }
//}

init();