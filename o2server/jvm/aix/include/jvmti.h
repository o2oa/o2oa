/*******************************************************************************
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 *
 * (c) Copyright IBM Corp. 1991, 2015 All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/

#ifndef jvmti_h
#define jvmti_h

#include "jni.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM * vm, char * options, void * reserved);
JNIEXPORT void JNICALL Agent_OnUnload(JavaVM * vm);
JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM* vm, char *options, void *reserved);

/* 
 *-----------------------------------------------------------------------------
 * JVMTI constants
 *-----------------------------------------------------------------------------
 */

#define JVMTI_VERSION_1_0 0x30010000
#define JVMTI_VERSION_1_1 0x30010100
#define JVMTI_VERSION_1_2 0x30010200
#define JVMTI_VERSION_1 (JVMTI_VERSION_1_0)

#define JVMTI_1_0_SPEC_VERSION (JVMTI_VERSION_1_0 + 37)	/* Spec version is 1.0.37 */
#define JVMTI_1_1_SPEC_VERSION (JVMTI_VERSION_1_1 + 102)	/* Spec version is 1.1.102 */
#define JVMTI_1_2_SPEC_VERSION (JVMTI_VERSION_1_2 + 1)	/* Spec version is 1.2.1 */
#define JVMTI_VERSION          (JVMTI_VERSION_1_2 + 3)  /* Spec version is 1.2.3 */

#define JVMTI_CLASS_STATUS_VERIFIED		0x00000001
#define JVMTI_CLASS_STATUS_PREPARED		0x00000002
#define JVMTI_CLASS_STATUS_INITIALIZED		0x00000004
#define JVMTI_CLASS_STATUS_ERROR		0x00000008
#define JVMTI_CLASS_STATUS_ARRAY		0x00000010
#define JVMTI_CLASS_STATUS_PRIMITIVE		0x00000020

#define JVMTI_THREAD_MIN_PRIORITY		1
#define JVMTI_THREAD_NORM_PRIORITY		5
#define JVMTI_THREAD_MAX_PRIORITY		10

#define JVMTI_THREAD_STATE_ALIVE			0x00000001
#define JVMTI_THREAD_STATE_TERMINATED			0x00000002
#define JVMTI_THREAD_STATE_RUNNABLE			0x00000004
#define JVMTI_THREAD_STATE_WAITING_INDEFINITELY		0x00000010
#define JVMTI_THREAD_STATE_WAITING_WITH_TIMEOUT		0x00000020
#define JVMTI_THREAD_STATE_SLEEPING			0x00000040
#define JVMTI_THREAD_STATE_WAITING			0x00000080
#define JVMTI_THREAD_STATE_IN_OBJECT_WAIT		0x00000100
#define JVMTI_THREAD_STATE_PARKED			0x00000200
#define JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER	0x00000400
#define JVMTI_THREAD_STATE_SUSPENDED			0x00100000
#define JVMTI_THREAD_STATE_INTERRUPTED			0x00200000
#define JVMTI_THREAD_STATE_IN_NATIVE			0x00400000
#define JVMTI_THREAD_STATE_VENDOR_1			0x10000000
#define JVMTI_THREAD_STATE_VENDOR_2			0x20000000
#define JVMTI_THREAD_STATE_VENDOR_3			0x40000000

#define JVMTI_JAVA_LANG_THREAD_STATE_MASK		\
	  JVMTI_THREAD_STATE_TERMINATED			\
	| JVMTI_THREAD_STATE_ALIVE			\
	| JVMTI_THREAD_STATE_RUNNABLE			\
	| JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER	\
	| JVMTI_THREAD_STATE_WAITING			\
	| JVMTI_THREAD_STATE_WAITING_INDEFINITELY	\
	| JVMTI_THREAD_STATE_WAITING_WITH_TIMEOUT

#define JVMTI_JAVA_LANG_THREAD_STATE_NEW		0

#define JVMTI_JAVA_LANG_THREAD_STATE_TERMINATED		\
	  JVMTI_THREAD_STATE_TERMINATED

#define JVMTI_JAVA_LANG_THREAD_STATE_RUNNABLE		\
	  JVMTI_THREAD_STATE_ALIVE			\
	| JVMTI_THREAD_STATE_RUNNABLE

#define JVMTI_JAVA_LANG_THREAD_STATE_BLOCKED		\
	  JVMTI_THREAD_STATE_ALIVE			\
	| JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER

#define JVMTI_JAVA_LANG_THREAD_STATE_WAITING		\
	  JVMTI_THREAD_STATE_ALIVE			\
	| JVMTI_THREAD_STATE_WAITING			\
	| JVMTI_THREAD_STATE_WAITING_INDEFINITELY

#define JVMTI_JAVA_LANG_THREAD_STATE_TIMED_WAITING	\
	  JVMTI_THREAD_STATE_ALIVE			\
	| JVMTI_THREAD_STATE_WAITING			\
	| JVMTI_THREAD_STATE_WAITING_WITH_TIMEOUT

#define JVMTI_VERSION_INTERFACE_JNI		0x00000000
#define JVMTI_VERSION_INTERFACE_JVMTI		0x30000000

#define JVMTI_VERSION_MASK_INTERFACE_TYPE	0x70000000
#define JVMTI_VERSION_MASK_MAJOR		0x0FFF0000
#define JVMTI_VERSION_MASK_MINOR		0x0000FF00
#define JVMTI_VERSION_MASK_MICRO		0x000000FF

#define JVMTI_VERSION_SHIFT_MAJOR		16
#define JVMTI_VERSION_SHIFT_MINOR		8
#define JVMTI_VERSION_SHIFT_MICRO		0

#define JVMTI_RESOURCE_EXHAUSTED_OOM_ERROR 1
#define JVMTI_RESOURCE_EXHAUSTED_JAVA_HEAP 2
#define JVMTI_RESOURCE_EXHAUSTED_THREADS 4

/*
 *-----------------------------------------------------------------------------
 * JVMTI enumerations
 *-----------------------------------------------------------------------------
 */

