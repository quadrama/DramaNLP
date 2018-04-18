[![Build Status](https://travis-ci.org/quadrama/DramaNLP.svg?branch=master)](https://travis-ci.org/quadrama/DramaNLP)
[![DOI](https://www.zenodo.org/badge/57984264.svg)](https://www.zenodo.org/badge/latestdoi/57984264)

# DramaNLP
This repository contains a number of UIMA components to process dramatic texts, as well as an executable pipeline. We follow general design ideas implemented in [DKPro Core](https://dkpro.github.io/dkpro-core/). The full pipeline reads in files in several TEI/XML dialects (see below), and applies the most important NLP tools on them, while keeping the structural annotation of the plays intact (and, if necessary, processing different text layers separately).

## Compiling from source

1. Clone the repository: `git clone https://github.com/quadrama/DramaNLP.git`
1. Enter the directory: `cd DramaNLP`
	- If necessary, switch to a branch `git checkout develop/1.0`
1. Download dependencies, compile everything and install it locally: `mvn compile install`
	This produces a lot of output, but at the end, you should see something like `BUILD SUCCESS`
1. To compile a runnable binary, enter the directory: `cd de.unistuttgart.ims.drama.main` and run `mvn package`. This creates a file called `drama.Main.jar` in the directory `target/assembly/`. This file contains the code and all its dependencies.


## Running entire pipeline

As an example, we'll work on the data from the GerDraCor collection (which is based on TextGrid). Download the files from [GitHub](https://github.com/quadrama/gerdracor) and store the XML files in a directory. We will call the directory `$TEIDIR` in the following examples. The directory `$OUTDIR` is used to store the output of the pipeline. You'll need the file `drama.Main.jar`.

Enter the following command in the command line interface:
`java -cp target/assembly/drama.Main.jar de.unistuttgart.ims.drama.main.TEI2XMI --input $TEIDIR --output $OUTDIR/xmi --csvOutput $OUTDIR/csv --skipSpeakerIdentifier --corpus GERDRACOR --collectionId "gdc" --doCleanup`

After running, the directory `$OUTDIR` contains two sub directories, `xmi` and `csv`, which are different file formats for the plays.


## TEI/XML dialects

This package supports the following drama corpora
- TextGrid (German)
- GerDraCor (German)
- theatre classique (French)
