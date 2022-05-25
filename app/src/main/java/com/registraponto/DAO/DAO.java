package com.registraponto.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.registraponto.DAO.anotacoes.Campo;
import com.registraponto.DAO.anotacoes.Tabela;
import com.registraponto.R;
import com.registraponto.util.Comuns;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;





public class DAO<T> extends SQLiteOpenHelper{


	private Class<?> tipo;

	private static final String BD_NOME = "registraponto";

	public static final String TAB_PREFIXO = "rgp_";

	private static final int BD_VERSAO = 4;



	private static final String[] BD_ESQUEMA ={

			"CREATE TABLE IF NOT EXISTS "+TAB_PREFIXO+"pontos (" +
					"id integer primary key autoincrement, " +
					"ano integer null," +
					"mes integer null," +
					"dia integer null," +
					"hora1 integer null," +
					"hora2 integer null," +
					"hora3 integer null," +
					"hora4 integer null," +
					"min1 integer null," +
					"min2 integer null," +
					"min3 integer null," +
					"min4 integer null," +
					"status integer null," +
					"horaTotal integer null," +
					"minTotal integer null);"};



	private Context contexto;



	public DAO(Class<?> tipo, Context contexto) {

		super(contexto, BD_NOME, null, BD_VERSAO);

		this.tipo = tipo;

		this.contexto  =contexto;

		this.preparaBD();
	}




	private boolean preparaBD(){

		try{

			if(Comuns.conexao==null)
				Comuns.conexao = this.getWritableDatabase();
		}
		catch (SQLException e){

			Comuns.mensagem( this.contexto, "Impossível conectar com a base de dados local.");
			return false;
		}

		return true;
	}




	public List<String> getTodasAsTabelasDoBD(SQLiteDatabase db){

		List<String> tabelas = new ArrayList<String>();

		Cursor cursor = db.rawQuery(
				"SELECT name FROM sqlite_master " +
						"WHERE " +
						"type='table';", null);

		if (cursor != null && cursor.getCount()>0) {

			cursor.moveToFirst();

			do {

				String nome = cursor.getString( cursor.getColumnIndex("name"));

				if(nome!=null && nome.length()>0)
					tabelas.add(nome);
			}
			while (cursor.moveToNext());
		}

		return tabelas;
	}





