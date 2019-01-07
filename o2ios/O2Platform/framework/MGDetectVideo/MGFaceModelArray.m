//
//  MGFaceModelArray.m
//  LandMask
//
//  Created by 张英堂 on 16/8/17.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import "MGFaceModelArray.h"

@implementation MGFaceModelArray

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.timeUsed = 0;
        self.detectRect = CGRectZero;
    }
    return self;
}

- (void)addModel:(MGFaceInfo *)model{
    [self.faceArray addObject:model];
}

- (MGFaceInfo *)modelWithIndex:(NSUInteger)index{
    MGFaceInfo *model = [self.faceArray objectAtIndex:index];

    return model;
}

-(NSMutableArray<MGFaceInfo *> *)faceArray{
    if (!_faceArray) {
        _faceArray = [NSMutableArray arrayWithCapacity:5];
    }
    return _faceArray;
}

-(NSUInteger)count{
    return self.faceArray.count;
}

- (NSString *)getDebugString{
    
    NSMutableString *tempString = [NSMutableString string];
    [tempString appendFormat:@"%s:%.2f ms", "关键点" ,self.timeUsed];
    [tempString appendFormat:@"\n%s:%.2f ms", "3D角度" ,self.AttributeTimeUsed];
    
    NSMutableString *conficeString = [NSMutableString string];

    if (self.count >= 1 ) {
        MGFaceInfo *model = [self modelWithIndex:0];
        [conficeString appendFormat:@"\n%s:%.2f", "质量", model.confidence];
        
        if (self.self.getFaceInfo) {
            [conficeString appendFormat:@"\n嘴巴:%zi", model.mouseStatus];
            [conficeString appendFormat:@"\n年龄:%.2f", model.age];
            [conficeString appendFormat:@"\n性别:%@", model.gender == 0 ? @"女":@"男"];
        }
    }

    [tempString appendString:conficeString];
    
    return tempString;
}

@end
