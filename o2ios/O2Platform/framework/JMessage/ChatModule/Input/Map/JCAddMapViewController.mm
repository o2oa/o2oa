/*
 *
 1、地理编码指的是将地址位置（中文地址）转换成经纬度，反地理编码指的是将经纬度转换成地址位置；
 2、在百度地图中需要用到三个关键性的类：BMKGeoCodeSearch、BMKGeoCodeSearchOption、BMKReverseGeoCodeOption；
 3、BMKGeoCodeSearch：地理编码主类，用来查询、返回结果信息（地址位置或经纬度）；
 4、BMKGeoCodeSearchOption：地理编码选项，即地理编码的数据模型，地址是通过该类传递进去的；
 5、BMKReverseGeoCodeOption：反地理编码选项，即反地理编码的数据模型，经纬度就是通过该类传递进去的；
 6、有了以上基本信息，开始做一个简单的示例：从手机页面上输入经纬度通过按钮事件将对应的地理位置输出到手机屏幕，反之亦然；
 *
 */
#import "JCAddMapViewController.h"

#define kScreenWidth [UIScreen mainScreen].bounds.size.width
#define kScreenHeight [UIScreen mainScreen].bounds.size.height

@interface JCAddMapViewController () <BMKMapViewDelegate, BMKLocationServiceDelegate, BMKGeoCodeSearchDelegate>
{
    BMKMapView* mapView;//地图视图
    BMKLocationService *locService; //定位
    
    BMKPointAnnotation *pointAnnotation; // 一开始显示的大头针
    BOOL annotaionShow; // 使大头针仅显示一个
    UIPanGestureRecognizer *mapViewPan;
    BOOL mapPanGestureRecognizer; // 使地图仅在拖动停止时显示周边信息
    
    UIButton *locationBtn; //返回原定位坐标点按钮
    
    UITableView *myTableView;
    NSMutableArray *addressDataArray;
    
    NSString *_latitude; // 最终选择的坐标
    NSString *_longitude; // 最终选择的坐标
    NSString *_address;  // 最终选择的地址
    NSString *_name;     // 最终选择的名称
    NSString *_city;          // 最终选择的城市
    
    NSString *screenAddress; // 筛选重复地址
    BMKGeoCodeSearch *_geoCodeSearch;
}

@end

@implementation JCAddMapViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"位置";
    self.view.backgroundColor = [UIColor whiteColor];
    addressDataArray = [NSMutableArray arrayWithCapacity:1];
    mapPanGestureRecognizer = NO;
    [self loadMapView];
    if (!_isOnlyShowMap) {
        [self loadAddressTableView];
    }
    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    mapView.delegate = self;
}

- (void)dealloc {
    mapView.delegate = nil;
    locService.delegate = nil;
    _geoCodeSearch.delegate = nil;
}

#pragma mark - baiduMap
- (void)loadMapView {
    annotaionShow = NO;
    if (_isOnlyShowMap) {
        mapView = [[BMKMapView alloc]initWithFrame:CGRectMake(0, 64, kScreenWidth, kScreenHeight)];
        
    } else {
        mapView = [[BMKMapView alloc]initWithFrame:CGRectMake(0, 64, kScreenWidth, kScreenHeight/2)];
        
        UIImageView *labelImage = [[UIImageView alloc] initWithFrame:CGRectMake((kScreenWidth-25 )/2, kScreenHeight/4-33, 25, 33)];
        labelImage.image = [UIImage imageNamed:@"icon_baidu_marker"];
        [mapView addSubview:labelImage];
        
        locationBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        locationBtn.frame = CGRectMake(10, kScreenHeight/2-60, 50, 50);
        locationBtn.backgroundColor = [UIColor lightGrayColor];
        [locationBtn setTitle:@"定位" forState:UIControlStateNormal];
        [locationBtn addTarget:self action:@selector(locationBtnClick) forControlEvents:UIControlEventTouchUpInside];
        [mapView addSubview:locationBtn];
        locationBtn.hidden = YES;
    }
    mapView.zoomLevel = 19;
    mapView.logoPosition = BMKLogoPositionRightBottom;
    mapView.userTrackingMode = BMKUserTrackingModeFollow;
    [self.view addSubview:mapView];
    mapViewPan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(panEd:)];
    [mapView addGestureRecognizer:mapViewPan];
    
    // 定位
    locService = [[BMKLocationService alloc]init];
    locService.delegate = self;
    //启动LocationService
    [locService startUserLocationService];
}

