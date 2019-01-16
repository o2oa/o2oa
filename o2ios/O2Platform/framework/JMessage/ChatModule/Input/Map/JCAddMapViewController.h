
#import <UIKit/UIKit.h>

#import <BaiduMapAPI_Base/BMKBaseComponent.h>
#import <BaiduMapAPI_Map/BMKMapComponent.h>
#import <BaiduMapAPI_Search/BMKSearchComponent.h>
#import <BaiduMapAPI_Location/BMKLocationComponent.h>
#import <BaiduMapAPI_Utils/BMKUtilsComponent.h>
#import <BaiduMapAPI_Map/BMKMapView.h>
#import "AddMapTableViewCell.h"

typedef void (^ MapAddress)(NSDictionary *dic);
@interface JCAddMapViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, copy) MapAddress addressBlock;
@property (nonatomic, assign) BOOL isOnlyShowMap;
@property (nonatomic, assign) double lon;
@property (nonatomic, assign) double lat;

@end
