# Gitter

A lightweight version control system built in Java. Track changes, manage branches, and maintain project history with a simple command-line interface. Initialize it in any directory to start versioning your files.

## Features

- **Repository Management** - Initialize version control in any directory
- **Staging Area** - Select which changes to include in the next commit
- **Commits** - Create snapshots of your project with descriptive messages
- **Branching** - Work on multiple features independently
- **Status Tracking** - See what's changed, staged, or untracked
- **Diff Viewer** - View file differences with syntax highlighting
- **History** - Browse through past commits
- **Reset** - Undo commits or unstage files

## Quick Start

**Prerequisites:** Java 17+, Maven 3.8+

```bash
# Build the project
mvn clean package
```

This creates `target/gitter.jar` - your version control system is ready!

## Setup

Make Gitter available system-wide so you can use it in any directory:

```bash
# Add to your ~/.bashrc or ~/.zshrc
export PATH="$PATH:/path/to/gitter"

# Make the wrapper executable
chmod +x /path/to/gitter/gitter

# Reload your shell
source ~/.bashrc  # or source ~/.zshrc

# Now use gitter anywhere
cd ~/my-project
gitter init
```

## Usage Guide

### Basic Workflow

```bash
# 1. Initialize a repository
gitter init

# 2. Make changes to your files
echo "Hello World" > file.txt

# 3. Stage files for commit
gitter add file.txt

# 4. Check what's staged
gitter status

# 5. Commit your changes
gitter commit -m "Add hello world file"

# 6. View commit history
gitter log
```

### Initialize a Repository

```bash
gitter init
```

Creates a `.gitter` directory to store version control data.

**Note:** All gitter commands work from any subdirectory within your project. Paths are automatically normalized relative to the repository root, so you can run commands from anywhere inside your project.

### Stage Files

```bash
# Stage a single file
gitter add file.txt

# Stage multiple files
gitter add file1.txt file2.txt file3.txt

# Stage with glob patterns
gitter add *.java
gitter add src/*.txt

# Stage entire directory
gitter add src/

# Stage all files
gitter add .

# Stage a deleted file
rm file.txt
gitter add file.txt
```

**Ignoring Files:** Create a `.gitterignore` file in your repository root to exclude files from version control. List exact file names or directory paths, one per line (e.g., `target/`, `node_modules/`, `.DS_Store`).

### Commit Changes

```bash
# Commit with a message
gitter commit -m "Add new feature"

# Commit with multiple message lines
gitter commit -m "Title" -m "Description paragraph 1" -m "Paragraph 2"

# Auto-stage modified and deleted tracked files (excludes new files)
gitter commit -a -m "Quick commit"
```

### View Status

```bash
gitter status
```

Shows three categories:
- **Changes to be committed** - Staged files (new, modified, deleted)
- **Changes not staged for commit** - Modified or deleted files
- **Untracked files** - Files not in version control

Empty categories are hidden.

### View Differences

```bash
gitter diff
```

Shows unstaged changes for all modified and deleted files in unified diff format:
- **Red lines**: Deletions
- **Green lines**: Additions
- **Cyan lines**: Hunk headers with line numbers

### View History

```bash
gitter log
```

Shows up to 10 most recent commits with:
- Commit hash (40-character SHA-1)
- Author information
- Date and time
- Commit message

### Work with Branches

```bash
# Create and switch to a new branch
gitter checkout -b feature-branch

# Switch to an existing branch
gitter checkout main
```

**Note:** Requires a clean working tree (no uncommitted changes).

### Reset Changes

```bash
# Unstage a specific file
gitter reset file.txt

# Unstage with patterns
gitter reset *.java

# Unstage all files
gitter reset

# Undo last commit (keeps changes in working directory)
gitter reset HEAD~1

# Undo last 2 commits
gitter reset HEAD~2
```

## Commands Reference

| Command | Description |
|---------|-------------|
| `gitter init` | Initialize a new repository |
| `gitter add <pathspec>...` | Stage files for commit |
| `gitter commit -m <msg>` | Create a new commit |
| `gitter commit -a -m <msg>` | Auto-stage and commit tracked files |
| `gitter status` | Show working tree status |
| `gitter diff` | Show unstaged changes |
| `gitter log` | Show commit history |
| `gitter reset [<commit>]` | Reset to a specific commit |
| `gitter reset [<pathspec>...]` | Unstage files |
| `gitter checkout [-b] <branch>` | Switch or create branches |

## Implementation & Design

### Architecture Overview

