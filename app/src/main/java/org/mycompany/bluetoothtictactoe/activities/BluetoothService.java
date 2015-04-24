package org.mycompany.bluetoothtictactoe.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.mycompany.bluetoothtictactoe.logger.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Malika Pahva (mxp134930) on 4/19/2015.
 * Course: CS6301.001
 *
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static final String NAME = "BluetoothGame";
    private static final UUID MY_UUID =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handler;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int state;
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    /**
     * This is a constructor which creates a new BluetoothChat session.
     *
     * Author : Malika Pahva (mxp134930)
     *
     * @param context
     *
     * @param handler
     */
    public BluetoothService(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.handler = handler;
    }

    /**
     * This method sets the current state of connection.
     *
     * Author : Malika Pahva (mxp134930)
     *
     * @param state
     *         the current connection state
     */
    private synchronized void setState(int state) {
        Log.debug(TAG, "setState() " + this.state + " -> " + state);
        this.state = state;
        handler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * This is a getter method which returns the
     * current connection state.
     *
     * Author : Malika Pahva (mxp134930)
     *
     */
    public synchronized int getState() {
        return state;
    }

    /**
     * This method starts the service for game. It starts
     * AcceptThread to begin a session in listening (server) mode
     *
     * Author : Malika Pahva (mxp134930)
     *
     */
    public synchronized void start() {
        Log.debug(TAG, "start");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_LISTEN);

        if (acceptThread == null) {
            acceptThread = new AcceptThread(true);
            acceptThread.start();
        }
    }

    /**
     * This method starts the ConnectThread to start a
     * connection to a remote device.
     *
     * Author : Malika Pahva (mxp134930)
     *
     * @param device
     *          device to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        Log.debug(TAG, "connect to: " + device);

        if (state == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * This method starts the ConnectedThread to manage the connection.
     *
     * Author : Malika Pahva (mxp134930)
     *
     * @param socket
     *
     * @param device
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
        Log.debug(TAG, "connected, Socket Type: Secure");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        Message msg = handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    /**
     * This method stops all the threads.
     *
     * Author : Malika Pahva (mxp134930)
     *
     */
    public synchronized void stop() {
        Log.debug(TAG, "stop");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }


        setState(STATE_NONE);
    }
    /*
    * This method writes data to connectedThread
    *
    * Author : Malika Pahva (mxp134930)
    *
    * @param dataToWrite
    */
    public void write(byte[] dataToWrite) {
        ConnectedThread thread;
        synchronized (this) {
            if (state != STATE_CONNECTED) return;
            thread = connectedThread;
        }
        thread.write(dataToWrite);
    }

    /**
     * This method specifies that the connection attempt
     * failed and notify about failure.
     *
     * Author : Malika Pahva (mxp134930)
     *
     */
    private void connectionFailed() {
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);
        BluetoothService.this.start();
    }

    /**
     * This method specifies that the connection was lost
     * and notify about this to activity.
     *
     * Author : Malika Pahva (mxp134930)
     *
     */
    private void connectionLost() {
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);
        BluetoothService.this.start();
    }

    /**
     * This Class runs the thread while listening for incoming connections.
     * It runs until a connection is accepted (or until cancelled).
     *
     * Author : Malika Pahva (mxp134930)
     *
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;
        /**
         * This is a constructor which initializes the Server socket.
         *
         * Author : Malika Pahva (mxp134930)         *
         *
         * @param secure
         */
        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            try {

                    tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME,
                            MY_UUID);

            } catch (IOException e) {
                Log.error(TAG, "Socket Type: Secure listen() failed", e);
            }
            serverSocket = tmp;
        }

        /**
         * This method runs the ConnectThread.
         *
         * Author : Malika Pahva (mxp134930)
         *
         */
        public void run() {
            Log.debug(TAG, "Socket Type: Secure" +
                    "BEGIN acceptThread" + this);
            setName("AcceptThread Secure");

            BluetoothSocket socket = null;

            while (state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.error(TAG, " accept() failed", e);
                    break;
                }
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.error(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.info(TAG, "END acceptThread");

        }

        /**
         * This method closes the socket.
         *
         * Author : Malika Pahva (mxp134930)
         *
         */
        public void cancel() {
            Log.debug(TAG, "cancel " + this);
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.error(TAG, "close() of server failed", e);
            }
        }
    }


    /**
     * This class runs the thread while listening for incoming connections.
     * It runs until a connection succeeds or fails.
     *
     * Author : Malika Pahva (mxp134930)
     *
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        /**
         * This is a constructor which initializes the device and  socket
         *
         * Author : Malika Pahva (mxp134930)
         *
         * @param device
         */
        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            BluetoothSocket tmp = null;
            try {

                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID);

            } catch (IOException e) {
                Log.error(TAG, "create() failed", e);
            }
            socket = tmp;
        }

        /**
         * This method runs the ConnectThread.
         *
         * Author : Malika Pahva (mxp134930)
         *
         */
        public void run() {
            Log.info(TAG, "BEGIN connectThread ");
            setName("ConnectThread");

            bluetoothAdapter.cancelDiscovery();
            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e2) {
                    Log.error(TAG, "unable to close() " +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            synchronized (BluetoothService.this) {
                connectThread = null;
            }

            connected(socket, device);
        }

        /**
         * This method closes the socket.
         *
         * Author : Malika Pahva (mxp134930)
         *
         */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.error(TAG, "close() of connect failed", e);
            }
        }
    }

    /**
     * This Class runs the thread during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     *
     * Author : Malika Pahva (mxp134930)
     *
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inStream;
        private final OutputStream outStream;

        /**
         * This is a constructor which initializes the socket, input
         * stream and output stream.
         *
         * Author : Malika Pahva (mxp134930)
         *
         * @param socket
         */
        public ConnectedThread(BluetoothSocket socket) {
            Log.debug(TAG, "create ConnectedThread ");
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.error(TAG, "temp sockets not created", e);
            }

            inStream = tmpIn;
            outStream = tmpOut;
        }

        /**
         * This method runs the ConnectedThread.
         *
         * Author : Malika Pahva (mxp134930)
         *
         */
        public void run() {
            Log.info(TAG, "BEGIN connectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inStream.read(buffer);

                    handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.error(TAG, "disconnected", e);
                    connectionLost();
                    BluetoothService.this.start();
                    break;
                }
            }
        }

        /**
         * This method writes to the connected OutStream.
         *
         * Author : Malika Pahva (mxp134930)
         *
         * @param dataToWrite
         *        bytes to write
         */
        public void write(byte[] dataToWrite) {
            try {
                outStream.write(dataToWrite);

                handler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, dataToWrite)
                        .sendToTarget();
            } catch (IOException e) {
                Log.error(TAG, "Exception during write", e);
            }
        }

        /**
         * This method closes the socket.
         *
         * Author : Malika Pahva (mxp134930)
         *
         */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.error(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This method returns true if devices are connected.
     *
     * Author : Malika Pahva (mxp134930)
     *
     */
    public boolean isConnected(){
        if (connectedThread !=null)
            return true;
        return false;
    }

}
