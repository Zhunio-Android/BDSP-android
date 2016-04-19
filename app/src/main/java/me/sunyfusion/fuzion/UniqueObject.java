package me.sunyfusion.fuzion;

/**
 * Created by Robert Wieland on 4/8/16.
 */
public class UniqueObject
{
    private String Name = null;
    private String Catagory = null;
    private String UserEntry = null;

    public UniqueObject(String cat, String name)
    {
        Catagory = cat;
        Name = name;
    }

    public String getName()
    {
        return Name;
    }

    public String getCatagory()
    {
        return Catagory;
    }

}
