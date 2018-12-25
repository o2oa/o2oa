ws_parameter = {
    webSocket : null
};
function ws_connect() {
    $('#result').html('');
    var str = '<table border="1" width="100%">';
    str += '<tr><td colspan="2"><a href="#" id="send">send</a>&nbsp;<a href="#" id="close">close</a></td></tr>';
    str += '<tr><td>message:</td><td><input type="text" id="message" style="width:95%"/></td></tr>';
    str += '</table>';
    $('#content').html(str);
    $('#send').click(function() {
	ws_send($('#message').val());
    });
    var url = location.href;
    url = url.substring(url.indexOf('://'));
    url = url.substring(0, url.indexOf('x_collaboration_assemble_websocket/'));
    url = 'ws' + url + 'x_collaboration_assemble_websocket/ws/collaboration';
    url = url + '?x-token=' + getCookie("x-token");
    ws_parameter.webSocket = new WebSocket(url);
    ws_parameter.webSocket.onopen = function(evt) {
	ws_onOpen(evt)
    };
    ws_parameter.webSocket.onclose = function(evt) {
	ws_onClose(evt)
    };
    ws_parameter.webSocket.onmessage = function(evt) {
	ws_onMessage(evt)
    };
    ws_parameter.webSocket.onerror = function(evt) {
	ws_onError(evt)
    };
}

function ws_send(messagae) {
    ws_parameter.webSocket.send(message);
}

function ws_onOpen(evt) {

}

function ws_onClose(evt) {

}

function ws_onMessage(evt) {
    $('#result').append(evt.data);
}

function ws_onError(evt) {
    $('#result').append(evt.data);
}
