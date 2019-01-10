/*******************************************************************************
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 *
 * (c) Copyright IBM Corp. 1991, 2015 All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/

#ifndef JNI_H
#define JNI_H

#include <stdio.h>  /* For 1.1 version of args (i.e., vfprintf) */
#include <stdarg.h> /* For va_list */
#include "jniport.h"

#if defined JNI_JCL_H
#include "jni_jcl.h"
#endif

#ifdef __cplusplus
extern "C" {
#endif

/* Boolean values */

#define JNI_FALSE 0
#define JNI_TRUE 1

/* Codes for ReleaseXArrayElements */

#define JNI_COMMIT 1
#define JNI_ABORT 2

/* JNI version numbers */

#define JNI_VERSION_1_1 0x00010001
#define JNI_VERSION_1_2 0x00010002
#define JNI_VERSION_1_4 0x00010004
#define JNI_VERSION_1_6 0x00010006
#define JNI_VERSION_1_8 0x00010008

#define JVMEXT_VERSION_1_1 0x7E010001

/* Error codes */

#define JNI_OK 	0
#define JNI_ERR 			(-1)
#define JNI_EDETACHED		(-2)
#define JNI_EVERSION		(-3)
#define JNI_ENOMEM			(-4)
#define JNI_EEXIST			(-5)
#define JNI_EINVAL			(-6)

typedef unsigned char jboolean;
/* jbyte is platform specific */
typedef unsigned short jchar;
typedef short jshort;
/* jint is platform specific */
/* jlong is platform specific */
typedef float jfloat;
typedef double jdouble;
typedef jint jsize;

#ifdef __cplusplus
class _jobject {};
class _jweak : public _jobject {};
class _jclass : public _jobject {};
class _jstring : public _jobject {};
class _jthrowable : public _jobject {};
class _jarray : public _jobject {};
class _jobjectArray : public _jarray {};
class _jbooleanArray : public _jarray {};
class _jbyteArray : public _jarray {};
class _jcharArray : public _jarray {};
class _jshortArray : public _jarray {};
class _jintArray : public _jarray {};
class _jlongArray : public _jarray {};
class _jfloatArray : public _jarray {};
class _jdoubleArray : public _jarray {};
typedef _jobject * jobject;
typedef _jweak * jweak;
typedef _jclass * jclass;
typedef _jstring * jstring;
typedef _jthrowable * jthrowable;
typedef _jarray * jarray;
typedef _jobjectArray * jobjectArray;
typedef _jbooleanArray * jbooleanArray;
typedef _jbyteArray * jbyteArray;
typedef _jcharArray * jcharArray;
typedef _jshortArray * jshortArray;
typedef _jintArray * jintArray;
typedef _jlongArray * jlongArray;
typedef _jfloatArray * jfloatArray;
typedef _jdoubleArray * jdoubleArray;
#else
struct _jobject;
typedef struct _jobject * jobject;
typedef jobject jweak;
typedef jobject jclass;
typedef jobject jstring;
typedef jobject jthrowable;
typedef jobject jarray;
typedef jarray jobjectArray;
typedef jarray jbooleanArray;
typedef jarray jbyteArray;
typedef jarray jcharArray;
typedef jarray jshortArray;
typedef jarray jintArray;
typedef jarray jlongArray;
typedef jarray jfloatArray;
typedef jarray jdoubleArray;
#endif

struct _jfieldID;
typedef struct _jfieldID *jfieldID;
struct _jmethodID;
typedef struct _jmethodID *jmethodID;

/* Used for CallXXXMethodA API */
typedef union jvalue {
	jboolean z;
	jbyte    b;
	jchar    c;
	jshort   s;
	jint     i;
	jlong    j;
	jfloat   f;
	jdouble  d;
	jobject  l;
} jvalue;

typedef enum jobjectRefType {
	JNIInvalidRefType = 0,
	JNILocalRefType = 1,
	JNIGlobalRefType = 2,
	JNIWeakGlobalRefType = 3,

	jobjectRefTypeEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jobjectRefType;

typedef struct {
	char *name;
	char *signature;
	void *fnPtr;
} JNINativeMethod;

/**
 * JavaVMQuery structure
 */
struct JavaVMQuery;
typedef struct JavaVMQuery JavaVMQuery;

/**
 * used in QueryGCStatus.
 */
#define JNI_GCQUERY_NURSERY_HEAP (jint) 0x3
#define JNI_GCQUERY_MATURE_HEAP (jint) 0x4

/**
 * GCStatus structure.
 */
typedef struct GCStatus {
	jint heap;
	jint count;
	jlong freestorage; 
	jlong totalstorage;
} GCStatus;

struct JNINativeInterface_;
struct JNIEnv_;
struct JNIInvokeInterface_;
struct JavaVM_;
#ifdef __cplusplus
typedef  JNIEnv_ JNIEnv;
typedef JavaVM_ JavaVM;
#else
typedef const struct JNINativeInterface_ *JNIEnv;
typedef const struct JNIInvokeInterface_ *JavaVM;
#endif

struct JNINativeInterface_ {
	void *reserved0;
	void *reserved1;
	void *reserved2;
	void *reserved3;
	jint (JNICALL * GetVersion)(JNIEnv *env);
	jclass (JNICALL * DefineClass)(JNIEnv *env, const char *name, jobject loader, const jbyte *buf, jsize bufLen);
	jclass (JNICALL * FindClass)(JNIEnv *env, const char *name);
	jmethodID (JNICALL * FromReflectedMethod)(JNIEnv *env, jobject method);
	jfieldID (JNICALL * FromReflectedField)(JNIEnv *env, jobject field);
	jobject (JNICALL * ToReflectedMethod)(JNIEnv *env, jclass cls, jmethodID methodID, jboolean isStatic);
	jclass (JNICALL * GetSuperclass)(JNIEnv *env, jclass clazz);
	jboolean (JNICALL * IsAssignableFrom)(JNIEnv *env, jclass clazz1, jclass clazz2);
	jobject (JNICALL * ToReflectedField)(JNIEnv *env, jclass cls, jfieldID fieldID, jboolean isStatic);
	jint (JNICALL * Throw)(JNIEnv *env, jthrowable obj);
	jint (JNICALL * ThrowNew)(JNIEnv *env, jclass clazz, const char *message);
	jthrowable (JNICALL * ExceptionOccurred)(JNIEnv *env);
	void (JNICALL * ExceptionDescribe)(JNIEnv *env);
	void (JNICALL * ExceptionClear)(JNIEnv *env);
	void (JNICALL * FatalError)(JNIEnv *env, const char *msg);
	jint (JNICALL * PushLocalFrame)(JNIEnv *env, jint capacity);
	jobject (JNICALL * PopLocalFrame)(JNIEnv *env, jobject result);
	jobject (JNICALL * NewGlobalRef)(JNIEnv *env, jobject obj);
	void (JNICALL * DeleteGlobalRef)(JNIEnv *env, jobject gref);
	void (JNICALL * DeleteLocalRef)(JNIEnv *env, jobject localRef);
	jboolean (JNICALL * IsSameObject)(JNIEnv *env, jobject ref1, jobject ref2);
	jobject (JNICALL * NewLocalRef)(JNIEnv *env, jobject ref);
	jint (JNICALL * EnsureLocalCapacity)(JNIEnv *env, jint capacity);
	jobject (JNICALL * AllocObject)(JNIEnv *env, jclass clazz);
	jobject (JNICALL * NewObject)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	jobject (JNICALL * NewObjectV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	jobject (JNICALL * NewObjectA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args);
	jclass (JNICALL * GetObjectClass)(JNIEnv *env, jobject obj);
	jboolean (JNICALL * IsInstanceOf)(JNIEnv *env, jobject obj, jclass clazz);
	jmethodID (JNICALL * GetMethodID)(JNIEnv *env, jclass clazz, const char *name, const char *sig);
	jobject (JNICALL * CallObjectMethod)(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	jobject (JNICALL * CallObjectMethodV)(JNIEnv *env, jobject obj, jmethodID methodID, va_list args);
	jobject (JNICALL * CallObjectMethodA)(JNIEnv *env, jobject obj, jmethodID methodID, jvalue * args);
	jboolean (JNICALL * CallBooleanMethod)(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	jboolean (JNICALL * CallBooleanMethodV)(JNIEnv *env, jobject obj, jmethodID methodID, va_list args);
	jboolean (JNICALL * CallBooleanMethodA)(JNIEnv *env, jobject obj, jmethodID methodID, jvalue * args);
	jbyte (JNICALL * CallByteMethod)(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	jbyte (JNICALL * CallByteMethodV)(JNIEnv *env, jobject obj, jmethodID methodID, va_list args);
	jbyte (JNICALL * CallByteMethodA)(JNIEnv *env, jobject obj, jmethodID methodID, jvalue *args);
	jchar (JNICALL * CallCharMethod)(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	jchar (JNICALL * CallCharMethodV)(JNIEnv *env, jobject obj, jmethodID methodID, va_list args);
	jchar (JNICALL * CallCharMethodA)(JNIEnv *env, jobject obj, jmethodID methodID, jvalue *args);
	jshort (JNICALL * CallShortMethod)(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	jshort (JNICALL * CallShortMethodV)(JNIEnv *env, jobject obj, jmethodID methodID, va_list args);
	jshort (JNICALL * CallShortMethodA)(JNIEnv *env, jobject obj, jmethodID methodID, jvalue *args);
	jint (JNICALL * CallIntMethod)(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	jint (JNICALL * CallIntMethodV)(JNIEnv *env, jobject obj, jmethodID methodID, va_list args);
	jint (JNICALL * CallIntMethodA)(JNIEnv *env, jobject obj, jmethodID methodID, jvalue *args);
	jlong (JNICALL * CallLongMethod)(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	jlong (JNICALL * CallLongMethodV)(JNIEnv *env, jobject obj, jmethodID methodID, va_list args);
	jlong (JNICALL * CallLongMethodA)(JNIEnv *env, jobject obj, jmethodID methodID, jvalue *args);
	jfloat (JNICALL * CallFloatMethod)(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	jfloat (JNICALL * CallFloatMethodV)(JNIEnv *env, jobject obj, jmethodID methodID, va_list args);
	jfloat (JNICALL * CallFloatMethodA)(JNIEnv *env, jobject obj, jmethodID methodID, jvalue *args);
	jdouble (JNICALL * CallDoubleMethod)(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	jdouble (JNICALL * CallDoubleMethodV)(JNIEnv *env, jobject obj, jmethodID methodID, va_list args);
	jdouble (JNICALL * CallDoubleMethodA)(JNIEnv *env, jobject obj, jmethodID methodID, jvalue *args);
	void (JNICALL * CallVoidMethod)(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	void (JNICALL * CallVoidMethodV)(JNIEnv *env, jobject obj, jmethodID methodID, va_list args);
	void (JNICALL * CallVoidMethodA)(JNIEnv *env, jobject obj, jmethodID methodID, jvalue * args);
	jobject (JNICALL * CallNonvirtualObjectMethod)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...);
	jobject (JNICALL * CallNonvirtualObjectMethodV)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args);
	jobject (JNICALL * CallNonvirtualObjectMethodA)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, jvalue * args);
	jboolean (JNICALL * CallNonvirtualBooleanMethod)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...);
	jboolean (JNICALL * CallNonvirtualBooleanMethodV)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args);
	jboolean (JNICALL * CallNonvirtualBooleanMethodA)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, jvalue * args);
	jbyte (JNICALL * CallNonvirtualByteMethod)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...);
	jbyte (JNICALL * CallNonvirtualByteMethodV)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args);
	jbyte (JNICALL * CallNonvirtualByteMethodA)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, jvalue *args);
	jchar (JNICALL * CallNonvirtualCharMethod)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...);
	jchar (JNICALL * CallNonvirtualCharMethodV)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args);
	jchar (JNICALL * CallNonvirtualCharMethodA)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, jvalue *args);
	jshort (JNICALL * CallNonvirtualShortMethod)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...);
	jshort (JNICALL * CallNonvirtualShortMethodV)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args);
	jshort (JNICALL * CallNonvirtualShortMethodA)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, jvalue *args);
	jint (JNICALL * CallNonvirtualIntMethod)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...);
	jint (JNICALL * CallNonvirtualIntMethodV)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args);
	jint (JNICALL * CallNonvirtualIntMethodA)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, jvalue *args);
	jlong (JNICALL * CallNonvirtualLongMethod)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...);
	jlong (JNICALL * CallNonvirtualLongMethodV)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args);
	jlong (JNICALL * CallNonvirtualLongMethodA)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, jvalue *args);
	jfloat (JNICALL * CallNonvirtualFloatMethod)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...);
	jfloat (JNICALL * CallNonvirtualFloatMethodV)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args);
	jfloat (JNICALL * CallNonvirtualFloatMethodA)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, jvalue *args);
	jdouble (JNICALL * CallNonvirtualDoubleMethod)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...);
	jdouble (JNICALL * CallNonvirtualDoubleMethodV)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args);
	jdouble (JNICALL * CallNonvirtualDoubleMethodA)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, jvalue *args);
	void (JNICALL * CallNonvirtualVoidMethod)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...);
	void (JNICALL * CallNonvirtualVoidMethodV)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args);
	void (JNICALL * CallNonvirtualVoidMethodA)(JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, jvalue * args);
	jfieldID (JNICALL * GetFieldID)(JNIEnv *env, jclass clazz, const char *name, const char *sig);
	jobject (JNICALL * GetObjectField)(JNIEnv *env, jobject obj, jfieldID fieldID);
	jboolean (JNICALL * GetBooleanField)(JNIEnv *env, jobject obj, jfieldID fieldID);
	jbyte (JNICALL * GetByteField)(JNIEnv *env, jobject obj, jfieldID fieldID);
	jchar (JNICALL * GetCharField)(JNIEnv *env, jobject obj, jfieldID fieldID);
	jshort (JNICALL * GetShortField)(JNIEnv *env, jobject obj, jfieldID fieldID);
	jint (JNICALL * GetIntField)(JNIEnv *env, jobject obj, jfieldID fieldID);
	jlong (JNICALL * GetLongField)(JNIEnv *env, jobject obj, jfieldID fieldID);
	jfloat (JNICALL * GetFloatField)(JNIEnv *env, jobject obj, jfieldID fieldID);
	jdouble (JNICALL * GetDoubleField)(JNIEnv *env, jobject obj, jfieldID fieldID);
	void (JNICALL * SetObjectField)(JNIEnv *env, jobject obj, jfieldID fieldID, jobject value);
	void (JNICALL * SetBooleanField)(JNIEnv *env, jobject obj, jfieldID fieldID, jboolean value);
	void (JNICALL * SetByteField)(JNIEnv *env, jobject obj, jfieldID fieldID, jbyte value);
	void (JNICALL * SetCharField)(JNIEnv *env, jobject obj, jfieldID fieldID, jchar value);
	void (JNICALL * SetShortField)(JNIEnv *env, jobject obj, jfieldID fieldID, jshort value);
	void (JNICALL * SetIntField)(JNIEnv *env, jobject obj, jfieldID fieldID, jint value);
	void (JNICALL * SetLongField)(JNIEnv *env, jobject obj, jfieldID fieldID, jlong value);
	void (JNICALL * SetFloatField)(JNIEnv *env, jobject obj, jfieldID fieldID, jfloat value);
	void (JNICALL * SetDoubleField)(JNIEnv *env, jobject obj, jfieldID fieldID, jdouble value);
	jmethodID (JNICALL * GetStaticMethodID)(JNIEnv *env, jclass clazz, const char *name, const char *sig);
	jobject (JNICALL * CallStaticObjectMethod)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	jobject (JNICALL * CallStaticObjectMethodV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	jobject (JNICALL * CallStaticObjectMethodA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args);
	jboolean (JNICALL * CallStaticBooleanMethod)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	jboolean (JNICALL * CallStaticBooleanMethodV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	jboolean (JNICALL * CallStaticBooleanMethodA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args);
	jbyte (JNICALL * CallStaticByteMethod)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	jbyte (JNICALL * CallStaticByteMethodV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	jbyte (JNICALL * CallStaticByteMethodA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args);
	jchar (JNICALL * CallStaticCharMethod)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	jchar (JNICALL * CallStaticCharMethodV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	jchar (JNICALL * CallStaticCharMethodA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args);
	jshort (JNICALL * CallStaticShortMethod)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	jshort (JNICALL * CallStaticShortMethodV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	jshort (JNICALL * CallStaticShortMethodA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args);
	jint (JNICALL * CallStaticIntMethod)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	jint (JNICALL * CallStaticIntMethodV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	jint (JNICALL * CallStaticIntMethodA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args);
	jlong (JNICALL * CallStaticLongMethod)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	jlong (JNICALL * CallStaticLongMethodV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	jlong (JNICALL * CallStaticLongMethodA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args);
	jfloat (JNICALL * CallStaticFloatMethod)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	jfloat (JNICALL * CallStaticFloatMethodV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	jfloat (JNICALL * CallStaticFloatMethodA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args);
	jdouble (JNICALL * CallStaticDoubleMethod)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	jdouble (JNICALL * CallStaticDoubleMethodV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	jdouble (JNICALL * CallStaticDoubleMethodA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args);
	void (JNICALL * CallStaticVoidMethod)(JNIEnv *env, jclass clazz, jmethodID methodID, ...);
	void (JNICALL * CallStaticVoidMethodV)(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args);
	void (JNICALL * CallStaticVoidMethodA)(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue * args);
	jfieldID (JNICALL * GetStaticFieldID)(JNIEnv *env, jclass clazz, const char *name, const char *sig);
	jobject (JNICALL * GetStaticObjectField)(JNIEnv *env, jclass clazz, jfieldID fieldID);
	jboolean (JNICALL * GetStaticBooleanField)(JNIEnv *env, jclass clazz, jfieldID fieldID);
	jbyte (JNICALL * GetStaticByteField)(JNIEnv *env, jclass clazz, jfieldID fieldID);
	jchar (JNICALL * GetStaticCharField)(JNIEnv *env, jclass clazz, jfieldID fieldID);
	jshort (JNICALL * GetStaticShortField)(JNIEnv *env, jclass clazz, jfieldID fieldID);
	jint (JNICALL * GetStaticIntField)(JNIEnv *env, jclass clazz, jfieldID fieldID);
	jlong (JNICALL * GetStaticLongField)(JNIEnv *env, jclass clazz, jfieldID fieldID);
	jfloat (JNICALL * GetStaticFloatField)(JNIEnv *env, jclass clazz, jfieldID fieldID);
	jdouble (JNICALL * GetStaticDoubleField)(JNIEnv *env, jclass clazz, jfieldID fieldID);
	void (JNICALL * SetStaticObjectField)(JNIEnv *env, jclass clazz, jfieldID fieldID, jobject value);
	void (JNICALL * SetStaticBooleanField)(JNIEnv *env, jclass clazz, jfieldID fieldID, jboolean value);
	void (JNICALL * SetStaticByteField)(JNIEnv *env, jclass clazz, jfieldID fieldID, jbyte value);
	void (JNICALL * SetStaticCharField)(JNIEnv *env, jclass clazz, jfieldID fieldID, jchar value);
	void (JNICALL * SetStaticShortField)(JNIEnv *env, jclass clazz, jfieldID fieldID, jshort value);
	void (JNICALL * SetStaticIntField)(JNIEnv *env, jclass clazz, jfieldID fieldID, jint value);
	void (JNICALL * SetStaticLongField)(JNIEnv *env, jclass clazz, jfieldID fieldID, jlong value);
	void (JNICALL * SetStaticFloatField)(JNIEnv *env, jclass clazz, jfieldID fieldID, jfloat value);
	void (JNICALL * SetStaticDoubleField)(JNIEnv *env, jclass clazz, jfieldID fieldID, jdouble value);
	jstring (JNICALL * NewString)(JNIEnv *env, const jchar *unicodeChars, jsize len);
	jsize (JNICALL * GetStringLength)(JNIEnv *env, jstring string);
	const jchar *(JNICALL * GetStringChars)(JNIEnv *env, jstring string, jboolean *isCopy);
	void (JNICALL * ReleaseStringChars)(JNIEnv *env, jstring string, const jchar *utf);
	jstring (JNICALL * NewStringUTF)(JNIEnv *env, const char *bytes);
	jsize (JNICALL * GetStringUTFLength)(JNIEnv *env, jstring string);
	const char* (JNICALL * GetStringUTFChars)(JNIEnv *env, jstring string, jboolean *isCopy);
	void (JNICALL * ReleaseStringUTFChars)(JNIEnv *env, jstring string, const char* utf);
	jsize (JNICALL * GetArrayLength)(JNIEnv *env, jarray array);
	jobjectArray (JNICALL * NewObjectArray)(JNIEnv *env, jsize length, jclass clazz, jobject initialElement);
	jobject (JNICALL * GetObjectArrayElement)(JNIEnv *env, jobjectArray array, jsize index);
	void (JNICALL * SetObjectArrayElement)(JNIEnv *env, jobjectArray array, jsize index, jobject value);
	jbooleanArray (JNICALL * NewBooleanArray)(JNIEnv *env, jsize length);
	jbyteArray (JNICALL * NewByteArray)(JNIEnv *env, jsize length);
	jcharArray (JNICALL * NewCharArray)(JNIEnv *env, jsize length);
	jshortArray (JNICALL * NewShortArray)(JNIEnv *env, jsize length);
	jintArray (JNICALL * NewIntArray)(JNIEnv *env, jsize length);
	jlongArray (JNICALL * NewLongArray)(JNIEnv *env, jsize length);
	jfloatArray (JNICALL * NewFloatArray)(JNIEnv *env, jsize length);
	jdoubleArray (JNICALL * NewDoubleArray)(JNIEnv *env, jsize length);
	jboolean * (JNICALL * GetBooleanArrayElements)(JNIEnv *env, jbooleanArray array, jboolean *isCopy);
	jbyte * (JNICALL * GetByteArrayElements)(JNIEnv *env, jbyteArray array, jboolean *isCopy);
	jchar * (JNICALL * GetCharArrayElements)(JNIEnv *env, jcharArray array, jboolean *isCopy);
	jshort * (JNICALL * GetShortArrayElements)(JNIEnv *env, jshortArray array, jboolean *isCopy);
	jint * (JNICALL * GetIntArrayElements)(JNIEnv *env, jintArray array, jboolean *isCopy);
	jlong * (JNICALL * GetLongArrayElements)(JNIEnv *env, jlongArray array, jboolean *isCopy);
	jfloat * (JNICALL * GetFloatArrayElements)(JNIEnv *env, jfloatArray array, jboolean *isCopy);
	jdouble * (JNICALL * GetDoubleArrayElements)(JNIEnv *env, jdoubleArray array, jboolean *isCopy);
	void (JNICALL * ReleaseBooleanArrayElements)(JNIEnv *env, jbooleanArray array, jboolean *elems, jint mode);
	void (JNICALL * ReleaseByteArrayElements)(JNIEnv *env, jbyteArray array, jbyte *elems, jint mode);
	void (JNICALL * ReleaseCharArrayElements)(JNIEnv *env, jcharArray array, jchar *elems, jint mode);
	void (JNICALL * ReleaseShortArrayElements)(JNIEnv *env, jshortArray array, jshort *elems, jint mode);
	void (JNICALL * ReleaseIntArrayElements)(JNIEnv *env, jintArray array, jint *elems, jint mode);
	void (JNICALL * ReleaseLongArrayElements)(JNIEnv *env, jlongArray array, jlong *elems, jint mode);
	void (JNICALL * ReleaseFloatArrayElements)(JNIEnv *env, jfloatArray array, jfloat *elems, jint mode);
	void (JNICALL * ReleaseDoubleArrayElements)(JNIEnv *env, jdoubleArray array, jdouble *elems, jint mode);
	void (JNICALL * GetBooleanArrayRegion)(JNIEnv *env, jbooleanArray array, jsize start, jsize len, jboolean *buf);
	void (JNICALL * GetByteArrayRegion)(JNIEnv *env, jbyteArray array, jsize start, jsize len, jbyte *buf);
	void (JNICALL * GetCharArrayRegion)(JNIEnv *env, jcharArray array, jsize start, jsize len, jchar *buf);
	void (JNICALL * GetShortArrayRegion)(JNIEnv *env, jshortArray array, jsize start, jsize len, jshort *buf);
	void (JNICALL * GetIntArrayRegion)(JNIEnv *env, jintArray array, jsize start, jsize len, jint *buf);
	void (JNICALL * GetLongArrayRegion)(JNIEnv *env, jlongArray array, jsize start, jsize len, jlong *buf);
	void (JNICALL * GetFloatArrayRegion)(JNIEnv *env, jfloatArray array, jsize start, jsize len, jfloat *buf);
	void (JNICALL * GetDoubleArrayRegion)(JNIEnv *env, jdoubleArray array, jsize start, jsize len, jdouble *buf);
	void (JNICALL * SetBooleanArrayRegion)(JNIEnv *env, jbooleanArray array, jsize start, jsize len, jboolean *buf);
	void (JNICALL * SetByteArrayRegion)(JNIEnv *env, jbyteArray array, jsize start, jsize len, jbyte *buf);
	void (JNICALL * SetCharArrayRegion)(JNIEnv *env, jcharArray array, jsize start, jsize len, jchar *buf);
	void (JNICALL * SetShortArrayRegion)(JNIEnv *env, jshortArray array, jsize start, jsize len, jshort *buf);
	void (JNICALL * SetIntArrayRegion)(JNIEnv *env, jintArray array, jsize start, jsize len, jint *buf);
	void (JNICALL * SetLongArrayRegion)(JNIEnv *env, jlongArray array, jsize start, jsize len, jlong *buf);
	void (JNICALL * SetFloatArrayRegion)(JNIEnv *env, jfloatArray array, jsize start, jsize len, jfloat *buf);
	void (JNICALL * SetDoubleArrayRegion)(JNIEnv *env, jdoubleArray array, jsize start, jsize len, jdouble *buf);
	jint (JNICALL * RegisterNatives)(JNIEnv *env, jclass clazz, const JNINativeMethod *methods, jint nMethods);
	jint (JNICALL * UnregisterNatives)(JNIEnv *env, jclass clazz);
	jint (JNICALL * MonitorEnter)(JNIEnv *env, jobject obj);
	jint (JNICALL * MonitorExit)(JNIEnv *env, jobject obj);
	jint (JNICALL * GetJavaVM)(JNIEnv *env, JavaVM **vm);
	void (JNICALL * GetStringRegion)(JNIEnv *env, jstring str, jsize start, jsize len, jchar *buf);
	void (JNICALL * GetStringUTFRegion)(JNIEnv *env, jstring str, jsize start, jsize len, char *buf);
	void * (JNICALL * GetPrimitiveArrayCritical)(JNIEnv *env, jarray array, jboolean *isCopy);
	void (JNICALL * ReleasePrimitiveArrayCritical)(JNIEnv *env, jarray array, void *carray, jint mode);
	const jchar * (JNICALL * GetStringCritical)(JNIEnv *env, jstring string, jboolean *isCopy);
	void (JNICALL * ReleaseStringCritical)(JNIEnv *env, jstring string, const jchar *carray);
	jweak (JNICALL * NewWeakGlobalRef)(JNIEnv *env, jobject obj);
	void (JNICALL * DeleteWeakGlobalRef)(JNIEnv *env, jweak obj);
	jboolean (JNICALL * ExceptionCheck)(JNIEnv *env);
	jobject (JNICALL * NewDirectByteBuffer)(JNIEnv *env, void *address, jlong capacity);
	void * (JNICALL * GetDirectBufferAddress)(JNIEnv *env, jobject buf);
	jlong (JNICALL * GetDirectBufferCapacity)(JNIEnv *env, jobject buf);
	jobjectRefType (JNICALL * GetObjectRefType)(JNIEnv* env, jobject obj);
};
struct JNIEnv_ {
	const struct JNINativeInterface_ *functions;
	void *reserved0;
	void *reserved1[6];
#ifdef __cplusplus
	jint GetVersion() { return functions->GetVersion(this); }
	jclass DefineClass(const char *name, jobject loader, const jbyte *buf, jsize bufLen) { return functions->DefineClass(this, name, loader, buf, bufLen); }
	jclass FindClass(const char *name) { return functions->FindClass(this, name); }
	jmethodID FromReflectedMethod(jobject method) { return functions->FromReflectedMethod(this, method); }
	jfieldID FromReflectedField(jobject field) { return functions->FromReflectedField(this, field); }
	jobject ToReflectedMethod(jclass cls, jmethodID methodID, jboolean isStatic) { return functions->ToReflectedMethod(this, cls, methodID, isStatic); }
	jclass GetSuperclass(jclass clazz) { return functions->GetSuperclass(this, clazz); }
	jboolean IsAssignableFrom(jclass clazz1, jclass clazz2) { return functions->IsAssignableFrom(this, clazz1, clazz2); }
	jobject ToReflectedField(jclass cls, jfieldID fieldID, jboolean isStatic) { return functions->ToReflectedField(this, cls, fieldID, isStatic); }
	jint Throw(jthrowable obj) { return functions->Throw(this, obj); }
	jint ThrowNew(jclass clazz, const char *message) { return functions->ThrowNew(this, clazz, message); }
	jthrowable ExceptionOccurred() { return functions->ExceptionOccurred(this); }
	void ExceptionDescribe() { functions->ExceptionDescribe(this); }
	void ExceptionClear() { functions->ExceptionClear(this); }
	void FatalError(const char *msg) { functions->FatalError(this, msg); }
	jint PushLocalFrame(jint capacity) { return functions->PushLocalFrame(this, capacity); }
	jobject PopLocalFrame(jobject result) { return functions->PopLocalFrame(this, result); }
	jobject NewGlobalRef(jobject obj) { return functions->NewGlobalRef(this, obj); }
	void DeleteGlobalRef(jobject gref) { functions->DeleteGlobalRef(this, gref); }
	void DeleteLocalRef(jobject localRef) { functions->DeleteLocalRef(this, localRef); }
	jboolean IsSameObject(jobject ref1, jobject ref2) { return functions->IsSameObject(this, ref1, ref2); }
	jobject NewLocalRef(jobject ref) { return functions->NewLocalRef(this, ref); }
	jint EnsureLocalCapacity(jint capacity) { return functions->EnsureLocalCapacity(this, capacity); }
	jobject AllocObject(jclass clazz) { return functions->AllocObject(this, clazz); }
	jobject NewObject(jclass clazz, jmethodID methodID, ...) { jobject retval; va_list parms; va_start(parms, methodID); retval = functions->NewObjectV(this, clazz, methodID, parms); va_end(parms); return retval; }
	jobject NewObjectV(jclass clazz, jmethodID methodID, va_list args) { return functions->NewObjectV(this, clazz, methodID, args); }
	jobject NewObjectA(jclass clazz, jmethodID methodID, jvalue *args) { return functions->NewObjectA(this, clazz, methodID, args); }
	jclass GetObjectClass(jobject obj) { return functions->GetObjectClass(this, obj); }
	jboolean IsInstanceOf(jobject obj, jclass clazz) { return functions->IsInstanceOf(this, obj, clazz); }
	jmethodID GetMethodID(jclass clazz, const char *name, const char *sig) { return functions->GetMethodID(this, clazz, name, sig); }
	jobject CallObjectMethod(jobject obj, jmethodID methodID, ...) { jobject retval; va_list parms; va_start(parms, methodID); retval = functions->CallObjectMethodV(this, obj, methodID, parms); va_end(parms); return retval; }
	jobject CallObjectMethodV(jobject obj, jmethodID methodID, va_list args) { return functions->CallObjectMethodV(this, obj, methodID, args); }
	jobject CallObjectMethodA(jobject obj, jmethodID methodID, jvalue * args) { return functions->CallObjectMethodA(this, obj, methodID,  args); }
	jboolean CallBooleanMethod(jobject obj, jmethodID methodID, ...) { jboolean retval; va_list parms; va_start(parms, methodID); retval = functions->CallBooleanMethodV(this, obj, methodID, parms); va_end(parms); return retval; }
	jboolean CallBooleanMethodV(jobject obj, jmethodID methodID, va_list args) { return functions->CallBooleanMethodV(this, obj, methodID, args); }
	jboolean CallBooleanMethodA(jobject obj, jmethodID methodID, jvalue * args) { return functions->CallBooleanMethodA(this, obj, methodID,  args); }
	jbyte CallByteMethod(jobject obj, jmethodID methodID, ...) { jbyte retval; va_list parms; va_start(parms, methodID); retval = functions->CallByteMethodV(this, obj, methodID, parms); va_end(parms); return retval; }
	jbyte CallByteMethodV(jobject obj, jmethodID methodID, va_list args) { return functions->CallByteMethodV(this, obj, methodID, args); }
	jbyte CallByteMethodA(jobject obj, jmethodID methodID, jvalue *args) { return functions->CallByteMethodA(this, obj, methodID, args); }
	jchar CallCharMethod(jobject obj, jmethodID methodID, ...) { jchar retval; va_list parms; va_start(parms, methodID); retval = functions->CallCharMethodV(this, obj, methodID, parms); va_end(parms); return retval; }
	jchar CallCharMethodV(jobject obj, jmethodID methodID, va_list args) { return functions->CallCharMethodV(this, obj, methodID, args); }
	jchar CallCharMethodA(jobject obj, jmethodID methodID, jvalue *args) { return functions->CallCharMethodA(this, obj, methodID, args); }
	jshort CallShortMethod(jobject obj, jmethodID methodID, ...) { jshort retval; va_list parms; va_start(parms, methodID); retval = functions->CallShortMethodV(this, obj, methodID, parms); va_end(parms); return retval; }
	jshort CallShortMethodV(jobject obj, jmethodID methodID, va_list args) { return functions->CallShortMethodV(this, obj, methodID, args); }
	jshort CallShortMethodA(jobject obj, jmethodID methodID, jvalue *args) { return functions->CallShortMethodA(this, obj, methodID, args); }
	jint CallIntMethod(jobject obj, jmethodID methodID, ...) { jint retval; va_list parms; va_start(parms, methodID); retval = functions->CallIntMethodV(this, obj, methodID, parms); va_end(parms); return retval; }
	jint CallIntMethodV(jobject obj, jmethodID methodID, va_list args) { return functions->CallIntMethodV(this, obj, methodID, args); }
	jint CallIntMethodA(jobject obj, jmethodID methodID, jvalue *args) { return functions->CallIntMethodA(this, obj, methodID, args); }
	jlong CallLongMethod(jobject obj, jmethodID methodID, ...) { jlong retval; va_list parms; va_start(parms, methodID); retval = functions->CallLongMethodV(this, obj, methodID, parms); va_end(parms); return retval; }
	jlong CallLongMethodV(jobject obj, jmethodID methodID, va_list args) { return functions->CallLongMethodV(this, obj, methodID, args); }
	jlong CallLongMethodA(jobject obj, jmethodID methodID, jvalue *args) { return functions->CallLongMethodA(this, obj, methodID, args); }
	jfloat CallFloatMethod(jobject obj, jmethodID methodID, ...) { jfloat retval; va_list parms; va_start(parms, methodID); retval = functions->CallFloatMethodV(this, obj, methodID, parms); va_end(parms); return retval; }
	jfloat CallFloatMethodV(jobject obj, jmethodID methodID, va_list args) { return functions->CallFloatMethodV(this, obj, methodID, args); }
	jfloat CallFloatMethodA(jobject obj, jmethodID methodID, jvalue *args) { return functions->CallFloatMethodA(this, obj, methodID, args); }
	jdouble CallDoubleMethod(jobject obj, jmethodID methodID, ...) { jdouble retval; va_list parms; va_start(parms, methodID); retval = functions->CallDoubleMethodV(this, obj, methodID, parms); va_end(parms); return retval; }
	jdouble CallDoubleMethodV(jobject obj, jmethodID methodID, va_list args) { return functions->CallDoubleMethodV(this, obj, methodID, args); }
	jdouble CallDoubleMethodA(jobject obj, jmethodID methodID, jvalue *args) { return functions->CallDoubleMethodA(this, obj, methodID, args); }
	void CallVoidMethod(jobject obj, jmethodID methodID, ...) { va_list parms; va_start(parms, methodID); functions->CallVoidMethodV(this, obj, methodID, parms); va_end(parms); }
	void CallVoidMethodV(jobject obj, jmethodID methodID, va_list args) { functions->CallVoidMethodV(this, obj, methodID, args); }
	void CallVoidMethodA(jobject obj, jmethodID methodID, jvalue * args) { functions->CallVoidMethodA(this, obj, methodID,  args); }
	jobject CallNonvirtualObjectMethod(jobject obj, jclass clazz, jmethodID methodID, ...) { jobject retval; va_list parms; va_start(parms, methodID); retval = functions->CallNonvirtualObjectMethodV(this, obj, clazz, methodID, parms); va_end(parms); return retval; }
	jobject CallNonvirtualObjectMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) { return functions->CallNonvirtualObjectMethodV(this, obj, clazz, methodID, args); }
	jobject CallNonvirtualObjectMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) { return functions->CallNonvirtualObjectMethodA(this, obj, clazz, methodID,  args); }
	jboolean CallNonvirtualBooleanMethod(jobject obj, jclass clazz, jmethodID methodID, ...) { jboolean retval; va_list parms; va_start(parms, methodID); retval = functions->CallNonvirtualBooleanMethodV(this, obj, clazz, methodID, parms); va_end(parms); return retval; }
	jboolean CallNonvirtualBooleanMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) { return functions->CallNonvirtualBooleanMethodV(this, obj, clazz, methodID, args); }
	jboolean CallNonvirtualBooleanMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) { return functions->CallNonvirtualBooleanMethodA(this, obj, clazz, methodID,  args); }
	jbyte CallNonvirtualByteMethod(jobject obj, jclass clazz, jmethodID methodID, ...) { jbyte retval; va_list parms; va_start(parms, methodID); retval = functions->CallNonvirtualByteMethodV(this, obj, clazz, methodID, parms); va_end(parms); return retval; }
	jbyte CallNonvirtualByteMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) { return functions->CallNonvirtualByteMethodV(this, obj, clazz, methodID, args); }
	jbyte CallNonvirtualByteMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallNonvirtualByteMethodA(this, obj, clazz, methodID, args); }
	jchar CallNonvirtualCharMethod(jobject obj, jclass clazz, jmethodID methodID, ...) { jchar retval; va_list parms; va_start(parms, methodID); retval = functions->CallNonvirtualCharMethodV(this, obj, clazz, methodID, parms); va_end(parms); return retval; }
	jchar CallNonvirtualCharMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) { return functions->CallNonvirtualCharMethodV(this, obj, clazz, methodID, args); }
	jchar CallNonvirtualCharMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallNonvirtualCharMethodA(this, obj, clazz, methodID, args); }
	jshort CallNonvirtualShortMethod(jobject obj, jclass clazz, jmethodID methodID, ...) { jshort retval; va_list parms; va_start(parms, methodID); retval = functions->CallNonvirtualShortMethodV(this, obj, clazz, methodID, parms); va_end(parms); return retval; }
	jshort CallNonvirtualShortMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) { return functions->CallNonvirtualShortMethodV(this, obj, clazz, methodID, args); }
	jshort CallNonvirtualShortMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallNonvirtualShortMethodA(this, obj, clazz, methodID, args); }
	jint CallNonvirtualIntMethod(jobject obj, jclass clazz, jmethodID methodID, ...) { jint retval; va_list parms; va_start(parms, methodID); retval = functions->CallNonvirtualIntMethodV(this, obj, clazz, methodID, parms); va_end(parms); return retval; }
	jint CallNonvirtualIntMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) { return functions->CallNonvirtualIntMethodV(this, obj, clazz, methodID, args); }
	jint CallNonvirtualIntMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallNonvirtualIntMethodA(this, obj, clazz, methodID, args); }
	jlong CallNonvirtualLongMethod(jobject obj, jclass clazz, jmethodID methodID, ...) { jlong retval; va_list parms; va_start(parms, methodID); retval = functions->CallNonvirtualLongMethodV(this, obj, clazz, methodID, parms); va_end(parms); return retval; }
	jlong CallNonvirtualLongMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) { return functions->CallNonvirtualLongMethodV(this, obj, clazz, methodID, args); }
	jlong CallNonvirtualLongMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallNonvirtualLongMethodA(this, obj, clazz, methodID, args); }
	jfloat CallNonvirtualFloatMethod(jobject obj, jclass clazz, jmethodID methodID, ...) { jfloat retval; va_list parms; va_start(parms, methodID); retval = functions->CallNonvirtualFloatMethodV(this, obj, clazz, methodID, parms); va_end(parms); return retval; }
	jfloat CallNonvirtualFloatMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) { return functions->CallNonvirtualFloatMethodV(this, obj, clazz, methodID, args); }
	jfloat CallNonvirtualFloatMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallNonvirtualFloatMethodA(this, obj, clazz, methodID, args); }
	jdouble CallNonvirtualDoubleMethod(jobject obj, jclass clazz, jmethodID methodID, ...) { jdouble retval; va_list parms; va_start(parms, methodID); retval = functions->CallNonvirtualDoubleMethodV(this, obj, clazz, methodID, parms); va_end(parms); return retval; }
	jdouble CallNonvirtualDoubleMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) { return functions->CallNonvirtualDoubleMethodV(this, obj, clazz, methodID, args); }
	jdouble CallNonvirtualDoubleMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallNonvirtualDoubleMethodA(this, obj, clazz, methodID, args); }
	void CallNonvirtualVoidMethod(jobject obj, jclass clazz, jmethodID methodID, ...) { va_list parms; va_start(parms, methodID); functions->CallNonvirtualVoidMethodV(this, obj, clazz, methodID, parms); va_end(parms); }
	void CallNonvirtualVoidMethodV(jobject obj, jclass clazz, jmethodID methodID, va_list args) { functions->CallNonvirtualVoidMethodV(this, obj, clazz, methodID, args); }
	void CallNonvirtualVoidMethodA(jobject obj, jclass clazz, jmethodID methodID, jvalue * args) { functions->CallNonvirtualVoidMethodA(this, obj, clazz, methodID,  args); }
	jfieldID GetFieldID(jclass clazz, const char *name, const char *sig) { return functions->GetFieldID(this, clazz, name, sig); }
	jobject GetObjectField(jobject obj, jfieldID fieldID) { return functions->GetObjectField(this, obj, fieldID); }
	jboolean GetBooleanField(jobject obj, jfieldID fieldID) { return functions->GetBooleanField(this, obj, fieldID); }
	jbyte GetByteField(jobject obj, jfieldID fieldID) { return functions->GetByteField(this, obj, fieldID); }
	jchar GetCharField(jobject obj, jfieldID fieldID) { return functions->GetCharField(this, obj, fieldID); }
	jshort GetShortField(jobject obj, jfieldID fieldID) { return functions->GetShortField(this, obj, fieldID); }
	jint GetIntField(jobject obj, jfieldID fieldID) { return functions->GetIntField(this, obj, fieldID); }
	jlong GetLongField(jobject obj, jfieldID fieldID) { return functions->GetLongField(this, obj, fieldID); }
	jfloat GetFloatField(jobject obj, jfieldID fieldID) { return functions->GetFloatField(this, obj, fieldID); }
	jdouble GetDoubleField(jobject obj, jfieldID fieldID) { return functions->GetDoubleField(this, obj, fieldID); }
	void SetObjectField(jobject obj, jfieldID fieldID, jobject value) { functions->SetObjectField(this, obj, fieldID, value); }
	void SetBooleanField(jobject obj, jfieldID fieldID, jboolean value) { functions->SetBooleanField(this, obj, fieldID, value); }
	void SetByteField(jobject obj, jfieldID fieldID, jbyte value) { functions->SetByteField(this, obj, fieldID, value); }
	void SetCharField(jobject obj, jfieldID fieldID, jchar value) { functions->SetCharField(this, obj, fieldID, value); }
	void SetShortField(jobject obj, jfieldID fieldID, jshort value) { functions->SetShortField(this, obj, fieldID, value); }
	void SetIntField(jobject obj, jfieldID fieldID, jint value) { functions->SetIntField(this, obj, fieldID, value); }
	void SetLongField(jobject obj, jfieldID fieldID, jlong value) { functions->SetLongField(this, obj, fieldID, value); }
	void SetFloatField(jobject obj, jfieldID fieldID, jfloat value) { functions->SetFloatField(this, obj, fieldID, value); }
	void SetDoubleField(jobject obj, jfieldID fieldID, jdouble value) { functions->SetDoubleField(this, obj, fieldID, value); }
	jmethodID GetStaticMethodID(jclass clazz, const char *name, const char *sig) { return functions->GetStaticMethodID(this, clazz, name, sig); }
	jobject CallStaticObjectMethod(jclass clazz, jmethodID methodID, ...) { jobject retval; va_list parms; va_start(parms, methodID); retval = functions->CallStaticObjectMethodV(this, clazz, methodID, parms); va_end(parms); return retval; }
	jobject CallStaticObjectMethodV(jclass clazz, jmethodID methodID, va_list args) { return functions->CallStaticObjectMethodV(this, clazz, methodID, args); }
	jobject CallStaticObjectMethodA(jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallStaticObjectMethodA(this, clazz, methodID, args); }
	jboolean CallStaticBooleanMethod(jclass clazz, jmethodID methodID, ...) { jboolean retval; va_list parms; va_start(parms, methodID); retval = functions->CallStaticBooleanMethodV(this, clazz, methodID, parms); va_end(parms); return retval; }
	jboolean CallStaticBooleanMethodV(jclass clazz, jmethodID methodID, va_list args) { return functions->CallStaticBooleanMethodV(this, clazz, methodID, args); }
	jboolean CallStaticBooleanMethodA(jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallStaticBooleanMethodA(this, clazz, methodID, args); }
	jbyte CallStaticByteMethod(jclass clazz, jmethodID methodID, ...) { jbyte retval; va_list parms; va_start(parms, methodID); retval = functions->CallStaticByteMethodV(this, clazz, methodID, parms); va_end(parms); return retval; }
	jbyte CallStaticByteMethodV(jclass clazz, jmethodID methodID, va_list args) { return functions->CallStaticByteMethodV(this, clazz, methodID, args); }
	jbyte CallStaticByteMethodA(jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallStaticByteMethodA(this, clazz, methodID, args); }
	jchar CallStaticCharMethod(jclass clazz, jmethodID methodID, ...) { jchar retval; va_list parms; va_start(parms, methodID); retval = functions->CallStaticCharMethodV(this, clazz, methodID, parms); va_end(parms); return retval; }
	jchar CallStaticCharMethodV(jclass clazz, jmethodID methodID, va_list args) { return functions->CallStaticCharMethodV(this, clazz, methodID, args); }
	jchar CallStaticCharMethodA(jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallStaticCharMethodA(this, clazz, methodID, args); }
	jshort CallStaticShortMethod(jclass clazz, jmethodID methodID, ...) { jshort retval; va_list parms; va_start(parms, methodID); retval = functions->CallStaticShortMethodV(this, clazz, methodID, parms); va_end(parms); return retval; }
	jshort CallStaticShortMethodV(jclass clazz, jmethodID methodID, va_list args) { return functions->CallStaticShortMethodV(this, clazz, methodID, args); }
	jshort CallStaticShortMethodA(jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallStaticShortMethodA(this, clazz, methodID, args); }
	jint CallStaticIntMethod(jclass clazz, jmethodID methodID, ...) { jint retval; va_list parms; va_start(parms, methodID); retval = functions->CallStaticIntMethodV(this, clazz, methodID, parms); va_end(parms); return retval; }
	jint CallStaticIntMethodV(jclass clazz, jmethodID methodID, va_list args) { return functions->CallStaticIntMethodV(this, clazz, methodID, args); }
	jint CallStaticIntMethodA(jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallStaticIntMethodA(this, clazz, methodID, args); }
	jlong CallStaticLongMethod(jclass clazz, jmethodID methodID, ...) { jlong retval; va_list parms; va_start(parms, methodID); retval = functions->CallStaticLongMethodV(this, clazz, methodID, parms); va_end(parms); return retval; }
	jlong CallStaticLongMethodV(jclass clazz, jmethodID methodID, va_list args) { return functions->CallStaticLongMethodV(this, clazz, methodID, args); }
	jlong CallStaticLongMethodA(jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallStaticLongMethodA(this, clazz, methodID, args); }
	jfloat CallStaticFloatMethod(jclass clazz, jmethodID methodID, ...) { jfloat retval; va_list parms; va_start(parms, methodID); retval = functions->CallStaticFloatMethodV(this, clazz, methodID, parms); va_end(parms); return retval; }
	jfloat CallStaticFloatMethodV(jclass clazz, jmethodID methodID, va_list args) { return functions->CallStaticFloatMethodV(this, clazz, methodID, args); }
	jfloat CallStaticFloatMethodA(jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallStaticFloatMethodA(this, clazz, methodID, args); }
	jdouble CallStaticDoubleMethod(jclass clazz, jmethodID methodID, ...) { jdouble retval; va_list parms; va_start(parms, methodID); retval = functions->CallStaticDoubleMethodV(this, clazz, methodID, parms); va_end(parms); return retval; }
	jdouble CallStaticDoubleMethodV(jclass clazz, jmethodID methodID, va_list args) { return functions->CallStaticDoubleMethodV(this, clazz, methodID, args); }
	jdouble CallStaticDoubleMethodA(jclass clazz, jmethodID methodID, jvalue *args) { return functions->CallStaticDoubleMethodA(this, clazz, methodID, args); }
	void CallStaticVoidMethod(jclass clazz, jmethodID methodID, ...) { va_list parms; va_start(parms, methodID); functions->CallStaticVoidMethodV(this, clazz, methodID, parms); va_end(parms); }
	void CallStaticVoidMethodV(jclass clazz, jmethodID methodID, va_list args) { functions->CallStaticVoidMethodV(this, clazz, methodID, args); }
	void CallStaticVoidMethodA(jclass clazz, jmethodID methodID, jvalue * args) { functions->CallStaticVoidMethodA(this, clazz, methodID,  args); }
	jfieldID GetStaticFieldID(jclass clazz, const char *name, const char *sig) { return functions->GetStaticFieldID(this, clazz, name, sig); }
	jobject GetStaticObjectField(jclass clazz, jfieldID fieldID) { return functions->GetStaticObjectField(this, clazz, fieldID); }
	jboolean GetStaticBooleanField(jclass clazz, jfieldID fieldID) { return functions->GetStaticBooleanField(this, clazz, fieldID); }
	jbyte GetStaticByteField(jclass clazz, jfieldID fieldID) { return functions->GetStaticByteField(this, clazz, fieldID); }
	jchar GetStaticCharField(jclass clazz, jfieldID fieldID) { return functions->GetStaticCharField(this, clazz, fieldID); }
	jshort GetStaticShortField(jclass clazz, jfieldID fieldID) { return functions->GetStaticShortField(this, clazz, fieldID); }
	jint GetStaticIntField(jclass clazz, jfieldID fieldID) { return functions->GetStaticIntField(this, clazz, fieldID); }
	jlong GetStaticLongField(jclass clazz, jfieldID fieldID) { return functions->GetStaticLongField(this, clazz, fieldID); }
	jfloat GetStaticFloatField(jclass clazz, jfieldID fieldID) { return functions->GetStaticFloatField(this, clazz, fieldID); }
	jdouble GetStaticDoubleField(jclass clazz, jfieldID fieldID) { return functions->GetStaticDoubleField(this, clazz, fieldID); }
	void SetStaticObjectField(jclass clazz, jfieldID fieldID, jobject value) { functions->SetStaticObjectField(this, clazz, fieldID, value); }
	void SetStaticBooleanField(jclass clazz, jfieldID fieldID, jboolean value) { functions->SetStaticBooleanField(this, clazz, fieldID, value); }
	void SetStaticByteField(jclass clazz, jfieldID fieldID, jbyte value) { functions->SetStaticByteField(this, clazz, fieldID, value); }
	void SetStaticCharField(jclass clazz, jfieldID fieldID, jchar value) { functions->SetStaticCharField(this, clazz, fieldID, value); }
	void SetStaticShortField(jclass clazz, jfieldID fieldID, jshort value) { functions->SetStaticShortField(this, clazz, fieldID, value); }
	void SetStaticIntField(jclass clazz, jfieldID fieldID, jint value) { functions->SetStaticIntField(this, clazz, fieldID, value); }
	void SetStaticLongField(jclass clazz, jfieldID fieldID, jlong value) { functions->SetStaticLongField(this, clazz, fieldID, value); }
	void SetStaticFloatField(jclass clazz, jfieldID fieldID, jfloat value) { functions->SetStaticFloatField(this, clazz, fieldID, value); }
	void SetStaticDoubleField(jclass clazz, jfieldID fieldID, jdouble value) { functions->SetStaticDoubleField(this, clazz, fieldID, value); }
	jstring NewString(const jchar *unicodeChars, jsize len) { return functions->NewString(this, unicodeChars, len); }
	jsize GetStringLength(jstring string) { return functions->GetStringLength(this, string); }
	const jchar* GetStringChars(jstring string, jboolean *isCopy) { return functions->GetStringChars(this, string, isCopy); }
	void ReleaseStringChars(jstring string, const jchar *utf) { functions->ReleaseStringChars(this, string, utf); }
	jstring NewStringUTF(const char *bytes) { return functions->NewStringUTF(this, bytes); }
	jsize GetStringUTFLength(jstring string) { return functions->GetStringUTFLength(this, string); }
	const char* GetStringUTFChars(jstring string, jboolean *isCopy) { return functions->GetStringUTFChars(this, string, isCopy); }
	void ReleaseStringUTFChars(jstring string, const char* utf) { functions->ReleaseStringUTFChars(this, string, utf); }
	jsize GetArrayLength(jarray array) { return functions->GetArrayLength(this, array); }
	jobjectArray NewObjectArray(jsize length, jclass clazz, jobject initialElement) { return functions->NewObjectArray(this, length, clazz, initialElement); }
	jobject GetObjectArrayElement(jobjectArray array, jsize index) { return functions->GetObjectArrayElement(this, array, index); }
	void SetObjectArrayElement(jobjectArray array, jsize index, jobject value) { functions->SetObjectArrayElement(this, array, index, value); }
	jbooleanArray NewBooleanArray(jsize length) { return functions->NewBooleanArray(this, length); }
	jbyteArray NewByteArray(jsize length) { return functions->NewByteArray(this, length); }
	jcharArray NewCharArray(jsize length) { return functions->NewCharArray(this, length); }
	jshortArray NewShortArray(jsize length) { return functions->NewShortArray(this, length); }
	jintArray NewIntArray(jsize length) { return functions->NewIntArray(this, length); }
	jlongArray NewLongArray(jsize length) { return functions->NewLongArray(this, length); }
	jfloatArray NewFloatArray(jsize length) { return functions->NewFloatArray(this, length); }
	jdoubleArray NewDoubleArray(jsize length) { return functions->NewDoubleArray(this, length); }
	jboolean* GetBooleanArrayElements(jbooleanArray array, jboolean *isCopy) { return functions->GetBooleanArrayElements(this, array, isCopy); }
	jbyte* GetByteArrayElements(jbyteArray array, jboolean *isCopy) { return functions->GetByteArrayElements(this, array, isCopy); }
	jchar*  GetCharArrayElements(jcharArray array, jboolean *isCopy) { return functions->GetCharArrayElements(this, array, isCopy); }
	jshort* GetShortArrayElements(jshortArray array, jboolean *isCopy) { return functions->GetShortArrayElements(this, array, isCopy); }
	jint* GetIntArrayElements(jintArray array, jboolean *isCopy) { return functions->GetIntArrayElements(this, array, isCopy); }
	jlong* GetLongArrayElements(jlongArray array, jboolean *isCopy) { return functions->GetLongArrayElements(this, array, isCopy); }
	jfloat* GetFloatArrayElements(jfloatArray array, jboolean *isCopy) { return functions->GetFloatArrayElements(this, array, isCopy); }
	jdouble* GetDoubleArrayElements(jdoubleArray array, jboolean *isCopy) { return functions->GetDoubleArrayElements(this, array, isCopy); }
	void ReleaseBooleanArrayElements(jbooleanArray array, jboolean *elems, jint mode) { functions->ReleaseBooleanArrayElements(this, array, elems, mode); }
	void ReleaseByteArrayElements(jbyteArray array, jbyte *elems, jint mode) { functions->ReleaseByteArrayElements(this, array, elems, mode); }
	void ReleaseCharArrayElements(jcharArray array, jchar *elems, jint mode) { functions->ReleaseCharArrayElements(this, array, elems, mode); }
	void ReleaseShortArrayElements(jshortArray array, jshort *elems, jint mode) { functions->ReleaseShortArrayElements(this, array, elems, mode); }
	void ReleaseIntArrayElements(jintArray array, jint *elems, jint mode) { functions->ReleaseIntArrayElements(this, array, elems, mode); }
	void ReleaseLongArrayElements(jlongArray array, jlong *elems, jint mode) { functions->ReleaseLongArrayElements(this, array, elems, mode); }
	void ReleaseFloatArrayElements(jfloatArray array, jfloat *elems, jint mode) { functions->ReleaseFloatArrayElements(this, array, elems, mode); }
	void ReleaseDoubleArrayElements(jdoubleArray array, jdouble *elems, jint mode) { functions->ReleaseDoubleArrayElements(this, array, elems, mode); }
	void GetBooleanArrayRegion(jbooleanArray array, jsize start, jsize len, jboolean *buf) { functions->GetBooleanArrayRegion(this, array, start, len, buf); }
	void GetByteArrayRegion(jbyteArray array, jsize start, jsize len, jbyte *buf) { functions->GetByteArrayRegion(this, array, start, len, buf); }
	void GetCharArrayRegion(jcharArray array, jsize start, jsize len, jchar *buf) { functions->GetCharArrayRegion(this, array, start, len, buf); }
	void GetShortArrayRegion(jshortArray array, jsize start, jsize len, jshort *buf) { functions->GetShortArrayRegion(this, array, start, len, buf); }
	void GetIntArrayRegion(jintArray array, jsize start, jsize len, jint *buf) { functions->GetIntArrayRegion(this, array, start, len, buf); }
	void GetLongArrayRegion(jlongArray array, jsize start, jsize len, jlong *buf) { functions->GetLongArrayRegion(this, array, start, len, buf); }
	void GetFloatArrayRegion(jfloatArray array, jsize start, jsize len, jfloat *buf) { functions->GetFloatArrayRegion(this, array, start, len, buf); }
	void GetDoubleArrayRegion(jdoubleArray array, jsize start, jsize len, jdouble *buf) { functions->GetDoubleArrayRegion(this, array, start, len, buf); }
	void SetBooleanArrayRegion(jbooleanArray array, jsize start, jsize len, jboolean *buf) { functions->SetBooleanArrayRegion(this, array, start, len, buf); }
	void SetByteArrayRegion(jbyteArray array, jsize start, jsize len, jbyte *buf) { functions->SetByteArrayRegion(this, array, start, len, buf); }
	void SetCharArrayRegion(jcharArray array, jsize start, jsize len, jchar *buf) { functions->SetCharArrayRegion(this, array, start, len, buf); }
	void SetShortArrayRegion(jshortArray array, jsize start, jsize len, jshort *buf) { functions->SetShortArrayRegion(this, array, start, len, buf); }
	void SetIntArrayRegion(jintArray array, jsize start, jsize len, jint *buf) { functions->SetIntArrayRegion(this, array, start, len, buf); }
	void SetLongArrayRegion(jlongArray array, jsize start, jsize len, jlong *buf) { functions->SetLongArrayRegion(this, array, start, len, buf); }
	void SetFloatArrayRegion(jfloatArray array, jsize start, jsize len, jfloat *buf) { functions->SetFloatArrayRegion(this, array, start, len, buf); }
	void SetDoubleArrayRegion(jdoubleArray array, jsize start, jsize len, jdouble *buf) { functions->SetDoubleArrayRegion(this, array, start, len, buf); }
	jint RegisterNatives(jclass clazz, const JNINativeMethod *methods, jint nMethods) { return functions->RegisterNatives(this, clazz, methods, nMethods); }
	jint UnregisterNatives(jclass clazz) { return functions->UnregisterNatives(this, clazz); }
	jint MonitorEnter(jobject obj) { return functions->MonitorEnter(this, obj); }
	jint MonitorExit(jobject obj) { return functions->MonitorExit(this, obj); }
	jint GetJavaVM(JavaVM **vm) { return functions->GetJavaVM(this, vm); }
	void GetStringRegion(jstring str, jsize start, jsize len, jchar *buf) { functions->GetStringRegion(this, str, start, len, buf); }
	void GetStringUTFRegion(jstring str, jsize start, jsize len, char *buf) { functions->GetStringUTFRegion(this, str, start, len, buf); }
	void* GetPrimitiveArrayCritical(jarray array, jboolean *isCopy) { return functions->GetPrimitiveArrayCritical(this, array, isCopy); }
	void ReleasePrimitiveArrayCritical(jarray array, void *carray, jint mode) { functions->ReleasePrimitiveArrayCritical(this, array, carray, mode); }
	const jchar* GetStringCritical(jstring string, jboolean *isCopy) { return functions->GetStringCritical(this, string, isCopy); }
	void ReleaseStringCritical(jstring string, const jchar *carray) { functions->ReleaseStringCritical(this, string, carray); }
	jweak NewWeakGlobalRef(jobject obj) { return functions->NewWeakGlobalRef(this, obj); }
	void DeleteWeakGlobalRef(jweak obj) { functions->DeleteWeakGlobalRef(this, obj); }
	jboolean ExceptionCheck() { return functions->ExceptionCheck(this); }
	jobject NewDirectByteBuffer(void *address, jlong capacity) { return functions->NewDirectByteBuffer(this, address, capacity); }
	void* GetDirectBufferAddress(jobject buf) { return functions->GetDirectBufferAddress(this, buf); }
	jlong GetDirectBufferCapacity(jobject buf) { return functions->GetDirectBufferCapacity(this, buf); }
	jobjectRefType GetObjectRefType(jobject obj) { return functions->GetObjectRefType(this, obj); }
