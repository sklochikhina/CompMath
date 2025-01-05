package extra_task;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.Arrays;

public class LinearAdvectionSolver {
    private final double a = -10.0, b = 10.0;   // Границы отрезка
    private final double h;                     // Шаг по пространству
    private final double r;                     // КФЛ параметр
    private final double T;                     // Время окончания счёта
    private final int N;                        // Число пространственных узлов
    private final double tau;                   // Шаг по времени
    private final int timeSteps;                // Число временных шагов

    public LinearAdvectionSolver(double h, double r, double T) {
        this.h = h;
        this.r = r;
        this.T = T;
        N = (int) ((b - a) / h);
        tau = r * h;
        timeSteps = (int) (T / tau);
    }

    private double initialCond(double num) {
        return (num < 0) ? 1.0 : 0.0;
    }

    public void solveCauchyProblem() throws InterruptedException {
        double[] x             = new double[N + 1]; // точки по шагу h
        double[] uExact        = new double[N + 1]; // точное решение
        double[] uGodunov      = new double[N + 1]; // по схеме Годунова
        double[] uImplicit     = new double[N + 1]; // по неявной схеме

        double[] uGodunovNext  = new double[N + 1];
        double[] uImplicitNext = new double[N + 1];

        // Инициализация x и начальных значений
        for (int j = 0; j <= N; j++) {
            x[j] = a + j * h;
            uExact[j] = uGodunov[j] = uImplicit[j] = initialCond(x[j]);
        }

        // Создание окна для отображения графика
        XYSeries exactSeries = new XYSeries("Точное решение");
        XYSeries godunovSeries = new XYSeries("Схема Годунова");
        XYSeries implicitSeries = new XYSeries("Неявная схема");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(exactSeries);
        dataset.addSeries(godunovSeries);
        dataset.addSeries(implicitSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Numerical Solutions of Linear Advection Equation",
                "x",
                "u(t, x)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Настройка стилей отображения данных
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Точное решение: линия, без точек
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);

        // Схема Годунова: точки + линия
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, true);

        // Неявная схема: точки + линия
        renderer.setSeriesLinesVisible(2, true);
        renderer.setSeriesShapesVisible(2, true);

        chart.getXYPlot().setRenderer(renderer); // Применение настроек рендера

        JFrame frame = new JFrame("Linear Advection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);

        // Шаг по времени
        for (int n = 0; n < timeSteps; n++) {
            // Точное решение
            for (int i = 0; i <= N; i++) {
                double shift = x[i] - n * tau;
                uExact[i] = initialCond(shift);
            }

            // Обновление схемы Годунова
            uGodunovNext[0] = initialCond(a);
            uGodunovNext[N] = initialCond(b);
            for (int j = 1; j < N; j++)
                uGodunovNext[j] = uGodunov[j] - r * (uGodunov[j] - uGodunov[j - 1]);

            // Обновление неявной схемы
            double[] aCoeff = new double[N - 2]; // Нижняя диагональ (коэффициенты перед u_{j-1}^{n+1})
            double[] bCoeff = new double[N - 1]; // Главная диагональ (коэффициенты перед u_j^{n+1})
            double[] cCoeff = new double[N - 2]; // Верхняя диагональ (коэффициенты перед u_{j+1}^{n+1})

            double[] dCoeff = new double[N - 1]; // Правая часть u_j^n

            Arrays.fill(aCoeff, -r / 2.0);
            Arrays.fill(bCoeff, 1.0);
            Arrays.fill(cCoeff, r / 2.0);

            dCoeff[0]     = uImplicit[1]     + r / 2.0 * initialCond(a);
            dCoeff[N - 2] = uImplicit[N - 1] + r / 2.0 * initialCond(b);
            if (N - 2 - 1 >= 0)
                System.arraycopy(uImplicit, 2, dCoeff, 1, N - 2 - 1);

            // Решение трёхдиагональной системы с использованием алгоритма Томаса (прогонки)
            double[] alpha = new double[N - 2];
            double[] beta = new double[N - 1];

            alpha[0] = -cCoeff[0] / bCoeff[0];
            beta[0] = dCoeff[0] / bCoeff[0];

            // Прямой ход
            for (int i = 1; i < N - 1; i++) {
                double y_i = bCoeff[i] + aCoeff[i - 1] * alpha[i - 1];
                if (i != N - 2)
                    alpha[i] = -cCoeff[i] / y_i;
                beta[i] = (dCoeff[i] - aCoeff[i - 1] * beta[i - 1]) / y_i;
            }

            uImplicitNext[0] = initialCond(a);
            uImplicitNext[N] = initialCond(b);

            uImplicitNext[N - 1] = beta[N - 2];

            // Обратный ход
            for (int i = N - 2; i >= 1; i--)
                uImplicitNext[i] = alpha[i - 1] * uImplicitNext[i + 1] + beta[i - 1];

            // Обновление решения
            System.arraycopy(uGodunovNext, 0, uGodunov, 0, N + 1);
            System.arraycopy(uImplicitNext, 0, uImplicit, 0, N + 1);

            // Обновление графиков
            godunovSeries.clear();
            exactSeries.clear();
            implicitSeries.clear();
            for (int i = 0; i < x.length; i++) {
                exactSeries.add(x[i], uExact[i]);
                godunovSeries.add(x[i], uGodunov[i]);
                implicitSeries.add(x[i], uImplicit[i]);
            }

            // Задержка для визуализации
            Thread.sleep(100); // 100 мс
        }
    }
}