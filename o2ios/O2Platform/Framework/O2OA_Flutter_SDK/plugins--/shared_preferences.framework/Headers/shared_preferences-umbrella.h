#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "SharedPreferencesPlugin.h"

FOUNDATION_EXPORT double shared_preferencesVersionNumber;
FOUNDATION_EXPORT const unsigned char shared_preferencesVersionString[];

