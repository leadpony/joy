# Joy

[![Apache 2.0 License](https://img.shields.io/:license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/org.leadpony.joy/joy.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.leadpony.joy%22%20AND%20a:%22joy%22)
[![Javadocs](https://www.javadoc.io/badge/jakarta.json/jakarta.json-api.svg)](https://www.javadoc.io/doc/jakarta.json/jakarta.json-api)
[![Build Status](https://travis-ci.org/leadpony/joy.svg?branch=master)](https://travis-ci.org/leadpony/joy)

Joy is a fast and robust JSON parser implementing Java API for JSON Processing (JSR 374).

## Key Features

* Fully compliant with version 1.1 of [Java API for JSON Processing (JSR 374)].
* Passes more than 2,000 tests provided by [JSON-P Test Suite].
* Works well with [Java API for JSON Binding (JSR 367)].
* Supports Java 8 and higher.
* Can be used as a modular jar in Java 9 and higher.
* Developed from scratch to produce cleaner code.

## Getting Started

First, add the JSON-P API as a dependency to your `pom.xml`.

```xml
<dependency>
    <groupId>jakarta.json</groupId>
    <artifactId>jakarta.json-api</artifactId>
    <version>1.1.6</version>
</dependency>
```

Next, add this software as an implementation of the API.

```xml
<dependency>
    <groupId>org.leadpony.joy</groupId>
    <artifactId>joy</artifactId>
    <version>1.2.0</version>
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

## Additional Resources
* [JSON-P API Reference in Javadoc]
* [Joy API Reference in Javadoc]
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
[Java API for JSON Binding (JSR 367)]: http://json-b.net/
[JSON-P Test Suite]: https://github.com/leadpony/jsonp-test-suite
[JSON-P API Reference in Javadoc]: https://www.javadoc.io/doc/jakarta.json/jakarta.json-api/1.1.5
[Joy API Reference in Javadoc]: https://javadoc.io/doc/org.leadpony.joy/joy
[Changelog]: CHANGELOG.md
[Maven]: https://maven.apache.org/
[Jakarta JSON Processing]: https://eclipse-ee4j.github.io/jsonp/
[Apache Johnzon]: https://johnzon.apache.org/
