# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 2.0.0 - 2020-10-29
### Changed
- Updated the Jakarta API to 2.0.0.

## 2.0.0-RC2 - 2020-04-26
### Added
- Added a new JSON-P provider `joy-yaml` which can parse YAML documents with the help of [snakeyaml-engine](https://bitbucket.org/asomov/snakeyaml-engine). This artifact can be used as a substitute for the basic provider `joy-classic`. `JsonParser` and `JsonReader` provided by the new provider
can process YAML documents.

### Changed
- The artifact ID of the original Joy is now changed to `joy-classic`.
- Improved `JsonNumber.numberValue()` which now may return an instance of `Integer` or `Long` if applicable.

### Fixed
- Fixed the error message emitted when an array in the JSON document is not closed correctly.

## 2.0.0-RC1 - 2020-04-05
### Changed
- The API package was renamed from `javax.json` to `jakarta.json`.
- Update the version of Jakarta JSON Processing API to 2.0.0-RC2.

## 1.3.0 - 2020-02-01
### Changed
- Collections and maps passed to `JsonBuilderFactory` now can contain instances of `JsonArrayBuilder` or `JsonObjectBuilder` as their values. (PR #3 contributed by [@toddobryan](https://github.com/toddobryan))

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
