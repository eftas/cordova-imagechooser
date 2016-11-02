module.exports = {
    open: function (success, failure) {
      var mySuccess = function(result){
        if (success){
          var fsObj = result.fs;
          requestFileSystem(2, 0, function(fs){
            var entry = new FileEntry(result.name, result.fullPath, fs, result.nativeURL);
            success(entry);
          }, function(err){
            if (failure) failure(err);
          });
        }
      };

      var myFailure = function(err){
        if (failure) failure(err);
      }
      cordova.exec(mySuccess, myFailure, "FileChooser", "open", []);
    }
};
