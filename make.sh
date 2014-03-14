#!/bin/bash -e

rm -f example/* core* hs_err* *.o *.so *.class example_wrap.cxx example_wrap.h
swig -java -c++ -package example -outdir example example.i
gcc -g -fPIC -c example_wrap.cxx
gcc -g -fPIC -c example.cpp
ld -G example_wrap.o example.o -o libexample.so
javac example/*.java
javac Triangle.java
javac example_test.java
java -cp . example_test


