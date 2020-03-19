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

#import "HRColorUtil.h"

void HSVColorFromRGBColor(const HRRGBColor* rgb,HRHSVColor* hsv){
    HRRGBColor rgb255 = {rgb->r * 255.0f,rgb->g * 255.0f,rgb->b * 255.0f};
    HRHSVColor hsv255 = {0.0f,0.0f,0.0f};
    
    float max = rgb255.r;
    if (max < rgb255.g) {
        max = rgb255.g;
    }
    if (max < rgb255.b) {
        max = rgb255.b;
    }
    hsv255.v = max;
    
    float min = rgb255.r;
    if (min > rgb255.g) {
        min = rgb255.g;
    }
    if (min > rgb255.b) {
        min = rgb255.b;
    }
    
    if (max == 0.0f) {
        hsv255.h = 0.0f;
        hsv255.s = 0.0f;
    }else{
        hsv255.s = 255*(max - min)/(double)max;
        int h = 0.0f;
        if(max == rgb255.r){
            h = 60 * (rgb255.g - rgb255.b) / (double)(max - min);
        }else if(max == rgb255.g){
            h = 60 * (rgb255.b - rgb255.r) / (double)(max - min) + 120;
        }else{
            h = 60 * (rgb255.r - rgb255.g) / (double)(max - min) + 240;
        }
        if(h < 0) h += 360;
        hsv255.h = (float)h;
    }
    hsv->h = hsv255.h / 360.0f;
    hsv->s = hsv255.s / 255.0f;
    hsv->v = hsv255.v / 255.0f;
}


void RGBColorFromHSVColor(const HRHSVColor* hsv,HRRGBColor* rgb){
   /*
    UIColorには
    [UIColor colorWithHue:<#(CGFloat)#> saturation:<#(CGFloat)#> brightness:<#(CGFloat)#> alpha:<#(CGFloat)#>]
    もあります
    */
    if(hsv->s == 0.0f)
    {
        rgb->r = rgb->g = rgb->b = hsv->v;
        return;
    }
    
    float h360 = hsv->h * 360.0f;
    int		i;
    float	f;
    float	m;
    float	n;
    float	k;
    
    i = floor(h360 /60);
    if(i < 0){
        i *= -1;
    }
    f = h360 / 60.0f - i;
    m = hsv->v * (1 - hsv->s);
    n = hsv->v * (1 - f * hsv->s);
    k = hsv->v * (1 - (1 - f) * hsv->s);
    
    switch (i) {
        case 0:{
            rgb->r = hsv->v;
            rgb->g = k;
            rgb->b = m;
            break;
        }
        case 1:{
            rgb->r = n;
            rgb->g = hsv->v;
            rgb->b = m;
            break;
        }
        case 2:{
            rgb->r = m;
            rgb->g = hsv->v;
            rgb->b = k;
            break;
        }
        case 3:{
            rgb->r = m;
            rgb->g = n;
            rgb->b = hsv->v;
            break;
        }
        case 4:{
            rgb->r = k;
            rgb->g = m;
            rgb->b = hsv->v;
            break;
        }
        case 5:{
            rgb->r = hsv->v;
            rgb->g = m;
            rgb->b = n;
            break;
        }
        default:
            break;
    }
}

void RGBColorFromUIColor(const UIColor* uiColor,HRRGBColor* rgb){
    const CGFloat* components = CGColorGetComponents(uiColor.CGColor);
    if(CGColorGetNumberOfComponents(uiColor.CGColor) == 2){
        rgb->r = components[0];
        rgb->g = components[0];
        rgb->b = components[0];
    }else{
        rgb->r = components[0];
        rgb->g = components[1];
        rgb->b = components[2];
    }
}

int HexColorFromRGBColor(const HRRGBColor* rgb){
    return (int)(rgb->r*255.0f) << 16 | (int)(rgb->g*255.0f) << 8 | (int)(rgb->b*255.0f) << 0;
}

int HexColorFromUIColor(const UIColor* color){
    HRRGBColor rgb_color;
    RGBColorFromUIColor(color, &rgb_color);
    return HexColorFromRGBColor(&rgb_color);
}

bool HRHSVColorEqualToColor(const HRHSVColor* hsv1,const HRHSVColor* hsv2){
    return (hsv1->h == hsv2->h) && (hsv1->s == hsv2->s) && (hsv1->v == hsv2->v);
}

void HSVColorAt(HRHSVColor* hsv,float x,float y,float saturationUpperLimit,float brightness){
    hsv->h = x;
    hsv->s = 1.0f - (y * saturationUpperLimit);
    hsv->v = brightness;
}