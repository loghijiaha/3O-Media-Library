const sqlite3 = require('sqlite3').verbose();
var jwt=require('jsonwebtoken');

var existUsers=[]
let db = new sqlite3.Database('./db/3o-media.db', (err) => {
  if (err) {
    return console.error(err.message);
  }
  console.log('Connected to the  SQlite database.');
});
let sql = `SELECT user_name FROM Users
           ORDER BY user_name`;
db.all(sql, [], (err, rows) => {
  if (err) {
    throw err;
  }
  rows.forEach((row) => {
    existUsers.push(row.user_name)
    console.log(row);
  });
});
function checkAuth(req,callback) {
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

exports.register = function(req,res){
  
  // console.log("req",req.body);
  checkAuth(req,function(response){
    if(response){
      var today = new Date();
    var users=[
    req.body.first_name,
    req.body.user_name,
    req.body.password,
    req.body.user_type,
    today,
    today
  ]
  console.log(users);
  console.log(existUsers);
  console.log(req.body.user_name);

  if(!existUsers.includes(req.body.user_name))
  {

    console.log(users);
    db.run('INSERT INTO Users(first_name,user_name,password,user_type,created,modified) VALUES(?,?,?,?,?,?)',users, function (error, results, fields) {
      if (error) {
        console.log("error ocurred",error);
        res.send({
          "code":400,
          "failed":"error ocurred"
        });
      }else{
        console.log('The solution is: ', results);
        res.send({
          "code":200,
          success :"user registered sucessfully"
            });
      }
      });
    }else{
      res.status(401).json({
              error:'0'
              });
    }
  }else{
      res.status(402).json({
              error:'1'
              });
    }
  
});
}
