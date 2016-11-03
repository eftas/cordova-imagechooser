module.exports = {
    open: function (success, failure) {
      var mySuccess = function(result){
        if (success){
          resolveLocalFileSystemURL(result, function(entry){
            success(entry);
          }, function(err){
            if(failure) failure(err);
          });
        }
      };

      var myFailure = function(err){
        if (failure) failure(err);
      }
      cordova.exec(mySuccess, myFailure, "FileChooser", "open", []);
    },

    getFileSystemPath: function(path, success, failure) {
      cordova.exec(success, failure, "FileChooser", "getFileSystemPath" [path]);
    }
};
