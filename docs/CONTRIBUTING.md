# Contributing to SyncChats

## 🤝 Welcome Contributors

Thank you for your interest in contributing to SyncChats! We appreciate all forms of contributions, whether it's bug reports, feature suggestions, documentation improvements, or code contributions.

## 🚀 Getting Started

### Prerequisites
- **Java 17** or higher
- **Gradle 8.10** or higher
- **Git** for version control
- **IDE** (IntelliJ IDEA recommended)

### Setting Up Development Environment

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/SyncChats.git
   cd SyncChats
   ```
3. **Set up the upstream remote**:
   ```bash
   git remote add upstream https://github.com/gonzyui/SyncChats.git
   ```
4. **Build the project**:
   ```bash
   ./gradlew clean build
   ```

## 🐛 Reporting Issues

### Bug Reports
When reporting bugs, please include:
- **Plugin version** and Minecraft server version
- **Detailed description** of the issue
- **Steps to reproduce** the problem
- **Expected vs actual behavior**
- **Server logs** or error messages
- **Configuration files** (remove sensitive information)

### Feature Requests
For feature requests, please provide:
- **Clear description** of the requested feature
- **Use case** explaining why this feature would be valuable
- **Detailed specifications** of how it should work
- **Examples** of similar features in other plugins (if applicable)

## 💻 Code Contributions

### Code Style
- **Package structure**: Follow `xyz.gonzyui.syncchats.*` pattern
- **Kotlin conventions**: Use standard Kotlin coding conventions
- **No comments**: Keep code clean without comments (self-documenting)
- **Consistent naming**: Use descriptive variable and function names

### Pull Request Process

1. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following the code style guidelines

3. **Test your changes**:
   ```bash
   ./gradlew clean build
   ```

4. **Commit your changes**:
   ```bash
   git commit -m "Add: descriptive commit message"
   ```

5. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a pull request** on GitHub

### Commit Message Format
Use clear, descriptive commit messages:
- `Add: new feature description`
- `Fix: bug description`
- `Update: component description`
- `Remove: removed feature description`

## 📚 Documentation

### Documentation Updates
- Update relevant documentation when changing functionality
- Ensure all new features are documented
- Keep documentation clear and concise
- Use English for all documentation

### README Updates
If your changes affect:
- Installation process
- Configuration options
- Available commands
- Requirements

Please update the README.md accordingly.

## 🧪 Testing

### Manual Testing
- Test your changes thoroughly
- Test with different Minecraft versions (if applicable)
- Test with different Discord configurations
- Verify backward compatibility

### Performance Testing
- Ensure changes don't negatively impact performance
- Test with high message volumes
- Monitor memory usage
- Check for resource leaks

## 🔍 Code Review Process

### What We Look For
- **Code quality**: Clean, readable, maintainable code
- **Functionality**: Feature works as intended
- **Performance**: No performance regressions
- **Compatibility**: Works with target Minecraft versions
- **Documentation**: Appropriate documentation updates

### Review Timeline
- We aim to review pull requests within **7 days**
- Complex changes may take longer
- We may request changes or clarifications
- Be patient and responsive to feedback

## 📋 Issue Labels

We use the following labels to organize issues:
- `bug`: Something isn't working
- `enhancement`: New feature or request
- `documentation`: Documentation improvements
- `good first issue`: Good for newcomers
- `help wanted`: Extra attention needed
- `duplicate`: Duplicate of existing issue
- `wontfix`: This will not be worked on

## 🎯 Current Focus Areas

We're currently interested in contributions for:
- **Bug fixes**: Any bugs you encounter
- **Performance optimizations**: Improving plugin efficiency
- **Documentation improvements**: Better guides and examples
- **Code quality**: Refactoring and cleanup

## 🚫 What We're Not Looking For

Currently, we're **not accepting** contributions for:
- **Major feature additions** (unless discussed first)
- **Architecture changes** (plugin is feature-complete)
- **Dependencies updates** (unless critical security fixes)
- **Code style changes** (current style is established)

## 🤔 Questions?

If you have questions about contributing:
- **Open an issue** with the `question` label
- **Check existing issues** for similar questions
- **Review the documentation** in the `docs/` folder

## 📜 License

By contributing to SyncChats, you agree that your contributions will be licensed under the MIT License.

## 🙏 Recognition

All contributors will be:
- **Acknowledged** in release notes
- **Added** to the contributors list
- **Appreciated** for their valuable contributions

---

**Thank you for contributing to SyncChats!** 🎉

*Last updated: July 14, 2025*