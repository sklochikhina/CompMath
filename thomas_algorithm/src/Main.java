public class Main {
    public static void main(String[] args) {
        System.out.println("Эта программа решает систему СЛАУ методом прогонки (методом Томаса).");
        
        TridiagonalMatrixSolver solver = new TridiagonalMatrixSolver(5);
        
        solver.setEpsilon(2);
        solver.setGamma(3);
        
        solver.solve(1);
    }
}

/* Modes:
* 0 - user input
* 1 - test 1
* 2 - test 2 (+ epsilon)
* 3 - test 3 (+ gamma)
* */