# Credential Manager Sample App

A modern Android application demonstrating the implementation of Android's Credential Manager API with a beautiful Jetpack Compose UI.

## Features

### 🔐 Authentication Flow
- **Sign In**: Email and password authentication with credential storage
- **Sign Up**: User registration with credential management
- **Sign Out**: Secure logout with credential cleanup
- **Auto-login**: Automatic sign-in using saved credentials

### 🎨 Modern UI Design
- **Gradient Backgrounds**: Beautiful gradient designs for visual appeal
- **Material 3 Design**: Latest Material Design components and theming
- **Smooth Animations**: Fluid transitions and loading states
- **Responsive Layout**: Optimized for different screen sizes
- **Dark/Light Theme**: Automatic theme switching support

### 🛡️ Security Features
- **Credential Manager Integration**: Secure storage using Android's Credential Manager API
- **Password Validation**: Client-side password strength validation
- **Error Handling**: Comprehensive error management and user feedback
- **State Management**: Proper authentication state handling

## Technical Implementation

### Architecture
- **MVVM Pattern**: Clean separation of concerns with ViewModel
- **Jetpack Compose**: Modern declarative UI framework
- **Navigation Component**: Type-safe navigation between screens
- **StateFlow**: Reactive state management
- **Coroutines**: Asynchronous operations

### Key Components

#### Data Layer
- `User.kt` - User data models
- `AuthState.kt` - Authentication state management
- `SignInRequest.kt` & `SignUpRequest.kt` - Request models

#### Credential Management
- `CredentialManagerService.kt` - Handles all credential operations
- Integration with Android's Credential Manager API
- Secure credential storage and retrieval

#### UI Layer
- `SignInScreen.kt` - Beautiful sign-in interface
- `SignUpScreen.kt` - User registration screen
- `DashboardScreen.kt` - Main app dashboard
- `AuthNavigation.kt` - Navigation setup

#### ViewModel
- `AuthViewModel.kt` - Manages authentication state and business logic

## Screenshots

### Sign In Screen
- Gradient background with app logo
- Email and password input fields
- Loading states and error handling
- Navigation to sign-up screen

### Sign Up Screen
- User registration form
- Password confirmation validation
- Real-time validation feedback
- Navigation to sign-in screen

### Dashboard Screen
- User profile display
- Statistics cards
- Feature showcase
- Sign-out functionality

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+ (Android 7.0)
- Kotlin 2.0.21+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run on device or emulator

### Dependencies
- **Credential Manager**: `androidx.credentials:credentials:1.2.2`
- **Navigation**: `androidx.navigation:navigation-compose:2.8.4`
- **ViewModel**: `androidx.lifecycle:lifecycle-viewmodel-compose:2.9.3`
- **Compose BOM**: `androidx.compose:compose-bom:2024.09.00`

## Usage

### Sign Up
1. Launch the app
2. Tap "Don't have an account? Sign Up"
3. Fill in your details
4. Tap "Sign Up" to create account

### Sign In
1. Enter your email and password
2. Tap "Sign In"
3. Access the dashboard

### Dashboard
- View your profile information
- See app statistics
- Explore available features
- Sign out when done

## Customization

### Theming
The app uses a custom color scheme with gradient backgrounds. You can modify colors in:
- `ui/theme/Color.kt` - Color definitions
- `ui/theme/Theme.kt` - Theme configuration

### UI Components
All screens are built with reusable components that can be easily customized:
- Card designs
- Button styles
- Input field configurations
- Animation parameters

## Security Considerations

- Credentials are stored using Android's secure Credential Manager
- No sensitive data is stored in plain text
- Proper error handling prevents information leakage
- Authentication state is properly managed

## Future Enhancements

- [ ] Biometric authentication
- [ ] Social login integration
- [ ] Password strength indicator
- [ ] Remember me functionality
- [ ] Multi-factor authentication
- [ ] Account recovery
- [ ] Profile picture upload

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Android Credential Manager API documentation
- Jetpack Compose samples
- Material Design guidelines
- Android development community
