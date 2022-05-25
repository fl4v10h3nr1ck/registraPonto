package com.registraponto.beans;

import java.io.Serializable;

import com.registraponto.DAO.anotacoes.Campo;
import com.registraponto.DAO.anotacoes.Tabela;




@Tabela(nome="pontos", prefixo="pts")
public class Ponto implements Serializable{

    @Campo(nome = "id", Inteiro = true, set = "setId", get = "getId", Id=true)
    private int id;

    @Campo(nome = "ano", Inteiro = true, set = "setAno", get = "getAno")
    private int ano;

    @Campo(nome = "mes", Inteiro = true, set = "setMes", get = "getMes")
    private int mes;

    @Campo(nome = "dia", Inteiro = true, set = "setDia", get = "getDia")
    private int dia;

    @Campo(nome = "hora1", Inteiro = true, set = "setHora1", get = "getHora1")
    private int hora1;

    @Campo(nome = "hora2", Inteiro = true, set = "setHora2", get = "getHora2")
    private int hora2;

    @Campo(nome = "hora3", Inteiro = true, set = "setHora3", get = "getHora3")
    private int hora3;

    @Campo(nome = "hora4", Inteiro = true, set = "setHora4", get = "getHora4")
    private int hora4;

    @Campo(nome = "min1", Inteiro = true, set = "setMin1", get = "getMin1")
    private int min1;

    @Campo(nome = "min2", Inteiro = true, set = "setMin2", get = "getMin2")
    private int min2;

    @Campo(nome = "min3", Inteiro = true, set = "setMin3", get = "getMin3")
    private int min3;

    @Campo(nome = "min4", Inteiro = true, set = "setMin4", get = "getMin4")
    private int min4;

    @Campo(nome = "status", Inteiro = true, set = "setStatus", get = "getStatus")
    private int status;

    @Campo(nome = "horaTotal", Inteiro = true, set = "setHoraTotal", get = "getHoraTotal")
    private int horaTotal;

    @Campo(nome = "minTotal", Inteiro = true, set = "setMinTotal", get = "getMinTotal")
    private int minTotal;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getHora1() {
        return hora1;
    }

    public void setHora1(int hora1) {
        this.hora1 = hora1;
    }

    public int getHora2() {
        return hora2;
    }

    public void setHora2(int hora2) {
        this.hora2 = hora2;
    }

    public int getHora3() {
        return hora3;
    }

    public void setHora3(int hora3) {
        this.hora3 = hora3;
    }

    public int getHora4() {
        return hora4;
    }

    public void setHora4(int hora4) {
        this.hora4 = hora4;
    }

    public int getMin1() {
        return min1;
    }

    public void setMin1(int min1) {
        this.min1 = min1;
    }

    public int getMin2() {
        return min2;
    }

    public void setMin2(int min2) {
        this.min2 = min2;
    }

    public int getMin3() {
        return min3;
    }

    public void setMin3(int min3) {
        this.min3 = min3;
    }

    public int getMin4() {
        return min4;
    }

    public void setMin4(int min4) {
        this.min4 = min4;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHoraTotal() {
        return horaTotal;
    }

    public void setHoraTotal(int horaTotal) {
        this.horaTotal = horaTotal;
    }

    public int getMinTotal() {
        return minTotal;
    }

    public void setMinTotal(int minTotal) {
        this.minTotal = minTotal;
    }

    public int[] getHorario1(){

        return new int[]{this.getHora1(), this.getMin1()};
    }

    public int[] getHorario2(){

        return new int[]{this.getHora2(), this.getMin2()};
    }

    public int[] getHorario3(){

        return new int[]{this.getHora3(), this.getMin3()};
    }

    public int[] getHorario4(){

        return new int[]{this.getHora4(), this.getMin4()};
    }

    public int[] getTotal(){

        return new int[]{this.getHoraTotal(), this.getMinTotal()};
    }


}