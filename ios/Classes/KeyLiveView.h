#import <Flutter/Flutter.h>

/// 一个实现FlutterPlatformView协议的基类(NSObject<FlutterPlatformView>)
@interface NVViewObject : NSObject<FlutterPlatformView>

/// 新声明一个方法存放flutter侧传过来的参数NSDictionary
@property NSDictionary* argpara;
- (instancetype)initWithArg:(NSDictionary*)arg;

@end
