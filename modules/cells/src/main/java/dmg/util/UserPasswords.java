package dmg.util ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class UserPasswords extends Hashtable {
   private File _passwdFile = null ;
   private long _updateTime = 0L;
   public UserPasswords( File passwdFile ){
      _passwdFile = passwdFile ;
   }
   @Override
   public String toString(){ return _passwdFile.getPath() ; }
   public String getPassword( String user ){
      String [] s = (String [])super.get( user ) ;
      return s == null ? null : s[1] ;
   }
   public String [] getRecord( String user ){
      return (String [] )super.get( user ) ;
   }
   public void removeRecord( String user ){
      super.remove( user ) ;
   }
   public void addRecord( String [] record ){
      if( ( record.length < 2 ) || ( record[0] == null ) || ( record[1] == null ) ) {
          throw new
                  IllegalArgumentException("Record [].length < 2");
      }
      super.put( record[0] , record ) ;
   }
   public void commit() throws IOException {

       if( _passwdFile == null ) {
           return;
       }

       String p = _passwdFile.getParent() ;
       p = p == null ? "." : p ;
       File   pwdFile = new File( p , "."+_passwdFile.getName() ) ;
       PrintWriter pw = new PrintWriter(
                         new FileWriter( pwdFile ) ) ;
       Enumeration  e = elements() ;
       try{
          while( e.hasMoreElements() ){
             Object [] a = (Object [])e.nextElement() ;
             for( int i = 0 ; ( i < a.length ) && ( a[i] != null ) ; i++ ){
                pw.print( a[i].toString()+":" );
             }
             pw.println("");
          }
       }catch(Exception xx){
          try{ pw.close() ; }catch(Exception ee ){}
          pwdFile.delete() ;
       }
       try{ pw.close() ; }catch(Exception ee ){}
       pwdFile.renameTo( _passwdFile ) ;
       _updateTime = _passwdFile.lastModified() ;
   }
   public void update() throws IOException {
      if( _passwdFile.exists()) {
          if( _updateTime > _passwdFile.lastModified() ) {
              return;
          }
          InputStream stream = new FileInputStream( _passwdFile ) ;
          try{
              _scanStream( stream ) ;
              _updateTime = _passwdFile.lastModified() ;
          }finally{
              try{ stream.close() ; }catch(Exception ee ){}
          }
      }
   }
   public void _scanStream( InputStream in ) throws IOException {

      BufferedReader reader = new BufferedReader(
                               new InputStreamReader( in ) ) ;
      String line ;
      while( ( line = reader.readLine() ) != null ){
         if( line.length() <= 0 ) {
             continue;
         }
         if( line.charAt(0) == '#' ) {
             continue;
         }
         StringTokenizer st = new StringTokenizer( line , ":" ) ;
         String [] a = new String[8] ;
         int i = 0 ;
         for( i = 0 ; ( i < a.length ) && st.hasMoreTokens() ; i++ ){
            a[i] = st.nextToken() ;
         }
         if( i < 2 ) {
             continue;
         }
         put( a[0] , a ) ;
      }

   }
   //
   // to be complient
   //
   public UserPasswords( InputStream in ) throws IOException {
      super() ;
      _scanStream( in ) ;
   }
   @Override
   public Object get( Object key ){
       Object x = super.get(key) ;
       if( ( x == null                    ) ||
           ( ! ( x instanceof Object [] ) ) ||
           ( ((Object[])x).length < 2     )    ) {
           return null;
       }
       return ((Object[])x)[1] ;
   }
   public static void main( String [] args )throws Exception {
      if( args.length < 3 ){
        System.err.println("Usage : ... <filename> ( put user passwd ... ) | get user" ) ;
        System.exit(4);
      }
      String filename = args[0] ;
      String command  = args[1] ;
      String user     = args[2] ;
      UserPasswords ups = new UserPasswords( new File(filename) ) ;

      if( command.equals( "put" ) ){

          String [] record = new String[args.length - 2] ;
          for( int i = 0 ; i < record.length ; i++ ) {
              record[i] = args[2 + i];
          }
          ups.addRecord( record ) ;
          ups.commit() ;
      }else if( command.equals( "get" ) ){
         ups.update() ;
         String [] record = ups.getRecord(user) ;
         if( record == null ){
           System.out.println("Record not found for : "+user ) ;
           System.exit(4);
         }
         for( int i = 0 ; ( i < record.length ) && ( record[i] != null ) ; i++ ) {
             System.out.print(record[i] + " ");
         }
         System.out.println("");
      }else{
        System.err.println("Usage : ... <filename> ( put user passwd ... ) | get user" ) ;
        System.exit(4);
      }
      System.exit(0);
   }
}
