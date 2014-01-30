package process;

import java.util.ArrayList;

import org.apache.commons.exec.LogOutputStream;

public class LogStream extends LogOutputStream 
{
    private final ArrayList<String> lines = new ArrayList<String>();
    @Override protected void processLine(String line, int level) 
    {
        lines.add(line);
    }   
    public ArrayList<String> getLines() 
    {
        return lines;
    }
}