# Joy

[![Apache 2.0 License](https://img.shields.io/:license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/org.leadpony.joy/joy.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.leadpony.joy%22%20AND%20a:%22joy%22)
[![Javadocs](https://www.javadoc.io/badge/jakarta.json/jakarta.json-api.svg)](https://www.javadoc.io/doc/jakarta.json/jakarta.json-api)
[![Build Status](https://travis-ci.org/leadpony/joy.svg?branch=master)](https://travis-ci.org/leadpony/joy)

Joy is a fast and robust JSON parser implementing Java API for JSON Processing (JSR 374).

## Key Features

* Fully compliant with version 1.1 of [Java API for JSON Processing (JSR 374)].
* Passes all tests provided by [JSON-P Test Suite].
* Supports Java 8 and higher.
* Can be used as a modular jar in Java 9 and higher.
* Developed from scratch to produce cleaner code.

## Getting Started

Add this software to `pom.xml` as the implementation of the JSON-P API instead of any other implementations.

```xml
<dependency>
    <groupId>org.leadpony.joy</groupId>
    <artifactId>joy</artifactId>
    <version>1.0.1</version>
    <scope>runtime</scope>
</dependency>
```

## Additional Resources
* [API Reference in Javadoc]
* [Changelog]

## Building from Source

JDK 9 and [Maven] are tools required to build this software. The following command builds and install it into your local Maven repository.

```bash
mvn clean install -P release
```

## Other Solutions

There are other implementations compatible with this software.

* [Jakarta JSON Processing] (Reference Implementation)
* [Apache Johnzon]

## Copyright Notice
Copyright &copy; 2019 the Joy Authors. This software is licensed under [Apache License, Versions 2.0][Apache 2.0 License].

[Apache 2.0 License]: https://www.apache.org/licenses/LICENSE-2.0
[Java API for JSON Processing (JSR 374)]: https://eclipse-ee4j.github.io/jsonp/
[JSON-P Test Suite]: https://github.com/leadpony/jsonp-test-suite
[API Reference in Javadoc]: https://www.javadoc.io/doc/jakarta.json/jakarta.json-api/1.1.5
[Changelog]: CHANGELOG.md
[Maven]: https://maven.apache.org/
[Jakarta JSON Processing]: https://eclipse-ee4j.github.io/jsonp/
[Apache Johnzon]: https://johnzon.apache.org/
