package com.example.screenwipetest3;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.widget.Spinner;
import java.io.IOException;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemoFragment1 extends Fragment implements AdapterView.OnItemSelectedListener {


    public DemoFragment1() {
        // Required empty public constructor
    }

    //User interface variables
    private Button buttonAxis1Pos;
    private Button buttonAxis1Neg;
    private Button buttonAxis2Pos;
    private Button buttonAxis2Neg;
    private TextView TextViewBlInfo;
    public static TextView textViewAxis1Position;
    public static TextView textViewAxis2Position;
    private Spinner spinner;

    //Variable to track which button is pressed
    private int ButtonState;
    //Variable to track which sensor module is selected
    public static int selectedModule = 0;
    //Array to track the positions of the servomotors of up to 16 sensor modules, each with 2 motors
    public static int[][] servoValue = new int [16][2];
    //Customized maximum deflection of the servomotors in degrees (90° is the maximum deflection of the servomotors)
    private int maxDeflection = 50;
    //Starting position of all servomotors (up to 32) in degrees separated with an "," for the first usage of the app on a device when no positions were saved yet
    public static final String firstStartPositions = "90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,";

    private Handler handler = new Handler();

    //Bluetooth variables
    private String btAddress = null;
    private String btName = null;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private boolean btConnected = false;
    //unique generated UUID for identification in bluetooth communication
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //f96867a2-9bfc-11ea-bb37-0242ac130002
    //device address of the HC-06 bluetooth module
    private static final String DEVICE_ADDRESS="98:D3:11:FD:43:BC";
    //Set to track all paired bluetooth devices
    private Set<BluetoothDevice> pairedDevices;

    /*
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get spinner object by id
        spinner = view.findViewById(R.id.spinner);
        //Array adapter to access the string array "numbers", located in strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.numbers, android.R.layout.simple_spinner_item);
        //Set a dropdown menu with the string array as items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Link the array adapter to the spinner
        spinner.setAdapter(adapter);
        //Set up a listener to catch the user access of the drop down menu
        spinner.setOnItemSelectedListener(this);

        //Get button objects by id
        TextViewBlInfo = view.findViewById(R.id.textView1);
        textViewAxis1Position = view.findViewById(R.id.textView2);
        textViewAxis2Position = view.findViewById(R.id.textView3);

        //Get text view objects by id
        buttonAxis1Pos = view.findViewById(R.id.button1);
        buttonAxis1Neg = view.findViewById(R.id.button2);
        buttonAxis2Pos = view.findViewById(R.id.button3);
        buttonAxis2Neg = view.findViewById(R.id.button4);

        try {
            bluetooth_connect_device();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Set up the listeners to catch the user access of the buttons
        //Axis 1 of the servomotor of the selected module
        //positive direction of rotation
        buttonAxis1Pos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //catch when the button is pressed
                    ButtonState = 1;
                    try {
                        count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //catch when the button is released
                    ButtonState = 0;
                }
                return true;
            }

        });
        //negative direction of rotation
        buttonAxis1Neg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //catch when the button is pressed
                    ButtonState = 2;
                    try {
                        count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //catch when the button is released
                    ButtonState = 0;
                }
                return true;
            }

        });

        //Axis 2 of the servomotor of the selected module
        //positive direction of rotation
        buttonAxis2Pos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //catch when the button is pressed
                    ButtonState = 3;
                    try {
                        count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //catch when the button is released
                    ButtonState = 0;
                }
                return true;
            }

        });
        //negative direction of rotation
        buttonAxis2Neg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //catch when the button is pressed
                    ButtonState = 4;
                    try {
                        count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //catch when the button is released
                    ButtonState = 0;
                }
                return true;
            }
        });
    }

     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_demo1,container,false);
        //Get spinner object by id
        spinner = view.findViewById(R.id.spinner);
        //Array adapter to access the string array "numbers", located in strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.numbers, android.R.layout.simple_spinner_item);
        //Set a dropdown menu with the string array as items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Link the array adapter to the spinner
        spinner.setAdapter(adapter);
        //Set up a listener to catch the user access of the drop down menu
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        //Get button objects by id
        TextViewBlInfo = view.findViewById(R.id.textView1);
        textViewAxis1Position = view.findViewById(R.id.textView2);
        textViewAxis2Position = view.findViewById(R.id.textView3);

        //Get text view objects by id
        buttonAxis1Pos = view.findViewById(R.id.button1);
        buttonAxis1Neg = view.findViewById(R.id.button2);
        buttonAxis2Pos = view.findViewById(R.id.button3);
        buttonAxis2Neg = view.findViewById(R.id.button4);

        try {
            bluetooth_connect_device();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Set up the listeners to catch the user access of the buttons
        //Axis 1 of the servomotor of the selected module
        //positive direction of rotation
        buttonAxis1Pos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //catch when the button is pressed
                    ButtonState = 1;
                    try {
                        count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //catch when the button is released
                    ButtonState = 0;
                }
                return true;
            }

        });
        //negative direction of rotation
        buttonAxis1Neg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //catch when the button is pressed
                    ButtonState = 2;
                    try {
                        count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //catch when the button is released
                    ButtonState = 0;
                }
                return true;
            }

        });

        //Axis 2 of the servomotor of the selected module
        //positive direction of rotation
        buttonAxis2Pos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //catch when the button is pressed
                    ButtonState = 3;
                    try {
                        count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //catch when the button is released
                    ButtonState = 0;
                }
                return true;
            }

        });
        //negative direction of rotation
        buttonAxis2Neg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //catch when the button is pressed
                    ButtonState = 4;
                    try {
                        count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //catch when the button is released
                    ButtonState = 0;
                }
                return true;
            }
        });
        textViewAxis1Position.setText("Axis 1: "+ servoValue[selectedModule][0]+"°");
        textViewAxis2Position.setText("Axis 2: "+ servoValue[selectedModule][1]+"°");
        return view;
    }


    //Set up the bluetooth connection with the bluetooth module connected to the arduino
    private void bluetooth_connect_device() throws IOException {
        try {
            //Get the default adapter
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            //fill the set with all bonded bluetooth devices
            pairedDevices = myBluetooth.getBondedDevices();
            //check if any bluetooth devices are paired to the smartphone
            if (pairedDevices.size() > 0) {
                //go through all paired devices
                for (BluetoothDevice bt : pairedDevices) {
                    //check if the HC-06 bluetooth module is paired
                    if(bt.getAddress().equals(DEVICE_ADDRESS)) {
                        //catch the address of the bluetooth module
                        btAddress = bt.getAddress();
                        //catch the name of the bluetooth module
                        btName = bt.getName();
                        //catch the connection state
                        btConnected = true;
                        //give the user feedback of the connection state
                        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
        catch(Exception we){}
        if(btConnected == true)
        {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            //Get a BluetoothDevice object for the given bluetooth hardware address
            BluetoothDevice device = myBluetooth.getRemoteDevice(btAddress);
            //Create an RFCOMM socket ready to start an insecure outgoing connection to this remote device using SDP lookup of UUID
            btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
            //Once the socket is connected it enables an IO stream for sending and receiving data in form of single bytes
            btSocket.connect();
            try {
                //Display the name and address of the connected bluetooth device
                TextViewBlInfo.setText("BT Name: " + btName + "\nBT Adress: " + btAddress);
            } catch (Exception e){}
        }
        else
        {
            //Display message if the device is not paired
            TextViewBlInfo.setText("Please pair the device first");
        }
    }

    //Sending the target position of the servomotors via bluetooth and changing the target position while a button is pressed
    private void count() throws IOException {
        try {
            switch (ButtonState)
            {
                //No button is pressed
                case 0:
                {
                    //Sending 1 byte of data to the bluetooth module
                    //The "33" is gives the arduino the information that there are no new target positions of the servomotors
                    btSocket.getOutputStream().write(33);
                    //stop repeating the counting process
                    return;
                }
                //When the button for positive direction of rotation on axis 1 is pressed
                case 1:
                {
                    //Check if the position of the servo has already reached its defined maximum deflection
                    if (servoValue[selectedModule][0] < 90 + maxDeflection)
                    {
                        //Count up the target position
                        servoValue[selectedModule][0]++;
                        //Display the new target position
                        textViewAxis1Position.setText("Axis 1: "+ servoValue[selectedModule][0]+"°");
                        //Sending a number from 1 to 16 to the bluetooth module to give the arduino the information about which axis 1 servomotor of the (up to 16) sensor modules is addressed
                        btSocket.getOutputStream().write(selectedModule +1);
                        //Sending the target position of the addressed servomotor
                        btSocket.getOutputStream().write(servoValue[selectedModule][0]);
                    }
                    else
                    {
                        //Display that the maximum deflection of the axis 1 servomotor of the selected sensor module is reached
                        textViewAxis1Position.setText("Axis 1: "+ servoValue[selectedModule][0]+"° (max)");
                    }
                    break;
                }
                //When the button for negative direction of rotation on axis 1 is pressed
                case 2:
                {
                    if (servoValue[selectedModule][0] > 90 - maxDeflection)
                    {
                        //Count down the target position
                        servoValue[selectedModule][0]--;
                        textViewAxis1Position.setText("Axis 1: "+ servoValue[selectedModule][0]+"°");
                        btSocket.getOutputStream().write(selectedModule +1);
                        btSocket.getOutputStream().write(servoValue[selectedModule][0]);
                    }
                    else
                    {
                        textViewAxis1Position.setText("Axis 1: "+ servoValue[selectedModule][0]+"° (max)");
                    }
                    break;
                }
                //When the button for positive direction of rotation on axis 2 is pressed
                case 3:
                {
                    if (servoValue[selectedModule][1] < 90 + maxDeflection)
                    {
                        servoValue[selectedModule][1]++;
                        textViewAxis2Position.setText("Axis 2: "+ servoValue[selectedModule][1]+"°");
                        //Sending a number from 17 to 32 to the bluetooth module to give the arduino the information about which axis 2 servomotor of the (up to 16) sensor modules is addressed
                        btSocket.getOutputStream().write(selectedModule +17);
                        btSocket.getOutputStream().write(servoValue[selectedModule][1]);
                    }
                    else
                    {
                        textViewAxis2Position.setText("Axis 2: "+ servoValue[selectedModule][1]+"° (max)");
                    }
                    break;
                }
                //When the button for negative direction of rotation on axis 2 is pressed
                case 4:
                {
                    if (servoValue[selectedModule][1] > 90 - maxDeflection)
                    {
                        servoValue[selectedModule][1]--;
                        textViewAxis2Position.setText("Axis 2: "+ servoValue[selectedModule][1]+"°");
                        btSocket.getOutputStream().write(selectedModule +17);
                        btSocket.getOutputStream().write(servoValue[selectedModule][1]);
                    }
                    else
                    {
                        textViewAxis2Position.setText("Axis 2: "+ servoValue[selectedModule][1]+"° (max)");
                    }
                    break;
                }
            }
            //repeat until no button is pressed anymore so that the target position will count as long as you hold a button
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        count();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 75);  //repeat every 75 milliseconds
        }  catch (IOException e) {
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //catch the selected position of the dropdown menu
        selectedModule = position;
        //Display the positions of the servomotors of the selected sensor module
        textViewAxis1Position.setText("Axis 1: "+ servoValue[selectedModule][0]+"°");
        textViewAxis2Position.setText("Axis 2: "+ servoValue[selectedModule][1]+"°");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    @Override
    public void onStop() {
        super.onStop();

        //Saving the position of all servos on the smartphone when the app stops
        //Building a string of all current positions of the servomotors, separated with an ","
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            str.append(servoValue[i][0]).append(",");
            str.append(servoValue[i][1]).append(",");
        }

        //Saving the built string to the shared preferences from where it can be accessed by this app at any time
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs",getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("value",str.toString());

        editor.apply();
    }



    @Override
    public void onStart() {
        super.onStart();


        //Loading the saved positions of all servos saved in the shared preferences
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs",getActivity().MODE_PRIVATE);
        //On the first start of the app when no shared preferences were saved yet the starting positions will be set as defined
        String savedString = prefs.getString("value", firstStartPositions);
        StringTokenizer st = new StringTokenizer(savedString, ",");
        //Read the saved positions from the loaded string
        for (int i = 0; i < 16; i++)
        {
            if (st.hasMoreTokens() == true) {
                servoValue[i][0] = Integer.parseInt(st.nextToken());
            }
            if (st.hasMoreTokens() == true) {
                servoValue[i][1] = Integer.parseInt(st.nextToken());
            }
        }


        //Display the loaded servo positions
        textViewAxis1Position.setText("Axis 1: "+ servoValue[selectedModule][0]+"°");
        textViewAxis2Position.setText("Axis 2: "+ servoValue[selectedModule][1]+"°");
    }
}
