/*
 * resources.getEntityManagerContainer() // 实体管理容器.
 * resources.getContext() //上下文根.
 * resources.getOrganization() //组织访问接口.
 * requestText //请求内容.
 * request //请求对象.
 */


print("批量导入人员");
var File = Java.type('java.io.File');

var Config = {
    interfaceUrl : "http://lyxx.zoneland.net:20030/x_program_center/jaxrs/invoke/personSync/execute",
    localPath : File.separator  + "data" + File.separator + "ImportPersonFromExcel" + File.separator,
    recordLocalPath : File.separator+'data'+ File.separator + "ImportPersonFromExcelRecord" + File.separator
};


(function(context) {
    'use strict';

    var Timer = Java.type('java.util.Timer');
    var Phaser = Java.type('java.util.concurrent.Phaser');

    var timer = new Timer('jsEventLoop', false);
    var phaser = new Phaser();

    var timeoutStack = 0;
    function pushTimeout() {
        timeoutStack++;
    }
    function popTimeout() {
        timeoutStack--;
        if (timeoutStack > 0) {
            return;
        }
        timer.cancel();
        phaser.forceTermination();
    }

    var onTaskFinished = function() {
        phaser.arriveAndDeregister();
    };

    context.setTimeout = function(fn, millis /* [, args...] */) {
        var args = [].slice.call(arguments, 2, arguments.length);

        var phase = phaser.register();
        var canceled = false;
        timer.schedule(function() {
            if (canceled) {
                return;
            }

            try {
                fn.apply(context, args);
            } catch (e) {
                print(e);
            } finally {
                onTaskFinished();
                popTimeout();
            }
        }, millis);

        pushTimeout();

        return function() {
            onTaskFinished();
            canceled = true;
            popTimeout();
        };
    };

    context.clearTimeout = function(cancel) {
        cancel();
    };

    context.setInterval = function(fn, delay /* [, args...] */) {
        var args = [].slice.call(arguments, 2, arguments.length);

        var cancel = null;

        var loop = function() {
            cancel = context.setTimeout(loop, delay);
            fn.apply(context, args);
        };

        cancel = context.setTimeout(loop, delay);
        return function() {
            cancel();
        };
    };

    context.clearInterval = function(cancel) {
        cancel();
    };

})(this);

var Utils = {
    parseResp : function( resp ){
        if( !resp || resp === null ){
            return {
                "type": "error",
                message : "服务响应是null，需要管理员查看后台日志"
            }
        }else{
            var json = JSON.parse( resp.toString() );
            return json;
        }
    },
    getFailText : function( json ){
        //{
        //    "type": "error",
        //    "message": "手机号错误:15268803358, 手机号已有值重复.",
        //    "date": "2018-08-05 02:51:35",
        //    "spent": 5,
        //    "size": -1,
        //    "count": 0,
        //    "position": 0,
        //    "prompt": "com.x.organization.assemble.control.jaxrs.person.ExceptionMobileDuplicate"
        //}
        var text;
        if( json.message ){
            text = json.message + ( json.prompt ? "("+json.prompt + ")" : "" );
        }else if( json.prompt ){
            text = json.prompt;
        }else{
            text = "未知异常";
        }
        print(text);
        return text;
    }
};

function getFileList(){

    var path = Config.localPath;

    var file = new File(path );

    var ArrayList = Java.type('java.util.ArrayList');
    var wjList = new ArrayList();

    if (file.exists()) {
        var fileList = file.listFiles();
        for (var i = 0; i < fileList.length; i++) {
            if (fileList[i].isFile()) {//判断是否为文件
                print( fileList[i].getName() );
                var name = fileList[i].getName().split(".");
                if( name[ name.length - 1 ].equals("xls") ){
                    wjList.add(fileList[i]);
                }
            }
        }
    }else{
        print("目录不存在:"+path);
    }
    return wjList;
}


