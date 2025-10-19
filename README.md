# Gitter - A Git-like Version Control System

A lightweight version control system implementation inspired by Git, built in Java. Gitter supports core Git functionality including commits, branches, staging, and more.

## Features

- ✅ **Repository Management**: Initialize repositories with `.gitter` directory
- ✅ **Staging Area**: Add files with glob patterns, directories, and exact paths
- ✅ **Commits**: Create commits with messages, auto-stage changes
- ✅ **Branching**: Create and switch between branches
- ✅ **Status Tracking**: View staged, unstaged, and untracked files
- ✅ **Diff Viewer**: Unified diff format with color coding
- ✅ **History**: View commit logs
- ✅ **Reset**: Undo commits or unstage files
- ✅ **Staged Deletions**: Track and stage deleted files

## Architecture

Gitter uses Git's proven architecture:
- **Content-addressable storage** with SHA-1 hashing
- **Object sharding** for efficient storage (objects/ab/cdef123...)
- **Unified object format** for blobs and commits
- **Index-based staging area**

## Prerequisites

- Java 17 or higher
- Maven 3.8 or higher

## Building

```bash
# Clone or navigate to the repository
cd gitter

# Build the project
mvn clean package

# The executable JAR will be created at: target/gitter.jar
```

## Installation

### Option 1: Using the wrapper script (Unix/Mac)

```bash
# Make the wrapper executable
chmod +x gitter

# Use it directly
./gitter init
./gitter add file.txt
./gitter commit -m "Initial commit"
```

### Option 2: Direct JAR execution

```bash
java -jar target/gitter.jar init
java -jar target/gitter.jar add file.txt
java -jar target/gitter.jar commit -m "Initial commit"
```

### Option 3: Create an alias

```bash
# Add to your ~/.bashrc or ~/.zshrc
alias gitter='java -jar /path/to/gitter/target/gitter.jar'
```

## Usage

### Initialize a Repository

```bash
gitter init
```

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

### Commit Changes

```bash
# Commit with a message
gitter commit -m "Add new feature"

# Commit with multiple message paragraphs
gitter commit -m "Title" -m "Description paragraph 1" -m "Paragraph 2"

# Auto-stage modified and deleted tracked files
gitter commit -a -m "Quick commit"
```

### View Status

```bash
gitter status
```

Output shows:
- **Changes to be committed**: Staged files (new, modified, deleted)
- **Changes not staged for commit**: Modified or deleted files
- **Untracked files**: Files not in version control

### View Differences

```bash
# Show diff for a specific file
gitter diff file.txt
```

Output uses unified diff format with color coding:
- Red lines: deletions
- Green lines: additions
- Cyan lines: hunk headers

### View History

```bash
gitter log
```

Shows up to 10 most recent commits with:
- Commit hash
- Author information
- Date and time
- Commit message

### Branching

```bash
# Create and switch to a new branch
gitter checkout -b feature-branch

# Switch to an existing branch
gitter checkout main

# Note: Switching branches requires a clean working tree
```

### Reset Changes

