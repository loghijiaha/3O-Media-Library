const sqlite3 = require('sqlite3').verbose();
const NodeID3 = require('node-id3');

var path = require('path');
var formidable = require('formidable');
var fs = require('fs');
let db = new sqlite3.Database('./db/3o-media.db', (err) => {
  if (err) {
    return console.error(err.message);
  }
  console.log('Connected to the  SQlite database.');
});
exports.addVideo = function(req,res){
  // console.log("req",req.body

  var form = new formidable.IncomingForm();
  form.parse(req, function (err, fields, files) {
    if(files.file.name){
        var oldpath = files.file.path;

    var newpath = path.join(__dirname, '../Video/Video'+files.file.name);   
    console.log(oldpath,newpath);
    NodeID3.read(oldpath, function(err, tags) {
      console.log(tags);
      if(err){
        res.send("<script type='text/javascript'> alert('Something wrong in metadata');location.href = 'http://localhost:8012/addMedia'</script>");
      }
      var metadata=[
        tags.title,
        tags.artist ,
        tags.image ? tags.image.imageBuffer: null ,
        files.file.name
      ]

      console.log(metadata);
      db.all('SELECT * FROM Video',[],function(error,rows,fields){
        var nameL=[]
        rows.forEach((row)=>{
          nameL.push(row.path)
        });
        console.log(nameL);
        if(error){
          console.log("error ocurred",error);
      res.send("<script type='text/javascript'> alert('No file selected');location.href = 'http://localhost:8012/addMedia'</script>");
        }else if(nameL.includes(files.file.name)){
          res.send("<script type='text/javascript'> alert('Already exists');location.href = 'http://localhost:8012/addMedia'</script>");
        }else{
          db.run('INSERT INTO Video(name,artist_name,image,path) VALUES(?,?,?,?)',metadata, function (error, results, fields) {
            if (error) {
              console.log("error ocurred",error);
              res.send("<script type='text/javascript'> alert('Adding Failed');location.href = 'http://localhost:8012/addMedia'</script>");
            }else{
              fs.createReadStream(oldpath).pipe(fs.createWriteStream(newpath));
              console.log('The solution is: ', results);
              res.send("<script type='text/javascript'> alert('Added Succesfully');location.href = 'http://localhost:8012/addMedia'</script>");
            }

        });
        }
      });
      
      

    });
    }else{
      res.send("<script type='text/javascript'> alert('No file selected');location.href = 'http://localhost:8012/addMedia'</script>");
    }
    
  });
    // var filePath = path.join(__dirname, '../',oldpath);
  // console.log(filePath);
  
 }  

exports.getAllVideo = function(req,res){
  db.all('SELECT * FROM Video ',[], function (error, rows) {
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
    
   res.status(200).send({
              file_list
              });
  }
  });
}