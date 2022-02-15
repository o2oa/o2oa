var Describe = function() {
	// 20180730
}
Describe.splitValue = function(str) {
	if (str) {
		if (str.length > 0) {
			return str.split(',');
		}
	}
	return [];
}
Describe.joinValue = function(o, split) {
	var s = ',';
	if (split) {
		s = '' + split;
	}
	if (o) {
		if (toString.apply(o) === '[object Array]') {
			return o.join(s);
		}
	}
	return o;
}
Describe.doPost = function(address, m, data) {
	$('#url').html(address);
	if ((m.resultContentType) && m.resultContentType.indexOf('application/json') > -1) {
		$.ajax({
			url : address,
			type : 'POST',
			headers : {
				'x-debugger' : true
			},
		    contentType : (m.contentType.indexOf('multipart/form-data') > -1) ? false : m.contentType ,
			processData : (m.contentType.indexOf('application/json') > -1) ? true : false,
			xhrFields : {
				'withCredentials' : true
			},
			data : ((m.contentType.indexOf('application/json') > -1) && (!m.useStringParameter) ? JSON.stringify(data) : data)
		}).always(function(resultJson) {
			$('#result').html(JSON.stringify(resultJson, null, 4));
			Describe.writeOut(m.outs, resultJson);
		});
	} else {
		$.ajax({
			url : address,
			type : 'POST',
			headers : {
				'x-debugger' : true
			},
			contentType : (m.contentType.indexOf('application/json') > -1) ? m.contentType : false,
			processData : (m.contentType.indexOf('application/json') > -1) ? true : false,
			xhrFields : {
				'withCredentials' : true
			},
			data : ((m.contentType.indexOf('application/json') > -1) && (!m.useStringParameter) ? JSON.stringify(data) : data)
		});
	}
}
Describe.doPut = function(address, m, data) {
	$('#url').html(address);
	if ((m.resultContentType) && m.resultContentType.indexOf('application/json') > -1) {
		$.ajax({
			url : address,
			type : 'PUT',
			headers : {
				'x-debugger' : true
			},
			contentType : (m.contentType.indexOf('application/json') > -1) ? m.contentType : false,
			processData : (m.contentType.indexOf('application/json') > -1) ? true : false,
			xhrFields : {
				'withCredentials' : true
			},
			data : ((m.contentType.indexOf('application/json') > -1) && (!m.useStringParameter) ? JSON.stringify(data) : data)
		}).always(function(resultJson) {
			$('#result').html(JSON.stringify(resultJson, null, 4));
			Describe.writeOut(m.outs, resultJson);
		});
	} else {
		$.ajax({
			url : address,
			type : 'PUT',
			headers : {
				'x-debugger' : true
			},
			contentType : (m.contentType.indexOf('application/json') > -1) ? m.contentType : false,
			processData : (m.contentType.indexOf('application/json') > -1) ? true : false,
			xhrFields : {
				'withCredentials' : true
			},
			data : ((m.contentType.indexOf('application/json') > -1) && (!m.useStringParameter) ? JSON.stringify(data) : data)
		});
	}
}
Describe.doGet = function(address, m) {
	$('#url').html(address);
	if ((m.resultContentType) && m.resultContentType.indexOf('application/json') > -1) {
		$.ajax({
			type : 'GET',
			dataType : 'json',
			url : address,
			headers : {
				'x-debugger' : true
			},
			contentType : m.contentType,
			xhrFields : {
				'withCredentials' : true
			},
			crossDomain : true
		}).always(function(resultJson) {
			$('#result').html(JSON.stringify(resultJson, null, 4));
			Describe.writeOut(m.outs, resultJson);
		});
	} else {
		window.open(address, '_blank');
	}
}
Describe.doDelete = function(address, m) {
	$('#url').html(address);
	if ((m.resultContentType) && m.resultContentType.indexOf('application/json') > -1) {
		$.ajax({
			type : 'DELETE',
			dataType : 'json',
			url : address,
			headers : {
				'x-debugger' : true
			},
			contentType : m.contentType,
			xhrFields : {
				'withCredentials' : true
			},
			crossDomain : true
		}).always(function(resultJson) {
			$('#result').html(JSON.stringify(resultJson, null, 4));
			Describe.writeOut(m.outs, resultJson);
		});
	} else {
		$.ajax({
			type : 'DELETE',
			dataType : 'json',
			url : address,
			headers : {
				'x-debugger' : true
			},
			contentType : m.contentType,
			xhrFields : {
				'withCredentials' : true
			},
			crossDomain : true
		});
	}
}
Describe.writeOut = function(outs, json) {
	if (outs && (outs.length) && json && json.data) {
		$.each(Object.keys(json.data), function(i, k) {
			$('#out_' + k + '_out', '#outs').html(json.data[k]);
		});
	}
}

