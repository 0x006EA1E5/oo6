#!/bin/bash
mvn jdocbook:resources
mvn -Ddocbook.name=user-guide jdocbook:generate
mvn -Ddocbook.name=designer-guide jdocbook:generate
mvn -Ddocbook.name=developer-guide jdocbook:generate
mvn -Ddocbook.name=hosting-guide jdocbook:generate



