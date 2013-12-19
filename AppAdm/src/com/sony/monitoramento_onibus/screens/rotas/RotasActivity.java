package com.sony.monitoramento_onibus.screens.rotas;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sony.monitoramento_onibus.R;
import com.sony.monitoramento_onibus.asynctasks.DownloadJSONTask;
import com.sony.monitoramento_onibus.asynctasks.SendPOSTTask;
import com.sony.monitoramento_onibus.model.Rota;
import com.sony.monitoramento_onibus.screens.RemoverDialog;
import com.sony.monitoramento_onibus.screens.onibus.OnibusActivity;
import com.sony.monitoramento_onibus.utils.MyLog;
import com.sony.monitoramento_onibus.utils.Utils;

public class RotasActivity extends Activity {
    ArrayList<Rota> myRotesArray;
    RemoverDialog dialog;
    private JSONObject jsonObjRotas;
    private JSONObject jsonObjPontos;
    private Spinner spRota;
    private Spinner spPontoAnt;
    private Spinner spPontoNovo;
    private Spinner spPontoPos;
    private Spinner spPontos;
    
    private boolean addRota = false;
    private boolean addPontoRota = false;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_rotes);

        this.myRotesArray = new ArrayList<Rota>();

        final ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.registerForContextMenu(this.findViewById(R.id.list_view_rotes));

        if (!Utils.isConnected(this)) {
            Utils.showNotConnected(this);
        } else {
        	new DownloadJSONTask(this).execute("http://" + Utils.getURL()
                    + "/web-api/rota");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_add_rota:
        	if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
            	addRota = true;
            	new DownloadJSONTask(this).execute("http://" + Utils.getURL()
                        + "/web-api/pontoonibus");
            }
            return true;
            
        case R.id.action_add_pontorota:
        	if (!Utils.isConnected(this)) {
                Utils.showCantWithoutConnection(this);
            } else {
            	addPontoRota = true;
            	new DownloadJSONTask(this).execute("http://" + Utils.getURL()
                        + "/web-api/pontoonibus");
            }
            return true;
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    
    private void adicionarPontoEmRota(JSONObject jsonObjPontos, JSONObject jsonObjRotas) {
    	List<String> rotas = new ArrayList<String>();
    	List<Integer> pontos = new ArrayList<Integer>();
     	try {
             JSONArray rowsArrays = jsonObjRotas.getJSONArray("rows");

             for (int i = 0; i < rowsArrays.length(); i++) {         	
            	 final String nomeRota =rowsArrays.getJSONObject(i).getString("nome");
                 rotas.add(nomeRota);
             }

         } catch (final JSONException e) {
             e.printStackTrace();
         }
     	
     	try {
            JSONArray rowsArrays = jsonObjPontos.getJSONArray("rows");

            for (int i = 0; i < rowsArrays.length(); i++) {         	
           	 final int nomePonto =rowsArrays.getJSONObject(i).getInt("id_pontoonibus");
                pontos.add(nomePonto);
            }

        } catch (final JSONException e) {
            e.printStackTrace();
        }
    	
    	final AlertDialog.Builder addRotaDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();

        addRotaDialog.setTitle(R.string.title_dialog_add_ponto_rota);

        final View view = inflater.inflate(R.layout.dialog_add_ponto_rota,
                null);

        addRotaDialog.setView(view);
        
        //rota a escolher
        ArrayAdapter<String> arrayAdapterRota = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_spinner_item, rotas);
		arrayAdapterRota.setDropDownViewResource(android.R.layout.simple_spinner_item);
	    spRota = (Spinner) view.findViewById(R.id.spinner_add_ponto_Rota);
		spRota.setAdapter(arrayAdapterRota);
		
		//ponto anterior a escolher
		ArrayAdapter<Integer> arrayAdapterPontoAnt = new ArrayAdapter<Integer>(getApplicationContext(),
				android.R.layout.simple_spinner_item, pontos);
		arrayAdapterPontoAnt.setDropDownViewResource(android.R.layout.simple_spinner_item);
	    spPontoAnt = (Spinner) view.findViewById(R.id.spPontoAnterior);
		spPontoAnt.setAdapter(arrayAdapterPontoAnt);
		
		//ponto novo a escolher
		ArrayAdapter<Integer> arrayAdapterPontoNovo = new ArrayAdapter<Integer>(getApplicationContext(),
				android.R.layout.simple_spinner_item, pontos);
		arrayAdapterPontoNovo.setDropDownViewResource(android.R.layout.simple_spinner_item);
	    spPontoNovo = (Spinner) view.findViewById(R.id.spPontoNovo);
		spPontoNovo.setAdapter(arrayAdapterPontoNovo);
		
		
		//ponto posterior a escolher
		ArrayAdapter<Integer> arrayAdapterPontoPos = new ArrayAdapter<Integer>(getApplicationContext(),
				android.R.layout.simple_spinner_item, pontos);
		arrayAdapterPontoPos.setDropDownViewResource(android.R.layout.simple_spinner_item);
	    spPontoPos = (Spinner) view.findViewById(R.id.spPontoPosterior);
		spPontoPos.setAdapter(arrayAdapterPontoPos);

        addRotaDialog.setPositiveButton(R.string.adicionar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	final String paramns ="numeroRota="+String.valueOf(RotasActivity.this.spRota.getSelectedItem())
                    			+"&pontoAnterior="+String.valueOf(RotasActivity.this.spPontoAnt.getSelectedItem())
                    			+"&pontoNovo="+String.valueOf(RotasActivity.this.spPontoNovo.getSelectedItem())
                    			+"&pontoPosterior="+String.valueOf(RotasActivity.this.spPontoPos.getSelectedItem()); 

                             new SendPOSTTask(RotasActivity.this).execute(
                                     "http://" + Utils.getURL()
                                             + "/web-api/rota/adicionarPontoRota",
                                     paramns);
                     
                    	addPontoRota=false;
                    }
                });

        addRotaDialog.setNegativeButton(R.string.cancelar,
                new DialogInterface.OnClickListener() {
        			
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	addPontoRota=false;
                        dialog.dismiss();
                    }
                });

        addRotaDialog.show();
		
	}

	private void adicionarRota(JSONObject jsonObjPontos) {
		
		List<Integer> pontos = new ArrayList<Integer>();
		
		try {
            JSONArray rowsArrays = jsonObjPontos.getJSONArray("rows");

            for (int i = 0; i < rowsArrays.length(); i++) {         	
           	 final int nomePonto =rowsArrays.getJSONObject(i).getInt("id_pontoonibus");
                pontos.add(nomePonto);
            }

        } catch (final JSONException e) {
            e.printStackTrace();
        }
		
		
        final AlertDialog.Builder addRotaDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();

        addRotaDialog.setTitle(R.string.title_dialog_add_rota);

        final View view = inflater.inflate(R.layout.dialog_add_rota,
                null);

        addRotaDialog.setView(view);
        
        ArrayAdapter<Integer> arrayAdapterPontos = new ArrayAdapter<Integer>(getApplicationContext(),
				android.R.layout.simple_spinner_item, pontos);
		arrayAdapterPontos.setDropDownViewResource(android.R.layout.simple_spinner_item);
	    spPontos = (Spinner) view.findViewById(R.id.spinner_add_rota);
		spPontos.setAdapter(arrayAdapterPontos);

        addRotaDialog.setPositiveButton(R.string.adicionar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final EditText nome = (EditText) view
                                .findViewById(R.id.editTextNome);
                        final EditText coor = (EditText) view
                                .findViewById(R.id.editTextCoor);

                        if ((nome.getText().length() > 0)
                                && (coor.getText().length() > 0)
                                ) {

                            final String paramns = "nome=" + nome.getText()
                                    + "&coord=" + coor.getText() + "&pontoInicial="
                                    + String.valueOf(RotasActivity.this.spPontos.getSelectedItem());

                            new SendPOSTTask(RotasActivity.this).execute(
                                    "http://" + Utils.getURL()
                                            + "/web-api/rota/adicionar",
                                    paramns);

                        } else {
                            MyLog.Debug("Parando");
                        }

                    }
                });

        addRotaDialog.setNegativeButton(R.string.cancelar,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        addRotaDialog.show();
    }
    
    

    public void callBackJSON(JSONObject json) {
    	 final ProgressBar progress = (ProgressBar) this
                 .findViewById(R.id.progressBarROTES);
    	
        MyLog.Info("CallBackJSON: " + json.toString());

        try {
            if (json.getInt("rowCount") > 0) {
            	if (json.getString("detail").equals("get_rotas")) {
            		jsonObjRotas = json;
            		
                    final ListView listView = (ListView) this
                            .findViewById(R.id.list_view_rotes);
                   
                    /*final Spinner Spinner = (Spinner) this
                            .findViewById(R.id.spinner_add_rota);*/
            		
	                final JSONArray rowsArrays = json.getJSONArray("rows");
	
	                this.myRotesArray.clear();
	
	                for (int i = 0; i < rowsArrays.length(); i++) {
	                    this.myRotesArray.add(new Rota(rowsArrays
	                            .getJSONObject(i).getInt("id_rota"),
	                            rowsArrays.getJSONObject(i).getString("nome")));
	                }
	
	                final RotaAdapter adapter = new RotaAdapter(
	                        this.getApplicationContext(), this.myRotesArray);
	                listView.setAdapter(adapter);
	
	                progress.setVisibility(View.GONE);
	                listView.setVisibility(View.VISIBLE);
            	}else if (json.getString("detail").equals("get_pontos")){
            		jsonObjPontos = json;
            		if(addPontoRota){
            			this.adicionarPontoEmRota(jsonObjPontos,jsonObjRotas);
            		}else if(addRota){
            			this.adicionarRota(jsonObjPontos);
            		}
            		
            	}
            	
            } else {
                progress.setVisibility(View.GONE);

            }

        } catch (final JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
        case R.id.context_menu_rotes:
            this.removerRotes((int) info.id);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.rota, menu);
        return true;
        
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_rotes, menu);
    }

    public void removerRotes(int position) {

        final Rota r = this.myRotesArray.get(position);

        final String paramns = "idRota=" + r.getID();

        this.dialog = new RemoverDialog(this);
        this.dialog.setTitle(this.getString(R.string.removing) + "...");
        this.dialog.setCancelable(true);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.show();

        new SendPOSTTask(this).execute("http://" + Utils.getURL()
                + "/web-api/rota/remover", paramns);

    }
    
    private void handlerRmRota(JSONObject json) {
        MyLog.Debug("HandlingRemRota: " + json.toString());

        final ProgressBar progressBar = (ProgressBar) this.dialog
                .findViewById(R.id.progressBarDialog);
        progressBar.setVisibility(View.GONE);

        final TextView textView = (TextView) this.dialog
                .findViewById(R.id.textViewDialog);

        try {

            if (!json.isNull("result3")) {
                final JSONObject result = json.getJSONObject("result3");

                if (!result.isNull("command")
                        && result.getString("command").equals("DELETE")
                        && !result.isNull("rowCount")
                        && (result.getInt("rowCount") == 1)) {

                    for (final Rota r : this.myRotesArray) {
                        if (r.getID() == json.getJSONObject("paramns").getInt(
                                "id_rota")) {
                            this.myRotesArray.remove(r);
                            break;
                        }
                    }

                    ((ListView) this.findViewById(R.id.list_view_rotes))
                            .invalidateViews();

                    if (this.myRotesArray.size() == 0) {
                        final TextView textViewEmpty = (TextView) this
                                .findViewById(R.id.spinner_add_rota);

                        textViewEmpty.setVisibility(View.VISIBLE);
                    }

                    this.dialog.dismiss();
                }

            } else if (!json.isNull("err")) {
                final JSONObject err = json.getJSONObject("err");

                this.dialog.setTitle(this.getString(R.string.error));
                textView.setText(err.getString("detail"));
                textView.setVisibility(View.VISIBLE);

                final Button btn = (Button) this.dialog
                        .findViewById(R.id.buttonDialog);

                btn.setVisibility(View.VISIBLE);

                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        RotasActivity.this.dialog.dismiss();
                    }
                });

            } else {
                MyLog.Debug(json.toString());
            }

        } catch (final JSONException e) {
            MyLog.Debug(json.toString());
            MyLog.Error(e.getMessage());
        }
    }
 
    public void callBackPOST(String result) {
        JSONObject json;
        
        MyLog.Info("CallBackPost: " + result);

        try {
            json = new JSONObject(result);

            if (json.getString("detail").equals("rem_rota")) {
                this.handlerRmRota(json);
            } else if (json.getString("detail").equals("add_rota")) {
                this.handlerAddRota();
            }else if (json.getString("detail").equals("add_ponto_rota")) {
                this.handlerAddPontoRota();
            }

        } catch (final JSONException e) {
            MyLog.Debug(result);
            MyLog.Error(e.getMessage());
        }

    }
       
    private void handlerAddRota() {
    	 MyLog.Info("Resultado do post");
    	  
		 Toast toast = Toast.makeText(this.getApplicationContext(), "Rota adicionada com Sucesso", Toast.LENGTH_SHORT);
		 toast.show();
    }
    
    private void handlerAddPontoRota() {
   	 MyLog.Info("Resultado do post");
   	  
		 Toast toast = Toast.makeText(this.getApplicationContext(), "Ponto adicionado a rota com Sucesso", Toast.LENGTH_SHORT);
		 toast.show();
   }
        

}
