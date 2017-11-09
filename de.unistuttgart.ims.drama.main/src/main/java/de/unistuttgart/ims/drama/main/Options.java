package de.unistuttgart.ims.drama.main;

import java.io.File;

import com.lexicalscope.jewel.cli.Option;

public interface Options {
	@Option
	File getInput();

	@Option
	File getOutput();

	/**
	 * Storage of the CSV files. Should be a directory.
	 * 
	 * @return A directory
	 */
	@Option(longName = "csvOutput")
	File getCSVOutput();
}