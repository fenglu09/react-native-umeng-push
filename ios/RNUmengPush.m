
#import "RNUmengPush.h"
#import <UMPush/UMessage.h>

#import <React/RCTConvert.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTBridge.h>

@implementation RNUmengPush

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()
@synthesize bridge = _bridge;

- (instancetype)init
{
  self = [super init];
  
  if (self) {
    
    NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
    
    [defaultCenter removeObserver:self];
    
    [defaultCenter addObserver:self
                      selector:@selector(noti_receiveRemoteNotification:)
                          name:GT_DID_RECEIVE_REMOTE_NOTIFICATION
                        object:nil];
    [defaultCenter addObserver:self
                      selector:@selector(noti_clickRemoteNotification:)
                          name:GT_DID_CLICK_NOTIFICATION
                        object:nil];
    [defaultCenter addObserver:self
                      selector:@selector(noti_registeClientId:)
                          name:GT_DID_REGISTE_CLIENTID
                        object:nil];
  }
  return self;
}

- (void) showMessage: (NSString *) msg {
  UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"提示" message: msg preferredStyle:UIAlertControllerStyleAlert];
  [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil]];
  
  UIWindow   *alertWindow = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  alertWindow.rootViewController = [[UIViewController alloc] init];
  alertWindow.windowLevel = UIWindowLevelAlert + 1;
  [alertWindow makeKeyAndVisible];
  [alertWindow.rootViewController presentViewController:alert animated:YES completion:nil];
}

- (void)noti_receiveRemoteNotification:(NSNotification *)notification {
  id obj = [notification object];
  //    if(receiveRemoteNotificationCallback)
  //        receiveRemoteNotificationCallback(@[obj]);
  [self.bridge.eventDispatcher sendAppEventWithName:@"receiveRemoteNotification"
                                               body:obj];
}

-(void)noti_registeClientId:(NSNotification *)notification {
  id obj = [notification object];
  [self.bridge.eventDispatcher sendAppEventWithName:@"registeClientId"
                                               body:obj];
}

// iOS 10 后才有点击事件的回调
- (void)noti_clickRemoteNotification:(NSNotification *)notification {
  id obj = [notification object];
  //    if (clickNotificationCallback) {
  //        clickNotificationCallback(@[obj]);
  //    }
  [self.bridge.eventDispatcher sendAppEventWithName:@"clickRemoteNotification"
                                               body:obj];
}
#pragma mark - 收到通知回调

/**
 *  初始化个推，主要用于通知APPdelegate发送js已经初始化完毕的通知，用于处理APP被杀死后，点击通知进入时，跳转问题
 */
RCT_EXPORT_METHOD(initPush) {
  [[NSNotificationCenter defaultCenter]postNotificationName:@"GT_INIT" object:nil];
}


//  /**未知错误*/
//  kUMessageErrorUnknown = 0,
//  /**响应出错*/
//  kUMessageErrorResponseErr = 1,
//  /**操作失败*/
//  kUMessageErrorOperateErr = 2,
//  /**参数非法*/
//  kUMessageErrorParamErr = 3,
//  /**条件不足(如:还未获取device_token，添加tag是不成功的)*/
//  kUMessageErrorDependsErr = 4,
//  /**服务器限定操作*/
//  kUMessageErrorServerSetErr = 5,
- (NSString *)checkErrorMessage:(NSInteger)code
{
  switch (code) {
    case 1:
      return @"响应出错";
      break;
    case 2:
      return @"操作失败";
      break;
    case 3:
      return @"参数非法";
      break;
    case 4:
      return @"条件不足(如:还未获取device_token，添加tag是不成功的)";
      break;
    case 5:
      return @"服务器限定操作";
      break;
    default:
      break;
  }
  return nil;
}

- (void)handleResponse:(id  _Nonnull)responseObject remain:(NSInteger)remain error:(NSError * _Nonnull)error completion:(RCTResponseSenderBlock)completion
{
  if (completion) {
    if (error) {
      NSString *msg = [self checkErrorMessage:error.code];
      if (msg.length == 0) {
        msg = error.localizedDescription;
      }
      completion(@[@(error.code), @(remain)]);
    } else {
      if ([responseObject isKindOfClass:[NSDictionary class]]) {
        NSDictionary *retDict = responseObject;
        if ([retDict[@"success"] isEqualToString:@"ok"]) {
          completion(@[@200, @(remain)]);
        } else {
          completion(@[@(-1), @(remain)]);
        }
      } else {
        completion(@[@(-1), @(remain)]);
      }
      
    }
  }
}

