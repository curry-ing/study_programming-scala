package objectsystem.variance;

public class JavaArrays {
    public static void main(String[] args) {
        Integer[] array1 = new Integer[] { new Integer(1), new Integer(2), new Integer(3) };
        Number[] array2 = array1;

        array2[2] = new Double(3.14);
    }
}
