/* File : example.h */
class Circle;

class Shape {
  int toto;
protected:
public:
  void *data;
  Shape();
  virtual ~Shape();
  double  x, y;   
  void move(double dx, double dy);
  virtual double area() = 0;
  virtual double perimeter() = 0;
  //static  int nshapes;
  Shape *getParam(Shape *c);
  Shape *getThis();
  Shape *getCircle();
  int getToto();
};

class Circle : public Shape {
private:
  double radius;
  Circle *c;
public:
  Circle();
  Circle(double r);
  double area();
  double perimeter();
  Circle test();
  Circle *test2();
};

class Square : public Shape {
private:
  double width;
  Square *s;
public:
  Square(double w);
  double area();
  double perimeter();
  Square *getSubSquare();
};
