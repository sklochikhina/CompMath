public class Main {
    public static void main(String[] args) {
        System.out.println("\nЭта программа решает кубическое уравнения вида x^3 + ax^2 + bx + c = 0\n");
        
        CubicEquationSolver equation = new CubicEquationSolver(0.00001, 2, -1, -2);
        equation.solve();
    }
}