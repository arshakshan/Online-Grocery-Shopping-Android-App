# Grocery Delivery App

This is a grocery delivery app built using Jetpack Compose and Kotlin. Users can browse through different categories of groceries and order items of their choice. The app leverages modern Android development practices to provide a seamless user experience.

## Features

- **Browse Categories:** Users can browse groceries by categories.
- **Search Functionality:** Users can search for specific grocery items.
- **Add to Cart:** Users can add items to their cart.
- **Place Order:** Users can place an order for the items in their cart.
- **User Authentication:** Simple user authentication to manage orders and preferences.
- **Firebase Integration:** Uses Firebase for storage and authentication.

## Technologies Used

- **Kotlin:** Programming language used for Android development.
- **Jetpack Compose:** Android’s modern toolkit for building native UI.
- **MVVM Architecture:** Utilized for clear separation of concerns and ease of testing.
- **Coroutines:** For asynchronous programming.
- **LiveData & Flow:** For reactive UI updates.
- **Hilt:** For dependency injection.
- **Room:** For local database management.
- **Retrofit:** For network requests.
- **Firebase:** For authentication and storage.

## Firebase Integration

### Authentication

Firebase Authentication is used to manage user authentication. It supports email and password sign-in methods.

#### Setup

1. **Add Firebase to your Android project:**
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Create a new project or select an existing project.
   - Click on the Android icon to add an Android app to your project.
   - Follow the instructions to download the `google-services.json` file and place it in the `app` directory of your Android project.

2. **Add Firebase Authentication SDK:**
   ```gradle
   implementation 'com.google.firebase:firebase-auth:21.0.1'
   ```

3. **Initialize Firebase in your project:**
   ```kotlin
   // In your Application class or MainActivity
   FirebaseApp.initializeApp(this)
   ```

4. **Use Firebase Authentication in your code:**
   ```kotlin
   val auth: FirebaseAuth = Firebase.auth

   fun signIn(email: String, password: String) {
       auth.signInWithEmailAndPassword(email, password)
           .addOnCompleteListener(this) { task ->
               if (task.isSuccessful) {
                   // Sign in success
                   val user = auth.currentUser
               } else {
                   // Sign in failure
                   Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
               }
           }
   }

   fun signOut() {
       auth.signOut()
   }
   ```

### Storage

Firebase Storage is used to store and retrieve user data such as profile pictures and other media.

#### Setup

1. **Add Firebase Storage SDK:**
   ```gradle
   implementation 'com.google.firebase:firebase-storage:20.0.1'
   ```

2. **Use Firebase Storage in your code:**
   ```kotlin
   val storage: FirebaseStorage = Firebase.storage
   val storageRef: StorageReference = storage.reference

   fun uploadImage(filePath: Uri) {
       val ref = storageRef.child("images/${UUID.randomUUID()}")
       ref.putFile(filePath)
           .addOnSuccessListener {
               // File upload success
           }
           .addOnFailureListener {
               // File upload failure
           }
   }

   fun downloadImage(imageRef: String) {
       val ref = storageRef.child(imageRef)
       ref.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
           // Use the bytes to display the image
       }.addOnFailureListener {
           // Handle the error
       }
   }
   ```

## Getting Started

### Prerequisites

- Android Studio (latest version recommended)
- Kotlin 1.5+
- Gradle 7.0+
- Android SDK 21+
- Firebase Account

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/grocery-delivery-app.git
   cd grocery-delivery-app
   ```

2. **Open the project in Android Studio:**
   - Open Android Studio.
   - Select `File -> Open...`.
   - Navigate to the cloned project directory and select it.

3. **Build the project:**
   - Click on the `Build` menu.
   - Select `Build Project`.

4. **Run the app:**
   - Click on the `Run` menu.
   - Select `Run 'app'`.

## Project Structure

- **`app/src/main/java/com/example/flash`**: Contains the Kotlin source code.
  - **`ui`**: UI components and screens.
  - **`viewmodel`**: ViewModels for managing UI-related data.
  - **`model`**: Data classes and repository.
  - **`network`**: Retrofit setup and API service.

- **`app/src/main/res`**: Contains the resources such as layouts, strings, and drawable files.

## Screenshots

![Home Screen](screenshots/home.png)
![Category Screen](screenshots/category.png)
![Cart Screen](screenshots/cart.png)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgements

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android Developer Documentation](https://developer.android.com/docs)
- [Firebase Documentation](https://firebase.google.com/docs)

## Contact

For any inquiries or feedback, please contact [arshakshan@gmail.com](arshakshan@gmail.com).
