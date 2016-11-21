package me.sunyfusion.bdsp.io;

import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * Created by Robert Wieland on 3/28/16.
 * This class will read from the buildApp.txt and will return info
 * from that text file.
 */

public class ReadFromInput {
    private String Type = null;    // To hold the type of category from the buildApp.txt when read.
    private Scanner infile;
    private Scanner in;
    private String Line;
    private int answer = 0;        // To hold the answer to categories such as camera. 1 = yes, 0 = no

    public ReadFromInput(Scanner sq) {
        infile = sq; // passes the scanned File
    }

    public void getNextLine() {
        do {
            Line = infile.nextLine();
        } while (Line.equals("") || Line.startsWith("//"));
        in = new Scanner(Line); // scans line
    }

    public void ReadLineCollectInfo() throws FileNotFoundException {
        // Resets variables to read the next line fresh.
        Type = null;
        answer = 0;
        if (in.hasNext()) {
            Type = in.next();
            if (in.hasNextInt()) {      // Checks for an answer, 1 = include, 0 = do not {
                answer = in.nextInt();
            }
        }
    }

    public String getType() {
        return Type;
    }

    public boolean enabled() {
        return answer == 1;
    }

    public String getArg(int arg) {
        String[] ls = Line.split(" ");
        if(arg < ls.length) {
            return ls[arg];
        }
        else return "";
    }
    public String[] getCurrentLine() {
        String[] ls = Line.split(" ");
        return ls;
    }
}
