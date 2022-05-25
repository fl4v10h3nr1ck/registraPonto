package com.registraponto;

import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.registraponto.DAO.DAO;
import com.registraponto.beans.Ponto;
import com.registraponto.componentes.ComboBox;
import com.registraponto.componentes.Tela;
import com.registraponto.util.Calculo;
import com.registraponto.util.Comuns;
import com.registraponto.util.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class TelaLista extends Tela {


    private ListaAdapter lista_de_itens;

    private DAO<Ponto> dao_pontos;

    private ComboBox ano;

    private ComboBox mes;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.tl_lista);

        this.dao_pontos = new DAO<Ponto>(Ponto.class, this);

        RecyclerView lista  = (RecyclerView) findViewById(R.id.lista_de_itens);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lista.setLayoutManager(layoutManager);

        this.lista_de_itens = new ListaAdapter(new ArrayList<>(0));
        lista.setAdapter(this.lista_de_itens);

        this.ano = (ComboBox) findViewById(R.id.ano);
        this.ano.prepara(tela, new ComboBox.ProcessadorDeEventosDeCombo(){
            public void aoMudarAEscolha(){

                mostraDados();

            }
        });

        this.ano.setDados(TelaPrincipal.getAnos());

        String anoAtual = Data.getAnoAtual();

        for(int i =0; i< this.ano.getDados().length; i++){

            if(this.ano.getDados()[i].equals(anoAtual)) {
                this.ano.setSelection(i);
                break;
            }
        }

        this.mes = (ComboBox) findViewById(R.id.mes);
        this.mes.prepara(tela, new ComboBox.ProcessadorDeEventosDeCombo(){
            public void aoMudarAEscolha(){

                mostraDados();

            }
        });
        this.mes.setDados(Comuns.MESES);

        this.mes.setSelection(Integer.parseInt(Data.getMesAtual())-1);

        TextView view = ((TextView)((View)this.findViewById(R.id.cabecalho)).findViewById(R.id.semana));
        view.setText("Dia da Semana");
        view.setTypeface(null, Typeface.BOLD);

        view = ((TextView)((View)this.findViewById(R.id.cabecalho)).findViewById(R.id.data));
        view.setText("Data");
        view.setTypeface(null, Typeface.BOLD);

        view = ((TextView)((View)this.findViewById(R.id.cabecalho)).findViewById(R.id.hora1));
        view.setText("Entrada");
        view.setTypeface(null, Typeface.BOLD);

        view = ((TextView)((View)this.findViewById(R.id.cabecalho)).findViewById(R.id.hora2));
        view.setText("Saída");
        view.setTypeface(null, Typeface.BOLD);

        view = ((TextView)((View)this.findViewById(R.id.cabecalho)).findViewById(R.id.hora3));
        view.setText("Entrada");
        view.setTypeface(null, Typeface.BOLD);

        view = ((TextView)((View)this.findViewById(R.id.cabecalho)).findViewById(R.id.hora4));
        view.setText("Saída");
        view.setTypeface(null, Typeface.BOLD);

        view = ((TextView)((View)this.findViewById(R.id.cabecalho)).findViewById(R.id.total));
        view.setText("Total");
        view.setTypeface(null, Typeface.BOLD);

        view = ((TextView)((View)this.findViewById(R.id.cabecalho)).findViewById(R.id.credito_debito));
        view.setText("Saldo");
        view.setTypeface(null, Typeface.BOLD);

        mostraDados();
    }


    public void onResume(){

        super.onResume();
    }


    private void mostraDados(){

        List<Ponto> lista = new ArrayList<Ponto>();

       // lista.add(new Ponto());

        String[] dias = TelaPrincipal.getDias(Integer.parseInt(this.ano.getSelectedItem().toString()),
                this.mes.getSelectedItemPosition()+1);

        for(String dia: dias) {


            int data[] = TelaPrincipal.getDataSelecionada(Integer.parseInt(this.ano.getSelectedItem().toString()),
                                                            this.mes.getSelectedItemPosition() + 1,
                                                                            Integer.parseInt(dia));

            Ponto ponto = this.dao_pontos.getPrimeiroOuNada(null, "###.ano = "+data[2]+
                                                                                " and ###.mes="+data[1]+
                                                                                    " and ###.dia="+data[0], null);

            if(ponto==null) {

                ponto = new Ponto();

                ponto.setAno(data[2]);
                ponto.setMes(data[1]);
                ponto.setDia(data[0]);
                ponto.setStatus(1);
            }

            lista.add(ponto);
        }

        int[] horas_mes = TelaPrincipal.setHoraMes(this.dao_pontos, Integer.parseInt(this.ano.getSelectedItem().toString()),
                                                                    this.mes.getSelectedItemPosition()+1, dias);

        ((TextView)findViewById(R.id.hora_mes)).setText(TelaPrincipal.formataHorario(horas_mes[0], horas_mes[1]));

        int[] horas_trabalhadas_mes = TelaPrincipal.getHorasTrabalhadasMes(this.dao_pontos, Integer.parseInt(this.ano.getSelectedItem().toString()),
                                                                            this.mes.getSelectedItemPosition()+1, dias);

        horas_trabalhadas_mes = Calculo.diferencaHorario(horas_trabalhadas_mes, horas_mes);

        ((TextView) findViewById(R.id.credito_mes)).setText("00:00");
        ((TextView) findViewById(R.id.credito_mes)).setTextColor(ContextCompat.getColor(tela, R.color.preto));

        if(horas_trabalhadas_mes[0]<0 || horas_trabalhadas_mes[1]<0) {

            ((TextView) findViewById(R.id.falta_mes)).setTextColor(ContextCompat.getColor(tela, R.color.preto));
            ((TextView) findViewById(R.id.credito_mes)).setTextColor(ContextCompat.getColor(tela, R.color.verde));

            ((TextView) findViewById(R.id.credito_mes)).setText(TelaPrincipal.formataHorario(horas_trabalhadas_mes[0]<0?(-1*horas_trabalhadas_mes[0]):horas_trabalhadas_mes[0],
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

        ((TextView)findViewById(R.id.falta_mes)).setText( TelaPrincipal.formataHorario(horas_trabalhadas_mes[0], horas_trabalhadas_mes[1]));


        lista_de_itens.atualizaLista(lista);
    }


    public class Linha extends RecyclerView.ViewHolder{

        public TextView semana;
        public TextView data;
        public TextView  hora1;
        public TextView  hora2;
        public TextView  hora3;
        public TextView  hora4;
        public TextView  total;
        public TextView  credito_debito;

        public View ref;


        public Linha(View itemView) {

            super(itemView);

            this.ref = itemView;

            this.semana = (TextView) itemView.findViewById(R.id.semana);
            this.data = (TextView) itemView.findViewById(R.id.data);
            this.hora1 = (TextView) itemView.findViewById(R.id.hora1);
            this.hora2 = (TextView) itemView.findViewById(R.id.hora2);
            this.hora3 = (TextView) itemView.findViewById(R.id.hora3);
            this.hora4 = (TextView) itemView.findViewById(R.id.hora4);
            this.total = (TextView) itemView.findViewById(R.id.total);
            this.credito_debito = (TextView) itemView.findViewById(R.id.credito_debito);
        }
    }


    public class ListaAdapter extends RecyclerView.Adapter<Linha> {


        private List<Ponto> itens;



        public ListaAdapter(ArrayList itens) {

            this.itens = itens;
        }


        @Override
        public Linha onCreateViewHolder(ViewGroup parent, int viewType) {

            return new Linha(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_de_itens, parent, false));
        }


        @Override
        public void onBindViewHolder(final Linha item, int position) {
/*
            if(position==0){



            }
            else {*/
                final Ponto ponto = itens.get(position);

                String data = Comuns.addPaddingAEsquerda(String.valueOf(ponto.getDia()), 2, "0") + "/" +
                        Comuns.addPaddingAEsquerda(String.valueOf(ponto.getMes()), 2, "0") + "/" + ponto.getAno();

                item.semana.setText(TelaPrincipal.getNomeDiaDaSemana(data));
                item.data.setText(data);

                if (ponto.getStatus() == 2) {

                    ((TextView) item.ref.findViewById(R.id.lb_area_feriado_descanso)).setText("Marcado como Feriado");
                    item.ref.findViewById(R.id.area_dados).setVisibility(View.GONE);
                    item.ref.findViewById(R.id.area_feriado_descanso).setVisibility(View.VISIBLE);
                } else {

                    int dia_semana = TelaPrincipal.getDiaDaSemana(data);

                    if (TelaPrincipal.isDiaDescanso(dia_semana) &&
                                    ponto.getHoraTotal() == 0 &&
                                        ponto.getMinTotal() == 0) {

                        ((TextView) item.ref.findViewById(R.id.lb_area_feriado_descanso)).setText("Descanso Semanal");
                        item.ref.findViewById(R.id.area_dados).setVisibility(View.GONE);
                        item.ref.findViewById(R.id.area_feriado_descanso).setVisibility(View.VISIBLE);
                    } else {

                        item.ref.findViewById(R.id.area_dados).setVisibility(View.VISIBLE);
                        item.ref.findViewById(R.id.area_feriado_descanso).setVisibility(View.GONE);

                        item.hora1.setText(TelaPrincipal.formataHorario(ponto.getHorario1()));
                        item.hora2.setText(TelaPrincipal.formataHorario(ponto.getHorario2()));
                        item.hora3.setText(TelaPrincipal.formataHorario(ponto.getHorario3()));
                        item.hora4.setText(TelaPrincipal.formataHorario(ponto.getHorario4()));
                        item.total.setText(TelaPrincipal.formataHorario(ponto.getTotal()));

                        int[] horario = Calculo.diferencaHorario(ponto.getTotal(), new int[]{Comuns.HORA_DIARIO, Comuns.MIN_DIARIO});

                        if (horario[0] < 0 || horario[1] < 0)
                            item.credito_debito.setTextColor(ContextCompat.getColor(tela, R.color.verde));
                        else {

                            if (horario[0] == 0 && horario[1] == 0)
                                item.credito_debito.setTextColor(ContextCompat.getColor(tela, R.color.preto));
                            else
                                item.credito_debito.setTextColor(ContextCompat.getColor(tela, R.color.vermelho));
                        }

                        item.credito_debito.setText(TelaPrincipal.formataHorario(TelaPrincipal.getModulo(horario)));
                    }
                }
           // }

            //atualizaLayout(item);
        }


        @Override
        public int getItemCount() {

            return itens != null ? itens.size() : 0;
        }


        public void atualizaLista(List<Ponto> novos){

            if(novos!=null && novos.size()>0) {

                this.itens.clear();

                this.itens.addAll(novos);

                notifyDataSetChanged();
            }
        }


    }
}


