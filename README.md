# Swagger Codegen for Lean JAX-RS interfaces - without the clutter!

## Overview
This [Swagger](https://swagger.io)-codegen plugin generates Java interfaces for 
JAX-RS, which can be used for server AND client alike. In contrast to the 
JAX-RS support built into swagger-codegen, this plugin can be used to repeatedly
generate java code in every build step.

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

						<apiPackage>handler</apiPackage>
						<modelPackage>model</modelPackage>
						<sourceFolder>${project.build.directory}/generated-sources/swagger</sourceFolder>
					</configuration>
				</execution>
			</executions>

		</plugin>
	</plugins>
</build>
```

