package it.vesuviana.servizi;

import it.vesuviana.servizi.command.CmdRetreiveStations;
import it.vesuviana.servizi.command.request.RetreiveStationsRequest;
import it.vesuviana.servizi.db.OfflineDbOpenHelper;
import it.vesuviana.servizi.model.Stazioni;
import it.vesuviana.servizi.model.Stazioni.Stazione;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class activity extends OrmLiteBaseActivity<OfflineDbOpenHelper>  {
	private final String LOG_TAG = getClass().getSimpleName();
	private ArrayAdapter<CharSequence> m_adapterForSpinner;
	private Spinner partenzaSpinner;
	private Spinner arrivoSpinner;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if(isConnected()) {
			// connesso a internet
			try {
				partenzaSpinner = (Spinner)findViewById(R.id.partenza);
				arrivoSpinner = (Spinner)findViewById(R.id.arrivo);
				
				// recupero delle stazioni
				Object response = new CmdRetreiveStations().execute(new RetreiveStationsRequest());

				@SuppressWarnings("unchecked")
				List<Stazione> stazioni = (List<Stazione>) response;
				
				Stazione partenza = new Stazione();
				
				partenza.codStazione = "0";
				partenza.nomeStaz = "Stazione";
				partenza.descrizioneBreve = "Seleziona una stazione di partenza";
				
				Collections.sort(stazioni, Stazioni.NOME_STAZIONE_COMPARATOR);
				
				stazioni.add(0, partenza);
				
				fillSpinner(partenzaSpinner, stazioni);
				fillSpinner(arrivoSpinner, stazioni);
				
				// inserimento delle stazioni nel db
				Dao<Stazione, Integer> simpleDao = getHelper().getStazioneDao();
				
				getHelper().clearTableStazione();
				
				int i = 0;
				for(Stazione s : stazioni)
					i += simpleDao.create(s);
				
				Toast.makeText(this, "record creati: OK" + i, Toast.LENGTH_LONG).show();
				
			} catch (IOException e) {
				Log.e(LOG_TAG, "Database exception", e);
				Toast.makeText(this, "IO exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			} catch (SQLException e) {
				Log.e(LOG_TAG, "Database exception", e);
				Toast.makeText(this, "Database exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			}
		}
		else {
			// non connesso
			Toast.makeText(this, R.string.avvisoNoConnection, Toast.LENGTH_LONG).show();
			
			try {
				// get our dao
				Dao<Stazione, Integer> simpleDao = getHelper().getStazioneDao();
				// query for all of the data objects in the database
				List<Stazione> stazioni = simpleDao.queryForAll();
				
				partenzaSpinner = (Spinner)findViewById(R.id.partenza);
				arrivoSpinner = (Spinner)findViewById(R.id.arrivo);
				
				fillSpinner(partenzaSpinner, stazioni);
				fillSpinner(arrivoSpinner, stazioni);
			} catch (SQLException e) {
				Log.e(LOG_TAG, "Database exception", e);
				Toast.makeText(this, "Database exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			} catch (IOException e) {
				Log.e(LOG_TAG, "Database exception", e);
				Toast.makeText(this, "IO exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	

	private void fillSpinner(Spinner spinner, List<Stazione> stazioni) throws IOException {
		m_adapterForSpinner = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		m_adapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
		spinner.setAdapter(m_adapterForSpinner);

		if(stazioni != null && stazioni.size() >0)
			for (Stazione s : stazioni)
				m_adapterForSpinner.add(s.nomeStaz);
	}


	private boolean isConnected() {
		ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connec.getNetworkInfo(0).isConnectedOrConnecting() || connec.getNetworkInfo(1).isConnectedOrConnecting()) {
			return true;
		}
		else {
			return false;           
		}
	}
}