How to run this project
-----------------------

This in a Maven 2 project.
The dependencies are declared in pom.xml.

All dependencies are in public repositories except for:

* JCR
  From day.com. maven should explain where to download form and how to install.
  (HINT: http://www.day.com/maven/jsr170/jars/jcr-1.0.jar)
  

* jackrabbit-ocm
  Not released yet so checkout out /contrib/jackrabbit-jcr-mapping/jcr-mapping from:
  http://svn.apache.org/viewvc/jackrabbit/trunk
  
  - svn checkout http://svn.apache.org/repos/asf/jackrabbit/trunk/contrib/jackrabbit-jcr-mapping/jcr-mapping jcr-mapping
  - cd jcr-mapping
  - open pom.xml and replace '1.4-SNAPSHOT' in all jackrabbit artifacts with '1.3'
  To build and install do:
  mvn -Dmaven.test.skip=true -DperformRelease=true install