// 使屏幕显示坐标回到大头针位置
- (void)locationBtnClick {
    CLLocationCoordinate2D coor;
    coor = pointAnnotation.coordinate;
    
    BMKCoordinateRegion region;
    region.center.latitude  = coor.latitude;
    region.center.longitude = coor.longitude;
    region.span.latitudeDelta  = 0;
    region.span.longitudeDelta = 0;
    [UIView animateWithDuration:1 animations:^{
        mapView.region = region;
    }];
    
}

#pragma mark 百度地图delegate
// 标注大头针
- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id <BMKAnnotation>)annotation
{
    if ([annotation isKindOfClass:[BMKPointAnnotation class]]) {
        BMKPinAnnotationView *newAnnotationView = [[BMKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"myAnnotation"];
        newAnnotationView.pinColor = BMKPinAnnotationColorPurple;
        newAnnotationView.animatesDrop = YES;// 设置该标注点动画显示
        return newAnnotationView;
    }
    return nil;
}

// 使地图仅在拖动停止时显示周边信息
- (void)panEd:(UIPanGestureRecognizer *)pan {
    mapPanGestureRecognizer = YES;
    [mapView removeGestureRecognizer:mapViewPan];
}
// 当地图停止拖动时显示周边信息
- (void)mapView:(BMKMapView *)view regionDidChangeAnimated:(BOOL)animated {
    
    if (!mapPanGestureRecognizer) {
        return;
    }
    
    // 把停止拖动时地图的中心点转换成大头针的经纬度
    BMKCoordinateRegion region = (BMKCoordinateRegion)view.region;
    
    //——————————初始化反地理编码类————————————
    //注意：必须先初始化地理编码类
    _geoCodeSearch = [[BMKGeoCodeSearch alloc]init];
    _geoCodeSearch.delegate = self;
    //初始化反地理编码类
    BMKReverseGeoCodeOption *reverseGeoCodeOption= [[BMKReverseGeoCodeOption alloc] init];
    //需要反地理编码的坐标位置
    reverseGeoCodeOption.reverseGeoPoint = CLLocationCoordinate2DMake(region.center.latitude, region.center.longitude);
    // 调用反地址编码方法，让其在代理方法中输出
    BOOL flag = [_geoCodeSearch reverseGeoCode:reverseGeoCodeOption];
    if (!flag) {
        [locService startUserLocationService];
    }
}

// 定位
//处理位置坐标更新
- (void)didUpdateBMKUserLocation:(BMKUserLocation *)userLocation {
    [locService stopUserLocationService];
    NSLog(@"当前的坐标是: %f,%f",userLocation.location.coordinate.latitude,userLocation.location.coordinate.longitude);

    //注意：必须先初始化地理编码类
    _geoCodeSearch = [[BMKGeoCodeSearch alloc]init];
    _geoCodeSearch.delegate = self;
    //初始化反地理编码类
    BMKReverseGeoCodeOption *reverseGeoCodeOption= [[BMKReverseGeoCodeOption alloc] init];
    //需要反地理编码的坐标位置
    reverseGeoCodeOption.reverseGeoPoint = CLLocationCoordinate2DMake(userLocation.location.coordinate.latitude, userLocation.location.coordinate.longitude);
    // 调用反地址编码方法，让其在代理方法中输出
    BOOL flag = [_geoCodeSearch reverseGeoCode:reverseGeoCodeOption];
    if (!flag) {
        [locService startUserLocationService];
    }else {
    }
}
#pragma mark 代理方法返回反地理编码结果
- (void)onGetReverseGeoCodeResult:(BMKGeoCodeSearch *)searcher result:(BMKReverseGeoCodeResult *)result errorCode:(BMKSearchErrorCode)error
{
    if (result) {
        [addressDataArray removeAllObjects];
        screenAddress = [NSString stringWithFormat:@"%@%@", result.addressDetail.streetName, result.addressDetail.streetNumber];
        
        _address = result.address;
        _name = result.addressDetail.streetName;
        _city = result.addressDetail.city;
        _latitude = [NSString stringWithFormat:@"%f", result.location.latitude];
        _longitude = [NSString stringWithFormat:@"%f", result.location.longitude];
        
        NSDictionary *dic = @{@"address":_address,@"name": _name, @"locationX":_latitude, @"locationY":_longitude, @"city":_city};
        [addressDataArray addObject:dic];
    }else{
        NSLog(@"找不到相对应的位置信息");
    }
    
    // 获取坐标周边信息
    int i = 0;
    for(BMKPoiInfo *poiInfo in result.poiList)
    {
        NSDictionary *dic = @{@"address":poiInfo.address, @"name":poiInfo.name, @"locationX":[NSString stringWithFormat:@"%f", poiInfo.pt.latitude], @"locationY":[NSString stringWithFormat:@"%f", poiInfo.pt.longitude], @"city":poiInfo.city};
        [addressDataArray addObject:dic];
        
        i++;
        if (i==result.poiList.count) {

            if (!annotaionShow) {
                annotaionShow = YES;
                locationBtn.hidden = NO;
                
                // 地图定位显示
                BMKCoordinateRegion region;
                
                if (_isOnlyShowMap) {
                    region.center.latitude  = _lat;
                    region.center.longitude = _lon;
                } else {
                    region.center.latitude  = [_latitude doubleValue];
                    region.center.longitude = [_longitude doubleValue];
                }
                
                
                region.span.latitudeDelta  = 0;
                region.span.longitudeDelta = 0;
                [UIView animateWithDuration:1 animations:^{
                    mapView.region = region;
                }];
                
                // 一开始显示的(大头针)
                pointAnnotation = [[BMKPointAnnotation alloc]init];
                CLLocationCoordinate2D coor;
                if (_isOnlyShowMap) {
                    coor.latitude = _lat;
                    coor.longitude = _lon;
                } else {
                    coor.latitude = [_latitude doubleValue];
                    coor.longitude = [_longitude doubleValue];
                }
                
                pointAnnotation.coordinate = coor;
                pointAnnotation.title = _name;
                [mapView addAnnotation:pointAnnotation];
            }
   
            [myTableView reloadData];
        }
    }
}

#pragma mark 地图底部tableView数据展示
- (void)loadAddressTableView {
    myTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, mapView.frame.origin.y+mapView.frame.size.height, kScreenWidth, kScreenHeight-mapView.frame.size.height-64) style:UITableViewStylePlain];
    myTableView.delegate = self;
    myTableView.dataSource = self;
    [self.view addSubview:myTableView];
}

