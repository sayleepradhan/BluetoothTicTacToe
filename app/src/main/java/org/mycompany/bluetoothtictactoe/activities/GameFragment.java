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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.mycompany.bluetoothtictactoe.R;
import org.mycompany.bluetoothtictactoe.logger.Log;
import org.mycompany.bluetoothtictactoe.model.TTTBoard;

/**
 * Created by Saylee Pradhan (sap140530) on 4/20/2015.
 * Course: CS6301.001
 *
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class GameFragment extends Fragment {

    private static final String TAG = "GameFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BUTTON = 3;

    // Layout Views
   
    private Button startGameButton;
    private ImageButton selectZeroButton;
    private ImageButton selectCrossButton;
    private ImageButton imageButtons[];
    private String selfSymbol;
    private String readMessage;
    private String oppSymbol;
    private boolean turn;
    private boolean flag = false;
    TTTBoard board;
    private boolean gameStatus;
    private boolean[] clicked;
    /**
     * Name of the connected device
     */
    private String connectedDeviceName = null;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer outStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter bluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothService bluetoothService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If BT is not on, request that it be enabled.
        // setupGame() will then be called during onActivityResult
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BUTTON);
            // Otherwise, setup the chat session
        } else if (bluetoothService == null) {
            setupGame();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (bluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                bluetoothService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
/**
 * This method populates the view with UI elements on creation.
 *
 * Author: Malika Pahva (mxp134930)
 *
 * @param View
 *
 * @param Bundle
 */
        startGameButton = (Button) view.findViewById(R.id.button_start);
        selectCrossButton = (ImageButton) view.findViewById(R.id.select_cross_btn);
        selectZeroButton = (ImageButton) view.findViewById(R.id.select_zero_btn);
        clicked = new boolean[9];
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
        gameStatus = false;
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

                Context context = getActivity().getApplicationContext();
                if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                    Toast toast = Toast.makeText(context, "Devices not paired", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    reset();
                    displayNewGameMsg();
                    flag = true;
                    sendData("newgame");
                    resetClickedButtons();
                    gameStatus = true;
                    setupBoard();
                }
            }
        });


        // Initializes the BluetoothService to perform bluetooth connections
        bluetoothService = new BluetoothService(getActivity(), handler);

        // Initializes the buffer for outgoing messages
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
                            symbolAssigned("X");
                            selfSymbol = "X";
                            oppSymbol = "O";
                            sendData("O");
                            selectCrossButton.setBackgroundColor(Color.TRANSPARENT);
                            selectCrossButton.setImageResource(R.drawable.cross_image);
                            selectCrossButton.setBackgroundColor(Color.LTGRAY);
                        } else {
                            Toast toast = Toast.makeText(context, "Symbol already selected", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {

                        Toast toast = Toast.makeText(context, "Devices not paired", Toast.LENGTH_SHORT);
                        toast.show();
                    } }
                else if (!gameStatus) {
                    Toast.makeText(context,"Select New Game", Toast.LENGTH_SHORT).show();
                }
            }
        });
        selectZeroButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Context context = getActivity().getApplicationContext();
                if (flag){
                    if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED) {
                        if (selfSymbol.equals("")) {
                            turn = true;
                            symbolAssigned("O");
                            selfSymbol = "O";
                            oppSymbol = "X";
                            sendData("X");
                            selectZeroButton.setBackgroundColor(Color.TRANSPARENT);
                            selectZeroButton.setImageResource(R.drawable.zero_image);
                            selectZeroButton.setBackgroundColor(Color.LTGRAY);
                        } else {
                            Toast toast = Toast.makeText(context, "Symbol already selected", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(context, "Devices not paired", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else if (!gameStatus) {
                    Toast.makeText(context,"Select New Game", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(0);
            }
        });

        imageButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(1);
            }
        });
        imageButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(2);
            }
        });

        imageButtons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(3);
            }
        });
        imageButtons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(4);
            }
        });
        imageButtons[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(5);
            }
        });
        imageButtons[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(6);
            }
        });
        imageButtons[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(7);
            }
        });
        imageButtons[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(8);
            }
        });
    }
    /**
     * This method makes this device discoverable.
     *
     * Author: Malika Pahva (mxp134930)

     */
    private
    void ensureDiscoverable() {
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
        // Check that we're actually connected before trying anything
        if (!bluetoothService.isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = message.getBytes();
            bluetoothService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            outStringBuffer.setLength(0);
//            mOutEditText.setText(outStringBuffer);
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
                            //mConversationArrayAdapter.clear();
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
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                     readMessage = new String(readBuf, 0, msg.arg1);
                     getMessage(readMessage);
                     readMessage = "";

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
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
            symbolAssigned("X");
            selfSymbol = "X";
            oppSymbol="O";
            selectCrossButton.setBackgroundColor(Color.TRANSPARENT);
            selectCrossButton.setImageResource(R.drawable.cross_image);
            selectCrossButton.setBackgroundColor(Color.LTGRAY);
        }
        else if (message.equals("O")){
            symbolAssigned("O");
            selfSymbol = "O";
            oppSymbol="X";
            selectZeroButton.setBackgroundColor(Color.TRANSPARENT);
            selectZeroButton.setImageResource(R.drawable.zero_image);
            selectZeroButton.setBackgroundColor(Color.LTGRAY);
        }
        else if (message.contains(":move")){
            int pos = Integer.parseInt(String.valueOf(message.charAt(0)));
            clicked[pos]=true;
            if (oppSymbol.equals("X")){
                markCross(pos);
                if (board.checkWinner('X')!=' '){
                    gameStatus = false;
                    Toast toast;
                    if (board.checkWinner('X')=='N')
                    {
                        toast = Toast.makeText(getActivity().getApplicationContext(),"Nobody Won :-|",Toast.LENGTH_LONG);
                    }
                    else
                        toast = Toast.makeText(getActivity().getApplicationContext(),"You Lost! :(",Toast.LENGTH_LONG);
                    toast.show();
                    clickForNewGame();
                }
            }
            else{
                markZero(pos);
                if (board.checkWinner('O')!=' '){
                    gameStatus = false;
                    Toast toast;
                    if (board.checkWinner('O')=='N')
                    {
                        toast = Toast.makeText(getActivity().getApplicationContext(),"Nobody Won :-|",Toast.LENGTH_LONG);
                    }
                    else
                        toast = Toast.makeText(getActivity().getApplicationContext(),"You Lost! :(",Toast.LENGTH_LONG);
                    toast.show();
                    clickForNewGame();
                }

            }
            if (gameStatus && !(message.contains("X") && !message.contains("O"))){
                displayWhenYourTurn();
                turn = true;
                return;
            }
        }

        else if (message.contains("newgame")){
            reset();
            displayNewGameMsg();
            resetClickedButtons();
            gameStatus=true;
            flag = true;
            setupBoard();
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
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BUTTON:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a  session
                    setupGame();
                } else {
                    // User did not enable Bluetooth or an error occurred
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
     * @param data

     */

    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        bluetoothService.connect(device);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_game, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
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
     *
     */
    public void displayResult(View v,char moveResult){
        Context context = getActivity().getApplicationContext();
        Toast toast;
        gameStatus=false;
        if (moveResult==selfSymbol.charAt(0)){
            //sendData("end:youlost");
            toast = Toast.makeText(context,"You Won! :)",Toast.LENGTH_LONG);
            toast.show();
        }
        else if (moveResult=='N'){
            //sendData("end:NobodyWon");
            toast = Toast.makeText(context,"Nobody Won :-|",Toast.LENGTH_LONG);
            toast.show();
        }
        //reset();
        //reset();
        clickForNewGame();
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
     * This method resets the game by resetting the ImageButtons
     *
     * Author: Malika Pahva (mxp134930)
     *
     */
    public void reset(){

        View view = getView();

        startGameButton = (Button) view.findViewById(R.id.button_start);
        selectCrossButton.setBackgroundColor(Color.TRANSPARENT);
        selectCrossButton.setImageResource(R.drawable.cross_image);
        selectZeroButton.setBackgroundColor(Color.TRANSPARENT);
        selectZeroButton.setImageResource(R.drawable.zero_image);
        imageButtons[0].setImageResource(android.R.color.transparent);
        imageButtons[1].setImageResource(android.R.color.transparent);
        imageButtons[2].setImageResource(android.R.color.transparent);
        imageButtons[3].setImageResource(android.R.color.transparent);
        imageButtons[4].setImageResource(android.R.color.transparent);
        imageButtons[5].setImageResource(android.R.color.transparent);
        imageButtons[6].setImageResource(android.R.color.transparent);
        imageButtons[7].setImageResource(android.R.color.transparent);
        imageButtons[8].setImageResource(android.R.color.transparent);
        selfSymbol = "";
        oppSymbol ="";
        setupBoard();
    }

    /**
     * This method prompts the user to start a new game.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public void clickForNewGame(){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Click 'New Game' to restart",Toast.LENGTH_LONG);
        toast.show();
    }
    /**
     * This method displays the symbol assigned to each user.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public void symbolAssigned(String symbol){
        Context context = getActivity().getApplicationContext();
        Toast toast = Toast.makeText(context,"Symbol Assigned: "+symbol,Toast.LENGTH_SHORT);
        toast.show();
    }
    public void buttonClick(int i){
        if (!clicked[i] && turn){
            if (selfSymbol=="")
                selectSymbolPrompt();
            else if (bluetoothService.getState() == BluetoothService.STATE_CONNECTED && turn == true && board.getSymbol(i) == ' ' && !board.noWinner()) {
                clicked[i]=true;
                if (selfSymbol.equals("X"))
                    imageButtons[i].setImageResource(R.drawable.cross_image);
                else
                    imageButtons[i].setImageResource(R.drawable.zero_image);
                turn = false;
                char moveResult = board.setMove(selfSymbol.charAt(0), i);
                sendData(i+":move"+moveResult);
                if (moveResult != ' ') {
                    displayResult(getView(), moveResult);
                }
                if (gameStatus)
                    displayWhenNotYourTurn();
            } else if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
                displayNotPaired();
            }
        }
        else if (clicked[i]){
            displayClicked();
        }
        else if (!turn && gameStatus) {
            displayWhenNotYourTurn();
        }
    }
    /**
     * This method indicates the user whose turn is up.
     *
     * Author: Malika Pahva (mxp134930)
     *
     */
    public void displayWhenYourTurn(){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Your Turn",Toast.LENGTH_SHORT);
        toast.show();
    }
}