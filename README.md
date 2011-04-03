BioScala
========

A bioinformatics library made with the Scala Language. 

This project is in it's early days with a minimum function to read
fasta files and principaly some code to deal with the Velvet Graph File.


Prerequisites
-------------

1. Maven >= 2
   http://maven.apache.org/
   
2. Scala Language >= 2.8
   http://www.scala-lang.org/
   
How to Download the Source
--------------------------

Use the git SCM software with the command:

  git clone git://github.com/lvaruzza/bioscala.git
  
Git is available in most of the Linux distributions and in the site:

http://git-scm.com/


How to compile
--------------

  mvn install

How to run the commands
-----------------------

The commands are availiable as shell scripts in bin directory

Example:

  ./bin/dumpContigs velvet preprocessor/colorspace_input.csfasta contigs