typedef enum jvmtiEventMode {
	JVMTI_ENABLE = 1,
	JVMTI_DISABLE = 0,

	jvmtiEventModeEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiEventMode;

typedef enum jvmtiHeapObjectFilter {
	JVMTI_HEAP_OBJECT_TAGGED = 1,
	JVMTI_HEAP_OBJECT_UNTAGGED = 2,
	JVMTI_HEAP_OBJECT_EITHER = 3,

	jvmtiHeapObjectFilterEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiHeapObjectFilter;

typedef enum jvmtiHeapRootKind {
	JVMTI_HEAP_ROOT_JNI_GLOBAL = 1,
	JVMTI_HEAP_ROOT_SYSTEM_CLASS = 2,
	JVMTI_HEAP_ROOT_MONITOR = 3,
	JVMTI_HEAP_ROOT_STACK_LOCAL = 4,
	JVMTI_HEAP_ROOT_JNI_LOCAL = 5,
	JVMTI_HEAP_ROOT_THREAD = 6,
	JVMTI_HEAP_ROOT_OTHER = 7,

	jvmtiHeapRootKindEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiHeapRootKind;

typedef enum jvmtiIterationControl {
	JVMTI_ITERATION_CONTINUE = 1,
	JVMTI_ITERATION_IGNORE = 2,
	JVMTI_ITERATION_ABORT = 0,

	jvmtiIterationControlEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiIterationControl;

typedef enum jvmtiJlocationFormat {
	JVMTI_JLOCATION_JVMBCI = 1,
	JVMTI_JLOCATION_MACHINEPC = 2,
	JVMTI_JLOCATION_OTHER = 0,

	jvmtiJlocationFormatEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiJlocationFormat;

typedef enum jvmtiObjectReferenceKind {
	JVMTI_REFERENCE_CLASS = 1,
	JVMTI_REFERENCE_FIELD = 2,
	JVMTI_REFERENCE_ARRAY_ELEMENT = 3,
	JVMTI_REFERENCE_CLASS_LOADER = 4,
	JVMTI_REFERENCE_SIGNERS = 5,
	JVMTI_REFERENCE_PROTECTION_DOMAIN = 6,
	JVMTI_REFERENCE_INTERFACE = 7,
	JVMTI_REFERENCE_STATIC_FIELD = 8,
	JVMTI_REFERENCE_CONSTANT_POOL = 9,

	jvmtiObjectReferenceKindEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiObjectReferenceKind;

typedef enum jvmtiParamKind {
	JVMTI_KIND_IN = 91,
	JVMTI_KIND_IN_PTR = 92,
	JVMTI_KIND_IN_BUF = 93,
	JVMTI_KIND_ALLOC_BUF = 94,
	JVMTI_KIND_ALLOC_ALLOC_BUF = 95,
	JVMTI_KIND_OUT = 96,
	JVMTI_KIND_OUT_BUF = 97,

	jvmtiParamKindEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiParamKind;

typedef enum jvmtiParamTypes {
	JVMTI_TYPE_JBYTE = 101,
	JVMTI_TYPE_JCHAR = 102,
	JVMTI_TYPE_JSHORT = 103,
	JVMTI_TYPE_JINT = 104,
	JVMTI_TYPE_JLONG = 105,
	JVMTI_TYPE_JFLOAT = 106,
	JVMTI_TYPE_JDOUBLE = 107,
	JVMTI_TYPE_JBOOLEAN = 108,
	JVMTI_TYPE_JOBJECT = 109,
	JVMTI_TYPE_JTHREAD = 110,
	JVMTI_TYPE_JCLASS = 111,
	JVMTI_TYPE_JVALUE = 112,
	JVMTI_TYPE_JFIELDID = 113,
	JVMTI_TYPE_JMETHODID = 114,
	JVMTI_TYPE_CCHAR = 115,
	JVMTI_TYPE_CVOID = 116,
	JVMTI_TYPE_JNIENV = 117,

	jvmtiParamTypesEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiParamTypes;

typedef enum jvmtiPhase {
	JVMTI_PHASE_ONLOAD = 1,
	JVMTI_PHASE_PRIMORDIAL = 2,
	JVMTI_PHASE_LIVE = 4,
	JVMTI_PHASE_START = 6,
	JVMTI_PHASE_DEAD = 8,

	jvmtiPhaseEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiPhase;

typedef enum jvmtiTimerKind {
	JVMTI_TIMER_USER_CPU = 30,
	JVMTI_TIMER_TOTAL_CPU = 31,
	JVMTI_TIMER_ELAPSED = 32,

	jvmtiTimerKindEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiTimerKind;

typedef enum jvmtiVerboseFlag {
	JVMTI_VERBOSE_OTHER = 0,
	JVMTI_VERBOSE_GC = 1,
	JVMTI_VERBOSE_CLASS = 2,
	JVMTI_VERBOSE_JNI = 4,

	jvmtiVerboseFlagEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiVerboseFlag;

#define JVMTI_HEAP_FILTER_TAGGED 0x4
#define JVMTI_HEAP_FILTER_UNTAGGED 0x8
#define JVMTI_HEAP_FILTER_CLASS_TAGGED 0x10
#define JVMTI_HEAP_FILTER_CLASS_UNTAGGED 0x20

#define JVMTI_VISIT_OBJECTS 0x100
#define JVMTI_VISIT_ABORT 0x8000

typedef enum jvmtiHeapReferenceKind {
	JVMTI_HEAP_REFERENCE_CLASS = 1,
	JVMTI_HEAP_REFERENCE_FIELD = 2,
	JVMTI_HEAP_REFERENCE_ARRAY_ELEMENT = 3,
	JVMTI_HEAP_REFERENCE_CLASS_LOADER = 4,
	JVMTI_HEAP_REFERENCE_SIGNERS = 5,
	JVMTI_HEAP_REFERENCE_PROTECTION_DOMAIN = 6,
	JVMTI_HEAP_REFERENCE_INTERFACE = 7,
	JVMTI_HEAP_REFERENCE_STATIC_FIELD = 8,
	JVMTI_HEAP_REFERENCE_CONSTANT_POOL = 9,
	JVMTI_HEAP_REFERENCE_SUPERCLASS = 10,
	JVMTI_HEAP_REFERENCE_JNI_GLOBAL = 21,
	JVMTI_HEAP_REFERENCE_SYSTEM_CLASS = 22,
	JVMTI_HEAP_REFERENCE_MONITOR = 23,
	JVMTI_HEAP_REFERENCE_STACK_LOCAL = 24,
	JVMTI_HEAP_REFERENCE_JNI_LOCAL = 25,
	JVMTI_HEAP_REFERENCE_THREAD = 26,
	JVMTI_HEAP_REFERENCE_OTHER = 27,

	jvmtiHeapReferenceKindEnsureWideEnum = 0x1000000		/* ensure 4-byte enum */
} jvmtiHeapReferenceKind;

typedef enum jvmtiPrimitiveType {
	JVMTI_PRIMITIVE_TYPE_BOOLEAN = 90,
	JVMTI_PRIMITIVE_TYPE_BYTE = 66,
	JVMTI_PRIMITIVE_TYPE_CHAR = 67,
	JVMTI_PRIMITIVE_TYPE_SHORT = 83,
	JVMTI_PRIMITIVE_TYPE_INT = 73,
	JVMTI_PRIMITIVE_TYPE_LONG = 74,
	JVMTI_PRIMITIVE_TYPE_FLOAT = 70,
	JVMTI_PRIMITIVE_TYPE_DOUBLE = 68,

	jvmtiPrimitiveTypeEnsureWideEnum = 0x1000000		/* ensure 4-byte enum */
} jvmtiPrimitiveType;

/*
 *-----------------------------------------------------------------------------
 * JVMTI error codes
 *-----------------------------------------------------------------------------
 */

typedef enum jvmtiError { 
	JVMTI_ERROR_NONE = 0,
	JVMTI_ERROR_INVALID_THREAD = 10,
	JVMTI_ERROR_INVALID_THREAD_GROUP = 11,
	JVMTI_ERROR_INVALID_PRIORITY = 12,
	JVMTI_ERROR_THREAD_NOT_SUSPENDED = 13,
	JVMTI_ERROR_THREAD_SUSPENDED = 14,
	JVMTI_ERROR_THREAD_NOT_ALIVE = 15,
	JVMTI_ERROR_INVALID_OBJECT = 20,
	JVMTI_ERROR_INVALID_CLASS = 21,
	JVMTI_ERROR_CLASS_NOT_PREPARED = 22,
	JVMTI_ERROR_INVALID_METHODID = 23,
	JVMTI_ERROR_INVALID_LOCATION = 24,
	JVMTI_ERROR_INVALID_FIELDID = 25,
	JVMTI_ERROR_NO_MORE_FRAMES = 31,
	JVMTI_ERROR_OPAQUE_FRAME = 32,
	JVMTI_ERROR_TYPE_MISMATCH = 34,
	JVMTI_ERROR_INVALID_SLOT = 35,
	JVMTI_ERROR_DUPLICATE = 40,
	JVMTI_ERROR_NOT_FOUND = 41,
	JVMTI_ERROR_INVALID_MONITOR = 50,
	JVMTI_ERROR_NOT_MONITOR_OWNER = 51,
	JVMTI_ERROR_INTERRUPT = 52,
	JVMTI_ERROR_INVALID_CLASS_FORMAT = 60,
	JVMTI_ERROR_CIRCULAR_CLASS_DEFINITION = 61,
	JVMTI_ERROR_FAILS_VERIFICATION = 62,
	JVMTI_ERROR_UNSUPPORTED_REDEFINITION_METHOD_ADDED = 63,
	JVMTI_ERROR_UNSUPPORTED_REDEFINITION_SCHEMA_CHANGED = 64,
	JVMTI_ERROR_INVALID_TYPESTATE = 65,
	JVMTI_ERROR_UNSUPPORTED_REDEFINITION_HIERARCHY_CHANGED = 66,
	JVMTI_ERROR_UNSUPPORTED_REDEFINITION_METHOD_DELETED = 67,
	JVMTI_ERROR_UNSUPPORTED_VERSION = 68,
	JVMTI_ERROR_NAMES_DONT_MATCH = 69,
	JVMTI_ERROR_UNSUPPORTED_REDEFINITION_CLASS_MODIFIERS_CHANGED = 70,
	JVMTI_ERROR_UNSUPPORTED_REDEFINITION_METHOD_MODIFIERS_CHANGED = 71,
	JVMTI_ERROR_UNMODIFIABLE_CLASS = 79,
	JVMTI_ERROR_NOT_AVAILABLE = 98,
	JVMTI_ERROR_MUST_POSSESS_CAPABILITY = 99,
	JVMTI_ERROR_NULL_POINTER = 100,
	JVMTI_ERROR_ABSENT_INFORMATION = 101,
	JVMTI_ERROR_INVALID_EVENT_TYPE = 102,
	JVMTI_ERROR_ILLEGAL_ARGUMENT = 103,
	JVMTI_ERROR_NATIVE_METHOD = 104,
	JVMTI_ERROR_CLASS_LOADER_UNSUPPORTED = 106,
	JVMTI_ERROR_OUT_OF_MEMORY = 110,
	JVMTI_ERROR_ACCESS_DENIED = 111,
	JVMTI_ERROR_WRONG_PHASE = 112,
	JVMTI_ERROR_INTERNAL = 113,
	JVMTI_ERROR_UNATTACHED_THREAD = 115,
	JVMTI_ERROR_INVALID_ENVIRONMENT = 116,

	JVMTI_ERROR_MAX = 116,

	jvmtiErrorEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiError;

/*
 *-----------------------------------------------------------------------------
 * JVMTI base types
 *-----------------------------------------------------------------------------
 */

typedef jobject jthread;

typedef jobject jthreadGroup;

typedef jlong jlocation;

/* hidden type */
struct _jrawMonitorID;

typedef struct _jrawMonitorID *jrawMonitorID;

typedef struct JNINativeInterface_ jniNativeInterface;

struct _jvmtiEnv;
struct JVMTINativeInterface_;
#ifdef __cplusplus
typedef _jvmtiEnv jvmtiEnv;
#else
typedef const struct JVMTINativeInterface_ * jvmtiEnv;
#endif

typedef void (JNICALL *jvmtiExtensionEvent)
	(jvmtiEnv *jvmti_env, ...);

typedef jvmtiError (JNICALL *jvmtiExtensionFunction)
	(jvmtiEnv *jvmti_env, ...);

typedef jvmtiIterationControl (JNICALL *jvmtiHeapObjectCallback)
	(jlong class_tag, jlong size, jlong *tag_ptr,
	 void *user_data);

typedef jvmtiIterationControl (JNICALL *jvmtiHeapRootCallback)
	(jvmtiHeapRootKind root_kind,
	 jlong class_tag, jlong size, jlong *tag_ptr,
	 void *user_data);

typedef jvmtiIterationControl (JNICALL *jvmtiObjectReferenceCallback)
	(jvmtiObjectReferenceKind reference_kind,
	 jlong class_tag, jlong size, jlong *tag_ptr,
	 jlong referrer_tag, jint referrer_index,
	 void *user_data);

typedef jvmtiIterationControl (JNICALL *jvmtiStackReferenceCallback)
	(jvmtiHeapRootKind root_kind,
	 jlong class_tag, jlong size, jlong *tag_ptr,
	 jlong thread_tag, jint depth, jmethodID method, jint slot,
	 void *user_data);

typedef void (JNICALL *jvmtiStartFunction)
	(jvmtiEnv *jvmti_env, JNIEnv *jni_env, void *arg);

typedef struct jvmtiFrameInfo {
	jmethodID method;
	jlocation location;
} jvmtiFrameInfo;

typedef struct jvmtiStackInfo {
	jthread thread;
	jint state;
	jvmtiFrameInfo* frame_buffer;
	jint frame_count;
} jvmtiStackInfo;

typedef struct jvmtiAddrLocationMap {
	const void *start_address;
	jlocation location;
} jvmtiAddrLocationMap;

typedef struct {
	unsigned int can_tag_objects : 1;
	unsigned int can_generate_field_modification_events : 1;
	unsigned int can_generate_field_access_events : 1;
	unsigned int can_get_bytecodes : 1;
	unsigned int can_get_synthetic_attribute : 1;
	unsigned int can_get_owned_monitor_info : 1;
	unsigned int can_get_current_contended_monitor : 1;
	unsigned int can_get_monitor_info : 1;
	unsigned int can_pop_frame : 1;
	unsigned int can_redefine_classes : 1;
	unsigned int can_signal_thread : 1;
	unsigned int can_get_source_file_name : 1;
	unsigned int can_get_line_numbers : 1;
	unsigned int can_get_source_debug_extension : 1;
	unsigned int can_access_local_variables : 1;
	unsigned int can_maintain_original_method_order : 1;
	unsigned int can_generate_single_step_events : 1;
	unsigned int can_generate_exception_events : 1;
	unsigned int can_generate_frame_pop_events : 1;
	unsigned int can_generate_breakpoint_events : 1;
	unsigned int can_suspend : 1;
	unsigned int can_redefine_any_class : 1;
	unsigned int can_get_current_thread_cpu_time : 1;
	unsigned int can_get_thread_cpu_time : 1;
	unsigned int can_generate_method_entry_events : 1;
	unsigned int can_generate_method_exit_events : 1;
	unsigned int can_generate_all_class_hook_events : 1;
	unsigned int can_generate_compiled_method_load_events : 1;
	unsigned int can_generate_monitor_events : 1;
	unsigned int can_generate_vm_object_alloc_events : 1;
	unsigned int can_generate_native_method_bind_events : 1;
	unsigned int can_generate_garbage_collection_events : 1;
	unsigned int can_generate_object_free_events : 1;
	unsigned int can_force_early_return : 1;
	unsigned int can_get_owned_monitor_stack_depth_info : 1;
	unsigned int can_get_constant_pool : 1;
	unsigned int can_set_native_method_prefix : 1;
	unsigned int can_retransform_classes : 1;
	unsigned int can_retransform_any_class : 1;
	unsigned int can_generate_resource_exhaustion_heap_events : 1;
	unsigned int can_generate_resource_exhaustion_threads_events : 1;
	unsigned int : 5;
	unsigned int : 16;
	unsigned int : 16;
	unsigned int : 16;
	unsigned int : 16;
	unsigned int : 16;
} jvmtiCapabilities;

typedef struct jvmtiClassDefinition {
	jclass klass;
	jint class_byte_count;
	const unsigned char *class_bytes;
} jvmtiClassDefinition;

typedef struct jvmtiParamInfo {
	char *name;
	jvmtiParamKind kind;
	jvmtiParamTypes base_type;
	jboolean null_ok;
} jvmtiParamInfo;

typedef struct jvmtiExtensionEventInfo {
	jint extension_event_index;
	char *id;
	char *short_description;
	jint param_count;
	jvmtiParamInfo *params;
} jvmtiExtensionEventInfo;

typedef struct jvmtiExtensionFunctionInfo {
	jvmtiExtensionFunction func;
	char *id;
	char *short_description;
	jint param_count;
	jvmtiParamInfo *params;
	jint error_count;
	jvmtiError *errors;
} jvmtiExtensionFunctionInfo;

typedef struct jvmtiLineNumberEntry {
	jlocation start_location;
	jint line_number;
} jvmtiLineNumberEntry;

typedef struct jvmtiLocalVariableEntry {
	jlocation start_location;
	jint length;
	char *name;
	char *signature;
	char *generic_signature;
	jint slot;
} jvmtiLocalVariableEntry;

typedef struct jvmtiMonitorUsage {
	jthread owner;
	jint entry_count;
	jint waiter_count;
	jthread *waiters;
	jint notify_waiter_count;
	jthread *notify_waiters;
} jvmtiMonitorUsage;

typedef struct jvmtiThreadGroupInfo {
	jthreadGroup parent;
	char *name;
	jint max_priority;
	jboolean is_daemon;
} jvmtiThreadGroupInfo;

typedef struct jvmtiThreadInfo {
	char *name;
	jint priority;
	jboolean is_daemon;
	jthreadGroup thread_group;
	jobject context_class_loader;
} jvmtiThreadInfo;

typedef struct jvmtiTimerInfo {
	jlong max_value;
	jboolean may_skip_forward;
	jboolean may_skip_backward;
	jvmtiTimerKind kind;
	jlong reserved1;
	jlong reserved2;
} jvmtiTimerInfo;

typedef struct jvmtiMonitorStackDepthInfo {
	jobject monitor;
	jint stack_depth;
} jvmtiMonitorStackDepthInfo;

typedef struct jvmtiHeapReferenceInfoField {
	jint index;
} jvmtiHeapReferenceInfoField;

typedef struct jvmtiHeapReferenceInfoArray {
	jint index;
} jvmtiHeapReferenceInfoArray;

typedef struct jvmtiHeapReferenceInfoConstantPool {
	jint index;
} jvmtiHeapReferenceInfoConstantPool;

typedef struct jvmtiHeapReferenceInfoStackLocal {
	jlong thread_tag;
	jlong thread_id;
	jint depth;
	jmethodID method;
	jlocation location;
	jint slot;
} jvmtiHeapReferenceInfoStackLocal;

typedef struct jvmtiHeapReferenceInfoJniLocal {
	jlong thread_tag;
	jlong thread_id;
	jint depth;
	jmethodID method;
} jvmtiHeapReferenceInfoJniLocal;

typedef struct jvmtiHeapReferenceInfoReserved {
	jlong reserved1;
	jlong reserved2;
	jlong reserved3;
	jlong reserved4;
	jlong reserved5;
	jlong reserved6;
	jlong reserved7;
	jlong reserved8;
} jvmtiHeapReferenceInfoReserved;


typedef union {
	jvmtiHeapReferenceInfoField field;
	jvmtiHeapReferenceInfoArray array;
	jvmtiHeapReferenceInfoConstantPool constant_pool;
	jvmtiHeapReferenceInfoStackLocal stack_local;
	jvmtiHeapReferenceInfoJniLocal jni_local;
	jvmtiHeapReferenceInfoReserved other;
} jvmtiHeapReferenceInfo;


typedef jint (JNICALL *jvmtiHeapIterationCallback)
	(jlong class_tag, 
	 jlong size, 
	 jlong* tag_ptr, 
	 jint length, 
	 void* user_data);

typedef jint (JNICALL *jvmtiHeapReferenceCallback)
	(jvmtiHeapReferenceKind reference_kind, 
	 const jvmtiHeapReferenceInfo* referrer_info, 
	 jlong class_tag, 
	 jlong referrer_class_tag, 
	 jlong size, 
	 jlong* tag_ptr, 
	 jlong* referrer_tag_ptr, 
	 jint length, 
	 void* user_data);


typedef jint (JNICALL *jvmtiPrimitiveFieldCallback)
	(jvmtiHeapReferenceKind reference_kind, 
	 const jvmtiHeapReferenceInfo* referrer_info, 
	 jlong class_tag, 
	 jlong* tag_ptr, 
	 jvalue value, 
	 jvmtiPrimitiveType value_type, 
	 void* user_data);

typedef jint (JNICALL *jvmtiArrayPrimitiveValueCallback)
	(jlong class_tag, 
	 jlong size, 
	 jlong* tag_ptr, 
	 jint element_count, 
	 jvmtiPrimitiveType element_type, 
	 const void* elements, 
	 void* user_data);

typedef jint (JNICALL *jvmtiStringPrimitiveValueCallback)
	(jlong class_tag, 
	 jlong size, 
	 jlong* tag_ptr, 
	 const jchar* value, 
	 jint value_length, 
	 void* user_data);

typedef jint (JNICALL *jvmtiReservedCallback)
	();

/**
 *  JVMTI Heap 1.1 Callbacks 
 */
typedef struct {
    jvmtiHeapIterationCallback heap_iteration_callback;
    jvmtiHeapReferenceCallback heap_reference_callback;
    jvmtiPrimitiveFieldCallback primitive_field_callback;
    jvmtiArrayPrimitiveValueCallback array_primitive_value_callback;
    jvmtiStringPrimitiveValueCallback string_primitive_value_callback;
    jvmtiReservedCallback reserved5;
    jvmtiReservedCallback reserved6;
    jvmtiReservedCallback reserved7;
    jvmtiReservedCallback reserved8;
    jvmtiReservedCallback reserved9;
    jvmtiReservedCallback reserved10;
    jvmtiReservedCallback reserved11;
    jvmtiReservedCallback reserved12;
    jvmtiReservedCallback reserved13;
    jvmtiReservedCallback reserved14;
    jvmtiReservedCallback reserved15;
} jvmtiHeapCallbacks;

/* 
 *-----------------------------------------------------------------------------
 * JVMTI events
 *-----------------------------------------------------------------------------
 */

typedef enum jvmtiEvent {
	JVMTI_MIN_EVENT_TYPE_VAL = 50,
	JVMTI_EVENT_VM_INIT = 50,
	JVMTI_EVENT_VM_DEATH = 51,
	JVMTI_EVENT_THREAD_START = 52,
	JVMTI_EVENT_THREAD_END = 53,
	JVMTI_EVENT_CLASS_FILE_LOAD_HOOK = 54,
	JVMTI_EVENT_CLASS_LOAD = 55,
	JVMTI_EVENT_CLASS_PREPARE = 56,
	JVMTI_EVENT_VM_START = 57,
	JVMTI_EVENT_EXCEPTION = 58,
	JVMTI_EVENT_EXCEPTION_CATCH = 59,
	JVMTI_EVENT_SINGLE_STEP = 60,
	JVMTI_EVENT_FRAME_POP = 61,
	JVMTI_EVENT_BREAKPOINT = 62,
	JVMTI_EVENT_FIELD_ACCESS = 63,
	JVMTI_EVENT_FIELD_MODIFICATION = 64,
	JVMTI_EVENT_METHOD_ENTRY = 65,
	JVMTI_EVENT_METHOD_EXIT = 66,
	JVMTI_EVENT_NATIVE_METHOD_BIND = 67,
	JVMTI_EVENT_COMPILED_METHOD_LOAD = 68,
	JVMTI_EVENT_COMPILED_METHOD_UNLOAD = 69,
	JVMTI_EVENT_DYNAMIC_CODE_GENERATED = 70,
	JVMTI_EVENT_DATA_DUMP_REQUEST = 71,
	JVMTI_EVENT_MONITOR_WAIT = 73,
	JVMTI_EVENT_MONITOR_WAITED = 74,
	JVMTI_EVENT_MONITOR_CONTENDED_ENTER = 75,
	JVMTI_EVENT_MONITOR_CONTENDED_ENTERED = 76,
	JVMTI_EVENT_RESOURCE_EXHAUSTED = 80,
	JVMTI_EVENT_GARBAGE_COLLECTION_START = 81,
	JVMTI_EVENT_GARBAGE_COLLECTION_FINISH = 82,
	JVMTI_EVENT_OBJECT_FREE = 83,
	JVMTI_EVENT_VM_OBJECT_ALLOC = 84,

	JVMTI_MAX_EVENT_TYPE_VAL = 84,
	jvmtiEventEnsureWideEnum = 0x1000000						/* ensure 4-byte enum */
} jvmtiEvent;

/*
 *-----------------------------------------------------------------------------
 * JVMTI callbacks
 *-----------------------------------------------------------------------------
 */

typedef void(JNICALL *jvmtiEventSingleStep)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jmethodID method,
	jlocation location);

typedef void(JNICALL *jvmtiEventBreakpoint)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jmethodID method,
	jlocation location);

typedef void(JNICALL *jvmtiEventFieldAccess)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jmethodID method,
	jlocation location,
	jclass field_klass,
	jobject object,
	jfieldID field);

typedef void(JNICALL *jvmtiEventFieldModification)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jmethodID method,
	jlocation location,
	jclass field_klass,
	jobject object,
	jfieldID field,
	char signature_type,
	jvalue new_value);

typedef void(JNICALL *jvmtiEventFramePop)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jmethodID method,
	jboolean was_popped_by_exception);

typedef void(JNICALL *jvmtiEventMethodEntry)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jmethodID method);

typedef void(JNICALL *jvmtiEventMethodExit)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jmethodID method,
	jboolean was_popped_by_exception,
	jvalue return_value);

typedef void(JNICALL *jvmtiEventNativeMethodBind)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jmethodID method,
	void* address,
	void** new_address_ptr);

typedef void(JNICALL *jvmtiEventException)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jmethodID method,
	jlocation location,
	jobject exception,
	jmethodID catch_method,
	jlocation catch_location);

typedef void(JNICALL *jvmtiEventExceptionCatch)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jmethodID method,
	jlocation location,
	jobject exception);

typedef void(JNICALL *jvmtiEventThreadStart)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread);

typedef void(JNICALL *jvmtiEventThreadEnd)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread);

