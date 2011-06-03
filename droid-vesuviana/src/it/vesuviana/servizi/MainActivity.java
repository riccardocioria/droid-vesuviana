package it.vesuviana.servizi;

import it.vesuviana.servizi.command.CmdRetreiveStations;
import it.vesuviana.servizi.command.request.RetreiveStationsRequest;
import it.vesuviana.servizi.db.OfflineDbOpenHelper;
import it.vesuviana.servizi.model.Preference;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends OrmLiteBaseActivity<OfflineDbOpenHelper>  {
	private final String LOG_TAG = getClass().getSimpleName();
	private final int DEFAULT_LAYOUT = R.layout.main;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			//set layout
			setContentView(getPreferredLayout());
			// riempimento delle stazioni in base al layout impostato
			fillStations(findViewById(R.id.partenza));
			fillStations(findViewById(R.id.arrivo));
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
		super.onCreateOptionsMenu(menu);
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
			} catch (SQLException e) {
				Log.e(LOG_TAG, "Database exception", e);
				Toast.makeText(this, "Database exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			} catch (IOException e) {
				Log.e(LOG_TAG, "IO exception", e);
				Toast.makeText(this, "IO exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			}
			return true;
		case R.id.spinnerLayout:
			try {
				setContentView(R.layout.main);
				setPreferredLayout(R.layout.main);
				fillStations(findViewById(R.id.partenza));
				fillStations(findViewById(R.id.arrivo));
			} catch (SQLException e) {
				Log.e(LOG_TAG, "Database exception", e);
				Toast.makeText(this, "Database exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			} catch (IOException e) {
				Log.e(LOG_TAG, "IO exception", e);
				Toast.makeText(this, "IO exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			}
			return true;
		case R.id.autoCompleteLayout:
			try {
				setContentView(R.layout.main_ac);
				setPreferredLayout(R.layout.main_ac);
				fillStations(findViewById(R.id.partenza));
				fillStations(findViewById(R.id.arrivo));
			} catch (SQLException e) {
				Log.e(LOG_TAG, "Database exception", e);
				Toast.makeText(this, "Database exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			} catch (IOException e) {
				Log.e(LOG_TAG, "IO exception", e);
				Toast.makeText(this, "IO exeption: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void fillStations(View viewId) throws IOException, SQLException {
		// get our dao
		Dao<Stazione, Integer> simpleDao = getHelper().getStazioneDao();
		// query for all of the data objects in the database
		List<Stazione> stazioni = simpleDao.queryForAll();
		
		if(stazioni.size() < 1)
			updateStations();
		
		if(viewId instanceof AutoCompleteTextView)
			fillAutoCompleteTextBox((AutoCompleteTextView)viewId, stazioni);
		if(viewId instanceof Spinner)
			fillSpinner((Spinner)viewId, stazioni);
	}

	private void fillAutoCompleteTextBox(AutoCompleteTextView textBox, List<Stazione> stazioni) throws IOException {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
		if(stazioni != null && stazioni.size() >0)
			for (Stazione s : stazioni)
				adapter.add(s.nomeStaz);
		textBox.setAdapter(adapter);
	}
	
	private void fillSpinner(Spinner spinner, List<Stazione> stazioni) throws IOException {
		ArrayAdapter<CharSequence> m_adapterForSpinner = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
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
	
	private int getPreferredLayout() throws SQLException {
		Dao<Preference, String> preferencesDao = getHelper().getPreferencesDao();
		Preference layout = preferencesDao.queryForId("layout");
		if(layout != null) {
			int preferredLayout = Integer.parseInt(layout.value);
			return preferredLayout;
		}
		return DEFAULT_LAYOUT;
	}
	
	private int setPreferredLayout(Integer preferredLayout) throws SQLException {
		Dao<Preference, String> preferencesDao = getHelper().getPreferencesDao();
		Preference layout = preferencesDao.queryForId("layout");
		if(layout != null) {
			layout.value = preferredLayout.toString();
			return preferencesDao.update(layout);
		}
		else {
			layout = new Preference();
			layout.name = "layout";
			layout.value = preferredLayout.toString();
			return preferencesDao.create(layout);
		}
	}
}