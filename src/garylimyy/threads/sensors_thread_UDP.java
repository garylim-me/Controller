//DEPRECATED!! Combined with camera thread

package garylimyy.threads;

import java.lang.Thread;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import garylimyy.controller.MainActivity;

import android.util.Log;

public class sensors_thread_UDP extends Thread {

	private DatagramSocket socket;	
	private DatagramPacket receivePacket;
	
	private boolean abort_ = false;
	boolean START = true;

	public sensors_thread_UDP() { //Constructor
		try {
			//Log.d("sensors_thread_UDP","GCS SENSORS THREAD: SETUP INIT!");
			socket = new DatagramSocket(MainActivity.SENSORS_PORT);
			byte[] data2 = new byte[12];
			receivePacket = new DatagramPacket(data2, data2.length);
			//Log.d("sensors_thread_UDP","GCS SENSORS THREAD: SETUP SUCCESSFUL!");
		} 		catch (Exception exception) {
			//Log.d("sensors_thread_UDP","GCS SENSORS THREAD Error: ", exception);
		}
	} 

	@Override
	public final void run() {
		super.run();
		//Log.d("sensors_thread_UDP","SENSORS THREAD: STARTED");

		while (true) {
			try { 
				while (true)
					loop();
			} 
			catch (ConnectException e) {
				if (abort_) 
					break;
			} 
			catch (Exception e) {
				Log.e("sensors_thread_UDP","Unexpected exception caught", e);
				break;
			} 
		}
	}


	protected void loop() throws ConnectException {
		//Log.d("sensors_thread_UDP","SENSORS THREAD: LOOP");
		try {			

			
    		try {
    			//Log.d("sensors_thread_UDP","SENSORS THREAD: ATT TO RECEIVE PACKET");
    			socket.receive(receivePacket);
    			//Log.d("sensors_thread_UDP","PACKET RECEIVED");
    		} catch (Exception e) {
    			//Log.d("sensors_thread_UDP","SENSORS THREAD: SOCKET PACKET ERROR " +  e.getMessage().toString());
    		}
			
    		byte[] data = receivePacket.getData();	
    		//Log.d("sensors_thread_UDP","SENSORS THREAD: SOCKET PACKET DATA RECEIVED");
			
			float txtacc1, txtacc2, txtacc3, txtdis1, txtdis2, txtdis3, txtgps1 = 0, txtgps2 = 0;

			short nb = (short) ((data[0] & 0xff) << 8 | (data[1] & 0xff)); 
			txtacc1 = (float)nb / 100;

			nb = (short) ((data[2] & 0xff) << 8 | (data[3] & 0xff)); 
			txtacc2 = (float)nb / 100;

			nb = (short) ((data[4] & 0xff) << 8 | (data[5] & 0xff)); 
			txtacc3 = (float)nb / 100;

			nb = (short) ((data[6] & 0xff) << 8 | (data[7] & 0xff)); 
			txtdis1 = (float)nb / 100;

			nb = (short) ((data[8] & 0xff) << 8 | (data[9] & 0xff)); 
			txtdis2 = (float)nb / 100;

			nb = (short) ((data[10] & 0xff) << 8 | (data[11] & 0xff)); 
			txtdis3 = (float)nb / 100;

			//MainActivity.set_sensors_values(txtacc1, txtacc2, txtacc3, txtdis1, txtdis2, txtdis3, txtgps1, txtgps2);
			//Log.d("sensors_thread_UDP","SENSORS THREAD: SENSOR VALUES SET: " + txtacc1 + txtacc2 + txtacc3 + txtdis1 + txtdis2 + txtdis3 + txtgps1 + txtgps2);

		} 
		catch (Exception e) {
			//Log.d("sensors_thread_UDP","SENSORS THREAD: SOCKET PACKET NOT RECEIVED " + e);
		} 
	}

	public synchronized final void abort() {
		abort_ = true;
		socket.close();
	}

}