Describe.createSampleMootools = function(m) {
	debugger;
	var address = window.location.href;
	address = address.substring(0,address.indexOf("/jest/"));
	var address = address +"/"+ m.path;
	if (m.pathParameters && m.pathParameters.length > 0) {
		$.each(m.pathParameters, function(pi, p) {
			address = address.replace('{' + p.name + '}', '替换参数'+pi);
		});
	}
	if (m.queryParameters && m.queryParameters.length > 0) {
		$.each(m.queryParameters, function(pi, p) {
			var query = p.name + '=' + '替换参数'+pi;
			if (address.indexOf("?") > 0) {
				address += '&' + query;
			} else {
				address += '?' + query;
			}
		});
	}
	
	var strSample="";
	if (m.contentType.indexOf('application/json') > -1) {
		        strSample =  "var data = {};" + "\n";
			if (m.ins && m.ins.length > 0) {
				$.each(m.ins, function(ii, i) {
							switch (i.type) {
						default:
							if (i.isBaseType) {
								if (i.isCollection) {
									  strSample += '       data["'+i.name+'"] = ["参数1"];' + "\n";
								} else {
									  strSample += '       data["'+i.name+'"] = "参数";' + "\n";
								}
							} else {
									if(i.isCollection){
										if(i.fieldValue){
										  if(i.fieldType =='enum'){
											   strSample += '       data["'+i.name+'"] = ["'+ i.fieldValue +'"];'+"\n";	
										  }else{
											   strSample += '       data["'+i.name+'"] = ['+ i.fieldValue +'];'+"\n";	
										  }
										  
										}else{
										  strSample += '       data["'+i.name+'"] = [{"参数1":"value1","参数2":"value2"}];'+"\n";
										}
									}else{
										  if(i.fieldType =='enum'){
												   strSample += '       data["'+i.name+'"] = "'+ i.fieldValue +'";'+"\n";	
											  }else{
												  
													strSample += '       data["'+i.name+'"] = {"参数1":"value1","参数2":"value2"};'+"\n";
											  }
									}
							}
						}
				});
			} else if (m.useJsonElementParameter) {
				strSample += 'data = {"参数1":"value1","参数2":"value2"};' +"\n";
			} else if (m.useStringParameter) {
				strSample += 'data = "参数";'+"\n";
			}

			strSample += " \n var mootoolsRequest = new Request({" + "\n";
		    strSample += "        url:'"+address + "',\n";
			strSample += "        method:'"+ m.type + "',\n";
			strSample += "        dataType:'json',\n";
		    strSample += "        headers : {'Content-Type':'application/json;charset=utf8','x-token':'实际的x-token'}" + ",\n";
			if((m.contentType.indexOf('application/json') > -1) && (!m.useStringParameter)){
				strSample += "        data:JSON.stringify(data),\n";
			}else{
			  	strSample += "        data:data,\n";
			}
            strSample += "        onRequest: function(){ },"+ "\n";
            strSample += "        onSuccess: function(responseText){},"+ "\n";
            strSample += "        onFailure: function(){}"+ "\n";
           strSample +="}).send();"+ "\n";
	} else {
		/*
			strSample = "var formData = new FormData();" + "\n";
			if (m.formParameters && m.formParameters.length > 0) {
				$.each(m.formParameters, function(pi, p) {
					if (p.type == "File") {
							//formData.append(p.name, $('input[type=file]', '#formParameters')[0].files[0]);
					strSample += 'formData.append("'+p.name+'", $("input[type=file]")[0].files[0]);' +  "\n";
					} else {
					strSample += 'formData.append("'+p.name+'", "参数'+pi+'");' +  "\n";
					}
				});
			}
			
			strSample += "$.ajax({" + "\n";
			strSample += "type : '"+ m.type + "',\n";
			strSample += "url : '"+address + "',\n";
			strSample += "headers : {'x-debugger' : true}" + ",\n";
			strSample += "contentType : false,\n";
			strSample += "processData  : false,\n";
			strSample += "xhrFields : {'withCredentials' : true}" + ",\n";
			strSample += "crossDomain : true"+ ",\n";
			strSample += "data : formData"+"\n";
			strSample += "});";	
			*/
	}

	return  strSample;
   }
   
Describe.createSampleJSO2= function(m) {
	var address = window.location.href;
	    address = address.substring(0,address.indexOf("/jest/"));
	var uri = address.substring(address.lastIndexOf("/")+1,address.length);
	 address =  m.path;
	 address = address.substring(address.indexOf("jaxrs/")+6,address.length);
	var parameter = "";
	if (m.pathParameters && m.pathParameters.length > 0) {
		$.each(m.pathParameters, function(pi, p) {
			address = address.replace('{' + p.name + '}', '替换参数'+pi);
			if(parameter == ""){
				parameter = "\"" + p.name + "\"" + ":" + '"替换参数'+pi +'"';
			}else{
				parameter = parameter +  ",\"" + p.name + "\"" + ":" + '替换参数'+pi +'"';
			}
		});
	}
	if (m.queryParameters && m.queryParameters.length > 0) {
		$.each(m.queryParameters, function(pi, p) {
			var query = p.name + '=' + '替换参数'+pi;
			if (address.indexOf("?") > 0) {
				address += '&' + query;
			} else {
				address += '?' + query;
			}
		});
	}
	
	var strSample="";
	if (m.contentType.indexOf('application/json') > -1) {
		  strSample =  "var data = {};" + "\n";
			if (m.ins && m.ins.length > 0) {
				$.each(m.ins, function(ii, i) {
					switch (i.type) {
						default:
							if (i.isBaseType) {
								if (i.isCollection) {
									  strSample += '       data["'+i.name+'"] = ["参数1"];' + "\n";
								} else {
									  strSample += '       data["'+i.name+'"] = "参数";' + "\n";
								}
							} else {
									if(i.isCollection){
										if(i.fieldValue){
										  if(i.fieldType =='enum'){
											   strSample += '       data["'+i.name+'"] = ["'+ i.fieldValue +'"];'+"\n";	
										  }else{
											   strSample += '       data["'+i.name+'"] = ['+ i.fieldValue +'];'+"\n";	
										  }
										  
										}else{
										  strSample += '       data["'+i.name+'"] = [{"参数1":"value1","参数2":"value2"}];'+"\n";
										}
									}else{
										if(i.fieldType =='enum'){
										  strSample += '       data["'+i.name+'"] = "'+ i.fieldValue + '";' + "\n";
										}else{
										  strSample += '       data["'+i.name+'"] = {"参数1":"value1","参数2":"value2"};'+"\n";
										}
									}
							}
						}
				});
			} else if (m.useJsonElementParameter) {
				strSample += 'data = {"参数1":"value1","参数2":"value2"};' +"\n";
			} else if (m.useStringParameter) {
				strSample += 'data = "参数";'+"\n";
			}
			 var functionName = "do";
			 strSample += "\n var root = \"" + uri + "\";" + "\n";
			 strSample += " var options = { " + "\n";
			 strSample += "                 " + functionName + ":{ //服务命名1，自定义"+ "\n";
			 strSample += "                           \"uri\": \"/" + m.path + "\","+ "\n";;
             strSample += "                           \"method\": \""+m.type+"\""+ "\n";
			 strSample += "                      }"+ "\n";
			 strSample += "     }" + "\n";
			 strSample += "var action = new this.Action( root, options);" + "\n\n";
			 strSample += "action.invoke({" + "\n";
			 strSample += "        \"name\": \"" + functionName+ "\", //自定义的服务名" + "\n"; 
			 strSample += "        \"parameter\": {" + parameter+ "},  //uri参数 " + "\n"; 
             strSample += "        \"data\": data, //请求的正文, JsonObject " +  "\n"; 
             strSample += "        \"success\": function(json){ //服务调用成功时的回调方法，json 是服务返回的数据" +  "\n"; 
             strSample += "        //这里进行具体的处理"+ "\n"; 
             strSample += "        }.bind(this),"+ "\n"; 
             strSample += "        \"failure\" : function(xhr){ //服务调用失败时的回调方法，xhr 为 XMLHttpRequest 对象" +  "\n";
             strSample += "        //这里进行具体的处理"+ "\n"; 
             strSample += "     },"+ "\n"; 
             strSample += "        \"async\" : true, //同步还是异步，默认为true" + "\n"; 
             strSample += "        \"withCredentials\" : true, //是否允许跨域请求，默认为true" + "\n"; 
             strSample += "        \"urlEncode\" : true //uri参数是否需要通过encodeURIComponent函数编码，默认为true" + "\n";
             strSample += "});"			
	} else {
		
	}
	return  strSample;
  }   
   
   
   
