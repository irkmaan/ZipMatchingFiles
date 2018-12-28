package us.irkmaan.zipmatching;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import us.irkmaan.zipmatching.filefilter.DirectoryFilter;

public class FileZipper 
{
	private static final DirectoryFilter DIR_FILTER = new DirectoryFilter();

	public static void zipMatches( File currDir, FileFilter fnf, String zipFileName, int compLevel, int minToZip )
	{
		zipMatches( currDir, fnf, zipFileName, null, compLevel, minToZip );
	}
	
	public static void zipMatches( File currDir, FileFilter fnf, String zipFileName, Pattern dirPattern, int compLevel, int minToZip )
	{
		System.out.println( currDir.getAbsolutePath() );
		
		File[] directories = currDir.listFiles( DIR_FILTER );
		
		if ( directories == null )
		{
			System.err.println( "Null result for listing directory " + currDir.getAbsolutePath() );
			return;
		}
		
		for ( int d = 0; d < directories.length; d++ )
		{
			if ( dirPattern != null && !dirPattern.matcher( directories[d].getName() ).matches() )
			{
				continue;
			}
						
			zipMatches( directories[d], fnf, zipFileName, dirPattern, compLevel, minToZip );
		}
		
		File[] zippables;
		
		if ( fnf != null )
		{
			zippables = currDir.listFiles( fnf );
		}
		else
		{
			zippables = currDir.listFiles();
		}
				
		if ( zippables.length >= minToZip )
		{
			doZip( zippables, currDir.getAbsolutePath() + File.separator + zipFileName, compLevel );
		}
	}
	
	private static void doZip( File[] zippables, String zipFilePath, int cLevel )
	{
		File zipFile = new File( zipFilePath );
		
		if ( zipFile.exists() )
		{
			return;
		}
		
		ZipOutputStream zipStream = null;
		
		try
		{
			zipStream = new ZipOutputStream( new FileOutputStream( zipFile ) );
		}
		catch (FileNotFoundException fnfe)
		{
			System.err.println( "Unable to create " + zipFile.getAbsolutePath() + ": " + fnfe.getMessage() );
			return;
		}
		
		if ( cLevel >= 0 && cLevel <= 9 )
		{
			zipStream.setLevel( cLevel );
		}
		
		List<File> successList = new ArrayList<File>();
		
		for ( int f = 0; f < zippables.length; f++ )
		{
			try
			{
				zipStream.putNextEntry( new ZipEntry( zippables[f].getName() ) );
			}
			catch (IOException ioe)
			{
				System.err.println( "Unable to create zip entry for " + zippables[f].getAbsolutePath() + ": " 
			                        + ioe.getMessage() );
				continue;
			}
			
			try
			{
				zipStream.write( FileUtils.readFileToByteArray( zippables[f] ) );
			}
			catch (IOException ioe)
			{
				System.err.println( "Unable to write zip entry for " + zippables[f].getAbsolutePath() + ": " 
			                        + ioe.getMessage() );
				continue;
			}
			
			successList.add( zippables[f] );
		}
		
		try
		{
			zipStream.closeEntry();
			zipStream.close();
		}
		catch (IOException ioe)
		{
			System.err.println( "Unable to close zip file " + zipFile.getAbsolutePath() + ": " + ioe.getMessage() );
			return;
		}
		
		for ( File successFile : successList )
		{
			successFile.delete();
		}
	}
}
