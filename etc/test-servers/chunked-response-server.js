var http = require('http');
var config = require('./server-config.json');
var port = config['ports']['chunked-response-server'];

http.createServer(function (req, res) {
    var chunk = '1234567890';
    var min_chunks = 10;
    var max_chunks = 100;

    var num_chunks = Math.floor(Math.random() * (max_chunks - min_chunks + 1)) + min_chunks;
    for (var i = 0; i < num_chunks; i++) {
      res.write(chunk);
    }
    res.end();

}).listen(port, '127.0.0.1');
console.log('Started server on port ' + port);
