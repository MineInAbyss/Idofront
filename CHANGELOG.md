# Changelog

All notable changes to this project between Minecraft versions will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/). We currently do not follow semver but
try to keep MAJOR changes in sync with Minecraft updates.

## [Unreleased]

### Added

- New `idofront-services` module for interfaces implemented by this plugin using Bukkit's ServiceManager API
- Start a changelog based on Keep a Changelog
- `Services.register` helper function
- New **command argument** helpers like offline player, options, etc...

### Changed

- **Feature system** has been reworked as a DSL built around Koin for dependency injection
- **Config system** has been reworked to use a builder class that can then pick whether to decode a single file, directory, or multi-entry format which has been moved here from Geary
- **SerializableItemStack** uses a new service for letting other plugins register custom item types instead of manually adding support for them
- Brigadier **command api** internals reworked with much cleaner inside logic and fixes to expected behaviour from Brigadier 

### Removed

- **Deprecated command api** removed, leaving only the Brigadier-based one

## [1.0.0] - 2025-08-12

### Changed

- Update to Minecraft 1.21.8
- Update to Kotlin 2.2
- Add androidx.sqlite library which provides lightweight native bindings we plan to use for our own database library
