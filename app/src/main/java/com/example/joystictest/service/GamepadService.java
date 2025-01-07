package com.example.joystictest.service;

import android.util.Log;

import com.example.joystictest.MainActivity;

public class GamepadService {

    public void processEvent(GamepadEvent event) {
        switch (event.getEventType()) {
            case "press":
                MainActivity.controller.pressButton(event.getCode());
                break;
            case "release":
                MainActivity.controller.releaseButton(event.getCode());
                break;
            case "move":
                MainActivity.controller.sendGamepadEvent(event.getType(), event.getCode(), event.getValue());
                break;
            default:
                Log.w("GamepadService", "Unknown event type: " + event.getEventType());
        }
    }
}

