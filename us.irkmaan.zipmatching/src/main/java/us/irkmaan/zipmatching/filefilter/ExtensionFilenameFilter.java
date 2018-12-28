package us.irkmaan.zipmatching.filefilter;

import java.io.File;
import java.io.FilenameFilter;

public class ExtensionFilenameFilter implements FilenameFilter 
{
	private String ext;
	
	public ExtensionFilenameFilter( String extension )
	{
		ext = extension;
	}
	
	@Override
	public boolean accept(File dir, String name) 
	{
		return name.endsWith( ext );
	}

}
