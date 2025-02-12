# Contributing to Dress Den

First off, thank you for considering contributing to Dress Den! It's people like you that make Dress Den such a great tool.

## Code of Conduct

This project and everyone participating in it is governed by the Dress Den Code of Conduct. By participating, you are expected to uphold this code.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the issue list as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

* Use a clear and descriptive title
* Describe the exact steps which reproduce the problem
* Provide specific examples to demonstrate the steps
* Describe the behavior you observed after following the steps
* Explain which behavior you expected to see instead and why
* Include screenshots if possible
* Include your environment details (OS, device, app version)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* Use a clear and descriptive title
* Provide a step-by-step description of the suggested enhancement
* Provide specific examples to demonstrate the steps
* Describe the current behavior and explain which behavior you expected to see instead
* Explain why this enhancement would be useful
* List some other applications where this enhancement exists, if applicable

### Pull Requests

* Fork the repo and create your branch from `main`
* If you've added code that should be tested, add tests
* If you've changed APIs, update the documentation
* Ensure the test suite passes
* Make sure your code lints
* Issue that pull request!

## Development Process

1. Fork the repository
2. Create a new branch for your feature/fix
3. Write your code
4. Write tests for your code
5. Run the test suite
6. Push your changes
7. Create a Pull Request

### Coding Style

* Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
* Use meaningful variable and function names
* Write clear comments for complex logic
* Keep functions small and focused
* Use proper indentation
* Follow MVVM architecture pattern
* Use dependency injection with Hilt
* Write unit tests for repositories and ViewModels

### Git Commit Messages

* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests liberally after the first line
* Consider starting the commit message with an applicable emoji:
    * 🎨 `:art:` when improving the format/structure of the code
    * 🐎 `:racehorse:` when improving performance
    * 🚱 `:non-potable_water:` when plugging memory leaks
    * 📝 `:memo:` when writing docs
    * 🐛 `:bug:` when fixing a bug
    * 🔥 `:fire:` when removing code or files
    * 💚 `:green_heart:` when fixing the CI build
    * ✅ `:white_check_mark:` when adding tests
    * 🔒 `:lock:` when dealing with security
    * ⬆️ `:arrow_up:` when upgrading dependencies
    * ⬇️ `:arrow_down:` when downgrading dependencies

### Documentation

* Update the README.md with details of changes to the interface
* Update the CHANGELOG.md with a note describing your changes
* Update any relevant documentation in the /docs directory

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/dressden/
│   │   │   ├── data/           # Data layer
│   │   │   ├── di/            # Dependency injection
│   │   │   ├── ui/            # UI layer
│   │   │   ├── utils/         # Utilities
│   │   │   └── work/          # Background work
│   │   └── res/               # Resources
│   └── test/                  # Unit tests
└── build.gradle
```

## Testing

* Write unit tests for all new code
* Ensure all tests pass before submitting a PR
* Follow the testing conventions in the existing codebase
* Use MockK for mocking in tests
* Test both success and failure scenarios

## Questions?

Feel free to contact the project maintainers if you have any questions.

## License

By contributing, you agree that your contributions will be licensed under its MIT License.
