function V(httpRequest) {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            alert(httpRequest.responseText);
        } else {
            alert('There was a problem with the request.');
        }
    }
}

(function(){
    var _worker = this;
    var _action = {
        _checkRequest: function(){
            if (this.request.readyState === XMLHttpRequest.DONE) {
                if (this.request.status === 200) {
                    this._doneRequest();
                } else {
                    this._errorRequest();
                }
            }
        },
        _createRequest: function(){
            this.request = new XMLHttpRequest();
            this.request.addEventListener("readystatechange", this._checkRequest.bind(this));
        },
        sendRequest: function(data){
            if (!this.request) this._createRequest();
            var method = data.method;
            var noCache = !!data.noCache;
            var async = !!data.loadAsync;
            var withCredentials = !!data.credentials;
            var url = data.address;
            var body = data.body;
            var debug = data.debug;
            var token = data.token;
            var tokenName = data.tokenName;

            if (noCache) url = url+(((url.indexOf("?")!==-1) ? "&" : "?")+(new Date()).getTime());

            this.request.open(method, url, async);

            this.request.withCredentials = withCredentials;
            this.request.setRequestHeader("Content-Type", "application/json; charset=utf-8");
            this.request.setRequestHeader("Accept", "text/html,application/json,*/*");
            if (debug) this.request.setRequestHeader("x-debugger", "true");
            if (token){
                this.request.setRequestHeader(tokenName, token);
                this.request.setRequestHeader("Authorization", token);
            }

            this.request.send(body);
        },

        _doneRequest: function(){
            var json = JSON.parse(this.request.responseText);
            var xToken = this.request.getResponseHeader(o2.tokenName);
            if (xToken){
                json.xToken = xToken;
            }
            _worker.postMessage({"type": "done", "data": json});
            _worker.close();
        },
        _errorRequest: function(){
            _worker.postMessage({"type":"error", "data": {"status":  this.request.status, "statusText":  this.request.statusText, "responseText":this.request.responseText}});
            _worker.close();
        }
    };

    this.action = _action;
})();

onmessage = function(e) {
    this.action.sendRequest(e.data);
    //
    //
    // console.log('Worker: Message received from main script');
    // var options = e.data;
    //
    // httpRequest = new XMLHttpRequest();
    //
    //
    // if (isNaN(result)) {
    //     postMessage('Please write two numbers');
    // } else {
    //     const workerResult = 'Result: ' + result;
    //     console.log('Worker: Posting message back to main script');
    //     postMessage(workerResult);
    // }
}