```bash
# Unstage a file
gitter reset file.txt

# Unstage multiple files with patterns
gitter reset *.java

# Reset all staged changes to HEAD
gitter reset

# Undo last commit (keep changes)
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
| `gitter commit -a -m <msg>` | Auto-stage and commit |
| `gitter status` | Show working tree status |
| `gitter diff <file>` | Show changes for a file |
| `gitter log` | Show commit history |
| `gitter reset [<commit>]` | Reset to a commit |
| `gitter reset [<pathspec>...]` | Unstage files |
| `gitter checkout [-b] <branch>` | Switch or create branches |

## Testing

### Run Unit Tests

```bash
mvn test
```

**Test Coverage:**
- **59 unit tests** across 7 test files
- Tests for all core models and utilities
- 100% pass rate
- Test files:
  - `CommitTest.java` (8 tests)
  - `FileEntryTest.java` (6 tests)
  - `HashUtilsTest.java` (5 tests)
  - `ObjectStoreTest.java` (6 tests)
  - `IndexingTest.java` (6 tests)
  - `FileUtilsTest.java` (15 tests)
  - `RepositoryStateTest.java` (13 tests)

### Manual Testing

All 8 core commands have been thoroughly tested with various scenarios:
- Pattern matching (glob patterns, directories, wildcards)
- Edge cases (deletions, modifications, new files)
- Multi-file operations
- Branch operations
- Commit history
- Status tracking

## Project Structure

```
gitter/
├── src/
│   ├── main/java/com/example/gitter/
│   │   ├── commands/        # Command implementations
│   │   │   ├── AddCommand.java
│   │   │   ├── CheckoutCommand.java
│   │   │   ├── CommitCommand.java
│   │   │   ├── DiffCommand.java
│   │   │   ├── InitCommand.java
│   │   │   ├── LogCommand.java
│   │   │   ├── ResetCommand.java
│   │   │   └── StatusCommand.java
│   │   ├── constants/       # Centralized constants
│   │   │   ├── Constants.java
│   │   │   ├── Messages.java
│   │   │   └── PathConstants.java
│   │   ├── models/          # Domain models
│   │   │   ├── Commit.java
│   │   │   ├── FileEntry.java
│   │   │   ├── ObjectContent.java
│   │   │   └── WorkingDirectoryStatus.java
│   │   ├── utils/           # Utility classes
│   │   │   ├── FileUtils.java
│   │   │   ├── HashUtils.java
│   │   │   ├── Indexing.java
│   │   │   ├── ObjectStore.java
│   │   │   ├── OutputFormatter.java
│   │   │   └── RepositoryState.java
│   │   └── Main.java        # Application entry point
│   └── test/java/com/example/gitter/
│       ├── models/          # Model tests
│       │   ├── CommitTest.java
│       │   └── FileEntryTest.java
│       └── utils/           # Utility tests
│           ├── FileUtilsTest.java
│           ├── HashUtilsTest.java
│           ├── IndexingTest.java
│           ├── ObjectStoreTest.java
│           └── RepositoryStateTest.java
├── gitter                   # Wrapper script
├── pom.xml                  # Maven configuration
└── README.md                # This file
```

## Implementation Details

### Object Storage

Gitter uses Git's content-addressable storage model:
- Objects are stored as `type size\0content`
- Files are sharded using first 2 characters of hash: `objects/ab/cdef123...`
- Supports blobs (file content) and commits

### Staging Area (Index)

- Plain text file storing: `path hash` per line
- Updated when files are added or reset
- Synchronized with commits on branch switches

### Commit Format

```
message: <commit message>
timestamp: <ISO 8601 timestamp>
parent: <parent commit hash>
files:
<path> <hash>
<path> <hash>
...
```

### Path Normalization

Commands work from subdirectories:
```bash
cd src/
gitter add file.txt    # Correctly resolves to src/file.txt
```

## Limitations

Intentional simplifications for assignment scope:

1. **Diff Command**: Single file only (no glob patterns, directories, or show-all)
2. **Checkout**: Blocks on ANY changes (Git only blocks conflicting changes)
3. **Commit References**: Only `HEAD~N` notation (no branch names, tags, or hashes)
4. **No Remote Operations**: No push, pull, fetch, or remote repositories
5. **No Merge**: Single branch workflow only
6. **No Staging Area Partials**: Can't stage partial file changes

## Statistics

- **Source Code**: ~2,800 lines
- **Test Code**: ~1,200 lines
- **Test Coverage**: 59 unit tests (100% pass rate)
- **Commands**: 8 (init, add, commit, status, log, diff, reset, checkout)
- **Build Time**: ~2 seconds
- **JAR Size**: ~5 MB (includes dependencies)

## License

This is an educational project created for learning purposes.

## Author

Created as a coding assignment to demonstrate understanding of:
- Version control system internals
- Git architecture
- Content-addressable storage
- Command-line application design
- Java development best practices


