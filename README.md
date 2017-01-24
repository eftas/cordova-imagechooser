Cordova ImageChooser Plugin

Requires Cordova >= 2.8.0

Install with Cordova CLI
	
	$ cordova plugin add https://github.com/eftas/cordova-imagechooser.git

Install with Plugman 

	$ plugman --platform android --project /path/to/project \ 
		--plugin https://github.com/eftas/cordova-imagechooser.git

API

	fileChooser.open(successCallback, failureCallback);

The success callback get the uri of the selected file

	fileChooser.open(function(uri) {
		alert(uri);
	});
	
Screenshot

![Screenshot](filechooser.png "Screenshot")

TODO rename `open` to pick, select, or choose.