typedef void(JNICALL *jvmtiEventClassLoad)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jclass klass);

typedef void(JNICALL *jvmtiEventClassPrepare)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jclass klass);

typedef void(JNICALL *jvmtiEventClassFileLoadHook)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jclass class_being_redefined,
	jobject loader,
	const char* name,
	jobject protection_domain,
	jint class_data_len,
	const unsigned char* class_data,
	jint* new_class_data_len,
	unsigned char** new_class_data);

typedef void(JNICALL *jvmtiEventVMStart)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env);

typedef void(JNICALL *jvmtiEventVMInit)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread);

typedef void(JNICALL *jvmtiEventVMDeath)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env);

typedef void(JNICALL *jvmtiEventCompiledMethodLoad)(
	jvmtiEnv *jvmti_env,
	jmethodID method,
	jint code_size,
	const void* code_addr,
	jint map_length,
	const jvmtiAddrLocationMap* map,
	const void* compile_info);

typedef void(JNICALL *jvmtiEventCompiledMethodUnload)(
	jvmtiEnv *jvmti_env,
	jmethodID method,
	const void* code_addr);

typedef void(JNICALL *jvmtiEventDynamicCodeGenerated)(
	jvmtiEnv *jvmti_env,
	const char* name,
	const void* address,
	jint length);

