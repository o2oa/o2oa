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

#import "HRCgUtil.h"

void HRSetRoundedRectanglePath(CGContextRef context,const CGRect rect,CGFloat radius){
    CGFloat lx = CGRectGetMinX(rect);
    CGFloat cx = CGRectGetMidX(rect);
    CGFloat rx = CGRectGetMaxX(rect);
    CGFloat by = CGRectGetMinY(rect);
    CGFloat cy = CGRectGetMidY(rect);
    CGFloat ty = CGRectGetMaxY(rect);
	
    CGContextMoveToPoint(context, lx, cy);
    CGContextAddArcToPoint(context, lx, by, cx, by, radius);
    CGContextAddArcToPoint(context, rx, by, rx, cy, radius);
    CGContextAddArcToPoint(context, rx, ty, cx, ty, radius);
    CGContextAddArcToPoint(context, lx, ty, lx, cy, radius);
    CGContextClosePath(context);
}

void HRDrawSquareColorBatch(CGContextRef context,CGPoint position,HRRGBColor* color,float size){
    float cx = position.x;
    float cy = position.y;
    
    float rRize = size;
    float backRSize = rRize + 3.0f;
    float shadowRSize = backRSize + 3.0f;
    
    CGRect rectEllipse = CGRectMake(cx - rRize, cy - rRize, rRize*2, rRize*2);
    CGRect rectBackEllipse = CGRectMake(cx - backRSize, cy - backRSize, backRSize*2, backRSize*2);
    CGRect rectShadowEllipse = CGRectMake(cx - shadowRSize, cy - shadowRSize, shadowRSize*2, shadowRSize*2);
    
    CGContextSaveGState(context);
    HRSetRoundedRectanglePath(context, rectBackEllipse,8.0f);
    CGContextClip(context);
    HRSetRoundedRectanglePath(context, rectShadowEllipse,8.0f);
    CGContextSetLineWidth(context, 5.5f);
    [[UIColor whiteColor] set];
    CGContextSetShadowWithColor(context, CGSizeMake(0.0f, 1.0f), 4.0f, [UIColor colorWithWhite:0.0f alpha:0.2f].CGColor);
    CGContextDrawPath(context, kCGPathStroke);
    CGContextRestoreGState(context);
    
    CGContextSaveGState(context);
    CGContextSetRGBFillColor(context, color->r, color->g, color->b, 1.0f);
    CGContextSetShadowWithColor(context, CGSizeMake(0.0f, 0.5f), 0.5f, [UIColor colorWithWhite:0.0f alpha:0.2f].CGColor);
    HRSetRoundedRectanglePath(context, rectEllipse,5.0f);
    CGContextDrawPath(context, kCGPathFill);
    CGContextRestoreGState(context);
}