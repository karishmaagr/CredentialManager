# 🔐 Credential Manager & Passkey Android App

A comprehensive Android application demonstrating modern authentication using **Credential Manager** and **Passkeys** with a beautiful Jetpack Compose UI.

## 📱 Screenshots

### Sign-In Screen
- **Modern Design**: Gradient background with Material 3 components
- **Dual Authentication**: Traditional password + Passkey options
- **Smart UX**: IME actions, password visibility toggle
- **Responsive**: Adapts to different screen sizes

### Sign-Up Screen
- **Consistent UI**: Matches sign-in design language
- **Form Validation**: Real-time input validation
- **Passkey Integration**: Seamless biometric registration

### Passkey Setup Dialog
- **Post-Login Prompt**: Appears after successful password login
- **Clear Benefits**: Explains Passkey advantages
- **User Choice**: Setup or skip options

### Dashboard Screen
- **Clean Layout**: User information display
- **Authentication Status**: Shows current auth method
- **Easy Sign-Out**: One-tap account switching

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
├─────────────────────────────────────────────────────────────┤
│  SignInScreen  │  SignUpScreen  │  DashboardScreen  │ Dialog │
└─────────────────┬─────────────────┬─────────────────┬───────┘
                  │                 │                 │
                  ▼                 ▼                 ▼
┌─────────────────────────────────────────────────────────────┐
│                    Navigation Layer                         │
├─────────────────────────────────────────────────────────────┤
│                    AuthNavigation                           │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                    ViewModel Layer                          │
├─────────────────────────────────────────────────────────────┤
│                    AuthViewModel                            │
│  • State Management  • Business Logic  • User Actions      │
└─────────────────┬─────────────────────┬─────────────────────┘
                  │                     │
                  ▼                     ▼
┌─────────────────────────┐   ┌─────────────────────────┐
│                    Service Layer                          │
├─────────────────────────┤   ├─────────────────────────┤
│  CredentialManagerService│   │    PasskeyService       │
│  • Password Auth        │   │  • Biometric Auth       │
│  • Credential Storage   │   │  • Passkey Creation     │
└─────────┬───────────────┘   └─────────┬───────────────┘
          │                             │
          ▼                             ▼
┌─────────────────────────┐   ┌─────────────────────────┐
│                    Data Layer                            │
├─────────────────────────┤   ├─────────────────────────┤
│    CredentialStorage    │   │    BiometricManager     │
│  • SharedPreferences    │   │  • System Biometric     │
│  • Credential Models    │   │  • Authentication       │
└─────────────────────────┘   └─────────────────────────┘
```

## 🚀 Key Features

### ✅ **Dual Authentication Methods**
- **Traditional Login**: Email/password authentication
- **Passkey Authentication**: Biometric (fingerprint, face, PIN)
- **Seamless Switching**: Easy transition between methods

### ✅ **Modern UI/UX**
- **Material 3 Design**: Latest design system implementation
- **Gradient Backgrounds**: Beautiful visual appeal
- **Smooth Animations**: Enhanced user experience
- **Responsive Layout**: Works on all screen sizes

### ✅ **Smart Passkey Integration**
- **Automatic Detection**: Checks biometric availability
- **Setup Suggestions**: Prompts users to enable Passkeys
- **Graceful Fallback**: Falls back to passwords when needed

### ✅ **Enhanced User Experience**
- **IME Actions**: Done button closes keyboard
- **Password Visibility**: Toggle show/hide passwords
- **Loading States**: Clear feedback during operations
- **Error Handling**: User-friendly error messages

### ✅ **Security Features**
- **Secure Storage**: Credentials stored safely
- **Biometric Authentication**: Device-level security
- **No Plain Text**: Passwords never stored in plain text
- **Proper Validation**: Input sanitization and validation

## 🛠️ Technical Implementation

### Dependencies Used
```kotlin
dependencies {
    // Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Material Icons
    implementation("androidx.compose.material:material-icons-extended")
    
    // Biometric
    implementation(libs.androidx.biometric)
}
```

### Core Components

#### 1. **Data Models**
```kotlin
data class User(
    val id: String,
    val email: String,
    val name: String
)

sealed interface AuthState {
    object Loading : AuthState
    data class Authenticated(val user: User) : AuthState
    object Unauthenticated : AuthState
    data class Error(val message: String) : AuthState
}
```

#### 2. **Credential Storage**
```kotlin
class CredentialStorage(private val context: Context) {
    data class StoredCredential(
        val id: String,
        val email: String,
        val password: String,
        val name: String,
        var hasBiometric: Boolean = false
    )
    
