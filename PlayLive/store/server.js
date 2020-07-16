var server = require('http').createServer(),
    io = require("socket.io").listen(server);

users = [];
connections = [];

server.listen(process.env.PORT || 3000);

console.log('Server running...');

/*app.get('/', function (req, res) {
    res.sendFile(__dirname + '/index.html');
});*/

io.sockets.on('connection', function (socket) {
   connections.push(socket);
   console.log('Connected: %s sockets connected', connections.length);

   socket.on('broadcast', function (message) {
      console.log(JSON.stringify(message));
   });

   socket.on('disconnect', function () {
       connections.splice(connections.indexOf(socket), 1);
       console.log('Disconnected: %s sockets connected', connections.length);
   });
});