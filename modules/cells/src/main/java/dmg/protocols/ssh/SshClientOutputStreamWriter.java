package dmg.protocols.ssh ;

import  java.io.* ;

public class SshClientOutputStreamWriter extends FilterWriter {

   OutputStream _outputStream = null ;
   public SshClientOutputStreamWriter( Writer output ){
     super( output ) ;
   }
   public SshClientOutputStreamWriter( OutputStream output ){
     super( new OutputStreamWriter( output ) ) ;
 //    System.out.println( "OutputStreamReader created" ) ;
   }
   public void write( char [] c )
      throws IOException  {
      printout( "SshOutputStreamWriter : write( char [] c )" ) ;
      write( c , 0 , c.length ) ;
   }
   public void write( char [] c , int off , int len )
      throws IOException  {
     printout( "SshOutputStreamWriter : write( char [] c , int off , int "+len+" )" ) ;
     for( int i = off ; i < (off+len) ; i++ ) {
         write((int) c[i]);
     }
   }
   public void write( int c )
      throws IOException  {
       printout( "SshOutputStreamWriter : write( int "+c+" )" ) ;
       if( c == '\n' ){   out.write( 0xa ) ; }
       else {
           out.write(c);
       }
   }
   public void write( String str ) throws IOException {
       printout( "SshOutputStreamWriter : write( String "+str+" )" ) ;
       for( int i= 0 ; i < str.length() ; i++ ) {
           write(str.charAt(i));
       }
   }
   public void write( String str , int off , int len )throws IOException{
       printout( "SshOutputStreamWriter : write( String str , int off , int len )" ) ;
       for( int i = off ; i < (off+len) ; i++ ) {
           write(str.charAt(i));
       }
   }
   void printout( String str ){ 
//     System.out.println( str ) ; 
   }
  

}
