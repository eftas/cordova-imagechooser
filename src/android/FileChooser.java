package com.megster.cordova;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import java.lang.Exception;
import java.net.MalformedURLException;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.file.FileUtils;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import org.json.JSONException;

public class FileChooser extends CordovaPlugin {

    private static final String TAG = "FileChooser";
    private static final String ACTION_OPEN = "open";
    private static final int PICK_FILE_REQUEST = 1;
    private CordovaWebView webView;
    CallbackContext callback;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);
      this.webView = webView;
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
      this.callback = callbackContext;
      if (action.equals(ACTION_OPEN)) {
          chooseFile();
          return true;
      }

      if (action.equals("getFileSystemPath")){
        getLocalFilePathForURL(args.getString(0));
        return true;
      }
      return false;
    }

    public void getLocalFilePathForURL(String url){
      FileUtils filePlugin = (FileUtils) webView.getPluginManager().getPlugin("File");
      try {
        String localPath = filePlugin.filesystemPathForURL(url);
        callback.success(localPath);
      }catch(MalformedURLException e){
        callback.error(e.toString());
      }catch (Exception e){
        callback.error(e.toString());
      }
    }

    public void chooseFile() {
        // type and title should be configurable
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        Intent chooser = Intent.createChooser(intent, "Select File");
        cordova.startActivityForResult(this, chooser, PICK_FILE_REQUEST);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callback.sendPluginResult(pluginResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST && callback != null) {
            if (resultCode == Activity.RESULT_OK) {
              Uri uri = data.getData();
              callback.success(uri.toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // TODO NO_RESULT or error callback?
                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                callback.sendPluginResult(pluginResult);
            } else {
                callback.error(resultCode);
            }
        }
    }
}
