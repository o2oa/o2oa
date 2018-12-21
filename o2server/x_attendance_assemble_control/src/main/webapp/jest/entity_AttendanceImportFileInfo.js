function upload_attendanceimportfileinfo() {
	//开始上传具体的文件
	$.ajaxFileUpload({				
		//处理文件上传操作的服务器端地址(可以传参数,已亲测可用)
	    url: '../servlet/upload/',
 		data:{},
		secureuri:false,            //是否启用安全提交,默认为false 
		fileElementId:'file',         //文件选择框的id属性
		dataType:'text',            //服务器返回的格式,可以是json或xml等
		success:function( data, status){        //服务器响应成功时的处理函数
			$('#result').html("数据导入成功！");
		},
		error:function(data, status, e){ //服务器响应失败时的处理函数
			console.log(e);
			console.log(data);
			$('#result').html('上传失败，请重试！！');
		}
	});
}

function download_attendanceimportfileinfo( id ) {
	
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
	
    window.open( '../servlet/download/'+id+'/stream' );
}

function data_get_attendanceimportfileinfo( id ) {
	var query_url = '../jaxrs/attendanceimportfileinfo/' + id;
	//如果未输入ID，那么就查询所有的应用信息
	if( id == null || id == undefined || id == "" ){
		query_url = '../jaxrs/attendanceimportfileinfo/list/all';
	}
    $.ajax({
		type : 'get',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : query_url,
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true
    }).done(function(json) {
    	$('#result').html( JSON.stringify( json, null, 4) );
    }).fail(function(json) {
    	failure(json);
    });
}


function data_delete_attendanceimportfileinfo( id ) {
	if( id == null || id == undefined || id == "" ){
		alert("请输入ID");
		return false;
	}
    $.ajax({
		type : 'delete',
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		url : '../jaxrs/attendanceimportfileinfo/' + id ,
		xhrFields : {
		    'withCredentials' : true
		},
		crossDomain : true
    }).done(function(json) {
    	$('#result').html(JSON.stringify(json.data, null, 4));
    });
}