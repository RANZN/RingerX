# RingerX

RingerX is an Android application that allows you to automatically schedule changes to your phone's ringer mode. Set specific times to switch between Normal, Vibrate, and Silent modes, ensuring you never miss an important call or get disturbed at the wrong time.

## Features

*   Schedule unlimited ringer mode changes.
*   Intuitive UI for adding, viewing, and deleting schedules.
*   Automatically switches between Normal, Vibrate, and Silent modes.
*   Intelligently handles Do Not Disturb (DND) settings.
*   Lightweight and efficient, using the Xposed Framework for seamless integration.

## How it Works

The application consists of two main parts:

1.  **The User Interface:** A standard Android app where you can create, manage, and delete your ringer schedules. These schedules are saved locally on your device.
2.  **The Xposed Module:** This module hooks into the Android SystemUI's clock. Every minute, it checks if a scheduled event matches the current time. If a match is found, it adjusts the device's ringer mode accordingly. The module reads the schedules from the main application via a Content Provider.

## Requirements

*   An Android device.
*   Root access.
*   The Xposed Framework (or a compatible variant like LSPosed) installed and active.

## Building

1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Build the project using the standard 'Build > Make Project' or by running the `assemble` Gradle task.
4.  Install the generated APK on your device.
5.  Activate the module in the Xposed Manager and reboot your device.

## License

This project is licensed under the MIT License - see the LICENSE.md file for details.
