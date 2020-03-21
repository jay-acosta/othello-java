import java.util.Arrays;

public class Test {
    public static void main(String[] args) {

        int n = 1000;
        int mod = 100;
        if(args.length == 2) {

            try {
                n = Integer.parseInt(args[0]);
                mod = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("Something went wrong with the formatting");
                System.out.println("Going back to default settings");
            }
        }

        System.out.println(n + ", " + mod);
    }
}
