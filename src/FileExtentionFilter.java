package BattleTanks;
import java.io.*;
class FileExtentionFilter implements FilenameFilter
{
	String ext;
	public FileExtentionFilter(String ext)
	{
		this.ext="."+ext;
	}
	public boolean accept(File dir, String name)
	{
		
		return name.endsWith(ext);
	}
}