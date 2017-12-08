package de.unistuttgart.ims.drama.main;

import java.io.File;

import com.lexicalscope.jewel.cli.Option;

public interface Options {
	@Option
	File getInput();

	@Option(defaultToNull = true)
	File getOutput();

}