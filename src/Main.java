public class Main {
    public static void main(String[] args) {
        System.out.println("This program solves the equation x^3 + ax^2 + bx + c = 0");
        
        CubicEquationSolver equation = new CubicEquationSolver(0.0001, 3, 0, -4);
        equation.solve();
    }
}
/*
void readInput(double& epsilon, double& a, double& b, double& c) {
    std::cout << "Please, enter the values of epsilon, a, b and c:" << std::endl;

    std::cout << "epsilon = ";
    std::cin >> epsilon;

    std::cout << "\na = ";
    std::cin >> a;

    std::cout << "\nb = ";
    std::cin >> b;

    std::cout << "\nc = ";
    std::cin >> c;
    std::cout << std::endl;
}
* */