#endif
};

/* 1.1 Args */
typedef struct JDK1_1InitArgs {
	jint version;
	char **properties;
	jint checkSource; 
	jint nativeStackSize;
	jint javaStackSize;
	jint minHeapSize;
	jint maxHeapSize;
	jint verifyMode;
	const char *classpath;
	jint (JNICALL * vfprintf)(FILE *fp, const char *format, va_list args);
	void (JNICALL * exit)(jint code);
	void (JNICALL * abort)(void);
	jint enableClassGC;
	jint enableVerboseGC;
	jint disableAsyncGC;
	jint verbose;
	jint debugAgent;
	jint debugPort;
} JDK1_1InitArgs;
typedef struct JDK1_1AttachArgs {
	void * __padding; /* C compilers don't allow empty structures. */
} JDK1_1AttachArgs;

/* 1.2 args */
typedef struct JavaVMOption {
	char *optionString;
	void *extraInfo;
} JavaVMOption;
typedef struct JavaVMInitArgs {
	jint version;

	jint nOptions;
	JavaVMOption *options;
	jboolean ignoreUnrecognized;
} JavaVMInitArgs;
typedef struct {
	jint version;
	char *name;
	jobject group;
} JavaVMAttachArgs;

struct JNIInvokeInterface_ {
	void *reserved0;
	void *reserved1;
	void *reserved2;
	jint (JNICALL * DestroyJavaVM)(JavaVM *vm);
	jint (JNICALL * AttachCurrentThread)(JavaVM *vm, void **penv, void *args);
	jint (JNICALL * DetachCurrentThread)(JavaVM *vm);
	jint (JNICALL * GetEnv)(JavaVM *vm, void **penv, jint version);
	jint (JNICALL * AttachCurrentThreadAsDaemon)(JavaVM *vm, void **penv, void *args);
};
struct JavaVM_ {
	const struct JNIInvokeInterface_ *functions;
	void *reserved0;
	void *reserved1;
	void *reserved2;
#ifdef __cplusplus
	jint DestroyJavaVM() { return functions->DestroyJavaVM(this); }
	jint AttachCurrentThread(void ** p_env, void * args) { return functions->AttachCurrentThread(this, p_env, args); }
	jint DetachCurrentThread() { return functions->DetachCurrentThread(this); }
	jint GetEnv(void ** p_env, jint version) { return functions->GetEnv(this, p_env, version); }
	jint AttachCurrentThreadAsDaemon(void ** p_env, void * args) { return functions->AttachCurrentThreadAsDaemon(this, p_env, args); }
#endif
};

