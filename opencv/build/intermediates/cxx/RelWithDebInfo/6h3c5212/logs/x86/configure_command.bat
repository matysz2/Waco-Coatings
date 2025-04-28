@echo off
"D:\\Programowanie\\Android SDK\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HD:\\Programowanie\\Waco2\\opencv\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=D:\\Programowanie\\Android SDK\\ndk\\26.1.10909125" ^
  "-DCMAKE_ANDROID_NDK=D:\\Programowanie\\Android SDK\\ndk\\26.1.10909125" ^
  "-DCMAKE_TOOLCHAIN_FILE=D:\\Programowanie\\Android SDK\\ndk\\26.1.10909125\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=D:\\Programowanie\\Android SDK\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=D:\\Programowanie\\Waco2\\opencv\\build\\intermediates\\cxx\\RelWithDebInfo\\6h3c5212\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=D:\\Programowanie\\Waco2\\opencv\\build\\intermediates\\cxx\\RelWithDebInfo\\6h3c5212\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=RelWithDebInfo" ^
  "-BD:\\Programowanie\\Waco2\\opencv\\.cxx\\RelWithDebInfo\\6h3c5212\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
