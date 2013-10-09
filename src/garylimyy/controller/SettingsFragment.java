package garylimyy.controller;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

	
	private EditText txtTuneLeft;
	private EditText txtTuneRight;
	private EditText txtSensitivity;
	private Button btnApplyTuning;
	
	public static int tuneLeftValue=0;
	public static int tuneRightValue=0;
	public static int sensitivityValue=100;
	
	
    public SettingsFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        getActivity().setTitle("Settings");

        txtTuneLeft = (EditText) rootView.findViewById (R.id.txtTuneLeft);
		txtTuneRight = (EditText) rootView.findViewById (R.id.txtTuneRight);
		txtSensitivity = (EditText) rootView.findViewById (R.id.txtSensitivity);
		btnApplyTuning = (Button) rootView.findViewById(R.id.btnApplyTuning);


		btnApplyTuning.setOnClickListener(new OnClickListener() {
			public void onClick(View v1) {
				
				try {
					tuneLeftValue = Integer.parseInt(txtTuneLeft.getText().toString());
					tuneRightValue = Integer.parseInt(txtTuneRight.getText().toString());
					sensitivityValue = Integer.parseInt(txtSensitivity.getText().toString());
					Toast.makeText(getActivity(), "Tuning values applied!", Toast.LENGTH_SHORT).show();
				} catch(NumberFormatException nfe) {
				   System.out.println("Could not parse " + nfe);
				   Toast.makeText(getActivity(), "Tuning values invalid!", Toast.LENGTH_SHORT).show();
				} 
			}
		});
        
        return rootView;
    }
    
    
}