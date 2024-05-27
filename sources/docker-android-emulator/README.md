# Android Emulator Docker Setup

## Commands to Run

1. **Navigate to Project Root:**

   ```bash
   cd ./project-root

2. **Run docker container with android emulator**
    ```bash
    docker run -d --network="host" -p 6080:6080 -p 5555:5555 -e EMULATOR_DEVICE="Samsung Galaxy S10" -e WEB_VNC=true --device /dev/kvm --name android-container budtmo/docker-android:emulator_11.0
3. **Verify if the container is running**
    ```bash
    docker ps

3. **Check connected devices**
    ```bash
    sudo apt install adb
    adb devices

3. **Install SDK in emulator**
    ```bash
    adb -s emulator-5554 install app-debug.apk



