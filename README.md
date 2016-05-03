# DramaNLP
This project collects a number of UIMA components to process dramatic texts. We follow general design ideas implemented in dkpro.

Currently, the following components are provided

## de.unistuttgart.ims.drama.api
Data structures and UIMA annotation types for dramatic texts.

## de.unistuttgart.quadrama.io.gutenbergde
Reads in dramatic texts from HTML files downloaded from gutenberg.spiegel.de

## de.unistuttgart.quadrama.core
Components that allow processing of dramatic texts. We make use of standard dkpro components for specific portions of the dramatic texts (e.g., figure speech)