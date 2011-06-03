package it.vesuviana.servizi;

import java.io.IOException;

import it.vesuviana.servizi.command.CmdRetrieveSolutions;
import it.vesuviana.servizi.command.request.RetrieveSolutionsRequest;
import it.vesuviana.servizi.model.Solution;
import it.vesuviana.servizi.model.soluzioni.JSONSoluzioni;
import it.vesuviana.servizi.model.soluzioni.Soluzione;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

public class ShowSearchActivity extends ListActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		//set layout
//		setContentView(R.layout.solutions_row);
		Solution toSearch = (Solution) getIntent().getSerializableExtra("toSearch");
		try {
			JSONSoluzioni response = (JSONSoluzioni)new CmdRetrieveSolutions().execute(new RetrieveSolutionsRequest(toSearch));
			setListAdapter(new SimpleAdapter(this, 
					response.getSoluzioni(), 
					android.R.layout.simple_list_item_2, 
					new String[] {Soluzione.ORARIO_PARTENZA, Soluzione.DATA}, 
					new int[] { android.R.id.text1, android.R.id.text2 })
			);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
