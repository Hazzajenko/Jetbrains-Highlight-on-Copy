# Highlight on Copy

![Build](https://github.com/Hazzajenko/Jetbrains-Highlight-on-Copy/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

## Development Status
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [x] Adjust the [pluginGroup](./gradle.properties) and [pluginName](./gradle.properties), as well as the [id](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [x] Convert VSCode extension functionality to IntelliJ Platform API
- [x] Implement copy and highlight functionality with multi-cursor support
- [x] Add configurable settings (colors, timeout)
- [x] Adjust the plugin description in `README`
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the `MARKETPLACE_ID` in the above README badges after publishing.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).

<!-- Plugin description -->
Highlights the text that was just copied to the clipboard with customizable colors and duration.

**Features:**
- 🎨 Highlights copied text with configurable background and foreground colors
- ⏱️ Adjustable highlight timeout duration (default: 1 second)
- 🎯 Multi-cursor support with intelligent selection handling
- 📄 Smart line copying when no text is selected (copies entire line)
- ⚙️ Easy configuration through IDE settings (`Tools → Highlight on Copy`)
- 🔄 Replaces default copy action (`Ctrl+C`/`Cmd+C`) with enhanced functionality

Perfect for visual feedback when copying code, making it immediately clear what was just copied to the clipboard. Converted from a popular VSCode extension to bring the same functionality to JetBrains IDEs.

**Usage:**
- Select text and press `Ctrl+C` (or `Cmd+C`) - selected text will be highlighted
- With no selection, press `Ctrl+C` - entire line will be copied and highlighted
- Multiple cursors supported - all selections will be highlighted
- Configure colors and timing in `Settings → Tools → Highlight on Copy`
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

### Key Implementation Details
- **Copy Action**: Replaces default copy with enhanced version that provides visual feedback
- **Multi-cursor Support**: Intelligently handles multiple selections, avoiding duplicates on same line
- **Highlighting**: Uses `MarkupModel.addRangeHighlighter()` with configurable `TextAttributes`
- **Settings**: Persistent configuration using `PersistentStateComponent`
- **Cleanup**: Automatic highlight removal using `Timer` with configurable timeout

### API Mapping from VSCode Extension
| VSCode API | IntelliJ Platform API |
|------------|----------------------|
| `vscode.commands.executeCommand("editor.action.clipboardCopyAction")` | `CopyPasteManagerEx.setContents()` |
| `vscode.window.createTextEditorDecorationType()` | `MarkupModel.addRangeHighlighter()` |
| `editor.setDecorations()` | `RangeHighlighter` with `TextAttributes` |
| `vscode.workspace.getConfiguration()` | `PersistentStateComponent` |
| `setTimeout()` | `java.util.Timer` |

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation

## Features

- 🎨 **Customizable highlighting**: Configure background and foreground colors
- ⏱️ **Adjustable timeout**: Control how long the highlight stays visible
- 🎯 **Multi-cursor support**: Handles multiple selections intelligently
- 📄 **Smart line copying**: Copies entire line when no text is selected
- ⚙️ **Easy configuration**: Simple settings panel in IDE preferences

## Installation

### From Source (Development)

1. **Clone this repository**:
   ```bash
   git clone https://github.com/yourusername/highlight-on-copy-plugin.git
   cd highlight-on-copy-plugin
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

The plugin replaces the default copy action (`Ctrl+C` / `Cmd+C`). When you copy text:

1. **With text selected**: Copies and highlights the selected text
2. **With no selection**: Copies and highlights the entire current line
3. **With multiple cursors**: Copies and highlights all selections

### Configuration

1. Go to `File → Settings → Tools → Highlight on Copy` (or `IntelliJ IDEA → Preferences → Tools → Highlight on Copy` on macOS)

2. **Available settings**:
  - **Background Color**: Color of the highlight background (default: yellow #FFFF00)
  - **Foreground Color**: Text color during highlight (leave empty for no change)
  - **Timeout**: How long the highlight lasts in milliseconds (default: 1000ms)

### Manual Action

You can also trigger the action manually:
- `Edit → Highlight and Copy` from the menu
- Use the keyboard shortcut `Ctrl+C` / `Cmd+C`

## Development

### Project Structure

```
src/main/kotlin/com/yourname/highlightoncopy/
├── HighlightOnCopyAction.kt          # Main action implementation
└── settings/
    ├── HighlightOnCopySettings.kt    # Settings service
    └── HighlightOnCopyConfigurable.kt # Settings UI

src/main/resources/META-INF/
└── plugin.xml                       # Plugin configuration

src/test/kotlin/
└── HighlightOnCopyActionTest.kt     # Unit tests
```

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

#### API Usage

- **`CopyPasteManagerEx`**: Handles clipboard operations
- **`MarkupModel.addRangeHighlighter()`**: Creates text highlighting
- **`TextAttributes`**: Defines highlight styling
- **`CaretModel`**: Manages cursor positions and selections
- **`PersistentStateComponent`**: Stores user settings

#### Multi-cursor Handling

The plugin intelligently handles multiple cursors by:
1. Sorting selections by line and column
2. Ignoring duplicate empty selections on the same line
3. Copying entire lines for empty selections
4. Joining multiple selections with newlines

#### Highlight Management

Highlights are:
- Applied using high-priority layers to be visible
- Automatically removed after the configured timeout
- Properly cleaned up on project disposal

## Conversion from VSCode Extension

This plugin was converted from a VSCode extension with the following mappings:

| VSCode API | IntelliJ Platform API |
|------------|----------------------|
| `vscode.commands.executeCommand("editor.action.clipboardCopyAction")` | `CopyPasteManagerEx.setContents()` |
| `vscode.window.createTextEditorDecorationType()` | `MarkupModel.addRangeHighlighter()` |
| `editor.setDecorations()` | `RangeHighlighter` with `TextAttributes` |
| `vscode.workspace.getConfiguration()` | `PersistentStateComponent` |
| `setTimeout()` | `java.util.Timer` |

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
- Basic copy and highlight functionality
- Multi-cursor support
- Configurable colors and timeout
- Comprehensive test suite

## Support

If you encounter any issues or have suggestions:

1. Check the [Issues](https://github.com/yourusername/highlight-on-copy-plugin/issues) page
2. Create a new issue with:
  - IDE version
  - Plugin version
  - Steps to reproduce
  - Expected vs actual behavior

## Related Projects

- [Original VSCode Extension](https://github.com/original-vscode-extension) (if applicable)
- [IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)