package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

public class AddDataInsert {

	public static Connection con = null;
	public static Properties conf;	
	public static String user_id ;
	public static String user_passwd;
	public static String dbip ;
	public static String dbport ;
	public static String dbname ;
	public static String url ;
	public static String CAHRSET;
	public static String ORG_PATH;
	public static String ORG_PATH2;
	public static String con_path;
	public static String WRITE_PATH;
	public static String WRITE_PATH2;
	public static String WRITE_PATH3;
	static final String driver[] = { "oracle.jdbc.driver.OracleDriver",
		"com.microsoft.sqlserver.jdbc.SQLServerDriver",
		"com.mysql.jdbc.Driver" };
	public static Statement codeStmt = null;
	public static Statement con_codeStmt = null;

	public static ResultSet codedataRs = null;
	public static ResultSet con_codedataRs = null;
	public static ResultSet cnt_job = null;
	public static java.sql.Statement stmt = null;
	public static int COMPARETYPE_NAME =0;
    public static int COMPARETYPE_DATE = 0;
    public static PreparedStatement pstmt_update = null;
    
    public static void main(String[] args) throws  InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException  {
		String config = args[0];
		//file_path = args[1];
		FileInputStream fis = new FileInputStream(config);
		conf = new Properties();
		conf.load(new java.io.BufferedInputStream(fis));

		String dbkind = conf.getProperty("DB_KIND").toUpperCase();
		 dbip = conf.getProperty("DB_IP");
		 dbport = conf.getProperty("DB_PORT");
		 dbname = conf.getProperty("DB_NAME");
		 user_id = conf.getProperty("USER");
		 user_passwd = conf.getProperty("PW");		 
		 
		CAHRSET = conf.getProperty("CAHRSET");
	
	
		 fis.close();
		 
		 long startTime = System.currentTimeMillis();
		 
		 try{
		    	if( dbkind.equals("ORACLE") ){
					Class.forName(driver[0].toString()).newInstance();
				}else if( dbkind.equals("MSSQL") ){
					Class.forName(driver[1].toString()).newInstance();
				}else if( dbkind.equals("MYSQL") ){
					Class.forName(driver[2].toString()).newInstance();
				}
			  
			    url = connUrlString(dbkind,dbip,dbport,dbname);	
			    
			    System.out.println("url : "+url); 
			    
				con = DriverManager.getConnection(url , user_id, user_passwd); 
				con.setAutoCommit(false);
								
		    }catch(Exception e){		    	
		    	e.printStackTrace();
			       // stmt.close();
				 con.close();
		    }
		 
		 
		 int chtchk = 0;
		 
		//1 : jbo Å×ÀÌºí È®ÀÎ
		 
		 String sql = "Select idx from public.music_jobs Where created_dt >= '"+args[0].toString()+"' group by idx";
		 
		  con_codedataRs = stmt.executeQuery(sql);
		  while(con_codedataRs.next()){						  
			  String idx= con_codedataRs.getString(1);
			 
			  String sql_2 = "select count(*) from public.dictionary_music_word where work_idx='"+idx+"'";
			  String c_cnt = "";
			  
			  cnt_job = stmt.executeQuery(sql_2);		  
			  c_cnt = cnt_job.getString(1);
			  
			  if(Integer.parseInt(c_cnt) > 0) {
				  
				 Statement stmt = null;
				 String dictionary_work = "insert into public.dictionary_work ("
				 		+ "				select idx,'music',amount,headword_st,headword_st,synonym_st,synonym_st,created_dt,completed_dt,user_id,created_by,complete_st,test_st,use_st,category from public.music_jobs where idx="+idx+"'"
				 		+ "			);";
				
				 try{
				      stmt.execute(dictionary_work);
				  }catch(Exception e){
					  System.out.println(e);
				  } 
				 String dictionary_music_word = "insert into public.dictionary_music_word ("
				 		+ "			select mo.idx,mo.album_id,mo.song_id,mo.artist_id,mo.album_name,mo.song_name,mo.artist_name,mo.rank,mo.genre,mj.created_dt,mj.completed_dt,mj.idx "
				 		+ " from public.music_jobs mj,music_original mo Where mj.idx=mo.job_idx and mj.idx='"+idx+"'"
				 		+ "			);";
				 
				 
				
				 
				 try{
				      stmt.execute(dictionary_music_word);
				  }catch(Exception e){
					  System.out.println(e);
				  } 
				 
				 String dictionary_music_word_song ="insert into public.dictionary_entity_pdic("
				 		+ "				select nextval('dictionary_entity_pdic_entity_pdic_idx_seq'),"
				 		+ "				mv.work_idx,"
				 		+ "				mv.music_word_idx,"
				 		+ "				'music',"
				 		+ "				(select word_category from public.dictionary_work where work_idx=mv.work_idx and work_type='music') as word_category,"
				 		+ "				mv.album_name,ma.name as head_word,'',"
				 		+ "				ma.history,"
				 		+ "				ma.warning_type,"
				 		+ "				ma.fail_type,"
				 		+ "				(select work_create_date from public.dictionary_work where work_idx=mv.work_idx and work_type='music') as work_create_date,"
				 		+ "				ma.modified_dt,'¾Ù¹ü' "
				 		+ "				from dictionary_music_word  as mv,music_albumheadwords as ma"
				 		+ "				where mv.music_word_idx=ma.original_idx AND mv.work_idx='"+idx+"'"
				 		+ "			)";
				 
				 try{
				      stmt.execute(dictionary_music_word_song);
				  }catch(Exception e){
					  System.out.println(e);
				  } 
				 
				 String dictionary_music_word_singer ="insert into public.dictionary_entity_pdic("
				 		+ "				select nextval('dictionary_entity_pdic_entity_pdic_idx_seq'),"
				 		+ "				mv.work_idx,"
				 		+ "				mv.music_word_idx,"
				 		+ "				'music',"
				 		+ "				(select word_category from public.dictionary_work where work_idx=mv.work_idx and work_type='music') as word_category,"
				 		+ "				mv.album_name,ma.name as head_word,'',"
				 		+ "				ma.history,"
				 		+ "				ma.warning_type,"
				 		+ "				ma.fail_type,"
				 		+ "				(select work_create_date from public.dictionary_work where work_idx=mv.work_idx and work_type='music') as work_create_date,"
				 		+ "				ma.modified_dt,'°¡¼ö' "
				 		+ "				from dictionary_music_word  as mv,music_artistheadwords as ma"
				 		+ "				where mv.music_word_idx=ma.original_idx AND mv.work_idx='"+idx+"'"
				 		+ "			)";
				 
				 try{
				      stmt.execute(dictionary_music_word_singer);
				  }catch(Exception e){
					  System.out.println(e);
				  } 
				 
				 String dictionary_music_word_album ="insert into public.dictionary_entity_pdic("
				 		+ "				select nextval('dictionary_entity_pdic_entity_pdic_idx_seq'),"
				 		+ "				mv.work_idx,"
				 		+ "				mv.music_word_idx,"
				 		+ "				'music',"
				 		+ "				(select word_category from public.dictionary_work where work_idx=mv.work_idx and work_type='music') as word_category,"
				 		+ "				mv.album_name,ma.name as head_word,'',"
				 		+ "				ma.history,"
				 		+ "				ma.warning_type,"
				 		+ "				ma.fail_type,"
				 		+ "				(select work_create_date from public.dictionary_work where work_idx=mv.work_idx and work_type='music') as work_create_date,"
				 		+ "				ma.modified_dt ,'°î'"
				 		+ "				from dictionary_music_word  as mv,music_songheadwords as ma"
				 		+ "				where mv.music_word_idx=ma.original_idx AND mv.work_idx='"+idx+"'"
				 		+ "			)";
				 
				 
				 try{
				      stmt.execute(dictionary_music_word_album);
				  }catch(Exception e){
					  System.out.println(e);
				  } 
				 
				 
				  
			  }
		  }
		  con_codedataRs.close();	
		 
		  con.commit();
			 if(stmt != null){stmt.close();}
		   	 if(con != null){con.commit();  con.close();}
		 	
				long elapsedTimes_dms = System.currentTimeMillis() - startTime;
			   	System.out.println("End Time !! "+elapsedTimes_dms+"ms");
		  
    }
    
    public static String connUrlString(String dbkind,String dbip,String dbport,String dbname) {
		if( dbkind.equals("ORACLE") )
			return "jdbc:oracle:thin:@" + dbip + ":" + dbport + ":" + dbname;
		else if( dbkind.equals("MSSQL") )
			return "jdbc:sqlserver://" + dbip + ":" + dbport + ";databaseName="
					+ dbname;
		else if( dbkind.equals("MYSQL") )
			return "jdbc:mysql://" + dbip + ":" + dbport + "/" + dbname
					+ "?useUnicode=true&characterEncoding="+CAHRSET;
		
		else
			return "";
	}


}