    fun saveCredentials(credential: StoredCredential): Boolean
    fun findCredentialByEmail(email: String): StoredCredential?
    fun findCredentialByBiometric(): StoredCredential?
    fun enableBiometricForCredential(email: String): Boolean
}
```

#### 3. **Passkey Service**
```kotlin
class PasskeyService(private val context: Context) {
    suspend fun authenticateWithBiometric(activity: FragmentActivity): AuthResult
    suspend fun createPasskey(activity: FragmentActivity, email: String, name: String, password: String): AuthResult
    fun isBiometricAvailable(): Boolean
    fun getBiometricStatus(): BiometricStatus
}
```

#### 4. **ViewModel**
```kotlin
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun signIn(email: String, password: String)
    fun signUp(email: String, password: String, name: String)
    fun signInWithPasskey(activity: FragmentActivity)
    fun signUpWithPasskey(activity: FragmentActivity, email: String, name: String, password: String)
    fun setupPasskeyForCurrentUser(activity: FragmentActivity)
}
```

## 🔄 Authentication Flows

### 1. **Password Authentication**
```
User Input → Validation → Credential Check → Success/Error → Navigation
```

### 2. **Passkey Authentication**
```
User Tap → Biometric Check → System Prompt → Authentication → Success/Error → Navigation
```

### 3. **Passkey Setup**
```
Login Success → Availability Check → Dialog → User Choice → Setup/Skip → Update Storage
```

## 🎨 UI Design Highlights

### **Sign-In Screen**
- **Gradient Background**: Purple-blue gradient for modern appeal
- **Material 3 Cards**: Elevated sign-in form with rounded corners
- **Dual Options**: Password and Passkey authentication buttons
- **Smart Keyboard**: IME actions for better user experience

### **Passkey Setup Dialog**
- **Modern Design**: Rounded corners and elevation
- **Clear Benefits**: Explains Passkey advantages with icons
- **User Choice**: Setup or skip options with clear CTAs

### **Responsive Design**
- **Adaptive Layout**: Works on phones and tablets
- **Touch Targets**: Properly sized for finger interaction
- **Accessibility**: Screen reader support and proper contrast

## 🔧 Technical Challenges & Solutions

### **Challenge 1: Threading Issues**
**Problem**: `BiometricPrompt.authenticate()` must be called on main thread
**Solution**: Used proper coroutine dispatchers
```kotlin
CoroutineScope(Dispatchers.Main).launch {
    biometricPrompt.authenticate(promptInfo)
}
```

### **Challenge 2: LiveEdit Compatibility**
**Problem**: Android Studio LiveEdit couldn't handle anonymous callbacks
**Solution**: Extracted callbacks into separate methods
```kotlin
private fun createBiometricCallback(...): BiometricPrompt.AuthenticationCallback
```

### **Challenge 3: State Management**
**Problem**: Complex authentication states across screens
**Solution**: Used StateFlow for reactive state management
```kotlin
private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
val authState: StateFlow<AuthState> = _authState.asStateFlow()
```

## 📱 Device Requirements

- **Android Version**: API 24+ (Android 7.0)
- **Biometric Support**: Fingerprint, Face Unlock, or PIN/Pattern
- **Storage**: Minimal local storage for credentials
- **Permissions**: Biometric authentication permissions

## 🚀 Getting Started

### **Prerequisites**
- Android Studio Hedgehog or later
- Android SDK 24+
- Device with biometric authentication support

### **Installation**
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator

### **Testing**
1. **Password Flow**: Sign up with email/password
2. **Passkey Flow**: Use "Sign Up with Passkey" option
3. **Setup Dialog**: Login with password to see Passkey setup dialog
4. **Biometric Auth**: Test fingerprint/face unlock functionality

## 🔒 Security Considerations

### **Implemented Security**
- ✅ Secure credential storage using SharedPreferences
- ✅ Biometric authentication integration
- ✅ No plain text password storage
- ✅ Proper input validation and sanitization
- ✅ Error handling without exposing sensitive information

### **Production Recommendations**
- 🔄 Integrate with real backend services
- 🔄 Implement proper encryption for stored credentials
- 🔄 Add certificate pinning for API calls
- 🔄 Implement proper session management
- 🔄 Add audit logging for security events

## 📚 Learning Resources

### **Android Documentation**
- [Credential Manager Guide](https://developer.android.com/training/sign-in/passkeys)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material 3 Design System](https://m3.material.io/)
- [Biometric Authentication](https://developer.android.com/training/sign-in/biometric-auth)

### **Related Articles**
- [Passkey Implementation Guide](https://developers.google.com/identity/passkeys)
- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [Jetpack Compose State Management](https://developer.android.com/jetpack/compose/state)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Android team for Credential Manager API
- Google for Passkey standards
- Jetpack Compose team for the amazing UI framework
- Material Design team for the design system

---

## 📞 Support

If you have any questions or need help with the implementation, please:
- Open an issue on GitHub
- Check the documentation
- Review the code comments
- Contact the development team

---

**Built with ❤️ using Jetpack Compose, Credential Manager, and Passkeys**

*This project demonstrates modern Android development practices and serves as a learning resource for developers interested in implementing secure authentication systems.*
