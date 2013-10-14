package garylimyy.controller;

import java.net.DatagramSocket;
import java.net.SocketException;

import garylimyy.threads.camera_thread_UDP;
import garylimyy.threads.ioio_thread_UDP;
import garylimyy.threads.sensors_thread_UDP;
import garylimyy.controller.ConnectionFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuTitles;

	//Threads:
	sensors_thread_UDP sensors_thread;
	camera_thread_UDP camera_thread; 
	
	//Fragments:
	public static Fragment ConnectionFragment;
	public static Fragment ControllerFragment;
	public static Fragment MapViewFragment;
	public static Fragment SettingsFragment;
	
	//Network:
	public static int IOIO_PORT = 9001; //sending port for doubot
//	public static int CAMERA_PORT = 9003; //not needed, using IOIO port
	public static int SENSORS_PORT = 9003; //to be removed once sensor thread is deleted
	public static String actualIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
        // Get IP address, set as global variable; remove? duplicated below
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		actualIpAddress = IP_Int_to_String(ipAddress);
		
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    //Placeholder code, to be re-written:
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_websearch:
        	// action for top right button
//            // create intent to perform web search for this planet
//            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//            // catch event that there's no activity to handle intent
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
    	
        // update the main content by replacing fragments
    	Fragment fragment;
    	
    	switch (position) {
	    	case 0: {	
	    		if (ConnectionFragment == null){
	    			
		    		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		    		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		    		int ipAddress = wifiInfo.getIpAddress();
		    		actualIpAddress = IP_Int_to_String(ipAddress);
		    		
		        	Bundle args = new Bundle();
		        	args.putString("actualIpAddress", actualIpAddress);
		        	ConnectionFragment = new ConnectionFragment();
		        	ConnectionFragment.setArguments(args); 
	        	}

	    		fragment = ConnectionFragment;
	        	break;
	    	}
	       	
	    	case 1: {
	        	if (ControllerFragment == null){
	        		ControllerFragment = new ControllerFragment();
	        	}
	    		fragment = ControllerFragment;
	            break;
	        }   
	    	
	    	case 2: {
	        	if (MapViewFragment == null){
	        		MapViewFragment = new MapViewFragment();
	        	}
	    		fragment = MapViewFragment;
	            break;
	        }
	    	
	    	case 3: {
	        	if (SettingsFragment == null){
	        		SettingsFragment = new SettingsFragment();
	        	}
	    		fragment = SettingsFragment;
	            break;
	        }
	    	
	    	default: {
	    		if (ControllerFragment == null){
	        		ControllerFragment = new ControllerFragment();
	        	}
	    		fragment = ControllerFragment;
	    	}
        	
    	}
    	    
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    
    
		public static String IP_Int_to_String (int ipAddress){
			
			String ipBinary = Integer.toBinaryString(ipAddress);

			//Leading zeroes are removed by toBinaryString, this will add them back.
			while(ipBinary.length() < 32) {
			    ipBinary = "0" + ipBinary;
			}

			//get the four different parts
			String a=ipBinary.substring(0,8);
			String b=ipBinary.substring(8,16);
			String c=ipBinary.substring(16,24);
			String d=ipBinary.substring(24,32);

			//Convert to numbers
			return Integer.parseInt(d,2)+"."+Integer.parseInt(c,2)+"."+Integer.parseInt(b,2)+"."+Integer.parseInt(a,2);
		}
		

	    public void onToggleClicked(View view) {
	    	((garylimyy.controller.ConnectionFragment) ConnectionFragment).onToggleClicked(view);
		}
		
}