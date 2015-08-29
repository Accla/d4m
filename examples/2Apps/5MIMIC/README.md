MIMIC Text Topic Modeling Example
=================================

This examples shows the D4M, Accumulo and Graphulo components
of a text processing application for a small sample of the open source
biomedical dataset called [MIMIC II][].

Requires: Accumulo database with Graphulo Jar installed.
Modify DBsetup.m so that it can connect to the Accumulo database.

* `mic01_Ingest`: Tokenize documents using a bag-of-words approach and ingest to Accumulo.
Parallel ingest requires pMatlab; set PARALLEL=true for parallel.
* `mic02_Filter`: Performs dictionary and high-pass degree filtering
server-side using Graphulo, storing filtered data in a new table.
Also writes a file containing all patients present in the filtered dataset
and a new degree table.
* `mic03_Tfidf`:  Apply a Term Frequency - Inverse Document Frequency
transformation server-side using Graphulo.
* `mic04_NMF`:    Factor the now fully pre-processed document-word table
into a W and H table that group documents and words into topics.
* `mic05_Visualize/`: cd into this directory and run `mic05_Visualize`
to launch the Matlab/Octave portion of a web server which services a query
for a particular document's top topics.
Reads from an `input.json` file that contains an example input query
and writes results to an `output.json` file.

[MIMIC II]: https://mimic.physionet.org/database.html
