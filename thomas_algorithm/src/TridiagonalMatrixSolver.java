import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class TridiagonalMatrixSolver {
    private final int N;
    
    private double[] a;
    private double[] b;
    private double[] c;
    
    private double[] x;
    private double[] f;
    
    private double[] alpha;
    private double[] beta;
    
    public double epsilon;
    public double gamma;
    
    TridiagonalMatrixSolver(int N) {
        this.N = N;
        initArrays();
    }
    
    private void initArrays() {
        //A = new double[N][N];
        a = new double[N - 1];
        b = new double[N - 1];
        c = new double[N];
        
        x = new double[N];
        f = new double[N];
        
        alpha = new double[N - 1];
        beta = new double[N];
    }
    
    /*private void fillMatrix() {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                if      (i == j)     A[i][j] = c[i];
                else if (i + 1 == j) A[i][j] = -b[i];
                else if (j + 1 == i) A[i][j] = -a[i];
                else                 A[i][j] = 0;
            }
    }*/
    
    private void readArray(Scanner scanner, String str, double[] array, int size) {
        System.out.println("Введите значения " + str + ":");
        for (int i = 0; i < size; i++)
            array[i] = scanner.nextDouble();
    }
    
    private void readInput() {
        Scanner scanner = new Scanner(System.in);
        
        readArray(scanner, "a", a, N - 1);
        readArray(scanner, "b", b, N - 1);
        readArray(scanner, "c", c, N);
        readArray(scanner, "f", f, N);
        
        scanner.close();
    }
    
    // Прямая прогонка
    private void directRun() {
        alpha[0] = -b[0] / c[0];
        beta[0] = f[0] / c[0];
        
        for (int i = 1; i < N; i++) {
            double y_i = c[i] + a[i - 1] * alpha[i - 1];
            if (i != N - 1)
                alpha[i] = -b[i] / y_i;
            beta[i] = (f[i] - a[i - 1] * beta[i - 1]) / y_i;
        }
    }
    
    // Обратная прогонка
    private void reverseRun() {
        x[N - 1] = beta[N - 1];
        for (int i = N - 2; i >= 0; i--)
            x[i] = alpha[i] * x[i + 1] + beta[i];
    }
    
    // Вывод результата
    private void printResult() {
        AtomicInteger count = new AtomicInteger(1); // используется для того, чтобы обозначить x1, x2 и т.д.
        System.out.println("Результат:");
        for (double xi : x) System.out.println("x" + count.getAndIncrement() + " = " + String.format("%.5f", xi));
    }
    
    public void solve(int mode) {
        switch (mode) {
            case 0: readInput(); break;
            case 1: test1Input(); break;
            case 2: test2Input(); break;
            case 3: test3Input(); break;
        }
        
        directRun();
        reverseRun();
        
        Arrays.sort(x);
        printResult();
    }
    
    private void test1Input() {
        Arrays.fill(a, -1);
        Arrays.fill(b, -1);
        Arrays.fill(c, 2);
        Arrays.fill(f, 2);
    }
    
    private void test2Input() {
        Arrays.fill(a, -1);
        Arrays.fill(b, -1);
        Arrays.fill(c, 2);
        Arrays.fill(f, 2 + epsilon);
    }
    
    private void test3Input() {
        Arrays.fill(a, -1);
        Arrays.fill(b, -1);
        for (int i = 0; i < N; i++) c[i] = countC(i);
        for (int i = 0; i < N; i++) f[i] = countF(i);
    }
    
    private double countC(int i) {
        return 2 * i + gamma;
    }
    
    private double countF(int i) {
        return 2 * (i + 1) + gamma;
    }
}
