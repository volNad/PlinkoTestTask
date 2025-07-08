# WebView & Stub Switcher Android App

> **Technical Task — Mobile App Developer Assignment**

A clean and efficient Android app that embeds a dynamic WebView with a stub (placeholder) fallback, controlled remotely via Firebase Remote Config. Designed to seamlessly switch content modes and bypass app store moderation as needed.

---

## 🎯 Project Objective

Develop a **whitepage app** that loads external URLs inside a WebView and supports toggling between WebView and a stub menu using remote configuration, enabling flexible content delivery without app updates.

---

## 🛠️ Features

- ✅ **Dynamic WebView Integration**  
  Loads URLs specified remotely, supporting diverse landing pages and tools.

- ✅ **Stub / Menu Switch**  
  Switch between WebView content and a stub menu dynamically based on Remote Config values.

- ✅ **Firebase Remote Config Keys**  
  - `currentContent`: The current URL loaded in the WebView  
  - `entryNavigation`: Defines the app entry point (`menu` for game menu, `content` for WebView)

- ✅ **Modern Android Architecture**  
  - XML-based layouts  
  - Navigation Components for smooth in-app navigation  
  - Material UI Components for consistent styling  
  - Custom animations and transitions for polished UX

---

## 🔧 Tech Stack

- Kotlin & Android SDK  
- Jetpack Navigation Component  
- Firebase Remote Config  
- Material Design Components  
- XML Layouts & ViewBinding  
- Custom Animations (AnimatorSet, ObjectAnimator)

---

### 🔑 Access & Testing Instructions

To test different configurations remotely via Firebase Remote Config, please **send me your email address** linked to your Google account.  
I will add you as a tester with permissions to update Remote Config parameters, allowing you to switch between the WebView and stub modes dynamically in the test app.

### 🔄 Remote Config Fetch Behavior

The app fetches updated Remote Config values **at most once per hour** or whenever the app’s cache is cleared.  
This means changes to `currentContent` or `entryNavigation` may take up to an hour to reflect on the device unless the cache is manually cleared.

## 📝 Notes

- This app is deliberately designed to work effectively against Google’s static analysis mechanisms to bypass moderation.
- The implementation uses proven techniques to dynamically switch content and minimize detection, ensuring smooth operation on the Play Store.
- Further optimizations are possible, but the current solution reliably meets the technical task requirements.
