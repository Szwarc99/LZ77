import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        LZ77 lz = new LZ77(4096,16);
        File fileToCompress = new File("test.txt");
        lz.Compress(fileToCompress);
//        File fileToDecompress = new File("result.lz77");
//        lz.Decompress(fileToDecompress);
        lz.unCompress("result");
    }
}
