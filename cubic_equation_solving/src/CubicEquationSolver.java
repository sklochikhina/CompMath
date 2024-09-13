import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CubicEquationSolver {
    private final int STEP = 100;
    private final int STOP = 4000 / STEP;
    
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
    
    private double dichotomyMethod(double a, double b) {
        double fa = f(a), fb = f(b);
        double temp = (b + a) / 2;
        if (Math.abs(b - a) < epsilon) return temp;
        double f_temp = f(temp);
        if (f_temp > 0) {
            if (fa > 0 && fb < 0) return dichotomyMethod(temp, b);
            else if (fa < 0 && fb > 0) return dichotomyMethod(a, temp);
        } else {
            if (fa > 0 && fb < 0) return dichotomyMethod(a, temp);
            else if (fa < 0 && fb > 0) return dichotomyMethod(temp, b);
        }
        return -1;
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
        if (D > 0) {
            x1 = (-a + Math.sqrt(D)) / 3;
            x2 = (-a - Math.sqrt(D)) / 3;
            if (x1 > x2) swap();
            fx1 = f(x1); fx2 = f(x2);
            
            if (fx1 * fx2 < 0) return 3;
            else if (fx1 * fx2 == 0) return 2;
            else return 1;
        }
        else if (D == 0) {
            x1 = (-a) / 3;
            return 1;
        } else return 0; // на самом деле, корень есть, но функция возрастающая - для неё отдельный метод
    }
    
    private void threeRoots() {
        if (fx1 > epsilon && fx2 < -epsilon) {
            cubic_roots.put(dichotomyMethod(x1, x2), 1);   // (x1, x2)
            intervalFrom(x2, 1);
            intervalUpTo(x1, 1);
        } else if (Math.abs(fx1) < epsilon && Math.abs(fx2) < epsilon) {
            cubic_roots.put((x2 - x1) / 2, 3);
        } else printError("Что-то пошло не так во время вычислений...");
    }
    
    private void twoRoots() {
        if (fx1 > epsilon && Math.abs(fx2) < epsilon) {
            cubic_roots.put(x2, 2);
            intervalUpTo(x1, 1);
        } else if (Math.abs(fx1) < epsilon && fx2 < -epsilon) {
            cubic_roots.put(x1, 2);
            intervalFrom(x2, 1);
        } else printError("Что-то пошло не так во время вычислений...");
    }
    
    private void oneRoot() {
        if (fx1 > epsilon && fx2 > epsilon) intervalUpTo(x1, 3);
        else if (fx1 < -epsilon && fx2 < -epsilon) intervalFrom(x2, 3);
        else printError("Что-то пошло не так во время вычислений...");
    }
    
    private void increasingFunc() {
        if (c > epsilon) intervalUpTo(0, 3);
        else if (Math.abs(c) < epsilon) cubic_roots.put(c, 3);
        else if (c < -epsilon) intervalFrom(0, 3);
    }
    
    // (-inf, x1)
    private void intervalUpTo(double x, int multiplicity) {
        double fa;
        for (int i = 1; i < STOP; i++) {
            fa = f(x - i * STEP);
            if (fa < 0)         { cubic_roots.put(dichotomyMethod(x - i * STEP, x), multiplicity); break; }
            if (i + 1 == STOP)  { printError("Невозможно взять больший интервал!"); return; }
        }
    }
    
    // (x2, +inf)
    private void intervalFrom(double x, int multiplicity) {
        double fb;
        for (int i = 1; i < STOP; i++) {
            fb = f(x + i * STEP);
            if (fb > 0)         { cubic_roots.put(dichotomyMethod(x, x + i * STEP), multiplicity); break; }
            if (i + 1 == STOP)  { printError("Невозможно взять больший интервал!"); return; }
        }
    }
    
    public void solve() {
        int roots = getRootsAmount(countDiscriminant());
        if (roots == 3) threeRoots();
        else if (roots == 2) twoRoots();
        else if (roots == 1) oneRoot();
        else increasingFunc();
        printRoots();
    }
    
    private void printRoots() {
        AtomicInteger count = new AtomicInteger(1); // используется для того, чтобы обозначить x1, x2 и т.д.
        cubic_roots.forEach((key, value) -> System.out.println("x" + count.getAndIncrement() + " = " + String.format("%.5f", key) + "\tкратность " + value));
    }
    
    private void printError(String message) {
        System.err.println(message);
    }
}