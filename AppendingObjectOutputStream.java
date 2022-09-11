package tp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * FATEC ADS Vespertino
 * 
 * Autores:
 *  
 * Pedro Henrique Bezerra Severino
 * Bahjet Mohamad Khalil
 * Arthur Goulart Tomaz
 * Gustavo Torres
 * Allan Matias
 * 
 */

public class AppendingObjectOutputStream extends ObjectOutputStream {

	  public AppendingObjectOutputStream(OutputStream out) throws IOException {
	    super(out);
	  }

	  @Override
	  protected void writeStreamHeader() throws IOException {
	    // do not write a header, but reset:
	    // this line added after another question
	    // showed a problem with the original
	    reset();
          }
}
