public class Main {
    public static void main(String[] args) {
        System.out.println("This program solves the equation x^3 + ax^2 + bx + c = 0");
        
        CubicEquationSolver equation = new CubicEquationSolver(0.001, 3, 0, -1);
        equation.solve();
    }
}