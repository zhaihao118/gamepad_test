package com.example.joystictest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joystictest.service.GamepadService;
import com.example.joystictest.service.SpringBootService;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private TextView textView;
    private Button btnKey;
    private Button btnJoy;

    ScrollView scrollView;

    public static final GamepadUtils controller = new GamepadUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SpringBootService.class);
        startService(intent);
//        // Set the activity to be floating and transparent
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//        );
//
//        // Set the layout parameters to make the window float above all windows
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        getWindow().setAttributes(params);

        setContentView(R.layout.main_activity);

        textView = findViewById(R.id.textView);
        btnKey = findViewById(R.id.btn_key);
        btnJoy = findViewById(R.id.btn_joy);
        scrollView = findViewById(R.id.scrollView);

        btnKey.setOnClickListener(v -> {
            controller.pressButton(30);
            controller.sendGamepadEvent(KeyEvent.KEYCODE_DPAD_CENTER, MotionEvent.AXIS_X, 100);
            controller.releaseButton(30);
        });

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
        InputDevice device = event.getDevice();
        str += device.toString() + "\r\n";

        boolean gamepadButton = event.isGamepadButton(keyCode);
        str += "Is Gamepad Button: " + gamepadButton + "\r\n";

        if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) || ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
            int action = event.getAction();
            str += "Gamepad Button Pressed, Action: " + action + "\r\n";
        } else {
            str += "Gamepad Button Not Pressed.\r\n";
        }

        // 检查按键是否为 keyCode 为 0
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            str += "KeyCode is 0 (Unknown key)\r\n";
        } else {
            str += "KeyCode: " + keyCode + "\r\n";
        }
        textView.append(str);
        textView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
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
            str += "Device ID: " + deviceId + ",keyboardType: " + keyboardType + ", sources: " + sources + ", Name: " + name + ", Descriptor: " + des + ", Vendor ID: " + vendorId + ", Product ID: " + productId + ", Controller Number: " + controllerNumber + "\r\n->" + dev.toString() + "\r\n\r\n";

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) || ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.close();
    }
}
