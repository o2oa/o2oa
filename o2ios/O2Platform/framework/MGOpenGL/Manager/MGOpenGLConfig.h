//
//  MGOpenGLConfig.h
//  LandMask
//
//  Created by 张英堂 on 16/8/17.
//  Copyright © 2016年 megvii. All rights reserved.
//

#ifndef MGOpenGLConfig_h
#define MGOpenGLConfig_h

#import <CoreMedia/CoreMedia.h>
#import <CoreVideo/CoreVideo.h>
#import <UIKit/UIKit.h>
#import <Availability.h>


enum {
    ATTRIB_VERTEX,
    ATTRIB_TEXTUREPOSITON,
    NUM_ATTRIBUTES
};

typedef struct {
    float Position[3];
    float TexCoord[2];
} Vertex;

static const GLfloat squareVertices[] = {
    -1.0f, -1.0f, // bottom left
    1.0f, -1.0f, // bottom right
    -1.0f,  1.0f, // top left
    1.0f,  1.0f, // top right
};
static const float textureVertices[] = {
    0.0f, 0.0f, // bottom left
    1.0f, 0.0f, // bottom right
    0.0f,  1.0f, // top left
    1.0f,  1.0f, // top right
};


#endif /* MGOpenGLConfig_h */
