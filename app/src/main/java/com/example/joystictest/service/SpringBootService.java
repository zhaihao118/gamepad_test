package com.example.joystictest.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SpringBootService extends Service {
    private static final String TAG = "SpringBootService";
    private ServerSocket serverSocket;
    private Thread serverThread;
    private GamepadService gamepadService;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        gamepadService = new GamepadService();
        startServerSocket();
    }

    private void startServerSocket() {
        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(8080);
                Log.d(TAG, "Server socket started on port 8080");
                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    handleClientSocket(clientSocket);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error with server socket", e);
            }
        });
        serverThread.start();
    }

    private void handleClientSocket(Socket clientSocket) {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    processMessage(message);
                }
                clientSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error handling client socket", e);
            }
        }).start();
    }

    private void processMessage(String message) {
        // Assuming the message is in the format: eventType,type,code,value
        String[] parts = message.split(",");
        if (parts.length == 4) {
            GamepadEvent event = new GamepadEvent();
            event.setEventType(parts[0]);
            event.setType(Integer.parseInt(parts[1]));
            event.setCode(Integer.parseInt(parts[2]));
            event.setValue(Integer.parseInt(parts[3]));
            gamepadService.processEvent(event);
        } else {
            Log.e(TAG, "Invalid message format: " + message);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy");
        if (serverThread != null && !serverThread.isInterrupted()) {
            serverThread.interrupt();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing server socket", e);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
