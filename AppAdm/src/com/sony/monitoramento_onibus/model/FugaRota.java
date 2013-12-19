package com.sony.monitoramento_onibus.model;

public class FugaRota {
    private int id;
    private String placaOnibus;
    private String inicio;
    private String fim;
    private int idOnibus;

    public FugaRota(int id, String placa, String inicio, String fim, int idOnibus) {
        this.id = id;
        this.inicio = inicio;
        this.fim = fim;
        this.placaOnibus = placa;
        this.idOnibus = idOnibus;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlacaOnibus() {
		return placaOnibus;
	}

	public void setPlacaOnibus(String placaOnibus) {
		this.placaOnibus = placaOnibus;
	}

	public String getInicio() {
		return inicio;
	}

	public void setInicio(String inicio) {
		this.inicio = inicio;
	}

	public String getFim() {
		return fim;
	}

	public void setFim(String fim) {
		this.fim = fim;
	}

	public int getIdOnibus() {
		return idOnibus;
	}

	public void setIdOnibus(int idOnibus) {
		this.idOnibus = idOnibus;
	}
    
}