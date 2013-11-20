package com.example.monitoramento_onibus.core;

public class Horario {

    private String horario;
    private int pontoOnibus;

    public Horario(int pontoOnibus, String horario) {
        this.pontoOnibus = pontoOnibus;
        this.horario = horario;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getPontoOnibus() {
        return pontoOnibus;
    }

    public void setPontoOnibus(int pontoOnibus) {
        this.pontoOnibus = pontoOnibus;
    }
}
