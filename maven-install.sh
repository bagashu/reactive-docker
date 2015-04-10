# Helper to package up the spray-swagger to install as a local-repository in our git system
# Reference: http://maven.apache.org/plugins/maven-install-plugin/usage.html
# This script wraps the following steps in a single script:
# Steps:
# - sbt publishLocal - This will install the artifact in .ivy2
# - cd to the ivy2 directory ~/.ivy2/local/org.almoehi/reactive-docker_2.11/0.1.1-CYAN-${this_version}
# - Run mvn install ... to produce the equivalent repository information as a Maven repository
# - cd to the directory under .m2: ~/.m2/repository/org/almoehi
# - tar up the contents of the  directory spray-swagger_2.10
# It will look like:
# almoehi/reactive-docker_2.11/${this_version}/_maven.repositories
# almoehi/reactive-docker_2.11/${this_version}/spray-swagger_2.10-${this_version}.jar
# gettyimages/spray-swagger_2.10/${this_version}/spray-swagger_2.10-${this_version}.pom
# gettyimages/spray-swagger_2.10/maven-metadata-local.xml
#
# Steps to use this script:
# Update the version.sbt file and set this_version below to the same value
# - From this directory at the command prompt run: ./maven-install
# - Untar /tmp/spray-swagger_2.10-${this_version}.tar in the bp-orchestrate-core repo under the local-repository/com/gettyimages directory
# If the version changed, there will be git work under the local-repository to add the new entries and/or remove the old

scalaV="2.11"
version=$(cat version.sbt | cut -d "=" -f2 | cut -d '"' -f2)
echo "Reactive-Docker Version is: "$version

this_dir=${PWD}
sbt publishLocal
if [ $? -ne 0 ]; then
    echo "FAILURE: publishLocal"
    exit 1
fi

cd ~/.ivy2/local/org.almoehi/reactive-docker_2.11/$version
if [ $? -ne 0 ]; then
    echo "FAILURE: Unable to cd to ivy2 cache directory"
    exit 1
fi

echo "Installing reactive-docker" in local maven repository...
mvn install:install-file -DgroupId=org.almoehi -DartifactId=reactive-docker_2.11 -Dfile=./jars/reactive-docker_2.11.jar -Dsources=./srcs/reactive-docker_2.11-sources.jar -Djavadoc=./docs/reactive-docker_2.11-javadoc.jar -DpomFile=./poms/reactive-docker_2.11.pom
if [ $? -ne 0 ]; then
    echo "FAILURE: Unable to install contents of ivy2 cache into Maven directory"
    exit 1
fi

cd ~/.m2/repository/org/almoehi
if [ $? -ne 0 ]; then
    echo "FAILURE: Unable to cd to Maven directory"
    exit 1
fi

tar -cvf /tmp/reactive-docker_2.11-$version.tar reactive-docker_2.11
if [ $? -ne 0 ]; then
    echo "FAILURE: Unable to create tar file from Maven directory"
    exit 1
fi
