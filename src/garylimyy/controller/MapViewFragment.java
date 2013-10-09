package garylimyy.controller;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MapViewFragment extends Fragment {
	
	//Sensors:
	private TextView txtviewAcc1;
	private TextView txtviewAcc2;
	private TextView txtviewAcc3;
	private TextView txtviewDis1;
	private TextView txtviewDis2;
	private TextView txtviewDis3;
	private TextView txtviewGps1;
	private TextView txtviewGps2;
		
	static Handler handlerSensors;
	
    public MapViewFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_status, container, false);
        
        getActivity().setTitle("Status");
        
    	txtviewAcc1 = (TextView) rootView.findViewById (R.id.txtacc1);
		txtviewAcc2 = (TextView) rootView.findViewById (R.id.txtacc2);
		txtviewAcc3 = (TextView) rootView.findViewById (R.id.txtacc3);
		txtviewDis1 = (TextView) rootView.findViewById (R.id.txtdis1);
		txtviewDis2 = (TextView) rootView.findViewById (R.id.txtdis2);
		txtviewDis3 = (TextView) rootView.findViewById (R.id.txtdis3);
		txtviewGps1 = (TextView) rootView.findViewById (R.id.txtgps1);
		txtviewGps2 = (TextView) rootView.findViewById (R.id.txtgps2);
        
		//Handler to refresh UI. will be re-written!
		handlerSensors = new Handler() { 
			public void handleMessage(Message msg) {
				txtviewAcc1.setText(Float.toString(txtacc1));
				txtviewAcc2.setText(Float.toString(txtacc2));
				txtviewAcc3.setText(Float.toString(txtacc3));
				txtviewDis1.setText(Float.toString(txtdis1));
				txtviewDis2.setText(Float.toString(txtdis2));
				txtviewDis3.setText(Float.toString(txtdis3));
				txtviewGps1.setText(Float.toString(txtgps1));
				txtviewGps2.setText(Float.toString(txtgps2));
			}
		};
        
        return rootView;
    }
    

	static float txtacc1, txtacc2, txtacc3, txtdis1, txtdis2, txtdis3, txtgps1, txtgps2 = 0;
	public static void set_sensors_values(float acc1, float acc2, float acc3, float dis1, float dis2, float dis3, float gps1, float gps2) {
		txtacc1 = acc1;
		txtacc2 = acc2;
		txtacc3 = acc3;
		txtdis1 = dis1;
		txtdis2 = dis2;
		txtdis3 = dis3;
		txtgps1 = gps1;
		txtgps2 = gps2;
		handlerSensors.sendEmptyMessage(0);
	
		//Log.d("SettingsFragment","sensor refreshed ");
	}
    

    
}