package in.nic.phra.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import in.nic.phra.app.welcomefragments.PrimaryRegistration;
import in.nic.phra.app.welcomefragments.PrimaryRegistrationRecordDetails;

public class WelcomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PrimaryRegistration.OnFragmentInteractionListener,
        PrimaryRegistrationRecordDetails.OnFragmentInteractionListener {
    private static final String TAG = "WelcomeActivity";
    private long backPressOne = 0;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sharedPreferences = getApplicationContext().getSharedPreferences("userSession", MODE_PRIVATE);


        //if statement: to prevent the reset in the event of change of the orientation of the screen
        //if block: to have a default fragment for the screen(optional)
//        if (savedInstanceState == null) {
//            Fragment fragment = null;
//            Class fragmentClass;
//            fragmentClass = PrimaryRegistration.class;
//            try {
//                fragment = (Fragment) fragmentClass.newInstance();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.forms_fragment, fragment).commit();
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        //drawer.setDrawerListener(toggle); deprecated using addDrawerListener instead
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String userFullName = sharedPreferences.getString("User_FullName", "<user_full_name>");
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Hello " + userFullName.substring(0, 1).toUpperCase() + userFullName.substring(1));
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction().commit();
        } else if (backPressOne == 0) {
            backPressOne = System.currentTimeMillis();
            Toast.makeText(this, "Press Back button again to exit", Toast.LENGTH_LONG).show();
        } else {
            if (System.currentTimeMillis() - backPressOne < 2000) { //waits for 2 second and listens for back button otherwise show the text again
                super.onBackPressed();
            } else {
                backPressOne = 0;
                Toast.makeText(this, "Press Back button again to exit", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_primary_registration) {
            fragmentClass = PrimaryRegistration.class;
            Log.i(TAG, "Changing Fragment to Primary Registration");
        } else if (id == R.id.nav_user_logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getFragmentManager();
            //adds frags to backstack to make back action viable see onBackPressed()
            if (fragment != null) {
                fragmentManager.beginTransaction().replace(R.id.forms_fragment, fragment).addToBackStack(fragment.getClass().getName()).commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
