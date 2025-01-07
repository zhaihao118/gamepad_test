import socket
import tkinter as tk
from tkinter import ttk

# #define BTN_MISC 0x100
# #define BTN_0 0x100
# #define BTN_1 0x101
# #define BTN_2 0x102
# #define BTN_3 0x103
# #define BTN_4 0x104
# #define BTN_5 0x105
# #define BTN_6 0x106
# #define BTN_7 0x107
# #define BTN_8 0x108
# #define BTN_9 0x109
# #define BTN_MOUSE 0x110
# #define BTN_LEFT 0x110
# #define BTN_RIGHT 0x111
# #define BTN_MIDDLE 0x112
# #define BTN_SIDE 0x113
# #define BTN_EXTRA 0x114
# #define BTN_FORWARD 0x115
# #define BTN_BACK 0x116
# #define BTN_TASK 0x117
# #define BTN_JOYSTICK 0x120
# #define BTN_TRIGGER 0x120
# #define BTN_THUMB 0x121
# #define BTN_THUMB2 0x122
# #define BTN_TOP 0x123
# #define BTN_TOP2 0x124
# #define BTN_PINKIE 0x125
# #define BTN_BASE 0x126
# #define BTN_BASE2 0x127
# #define BTN_BASE3 0x128
# #define BTN_BASE4 0x129
# #define BTN_BASE5 0x12a
# #define BTN_BASE6 0x12b
# #define BTN_DEAD 0x12f
# #define BTN_GAMEPAD 0x130
# #define BTN_SOUTH 0x130
# #define BTN_A BTN_SOUTH
# #define BTN_EAST 0x131
# #define BTN_B BTN_EAST
# #define BTN_C 0x132
# #define BTN_NORTH 0x133
# #define BTN_X BTN_NORTH
# #define BTN_WEST 0x134
# #define BTN_Y BTN_WEST
# #define BTN_Z 0x135
# #define BTN_TL 0x136
# #define BTN_TR 0x137
# #define BTN_TL2 0x138
# #define BTN_TR2 0x139
# #define BTN_SELECT 0x13a
# #define BTN_START 0x13b
# #define BTN_MODE 0x13c
# #define BTN_THUMBL 0x13d
# #define BTN_THUMBR 0x13e
# #define BTN_DIGI 0x140
# #define BTN_TOOL_PEN 0x140
# #define BTN_TOOL_RUBBER 0x141
# #define BTN_TOOL_BRUSH 0x142
# #define BTN_TOOL_PENCIL 0x143
# #define BTN_TOOL_AIRBRUSH 0x144
# #define BTN_TOOL_FINGER 0x145
# #define BTN_TOOL_MOUSE 0x146
# #define BTN_TOOL_LENS 0x147
# #define BTN_TOOL_QUINTTAP 0x148
# #define BTN_STYLUS3 0x149
# #define BTN_TOUCH 0x14a
# #define BTN_STYLUS 0x14b
# #define BTN_STYLUS2 0x14c
# #define BTN_TOOL_DOUBLETAP 0x14d
# #define BTN_TOOL_TRIPLETAP 0x14e
# #define BTN_TOOL_QUADTAP 0x14f
# #define BTN_WHEEL 0x150
# #define BTN_GEAR_DOWN 0x150
# #define BTN_GEAR_UP 0x151

class GamepadEmulator:
    def __init__(self, master):
        self.master = master
        self.master.title("Gamepad Emulator")

        self.server_ip = '127.0.0.1'
        self.server_port = 8080
        self.socket = None

        self.create_widgets()
        self.connect_to_server()

    def create_widgets(self):
        buttons = [
            ("Press A", "304"),
            ("Press B", "305"),
            ("Press X", "307"),
            ("Press Y", "308"),
            ("Press L1", "310"),
            ("Press R1", "311"),
            ("Press L2", "312"),
            ("Press R2", "313"),
            ("Press Select", "314"),
            ("Press Start", "315"),
            ("Press L3", "317"),
            ("Press R3", "318")
        ]

        for i, (text, command) in enumerate(buttons):
            btn = ttk.Button(self.master, text=text)
            btn.bind("<ButtonPress>", lambda event, cmd=command: self.send_message("press,1,{},1\n".format(cmd)))
            btn.bind("<ButtonRelease>", lambda event, cmd=command: self.send_message("release,1,{},0\n".format(cmd)))
            btn.grid(row=i // 2, column=i % 2, padx=10, pady=10)

        self.label_x = ttk.Label(self.master, text="X Axis")
        self.label_x.grid(row=6, column=0, padx=10, pady=10)

        self.scale_x = ttk.Scale(self.master, from_=0, to=255, orient='horizontal', command=self.update_x_axis)
        self.scale_x.set(128)
        self.scale_x.grid(row=6, column=1, padx=10, pady=10)

        self.label_y = ttk.Label(self.master, text="Y Axis")
        self.label_y.grid(row=7, column=0, padx=10, pady=10)

        self.scale_y = ttk.Scale(self.master, from_=0, to=255, orient='horizontal', command=self.update_y_axis)
        self.scale_y.set(128)
        self.scale_y.grid(row=7, column=1, padx=10, pady=10)

    def connect_to_server(self):
        try:
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.connect((self.server_ip, self.server_port))
            self.socket.setblocking(True)
        except Exception as e:
            print("Error connecting to server: {}".format(e))

    def send_message(self, message):
        try:
            if self.socket:
                self.socket.send(message.encode('utf-8'))
                self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_RCVBUF, 1)  # This may force data flush
                print("Sent: {}".format(message))
            else:
                print("Socket is not connected")
        except Exception as e:
            print("Error: {}".format(e))

    def update_x_axis(self, value):
        self.send_message("axis,3,0,{}".format(int(float(value))))

    def update_y_axis(self, value):
        self.send_message("axis,3,1,{}".format(int(float(value))))
if __name__ == "__main__":
    root = tk.Tk()
    app = GamepadEmulator(root)
    root.mainloop()