package us.irkmaan.zipmatching;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import us.irkmaan.zipmatching.filefilter.ExtensionFilenameFilter;
import us.irkmaan.zipmatching.filefilter.PatternFilenameFilter;

public class Main 
{	
	public static final String OPT_COMP_LEVEL = "c";
	public static final String OPT_MIN_IN_DIR = "m";
	public static final String OPT_BASE_DIR = "b";
	public static final String OPT_FILE_PATTERN = "f";
	public static final String OPT_OUTPUT_FILE = "o";
	public static final String OPT_DIR_PATTERN = "d";
	public static final String OPT_FILE_EXT = "e";
	public static final String OPT_FILE_ALL = "a";

	
	public static final String DEFAULT_COMP_LEVEL = "-1";
	public static final String DEFAULT_MIN_IN_DIR = "2";
	public static final String DEFAULT_BASE_DIR = ".";
	public static final String DEFAULT_FILE_PATTERN = null;
	public static final String DEFAULT_DIR_PATTERN = null;
	public static final String DEFAULT_FILE_EXT = null;
	
	public static void main( String[] args ) 
	{
		Args theArgs = processArgs( args );
		
		File currDir = new File( theArgs.baseDirPath );
		
		if ( !currDir.isDirectory() )
		{
			System.err.println( theArgs.baseDirPath + " is not a directory." );
			System.exit( 2 );
		}
		
		FilenameFilter fnf;
		
		if ( theArgs.filePattern == null )
		{
			if ( theArgs.fileExt == null )
			{
				fnf = null;
			}
			else
			{
				fnf = new ExtensionFilenameFilter( theArgs.fileExt );
			}
		}
		else
		{
			fnf = new PatternFilenameFilter( theArgs.filePattern );
		}
				
		if ( theArgs.dirPattern != null )
		{
			FileZipper.zipMatches( currDir, fnf, theArgs.outputFile, Pattern.compile( theArgs.dirPattern ), theArgs.compLevel, 
					theArgs.minInDir );
		}
		else
		{
			FileZipper.zipMatches( currDir, fnf, theArgs.outputFile, theArgs.compLevel, theArgs.minInDir );
		}
	}
	
	private static Args processArgs( String[] args )
	{
		Options cliOptions = new Options();
		
		cliOptions.addOption( Option.builder( OPT_COMP_LEVEL ).longOpt( "comp-level" ).hasArg().desc( "Zip compression level [0-9]" )
				.build() );
		cliOptions.addOption( Option.builder( OPT_MIN_IN_DIR ).longOpt( "min-in-dir" ).hasArg()
				.desc( "Minimum number of matching files in directory for zipping to occur" ).build() );
		cliOptions.addOption( Option.builder( OPT_BASE_DIR ).longOpt( "base-dir" ).hasArg().desc( "Base directory" ).build() );
		cliOptions.addOption( Option.builder( OPT_FILE_PATTERN ).longOpt( "file-pattern" ).hasArg()
				.desc( "Pattern of names of files to be zipped" ).build() );
		cliOptions.addOption( Option.builder( OPT_FILE_EXT ).longOpt( "file-ext" ).hasArg()
				.desc( "Extension of files to be zipped (including leading period if any)" ).build() );
		cliOptions.addOption( Option.builder( OPT_OUTPUT_FILE ).longOpt( "output-file" ).hasArg().desc( "Output file name" ).required()
				.build() );
		cliOptions.addOption( Option.builder( OPT_DIR_PATTERN ).longOpt( "dir-pattern" ).hasArg()
				.desc( "Pattern of names of directories to be processed" ).build() );
		cliOptions.addOption( Option.builder( OPT_FILE_ALL ).longOpt( "all-files" )
				.desc( "Indicates that all files are to be processed" ).build() );
		
		CommandLineParser cliParser = new DefaultParser();
		CommandLine cliArgs = null;
		
		try 
		{
			cliArgs = cliParser.parse( cliOptions, args );
		} 
		catch (ParseException e) 
		{
			System.err.println( "Unable to parse command line arguments: " + e.getMessage() );
			System.exit(1);
		}
		
		Args theArgs = new Args();
				
		try
		{
			theArgs.compLevel = Integer.parseInt( cliArgs.getOptionValue( OPT_COMP_LEVEL, DEFAULT_COMP_LEVEL ) );
		}
		catch (NumberFormatException nfe)
		{
			System.err.println( "Compression level must be an integer; instead was " + cliArgs.getOptionValue( OPT_COMP_LEVEL ) );
			System.exit(2);
		}
		
		if ( theArgs.compLevel > 9 )
		{
			System.err.println( "Invalid compression level " + theArgs.compLevel + "; must be between 0 and 9" );
			System.exit(4);
		}
		
		try
		{
			theArgs.minInDir = Integer.parseInt( cliArgs.getOptionValue( OPT_MIN_IN_DIR, DEFAULT_MIN_IN_DIR ) );
		}
		catch (NumberFormatException nfe)
		{
			System.err.println( "Minimum matching files in directory must be an integer; instead was " 
						+ cliArgs.getOptionValue( OPT_MIN_IN_DIR ) );
			System.exit(3);
		}
		
		theArgs.filePattern = cliArgs.getOptionValue( OPT_FILE_PATTERN, DEFAULT_FILE_PATTERN );
		theArgs.fileExt = cliArgs.getOptionValue( OPT_FILE_EXT, DEFAULT_FILE_EXT );
		theArgs.allFiles = cliArgs.hasOption( OPT_FILE_ALL );
		
		if ( !theArgs.allFiles && theArgs.filePattern == null && theArgs.fileExt == null )
		{
			System.err.println( "Must specify that all files are to be processed, or either pattern or extension to filter file names by." );
			System.exit(5);
		}
		
		theArgs.baseDirPath = cliArgs.getOptionValue( OPT_BASE_DIR, DEFAULT_BASE_DIR );
		theArgs.dirPattern = cliArgs.getOptionValue( OPT_DIR_PATTERN, DEFAULT_DIR_PATTERN );
		theArgs.outputFile = cliArgs.getOptionValue( OPT_OUTPUT_FILE );  // this is required from the command line
	
		return theArgs;
	}
	
	public static class Args
	{
		public int compLevel;
		public int minInDir;
		public String baseDirPath;
		public String dirPattern;
		public String filePattern;
		public String outputFile;
		public String fileExt;
		public boolean allFiles;
	}
}
