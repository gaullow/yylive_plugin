#import "AppDelegate.h"
#import "GeneratedPluginRegistrant.h"

#import "KeyLiveViewFactory.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  [GeneratedPluginRegistrant registerWithRegistry:self];
  // Override point for customization after application launch.

  //核心代码
    /**********flutter显示原生组件flutter-Start**********/
    KeyLiveViewFactory *keyLiveViewFactory = [[KeyLiveViewFactory alloc] init];
    //native:插件名，和其他插件区分开 唯一性
    //nvview:flutter中UiKitView组件需要的viewType属性 这样flutter就能找到viewFactory工厂方法返回对应的原生组件
    [[self registrarForPlugin:@"native"] registerViewFactory:keyLiveViewFactory withId:@"com.example.yylive_plugin/nativeKeyLiveView"];
    /**********flutter显示原生组件flutter-End**********/
  //....

  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

@end
