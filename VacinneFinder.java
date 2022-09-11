package tp;

// Area de importação
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

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

public class VacinneFinder {

    static Scanner in = new Scanner(System.in, "ISO-8859-1"); // Scanner global , com os caracteres em latino que é ISO-8859-1
    
    static Path pathCidade = Paths.get("src/tp/binary/cidade.dat"); // caminho dos arquivos Relativo                   
    static Path pathTipoDose = Paths.get("src/tp/binary/tipodose.dat");// caminho dos arquivos Relativo   
    static Path pathDosesAplicadas = Paths.get("src/tp/binary/dosesaplicadas.dat");// caminho dos arquivos Relativo   
    static Path pathImportar = Paths.get("src/tp/INSIRA OS DADOS AQUI/vacinometro.csv");// caminho dos arquivos Relativo   
    static Path pathExportar = Paths.get("src/tp/RETIRE OS DADOS AQUI/vacinometroExportado.csv");// caminho dos arquivos Relativo   

    public static void main(String[] args) {
        int op = -1;
        do {  // vai entrar em um loop
            try {        // Menu de opcoes pro usuario
                System.out.println("Ola, seja bem vindo ao Vaccine Finder!"); 
                System.out.println("<1> Insira uma nova cidade");
                System.out.println("<2> Listar cidades");
                System.out.println("<3> Insira um novo tipo de dose");
                System.out.println("<4> Listar tipos de doses");
                System.out.println("<5> Atualizar doses aplicadas em uma cidade");
                System.out.println("<6> Atualizar doses aplicadas em varias cidades");
                System.out.println("<7> Listar doses de uma cidade");
                System.out.println("<8> Importar dados");
                System.out.println("<9> Exportar dados");
                System.out.println("<0> Sair do sistema");

                op = in.nextInt();
                in.nextLine();
                switch (op) { // estrutura Switch case baseada nas opções
                    // cada case vai chamar um metodo
                    case 1:
                        System.out.print("Nome da cidade: ");
                        String cidade = in.nextLine();
                        novaCidade(cidade); //*
                        break;
                    case 2:
                        listarCidade(); //*
                        break;
                    case 3:
                        System.out.print("Tipo de dose: ");
                        String tipos = in.nextLine();
                        novaDose(tipos); //*
                        break;
                    case 4:
                        listarDose(); //*
                        break;
                    case 5:
                        atualizarDose(); //*
                        break;
                    case 6:
                        atualizarDoses(); //*
                        break;
                    case 7:
                        listarDosesAplicadas(); //*
                        break;
                    case 8:
                        importarDados(); //*
                        break;
                    case 9:
                        exportarDados(); //*
                        break;
                    case 0:
                        break; // 0 para for sair do sistema
                    default: // se digitar algo fora do menu opção invalida
                        System.out.println("Opcao Invalida!");
                }
            } catch (InputMismatchException e) { //se digitar algo que não for um numero 
                System.out.println("Opcao invalida"); // tratamento de excecao
                in.next();
            }

        } while (op != 0); // loop enquanto for diferente de 0 , se digitar 0 sai do loop
        in.close(); // fechar o Scanner;
    }
    //Torres
    private static boolean cidadeExiste(String nome) { // verificar se a cidade existe no arquivo binario
        try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathCidade))) { // tentar ler o arquivo cidade
            while (true) { // looping infinito
                Cidade c = (Cidade) input.readObject(); //Instanciar Cidade baseado no que tem na classe
                if (new String(c.nome, "utf-8").equalsIgnoreCase(nome)) { // se existir cidades iguais, ele retorna true , e sai do metodo
                    return true;//retorna true
                }
            }
            //tratamento de Exceções
        } catch (EOFException e) {
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("Tipo de objeto invalido!");
        } catch (IOException e) {
            System.out.println("Erro de leitura no arquivo");
        }
        return false; // senão retorna false
    }
    //Torres
    private static void novaCidade(String cidade) { // adicionar uma nova cidade no arquivo binario
        Cidade nova = new Cidade(); 
        try {

            nova.nome = cidade.getBytes("utf-8"); // converter e armazenar no nome o parametro cidade para bytes baseado no utf8 .

            if (Files.exists(pathCidade)) { // se o arquivo cidade existir 

                if (cidadeExiste(new String(nova.nome, "utf-8"))) { // se O metodo cidadeExiste for true
                    System.out.println("Aviso: Essa cidade ja esta cadastrada!");
                    return; // da o return 
                }

                List lista = new ArrayList(); // cria um array de elementos indefinidos
                try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathCidade))) {// tentar ler o arquivo cidade
                    while (true) { // Loop Infinito
                        Cidade c = (Cidade) input.readObject(); //Instanciar Cidade baseado no que tem na classe
                        lista.add(c); // adiciona o objeto para a lista da array
                    }
                } catch (EOFException e) { // se o arquivo não existir , criar um novo
                    try ( ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(pathCidade))) {
                        lista.add(nova);  // armazena as novas cidades no arraylist
                        Collections.sort(lista); // organizar ordem alfabetica
                        
                        for (int i = 0; i < lista.size(); i++) {
                            output.writeObject(lista.get(i));
                        }

                    } catch (IOException e1) {
                        System.out.println("Erro de escrita do arquivo!");
                    }

                } catch (FileNotFoundException e) {
                    System.out.println("Nao foi possivel abrir o arquivo cidade.dat!");
                } catch (IOException e) {
                    System.out.println("Erro de escrita no arquivo cidade.dat!");
                } catch (Exception e) {
                }

                if (Files.exists(pathDosesAplicadas)) { // se o arquivo Doses aplicadas existe
                    //continua escrevendo no arquivo que ja existe
                    try ( FileOutputStream fos = new FileOutputStream(pathDosesAplicadas.toString(), true);  AppendingObjectOutputStream output = new AppendingObjectOutputStream(fos)) {
                        
                        ArrayList<String> tiposDoses = new ArrayList<String>();//nova array list Strings
                        try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathTipoDose))) {//ler o arquivo Dose

                            while (true) { // loop infinito
                                TiposDoses d = (TiposDoses) input.readObject(); //Instanciar TiposDoses baseado no que tem na classe
                                String bytesDecoded = new String(d.tipo, "utf-8"); // converte de bytes para String
                                tiposDoses.add(bytesDecoded); //adiciona na arrayList tipos Dose
                            }

                        } catch (EOFException e) {
                        } catch (ClassNotFoundException e) {
                            System.out.println("Tipo de objeto invalido!");
                        } catch (IOException e) {
                            System.out.println("Erro de leitura no arquivo");
                        }

                        for (int i = 0; i < tiposDoses.size(); i++) {
                            output.writeObject(new DosesAplicadas(nova.nome, tiposDoses.get(i).getBytes("UTF-8"), 0));//escreve dentro do objeto
                        }                                                                                            //o nome da cidade e os tipos de dose
                    }
                }

            } else {
                //se o arquivo não existir , cria um novo
                try ( ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(pathCidade))) {
                    output.writeObject(nova);
                } catch (IOException e) {
                    System.out.println("Erro de escrita no arquivo cidade.dat!");
                }
            }

        } catch (InvalidPathException e) {
            System.out.println("Nao foi possivel encontrar o arquivo cidade.dat!");
        } catch (InputMismatchException e) {
            System.out.println("Erro de entrada de dados!");
        } catch (IOException e) {
            System.out.println("Erro na entrada de dados!");
        }

    }
    //Torres
    private static void listarCidade() { // listar as cidades existentes no arquivo binario
        try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathCidade))) { //ler arquivo
            System.out.println("Lista de cidades: ");
            while (true) { // loop infinito
                Cidade c = (Cidade) input.readObject(); // Instancia cidade baseado no que tem na classe
                String bytesDecoded = new String(c.nome, "utf-8"); //converter de bytes para String nome
                System.out.println(bytesDecoded); 
            }
        } catch (EOFException e) {
            System.out.println();
            return;
        } catch (ClassNotFoundException e) {
            System.out.println("Tipo de objeto invalido!");
        } catch (IOException e) {
            System.out.println("Erro de leitura no arquivo");
        }
    }
    //Bahjet
    private static boolean doseExiste(String tipo) { // verificar se a cidade existe no arquivo binario
        try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathTipoDose))) {
            while (true) { // looping infinito
                TiposDoses d = (TiposDoses) input.readObject(); //Instanciar TiposDoses baseado no que tem na classe
                if (new String(d.tipo, "utf-8").equalsIgnoreCase(tipo)) { // se existir cidades iguais, ele retorna true
                    return true;
                }
            }
        } catch (EOFException e) {
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("Tipo de objeto invalido!");
        } catch (IOException e) {
            System.out.println("Erro de leitura no arquivo");
        }
        return false;
    }

    private static void novaDose(String tipos) { //Bahjet
        // adicionar uma nova cidade no arquivo binario
        TiposDoses nov = new TiposDoses(); // instaciar Tipos Dose
        try {

            nov.tipo = tipos.getBytes("UTF-8");
            
            String bytesDecoded = new String(nov.tipo, "utf-8"); //converter byte para String
            
            if (Files.exists(pathTipoDose)) { // se arquivo existir

                if (doseExiste(bytesDecoded)) { // se Dose existe retornar true, exibir a mensagem abaixo e dar return
                    System.out.println("Aviso: esse tipo de dose ja esta cadastrado!");
                    return;
                }
                
                List lista = new ArrayList(); // cria um array de elementos indefinidos
                try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathTipoDose))) { // tentar ler o arquivo se existir
                    while (true) {
                        TiposDoses d = (TiposDoses) input.readObject(); //criando e lendo o objeto
                        lista.add(d); // adicionando na lista
                    }
                } catch (EOFException e) {
                    try ( ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(pathTipoDose))) { //escrever um novo
                        lista.add(nov); //adicionar na lista
                        for (int i = 0; i < lista.size(); i++) {
                            output.writeObject(lista.get(i));// escrevendo dentro da lista
                        }

                    } catch (IOException e1) {
                        System.out.println("Erro de escrita do arquivo!");
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Nao foi possivel abrir o arquivo tipodose.dat!");
                } catch (IOException e) {
                    System.out.println("Erro de escrita no arquivo tipodose.dat!");
                } catch (ClassNotFoundException e) {
                    System.out.println("Erro de Classe não encontrada!");
                }

                if (Files.exists(pathDosesAplicadas)) {
                    try ( ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(pathDosesAplicadas))) {
                        ArrayList<String> cidade = new ArrayList<String>();
                        try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathCidade))) {
                            String cBytesDecoded;
                            while (true) {
                                Cidade c = (Cidade) input.readObject();
                                cBytesDecoded = new String(c.nome, "utf-8"); //Converter Byte para String
                                cidade.add(bytesDecoded); // adicionar na lista cidade
                            }
                        } catch (EOFException e) {
                        } catch (ClassNotFoundException e) {
                            System.out.println("Tipo de objeto invalido!");
                        } catch (IOException e) {
                            System.out.println("Erro de leitura no arquivo");
                        }

                        ArrayList<String> tiposDoses = new ArrayList<String>(); 
                        try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathTipoDose))) {
                            String dBytesDecoded;
                            while (true) {
                                TiposDoses d = (TiposDoses) input.readObject();
                                dBytesDecoded = new String(d.tipo, "utf-8");
                                tiposDoses.add(bytesDecoded);

                            }

                        } catch (EOFException e) {
                        } catch (ClassNotFoundException e) {
                            System.out.println("Tipo de objeto invalido!");
                        } catch (IOException e) {
                            System.out.println("Erro de leitura no arquivo");
                        }
                        
                        ArrayList<DosesAplicadas> dosesAplicadas = new ArrayList<DosesAplicadas>();
                        try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathDosesAplicadas))) {

                            while (true) {
                                DosesAplicadas da = (DosesAplicadas) input.readObject();
                                dosesAplicadas.add(da); // adicionar dentro da array
                            }
                        } catch (EOFException e) {
                        } catch (ClassNotFoundException e) {
                            System.out.println("Tipo de objeto invalido!");
                        } catch (IOException e) {
                            System.out.println("Erro de leitura no arquivo");
                        }
                        
                        //Arthur
                        for (int i = 0; i < cidade.size(); i++) {
                            dosesAplicadas.add(new DosesAplicadas(cidade.get(i).getBytes("utf-8"), nov.tipo, 0));
                        }
                        
                        
                        DosesAplicadas doseA;
                        for (int i = 0; i < cidade.size(); i++) {
                            for (int j = 0; j < tiposDoses.size(); j++) {

                                for (int k = 0; k < dosesAplicadas.size(); k++) {
                                    doseA = dosesAplicadas.get(k);
                                    if (new String(doseA.cidade, "utf-8").equalsIgnoreCase(cidade.get(i)) && new String(doseA.dose, "utf-8").equalsIgnoreCase(tiposDoses.get(j))) {
                                                                                
                                        output.writeObject(new DosesAplicadas(cidade.get(i).getBytes("UTF-8"),
                                        tiposDoses.get(j).getBytes("UTF-8"), doseA.quantidade));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                try ( ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(pathTipoDose))) {
                    output.writeObject(nov);
                } catch (IOException e) {
                    System.out.println("Erro de escrita no arquivo tipodose.dat!");
                }
            }
        } catch (InvalidPathException e) {
            System.out.println("Nao foi possivel encontrar o arquivo tipodose.dat!");
        } catch (InputMismatchException e) {
            System.out.println("Erro de entrada de dados!");
        } catch (IOException e) {
            System.out.println("Erro na entrada de dados!");
        }
    }
    //Bahjet
    private static void listarDose() { // listar as cidades existentes no arquivo binario
        try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathTipoDose))) {
            System.out.println("Lista de tipos de doses:");
            while (true) {
                TiposDoses d = (TiposDoses) input.readObject();
                String bytesDecoded = new String(d.tipo, "utf-8");
                System.out.println(bytesDecoded);
            }
        } catch (EOFException e) {
            System.out.println();
        } catch (ClassNotFoundException e) {
            System.out.println("Tipo de objeto invalido!");
        } catch (IOException e) {
            System.out.println("Erro de leitura no arquivo");
        }
    }
    
    private static void atualizarDose() { //Arthur

        if (Files.exists(pathDosesAplicadas)) {

            ArrayList<String> cidades = new ArrayList<String>();
            try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathCidade))) {
                Cidade c;
                while (true) {
                    c = (Cidade) input.readObject();
                    cidades.add(new String(c.nome, "utf-8"));
                }
            } catch (EOFException e) {
            } catch (ClassNotFoundException e) {
                System.out.println("Tipo de objeto invalido!");
            } catch (IOException e) {
                System.out.println("Erro de leitura no arquivo");
            }

            ArrayList<String> tiposDoses = new ArrayList<String>();
            try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathTipoDose))) {
                TiposDoses d;
                while (true) {
                    d = (TiposDoses) input.readObject();
                    tiposDoses.add(new String(d.tipo, "utf-8"));
                }
            } catch (EOFException e) {
            } catch (ClassNotFoundException e) {
                System.out.println("Tipo de objeto invalido!");
            } catch (IOException e) {
                System.out.println("Erro de leitura no arquivo");
            }

            System.out.println("Insira a cidade que voce deseja atualizar o numero de doses:");
            String cidadeDose = in.nextLine();

            boolean existe = false;
            for (int i = 0; i < cidades.size(); i++) {
                if (cidades.get(i).equalsIgnoreCase(cidadeDose)) {
                    existe = true;
                    break;
                }
            }
            if (existe == false) {
                System.out.println("Essa cidade não foi inserida");
                return;
            }

            for (int j = 0; j < tiposDoses.size(); j++) {
                System.out.println("Insira a quantidade de doses do tipo " + tiposDoses.get(j));
                editarDosesAplicadas(cidadeDose, tiposDoses.get(j), in.nextInt());
            }
        } else {
            try ( ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(pathDosesAplicadas))) {

                ArrayList<String> cidades = new ArrayList<String>();
                try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathCidade))) {

                    while (true) {
                        Cidade c = (Cidade) input.readObject();
                        String bytesDecoded = new String(c.nome, "utf-8");
                        cidades.add(bytesDecoded);

                    }

                } catch (EOFException e) {
                } catch (ClassNotFoundException e) {
                    System.out.println("Tipo de objeto invalido!");
                } catch (IOException e) {
                    System.out.println("Erro de leitura no arquivo");
                }
                ArrayList<String> tiposDoses = new ArrayList<String>();
                try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathTipoDose))) {

                    while (true) {
                        TiposDoses d = (TiposDoses) input.readObject();
                        String bytesDecoded = new String(d.tipo, "utf-8");
                        tiposDoses.add(bytesDecoded);

                    }

                } catch (EOFException e) {
                } catch (ClassNotFoundException e) {
                    System.out.println("Tipo de objeto invalido!");
                } catch (IOException e) {
                    System.out.println("Erro de leitura no arquivo");
                }

                for (int i = 0; i < cidades.size(); i++) {
                    for (int j = 0; j < tiposDoses.size(); j++) {
                        // doses = new DosesAplicadas(cidades.get(i).getBytes("UTF-8"),
                        // tiposDoses.get(j).getBytes("UTF-8"), "0".getBytes());
                        output.writeObject(new DosesAplicadas(cidades.get(i).getBytes("UTF-8"),
                                tiposDoses.get(j).getBytes("UTF-8"), 0));
                    }
                }

                System.out.println("Insira a cidade que voce deseja atualizar o numero de doses");
                String cidadeDose = in.nextLine();
                ArrayList<DosesAplicadas> dosesAplicadas = new ArrayList<DosesAplicadas>();
                boolean existe = false;
                for (int i = 0; i < cidades.size(); i++) {
                    if (cidades.get(i).equalsIgnoreCase(cidadeDose)) {
                        existe = true;
                        break;
                    }

                }

                if (existe == false) {
                    System.out.println("Essa cidade não foi inserida");

                    return;
                }
                for (int j = 0; j < tiposDoses.size(); j++) {
                    System.out.println("Insira a quantidade de doses do tipo " + tiposDoses.get(j));
                    editarDosesAplicadas(cidadeDose, tiposDoses.get(j), in.nextInt());

                }

            } catch (IOException e) {
                System.out.println("Erro de escrita no arquivo cidade.dat!");
            }
        }
    }

    public static void editarDosesAplicadas(String c, String d, int q) { //Arthur

        ArrayList<DosesAplicadas> dosesAplicadas = new ArrayList<DosesAplicadas>();

        try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathDosesAplicadas))) {

            while (true) {
                DosesAplicadas da = (DosesAplicadas) input.readObject();
                dosesAplicadas.add(da);

            }
        } catch (EOFException e) {
            try {

                DosesAplicadas doseA;
                for (int i = 0; i < dosesAplicadas.size(); i++) {
                    doseA = dosesAplicadas.get(i);
                    if (new String(doseA.cidade, "utf-8").equalsIgnoreCase(c)
                            && new String(doseA.dose, "utf-8").equalsIgnoreCase(d)) {
                        dosesAplicadas.set(i, new DosesAplicadas(doseA.cidade, doseA.dose, q));
                        try ( ObjectOutputStream output = new ObjectOutputStream(
                                Files.newOutputStream(pathDosesAplicadas))) {
                            for (int j = 0; j < dosesAplicadas.size(); j++) {
                                output.writeObject(dosesAplicadas.get(j));

                            }
                            return;
                        }
                    }
                }

                System.out.println("Cidade ou tipo de dose não existe!");
                return;
            } catch (Exception e1) {
                System.out.println("Erro de conversão");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Tipo de objeto invalido!");
        } catch (IOException e) {
            System.out.println("Erro de leitura no arquivo");
        }
    }

    public static void atualizarDoses() { // Arthur
        boolean continuar = false;
        String op;
        do {
            atualizarDose();
            in.nextLine();
            System.out.println("Deseja continuar? (s ou n)");
            op = in.nextLine();
            switch (op) {
                case "s":
                case "sim":
                case "y":
                case "yes":
                    continuar = true;
                    break;
                default:
                    continuar = false;
                    break;
            }

        } while (continuar == true);

    }

    public static void listarDosesAplicadas() { // Arthur

        if (!Files.exists(pathDosesAplicadas)) {
            criarDosesAplicadas();
        }

        System.out.print("Insira a cidade \n");
        String cidade = "";
        cidade = in.nextLine();

        ArrayList<DosesAplicadas> dosesAplicadas = new ArrayList<DosesAplicadas>();
        try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathDosesAplicadas))) {

            while (true) {
                DosesAplicadas da = (DosesAplicadas) input.readObject();
                dosesAplicadas.add(da);
            }
        } catch (EOFException e) {
            try {
                System.out.println("Doses da cidade: " + cidade);
                DosesAplicadas doseA;
                for (int i = 0; i < dosesAplicadas.size(); i++) {
                    doseA = dosesAplicadas.get(i);
                    if (new String(doseA.cidade, "utf-8").equalsIgnoreCase(cidade)) {
                        System.out.println(
                                "Tipo: " + new String(doseA.dose, "utf-8") + " | Quantidade: " + doseA.quantidade);
                    }
                }
                System.out.println();
                return;

            } catch (Exception e1) {
                System.out.println("Erro de conversão");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Tipo de objeto invalido!");
        } catch (IOException e) {
            System.out.println("Erro de leitura no arquivo");
        }

    }

    public static void criarDosesAplicadas() { // Pedro
        try ( ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(pathDosesAplicadas))) {

            ArrayList<String> cidades = new ArrayList<String>();
            try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathCidade))) {

                while (true) {
                    Cidade c = (Cidade) input.readObject();
                    String bytesDecoded = new String(c.nome, "utf-8");
                    cidades.add(bytesDecoded);
                }

            } catch (EOFException e) {
            } catch (ClassNotFoundException e) {
                System.out.println("Tipo de objeto invalido!");
            } catch (IOException e) {
                System.out.println("Erro de leitura no arquivo");
            }
            ArrayList<String> tiposDoses = new ArrayList<String>();
            try ( ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathTipoDose))) {

                while (true) {
                    TiposDoses d = (TiposDoses) input.readObject();
                    String bytesDecoded = new String(d.tipo, "utf-8");
                    tiposDoses.add(bytesDecoded);

                }

            } catch (EOFException e) {
            } catch (ClassNotFoundException e) {
                System.out.println("Tipo de objeto invalido!");
            } catch (IOException e) {
                System.out.println("Erro de leitura no arquivo");
            }

            for (int i = 0; i < cidades.size(); i++) {
                for (int j = 0; j < tiposDoses.size(); j++) {
                    output.writeObject(new DosesAplicadas(cidades.get(i).getBytes("UTF-8"),
                            tiposDoses.get(j).getBytes("UTF-8"), 0));
                }
            }

        } catch (IOException e) {
            System.out.println("Erro de escrita no arquivo dosesAplicadas.dat!");
        }

    }
    
    public static void importarDados() { // Pedro
        
        try (BufferedReader br = new BufferedReader(new FileReader(pathImportar.toString()))) {
            
            System.out.println("Insira o arquivo .csv na pasta 'INSIRA OS DADOS AQUI' com o nome 'vacinometro' :");
            System.out.println("Insira 'SIM' quando já tiver inserido o arquivo!");
            in.nextLine();
            
            if(!Files.exists(pathImportar)) {
                System.out.println("Insira o arquivo .csv na pasta 'INSIRA OS DADOS AQUI' com o nome 'vacinometro' !");
                return;
            }
            
            if(Files.exists(pathCidade)) {
                String p = pathCidade.toString();
                File a = new File(p);
                a.delete();
            }
            if(Files.exists(pathTipoDose)) {
                String p = pathTipoDose.toString();
                File a = new File(p);
                a.delete();
            }
            if(Files.exists(pathDosesAplicadas)) {
                String p = pathDosesAplicadas.toString();
                File a = new File(p);
                a.delete();
            }
            
            ArrayList<DosesAplicadas> qt = new ArrayList<DosesAplicadas>();
            br.readLine(); // Le o cabeçalho do arquivo .CSV
            while (br.ready()) {
                String[] linha = br.readLine().split(";");
                novaCidade(linha[0]);
                novaDose(linha[1]);
                qt.add(new DosesAplicadas(linha[0].getBytes("utf-8"), linha[1].getBytes("utf-8"), Integer.parseInt(linha[2])));
            }
            br.readLine();
            criarDosesAplicadas();
            
            System.out.println("Aguarde algum tempo enquanto o arquivo esta sendo importado...");
            DosesAplicadas qtA;
            for(int i = 0; i < qt.size(); i++) {
                qtA = qt.get(i);
                editarDosesAplicadas(new String(qtA.cidade, "utf-8"), new String(qtA.dose, "utf-8"), qtA.quantidade);
            }
            System.out.println("Arquivo Importado com sucesso");
            return;
            
        } catch (IOException e) {
            System.out.println("Erro de escrita no arquivo .csv!");
        } catch (NumberFormatException e) {
            System.out.println("Erro de formato de dados");
        }
        
    }

    public static void exportarDados() {
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(pathDosesAplicadas));
                BufferedWriter bw = new BufferedWriter(new FileWriter((pathExportar).toString()))) {
            bw.write("Município;Dose;Total Doses Aplicadas"); // cabeçalho do arquivo CSV
            bw.newLine();
            while (true) {
                DosesAplicadas da = (DosesAplicadas) input.readObject();
                bw.write(String.format("%s;%s;%d", new String(da.cidade, "utf-8"), new String(da.dose, "utf-8"), da.quantidade));
                bw.newLine();
            }
        } catch (EOFException e) {
            System.out.println("Arquivo Exportado com sucesso");
        } catch (ClassNotFoundException e) {
            System.out.println("Tipo de objeto invalido!");
        } catch (IOException e) {
            System.out.println("Erro de leitura no arquivo");
        }
    }
    
}