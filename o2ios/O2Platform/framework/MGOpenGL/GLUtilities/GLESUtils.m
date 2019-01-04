//
//  GLESUtils.m
//  Tutorial02
//
//  Created by kesalin@gmail.com on 12-11-25.
//  Copyright (c) 2012年 http://blog.csdn.net/kesalin/. All rights reserved.
//

#import "GLESUtils.h"

#define LogInfo printf
#define LogError printf


@implementation GLESUtils

+(GLuint)loadShader:(GLenum)type withFilepath:(NSString *)shaderFilepath
{
    NSError* error;
    NSString* shaderString = [NSString stringWithContentsOfFile:shaderFilepath 
                                                       encoding:NSUTF8StringEncoding
                                                          error:&error];
    if (!shaderString) {
        NSLog(@"Error: loading shader file: %@ %@", shaderFilepath, error.localizedDescription);
        return 0;
    }
    
    return [self loadShader:type withString:shaderString];
}

+(GLuint)loadShader:(GLenum)type withString:(NSString *)shaderString
{   
    // Create the shader object
    GLuint shader = glCreateShader(type);
    if (shader == 0) {
        NSLog(@"Error: failed to create shader.");
        return 0;
    }
    
    // Load the shader source
    const char * shaderStringUTF8 = [shaderString UTF8String];
    glShaderSource(shader, 1, &shaderStringUTF8, NULL);
    
    // Compile the shader
    glCompileShader(shader);
    
    // Check the compile status
    GLint compiled = 0;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    
    if (!compiled) {
        GLint infoLen = 0;
        glGetShaderiv ( shader, GL_INFO_LOG_LENGTH, &infoLen );
        
        if (infoLen > 1) {
            char * infoLog = malloc(sizeof(char) * infoLen);
            glGetShaderInfoLog (shader, infoLen, NULL, infoLog);
            NSLog(@"Error compiling shader:\n%s\n", infoLog );            
            
            free(infoLog);
        }
        
        glDeleteShader(shader);
        return 0;
    }

    return shader;
}

+(GLuint)loadProgram:(NSString *)vertexShaderFilepath withFragmentShaderFilepath:(NSString *)fragmentShaderFilepath
{
    // Load the vertex/fragment shaders
    GLuint vertexShader = [self loadShader:GL_VERTEX_SHADER
                              withFilepath:vertexShaderFilepath];
    if (vertexShader == 0)
        return 0;
    
    GLuint fragmentShader = [self loadShader:GL_FRAGMENT_SHADER
                                withFilepath:fragmentShaderFilepath];
    if (fragmentShader == 0) {
        glDeleteShader(vertexShader);
        return 0;
    }
    
    // Create the program object
    GLuint programHandle = glCreateProgram();
    if (programHandle == 0)
        return 0;
    
    glAttachShader(programHandle, vertexShader);
    glAttachShader(programHandle, fragmentShader);
    
    // Link the program
    glLinkProgram(programHandle);
    
    // Check the link status
    GLint linked;
    glGetProgramiv(programHandle, GL_LINK_STATUS, &linked);
    
    if (!linked) {
        GLint infoLen = 0;
        glGetProgramiv(programHandle, GL_INFO_LOG_LENGTH, &infoLen);
        
        if (infoLen > 1){
            char * infoLog = malloc(sizeof(char) * infoLen);
            glGetProgramInfoLog(programHandle, infoLen, NULL, infoLog);

            NSLog(@"Error linking program:\n%s\n", infoLog);            
            
            free(infoLog);
        }
        
        glDeleteProgram(programHandle );
        return 0;
    }
    
    // Free up no longer needed shader resources
    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);
    
    return programHandle;
}


+ (const GLchar *)readFile:(NSString *)name
{
    NSString *path;
    const GLchar *source;
    
    path = [[NSBundle mainBundle] pathForResource:name ofType: nil];
    source = (GLchar *)[[NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil] UTF8String];
    return source;
}


