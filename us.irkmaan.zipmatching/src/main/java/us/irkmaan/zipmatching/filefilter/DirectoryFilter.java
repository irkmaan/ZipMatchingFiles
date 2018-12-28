package us.irkmaan.zipmatching.filefilter;

import java.io.File;
import java.io.FileFilter;

public class DirectoryFilter implements FileFilter {

	@Override
	public boolean accept(File arg0) 
	{
		return arg0.isDirectory();
	}

}
