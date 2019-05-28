var express = require('express');
var app=express();
var bodyParser= require('body-parser');
var jwt=require('jsonwebtoken');
const cookieParser = require('cookie-parser');
const session = require('express-session');
const sqlite3 = require('sqlite3').verbose();
var register = require('./routes/registerRoutes');
var audio = require('./routes/audioRoutes');
var video = require('./routes/videoRoutes');
var ebook = require('./routes/ebookRoutes');
var enabled=true;
var fileSystem = require('fs')
var path = require('path');
var home = path.join(__dirname, '/index.html');

let db = new sqlite3.Database('./db/3o-media.db', (err) => {
  if (err) {
    return console.error(err.message);
  }
  console.log('Connected to the  SQlite database.');
});
var users=[]
let sql = `SELECT DISTINCT  user_name,password FROM Users
           ORDER BY user_name`;
           
checkAuth=function checkAuth(req,callback) {
  var token = req.body.token || req.query.token || req.headers['x-access-token'];
    if(token){
      //Decode the token
      jwt.verify(token,"HarlyAndJoker",(err,decod)=>{
        if(err){
          return callback(0);
        }
        else{
          //If decoded then call next() so that respective route is called.
          req.decoded=decod;
          return callback(1);
        }
      });
    }
    else{

      return callback(0);
    }

}
db.all(sql, [], (err, rows) => {
  if (err) {
    throw err;
  }
  rows.forEach((row) => {
    users.push(row)
  });
});

app.use( bodyParser.json() );
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(session({
  secret: 'HarlyAndJoker',
  resave: true,
  saveUninitialized: true
}));

app.use(express.static('./'));
app.get('/auth', (req, res) => {
  checkAuth(req,function(response){
    console.log('Entered Successfully', response);
  });
});

const MESSAGES = {
 TOKEN_NOT_FOUND: 'Valid tokens not present or Access not enabled ',
 TOKEN_EXPIRES: 'Download token expired'};

// Middlware for download Verifier
const JWTDownloadVerifier = async (req, res, next) => {
  try {
    const token = req.body.token;
    if (!token || !enabled) {
      return res.status(400).json({ message: MESSAGES.TOKEN_NOT_FOUND });
    }
    const secret = 'HarlyAndJoker';
    jwt.verify(token, secret);
  } catch (err) {
    return res.status(403).json({ message: MESSAGES.TOKEN_EXPIRES });
  }
  return next();};

app.post('/playAudio', JWTDownloadVerifier, (req, res, next)  =>{
    var filePath = path.join(__dirname, 'Music/'+req.body.path);
    console.log(req.body)
    var stat = fileSystem.statSync(filePath);
    res.writeHead(200, {
        'Content-Type': 'audio/mpeg',
        'Content-Length': stat.size
    });
    console.log("Playing audio");
    var readStream = fileSystem.createReadStream(filePath);
    // replaced all the event handlers with a simple call to readStream.pipe()
    readStream.pipe(res);
});
app.post('/playVideo', JWTDownloadVerifier, (req, res, next)  =>{
    var filePath = path.join(__dirname, 'Video/'+req.body.path);
    var stat = fileSystem.statSync(filePath);
    res.writeHead(200, {
        'Content-Type': 'video/mp4'|'video/3gpp'|'video/quicktime',
        'Content-Length': stat.size
    });
    console.log("Playing video");

    var readStream = fileSystem.createReadStream(filePath);
    // replaced all the event handlers with a simple call to readStream.pipe()
    readStream.pipe(res);
});
app.post('/playEbook', JWTDownloadVerifier, (req, res, next)  =>{
    var filePath = path.join(__dirname, 'Ebook/'+req.body.path);
    var stat = fileSystem.statSync(filePath);
    res.writeHead(200, {
        'Content-Type': 'application/pdf',
        'Content-Length': stat.size
    });
    var readStream = fileSystem.createReadStream(filePath);
    // replaced all the event handlers with a simple call to readStream.pipe()
    readStream.pipe(res);
});

app.use('/home', express.static(path.join(__dirname, '/index.html')))
app.use('/addMedia', express.static(path.join(__dirname, '/charts.html')))

app.use('/remoteStreaming', express.static(path.join(__dirname, '/remote.html')))

app.get('/getIp', (req, res)=>{
  checkAuth(req,function(response){
    var message;
    if(response){
      var os = require('os');
      var ifaces = os.networkInterfaces();
      Object.keys(ifaces).forEach(function (ifname) {
        var alias = 0;

        ifaces[ifname].forEach(function (iface) {
          if ('IPv4' !== iface.family || iface.internal !== false) {
            // skip over internal (i.e. 127.0.0.1) and non-ipv4 addresses
            return;
          }
          var ip = iface.address;

          if (alias >= 1) {
            // this single interface has multiple ipv4 addresses
            console.log(ifname + ':' + alias, iface.address);
            // res.status(200).json({
            //     ip       
            // });
          } else {
            // this interface has only one ipv4 adress
            // res.status(200).json({
            //     ifname,
            //     ip
            // });
            console.log(ifname, iface.address);
          }
          ++alias;
          res.status(200).json({
                ip       
            });
        });
      });
    }else{
      console.log('error');
      res.status(403).json({
      message:"Access denied"
      });
    }
  });
});
app.post('/register',register.register);
app.post('/logout', (req, res)=>{
            console.log('Logout success')

  req.session.destroy(function(err){  
        if(err){  
            res.status(403);  
        }  
        else  
        {  
          console.log('Logout success')
          res.status(200);
        }  
    }); 
});
app.post('/login',(req,res)=>{
    var message;
    console.log(req.body.user_name,req.body.password);
    for(var user of users){
      if(user.user_name!=req.body.user_name){
          message="Wrong Name";
          res.status(403).json({
              message
              });
              break;


      }else{
          if(user.password!=req.body.password){
              message="Wrong Password";
              res.status(403).json({
              message
              });
              
              break;
          }
          else{
            //create the token.
              var token=jwt.sign(user,"HarlyAndJoker");
              message="Login Successful";

              // res.redirect('/remote?token='+token);
              res.status(200).json({
              message,
              token
              });

        
              break;  
          }
      }
    }
});

app.use('/remote',(req, res, next)=>{
  // check header or url parameters or post parameters for token
  var check=checkAuth(req,function(response){
    if(response){
      res.redirect('/remoteStreaming');
    }else{
      res.status(403).json({
        message:"Error"
      });
    }
  });
});
app.post('/setStreaming',(req,res)=>{
  console.log('coming state',req.body.chkbox_value);
  if(req.body.chkbox_value==true){
    enabled=true;
    console.log('Remote enabled');
  }else{console.log(videoList);
    enabled=false;
    console.log('Remote disabled');

  }
});
app.post('/addAudio',audio.addAudio);
app.post('/addVideo',video.addVideo);
app.post('/addEbook',ebook.addEbook);

app.get('/getAllAudio',audio.getAllAudio);
app.get('/getAllVideo',video.getAllVideo);
app.get('/getAllEbook',ebook.getAllEbook);
app.get('/getAllAuthor',function(req,res){
  db.all('SELECT * FROM Artist ',[], function (error, rows) {
  if (error) {
    console.log("error ocurred",error);
    res.send({
      "code":400,
      "failed":"error ocurred"
    })
  }else{
    console.log('The solution is: ', rows);
    var file_list=[];
    rows.forEach((row) => {
      file_list.push(row)
      console.log(row);
    });
    
  res.send(JSON.stringify({file_list:file_list}));
  }
  });
})
app.listen(8012, function(){
  console.log('listening on port 8012');
});