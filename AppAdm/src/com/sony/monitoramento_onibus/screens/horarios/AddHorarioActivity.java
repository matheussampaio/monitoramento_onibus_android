package com.sony.monitoramento_onibus.screens.horarios;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.sony.monitoramento_onibus.R;


public class AddHorarioActivity extends Activity{
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.setContentView(R.layout.activity_add_horario);
	        
	        //NOVO  <<<<<<<<<<<<<<<<<<<
	        this.setupActionBar();
	 	}
	 	/**
	     * Set up the {@link android.app.ActionBar}.
	     */
	    private void setupActionBar() {

	        this.getActionBar().setDisplayHomeAsUpEnabled(true);

	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle presses on the action bar items
	        switch (item.getItemId()) {
	        case android.R.id.home:
	            NavUtils.navigateUpFromSameTask(this);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
}
