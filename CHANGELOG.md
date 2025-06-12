<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Jetbrains-Highlight-on-Copy Changelog

## [Unreleased]

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
