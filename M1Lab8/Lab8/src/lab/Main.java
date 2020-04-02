package lab;

public class Main {

    public static void main(String[] args) {
        int[] dimension = {100, 1000, 5000};

        for (int i = 0; i < 3; i++) {
            Sequential.calculate(args, dimension[i]);
            RibbonScheme.calculate(args, dimension[i]);
            FoxMethod.calculate(args, dimension[i]);
            CannonMethod.calculate(args, dimension[i]);
        }
    }
}