import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:yylive_plugin/yylive_plugin_method_channel.dart';

void main() {
  MethodChannelYylivePlugin platform = MethodChannelYylivePlugin();
  const MethodChannel channel = MethodChannel('yylive_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
