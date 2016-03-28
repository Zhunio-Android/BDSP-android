package me.sunyfusion.fuzion;

import java.io.*;
import java.io.File;
import java.util.Scanner;


/**
 * Created by Robert Wieland on 3/28/16.
 * This class will read from the buildApp.txt and will return info
 * from that text file.
 */

public class ReadFromInput
{
    private String Type = null;    // To hold the type of category from the buildApp.txt when read.
    private String Name = null;    // To hold the unique category name from the buildApp.txt when read.
    private int answer = 0;        // To hold the answer to categories such as camera. 1 = yes, 0 = no

    public void ReadLineCollectInfo() throws FileNotFoundException
    {
        File input = new File("buildApp.txt");
        Scanner infile = new Scanner(input);
        String Line;

        if (infile.hasNextLine())
        {
            Line = infile.nextLine();   // gets the line
            Scanner line = new Scanner(Line); // sets Scanner to that line
            if(line.hasNext())
            {
                Type = line.next(); // Gets the category and assigns to Type
                if(line.hasNextInt())
                {
                    answer = line.nextInt();
                }

                else if(line.hasNext())
                    Name = line.next(); // Gets unique name of category

                else    // Final category (endFile)
                {
                    Name = null;
                    answer = 0;
                }

            }

        }

        // Resets variables to read the next line fresh.
        Type = null;
        Name = null;
        answer = 0;

        //Do I need to line.close() ?

    }

    public String getCategoryFromFile()
    {
        return Type;
    }

    public int getAnswer()
    {
        return answer;
    }

    public String getUnigueName()
    {
        return Name;
    }

}
