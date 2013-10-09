package garylimyy.threads;

import java.io.ByteArrayInputStream;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import garylimyy.controller.ControllerFragment;
import garylimyy.controller.MainActivity;
import garylimyy.controller.MapViewFragment;

import android.util.Log;
import android.widget.Toast;

public class camera_thread_UDP extends Thread {
	
	private DatagramSocket socket=null;	
	private DatagramPacket receivePacket;
	
	public static int HEADER_SIZE = 5;
	public static int DATAGRAM_MAX_SIZE = 1250;
	public static int DATA_MAX_SIZE = DATAGRAM_MAX_SIZE - HEADER_SIZE;
	
	int current_frame = -1;
	int slicesStored = 0;
	byte[] imageData = null;
	byte[] sensorData = null;
	
	private boolean abort_ = false;
	boolean START = true;
	
	public camera_thread_UDP(DatagramSocket socketIn) { //Constructor
		try {
			Log.d("GCS INIT","GCS CAMERA THREAD: SETUP INIT!");
			socket = socketIn;
			byte[] data2 = new byte[DATAGRAM_MAX_SIZE];
			receivePacket = new DatagramPacket(data2, data2.length);
			Log.d("GCS INIT","GCS CAMERA THREAD: SETUP SUCCESSFUL!");
		} 		catch (Exception exception) {
			Log.d("GCS INIT","GCS CAMERA THREAD Error: ", exception);
		}
	} 


	@Override
	public final void run() {
		super.run();
		Log.d("GCS INIT","CAMERA THREAD: run STARTED");

		while (true) {
			try {
				while (true)
					loop();
			}
			catch (ConnectException e) {
				if (abort_)
					Log.d("GCS INIT","CAMERA THREAD: connection lost");
					break;
			} 
			catch (Exception e) {
				Log.e("AbstractIOIOActivity","Unexpected exception caught", e);
				break;
			} 
		}
	}
	
	protected void loop() throws ConnectException {
		//Log.d("GCS LOOP","CAMERA THREAD: loop start");
		try {
			
    		try {
    			Log.d("Camera_thread_UDP","CAMERA THREAD: ATT TO RECEIVE PACKET");
    			socket.receive(receivePacket);
    			Log.d("Camera_thread_UDP","1 Packet Received");
    		} catch (Exception e) {
    			Log.d("Camera_thread_UDP","CAMERA THREAD: SOCKET PACKET ERROR " +  e.getMessage().toString());
    		}
    		
    		byte[] data = receivePacket.getData();	
    		Log.d("Camera_thread_UDP","CAMERA THREAD: SOCKET PACKET DATA RECEIVED");
    		
    		int frame_nb = (int)data[0];
    		int nb_packets = (int)data[1];
    		int packet_nb = (int)data[2];
    		int size_packet = (int) ((data[3] & 0xff) << 8 | (data[4] & 0xff)); 
    		//Log.d("GCS LOOP","CAMERA THREAD: data extracted");
    		
    		//First, test if packet is for sensors
    		if (frame_nb == -1) {
    			//assumes only 1 packet if it's a sensor packet
//    			System.arraycopy(data, HEADER_SIZE, sensorData, packet_nb * DATA_MAX_SIZE, size_packet);
//    			
//    			float txtacc1, txtacc2, txtacc3, txtdis1, txtdis2, txtdis3, txtgps1 = 0, txtgps2 = 0;
//
//    			short nb = (short) ((data[0] & 0xff) << 8 | (data[1] & 0xff)); 
//    			txtacc1 = (float)nb / 100;
//
//    			nb = (short) ((data[2] & 0xff) << 8 | (data[3] & 0xff)); 
//    			txtacc2 = (float)nb / 100;
//
//    			nb = (short) ((data[4] & 0xff) << 8 | (data[5] & 0xff)); 
//    			txtacc3 = (float)nb / 100;
//
//    			nb = (short) ((data[6] & 0xff) << 8 | (data[7] & 0xff)); 
//    			txtdis1 = (float)nb / 100;
//
//    			nb = (short) ((data[8] & 0xff) << 8 | (data[9] & 0xff)); 
//    			txtdis2 = (float)nb / 100;
//
//    			nb = (short) ((data[10] & 0xff) << 8 | (data[11] & 0xff)); 
//    			txtdis3 = (float)nb / 100;
//
//    			//proper handler to be implemented!
//    			MapViewFragment.set_sensors_values(txtacc1, txtacc2, txtacc3, txtdis1, txtdis2, txtdis3, txtgps1, txtgps2);
    			
    		} else {
    			
    			if((packet_nb==0) && (current_frame != frame_nb)) {
        			current_frame = frame_nb;
        			slicesStored = 0;				
        			imageData = new byte[nb_packets * DATA_MAX_SIZE];
        		}

        		if(frame_nb == current_frame) {
        			System.arraycopy(data, HEADER_SIZE, imageData, packet_nb * DATA_MAX_SIZE, size_packet);
        			slicesStored++;				
        		}

        		/* If image is complete display it */
        		if (slicesStored == nb_packets) {	
        			ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        			ControllerFragment.set_image(bis);
        			Log.d("Camera_thread_UDP","Full Packet Received and image set");
        		}
    		}
    		
    		    		
		} catch (Exception e) {
			e.printStackTrace();
		}				

	}

	public synchronized final void abort() {
		abort_ = true;
		socket.close();
	}

}