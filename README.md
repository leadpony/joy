# Joy

[![Apache 2.0 License](https://img.shields.io/:license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/org.leadpony.joy/joy-classic.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.leadpony.joy%22%20AND%20a:%22joy-classic%22)
[![Javadocs](https://www.javadoc.io/badge/jakarta.json/jakarta.json-api.svg)](https://www.javadoc.io/doc/jakarta.json/jakarta.json-api/1.1.6/index.html)
[![Build Status](https://travis-ci.org/leadpony/joy.svg?branch=master)](https://travis-ci.org/leadpony/joy)

Joy is yet another implementation of [Jakarta JSON Processing API] (JSON-P).

## Key Features

* Fully compliant with the latest specification of [Jakarta JSON Processing API].
* Passes more than 2,000 tests provided by [JSON-P Test Suite].
* Works perfect with [Jakarta JSON Binding API] (JSON-B).
* Supports YAML parsing and reading with the help of [snakeyaml-engine].
* Supports Java 8 and higher.
* Can be used as a modular jar in Java 9 and higher.

## Getting Started

### Joy version 2

Joy version 2 is an implementation of [Jakarta JSON Processing API] version 2.x, which is now migrated to `jakarta.json` package. Please note that this is still a prerelease and the API may be changed.

For using this version, the following 2 dependencies neeed to be added in your `pom.xml` as an API and its implementation, respectively.

```xml
<dependency>
    <groupId>jakarta.json</groupId>
    <artifactId>jakarta.json-api</artifactId>
    <version>2.0.0</version>
</dependency>

<dependency>
    <groupId>org.leadpony.joy</groupId>
    <artifactId>joy-classic</artifactId>
    <version>2.1.0</version>
    <scope>runtime</scope>
</dependency>
```

### Joy version 1

Joy version 1 is an implementation of [Jakarta JSON Processing API] version 1, which was defined in `javax.json` package. For using this version, the following 2 dependencies neeed to be added in your `pom.xml` as an API and its implementation, respectively.

```xml
<dependency>
    <groupId>jakarta.json</groupId>
    <artifactId>jakarta.json-api</artifactId>
    <version>1.1.6</version>
</dependency>

<dependency>
    <groupId>org.leadpony.joy</groupId>
    <artifactId>joy</artifactId>
    <version>1.3.0</version>
    <scope>runtime</scope>
</dependency>
```

## Beyond the Standard API

For using the provider-specific API shown below, `scope` element of `dependency` must be changed from `runtime` to `compile` or be removed entirely.

The provider-specific API is available in `org.leadpony.joy.api` package.

### JsonGenerator

The following configuration properties are added.

* INDENTATION_SIZE

  Specifies the number of spaces to be used as an
  indentation. The value of the property must be an integer. By default the
  number is 4.

* TAB_INDENTATION

  Uses a tab for indentation instead of spaces. The
  value of the property could be anything.

## YAML Support

All you need to do for parsing/reading YAML documents is switching the implementation from `joy-classic` to `joy-yaml` as shown below:

```xml
<dependency>
    <groupId>org.leadpony.joy</groupId>
    <artifactId>joy-yaml</artifactId>
    <version>2.1.0</version>
    <scope>runtime</scope>
</dependency>
```

Please note that there are some restrictions in the current implementation of `joy-yaml`.
* Aliases are not supported and cause the parser/reader to throw an exception.
* Anchors are silently ignored.
* Multiple documents are not supported.
* Both generating and writing YAML documents are not supported yet.

## Additional Resources
* [Jakarta JSON Processing API Reference in Javadoc](https://www.javadoc.io/doc/jakarta.json/jakarta.json-api)
* [Joy API Reference in Javadoc](https://javadoc.io/doc/org.leadpony.joy/joy-core)
* [Changelog](CHANGELOG.md)

## Building from Source

JDK 14 and [Maven] are the tools required to build this software. The following command builds and install it into your local Maven repository.

```bash
mvn clean install -P release
```

## Other Solutions

There are other implementations compatible with this software.

* [Jakarta JSON Processing] (Reference Implementation)
* [Apache Johnzon]

## Copyright Notice
Copyright 2019-2021 the original author or authors. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this product except in compliance with the License.
You may obtain a copy of the License at
<http://www.apache.org/licenses/LICENSE-2.0>

[Apache 2.0 License]: https://www.apache.org/licenses/LICENSE-2.0
[Apache Johnzon]: https://johnzon.apache.org/
[Jakarta JSON Binding API]: http://json-b.net/
[Jakarta JSON Processing]: https://eclipse-ee4j.github.io/jsonp/
[Jakarta JSON Processing API]: https://eclipse-ee4j.github.io/jsonp/
[JSON-P Test Suite]: https://github.com/leadpony/jsonp-test-suite
[Maven]: https://maven.apache.org/
[snakeyaml-engine]: https://bitbucket.org/asomov/snakeyaml-engine