//////// DO NOT EDIT THIS FILE - it is machine generated ////////

#include "CpseisDsig.hh"
#include <jni.h>

extern "C"
{
  JNIEXPORT jlong JNICALL Java_org_cpseis_wrappers_CpseisDsig_nativeCreate
    (JNIEnv *env, jobject obj)
  {
    CpseisDsig *module = new CpseisDsig();
    return (jlong)module;
  }
}