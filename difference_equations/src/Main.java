public class Main {
    public static void main(String[] args) {
        EquationSolver solver = new EquationSolver(4, 6, 0.1);
        //solver.solve_diff_scheme_1st_order();
        //solver.solve_symmetrical_diff_scheme_2nd_order();
        solver.solve_compact_diff_scheme_4nd_order();
    }
}