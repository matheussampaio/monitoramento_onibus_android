package com.sony.monitoramento_onibus.screens.onibus;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.sony.monitoramento_onibus.asynctasks.SendPOSTTask;
import com.sony.monitoramento_onibus.model.Onibus;
import com.sony.monitoramento_onibus.screens.RemoverDialog;
import com.sony.monitoramento_onibus.utils.MyLog;
import com.sony.monitoramento_onibus.utils.Utils;

public class OnibusActivity extends Activity {
    private ArrayList<Onibus> myOnibusArray;
    private RemoverDialog dialog;
    private Spinner spRota;
    private JSONObject jsonObj;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_onibus);

        this.myOnibusArray = new ArrayList<Onibus>();

        final ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.registerForContextMenu(this.findViewById(R.id.list_view_onibus));

        if (!Utils.isConnected(this)) {
            Utils.showNotConnected(this);
        } else {
            new DownloadJSONTask(this).execute("http://" + Utils.getURL()
                    + "/web-api/onibus");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_add_onibus:

            if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
            	
                new DownloadJSONTask(this).execute("http://" + Utils.getURL()
                            + "/web-api/rota");
            }
            return true;
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void adicionarOnibus(JSONObject json) {
    	List<String> rotas = new ArrayList<String>();
     	
     	try {
             JSONArray rowsArrays = json.getJSONArray("rows");

             for (int i = 0; i < rowsArrays.length(); i++) {         	
            	 final String nomeRota =rowsArrays.getJSONObject(i).getString("nome");
                 rotas.add(nomeRota);
             }

         } catch (final JSONException e) {
             e.printStackTrace();
         }	
    	
    	
    	final AlertDialog.Builder addDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();

        addDialog.setTitle(R.string.title_dialog_add_onibus);

        final View view = inflater.inflate(R.layout.dialog_add_onibus,
                null);
        addDialog.setView(view);
        
        ArrayAdapter<String> arrayAdapterRota = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_spinner_item, rotas);
		arrayAdapterRota.setDropDownViewResource(android.R.layout.simple_spinner_item);
	    spRota = (Spinner) view.findViewById(R.id.spinnerRota);
		spRota.setAdapter(arrayAdapterRota);


        addDialog.setPositiveButton(R.string.adicionar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                       final EditText nome = (EditText) view
                                .findViewById(R.id.editPlaca);
                        
                       final String paramns = "placa=" + nome.getText()
                               + "&nome=" + String.valueOf(OnibusActivity.this.spRota
                                       .getSelectedItem()); 

                            new SendPOSTTask(OnibusActivity.this).execute(
                                    "http://" + Utils.getURL()
                                            + "/web-api/onibus/adicionar",
                                    paramns);
                    }
                });

        addDialog.setNegativeButton(R.string.cancelar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        addDialog.show();
		
	}

	public void callBackJSON(JSONObject json) {
		jsonObj = json;
        try {
        	if (jsonObj.getInt("rowCount") > 0) {
        		final JSONArray rowsArrays = jsonObj.getJSONArray("rows");

	           if (json.getString("detail").equals("get_onibus")) {
	            	final ListView listView = (ListView) this
	                        .findViewById(R.id.list_view_onibus);
	                final ProgressBar progress = (ProgressBar) this
	                        .findViewById(R.id.progressBarO);
	
	                        for (int i = 0; i < rowsArrays.length(); i++) {
	                            this.myOnibusArray.add(new Onibus(rowsArrays
	                                    .getJSONObject(i).getInt("id_onibus"),
	                                    rowsArrays.getJSONObject(
	                                    i).getString("placa")));
	                        }
	
	                        final OnibusAdapter adapter = new OnibusAdapter(
	                                this.getApplicationContext(), this.myOnibusArray);
	                        listView.setAdapter(adapter);
	
	                        progress.setVisibility(View.GONE);
	                        listView.setVisibility(View.VISIBLE);
	                        
	            } else if (json.getString("detail").equals("get_rotas")) {
	            	this.adicionarOnibus(jsonObj);
	            }
        	 } else {
                 // TODO Tratar sem ponto de onibus;
             }

        } catch (final JSONException e) {
            MyLog.Debug(json.toString()+ "XXXXXXXXXXXXXXXXXXXXXXXXXXX");
            MyLog.Error(e.getMessage());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.getMenuInflater().inflate(R.menu.context_menu_onibus, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.onibus, menu);
        return true;
    }

    public void removerOnibus(int position) {

        final Onibus onibus = this.myOnibusArray.get(position);

        final String paramns = "idOnibus=" + onibus.getId();

        this.dialog = new RemoverDialog(this);
        this.dialog.setTitle(this.getString(R.string.removing) + "...");
        this.dialog.setCancelable(false);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.show();

        new SendPOSTTask(this).execute("http://" + Utils.getURL()
                + "/web-api/onibus/remover", paramns);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
        case R.id.context_menu_onibus:
            this.removerOnibus((int) info.id);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    public void callBackPOST(String result) {
    	JSONObject json;

        MyLog.Info("CallBackPost: " + result);

        try {
            json = new JSONObject(result);

            if (json.getString("detail").equals("rem_onibus")) {
                this.handlerRmOnibus(json);
            } else if (json.getString("detail").equals("add_onibus")) {
                this.handlerAddOnibus(json);
            }

        } catch (final JSONException e) {
            MyLog.Debug(result);
            MyLog.Error(e.getMessage());
        }

    }

	private void handlerRmOnibus(JSONObject json) {

        final ProgressBar progressBar = (ProgressBar) this.dialog
                .findViewById(R.id.progressBarDialog);
        progressBar.setVisibility(View.GONE);

        final TextView textView = (TextView) this.dialog
                .findViewById(R.id.textViewDialog);

        try {

            if (!json.isNull("name") && json.getString("name").equals("error")) {
                this.dialog.setTitle(this.getString(R.string.error));
                textView.setText(json.getString("detail"));
                textView.setVisibility(View.VISIBLE);

                final Button btn = (Button) this.dialog
                        .findViewById(R.id.buttonDialog);

                btn.setVisibility(View.VISIBLE);

                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        OnibusActivity.this.dialog.dismiss();
                    }
                });
            } else if (!json.isNull("command")
                    && json.getString("command").equals("DELETE")
                    && !json.isNull("rowCount")
                    && (json.getInt("rowCount") == 1)) {

                for (final Onibus o : this.myOnibusArray) {
                    if (o.getId() == json.getInt("idOnibus")) {
                        this.myOnibusArray.remove(o);
                        break;
                    }
                }

                ((ListView) this.findViewById(R.id.list_view_onibus))
                        .invalidateViews();
                this.dialog.dismiss();
            }

        } catch (final JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}

	private void handlerAddOnibus(JSONObject json) {
         MyLog.Info("Resultado do post");
  
		 Toast toast = Toast.makeText(this.getApplicationContext(), "Onibus adicionado com Sucesso", Toast.LENGTH_SHORT);
		 toast.show();

	}

}
