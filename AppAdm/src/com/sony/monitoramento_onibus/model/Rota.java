package com.sony.monitoramento_onibus.model;

public class Rota {
    private int id;
    private String nome;

    public Rota(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }
}
