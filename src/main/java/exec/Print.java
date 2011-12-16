package exec;

public class Print {

    private Print() {}


    public static void print(Object s) {
	System.out.print(s);
    }


    public static void println(Object s) {
	System.out.println(s);
    }


    public static void printErr(Object s) {
	System.err.println(s);
    }

}
