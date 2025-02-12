# Dress Den - Fashion E-commerce App

Dress Den is a modern Android e-commerce application built with the latest Android development tools and best practices. The app provides a seamless shopping experience for fashion enthusiasts.

## Features

- **Authentication**
  - Email/Password Sign In
  - Social Media Integration
  - Password Reset
  - User Profile Management

- **Shopping Experience**
  - Product Categories
  - New Arrivals
  - Popular Items
  - Wishlist Management
  - Shopping Cart
  - Secure Checkout

- **Product Features**
  - Detailed Product Views
  - Size and Color Selection
  - Product Reviews and Ratings
  - Related Products
  - Share Products

- **User Features**
  - Order History
  - Order Tracking
  - Saved Addresses
  - Payment Methods
  - Notifications

## Technical Stack

- **Architecture**
  - MVVM Architecture Pattern
  - Clean Architecture Principles
  - Repository Pattern
  - Single Activity Pattern
  - Navigation Component

- **Libraries & Frameworks**
  - Kotlin
  - Android Jetpack Components
  - Hilt for Dependency Injection
  - Room Database
  - Retrofit for Networking
  - Firebase Authentication
  - Firebase Cloud Messaging
  - WorkManager
  - Glide for Image Loading

- **Testing**
  - Unit Tests
  - Integration Tests
  - UI Tests with Espresso

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/dressden/
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   ├── model/
│   │   │   │   ├── remote/
│   │   │   │   └── repository/
│   │   │   ├── di/
│   │   │   ├── service/
│   │   │   ├── ui/
│   │   │   │   ├── auth/
│   │   │   │   ├── cart/
│   │   │   │   ├── home/
│   │   │   │   └── profile/
│   │   │   ├── utils/
│   │   │   └── work/
│   │   └── res/
│   └── test/
└── build.gradle
```

## Setup Instructions

1. Clone the repository
```bash
git clone https://github.com/yourusername/dress-den.git
```

2. Add your `google-services.json` file to the app directory

3. Update the API base URL in `build.gradle`:
```gradle
buildConfigField "String", "API_BASE_URL", "\"https://your-api-url.com/\""
```

4. Build and run the project
```bash
./gradlew assembleDebug
```

## Development Guidelines

- Follow the MVVM architecture pattern
- Use Kotlin coroutines for asynchronous operations
- Write unit tests for repositories and ViewModels
- Follow Material Design guidelines for UI components
- Use data binding for view binding
- Handle configuration changes appropriately
- Implement proper error handling
- Follow clean code principles

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For any queries, please reach out to:
- Email: support@dressden.com
- Website: www.dressden.com

## Acknowledgments

- Android Development Team
- Firebase Team
- All contributors and testers
