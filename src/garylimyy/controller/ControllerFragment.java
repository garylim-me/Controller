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
import android.widget.TextView;

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
	private static TextView leftSeekBarValue;
	private static TextView rightSeekBarValue;
	private static TextView armSeekBarValue;
	int CENTER_POSITION = 1450; //for neutral left/right position
	int PROGRESS_BAR_MID = 450; //middle position of progress bar (0-900)
	
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
		
		armSeekBarValue = (TextView) rootView.findViewById (R.id.armSeekBarValue);
		leftSeekBarValue = (TextView) rootView.findViewById (R.id.leftSeekBarValue);
		rightSeekBarValue = (TextView) rootView.findViewById (R.id.rightSeekBarValue);
		
		armSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int BarValue = progress+1000;
				armSeekBarValue.setText(Integer.toString(BarValue));
				ioio_thread.armServo=BarValue;
				ioio_thread.servoUpdate=true;
			}
	
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
//				armSeekBar.setProgress(PROGRESS_BAR_MID); //temp
			}
		});
		
		leftSeekBar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
					int BarValue = (CENTER_POSITION+SettingsFragment.tuneLeftValue);
					leftSeekBarValue.setText(Integer.toString(BarValue));
					ioio_thread.leftServo=BarValue;
					ioio_thread.servoUpdate=true;
					ioio_thread.servoMove = false;
					lastLeftUpTime = System.currentTimeMillis();
					
					leftSeekBar.setProgressAndThumb(PROGRESS_BAR_MID);
				}
				return false;
			}
		});
		
		leftSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (System.currentTimeMillis() - lastLeftUpTime > 500) {
					int BarValue = (-(progress-PROGRESS_BAR_MID)*SettingsFragment.sensitivityValue/100+CENTER_POSITION+SettingsFragment.tuneLeftValue);
					leftSeekBarValue.setText(Integer.toString(BarValue));
					ioio_thread.leftServo=(int) BarValue;
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
					int BarValue = CENTER_POSITION+SettingsFragment.tuneRightValue;
					rightSeekBarValue.setText(Integer.toString(BarValue));
					ioio_thread.rightServo=BarValue;
					ioio_thread.servoUpdate=true;
					ioio_thread.servoMove = false;
					lastRightUpTime = System.currentTimeMillis();
					
					rightSeekBar.setProgressAndThumb(PROGRESS_BAR_MID);
				}
				return false;
			}
		});
		
		rightSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (System.currentTimeMillis() - lastRightUpTime > 500) {
					int BarValue = (-(progress-PROGRESS_BAR_MID)*(SettingsFragment.sensitivityValue/100)+CENTER_POSITION+SettingsFragment.tuneRightValue);
					rightSeekBarValue.setText(Integer.toString(BarValue));
					ioio_thread.rightServo=BarValue;
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