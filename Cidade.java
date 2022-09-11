package tp;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public class Cidade implements Serializable, Comparable<Cidade> {
    public byte[] nome;
   
    @Override
    public int compareTo(Cidade e){
        
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY);
        try {
            return collator.compare(new String(this.nome , "utf-8"), new String(e.nome , "utf-8"));
        } catch (UnsupportedEncodingException ex) {
            return 0;
        }
       
    }
    
}
