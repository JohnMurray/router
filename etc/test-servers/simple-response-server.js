var http = require('http');
var config = require('./server-config.json');
var port = config['ports']['simple-response-server'];

http.createServer(function (req, res) {
    var body = '1234567890';
    res.writeHead(200, {'Content-Type': 'text/plain', 'Content-Length': body.length});
    res.end(body);
}).listen(port, '127.0.0.1');
console.log('Started server on port ' + port);