typedef void(JNICALL *jvmtiEventDataDumpRequest)(
	jvmtiEnv *jvmti_env);

typedef void(JNICALL *jvmtiEventMonitorContendedEnter)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jobject object);

typedef void(JNICALL *jvmtiEventMonitorContendedEntered)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jobject object);

typedef void(JNICALL *jvmtiEventMonitorWait)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jobject object,
	jlong timeout);

typedef void(JNICALL *jvmtiEventMonitorWaited)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jobject object,
	jboolean timed_out);

typedef void(JNICALL *jvmtiEventVMObjectAlloc)(
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jthread thread,
	jobject object,
	jclass object_klass,
	jlong size);

typedef void(JNICALL *jvmtiEventObjectFree)(
	jvmtiEnv *jvmti_env,
	jlong tag);

typedef void(JNICALL *jvmtiEventGarbageCollectionStart)(
	jvmtiEnv *jvmti_env);

typedef void(JNICALL *jvmtiEventGarbageCollectionFinish)(
	jvmtiEnv *jvmti_env);

typedef void (JNICALL *jvmtiEventResourceExhausted) (
	jvmtiEnv *jvmti_env,
	JNIEnv* jni_env,
	jint flags,
	const void* reserved,
	const char* description);

typedef void * jvmtiEventReserved;

typedef struct {
	jvmtiEventVMInit VMInit;
	jvmtiEventVMDeath VMDeath;
	jvmtiEventThreadStart ThreadStart;
	jvmtiEventThreadEnd ThreadEnd;
	jvmtiEventClassFileLoadHook ClassFileLoadHook;
	jvmtiEventClassLoad ClassLoad;
	jvmtiEventClassPrepare ClassPrepare;
	jvmtiEventVMStart VMStart;
	jvmtiEventException Exception;
	jvmtiEventExceptionCatch ExceptionCatch;
	jvmtiEventSingleStep SingleStep;
	jvmtiEventFramePop FramePop;
	jvmtiEventBreakpoint Breakpoint;
	jvmtiEventFieldAccess FieldAccess;
	jvmtiEventFieldModification FieldModification;
	jvmtiEventMethodEntry MethodEntry;
	jvmtiEventMethodExit MethodExit;
	jvmtiEventNativeMethodBind NativeMethodBind;
	jvmtiEventCompiledMethodLoad CompiledMethodLoad;
	jvmtiEventCompiledMethodUnload CompiledMethodUnload;
	jvmtiEventDynamicCodeGenerated DynamicCodeGenerated;
	jvmtiEventDataDumpRequest DataDumpRequest;
	jvmtiEventReserved reserved72;
	jvmtiEventMonitorWait MonitorWait;
	jvmtiEventMonitorWaited MonitorWaited;
	jvmtiEventMonitorContendedEnter MonitorContendedEnter;
	jvmtiEventMonitorContendedEntered MonitorContendedEntered;
	jvmtiEventReserved reserved77;
	jvmtiEventReserved reserved78;
	jvmtiEventReserved reserved79;
	jvmtiEventResourceExhausted ResourceExhausted;
	jvmtiEventGarbageCollectionStart GarbageCollectionStart;
	jvmtiEventGarbageCollectionFinish GarbageCollectionFinish;
	jvmtiEventObjectFree ObjectFree;
	jvmtiEventVMObjectAlloc VMObjectAlloc;
} jvmtiEventCallbacks;

/*
 *-----------------------------------------------------------------------------
 * JVMTI methods
 *-----------------------------------------------------------------------------
 */