#pragma mark - TableView delegate
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (myTableView.contentOffset.y > 0) {
        [tableView setContentOffset:CGPointMake(0,0) animated:YES];
    }
    return addressDataArray.count;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 60;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifier = @"cellId";
    
    AddMapTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (cell == nil) {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"AddMapTableViewCell" owner:nil options:nil];
        for (id oneObject in nib) {
            if ([oneObject isKindOfClass:[AddMapTableViewCell class]]) {
                cell = (AddMapTableViewCell *)oneObject;
            }
        }
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    
    cell.cellImage.image = [UIImage imageNamed:@"定位2.png"];
    
    cell.cellLabelText.text = [addressDataArray[indexPath.row] objectForKey:@"name"];
    cell.cellDetailTextLabelText.text = [addressDataArray[indexPath.row] objectForKey:@"address"];
    
    if (indexPath.row == 0) {
        cell.cellImage.image = [UIImage imageNamed:@"定位1.png"];
        cell.cellLabelText.textColor = [UIColor orangeColor];
        cell.cellDetailTextLabelText.textColor = [UIColor orangeColor];
    }
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSDictionary *dic = addressDataArray[indexPath.row];
    _address = [dic objectForKey:@"address"];
    _name = [dic objectForKey:@"name"];
    _city = [dic objectForKey:@"city"];
    _latitude = [dic objectForKey:@"locationX"];
    _longitude = [dic objectForKey:@"locationY"];
    [self back];
}

- (void)back {
    if (_address!= nil && _latitude!= nil && _longitude!= nil && _city!= nil) {
        if (_name == nil) {
            _name = _address;
        }
        NSDictionary *dic = @{@"address":_address,@"name":_name, @"lat":_latitude, @"lon":_longitude, @"city":_city};
        if (self.addressBlock) {
            self.addressBlock(dic);
        }
    }
    [self.navigationController popViewControllerAnimated:YES];
}

@end
