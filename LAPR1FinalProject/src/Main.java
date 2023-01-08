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
        
        float h = 0;
        float n = 0;
        float s = 0;
        float sDias = 0;
        int dias = 0;
        int option = 1;
        int idMetodo = 0;
        String caminhoFinal = "LAPR1FinalProject/Ficheiros_Resultados/";
        String caminhoInicial = "LAPR1FinalProject/ficheiroSIR.csv";

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
                        int escolha = scanner.nextInt();
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
            modoInterativo(h, n, s, sDias, dias, option, caminhoFinal, caminhoInicial, idMetodo);
        } else {
            modoNaoInterativo(args, h, n, s, sDias, dias, option, caminhoFinal, caminhoInicial, idMetodo);
        }
        System.out.println("*******************Fim do Programa*******************");
    }

    /*************************************************************************
     *Função para escrever os dados do sistema em ficheiroResultado.csv      *
     *************************************************************************
     * @param caminho_ficheiro Localização do ficheiro final                 *
     * @param resultados [dias][4] = matriz com a informação final           *
     * @param dias Limite de dias                                            *
     *************************************************************************/
    public static void printFile(String caminho_ficheiro, float resultados[][], int dias) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(caminho_ficheiro);    // Criar o ficheiro tests.csv

        pw.print("Dia;S;I;R;N\n");    // Print do cabeçalho

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
     *Função de Euler     											         *
     *************************************************************************
     * @param dias Número de dias             								 *
     * @param h Step            							         		 *
     * @param matrix                                                         *
     * @param linhas Número de linhas 									     *
     * @param n Valor da população  									     *
     * @param s n-1 												 	     *
     * @param sDias Número de dias 										     *
     * @param caminhoFinal Caminho de ficheiros de resultados finais      	 *
     * @param nomes    Lista de nomes									     *
     * @param indexPess Index da pessoa     								 *
     *************************************************************************/
    public static void Euler(float[][] valoresMetodos, float[][] matrix, int linhas, String caminhoFinal, String[] nomes, int index) {

        // Inicialização das variáveis ( valores provenientes da matriz) 
        int indexPess = (int) valoresMetodos[index][0];
        float h = valoresMetodos[index][1];
        float n = valoresMetodos[index][2];
        int dias = (int) valoresMetodos[index][3];
        float s = valoresMetodos[index][2] - 1;
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

        // Criação do nome para o caminho final (especificando o metodo usado e todos os parametros)
        String caminhoFinalGnu = caminhoFinal + nomes[indexPess] + "m1" + "p" + String.valueOf(h).replace(".", "") + "t" + (int) n + "d" + dias + ".csv";

        try {
            printFile(caminhoFinalGnu, resultados, dias);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*************************************************************************
     *Função de Runge_kutta     											 *
     *************************************************************************
     * @param dias Número de dias          								     *
     * @param h Step        							         		     *
     * @param matrix                                                         *
     * @param linhas Número de linhas 									     *
     * @param n Número da população  								         *
     * @param s n-1 												 	     *
     * @param sdias Número de dias 										     *
     * @param caminhoFinal Caminho de ficheiros de resultados finais         *
     * @param nomes Lista de nomes	    									 *
     * @param indexPess Index da pessoa			        					 *
     *************************************************************************/
    public static void Runge_Kutta(float[][] valoresMetodos, float[][] matrix, int linhas, String caminhoFinal, String[] nomes, int index) {

        int indexPess = (int) valoresMetodos[index][0];
        float h = valoresMetodos[index][1];
        float n = valoresMetodos[index][2];
        int dias = (int) valoresMetodos[index][3];
        float s = valoresMetodos[index][2] - 1;
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
     * Função para verificar o número de linhas do ficheiro csv              *
     *************************************************************************
     * @param caminhoInicial Localização do ficheiros de dados iniciais      *
     * @return linhas Número de linhas                                       *
     *************************************************************************/
    public static int checkNumberOfLines(String caminhoInicial) {

        int linhas = 0;

        // Chamar a função checkNumberOfLines
        try {

            Scanner scanner = new Scanner(new File(caminhoInicial));
            String line;

            while (scanner.hasNextLine()) {

                line = scanner.nextLine();

                if (line.trim().length() > 0) {     // verificar se existe caracteres na linha   
                    linhas++;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        linhas -= 1;            // remover a linha de cabeçalho
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

        String[] nomes = new String[linhas]; // Inicializar a array de nomes

        try {
            Scanner scanner = new Scanner(new File(caminhoInicial));

            int lineNumber = 0;

            while (scanner.hasNextLine()) {

                int j;
                String line = scanner.nextLine();
                String[] values = line.split(";");

                if (lineNumber != 0) {  // Passar a frente a linha do cabeçalho
                    nomes[lineNumber - 1] = values[0];  // O primeiro elemento da linha é o nome da pessoa

                    for (j = 1; j < 5; j++) {
                        // Colocar os valores na matriz substituindo os valores decimais de "," para "."
                        matrix[lineNumber - 1][j - 1] = Float.valueOf(values[j].replace(",", "."));
                    }
                }
                lineNumber++; // Próxima linha
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return nomes;
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
     *************************************************************************/
    public static void modoInterativo(float h, float n, float s, float sDias, int dias, int option, String caminhoFinal, String caminhoInicial, int idMetodo) {

        idMetodo = 1;
        int counter = 0;
        int c;
        String caminhoFinalGnu;
        int indexPess;
        int linhas = checkNumberOfLines(caminhoInicial);
        int i;
        // Matrix para colocar os valores
        float[][] matrix = new float[linhas][4];
        String[] nomes = readFile(matrix, linhas, caminhoInicial);
        float[][] valoresInseridos = new float[30][5];
        int[][] indices = new int[30][32];
        float metodosIguais[][] = new float[15][5];
        int temp = 0;

        while (option != 0 && counter < 30) {
            System.out.println("\u001B[1mSelecione uma pessoa:\u001B[0m");
            for (i = 0; i < linhas; i++) {
                System.out.println(i + 1 + " - |" + nomes[i] + "|");
            }

            indexPess = scanner.nextInt() - 1;

            while ((indexPess < 0) || (indexPess >= linhas)) {
                mensagemErro(4);
                indexPess = scanner.nextInt() - 1;
            }

            System.out.println("\u001B[1mValor de h? (Ex.: 0,1)\u001B[0m");
            h = scanner.nextFloat();

            while (h <= 0 || h >= 1) {

                mensagemErro(7);
                h = scanner.nextFloat();
            }

            System.out.println("\u001B[1mValor da população? (Ex.: 1000)\u001B[0m");
            n = scanner.nextFloat();

            while (n <= 0) {

                mensagemErro(7);
                n = scanner.nextFloat();
            }

            System.out.println("\u001B[1mNúmero de dias? (Ex.: 30)\u001B[1m");
            dias = scanner.nextInt();

            while (dias <= 0) {

                mensagemErro(7);
                dias = scanner.nextInt();
            }

            System.out.println(" -----------------------\u001B[1mMÉTODOS\u001B[0m-----------------------");
            System.out.println("| 1 - Método de Euler				      |");
            System.out.println("| 2 - Método de Runge-Kutta de 4ª ordem		      |");
            System.out.println(" -----------------------------------------------------");
            option = scanner.nextInt();

            while (option > 2 || option < 1) {

                mensagemErro(3);
                option = scanner.nextInt();
            }

            indices[indexPess][0]++;
            valoresInseridos[counter][0] = indexPess;
            valoresInseridos[counter][1] = h;
            valoresInseridos[counter][2] = n;
            valoresInseridos[counter][3] = dias;
            valoresInseridos[counter][4] = option;
            indices[indexPess][1] += option;
            counter++;
            c = counter - 1;

            for (i = 0; i < c; i++) {

                if (valoresInseridos[i][0] == indexPess) {

                    if (valoresInseridos[i][1] == h && valoresInseridos[i][2] == n && valoresInseridos[i][3] == dias && valoresInseridos[i][4] == option) {

                        System.out.println("Já existe um ficheiro para esses valores");
                        counter--;
                        indices[indexPess][1] -= option;
                    }
                    if (valoresInseridos[i][1] == h && valoresInseridos[i][2] == n && valoresInseridos[i][3] == dias && valoresInseridos[i][4] != option) {

                        indices[temp][2] = 1;
                        metodosIguais[temp][0] = indexPess;
                        metodosIguais[temp][1] = h;
                        metodosIguais[temp][2] = n;
                        metodosIguais[temp][3] = dias;
                        metodosIguais[temp][4] = 3;
                        temp++;
                    }
                }
            }
            if (counter != 30) {
                System.out.println("\u001B[1mDeseja inserir novos dados?" + " (mais " + (30 - counter) + " gráficos disponíveis)" + " |1-Sim| |0-Não|\u001B[0m");
                option = scanner.nextInt();

                while (option != 1 && option != 0) {
                    mensagemErro(6);
                    option = scanner.nextInt();
                }
            }
        }

        try {
            printFileValores(valoresInseridos, counter);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        float[][] valoresMetodos = newReadValues(counter);
        for (i = 0; i < counter; i++) {

            if (valoresMetodos[i][4] == 1 || valoresMetodos[i][4] == 3) {

                Euler(valoresMetodos, matrix, linhas, caminhoFinal, nomes, i);
            }
            if (valoresMetodos[i][4] == 2 || valoresMetodos[i][4] == 3) {

                Runge_Kutta(valoresMetodos, matrix, linhas, caminhoFinal, nomes, i);
            }
        }

        System.out.println("\u001B[1mDeseja converter os resultados em gráfico? |1-Sim| |0-Não|\u001B[0m");
        option = scanner.nextInt();
        c = counter;

        while (option != 1 && option != 0) {

            mensagemErro(6);
            option = scanner.nextInt();
        }
        while (option != 0 && c > 0) {

            System.out.println("\u001B[1mDeseja fazer o gráfico de quem?\u001B[0m");

            for (i = 0; i < linhas; i++) {

                if (indices[i][0] >= 1) {

                    System.out.println(i + 1 + " - |" + nomes[i] + "|");
                }
            }
            System.out.println("\u001B[1m0 - Fazer de Todos (ao fazer isto irá guardar todos os gráficos automáticamente)\u001B[0m");
            indexPess = scanner.nextInt() - 1;
            if (indexPess != -1) {
                while (indexPess < 0 || indexPess > counter || indices[indexPess][1] == 0) {
                    mensagemErro(4);
                    indexPess = scanner.nextInt() - 1;
                }
                System.out.println("Selecione um dos valores disponíveis para a/o" + nomes[indexPess]);
                for (i = 0; i < counter; i++) {

                    if (valoresMetodos[i][0] == indexPess) {

                        System.out.println(i + 1 + "- h:" + valoresMetodos[i][1] + " população:" + (int) valoresMetodos[i][2] + " dias" + (int) valoresMetodos[i][3] + " método" + (int) valoresMetodos[i][4] + "\n");
                    }
                }
                option = scanner.nextInt() - 1;
                while (option < 0 || option > counter) {
                    mensagemErro(4);
                    option = scanner.nextInt() - 1;
                }
                int var = 0;

                while (option >= 0 && option < counter) {

                    if (indices[indexPess][option + 3] == 0) {

                        if (valoresMetodos[option][0] == indexPess) {

                            indices[indexPess][option + 3]++;
                            indices[indexPess][1] -= valoresMetodos[option][4];
                            caminhoFinalGnu = caminhoFinal + nomes[(int) valoresMetodos[option][0]] + "m" + (int) valoresMetodos[option][4] + "p" + String.valueOf(valoresMetodos[option][1]).replace(".", "") + "t" + (int) valoresMetodos[option][2] + "d" + (int) valoresMetodos[option][3] + ".csv";
                            gnuplot(caminhoFinalGnu, (int) valoresMetodos[indexPess][3], idMetodo);
                            c--;
                            option = -1;

                        } else {
                            var = 1;
                        }

                    } else {

                        System.out.println("Esse gráfico já foi feito");
                        var = 1;
                    }

                    if (var == 1) {

                        option = -1;
                        while (option < 0 || option >= counter) {

                            mensagemErro(3);
                            option = scanner.nextInt() - 1;
                        }
                        var = 0;

                    }
                }

            } else {

                idMetodo = 0;

                for (i = 0; i < counter; i++) {

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
            if (indexPess != -1 && c > 0) {

                System.out.println("\u001B[1mDeseja fazer o gráfico de outra pessoa? |1-Sim| |0-Não|\u001B[0m");
                option = scanner.nextInt();

                while (option != 1 && option != 0) {
                    mensagemErro(6);
                    option = scanner.nextInt();
                }
            } else {
                System.out.println("\u001B[1mOs gráficos foram concluídos com sucesso\u001B[0m");
            }
        }
        int x = 0;

        for (i = 0; i < counter; i++) {

            if (metodosIguais[i][4] == 3) {

                System.out.println("\u001B[1mEstão disponíveis análises gráficas?\u001B[0m");
                i = counter;
                x = 1;
            }
        }
        if (x == 1) {

            System.out.println("\u001B[0mDeseja fazer as análises? (isto irá guardar os resultados) |1-Sim| |0-Não|\u001B[0m");
            option = scanner.nextInt();

            while (option != 0 && option != 1) {
                mensagemErro(6);
                option = scanner.nextInt();
            }
            if (option != 0) {
                String compareEuler = "";
                String compareKutta = "";

                for (i = 0; i < counter; i++) {

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
     *Função ModoNãoInterativo    										     *
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
     *************************************************************************/
    public static void modoNaoInterativo(String[] args, float h, float n, float s, float sDias, int dias, int option, String caminhoFinal, String caminhoInicial, int idMetodo) {

        if (args.length != 9) {
            mensagemErro(1);
            System.exit(0);
        }

        for (int b = 1; b < 8; b += 2) {

            if (args[b].equals("-m")) {
                option = Integer.valueOf(args[b + 1]);
            }
            if (args[b].equals("-p")) {
                h = Float.valueOf(args[b + 1]);
            }
            if (args[b].equals("-t")) {
                n = Float.valueOf(args[b + 1]);
            }
            if (args[b].equals("-d")) {
                dias = Integer.valueOf(args[b + 1]);
            }
        }

        caminhoInicial = args[0];
        caminhoInicial = "LAPR1FinalProject/" + caminhoInicial;

        if (option != 1 && option != 2 || h <= 0 || h >= 1 || n <= 0 || dias <= 0) {
            mensagemErro(1);
            System.exit(0);
        }

        if (!((args[0].substring(args[0].length() - 4, args[0].length())).equals(".csv"))) {
            mensagemErro(2);
            System.exit(0);
        }

        int linhas = checkNumberOfLines(caminhoInicial);
        float[][] valoresMetodos = new float[linhas][5];

        for (int i = 0; i < linhas; i++) {

            valoresMetodos[i][0] = i;
            valoresMetodos[i][1] = h;
            valoresMetodos[i][2] = n;
            valoresMetodos[i][3] = dias;
            valoresMetodos[i][4] = option;
        }

        // Matrix para colocar os valores
        float[][] matrix = new float[linhas][4];
        String[] nomes = readFile(matrix, linhas, caminhoInicial);

        int indexPess = 0;

        while (indexPess < linhas) {

            switch (option) {
                case 1:
                    Euler(valoresMetodos, matrix, linhas, caminhoFinal, nomes, indexPess);
                    break;
                case 2:
                    Runge_Kutta(valoresMetodos, matrix, linhas, caminhoFinal, nomes, indexPess);
                    break;
                default:
                    mensagemErro(3);
                    break;
            }
            indexPess++;
        }

        String caminhoFinalGnu;

        for (int i = 0; i < indexPess; i++) {

            caminhoFinalGnu = caminhoFinal + nomes[i] + "m1" + "p" + String.valueOf(h).replace(".", "") + "t" + (int) n + "d" + dias + ".csv";
            gnuplot(caminhoFinalGnu, dias, idMetodo);
            caminhoFinalGnu = caminhoFinal + nomes[i] + "m2" + "p" + String.valueOf(h).replace(".", "") + "t" + (int) n + "d" + dias + ".csv";
            gnuplot(caminhoFinalGnu, dias, idMetodo);
        }
        System.out.println("Os ficheiros (.csv) e os gráficos (.png) foram criados com sucesso e encontram-se em: '/lapr1_1dm_grupo02/LAPR1FinalProject/Ficheiros_Resultados/'\n");
    }

    /*************************************************************************
     *Função mensagemErro                                                    *
     *************************************************************************
     * @param valor Id do erro                                               *             
     *************************************************************************/
    public static void mensagemErro(int valor) {

        // 0 = tudo bem
        // 1 = erro na estrutura do ficheiro
        // 2 = tem demasiadas casas decimais
        switch (valor) {

            case 1:
                System.out.println("***************************************************************************************");
                System.out.println("           -> Erro na estrutura do ficheiro de input. Verifique se está correto <-     ");
                System.out.println("***************************************************************************************");
                break;
            case 2:
                System.out.println("*********************************************************************************************");
                System.out.println("					-> Tipo de ficheiro inválido (deveria ser do tipo .csv) <-					");
                System.out.println("**********************************************************************************************");
                break;
            case 3:
                System.out.println("********************************************************");
                System.out.println("           -> Método inválido/inexistente <-          ");
                System.out.println("********************************************************");
                break;
            case 4:
                System.out.println("********************************************************");
                System.out.println("           -> Opção inválida/inexistente <-            ");
                System.out.println("********************************************************");
                break;
            case 5:
                System.out.println("********************************************************");
                System.out.println("           -> Escolha uma pessoa diferente <-            ");
                System.out.println("********************************************************");
                break;
            case 6:
                System.out.println("********************************************************");
                System.out.println("           -> Opção inválida <-                           ");
                System.out.println("     Opções disponíveis |1-Sim| |0-Não|                   ");
                System.out.println("********************************************************");
                break;
            case 7:
                System.out.println("********************************************************");
                System.out.println("           -> Valor inválido <-                           ");
                System.out.println("********************************************************");
                break;
            case 8:
                System.out.println("********************************************************");
                System.out.println("           -> Ambos os métodos já foram feitos <-                           ");
                System.out.println("********************************************************");
                break;
        }
    }

    /*************************************************************************
     *Função gnuplot                                                         *
     *************************************************************************
     * @param caminhoFinalGnu                                                *             
     *************************************************************************/

    public static void gnuplot(String caminhoFinalGnu, int dias, int idMetodo) {

        String caminhoPng = caminhoFinalGnu.substring(0, caminhoFinalGnu.length() - 4);
        String[] g = {"-e", "set term png size 1200, 800",
                "-e", "set output '" + caminhoPng + ".png'",
                "-e", "replot"
        };

        String[] s = {"LAPR1FinalProject/gnuplot/bin/gnuplot.exe",
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
            Process prc = rt.exec(s);
            int ans = 0;

            if (idMetodo == 1) {

                System.out.print("Deseja guardar o gráfico? |1- Sim| |0- Não|");
                ans = scanner.nextInt();

                while (ans != 0 && ans != 1) {

                    mensagemErro(6);
                    ans = scanner.nextInt();
                }
            }

            rt.exec("taskkill /im gnuplot_qt.exe");
            prc.destroy();

            if (ans == 1 || idMetodo == 0) {

                int slength = s.length - 5;
                String[] t = new String[slength + g.length];
                System.arraycopy(s, 0, t, 0, slength);
                System.arraycopy(g, 0, t, slength, g.length);
                rt.exec(t);
            }

        } catch (Exception e) {
            System.err.println("Fail: " + e);
        }
    }

    public static void comparePlot(String compareEuler, String compareKutta, int dias, String nome) {

        String caminhoPng = "LAPR1FinalProject/" + nome + "m1&m2";
        String[] g = {"-e", "set term png size 1200, 800",
                "-e", "set output '" + caminhoPng + ".png'",
                "-e", "replot"
        };

        String[] s = {"LAPR1FinalProject/gnuplot/bin/gnuplot.exe",
                "-e", "set datafile separator ';'",
                "-e", "plot '" + compareEuler + "' u 1:2 w l title 'S1' lc rgb '#0000f8' lw 2,'" + compareEuler + "' u 1:3 w l title 'I1' lc rgb '#8b0000' lw 2,'" + compareEuler + "' u 1:4 w l title 'R1' lc rgb '#00a600' lw 2,'" + compareKutta + "' u 1:2 w l title 'S2' lc rgb '#87CEFA' lw 2 dt 2,'" + compareKutta + "' u 1:3 w l title 'I2' lc rgb '#F08080' lw 2 dt 2,'" + compareKutta + "' u 1:4 w l title 'R2' lc rgb '#90EE90' lw 2 dt 2",
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
            Process prc = rt.exec(s);

            rt.exec("taskkill /im gnuplot_qt.exe");
            prc.destroy();
            int slength = s.length - 5;
            String[] t = new String[slength + g.length];
            System.arraycopy(s, 0, t, 0, slength);
            System.arraycopy(g, 0, t, slength, g.length);
            rt.exec(t);

        } catch (Exception e) {
            System.err.println("Fail: " + e);
        }
    }

    public static void printFileValores(float[][] valoresInseridos, int counter) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter("LAPR1FinalProject/ValoresInseridos.csv");    // Criar o ficheiro tests.csv

        pw.print("Index;step;populacao;dias\n");    // Print do cabeçalho

        for (int i = 0; i < counter; i++) {

            pw.print(String.valueOf((int) valoresInseridos[i][0]) + ";" + String.valueOf(valoresInseridos[i][1]).replace(".", ",") + ";" + String.valueOf(valoresInseridos[i][2]) + ";" + String.valueOf(valoresInseridos[i][3]).replace(".", ",") + ";" + String.valueOf((int) valoresInseridos[i][4]));
            pw.println();
        }
        pw.close();
    }

    public static float[][] newReadValues(int counter) {

        float[][] valoresMetodos = new float[counter][5];

        try {
            File file = new File("LAPR1FinalProject/ValoresInseridos.csv");
            Scanner scanner = new Scanner(file);

            int lineNumber = 0;

            while (scanner.hasNextLine()) {

                int j;
                String line = scanner.nextLine();
                String[] values = line.split(";");

                if (lineNumber != 0) {  // Passar a frente a linha do cabeçalho

                    for (j = 0; j < 5; j++) {

                        // Colocar os valores na matriz substituindo os valores decimais de "," para "."
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
}