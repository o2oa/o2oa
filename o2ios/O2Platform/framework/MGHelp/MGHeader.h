//
//  MGMacro.h
//

#ifndef text_MGMacro_h
#define text_MGMacro_h


#define MEGVII_EGG_MODEL  @"MegviiEGG_0_1_0_model"

#define WIN_WIDTH  [UIScreen mainScreen].bounds.size.width
#define WIN_HEIGHT [UIScreen mainScreen].bounds.size.height

#define MGColorWithRGB(R, G, B, A) [UIColor colorWithRed:R/255.0f green:G/255.0f blue:B/255.0f alpha:A]


#if DEBUG
#define MGLog(FORMAT, ...) fprintf(stderr,"%s:%d   \t%s\n",[[[NSString stringWithUTF8String:__FILE__] lastPathComponent] UTF8String], __LINE__, [[NSString stringWithFormat:FORMAT, ##__VA_ARGS__] UTF8String]);
#else
#define NSLog(FORMAT, ...) nil
#endif

#define MGLocalString(key) NSLocalizedStringFromTable(key, @"MGFaceDetection", nil)


#endif
