package com.megster.cordova;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Base64;
import android.content.Context;
import android.content.ContentResolver;
import java.io.FileReader;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.lang.Exception;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class FileChooser extends CordovaPlugin {

    private static final String TAG = "FileChooser";
    private static final String ACTION_OPEN = "open";
    private static final int PICK_FILE_REQUEST = 1;
    CallbackContext callback;

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION_OPEN)) {
            chooseFile(callbackContext);
            return true;
        }

        return false;
    }

    public void chooseFile(CallbackContext callbackContext) {

        // type and title should be configurable

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        Intent chooser = Intent.createChooser(intent, "Select File");
        cordova.startActivityForResult(this, chooser, PICK_FILE_REQUEST);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST && callback != null) {
            if (resultCode == Activity.RESULT_OK) {
              try{
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Context context= this.cordova.getActivity().getApplicationContext();
                ContentResolver cr = context.getContentResolver();
                Uri uri = data.getData();
                String mimeType = cr.getType(uri);
                File file = new File(uri);
                FileReader reader = new FileReader(file);
                final int BUFFER_SIZE = 8192;
                int offset = 0;
                int chunk = 8192;
                byte[] buffer = new byte[BUFFER_SIZE];
                for(;;){
                  int bytesRead = reader.read(buffer, offset, chunk);
                  if (bytesRead <= 0) {
                    break;
                  }
                  os.write(buffer, 0, bytesRead);
                  offset = chunk;
                  chunk += bytesRead;
                }

                byte[] base64 = Base64.encode(os.toByteArray(), Base64.NO_WRAP);
                String s = "data:" + mimeType + ";base64," + new String(base64, "US-ASCII");
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("filename", file.getName());
                jsonObj.put("data", s);
                callback.success(jsonObj);
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