Describe.createSampleO2= function(m) {
	var address = window.location.href;
	    address = address.substring(0,address.indexOf("/jest/"));
	var uri = address.substring(address.lastIndexOf("/")+1,address.length);
	 address =  m.path;
	 address = address.substring(address.indexOf("jaxrs/")+6,address.length);
	if (m.pathParameters && m.pathParameters.length > 0) {
		$.each(m.pathParameters, function(pi, p) {
			address = address.replace('{' + p.name + '}', '替换参数'+pi);
		});
	}
	if (m.queryParameters && m.queryParameters.length > 0) {
		$.each(m.queryParameters, function(pi, p) {
			var query = p.name + '=' + '替换参数'+pi;
			if (address.indexOf("?") > 0) {
				address += '&' + query;
			} else {
				address += '?' + query;
			}
		});
	}
	
	var strSample="";
	if (m.contentType.indexOf('application/json') > -1) {
			if (m.ins && m.ins.length > 0) {
				 strSample =  "var data = {" + "\n";
				$.each(m.ins, function(ii, i) {
						switch (i.type) {
						default:
							if (i.isBaseType) {
								if (i.isCollection) {
									  strSample += '       "'+i.name+'" : ["参数1"],' + "\n";
								} else {
									  strSample += '       "'+i.name+'" : "参数",' + "\n";
								}
							} else {
									if(i.isCollection){
										if(i.fieldValue){
										  if(i.fieldType =='enum'){
											   strSample += '       "'+i.name+'" : ["'+ i.fieldValue +'"],'+"\n";	
										  }else{
											   strSample += '       "'+i.name+'" : ['+ i.fieldValue +'],'+"\n";	
										  }
										  
										}else{
										  strSample += '       "'+i.name+'" : [{"参数1":"value1","参数2":"value2"}],'+"\n";
										}
									}else{
										if(i.fieldType =='enum'){
											strSample += '       "'+i.name+'" : "'+i.fieldValue+'"\n';
										}else{
											strSample += '       "'+i.name+'" : {"参数1":"value1","参数2":"value2"},'+"\n";
										}
									
									}
							}
						}
				});
				
				 strSample = strSample.substring(0,strSample.lastIndexOf(","));
			     strSample = strSample +"\n"+ "}"+"\n";
				 
			} else if (m.useJsonElementParameter) {
				strSample += 'data = {"参数1":"value1","参数2":"value2"};' +"\n";
			} else if (m.useStringParameter) {
				strSample += 'data = "参数";'+"\n";
			}
		
			if(m.type=="POST"){
			   strSample += " \n var string = JSON.stringify(data);" + "\n";
               strSample += " var apps = this.applications;"+ "\n";
               strSample += " var serviceRoot = \"" + uri + "\";"+ "\n";
               strSample += " var path = \"" + address + "\";"+ "\n"; ;
               strSample += " var resp = apps.postQuery( serviceRoot, path , string);"+ "\n";
			}
			if(m.type=="GET"){
               strSample += " \n var apps = this.applications;"+ "\n";
               strSample += " var serviceRoot = \"" + uri + "\";"+ "\n";
                strSample += " var path = \"" + address + "\";"+ "\n"; ;
               strSample += " var resp = apps.getQuery( serviceRoot, path );"+ "\n";
			}
			if(m.type=="PUT"){
			   strSample += " \n var string = JSON.stringify(data)"+ "\n";
               strSample += " var apps = this.applications"+ "\n";
               strSample += " var serviceRoot = \"" + uri + "\";"+ "\n";
               strSample += " var path = \"" + address+ "\";"+ "\n"; ;
               strSample += " var resp = apps.putQuery( serviceRoot, path , string);"+ "\n";
			}
			if(m.type=="DELETE"){
			   strSample += " \n var apps = this.applications;"+ "\n";
               strSample += " var serviceRoot = \" "+ uri + "\";"+ "\n";
                 strSample += " var path = \"" + address + "\";"+ "\n"; ;
               strSample += " var resp = apps.deleteQuery( serviceRoot, path);"+ "\n";
			}
               strSample += " var json = JSON.parse( resp.toString() );"+ "\n";
			
	} 
	return  strSample;
  }
  
  
Describe.createSample= function(m) {
	var address = window.location.href;
	address = address.substring(0,address.indexOf("/jest/"));
	var address = address +"/"+ m.path;
	if (m.pathParameters && m.pathParameters.length > 0) {
		$.each(m.pathParameters, function(pi, p) {
			address = address.replace('{' + p.name + '}', '替换参数'+pi);
		});
	}
	if (m.queryParameters && m.queryParameters.length > 0) {
		$.each(m.queryParameters, function(pi, p) {
			var query = p.name + '=' + '替换参数'+pi;
			if (address.indexOf("?") > 0) {
				address += '&' + query;
			} else {
				address += '?' + query;
			}
		});
	}
	
	var strSample="";
	if (m.contentType.indexOf('application/json') > -1) {
			if (m.ins && m.ins.length > 0) {
				strSample =  "var data = {" + "\n";
				$.each(m.ins, function(ii, i) {
						switch (i.type) {
						default:
							if (i.isBaseType) {
								if (i.isCollection) {
									  strSample += '       "'+i.name+'" : ["参数1"],' + "\n";
								} else {
									  strSample += '       "'+i.name+'" : "参数",' + "\n";
								}
							} else {
									if(i.isCollection){
										if(i.fieldValue){
										  if(i.fieldType =='enum'){
											   strSample += '       "'+i.name+'" : ["'+ i.fieldValue +'"],'+"\n";	
										  }else{
											   strSample += '       "'+i.name+'" : ['+ i.fieldValue +'],'+"\n";	
										  }
										  
										}else{
										  strSample += '       "'+i.name+'" : [{"参数1":"value1","参数2":"value2"}],'+"\n";
										}
									}else{
										if(i.fieldType =='enum'){
											 
											strSample += '       "'+i.name+'" : "'+i.fieldValue+'"\n';
										}else{
											strSample += '       "'+i.name+'" : {"参数1":"value1","参数2":"value2"},'+"\n";
										}
										
										
									}
							}
						}
				});
				
				 strSample = strSample.substring(0,strSample.lastIndexOf(","));
			     strSample = strSample +"\n"+ "}"+"\n";
			} else if (m.useJsonElementParameter) {
				strSample += '    data = {"参数1":"value1","参数2":"value2"};' +"\n";
			} else if (m.useStringParameter) {
				strSample += '    data = "参数";'+"\n";
			}
			
			var dataBlank = true;
			if(strSample != ""){
				dataBlank = false;
			}
			strSample += "\n$.ajax({" + "\n";
			strSample += "        type : '"+ m.type + "',\n";
			strSample += "        dataType : 'json'" + ",\n";
			strSample += "        url : '"+address + "',\n";
			strSample += "        headers : {'x-debugger' : true}" + ",\n";
			strSample += "        contentType : '"+m.contentType+ "',\n";
			strSample += "        xhrFields : {'withCredentials' : true}" + ",\n";
			strSample += "        crossDomain : true"+ ",\n";
			
			if(dataBlank == false){
			   if((m.contentType.indexOf('application/json') > -1) && (!m.useStringParameter)){
				 strSample += "       data : JSON.stringify(data),\n";
				}else{
				  strSample += "      data : data,"+"\n";
				}
			}
			
		   strSample = strSample.substring(0,strSample.lastIndexOf(","));
		   strSample = strSample +"\n";
					 
			strSample += "}).always(function(resultJson) {"+"\n";
			strSample += "        alert(JSON.stringify(resultJson, null, 4))" +"\n";
			strSample += "});";
			
	} else {
			strSample = "var formData = new FormData();" + "\n";
			if (m.formParameters && m.formParameters.length > 0) {
				$.each(m.formParameters, function(pi, p) {
					if (p.type == "File") {
							//formData.append(p.name, $('input[type=file]', '#formParameters')[0].files[0]);
					strSample += 'formData.append("'+p.name+'", $("input[type=file]")[0].files[0]);' +  "\n";
					} else {
					strSample += 'formData.append("'+p.name+'", "参数'+pi+'");' +  "\n";
					}
				});
			}
			strSample += "$.ajax({" + "\n";
			strSample += "        type : '"+ m.type + "',\n";
			strSample += "        url : '"+address + "',\n";
			strSample += "        headers : {'x-debugger' : true}" + ",\n";
			//strSample += "        contentType : false,\n";
			strSample += "        contentType : '"+m.contentType+ "',\n";
			strSample += "        processData  : false,\n";
			strSample += "        xhrFields : {'withCredentials' : true}" + ",\n";
			strSample += "        crossDomain : true"+ ",\n";
			strSample += "        data : formData"+"\n";
			strSample += "});";	
	}

	return  strSample;
   }
