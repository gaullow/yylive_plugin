import 'package:flutter/services.dart';
import 'package:flutter/material.dart';

import 'nativeView.dart';

class YylivePlugin {
  static const MethodChannel methodChannel =
      MethodChannel('yylive_plugin_method');

  static Future<String?> get platformVersion async {
    final String? version =
        await methodChannel.invokeMethod('getPlatformVersion');
    return version;
  }

  static void init(int id) {
    setText("默认值");
  }

  static Future<void> setText(String text) async {
    return methodChannel.invokeMethod('setText', text);
  }

  static Widget nativeView(dynamic creationParams,
      {double? width, height, int? duration, ValueChanged<String>? onOver}) {
    return NativeView(
        creationParams: creationParams,
        width: width,
        height: width,
        duration: duration,
        onOver: onOver);
  }
}
