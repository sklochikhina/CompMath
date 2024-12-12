import javax.swing.*;
import java.util.ArrayList;

import static java.lang.Math.exp;

public class EquationSolver {
    private final double a;
    private final double b;
    private final double h;

    private final double h1;
    private final double h2;
    private final double h3;

    private final int size_h1;
    private final int size_h2;
    private final int size_h3;

    private final ArrayList<Double> x;      // узлы основной разностной сетки с шагом h

    private final ArrayList<Double> y_ex;   // yex(x) = e^sin_x - точное решение задачи
    private final ArrayList<Double> y_h1;   // yh1(x) = yh1(x - h1) * (1 + h1 * cos_x)
    private final ArrayList<Double> y_h2;   // yh2(x) = yh2(x - h2) * (1 + h2 * cos_x)
    private final ArrayList<Double> y_h3;   // yh3(x) = yh3(x - h3) * (1 + h3 * cos_x)

    EquationSolver(double a, double b, double h) {
        this.a = a;
        this.b = b;
        this.h = h;

        h1 = h;
        h2 = h / 3.0;
        h3 = h / 9.0;

        size_h1 = count_size(h1);
        size_h2 = count_size(h2);
        size_h3 = count_size(h3);

        // y's
        y_ex = new ArrayList<>(size_h1);
        y_h1 = new ArrayList<>(size_h1);
        y_h2 = new ArrayList<>(size_h2);
        y_h3 = new ArrayList<>(size_h3);

        double first = exp(Math.sin(a));

        fill_arr(y_ex, h, size_h1);
        y_h1.add(first);
        y_h2.add(first);
        y_h3.add(first);

        // x_j array
        x = new ArrayList<>(size_h1);
        fill_x();
    }

    private int count_size(double step) {
        return (int) Math.round((b - a) / step + 0.5);
    }

    private void fill_arr(ArrayList<Double> arr, double step, int size) {
        for (int i = 0; i < size; i++)
            arr.add(exp(Math.sin(a + i * step)));
    }

    private void fill_x() {
        for (int i = 0; i < size_h1; i++)
            x.add(a + i * h);
    }

    private double log3(double x) {
        return Math.log(x) / Math.log(3);
    }

    public void solve_diff_scheme_1st_order() {
        for (int i = 1; i < size_h1; i++)
            y_h1.add(y_h1.get(i - 1) * (1 + h1 * Math.cos(a + (i - 1) * h1)));

        for (int i = 1; i < size_h2; i++)
            y_h2.add(y_h2.get(i - 1) * (1 + h2 * Math.cos(a + (i - 1) * h2)));

        for (int i = 1; i < size_h3; i++)
            y_h3.add(y_h3.get(i - 1) * (1 + h3 * Math.cos(a + (i - 1) * h3)));

        count_delta_p();
    }

    public void solve_symmetrical_diff_scheme_2nd_order() {
        countY(y_h1, h1, size_h1);
        countY(y_h2, h2, size_h2);
        countY(y_h3, h3, size_h3);
        count_delta_p();
    }

    public void countY(ArrayList<Double> y, double step, int size) {
        for (int i = 1; i < size; i++) {
            double cos_j   = Math.cos(a + (i - 1) * step);
            double cos_j_1 = Math.cos(a + i * step);
            y.add(y.get(i - 1) * (2 + step * cos_j) / (2 - step * cos_j_1));
        }
    }

    public void solve_compact_diff_scheme_4nd_order() {
        y_h1.add(countSecondElem(y_h1.getFirst(), h1));
        y_h2.add(countSecondElem(y_h2.getFirst(), h2));
        y_h3.add(countSecondElem(y_h3.getFirst(), h3));

        countRest(y_h1, h1, size_h1);
        countRest(y_h2, h2, size_h2);
        countRest(y_h3, h3, size_h3);

        count_delta_p();
    }

    private double countSecondElem(double y_0, double step) {
        double cos_0 = Math.cos(a);
        double cos_1 = Math.cos(a + step);
        double cos_2 = Math.cos(a + 2 * step);
        return y_0 * ((12 + 5 * step * cos_0) / (step * cos_2) - (3 + step * cos_0) / (3 - step * cos_2)) /
                     (4 * step * cos_1 / (3 - step * cos_2) + (12 - 8 * step * cos_1) / (step * cos_2));
    }

    private void countRest(ArrayList<Double> y, double step, int size) {
        for (int i = 2; i < size; i++)
            y.add((y.get(i - 1) * 4 * step * Math.cos(a + (i - 1) * step) + y.get(i - 2) * (3 + step * Math.cos(a + (i - 2) * step))) /
                    (3 - step * Math.cos(a + i * step)));
    }

    private void count_delta_p() {
        double[] delta = new double[size_h1]; // |yex(xj) − yh1(xj)|
        double[] p     = new double[size_h1]; // log3(|yh1(xj) − yh2(xj)| / |yh2(xj) − yh3(xj)|)

        for (int i = 0; i < delta.length; i++)
            delta[i] = Math.abs(y_ex.get(i) - y_h1.get(i));

        for (int i = 0, j = 0, k = 0; i < p.length && j < y_h2.size() && k < y_h3.size(); i++, j += 3, k += 9) {
            double numerator   = Math.abs(y_h1.get(i) - y_h2.get(j));
            double denominator = Math.abs(y_h2.get(j) - y_h3.get(k));
            p[i] = log3(numerator / denominator);
        }

        printResult(delta, p);
    }

    private void printResult(double[] delta, double[] p) {
        JFrame frame = new JFrame();
        frame.setTitle("Result Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] columnNames = {"j", "x_j", "y_ex(x_j)", "delta_j", "p_j"};

        JTable table = new JTable(new Object[size_h1 + 1][5], columnNames);

        for (int i = 0; i < y_ex.size(); i++) {
            table.setValueAt(i,           i, 0);
            table.setValueAt(x.get(i),    i, 1);
            table.setValueAt(y_ex.get(i), i, 2);
            table.setValueAt(delta[i],    i, 3);
            table.setValueAt(p[i],        i, 4);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
