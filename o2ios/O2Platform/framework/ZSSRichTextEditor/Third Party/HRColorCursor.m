/*-
 * Copyright (c) 2011 Ryota Hayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR(S) ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $FreeBSD$
 */

#import "HRColorCursor.h"
#import "HRCgUtil.h"

@implementation HRColorCursor

+ (CGSize) cursorSize 
{
    return CGSizeMake(30.0, 30.0f);
}

+ (float) outlineSize
{
    return 4.0f;
}

+ (float) shadowSize
{
    return 2.0f;
}


- (id)initWithPoint:(CGPoint)point
{
    CGSize size = [HRColorCursor cursorSize];
    CGRect frame = CGRectMake(point.x, point.y, size.width, size.height);
    self = [super initWithFrame:frame];
    if (self) {
        [self setBackgroundColor:[UIColor clearColor]];
        [self setUserInteractionEnabled:FALSE];
        _currentColor.r = _currentColor.g = _currentColor.b = 1.0f;
    }
    return self;
}

- (void)setColorRed:(float)red andGreen:(float)green andBlue:(float)blue{
    _currentColor.r = red;
    _currentColor.g = green;
    _currentColor.b = blue;
    [self setNeedsDisplay];
}

- (void)drawRect:(CGRect)rect
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    float outlineSize = [HRColorCursor outlineSize];
    CGSize cursorSize = [HRColorCursor cursorSize];
    float shadowSize = [HRColorCursor shadowSize];
    
    CGContextSaveGState(context);
    HRSetRoundedRectanglePath(context, CGRectMake(shadowSize, shadowSize, cursorSize.width - shadowSize*2.0f, cursorSize.height - shadowSize*2.0f), 2.0f);
    [[UIColor whiteColor] set];
    CGContextSetShadow(context, CGSizeMake(0.0f, 1.0f), shadowSize);
    CGContextDrawPath(context, kCGPathFill);
    CGContextRestoreGState(context);
    
    
    [[UIColor colorWithRed:_currentColor.r green:_currentColor.g blue:_currentColor.b alpha:1.0f] set];
    CGContextFillRect(context, CGRectMake(outlineSize + shadowSize, outlineSize + shadowSize, cursorSize.width - (outlineSize + shadowSize)*2.0f, cursorSize.height - (outlineSize + shadowSize)*2.0f));
}


@end
