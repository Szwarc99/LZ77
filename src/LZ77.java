import java.io.*;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LZ77 {
    ArrayList<Character> window;
    ArrayList<Byte> decompressWindow;
    StringBuffer buffer;
    String codedString;
    int b, w;

    public LZ77(int b, int w) {
        buffer = new StringBuffer();
        window = new ArrayList<>();
        decompressWindow = new ArrayList<>();
        this.b = b;
        this.w = w;
    }

    public void Compress(File file) throws FileNotFoundException {
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader, w);  //Creation of BufferedReader object
            BufferedWriter writer = new BufferedWriter(new FileWriter("result" + ".lz77"));

            String tempMatch;
            String cMatch = "";
            int c;
            int index = 0;
            int currentWindowSize = 0;
            while ((c = br.read()) != -1)         //Read char by Char
            {
                window.add((char) c);
                currentWindowSize++;
                tempMatch = "";
                if (currentWindowSize == w) {
                    for (int win = 0; win < currentWindowSize; win++) {
                        tempMatch += window.get(win);
                        index = buffer.indexOf(tempMatch);
                        if (index != -1) {
                            if (tempMatch.length() > cMatch.length()) {
                                cMatch = tempMatch;
                            }
                        } else {
                            break;
                        }
                    }

                    //new character
                    if (tempMatch.length() == 1 && index == -1) {
                        cMatch = tempMatch;
                        if (buffer.length() == b) {
                            buffer.deleteCharAt(0);
                        }
                        buffer.append(cMatch);
                        writer.write("~" + "0" + "~"+ "0"+ "~" + cMatch);
                        window.remove(0);
                        currentWindowSize--;
                        cMatch = "";
                    }
                    //adding new char to buffer, if there is space
                    else if (cMatch.length() + 1 <= b - buffer.length()) {
                        buffer.append(tempMatch);
                        for (int j = 0; j < tempMatch.length(); j++) {
                            window.remove(0);
                            currentWindowSize--;
                        }
                        codedString = "~" + buffer.indexOf(cMatch) + "~" + cMatch.length() + "~" + tempMatch.substring(tempMatch.length() - 1);
                        writer.write(codedString);
                        cMatch = "";
                    }
                    //adding new char to buffer, if there is not enough space
                    else if (cMatch.length() + 1 > b - buffer.length()) {
                        int x = (buffer.length() + cMatch.length()) - b;
                        for (int k = 0; k < x + 1; k++) {
                            buffer.deleteCharAt(0);
                        }
                        buffer.append(tempMatch);
                        for (int j = 0; j < tempMatch.length(); j++) {
                            window.remove(0);
                            currentWindowSize--;
                        }
                        codedString = "~" + buffer.indexOf(cMatch) + "~" + cMatch.length() + "~" + tempMatch.substring(tempMatch.length() - 1);
                        writer.write(codedString);
                        cMatch = "";
                    }
                }
            }
            writer.close();
            br.close();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Decompress(File file) throws FileNotFoundException {
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader, w);  //Creation of BufferedReader object
            BufferedWriter writer = new BufferedWriter(new FileWriter("decompressed" + ".txt"));
            StreamTokenizer st = new StreamTokenizer(br);

            st.ordinaryChar((int)' ');
            st.ordinaryChar((int)'.');
            st.ordinaryChar((int)'-');
            st.ordinaryChar((int)'\n');
            st.wordChars((int)'\n', (int)'\n');
            st.wordChars((int)' ', (int)'}');

            int offset = 0;
            int length = 0;
            String output="";
            while (st.nextToken() != StreamTokenizer.TT_EOF)         //Read char by Char
            {
                switch(st.ttype){
                    case StreamTokenizer.TT_NUMBER:
                        if (st.nval==0){
                            break;
                        }
                        else{
                            offset= (int) st.nval;
                            st.nextToken();
                            length= (int) st.nval;
                            output = buffer.substring(offset, length+1);
                            if (buffer.length()+output.length()>b){
                                for (int i=0;i<buffer.length()+output.length()-b;i++){
                                    buffer.deleteCharAt(0);
                                }
                            }
                            buffer.append(output);
                            st.nextToken();
                            if (!output.substring(output.length() - 1).equals(st.sval)){
                                System.out.println("something went wrong");
                            }
                            break;
                        }
                    case StreamTokenizer.TT_WORD:
                        System.out.println("something went wrong with words");
                    default:

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void trimSearchBuffer() {
        if (buffer.length() > b) {
           buffer =
                    buffer.delete(0,  buffer.length() - b);
        }
    }

    public void unCompress(String infile) throws IOException {
        BufferedReader mIn = new BufferedReader(new FileReader(infile+".lz77"));
        buffer = new StringBuffer(b);

        StreamTokenizer st = new StreamTokenizer(mIn);

        st.ordinaryChar((int)' ');
        st.ordinaryChar((int)'.');
        st.ordinaryChar((int)'-');
        st.ordinaryChar((int)'\n');
        st.wordChars((int)'\n', (int)'\n');
        st.wordChars((int)' ', (int)'}');

        int offset, length;
        while (st.nextToken() != StreamTokenizer.TT_EOF) {
            switch (st.ttype) {
                case StreamTokenizer.TT_WORD:
                    if (buffer.length()+st.sval.length()>b){
                        for (int i=0; i< buffer.length()+st.sval.length()-b;i++){
                            buffer.deleteCharAt(0);
                        }
                    }
                    buffer.append(st.sval);
                    System.out.print(st.sval);
                    // Adjust search buffer size if necessary
                    break;
                case StreamTokenizer.TT_NUMBER:
                    offset = (int)st.nval; // set the offset
                    st.nextToken(); // get the separator (hopefully)
                    if (st.ttype == StreamTokenizer.TT_WORD) {

                        if (buffer.length()+st.sval.length()>b){
                            for (int i=0; i< buffer.length()+st.sval.length()-b;i++){
                                buffer.deleteCharAt(0);
                            }
                        }
                        // we got a word instead of the separator,
                        // therefore the first number read was actually part of a word
                        buffer.append(offset+st.sval);
                        System.out.print(offset+st.sval);
                        break; // break out of the switch
                    }
                    // if we got this far then we must be reading a
                    // substitution pointer
                    st.nextToken(); // get the length
                    length = (int)st.nval;
                    // output substring from search buffer
                    String output = buffer.substring(offset, offset+length);
                    System.out.print(output);
                    if (buffer.length()+output.length()>b){
                        for (int i=0; i< buffer.length()+output.length()-b;i++){
                            buffer.deleteCharAt(0);
                        }
                    }
                    buffer.append(output);
                    // Adjust search buffer size if necessary
                    break;
                default:
                    // consume a '~'
            }
        }
        mIn.close();
    }
}