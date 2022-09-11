package tp;

import java.io.Serializable;

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
public class DosesAplicadas implements Serializable {

    public byte[] cidade;
    public byte[] dose;
    public int quantidade;

    public DosesAplicadas(byte[] c, byte[] d, int q) {
        cidade = c;
        dose = d;
        quantidade = q;
    }

}
