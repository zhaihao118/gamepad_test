package com.example.joystictest;

public class GamepadUtils {

    // Load the native library
    static {
        System.loadLibrary("joystick");
    }

    // Declare native methods
    public native int initUinput();
    public native void closeUinput();
    public native int sendGamepadEvent(int type, int code, int value);

    private int deviceHandle = -1;

    public GamepadUtils() {
        deviceHandle = initUinput();
    }

    public void close() {
        closeUinput();
    }

    // 示例：发送一个按钮按下的事件
    public void pressButton(int buttonCode) {
        sendGamepadEvent(1, buttonCode, 1);  // 1代表按下
    }

    // 示例：发送一个按钮释放的事件
    public void releaseButton(int buttonCode) {
        sendGamepadEvent(1, buttonCode, 0);  // 0代表释放
    }

    // 更多事件...
}