Describe.createSampleCommon= function(m,className) {

	var address = window.location.href;
		address = address.substring(0,address.indexOf("/jest/"));
	var root = address.substring(address.lastIndexOf("/")+1,address.length);

	var parameter = "";
	if (m.pathParameters && m.pathParameters.length > 0) {
			$.each(m.pathParameters, function(pi, p) {
				if(parameter == ""){
					parameter =  p.name ;
				}else{
					parameter = parameter +  "," + p.name;
				}
			});
		}
	var query = "";
	if (m.queryParameters && m.queryParameters.length > 0) {
			$.each(m.queryParameters, function(pi, p) {
				if (query == "") {
					 query = "&" + p.name + '=' + '替换参数'+pi;
				} else {
					 query = query + "&"+ p.name + '=' + '替换参数'+pi;
				}
			});
		}
	var strSample="";
	var body = "";
	if (m.contentType.indexOf('application/json') > -1) {
				if (m.ins && m.ins.length > 0) {
					 body =  "var data = {" + "\n";
					$.each(m.ins, function(ii, i) {
						switch (i.type) {
						default:
							if (i.isBaseType) {
								if (i.isCollection) {
									  body += '       "'+i.name+'" : ["参数1"],' + "\n";
								} else {
									  body += '       "'+i.name+'" : "参数",' + "\n";
								}
							} else {
									if(i.isCollection){
										if(i.fieldValue){
										  if(i.fieldType =='enum'){
											   body += '       "'+i.name+'" : ["'+ i.fieldValue +'"],'+"\n";	
											   body +=(i.fieldSample ? "  "+'<span style="color:red">//注解：'+i.fieldSample +'</span>\n':"");
										  }else{
											   body += '       "'+i.name+'" : ['+ i.fieldValue +'],'+"\n";	
											   body +=(i.fieldSample ? "  "+'<span style="color:red">//注解：'+i.fieldSample +'</span>\n':"");
										  }
										  
										}else{
										  body += '       "'+i.name+'" : [{"参数1":"value1","参数2":"value2"}],'+"\n";
										}
									}else{
										 if(i.fieldType =='enum'){
											 body += '       "'+i.name+'" : "'+ i.fieldValue +'",'+"\n";	
											 body +=(i.fieldSample ? "  "+'<span style="color:red">//注解：'+i.fieldSample +'</span>\n':"");
								
										 }else{
										   body += '       "'+i.name+'" : {"参数1":"value1","参数2":"value2"},'+"\n";
										 }
									}
							}
						}
					});
					
					debugger;
					 body = body.substring(0,body.lastIndexOf(","));
					 body = body +"\n"+ "}"+"\n";
					 
				} else if (m.useJsonElementParameter) {
					body += '       data = {"参数1":"value1","参数2":"value2"};' +"\n";
				} else if (m.useStringParameter) {
					body += '       data = "参数";'+"\n";
				}
	 if(m.type != "GET" ){
		 if( body != ""){
	        strSample += body;	
		 }	   
	 }			
	 strSample += "var action = this.Actions.load(\"" + root + "\");\n";
	 strSample += "       action."+ className + "."+m.name+ "(//平台封装好的方法\n";
	 if(parameter!=""){
	   strSample += "      " + parameter  +",//uri的参数\n";
	 }
	 if(m.type != "GET" ){
		 if( body != ""){
	        strSample += "      data,//body请求参数\n";	
		 }	   
	 }
	 strSample += "      function( json ){ //服务调用成功的回调函数, json为服务传回的数据\n";
	 strSample += "         data = json.data; //为变量data赋值\n";
	 strSample += "      }.bind(this),\n";
	 strSample +=  "     function( json ){ //服务调用失败的回调函数, json为服务传回的数据\n";
	 strSample +=  "        data = json.data; //为变量data赋值\n";
	 strSample +=  "     }.bind(this)\n";
	 //strSample += "      false //同步执行 \n";
	 strSample += "    );\n";
				
	}else{
			var formData = "var formData = new FormData();" + "\n";
			var hasFile = false;
			if (m.formParameters && m.formParameters.length > 0) {
				$.each(m.formParameters, function(pi, p) {
					if (p.type == "File") {
					formData += '      formData.append("'+p.name+'", $("input[type=file]").files[0]);' +  "\n";
					hasFile = true;
					} else {
					formData += '      formData.append("'+p.name+'", "参数值'+pi+'");' +  "\n";
					}
				});
			}
		 strSample += formData;
		 strSample += "var action = this.Actions.load(\"" + root + "\");\n";
		 //strSample += "action."+m.name+ "(//平台封装好的方法\n";
		 strSample += "       action."+ className + "."+m.name+ "(//平台封装好的方法\n";
		  // strSample += "      "+parameter  +",//uri的参数\n";
		  if(parameter!=""){
	        strSample += "      " + parameter  +",//uri的参数\n";
	       }
		  strSample +=  "      formData"+",//from参数\n";
		 if(hasFile == true){
		    strSample +=  '      $("input[type=file]").files[0]'+",//file参数\n";	 
		 }
		 strSample +=  "function( json ){ //服务调用成功的回调函数, json为服务传回的数据\n";
		 strSample +=  "      data = json.data; //为变量data赋值\n";
		 strSample +=  "}.bind(this),\n";
		 strSample +=  "function( json ){ //服务调用失败的回调函数, json为服务传回的数据\n";
		 strSample +=  "      data = json.data; //为变量data赋值\n";
		 strSample +=  "}.bind(this)\n";
		 //strSample +=  "false //同步执行 \n";
		 strSample += ");\n"
		} 
   return  strSample ;		
   }
   
