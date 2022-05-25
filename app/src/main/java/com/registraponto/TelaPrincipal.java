package com.registraponto;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.registraponto.DAO.DAO;
import com.registraponto.DAO.anotacoes.Campo;
import com.registraponto.beans.Ponto;
import com.registraponto.componentes.ComboBox;
import com.registraponto.componentes.Tela;
import com.registraponto.util.Calculo;
import com.registraponto.util.Comuns;
import com.registraponto.util.Data;


public class TelaPrincipal extends Tela {



    //private ListaAdapter lista_pontos;

    private DAO<Ponto> dao_pontos;

    private Ponto pontoAtual;

    private ComboBox ano;

    private ComboBox mes;

    private ComboBox dia;

    private int[] horas_mes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.tl_principal);

        this.dao_pontos = new DAO<Ponto>(Ponto.class, this);

        this.horas_mes= new int[2];

        this.ano = (ComboBox) findViewById(R.id.ano);
        this.ano.prepara(tela, new ComboBox.ProcessadorDeEventosDeCombo(){
            public void aoMudarAEscolha(){

                atualizaDias();

                mostraDados();

                setHoraMes();

                setTrabalhadas();

                setFaltaNoMes();
            }
        });

        this.ano.setDados(getAnos());

        this.mes = (ComboBox) findViewById(R.id.mes);
        this.mes.prepara(tela, new ComboBox.ProcessadorDeEventosDeCombo(){
            public void aoMudarAEscolha(){

                atualizaDias();

                mostraDados();

                setHoraMes();

                setTrabalhadas();

                setFaltaNoMes();
            }
        });
        this.mes.setDados(Comuns.MESES);

        this.dia = (ComboBox) findViewById(R.id.dia);
        this.dia.prepara(tela, new ComboBox.ProcessadorDeEventosDeCombo(){
            public void aoMudarAEscolha(){

                mostraDados();

                setTrabalhadas();

                setFaltaNoMes();
            }
        });

        findViewById(R.id.hora1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                int hora = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(tela, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        pontoAtual.setHora1(hourOfDay);
                        pontoAtual.setMin1(minute);

                        ((TextView)findViewById(R.id.hora1)).setText(formataHorario(pontoAtual.getHora1(), pontoAtual.getMin1()));

                        setTrabalhadas();
                    }
                }, hora, min, false);

                timePickerDialog.show();
            }
        });


        findViewById(R.id.hora2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                int hora = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(tela, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        pontoAtual.setHora2(hourOfDay);
                        pontoAtual.setMin2(minute);

                        ((TextView)findViewById(R.id.hora2)).setText(formataHorario(pontoAtual.getHora2(), pontoAtual.getMin2()));

                        setTrabalhadas();
                    }
                }, hora, min, false);

                timePickerDialog.show();
            }
        });

        findViewById(R.id.hora3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                int hora = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(tela, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        pontoAtual.setHora3(hourOfDay);
                        pontoAtual.setMin3(minute);

                        ((TextView)findViewById(R.id.hora3)).setText(formataHorario(pontoAtual.getHora3(), pontoAtual.getMin3()));

                        setTrabalhadas();
                    }
                }, hora, min, false);

                timePickerDialog.show();
            }
        });

        findViewById(R.id.hora4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                int hora = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(tela, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                        pontoAtual.setHora4(hourOfDay);
                        pontoAtual.setMin4(minute);

                        ((TextView)findViewById(R.id.hora4)).setText(formataHorario(pontoAtual.getHora4(), pontoAtual.getMin4()));

                        setTrabalhadas();
                    }
                }, hora, min, false);

                timePickerDialog.show();
            }
        });


        findViewById(R.id.bt_salvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pontoAtual.getHora1()>0 &&
                        pontoAtual.getHora2()>0 &&
                            Calculo.comparaHorario(pontoAtual.getHorario1(), pontoAtual.getHorario2())>=0){

                    Comuns.mensagem(tela, "Quadra de horários com intervalos inválidos.");
                    return;
                }

                if(pontoAtual.getHora2()>0 &&
                        pontoAtual.getHora3()>0 &&
                            Calculo.comparaHorario(pontoAtual.getHorario2(), pontoAtual.getHorario3())>=0){

                    Comuns.mensagem(tela, "Quadra de horários com intervalos inválidos.");
                    return;
                }

                if(pontoAtual.getHora3()>0 &&
                        pontoAtual.getHora4()>0 && Calculo.comparaHorario(pontoAtual.getHorario3(), pontoAtual.getHorario4())>=0){

                    Comuns.mensagem(tela, "Quadra de horários com intervalos inválidos.");
                    return;
                }

                if(pontoAtual.getId()>0)
                    dao_pontos.altera(pontoAtual);
                else
                    dao_pontos.novo(pontoAtual);


                mostraDados();

                setFaltaNoMes();

                getOutrosDados();

                Comuns.mensagem(tela, "Salvo com sucesso.");
            }
        });

        findViewById(R.id.bt_feriado).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pontoAtual.setStatus(2);

                pontoAtual.setHora1(0);
                pontoAtual.setHora2(0);
                pontoAtual.setHora3(0);
                pontoAtual.setHora4(0);

                pontoAtual.setMin1(0);
                pontoAtual.setMin2(0);
                pontoAtual.setMin3(0);
                pontoAtual.setMin4(0);

                pontoAtual.setHoraTotal(0);
                pontoAtual.setMinTotal(0);

                if(pontoAtual.getId()>0)
                    dao_pontos.altera(pontoAtual);
                else
                    dao_pontos.novo(pontoAtual);

                mostraDados();

                setHoraMes();

                setTrabalhadas();

                setFaltaNoMes();

                Comuns.mensagem(tela, "Salvo com sucesso.");
            }
        });

        findViewById(R.id.bt_desmarcar_feriado).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pontoAtual.setStatus(1);

                dao_pontos.altera(pontoAtual);

                mostraDados();

                setHoraMes();

                setTrabalhadas();

                setFaltaNoMes();

                Comuns.mensagem(tela, "Salvo com sucesso.");
            }
        });

        findViewById(R.id.bt_relatorio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(tela, TelaLista.class);
                tela.startActivity(i);
            }
        });

        int diaAtual = Integer.parseInt(Data.getDiaAtual());

        int mesAtual = Integer.parseInt(Data.getMesAtual());

        int anoAtual = Integer.parseInt(Data.getAnoAtual());

        if(diaAtual>27) {
            mesAtual++;

            if(mesAtual== 13) {
                mesAtual = 0;
                anoAtual++;
            }
        }

        this.mes.setSelection(mesAtual-1);

        for(int i =0; i< this.ano.getDados().length; i++){

            if(this.ano.getDados()[i].equals(String.valueOf(anoAtual))) {
                this.ano.setSelection(i);
                break;
            }
        }

        atualizaDias();

        mostraDados();

        ((TextView)findViewById(R.id.diario)).setText(formataHorario(Comuns.HORA_DIARIO, Comuns.MIN_DIARIO));

        setHoraMes();

        setTrabalhadas();

        setFaltaNoMes();
    }


    public void onResume(){

        super.onResume();
    }


    public static String[] getAnos(){

        int ano_atual = Integer.parseInt(Data.getAnoAtual());

        String[] anos = new String[(ano_atual - Comuns.ANO_INICIAL)+1];

        for(int i = Comuns.ANO_INICIAL, j =0; i<=ano_atual; i++,j++)
            anos[j] = String.valueOf(i);

        return anos;
    }


    public void atualizaDias(){

        this.dia.setDados(getDias(Integer.parseInt(ano.getSelectedItem().toString()), mes.getSelectedItemPosition()+1));

        int diaAtual  = Integer.parseInt(Data.getDiaAtual());

        for(int i =0; i< this.dia.getDados().length; i++){

            if(Integer.parseInt(this.dia.getDados()[i]) == diaAtual) {
                this.dia.setSelection(i);
                break;
            }
        }
    }


    public static String[] getDias(int ano, int mes){

        int mesSelecionado = mes;

        boolean tem31 = Data.validaData(Data.getEmFormatoDeData(ano,
                mesSelecionado == 1?12:mesSelecionado-1, 31));

        boolean bissexto = Data.validaData(Data.getEmFormatoDeData(ano,
                2, 29));

        List<Integer> dias=  new ArrayList<>();

        dias.add(28);

        if(mesSelecionado != 3){

            dias.add(29);
            dias.add(30);

            if(tem31)
                dias.add(31);
        }
        else{

            if(bissexto)
                dias.add(29);
        }


        for(int i = 1; i<= 27; i++)
            dias.add(i);

        String[] dias_aux = new String[dias.size()];

        for(int i =0; i< dias.size(); i++)
            dias_aux[i] = Comuns.addPaddingAEsquerda(dias.get(i).toString(), 2, "0");

        return dias_aux;
    }


    private void mostraDados(){

        int data[] = getDataSelecionada();

        String aux  = Comuns.addPaddingAEsquerda(String.valueOf(data[0]), 2, "0")+"/"+
                        Comuns.addPaddingAEsquerda(String.valueOf(data[1]), 2, "0")+"/"+data[2];

        ((TextView) findViewById(R.id.data)).setText( getNomeDiaDaSemana(aux)+", "+aux);

        pontoAtual = this.dao_pontos.getPrimeiroOuNada(null, "###.ano = "+data[2]+
                                                                        " and ###.mes="+data[1]+
                                                                        " and ###.dia="+data[0], null);

         if(pontoAtual==null){

             pontoAtual = new Ponto();

             pontoAtual.setAno(data[2]);
             pontoAtual.setMes(data[1]);
             pontoAtual.setDia(data[0]);
             pontoAtual.setStatus(1);

             findViewById(R.id.area_ponto).setVisibility(View.VISIBLE);
             findViewById(R.id.area_feriado).setVisibility(View.GONE);

             ((TextView) findViewById(R.id.lb_bt_salvar)).setText("Salvar");
         }
         else{

             if(pontoAtual.getStatus()==2){

                 findViewById(R.id.area_ponto).setVisibility(View.GONE);
                 findViewById(R.id.area_feriado).setVisibility(View.VISIBLE);
             }
             else{

                 findViewById(R.id.area_ponto).setVisibility(View.VISIBLE);
                 findViewById(R.id.area_feriado).setVisibility(View.GONE);

                 ((TextView) findViewById(R.id.lb_bt_salvar)).setText("Atualizar");

             }
         }

        ((TextView)findViewById(R.id.hora1)).setText(formataHorario(pontoAtual.getHora1(), pontoAtual.getMin1()));
        ((TextView)findViewById(R.id.hora2)).setText(formataHorario(pontoAtual.getHora2(), pontoAtual.getMin2()));
        ((TextView)findViewById(R.id.hora3)).setText(formataHorario(pontoAtual.getHora3(), pontoAtual.getMin3()));
        ((TextView)findViewById(R.id.hora4)).setText(formataHorario(pontoAtual.getHora4(), pontoAtual.getMin4()));
    }


    private void setHoraMes(){

        this.horas_mes = setHoraMes(this.dao_pontos, Integer.parseInt(this.ano.getSelectedItem().toString()),
                                            this.mes.getSelectedItemPosition()+1, this.dia.getDados());

        ((TextView)findViewById(R.id.hora_mes)).setText(formataHorario(this.horas_mes[0], this.horas_mes[1]));
    }


    public static int[] setHoraMes(DAO<Ponto> dao_pontos, int ano, int mes, String[] dias){

        int[] horas_mes = new int[2];
        horas_mes[0] = 0;
        horas_mes[1] = 0;

        for(int i =0; i< dias.length; i++){

            int data[] = getDataSelecionada(ano, mes, Integer.parseInt(dias[i]));

            int dia_semana = getDiaDaSemana(data[0]+"/"+data[1]+"/"+data[2]);

            if(isDiaDescanso(dia_semana))
                continue;
            else{

                Ponto pontoAux = dao_pontos.getPrimeiroOuNada(null, "###.ano = "+data[2]+
                        " and ###.mes="+data[1]+
                        " and ###.dia="+data[0]+
                        " and ###.status=2", null);
                if(pontoAux!=null) //exclui dias uteis marcados como feriado
                    continue;
            }

            horas_mes = Calculo.somaHorario(horas_mes, new int[]{Comuns.HORA_DIARIO, Comuns.MIN_DIARIO});
        }

        return horas_mes;
    }


    private void setTrabalhadas(){

        int[] horario = new int[2];

        horario[0]  =0;
        horario[1]  =0;

        int[] intervalo1 = null;

        int[] intervalo2 = null;

        if(pontoAtual.getHora1()>0 && pontoAtual.getHora2() >0 &&
                Calculo.comparaHorario(new int[]{pontoAtual.getHora1(), pontoAtual.getMin1()}, new int[]{pontoAtual.getHora2(), pontoAtual.getMin2()})<0)
            intervalo1 = Calculo.diferencaHorario(new int[]{this.pontoAtual.getHora1(), this.pontoAtual.getMin1()}, new int[]{this.pontoAtual.getHora2(), this.pontoAtual.getMin2()});

        if(pontoAtual.getHora3()>0 && pontoAtual.getHora4() >0 &&
                (pontoAtual.getHora2()==0 ||
                        Calculo.comparaHorario(new int[]{pontoAtual.getHora2(), pontoAtual.getMin2()}, new int[]{pontoAtual.getHora3(), pontoAtual.getMin3()})<0) &&

                Calculo.comparaHorario(new int[]{pontoAtual.getHora3(), pontoAtual.getMin3()}, new int[]{pontoAtual.getHora4(), pontoAtual.getMin4()})<0)
            intervalo2 = Calculo.diferencaHorario(new int[]{this.pontoAtual.getHora3(), this.pontoAtual.getMin3()}, new int[]{this.pontoAtual.getHora4(), this.pontoAtual.getMin4()});

        if(intervalo1==null){

            intervalo1 = new int[2];

            intervalo1[0]  =0;
            intervalo1[1]  =0;
        }

        if(intervalo2==null){

            intervalo2 = new int[2];

            intervalo2[0]  =0;
            intervalo2[1]  =0;
        }

        horario = Calculo.somaHorario(intervalo1, intervalo2);

        this.pontoAtual.setHoraTotal(horario[0]);

        this.pontoAtual.setMinTotal(horario[1]);

        ((TextView)findViewById(R.id.trabalhadas)).setText(formataHorario(this.pontoAtual.getHoraTotal(), this.pontoAtual.getMinTotal()));

        horario = Calculo.diferencaHorario(horario, new int[]{Comuns.HORA_DIARIO, Comuns.MIN_DIARIO});

        ((TextView) findViewById(R.id.credito_dia)).setText("00:00");
        ((TextView) findViewById(R.id.credito_dia)).setTextColor(ContextCompat.getColor(tela, R.color.preto));

        if(horario[0]<0 || horario[1]<0){

            ((TextView) findViewById(R.id.falta_dia)).setTextColor(ContextCompat.getColor(tela, R.color.preto));

            ((TextView) findViewById(R.id.credito_dia)).setTextColor(ContextCompat.getColor(tela, R.color.verde));

            ((TextView) findViewById(R.id.credito_dia)).setText(formataHorario(horario[0]<0?(-1*horario[0]):horario[0],
                                                                                    horario[1]<0?(-1*horario[1]):horario[1]));

            horario[0] = 0;
            horario[1] = 0;
        }
        else {

            if(horario[0]==0 && horario[1]==0)
                ((TextView) findViewById(R.id.falta_dia)).setTextColor(ContextCompat.getColor(tela, R.color.preto));
            else
                ((TextView) findViewById(R.id.falta_dia)).setTextColor(ContextCompat.getColor(tela, R.color.vermelho));
        }

        ((TextView) findViewById(R.id.falta_dia)).setText(formataHorario(horario[0], horario[1]));

        getOutrosDados();
    }


    private int[] getDataSelecionada() {

        return getDataSelecionada(Integer.parseInt(this.dia.getSelectedItem().toString()));
    }


    private int[] getDataSelecionada(int dia) {

        return getDataSelecionada( Integer.parseInt(this.ano.getSelectedItem().toString()),
                                    this.mes.getSelectedItemPosition()+1, dia);
    }


    public static int[] getDataSelecionada(int ano, int mes, int dia) {

        int[] data = new int[3];

        data[0] = dia;

        if (data[0] > 27) {

            if (mes == 1) {

                data[1] = 12;

                data[2] = ano - 1;
            }
            else {

                data[1] = mes-1;

                data[2] = ano;
            }
        }
        else {

            data[1] = mes;

            data[2] = ano;
        }

        return data;
    }


    private void setFaltaNoMes(){

        int[] horas_trabalhadas_mes = getHorasTrabalhadasMes(this.dao_pontos, Integer.parseInt(this.ano.getSelectedItem().toString()),
                                                                            this.mes.getSelectedItemPosition()+1, this.dia.getDados());

        horas_trabalhadas_mes = Calculo.diferencaHorario(horas_trabalhadas_mes, horas_mes);

        ((TextView) findViewById(R.id.credito_mes)).setText("00:00");
        ((TextView) findViewById(R.id.credito_mes)).setTextColor(ContextCompat.getColor(tela, R.color.preto));

        if(horas_trabalhadas_mes[0]<0 || horas_trabalhadas_mes[1]<0) {

            ((TextView) findViewById(R.id.falta_mes)).setTextColor(ContextCompat.getColor(tela, R.color.preto));
            ((TextView) findViewById(R.id.credito_mes)).setTextColor(ContextCompat.getColor(tela, R.color.verde));

            ((TextView) findViewById(R.id.credito_mes)).setText(formataHorario(horas_trabalhadas_mes[0]<0?(-1*horas_trabalhadas_mes[0]):horas_trabalhadas_mes[0],
                                                                    horas_trabalhadas_mes[1]<0?(-1*horas_trabalhadas_mes[1]):horas_trabalhadas_mes[1]));

            horas_trabalhadas_mes[0] = 0;
            horas_trabalhadas_mes[1] = 0;
        }
        else{

            if(horas_trabalhadas_mes[0]==0 && horas_trabalhadas_mes[1]==0)
                ((TextView) findViewById(R.id.falta_mes)).setTextColor(ContextCompat.getColor(tela, R.color.preto));
            else
                ((TextView) findViewById(R.id.falta_mes)).setTextColor(ContextCompat.getColor(tela, R.color.vermelho));
        }

        ((TextView)findViewById(R.id.falta_mes)).setText(formataHorario(horas_trabalhadas_mes[0], horas_trabalhadas_mes[1]));
    }


    public static int[] getHorasTrabalhadasMes(DAO<Ponto> dao_pontos, int ano, int mes, String[] dias){

        int[] horas_trabalhadas_mes = new int[2];
        horas_trabalhadas_mes[0] = 0;
        horas_trabalhadas_mes[1] = 0;

        for(int i =0; i< dias.length; i++) {

            int data[] = getDataSelecionada(ano, mes, Integer.parseInt(dias[i]));

            Ponto pontoAux = dao_pontos.getPrimeiroOuNada(null, "###.ano = "+data[2]+
                    " and ###.mes="+data[1]+
                    " and ###.dia="+data[0]+
                    " and ###.status=1", null);
            if(pontoAux!=null)
                horas_trabalhadas_mes = Calculo.somaHorario(horas_trabalhadas_mes, new int[]{pontoAux.getHoraTotal(), pontoAux.getMinTotal()});
        }

        return horas_trabalhadas_mes;
    }


    public static String formataHorario(int[] horario) {

        return formataHorario(horario[0], horario[1]);
    }


    public static String formataHorario(int hora, int min){

        return Comuns.addPaddingAEsquerda( String.valueOf(hora), 2, "0")+":"+
                                Comuns.addPaddingAEsquerda( String.valueOf(min), 2, "0");
    }


    public static int getDiaDaSemana(String data){

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Brazil/East"));

        calendar.setTime(Data.converteStringParaData(data));

        return calendar.get(Calendar.DAY_OF_WEEK);
    }


    public static String getNomeDiaDaSemana(String data){

        String nome = "";

        switch (getDiaDaSemana(data)){

            case Calendar.MONDAY:  nome = "Segunda-feira";break;
            case Calendar.TUESDAY:  nome = "Terça-feira";break;
            case Calendar.WEDNESDAY:  nome = "Quarta-feira";break;
            case Calendar.THURSDAY:  nome = "Quinta-feira";break;
            case Calendar.FRIDAY:  nome = "Sexta-feira";break;
            case Calendar.SATURDAY:  nome = "Sábado";break;
            case Calendar.SUNDAY:  nome = "Domingo";
        }

        return nome;
    }


    public static int[] getModulo(int[] horario){

        horario[0] = horario[0]<0?(-1*horario[0]):horario[0];
        horario[1] = horario[1]<0?(-1*horario[1]):horario[1];

        return horario;
    }


    public void getOutrosDados(){

        int[] cumprir= new  int[2];
        cumprir[0]  =0;
        cumprir[1]  =0;

        int[] cumprido= new  int[2];
        cumprido[0]  =0;
        cumprido[1]  =0;

        int diaAtual = Integer.parseInt(this.dia.getSelectedItem().toString());

        for(String dia:  this.dia.getDados()){

            int dia_int = Integer.parseInt(dia);

            int data[] = getDataSelecionada(dia_int);

            int dia_semana = getDiaDaSemana(data[0]+"/"+data[1]+"/"+data[2]);

            Ponto pontoAux = dao_pontos.getPrimeiroOuNada(null, "###.ano = "+data[2]+
                                                                            " and ###.mes="+data[1]+
                                                                            " and ###.dia="+data[0] , null);
            if(pontoAux!=null){

                if(pontoAux.getStatus()!=2){

                    if(!isDiaDescanso(dia_semana))   //exclui sabados e domingos
                        cumprir = Calculo.somaHorario(cumprir, new int[]{Comuns.HORA_DIARIO, Comuns.MIN_DIARIO});

                    cumprido = Calculo.somaHorario(cumprido, pontoAux.getTotal());
                }

            }
            else{

                if(isDiaDescanso(dia_semana))   //exclui sabados e domingos
                    continue;
                else
                    cumprir = Calculo.somaHorario(cumprir, new int[]{Comuns.HORA_DIARIO, Comuns.MIN_DIARIO});
            }

            if(dia_int == diaAtual)
                break;
        }

        ((TextView)findViewById(R.id.cumprir)).setText(formataHorario(cumprir[0], cumprir[1]));
        ((TextView)findViewById(R.id.cumprido)).setText(formataHorario(cumprido[0], cumprido[1]));

        int[] diferenca = Calculo.diferencaHorario(cumprido, cumprir);

        if(diferenca[0]==0 && diferenca[1]==0)
            ((TextView) findViewById(R.id.saldo)).setTextColor(ContextCompat.getColor(tela, R.color.preto));
        else {

            if (diferenca[0] < 0 || diferenca[1] < 0)
                ((TextView) findViewById(R.id.saldo)).setTextColor(ContextCompat.getColor(tela, R.color.verde));
            else
                ((TextView) findViewById(R.id.saldo)).setTextColor(ContextCompat.getColor(tela, R.color.vermelho));
        }

        ((TextView) findViewById(R.id.saldo)).setText(formataHorario(diferenca[0]<0?(-1*diferenca[0]):diferenca[0],
                                                                    diferenca[1]<0?(-1*diferenca[1]):diferenca[1]));
    }

    public static boolean isDiaDescanso(int dia_semana){

       return dia_semana == Calendar.SATURDAY || dia_semana == Calendar.SUNDAY;
    }

}


