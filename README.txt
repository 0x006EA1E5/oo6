How to run this project
-----------------------

This in a Maven 2 project.
The dependencies are declared in pom.xml.

All dependencies are in public repositories except for:

* JCR
  From day.com. maven should explain where to download form and how to install.

* jackrabbit-ocm
  Not released yet so checkout out /contrib/jackrabbit-jcr-mapping/jcr-mapping from:
  http://svn.apache.org/viewvc/jackrabbit/trunk
  To build and install do:
  mvn -Dmaven.test.skip=true -DperformRelease=true install