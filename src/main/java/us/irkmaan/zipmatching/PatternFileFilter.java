package us.irkmaan.zipmatching;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class PatternFileFilter implements FileFilter 
{
	private Pattern _pattern;
	
	public PatternFileFilter( String patternString )
	{
		_pattern = Pattern.compile( patternString );
	}

	@Override
	public boolean accept(File arg0) 
	{
		return _pattern.matcher( arg0.getName() ).matches() && arg0.isFile();
	}

}
