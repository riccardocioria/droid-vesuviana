package it.vesuviana.servizi.command.request;

public class RetreiveStationsRequest extends Request {
	public RetreiveStationsRequest(String url) {
		super(url);
	}
	
	public RetreiveStationsRequest() {
		this("http://servizi.vesuviana.it/Orari/integrazione3/OrarioDinamico/www/FrontJS/jsonServer.asp?l=it&v=stazioni&r=listaStazioni");
	}

}
