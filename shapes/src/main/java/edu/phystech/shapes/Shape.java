package edu.phystech.shapes;
import java.util.ArrayList;
import java.util.List;

class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return x;
    }

    public double y(){
        return y;
    }

    public Point add(final Point p) {
        return new Point(x + p.x, y + p.y);
    }

    public Point subtract(final Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Point multiply(double m) {
        return new Point(x * m, y * m);
    }

    public Point devide(double m) {
        return new Point(x / m, y / m);
    }

    public Point rotate(Point p, double angle) {
        double dist = distance(this, p);
        double ang1 = 0; 
        if (x != p.x) {
            ang1 = Math.atan((y - p.y) / (x - p.x));
        } else {
            ang1 = Math.PI / 2;
        }

        if (x < p.x) {
            ang1 = Math.PI + ang1; 
        }
        ang1 += angle;

        return new Point(dist * Math.cos(ang1) + p.x, dist * Math.sin(ang1) + p.y);
    }

    public Point rotate(double angle) {
        return rotate(new Point (0, 0), angle);
    }

    public void set(Point p) {
        x = p.x;
        y = p.y;
    }

    public static Point center(final Point a, final Point b) {
        return new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
    }

    public static double distance(final Point a, final Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }
}

class Vector2 extends Point {
    public Vector2(double x, double y) {
        super(x, y);
    }

    public Vector2(Point p) {
        super(p.x, p.y);
    }

    public Vector2(Point bp, Point ep) {
        super(ep.x - bp.x, ep.y - bp.y);
    }

    public double angle() {
        return Math.atan(y / x);
    }

    public static Vector2 createVector2(double length, double angle) {
        return new Vector2(length * Math.cos(angle), length * Math.sin(angle));
    }

    public static double scalarProduct(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }

    public static double vectorProduct(Vector2 a, Vector2 b) {
        return a.x * b.y - b.x * a.y;
    }

    public static double angle(Vector2 a, Vector2 b) {
        return Math.acos(scalarProduct(a, b) / (a.length() * b.length()));
    }
}

abstract public class Shape {
    abstract public Point center();

    abstract double perimeter();

    abstract double area();

    abstract void translate(final Point newCenter);

    abstract void rotate(final double angle);

    abstract void scale(final double coefficient);
}

class Ellipse extends Shape {
    public Point A;
    public Point B;
    public double per;

    public Ellipse(Point A, Point B, double per) {
        this.A = A;
        this.B = B;
        this.per = Math.abs(per);
    }

    @Override
    public Point center() {
        return Point.center(A, B);
    }

    private Point get_a_b() {
        double dist = Point.distance(A, B);
        double a = dist / 2 + per;
        double len = dist + per * 2;
        double b = Math.sqrt((len * len - dist * dist) / 4);

        return new Point(a, b);
    }

    @Override
    public double perimeter() {
        Point ab = this.get_a_b();
        return 4 * (Math.PI * ab.x * ab.y + Math.pow((ab.x - ab.y), 2)) / (ab.x + ab.y);
    }

    @Override
    public double area() {
        Point ab = this.get_a_b();
        return Math.PI * ab.x * ab.y;
    }

    @Override
    public void translate(final Point newCenter) {
        Point curCenter = this.center();
        Point shift = newCenter.subtract(curCenter);
        A = A.add(shift);
        B = B.add(shift);
    }

    @Override
    public void rotate(final double angle) {
        Point curCenter = this.center();
        A = A.rotate(curCenter, angle);
        B = B.rotate(curCenter, angle);
    }

    @Override
    public void scale(final double coefficient) {
        Point curCenter = this.center();
        A = A.subtract(curCenter).multiply(coefficient).add(curCenter);
        B = B.subtract(curCenter).multiply(coefficient).add(curCenter);
        per = Math.abs(per * coefficient);
    }

    public List<Point> focuses() {
        List<Point> lst = new ArrayList<Point>();
        lst.add(A);
        lst.add(B);
        return lst;
    }

    public double focalDistance() {
        return Point.distance(A, B) / 2;
    }

    public double majorSemiAxis() {
        return Point.distance(A, B) / 2 + per;
    }

    public double minorSemiAxis() {
        double dist = Point.distance(A, B);
        double len = dist + per * 2;
        return Math.sqrt((len * len - dist * dist) / 4);
    }

    public double eccentricity() {
        return this.focalDistance() / this.majorSemiAxis();
    }
}

class Circle extends Ellipse {
    public Circle(Point C, double per) {
        super(C, C, per);
    }

    @Override
    public double focalDistance() {
        return 0;
    }

