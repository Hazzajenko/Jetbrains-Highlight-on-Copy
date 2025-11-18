<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Jetbrains-Highlight-on-Copy Changelog

## [Unreleased]

### Changed

- Updated IntelliJ Platform from 2024.2.5 to 2025.2.3 for compatibility with latest JetBrains IDE releases
- Updated Kotlin from 2.1.21 to 2.2.21
- Updated IntelliJ Platform Gradle Plugin from 2.6.0 to 2.10.4
- Updated Kotlinx Kover from 0.9.1 to 0.9.3
- Updated Gradle Changelog Plugin from 2.2.1 to 2.4.0
- Updated Qodana from 2025.1.1 to 2025.2.1
- Updated GitHub Actions workflows:
  - actions/checkout from 4 to 5
  - actions/setup-java from 4 to 5
  - gradle/actions from 4 to 5
  - actions/upload-artifact from 4 to 5
  - JetBrains/qodana-action from 2025.1 to 2025.2

## [1.0.1] - 2025-06-10

### Added

- Comprehensive unit test suite covering all core functionality
- Tests for color parsing with various hex formats including edge cases
- Tests for settings persistence and state management
- Tests for UI configuration components and lifecycle

### Changed

- Migrated from deprecated `addAnActionListener` to declarative listener registration using message bus infrastructure for better performance and future compatibility

### Fixed

- Fixed an issue where text selections were not restored after the highlight animation finished.
- Fixed a bug where copying a line with no selection would cause the entire line to become selected after the highlight.

### Removed

- Removed deprecated HighlightOnCopyAction and HighlightOnCopyActionTest as its not needed anymore.

## [1.0.0] - 2025-06-07

### Added

- Visual feedback when copying text to clipboard with customizable blinking effect
- Multi-cursor support with intelligent selection handling
- Smart line copying - highlights entire line when no text is selected
- Configurable background and foreground colors for highlights
- Support for both standard (#RRGGBB) and alpha (#RRGGBBAA) hex color formats
- Adjustable blink count and timing interval settings
- Compatibility with all copy methods (Ctrl+C, Edit menu, right-click copy)
- Easy configuration through IDE settings panel at `Tools → Highlight on Copy`

[Unreleased]: https://github.com/Hazzajenko/Jetbrains-Highlight-on-Copy/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/Hazzajenko/Jetbrains-Highlight-on-Copy/releases/tag/v1.0.0
