/*
 * resources.getEntityManagerContainer() // 实体管理容器.
 * resources.getContext() //上下文根.
 * resources.getOrganization() //组织访问接口.
 * requestText //请求内容.
 * request //请求对象.
 */


printLog("运行代理渠道任务明细下发文件");

load("nashorn:mozilla_compat.js");

var File = Java.type('java.io.File');
var Root_Dir = "D:"+File.separator+'FTPFile'+ File.separator + "ChannelTask" + File.separator + "Detail" + File.separator;

var Root_Dir_Record = "D:"+File.separator+'FTPFile'+ File.separator + "ChannelTaskRecorder" + File.separator ;

function getSeq( nowStr ){
    var str = resources.getWebservicesClient().jaxrsGet( "x_processplatform_assemble_surface",
        "applicationdict/ChannelTaskDetail_Seq/application/channelTaskProcess/seq/data" );
    str = getPureText( str.toString() );
    var arr = str.split("-");
    var seq = 0;
    if( arr.length > 1 && arr[0] === nowStr )seq = parseInt( arr[1] );
    seq ++;
    var seqStr = "0000" + seq;
    seqStr = seqStr.substr(seqStr.length - 4, 4 );
    resources.getWebservicesClient().jaxrsPut( "x_processplatform_assemble_surface",
        "applicationdict/ChannelTaskDetail_Seq/application/channelTaskProcess/seq/data", nowStr+"-"+seqStr );
    return seqStr;
}

function createRecordFloder(){
    var Date = Java.type( "java.util.Date" );
    var now = new Date();
    var recordPath = Root_Dir_Record +
        new java.text.SimpleDateFormat("yyyy").format(now) + File.separator +
        new java.text.SimpleDateFormat("MM").format(now) + File.separator +
        new java.text.SimpleDateFormat("dd").format(now) + File.separator + "Detail";
    var recordDir = new File(recordPath);
    if (!recordDir.exists()) {
        if(!recordDir.mkdirs()){
            print( "创建记录文件夹失败："+ recordPath )
            return null;
        }
    }
    return recordDir;
}



function createFile(){

    var Date = Java.type( "java.util.Date" );
    var now = new Date();

    var nowStr = new java.text.SimpleDateFormat("yyyyMMdd").format(now);

    var seq = getSeq( nowStr );

    var path = Root_Dir + nowStr + '_ChannelTaskDetail_' + ( seq || '0001' ) + '.REQ';
    print( "path="+path );

    var file = new File(path );
    if (file.exists()) { // 如果已存在,删除旧文件
        file.delete();
    }
    if(!file.createNewFile()){
        printLog("不能创建文件:"+path);
        return null;
    }else{
        printLog("创建文件:"+path);
        return file;
    }
}

function printLog( text ){
    print( text );
}