- (void)handleGetTagResponse:(NSSet * _Nonnull)responseTags remain:(NSInteger)remain error:(NSError * _Nonnull)error completion:(RCTResponseSenderBlock)completion
{
  if (completion) {
    if (error) {
      NSString *msg = [self checkErrorMessage:error.code];
      if (msg.length == 0) {
        msg = error.localizedDescription;
      }
      completion(@[@(error.code), @(remain), @[]]);
    } else {
      if ([responseTags isKindOfClass:[NSSet class]]) {
        NSArray *retList = responseTags.allObjects;
        completion(@[@200, @(remain), retList]);
      } else {
        completion(@[@(-1), @(remain), @[]]);
      }
    }
  }
}
- (void)handleAliasResponse:(id  _Nonnull)responseObject error:(NSError * _Nonnull)error completion:(RCTResponseSenderBlock)completion
{
  if (completion) {
    if (error) {
      NSString *msg = [self checkErrorMessage:error.code];
      if (msg.length == 0) {
        msg = error.localizedDescription;
      }
      completion(@[@(error.code)]);
    } else {
      if ([responseObject isKindOfClass:[NSDictionary class]]) {
        NSDictionary *retDict = responseObject;
        if ([retDict[@"success"] isEqualToString:@"ok"]) {
          completion(@[@200]);
        } else {
          completion(@[@(-1)]);
        }
      } else {
        completion(@[@(-1)]);
      }
      
    }
  }
}

RCT_EXPORT_METHOD(addTag:(NSString *)tag response:(RCTResponseSenderBlock)completion)
{
  [UMessage addTags:tag response:^(id  _Nonnull responseObject, NSInteger remain, NSError * _Nonnull error) {
    [self handleResponse:responseObject remain:remain error:error completion:completion];
  }];
}

RCT_EXPORT_METHOD(deleteTag:(NSString *)tag response:(RCTResponseSenderBlock)completion)
{
  [UMessage deleteTags:tag response:^(id  _Nonnull responseObject, NSInteger remain, NSError * _Nonnull error) {
    [self handleResponse:responseObject remain:remain error:error completion:completion];
  }];
}

RCT_EXPORT_METHOD(listTag:(RCTResponseSenderBlock)completion)
{
  [UMessage getTags:^(NSSet * _Nonnull responseTags, NSInteger remain, NSError * _Nonnull error) {
    [self handleGetTagResponse:responseTags remain:remain error:error completion:completion];
  }];
}

RCT_EXPORT_METHOD(addAlias:(NSString *)name type:(NSString *)type response:(RCTResponseSenderBlock)completion)
{
  [UMessage addAlias:name type:type response:^(id  _Nonnull responseObject, NSError * _Nonnull error) {
    [self handleAliasResponse:responseObject error:error completion:completion];
  }];
}

RCT_EXPORT_METHOD(addExclusiveAlias:(NSString *)name type:(NSString *)type response:(RCTResponseSenderBlock)completion)
{
  [UMessage setAlias:name type:type response:^(id  _Nonnull responseObject, NSError * _Nonnull error) {
    [self handleAliasResponse:responseObject error:error completion:completion];
  }];
}

RCT_EXPORT_METHOD(deleteAlias:(NSString *)name type:(NSString *)type response:(RCTResponseSenderBlock)completion)
{
  [UMessage removeAlias:name type:type response:^(id  _Nonnull responseObject, NSError * _Nonnull error) {
    [self handleAliasResponse:responseObject error:error completion:completion];
  }];
}

RCT_REMAP_METHOD(getDeviceToken,
                 findEventsWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
  NSString *deviceToken = [[NSUserDefaults standardUserDefaults] valueForKey:@"deviceToken"];
  if (deviceToken.length > 0) {
    resolve(deviceToken);
  } else {
    NSError *error = @"no deviceToken";
    reject(@"no_events", @"There were no events", error);
//    resolve(@"0");
  }
}


@end
  
