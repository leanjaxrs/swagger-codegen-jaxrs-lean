# Lean JAX-RS Swagger Codegen Plugin: interfaces without the clutter!

[![Build Status](https://travis-ci.org/leanjaxrs/swagger-codegen-jaxrs-lean.svg?branch=master)](https://travis-ci.org/leanjaxrs/swagger-codegen-jaxrs-lean)

## Overview
This [swagger](https://swagger.io)-codegen plugin generates Java interfaces for 
JAX-RS, which can be used for server AND client alike. 

While Swagger already has built-in plugins claiming to support JAX-RS,
these will generate whole maven projects including directory structure, POMs
and other files. While this may be a good starting point for some, this approach
is problematic when you only need the generated code and nothing else, or if 
you want to add code generation to your build process. They also generate a
plethora of unnecessary dependencies.

In contrast, this plugin will only generate the Java interfaces for the APIs and
their respective models, which makes it much smoother to handle.

Features:
* Generates only Java files and packages, without anything else that makes integration into existing projects harder (no POMs, no directory structures like src/main/..)
* Generates your API as Java interfaces instead of classes, which will give you more flexibility when implementing them (the base class 'slot' is no longer occupied by Swagger-generated code)
* Properly represented method return types: While the original JAX-RS codegen plugin only generates API method return types as javax.ws.rs.Response, this plugin will return actual types, making your methods finally type safe. You'll still be able to report HTTP status (404 and the like) by throwing JAX-RS's WebApplicationException
* Models will use unboxed types (e.g. int instead of Integer) when the property is set to be required. This makes it much clearer to the implementor that such a property can never be null.
* Generates a neat build() method, allowing fluent initialization of your model classes
* JavaDoc DocLint-ready: The generated comments will pass DocLint tests
 
Check out [OpenAPI-Spec](https://github.com/OAI/OpenAPI-Specification) for additional information about the Swagger project, including additional libraries with support for other languages and more. 

## How do I use this?

Use this codegen plugin in your maven POM with the swagger-codegen-maven-plugin:

```
<build>
	<plugins>
		<plugin>
			<groupId>io.swagger</groupId>
			<artifactId>swagger-codegen-maven-plugin</artifactId>
			<version>2.2.1</version>
			
			<!-- bind the jaxrslean swagger-codegen plugin, so the 'jaxrs-lean' language
			becomes available in swagger -->
			<dependencies>
				<dependency>
					<groupId>com.github.upachler.swagger.codegen.jaxrslean</groupId>
					<artifactId>jaxrslean</artifactId>
					<version>1.0-SNAPSHOT</version>
				</dependency>
			</dependencies>
			
			<executions>
				<execution>
					<goals>
						<goal>generate</goal>
					</goals>
					<configuration>
						<inputSpec>${basedir}/src/main/swagger/swagger.yaml</inputSpec>
						<!-- the jaxrs-lean language provided by the jaxrslean codegen plugin -->
						<language>jaxrs-lean</language>

						<apiPackage>com.company.api</apiPackage>
						<modelPackage>com.company.api.model</modelPackage>
						<sourceFolder>${project.build.directory}/generated-sources/swagger</sourceFolder>
					</configuration>
				</execution>
			</executions>

		</plugin>
	</plugins>
</build>
```