typedef struct JVMTINativeInterface_ {
	void *reserved1;
	jvmtiError (JNICALL * SetEventNotificationMode)(jvmtiEnv* env,	jvmtiEventMode mode,	jvmtiEvent event_type,	jthread event_thread,	...);
	void *reserved3;
	jvmtiError (JNICALL * GetAllThreads)(jvmtiEnv* env,	jint* threads_count_ptr,	jthread** threads_ptr);
	jvmtiError (JNICALL * SuspendThread)(jvmtiEnv* env,	jthread thread);
	jvmtiError (JNICALL * ResumeThread)(jvmtiEnv* env,	jthread thread);
	jvmtiError (JNICALL * StopThread)(jvmtiEnv* env,	jthread thread,	jobject exception);
	jvmtiError (JNICALL * InterruptThread)(jvmtiEnv* env,	jthread thread);
	jvmtiError (JNICALL * GetThreadInfo)(jvmtiEnv* env,	jthread thread,	jvmtiThreadInfo* info_ptr);
	jvmtiError (JNICALL * GetOwnedMonitorInfo)(jvmtiEnv* env,	jthread thread,	jint* owned_monitor_count_ptr,	jobject** owned_monitors_ptr);
	jvmtiError (JNICALL * GetCurrentContendedMonitor)(jvmtiEnv* env,	jthread thread,	jobject* monitor_ptr);
	jvmtiError (JNICALL * RunAgentThread)(jvmtiEnv* env,	jthread thread,	jvmtiStartFunction proc,	const void* arg,	jint priority);
	jvmtiError (JNICALL * GetTopThreadGroups)(jvmtiEnv* env,	jint* group_count_ptr,	jthreadGroup** groups_ptr);
	jvmtiError (JNICALL * GetThreadGroupInfo)(jvmtiEnv* env,	jthreadGroup group,	jvmtiThreadGroupInfo* info_ptr);
	jvmtiError (JNICALL * GetThreadGroupChildren)(jvmtiEnv* env,	jthreadGroup group,	jint* thread_count_ptr,	jthread** threads_ptr,	jint* group_count_ptr,	jthreadGroup** groups_ptr);
	jvmtiError (JNICALL * GetFrameCount)(jvmtiEnv* env,	jthread thread,	jint* count_ptr);
	jvmtiError (JNICALL * GetThreadState)(jvmtiEnv* env,	jthread thread,	jint* thread_state_ptr);
	jvmtiError (JNICALL * GetCurrentThread)(jvmtiEnv* env, jthread* thread_ptr);
	jvmtiError (JNICALL * GetFrameLocation)(jvmtiEnv* env,	jthread thread,	jint depth,	jmethodID* method_ptr,	jlocation* location_ptr);
	jvmtiError (JNICALL * NotifyFramePop)(jvmtiEnv* env,	jthread thread,	jint depth);
	jvmtiError (JNICALL * GetLocalObject)(jvmtiEnv* env,	jthread thread,	jint depth,	jint slot,	jobject* value_ptr);
	jvmtiError (JNICALL * GetLocalInt)(jvmtiEnv* env,	jthread thread,	jint depth,	jint slot,	jint* value_ptr);
	jvmtiError (JNICALL * GetLocalLong)(jvmtiEnv* env,	jthread thread,	jint depth,	jint slot,	jlong* value_ptr);
	jvmtiError (JNICALL * GetLocalFloat)(jvmtiEnv* env,	jthread thread,	jint depth,	jint slot,	jfloat* value_ptr);
	jvmtiError (JNICALL * GetLocalDouble)(jvmtiEnv* env,	jthread thread,	jint depth,	jint slot,	jdouble* value_ptr);
	jvmtiError (JNICALL * SetLocalObject)(jvmtiEnv* env,	jthread thread,	jint depth,	jint slot,	jobject value);
	jvmtiError (JNICALL * SetLocalInt)(jvmtiEnv* env,	jthread thread,	jint depth,	jint slot,	jint value);
	jvmtiError (JNICALL * SetLocalLong)(jvmtiEnv* env,	jthread thread,	jint depth,	jint slot,	jlong value);
	jvmtiError (JNICALL * SetLocalFloat)(jvmtiEnv* env,	jthread thread,	jint depth,	jint slot,	jfloat value);
	jvmtiError (JNICALL * SetLocalDouble)(jvmtiEnv* env,	jthread thread,	jint depth,	jint slot,	jdouble value);
	jvmtiError (JNICALL * CreateRawMonitor)(jvmtiEnv* env,	const char* name,	jrawMonitorID* monitor_ptr);
	jvmtiError (JNICALL * DestroyRawMonitor)(jvmtiEnv* env,	jrawMonitorID monitor);
	jvmtiError (JNICALL * RawMonitorEnter)(jvmtiEnv* env,	jrawMonitorID monitor);
	jvmtiError (JNICALL * RawMonitorExit)(jvmtiEnv* env,	jrawMonitorID monitor);
	jvmtiError (JNICALL * RawMonitorWait)(jvmtiEnv* env,	jrawMonitorID monitor,	jlong millis);
	jvmtiError (JNICALL * RawMonitorNotify)(jvmtiEnv* env,	jrawMonitorID monitor);
	jvmtiError (JNICALL * RawMonitorNotifyAll)(jvmtiEnv* env,	jrawMonitorID monitor);
	jvmtiError (JNICALL * SetBreakpoint)(jvmtiEnv* env,	jmethodID method,	jlocation location);
	jvmtiError (JNICALL * ClearBreakpoint)(jvmtiEnv* env,	jmethodID method,	jlocation location);
	void *reserved40;
	jvmtiError (JNICALL * SetFieldAccessWatch)(jvmtiEnv* env,	jclass klass,	jfieldID field);
	jvmtiError (JNICALL * ClearFieldAccessWatch)(jvmtiEnv* env,	jclass klass,	jfieldID field);
	jvmtiError (JNICALL * SetFieldModificationWatch)(jvmtiEnv* env,	jclass klass,	jfieldID field);
	jvmtiError (JNICALL * ClearFieldModificationWatch)(jvmtiEnv* env,	jclass klass,	jfieldID field);
	jvmtiError (JNICALL * IsModifiableClass)(jvmtiEnv* env, jclass klass, jboolean* is_modifiable_class_ptr);
	jvmtiError (JNICALL * Allocate)(jvmtiEnv* env,	jlong size,	unsigned char** mem_ptr);
	jvmtiError (JNICALL * Deallocate)(jvmtiEnv* env,	unsigned char* mem);
	jvmtiError (JNICALL * GetClassSignature)(jvmtiEnv* env,	jclass klass,	char** signature_ptr,	char** generic_ptr);
	jvmtiError (JNICALL * GetClassStatus)(jvmtiEnv* env,	jclass klass,	jint* status_ptr);
	jvmtiError (JNICALL * GetSourceFileName)(jvmtiEnv* env,	jclass klass,	char** source_name_ptr);
	jvmtiError (JNICALL * GetClassModifiers)(jvmtiEnv* env,	jclass klass,	jint* modifiers_ptr);
	jvmtiError (JNICALL * GetClassMethods)(jvmtiEnv* env,	jclass klass,	jint* method_count_ptr,	jmethodID** methods_ptr);
	jvmtiError (JNICALL * GetClassFields)(jvmtiEnv* env,	jclass klass,	jint* field_count_ptr,	jfieldID** fields_ptr);
	jvmtiError (JNICALL * GetImplementedInterfaces)(jvmtiEnv* env,	jclass klass,	jint* interface_count_ptr,	jclass** interfaces_ptr);
	jvmtiError (JNICALL * IsInterface)(jvmtiEnv* env,	jclass klass,	jboolean* is_interface_ptr);
	jvmtiError (JNICALL * IsArrayClass)(jvmtiEnv* env,	jclass klass,	jboolean* is_array_class_ptr);
	jvmtiError (JNICALL * GetClassLoader)(jvmtiEnv* env,	jclass klass,	jobject* classloader_ptr);
	jvmtiError (JNICALL * GetObjectHashCode)(jvmtiEnv* env,	jobject object,	jint* hash_code_ptr);
	jvmtiError (JNICALL * GetObjectMonitorUsage)(jvmtiEnv* env,	jobject object,	jvmtiMonitorUsage* info_ptr);
	jvmtiError (JNICALL * GetFieldName)(jvmtiEnv* env,	jclass klass,	jfieldID field,	char** name_ptr,	char** signature_ptr,	char** generic_ptr);
	jvmtiError (JNICALL * GetFieldDeclaringClass)(jvmtiEnv* env,	jclass klass,	jfieldID field,	jclass* declaring_class_ptr);
	jvmtiError (JNICALL * GetFieldModifiers)(jvmtiEnv* env,	jclass klass,	jfieldID field,	jint* modifiers_ptr);
	jvmtiError (JNICALL * IsFieldSynthetic)(jvmtiEnv* env,	jclass klass,	jfieldID field,	jboolean* is_synthetic_ptr);
	jvmtiError (JNICALL * GetMethodName)(jvmtiEnv* env,	jmethodID method,	char** name_ptr,	char** signature_ptr,	char** generic_ptr);
	jvmtiError (JNICALL * GetMethodDeclaringClass)(jvmtiEnv* env,	jmethodID method,	jclass* declaring_class_ptr);
	jvmtiError (JNICALL * GetMethodModifiers)(jvmtiEnv* env,	jmethodID method,	jint* modifiers_ptr);
	void *reserved67;
	jvmtiError (JNICALL * GetMaxLocals)(jvmtiEnv* env,	jmethodID method,	jint* max_ptr);
	jvmtiError (JNICALL * GetArgumentsSize)(jvmtiEnv* env,	jmethodID method,	jint* size_ptr);
	jvmtiError (JNICALL * GetLineNumberTable)(jvmtiEnv* env,	jmethodID method,	jint* entry_count_ptr,	jvmtiLineNumberEntry** table_ptr);
	jvmtiError (JNICALL * GetMethodLocation)(jvmtiEnv* env,	jmethodID method,	jlocation* start_location_ptr,	jlocation* end_location_ptr);
	jvmtiError (JNICALL * GetLocalVariableTable)(jvmtiEnv* env,	jmethodID method,	jint* entry_count_ptr,	jvmtiLocalVariableEntry** table_ptr);
	jvmtiError (JNICALL * SetNativeMethodPrefix)(jvmtiEnv* env, const char* prefix);
	jvmtiError (JNICALL * SetNativeMethodPrefixes)(jvmtiEnv* env, jint prefix_count, char** prefixes);
	jvmtiError (JNICALL * GetBytecodes)(jvmtiEnv* env,	jmethodID method,	jint* bytecode_count_ptr,	unsigned char** bytecodes_ptr);
	jvmtiError (JNICALL * IsMethodNative)(jvmtiEnv* env,	jmethodID method,	jboolean* is_native_ptr);
	jvmtiError (JNICALL * IsMethodSynthetic)(jvmtiEnv* env,	jmethodID method,	jboolean* is_synthetic_ptr);
	jvmtiError (JNICALL * GetLoadedClasses)(jvmtiEnv* env,	jint* class_count_ptr,	jclass** classes_ptr);
	jvmtiError (JNICALL * GetClassLoaderClasses)(jvmtiEnv* env,	jobject initiating_loader,	jint* class_count_ptr,	jclass** classes_ptr);
	jvmtiError (JNICALL * PopFrame)(jvmtiEnv* env,	jthread thread);
	jvmtiError (JNICALL * ForceEarlyReturnObject)(jvmtiEnv* env, jthread thread, jobject value);
	jvmtiError (JNICALL * ForceEarlyReturnInt)(jvmtiEnv* env, jthread thread, jint value);
	jvmtiError (JNICALL * ForceEarlyReturnLong)(jvmtiEnv* env, jthread thread, jlong value);
	jvmtiError (JNICALL * ForceEarlyReturnFloat)(jvmtiEnv* env, jthread thread, jfloat value);
	jvmtiError (JNICALL * ForceEarlyReturnDouble)(jvmtiEnv* env, jthread thread, jdouble value);
	jvmtiError (JNICALL * ForceEarlyReturnVoid)(jvmtiEnv* env, jthread thread);
	jvmtiError (JNICALL * RedefineClasses)(jvmtiEnv* env,	jint class_count,	const jvmtiClassDefinition* class_definitions);
	jvmtiError (JNICALL * GetVersionNumber)(jvmtiEnv* env,	jint* version_ptr);
	jvmtiError (JNICALL * GetCapabilities)(jvmtiEnv* env,	jvmtiCapabilities* capabilities_ptr);
	jvmtiError (JNICALL * GetSourceDebugExtension)(jvmtiEnv* env,	jclass klass,	char** source_debug_extension_ptr);
	jvmtiError (JNICALL * IsMethodObsolete)(jvmtiEnv* env,	jmethodID method,	jboolean* is_obsolete_ptr);
	jvmtiError (JNICALL * SuspendThreadList)(jvmtiEnv* env,	jint request_count,	const jthread* request_list,	jvmtiError* results);
	jvmtiError (JNICALL * ResumeThreadList)(jvmtiEnv* env,	jint request_count,	const jthread* request_list,	jvmtiError* results);
	void *reserved94;
	void *reserved95;
	void *reserved96;
	void *reserved97;
	void *reserved98;
	void *reserved99;
	jvmtiError (JNICALL * GetAllStackTraces)(jvmtiEnv* env,	jint max_frame_count,	jvmtiStackInfo** stack_info_ptr,	jint* thread_count_ptr);
	jvmtiError (JNICALL * GetThreadListStackTraces)(jvmtiEnv* env,	jint thread_count,	const jthread* thread_list,	jint max_frame_count,	jvmtiStackInfo** stack_info_ptr);
	jvmtiError (JNICALL * GetThreadLocalStorage)(jvmtiEnv* env,	jthread thread,	void** data_ptr);
	jvmtiError (JNICALL * SetThreadLocalStorage)(jvmtiEnv* env,	jthread thread,	const void* data);
	jvmtiError (JNICALL * GetStackTrace)(jvmtiEnv* env,	jthread thread,	jint start_depth,	jint max_frame_count,	jvmtiFrameInfo* frame_buffer,	jint* count_ptr);
	void *reserved105;
	jvmtiError (JNICALL * GetTag)(jvmtiEnv* env,	jobject object,	jlong* tag_ptr);
	jvmtiError (JNICALL * SetTag)(jvmtiEnv* env,	jobject object,	jlong tag);
	jvmtiError (JNICALL * ForceGarbageCollection)(jvmtiEnv* env);
	jvmtiError (JNICALL * IterateOverObjectsReachableFromObject)(jvmtiEnv* env,	jobject object,	jvmtiObjectReferenceCallback object_reference_callback,	const void * user_data);
	jvmtiError (JNICALL * IterateOverReachableObjects)(jvmtiEnv* env,	jvmtiHeapRootCallback heap_root_callback,	jvmtiStackReferenceCallback stack_ref_callback,	jvmtiObjectReferenceCallback object_ref_callback, const void * user_data);
	jvmtiError (JNICALL * IterateOverHeap)(jvmtiEnv* env,	jvmtiHeapObjectFilter object_filter,	jvmtiHeapObjectCallback heap_object_callback, const void * user_data);
	jvmtiError (JNICALL * IterateOverInstancesOfClass)(jvmtiEnv* env,	jclass klass,	jvmtiHeapObjectFilter object_filter,	jvmtiHeapObjectCallback heap_object_callback, const void * user_data);
	void *reserved113;
	jvmtiError (JNICALL * GetObjectsWithTags)(jvmtiEnv* env,	jint tag_count,	const jlong* tags,	jint* count_ptr,	jobject** object_result_ptr,	jlong** tag_result_ptr);
	jvmtiError (JNICALL * FollowReferences)(jvmtiEnv* env, jint heap_filter, jclass klass, jobject initial_object, const jvmtiHeapCallbacks* callbacks, const void* user_data);
	jvmtiError (JNICALL * IterateThroughHeap)(jvmtiEnv* env, jint heap_filter, jclass klass, const jvmtiHeapCallbacks* callbacks, const void* user_data);
	void *reserved117;
	void *reserved118;
	void *reserved119;
	jvmtiError (JNICALL * SetJNIFunctionTable)(jvmtiEnv* env,	const jniNativeInterface* function_table);
	jvmtiError (JNICALL * GetJNIFunctionTable)(jvmtiEnv* env,	jniNativeInterface** function_table);
	jvmtiError (JNICALL * SetEventCallbacks)(jvmtiEnv* env,	const jvmtiEventCallbacks* callbacks,	jint size_of_callbacks);
	jvmtiError (JNICALL * GenerateEvents)(jvmtiEnv* env,	jvmtiEvent event_type);
	jvmtiError (JNICALL * GetExtensionFunctions)(jvmtiEnv* env,	jint* extension_count_ptr,	jvmtiExtensionFunctionInfo** extensions);
	jvmtiError (JNICALL * GetExtensionEvents)(jvmtiEnv* env,	jint* extension_count_ptr,	jvmtiExtensionEventInfo** extensions);
	jvmtiError (JNICALL * SetExtensionEventCallback)(jvmtiEnv* env,	jint extension_event_index,	jvmtiExtensionEvent callback);
	jvmtiError (JNICALL * DisposeEnvironment)(jvmtiEnv* env);
	jvmtiError (JNICALL * GetErrorName)(jvmtiEnv* env,	jvmtiError error,	char** name_ptr);
	jvmtiError (JNICALL * GetJLocationFormat)(jvmtiEnv* env,	jvmtiJlocationFormat* format_ptr);
	jvmtiError (JNICALL * GetSystemProperties)(jvmtiEnv* env,	jint* count_ptr,	char*** property_ptr);
	jvmtiError (JNICALL * GetSystemProperty)(jvmtiEnv* env,	const char* property,	char** value_ptr);
	jvmtiError (JNICALL * SetSystemProperty)(jvmtiEnv* env,	const char* property,	const char* value);
	jvmtiError (JNICALL * GetPhase)(jvmtiEnv* env,	jvmtiPhase* phase_ptr);
	jvmtiError (JNICALL * GetCurrentThreadCpuTimerInfo)(jvmtiEnv* env,	jvmtiTimerInfo* info_ptr);
	jvmtiError (JNICALL * GetCurrentThreadCpuTime)(jvmtiEnv* env,	jlong* nanos_ptr);
	jvmtiError (JNICALL * GetThreadCpuTimerInfo)(jvmtiEnv* env,	jvmtiTimerInfo* info_ptr);
	jvmtiError (JNICALL * GetThreadCpuTime)(jvmtiEnv* env,	jthread thread,	jlong* nanos_ptr);
	jvmtiError (JNICALL * GetTimerInfo)(jvmtiEnv* env,	jvmtiTimerInfo* info_ptr);
	jvmtiError (JNICALL * GetTime)(jvmtiEnv* env,	jlong* nanos_ptr);
	jvmtiError (JNICALL * GetPotentialCapabilities)(jvmtiEnv* env,	jvmtiCapabilities* capabilities_ptr);
	void *reserved141;
	jvmtiError (JNICALL * AddCapabilities)(jvmtiEnv* env,	const jvmtiCapabilities* capabilities_ptr);
	jvmtiError (JNICALL * RelinquishCapabilities)(jvmtiEnv* env,	const jvmtiCapabilities* capabilities_ptr);
	jvmtiError (JNICALL * GetAvailableProcessors)(jvmtiEnv* env,	jint* processor_count_ptr);
	jvmtiError (JNICALL * GetClassVersionNumbers)(jvmtiEnv* env, jclass klass, jint* minor_version_ptr, jint* major_version_ptr);
	jvmtiError (JNICALL * GetConstantPool)(jvmtiEnv* env, jclass klass, jint* constant_pool_count_ptr, jint* constant_pool_byte_count_ptr, unsigned char** constant_pool_bytes_ptr);
	jvmtiError (JNICALL * GetEnvironmentLocalStorage)(jvmtiEnv* env,	void** data_ptr);
	jvmtiError (JNICALL * SetEnvironmentLocalStorage)(jvmtiEnv* env,	const void* data);
	jvmtiError (JNICALL * AddToBootstrapClassLoaderSearch)(jvmtiEnv* env,	const char* segment);
	jvmtiError (JNICALL * SetVerboseFlag)(jvmtiEnv* env,	jvmtiVerboseFlag flag,	jboolean value);
	jvmtiError (JNICALL * AddToSystemClassLoaderSearch)(jvmtiEnv* env, const char* segment);
	jvmtiError (JNICALL * RetransformClasses)(jvmtiEnv* env, jint class_count, const jclass* classes);
	jvmtiError (JNICALL * GetOwnedMonitorStackDepthInfo)(jvmtiEnv* env, jthread thread, jint* monitor_info_count_ptr, jvmtiMonitorStackDepthInfo** monitor_info_ptr);
	jvmtiError (JNICALL * GetObjectSize)(jvmtiEnv* env,	jobject object,	jlong* size_ptr);
	jvmtiError (JNICALL * GetLocalInstance)(jvmtiEnv* env, jthread thread, jint depth, jobject* value_ptr);
} jvmtiNativeInterface;

