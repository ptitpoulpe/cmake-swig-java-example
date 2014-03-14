/* File : example.i */
%module(directors="1") example
%include "typemaps.i"

%pragma(java) jniclasscode=%{
  static {
    NativeLib.nativeInit();
  }
%}

%typemap(javabody) SWIGTYPE %{
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected $javaclassname(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
    exampleJNI.setDatata(cPtr, this);
  }

  protected static long getCPtr($javaclassname obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }
%}

%typemap(javaout) SWIGTYPE * {
  long _c = $jnicall;
  Object o = exampleJNI.getDatata(_c);
  System.out.println("Test getDatata: "+o);
  $javaclassname jc;
  if (o==null) {
   jc = new $javaclassname(_c, $owner);
  } else {
   jc = ($javaclassname) o;
  }
  System.out.println("Test datata: "+jc);
  return jc;
}

%{
#include "example.hpp"

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jobject JNICALL Java_example_exampleJNI_datata(JNIEnv *env, jclass cls, jlong jarg1)
{
  void *jo = ((Circle *)jarg1)->data;
  jobject res;
  if (jo) {
    res = (jobject) jo;
  } else {
    jclass clss = env->FindClass("example/Circle");
    jmethodID constru = env->GetMethodID(clss, "<init>", "()V"); // no parameters  
    res = env->NewObject(clss, constru);
    res = env->NewGlobalRef(res);
    ((Circle *)jarg1)->data = res;
  }
  printf("datata:c %p\n", res);
  return res;
}

JNIEXPORT jobject JNICALL JNICALL Java_example_exampleJNI_getDatata(JNIEnv *env, jclass cls, jlong jarg1)
{
  printf("getDatata: %p, %p\n",(Shape *)jarg1, ((Shape *)jarg1)->data);
  
  return (jobject)((Shape *)jarg1)->data;
}

JNIEXPORT void JNICALL JNICALL Java_example_exampleJNI_setDatata(JNIEnv *env, jclass cls, jlong jarg1, jobject jarg2)
{
  printf("setDatata: %p, %p\n",(Shape *)jarg1, (void *)jarg2);

  ((Shape *)jarg1)->data = (void *) env->NewGlobalRef(jarg2);
}

#ifdef __cplusplus
}
#endif

%}

/* Let's just grab the original header file here */
%feature("director") Shape;
%include "example.hpp"

%native(datata) jobject datata(jong jarg1);

%native(getDatata) jobject getDatata(jlong jarg1);
%native(setDatata) void setDatata(jlong jarg1, jobject jarg2);