#pragma mark -
/* Compile a shader from the provided source(s) */
GLint glueCompileShader(GLenum target, GLsizei count, const GLchar **sources, GLuint *shader)
{
    GLint status;
    
    *shader = glCreateShader(target);
    glShaderSource(*shader, count, sources, NULL);
    glCompileShader(*shader);
    
#if defined(DEBUG)
    GLint logLength = 0;
    glGetShaderiv(*shader, GL_INFO_LOG_LENGTH, &logLength);
    if (logLength > 0)
    {
        GLchar *log = (GLchar *)malloc(logLength);
        glGetShaderInfoLog(*shader, logLength, &logLength, log);
        LogInfo("Shader compile log:\n%s", log);
        free(log);
    }
#endif
    
    glGetShaderiv(*shader, GL_COMPILE_STATUS, &status);
    if (status == 0)
    {
        int i;
        
        LogError("Failed to compile shader:\n");
        for (i = 0; i < count; i++)
            LogInfo("%s", sources[i]);
    }
    
    return status;
}


/* Link a program with all currently attached shaders */
GLint glueLinkProgram(GLuint program)
{
    GLint status;
    
    glLinkProgram(program);
    
#if defined(DEBUG)
    GLint logLength = 0;
    glGetProgramiv(program, GL_INFO_LOG_LENGTH, &logLength);
    if (logLength > 0)
    {
        GLchar *log = (GLchar *)malloc(logLength);
        glGetProgramInfoLog(program, logLength, &logLength, log);
        LogInfo("Program link log:\n%s", log);
        free(log);
    }
#endif
    
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if (status == 0)
        LogError("Failed to link program %d", program);
    
    return status;
}


/* Validate a program (for i.e. inconsistent samplers) */
GLint glueValidateProgram(GLuint program)
{
    GLint status;
    
    glValidateProgram(program);
    
#if defined(DEBUG)
    GLint logLength = 0;
    glGetProgramiv(program, GL_INFO_LOG_LENGTH, &logLength);
    if (logLength > 0)
    {
        GLchar *log = (GLchar *)malloc(logLength);
        glGetProgramInfoLog(program, logLength, &logLength, log);
        LogInfo("Program validate log:\n%s", log);
        free(log);
    }
#endif
    
    glGetProgramiv(program, GL_VALIDATE_STATUS, &status);
    if (status == 0)
        LogError("Failed to validate program %d", program);
    
    return status;
}


/* Return named uniform location after linking */
GLint glueGetUniformLocation(GLuint program, const GLchar *uniformName)
{
    GLint loc;
    
    loc = glGetUniformLocation(program, uniformName);
    
    return loc;
}


/* Convenience wrapper that compiles, links, enumerates uniforms and attribs */
GLint glueCreateProgram(const GLchar *vertSource, const GLchar *fragSource,
                        GLsizei attribNameCt, const GLchar **attribNames,
                        const GLint *attribLocations,
                        GLsizei uniformNameCt, const GLchar **uniformNames,
                        GLint *uniformLocations,
                        GLuint *program)
{
    GLuint vertShader = 0, fragShader = 0, prog = 0, status = 1, i;
    
    // Create shader program
    prog = glCreateProgram();
    
    // Create and compile vertex shader
    status *= glueCompileShader(GL_VERTEX_SHADER, 1, &vertSource, &vertShader);
    
    // Create and compile fragment shader
    status *= glueCompileShader(GL_FRAGMENT_SHADER, 1, &fragSource, &fragShader);
    
    // Attach vertex shader to program
    glAttachShader(prog, vertShader);
    
    // Attach fragment shader to program
    glAttachShader(prog, fragShader);
    
    // Bind attribute locations
    // This needs to be done prior to linking
    for (i = 0; i < attribNameCt; i++)
    {
        if(strlen(attribNames[i]))
            glBindAttribLocation(prog, attribLocations[i], attribNames[i]);
    }
    
    // Link program
    status *= glueLinkProgram(prog);
    
    // Get locations of uniforms
    if (status)
    {
        for(i = 0; i < uniformNameCt; i++)
        {
            if(strlen(uniformNames[i]))
                uniformLocations[i] = glueGetUniformLocation(prog, uniformNames[i]);
        }
        *program = prog;
    }
    
    // Release vertex and fragment shaders
    if (vertShader)
        glDeleteShader(vertShader);
    if (fragShader)
        glDeleteShader(fragShader);
    
    return status;
}

@end