    @Override
    public double eccentricity() {
        return 0;
    }

    @Override
    public double majorSemiAxis() {
        return per;
    }

    @Override
    public double minorSemiAxis() {
        return per;
    }

    @Override
    public double area() {
        return Math.PI * per * per;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * per;
    }

    public double radius() {
        return per;
    }
}

class Rectangle extends Shape {
    Point A;
    Point B;
    double a;

    public Rectangle(Point A, Point B, double a) {
        this.A = A;
        this.B = B;

        this.a = a;
    }

    public List<Point> vertices() {
        List<Point> lst = new ArrayList<>();
        double ang = Math.atan((B.y - A.y) / (B.x - A.x));
        if (B.x > A.x) {
            lst.add(new Point(A.x + a / 2 * Math.sin(ang), A.y - a / 2 * Math.cos(ang)));
            lst.add(new Point(B.x + a / 2 * Math.sin(ang), B.y - a / 2 * Math.cos(ang)));
            lst.add(new Point(B.x - a / 2 * Math.sin(ang), B.y + a / 2 * Math.cos(ang)));
            lst.add(new Point(A.x - a / 2 * Math.sin(ang), A.y + a / 2 * Math.cos(ang)));
        } else {
            lst.add(new Point(A.x - a / 2 * Math.sin(ang), A.y + a / 2 * Math.cos(ang)));
            lst.add(new Point(B.x - a / 2 * Math.sin(ang), B.y + a / 2 * Math.cos(ang)));
            lst.add(new Point(B.x + a / 2 * Math.sin(ang), B.y - a / 2 * Math.cos(ang)));
            lst.add(new Point(A.x + a / 2 * Math.sin(ang), A.y - a / 2 * Math.cos(ang)));
        }
        return lst;
    }

    public double firstSide() {
        return Math.sqrt(Math.pow(A.x - B.x, 2) + Math.pow(A.y - B.y, 2));
    }

    public double secondSide() {
        return a;
    }
    
    public double diagonal() {
        return Math.sqrt(Math.pow(this.firstSide(), 2) + Math.pow(secondSide(), 2));
    }

    @Override
    public Point center() {
        return Point.center(A, B);
    }

    @Override
    public double perimeter() {
        return (this.firstSide() + this.secondSide()) * 2;
    }

    @Override
    public double area() {
        return firstSide() * secondSide();
    }

    @Override
    public void translate(final Point newCenter) {
        Point curCenter = center();
        A = A.subtract(curCenter).add(newCenter);
        B = B.subtract(curCenter).add(newCenter);
    }

    @Override
    public void rotate(final double angle) {
        Point curCenter = center();
        A = A.subtract(curCenter).rotate(angle).add(curCenter);
        B = B.subtract(curCenter).rotate(angle).add(curCenter);
    }

    @Override
    public void scale(final double coefficient) {
        Point curCenter = center();
        A = A.subtract(curCenter).multiply(coefficient).add(curCenter);
        B = B.subtract(curCenter).multiply(coefficient).add(curCenter);
        a *= Math.abs(coefficient);
    }
}

class Square extends Rectangle {
    public Square(Point A, Point B) {
        super(A, B, Math.sqrt(Math.pow(A.x - B.x, 2) + Math.pow(A.y - B.y, 2)));
    }

    public double size() {
        return this.a;
    }

    public Circle circumscribedCircle() {
        return new Circle(Point.center(A, B), this.a / Math.sqrt(2));
    }

    public Circle inscribedCircle() {
        return new Circle(Point.center(A, B), this.a / 2);
    }

    public double side() {
        return a;
    }
}

class Triangle extends Shape {
    Point A;
    Point B;
    Point C;

    public Triangle(Point A, Point B, Point C) {
        this.A = A;
        this.B = B;
        this.C = C;
    }

    public List<Point> vertices() {
        List<Point> lst = new ArrayList<>();

        lst.add(A);
        lst.add(B);
        lst.add(C);

        return lst;
    }

    // (x - Ax)^2 + (y - Ay)^2 = R^2
    // (x - Bx)^2 + (y - By)^2 = R^2
    // (x - Cx)^2 + (y - Cy)^2 = R^2
    // x^2 - 2xAx + Ax^2 + y^2 - 2yAy + Ay^2 = R^2
    // x^2 - 2xBx + Bx^2 + y^2 - 2yBy + By^2 = R^2
    // x^2 - 2xCx + Cx^2 + y^2 - 2yCy + Cy^2 = R^2
    // 2x^2 - 4xAx + 2Ax^2 + 2y^2 - 4yAy + 2Ay^2 = 2R^2
    // -4xAx + 2xBx + 2xCx + 2Ax^2 - Bx^2 - Cx^2 + ... = 0

