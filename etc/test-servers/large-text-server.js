var http = require('http');
var config = require('./server-config.json');
var port = config['ports']['simple-large-response-server'];

http.createServer(function (req, res) {
    var body = '';
    var text = "1234567890";
    var min = 500;
    var max = 5000;
    var randomLimit = Math.floor(Math.random() * (max - min + 1)) + min;
    for(var i = 0; i < randomLimit; i += text.length) {
	   body += text;
    }
    res.writeHead(200, {'Content-Type': 'text/plain', 'Content-Length': body.length});
    res.end(body);

}).listen(port, '127.0.0.1');
console.log('Started server on port ' + port);
