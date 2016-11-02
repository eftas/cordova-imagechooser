module.exports = {
    open: function (success, failure) {
      var mySuccess = function(result){
        var entry = new FileEntry(result.name, result.fullPath, fs, result.nativeURL);
        if (success) success(entry);
      };

      var myFailure = function(err){
        if (failure) failure(err);
      }
      cordova.exec(mySuccess, myFailure, "FileChooser", "open", []);
    }
};
