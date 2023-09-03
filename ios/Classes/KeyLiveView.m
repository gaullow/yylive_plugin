#import <Foundation/Foundation.h>
#import <Flutter/Flutter.h>

#import "KeyLiveView.h"
#import "KeyLiveViewUi.h"

@implementation NVViewObject

- (instancetype)initWithArg:(NSDictionary*)arg{
    NSLog(@"==================args1 = %@", arg);
    if (self = [super init]){
        self.argpara = arg;
    }
    return self;
}

/// 实现协议方法<FlutterPlatformViewFactory>
/// 这里返回原生组件
/// 返回的原生组件大小是撑满flutter侧的大小的 这里设置了frame也不会起作用
- (nonnull UIView *)view {
    NSLog(@"==================videoPath:%@", self.argpara[@"videoPath"]);
    KeyLiveViewUi *v = [[KeyLiveViewUi alloc] init];
    [v playKeyView:self.argpara[@"videoPath"]];
//    [v playUrlView:@"https://lxcode.bs2cdn.yy.com/92d5a19f-4288-41e6-835a-e092880c4af7.mp4"];
    return v;
}

@end
