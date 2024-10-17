import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CubicEquationSolver {
    private final int STEP = 1;
    //private int iterCounter = 0;
    
    private final double epsilon;
    private final double a;
    private final double b;
    private final double c;
    
    private double x1;
    private double x2;
    private double fx1;
    private double fx2;
    
    private final HashMap<Double, Integer> cubic_roots;
    
    public CubicEquationSolver(double epsilon, double a, double b, double c) {
        this.epsilon = epsilon;
        this.a = a;
        this.b = b;
        this.c = c;
        cubic_roots = new HashMap<>();
    }
    
    private double dichotomyMethod(double left, double right, int deep) {
        double fa = f(left), fb = f(right);
        
        if (Math.abs(fa) <= epsilon) return left;
        if (Math.abs(fb) <= epsilon) return right;
        
        double middle = (right + left) / 2;
        
        int STOP = 4000;
        if (deep >= STOP) return middle;
        
        double f_middle = f(middle);
        
        if (Math.abs(f_middle) <= epsilon) {
            return middle;
        } else if (f_middle > epsilon) {
            if (fa > epsilon) return dichotomyMethod(middle, right, deep + 1);
            else              return dichotomyMethod(left, middle, deep + 1);
        } else if (f_middle < epsilon) {
            if (fa > epsilon) return dichotomyMethod(left, middle, deep + 1);
            else              return dichotomyMethod(middle, right, deep + 1);
        } else return -1;
    }
    
    // считаем значение функции f(x) = x^3 + ax^2 + bx + c в точке x
    private double f(double x) {
        return x * x * x + a * x * x + b * x + c;
    }
    
    // считаем дискриминант производной 3x^2 + 2ax + b
    private double countDiscriminant() {
        return a * a - 3 * b;
    }
    
    private void swap() {
        double tmp = x1;
        x1 = x2;
        x2 = tmp;
    }
    
    // количество корней в кубическом уравнении
    private int getRootsAmount(double D) {
        if (D < -4 * a * epsilon) return 0;
        else if (Math.abs(D) <= 4 * a) {
            x1 = (-a) / 2;
            fx1 = f(x1);
            return 1;
        }
        else {
            x1 = (-a - Math.sqrt(D)) / 3;
            x2 = (-a + Math.sqrt(D)) / 3;
            if (x1 > x2) swap();
            fx1 = f(x1);
            fx2 = f(x2);
            return 2;
        }
    }
    
    // (-inf, x)
    private void intervalUpTo(double x, int multiplicity) {
        double left_bound = x;
        while (f(left_bound) >= epsilon) left_bound -= STEP;
        cubic_roots.put(dichotomyMethod(left_bound, x, 0), multiplicity);
    }
    
    // (x, +inf)
    private void intervalFrom(double x, int multiplicity) {
        double right_bound = x;
        while (f(right_bound) <= epsilon) right_bound += STEP;
        cubic_roots.put(dichotomyMethod(x, right_bound, 0), multiplicity);
    }
    
    private void twoRoots() {
        if (fx1 > epsilon && fx2 > epsilon) {
            intervalUpTo(x1, 3);
        } else if (Math.abs(fx2) <= epsilon) {
            cubic_roots.put(x2, 2);
            intervalUpTo(x1, 1);
        } else if (fx1 > epsilon && fx2 < -epsilon) {
            intervalUpTo(x1, 1);
            cubic_roots.put(dichotomyMethod(x1, x2, 0), 1);   // (x1, x2)
            intervalFrom(x2, 1);
        } else if (Math.abs(fx1) <= epsilon) {
            cubic_roots.put(x1, 2);
            intervalFrom(x2, 1);
        } else if (fx1 < -epsilon) {
            intervalFrom(x2, 3);
        } else cubic_roots.put((x2 + x1) / 2, 3);
    }
    
    private void oneRoot() {
        if (a == 0 && b == 0 && c == 0) cubic_roots.put(0.0, 3);
        else if (fx1 > epsilon) intervalUpTo(x1, 3);
        else if (fx1 < -epsilon) intervalFrom(x2, 3);
        else printError();
    }
    
    private void increasingFunc() {
        if (c > epsilon) intervalUpTo(0, 3);
        else if (Math.abs(c) < epsilon) cubic_roots.put(c, 3);
        else if (c < -epsilon) intervalFrom(0, 3);
    }
    
    public void solve() {
        int roots = getRootsAmount(countDiscriminant());
        if      (roots == 2) twoRoots();
        else if (roots == 1) oneRoot();
        else if (roots == 0) increasingFunc();
        printRoots();
    }
    
    private void printRoots() {
        AtomicInteger count = new AtomicInteger(1); // используется для того, чтобы обозначить x1, x2 и т.д.
        cubic_roots.forEach((key, value) -> System.out.println("x" + count.getAndIncrement() + " = " + String.format("%.16f", key) + "\tкратность " + value));
    }
    
    private void printError() {
        System.err.println("Что-то пошло не так во время вычислений...");
    }
}