function importExcel( file ) {

    var FileInputStream = Java.type('java.io.FileInputStream');
    // var InputStreamReader = Java.type('java.io.InputStreamReader');
    //var BufferedReader = Java.type('java.io.BufferedReader');
    //var FileReader = Java.type('java.io.FileReader');


    var errorMsg = "";
    var inStream = null;
    try {
        //inStream = new InputStreamReader(new FileInputStream(file), "GBK");
        inStream = new FileInputStream(file);
        var workBook = null;
        try {
            var fileName = file.getName();
            if (fileName.endsWith(".xls")) { // 97-03
                var HSSFWorkbook = Java.type('org.apache.poi.hssf.usermodel.HSSFWorkbook');
                workBook = new HSSFWorkbook(inStream);
            } else if (fileName.endsWith(".xlsx")) { // 2007
                var XSSFWorkbook = Java.type('org.apache.poi.hssf.usermodel.XSSFWorkbook');
                workBook = new XSSFWorkbook(inStream);
            } else {
                errorMsg = "不支持的文件类型！";
                print(errorMsg);
                return false;
            }
        } catch ( e) {
            errorMsg = "解析Excel文件出错！";
            print(errorMsg);
            e.printStackTrace();
            return false;
        } finally {
            inStream.close();
        }

        if( errorMsg.length === 0 ) {
            var sheets = null !== workBook ? workBook.getNumberOfSheets() : 0;
            var sheet = workBook.getSheetAt(0); // 读取第一个sheet

            var rows = sheet.getPhysicalNumberOfRows(); // 获得行数

            if (rows > 1) { // 第一行默认为标题
                runImportRow( sheet, 1, rows  );
                //for (var j = 1; j < rows; j++) {
                //    print("正在执行导入第"+j+"行");
                //    var row = sheet.getRow(j);
                //    var cells = row.getLastCellNum();// 获得列数
                //    if (cells > 0) {
                //        errorMsg = importRow( row , cells);
                //        if( errorMsg !== "" )return false;
                //    }
                //}
            } else {
                errorMsg = "EXCEL没有数据，请确定。";
            }
        }
        if( errorMsg.length() > 0 ) {
            print("错误消息：" + errorMsg);
            return false;
        }else {
            print( file.getName()+"导入成功！");
            return true;
        }
    } catch (ex) {
        ex.printStackTrace();
        return false;
    }
}


var Cell = Java.type("org.apache.poi.ss.usermodel.Cell");
function runImportRow( sheet, rowIndex, total  ){
    if( rowIndex < total ){
        var row = sheet.getRow(rowIndex);
        var cells = row.getLastCellNum();// 获得列数
        if (cells > 0) {
            print("正在执行导入第"+rowIndex+"行");
            var errorMsg = importRow( row , cells);
            //if( errorMsg !== "" )return false;
        }

        rowIndex = rowIndex + 1;
        setTimeout(function() {
            runImportRow( sheet, rowIndex, total );
        }, 100 );
    }
}

function importRow( row, cells ){
    var json = {
        "action" : "add",
        "forceFlag" : "yes",
        "attributeList" : []
    };
    print( "cells = " + cells );
    for (var k = 0; k < cells; k++) {
        var value = "";
        var cell = row.getCell(k);
        if( cell !== null ) {
            if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC && java.lang.String.valueOf(cell.getNumericCellValue()).indexOf("E")==-1){   //数字
                value = java.lang.String.valueOf(cell.getNumericCellValue());
            }else {
                //return new DecimalFormat("#").format(cell.getNumericCellValue());
                cell.setCellType(Cell.CELL_TYPE_STRING);  // 全部置成String类型的单元格
                value = cell.getStringCellValue().trim();
            }
        }


        var flag = true;
        switch( k ) {
            case 0:
                json.employee = value; //工号
                break;
            case 1:
                json.unique = value;
                break;
            case 2 :
                json.mobile = value;
                break;
            case 3 :
                json.mail = value;
                break;
            case 4 :
                json.name = value;
                break;
            case 5 :
                json.unitList = [{ flag : value }];
                break;
        }
        if( !flag )return "";
    }
    if( !json.unique || json.unique == "0")return "";
    return sendRequest( json );
}

function sendRequest( body ){
    var errorText = "";
    try{
        if( typeof( body ) == "string" )body = JSON.parse(bodyStr);
        body.saveFlag = "no";
        var resp = com.x.base.core.project.connection.CipherConnectionAction.post(false,Config.interfaceUrl,JSON.stringify(body));
        // print("resend person resp="+resp);
        var response = Utils.parseResp( resp );
        // print("resend person response="+JSON.stringify(response));
        if( response.type && response.type == "success" && response.data && response.data.value && response.data.value.result && response.data.value.result == "success" ){
        }else{
            errorText = Utils.getFailText( response );
        }
    }catch(e){
        errorText= e.printStackTrace();
    }finally{
        print("resend person errorText="+errorText);
        return errorText;
    }
}

function init(){
    var fileList = getFileList();
    for(var i = 0; i<fileList.size(); i++){
        try{
            var file = fileList.get(i);
            if( importExcel( file ) ){
                print( "正在修改文件目录："+file.getName() );
                var newFilePath = Config.recordLocalPath + file.getName();
                if (file.renameTo(new File( newFilePath ))) {
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