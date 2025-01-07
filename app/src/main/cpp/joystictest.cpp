// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("joystic");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("joystic")
//      }
//    }
#include <jni.h>
#include <fcntl.h>
#include <unistd.h>
#include <linux/input.h>
#include <linux/uinput.h>
#include <string.h>
#include <stdio.h>
#include <errno.h> // Include errno for error reporting


#define UINPUT_DEVICE "/dev/uinput"

static int uinput_fd = -1;

extern "C" {

JNIEXPORT jint JNICALL Java_com_example_joystictest_GamepadUtils_initUinput(JNIEnv *env, jobject obj) {
    uinput_fd = open(UINPUT_DEVICE, O_WRONLY | O_NONBLOCK);
    if (uinput_fd == -1) {
        fprintf(stderr, "Failed to open uinput device: %s\n", strerror(errno)); // Log the error
        return -1; // Failed to open
    }

    // Enable key event
    if (ioctl(uinput_fd, UI_SET_EVBIT, EV_KEY) < 0) {
        perror("ioctl");
        return 1;
    }

    // Enable gamepad buttons (example: A, B, X, Y)
    if (ioctl(uinput_fd, UI_SET_KEYBIT, BTN_A) < 0 ||
        ioctl(uinput_fd, UI_SET_KEYBIT, BTN_B) < 0 ||
        ioctl(uinput_fd, UI_SET_KEYBIT, BTN_X) < 0 ||
        ioctl(uinput_fd, UI_SET_KEYBIT, BTN_Y) ||
        ioctl(uinput_fd, UI_SET_KEYBIT, BTN_LEFT) ||
        ioctl(uinput_fd, UI_SET_KEYBIT, BTN_RIGHT) ||
        ioctl(uinput_fd, UI_SET_KEYBIT, BTN_SELECT) ||
        ioctl(uinput_fd, UI_SET_KEYBIT, BTN_START) < 0) {
        perror("ioctl");
        return 1;
    }

    // Enable absolute axis events
    if (ioctl(uinput_fd, UI_SET_EVBIT, EV_ABS) < 0) {
        perror("ioctl");
        return 1;
    }

    // Enable gamepad axes (example: X, Y, Z, RX, RY, RZ)
    if (ioctl(uinput_fd, UI_SET_ABSBIT, ABS_X) < 0 ||
        ioctl(uinput_fd, UI_SET_ABSBIT, ABS_Y) < 0 ||
        ioctl(uinput_fd, UI_SET_ABSBIT, ABS_Z) < 0 ||
        ioctl(uinput_fd, UI_SET_ABSBIT, ABS_RX) < 0 ||
        ioctl(uinput_fd, UI_SET_ABSBIT, ABS_RY) < 0 ||
        ioctl(uinput_fd, UI_SET_ABSBIT, ABS_RZ) < 0) {
        perror("ioctl");
        return 1;
    }

    struct uinput_user_dev uidev;
    memset(&uidev, 0, sizeof(struct uinput_user_dev));

    // Set device name, input event types, etc.
    strncpy(uidev.name, "Virtual Gamepad", UINPUT_MAX_NAME_SIZE);
    uidev.id.bustype = BUS_USB;
    uidev.id.vendor = 0x1234;
    uidev.id.product = 0x5678;
    uidev.id.version = 1;

    if (write(uinput_fd, &uidev, sizeof(struct uinput_user_dev)) < 0) {
        close(uinput_fd);
        return -1; // Failed to write
    }

    if (ioctl(uinput_fd, UI_DEV_CREATE) < 0) {
        close(uinput_fd);
        return -1; // Failed to create device
    }

    return 0; // Success
}

JNIEXPORT void JNICALL Java_com_example_joystictest_GamepadUtils_closeUinput(JNIEnv *env, jobject obj) {
    if (uinput_fd != -1) {
        ioctl(uinput_fd, UI_DEV_DESTROY);
        close(uinput_fd);
    }
}

JNIEXPORT jint JNICALL Java_com_example_joystictest_GamepadUtils_sendGamepadEvent(JNIEnv *env, jobject obj, jint type, jint code, jint value) {
    if (uinput_fd == -1) {
        return -1; // Device not initialized
    }

    struct input_event ev;
    memset(&ev, 0, sizeof(struct input_event));

    ev.type = type;
    ev.code = code;
    ev.value = value;

    if (write(uinput_fd, &ev, sizeof(struct input_event)) < 0) {
        return -1; // Failed to write
    }

    // Send event and sync
    ev.type = EV_SYN;
    ev.code = SYN_REPORT;
    ev.value = 0;

    if (write(uinput_fd, &ev, sizeof(struct input_event)) < 0) {
        return -1; // Failed to sync
    }

    return 0; // Success
}

}