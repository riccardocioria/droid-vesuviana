package it.vesuviana.servizi;

import java.io.IOException;
import it.vesuviana.servizi.command.CmdRetrieveSolutions;
import it.vesuviana.servizi.command.request.RetrieveSolutionsRequest;
import it.vesuviana.servizi.model.Solution;
import it.vesuviana.servizi.model.soluzioni.JSONSoluzioni;
import it.vesuviana.servizi.model.soluzioni.Soluzione;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShowSearchActivity extends ListActivity {
	static final int DIALOG_DETAIL = 0;
	protected Soluzione[] soluzioni;
	ProgressDialog dialog;
	SearchThread thread;
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	showSearched();
        	dialog.dismiss();
        }
    };
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = ProgressDialog.show(ShowSearchActivity.this, "", 
                getString(R.string.caricamento), true);
		thread = new SearchThread(handler);
		thread.start();
	}
	
	private void showSearched() {
		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Context mContext = getApplicationContext();
				Dialog dialog = new Dialog(mContext);
				
				dialog.setContentView(R.layout.detail_dialog);
				dialog.setTitle("Dettagli viaggio");
				dialog.setCancelable(true);
				
				populateDetailDialog(dialog, position);
//				dialog.setOwnerActivity(ShowSearchActivity.this);
//				dialog.show();
				//TODO show Dialog
			}
		});
		setListAdapter(new MyAdapter());
	}
	
	private void populateDetailDialog(Dialog dialog, int position) {
		TextView orarioPartenza = (TextView) dialog.findViewById(R.id.dialogOrarioPartenza);
		TextView orarioArrivo = (TextView) dialog.findViewById(R.id.dialogOrarioArrivo);
		TextView numCambi = (TextView) dialog.findViewById(R.id.dialogNumCambi);
		TextView numMezzo1 = (TextView) dialog.findViewById(R.id.dialogNumMezzo1);
		
		orarioPartenza.setText(soluzioni[position].getOraPartenza());
		orarioArrivo.setText(soluzioni[position].getOraArrivo());
		numCambi.setText(soluzioni[position].getNumCambi().toString());
		numMezzo1.setText(soluzioni[position].getNumMezzo1().toString());
	}
	
	private final class SearchThread extends Thread {
		Handler mHandler;
		
		public SearchThread(Handler h) {
			mHandler = h;
		}
		@Override 
		public void run() {
			Solution toSearch = (Solution) getIntent().getSerializableExtra("toSearch");
			try {
				JSONSoluzioni response = (JSONSoluzioni)new CmdRetrieveSolutions().execute(new RetrieveSolutionsRequest(toSearch));
				soluzioni = response.getJSONSoluzioni()[0].getSoluzioni();
				mHandler.sendEmptyMessage(RESULT_OK);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
