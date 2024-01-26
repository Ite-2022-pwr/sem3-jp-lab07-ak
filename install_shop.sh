#!/usr/bin/bash
mvn install:install-file \
   -Dfile=./shop-1.0-SNAPSHOT.jar \
   -DgroupId=pl.edu.pwr.tkubik.jp \
   -DartifactId=shop \
   -Dversion=1.0-SNAPSHOT \
   -Dpackaging=jar \
   -DgeneratePom=true
