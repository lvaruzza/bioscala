scala -cp target/bioscala-1.0-SNAPSHOT.jar:target/lib/db4o-full-java5-8.1-SNAPSHOT.jar \
      bio.velvet.ExportContigs \
      /data/customers/sugarcane/SC03/assembly.PE.454/velvet/ \
      /data/customers/sugarcane/SC03/assembly.PE.454/preprocessor/colorspace_input.csfasta \
      contigs 

