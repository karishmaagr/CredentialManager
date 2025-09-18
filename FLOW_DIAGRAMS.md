# Authentication Flow Diagrams

## 1. Main Authentication Flow

```
┌─────────────────┐
│   App Launch    │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ User Authenticated? │
└─────────┬───────┘
          │
    ┌─────┴─────┐
    │           │
    ▼           ▼
┌─────────┐ ┌─────────┐
│  Yes    │ │   No    │
└────┬────┘ └────┬────┘
     │           │
     ▼           ▼
┌─────────┐ ┌─────────┐
│Dashboard│ │Sign In  │
│ Screen  │ │ Screen  │
└─────────┘ └────┬────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Authentication  │
        │    Method?      │
        └─────────┬───────┘
                  │
        ┌─────────┴─────────┐
        │                   │
        ▼                   ▼
┌─────────────┐     ┌─────────────┐
│   Password  │     │   Passkey   │
│   Login     │     │   Login     │
└──────┬──────┘     └──────┬──────┘
       │                   │
       ▼                   ▼
┌─────────────┐     ┌─────────────┐
│ Valid Creds?│     │Biometric OK?│
└──────┬──────┘     └──────┬──────┘
       │                   │
   ┌───┴───┐           ┌───┴───┐
   │       │           │       │
   ▼       ▼           ▼       ▼
┌─────┐ ┌─────┐     ┌─────┐ ┌─────┐
│ Yes │ │ No  │     │ Yes │ │ No  │
└──┬──┘ └──┬──┘     └──┬──┘ └──┬──┘
   │       │           │       │
   ▼       ▼           ▼       ▼
┌─────┐ ┌─────┐     ┌─────┐ ┌─────┐
│Success│Error │     │Success│Error │
└──┬───┘ └──┬──┘     └──┬───┘ └──┬──┘
   │        │           │        │
   └────────┼───────────┼────────┘
            │           │
            └───────────┘
                   │
                   ▼
            ┌─────────────┐
            │ Passkey     │
            │ Available?  │
            └──────┬──────┘
                   │
            ┌──────┴──────┐
            │             │
            ▼             ▼
      ┌─────────┐   ┌─────────┐
      │   Yes   │   │   No    │
      └────┬────┘   └────┬────┘
           │             │
           ▼             ▼
    ┌─────────────┐ ┌─────────┐
    │Show Passkey │ │Dashboard│
    │Setup Dialog │ │ Screen  │
    └──────┬──────┘ └─────────┘
           │
           ▼
    ┌─────────────┐
    │ User Choice │
    └──────┬──────┘
           │
    ┌──────┴──────┐
    │             │
    ▼             ▼
┌─────────┐ ┌─────────┐
│ Setup   │ │  Skip   │
└────┬────┘ └────┬────┘
     │           │
     ▼           ▼
┌─────────┐ ┌─────────┐
│Create   │ │Dashboard│
│Passkey  │ │ Screen  │
└────┬────┘ └─────────┘
     │
     ▼
┌─────────┐
│Success? │
└────┬────┘
     │
┌────┴────┐
│         │
▼         ▼
┌─────┐ ┌─────┐
│ Yes │ │ No  │
└──┬──┘ └──┬──┘
   │       │
   ▼       ▼
┌─────┐ ┌─────┐
│Dashboard│Error │
└─────┘ └──┬──┘
           │
           ▼
      ┌─────────┐
      │Sign In  │
      │ Screen  │
      └─────────┘
```

## 2. Passkey Creation Flow (Sign Up)

```
User clicks "Sign Up with Passkey"
           │
           ▼
┌─────────────────────────┐
│ AuthViewModel.          │
│ signUpWithPasskey()     │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ PasskeyService.         │
│ createPasskey()         │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ BiometricPrompt.        │
│ authenticate()          │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ Show Biometric Dialog   │
│ "Use fingerprint/face"  │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ User Authenticates      │
│ (Fingerprint/Face/PIN)  │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ Authentication Success  │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ CredentialStorage.      │
│ saveCredentials()       │
│ (hasBiometric = true)   │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ Return AuthResult       │
│ (success = true)        │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ Navigate to Dashboard   │
└─────────────────────────┘
```

## 3. Passkey Authentication Flow (Sign In)

```
User clicks "Sign In with Passkey"
           │
           ▼
┌─────────────────────────┐
│ AuthViewModel.          │
│ signInWithPasskey()     │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ PasskeyService.         │
│ authenticateWithBiometric() │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ CredentialStorage.      │
│ findCredentialByBiometric() │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ Biometric Credential    │
│ Found?                  │
└─────────┬───────────────┘
          │
    ┌─────┴─────┐
    │           │
    ▼           ▼
┌─────────┐ ┌─────────┐
│  Yes    │ │   No    │
└────┬────┘ └────┬────┘
     │           │
     ▼           ▼
┌─────────┐ ┌─────────┐
│Continue │ │ Show    │
│         │ │ Error   │
└────┬────┘ └────┬────┘
     │           │
     ▼           ▼
┌─────────┐ ┌─────────┐
│Biometric│ │Sign In  │
│Prompt   │ │ Screen  │
└────┬────┘ └─────────┘
     │
     ▼
┌─────────────────────────┐
│ User Authenticates      │
│ (Fingerprint/Face/PIN)  │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ Authentication Success  │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ Return Stored User      │
│ Credentials             │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ Navigate to Dashboard   │
└─────────────────────────┘
```

## 4. Post-Login Passkey Setup Flow

```
User successfully logs in with password
           │
           ▼
┌─────────────────────────┐
│ AuthViewModel.          │
│ handleAuthResult()      │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ Check if Passkey        │
│ Available & Not Set Up  │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ Show Passkey Setup      │
│ Dialog                  │
└─────────┬───────────────┘
          │
          ▼
┌─────────────────────────┐
│ User Choice             │
└─────────┬───────────────┘
          │
    ┌─────┴─────┐
    │           │
    ▼           ▼
┌─────────┐ ┌─────────┐
│ Setup   │ │  Skip   │
└────┬────┘ └────┬────┘
     │           │
     ▼           ▼
┌─────────┐ ┌─────────┐
│Create   │ │Hide     │
│Passkey  │ │Dialog   │
└────┬────┘ └────┬────┘
     │           │
     ▼           ▼
┌─────────┐ ┌─────────┐
│Success? │ │Dashboard│
└────┬────┘ └─────────┘
     │
┌────┴────┐
│         │
▼         ▼
┌─────┐ ┌─────┐
│ Yes │ │ No  │
└──┬──┘ └──┬──┘
   │       │
   ▼       ▼
┌─────┐ ┌─────┐
│Hide │ │Show │
│Dialog│ │Error│
└──┬──┘ └──┬──┘
   │       │
   ▼       ▼
┌─────┐ ┌─────┐
│Dashboard│Sign In│
└─────┘ └──┬──┘
           │
           ▼
      ┌─────────┐
      │Sign In  │
      │ Screen  │
      └─────────┘
```

## 5. System Architecture Overview

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

## 6. State Management Flow

```
┌─────────────────┐
│   Initial State │
│   (Loading)     │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ Check Auth State│
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ User Action     │
│ (Sign In/Up)    │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ Update Loading  │
│ State (true)    │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ Call Service    │
│ Method          │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ Service Result  │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ Update Auth     │
│ State           │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ Update Loading  │
│ State (false)   │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ Update UI       │
│ (Navigate/Show) │
└─────────────────┘
```

These flow diagrams provide a comprehensive visual representation of how the authentication system works, from user interactions to system responses, making it easier to understand the complete flow of the application.
