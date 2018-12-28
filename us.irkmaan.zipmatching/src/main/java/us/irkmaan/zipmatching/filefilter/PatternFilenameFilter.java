package us.irkmaan.zipmatching.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class PatternFilenameFilter implements FilenameFilter 
{
	private Pattern _pattern;
	
	public PatternFilenameFilter( String patternString )
	{
		_pattern = Pattern.compile( patternString );
	}

	@Override
	public boolean accept(File dir, String name) 
	{
		return _pattern.matcher( name ).matches();
	}

}
