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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends OrmLiteBaseActivity<OfflineDbOpenHelper>  {
	private final String LOG_TAG = getClass().getSimpleName();
	private ArrayAdapter<CharSequence> m_adapterForSpinner;
	private Spinner partenzaSpinner;
	private Spinner arrivoSpinner;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			// get our dao
			Dao<Stazione, Integer> simpleDao = getHelper().getStazioneDao();
			// query for all of the data objects in the database
			List<Stazione> stazioni = simpleDao.queryForAll();
			
			if(stazioni.size() < 1)
				updateStations();
			
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.update:
			try {
				updateStations();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Database exception", e);
				Toast.makeText(this, "Database exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			} catch (SQLException e) {
				Log.e(LOG_TAG, "IO exception", e);
				Toast.makeText(this, "IO exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
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

	private void updateStations() throws IOException, SQLException {
		if (isConnected()) {
			// recupero delle stazioni
			Object response = new CmdRetreiveStations().execute(new RetreiveStationsRequest());

			@SuppressWarnings("unchecked")
			List<Stazione> stazioni = (List<Stazione>) response;

			Collections.sort(stazioni, Stazioni.NOME_STAZIONE_COMPARATOR);

			// inserimento delle stazioni nel db
			Dao<Stazione, Integer> simpleDao = getHelper().getStazioneDao();

			getHelper().clearTableStazione();

			int i = 0;
			for(Stazione s : stazioni)
				i += simpleDao.create(s);

			Toast.makeText(this, "Record creati: " + i, Toast.LENGTH_LONG).show();
		}
		else {
			throw new IOException("Attenzione! Non è presente alcuna connessione, i dati potrebbero essere non aggiornati.");
		}
	}
}