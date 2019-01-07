source 'https://github.com/CocoaPods/Specs.git'
platform :ios, '10.0'

# swift42里面的库已经支持swift4.2了 其他暂时先用4.1版本
swift42 = ['BSImagePicker','Charts','Eureka', 'GradientCircularProgress', 'HandyJSON', 'SwiftyTimer']

post_install do |installer|
    installer.pods_project.targets.each do |target|
        swift_version = '4.1'
        if swift42.include?(target.name)
            print "set pod #{target.name} swift version to 4.2\n"
            swift_version = '4.2'
        end
        target.build_configurations.each do |config|
            config.build_settings['SWIFT_VERSION'] = swift_version
        end
    end
end

 
target 'O2Platform' do
    use_frameworks!

    pod 'Alamofire', '~> 4.7'
    pod 'AlamofireImage', '~> 3.3'
    pod 'AlamofireNetworkActivityIndicator', '~> 2.0'
    pod 'AlamofireObjectMapper', '~> 5.1'
    pod 'SwiftyUserDefaults', '~>3.0'
    pod 'SwiftyJSON', '~>3.1'
    pod 'SDWebImage', '~>4.0'
    #pod 'CVCalendar', '~> 1.6.1'
    #pod 'ReachabilitySwift', '~> 3'
    pod 'BWSwipeRevealCell', '~> 2.0'
    pod 'BSImagePicker'
    pod 'Eureka'
    pod 'SwiftyTimer'
    pod 'EZSwiftExtensions', :path => '/Users/fancy/ios/dependence/EZSwiftExtensions'
    #pod 'DZNEmptyDataSet'
    pod 'Charts'
    pod 'GradientCircularProgress', :git => 'https://github.com/keygx/GradientCircularProgress.git'
    pod 'ImageSlideshow', '~> 1.5'
    pod 'ImageSlideshow/Alamofire'
    pod 'swiftScan', '~> 1.1.2'
    # pod 'Segmentio', '~> 3.0'
    pod 'JGProgressHUD'
    #pod 'RAMAnimatedTabBarController', '~> 2.0.13'
    #pod 'YALField', :git => 'https://github.com/Yalantis/YALField.git'
    pod 'Whisper'
    ##2017.6月2号增加日志框架
    pod 'CocoaLumberjack/Swift'
    pod 'MBProgressHUD', '~> 1.0.0'
    pod 'SnapKit', '~> 4.0.0'
    pod 'PromisesSwift', '~> 1.0'
    
    ## 2.0 引入的第三方库 ##
    pod 'Moya', '~> 11.0.2'
    pod 'Moya/RxSwift', '~> 11.0.2'
    pod 'ProgressHUDSwift', '~> 0.2'
    pod 'SwiftValidator', :git => 'https://github.com/jpotts18/SwiftValidator.git', :branch => 'master'
    pod 'HandyJSON', '~> 4.2.0'
    pod 'ReactiveSwift', '~> 3.0'
    pod 'ReactiveCocoa', '~> 7.0'
    pod 'JHTAlertController', :path => '/Users/fancy/ios/dependence/JHTAlertController'
    pod 'CYLTabBarController', '~> 1.17.4'
    #pod 'BaiduMapKit'
    pod 'YHPopupView'
    pod 'YHPhotoKit'
    pod 'FMDB', '~> 2.6.2'
#    pod 'RxSwift', '~> 4.4.0'
    pod 'RxCocoa', '~> 4.0'
    pod 'PromiseKit', '~> 4.4'
    # 极光组件
    #pod 'JPush' 还是手动引入比较好
    #日历控件
    # 本地组件
    pod 'DatePickerDialogSwift', :path => '/Users/fancy/ios/dependence/DatePickerDialogSwift'
    
    #表格数据源为空时显示
    pod 'EmptyDataSet-Swift', '~> 4.0.2'
    #segmentedControl
    pod 'BetterSegmentedControl', '~> 0.9'
    pod 'FSCalendar'
    pod 'JZCalendarWeekView', '~> 0.4'
    # 高斯模糊UIViewController
    pod 'MIBlurPopup'
    # 日历控件
    pod 'JTCalendar', '~> 2.0'
    # Bugly 异常上报管理
    pod 'Bugly'
    
   
    
end