Describe.prototype = {
	"load" : function() {
		var str = '<ul>';
		$.getJSON('../describe/describe.json?rd=' + Math.random(), function(json) {
			Describe.json = json;
			$.each(json.jaxrs, function(ji, j) {
				str += '<li xtype="menu" ' + 'style="margin-top: 30px;font-size:14px;font-weight:bold;"title="' +'" >' + j.name + ' <span style="font-style:italic">(' + j.description+ ')</span>';
				$.each(j.methods, function(mi, m) {
					str += '<ul><li xtype="li"  style="margin-top: 10px;margin-left:-24px;font-size:12px; font-weight:normal;line-height:18px" ><a  title="' + m.path + '"id ="' + j.name + '_' + m.name + '" href="#"><b>' + m.name+'</b><br/><span style="color: #666666;">-'+ m.description + '</span>' + '</a></li></ul>';
				});
				str += '</li>'
			});
			str += '</ul>';
			$("#menu").html(str);
			$.each(json.jaxrs, function(ji, j) {
				$.each(j.methods, function(mi, m) {
					$('#' + j.name + '_' + m.name).click(
							function() {
								$('#result').html('');
								var sample = "";
								var txt = '<fieldset id="method"><legend>Method</legend>';
								txt += '<table>';
								txt += '<tr><td style="width:100px;">name:</td><td><a href="../describe/sources/' + m.className.replace(/\./g, '/') + '.java">' + m.name + '</a></td></tr>';
								txt += '<tr><td>path:</td><td>' + m.path + '</td></tr>';
								txt += '<tr><td>type:</td><td>' + m.type + '</td></tr>';
								txt += '<tr><td>description:</td><td>' + m.description + '</td></tr>';
								txt += '</table>';
								txt += '<button id="' + m.name + "_" + m.type + '">' + m.type + '</button>';
								txt += '<div id="url">&nbsp;</div>';
								txt += '</fieldset>';
								if (m.pathParameters && m.pathParameters.length > 0) {
									txt += '<fieldset id="pathParameters"><legend>Path Parameter</legend>';
									txt += '<table >';
									$.each(m.pathParameters, function(pi, p) {
										if (m.name == 'listNext' || m.name == 'listPrev') {
											switch (p.name) {
											case 'flag':
											case 'id':
												txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid" value="(0)"/></td><td>' + p.name
														+ ':' + p.description + '</td></tr>';
												break;
											case 'count':
												txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid" value="20"/></td><td>' + p.name + ':'
														+ p.description + '</td></tr>';
												break;
											default:
												txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + p.name + ':'
														+ p.description + '</td></tr>';
												break
											}
										} else {
											txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + p.name + ':'
													+ p.description + '</td></tr>';
										}
									});
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								if (m.formParameters && m.formParameters.length > 0) {
									txt += '<fieldset id="formParameters"><legend>Form Parameter</legend>';
									txt += '<table >';
									$.each(m.formParameters, function(pi, p) {
										if (p.type == "File") {
											txt += '<tr><td><input type="file" name="' + p.name + '" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>'
													+ p.name + ':' + p.description + '</td></tr>';
										} else {
											txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + p.name + ':'
													+ p.description + '</td></tr>';
										}
									});
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								if (m.queryParameters && m.queryParameters.length > 0) {
									txt += '<fieldset id="queryParameters"><legend>Query Parameter</legend>';
									txt += '<table >';
									$.each(m.queryParameters, function(pi, p) {
										txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + p.name + ':' + p.description
												+ '</td></tr>';
									});
									txt += '</table>';
									txt += '</fieldset>';
								}
								
							  if (m.ins && m.ins.length > 0) {
									txt += '<fieldset id="ins"><legend>In</legend>';
									txt += '<table>';
									$.each(m.ins, function(ii, i) {
										if (i.isCollection) {
											
											txt += '<tr><td><textarea id="' + i.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + i.name + ':' + i.description +(i.fieldValue ? "  "+'。数据格式：<span style="color:red">'+i.fieldValue +'</span>':"") + (i.fieldSample ? "  "+'<span style="color:red">'+i.fieldSample +'</span>':"") 
											+'</td></tr>';
										} else {
											txt += '<tr><td><input type="text" id="' + i.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + i.name + ':'
											
													+ i.description+ (i.fieldValue ? "  "+'。数据格式：<span style="color:red">'+i.fieldValue +'</span>':"") + (i.fieldSample ? "  "+'<span style="color:red">'+i.fieldSample +'</span>':"") 
													+'</td></tr>';
						
										}
									});
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								if (m.useJsonElementParameter) {
									txt += '<fieldset><legend>JsonElement</legend>';
									txt += '<table><tr><td>';
									txt += '<textarea id="jsonElement" style="height:300px; width:600px; padding:1px; border:1px #000000 solid"/>';
									txt += '</td><td>json</td></tr>';
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								if (m.useStringParameter) {
									txt += '<fieldset><legend>String</legend>';
									txt += '<table><tr><td>';
									txt += '<textarea id="string" style="height:300px; width:600px; padding:1px; border:1px #000000 solid"/>';
									txt += '</td><td>string</td></tr>';
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								if (m.outs && m.outs.length > 0) {
									txt += '<fieldset id="outs"><legend>Out</legend>';
									txt += '<table>';
									$.each(m.outs, function(oi, o) {
										txt += '<tr><td style="width: 160px;">' + o.name + '</td><td style="width: 90px;">' + o.type + '</td><td style="width: 90px;">' + (o.isCollection ? 'multi' : 'single') + '</td><td style="width: 90px;">' + o.description + '</td><td id="out_'
												+ o.name + '_out">&nbsp;</td></tr>';
									});
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								$('#content').html(txt);
								
								$('#' + m.name + '_' + m.type, '#method').click(function() {
									var address = '../' + m.path;
									if (m.pathParameters && m.pathParameters.length > 0) {
										$.each(m.pathParameters, function(pi, p) {
											address = address.replace('{' + p.name + '}', encodeURIComponent($('#' + p.name, '#pathParameters').val()));
										});
									}
									if (m.queryParameters && m.queryParameters.length > 0) {
										$.each(m.queryParameters, function(pi, p) {
											var query = p.name + '=' + encodeURIComponent($('#' + p.name, '#queryParameters').val());
											if (address.indexOf("?") > 0) {
												address += '&' + query;
											} else {
												address += '?' + query;
											}
										});
									}
									if (m.contentType.indexOf('application/json') > -1) {
										switch (m.type) {
										case 'POST':
											var data = {};
											if (m.ins && m.ins.length > 0) {
												$.each(m.ins, function(ii, i) {
													switch (i.type) {
													default:
														if (i.isBaseType) {
															if($('#' + i.name, '#ins').val() != ""){
																if (i.isCollection) {
																	data[i.name] = Describe.splitValue($('#' + i.name, '#ins').val());
																} else {
																	data[i.name] = $('#' + i.name, '#ins').val();
																}
															}
														} else {
															if($('#' + i.name, '#ins').val() == ""){
																/*
																if(i.isCollection){
																	data[i.name] = [{}];
																}else{
																	data[i.name] = {};
																}*/
															}else{
																if(i.fieldType){
																	if(i.fieldType == "enum"){
																	   data[i.name] = $('#' + i.name, '#ins').val();
																	}else{
																		data[i.name] = $.parseJSON($('#' + i.name, '#ins').val());
																	}
																}else{
																    if (i.isCollection) {
																       data[i.name] = Describe.splitValue($('#' + i.name, '#ins').val());
															        } else {
																       data[i.name] = $.parseJSON($('#' + i.name, '#ins').val());
															        }
																}
															}
														}
													}
												});
											} else if (m.useJsonElementParameter) {
												data = $.parseJSON($('#jsonElement').val());
											} else if (m.useStringParameter) {
												data = $('#string').val();
											}
											Describe.doPost(address, m, data);
											break;
										case 'PUT':
											var data = {};
											if (m.ins && m.ins.length > 0) {
												$.each(m.ins, function(ii, i) {
													switch (i.type) {
													default:
														if (i.isBaseType) {
															if($('#' + i.name, '#ins').val() != ""){
																if (i.isCollection) {
																	data[i.name] = Describe.splitValue($('#' + i.name, '#ins').val());
																} else {
																	data[i.name] = $('#' + i.name, '#ins').val();
																}
															}
														} else {
															if($('#' + i.name, '#ins').val() == ""){
																/*
																if(i.isCollection){
																	data[i.name] = [{}];
																}else{
																	data[i.name] = {};
																}*/
															}else{
															   if(i.fieldType){
																	if(i.fieldType == "enum"){
																	   data[i.name] = $('#' + i.name, '#ins').val();
																	}else{
																		data[i.name] = $.parseJSON($('#' + i.name, '#ins').val());
																	}
																}else{
																   if (i.isCollection) {
																      data[i.name] = Describe.splitValue($('#' + i.name, '#ins').val());
															       } else {
																         if (i.isCollection) {
																			   data[i.name] = Describe.splitValue($('#' + i.name, '#ins').val());
																		} else {
																			   data[i.name] = $.parseJSON($('#' + i.name, '#ins').val());
																		}
															       }
																}
															}
														
														}
													}
												});
											} else if (m.useJsonElementParameter) {
												data = $.parseJSON($('#jsonElement').val());
											} else if (m.useStringParameter) {
												data = $('#string').val();
											}
											Describe.doPut(address, m, data);
											break;
										case 'GET':
											Describe.doGet(address, m);
											break;
										case 'DELETE':
											Describe.doDelete(address, m);
											break;
										default:
											break;
										}
										
									} else {
										switch (m.type) {
										case 'POST':
											var formData = new FormData();
											if (m.formParameters && m.formParameters.length > 0) {
												$.each(m.formParameters, function(pi, p) {
													if (p.type == "File") {
														formData.append(p.name, $('input[type=file]', '#formParameters')[0].files[0]);
													} else {
														formData.append(p.name, $('#' + p.name, '#formParameters').val());
													}
												});
											}
											Describe.doPost(address, m, formData);
											break;
										case 'PUT':
											var formData = new FormData();
											if (m.formParameters && m.formParameters.length > 0) {
												$.each(m.formParameters, function(pi, p) {
													if (p.type == "File") {
														formData.append(p.name, $('input[type=file]', '#formParameters')[0].files[0]);
													} else {
														formData.append(p.name, $('#' + p.name, '#formParameters').val());
													}
												});
											}
											Describe.doPut(address, m, formData);
											break;
										case 'GET':
											Describe.doGet(address, m);
											break;
										case 'DELETE':
											Describe.doDelete(address, m);
											break;
										default:
											break;
										}
									}
								})
								
								debugger;
								$('#Sample').html("<div style=\"border-bottom:1px solid #E6E6E6;padding-bottom: 40px;line-height:21px\"><span style=\"font-size:17px;font-weight:bold;color: #1E7ACE;\">\n平台推荐脚本样例</span>\n\n"+ Describe.createSampleCommon(m,j.name)+ "</div><div  style=\"border-bottom:1px solid #E6E6E6;padding-bottom: 40px;line-height:21px\"><span style=\"font-size:17px;font-weight:bold;\">\n\n后台脚本样例</span>\n\n" + Describe.createSampleO2(m) + "</div><div  style=\"line-height:21px\"><span style=\"font-size:17px;font-weight:bold;\">\n\njquery样例</span>\n\n<span style=\"\">"+ Describe.createSample(m)+"</span></div>");
							});
				});
			});
		 
		  $("[xtype='menu']").click(
				  function(event) {
					    if(event.stopPropagation){
						    event.stopPropagation();
						  }else{
						     event.cancelBubble = true;
						  }
						$(this).children().each(function(i){
							debugger;
							if(this.tagName != "SPAN"){
							$(this).toggle();
							}
						});
					    //$(this).children().toggle();
					});
		  $("[xtype='li']").click( function(event) {
			    if(event.stopPropagation){
				    event.stopPropagation();
				  }else{
				     event.cancelBubble = true;
				  }
			})
			$("[xtype='menu']").each(function(i){ 
			if(i!=0){
			  // $(this).children().toggle();
			  $(this).children().each(function(i){
					
							if(this.tagName != "SPAN"){
							$(this).toggle();
							}
						});
			  }
			}
			);
		});
	},
  "search":function(strKey) {
	var str = '<ul>';
	var strTemp = "";
	    $.each(Describe.json.jaxrs, function(ji, j) {
			    var flag = false;
				strTemp = '<li xtype="menu" ' + 'style="margin-top: 30px;font-size:14px;font-weight:bold;"title="' +'" >' + j.name + ' <span style="font-style:italic">(' + j.description+ ')</span>';
				$.each(j.methods, function(mi, m) {
					if((m.name.toUpperCase().indexOf(strKey.toUpperCase())>-1) || (m.description.toUpperCase().indexOf(strKey.toUpperCase())>-1) || (m.path.toUpperCase().indexOf(strKey.toUpperCase())>-1)){
					flag = true;
					
					var tempKey =  strKey;
					var tempReplace = "<span style='color: #f31313'>"+ strKey + "</span>";
					 debugger;
					var strDescripthion = m.description.replace(tempKey, tempReplace);
					var strName = m.name.replace(tempKey, tempReplace);
					var strPath = m.path;
					var startPost = m.name.toUpperCase().indexOf(strKey.toUpperCase());
					if(startPost>-1){
						tempReplace = m.name.substr(startPost,tempKey.length);
						tempKey = tempReplace;
						tempReplace = "<span style='color: #f31313'>"+ tempReplace + "</span>";
						strName = m.name.replace(tempKey, tempReplace);
					}
					tempKey =  strKey;
					startPost = m.description.toUpperCase().indexOf(strKey.toUpperCase());
					if(startPost>-1){
						tempReplace = m.description.substr(startPost,tempKey.length);
						tempKey =  tempReplace;
						tempReplace = "<span style='color: #f31313'>"+ tempReplace + "</span>";
						strDescripthion = m.description.replace(tempKey, tempReplace);
					}
					
					
					strTemp += '<ul><li xtype="li"  style="margin-top: 10px;margin-left:-24px;font-size:12px; font-weight:normal;line-height:18px" ><a title = "' + strPath+ '"  id ="' + j.name + '_' + m.name + '" href="#"><b>' + strName+'</b><br/><span style="color: #666666;">-'+strDescripthion + '</span>' + '</a></li></ul>';
					}
				});
				strTemp += '</li>';
				
				if(flag == true){
				   str += strTemp;
				}
			});
			str += '</ul>';
			$("#menu").html(str);
			this.display(Describe.json);
  },
   "display":function(json) {
			$.each(json.jaxrs, function(ji, j) {
				$.each(j.methods, function(mi, m) {
					$('#' + j.name + '_' + m.name).click(
							function() {
								$('#result').html('');
								var sample = "";
								var txt = '<fieldset id="method"><legend>Method</legend>';
								txt += '<table>';
								txt += '<tr><td style="width:100px;">name:</td><td><a href="../describe/sources/' + m.className.replace(/\./g, '/') + '.java">' + m.name + '</a></td></tr>';
								txt += '<tr><td>path:</td><td>' + m.path + '</td></tr>';
								txt += '<tr><td>type:</td><td>' + m.type + '</td></tr>';
								txt += '<tr><td>description:</td><td>' + m.description + '</td></tr>';
								txt += '</table>';
								txt += '<button id="' + m.name + "_" + m.type + '">' + m.type + '</button>';
								txt += '<div id="url">&nbsp;</div>';
								txt += '</fieldset>';
								if (m.pathParameters && m.pathParameters.length > 0) {
									txt += '<fieldset id="pathParameters"><legend>Path Parameter</legend>';
									txt += '<table >';
									$.each(m.pathParameters, function(pi, p) {
										if (m.name == 'listNext' || m.name == 'listPrev') {
											switch (p.name) {
											case 'flag':
											case 'id':
												txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid" value="(0)"/></td><td>' + p.name
														+ ':' + p.description + '</td></tr>';
												break;
											case 'count':
												txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid" value="20"/></td><td>' + p.name + ':'
														+ p.description + '</td></tr>';
												break;
											default:
												txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + p.name + ':'
														+ p.description + '</td></tr>';
												break
											}
										} else {
											txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + p.name + ':'
													+ p.description + '</td></tr>';
										}
									});
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								if (m.formParameters && m.formParameters.length > 0) {
									txt += '<fieldset id="formParameters"><legend>Form Parameter</legend>';
									txt += '<table >';
									$.each(m.formParameters, function(pi, p) {
										if (p.type == "File") {
											txt += '<tr><td><input type="file" name="' + p.name + '" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>'
													+ p.name + ':' + p.description + '</td></tr>';
										} else {
											txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + p.name + ':'
													+ p.description + '</td></tr>';
										}
									});
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								if (m.queryParameters && m.queryParameters.length > 0) {
									txt += '<fieldset id="queryParameters"><legend>Query Parameter</legend>';
									txt += '<table >';
									$.each(m.queryParameters, function(pi, p) {
										txt += '<tr><td><input type="text" id="' + p.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + p.name + ':' + p.description
												+ '</td></tr>';
									});
									txt += '</table>';
									txt += '</fieldset>';
								}
								
							  if (m.ins && m.ins.length > 0) {
									txt += '<fieldset id="ins"><legend>In</legend>';
									txt += '<table>';
									$.each(m.ins, function(ii, i) {
										if (i.isCollection) {
											
											txt += '<tr><td><textarea id="' + i.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + i.name + ':' + i.description +(i.fieldValue ? "  "+'。数据格式：<span style="color:red">'+i.fieldValue +'</span>':"") + (i.fieldSample ? "  "+'<span style="color:red">'+i.fieldSample +'</span>':"") 
											'</td></tr>';
										} else {
											txt += '<tr><td><input type="text" id="' + i.name + '" style="width:600px; padding:1px; border:1px #000000 solid"/></td><td>' + i.name + ':'
													+ i.description
											'</td></tr>';
										}
									});
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								
								
								if (m.useJsonElementParameter) {
									txt += '<fieldset><legend>JsonElement</legend>';
									txt += '<table><tr><td>';
									txt += '<textarea id="jsonElement" style="height:300px; width:600px; padding:1px; border:1px #000000 solid"/>';
									txt += '</td><td>json</td></tr>';
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								if (m.useStringParameter) {
									txt += '<fieldset><legend>String</legend>';
									txt += '<table><tr><td>';
									txt += '<textarea id="string" style="height:300px; width:600px; padding:1px; border:1px #000000 solid"/>';
									txt += '</td><td>string</td></tr>';
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								if (m.outs && m.outs.length > 0) {
									txt += '<fieldset id="outs"><legend>Out</legend>';
									txt += '<table>';
									$.each(m.outs, function(oi, o) {
										txt += '<tr><td style="width: 160px;">' + o.name + '</td><td style="width: 90px;">' + o.type + '</td><td style="width: 90px;">' + (o.isCollection ? 'multi' : 'single') + '</td><td style="width: 90px;">' + o.description + '</td><td id="out_'
												+ o.name + '_out">&nbsp;</td></tr>';
									});
									txt += '</table>';
									txt += '</fieldset>';
								}
								
								$('#content').html(txt);
								
								$('#' + m.name + '_' + m.type, '#method').click(function() {
									var address = '../' + m.path;
									if (m.pathParameters && m.pathParameters.length > 0) {
										$.each(m.pathParameters, function(pi, p) {
											address = address.replace('{' + p.name + '}', encodeURIComponent($('#' + p.name, '#pathParameters').val()));
										});
									}
									if (m.queryParameters && m.queryParameters.length > 0) {
										$.each(m.queryParameters, function(pi, p) {
											var query = p.name + '=' + encodeURIComponent($('#' + p.name, '#queryParameters').val());
											if (address.indexOf("?") > 0) {
												address += '&' + query;
											} else {
												address += '?' + query;
											}
										});
									}
									if (m.contentType.indexOf('application/json') > -1) {
										switch (m.type) {
										case 'POST':
											var data = {};
											if (m.ins && m.ins.length > 0) {
												$.each(m.ins, function(ii, i) {
													switch (i.type) {
													default:
														if (i.isBaseType) {
															if($('#' + i.name, '#ins').val() != ""){
															if (i.isCollection) {
																data[i.name] = Describe.splitValue($('#' + i.name, '#ins').val());
															} else {
																data[i.name] = $('#' + i.name, '#ins').val();
															}
															}
														} else {
															if($('#' + i.name, '#ins').val() == ""){
																/*if(i.isCollection){
																	data[i.name] = [{}];
																}else{
																	data[i.name] = {};
																}*/
															}else{
																if(i.fieldType){
																	if(i.fieldType == "enum"){
																	   data[i.name] = $('#' + i.name, '#ins').val();
																	}else{
																		data[i.name] = $.parseJSON($('#' + i.name, '#ins').val());
																	}
																}else{
																   data[i.name] = $.parseJSON($('#' + i.name, '#ins').val());
																}
															}
														}
													}
												});
											} else if (m.useJsonElementParameter) {
												data = $.parseJSON($('#jsonElement').val());
											} else if (m.useStringParameter) {
												data = $('#string').val();
											}
											Describe.doPost(address, m, data);
											break;
										case 'PUT':
											var data = {};
											if (m.ins && m.ins.length > 0) {
												$.each(m.ins, function(ii, i) {
													switch (i.type) {
													default:
														if (i.isBaseType) {
															if($('#' + i.name, '#ins').val() != ""){
															if (i.isCollection) {
																data[i.name] = Describe.splitValue($('#' + i.name, '#ins').val());
															} else {
																data[i.name] = $('#' + i.name, '#ins').val();
															}
															}
														} else {
															if($('#' + i.name, '#ins').val() == ""){
																/*if(i.isCollection){
																	data[i.name] = [{}];
																}else{
																	data[i.name] = {};
																}*/
															}else{
																if(i.fieldType){
																	if(i.fieldType == "enum"){
																	   data[i.name] = $('#' + i.name, '#ins').val();
																	}else{
																		data[i.name] = $.parseJSON($('#' + i.name, '#ins').val());
																	}
																}else{
																   data[i.name] = $.parseJSON($('#' + i.name, '#ins').val());
																}
															}
														
														}
													}
												});
											} else if (m.useJsonElementParameter) {
												data = $.parseJSON($('#jsonElement').val());
											} else if (m.useStringParameter) {
												data = $('#string').val();
											}
											Describe.doPut(address, m, data);
											break;
										case 'GET':
											Describe.doGet(address, m);
											break;
										case 'DELETE':
											Describe.doDelete(address, m);
											break;
										default:
											break;
										}
										
									} else {
										switch (m.type) {
										case 'POST':
											var formData = new FormData();
											if (m.formParameters && m.formParameters.length > 0) {
												$.each(m.formParameters, function(pi, p) {
													if (p.type == "File") {
														formData.append(p.name, $('input[type=file]', '#formParameters')[0].files[0]);
													} else {
														formData.append(p.name, $('#' + p.name, '#formParameters').val());
													}
												});
											}
											Describe.doPost(address, m, formData);
											break;
										case 'PUT':
											var formData = new FormData();
											if (m.formParameters && m.formParameters.length > 0) {
												$.each(m.formParameters, function(pi, p) {
													if (p.type == "File") {
														formData.append(p.name, $('input[type=file]', '#formParameters')[0].files[0]);
													} else {
														formData.append(p.name, $('#' + p.name, '#formParameters').val());
													}
												});
											}
											Describe.doPut(address, m, formData);
											break;
										case 'GET':
											Describe.doGet(address, m);
											break;
										case 'DELETE':
											Describe.doDelete(address, m);
											break;
										default:
											break;
										}
									}
								})
								
								debugger;
								$('#Sample').html("<div style=\"border-bottom:1px solid #E6E6E6;padding-bottom: 40px;line-height:21px\"><span style=\"font-size:17px;font-weight:bold;color: #1E7ACE;\">\n平台推荐脚本样例</span>\n\n"+ Describe.createSampleCommon(m,j.name)+ "</div><div  style=\"border-bottom:1px solid #E6E6E6;padding-bottom: 40px;line-height:21px\"><span style=\"font-size:17px;font-weight:bold;\">\n\n后台脚本样例</span>\n\n" + Describe.createSampleO2(m) + "</div><div  style=\"line-height:21px\"><span style=\"font-size:17px;font-weight:bold;\">\n\njquery样例</span>\n\n<span style=\"\">"+ Describe.createSample(m)+"</span></div>");
							});
				});
			});
		 
		  $("[xtype='menu']").click(
				  function(event) {
					    if(event.stopPropagation){
						    event.stopPropagation();
						  }else{
						     event.cancelBubble = true;
						  }
						$(this).children().each(function(i){
							debugger;
							if(this.tagName != "SPAN"){
							$(this).toggle();
							}
						});
					});
		  $("[xtype='li']").click( function(event) {
			    if(event.stopPropagation){
				    event.stopPropagation();
				  }else{
				     event.cancelBubble = true;
				  }
			})
	}
}