function setWorkFlag( workCompletedId, fileName ){
    printLog( "设置标志位" );
    if( fileName === "" ){
        resources.getWebservicesClient().jaxrsPut('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/interfaceStatus", "detailNone" );
    }else{
        resources.getWebservicesClient().jaxrsPut('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/interfaceStatus", "detailDone" );
        resources.getWebservicesClient().jaxrsPut('x_processplatform_assemble_surface', "data/workcompleted/"+workCompletedId+"/File_Name", fileName );
    }
}

function getWorkData(){
    var filterList = {"filterList": [{
        "logic":"and",
        "path": "interfaceStatus",
        "title": "接口状态",
        "comparison":"equals",
        "comparisonTitle":"等于",
        "value": "wait",
        "formatType":"textValue"
    }]};
    //var filterList = {"filterList": [] };
    var json = resources.getWebservicesClient().jaxrsPut('x_query_assemble_surface', "view/flag/workCompletedByBranch/query/channelTask/execute", JSON.stringify( filterList ) );
    //var json = resources.getWebservicesClient().jaxrsGet('x_processplatform_assemble_surface', "data/workcompleted/e8db5cba-2d35-4b91-86a2-6c482ed7da55" );

    var obj = json.getAsJsonObject();
    return obj.get("grid");
}

function getNumberData( workId, branch ){
    var filterList = { filterList : [{
        "logic":"and",
        "path": "workId",
        "title": "workId",
        "comparison":"equals",
        "comparisonTitle":"等于",
        "value": workId,
        "formatType":"textValue"
    },{
        "logic":"and",
        "path": "branch",
        "title": "branch",
        "comparison":"equals",
        "comparisonTitle":"等于",
        "value": branch,
        "formatType":"textValue"
    }]};

    var json = resources.getWebservicesClient().jaxrsPut('x_query_assemble_surface', encodeURI("view/flag/byPhoneNumber/query/channelTask/execute"), JSON.stringify( filterList ) );
    //print( "getNumberData="+json.get("grid") );
    var grid = json.get("grid");
    return grid.getAsJsonArray();
}

function isEmpty( str ){
    if( str === null )return true;
    var s = str.trim();
    if( s === "\"\"" )return true;
    if( s.equals("\"\"") )return true;
    if( s === "" )return true;
    if( s.equals("") )return true;
    return false;
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

function ouputNumberList( numberList, workId, branchWorkId, numberCount ){
    var file = createFile();
    if( file === null )return null;

    printLog( "输出数据" );

    var pw = new java.io.PrintWriter(file, "GBK");

    //pw.print("TOTAL_NUM:");
    pw.print( numberCount || numberList.size() );

    pw.write(0x0d);
    pw.write(0x0a);
    pw.write(0x0d);
    pw.write(0x0a);


    var iter = numberList.iterator();
    //2、通过循环迭代
    //hasNext():判断是否存在下一个元素
    while(iter.hasNext()){
        //如果存在，则调用next实现迭代
        var obj = iter.next();  //把Object型强转成int型

        var data = obj.get("data");

        var number = getPureText( data.get("subject").toString() );
        //pw.print("SERIAL_NUMBER:");
        pw.print( number );
        pw.print("|");

        //pw.print("Task_id:");
        pw.print( workId );
        pw.print("|");

        //pw.print("Sub_task_id:");
        pw.print( branchWorkId );

        pw.write(0x0d);
        pw.write(0x0a);

    }
    pw.close();

    return file;
}


function init(){
    var FileUtils = Java.type( "org.apache.commons.io.FileUtils" );

    var workList = getWorkData();
    print( "workList="+workList );

    if( workList.size() > 0 ){
        var recorderDir = createRecordFloder();
    }

    var iter = workList.iterator();
    //2、通过循环迭代
    //hasNext():判断是否存在下一个元素
    while(iter.hasNext()){
        //如果存在，则调用next实现迭代
        var obj = iter.next();  //把Object型强转成int型

        var data = obj.get("data");

        var branch = data.get("currentUnit").toString();

        var workId = data.get("provinceWorkId").toString();
        if( isEmpty( workId ) )workId = data.get("cityWorkId").toString();
        if( isEmpty( workId ) )workId = data.get("countyWorkId").toString();
        if( isEmpty( workId ) )workId = data.get("branchWorkId").toString();

        var branchWorkId = data.get("branchWorkId").toString();

        var numberCount = data.get("numberCount").toString();

        var workCompletedId = data.get("workCompletedId").toString();
        workCompletedId = getPureText(workCompletedId);

        print("workId="+workId);
        print("branch="+branch);

        var file = null;
        if( !isEmpty( workId ) && !isEmpty( branch ) ){
            workId = getPureText( workId );
            branch = getPureText( branch );
            branchWorkId = getPureText( branchWorkId );
            numberCount = getPureText(numberCount);
            var numberList = getNumberData( workId, branch );
            if( numberList.size() > 0 ){
                file = ouputNumberList( numberList, workId, branchWorkId, numberCount );
            }else{
                print( '根据workId='+workId + ",branch="+ branch + "未找到号码！" );
            }
        }

        if( workCompletedId && workCompletedId !== "" && file!== null ){
            setWorkFlag(workCompletedId, file.getName());

            if( recorderDir!==null ){
                try{
                    FileUtils.copyFileToDirectory( file, recorderDir );
                    print("明细文件拷贝到记录文件夹成功："+recorderDir.getCanonicalPath());
                }catch(e){
                    e.printStackTrace();
                    print("明细文件拷贝到记录文件夹出错："+recorderDir.getCanonicalPath() + " 错误："+e.getMessage())
                }
            }

            print( '根据workId='+workId + ",branch="+ branch + "下发明细文件成功！" );
        }
    }

}

init();

