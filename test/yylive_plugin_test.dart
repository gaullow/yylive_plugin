import 'package:flutter_test/flutter_test.dart';
import 'package:yylive_plugin/yylive_plugin.dart';
import 'package:yylive_plugin/yylive_plugin_platform_interface.dart';
import 'package:yylive_plugin/yylive_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockYylivePluginPlatform
    with MockPlatformInterfaceMixin
    implements YylivePluginPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final YylivePluginPlatform initialPlatform = YylivePluginPlatform.instance;

  test('$MethodChannelYylivePlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelYylivePlugin>());
  });

  test('getPlatformVersion', () async {
    YylivePlugin yylivePlugin = YylivePlugin();
    MockYylivePluginPlatform fakePlatform = MockYylivePluginPlatform();
    YylivePluginPlatform.instance = fakePlatform;

    expect(await yylivePlugin.getPlatformVersion(), '42');
  });
}
