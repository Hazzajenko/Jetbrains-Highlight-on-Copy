# Highlight on Copy

![Build](https://github.com/Hazzajenko/Jetbrains-Highlight-on-Copy/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

<!-- Plugin description -->
Highlights the text that was just copied to the clipboard with customizable colors and blinking effects.

**Features:**
- 🎨 Highlights copied text with configurable background and foreground colors
- ✨ Configurable blinking effect with customizable blink count and interval
- 🎯 Multi-cursor support with intelligent selection handling
- 📄 Smart line copying when no text is selected (copies entire line)
- ⚙️ Easy configuration through IDE settings (`Tools → Highlight on Copy`)
- 🔄 Works with ANY copy action - Ctrl+C, Edit menu, right-click copy, etc.
- 🎨 Supports both standard (#RRGGBB) and alpha (#RRGGBBAA) hex colors

Perfect for visual feedback when copying code, making it immediately clear what was just copied to the clipboard.

**Usage:**
- Copy text using any method (Ctrl+C, Edit menu, right-click) - copied text will blink
- With no selection, copy action will highlight the entire line
- Multiple cursors supported - all selections will be highlighted
- Configure colors, blink count, and timing in `Settings → Tools → Highlight on Copy`
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Highlight on Copy"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/Hazzajenko/Jetbrains-Highlight-on-Copy/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Development

### Building
```bash
./gradlew build
```

### Testing
```bash
./gradlew test
```

### Running in Development IDE
```bash
./gradlew runIde
```


## Features

- 🎨 **Customizable highlighting**: Configure background and foreground colors
- ✨ **Configurable blinking**: Control blink count and timing
- 🎯 **Multi-cursor support**: Handles multiple selections intelligently
- 📄 **Smart line copying**: Highlights entire line when no text is selected
- ⚙️ **Easy configuration**: Simple settings panel in IDE preferences
- 🔄 **Works with any copy method**: Ctrl+C, Edit menu, right-click copy
- 🎨 **Alpha color support**: Use transparent colors with 8-character hex codes

## Installation

### From Source (Development)

1. **Clone this repository**:
   ```bash
   git clone https://github.com/Hazzajenko/Jetbrains-Highlight-on-Copy.git
   cd Jetbrains-Highlight-on-Copy
   ```

2. **Open in IntelliJ IDEA**:
  - `File → Open` and select the project directory
  - Wait for Gradle to sync

3. **Build and run**:
   ```bash
   ./gradlew runIde
   ```
   This will start a new IDE instance with your plugin loaded.

### From JetBrains Marketplace (Once Published)

1. Go to `File → Settings → Plugins` (or `IntelliJ IDEA → Preferences → Plugins` on macOS)
2. Search for "Highlight on Copy"
3. Click Install and restart the IDE

## Usage

### Basic Usage

The plugin listens for any copy action and provides visual feedback. When you copy text:

1. **With text selected**: Highlights the selected text with a blinking effect
2. **With no selection**: Highlights the entire current line
3. **With multiple cursors**: Highlights all selections
4. **Works with any copy method**: Ctrl+C, Edit menu, right-click copy, etc.

### Configuration

1. Go to `File → Settings → Tools → Highlight on Copy` (or `IntelliJ IDEA → Preferences → Tools → Highlight on Copy` on macOS)

2. **Available settings**:
  - **Background Color**: Color of the highlight background (default: light red #E66159)
  - **Foreground Color**: Text color during highlight (leave empty for no change)
  - **Number of Blinks**: How many times the text blinks (default: 1)
  - **Blink Interval**: Time between blinks in milliseconds (default: 150ms)
  - **Highlight Timeout**: Legacy setting for backwards compatibility (default: 1000ms)

### Copy Methods

The plugin works with all copy methods:
- Keyboard shortcut: `Ctrl+C` / `Cmd+C`
- Edit menu: `Edit → Copy`
- Right-click context menu: `Copy`
- Any other copy action in the IDE

## Development


### Building

```bash
# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Run IDE with plugin
./gradlew runIde

# Verify plugin compatibility
./gradlew verifyPlugin
```

### Testing

The plugin includes comprehensive tests:

```bash
./gradlew test
```

Test coverage includes:
- Simple text selection copying
- Empty selection (line copying)
- Multi-cursor selections
- Action availability checks

### Key Implementation Details


## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Make your changes and add tests
4. Run tests: `./gradlew test`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Changelog

### 1.0.0
- Initial release
- Configurable blinking effect when copying text
- Multi-cursor support with intelligent handling
- Customizable colors including alpha transparency
- Blink count and interval settings
- Works with any copy method (Ctrl+C, menu, right-click)
- Smart line highlighting when no text is selected

## Support

If you encounter any issues or have suggestions:

1. Check the [Issues](https://github.com/Hazzajenko/Jetbrains-Highlight-on-Copy/issues) page
2. Create a new issue with:
  - IDE version
  - Plugin version
  - Steps to reproduce
  - Expected vs actual behavior