	@Override
	public void onCreate(SQLiteDatabase db) {
		try{

			for(String query: this.BD_ESQUEMA)
				db.execSQL(query);

		}
		catch (SQLException e){
			Comuns.mensagem( this.contexto, "Um erro ocorreu ao gerar a base de dados local.");
		}

	}





	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try{

			Comuns.mensagem( this.contexto, "Base de dados atualizada: v"+oldVersion+".0 para v"+newVersion+".0");


			List<String> tabelas = this.getTodasAsTabelasDoBD(db);

			if(tabelas!=null && tabelas.size()>0) {
				for (String nome : tabelas) {
					if (nome.contains(TAB_PREFIXO)) {

						//if(!nome.contains("usuarios"))
						db.execSQL("drop table if exists " + nome + ";");
					}
				}
			}

			this.onCreate(db);

		}
		catch (SQLException e){
			Comuns.mensagem( this.contexto, "Um erro ocorreu ao gerar a base de dados local.");
		}
	}




	/**********************************************************************************/




	public int novo( T aux){

		try {

			if (aux == null || !this.tipo.isAnnotationPresent(Tabela.class)) {

				this.msgDeErroAoSalvar();
				return -1;
			}

			String tabela = TAB_PREFIXO+this.tipo.getAnnotation(Tabela.class).nome();

			ContentValues valores = new ContentValues();

			Field[] fields = this.tipo.getDeclaredFields();

			for (Field field : fields) {

				if (field.isAnnotationPresent(Campo.class) &&
						!field.getAnnotation(Campo.class).Id() &&
						!field.getAnnotation(Campo.class).select_apenas()) {

					String campo = field.getAnnotation(Campo.class).nome();

					Object valor = this.tipo.getDeclaredMethod(field.getAnnotation(Campo.class).get()).invoke(aux);

					if (valor == null) {

						valores.putNull(campo);
						continue;
					}

					if (field.getAnnotation(Campo.class).Inteiro()) {

						String aux_val = valor.toString();

						if (aux_val.length() == 0 || Integer.parseInt(aux_val) == 0)
							valores.putNull(campo);
						else
							valores.put(campo, Integer.parseInt(aux_val));

						continue;
					}


					valores.put(campo, valor.toString());


				}
			}
			return (int) Comuns.conexao.insert(tabela, null, valores);

		}
		catch (	SQLException |
				IllegalAccessException |
				IllegalArgumentException |
				InvocationTargetException |
				NoSuchMethodException |
				SecurityException e) {

			this.msgDeErroAoSalvar();
			return -1;
		}
	}







	public List<T> get(){

		return	this.get(null, null, null);
	}






	public T get(int id){

		String where = this.tipo.getAnnotation(Tabela.class).prefixo()+".";
		for (Field field : this.tipo.getDeclaredFields()) {

			if(!field.isAnnotationPresent(Campo.class))
				continue;

			if (field.getAnnotation(Campo.class).Id()){
				where +=field.getAnnotation(Campo.class).nome();
				break;
			}
		}

		List<T> aux = this.get("", where+"="+id, "", 1);

		return aux!=null && aux.size()>0? aux.get(0):null;
	}


	public List<T> get(String join, String where, String orderBy) {

		return this.get(join, where, orderBy, null);
	}


	public List<T> get(String join, String where, String orderBy, Integer limite) {

		List<T> lista = new ArrayList<T>();

		Cursor cursor = null;

		try{

			String tabela = TAB_PREFIXO+this.tipo.getAnnotation(Tabela.class).nome();
			String tabela_prefixo = this.tipo.getAnnotation(Tabela.class).prefixo();

			Field[] fields = this.tipo.getDeclaredFields();

			StringBuilder query = new StringBuilder("SELECT ");


			for (Field field : fields) {

				if (field.isAnnotationPresent(Campo.class)) {

					query.append(
							(field.getAnnotation(Campo.class).prefixo().length() > 0 ?
									field.getAnnotation(Campo.class).prefixo() :
									tabela_prefixo)
									+ "." +
									field.getAnnotation(Campo.class).nome() +
									(field.getAnnotation(Campo.class).rotulo().length() > 0 ?
											" as " + field.getAnnotation(Campo.class).rotulo() :
											"") +
									", ");

				}
			}

			query.delete(query.length() -2, query.length());

			query.append(" FROM "+tabela+" as "+tabela_prefixo+" "+
					this.tipo.getAnnotation(Tabela.class).join() +
					(join != null && join.length()>0?" "+join.replace("###", tabela_prefixo):"")+
					(where != null && where.length()>0?" WHERE "+where.replace("###", tabela_prefixo):"")+
					(orderBy != null && orderBy.length()>0?" ORDER BY "+orderBy.replace("###", tabela_prefixo):"")+
					(limite!=null && limite>0?" LIMIT "+limite:""));

			cursor = Comuns.conexao.rawQuery(query.toString(), null);


			if (cursor != null && cursor.getCount()>0) {

				cursor.moveToFirst();

				do {

					T aux = (T) this.tipo.newInstance();

					for (Field field : fields){

						if (field.isAnnotationPresent(Campo.class)){

							int index = cursor.getColumnIndex(
									field.getAnnotation(Campo.class).rotulo().length()>0?
											field.getAnnotation(Campo.class).rotulo():
											field.getAnnotation(Campo.class).nome());


							if (field.getAnnotation(Campo.class).Inteiro())
								aux.getClass().getDeclaredMethod(field.getAnnotation(Campo.class).set(), int.class).
										invoke(aux, cursor.isNull(index)?0:cursor.getInt(index));
							else
								aux.getClass().getDeclaredMethod(field.getAnnotation(Campo.class).set(), String.class).
										invoke(aux, cursor.getString(index));
						}
					}


					lista.add(aux);
				}
				while (cursor.moveToNext());
			}

			return lista;
		}
		catch (	SQLException |
				InstantiationException |
				IllegalAccessException |
				IllegalArgumentException |
				InvocationTargetException |
				NoSuchMethodException |
				SecurityException e) {


			this.msgDeErroAoObter();

			return new ArrayList<T>();
		}
		finally {

			if(cursor!=null)
				cursor.close();
		}
	}









	public T getPrimeiroOuNada(String join, String where, String orderBy){

		List<T> lista = this.get(join, where, orderBy, 1);

		if(lista!=null && lista.size()>0)
			return lista.get(0);

		return null;
	}





	public boolean remove(int id){

		if(id<=0)
			return false;


		try{

			String tabela = TAB_PREFIXO+this.tipo.getAnnotation(Tabela.class).nome();

			String query = "";

			Field[] fields = this.tipo.getDeclaredFields();

			for (Field field : fields) {

				if (field.isAnnotationPresent(Campo.class) && field.getAnnotation(Campo.class).Id()){

					query = field.getAnnotation(Campo.class).nome()+" = "+id;
					break;
				}
			}


			return Comuns.conexao.delete(tabela, query, null)>0;
		}
		catch (	SQLException |
				IllegalArgumentException |
				SecurityException e) {


			this.msgDeErroAoDeletar();

			return false;
		}
	}






	public boolean remove(String where){

		if(where==null || where.length()==0)
			return false;

		try{

			String tabela = TAB_PREFIXO+this.tipo.getAnnotation(Tabela.class).nome();

			return Comuns.conexao.delete(tabela, where, null)>0;
		}
		catch (	SQLException |
				IllegalArgumentException |
				SecurityException e) {


			this.msgDeErroAoDeletar();

			return false;
		}
	}





	public boolean altera(T aux){

		try {

			if(aux == null || !this.tipo.isAnnotationPresent(Tabela.class)){

				this.msgDeErroAoAlterar();
				return false;
			}

			String tabela = TAB_PREFIXO+this.tipo.getAnnotation(Tabela.class).nome();

			String id = null;

			ContentValues valores = new ContentValues();

			Field[] fields = this.tipo.getDeclaredFields();

			for (Field field : fields) {

				if (field.isAnnotationPresent(Campo.class) && !field.getAnnotation(Campo.class).select_apenas()){

					if(field.getAnnotation(Campo.class).Id()){

						id = field.getAnnotation(Campo.class).nome()+" = "+
								this.tipo.getDeclaredMethod(field.getAnnotation(Campo.class).get()).invoke(aux);
						continue;
					}


					String campo = field.getAnnotation(Campo.class).nome();

					Object valor = this.tipo.getDeclaredMethod(field.getAnnotation(Campo.class).get()).invoke(aux);

					if (valor == null) {

						valores.putNull(campo);
						continue;
					}

					if (field.getAnnotation(Campo.class).Inteiro()) {

						String aux_val = valor.toString();

						if (aux_val.length() == 0 || Integer.parseInt(aux_val) == 0)
							valores.putNull(campo);
						else
							valores.put(campo, Integer.parseInt(aux_val));

						continue;
					}


					valores.put(campo, valor.toString());
				}
			}


			return Comuns.conexao.update(tabela, valores, id, null)>0;

		}
		catch (	SQLException |
				IllegalAccessException |
				IllegalArgumentException |
				InvocationTargetException |
				NoSuchMethodException |
				SecurityException e) {


			this.msgDeErroAoAlterar();

			return false;
		}
	}






	public int getCont(String join, String where){

		String id = this.tipo.getAnnotation(Tabela.class).prefixo()+".";

		for (Field field : this.tipo.getDeclaredFields()) {

			if(!field.isAnnotationPresent(Campo.class))
				continue;

			if (field.getAnnotation(Campo.class).Id()){
				id +=field.getAnnotation(Campo.class).nome();
				break;
			}
		}

		Cursor cursor = null;

		try{

			String tabela = TAB_PREFIXO+this.tipo.getAnnotation(Tabela.class).nome();
			String tabela_prefixo = this.tipo.getAnnotation(Tabela.class).prefixo();

			StringBuilder query = new StringBuilder("SELECT count(").append(id).append(") ");

			query.append(" FROM "+tabela+" as "+tabela_prefixo+" "+
					this.tipo.getAnnotation(Tabela.class).join() +
					(join != null && join.length()>0?" "+join.replace("###", tabela_prefixo):"")+
					(where != null && where.length()>0?" WHERE "+where.replace("###", tabela_prefixo):""));

			cursor = Comuns.conexao.rawQuery(query.toString(), null);

			if (cursor != null && cursor.getCount()>0) {

				cursor.moveToFirst();

				return cursor.getInt(0);
			}
		}
		catch (	SQLException |
				IllegalArgumentException |
				SecurityException e) {


			this.msgDeErroAoObter();
		}
		finally {

			if(cursor!=null)
				cursor.close();
		}

		return 0;
	}





	public void executa(String query){

		try {

			if(query==null || query.length()==0)
				return;

			Comuns.conexao.execSQL(query);

		}
		catch (	SQLException |
				IllegalArgumentException |
				SecurityException e) {


			this.msgDeErroAoAlterar();

			return;
		}
	}







	private void msgDeErroAoSalvar(){

		Comuns.mensagem(this.contexto, "Um erro ocorreu ao salvar as informações.");
	}


	private void msgDeErroAoObter(){

		Comuns.mensagem(this.contexto, "Um erro ocorreu ao solicitar as informações.");
	}


	private void msgDeErroAoDeletar(){

		Comuns.mensagem(this.contexto, "Um erro ocorreu ao remover as informações.");
	}


	private void msgDeErroAoAlterar(){

		Comuns.mensagem(this.contexto, "Um erro ocorreu ao atualizar as informações.");
	}

}
