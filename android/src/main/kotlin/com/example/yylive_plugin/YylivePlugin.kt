package com.example.yylive_plugin

import android.app.Activity
import android.util.Log
import android.widget.TextView
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class YylivePlugin : FlutterPlugin, MethodCallHandler {
  private var mChannelMethod: MethodChannel? = null

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    if ("getPlatformVersion" == call.method) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }
  }

  override fun onAttachedToEngine(binding: FlutterPluginBinding) {
    mChannelMethod = MethodChannel(binding.binaryMessenger, "yylive_plugin_method")
    mChannelMethod!!.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
  }
}