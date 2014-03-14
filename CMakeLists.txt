cmake_minimum_required(VERSION 2.8.6)
project (Example)

#set(CMAKE_MODULE_PATH "${CMAKE_MODULE_PATH};Modules/")
find_package(Java REQUIRED COMPONENTS Runtime Development)
find_package(JNI REQUIRED)
find_package(SWIG REQUIRED)
include(UseJava)
include(UseSWIG)

add_library (Example "example.hpp" "example.cpp")
SET_TARGET_PROPERTIES(Example PROPERTIES LINKER_LANGUAGE CXX)

set(CMAKE_SWIG_FLAGS -package example)
set(CMAKE_SWIG_OUTDIR "example")
SET_SOURCE_FILES_PROPERTIES(example.i PROPERTIES CPLUSPLUS 1)
SET_SOURCE_FILES_PROPERTIES(example.i PROPERTIES SWIG_FLAGS "-includeall")

set(EXAMPLE_JAVA_FILES
  example/Shape.java
  example/example.java
  example/exampleJNI.java
  example/Circle.java
  example/Square.java
  example/SWIGTYPE_p_jong.java
  example/SWIGTYPE_p_void.java
  example/NativeLib.java
  )

set(CMAKE_JNI_TARGET TRUE)
add_jar(example_swig_jar ${EXAMPLE_JAVA_FILES}
       	VERSION 1.2.0
	ENTRY_POINT example)
install_jar(example_swig_jar ${LIB_INSTALL_DIR}/example)
install_jni_symlink(example_swig_jar .)

swig_add_module(example_swig java example.i "example.hpp" "example.cpp")
swig_link_libraries(example_swig Example)

set(JAVA_BUNDLE "${CMAKE_HOME_DIRECTORY}/java_bundle.sh")
set(JAVA_BUNDLE_SO_FILES "${CMAKE_HOME_DIRECTORY}/libexample_swig.so")
set(MANIFEST_FILE "${CMAKE_HOME_DIRECTORY}/MANIFEST")
ADD_CUSTOM_COMMAND(TARGET example_swig
                   POST_BUILD
		   DEPEND example_swig_jar
		   COMMAND ${JAVA_BUNDLE} "example_swig_jar.jar" "${Java_JAVA_EXECUTABLE}" "example.NativeLib" "${CMAKE_STRIP}" -so ${JAVA_BUNDLE_SO_FILES} -txt ${JAVA_BUNDLE_TXT_FILES}
                   COMMAND ${JAVA_ARCHIVE} -uvf "example_swig_jar.jar" "NATIVE"
		   COMMAND ${CMAKE_COMMAND} -E echo "Class-Path: ." > ${MANIFEST_FILE}
                   COMMAND ${JAVA_ARCHIVE} -uvmf "MANIFEST" "example_swig_jar.jar"
                   COMMAND ${CMAKE_COMMAND} -E remove ${MANIFEST_FILE}
		   )

ADD_CUSTOM_COMMAND(TARGET example_swig
                   POST_BUILD
	           COMMAND ${JAVA_COMPILE} "example_test.java")
	   
enable_testing()
ADD_TEST(example-test "java" "example_test")
