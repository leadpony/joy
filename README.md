# Joy

Joy is yet another implementation of [Jakarta JSON Processing API] (JSON-P).

## Key Features

* Fully compliant with the latest specification of [Jakarta JSON Processing API].
* Passes more than 2,000 tests provided by [JSON-P Test Suite].
* Works perfect with [Jakarta JSON Binding API] (JSON-B).
* Supports YAML parsing and reading with the help of [snakeyaml-engine].
* Supports Java 8 and higher.
* Can be used as a modular jar in Java 9 and higher.

## Getting Started

Joy version 2 is an implementation of [Jakarta JSON Processing API] version 2.x, which is now migrated to `jakarta.json` package. 

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

## Copyright Notice
Copyright 2019-2021 the original author or authors. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this product except in compliance with the License.
You may obtain a copy of the License at
<http://www.apache.org/licenses/LICENSE-2.0>

[Apache 2.0 License]: https://www.apache.org/licenses/LICENSE-2.0
[Jakarta JSON Binding API]: http://json-b.net/
[Jakarta JSON Processing]: https://eclipse-ee4j.github.io/jsonp/
[Jakarta JSON Processing API]: https://eclipse-ee4j.github.io/jsonp/
[JSON-P Test Suite]: https://github.com/leadpony/jsonp-test-suite
[Maven]: https://maven.apache.org/
[snakeyaml-engine]: https://bitbucket.org/asomov/snakeyaml-engine
