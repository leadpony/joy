# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.2.0 - 2019-09-29
### Changed
- Update the JSON Processing API to version 1.1.6.

### Fixed
- A bug which was causing `JsonParser.skipArray()` and `JsonParser.skipObject()` to throw an `IllegalStateException` wrongly when the current state is not the start of the array/object.

## 1.1.0 - 2019-08-04
### Added
- New configuration properties `INDENTATION_SIZE` and `TAB_INDENTATION` were added to the extended interface `JsonGenerator`.
  (Issue #1 originally proposed by @hohwille)

## 1.0.1 - 2019-07-28
### Fixed
- `getConfigInUse()` methods now return only properties recognized by the factory.

## 1.0.0 - 2019-07-23
### Added
- First release to the Maven Central Repository.
