var express = require('express');
var tmp = require('tmp');
var bodyParser = require('body-parser');
var fs = require('fs');
var child = require('child_process');
var app = express();
var gm = require('gm').subClass({imageMagick: true});



// Web Server - Done
// 1. Phone takes image
// 2. Phone uploads image to server
// 3. Server saves to file
// 4. Server runs guess.sh on the file
// 5. Server generates new image with Wiener or Not Wiener on top
// 6. Server sends edited image back to phone


function getTempImageFile(callback) {
    var options = {
        prefix: "wiener",
        postfix: ".jpg",
        dir: "user_wieners"
    };
    tmp.file(options, callback);
}

function saveRequestImage(fileFD, req, callback) {
    console.log(req.body);
    fs.write(fileFD, req.body, callback);
}

function sendResponseImage(file, res, callback) {
    var options = {
        root: "./"
    };
    res.sendFile(file, options, callback);
}

function testFileForWiener(file, callback) {
    var options = {
        cwd: "wiener",
        stdio: ['ignore', 'pipe', process.stderr]
    };
    guess = child.spawn('/bin/bash', ['guess.sh', "../" + file], options);

    var output = ""
    guess.stdio[1].on('data', function(chunk) {
        output += chunk;
    });
    guess.stdio[1].on('end', function() {
        console.log(output);
        callback(null, output.trim() === "Wiener");
    });

}

function generateWienerImage(file, isWiener, callback) {
    var overlayFile;
    if (isWiener) {
        overlayFile = "overlays/wiener.png";
    } else {
        overlayFile = "overlays/notwiener.png";
    }
    getTempImageFile(function(err, wienerFile, wienerFD) {
        if (err) {
            callback(err);
            return;
        }
        fs.close(wienerFD, function(err) {
            if (err) {
                callback(err);
                return;
            }
            gm(file)
                .composite(overlayFile)
                .gravity("center")
                .geometry("XwH")
                .write(wienerFile, function(err) {
                    callback(err, wienerFile);
                });
        });
    });}

function pretendErrorDoesntExist(err) {
    if (err) {
        console.log("ERR: " + err);
    }
}

app.post('/testwiener', bodyParser.raw({ limit: '10mb' }), function(req, res) {
    getTempImageFile(function(err, tempImage, tempImageFD) {
        pretendErrorDoesntExist(err);
        saveRequestImage(tempImageFD, req, function(err) {
            pretendErrorDoesntExist(err);
            testFileForWiener(tempImage, function(err, isWiener) {
            pretendErrorDoesntExist(err);
                generateWienerImage(tempImage, isWiener, function(err, wienerImage) {
                    pretendErrorDoesntExist(err);
                    sendResponseImage(wienerImage, res, function(err) {
                        pretendErrorDoesntExist(err);
                        console.log("We sent back an image!");
                        if (isWiener) {
                            console.log("It was a wiener.");
                        } else {
                            console.log("It was not a wiener.");
                        }
                    });
                });
            });
        });
    });
});


var server = app.listen(80, function() {
	var host = server.address().address
	var port = server.address().port

	console.log("Example app listening at http://%s:%s", host, port)
})
