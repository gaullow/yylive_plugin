#import <Flutter/Flutter.h>
#import "KeyLiveViewFactory.h"
#import "KeyLiveView.h"


@implementation KeyLiveViewFactory

/// 与flutter中 creationParams creationParamsCodec 对应 不实现此方法args则为null
- (NSObject<FlutterMessageCodec> *)createArgsCodec{
    return [FlutterJSONMessageCodec sharedInstance];
}

/// 实现协议方法<FlutterPlatformViewFactory>
/// 这里需要返回一个实现FlutterPlatformView协议的基类(NSObject<FlutterPlatformView>)
/// viewId是自动生成的无法指定
/// args flutter侧传递的参数
- (nonnull NSObject<FlutterPlatformView> *)createWithFrame:(CGRect)frame viewIdentifier:(int64_t)viewId arguments:(id _Nullable)args {
    NSLog(@"frame = %@",NSStringFromCGRect(frame));
    NSLog(@"==================viewId = %lld args = %@",viewId, args);
    
    return [[NVViewObject alloc] initWithArg:args];
}

@end
