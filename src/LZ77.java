import java.io.*;
import java.util.ArrayList;

public class LZ77 {
    ArrayList<Character> window;
    StringBuffer buffer;
    int b, w;

    public LZ77(int b, int w) {
        buffer = new StringBuffer();
        window = new ArrayList<>();
        this.b = b;
        this.w = w;
    }
    public void compress(File file) {
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader, w);  //Creation of BufferedReader object
            BufferedWriter writer = new BufferedWriter(new FileWriter("result"+".lz77"));

            String tempMatch;
            String cMatch = "";
            int c;
            int index=0;
            int currentWindowSize = 0;
            while ((c = br.read()) != -1)         //Read char by Char
            {
                window.add((char) c);
                currentWindowSize++;
                tempMatch="";
                if (currentWindowSize == w) {
                    for (int win = 0; win < currentWindowSize; win++) {
                        tempMatch += window.get(win);
                        index = buffer.indexOf(tempMatch);
                        if (index!=-1) {
                            if (tempMatch.length() > cMatch.length()) {
                                cMatch = tempMatch;
                            }
                        } else {
                            break;
                        }
                    }
                    if (cMatch.length() + 1 <= b - buffer.length()&&buffer.length()!=0) {
                        buffer.append(cMatch);
                        for (int j = 0; j < cMatch.length(); j++) {
                            window.remove(0);
                            currentWindowSize--;
                        }

                    } else if ((cMatch.length() + 1) > (b - buffer.length()) && buffer.length() != 0) {
                        int x = (buffer.length() + cMatch.length()) - b;
                        for (int k = 0; k < x; k++) {
                            buffer.deleteCharAt(0);
                        }
                        buffer.append(cMatch);
                        for (int j = 0; j < cMatch.length(); j++) {
                            window.remove(0);
                            currentWindowSize--;
                        }

                    } else if (buffer.length() == b) {
                        for (int k = 0; k < cMatch.length(); k++) {
                            buffer.deleteCharAt(0);
                        }
                        buffer.append(cMatch);
                        for (int j = 0; j < cMatch.length(); j++) {
                            window.remove(0);
                            currentWindowSize--;
                        }

                    }
                    if (buffer.length() == 0) {
                        buffer.append(cMatch);
                        writer.write("0,"+"0"+cMatch);
                        window.remove(0);
                        currentWindowSize--;
                    }
                    else {
                        String codedString = buffer.indexOf(cMatch)+cMatch.length()+cMatch.substring(cMatch.length()-1);

                        writer.write(codedString);
                        cMatch="";
                    }

//                    String raw = cMatch;
//                    if (codedString.length()<raw.length()){
//
//                    }
//                    else writer.write(raw);
//                    cMatch = "";
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
