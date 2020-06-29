//
//  ConvertMp3.h
//  SwiftRecorder
//
//  Created by iOS on 2018/9/25.
//  Copyright © 2018 AidaHe. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ConvertMp3 : NSObject

- (void) audioPCMtoMP3:(NSString *)audioFileSavePath mp3File:(NSString *)mp3FilePath;
    
@end
