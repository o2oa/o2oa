//
//  PgyUpdateManager.h
//  Pods
//
//  Created by Scott Lei on 15/9/16.
//
//

#import <Foundation/Foundation.h>

@interface PgyUpdateManager : NSObject

+ (PgyUpdateManager *)sharedPgyManager;

/**
 *  启动蒲公英SDK
 *  @param appId 应用程序ID，从蒲公英网站上获取。
 */
- (void)startManagerWithAppId:(NSString *)appId;

/**
 *  检查是否有版本更新。
 *  如果开发者在蒲公英上提交了新版本，则调用此方法后会弹出更新提示界面。
 */
- (void)checkUpdate;

/**
 *  检查是否有版本更新。
 *
 *  @param delegate 自定义checkUpdateWithDelegete方法的对象
 *  @param updateMethodWithDictionary 当checkUpdateWithDelegete事件完成时此方法会被调用，包含更新信息的字典也被回传。
 *         如果有更新信息，那么字典里就会包含新版本的信息，否则的话字典信息为nil。
 */
- (void)checkUpdateWithDelegete:(id)delegate selector:(SEL)updateMethodWithDictionary;

/**
 *  检查更新是根据本地存储的Build号和蒲公英上的最新Build号比较来完成的。如果调用checkUpdateWithDelegete，SDK会获取到最新的
 *  Build号，但是checkUpdateWithDelegete方法自己不会来更新本地版本号，如果需要更新本地版本号，则需要调用此方法。
 */
- (void)updateLocalBuildNumber;

@end
