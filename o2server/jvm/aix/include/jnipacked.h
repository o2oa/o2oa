/*******************************************************************************
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 *
 * (c) Copyright IBM Corp. 2012, 2015 All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
/*******************************************************************************
 * The code and application programming interfaces herein are technology
 * preview information that may not be made generally available by IBM as or
 * in a product.  You are permitted to use the information only for internal
 * use for evaluation purposes and not for use in a production environment.
 * IBM provides the information without obligation of support and "as is"
 * without warranty of any kind.
 *******************************************************************************/

#ifndef JNIPACKED_H
#define JNIPACKED_H

/**
 * This file is intended for customer use. Do not add internal JVM dependencies.
 */
#include "jni.h"

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Version digits are: 7B VV vv mm
 * where:
 *  7B is fixed, and identifies this module
 *  VV is the major version
 *  vv is the minor version
 *  mm is the modification number
 */
#define J9PACKED_VERSION_0_1_PRERELEASE 0x7B000001
#define J9PACKED_VERSION_0_2_PRERELEASE 0x7B000200
#define J9PACKED_INTERFACE_EYECATCHER  {'J','9','P','K'}

typedef jobject jpackedarray;

/**
 * This function table is per-JVM, not per-thread.
 */
typedef struct J9PackedInterfaceFunctions_ {
	char eyecatcher[4];
	jint length;
	jint version;
	jint modification;

	void (JNICALL *SetNestedPackedField)(JNIEnv *env, jobject packedObject, jfieldID fieldID, jobject value);
	void (JNICALL *SetPackedArrayElement)(JNIEnv *env, jpackedarray array, jsize index, jobject value);
	void (JNICALL *SetPackedBooleanArrayElement)(JNIEnv *env, jpackedarray array, jsize index, jboolean value);
	void (JNICALL *SetPackedByteArrayElement)(JNIEnv *env, jpackedarray array, jsize index, jbyte value);
	void (JNICALL *SetPackedShortArrayElement)(JNIEnv *env, jpackedarray array, jsize index, jshort value);
	void (JNICALL *SetPackedCharArrayElement)(JNIEnv *env, jpackedarray array, jsize index, jchar value);
	void (JNICALL *SetPackedIntArrayElement)(JNIEnv *env, jpackedarray array, jsize index, jint value);
	void (JNICALL *SetPackedLongArrayElement)(JNIEnv *env, jpackedarray array, jsize index, jlong value);
	void (JNICALL *SetPackedFloatArrayElement)(JNIEnv *env, jpackedarray array, jsize index, jfloat value);
	void (JNICALL *SetPackedDoubleArrayElement)(JNIEnv *env, jpackedarray array, jsize index, jdouble value);
	jobject (JNICALL *GetPackedArrayElement)(JNIEnv *env, jpackedarray array, jsize index);
	jboolean (JNICALL *GetPackedBooleanArrayElement)(JNIEnv *env, jpackedarray array, jsize index);
	jbyte (JNICALL *GetPackedByteArrayElement)(JNIEnv *env, jpackedarray array, jsize index);
	jshort (JNICALL *GetPackedShortArrayElement)(JNIEnv *env, jpackedarray array, jsize index);
	jchar (JNICALL *GetPackedCharArrayElement)(JNIEnv *env, jpackedarray array, jsize index);
	jint (JNICALL *GetPackedIntArrayElement)(JNIEnv *env, jpackedarray array, jsize index);
	jlong (JNICALL *GetPackedLongArrayElement)(JNIEnv *env, jpackedarray array, jsize index);
	jfloat (JNICALL *GetPackedFloatArrayElement)(JNIEnv *env, jpackedarray array, jsize index);
	jdouble (JNICALL *GetPackedDoubleArrayElement)(JNIEnv *env, jpackedarray array, jsize index);
	jlong (JNICALL *GetClassPackedDataSize)(JNIEnv *env, jclass clazz);
	jobject (JNICALL *AllocNativePackedObject)(JNIEnv *env, jclass clazz, void *address);
	jpackedarray (JNICALL *AllocNativePackedArray)(JNIEnv *env, jclass elementClass, jsize length, void *address);
	jpackedarray (JNICALL *AllocPackedArray)(JNIEnv *env, jclass elementClass, jsize length);
	void (JNICALL *FreeNativePackedObject)(JNIEnv *env, jobject nativePackedObject);
	void *(JNICALL *GetPackedObjectPointer)(JNIEnv *env, jobject packedObject, jboolean *isCopy);
	void (JNICALL *ReleasePackedObjectPointer)(JNIEnv *env, jobject packedObject, void *ptr, jint mode);
	void *(JNICALL *GetPackedArrayElements)(JNIEnv *env, jpackedarray packedArray, jboolean *isCopy);
	void (JNICALL *ReleasePackedArrayElements)(JNIEnv *env, jpackedarray packedArray, void *elems, jint mode);
	void (JNICALL *GetPackedArrayRegion)(JNIEnv *env, jpackedarray packedArray, jsize start, jsize length, void *buf);
	void (JNICALL *SetPackedArrayRegion)(JNIEnv *env, jpackedarray packedArray, jsize start, jsize length, void *buf);
	jboolean (JNICALL *IsIdentical)(JNIEnv *env, jobject ref1, jobject ref2);
	jclass (JNICALL *GetPackedArrayClass)(JNIEnv *env, jclass elementClass);
	jclass (JNICALL *GetPackedArrayClassComponentType)(JNIEnv *env, jclass packedArrayClass);
	jint (JNICALL *GetPackedArrayLength)(JNIEnv *env, jpackedarray array);
} J9PackedInterfaceFunctions_;

typedef const struct J9PackedInterfaceFunctions_ *J9PackedJNIEnv;

#ifdef __cplusplus
}
#endif

#endif /* JNIPACKED_H */
