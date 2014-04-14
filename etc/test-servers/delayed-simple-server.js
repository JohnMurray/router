/*
 * This server will take a random amount of time before
 * responding with a simple response. Since this service
 * takes so long to respond, it could easily cause a large
 * number of connection to build up on the router.
 */
var http = require('http');
var config = require('./server-config.json');
var port = config['ports']['delayed-simple-server'];

http.createServer(function (req, res) {
    var min = 60000;
    var max = 10000;
    var randomPause = Math.floor(Math.random() * (max - min + 1)) + min;
    console.log("delayed for " + randomPause + "ms");

    setTimeout(function(){
      res.writeHead(200, {'Content-Type': 'text/plain', 'Content-Length': "1234567890".length});
      res.end('1234567890');
    }, randomPause);

}).listen(port, '127.0.0.1');
console.log('Started server on port ' + port);
