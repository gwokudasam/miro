### Cover Lever

Hope this POC fulfils the requirements, and the approach finds its adherents amongst reviews.

_Tha approach taken is experimental and not meant to be a "this is how I always did/do this"!_
As long as it delivers the expected behaviour - it should be fine.


Project uses a lot of code generation (there were even more at some points, like [DSL assertions for tests](https://joel-costigliola.github.io/assertj/assertj-assertions-generator.html))
From some time not long ago most of the needed components of business layer as well as models, projections, dsl criteria
present all in one library, so the choice was made based on that, and overall quite inspiring impression on [Immutables](http://immutables.github.io/)
from some time before from the project where we used intensively.

The main principle put in the basis is to write as less code as possible still complying with commonly known practices,
to make code maintainable and readable for junior or senior worked with Spring or other technologies before, leverage immutability,

There are not much code and/or documentation but this is one of the goals of the POC 



### Prerequisites

- Java 14+

##### to see generated sources 
```shell script
mvn compile
```






## IntelliJ generated project `readme` below
### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.4.0-M1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.4.0-M1/maven-plugin/reference/html/#build-image)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/htmlsingle/#using-boot-devtools)
* [Validation](https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/htmlsingle/#boot-features-validation)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

