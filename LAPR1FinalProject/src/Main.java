import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.lang.Runtime;

/*	2 Modos disponíveis:
	-> Modo interativo     -> (java -jar lapr1_1dm_grupo02.jar)

	-> Modo não interativo
        * args[0] -> caminho do ficheiro.csv
        * args[1] -> -m
        * args[2] -> metodo a usar (1-Euler, 2-RK4)
        * args[3] -> -p
        * args[4] -> h (0<h<1)
        * args[5] -> -t
        * args[6] -> N (N~=1000)
        * args[7] -> -d
        * args[8] -> dias (0<dias)
        * teste para os parametros ->  java -jar lapr1_1dm_grupo02.jar ficheiroSIR.csv -m 1 -p 0.1 -t 1000 -d 30
 */
public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("*******************\u001B[1mInício do Programa\u001B[0m*******************\n");
        float h = 0;
        float n = 0;
        float s = 0;
        float sDias = 0;
        int dias = 0;
        int option = 1;
        int idMetodo = 0;
        String caminhoFinal = "LAPR1FinalProject/Ficheiros_Resultados/";
        String caminhoInicial = "LAPR1FinalProject/ficheiroSIR.csv";
        String input = "";
        float escolha = 0;

        if (args.length == 0) {

            File file = new File(caminhoFinal);

            if (file.exists() && file.isDirectory()) {

                File[] files = file.listFiles();

                if (files != null && files.length > 0) {
                    
                    int shouldDelete = 0;

                    for (File ficheiro : files) {

                        if (!ficheiro.getName().equals(".gitignore")) {

                            shouldDelete++;
                        }
                    }
                    if (shouldDelete > 0) {

                        System.out.println("\u001B[1mDeseja apagar todos os ficheiros anteriormente criados pelo programa? |1-Sim| |0-Não|\u001B[0m");
                        input = scanner.next();
                        escolha = checkIntSN(input, escolha);
                        if (escolha == 1) {

                            for (File ficheiro : files) {

                                if (!ficheiro.getName().equals(".gitignore")) {
                                    ficheiro.delete();
                                }
                            }
                        }
                    }
                }
            }

            modoInterativo(h, n, s, sDias, dias, option, caminhoFinal, caminhoInicial, idMetodo, input, escolha);
        } else {

            modoNaoInterativo(args, h, n, s, sDias, dias, option, caminhoFinal, caminhoInicial, idMetodo, input);
        }
        System.out.println("\n*******************\u001B[1mFim do Programa\u001B[0m*******************");
    }

    /*************************************************************************
     *Função modoInterativo    							    				 *
     *************************************************************************
     * @param h Step        							         		     *
     * @param n Valor da população  									     *
     * @param s n-1 												 	     *
     * @param sDias Número de dias 										     *
     * @param dias                                                           *
     * @param option Método	            								     *
     * @param caminhoFinal Caminho de ficheiros de resultados finais         *
     * @param caminhoInicial Localização do ficheiros de dados iniciais      *
     * @param idMetodo identificador de modoInterativo                       *
     * @param input leitura do input                                         *
     * @param checker verifica os valores                                    *
     *************************************************************************/
    public static void modoInterativo(float h, float n, float s, float sDias, int dias, int option, String caminhoFinal, String caminhoInicial, int idMetodo, String input, float checker) {

        int counterGraficos = 0;
        int counterGeral;
        int countEqual = 0;
        String caminhoFinalGnu;
        int indexPess;
        int linhas = checkNumberOfLines(caminhoInicial);
        int i;
        float[][] matrix = new float[linhas][4];
        String[] nomes = readFile(matrix, linhas, caminhoInicial);
        float[][] valoresInseridos = new float[30][5];
        int[][] indices = new int[30][32];
        float metodosIguais[][] = new float[15][5];
        idMetodo = 1;
        int pass;
        int counterTemp = 0;

        while (option != 0 && counterGraficos < 30) {
            System.out.println("\u001B[1mSelecione uma pessoa:\u001B[0m");
            for (i = 0; i < linhas; i++) {
                System.out.println(i + 1 + " - |" + nomes[i] + "|");
            }

            input = scanner.next();
            for(i = 0; i < input.length(); i++) {
                if(input.indexOf(",") == i){
                    input = input.replaceAll(",", ".");
                }
            }
            checker = Float.valueOf(input) - 1;
            indexPess = (int) checker;

            while (indexPess < 0 || indexPess >= linhas || checker != indexPess) {
                mensagemErro(4);
                input = scanner.next();
                for(i = 0; i < input.length(); i++) {
                    if(input.indexOf(",") == i){
                        input = input.replaceAll(",", ".");
                    }
                }
                checker = Float.valueOf(input) - 1;
                indexPess = (int) checker;
            }

            System.out.println("\u001B[1mValor de h? (Ex.: 0,1)\u001B[0m");
            input = scanner.next();
            input = input.replaceAll(",", ".");
            h = Float.valueOf(input);

            while (h <= 0 || h >= 1) {

                mensagemErro(7);
                input = scanner.next();
                input = input.replaceAll(",", ".");
                h = Float.valueOf(input);
            }


            System.out.println("\u001B[1mValor da população? (Ex.: 1000)\u001B[0m");
            input = scanner.next();
            for(i = 0; i < input.length(); i++) {
                if(input.indexOf(",") == i){
                    input = input.replaceAll(",", ".");
                }
            }
            n = Float.valueOf(input);

            while (n <= 0 || n != (int) n) {

                mensagemErro(7);
                input = scanner.next();
                for(i = 0; i < input.length(); i++) {
                    if(input.indexOf(",") == i){
                        input = input.replaceAll(",", ".");
                    }
                }
                n = Float.valueOf(input);
            }

            System.out.println("\u001B[1mNúmero de dias? (Ex.: 30)\u001B[0m");
            input = scanner.next();
            for(i = 0; i < input.length(); i++) {
                if(input.indexOf(",") == i){
                    input = input.replaceAll(",", ".");
                }
            }
            checker = Float.valueOf(input);
            dias = (int) checker;

            while (dias <= 0 || dias != checker) {
                
                mensagemErro(7);
                input = scanner.next();
                for(i = 0; i < input.length(); i++) {
                    if(input.indexOf(",") == i){
                        input = input.replaceAll(",", ".");
                    }
                }
                checker = Float.valueOf(input);
                dias = (int) checker;
            }

            System.out.println(" -----------------------\u001B[1mMÉTODOS\u001B[0m-----------------------");
            System.out.println("| \u001B[1m1 - Método de Euler\u001B[0m		                      |");
            System.out.println("| \u001B[1m2 - Método de Runge-Kutta de 4ª ordem\u001B[0m               |");
            System.out.println(" -----------------------------------------------------");
            input = scanner.next();
            for(i = 0; i < input.length(); i++) {
                if(input.indexOf(",") == i){
                    input = input.replaceAll(",", ".");
                }
            }
            checker = Float.valueOf(input);
            option = (int) checker;

            while (option > 2 || option < 1 || option != checker) {

                mensagemErro(3);
                input = scanner.next();
                for(i = 0; i < input.length(); i++) {
                    if(input.indexOf(",") == i){
                        input = input.replaceAll(",", ".");
                    }
                }
                checker = Float.valueOf(input);
                option = (int) checker;
            }

            indices[indexPess][0]++;
            valoresInseridos[counterGraficos][0] = indexPess;
            valoresInseridos[counterGraficos][1] = h;
            valoresInseridos[counterGraficos][2] = n;
            valoresInseridos[counterGraficos][3] = dias;
            valoresInseridos[counterGraficos][4] = option;
            indices[indexPess][1] += option;
            counterGraficos++;
            counterGeral = counterGraficos - 1;

            for (i = 0; i < counterGeral; i++) {

                if (valoresInseridos[i][0] == indexPess) {

                    if (valoresInseridos[i][1] == h && valoresInseridos[i][2] == n && valoresInseridos[i][3] == dias && valoresInseridos[i][4] == option) {

                        System.out.println("*******************\u001B[1mJá existe um ficheiro para esses valores\u001B[0m*******************\n");
                        counterGraficos--;
                        indices[indexPess][1] -= option;
                    }
                    if (valoresInseridos[i][1] == h && valoresInseridos[i][2] == n && valoresInseridos[i][3] == dias && valoresInseridos[i][4] != option) {

                        indices[countEqual][2] = 1;
                        metodosIguais[countEqual][0] = indexPess;
                        metodosIguais[countEqual][1] = h;
                        metodosIguais[countEqual][2] = n;
                        metodosIguais[countEqual][3] = dias;
                        metodosIguais[countEqual][4] = 3;
                        countEqual++;
                    }
                }
            }
            if (counterGraficos != 30) {
                System.out.println("\u001B[1mDeseja inserir novos dados?" + " (mais " + (30 - counterGraficos) + " gráficos disponíveis)" + " |1-Sim| |0-Não|\u001B[0m");
                input = scanner.next();
                option = checkIntSN(input, checker);
            }
        }

        try {
            printFileValores(valoresInseridos, counterGraficos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        valoresInseridos = null;
        float[][] valoresMetodos = newReadValores(counterGraficos);
        for (i = 0; i < counterGraficos; i++) {
            indexPess = (int) valoresMetodos[i][0];
            h = valoresMetodos[i][1];
            n = valoresMetodos[i][2];
            dias = (int) valoresMetodos[i][3];

            if (valoresMetodos[i][4] == 1 || valoresMetodos[i][4] == 3) {

                euler(h, n, dias, matrix, linhas, caminhoFinal, nomes, indexPess);
            }
            if (valoresMetodos[i][4] == 2 || valoresMetodos[i][4] == 3) {

                rK4(h, n, dias, matrix, linhas, caminhoFinal, nomes, indexPess);
            }
        }

        System.out.println("\u001B[1mDeseja converter os resultados em gráfico? |1-Sim| |0-Não|\u001B[0m");
        input = scanner.next();
        option = checkIntSN(input, checker);
        counterGeral = counterGraficos;

        while (option != 0 && counterGeral > 0) {

            System.out.println("\n\u001B[1mDeseja fazer o gráfico de quem?\u001B[0m");

            for (i = 0; i < linhas; i++) {

                if (indices[i][0] >= 1) {

                    System.out.println(i + 1 + " - |" + nomes[i] + "|");
                }
            }

            pass = 0;
            System.out.println("0 - Fazer de Todos (ao fazer isto irá guardar todos os gráficos automáticamente)");

            indexPess = scanner.nextInt() - 1;

            while (indexPess != -1 && pass == 0) {

                while (indexPess < 0 || indexPess >= counterGraficos || indices[indexPess][1] == 0) {

                    if (indexPess < 0 || indexPess >= counterGraficos) {

                        mensagemErro(4);

                    } else if (indices[indexPess][1] == 0) {

                        mensagemErro(5);
                    }
                    indexPess = scanner.nextInt() - 1;

                    if (indexPess == -1) {

                        pass = 1;

                        for (i = 0; i < counterGraficos; i++) {

                            if (indices[i][1] != 0) {

                                indexPess = i;
                            }
                        }
                    }
                }

                if (pass == 1) {

                    indexPess = -1;
                    pass = 0;
                }

                if (indexPess != -1) {

                    System.out.println("\n\u001B[1mSelecione um dos valores disponíveis para a/o " + nomes[indexPess] + ":\u001B[0m");
                    String met = "";
                    counterTemp = 0;

                    for (i = 0; i < counterGraficos; i++) {

                        if (valoresMetodos[i][0] == indexPess) {

                            if ((int) valoresMetodos[i][4] == 1) {

                                met = "Euler";

                            } else met = "Runge-Kutta";

                            System.out.println("\n" + (counterTemp + 1) + "- h:" + valoresMetodos[i][1] + " população:" + (int) valoresMetodos[i][2] + " dias:" + (int) valoresMetodos[i][3] + " método:" + met);
                            counterTemp++;
                        }
                    }
                    option = scanner.nextInt();

                    while (option <= 0 || option > counterGraficos) {

                        mensagemErro(4);
                        option = scanner.nextInt();
                    }
                    int var = 0;

                    while (option >= 0 && option <= counterGraficos) {

                        counterTemp = option;

                        for (i = 0; i < counterGraficos; i++) {

                            if (valoresMetodos[i][0] == indexPess) {

                                counterTemp--;
                                if (counterTemp == 0) {
                                    option = i;
                                }
                            }
                        }
                        if(counterTemp <= 0){
                                
                            if (indices[indexPess][option + 3] == 0) {

                                if (valoresMetodos[option][0] == indexPess) {

                                    indices[indexPess][option + 3]++;
                                    indices[indexPess][1] -= valoresMetodos[option][4];
                                    caminhoFinalGnu = caminhoFinal + nomes[(int) valoresMetodos[option][0]] + "m" + (int) valoresMetodos[option][4] + "p" + String.valueOf(valoresMetodos[option][1]).replace(".", "") + "t" + (int) valoresMetodos[option][2] + "d" + (int) valoresMetodos[option][3] + ".csv";
                                    gnuplot(caminhoFinalGnu, (int) valoresMetodos[indexPess][3], idMetodo);
                                    counterGeral--;
                                    option = -1;
                                    pass = 1;

                                } else {
                                    var = 1;
                                }

                            } else {

                                System.out.println("Esse gráfico já foi feito");
                                var = 1;
                            }

                            if (var == 1) {

                                option = -1;
                                while (option < 0 || option >= counterGraficos) {

                                    mensagemErro(3);
                                    option = scanner.nextInt();
                                }
                                var = 0;
                            } 
                        }else{
                            pass = 1;
                            option = -2;
                        } 
                    }
                } 
            }
            if (indexPess == -1) {

                idMetodo = 0;

                for (i = 0; i < counterGraficos; i++) {

                    if ((int) valoresMetodos[i][4] == 3) {

                        for (int j = 1; j < 3; j++) {

                            caminhoFinalGnu = caminhoFinal + nomes[(int) valoresMetodos[i][0]] + "m" + j + "p" + String.valueOf(valoresMetodos[i][1]).replace(".", "") + "t" + (int) valoresMetodos[i][2] + "d" + (int) valoresMetodos[i][3] + ".csv";
                            gnuplot(caminhoFinalGnu, (int) valoresMetodos[i][3], idMetodo);
                        }
                    } else {

                        caminhoFinalGnu = caminhoFinal + nomes[(int) valoresMetodos[i][0]] + "m" + (int) valoresMetodos[i][4] + "p" + String.valueOf(valoresMetodos[i][1]).replace(".", "") + "t" + (int) valoresMetodos[i][2] + "d" + (int) valoresMetodos[i][3] + ".csv";
                        gnuplot(caminhoFinalGnu, (int) valoresMetodos[i][3], idMetodo);
                    }
                }
                option = 0;
            }
            if (indexPess != -1 && counterGeral > 0) {

                System.out.println("\u001B[1mDeseja fazer o gráfico de outra pessoa? |1-Sim| |0-Não|\u001B[0m");
                input = scanner.next();
                option = checkIntSN(input, checker);
            } else {
                System.out.println("*******************\u001B[1mOs gráficos foram concluídos com sucesso\u001B[0m*******************");
            }
        }
        int x = 0;

        for (i = 0; i < counterGraficos; i++) {

            if (metodosIguais[i][4] == 3) {

                System.out.println("\n\u001B[1mEstão disponíveis análises gráficas?\u001B[0m");
                i = counterGraficos;
                x = 1;
            }
        }
        if (x == 1) {

            System.out.println("\u001B[1mDeseja fazer as análises? (isto irá guardar os resultados) |1-Sim| |0-Não|\u001B[0m");
            input = scanner.next();
            option = checkIntSN(input, checker);
            if (option != 0) {
                String compareEuler = "";
                String compareKutta = "";

                for (i = 0; i < counterGraficos; i++) {

                    if (metodosIguais[i][4] == 3) {
                        compareEuler = caminhoFinal + nomes[(int) metodosIguais[i][0]] + "m1" + "p" + String.valueOf(metodosIguais[i][1]).replace(".", "") + "t" + (int) metodosIguais[i][2] + "d" + (int) metodosIguais[i][3] + ".csv";
                        compareKutta = caminhoFinal + nomes[(int) metodosIguais[i][0]] + "m2" + "p" + String.valueOf(metodosIguais[i][1]).replace(".", "") + "t" + (int) metodosIguais[i][2] + "d" + (int) metodosIguais[i][3] + ".csv";
                        comparePlot(compareEuler, compareKutta, (int) metodosIguais[i][3], nomes[(int) metodosIguais[i][0]]);
                    }
                }
            }
        }
    }

    /*************************************************************************
     *Função modoNãoInterativo    										     *
     *************************************************************************
     * @param args                                                           *
     * @param h Step        							         		     *
     * @param n Valor da população  									     *
     * @param s n-1 												 	     *
     * @param sDias Número de dias 										     *
     * @param dias                                                           *
     * @param option Método												     *
     * @param caminhoFinal Caminho de ficheiros de resultados finais      	 *
     * @param caminhoInicial Localização do ficheiros de dados iniciais      *
     * @param idMetodo identificador de modoInterativo                       *
     * @param input leitura do input                                         *
     *************************************************************************/
    public static void modoNaoInterativo(String[] args, float h, float n, float s, float sDias, int dias, int option, String caminhoFinal, String caminhoInicial, int idMetodo, String input) {

        float checkerM = 0;
        float checkerD = 0;
        int i;
        if (args.length != 9) {
            mensagemErro(1);
            System.exit(0);
        }

        for (int b = 1; b < 8; b += 2) {

            if (args[b].equals("-m")) {
                input = args[b + 1];
                for(i = 0; i < input.length(); i++) {
                    if(input.indexOf(",") == i){
                        input = input.replaceAll(",", ".");
                    }
                }
                checkerM = Float.valueOf(input);
                option = (int) checkerM;
            }
            if (args[b].equals("-p")) {
                input = args[b + 1];
                for(i = 0; i < input.length(); i++) {
                    if(input.indexOf(",") == i){
                        input = input.replaceAll(",", ".");
                    }
                }
                h = Float.valueOf(input);
            }
            if (args[b].equals("-t")) {
                input = args[b + 1];
                for(i = 0; i < input.length(); i++) {
                    if(input.indexOf(",") == i){
                        input = input.replaceAll(",", ".");
                    }
                }
                n = Float.valueOf(input);
            }
            if (args[b].equals("-d")) {
                input = args[b + 1];
                for(i = 0; i < input.length(); i++) {
                    if(input.indexOf(",") == i){
                        input = input.replaceAll(",", ".");
                    }
                }
                checkerD = Float.valueOf(input);
                dias = (int) checkerD;
            }
        }

        caminhoInicial = args[0];
        caminhoInicial = "LAPR1FinalProject/" + caminhoInicial;

        if (option != 1 && option != 2 || h <= 0 || h >= 1 || n <= 0 || dias <= 0) {
            mensagemErro(1);
            System.exit(0);
        }

        if (n != (int) n || checkerM != option || checkerD != dias) {
            mensagemErro(1);
            System.exit(0);
        }

        if (!((args[0].substring(args[0].length() - 4, args[0].length())).equals(".csv"))) {
            mensagemErro(2);
            System.exit(0);
        }

        int linhas = checkNumberOfLines(caminhoInicial);
        float[][] matrix = new float[linhas][4];
        String[] nomes = readFile(matrix, linhas, caminhoInicial);

        int indexPess = 0;

        while (indexPess < linhas) {

            switch (option) {
                case 1:
                    euler(h, n, dias, matrix, linhas, caminhoFinal, nomes, indexPess);
                    break;
                case 2:
                    rK4(h, n, dias, matrix, linhas, caminhoFinal, nomes, indexPess);
                    break;
                default:
                    mensagemErro(3);
                    break;
            }
            indexPess++;
        }

        String caminhoFinalGnu;

        for (i = 0; i < indexPess; i++) {

            caminhoFinalGnu = caminhoFinal + nomes[i] + "m1" + "p" + String.valueOf(h).replace(".", "") + "t" + (int) n + "d" + dias + ".csv";
            gnuplot(caminhoFinalGnu, dias, idMetodo);
            caminhoFinalGnu = caminhoFinal + nomes[i] + "m2" + "p" + String.valueOf(h).replace(".", "") + "t" + (int) n + "d" + dias + ".csv";
            gnuplot(caminhoFinalGnu, dias, idMetodo);
        }
        System.out.println("Os ficheiros (.csv) e os gráficos (.png) foram criados com sucesso e encontram-se em: '/lapr1_1dm_grupo02/LAPR1FinalProject/Ficheiros_Resultados/'\n");
    }

    /*************************************************************************
     *Função Sistema EDOs      												 *
     *************************************************************************
     * @param t Dias          										         *
     * @param s Suscetíveis        									         *
     * @param taxaProp β        										     *
     * @param inf infetados        									         *
     * @return valor final              									 *
     *************************************************************************/
    public static float functionS(float t, float s, float taxaProp, float inf) {
        return -taxaProp * s * inf;
    }

    /*************************************************************************
     *Função Sistema EDOs       										     *
     *************************************************************************
     * @param t Dias          										         *
     * @param inf Número de Infetados       							     *
     * @param taxaPop ρ       							                     *
     * @param taxaProp β       							                     *
     * @param taxaRej γ     							                     *
     * @param taxaReI α                                                      *
     * @param s                                                              *
     * @param rec Número recuperados                                         *
     * @return valor final              									 *
     *************************************************************************/
    public static float functionI(float t, float inf, float taxaPop, float taxaProp, float taxaRej, float taxaReI, float s, float rec) {
        return taxaPop * taxaProp * s * inf - taxaRej * inf + taxaReI * rec;
    }

    /*************************************************************************
     *Função Sistema EDOs       										     *
     *************************************************************************
     * @param t Dias          				        						 *
     * @param rec Número de recuperados            							 *
     * @param taxaRej γ            							                 *
     * @param taxaReI α                                                      *
     * @param taxaPop ρ               							             *
     * @param taxaProp β               							             *
     * @param inf Número de Infetados              							 *
     * @param s                                                              *
     * @return valor final              									 *
     *************************************************************************/
    public static float functionR(float t, float rec, float taxaRej, float taxaReI, float taxaPop, float taxaProp, float inf, float s) {
        return taxaRej * inf - taxaReI * rec + (1 - taxaPop) * taxaProp * s * inf;
    }

    /*************************************************************************
     *Função de euler     											         *
     *************************************************************************
     * @param h Step            							         		 *
     * @param n Valor da população  									     *
     * @param dias Número de dias             								 *
     * @param matrix                                                         *
     * @param linhas Número de linhas 									     *
     * @param caminhoFinal Caminho de ficheiros de resultados finais      	 *
     * @param nomes    Lista de nomes									     *
     * @param indexPess Index da pessoa     								 *
     *************************************************************************/
    public static void euler(float h, float n, int dias, float[][] matrix, int linhas, String caminhoFinal, String[] nomes, int indexPess) {

        float s = n - 1;
        float sDias = s;
        float taxaProp = matrix[indexPess][0];
        float taxaRej = matrix[indexPess][1];
        float taxaPop = matrix[indexPess][2];
        float taxaReI = matrix[indexPess][3];
        float iDias = 1;
        float rDias = 0;
        float inf = 1;
        float rec = 0;
        float t = 0;
        int i = 0;
        float[][] resultados = new float[dias + 1][5];

        resultados[i][0] = i;
        resultados[i][1] = s;
        resultados[i][2] = inf;
        resultados[i][3] = rec;
        resultados[i][4] = n;

        System.out.println("\u001B[1mMétodo de Euler: " + nomes[indexPess] + "(h:" + h + " N:" + (int) n + " Dias:" + dias + ")" + "\u001B[0m\n");
        System.out.printf("Valor de S[%d]: %.2f%n", i, sDias);
        System.out.printf("Valor de I[%d]: %.2f%n", i, iDias);
        System.out.printf("Valor de R[%d]: %.2f%n", i, rDias);
        System.out.printf("Valor de N: %.2f%n", (sDias + iDias + rDias));
        System.out.printf("%n");

        while (i < dias) {

            for (float j = 0; j < 1; j += h) {

                sDias = s + h * functionS((t + i * h), s, taxaProp, inf);
                iDias = inf + h * functionI((t + i * h), inf, taxaPop, taxaProp, taxaRej, taxaReI, s, rec);
                rDias = rec + h * functionR((t + i * h), rec, taxaRej, taxaReI, taxaPop, taxaProp, inf, s);
                s = sDias;
                inf = iDias;
                rec = rDias;
            }

            System.out.printf("Valor de S[%d]: %.2f%n", (i + 1), sDias);
            System.out.printf("Valor de I[%d]: %.2f%n", (i + 1), iDias);
            System.out.printf("Valor de R[%d]: %.2f%n", (i + 1), rDias);
            System.out.printf("Valor de N: %.2f%n", (sDias + iDias + rDias));
            System.out.printf("%n");
            i++;

            resultados[i][0] = i;
            resultados[i][1] = sDias;
            resultados[i][2] = iDias;
            resultados[i][3] = rDias;
            resultados[i][4] = sDias + iDias + rDias;
        }

        String caminhoFinalGnu = caminhoFinal + nomes[indexPess] + "m1" + "p" + String.valueOf(h).replace(".", "") + "t" + (int) n + "d" + dias + ".csv";

        try {
            printFile(caminhoFinalGnu, resultados, dias);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*************************************************************************
     *Função de rK4     											         *
     *************************************************************************
     * @param h Step            							         		 *
     * @param n Valor da população  									     *
     * @param dias Número de dias             								 *
     * @param matrix                                                         *
     * @param linhas Número de linhas 									     *
     * @param caminhoFinal Caminho de ficheiros de resultados finais      	 *
     * @param nomes    Lista de nomes									     *
     * @param indexPess Index da pessoa     								 *
     *************************************************************************/
    public static void rK4(float h, float n, int dias, float[][] matrix, int linhas, String caminhoFinal, String[] nomes, int indexPess) {

        float s = n - 1;
        float sDias = s;
        float taxaProp = matrix[indexPess][0];
        float taxaRej = matrix[indexPess][1];
        float taxaPop = matrix[indexPess][2];
        float taxaReI = matrix[indexPess][3];
        float iDias = 1;
        float rDias = 0;
        float inf = 1;
        float rec = 0;
        float t = 0;
        int i = 0;
        float[][] resultados = new float[dias + 1][5];

        resultados[i][0] = i;
        resultados[i][1] = s;
        resultados[i][2] = inf;
        resultados[i][3] = rec;
        resultados[i][4] = n;

        System.out.println("\u001B[1mMétodo de Runge-Kutta: " + nomes[indexPess] + "(h:" + h + " N:" + (int) n + " Dias:" + dias + ")" + "\u001B[0m\n");
        System.out.printf("Valor de S[%d]: %.2f%n", i, sDias);
        System.out.printf("Valor de I[%d]: %.2f%n", i, iDias);
        System.out.printf("Valor de R[%d]: %.2f%n", i, rDias);
        System.out.printf("Valor de N: %.2f%n", (sDias + iDias + rDias));
        System.out.printf("%n");

        while (i < dias) {

            for (float j = 0; j < 1; j += h) {

                float Sk1 = h * functionS(t, s, taxaProp, inf);
                float Ik1 = h * functionI(t, inf, taxaPop, taxaProp, taxaRej, taxaReI, s, rec);
                float Rk1 = h * functionR(t, rec, taxaRej, taxaReI, taxaPop, taxaProp, inf, s);

                float Sk2 = h * functionS((t + h / 2), (s + Sk1 / 2), taxaProp, (inf + Ik1 / 2));
                float Ik2 = h * functionI((t + h / 2), (inf + Ik1 / 2), taxaPop, taxaProp, taxaRej, taxaReI, (s + Sk1 / 2), (rec + Rk1 / 2));
                float Rk2 = h * functionR((t + h / 2), (rec + Rk1 / 2), taxaRej, taxaReI, taxaPop, taxaProp, (inf + Ik1 / 2), (s + Sk1 / 2));

                float Sk3 = h * functionS((t + h / 2), (s + Sk2 / 2), taxaProp, (inf + Ik2 / 2));
                float Ik3 = h * functionI((t + h / 2), (inf + Ik2 / 2), taxaPop, taxaProp, taxaRej, taxaReI, (s + Sk2 / 2), (rec + Rk2 / 2));
                float Rk3 = h * functionR((t + h / 2), (rec + Rk2 / 2), taxaRej, taxaReI, taxaPop, taxaProp, (inf + Ik2 / 2), (s + Sk2 / 2));

                float Sk4 = h * functionS((t + h), (s + Sk3), taxaProp, (inf + Ik3));
                float Ik4 = h * functionI((t + h), (inf + Ik3), taxaPop, taxaProp, taxaRej, taxaReI, (s + Sk3), (rec + Rk3));
                float Rk4 = h * functionR((t + h), (rec + Rk3), taxaRej, taxaReI, taxaPop, taxaProp, (inf + Ik3), (s + Sk3));

                float Sk = (Sk1 + 2 * Sk2 + 2 * Sk3 + Sk4) / 6;
                float Ik = (Ik1 + 2 * Ik2 + 2 * Ik3 + Ik4) / 6;
                float Rk = (Rk1 + 2 * Rk2 + 2 * Rk3 + Rk4) / 6;

                sDias = s + Sk;
                iDias = inf + Ik;
                rDias = rec + Rk;
                t += h;
                s = sDias;
                inf = iDias;
                rec = rDias;
            }

            System.out.printf("Valor de S[%d]: %.2f%n", (i + 1), sDias);
            System.out.printf("Valor de I[%d]: %.2f%n", (i + 1), iDias);
            System.out.printf("Valor de R[%d]: %.2f%n", (i + 1), rDias);
            System.out.printf("Valor de N: %.2f%n", (sDias + iDias + rDias));
            System.out.printf("%n");
            i++;

            resultados[i][0] = i;
            resultados[i][1] = sDias;
            resultados[i][2] = iDias;
            resultados[i][3] = rDias;
            resultados[i][4] = sDias + iDias + rDias;
        }

        String caminhoFinalGnu = caminhoFinal + nomes[indexPess] + "m2" + "p" + String.valueOf(h).replace(".", "") + "t" + (int) n + "d" + dias + ".csv";
        try {
            printFile(caminhoFinalGnu, resultados, dias);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*************************************************************************
     * Função checkNumberOfLines                                             *
     *************************************************************************
     * @param caminhoInicial Localização do ficheiros de dados iniciais      *
     * @return linhas Número de linhas                                       *
     *************************************************************************/
    public static int checkNumberOfLines(String caminhoInicial) {

        int linhas = 0;

        try {

            Scanner scanner = new Scanner(new File(caminhoInicial));
            String line;

            while (scanner.hasNextLine()) {

                line = scanner.nextLine();

                if (line.trim().length() > 0) {      
                    linhas++;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        linhas -= 1;          
        return linhas;
    }

    /*************************************************************************
     *Função readFile                                                        *
     *************************************************************************
     * @param matrix                                                         *
     * @param linhas Número de linhas                                        *  
     * @param caminhoInicial Localização do ficheiros de dados iniciais      *
     * @return nomes Lista de nomes                                          *           
     *************************************************************************/
    public static String[] readFile(float[][] matrix, int linhas, String caminhoInicial) {

        String[] nomes = new String[linhas]; 

        try {
            Scanner scanner = new Scanner(new File(caminhoInicial));

            int lineNumber = 0;

            while (scanner.hasNextLine()) {

                int j;
                String line = scanner.nextLine();
                String[] values = line.split(";");

                if (lineNumber != 0) {  
                    nomes[lineNumber - 1] = values[0];  

                    for (j = 1; j < 5; j++) {
                    
                        matrix[lineNumber - 1][j - 1] = Float.valueOf(values[j].replace(",", "."));
                    }
                }
                lineNumber++; 
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return nomes;
    }

    /*************************************************************************
     *Função mensagemErro                                                    *
     *************************************************************************
     * @param valor Id do erro                                               *             
     *************************************************************************/
    public static void mensagemErro(int valor) {

        switch (valor) {

            case 1:
                System.out.println("***************************************************************************************");
                System.out.println("           \u001B[1m-> Erro na estrutura do ficheiro de input. Verifique se está correto <-\u001B[0m     ");
                System.out.println("***************************************************************************************");
                break;
            case 2:
                System.out.println("*********************************************************************************************");
                System.out.println("					\u001B[1m-> Tipo de ficheiro inválido (deveria ser do tipo .csv) <-\u001B[0m					");
                System.out.println("**********************************************************************************************");
                break;
            case 3:
                System.out.println("********************************************************");
                System.out.println("           \u001B[1m-> Método inválido/inexistente <-\u001B[0m          ");
                System.out.println("********************************************************");
                break;
            case 4:
                System.out.println("********************************************************");
                System.out.println("           \u001B[1m-> Opção inválida/inexistente <-\u001B[0m            ");
                System.out.println("********************************************************");
                break;
            case 5:
                System.out.println("********************************************************");
                System.out.println("           \u001B[1m-> Escolha uma pessoa diferente <-\u001B[0m            ");
                System.out.println("********************************************************");
                break;
            case 6:
                System.out.println("********************************************************");
                System.out.println("           \u001B[1m-> Opção inválida <-                           ");
                System.out.println("     Opções disponíveis |1-Sim| |0-Não|\u001B[0m                   ");
                System.out.println("********************************************************");
                break;
            case 7:
                System.out.println("********************************************************");
                System.out.println("           \u001B[1m-> Valor inválido <-\u001B[0m                          ");
                System.out.println("********************************************************");
                break;
            case 8:
                System.out.println("********************************************************");
                System.out.println("           \u001B[1m-> Ambos os métodos já foram feitos <-\u001B[0m                           ");
                System.out.println("********************************************************");
                break;
        }
    }

    /*************************************************************************
     *Função printFile                                                       *
     *************************************************************************
     * @param caminho_ficheiro Localização do ficheiro final                 *
     * @param resultados [dias][4] = matriz com a informação final           *
     * @param dias Limite de dias                                            *
     *************************************************************************/
    public static void printFile(String caminho_ficheiro, float resultados[][], int dias) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(caminho_ficheiro);

        pw.print("Dia;S;I;R;N\n");

        for (int i = 0; i < dias; i++) {
            pw.print((int) (resultados[i][0]) + ";");

            for (int j = 1; j < 5; j++) {
                pw.print(String.valueOf(resultados[i][j]).replace(".", ","));
                if (j < 4) {
                    pw.print(";");
                }
            }
            pw.println();
        }
        pw.close();
    }

    /*************************************************************************
     *Função gnuplot                                                         *
     *************************************************************************
     * @param caminhoFinalGnu caminho csv                                    *
     * @param dias número de dias                                            *
     * @param idMetodo identificador do modo                                 *              
     *************************************************************************/
    public static void gnuplot(String caminhoFinalGnu, int dias, int idMetodo) {

        String caminhoPng = caminhoFinalGnu.substring(0, caminhoFinalGnu.length() - 4);
        String[] png = {"-e", "set term png size 1200, 800",
                "-e", "set output '" + caminhoPng + ".png'",
                "-e", "replot"
        };

        String[] plot = {"LAPR1FinalProject/gnuplot/bin/gnuplot.exe",
                "-e", "set datafile separator ';'",
                "-e", "plot '" + caminhoFinalGnu + "' u 1:2 w l title 'S' lc rgb '#0000f8' lw 2,'" + caminhoFinalGnu + "' u 1:3 w l title 'I' lc rgb '#8b0000' lw 2,'" + caminhoFinalGnu + "' u 1:4 w l title 'R' lc rgb '#00a600' lw 2",
                "-e", "set xlabel 'Número de Dias' font ',16'",
                "-e", "set ylabel 'N' font ',16' rotate by 0",
                "-e", "set grid",
                "-e", "set key box",
                "-e", "set key width 1",
                "-e", "set key height 1",
                "-e", "set key font ',16'",
                "-e", "set border 3",
                "-e", "set tics nomirror",
                "-e", "set xtics 0,1," + dias,
                "-e", "set border lw 2",
                "-p", "-e", "set term wxt size 1200, 800",
                "-e", "replot"
        };
        try {

            Runtime rt = Runtime.getRuntime();
            Process prc = rt.exec(plot);
            int ans = 0;

            if (idMetodo == 1) {
                String input;
                float checker = 0;
                System.out.println("\u001B[1mDeseja guardar o gráfico? |1-Sim| |0-Não|\u001B[0m");
                input = scanner.next();
                ans = checkIntSN(input, checker);
            }

            rt.exec("taskkill /im gnuplot_qt.exe");
            prc.destroy();

            if (ans == 1 || idMetodo == 0) {

                int slength = plot.length - 5;
                String[] t = new String[slength + png.length];
                System.arraycopy(plot, 0, t, 0, slength);
                System.arraycopy(png, 0, t, slength, png.length);
                rt.exec(t);
            }

        } catch (Exception e) {
            System.err.println("Fail: " + e);
        }
    }

    /*************************************************************************
     *Função comparePlot                                                     *
     *************************************************************************
     * @param compareEuler caminho csv Euler                                 *
     * @param compareKutta caminho csv Kutta                                 *
     * @param dias número de dias                                            *  
     * @param nome nome da pessoa a comparar                                 *           
     *************************************************************************/
    public static void comparePlot(String compareEuler, String compareKutta, int dias, String nome) {

        String caminhoPng = "LAPR1FinalProject/Ficheiros_Resultados/" + nome + "m1&m2";
        String[] png = {"-e", "set term png size 1200, 800",
                "-e", "set output '" + caminhoPng + ".png'",
                "-e", "replot"
        };

        String[] plot = {"LAPR1FinalProject/gnuplot/bin/gnuplot.exe",
                "-e", "set datafile separator ';'",
                "-e", "plot '" + compareEuler + "' u 1:2 w l title 'SEuler' lc rgb '#0000f8' lw 2,'" + compareEuler + "' u 1:3 w l title 'IEuler' lc rgb '#8b0000' lw 2,'" + compareEuler + "' u 1:4 w l title 'REuler' lc rgb '#00a600' lw 2,'" + compareKutta + "' u 1:2 w l title 'SKutta' lc rgb '#87CEFA' lw 2 dt 2,'" + compareKutta + "' u 1:3 w l title 'IKutta' lc rgb '#F08080' lw 2 dt 2,'" + compareKutta + "' u 1:4 w l title 'RKutta' lc rgb '#90EE90' lw 2 dt 2",
                "-e", "set xlabel 'Número de Dias' font ',16'",
                "-e", "set ylabel 'N' font ',16' rotate by 0",
                "-e", "set grid",
                "-e", "set key box",
                "-e", "set key width 1",
                "-e", "set key height 1",
                "-e", "set key font ',16'",
                "-e", "set border 3",
                "-e", "set tics nomirror",
                "-e", "set xtics 0,1," + dias,
                "-e", "set border lw 2",
                "-p", "-e", "set term wxt size 1200, 800",
                "-e", "replot"
        };

        try {

            Runtime rt = Runtime.getRuntime();
            Process prc = rt.exec(plot);
            rt.exec("taskkill /im gnuplot_qt.exe");
            prc.destroy();
            int slength = plot.length - 5;
            String[] t = new String[slength + png.length];
            System.arraycopy(plot, 0, t, 0, slength);
            System.arraycopy(png, 0, t, slength, png.length);
            rt.exec(t);

        } catch (Exception e) {
            System.err.println("Fail: " + e);
        }
    }

    /*************************************************************************
     *Função printFileValores                                                *
     *************************************************************************
     * @param valoresInseridos valores inseridos pelo utilizador             *
     * @param counter contador (número de valores inseridos)                 *          
     *************************************************************************/
    public static void printFileValores(float[][] valoresInseridos, int counter) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter("LAPR1FinalProject/ValoresInseridos.csv");    // Criar o ficheiro tests.csv

        pw.print("Index;step;populacao;dias\n");    // Print do cabeçalho

        for (int i = 0; i < counter; i++) {

            pw.print(String.valueOf((int) valoresInseridos[i][0]) + ";" + String.valueOf(valoresInseridos[i][1]).replace(".", ",") + ";" + String.valueOf(valoresInseridos[i][2]) + ";" + String.valueOf(valoresInseridos[i][3]).replace(".", ",") + ";" + String.valueOf((int) valoresInseridos[i][4]));
            pw.println();
        }
        pw.close();
    }

    /*************************************************************************
     *Função newReadValores                                                  *
     *************************************************************************
     * @param counter contador (número de valores inseridos)                 *          
     *************************************************************************/
    public static float[][] newReadValores(int counter) {

        float[][] valoresMetodos = new float[counter][5];

        try {
            File file = new File("LAPR1FinalProject/ValoresInseridos.csv");
            Scanner scanner = new Scanner(file);

            int lineNumber = 0;

            while (scanner.hasNextLine()) {

                int j;
                String line = scanner.nextLine();
                String[] values = line.split(";");

                if (lineNumber != 0) {  

                    for (j = 0; j < 5; j++) {

                        
                        valoresMetodos[lineNumber - 1][j] = Float.valueOf(values[j].replace(",", "."));
                    }
                }
                lineNumber++; // Próxima linha
            }
            scanner.close();
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return valoresMetodos;
    }

    /*************************************************************************
     *Função checkIntSN                                                      *
     *************************************************************************
     * @param input valores inseridos pelo utilizador                        *   
     * @param escolha float de input                                         *
     * @return (int) escolha                                                 *         
     *************************************************************************/
    public static int checkIntSN(String input, float escolha){
        int i;
        for(i = 0; i < input.length(); i++) {
            if(input.indexOf(",") == i){
                input = input.replaceAll(",", ".");
            }
        }
        escolha = Float.valueOf(input);
        while (escolha < 0 || escolha > 1 || escolha != (int)escolha) {
            mensagemErro(6);
            input = scanner.next();
            for(i = 0; i < input.length(); i++) {
                if(input.indexOf(",") == i){
                    input = input.replaceAll(",", ".");
                }
            }
            escolha = Float.valueOf(input);
        }
        return (int) escolha;
    } 
}