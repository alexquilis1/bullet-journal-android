# 📝 Bullet Journal Android App

> A modern, feature-rich digital bullet journal app built with Kotlin and Android Architecture Components

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple.svg)
![Android](https://img.shields.io/badge/Android-API_21+-green.svg)
![Room](https://img.shields.io/badge/Database-Room-blue.svg)
![License](https://img.shields.io/badge/License-Custom-red.svg)

## 🎯 What it does

Transform your daily journaling experience with this comprehensive digital bullet journal that combines the simplicity of traditional journaling with modern mobile features:

- ✍️ **Rich Text Entries** - Create detailed journal entries with titles and descriptions
- 📸 **Visual Memories** - Attach photos from gallery or camera to your entries
- 🗓️ **Date Tracking** - Automatic date management for chronological organization
- 📱 **Intuitive Interface** - Clean, user-friendly design optimized for daily use
- 🔔 **Smart Reminders** - Daily notifications to maintain your journaling habit
- ✏️ **Edit & Delete** - Full CRUD operations with confirmation dialogs
- 💾 **Local Storage** - Secure offline storage with Room database

## ✨ Key Features

### 📖 **Journal Management**
- Create unlimited journal entries with rich content
- Edit existing entries with seamless navigation
- Delete entries with confirmation protection
- Chronological sorting (newest entries first)

### 📷 **Image Integration**
- **Camera Capture** - Take photos directly within the app
- **Gallery Selection** - Choose existing photos from device storage
- **Smart Compression** - Automatic image optimization for storage efficiency
- **Base64 Storage** - Embedded images for offline reliability

### 🎨 **User Experience**
- **Material Design** - Modern Android UI/UX standards
- **Responsive Layout** - Optimized for various screen sizes
- **Contextual Actions** - Tap to edit, long-press to delete
- **Visual Feedback** - Snackbar notifications and confirmation dialogs

### 🔔 **Habit Building**
- **Daily Reminders** - WorkManager-powered notifications
- **Customizable Timing** - Adjustable reminder schedules
- **Engagement Tracking** - Visual cues for empty journal states

## 🛠️ Technical Architecture

Built with modern Android development best practices:

- **[Kotlin](https://kotlinlang.org/)** - 100% Kotlin codebase
- **[MVVM Architecture](https://developer.android.com/jetpack/guide)** - Clean separation of concerns
- **[Room Database](https://developer.android.com/training/data-storage/room)** - Local SQLite abstraction
- **[LiveData](https://developer.android.com/topic/libraries/architecture/livedata)** - Reactive data observation
- **[ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)** - UI state management
- **[Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - Asynchronous programming
- **[WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)** - Background task scheduling
- **[RecyclerView](https://developer.android.com/guide/topics/ui/layout/recyclerview)** - Efficient list rendering

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API 21+ (Android 5.0)
- Kotlin 1.5+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/bullet-journal-android.git
   cd bullet-journal-android
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build and run**
   - Connect an Android device or start an emulator
   - Click "Run" or press `Ctrl+R`

### Required Permissions

The app requests the following permissions:
- **Camera** - For taking photos directly in the app
- **Storage** - For selecting images from gallery (handled automatically)

## 🏗️ Project Structure

```
app/src/main/java/com/example/bulletjournalapp/
├── MainActivity.kt              # Main activity with journal list
├── AddEntryActivity.kt          # Add/edit entry screen
├── JournalEntry.kt              # Data model with Room annotations
├── JournalEntryDao.kt           # Database access object
├── JournalDatabase.kt           # Room database configuration
├── JournalViewModel.kt          # MVVM ViewModel for data management
├── JournalEntryAdapter.kt       # RecyclerView adapter
└── ReminderWorker.kt            # Background notification worker
```

## 💡 Key Implementation Highlights

### **Smart Image Management**
```kotlin
// Automatic image compression and Base64 encoding
private fun compressImage(bitmap: Bitmap): Bitmap {
    val maxWidth = 1024
    val maxHeight = 1024
    // Intelligent scaling while maintaining aspect ratio
}
```

### **Modern Database Design**
```kotlin
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: String,
    val imageUri: String? = null // Base64 encoded images
)
```

### **Reactive UI Updates**
```kotlin
// LiveData ensures UI stays synchronized with database
journalViewModel.allEntries.observe(this) { entries ->
    updateUI(entries)
}
```

## 🎓 What I Learned

This project helped me master:
- **Android Architecture Components** (Room, ViewModel, LiveData)
- **Kotlin Coroutines** for asynchronous database operations
- **Image processing and storage** optimization techniques
- **WorkManager** for reliable background tasks
- **Material Design** principles and user experience
- **MVVM architecture** implementation in Android
- **Database design** for mobile applications

## 🔮 Future Enhancements

- [ ] **Cloud Sync** - Backup entries to cloud storage
- [ ] **Categories & Tags** - Organize entries with custom labels
- [ ] **Search Functionality** - Find entries by content or date
- [ ] **Export Options** - PDF or text export capabilities
- [ ] **Themes** - Dark mode and custom color schemes
- [ ] **Rich Text Formatting** - Bold, italic, lists, etc.
- [ ] **Location Tagging** - GPS coordinates for entries
- [ ] **Voice Notes** - Audio recording integration
- [ ] **Statistics** - Journaling habits and insights

## 🤝 Contributing

Contributions are welcome! Here are some ways you can help:
- Report bugs or suggest features
- Improve UI/UX design
- Add new functionality
- Optimize performance
- Write tests

Please feel free to open an issue or submit a pull request.

## 📄 License

This project is licensed under a Custom License - see the [LICENSE](LICENSE) file for details.

**Note:** Commercial use requires notification and may require permission. Contact for commercial licensing inquiries.

---

⭐ **Enjoying this app?** Give it a star and help others discover the joy of digital journaling!
