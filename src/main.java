import java.io.File;
import java.io.FileNotFoundException;

public class main {
    public static void main(String[] args) throws FileNotFoundException {
        LZ77 lz = new LZ77(4096,16);
//        File fileToCompress = new File("C:\\Users\\wojci\\IdeaProjects\\LZ77\\test.txt");
//       lz.Compress(fileToCompress);
        File fileToDecompress = new File("C:\\Users\\wojci\\IdeaProjects\\LZ77\\result.lz77");
        lz.Decompress(fileToDecompress);
    }
}
