module.exports = {
    open: function (success, failure) {
      var mySuccess = function(result){
        if (success){
          var fsObj = result.fs;
          fileSystems.getFs(fsObj.name, function(fs){
            if (!fs) {
              fs = new FileSystem(file_system.name, file_system.root);
            }
            var entry = new FileEntry(result.name, result.fullPath, fs, result.nativeURL);
            success(entry);
          });
        }
      };

      var myFailure = function(err){
        if (failure) failure(err);
      }
      cordova.exec(mySuccess, myFailure, "FileChooser", "open", []);
    }
};
