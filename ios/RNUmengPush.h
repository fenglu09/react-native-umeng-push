
#import <Foundation/Foundation.h>
#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

#define GT_DID_RECEIVE_REMOTE_NOTIFICATION @"GTDidReciveRemoteNotification"
#define GT_DID_CLICK_NOTIFICATION @"GTDidClickNotification"
#define GT_DID_REGISTE_CLIENTID @"GTDidRegisteClient"


@interface RNUmengPush : NSObject <RCTBridgeModule>

@end
  
