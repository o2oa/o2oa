//
//  BMKVersion.h
//  BMapKit
//
//  Copyright 2011 Baidu Inc. All rights reserved.
//

#import <UIKit/UIKit.h>


/*****更新日志：*****
 V0.1.0： 测试版
 支持地图浏览，基础操作
 支持POI搜索
 支持路线搜索
 支持地理编码功能
 --------------------
 V1.0.0：正式发布版
 地图浏览，操作，多点触摸，动画
 标注，覆盖物
 POI、路线搜索
 地理编码、反地理编码
 定位图层
 --------------------
 V1.1.0：
 离线地图支持
 --------------------
 V1.1.1：
 增加suggestionSearch接口
 可以动态更改annotation title
 fix小内存泄露问题
 --------------------
 V1.2.1：
 增加busLineSearch接口
 修复定位圈范围内不能拖动地图的bug
 
 --------------------
 V2.0.0
 
 新增：
 全新的3D矢量地图渲染
 BMKMapView设定地图旋转俯视角度：rotation、overlooking
 BMKMapView设定指南针显示位置：compassPosition
 BMKMapView控制生命周期：viewWillAppear、viewWillDisappear
 地图标注可点，BMKMapViewDelegate新增接口回调接口onClickedMapPoi
 BMKAnnotationView设置annotation是否启用3D模式：enabled3D
 overlay绘制方式改变，采用opengl绘制：
 BMKOverlayView使用opengl渲染接口：glRender子类重载此函数实现gl绘制
 基本opengl线绘制：renderLinesWithPoints
 基本opengl面绘制：renderRegionWithPointsl
 全新的矢量离线地图数据：
 BMKOfflineMap下载离线地图：start
 BMKOfflineMap更新离线地图：update
 BMKOfflineMap暂停下载或更新：pasue
 获得热点城市列表：getHotCityList
 获得支持离线数据的城市：getOfflineCityList
 根据城市名查询城市信息：searchCity
 更新：
 BMKMapView的缩放级别zoomLevel更新为float型，实现无级缩放
 更新地图类型枚举：
 enum {   BMKMapTypeStandard  = 1,              ///< 标准地图
 BMKMapTypeTrafficOn = 2,              ///< 实时路况
 BMKMapTypeSatellite = 4,              ///< 卫星地图
 BMKMapTypeTrafficAndSatellite = 8,    ///< 同时打开实时路况和卫星地图
 };
 
 
 --------------------
 v2.0.1
 新增：
 §    MapView增加事件监听
 BMKMapviewDelegate中- mapView: onClickedMapBlank:方法监听地图单击事件
 BMKMapviewDelegate中- mapView: onDoubleClick:方法监听地图双击事件
 BMKMapviewDelegate中- mapView: onLongClick:方法监听地图长按事件
 §    地图截图功能
 BMKmapview中 -(UIImage*) takeSnapshot;
 §    路径规划增加途经点
 BMKSearch中- (BOOL)drivingSearch: startNode: endCity: endNode: throughWayPoints:
 §    suggestion搜索支持按城市搜索
 优化：
 §    全面支持iPad
 §    优化添加海量annotation逻辑
 §    BMKOfflineMap中：
 - (BOOL)pasue:(int)cityID;
 改为
 - (BOOL)pause:(int)cityID
 §    BMKMapview中：
 @property (nonatomic) CGPoint compassPositon;
 改为
 @property (nonatomic) CGPoint compassPosition;
 §    BMKRouteAddrResult结构添加属性：
 @synthesize wayPointPoiList;
 @synthesize wayPointCityList;
 §    BMKPlanNode中添加属性：
 @synthesize cityName; 添加城市属性
 §    BMKSuggestionresult结构添加属性：
 @synthesize districtList; 返回区域列表
 修复：
 §    修复与第三方类库冲突的问题
 修复与gzip、Reachability、png、jpeg、json、xml、sqlite等第三方类库冲突问题
 
 
 --------------------
 v2.0.2
 新增：
 1.全新的key验证体系
 
 2.增加短串分享接口
 1）在BMKType中新增数据结构：BMK_SHARE_URL_TYPE（分享串数据类型）
 2）在BMKSearch中新增接口-(BOOL)poiDetailShareUrl:(NSString*) uid; 发起poi短串搜索
 3）在BMKSearch中新增接口-(BOOL)reverseGeoShareUrl:(CLLocationCoordinate2D)coor
 poiName:(NSString*)name
 poiAddress:(NSString*)address; 发起反geo短串搜索
 4）在BMKSearchDelegate中新增接口-(void)onGetShareUrl:(NSString*) url
 withType:(BMK_SHARE_URL_TYPE) urlType
 errorCode:(int)error; 返回短串分享url
 3.比例尺控件
 1）在BMKMapview中新增属性@property (nonatomic) BOOL showMapScaleBar;比例尺是否显示
 2）在BMKMapview中新增属性@property (nonatomic) CGPoint mapScaleBarPosition;比例尺显示位置
 
 4.定位罗盘效果
 1）在BMKMapview中新增数据结构：BMKUserTrackingMode（定位模式）
 2）在BMKMapview中新增属性@property (nonattomic) BMKUserTrackingMode userTrackingMode; 设定定位模式
 
 5.驾车避让拥堵策略
 1）在BMKSearch中新增驾车检索策略常量BMKCarTrafficFIRST = 60,///<驾车检索策略常量：躲避拥堵
 
 6.路径查询增加时间、打车费用结果
 1）在BMKSearch中新增类：BMKTime（此类代表一个时间段，每个属性都是一个时间段。）
 2）在BMKTransitRoutePlan中新增属性@property (nonatomic) float price; 白天打车估价，单位(元)
 3）在BMKTransitRoutePlan中新增属性@property (nonatomic, retain) BMKTime* time; 方案所用时间
 4）在BMKRoutePlan中新增属性@property (nonatomic, retain) BMKTime* time; 方案预计的行驶时间
 
 优化：
 1）对在BMKMapview中的接口- (void)removeAnnotations:(NSArray *)annotations;（移除一组标注）进行了优化
 
 修复：
 1）修复离线地图――支持离线包的城市列表中省份下无子城市的问题
 2）修复前台数据请求后退至后台opengl继续渲染，应用Crash的问题
 
 --------------------
 v2.1.0
 新增：
 1.全面接入LBS.云V2.0，全面开放LBS.云检索能力
 1)在BMKCloudSearchInfo中新增类BMKBaseCloudSearchInfo，BMKCloudSearchInfo，BMKCloudLocalSearchInfo，BMKCloudNearbySearchInfo，BMKCloudBoundSearchInfo，BMKCloudDetailSearchInfo来存储云检索参数
 2)在BMKCloudPOIList中新增类BMKCloudPOIList来存储云检索结果
 3)在BMKCloudSearch中新增数据结构：BMKCloudSearchType（云检索类型）
 4)在BMKCloudSearch中新增接口- (BOOL)localSearchWithSearchInfo:;发起本地云检索
 5)在BMKCloudSearch中新增接口- (BOOL) nearbySearchWithSearchInfo:;发起周边云检索
 6)在BMKCloudSearch中新增接口- (BOOL) boundSearchWithSearchInfo:;发起矩形云检索
 7)在BMKCloudSearch中新增接口- (BOOL) detailSearchWithSearchInfo:;发起详情云检索
 8)在BMKCloudSearch中新增接口- (void)onGetCloudPoiResult: searchType: errorCode:;返回云检索列表结果
 9)在BMKCloudSearch中新增接口- (void) onGetCloudPoiDetailResult: searchType: errorCode:;返回云检索详情结果
 2.增加图片图层BMKGroundOverlay、BMKGroundOverlayView
 1)在BMKGroundOverlay中新增属性@property (nonatomic,assign) CLLocationCoordinate2D pt;两种绘制GroundOverlay的方式之一：绘制的位置地理坐标，与anchor配对使用
 2)在BMKGroundOverlay中新增属性@property (nonatomic,assign) CGPoint anchor;用位置绘制时图片的锚点，图片左上角为(0.0f,0.0f),向右向下为正
 3)在BMKGroundOverlay中新增属性@property (nonatomic,assign) BMKCoordinateBounds bound;两种绘制GroundOverlay的方式之二：绘制的地理区域范围，图片在此区域内合理缩放
 4)在BMKGroundOverlay中新增属性@property(nonatomic, strong) UIImage *icon;绘制图片
 5)在BMKGroundOverlay中新增接口+(BMKGroundOverlay*)groundOverlayWithPosition:zoomLevel:anchor:icon:;根据指定经纬度坐标生成一个groundOverlay
 6)在BMKGroundOverlay中新增接口+(BMKGroundOverlay*) groundOverlayWithBounds:icon:;根据指定区域生成一个groundOverlay
 3.增加自定义泡泡
 1)在BMKActionPaopaoView中新增接口- (id)initWithCustomView:(UIView*)customView;泡泡显示View自定义
 4.增加地图中心点映射屏幕点
 1)在BMKMapView中新增接口- (void)setMapCenterToScreenPt:;设置地图中心点在地图中的屏幕坐标位置
 5.增加以手势触摸点中心为基准旋转缩放底图功能和控制开关
 1)在BMKMapView中新增属性@property(nonatomic, getter=isChangeWithTouchPointCenterEnabled) BOOL ChangeWithTouchPointCenterEnabled;设定地图View能否支持以手势中心点为轴进行旋转和缩放（默认以屏幕中心点为旋转和缩放中心）
 6.增加同时改变地图俯角，旋转角度，缩放比例，中心点接口
 1)新增类BMKMapStatus来表示地图状态参数
 2)在BMKMapView中新增接口- (void)setMapStatus:;设置地图状态
 3)在BMKMapView中新增接口- (void)setMapStatus: withAnimation:;设置地图状态（指定是否需要动画效果）
 4)在BMKMapView中新增接口- (void)setMapStatus: withAnimation: withAnimationTime:;设置地图状态（指定是否需要动画效果＋指定动画时间）
 5)在BMKMapView中新增接口- (BMKMapStatus*)getMapStatus;获取地图状态
 7.增加地图状态改变实时通知
 1)在BMKMapView中新增接口- (void)mapStatusDidChanged:;地图状态改变完成后会调用此接口
 
 优化：
 1.点击Annotation置顶
 1)在BMKMapView中新增属性@property (nonatomic, assign) BOOL isSelectedAnnotationViewFront;设定是否总让选中的annotaion置于最前面
 2.定位脱离MapView
 1)在BMKUserLocation中新增接口-(void)startUserLocationService;打开定位服务
 2)在BMKUserLocation中新增接口-(void)stopUserLocationService;关闭定位服务
 3)在BMKUserLocation中新增接口- (void)viewDidGetLocatingUser:;开启定位后，会自动调用此接口返回当前位置的经纬度
 3.定位图层样式自定义灵活性优化
 1)新增类BMKLocationViewDisplayParam来存储定位图层自定义参数
 2)在BMKMapView中新增接口- (void)updateLocationViewWithParam:;动态定制定位图层样式
 
 修复：
 1)修复点击annotation回调长按监听接口- (void)mapview: onLongClick:的问题
 2)修复TransitRoutePlan中Content属性为空的问题
 3)修复缩放底图，接口- (void)mapView: regionDidChangeAnimated:不回调的问题
 4)修复从其他页面返回原页面泡泡被压盖的问题
 5)解决WiFi无网络信号时首次加载卡屏的问题
 
 --------------------
 v2.1.1
 新增：
 1.新增调启百度地图导航的接口（百度地图导航和Web端导航）
 在BMKNavigation中新增类枚举类型的数据结构BMK_NAVI_TYPE来定义调起导航的两种类型：BMK_NAVI_TYPE_NATIVE(调起客户端导航)和BMK_NAVI_TYPE_WEB(调起web导航)
 在BMKNavigation中新增类NaviPara来管理调起导航时传入的参数
 在类NaviPara中新增属性@property (nonatomic, retain) BMKPlanNode* startPoint;定义导航的起点
 在类NaviPara中新增属性@property (nonatomic, retain) BMKPlanNode* endPoint;定义导航的终点
 在类NaviPara中新增属性@property (nonatomic, assign) BMK_NAVI_TYPE naviType;定义导航的类型
 在类NaviPara中新增属性@property (nonatomic, retain) NSString* appScheme;定义应用返回scheme
 在类NaviPara中新增属性@property (nonatomic, retain) NSString* appName;定义应用名称
 在BMKNavigation中新增接口+ (void)openBaiduMapNavigation:;根据传入的参数调启导航
 
 2.几何图形绘制中，增加弧线绘制方法
 在BMKArcline中新增接口+ (BMKArcline *)arclineWithPoints:;根据指定坐标点生成一段圆弧
 在BMKArcline中新增接口+ (BMKArcline *)arclineWithCoordinates:;根据指定经纬度生成一段圆弧
 在类BMKArclineView中新增属性@property (nonatomic, readonly) BMKArcline *arcline;来定义该View对应的圆弧数据对象
 在BMKArclineView中新增接口- (id)initWithArcline:;根据指定的弧线生成一个圆弧View
 
 3.几何图形绘制中，扩增凹多边形绘制能力
 
 4.新增Key验证返回值
 在BMKMapManager中新增枚举数据类型EN_PERMISSION_STATUS类来定义key验证错误码
 服务端具体返回的错误码请参见http://developer.baidu.com/map/lbs-appendix.htm#.appendix2
 
 5.新增公交换乘查询中的结果字段
 在类BMKLine中新增属性@property (nonatomic) int zonePrice;定义路段价格
 在类BMKLine中新增属性@property (nonatomic) int totalPrice;定义线路总价格
 在类BMKLine中新增属性@property (nonatomic) int time;定义线路耗时，单位：秒
 在类BMKRoute中新增属性@property (nonatomic) int time;定义此路段的消耗时间，单位：秒
 
 优化：
 优化Key鉴权认证策略
 优化几何图形绘制中，折线段绘制末端圆滑
 提升添加、删除几何图形覆盖物的效率
 修复：
 修复iOS7系统下，定位图层拖图时卡顿的bug
 修复POI检索结果中，结果页索引始终为0的bug
 修复驾车线路规划中，最后一个节点提示信息有误的bug
 --------------------
 v2.2.0
 新增：
 1. 新增地图多实例能力，开发者可在同一个页面上构建多张相互独立的地图，各地图上的覆盖物互不干扰；
 2. 新增检索多实例能力，开发者可并行发起多个检索来满足自己实际的业务需求
 由于新增检索多实例能力，因此需要在BMKSearchDelegate的回调中增加searcher参数来表明是哪个检索对象发起的检   索。所以应用检索多实例时需要将检索结果和searcher来进行一一对应。示例如下：
 - (void)onGetPoiResult:(BMKSearch*)searcher result:(NSArray*)poiResultListsearchType:(int)type errorCode:(int)error{
 if(searcher==_search){
 NSLog(@"这是_search 对应的POI搜索结果");
 }else if(searcher==_search2){
 NSLog(@"这是_search2对应的POI搜索结果");
 }
 }
 3. 新增地图最大、最小缩放等级的控制方法
 在类BMKMapView中新增属性@property (nonatomic) float minZoomLevel;来设定地图的自定义最小比例尺级别
 在类BMKMapView中新增属性@property (nonatomic) float maxZoomLevel;来设定地图的自定义最大比例尺级别
 4. 新增地图操作的手势控制开关
 在类BMKMapView中新增属性@property(nonatomic, getter=isZoomEnabledWithTap) BOOL zoomEnabledWithTap;来设定地图View能否支持用户单指双击放大地图，双指单击缩小地图
 在类BMKMapView中新增属性@property(nonatomic, getter=isOverlookEnabled) BOOL overlookEnabled;来设定地图View能否支持俯仰角
 在类BMKMapView中新增属性@property(nonatomic, getter=isRotateEnabled) BOOL rotateEnabled;来设定地图View能否支持旋转
 
 
 修复：
 1.  修复遗留zip库冲突问题
 2.  解决Documents下的非用户数据上传iCloud的问题
 3.  修复BMKMapViewDelegate中regionDidChangeAnimated / regionWillChangeAnimated图区变化问题
 --------------------
 v2.2.1
 修复：
 1.  修复v2.2.0版本覆盖安装后，地图无法正常显示的问题
 2.  修复地图高级别下，道路名称不显示的问题
 3.  修复BMKMapManage的stop方法不可用的问题
 4.  修复setMapStatus中设置地图等级异常的问题
 5.  修复地图中心点偏移时，拖动地图覆盖物异常的问题
 6.  修复BMKMapView中手势控制相关的enable属性获取不正确的问题
 7.  修复与XML库冲突的问题
 --------------------
 v2.3.0
 新增：
 可根据开发者的实际需求，下载满足需求的定制功能开发包
 1.  基础地图:包括基本矢量地图、卫星图、实时路况图、离线地图及各种地图覆盖物，此外还包括各种与地图相关的操作和事件监听
 2.  检索功能:包括POI检索、公交信息查询、路线规划、正向/反向地理编码、在线建议查询、短串分享等功能
 针对检索业务设计了全新更易用、学习成本更低的程序功能接口
 3.  LBS云检索:提供周边、区域、城市内、详情多种方式检索用户存储在LBS云内的自有数据
 4.  定位功能:提供便捷的接口，帮助用户快捷获取当前位置信息
 实现全面升级优化，定位功能可脱离地图单独使用
 5.  计算工具:包括测距（两点之间地理距离）、坐标转换、调起百度地图导航等功能
 --------------------
 v2.4.0
 新增：
 基础地图
 1. 开放热力图绘制能力，帮助用户绘制自有数据热力图；
 在文件BMKHeatMap.h中新增类BMKHeatMapNode来表示热力图数据的单个数据节点
 在类BMKHeatMapNode中新增属性@property (nonatomic) CLLocationCoordinate2D pt;定义点的位置坐标
 在类BMKHeatMapNode中新增属性@property (nonatomic) double intensity;定义点的强度权值
 在BMKHeatMap中新增类BMKHeatMap来存储热力图的绘制数据和自定义热力图的显示样式
 在类BMKHeatMap中新增属性@property (nonatomic, assign) int mRadius; 设置热力图的柔化半径
 在类BMKHeatMap中新增属性@property (nonatomic, retain) BMKGradient* mGradient; 设置热力图的渐变色
 在类BMKHeatMap中新增属性@property (nonatomic, assign) double mOpacity; 设置热力图的透明度
 在类BMKHeatMap中新增属性@property (nonatomic, retain) NSMutableArray* mData; 设置热力图数据
 在类BMKMapView中新增方法- (void)addHeatMap:;来添加热力图
 在类BMKMapView中新增方法- (void)removeHeatMap;来删除热力图
 
 检索功能
 1. 开放POI的Place详情信息检索能力；
 在BMKPoiSearchOption.h文件中新增poi详情检索信息类BMKPoiDetailSearchOption
 在类 BMKPoiDetailSearchOption中新增属性@property (nonatomic, retain) NSString* poiUid; poi的uid
 在BMKPoiSearchType.h文件中新增poi详情检索结果类BMKPoiDetailResult
 在类BMKPoiSearch中新增方法- (BOOL)poiDetailSearch:;来根据poi uid 发起poi详情检索
 在BMKPoiSearchDelegate中新增回调- (void)onGetPoiDetailResult: result: errorCode:;来返回POI详情搜索结果
 
 定位功能
 1. 新增定位多实例，满足开发者在多个页面分别使用定位的需求；
 优化：
 1. 高级别地图下做平移操作时，标注覆盖物移动流畅性优化；
 修复：
 1. 修复相邻地形图图层拼接时，接缝过大的问题；
 2. 修复检索内存泄露的问题；
 3. 修复定位图层内存泄露的问题；
 --------------------
 v2.4.1
 优化：
 1. 优化底图相关的内存使用问题；
 
 --------------------
 v2.5.0
 使用Xcode6创建工程时注意事项如下：
 在info.plist中添加：Bundle display name （Xcode6新建的项目没有此配置，若没有会造成manager start failed）
 【 新 增 】
 1. 新增对arm64 CPU架构的适配；
 基础地图
 1. 新增对iPhone6、iPhone6 plus的屏幕适配；
 定位功能
 1. 新增对iOS8定位的适配；
 在使用SDK为您提供的定位功能时，注意事项如下：
 需要在info.plist里添加（以下二选一，两个都添加默认使用NSLocationWhenInUseUsageDescription）：
 NSLocationWhenInUseUsageDescription  ，允许在前台使用时获取GPS的描述
 NSLocationAlwaysUsageDescription  ，允许永久使用GPS的描述
 【 修 复 】
 修复Tabber控制器中使用定位弹出框异常的问题；
 修复scrollenable=no，仍可以移动地图的问题；
 修复多边形在特定坐标下显示异常问题；
 修复定位时间戳错误的问题；
 修复autolayout时，BMKMapView横屏时无法自动扩展的问题；
 修复从B页返回到A页后，在A页的viewWillAppear方法中setCenterCoordinate无效的问题；
 
 --------------------
 v2.6.0
 注意：新版本开发包头文件中部分接口和枚举类型有变更，请确保使用最新版本的头文件。
 【 变 更 】
 定位功能
 1、修改BMKLocationServiceDelegate：
 - (void)didUpdateBMKUserLocation:(BMKUserLocation *)userLocation;  //修改用户位置更新后的回调
 
 【 新 增 】
 基础地图
 1. 地图类型修改为：enum {
 BMKMapTypeStandard = 1,               ///<标准地图
 BMKMapTypeSatellite = 2,               ///<卫星地图
 };
 typedefNSUIntegerBMKMapType;
 2. 在类BMKMapView中新增：
 属性：baiduHeatMapEnabled，设定地图是否打开百度城市热力图图层（百度自有数据）
 接口：- isSurpportBaiduHeatMap，判断当前图区是否支持百度热力图
 属性：buildingsEnabled ，设定地图是否现实3D楼块效果
 属性：trafficEnabled, 设定地图是否打开路况图层
 接口：- (void)showAnnotations: animated:，设置地图使显示区域显示所有annotations
 接口：+(void)willBackGround，当应用即将后台时调用，停止一切调用opengl相关的操作
 接口：+(void)didForeGround，当应用恢复前台状态时调用
 3. 在类BMKMapViewDelegate中新增接口：
 - (void)mapViewDidFinishLoading: 地图初始化完毕时会调用
 - (void)mapView: onDrawMapFrame: 地图渲染每一帧画面过程中(地图更新)会调用
 4. 在BMKGroundOverlay.h中新增透明度设置属性: alpha
 5. 新增虚线绘制样式polyline／polygon的边框／circle的边框均可设定为虚线样式并指定颜色，自定义overlay也可在glrender中实现。
 新增资源：
 在mapapi.bundle的images文件夹中增加lineDashTexture.png，用于生成虚线纹理
 新增属性：
    在BMKOverlayGLBaseView.h中，@propertyBOOLlineDash;// 是否为虚线样式
    在BMKOverlayView.h中新增方法：
 -(void)renderLinesWithPoints:(BMKMapPoint *)points pointCount:(NSUInteger)pointCount
 strokeColor:(UIColor *)strokeColor
 lineWidth:(CGFloat)lineWidth
 looped:(BOOL)looped
 lineDash:(BOOL)lineDash;
 注：该方法再BMKPolylineView／BMKPolygonView／BMKCircleView绘制中会自动调用，用户自定义view也可以调用这个方法实线虚线样式。
 6. 新增自定义纹理绘制线：polyline／polygon的边框／circle的边框均可设定指定纹理，自定义overlay也可在glrender中实现。
 在BMKOverlayView.h中新增属性：
 @property (nonatomic, readonly) GLuintstrokeTextureID;//关联的纹理对象ID
 在BMKOverlayView.h中新增方法：
 -(GLuint)loadStrokeTextureImage:(UIImage *)textureImage;
 
 定位功能
 1、在BMKLocationService新增接口：
 +setLocationDistanceFilter:  //设置定位的最小更新距离(米)
 +getCurrentLocationDistanceFilter  //获取定位的最小更新距离(米)
 +setLocationDesiredAccuracy:  //设置定位精准度
 +getCurrentLocationDesiredAccuracy  //获取定位精准度
 
 【 优 化 】
 1. SDK配置使用ARC；
 2. 更新鉴权错误码；
 
 【 修 复 】
 1. 修复定位服务中，开启定位和停止定位没有成对使用造成的问题；
 2. 修复使用circleWithCenterCoordinate:radius:画圆时半径误差偏大的问题；
 3. 修复在6plus上标注显示过小的问题；
 4. 修复annotation拖拽结束后向下偏移的问题；
 
 
 --------------------
 v2.7.0
 自当前版本起，百度地图iOS SDK推出 .framework形式的开发包。此种类型的开发包配置简单、使用方便，欢迎开发者选用！
 【 新 增 】
     基础地图
 1. 增加地图缩放等级到20级（10米）；
 2. 新增地理坐标与OpenGL坐标转换接口：
 BMKMapView新增接口：
 - (CGPoint)glPointForMapPoint:(BMKMapPoint)mapPoint;//将BMKMapPoint转换为opengles可         以直接使用的坐标
 - (CGPoint *)glPointsForMapPoints:(BMKMapPoint *)mapPoints count:(NSUInteger)count;// 批量将BMKMapPoint转换为opengles可以直接使用的坐标
 3. 开放区域截图能力：
 BMKMapView新增接口：
 - (UIImage*)takeSnapshot:(CGRect)rect;// 获得地图区域区域截图
 
     检索功能
 1. 开放驾车线路规划，返回多条线路的能力；
 BMKDrivingRouteResult中，routes数组有多条数据，支持检索结果为多条线路
 2. 驾车线路规划结果中，新增路况信息字段：
 BMKDrivingRoutePlanOption新增属性：
 ///驾车检索获取路线每一个step的路况，默认使用BMK_DRIVING_REQUEST_TRAFFICE_TYPE_NONE
 @property (nonatomic) BMKDrivingRequestTrafficType drivingRequestTrafficType;
 BMKDrivingStep新增属性：
 ///路段是否有路况信息
 @property (nonatomic) BOOL hasTrafficsInfo;
 ///路段的路况信息，成员为NSNumber。0：无数据；1：畅通；2：缓慢；3：拥堵
 @property (nonatomic, strong) NSArray* traffics;
 3.废弃接口：
 2.7.0开始，BMKDrivingRouteLine中，废弃属性：isSupportTraffic
 
     计算工具
 1. 新增点与圆、多边形位置关系判断方法：
 工具类（BMKGeometry.h）中新增接口：
 //判断点是否在圆内
 UIKIT_EXTERN BOOL BMKCircleContainsPoint(BMKMapPoint point, BMKMapPoint center, double radius);
 UIKIT_EXTERN BOOL BMKCircleContainsCoordinate(CLLocationCoordinate2D point, CLLocationCoordinate2D center, double radius);
 //判断点是否在多边形内
 UIKIT_EXTERN BOOL BMKPolygonContainsPoint(BMKMapPoint point, BMKMapPoint *polygon, NSUInteger count);
 UIKIT_EXTERN BOOL BMKPolygonContainsCoordinate(CLLocationCoordinate2D point, CLLocationCoordinate2D *polygon, NSUInteger count);
 2. 新增获取折线外某点到这线上距离最近的点：
 工具类（BMKGeometry.h）中新增接口：
 UIKIT_EXTERN BMKMapPoint BMKGetNearestMapPointFromPolyline(BMKMapPoint point, BMKMapPoint* polyline, NSUInteger count);
 3、新增计算地理矩形区域的面积
 工具类（BMKGeometry.h）中新增接口：
 UIKIT_EXTERN double BMKAreaBetweenCoordinates(CLLocationCoordinate2D leftTop, CLLocationCoordinate2D rightBottom);
 
 【 优 化 】
 1. 减少首次启动SDK时的数据流量；
 2. 减少协议优化升级；
 3. 优化Annotation拖拽方法（长按后开始拖拽）；
 
 【 修 复 】
 1. 修复在线地图和离线地图穿插使用时，地图内存不释放的bug；
 2. 修复云检索过程中偶现崩溃的bug；
 3. 修复地图在autolayout布局下无效的bug；
 4. 修复BMKAnnotationView重叠的bug；
 
 
 --------------------
 v2.8.0
 
 注：百度地图iOS SDK向广大开发者提供了配置更简单的 .framework形式的开发包，请开发者选择此种类型的开发包使用。
 
 【 新  增 】
      周边雷达
    利用周边雷达功能，开发者可在App内低成本、快速实现查找周边使用相同App的用户位置的功能。
 新增周边雷达管理类：BMKRadarManager
 新增周边雷达protocol：BMKRadarManagerDelegate
 1.提供单次位置信息上传功能；
 - (BOOL)uploadInfoRequest:(BMKRadarUploadInfo*) info;
 2.提供位置信息连续自动上传功能；
 - (void)startAutoUpload:(NSTimeInterval) interval;//启动自动上传用户位置信息
 - (void)stopAutoUpload;//停止自动上传用户位置信息
 3.提供周边位置信息检索功能；
 - (BOOL)getRadarNearbySearchRequest:(BMKRadarNearbySearchOption*) option;
 4.提供清除我的位置信息功能
 - (BOOL)clearMyInfoRequest;
 
  基础地图
    1.新增折线多段颜色绘制能力；
 1）BMKPolyline中新增接口：
 ///纹理索引数组
 @property (nonatomic, strong) NSArray *textureIndex;
 //分段纹理绘制，根据指定坐标点生成一段折线
 + (BMKPolyline *)polylineWithPoints:(BMKMapPoint *)points count:(NSUInteger)count textureIndex:(NSArray*) textureIndex;
 //根据指定坐标点生成一段折线
 + (BMKPolyline *)polylineWithCoordinates:(CLLocationCoordinate2D *)coords count:(NSUInteger)count textureIndex:(NSArray*) textureIndex;
 2）BMKPolylineView新增接口
 /// 是否分段纹理绘制（突出显示）
 @property (nonatomic, assign) BOOL isFocus;
 2.可以修改BMKPolyline、BMKPolygon、BMKCircle、BMKArcline的端点数据了
 3.新增地图强制刷新功能：
 BMKMapView新增接口：
 - (void)mapForceRefresh;//强制刷新mapview
 
     检索功能
 1.在线建议检索结果开放POI经纬度及UID信息；
 BMKSuggestionResult新增接口：
 ///poiId列表，成员是NSString
 @property (nonatomic, strong) NSArray* poiIdList;
 ///pt列表，成员是：封装成NSValue的CLLocationCoordinate2D
 @property (nonatomic, strong) NSArray* ptList;
 2.更新检索状态码
 BMKSearchErrorCode中新增：
 BMK_SEARCH_NETWOKR_ERROR,///网络连接错误
 BMK_SEARCH_NETWOKR_TIMEOUT,///网络连接超时
 BMK_SEARCH_PERMISSION_UNFINISHED,///还未完成鉴权，请在鉴权通过后重试
 
     计算工具
 1.新增调启百度地图客户端功能；
 1）调起百度地图客户端 – poi调起
 新增调起百度地图poi管理类：BMKOpenPoi
 //调起百度地图poi详情页面
 + (BMKOpenErrorCode)openBaiduMapPoiDetailPage:(BMKOpenPoiDetailOption *) option;
 //调起百度地图poi周边检索页面
 + (BMKOpenErrorCode)openBaiduMapPoiNearbySearch:(BMKOpenPoiNearbyOption *) option;
 2）调起百度地图客户端 – 路线调起
 新增调起百度地图路线管理类类：BMKOpenRoute
 //调起百度地图步行路线页面
 + (BMKOpenErrorCode)openBaiduMapWalkingRoute:(BMKOpenWalkingRouteOption *) option;
 //调起百度地图公交路线页面
 + (BMKOpenErrorCode)openBaiduMapTransitRoute:(BMKOpenTransitRouteOption *) option;
 //调起百度地图驾车路线检索页面
 + (BMKOpenErrorCode)openBaiduMapDrivingRoute:(BMKOpenDrivingRouteOption *) option;
 2.新增本地收藏夹功能；
 新增收藏点信息类：BMKFavPoiInfo
 新增收藏点管理类：BMKFavPoiManager
 新增接口：
 //添加一个poi点
 - (NSInteger)addFavPoi:(BMKFavPoiInfo*) favPoiInfo;
 //获取一个收藏点信息
 - (BMKFavPoiInfo*)getFavPoi:(NSString*) favId;
 //获取所有收藏点信息
 - (NSArray*)getAllFavPois;
 //更新一个收藏点
 - (BOOL)updateFavPoi:(NSString*) favIdfavPoiInfo:(BMKFavPoiInfo*) favPoiInfo;
 //删除一个收藏点
 - (BOOL)deleteFavPoi:(NSString*) favId;
 //清空所有收藏点
 - (BOOL)clearAllFavPois;
 
 【 修  复 】
 1、修复setMinLevel、setMaxLevel生效的是整型的问题；
 2、修复setRegion精准度不高的问题；
 3、修复POI检索结果，pageNum不正确的问题；
 4、修复定位结果海拔始终为0的问题；
 5、修复反地理编码检索在特定情况下，收不到回调的问题；
 
 
 --------------------
 v2.8.1
 
 注：百度地图iOS SDK向广大开发者提供了配置更简单的 .framework形式的开发包，请开发者选择此种类型的开发包使用。自V2.8.1后，百度地图iOS SDK将不再提供 .a形式的开发包。
 
 【 修  复 】
 修复了升级IOS 9 beta 3系统后闪退的问题
 
 【 提  示 】
 1、由于iOS9改用更安全的https，为了能够在iOS9中正常使用地图SDK，请在"Info.plist"中进行如下配置，否则影响SDK的使用。
 <key>NSAppTransportSecurity</key>
 <dict>
 <key>NSAllowsArbitraryLoads</key>
 <true/>
 </dict>
 2、如果在iOS9中使用了调起百度地图客户端功能，必须在"Info.plist"中进行如下配置，否则不能调起百度地图客户端。
 <key>LSApplicationQueriesSchemes</key>
 <array>
 <string>baidumap</string>
 </array>
 
 
 --------------------
 v2.9.0
 
 注：百度地图iOS SDK向广大开发者提供了配置更简单的 .framework形式的开发包，请开发者选择此种类型的开发包使用。自v2.9.0起，百度地图iOS SDK将不再提供 .a形式的开发包。
    自v2.9.0起，采用分包的形式提供 .framework包，请广大开发者使用时确保各分包的版本保持一致。其中BaiduMapAPI_Base.framework为基础包，使用SDK任何功能都需导入，其他分包可按需导入。
 
 【 新版提示 】
 1.自v2.9.0起，将启用新的地图资源服务，旧地图离线包在新版上不可使用；同时官方不再支持地图离线包下载，所以v2.9.0起，去掉“手动离线导入接口”，SDK离线下载接口维持不变。
 2.自v2.9.0起，iOS SDK采用分包形式，旧包无法与新包同时混用，请将之前所有旧包(包含bundle资源)并全部替换为新包。
 3.自v2.9.0起，iOS SDK使用新的矢量地图样式，地图显示更加清新，和百度地图客户端保持一致
 
 【 新  增  / 废  弃 】
   基础地图
 1.适配iOS 9和 iPhone 6s
 2.新增点聚合功能开源
 增加点聚合功能，并在demo中开放源代码，具体请参考demo
 3.支持线绘制功能扩展：支持纹理图片平铺绘制，缩放，分段颜色设置
 BMKOverlayGLBasicView新增属性：
 /// 是否纹理图片平铺绘制，默认NO
 @property (assign, nonatomic) BOOL tileTexture;
 /// 纹理图片是否缩放（tileTexture为YES时生效），默认NO
 @property (assign, nonatomic) BOOL keepScale;
 BMKOverlayView新增属性：
 /// 使用分段颜色绘制时，必须设置（内容必须为UIColor）
 @property (nonatomic, strong) NSArray *colors;
 4.支持底图标注控制
 BMKMapView 新增属性:
 ///设定地图是否显示底图poi标注，默认YES
 @property(nonatomic, assign) BOOL showMapPoi;
 5.新增TileOverlay图层，分为离线、在线tileOverlay绘制(使用方法请参考demo)。
 新增类： BMKTileLayer、BMKURLTileLayer、BMKSyncTileLayer、BMKTileLayerView
 6.BMKMapStatus新增只读属性：visibleMapRect
 7.BMKOfflineMap废弃扫描导入离线包接口，不再支持离线包导入
 废弃接口：
 - (BOOL)scan:(BOOL)deleteFailedr __deprecated_msg("废弃方法(空实现),自2.9.0起废弃,不支持扫描导入离线包");
 8.更新离线城市BMKOLUpdateElement status状态
 
   检索功能
 反geo检索结果新增商圈
 BMKReverseGeoCodeResult新增属性：
 ///商圈名称
 @property (nonatomic, strong) NSString* businessCircle;
 
   定位功能
 废弃接口（空实现）：
 + (void)setLocationDistanceFilter:(CLLocationDistance) distanceFilter __deprecated_msg("废弃方法	（空实现），使用distanceFilter属性替换");
 + (CLLocationDistance)getCurrentLocationDistanceFilter __deprecated_msg("废弃方法（空实现），使用distanceFilter属性替换");
 + (void)setLocationDesiredAccuracy:(CLLocationAccuracy) desiredAccuracy __deprecated_msg("废弃方法（空实现），使用desiredAccuracy属性替换");
 + (CLLocationAccuracy)getCurrentLocationDesiredAccuracy __deprecated_msg("废弃方法（空实现），使用desiredAccuracy属性替换");
 新增属性：
 /// 设定定位的最小更新距离。默认为kCLDistanceFilterNone
 @property(nonatomic, assign) CLLocationDistance distanceFilter;
 /// 设定定位精度。默认为kCLLocationAccuracyBest。
 @property(nonatomic, assign) CLLocationAccuracy desiredAccuracy;
 /// 设定最小更新角度。默认为1度，设定为kCLHeadingFilterNone会提示任何角度改变。
 @property(nonatomic, assign) CLLocationDegrees headingFilter;
 /// 指定定位是否会被系统自动暂停。默认为YES。只在iOS 6.0之后起作用。
 @property(nonatomic, assign) BOOL pausesLocationUpdatesAutomatically;
 ///指定定位：是否允许后台定位更新。默认为NO。只在iOS 9.0之后起作用。设为YES时，Info.plist中 UIBackgroundModes 必须包含 "location"
 @property(nonatomic, assign) BOOL allowsBackgroundLocationUpdates;
 
 【 修  复 】
 1、修复不加载@3x图片的问题；
 2、修复公交路线规划，换乘方案内容缺失的问题；
 3、修复iOS 8.2系统版本以前，AnnotationView 中加入约束会卡住的问题；
 4、修复使用xcode 7编译时SDK产生的编译警告；
 5、修复BMKMapView在特定的使用条件下crash的问题
 
 
 --------------------
 v2.9.1
 
 注：百度地图iOS SDK向广大开发者提供了配置更简单的 .framework形式的开发包，请开发者选择此种类型的开发包使用。自v2.9.0起，百度地图iOS SDK将不再提供 .a形式的开发包。
 自v2.9.0起，采用分包的形式提供 .framework包，请广大开发者使用时确保各分包的版本保持一致。其中BaiduMapAPI_Base.framework为基础包，使用SDK任何功能都需导入，其他分包可按需导入。
 
 【新版提示】
 1.自V2.9.0起，将启用新的地图资源服务，旧地图离线包在新版上不可使用；同时官方不再支持地图离线包下载，所以V2.9.0起，去掉“手动离线导入接口”，SDK离线下载接口维持不变。
 2.自V2.9.0起，iOS SDK采用分包形式，旧包无法与新包同时混用，请将之前所有旧包(包含bundle资源)并全部替换为新包。
 3.自V2.9.0起，iOS SDK使用新的矢量地图样式，地图显示更加清新，和百度地图客户端保持一致
 
 
 【 新  增 】
   检索功能
 1、新增类：BMKPoiAddressInfo（POI门址信息类）
 2、BMKPoiResult新增接口：
	///是否返回的有门址信息列表
	@property (nonatomic, assign) BOOL isHavePoiAddressInfoList;
	///门址信息列表，成员是BMKPoiAddrsInfo(当进行的是poi城市检索，且检索关键字是具体的门址信息（如在北京搜"上地十街10号"）时，会返回此信息)
	@property (nonatomic, strong) NSArray* poiAddressInfoList;
 
 【 修  复 】
 1、修复iOS9后台定位问题；
 2、修复sug检索特殊case引起的crash的问题；
 3、修复自定义AnnotationView，启用3D效果后（enabled3D=YES）点击标注没有响应的问题；
 4、修复获取离线地图包大小信息时，包大小错误的问题。
 
 
 --------------------
 v2.10.0
 
 注：百度地图iOS SDK向广大开发者提供了配置更简单的 .framework形式的开发包，请开发者选择此种类型的开发包使用。自v2.9.0起，百度地图iOS SDK将不再提供 .a形式的开发包。
 自v2.9.0起，采用分包的形式提供 .framework包，请广大开发者使用时确保各分包的版本保持一致。其中BaiduMapAPI_Base.framework为基础包，使用SDK任何功能都需导入，其他分包可按需导入。
 
 【 新版提示 】
 1.自v2.9.0起，将启用新的地图资源服务，旧地图离线包在新版上不可使用；同时官方不再支持地图离线包下载，所以v2.9.0起，去掉“手动离线导入接口”，SDK离线下载接口维持不变。
 2.自v2.9.0起，iOS SDK采用分包形式，旧包无法与新包同时混用，请将之前所有旧包(包含bundle资源)并全部替换为新包。
 3.自v2.9.0起，iOS SDK使用新的矢量地图样式，地图显示更加清新，和百度地图客户端保持一致
 
 【 新  增  / 废  弃 】
   基础地图
 1、新增3D-Touch的回调
 BMKMapView 新增属性:
 /// 设定地图是否回调force touch事件，默认为NO，仅适用于支持3D Touch的情况，开启后会回调 - mapview:onForceTouch:force:maximumPossibleForce:
 @property(nonatomic) BOOL forceTouchEnabled;
 BMKMapViewDelegate 新增:
 - (void)mapview:(BMKMapView *)mapView onForceTouch:(CLLocationCoordinate2D)coordinate force:(CGFloat)force maximumPossibleForce:(CGFloat)maximumPossibleForce;
 2、新增个性化地图模板，支持黑夜模式、清新蓝等风格地图
 BMKMapView 新增方法:
 + (void)customMapStyle:(NSString*) customMapStyleJsonFilePath;
 3、新增设置地图边界区域的方法:
 BMKMapView 新增属性:
 ///地图预留边界，默认：UIEdgeInsetsZero。设置后，会根据mapPadding调整logo、比例尺、指南针的位置，以及targetScreenPt(BMKMapStatus.targetScreenPt)
 @property (nonatomic) UIEdgeInsets mapPadding;
 4、开放显示21级地图，但不支持卫星图、热力图、交通路况图层的21级地图。
 5、BMKMapType新增BMKMapTypeNone类型：不加载百度地图瓦片，显示为空白地图。和瓦片图功能配合使用，减少加载数据
 6、新增限制地图的显示范围的方法
 BMKMapView 新增属性:
 @property (nonatomic) BMKCoordinateRegion limitMapRegion;
 7、支持自定义百度logo位置，共支持6个位置，使用枚举类型控制显示的位置
 BMKMapView 新增属性:
 @property (nonatomic) BMKLogoPosition logoPosition;
 8、新增禁用所有手势功能
 BMKMapView 新增属性:
 @property(nonatomic) BOOL gesturesEnabled;
 9、新增获取指南针大小的方法，并支持更换指南针图片
 BMKMapView 新增属性、方法:
 @property (nonatomic, readonly) CGSize compassSize;
 - (void)setCompassImage:(UIImage *)image;
 10、新增获取比例尺大小的方法
 BMKMapView 新增属性:
 /// 比例尺的宽高
 @property (nonatomic, readonly) CGSize mapScaleBarSize;
 11、增加自定义定位精度圈的填充颜色和边框
 BMKLocationViewDisplayParam 新增属性：
 ///精度圈 填充颜色
 @property (nonatomic, strong) UIColor *accuracyCircleFillColor;
 ///精度圈 边框颜色
 @property (nonatomic, strong) UIColor *accuracyCircleStrokeColor;
 12、新增获取矩形范围内所有marker点的方法
 BMKMapView 新增方法:
 - (NSArray *)annotationsInCoordinateBounds:(BMKCoordinateBounds) bounds;
 13、BMKMapView废弃接口:
 +(void)willBackGround;//逻辑由地图SDK控制
 +(void)didForeGround;//逻辑由地图SDK控制
 
   检索功能
 1、新增骑行规划检索
 BMKRouteSearch 新增骑行路线检索方法:
 - (BOOL)ridingSearch:(BMKRidingRoutePlanOption*) ridingRoutePlanOption;
 BMKRouteSearchDelegate 新增返回骑行检索结果回调:
 - (void)onGetRidingRouteResult:(BMKRouteSearch*)searcher result:(BMKRidingRouteResult*)result errorCode:(BMKSearchErrorCode)error;
 新增类:
 BMKRidingRoutePlanOption 骑行查询基础信息类
 BMKRidingRouteResult 骑行路线结果类
 2、新增行政区边界数据检索
 新增类:
 BMKDistrictSearch 行政区域搜索服务类
 BMKDistrictSearchDelegate 行政区域搜索结果Delegate
 BMKDistrictSearchOption 行政区域检索信息类
 BMKDistrictResult 行政区域检索结果类
 3、新增驾车、公交、骑行、步行路径规划短串分享检索
 BMKShareURLSearch 新增获取路线规划短串分享方法:
 - (BOOL)requestRoutePlanShareURL:(BMKRoutePlanShareURLOption *)routePlanShareUrlSearchOption;
 BMKShareURLSearchDelegate 新增返回路线规划分享url结果回调:
 - (void)onGetRoutePlanShareURLResult:(BMKShareURLSearch *)searcher result:(BMKShareURLResult *)result errorCode:(BMKSearchErrorCode)error;
 
   计算工具
 支持调起百度地图客户端骑行、步行导航功能（百度地图App 8.8 以上版本支持）
 BMKNavigation 新增方法:
 //调起百度地图客户端骑行导航页面
 + (BMKOpenErrorCode)openBaiduMapRideNavigation:(BMKNaviPara*)para;
 //调起百度地图客户端步行导航页面
 + (BMKOpenErrorCode)openBaiduMapWalkNavigation:(BMKNaviPara*)para;
 
 【 修  复 】
 1、修复只使用检索时，首次鉴权失败（网络问题），再次发起鉴权无效的问题
 2、修复使用地图前使用离线地图，首次安装应用地图白屏的问题
 3、修复拖拽地图时，点击到标注，会触发didSelectAnnotationView:的回调，不回调regionDidChangeAnimated的问题
 4、修复BMKTransitStep 里的stepType中地铁和公交未做区分的问题
 
 
 --------------------
 --------------------
 v2.10.2
 
 注：百度地图iOS SDK向广大开发者提供了配置更简单的 .framework形式的开发包，请开发者选择此种类型的开发包使用。自v2.9.0起，百度地图iOS SDK将不再提供 .a形式的开发包。
 自v2.9.0起，采用分包的形式提供 .framework包，请广大开发者使用时确保各分包的版本保持一致。其中BaiduMapAPI_Base.framework为基础包，使用SDK任何功能都需导入，其他分包可按需导入。
 
 【 新版提示 】
 1.自v2.9.0起，将启用新的地图资源服务，旧地图离线包在新版上不可使用；同时官方不再支持地图离线包下载，所以v2.9.0起，去掉“手动离线导入接口”，SDK离线下载接口维持不变。
 2.自v2.9.0起，iOS SDK采用分包形式，旧包无法与新包同时混用，请将之前所有旧包(包含bundle资源)并全部替换为新包。
 3.自v2.9.0起，iOS SDK使用新的矢量地图样式，地图显示更加清新，和百度地图客户端保持一致
 
 【 新  增 】
   基础地图
 1、新增个性化地图道路文字颜色设置（包括高速及国道、城市主路、普通道路）
 
 【 变  更 】
   检索功能
 1、行政区边界数据检索：为兼容不连续的行政区，行政区边界数据检索结果(BMKDistrictResult)，行政区边界坐标点变更为：
 /// 行政区边界直角地理坐标点数据(NSString数组，字符串数据格式为: @"x,y;x,y")
 @property (nonatomic, strong) NSArray *paths;
 原接口作废
 
 【 优  化 】
 1、优化瓦片图性能：支持同时下载多张瓦片图、优化下载中断的重加载机制
 
 【 修  复 】
 1、修复sug检索某些特殊case，city、district为空的情况
 2、修复同步瓦片图内存问题
 3、修复在iOS6运行crash的问题
 4、修复 CVHttpResponse::ReadData 极其偶现的crash
 5、修复某些case下，点击polyline不会回调的问题
 6、修复调起客户端驾车导航后，关闭导航后，不会弹出“是否返回原应用”提示的问题
 
 --------------------
 v3.0.0
 
 注：百度地图iOS SDK向广大开发者提供了配置更简单的 .framework形式的开发包，请开发者选择此种类型的开发包使用。自v2.9.0起，百度地图iOS SDK将不再提供 .a形式的开发包。
 自v2.9.0起，采用分包的形式提供 .framework包，请广大开发者使用时确保各分包的版本保持一致。其中BaiduMapAPI_Base.framework为基础包，使用SDK任何功能都需导入，其他分包可按需导入。

 【 新版提示 】
 1.自v3.0.0起，iOS SDK全面支持ipv6网络
 
 【 新  增 】
   基础地图
 1、新增室内地图功能
 新增室内地图信息类：BMKBaseIndoorMapInfo
 BMKMapView新增接口:
 /// 设定地图是否显示室内图（包含室内图标注），默认不显示
 @property (nonatomic, assign) BOOL baseIndoorMapEnabled;
 /// 设定室内图标注是否显示，默认YES，仅当显示室内图（baseIndoorMapEnabled为YES）时生效
 @property (nonatomic, assign) BOOL showIndoorMapPoi;
 // 设置室内图楼层
 - (BMKSwitchIndoorFloorError)switchBaseIndoorMapFloor:(NSString*)strFloor withID:(NSString*)strID;
 // 获取当前聚焦的室内图信息
 - (BMKBaseIndoorMapInfo*)getFocusedBaseIndoorMapInfo;
 BMKMapViewDelegate新增接口：
 //地图进入/移出室内图会调用此接口
 - (void)mapview:(BMKMapView *)mapView baseIndoorMapWithIn:(BOOL)flag baseIndoorMapInfo:(BMKBaseIndoorMapInfo *)info;
 2、普通地图与个性化地图切换可以自由切换，BMKMapView新增接口:
 + (void)enableCustomMapStyle:(BOOL) enable;
 3、个性化地图配置json文件出错时，打印log提示
 4、设置mapPadding时可控制地图中心是否跟着移动，BMKMapView新增接口:
 @property (nonatomic) BOOL updateTargetScreenPtWhenMapPaddingChanged;
 5、BMKMapPoi中新增属性：
 ///点标注的uid，可能为空
 @property (nonatomic,strong) NSString* uid;
 
   检索功能
 1、新增室内POI检索
 新增室内POI检索参数信息类：BMKPoiIndoorSearchOption
 新增室内POI搜索结果类：BMKPoiIndoorResult
 新增室内POI信息类：BMKPoiIndoorInfo
 BMKPoiSearch新增接口：
 //poi室内检索
 - (BOOL)poiIndoorSearch:(BMKPoiIndoorSearchOption*)option;
 BMKPoiSearchDelegate新增接口：
 //返回POI室内搜索结果
- (void)onGetPoiIndoorResult:(BMKPoiSearch*)searcher result:(BMKPoiIndoorResult*)poiIndoorResult errorCode:(BMKSearchErrorCode)errorCode;
 2、驾车路线规划结果新增3个属性：打车费用信息、拥堵米数、红路灯个数，BMKDrivingRouteLine新增接口：
 ///路线红绿灯个数
 @property (nonatomic, assign) NSInteger lightNum;
 ///路线拥堵米数，发起请求时需设置参数 drivingRequestTrafficType = BMK_DRIVING_REQUEST_TRAFFICE_TYPE_PATH_AND_TRAFFICE 才有值
 @property (nonatomic, assign) NSInteger congestionMetres;
 ///路线预估打车费(元)，负数表示无打车费信息
 @property (nonatomic, assign) NSInteger taxiFares;
 3、busline检索新增参考票价和上下线行信息，BMKBusLineResult新增接口：
 ///公交线路方向
 @property (nonatomic, strong) NSString* busLineDirection;
 ///起步票价
 @property (nonatomic, assign) CGFloat basicPrice;
 ///全程票价
 @property (nonatomic, assign) CGFloat totalPrice;
 4、poi检索结果新增是否有全景信息，BMKPoiInfo新增接口：
 @property (nonatomic, assign) BOOL panoFlag;
 
   计算工具
 新增调起百度地图客户端全景功能
 新增调起百度地图全景类：BMKOpenPanorama
 新增调起百度地图全景参数类：BMKOpenPanoramaOption
 新增调起百度地图全景delegate：BMKOpenPanoramaDelegate
 
 
 【 修  复 】
 1、修复反复添加移除离线瓦片图时偶现的crash问题
 2、修复上传AppStore时提示访问私有api:-setOverlayGeometryDelegate:的问题
 3、修复地图网络解析时偶现的crash问题

 
 --------------------
v3.1.0
 
 注：百度地图iOS SDK向广大开发者提供了配置更简单的 .framework形式的开发包，请开发者选择此种类型的开发包使用。自v2.9.0起，百度地图iOS SDK将不再提供 .a形式的开发包。
 自v2.9.0起，采用分包的形式提供 .framework包，请广大开发者使用时确保各分包的版本保持一致。其中BaiduMapAPI_Base.framework为基础包，使用SDK任何功能都需导入，其他分包可按需导入。

 【 新  增 】
   基础地图
 1、开放高清4K地图显示（无需设置）
 2、瓦片图新增异步加载方法：
    新增异步加载类：BMKAsyncTileLayer
 3、新增地图渲染完成回调方法：
    - (void)mapViewDidFinishRendering:(BMKMapView *)mapView;
 4、新增定位显示类型：BMKUserTrackingModeHeading（在普通定位模式的基础上显示方向）
 
   检索功能
 1、新增室内路径规划
    BMKRouteSearch新增发起室内路径规划接口：
    - (BOOL)indoorRoutePlanSearch:(BMKIndoorRoutePlanOption*) indoorRoutePlanOption;
    BMKRouteSearchDelegate新增室内路径规划结果回调：
    - (void)onGetIndoorRouteResult:(BMKRouteSearch*)searcher result:(BMKIndoorRouteResult*)result errorCode:(BMKSearchErrorCode)error;
    新增室内路径规划检索参数类：BMKIndoorRoutePlanOption
    新增室内路径规划检索结果类：BMKIndoorRouteResult
 2、增加新的公共交通线路规划（支持同城和跨城）
    BMKRouteSearch增加新的公共交通线路规划接口：
    - (BOOL)massTransitSearch:(BMKMassTransitRoutePlanOption*)routePlanOption;
    BMKRouteSearchDelegate增加新的公共交通线路规划结果回调：
    - (void)onGetMassTransitRouteResult:(BMKRouteSearch*)searcher result:(BMKMassTransitRouteResult*)result errorCode:(BMKSearchErrorCode)error;
    增加新的公共交通线路规划检索参数类：BMKMassTransitRoutePlanOption
    增加新的公共交通线路规划检索结果类：BMKMassTransitRouteResult
 
   LBS云检索
 1、新增云RGC检索功能
    BMKCloudSearch新增发起云RGC检索接口：
    - (BOOL)cloudReverseGeoCodeSearch:(BMKCloudReverseGeoCodeSearchInfo*)searchInfo;
    BMKCloudSearchDelegate新增云RGC检索结果回调：
    - (void)onGetCloudReverseGeoCodeResult:(BMKCloudReverseGeoCodeResult*)cloudRGCResult searchType:(BMKCloudSearchType) type errorCode:(NSInteger) errorCode;
    新增云RGC检索参数类：BMKCloudReverseGeoCodeSearchInfo
    新增云RGC检索结果类：BMKCloudReverseGeoCodeResult
 
 【 优  化 】
 1、优化Marker加载性能：添加Marker和加载大量Marker时，性能大幅提高。
 2、优化地图内存
 
 【 修  复 】
 1、长按地图某区域，OnLongClick会被不停调用的问题
 2、绘制弧线，特殊case提示画弧失败的问题
 3、一次点击事件，点击地图空白处回调和点击覆盖物回调都会调用的问题
 
 
 --------------------
 v3.2.0
 
 注：自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）。
 
 【 新版提示 】
 1、自v3.2.0起，全面支持HTTPS
 2、自v3.2.0起，地图引擎全面升级，主要升级特征有：
    渲染架构技术升级，OpenGL ES从1.0升级到2.0
    地图数据加载升级，加载性能大幅提升
 
 【 新  增 】
   检索功能
 1、建议检索支持港澳台；建议检索可控制只返回指定城市的检索结果
 BMKSuggestionSearchOption新增属性：
 ///是否只返回指定城市检索结果（默认：NO）（提示：海外区域暂不支持设置cityLimit）
 @property (nonatomic, assign) BOOL cityLimit;
 2、反地址编码结果BMKReverseGeoCodeResult新增属性：
 ///结合当前位置POI的语义化结果描述
 @property (nonatomic, strong) NSString* sematicDescription;
 
 【 优  化 】
 1、建议检索和反地址编码检索服务升级，提供更加优质的服务
 
 【 修  复 】
 1、修复国外定位偏移的问题
 2、修复特殊情况下，移除BMKGroundOverlay时的问题
 
 
 --------------------
 v3.2.1
 
 注：自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）。
 
 【 新版提示 】
 1、自v3.2.0起，全面支持HTTPS
 2、自v3.2.0起，地图引擎全面升级，主要升级特征有：
 渲染架构技术升级，OpenGL ES从1.0升级到2.0
 地图数据加载升级，加载性能大幅提升
 
 【 修  复 】
 修复下载离线地图时，delegate方法返回state错误问题
 
 
 --------------------
 v3.3.0
 
 
注：自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）。
 
 新 版 提 示 】
 【 注 意 】
 1、自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）
 添加方法：在 TARGETS->Build Phases-> Link Binary With Libaries中点击“+”按钮，在弹出的窗口中点击“Add Other”按钮，选择libssl.a和libcrypto.a添加到工程中 。
 
 2、支持CocoaPods导入
 pod setup //更新CocoPods的本地库
 pod search BaiduMapKit  //下载最新地图SDK
 
 【 新 增 】
 [ 基 础 地 图 ]
 3D地图下，增加显示天空效果，无需设置
 
 [ 工 具 ]
 1．全面支持GCJ02坐标输入/输出，全局设置方法如下：
 [BMKMapManager setCoordinateTypeUsedInBaiduMapSDK:BMK_COORDTYPE_COMMON];//默认为BD09LL坐标，且此方法仅在国内生效，国外均为WGS84坐标
 
 2. 新增调启步行AR导航接口：openBaiduMapwalkARNavigation
 
 [ LBS云]
 云检索中，keywords 改为非必填项
 
 【 优 化 】
 优化个性化地图元素分类
 
 【 修 复 】
 少部分地铁线及室内图无法显示问题（v3.2.0引入的问题）。
 未下载全国离线基础包时，离线状态下全国（球）地图显示异常。
 
 --------------------
 v3.3.1
 
 【 新 版 提 示 】
 【 注 意 】
 1、自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）
 添加方法：在 TARGETS->Build Phases-> Link Binary With Libaries中点击“+”按钮，在弹出的窗口中点击“Add Other”按钮，选择libssl.a和libcrypto.a添加到工程中 。
 
 2、支持CocoaPods导入
 pod setup //更新CocoPods的本地库
 pod search BaiduMapKit  //下载最新地图SDK
 
 【 新 增 】
 [ 检 索 ]
 逆地理编码返回结果新增2个属性：cityCode(城市编码) 和adCode（行政区域编码）
 
 【 优 化 】
 1.增加重试机制，优化鉴权时长
 2.解决Xcode8.3编译时出现大量warning的问题
 3.swift Demo：swift语言升级为 swift v3.1，优化升级swift Demo。
 
 --------------------
 v3.3.2
 
 
 【 新 版 提 示 】
 【 注 意 】
 1、自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）
 添加方法：在 TARGETS->Build Phases-> Link Binary With Libaries中点击“+”按钮，在弹出的窗口中点击“Add Other”按钮，选择libssl.a和libcrypto.a添加到工程中 。
 
 2、支持CocoaPods导入
 pod setup //更新CocoPods的本地库
 pod search BaiduMapKit  //查看最新地图SDK
 
 【 新 增 】
 【 优 化 】
 1.修复个性化地图在部分使用场景下，不显示的问题。（受影响版本v3.3.0、v3.3.1）
 
 
 --------------------
 v3.3.4
 
 
 【 新 版 提 示 】
 【 注 意 】
 1、自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）
 添加方法：在 TARGETS->Build Phases-> Link Binary With Libaries中点击“+”按钮，在弹出的窗口中点击“Add Other”按钮，选择libssl.a和libcrypto.a添加到工程中 。
 
 2、支持CocoaPods导入
 pod setup //更新CocoPods的本地库
 pod search BaiduMapKit  //查看最新地图SDK
 
 【 新 增 】
 1.BMKLocationViewDisplayParam类中增加 canShowCallOut 属性，用于设定用户点击定位图标时，是否弹出paopaoView。
 2.BMKLocationViewDisplayParam类中增加 locationViewHierarchy 属性，用于设定locationView始终处于视图层级的最下层或最上层。
 
 【 优 化 】
 1.修复添加Annotation时，Overlay偶尔绘制不完整的BUG。
 2.修复Swift调用SDK时，cityCode countryCode等字段类型不兼容的问题。
 3.保证新添加的Annotation会在mapView的视图层级的上层。
 4.DEMO中绘制路径规划结果时，修复计算显示区域的BUG。
 
 --------------------
 v3.4.0
 
 
 
 【 新 版 提 示 】
 【 注 意 】
 1、自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）
 添加方法：在 TARGETS->Build Phases-> Link Binary With Libaries中点击“+”按钮，在弹出的窗口中点击“Add Other”按钮，选择libssl.a和libcrypto.a添加到工程中 。
 
 2、支持CocoaPods导入
 pod setup //更新CocoPods的本地库
 pod search BaiduMapKit  //查看最新地图SDK
 
 【 新 增 】
 【基 础 地 图】
 1.新增当双击手势放大地图时，可以设置地图中心点是否移动至点击处的属性
 BMKMapView新增：
 ///双击手势放大地图时, 设置为YES, 地图中心点移动至点击处; 设置为NO，地图中心点不变；默认为YES;
 @property(nonatomic, getter=isChangeCenterWithDoubleTouchPointEnabled) BOOL ChangeCenterWithDoubleTouchPointEnabled;
 
 2.支持标注锁定在屏幕固定位置
 BMKPointAnnotation新增：
 ///Annotation固定在指定屏幕位置,  必须与screenPointToLock一起使用。 注意：拖动Annotation isLockedToScreen会被设置为false。
 ///若isLockedToScreen为true，拖动地图时annotaion不会跟随移动；
 ///若isLockedToScreen为false，拖动地图时annotation会跟随移动。
 @property (nonatomic, assign) BOOL isLockedToScreen;
 
 ///标注在屏幕中锁定的位置，注意：地图初始化后才能设置screenPointToLock。可以在地图加载完成的回调方法：mapViewDidFinishLoading中使用此属性。
 @property (nonatomic, assign) CGPoint screenPointToLock;
 
 3.新增接口：设定地理范围在屏幕中的显示区域
 BMKMapView新增：
 ///根据当前mapView的窗口大小，预留insets指定的边界区域后，将mapRect指定的地理范围显示在剩余的区域内，并尽量充满
 ///@param mapRect 要显示的地图范围，用直角坐标系表示
 ///@param insets 屏幕四周预留的最小边界（mapRect的内容不会显示在该边界范围内）
 ///@param animate 是否采用动画效果
- (void)fitVisibleMapRect:(BMKMapRect)mapRect edgePadding:(UIEdgeInsets)insets withAnimated:(BOOL)animate;

【 优 化 】
1.解决反复创建和销毁mapView时内存泄漏的问题
2.对拖动标注时的弹跳动画效果进行优化
3.修复mapView调用selectAnnotation方法时，回调didSelectAnnotationView不调用的问题。
4.修复行政区域检索福建和浙江区域没有返回数据的问题
5.修复部分使用场景下，设置mapPadding时，overlay位置偏移的问题
6.修复部分使用场景下，加载mapView闪黑屏的问题

 --------------------
 v3.4.2
 
 
 【 新 版 提 示 】
 【 注 意 】
 1、自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）
 添加方法：在 TARGETS->Build Phases-> Link Binary With Libaries中点击“+”按钮，在弹出的窗口中点击“Add Other”按钮，选择libssl.a和libcrypto.a添加到工程中 。
 
 2、支持CocoaPods导入
 pod setup //更新CocoPods的本地库
 pod search BaiduMapKit  //查看最新地图SDK
 
 【修复】
 1.修复多页面多地图场景下，切换页面导致的crash问题。
 2.修复检索对象对delegate的强引用问题。
 3.修复在一些罕见场景下，Bugly报告的crash问题。
 4.修复第一次通过setBuildingsEnabled接口设置不显示3D楼块效果失效的BUG。
 
 【优化】
 1.删除annotation后，不再删除其对应的annotationView的subView。开发者dequeue出可重用的annotationView后，为了避免内容堆叠问题，可以自行去避免，如remove subview或者使用不同的reuseIdentifier等。
 2.每个reuseIdentifier可缓存多个annotationView，当开发者removeAnnotation时，SDK会将对应的annotationView加入缓存队列。

 --------------------
 v3.4.4
 
 
 【 新 版 提 示 】
 【 注 意 】
 1、自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）
 添加方法：在 TARGETS->Build Phases-> Link Binary With Libaries中点击“+”按钮，在弹出的窗口中点击“Add Other”按钮，选择libssl.a和libcrypto.a添加到工程中 。
 
 2、支持CocoaPods导入
 pod setup //更新CocoPods的本地库
 pod search BaiduMapKit  //查看最新地图SDK
 
 【新增】
 1.新增 BMKConvertToBaiduMercatorFromBD09LL 与 BMKConvertToBD09LLFromBaiduMercator 方法，用于百度经纬度和百度墨卡托之间的转换。
 2.新增 CLLocationCoordinate2D BMKCoordTrans(CLLocationCoordinate2D coordinate, BMK_COORD_TYPE fromType, BMK_COORD_TYPE toType); 方法，支持WGS84LL->BD09LL, GCJ02LL->BD09LL, BD09LL->GCJ02LL三种经纬度之间的直接转换。
 
 【修复】
 1.支持iOS11系统定位权限
 2.个性化地图部分catlog不显示的问题
 3.室内图无背景色的问题
 4.polygon绘制大量节点折线，超出数量，产生飞线问题
 5.部分场景下，点击离线地图crash的问题
 
 --------------------
 v4.0.0
 
 
 【 新 版 提 示 】
 【 注 意 】
 1、自v3.2.0起，百度地图iOS SDK全面支持HTTPS，需要广大开发者导入第三方openssl静态库：libssl.a和libcrypto.a（存放于thirdlib目录下）
 添加方法：在 TARGETS->Build Phases-> Link Binary With Libaries中点击“+”按钮，在弹出的窗口中点击“Add Other”按钮，选择libssl.a和libcrypto.a添加到工程中 。
 
 2、支持CocoaPods导入
 pod setup //更新CocoPods的本地库
 pod search BaiduMapKit  //查看最新地图SDK
 
 【新增】
 1.升级引擎，提升底图加载速度。
 2.升级数据服务版本与地图客户端一致。
 3.适配V4.1.x(即以上)版本导航SDK。只有V4.0.0及以上版本的地图SDK才能与V4.1.x版本的导航SDK同时使用，否则会编译报错。
 4.新增海外离线地图下载控制。
 
 【优化】
 1.BMKPolyline采用多段纹理时，交接处更加绘制效果更平滑。
 2.优化高架桥、天桥等高精道路的显示效果，增加阴影，深度效果。
 3.室内图下，楼的侧立面增加玻璃罩效果。
 4.为了优化小比例尺下的显示效果，将zoomLevel的最小值由3改为4。
 5.优化地图释放内存回收机制。
 
 【修复】
 1.BMKPoiDetailResult无法获取到POI地理坐标的BUG。
 2.打开百度地图客户端返回后（前后台切换）黑屏的BUG。
 3.部分国家和地图的离线地图大小为负数的BUG。
 4.修复iOS7系统下使用定位服务会crash的BUG。
 
 
 *********************/
/**
 *获取当前地图API的版本号
 *return  返回当前API的版本号
 */
UIKIT_STATIC_INLINE NSString* BMKGetMapApiVersion()
{
    return @"4.0.0";
}

/**
 *获取当前地图API base组件 的版本号
 *当前base组件版本 : 4.0.0
 *return  返回当前API base组件 的版本号
 */
UIKIT_EXTERN NSString* BMKGetMapApiBaseComponentVersion();

