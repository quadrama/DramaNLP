[![Build Status](https://travis-ci.org/quadrama/DramaNLP.svg?branch=master)](https://travis-ci.org/quadrama/DramaNLP)


# DramaNLP
This project collects a number of UIMA components to process dramatic texts. We follow general design ideas implemented in dkpro.

## Usage
The module `de.unistuttgart.ims.drama.examples` contains several classes with main methods to illustrate how to use the components. 

### Visualise.main()
Reads a list of urls from a file (the urls point to textgrid TEI XML documents), processes them and prints out UIMA XMI files, to be found in `target/`. The XMI files contain the drama text and all annotations.

## Components

Currently, the following components are provided

### de.unistuttgart.ims.drama.api
UIMA annotation types for dramatic texts.

### de.unistuttgart.ims.drama.io.gutenbergde
Reads in dramatic texts from HTML files downloaded from gutenberg.spiegel.de
Currently, this component expects preprocessing. Scripts can be found in `src/main/perl`.

### de.unistuttgart.ims.drama.io.tei.textgrid
Parsing textgrid TEI texts, as good as possible.

### de.unistuttgart.ims.drama.core
Components that allow processing of dramatic texts. We make use of standard dkpro components for specific portions of the dramatic texts (e.g., figure speech).
`DramaSpeechSegmenter.getWrappedSegmenterDescription(Class<? extends AnalysisComponent> compClass)` can be used
to run a dkpro segmenter (for creating token and sentence annotations). Following dkpro components only rely on these annotations and can be used directly (as token and sentence annotations get projected into the drama text.
Stage directions will not be analysed at the moment.

### de.unistuttgart.ims.drama.graph
Components to extract different kinds of networks from the text
