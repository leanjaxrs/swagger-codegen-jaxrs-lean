# Swagger Codegen for Lean JAX-RS interfaces - without the clutter!

## Overview
This [Swagger](https://swagger.io)-codegen plugin generates Java interfaces for 
JAX-RS, which can be used for server AND client alike. In contrast to the 
JAX-RS support built into swagger-codegen, this plugin can be used to repeatedly
generate java code in every build step.

Check out [OpenAPI-Spec](https://github.com/OAI/OpenAPI-Specification) for additional information about the Swagger project, including additional libraries with support for other languages and more. 

## How do I use this?

Use this plugin in your maven POM, in you swagger codegen plugin configuration,
like this:

```
	<build>
		<plugins>
			<plugin>
				<groupId>io.swagger</groupId>
				<artifactId>swagger-codegen-maven-plugin</artifactId>
				<version>2.2.1</version>
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
							<!-- language file, like e.g. JavaJaxRSCodegen shipped with swagger -->
							<language>jaxrs-lean</language>
							<addCompileSourceRoot>true</addCompileSourceRoot>
							
							<apiPackage>handler</apiPackage>
							<modelPackage>model</modelPackage>
							<invokerPackage>invoker</invokerPackage>
							<sourceFolder>${project.build.directory}/generated-sources/swagger</sourceFolder>
						</configuration>
					</execution>
				</executions>

			</plugin>
		</plugins>
	</build>
```

