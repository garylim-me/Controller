package garylimyy.threads;

import garylimyy.controller.MainActivity;

import java.io.IOException;
import java.lang.Thread;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.util.Log;

public class ioio_thread_UDP extends Thread {

	private DatagramSocket socket;	
	private InetAddress serverAddr;
	
	public static int armServo;
	public static int leftServo;
	public static int rightServo;
	public static boolean servoUpdate;
	public static boolean servoMove;

	public ioio_thread_UDP(String DoubotIPIn, DatagramSocket socketIn) { //Constructor
		try {
			socket = socketIn;
			serverAddr = InetAddress.getByName(DoubotIPIn);
			Log.d("ioio_thread_UDP","Setup successful");
		} catch (Exception exception) {
			serverAddr = null;
			Log.d("ioio_thread_UDP","Setup error: ", exception);
		}
	} 

	@Override
	public final void run() {
		super.run();
		//Log.d("ioio_thread_UDP","Run");

		//Sending initial packet to establish connection. Contains Controller's IP
		try {
			byte[] ipData = MainActivity.actualIpAddress.getBytes();
			int packetSize = ipData.length;
			DatagramPacket packet = new DatagramPacket(ipData, packetSize, serverAddr, MainActivity.IOIO_PORT);			
			socket.send(packet);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
//	    	byte[] receiveData = new byte[1024];
//	    	DatagramPacket packet_R = new DatagramPacket(receiveData, receiveData.length);
//	    	
//	    	socket.receive(packet_R);
//			String controllerIP = new String(packet_R.getData(),0,packet_R.getLength());
//			Log.d("MainThread","STRING IN PACKET: " + controllerIP + ", DETECTED IP: " + packet_R.getAddress().toString().substring(1) + ", DETECTED PORT: "+ packet_R.getPort());
			
        
		while (true) { 
			
			if (servoUpdate && servoMove) {

				byte[] data2 = new byte[6];
				data2[0] = (byte) (armServo >> 8);
				data2[1] = (byte) armServo; 
				data2[2] = (byte) (leftServo >> 8);
				data2[3] = (byte) leftServo; 
				data2[4] = (byte) (rightServo >> 8);
				data2[5] = (byte) rightServo; 

				try {
					//Log.d("ioio_thread_UDP","attempting to send packet..");
					int packetSize = data2.length;
					DatagramPacket packet = new DatagramPacket(data2, packetSize, serverAddr, MainActivity.IOIO_PORT);
					socket.send(packet);
					Log.d("ioio_thread_UDP","Packet sent");
				} catch (Exception e) {
					//Log.d("ioio_thread_UDP","packet send error " +  e.getMessage().toString());
				}

				servoUpdate = false;
			} else if (servoUpdate && servoMove==false) {
				
				// to send multiple times to ensure packet gets sent
				for (int i = 0; i<5; i++) {
					
					byte[] data2 = new byte[6];
					data2[0] = (byte) (armServo >> 8);
					data2[1] = (byte) armServo; 
					data2[2] = (byte) (leftServo >> 8);
					data2[3] = (byte) leftServo; 
					data2[4] = (byte) (rightServo >> 8);
					data2[5] = (byte) rightServo; 

					try {
						//Log.d("ioio_thread_UDP","attempting to send packet..");
						int packetSize = data2.length;
						DatagramPacket packet = new DatagramPacket(data2, packetSize, serverAddr, MainActivity.IOIO_PORT);
						socket.send(packet);
						Log.d("ioio_thread_UDP","Packet sent");
					} catch (Exception e) {
						//Log.d("ioio_thread_UDP","packet send error " +  e.getMessage().toString());
					}
					
				}
				
				servoUpdate = false;
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	
	
	public synchronized final void abort() {
		//		abort_ = true;
		//socket.close();
	}

}