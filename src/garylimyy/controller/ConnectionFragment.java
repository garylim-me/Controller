package garylimyy.controller;

import java.net.DatagramSocket;
import java.net.SocketException;

import garylimyy.threads.camera_thread_UDP;
import garylimyy.threads.ioio_thread_UDP;
import garylimyy.threads.sensors_thread_UDP;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ConnectionFragment extends Fragment {

	//Threads
	public static ioio_thread_UDP ioio_thread;
//	sensors_thread_UDP sensors_thread;
	public static camera_thread_UDP camera_thread;
	
	private EditText txtTabletIP;
	private EditText txtPhoneIP;
	private ToggleButton btnStart;
	private String PHONE_IP;
	
	public DatagramSocket socket;	
	
    public ConnectionFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connection, container, false);
        String actualIpAddress = getArguments().getString("actualIpAddress");
        getActivity().setTitle("Connection");
        
		//Declaring variables
		btnStart = (ToggleButton) rootView.findViewById(R.id.btnStart);
		txtTabletIP = (EditText) rootView.findViewById(R.id.txtTabletIP);
		txtPhoneIP = (EditText) rootView.findViewById(R.id.txtPhoneIP);
		
		//restoring previously saves state:
        if (savedInstanceState != null) {
            // Restore last state for checked position.
        	btnStart.setChecked(savedInstanceState.getBoolean("btnStartChecked"));
        	txtTabletIP.setText(savedInstanceState.getString("txtTabletIP"));
        	txtPhoneIP.setText(savedInstanceState.getString("txtPhoneIP"));
        }
		
        txtTabletIP.append(actualIpAddress); //modified 
        
        return rootView;
    }
    
    
    public static String IP_Int_to_String (int ipAddress){

		String ipBinary = Integer.toBinaryString(ipAddress);

		//Leading zeroes are removed by toBinaryString, this will add them back.
		while (ipBinary.length() < 32) {
		    ipBinary = "0" + ipBinary;
		}
		
		//get the four different parts
		String a = ipBinary.substring(0,8);
		String b = ipBinary.substring(8,16);
		String c = ipBinary.substring(16,24);
		String d = ipBinary.substring(24,32);

		//Convert to numbers
		return Integer.parseInt(d,2)+"."+Integer.parseInt(c,2)+"."+Integer.parseInt(b,2)+"."+Integer.parseInt(a,2);
		
	}
    
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("btnStartChecked", btnStart.isChecked());
        outState.putString("txtPhoneIP",txtPhoneIP.getText().toString());
        outState.putString("txtTabletIP",txtTabletIP.getText().toString());
    }
    
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    if (savedInstanceState != null) {
//        // Restore last state for checked position.
//    	btnStart.setChecked(savedInstanceState.getBoolean("btnStartChecked"));
//    	txtTabletIP.setText(savedInstanceState.getString("txtTabletIP"));
//    	txtPhoneIP.setText(savedInstanceState.getString("txtPhoneIP"));
//        }
//}
    
    public void onToggleClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {

			PHONE_IP = txtPhoneIP.getText().toString();

			socket = null;
			try {
				socket = new DatagramSocket(MainActivity.IOIO_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
			//this thread sends
			ioio_thread = new ioio_thread_UDP(PHONE_IP, socket);
			ioio_thread.start();
			
			//to be removed!
//			sensors_thread = new sensors_thread_UDP();
//			sensors_thread.start();
//			
			//this thread receives
			camera_thread = new camera_thread_UDP(socket);
			//camera_thread.setPriority((Thread.MAX_PRIORITY + Thread.NORM_PRIORITY) / 2);
			camera_thread.start();
			
			Toast.makeText(getActivity(), "Connection initiated", Toast.LENGTH_SHORT).show();
	    } else {
			ioio_thread.abort();
			camera_thread.abort();
			Toast.makeText(getActivity(), "Connection aborted", Toast.LENGTH_SHORT).show();
	    }
	}
}