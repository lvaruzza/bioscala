bioscala
========

A bioinformatics library made with the Scala Language. 

This project is in it's early days with a minimum function to read
fasta files and principaly some code to deal with the Velvet Graph File.


Prerequisites
-------------

1. Maven >= 2
2. scala >= 2.8

How to compile
--------------

  mvn install

How to run the commands
-----------------------

The commands are availiable as shell scripts in bin directory

Example:

  ./bin/dumpContigs velvet preprocessor/colorspace_input.csfasta contigs
