import java.io.File;
import java.io.FileNotFoundException;

public class main {
    public static void main(String[] args) throws FileNotFoundException {
        LZ77 lz = new LZ77(4096,16);
        File file = new File("C:\\Users\\Piotrek\\IdeaProjects\\LZ77\\test.txt");
        lz.compress(file);
    }
}
