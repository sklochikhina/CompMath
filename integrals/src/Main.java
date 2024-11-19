import java.lang.Math;

public class Main {
    private static final int BEGIN = 5;
    private static final int END = 7;

    public static double calculateIntegral(double x) {
        // Вычисляем f(x) = e^x * cos(x)
        return Math.exp(x) * Math.cos(x);
    }

    public static double trap(double a, double b) {
        return ((b - a) / 2) * (calculateIntegral(a) + calculateIntegral(b));
    }

    public static double parab(double a, double b) {
        return ((b - a) / 6) * (calculateIntegral(a) + 4 * calculateIntegral((a+b)/2)+ calculateIntegral(b));
    }

    public static double p1(double a, double b) {
        return ((b-a) / 8) * (calculateIntegral(a) + 3 * calculateIntegral((2*a + b)/3) + 3 * calculateIntegral((a+ 2 * b)/3) + calculateIntegral(b));
    }

    public static double func(double a, double b){
        return ((Math.exp(b) * Math.cos(b) + Math.exp(b) * Math.sin(b)) - (Math.exp(a) * Math.cos(a) + Math.exp(a) * Math.sin(a))) / 2;
    }

    public static double runge(double s1, double s2, double s3){
        return Math.log((s1 - s2) / (s2 - s3)) / Math.log(2);
    }

    public static double integralParab(double result_parab, double a, double b, double N){
        double h = (b - a) / N;
        double begin = BEGIN; // начало локального отрезка
        double end = begin + h; // конец локального отрезка
        for (int i = 0; i < N; i++){
            result_parab += parab(begin, end);
            begin = end;
            end += h;
        }
        return result_parab;
    }

    public static double integralTrap(double result_trap, double a, double b, double N){
        double h = (b - a) / N;
        double begin = BEGIN;   // начало локального отрезка
        double end = begin + h; // конец локального отрезка
        for (int i = 0; i < N; i++){
            result_trap += trap(begin, end);
            begin = end;
            end += h;
        }
        return result_trap;
    }

    public static double integralP1(double result_p1, double a, double b, double N){
        double h = (b - a) / N;
        double begin = BEGIN;   // начало локального отрезка
        double end = begin + h; // конец локального отрезка
        for (int i = 0; i < N; i++){
            result_p1 += p1(begin, end);
            begin = end;
            end += h;
        }
        return result_p1;
    }

    public static void main(String[] args) {
        double a = BEGIN; // Начало отрезка
        double b = END; // Конец отрезка

        int N = 80;

        double result_trap = 0;
        double result_parab = 0;
        double result_1 = 0;

        result_parab = integralParab(result_parab, a, b, N);
        result_trap = integralTrap(result_trap, a, b, N);
        result_1 = integralP1(result_1, a, b, N);

        double result_trap2 = 0;
        double result_parab2 = 0;
        double result_1_2 = 0;

        result_parab2 = integralParab(result_parab2, a, b, 2 * N);
        result_trap2 = integralTrap(result_trap2, a, b, 2 * N);
        result_1_2 = integralP1(result_1_2, a, b, 2 * N);

        double result_trap4 = 0;
        double result_parab4 = 0;
        double result_1_4 = 0;

        result_parab4 = integralParab(result_parab4, a, b, 4 * N);
        result_trap4 = integralTrap(result_trap4, a, b, 4 * N);
        result_1_4 = integralP1(result_1_4, a, b, 4 * N);

        // ----- обычное взятие интеграла ----- //
        double integral = func(a, b);
        System.out.printf("Вычисленное значение интеграла: %.7f%n", integral);
        System.out.println("\n");

        // ----- формула трапеций ----- //
        System.out.printf("Результат интеграла по формуле трапеций: %.7f%n", result_trap);
        System.out.printf("Разность с точным интегралом интеграла по формуле трапеций: %.7f%n", Math.abs(result_trap- integral));
        System.out.printf("Порядок точности по формуле трапеций: %.7f%n", runge(result_trap, result_trap2, result_trap4));
        System.out.println("\n");

        // ----- формула парабол ----- //
        System.out.printf("Результат интеграла по формуле парабол: %.7f%n", result_parab);
        System.out.printf("Разность с точным интегралом интеграла по формуле парабол: %.7f%n", Math.abs(result_parab - integral));
        System.out.printf("Порядок точности по формуле парабол: %.7f%n", runge(result_parab, result_parab2, result_parab4));
        System.out.println("\n");

        // ----- формула из пункта 1 ----- //
        System.out.printf("Результат интеграла по формуле из пункта 1: %.7f%n", result_1);
        System.out.printf("Разность с точным интегралом интеграла по формуле из пункта 1: %.7f%n", Math.abs(result_1 - integral));
        System.out.printf("Порядок точности по формуле из пункта 1: %.7f%n", runge(result_1, result_1_2, result_1_4));
        System.out.println("\n");

        // ----- сравнение полученных значений ----- //
        System.out.println("Сравнение значений интегралов между собой: ");
        System.out.printf("|I_par - I_trap| = %.7f%n", Math.abs(result_parab - result_trap));
        System.out.printf("|I_par - I_p1| = %.7f%n", Math.abs(result_1 - result_parab));
        System.out.printf("|I_p1 - I_trap| = %.7f%n", Math.abs(result_1 - result_trap));
    }
}