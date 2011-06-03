package it.vesuviana.servizi.model.soluzioni;

import com.google.gson.annotations.SerializedName;

public class Soluzioni {
	@SerializedName("errore")
	public Errore errore;
	
	@SerializedName("intestazione")
	public Intestazione intestazione;
	
	@SerializedName("tariffa")
	public Tariffa tariffa;
	
	@SerializedName("soluzioni")
	public Soluzione[] soluzioni;

	/**
	 * @return the errore
	 */
	public Errore getErrore() {
		return errore;
	}

	/**
	 * @param errore the errore to set
	 */
	public void setErrore(Errore errore) {
		this.errore = errore;
	}

	/**
	 * @return the intestazione
	 */
	public Intestazione getIntestazione() {
		return intestazione;
	}

	/**
	 * @param intestazione the intestazione to set
	 */
	public void setIntestazione(Intestazione intestazione) {
		this.intestazione = intestazione;
	}

	/**
	 * @return the tariffa
	 */
	public Tariffa getTariffa() {
		return tariffa;
	}

	/**
	 * @param tariffa the tariffa to set
	 */
	public void setTariffa(Tariffa tariffa) {
		this.tariffa = tariffa;
	}

	/**
	 * @return the soluzioni
	 */
	public Soluzione[] getSoluzioni() {
		return soluzioni;
	}

	/**
	 * @param soluzioni the soluzioni to set
	 */
	public void setSoluzioni(Soluzione[] soluzioni) {
		this.soluzioni = soluzioni;
	}
}
