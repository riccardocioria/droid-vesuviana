package it.vesuviana.servizi;

import java.io.IOException;
import it.vesuviana.servizi.command.CmdRetrieveSolutions;
import it.vesuviana.servizi.command.request.RetrieveSolutionsRequest;
import it.vesuviana.servizi.model.Solution;
import it.vesuviana.servizi.model.soluzioni.JSONSoluzioni;
import it.vesuviana.servizi.model.soluzioni.Soluzione;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShowSearchActivity extends ListActivity {
	protected Soluzione[] soluzioni;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		//set layout
		//		setContentView(R.layout.solutions_row);
		Solution toSearch = (Solution) getIntent().getSerializableExtra("toSearch");
		try {
			JSONSoluzioni response = (JSONSoluzioni)new CmdRetrieveSolutions().execute(new RetrieveSolutionsRequest(toSearch));
			soluzioni = response.getJSONSoluzioni()[0].getSoluzioni();
//			setListAdapter(new SimpleAdapter(this, 
//					response.getSoluzioni(), 
//					android.R.layout.simple_list_item_2, 
//					new String[] {Soluzione.ORARIO_PARTENZA, Soluzione.DATA}, 
//					new int[] { android.R.id.text1, android.R.id.text2 })
//			);
			setListAdapter(new MyAdapter());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private final class MyAdapter extends BaseAdapter
	{
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			RowWrapper wrapper;
			if (convertView == null)
			{
				convertView = getLayoutInflater().inflate(
						R.layout.solutions_row, null);
				wrapper = new RowWrapper(convertView);
				convertView.setTag(wrapper);
			}
			else
			{
				wrapper = (RowWrapper) convertView.getTag();
			}
			Soluzione soluzione = (Soluzione) getItem(position);

			wrapper.poulate(soluzione);
			 
			return convertView;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public Object getItem(int position)
		{
			return soluzioni[position];
		}

		public int getCount()
		{
			return soluzioni.length;
		}
	}
	
	private static class RowWrapper
	{
		private TextView partenzaTestView;
	 
		private TextView dataTextView;
	 
		private TextView arrivoTextView;
	 
		public RowWrapper(View convertView)
		{
			partenzaTestView = (TextView) 
				convertView.findViewById(R.id.listOrarioPartenza);
			dataTextView = (TextView) 
				convertView.findViewById(R.id.listData);
			arrivoTextView = (TextView) 
				convertView.findViewById(R.id.listOrarioArrivo);
		}
	 
		public void poulate(Soluzione soluzione)
		{
			dataTextView.setText(soluzione.getDataPrimaRich());
			partenzaTestView.setText(soluzione.getOraPartenza());
			arrivoTextView.setText(soluzione.getOraArrivo());
		}
	}
}
