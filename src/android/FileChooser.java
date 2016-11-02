package com.megster.cordova;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import java.lang.Exception;

import org.apache.cordova.file.ContentFilesystem;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

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
      return false;
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
        callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST && callback != null) {
            if (resultCode == Activity.RESULT_OK) {
              try{
                Uri uri = data.getData();
                ContentFilesystem cf = new ContentFilesystem(this.webView.getContext(), this.webView.getResourceApi());
                JSONObject res = cf.makeEntryForNativeUri(uri);
                callback.success(res);
              }catch(Exception e){
                callback.error(resultCode);
              }
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
