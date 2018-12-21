/*******************************************************************************
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 *
 * (c) Copyright IBM Corp. 1991, 2015 All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/

#ifndef jniport_h
#define jniport_h

#if defined(WIN32) || (defined(_WIN32)) || defined(RIM386) || (defined(BREW) && defined(AEE_SIMULATOR))

#define JNIEXPORT __declspec(dllexport)
#define JNICALL __stdcall
typedef signed char jbyte;
typedef int jint;
typedef __int64 jlong;

#else

#define JNIEXPORT 

typedef signed char jbyte;
typedef long long jlong;

#ifdef BREW
#include "AEEFile.h"
#define FILE IFile
#endif

typedef int jint;

#endif /* WIN32 */

#ifndef JNICALL
#define JNICALL
#endif

#ifndef JNIEXPORT
#define JNIEXPORT
#endif

#ifndef JNIIMPORT
#define JNIIMPORT
#endif

#ifdef _JNI_IMPLEMENTATION_
#define _JNI_IMPORT_OR_EXPORT_ JNIEXPORT
#else
#define _JNI_IMPORT_OR_EXPORT_ JNIIMPORT
#endif

#endif     /* jniport_h */
