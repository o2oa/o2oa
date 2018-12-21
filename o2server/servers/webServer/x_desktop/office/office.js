function load(){
    var str = '';
    var copyright = "金格科技iWebOffice2015智能文档中间件[演示版];V5.0S0xGAAEAAAAAAAAAEAAAAHABAACAAQAALAAAAO98naKRJ/x1wD47iJF5G4zPGELwHDAiSU1UFfkGHerydasPoOUL5vcnQXV22z1X3+JeME0bL5Epd8WmfHOKZR4oK4IuEoUjDMjjvtsoPgvwmtD3weIwWlaOqb7JoAVQj+oorgBBbc2CuttNj0VW/axaJc9ZSGZoHCSw5gAIYq6fYeMbrXil+w3FjTKB3HP37kd75BB4762kXrN8VNnQ9yeSkrpHrJOT5T1rJuY37tc57Aam3Mnw8XVFNtoBuJi/CPXIvPQGbbaw9EOnPEtrsY8q2PW7hUkHqvQKKdXVBSZ7+g5v5USWbAq9IbYMuwqtFior5gNR9vSohnRT/4ayfrrtYfLh6SflvjP8NcDPv/xOPXV3ahq3i6Sxa4GaZwDp+ZUXiWJQa2MAtq4og+i0L5wLt4tP48AlC0Sl67/J9ct9XJ3I77Moj6/53xrB+KH4WRGVqbeqFqGdXZIraWuWy/CJ+iZlxkz/puoy9ZYwoIAE80nelMPVx6qhM6suqdBqUuGbvq1AP1nf75tR1C+Bels=";
    str += '<object id="WebOffice2015" ';

    str += ' width="100%"';
    str += ' height="800px"';

    if ((window.ActiveXObject!=undefined) || (window.ActiveXObject!=null) ||"ActiveXObject" in window)
    {
        str += ' CLASSID="CLSID:D89F482C-5045-4DB5-8C53-D2C9EE71D025"  codebase="iWebOffice2015.cab#version=12,2,0,382"';
        str += '>';
        str += '<param name="Copyright" value="' + copyright + '">';
    }
    else
    {
        str += ' progid="Kinggrid.iWebOffice"';
        str += ' type="application/iwebplugin"';
        str += ' OnCommand="OnCommand"';
        str += ' OnReady="OnReady"';
        str += ' OnOLECommand="OnOLECommand"';
        str += ' OnExecuteScripted="OnExecuteScripted"';
        str += ' OnQuit="OnQuit"';
        str += ' OnSendStart="OnSendStart"';
        str += ' OnSending="OnSending"';
        str += ' OnSendEnd="OnSendEnd"';
        str += ' OnRecvStart="OnRecvStart"';
        str += ' OnRecving="OnRecving"';
        str += ' OnRecvEnd="OnRecvEnd"';
        str += ' OnRightClickedWhenAnnotate="OnRightClickedWhenAnnotate"';
        str += ' OnFullSizeBefore="OnFullSizeBefore"';
        str += ' OnFullSizeAfter="OnFullSizeAfter"';
        str += ' Copyright="' + copyright + '"';
        str += '>';
    }
    str += '</object>';
    $("officeDiv").set("html", str);
}
var WebOffice =null;
window.addEvent('domready', function() {
    load();
    var obj = $("WebOffice2015");
    WebOffice = new WebOffice2015();
    WebOffice.setObj(obj);

    // WebOffice.WebUrl="<%=mServerUrl%>";             //WebUrl:系统服务器路径，与服务器文件交互操作，如保存、打开文档，重要文件
    // WebOffice.RecordID="<%=mRecordID%>";            //RecordID:本文档记录编号
    // WebOffice.FileName="<%=mFileName%>";            //FileName:文档名称
    // WebOffice.FileType="<%=mFileType%>";            //FileType:文档类型  .doc  .xls
    // WebOffice.UserName="<%=mUserName%>";            //UserName:操作用户名，痕迹保留需要
    WebOffice.AppendMenu("1","打开本地文件(&L)");    //多次给文件菜单添加
    WebOffice.AppendMenu("2","保存本地文件(&S)");
    WebOffice.AppendMenu("3","-");
    WebOffice.AppendMenu("4","打印预览(&C)");
    WebOffice.AppendMenu("5","退出打印预览(&E)");
    WebOffice.AddCustomMenu();                       //一次性多次添加包含二次菜单
    WebOffice.Skin('black');                        //设置皮肤
    WebOffice.HookEnabled();
    WebOffice.SetCaption();

    WebOffice.CreateFile();

    WebOffice.setEditType("1");         //EditType:编辑类型  方式一   WebOpen之后
    WebOffice.VBASetUserName("tommy");    //设置用户名
    //WebOffice.AddToolbar();//打开文档时显示手写签批工具栏
    WebOffice.ShowCustomToolbar(false);//隐藏手写签批工具栏

    var doc = obj.ActiveDocument;
});

function save() {
    // var WebOffice = new WebOffice2015();
    // WebOffice.FileType=".doc";
    // var obj = $("WebOffice2015");
    // WebOffice.setObj(obj);

    // var httpclient = obj.Http; //设置http对象
    // httpclient.Clear();
    // this.WebSetMsgByName("USERNAME", this.UserName);
    // this.WebSetMsgByName("RECORDID", this.RecordID);
    // this.WebSetMsgByName("OPTION", "SAVEFILE");
    // this.WebSetMsgByName("FILENAME", this.FileName); //加载FileName
    // this.WebSaveLocalFile(this.getFilePath() + this.FileName);
    //WebOffice.WebUrl="<%=mServerUrl%>";
    // var doc = obj.ActiveDocument;
    // doc.save();
    //obj.ShowDialog(2);
    //obj.Save("c:/123.doc", 0, true);
    WebOffice.WebSaveLocalFile("e:\\123.doc");
    //obj.Close();

    //httpclient.AddForm("FormData", this.WebSendMessage());
    // httpclient.AddFile("FileData", "c:\\123.doc");    //需要上传的文件
    // //this.WebClearMessage();
    // //httpclient.ShowProgressUI = false;           //隐藏进度条
    // if (httpclient.Open(1, "http://192.168.10.206:20020/x_file_assemble_control/jaxrs/attachment/upload/folder/(0)", false)) {//这里采用同步方式打开文档。我要返回值
    //     if (!httpclient.Send()) {
    //
    //         if (httpclient.Status == 0) {
    //             if (!httpclient.Send()) {
    //                 return false;
    //             }
    //         }
    //     }
    //     httpclient.Clear();
    //     return true;
    // }
    // return false;


    // if (this.SAVEFILE(httpclient, this.FilePath + this.FileName)) {
    //     this.Status = "保存文件成功";
    //     return true;
    // }
    // this.Status = "保存文件失败";
    // return false;
}