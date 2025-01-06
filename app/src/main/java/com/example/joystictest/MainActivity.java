package com.example.joystictest;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        textView = findViewById(R.id.textView);

        // 设置一个触摸事件监听器，监听手柄按钮按下
        findViewById(R.id.rootView).setOnGenericMotionListener((v, e) -> {
            int sources = e.getSource();
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                int action = e.getAction();
                Toast.makeText(this, "Gamepad Button Pressed, Action: " + action, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getGameControllerIds();
    }

    // 监听键盘按键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String str = "";
        int sources = event.getSource();
        if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) || ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
            int action = event.getAction();
            str = "Gamepad Button Pressed, Action: " + action + "\r\n";
            Toast.makeText(this, "Gamepad Button Pressed, Action: " + action, Toast.LENGTH_SHORT).show();
        } else {
            str = "Gamepad Button Not Pressed.\r\n";
        }

        // 检查按键是否为 keyCode 为 0
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            Toast.makeText(this, "KeyCode is 0 (Unknown key)", Toast.LENGTH_SHORT).show();
            str += "KeyCode is 0 (Unknown key)\r\n";
        } else {
            Toast.makeText(this, "KeyCode: " + keyCode, Toast.LENGTH_SHORT).show();
            str += "KeyCode: " + keyCode + "\r\n";
        }
        textView.setText(str);
        return super.onKeyDown(keyCode, event);
    }


    // 监听游戏手柄（joystick）的事件
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // 监听游戏手柄的事件
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
            float x = event.getAxisValue(MotionEvent.AXIS_X);
            float y = event.getAxisValue(MotionEvent.AXIS_Y);

            // 在屏幕上显示摇杆的移动
            textView.setText("Joystick X: " + x + ", Y: " + y);
            Toast.makeText(this, "Joystick moved! X: " + x + ", Y: " + y, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    public ArrayList<Integer> getGameControllerIds() {
        String str = "";
        ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();
            String des = dev.getDescriptor();
            String name = dev.getName();
            int vendorId = dev.getVendorId();
            int productId = dev.getProductId();
            int controllerNumber = dev.getControllerNumber();
            int keyboardType = dev.getKeyboardType();
            str += "Device ID: " + deviceId + ",keyboardType: " + keyboardType + ", sources: " + sources + ", Name: " + name + ", Descriptor: " + des + ", Vendor ID: " + vendorId + ", Product ID: " + productId + ", Controller Number: " + controllerNumber + "\r\n->" + dev.toString() +"\r\n\r\n";

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)|| ((sources & InputDevice.SOURCE_JOYSTICK)== InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        str += "\r\nGame Controller Device IDs: ";
        for (int i = 0; i < gameControllerDeviceIds.size(); i++) {
            str += gameControllerDeviceIds.get(i) + ", ";
        }
        textView.setText(str);
        return gameControllerDeviceIds;
    }
}