struct JVMExtensionInterface_ {
	char eyecatcher[4];
	jint length;
	jint version;
	jint modification;
	JavaVM *vm;
	jint (JNICALL * ResetJavaVM)(JavaVM *vm);
	jint (JNICALL * QueryJavaVM)(JavaVM *vm, jint nQueries, JavaVMQuery *queries);
	jint (JNICALL * QueryGCStatus)(JavaVM *vm, jint *nheaps, GCStatus *status, jint statusSize); 
};

struct JVMExt_ {
	const struct JVMExtensionInterface_ *functions;
#ifdef __cplusplus
	jint ResetJavaVM() {return functions->ResetJavaVM(functions->vm);}
	jint QueryJavaVM(jint nQueries, JavaVMQuery *queries) { return functions->QueryJavaVM(functions->vm, nQueries, queries);}
	jint QueryGCStatus(jint *nheaps, GCStatus *status, jint statusSize){ return functions->QueryGCStatus(functions->vm, nheaps, status, statusSize); }                                                 
#endif
};

#ifdef __cplusplus
typedef  JVMExt_ JVMExt;
#else
typedef const struct JVMExtensionInterface_ *JVMExt;
#endif

jint JNICALL JNI_GetDefaultJavaVMInitArgs(void *);
jint JNICALL JNI_CreateJavaVM(JavaVM **, void **, void *);
jint JNICALL JNI_GetCreatedJavaVMs(JavaVM **, jsize, jsize *);
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *, void *); 
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *, void *); 

#ifdef __cplusplus
}
#endif

#endif /* JNI_H */
