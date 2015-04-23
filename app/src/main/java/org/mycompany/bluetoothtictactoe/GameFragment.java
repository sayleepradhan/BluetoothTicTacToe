package org.mycompany.bluetoothtictactoe;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.mycompany.bluetoothtictactoe.common.logger.Log;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class GameFragment extends Fragment {

    private static final String TAG = "GameFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    //private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
   
    private Button mstartGameBtn;
    private Button select_zero_btn;
    private Button select_cross_btn;
    private ImageButton btn[];
    private String self_symbol;
    private String readMessage;
    private String opp_symbol;
    private boolean turn;
    private boolean flag = false;
    TTTBoard board;
    TextView game_status;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothService mService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If BT is not on, request that it be enabled.
        // setupGame() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mService == null) {
            setupGame();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mService.start();
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

        mstartGameBtn = (Button) view.findViewById(R.id.button_start);
        select_cross_btn = (Button) view.findViewById(R.id.select_cross_btn);
        select_zero_btn = (Button) view.findViewById(R.id.select_zero_btn);
        btn= new ImageButton[9];
        btn[0] = (ImageButton) view.findViewById(R.id.btn_0_0);
        btn[1] = (ImageButton) view.findViewById(R.id.btn_0_1);
        btn[2] = (ImageButton) view.findViewById(R.id.btn_0_2);
        btn[3] = (ImageButton) view.findViewById(R.id.btn_1_0);
        btn[4] = (ImageButton) view.findViewById(R.id.btn_1_1);
        btn[5] = (ImageButton) view.findViewById(R.id.btn_1_2);
        btn[6] = (ImageButton) view.findViewById(R.id.btn_2_0);
        btn[7] = (ImageButton) view.findViewById(R.id.btn_2_1);
        btn[8] = (ImageButton) view.findViewById(R.id.btn_2_2);
        self_symbol = "";
        opp_symbol ="";
        setupBoard();
    }

    /**
     * Set up the UI and background operations for chat.
     */


    private void setupGame() {
        Log.d(TAG, "setupGame()");

        // Initialize the start button with a listener that for click events
        game_status = (TextView) getView().findViewById(R.id.game_status_icon);
        mstartGameBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               flag = true;
                Context context = getActivity().getApplicationContext();
                int id = BluetoothService.STATE_CONNECTED;
                if (mService.getState() != BluetoothService.STATE_CONNECTED)
                {

                    Toast toast = Toast.makeText(context,"Devices not paired",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    setupBoard();
                }
            }
        });


        // Initialize the BluetoothService to perform bluetooth connections
        mService = new BluetoothService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    public void setupBoard() {
        if (flag) {
            board = new TTTBoard();
            select_cross_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Context context = getActivity().getApplicationContext();
                    if (mService.getState() == BluetoothService.STATE_CONNECTED) {
                        if (self_symbol.equals("")) {
                            turn = true;
                            self_symbol = "X";
                            opp_symbol = "O";
                            sendData("O");
                            select_zero_btn.setBackgroundColor(Color.LTGRAY);
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
            select_zero_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Context context = getActivity().getApplicationContext();
                    if (mService.getState() == BluetoothService.STATE_CONNECTED) {
                        if (self_symbol.equals("")) {
                            turn = true;
                            self_symbol = "O";
                            opp_symbol = "X";
                            sendData("X");
                            select_cross_btn.setBackgroundColor(Color.LTGRAY);
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
            btn[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mService.getState() == BluetoothService.STATE_CONNECTED && self_symbol != "" && turn == true && board.getSymbol(0) == ' ' && !board.noWinner()) {
                        if (self_symbol.equals("X"))
                            btn[0].setImageResource(R.drawable.cross_image);
                        else
                            btn[0].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(self_symbol.charAt(0), 0);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        }
                        sendData("0:move");
                        game_status.setText("Opponent's Turn");
                    } else if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    } else if (board.noWinner()) {
                        game_status.setText("Nobody Won!");
                        return;
                    } else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (self_symbol == "") {
                        selectSymbolPrompt();
                    } else {
                        displayNotPaired();
                    }
                }
            });

            btn[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mService.getState() == BluetoothService.STATE_CONNECTED && self_symbol != "" && turn == true && board.getSymbol(1) == ' ' && !board.noWinner()) {
                        if (self_symbol.equals("X"))
                            btn[1].setImageResource(R.drawable.cross_image);
                        else
                            btn[1].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(self_symbol.charAt(0), 1);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            game_status.setText("Opponent's Turn");
                        sendData("1:move");
                    } else if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    } else if (board.noWinner()) {
                        game_status.setText("Nobody Won!");
                        return;
                    } else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (self_symbol == "") {
                        selectSymbolPrompt();
                    } else {
                        displayNotPaired();
                    }
                }
            });
            btn[2].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mService.getState() == BluetoothService.STATE_CONNECTED && self_symbol != "" && turn == true && board.getSymbol(2) == ' ' && !board.noWinner()) {
                        if (self_symbol.equals("X"))
                            btn[2].setImageResource(R.drawable.cross_image);
                        else
                            btn[2].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(self_symbol.charAt(0), 2);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            game_status.setText("Opponent's Turn");
                        sendData("2:move");

                    } else if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    } else if (board.noWinner()) {
                        game_status.setText("Nobody won!");
                        return;
                    } else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (self_symbol == "") {
                        selectSymbolPrompt();
                    } else {
                        displayNotPaired();
                    }
                }
            });
            btn[3].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mService.getState() == BluetoothService.STATE_CONNECTED && self_symbol != "" && turn == true && board.getSymbol(3) == ' ' && board.noWinner()) {
                        if (self_symbol.equals("X"))
                            btn[3].setImageResource(R.drawable.cross_image);
                        else
                            btn[3].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(self_symbol.charAt(0), 3);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            game_status.setText("Opponent's Turn");
                        sendData("3:move");
                    } else if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    } else if (board.noWinner()) {
                        game_status.setText("Nobody Won!");
                        return;
                    } else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (self_symbol == "") {
                        selectSymbolPrompt();
                    } else {
                        displayNotPaired();
                    }
                }
            });
            btn[4].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mService.getState() == BluetoothService.STATE_CONNECTED && self_symbol != "" && turn == true && board.getSymbol(4) == ' ' && !board.noWinner()) {
                        if (self_symbol.equals("X"))
                            btn[4].setImageResource(R.drawable.cross_image);
                        else
                            btn[4].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(self_symbol.charAt(0), 4);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            game_status.setText("Opponent's Turn");
                        sendData("4:move");
                    } else if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    } else if (board.noWinner()) {
                        game_status.setText("Nobody Won!");
                        return;
                    } else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (self_symbol == "") {
                        selectSymbolPrompt();
                    } else {
                        displayNotPaired();
                    }
                }
            });
            btn[5].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mService.getState() == BluetoothService.STATE_CONNECTED && self_symbol != "" && turn == true && board.getSymbol(5) == ' ' && !board.noWinner()) {
                        if (self_symbol.equals("X"))
                            btn[4].setImageResource(R.drawable.cross_image);
                        else
                            btn[4].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(self_symbol.charAt(0), 5);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            game_status.setText("Opponent's Turn");
                        sendData("5:move");
                    } else if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    } else if (board.noWinner()) {
                        game_status.setText("Nobody Won!");
                        return;
                    } else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (self_symbol == "") {
                        selectSymbolPrompt();
                    } else {
                        displayNotPaired();
                    }
                }
            });
            btn[6].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mService.getState() == BluetoothService.STATE_CONNECTED && self_symbol != "" && turn == true && board.getSymbol(6) == ' ' && !board.noWinner()) {
                        if (self_symbol.equals("X"))
                            btn[6].setImageResource(R.drawable.cross_image);
                        else
                            btn[6].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(self_symbol.charAt(0), 6);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            game_status.setText("Opponent's Turn");
                        sendData("6:move");
                    } else if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    } else if (board.noWinner()) {
                        game_status.setText("Nobody Won!");
                        return;
                    } else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (self_symbol == "") {
                        selectSymbolPrompt();
                    } else {
                        displayNotPaired();
                    }
                }
            });
            btn[7].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mService.getState() == BluetoothService.STATE_CONNECTED && self_symbol != "" && turn == true && board.getSymbol(7) == ' ' && !board.noWinner()) {
                        if (self_symbol.equals("X"))
                            btn[7].setImageResource(R.drawable.cross_image);
                        else
                            btn[7].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(self_symbol.charAt(0), 7);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            game_status.setText("Opponent's Turn");
                        sendData("7:move");
                    } else if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    } else if (board.noWinner()) {
                        game_status.setText("Nobody Won!");
                        return;
                    } else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (self_symbol == "") {
                        selectSymbolPrompt();
                    } else {
                        displayNotPaired();
                    }
                }
            });
            btn[8].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mService.getState() == BluetoothService.STATE_CONNECTED && self_symbol != "" && turn == true && board.getSymbol(8) == ' ') {
                        if (self_symbol.equals("X"))
                            btn[8].setImageResource(R.drawable.cross_image);
                        else
                            btn[8].setImageResource(R.drawable.zero_image);
                        turn = false;
                        char moveResult = board.setMove(self_symbol.charAt(0), 8);
                        if (moveResult != ' ') {
                            displayResult(v, moveResult);
                        } else
                            game_status.setText("Opponent's Turn");
                        sendData("8:move");
                    } else if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                        displayNotPaired();
                    } else if (board.noWinner()) {
                        game_status.setText("Nobody Won!");
                        return;
                    } else if (!turn) {
                        displayWhenNotYourTurn();
                    } else if (self_symbol == "") {
                        selectSymbolPrompt();
                    } else {
                        displayNotPaired();
                    }
                }
            });
        } else {
            Toast.makeText(this.getActivity(),"Select New Game", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = message.getBytes();
            mService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
           //mOutEditText.setText(mOutStringBuffer);
        }
    }

    private void sendData(String message) {
        // Check that we're actually connected before trying anything
        if (!mService.isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = message.getBytes();
            mService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
        }
    }
    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
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
     * Updates the status on the action bar.
     *
     * @param subTitle status
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
     * The Handler that gets information back from the BluetoothService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
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
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                     readMessage = new String(readBuf, 0, msg.arg1);
                     getMessage(readMessage);
                     readMessage = "";
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
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

    public void getMessage(String message){
        if (message.equals("X")){
            Context context = getActivity().getApplicationContext();
            Toast toast = Toast.makeText(context,"Symbol Assigned: X",Toast.LENGTH_SHORT);
            toast.show();
            self_symbol = "X";
            opp_symbol="O";
            select_zero_btn.setBackgroundColor(Color.LTGRAY);
//            select_zero_btn.setClickable(false);
        }
        else if (message.equals("O")){
            Context context = getActivity().getApplicationContext();
            Toast toast = Toast.makeText(context,"Symbol Assigned: O",Toast.LENGTH_SHORT);
            toast.show();
            self_symbol = "O";
            opp_symbol="X";
//            select_cross_btn.setClickable(false);
            select_cross_btn.setBackgroundColor(Color.LTGRAY);
        }
        else if (message.contains(":move")){
            int pos = Integer.parseInt(String.valueOf(message.charAt(0)));

            if (opp_symbol.equals("X")){
                markCross(pos);
                if (board.checkWinner('X')!=' ')
                    displayResult(getView(),'X');
                else{
                   game_status.setText("Your Turn");
                    turn = true;
                }

            }
            else{
                markZero(pos);
                if (board.checkWinner('O')!=' ')
                    displayResult(getView(),'O');
                else{
                    game_status.setText("Your Turn");
                    turn = true;
                }
            }

        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a  session
                    setupGame();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     */
    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mService.connect(device);
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
    public void displayNotPaired(){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Devices not paired",Toast.LENGTH_SHORT);
        toast.show();
    }
    public void selectSymbolPrompt(){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Select a Symbol",Toast.LENGTH_SHORT);
        toast.show();
    }
    public void displayWhenNotYourTurn(){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Opponent's Turn",Toast.LENGTH_SHORT);
        toast.show();
    }
    public void displayResult(View v,char moveResult){
        //game_status = (TextView) v.findViewById(R.id.game_status_icon);
        if (moveResult==self_symbol.charAt(0)){
            game_status.setText("You Won!");
        }
        else
           game_status.setText("You Lost");
        setupBoard();
    }

    public void markCross(int position){
        if (board.getSymbol(position)==' '){
            board.setMove('X',position);
            btn[position].setImageResource(R.drawable.cross_image);
        }
    }
    public void markZero(int position){
        if (board.getSymbol(position)==' '){
            board.setMove('O',position);
            btn[position].setImageResource(R.drawable.zero_image);
        }
    }


}
