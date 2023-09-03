#import <Flutter/Flutter.h>

#import <YYEVA/YYEVA.h>
#import "KeyLiveViewUi.h"
#import "YYEVAPlayer+HttpURL.h"

@implementation KeyLiveViewUi
- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]){
//         self.backgroundColor = UIColor.greenColor;
        // UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(50, 50, 100, 50)];
        // label.backgroundColor = UIColor.purpleColor;
        // label.text = @"原生组件";
        // [self addSubview:label];
    }
    return self;
}
- (void)dealloc{
    NSLog(@"原生组件销毁释放这里会被调用 实验证明 flutter侧页面关闭后这里会调用 dealloc 所以是自动释放的");
}

- (CGRect)playViewFrame
{
    return CGRectMake(0, 10, [UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height - 300);
}

- (void)playKeyView:(NSString *)videoPath
{
    NSString *file = [[NSBundle mainBundle] pathForResource:[@"resource/" stringByAppendingString:videoPath] ofType:nil];
//        NSString *str = @"替换文字";
//        NSString *str = self.args[@"videoPath"];

    NSString *png1 = [[NSBundle mainBundle] pathForResource:@"resource/avatar.png" ofType:nil];
    NSString *png2 = [[NSBundle mainBundle] pathForResource:@"resource/ball_2.png" ofType:nil];
    NSString *png3 = [[NSBundle mainBundle] pathForResource:@"resource/ball_3.png" ofType:nil];

    YYEVAPlayer *player = [[YYEVAPlayer alloc] init];
    player.delegate = self;
    [self addSubview:player];
    player.frame = [self playViewFrame];

    //配置相关属性
    [player setImageUrl:png2 forKey:@"key"];
//        [player setText:str.length ? str :@"可替换文案" forKey:@"keyname.png"];

    NSMutableParagraphStyle *paragraphStyle = [NSMutableParagraphStyle new];
    paragraphStyle.alignment = NSTextAlignmentCenter;
    paragraphStyle.lineBreakMode = NSLineBreakByTruncatingTail;
    NSMutableAttributedString *attrText = [[NSMutableAttributedString alloc] initWithString:@"可替换富文本" attributes:@{NSFontAttributeName: [UIFont systemFontOfSize:32 weight:UIFontWeightBold], NSForegroundColorAttributeName: [UIColor greenColor], NSBackgroundColorAttributeName: [UIColor blueColor], NSParagraphStyleAttributeName: paragraphStyle}];

    NSTextAttachment *attach = [NSTextAttachment new];
    attach.bounds = CGRectMake(0, 0, 32, 32);
    attach.image = [UIImage imageNamed:@"resource/ball_1.png"];
    NSAttributedString *attachString = [NSAttributedString attributedStringWithAttachment:attach];
    [attrText appendAttributedString:attachString];

    [player setAttrText:attrText forKey:@"keyname.png"];


//    player.regionMode = YYEVAColorRegion_AlphaMP4_LeftGrayRightColor; //指定色彩区域
    //开始播
    [player play:file repeatCount:2];
   
}

- (void)playUrlView:(NSString *)videoPath
{
    YYEVAPlayer *player = [[YYEVAPlayer alloc] init];
    player.delegate = self;
    [self addSubview:player];
    player.frame = [self playViewFrame];
    //开始播
    [player playHttpURL:videoPath];
}

@end