    private Point intersection_midline_perpendiculars() {
        double k1x = 2 * (B.x + C.x - 2 * A.x);
        double k1y = 2 * (B.y + C.y - 2 * A.y);
        double sh1 = 2 * A.x * A.x - B.x * B.x - C.x * C.x 
                    + 2 * A.y * A.y - B.y * B.y - C.y * C.y;
        double k2x = 2 * (C.x + A.x - 2 * B.x);
        double k2y = 2 * (C.y + A.y - 2 * B.y);
        double sh2 = 2 * B.x * B.x - C.x * C.x - A.x * A.x
                    + 2 * B.y * B.y - C.y * C.y - A.y * A.y;

        double y = (k1x * sh2 / k2x - sh1) / (k1y -  k1x * k2y / k2x);
        double x = - (k2y * y + sh2) / k2x;
        return new Point(x, y);
    }

    public Circle circumscribedCircle() {
        Point circumCenter = intersection_midline_perpendiculars();
        return new Circle(circumCenter, Point.distance(circumCenter, A));
    }

    public Circle inscribedCircle() {
        Vector2 ab = new Vector2(A, B);
        double abl = ab.length();
        Vector2 bc = new Vector2(B, C);
        double bcl = bc.length();
        Vector2 ac = new Vector2(A, C);
        double acl = ac.length();

        Vector2 ab1 = new Vector2(ac.multiply(abl / (abl + bcl)));
        double ab1l = ab1.length();
        Vector2 b1b = new Vector2(ab1, ab);
        Vector2 b1o = new Vector2(b1b.multiply(ab1l / (ab1l + abl)));
        Point ao = ab1.add(b1o);
        Point center = A.add(ao);

        double pr = (abl + bcl + acl) / 2;
        return new Circle(center, Math.sqrt(pr * (pr - abl) * (pr - bcl) * (pr - acl)) / pr);
    }


    public Point orthocenter() {
        Vector2 ab = new Vector2(A, B);
        double abl = ab.length();
        Vector2 bc = new Vector2(B, C);
        double bcl = bc.length();
        Vector2 ac = new Vector2(A, C);
        double acl = ac.length();

        Vector2 ab1 = new Vector2(ac.multiply((abl * abl - bcl * bcl + acl * acl) / (2 * acl * acl)));
        Vector2 ba1 = new Vector2(bc.multiply((abl * abl - acl * acl + bcl * bcl) / (2 * bcl * bcl)));
        Vector2 aa1 = new Vector2(ab.add(ba1));

        double cosalp = aa1.length() / ac.length();
        double ahl = ab1.length() / cosalp;
        double ang = Vector2.angle(ac, ab);
        Vector2 ah = aa1;

        if (ang < Math.PI / 2) {
            ah = new Vector2(aa1.multiply(ahl / aa1.length()));
        } else {
            ah = new Vector2(aa1.multiply(- ahl / aa1.length()));
        }

        return A.add(ah);
    }

    public Circle ninePointsCircle() {
        Point center = Point.center(orthocenter(), intersection_midline_perpendiculars());
        double radius = Point.distance((new Vector2(A, C)).multiply(0.5).add(A), center);

        return new Circle(center, radius);
    }

    @Override
    public Point center() {
        return new Point((A.x + B.x + C.x) / 3, (A.y + B.y + C.y) / 3);
    }

    @Override
    public double perimeter() {
        return Point.distance(A, B) + Point.distance(B, C) + Point.distance(C, A);
    }

    @Override
    public double area() {
        double ab = Point.distance(A, B);
        double bc = Point.distance(B, C);
        double ca = Point.distance(C, A);
        double pr = (ab + bc + ca) / 2;
        return Math.sqrt(pr * (pr - ab) * (pr - bc) * (pr - ca));
    }

    @Override
    public void translate(final Point newCenter) {
        Point swift = this.center().subtract(newCenter);
        A = A.subtract(swift);
        B = B.subtract(swift);
        C = C.subtract(swift);
    }

    @Override
    public void rotate(final double angle) {
        Point curCenter = center();
        A = A.rotate(curCenter, angle);
        B = B.rotate(curCenter, angle);
        C = C.rotate(curCenter, angle);
    }

    @Override
    public void scale(final double coefficient) {
        Point curCenter = this.center();
        A = A.subtract(curCenter).multiply(coefficient).add(curCenter);
        B = B.subtract(curCenter).multiply(coefficient).add(curCenter);
        C = C.subtract(curCenter).multiply(coefficient).add(curCenter);
    }
}
