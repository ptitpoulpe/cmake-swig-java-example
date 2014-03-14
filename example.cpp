#include "example.hpp"
#include <stdio.h>

#include <cstddef>

Shape::Shape() {
  data = NULL;
  //nshapes++;
  toto = 1024;
  printf("new shape %p\n", this);
}
Shape::~Shape() { 
  //nshapes--;
}
void Shape::move(double dx, double dy) {}
Shape *Shape::getParam(Shape *c) {return c;}
Shape *Shape::getThis() {return this;}
Shape *Shape::getCircle() {return new Circle();}
int Shape::getToto() {return toto;}

Circle::Circle() : radius(1.0), c(NULL) {};
Circle::Circle(double r) : radius(r), c(NULL) {};
double Circle::area() {return radius*2;};
double Circle::perimeter() {return radius*3;};
Circle Circle::test() {return Circle(4);}
Circle *Circle::test2() {if (c==NULL) c = new Circle(5); return c;}

Square::Square(double w) : width(w), s(NULL) { printf("new square %p\n", this);}
double Square::area() {return width*2;}
double Square::perimeter() {return width*2;}
Square *Square::getSubSquare() {
  if (s==NULL) 
    s = new Square(5);
  return s;
}

