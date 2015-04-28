package org.mycompany.bluetoothtictactoe.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.mycompany.bluetoothtictactoe.R;
import org.mycompany.bluetoothtictactoe.model.TTTBoard;
import org.mycompany.bluetoothtictactoe.logger.Log;

/**
 * Created by Saylee Pradhan (sap140530) on 4/20/2015.
 * Course: CS6301.001
 *
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class GameFragment extends Fragment {

    private static final String TAG = "GameFragment";

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BUTTON = 3;

    private Button startGameButton;
    private Button selectZeroButton;
    private Button selectCrossButton;
    private ImageButton imageButtons[];
    private String selfSymbol;
    private String readMessage;
    private String oppSymbol;
    private boolean turn;
    private boolean flag = false;
    TTTBoard board;
    TextView gameStatus;
    private boolean[] clicked;
    private String connectedDeviceName = null;

    private StringBuffer outStringBuffer;

    private BluetoothAdapter bluetoothAdapter = null;


    private BluetoothService bluetoothService = null;

    /**
     * This method create new fragments for main screen.
     *
     * Author: Malika Pahva (mxp134930)
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    /**
     * This method enables the bluetooth adapter.
     *
     * Author:  Saylee Pradhan (sap140530)
     *
     */
    @Override
    public void onStart() {
        super.onStart();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BUTTON);
        } else if (bluetoothService == null) {
            setupGame();
        }
    }

    /**
     * This method closes the bluetooth Service.
     *
     * Author:  Saylee Pradhan (sap140530)
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }

    /**
     * This method enables the bluetooth adapter after resuming
     * the fragment.
     *
     * Author:  Saylee Pradhan (sap140530)
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        if (bluetoothService != null) {
            if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
                bluetoothService.start();
            }
        }
    }

    /**
     * This method sets up the view of the screen on creation
     * of fragment.
     *
     * Author: Malika Pahva (mxp134930)
     *
     * @param inflater
     *
     * @param container
     *
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }


    /**
     * This method populates the view with UI elements on creation.
     *
     * Author: Malika Pahva (mxp134930)
     *
     * @param view
     *
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        startGameButton = (Button) view.findViewById(R.id.button_start);
        selectCrossButton = (Button) view.findViewById(R.id.select_cross_btn);
        selectZeroButton = (Button) view.findViewById(R.id.select_zero_btn);
        imageButtons = new ImageButton[9];
        imageButtons[0] = (ImageButton) view.findViewById(R.id.btn_0_0);
        imageButtons[1] = (ImageButton) view.findViewById(R.id.btn_0_1);
        imageButtons[2] = (ImageButton) view.findViewById(R.id.btn_0_2);
        imageButtons[3] = (ImageButton) view.findViewById(R.id.btn_1_0);
        imageButtons[4] = (ImageButton) view.findViewById(R.id.btn_1_1);
        imageButtons[5] = (ImageButton) view.findViewById(R.id.btn_1_2);
        imageButtons[6] = (ImageButton) view.findViewById(R.id.btn_2_0);
        imageButtons[7] = (ImageButton) view.findViewById(R.id.btn_2_1);
        imageButtons[8] = (ImageButton) view.findViewById(R.id.btn_2_2);
        selfSymbol = "";
        oppSymbol ="";
        clicked = new boolean[9];
        resetClickedButtons();
        setupBoard();

    }

    /**
     * This method defines the behavior for UI elements on the game screen
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */


    private void setupGame() {
        Log.debug(TAG, "setupGame()");

        /**
         * This method initializes the start button with a listener that for click events
         *
         * Author: Saylee Pradhan (sap140530)

         */
        startGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetGame();
                enableButtons();
                flag = true;
                resetClickedButtons();
                sendData("newgame");
                Context context = getActivity().getApplicationContext();
                int id = BluetoothService.STATE_CONNECTED;
                if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                    Toast toast = Toast.makeText(context, "Devices not paired", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    setupBoard();
                }
            }
        });


        bluetoothService = new BluetoothService(getActivity(), handler);

        outStringBuffer = new StringBuffer("");
    }

    /**
     * This method initializes the button for selecting Zero, Cross and the buttons in the 9X9 grid
     *
     * Author: Saylee Pradhan (sap140530)

     */
    public void setupBoard() {

            board = new TTTBoard();
            selectCrossButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Context context = getActivity().getApplicationContext();
                    if (flag) {
                        if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED) {
                            if (selfSymbol.equals("")) {
                                turn = true;
                                selfSymbol = "X";
                                oppSymbol = "O";
                                sendData("O");
                                selectZeroButton.setBackgroundColor(Color.LTGRAY);
                            } else {
                                Toast toast = Toast.makeText(context, "Symbol already selected", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(context, "Devices not paired", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        Toast.makeText(context, "Select New Game", Toast.LENGTH_LONG).show();
                    }
                }
            });
            selectZeroButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Context context = getActivity().getApplicationContext();
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED) {
                        if (selfSymbol.equals("")) {
                            turn = true;
                            selfSymbol = "O";
                            oppSymbol = "X";
                            sendData("X");
                            selectCrossButton.setBackgroundColor(Color.LTGRAY);
                        } else {
                            Toast toast = Toast.makeText(context, "Symbol already selected", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    } else {
                        Toast toast = Toast.makeText(context, "Devices not paired", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        imageButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked[0]){
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED && selfSymbol != "" && turn == true && board.getSymbol(0) == ' ') {
                        clicked[0]=true;
                        if (selfSymbol.equals("X"))
                            imageButtons[0].setImageResource(R.drawable.cross_image);
                        else
                            imageButtons[0].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(selfSymbol.charAt(0), 0);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            gameStatus.setText("Opponent's Turn");
                        sendData("0:move");
                    } else if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    }
                    else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (selfSymbol == "") {
                        selectSymbolPrompt();
                    }
                    if (board.noWinner()){
                        disableButtons();
                        sendData("disable");
                        gameStatus.setText("Nobody Won!");
                    }
                }
                else {
                    displayClicked();
                }
            }
        });

        imageButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked[1]){
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED && selfSymbol != "" && turn == true && board.getSymbol(1) == ' ') {
                        clicked[1]=true;
                        if (selfSymbol.equals("X"))
                            imageButtons[1].setImageResource(R.drawable.cross_image);
                        else
                            imageButtons[1].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(selfSymbol.charAt(0), 1);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            gameStatus.setText("Opponent's Turn");
                        sendData("1:move");
                    } else if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    }
                    else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (selfSymbol == "") {
                        selectSymbolPrompt();
                    }
                    if (board.noWinner()){
                        disableButtons();
                        sendData("disable");
                        gameStatus.setText("Nobody Won!");
                    }
                }
                else {
                    displayClicked();
                }
            }
        });
        imageButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked[2]){
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED && selfSymbol != "" && turn == true && board.getSymbol(2) == ' ') {
                        clicked[2]=true;
                        if (selfSymbol.equals("X"))
                            imageButtons[2].setImageResource(R.drawable.cross_image);
                        else
                            imageButtons[2].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(selfSymbol.charAt(0), 2);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            gameStatus.setText("Opponent's Turn");
                        sendData("2:move");
                    } else if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    }
                    else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (selfSymbol == "") {
                        selectSymbolPrompt();
                    }
                    if (board.noWinner()){
                        disableButtons();
                        sendData("disable");
                        gameStatus.setText("Nobody Won!");
                    }
                }
                else {
                    displayClicked();
                }
            }
        });

        imageButtons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked[3]){
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED && selfSymbol != "" && turn == true && board.getSymbol(3) == ' ') {
                        clicked[3]=true;
                        if (selfSymbol.equals("X"))
                            imageButtons[3].setImageResource(R.drawable.cross_image);
                        else
                            imageButtons[3].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(selfSymbol.charAt(0), 3);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            gameStatus.setText("Opponent's Turn");
                        sendData("3:move");
                    } else if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    }
                    else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (selfSymbol == "") {
                        selectSymbolPrompt();
                    }
                    if (board.noWinner()){
                        disableButtons();
                        sendData("disable");
                        gameStatus.setText("Nobody Won!");
                    }
                }
                else {
                    displayClicked();
                }
            }
        });
        imageButtons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked[4]){
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED && selfSymbol != "" && turn == true && board.getSymbol(4) == ' ') {
                        clicked[4]=true;
                        if (selfSymbol.equals("X"))
                            imageButtons[4].setImageResource(R.drawable.cross_image);
                        else
                            imageButtons[4].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(selfSymbol.charAt(0), 4);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            gameStatus.setText("Opponent's Turn");
                        sendData("4:move");
                    } else if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    }
                    else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (selfSymbol == "") {
                        selectSymbolPrompt();
                    }
                    if (board.noWinner()){
                        disableButtons();
                        sendData("disable");
                        gameStatus.setText("Nobody Won!");
                    }
                }
                else {
                    displayClicked();
                }
            }
        });
        imageButtons[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked[5]){
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED && selfSymbol != "" && turn == true && board.getSymbol(5) == ' ') {
                        clicked[5]=true;
                        if (selfSymbol.equals("X"))
                            imageButtons[5].setImageResource(R.drawable.cross_image);
                        else
                            imageButtons[5].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(selfSymbol.charAt(0), 5);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            gameStatus.setText("Opponent's Turn");
                        sendData("5:move");
                    } else if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    }
                    else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (selfSymbol == "") {
                        selectSymbolPrompt();
                    }
                    if (board.noWinner()){
                        disableButtons();
                        sendData("disable");
                        gameStatus.setText("Nobody Won!");
                    }
                }
                else {
                    displayClicked();
                }
            }
        });
        imageButtons[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked[6]){
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED && selfSymbol != "" && turn == true && board.getSymbol(6) == ' ') {
                        clicked[6]=true;
                        if (selfSymbol.equals("X"))
                            imageButtons[6].setImageResource(R.drawable.cross_image);
                        else
                            imageButtons[6].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(selfSymbol.charAt(0), 6);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            gameStatus.setText("Opponent's Turn");
                        sendData("6:move");
                    } else if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    }
                    else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (selfSymbol == "") {
                        selectSymbolPrompt();
                    }
                    if (board.noWinner()){
                        disableButtons();
                        sendData("disable");
                        gameStatus.setText("Nobody Won!");
                    }
                }
                else {
                    displayClicked();
                }
            }
        });
        imageButtons[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked[7]){
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED && selfSymbol != "" && turn == true && board.getSymbol(7) == ' ') {
                        clicked[7]=true;
                        if (selfSymbol.equals("X"))
                            imageButtons[7].setImageResource(R.drawable.cross_image);
                        else
                            imageButtons[7].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(selfSymbol.charAt(0), 7);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            gameStatus.setText("Opponent's Turn");
                        sendData("7:move");
                    } else if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    }
                    else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (selfSymbol == "") {
                        selectSymbolPrompt();
                    }
                    if (board.noWinner()){
                        gameStatus.setText("Nobody Won!");
                        sendData("disable");
                        disableButtons();
                    }
                }
                else {
                    displayClicked();
                }
            }
        });
        imageButtons[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clicked[8]){
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED && selfSymbol != "" && turn == true && board.getSymbol(8) == ' ') {
                        clicked[8]=true;
                        if (selfSymbol.equals("X"))
                            imageButtons[8].setImageResource(R.drawable.cross_image);
                        else
                            imageButtons[8].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(selfSymbol.charAt(0), 8);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            gameStatus.setText("Opponent's Turn");
                        sendData("8:move");
                    } else if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    }
                    else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (selfSymbol == "") {
                        selectSymbolPrompt();
                    }
                    if (board.noWinner()){
                        disableButtons();
                        sendData("disable");
                        gameStatus.setText("Nobody Won!");
                    }
                }
                else {
                    displayClicked();
                }
            }
        });
    }
    /**
     * This method makes this device discoverable.
     *
     * Author: Malika Pahva (mxp134930)

     */
    private void ensureDiscoverable() {
        if (bluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

/**
 * This method sends a message to the other device.
 *
 * Author: Malika Pahva (mxp134930)

 */
    private void sendData(String message) {
        if (!bluetoothService.isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            bluetoothService.write(send);

            outStringBuffer.setLength(0);
        }
    }

    /**
     * This method updates the status on the action bar..
     *
     * Author: Saylee Pradhan (sap140530)

     */

    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * This method updates the status on the action bar.
     *
     * Author: Saylee Pradhan (sap140530)

     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * This method defines the Handler that gets information back from the BluetoothService
     *
     * Author: Malika Pahva (mxp134930)

     */

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, connectedDeviceName));
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);

                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                     readMessage = new String(readBuf, 0, msg.arg1);
                     getMessage(readMessage);
                     readMessage = "";

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + connectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * This method defines various actions to be carried out by the device on receiving different messages.
     *
     * Author: Malika Pahva (mxp134930)

     */
    public void getMessage(String message){
        if (message.equals("X")){
            Context context = getActivity().getApplicationContext();
            Toast toast = Toast.makeText(context,"Symbol Assigned: X",Toast.LENGTH_SHORT);
            toast.show();
            selfSymbol = "X";
            oppSymbol ="O";
            selectZeroButton.setBackgroundColor(Color.LTGRAY);
        }
        else if (message.equals("O")){
            Context context = getActivity().getApplicationContext();
            Toast toast = Toast.makeText(context,"Symbol Assigned: O",Toast.LENGTH_SHORT);
            toast.show();
            selfSymbol = "O";
            oppSymbol ="X";
            selectCrossButton.setBackgroundColor(Color.LTGRAY);
        }
        else if (message.contains(":move")){
            int pos = Integer.parseInt(String.valueOf(message.charAt(0)));

            if (oppSymbol.equals("X")){
                markCross(pos);
                if (board.checkWinner('X')!=' ')
                    displayResult(getView(),'X');
                else{
                   gameStatus.setText("Your Turn");
                    turn = true;
                }

            }
            else{
                markZero(pos);
                if (board.checkWinner('O')!=' ')
                    displayResult(getView(),'O');
                else{
                    gameStatus.setText("Your Turn");
                    turn = true;
                }
            }

        }
        else if (message.contains("newgame")){
            displayNewGameMsg();
            resetGame();
        }
        else if (message.contains("disable")){
            disableButtons();
        }
    }
    /**
     * This method communicates with the DeviceListActivity to connect  and to start
     * a session with the device.
     *
     * Author: Saylee Pradhan (sap140530)

     */

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BUTTON:
                if (resultCode == Activity.RESULT_OK) {
                    setupGame();
                } else {
                    Log.debug(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * This method establishes connection with other device
     *
     * Author: Malika Pahva (mxp134930)
     *
     * @param data

     */

    private void connectDevice(Intent data) {
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        bluetoothService.connect(device);
    }

    /**
     * This method sets the game menu on action bar.
     *
     * Author: Malika Pahva(mxp134930)
     *
     * @param
     *
     * @return boolean
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_game, menu);
    }

    /**
     * This method specifies the action of on click of menu item.
     *
     * Author: Malika Pahva(mxp134930)
     *
     * @param
     *
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect_scan: {
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.discoverable: {
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    /**
     * This method displays a toast when the devices are not paired.
     *
     * Author: Malika Pahva (mxp134930)
     *
     */
    public void displayNotPaired(){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Devices not paired",Toast.LENGTH_SHORT);
        toast.show();
    }
    /**
     * This method prompts the user to select a symbol.
     *
     * Author: Malika Pahva (mxp134930)
     *
     */
    public void selectSymbolPrompt(){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Select a Symbol",Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * This method displays a toast when the user tries to make a move when it is not her turn.
     *
     * Author: Malika Pahva (mxp134930)
     *
     */
    public void displayWhenNotYourTurn(){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Opponent's Turn",Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * This method displays a toast when a new game is started by the other user.
     *
     * Author: Malika Pahva (mxp134930)
     *
     */
    public void displayNewGameMsg(){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"New Game Started",Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * This method displays a toast when the user tries to click on a cell which is already marked.
     *
     * Author: Malika Pahva (mxp134930)
     *
     */
    public void displayClicked(){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Cell already marked",Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * This method displays a toast when either of the users has won or lost.
     *
     * Author: Malika Pahva (mxp134930)
     *
     */
    public void displayResult(View v,char moveResult){
        if (moveResult== selfSymbol.charAt(0)){
            gameStatus.setText("You Won!\n " +
                    "Click 'New Game' to restart");
        }

        else {
            gameStatus.setText("You Lost\n" +
                    " Click 'New Game' to restart");
        }
        disableButtons();
        setupBoard();
    }

    /**
     * This method marks a move by sending information to the TTTBoard class
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public void markCross(int position){
        if (board.getSymbol(position)==' '){
            board.setMove('X',position);
            imageButtons[position].setImageResource(R.drawable.cross_image);
        }
    }
    /**
     * This method marks a move by sending information to the TTTBoard class
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public void markZero(int position){
        if (board.getSymbol(position)==' '){
            board.setMove('O',position);
            imageButtons[position].setImageResource(R.drawable.zero_image);
        }
    }
    /**
     * This method resets all the clicked buttons
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public void resetClickedButtons(){
        for (int i =0;i <9;i++){
            clicked[i] = false;
        }
    }

    /**
     * This method resets the game by restarting the fragment
     *
     * Author: Malika Pahva (mxp134930)
     *
     */
    public void resetGame(){

        Fragment frg = null;
        frg = getFragmentManager().findFragmentById(R.id.sample_content_fragment);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    /**
     * This method disables all the buttons on the 9X9 grid
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public void disableButtons(){
        for (int i=0; i<9;i++){
            imageButtons[i].setEnabled(false);
        }
    }
    /**
     * This method enables all the buttons on the 9X9 grid
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public void enableButtons(){
        for (int i=0; i<9;i++){
            imageButtons[i].setEnabled(true);
        }
    }


}
