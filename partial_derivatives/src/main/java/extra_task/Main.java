package extra_task;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        LinearAdvectionSolver solver = new LinearAdvectionSolver(0.1, 0.4, 5);
        solver.solveCauchyProblem();
    }
}