struct _jvmtiEnv {
	const struct JVMTINativeInterface_ * functions;
#ifdef __cplusplus
	jvmtiError SetEventNotificationMode (jvmtiEventMode mode,	jvmtiEvent event_type,	jthread event_thread,	...) { return functions->SetEventNotificationMode(this, mode, event_type, event_thread); }
	jvmtiError GetAllThreads (jint* threads_count_ptr,	jthread** threads_ptr) { return functions->GetAllThreads(this, threads_count_ptr, threads_ptr); }
	jvmtiError SuspendThread (jthread thread) { return functions->SuspendThread(this, thread); }
	jvmtiError ResumeThread (jthread thread) { return functions->ResumeThread(this, thread); }
	jvmtiError StopThread (jthread thread,	jobject exception) { return functions->StopThread(this, thread, exception); }
	jvmtiError InterruptThread (jthread thread) { return functions->InterruptThread(this, thread); }
	jvmtiError GetThreadInfo (jthread thread,	jvmtiThreadInfo* info_ptr) { return functions->GetThreadInfo(this, thread, info_ptr); }
	jvmtiError GetOwnedMonitorInfo (jthread thread,	jint* owned_monitor_count_ptr,	jobject** owned_monitors_ptr) { return functions->GetOwnedMonitorInfo(this, thread, owned_monitor_count_ptr, owned_monitors_ptr); }
	jvmtiError GetCurrentContendedMonitor (jthread thread,	jobject* monitor_ptr) { return functions->GetCurrentContendedMonitor(this, thread, monitor_ptr); }
	jvmtiError RunAgentThread (jthread thread,	jvmtiStartFunction proc,	const void* arg,	jint priority) { return functions->RunAgentThread(this, thread, proc, arg, priority); }
	jvmtiError GetTopThreadGroups (jint* group_count_ptr,	jthreadGroup** groups_ptr) { return functions->GetTopThreadGroups(this, group_count_ptr, groups_ptr); }
	jvmtiError GetThreadGroupInfo (jthreadGroup group,	jvmtiThreadGroupInfo* info_ptr) { return functions->GetThreadGroupInfo(this, group, info_ptr); }
	jvmtiError GetThreadGroupChildren (jthreadGroup group,	jint* thread_count_ptr,	jthread** threads_ptr,	jint* group_count_ptr,	jthreadGroup** groups_ptr) { return functions->GetThreadGroupChildren(this, group, thread_count_ptr, threads_ptr, group_count_ptr, groups_ptr); }
	jvmtiError GetFrameCount (jthread thread,	jint* count_ptr) { return functions->GetFrameCount(this, thread, count_ptr); }
	jvmtiError GetThreadState (jthread thread,	jint* thread_state_ptr) { return functions->GetThreadState(this, thread, thread_state_ptr); }
	jvmtiError GetCurrentThread (jthread* thread_ptr) {  return functions->GetCurrentThread(this, thread_ptr); }
	jvmtiError GetFrameLocation (jthread thread,	jint depth,	jmethodID* method_ptr,	jlocation* location_ptr) { return functions->GetFrameLocation(this, thread, depth, method_ptr, location_ptr); }
	jvmtiError NotifyFramePop (jthread thread,	jint depth) { return functions->NotifyFramePop(this, thread, depth); }
	jvmtiError GetLocalObject (jthread thread,	jint depth,	jint slot,	jobject* value_ptr) { return functions->GetLocalObject(this, thread, depth, slot, value_ptr); }
	jvmtiError GetLocalInt (jthread thread,	jint depth,	jint slot,	jint* value_ptr) { return functions->GetLocalInt(this, thread, depth, slot, value_ptr); }
	jvmtiError GetLocalLong (jthread thread,	jint depth,	jint slot,	jlong* value_ptr) { return functions->GetLocalLong(this, thread, depth, slot, value_ptr); }
	jvmtiError GetLocalFloat (jthread thread,	jint depth,	jint slot,	jfloat* value_ptr) { return functions->GetLocalFloat(this, thread, depth, slot, value_ptr); }
	jvmtiError GetLocalDouble (jthread thread,	jint depth,	jint slot,	jdouble* value_ptr) { return functions->GetLocalDouble(this, thread, depth, slot, value_ptr); }
	jvmtiError SetLocalObject (jthread thread,	jint depth,	jint slot,	jobject value) { return functions->SetLocalObject(this, thread, depth, slot, value); }
	jvmtiError SetLocalInt (jthread thread,	jint depth,	jint slot,	jint value) { return functions->SetLocalInt(this, thread, depth, slot, value); }
	jvmtiError SetLocalLong (jthread thread,	jint depth,	jint slot,	jlong value) { return functions->SetLocalLong(this, thread, depth, slot, value); }
	jvmtiError SetLocalFloat (jthread thread,	jint depth,	jint slot,	jfloat value) { return functions->SetLocalFloat(this, thread, depth, slot, value); }
	jvmtiError SetLocalDouble (jthread thread,	jint depth,	jint slot,	jdouble value) { return functions->SetLocalDouble(this, thread, depth, slot, value); }
	jvmtiError CreateRawMonitor (const char* name,	jrawMonitorID* monitor_ptr) { return functions->CreateRawMonitor(this, name, monitor_ptr); }
	jvmtiError DestroyRawMonitor (jrawMonitorID monitor) { return functions->DestroyRawMonitor(this, monitor); }
	jvmtiError RawMonitorEnter (jrawMonitorID monitor) { return functions->RawMonitorEnter(this, monitor); }
	jvmtiError RawMonitorExit (jrawMonitorID monitor) { return functions->RawMonitorExit(this, monitor); }
	jvmtiError RawMonitorWait (jrawMonitorID monitor,	jlong millis) { return functions->RawMonitorWait(this, monitor, millis); }
	jvmtiError RawMonitorNotify (jrawMonitorID monitor) { return functions->RawMonitorNotify(this, monitor); }
	jvmtiError RawMonitorNotifyAll (jrawMonitorID monitor) { return functions->RawMonitorNotifyAll(this, monitor); }
	jvmtiError SetBreakpoint (jmethodID method,	jlocation location) { return functions->SetBreakpoint(this, method, location); }
	jvmtiError ClearBreakpoint (jmethodID method,	jlocation location) { return functions->ClearBreakpoint(this, method, location); }
	jvmtiError SetFieldAccessWatch (jclass klass,	jfieldID field) { return functions->SetFieldAccessWatch(this, klass, field); }
	jvmtiError ClearFieldAccessWatch (jclass klass,	jfieldID field) { return functions->ClearFieldAccessWatch(this, klass, field); }
	jvmtiError SetFieldModificationWatch (jclass klass,	jfieldID field) { return functions->SetFieldModificationWatch(this, klass, field); }
	jvmtiError ClearFieldModificationWatch (jclass klass,	jfieldID field) { return functions->ClearFieldModificationWatch(this, klass, field); }
	jvmtiError IsModifiableClass (jclass klass, jboolean* is_modifiable_class_ptr) { return functions->IsModifiableClass(this, klass, is_modifiable_class_ptr); }
	jvmtiError Allocate (jlong size,	unsigned char** mem_ptr) { return functions->Allocate(this, size, mem_ptr); }
	jvmtiError Deallocate (unsigned char* mem) { return functions->Deallocate(this, mem); }
	jvmtiError GetClassSignature (jclass klass,	char** signature_ptr,	char** generic_ptr) { return functions->GetClassSignature(this, klass, signature_ptr, generic_ptr); }
	jvmtiError GetClassStatus (jclass klass,	jint* status_ptr) { return functions->GetClassStatus(this, klass, status_ptr); }
	jvmtiError GetSourceFileName (jclass klass,	char** source_name_ptr) { return functions->GetSourceFileName(this, klass, source_name_ptr); }
	jvmtiError GetClassModifiers (jclass klass,	jint* modifiers_ptr) { return functions->GetClassModifiers(this, klass, modifiers_ptr); }
	jvmtiError GetClassMethods (jclass klass,	jint* method_count_ptr,	jmethodID** methods_ptr) { return functions->GetClassMethods(this, klass, method_count_ptr, methods_ptr); }
	jvmtiError GetClassFields (jclass klass,	jint* field_count_ptr,	jfieldID** fields_ptr) { return functions->GetClassFields(this, klass, field_count_ptr, fields_ptr); }
	jvmtiError GetImplementedInterfaces (jclass klass,	jint* interface_count_ptr,	jclass** interfaces_ptr) { return functions->GetImplementedInterfaces(this, klass, interface_count_ptr, interfaces_ptr); }
	jvmtiError IsInterface (jclass klass,	jboolean* is_interface_ptr) { return functions->IsInterface(this, klass, is_interface_ptr); }
	jvmtiError IsArrayClass (jclass klass,	jboolean* is_array_class_ptr) { return functions->IsArrayClass(this, klass, is_array_class_ptr); }
	jvmtiError GetClassLoader (jclass klass,	jobject* classloader_ptr) { return functions->GetClassLoader(this, klass, classloader_ptr); }
	jvmtiError GetObjectHashCode (jobject object,	jint* hash_code_ptr) { return functions->GetObjectHashCode(this, object, hash_code_ptr); }
	jvmtiError GetObjectMonitorUsage (jobject object,	jvmtiMonitorUsage* info_ptr) { return functions->GetObjectMonitorUsage(this, object, info_ptr); }
	jvmtiError GetFieldName (jclass klass,	jfieldID field,	char** name_ptr,	char** signature_ptr,	char** generic_ptr) { return functions->GetFieldName(this, klass, field, name_ptr, signature_ptr, generic_ptr); }
	jvmtiError GetFieldDeclaringClass (jclass klass,	jfieldID field,	jclass* declaring_class_ptr) { return functions->GetFieldDeclaringClass(this, klass, field, declaring_class_ptr); }
	jvmtiError GetFieldModifiers (jclass klass,	jfieldID field,	jint* modifiers_ptr) { return functions->GetFieldModifiers(this, klass, field, modifiers_ptr); }
	jvmtiError IsFieldSynthetic (jclass klass,	jfieldID field,	jboolean* is_synthetic_ptr) { return functions->IsFieldSynthetic(this, klass, field, is_synthetic_ptr); }
	jvmtiError GetMethodName (jmethodID method,	char** name_ptr,	char** signature_ptr,	char** generic_ptr) { return functions->GetMethodName(this, method, name_ptr, signature_ptr, generic_ptr); }
	jvmtiError GetMethodDeclaringClass (jmethodID method,	jclass* declaring_class_ptr) { return functions->GetMethodDeclaringClass(this, method, declaring_class_ptr); }
	jvmtiError GetMethodModifiers (jmethodID method,	jint* modifiers_ptr) { return functions->GetMethodModifiers(this, method, modifiers_ptr); }
	jvmtiError GetMaxLocals (jmethodID method,	jint* max_ptr) { return functions->GetMaxLocals(this, method, max_ptr); }
	jvmtiError GetArgumentsSize (jmethodID method,	jint* size_ptr) { return functions->GetArgumentsSize(this, method, size_ptr); }
	jvmtiError GetLineNumberTable (jmethodID method,	jint* entry_count_ptr,	jvmtiLineNumberEntry** table_ptr) { return functions->GetLineNumberTable(this, method, entry_count_ptr, table_ptr); }
	jvmtiError GetMethodLocation (jmethodID method,	jlocation* start_location_ptr,	jlocation* end_location_ptr) { return functions->GetMethodLocation(this, method, start_location_ptr, end_location_ptr); }
	jvmtiError GetLocalVariableTable (jmethodID method,	jint* entry_count_ptr,	jvmtiLocalVariableEntry** table_ptr) { return functions->GetLocalVariableTable(this, method, entry_count_ptr, table_ptr); }
	jvmtiError SetNativeMethodPrefix (const char* prefix) { return functions->SetNativeMethodPrefix(this, prefix); }
	jvmtiError SetNativeMethodPrefixes (jint prefix_count, char** prefixes) { return functions->SetNativeMethodPrefixes(this, prefix_count, prefixes); }
	jvmtiError GetBytecodes (jmethodID method,	jint* bytecode_count_ptr,	unsigned char** bytecodes_ptr) { return functions->GetBytecodes(this, method, bytecode_count_ptr, bytecodes_ptr); }
	jvmtiError IsMethodNative (jmethodID method,	jboolean* is_native_ptr) { return functions->IsMethodNative(this, method, is_native_ptr); }
	jvmtiError IsMethodSynthetic (jmethodID method,	jboolean* is_synthetic_ptr) { return functions->IsMethodSynthetic(this, method, is_synthetic_ptr); }
	jvmtiError GetLoadedClasses (jint* class_count_ptr,	jclass** classes_ptr) { return functions->GetLoadedClasses(this, class_count_ptr, classes_ptr); }
	jvmtiError GetClassLoaderClasses (jobject initiating_loader,	jint* class_count_ptr,	jclass** classes_ptr) { return functions->GetClassLoaderClasses(this, initiating_loader, class_count_ptr, classes_ptr); }
	jvmtiError PopFrame (jthread thread) { return functions->PopFrame(this, thread); }
	jvmtiError ForceEarlyReturnObject (jthread thread, jobject value) { return functions->ForceEarlyReturnObject(this, thread, value); }
	jvmtiError ForceEarlyReturnInt (jthread thread, jint value) { return functions->ForceEarlyReturnInt(this, thread, value); }
	jvmtiError ForceEarlyReturnLong (jthread thread, jlong value) { return functions->ForceEarlyReturnLong(this, thread, value); }
	jvmtiError ForceEarlyReturnFloat (jthread thread, jfloat value) { return functions->ForceEarlyReturnFloat(this, thread, value); }
	jvmtiError ForceEarlyReturnDouble (jthread thread, jdouble value) { return functions->ForceEarlyReturnDouble(this, thread, value); }
	jvmtiError ForceEarlyReturnVoid (jthread thread) { return functions->ForceEarlyReturnVoid(this, thread); }
	jvmtiError RedefineClasses (jint class_count,	const jvmtiClassDefinition* class_definitions) { return functions->RedefineClasses(this, class_count, class_definitions); }
	jvmtiError GetVersionNumber (jint* version_ptr) { return functions->GetVersionNumber(this, version_ptr); }
	jvmtiError GetCapabilities (jvmtiCapabilities* capabilities_ptr) { return functions->GetCapabilities(this, capabilities_ptr); }
	jvmtiError GetSourceDebugExtension (jclass klass,	char** source_debug_extension_ptr) { return functions->GetSourceDebugExtension(this, klass, source_debug_extension_ptr); }
	jvmtiError IsMethodObsolete (jmethodID method,	jboolean* is_obsolete_ptr) { return functions->IsMethodObsolete(this, method, is_obsolete_ptr); }
	jvmtiError SuspendThreadList (jint request_count,	const jthread* request_list,	jvmtiError* results) { return functions->SuspendThreadList(this, request_count, request_list, results); }
	jvmtiError ResumeThreadList (jint request_count,	const jthread* request_list,	jvmtiError* results) { return functions->ResumeThreadList(this, request_count, request_list, results); }
	jvmtiError GetAllStackTraces (jint max_frame_count,	jvmtiStackInfo** stack_info_ptr,	jint* thread_count_ptr) { return functions->GetAllStackTraces(this, max_frame_count, stack_info_ptr, thread_count_ptr); }
	jvmtiError GetThreadListStackTraces (jint thread_count,	const jthread* thread_list,	jint max_frame_count,	jvmtiStackInfo** stack_info_ptr) { return functions->GetThreadListStackTraces(this, thread_count, thread_list, max_frame_count, stack_info_ptr); }
	jvmtiError GetThreadLocalStorage (jthread thread,	void** data_ptr) { return functions->GetThreadLocalStorage(this, thread, data_ptr); }
	jvmtiError SetThreadLocalStorage (jthread thread,	const void* data) { return functions->SetThreadLocalStorage(this, thread, data); }
	jvmtiError GetStackTrace (jthread thread,	jint start_depth,	jint max_frame_count,	jvmtiFrameInfo* frame_buffer,	jint* count_ptr) { return functions->GetStackTrace(this, thread, start_depth, max_frame_count, frame_buffer, count_ptr); }
	jvmtiError GetTag (jobject object,	jlong* tag_ptr) { return functions->GetTag(this, object, tag_ptr); }
	jvmtiError SetTag (jobject object,	jlong tag) { return functions->SetTag(this, object, tag); }
	jvmtiError ForceGarbageCollection (void) { return functions->ForceGarbageCollection(this); }
	jvmtiError IterateOverObjectsReachableFromObject (jobject object,	jvmtiObjectReferenceCallback object_reference_callback,	const void * user_data) { return functions->IterateOverObjectsReachableFromObject(this, object, object_reference_callback, user_data); }
	jvmtiError IterateOverReachableObjects (jvmtiHeapRootCallback heap_root_callback,	jvmtiStackReferenceCallback stack_ref_callback,	jvmtiObjectReferenceCallback object_ref_callback,	const void * user_data) { return functions->IterateOverReachableObjects(this, heap_root_callback, stack_ref_callback, object_ref_callback, user_data); }
	jvmtiError IterateOverHeap (jvmtiHeapObjectFilter object_filter,	jvmtiHeapObjectCallback heap_object_callback,	const void * user_data) { return functions->IterateOverHeap(this, object_filter, heap_object_callback, user_data); }
	jvmtiError IterateOverInstancesOfClass (jclass klass,	jvmtiHeapObjectFilter object_filter,	jvmtiHeapObjectCallback heap_object_callback,	const void * user_data) { return functions->IterateOverInstancesOfClass(this, klass, object_filter, heap_object_callback, user_data); }
	jvmtiError GetObjectsWithTags (jint tag_count,	const jlong* tags,	jint* count_ptr,	jobject** object_result_ptr,	jlong** tag_result_ptr) { return functions->GetObjectsWithTags(this, tag_count, tags, count_ptr, object_result_ptr, tag_result_ptr); }
	jvmtiError FollowReferences (jint heap_filter, jclass klass, jobject initial_object, const jvmtiHeapCallbacks* callbacks, const void* user_data) { return functions->FollowReferences(this, heap_filter, klass, initial_object, callbacks, user_data); }
	jvmtiError IterateThroughHeap (jint heap_filter, jclass klass, const jvmtiHeapCallbacks* callbacks, const void* user_data) { return functions->IterateThroughHeap(this, heap_filter, klass, callbacks, user_data); }
	jvmtiError SetJNIFunctionTable (const jniNativeInterface* function_table) { return functions->SetJNIFunctionTable(this, function_table); }
	jvmtiError GetJNIFunctionTable (jniNativeInterface** function_table) { return functions->GetJNIFunctionTable(this, function_table); }
	jvmtiError SetEventCallbacks (const jvmtiEventCallbacks* callbacks,	jint size_of_callbacks) { return functions->SetEventCallbacks(this, callbacks, size_of_callbacks); }
	jvmtiError GenerateEvents (jvmtiEvent event_type) { return functions->GenerateEvents(this, event_type); }
	jvmtiError GetExtensionFunctions (jint* extension_count_ptr,	jvmtiExtensionFunctionInfo** extensions) { return functions->GetExtensionFunctions(this, extension_count_ptr, extensions); }
	jvmtiError GetExtensionEvents (jint* extension_count_ptr,	jvmtiExtensionEventInfo** extensions) { return functions->GetExtensionEvents(this, extension_count_ptr, extensions); }
	jvmtiError SetExtensionEventCallback (jint extension_event_index,	jvmtiExtensionEvent callback) { return functions->SetExtensionEventCallback(this, extension_event_index, callback); }
	jvmtiError DisposeEnvironment (void) { return functions->DisposeEnvironment(this); }
	jvmtiError GetErrorName (jvmtiError error,	char** name_ptr) { return functions->GetErrorName(this, error, name_ptr); }
	jvmtiError GetJLocationFormat (jvmtiJlocationFormat* format_ptr) { return functions->GetJLocationFormat(this, format_ptr); }
	jvmtiError GetSystemProperties (jint* count_ptr,	char*** property_ptr) { return functions->GetSystemProperties(this, count_ptr, property_ptr); }
	jvmtiError GetSystemProperty (const char* property,	char** value_ptr) { return functions->GetSystemProperty(this, property, value_ptr); }
	jvmtiError SetSystemProperty (const char* property,	const char* value) { return functions->SetSystemProperty(this, property, value); }
	jvmtiError GetPhase (jvmtiPhase* phase_ptr) { return functions->GetPhase(this, phase_ptr); }
	jvmtiError GetCurrentThreadCpuTimerInfo (jvmtiTimerInfo* info_ptr) { return functions->GetCurrentThreadCpuTimerInfo(this, info_ptr); }
	jvmtiError GetCurrentThreadCpuTime (jlong* nanos_ptr) { return functions->GetCurrentThreadCpuTime(this, nanos_ptr); }
	jvmtiError GetThreadCpuTimerInfo (jvmtiTimerInfo* info_ptr) { return functions->GetThreadCpuTimerInfo(this, info_ptr); }
	jvmtiError GetThreadCpuTime (jthread thread,	jlong* nanos_ptr) { return functions->GetThreadCpuTime(this, thread, nanos_ptr); }
	jvmtiError GetTimerInfo (jvmtiTimerInfo* info_ptr) { return functions->GetTimerInfo(this, info_ptr); }
	jvmtiError GetTime (jlong* nanos_ptr) { return functions->GetTime(this, nanos_ptr); }
	jvmtiError GetPotentialCapabilities (jvmtiCapabilities* capabilities_ptr) { return functions->GetPotentialCapabilities(this, capabilities_ptr); }
	jvmtiError AddCapabilities (const jvmtiCapabilities* capabilities_ptr) { return functions->AddCapabilities(this, capabilities_ptr); }
	jvmtiError RelinquishCapabilities (const jvmtiCapabilities* capabilities_ptr) { return functions->RelinquishCapabilities(this, capabilities_ptr); }
	jvmtiError GetAvailableProcessors (jint* processor_count_ptr) { return functions->GetAvailableProcessors(this, processor_count_ptr); }
	jvmtiError GetClassVersionNumbers (jclass klass, jint* minor_version_ptr, jint* major_version_ptr) { return functions->GetClassVersionNumbers(this, klass, minor_version_ptr, major_version_ptr); }
	jvmtiError GetConstantPool (jclass klass, jint* constant_pool_count_ptr, jint* constant_pool_byte_count_ptr, unsigned char** constant_pool_bytes_ptr) { return functions->GetConstantPool(this, klass, constant_pool_count_ptr, constant_pool_byte_count_ptr, constant_pool_bytes_ptr); }
	jvmtiError GetEnvironmentLocalStorage (void** data_ptr) { return functions->GetEnvironmentLocalStorage(this, data_ptr); }
	jvmtiError SetEnvironmentLocalStorage (const void* data) { return functions->SetEnvironmentLocalStorage(this, data); }
	jvmtiError AddToBootstrapClassLoaderSearch (const char* segment) { return functions->AddToBootstrapClassLoaderSearch(this, segment); }
	jvmtiError SetVerboseFlag (jvmtiVerboseFlag flag,	jboolean value) { return functions->SetVerboseFlag(this, flag, value); }
	jvmtiError AddToSystemClassLoaderSearch (const char* segment) { return functions->AddToSystemClassLoaderSearch(this, segment); }
	jvmtiError RetransformClasses (jint class_count, const jclass* classes) { return functions->RetransformClasses(this, class_count, classes); }
	jvmtiError GetOwnedMonitorStackDepthInfo (jthread thread, jint* monitor_info_count_ptr, jvmtiMonitorStackDepthInfo** monitor_info_ptr) { return functions->GetOwnedMonitorStackDepthInfo(this, thread, monitor_info_count_ptr, monitor_info_ptr); }
	jvmtiError GetObjectSize (jobject object, jlong* size_ptr) { return functions->GetObjectSize(this, object, size_ptr); }
	jvmtiError GetLocalInstance (jthread thread, jint depth, jobject* value_ptr) { return functions->GetLocalInstance(this, thread, depth, value_ptr); }
#endif
};

#ifdef __cplusplus
}
#endif

#endif     /* jvmti_h */
