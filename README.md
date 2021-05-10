# GTF Case Study - Library Dependencies in Android

This project contains a case study for the [Graph Transformation Framework](https://github.com/FHOOEAIST/GTF), based on library dependencies of Android apps of the [F-Droid](https://www.f-droid.org/) catalogue.

For this task, this project contains a crawler implementation to get the Gradle build files of the applications of this catalogue. Based on these build files, the dependencies are extracted and are collected together with the associated base dependencies to create a dependency tree. This dependency tree is in turn converted into a graph using the GTF framework. 

## Requirements

- [Docker](https://www.docker.com/): Only required if you want to reproduce the complete process from crawling the data to the use case itself. 
- [OpenJDK11](https://openjdk.java.net/projects/jdk/11/): This project is based on a Java 11 project and uses OpenJDK as implementation of the Java SE Platform .
- [Maven](https://maven.apache.org/): This project is based on a Java 11 project using Maven.

## Getting Started

If you want to reproduce our results you can use the provided JSON-based dependency tree files of the associated F-Droid projects (start with step 7). 

In the case you want to start from the scratch you can also crawl the data on your own based on the provided crawler implementations.

For this execute the following steps:

1. Download the F-Droid [index-v1.jar](https://f-droid.org/repo/index-v1.jar) and unzip the .jar file to get the `index-v1.json` indices.
2. Save the file to the project resources as `fdroid.json` file.
3. Start the [Tor Proxy](https://hub.docker.com/r/dperson/torproxy) Docker image. 
4. Use the `GradleCrawlerMain.java` to retrieve the gradle build files of the Andoid apps of F-Droid (based on the `fdroid.json` file)
5. Use the `GradleDependencyExtractor.java` to extract the dependencies of the individual projects
6. Based on the extracted dependencies you can use the `DependencyResolutionMain.java` to create the JSON-based dependency tree files of the associated F-Droid projects.
7. Use `CaseStudyMain.java` to run the case study based on the dependency tree files. This will execute the actual case study and for this will:
   - Build the total graph for all crawled FDroid projects
   - Transform the total graph to a Graph Viz representation
   - Transform the total graph to a list of dependencies
   - Extract a sub graph for the "Antenna Pod" project
   - Transform the sub graph to a Graph Viz representation
   - Verify the "Antenna Pod" sub graph according to duplicated dependendencies with different version

## FAQ

If you have any questions, please checkout our <insert FAQ link here if using maven site, otherwise write a small FAQ section here>.

## Contributing

**First make sure to read our [general contribution guidelines](https://fhooeaist.github.io/CONTRIBUTING.html).**
   
## Licence

Copyright (c) 2021 the original author or authors.
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES.

This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, You can obtain one at https://mozilla.org/MPL/2.0/.

## Research

If you are going to use this project as part of a research paper, we would ask you to reference this project by citing
it. 

<TODO zenodo doi>