Gitter follows a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────┐
│     Commands (CLI Interface)        │  ← Picocli framework
├─────────────────────────────────────┤
│  Options → Strategy (Pattern)       │  ← Command execution logic
├─────────────────────────────────────┤
│  Utils (Core functionality)         │  ← Object storage, indexing, etc.
├─────────────────────────────────────┤
│  Models (Domain objects)            │  ← Commit, FileEntry, etc.
└─────────────────────────────────────┘
```

### Design Patterns

**1. Strategy Pattern**
- Each command has multiple strategies (e.g., `StandardCommitStrategy`, `StageAllCommitStrategy`)
- Allows different execution paths based on options
- Makes commands extensible without modifying core logic

**2. Builder Pattern**
- Used for constructing command options objects
- Provides clean, readable API for option creation

**3. Template Method Pattern**
- `AbstractCommitStrategy` and `AbstractCheckoutStrategy` define common workflow
- Subclasses override specific steps
- Ensures consistent behavior across variants

### Key Components

**Commands Package**
- Organized by feature (add, commit, checkout, etc.)
- Each command has: Command class, Options class, Strategy class(es)
- Picocli annotations define CLI interface

**Utils Package**
- `ObjectStore`: Content-addressable storage with SHA-1 hashing
- `Indexing`: Manages staging area operations
- `RepositoryState`: Queries current repository state
- `OutputFormatter`: Centralized output formatting
- `FileUtils`: File system operations with path normalization

**Models Package**
- `Commit`: Represents a commit with metadata and file mappings
- `FileEntry`: Represents a staged file with hash
- `WorkingDirectoryStatus`: Categorizes file states
- `ObjectContent`: Generic storage object representation

### Storage Design

**Content-Addressable Storage**
- Files stored by SHA-1 hash of content
- Sharded into directories using first 2 hash characters
- Format: `objects/ab/cdef123...`

**Object Format**
```
type size\0content
```

**Index (Staging Area)**
- Plain text file: `.gitter/index`
- Format: `path hash` per line
- Represents full snapshot of tracked files

Example:
```
src/App.java 3a1f5c8d9e2b4a7c6f1e0d9c8b7a6f5e4d3c2b1a
README.md 7b2e8f3c1a9d4e6b8c5f2a1e9d7c4b6a8f3e1c2d
pom.xml 9d4f6a2c8e1b7a3f5c9e2d1a8b6c4e7f3a9d1c2e
```

**Commit Format**
```
message: <commit message>
timestamp: <ISO 8601 timestamp>
parent: <parent commit hash>
files:
<path> <hash>
<path> <hash>
...
```

### Extensibility Points

**Adding New Commands**
1. Create command package under `commands/`
2. Implement `<Command>Command.java` with Picocli annotations
3. Create `<Command>Options.java` with builder
4. Implement `CommandStrategy<Options>` interface
5. Register in `App.java` subcommands

**Adding New Options to Existing Commands**
1. Add field to Options class with Picocli annotation
2. Modify or create new Strategy to handle option
3. Update Options.getStrategy() to route appropriately

**Custom Output Formatters**
- All output goes through `OutputFormatter`
- Modify methods to change display format
- Centralized location for all UI changes

## Testing

### Run Tests

```bash
mvn test
```

### Test Coverage

- **142 unit tests** across 17 test files
- 100% pass rate
- Comprehensive coverage of:
  - **Models** - Commit serialization, file entries, working directory status
  - **Utils** - Object storage, SHA-1 hashing, index operations, file utilities, ignore patterns, repository state
  - **Command Options** - Validation and routing logic for all 8 commands

## Project Structure

```
gitter/
├── src/main/java/com/example/gitter/
│   ├── commands/           # Command implementations
│   │   ├── add/           # Add command with strategy
│   │   ├── checkout/      # Checkout with multiple strategies
│   │   ├── commit/        # Commit with standard/stage-all strategies
│   │   ├── diff/          # Diff command
│   │   ├── init/          # Init command
│   │   ├── log/           # Log command
│   │   ├── reset/         # Reset with commit/file strategies
│   │   ├── status/        # Status command
│   │   └── strategy/      # CommandStrategy interface
│   ├── constants/         # Centralized constants
│   ├── models/            # Domain models
│   ├── utils/             # Core functionality
│   └── App.java           # Application entry point
├── src/test/java/         # Unit tests
├── pom.xml                # Maven configuration
└── gitter                 # Wrapper script
```

## License

Educational project for learning purposes.
