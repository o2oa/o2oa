//
//  GLESUtils.h
//  Tutorial02
//
//  Created by kesalin on 12-11-25.
//  Copyright (c) 2012年 Created by kesalin@gmail.com on. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <OpenGLES/ES2/gl.h>
#import <OpenGLES/ES2/glext.h>


@interface GLESUtils : NSObject


+(GLuint)loadShader:(GLenum)type withString:(NSString *)shaderString;
+(GLuint)loadShader:(GLenum)type withFilepath:(NSString *)shaderFilepath;


+(GLuint)loadProgram:(NSString *)vertexShaderFilepath withFragmentShaderFilepath:(NSString *)fragmentShaderFilepath;

+ (const GLchar *)readFile:(NSString *)name;



GLint glueCompileShader(GLenum target, GLsizei count, const GLchar **sources, GLuint *shader);
GLint glueLinkProgram(GLuint program);
GLint glueValidateProgram(GLuint program);
GLint glueGetUniformLocation(GLuint program, const GLchar *name);

GLint glueCreateProgram(const GLchar *vertSource, const GLchar *fragSource,
                        GLsizei attribNameCt, const GLchar **attribNames,
                        const GLint *attribLocations,
                        GLsizei uniformNameCt, const GLchar **uniformNames,
                        GLint *uniformLocations,
                        GLuint *program);



@end
