package garylimyy.controller;

import java.io.ByteArrayInputStream;

import garylimyy.threads.camera_thread_UDP;
import garylimyy.threads.ioio_thread_UDP;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Fragment that appears in the "content_frame", shows the controller
 */

public class ControllerFragment extends Fragment {
    
	//Controls:
	private long lastLeftUpTime=0;
	private long lastRightUpTime=0;
	private static VerticalSeekBar leftSeekBar;
	private static VerticalSeekBar rightSeekBar;
	private static SeekBar armSeekBar;
	
	//Threads:
	camera_thread_UDP camera_thread;
	ioio_thread_UDP ioio_thread;
	
	//Camera:
	static private Handler handlerCamera;
	static ImageView imageView;
	static private Bitmap b;
	
	
    public ControllerFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_controller, container, false);
        getActivity().setTitle("Controller");
        
        armSeekBar = (SeekBar) rootView.findViewById (R.id.armSeekBar);
		leftSeekBar = (VerticalSeekBar) rootView.findViewById (R.id.leftSeekBar);
		rightSeekBar = (VerticalSeekBar) rootView.findViewById (R.id.rightSeekBar);
		imageView = (ImageView) rootView.findViewById (R.id.imageview);
		
		armSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				ioio_thread.armServo=progress+1000;
				ioio_thread.servoUpdate=true;
			}
	
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		
		leftSeekBar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
	//				Toast.makeText(ControlActivity.this, "seekbar reset", Toast.LENGTH_SHORT).show();
					ioio_thread.leftServo=(500+1000+SettingsFragment.tuneLeftValue);
					ioio_thread.servoUpdate=true;
					ioio_thread.servoMove = false;
					lastLeftUpTime = System.currentTimeMillis();
				}
				return false;
			}
		});
		
		leftSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (System.currentTimeMillis() - lastLeftUpTime > 500) {
					ioio_thread.leftServo=(int) (-(progress-500)*SettingsFragment.sensitivityValue/100+1500+SettingsFragment.tuneLeftValue);
					ioio_thread.servoMove = true;
					ioio_thread.servoUpdate=true;	
				}
			}
	
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		
		rightSeekBar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_UP) {
	//				Toast.makeText(ControlActivity.this, "seekbar reset", Toast.LENGTH_SHORT).show();
					ioio_thread.rightServo=500+600+SettingsFragment.tuneRightValue;
					ioio_thread.servoUpdate=true;
					ioio_thread.servoMove = false;
					lastRightUpTime = System.currentTimeMillis();
				}
				return false;
			}
		});
		
		rightSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (System.currentTimeMillis() - lastRightUpTime > 500) {
					ioio_thread.rightServo=(int) (-(progress-500)*SettingsFragment.sensitivityValue/100+500+600+SettingsFragment.tuneRightValue);
					ioio_thread.servoMove = true;
					ioio_thread.servoUpdate=true;	
				}
			}
	
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		
		
		//handler: to be re-written!
		handlerCamera = new Handler() { 
			@Override
			public void handleMessage(Message msg) {
				imageView.setImageBitmap(b);
			}
		};		
		
        return rootView;
    }
    
	public static void set_image (ByteArrayInputStream bis) {
		b = BitmapFactory.decodeStream(bis);    
		handlerCamera.sendEmptyMessage(0);
	}
	
}