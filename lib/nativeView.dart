import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class NativeView extends StatefulWidget {
  final double? width, height;
  final dynamic creationParams;
  final int? duration;
  final ValueChanged<String>? onOver;

  const NativeView({
    Key? key,
    this.width = 200,
    this.height = 200,
    required this.duration,
    required this.onOver,
    this.creationParams,
  }) : super(key: key);

  @override
  NativeViewState createState() => NativeViewState();
}

class NativeViewState extends State<NativeView> {
  String viewType = "com.example.yylive_plugin/nativeKeyLiveView";
  MethodChannel? methodChannel;

  @override
  void initState() {
    super.initState();
    if (widget.duration! > 0) {
      Future.delayed(
          Duration(seconds: widget.duration!), () => widget.onOver!(""));
    }
  }

  //注册cannel
  void _registerChannel(int id) {
    methodChannel = MethodChannel("${viewType}_$id");
    play();
  }

  Future<void> play() async {
    return methodChannel?.invokeMethod('play');
  }

  Future<void> setText(String text) async {
    if (text.isNotEmpty) {
      return methodChannel?.invokeMethod('setText', text);
    }
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: widget.width,
      height: widget.height,
      child: Platform.isAndroid
          ? AndroidView(
              viewType: viewType,
              // 编码类型
              // StandardMessageCodec
              // JSONMessageCodec
              // StringCodec
              // BinaryCodec
              creationParams: widget.creationParams,
              creationParamsCodec: const StandardMessageCodec(),
              onPlatformViewCreated: _registerChannel, //初始化
            )
          : UiKitView(
              viewType: viewType,
              creationParams: widget.creationParams,
              creationParamsCodec: const JSONMessageCodec(),
            ),
    );
  }
}
