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
        String caminhoFinal = "LAPR1FinalProject/";
        String caminhoInicial = "LAPR1FinalProject/ficheiroSIR.csv";

        if (args.length == 0) {
            modoInterativo(h, n, s, sDias, dias, option, caminhoFinal, caminhoInicial);

        } else {
            modoNaoInterativo(args, h, n, s, sDias, dias, option, caminhoFinal, caminhoInicial);
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
     * @param matrix        												 *
     * @param linhas Número de linhas 									     *
     * @param n Valor da população  									     *
     * @param s n-1 												 	     *
     * @param sDias Número de dias 										     *
     * @param caminhoFinal Caminho de ficheiros de resultados finais      	 *
     * @param nomes	Lista de nomes											 *
     * @param indexPess Index da pessoa     								 *
     *************************************************************************/
    public static void Euler(int dias, float h, float[][] matrix, int linhas, float n, float s, float sDias, String caminhoFinal, String[] nomes, int indexPess) {

        // Inicialização das variáveis ( valores provenientes da matriz) 
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
     * @param matrix 												         *
     * @param linhas Número de linhas 									     *
     * @param n Número da população  								         *
     * @param s n-1 												 	     *
     * @param sDias Número de dias 										     *
     * @param caminhoFinal Caminho de ficheiros de resultados finais         *
     * @param nomes Lista de nomes	    									 *
     * @param indexPess Index da pessoa			        					 *
     *************************************************************************/
    public static void Runge_Kutta(int dias, float h, float[][] matrix, int linhas, float n, float s, float sDias, String caminhoFinal, String[] nomes, int indexPess) {

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
     * @param dias													         *
     * @param option Método	            								     *
     * @param caminhoFinal Caminho de ficheiros de resultados finais         *
     * @param caminhoInicial Localização do ficheiros de dados iniciais      *
     *************************************************************************/
    public static void modoInterativo(float h, float n, float s, float sDias, int dias, int option, String caminhoFinal, String caminhoInicial) {

        int linhas = checkNumberOfLines(caminhoInicial);

        // Matrix para colocar os valores
        float[][] matrix = new float[linhas][4];
        String[] nomes = readFile(matrix, linhas, caminhoInicial);
        int[] indices = new int[linhas];
        int[] metodos = new int[linhas];

        //Modo interativo
        int counter = 0;
        int countergrafic = 0;

        while (counter < linhas*2 && option != 0) {

            System.out.println("Selecione uma pessoa");

            for (int i = 0; i < linhas; i++) {
                System.out.println(i + 1 + " - |" + nomes[i] + "|");
            }

            int indexPess = scanner.nextInt() - 1;

            while (((indexPess < 0) || (indexPess >= linhas) || (indices[indexPess] >= 2))) {

                if ((indexPess < 0) || (indexPess >= linhas)) {
                    mensagemErro(4);
                } else {
                    System.out.println(nomes[indexPess] + " já foi selecionado/a");
                    mensagemErro(5);
                }
                indexPess = scanner.nextInt() - 1;
            }

            indices[indexPess]++;
            System.out.println(" Valor de h? (Ex.: 0,1)");
            h = scanner.nextFloat();

            while (h <= 0 || h >= 1) {
                mensagemErro(7);
                h = scanner.nextFloat();
            }

            System.out.println(" Valor da população? (Ex.: 1000)");
            n = scanner.nextFloat();
            while (n <= 0) {
                mensagemErro(7);
                n = scanner.nextFloat();
            }
            s = n - 1;
            sDias = n - 1;

            System.out.println(" Número de dias? (Ex.: 30)");
            dias = scanner.nextInt();

            while (dias <= 0) {
                mensagemErro(7);
                dias = scanner.nextInt();
            }

            System.out.println(" -----------------------MENU-----------------------");
            System.out.println("| 1 - Método de Euler				   |");
            System.out.println("| 2 - Método de Runge-Kutta de 4ª ordem		   |");
            System.out.println(" --------------------------------------------------");
            option = scanner.nextInt();

            while (option != 2 && option != 1 || metodos[indexPess] == option) {
                mensagemErro(1);
                option = scanner.nextInt();
            }

            if (option == 1) {

                Euler(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, indexPess);
                metodos[indexPess] ++;
            } else {

                Runge_Kutta(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, indexPess);
                Euler(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, indexPess);
                metodos[indexPess] += 2;
            }

            counter++;

            if (counter != linhas*2) {

                System.out.println("Deseja Procurar mais nomes? |1-Sim| |0-Não|");
                option = scanner.nextInt();

                while (option != 1 && option != 0) {

                    mensagemErro(6);
                    option = scanner.nextInt();
                }
            }
        }
        if (counter == linhas*2) {

            System.out.println("Já percorreu todas as pessoas");
        }

        System.out.println("Deseja converter os resultados em gráfico? |1-Sim| |0-Não|");
        option = scanner.nextInt();

        while (option != 1 && option != 0) {

            mensagemErro(6);
            option = scanner.nextInt();
        }
        while (countergrafic < counter && option != 0) {

            int pess = 0;

            if (counter != 0) {

                System.out.println("Deseja fazer o gráfico de quem?");

                for (int i = 0; i < linhas; i++) {
                    if (indices[i] >= 1) {
                        System.out.println(i + 1 + " - |" + nomes[i] + "|");
                    }
                }
                pess = scanner.nextInt() - 1;

            } else {
                for (int i = 0; i < linhas; i++) {
                    if (indices[i] == 1) {
                        pess = i;
                    }
                }
            }
            while (pess >= linhas || pess < 0 || indices[pess] == 0 || metodos[pess] < 1) {

                mensagemErro(4);
                pess = scanner.nextInt() - 1;
            }
            System.out.println("1 - Método de Euler");
            System.out.println("2 - Método de Runge-Kutta de 4ª ordem");
            option = scanner.nextInt();

            while (option != 2 && option != 1 || metodos[pess] != option && metodos[pess] != 3) {

                mensagemErro(1);
                option = scanner.nextInt();
            }

            String caminhoFinalGnu = caminhoFinal + nomes[pess] + "m" + option + "p" + String.valueOf(h).replace(".", "") + "t" + (int) n + "d" + dias + ".csv";
            gnuplot(caminhoFinalGnu, dias);
            metodos[pess] -= option;
            countergrafic++;

            if (countergrafic != counter) {

                System.out.println("Deseja fazer um novo gráfico? |1- Sim| |0- Não|");
                option = scanner.nextInt();

                while (option != 1 && option != 0) {
                    mensagemErro(6);
                    option = scanner.nextInt();
                }
            }
        }
        if (countergrafic == counter) {
            System.out.println("Todos os gráficos já foram concluidos");
        }
        scanner.close();
    }

    /*************************************************************************
     *Função ModoNãoInterativo    										     *
     *************************************************************************
     * @param args                 							         		 *
     * @param h Step        							         		     *
     * @param n Valor da população  									     *
     * @param s n-1 												 	     *
     * @param sDias Número de dias 										     *
     * @param dias													         *
     * @param option Método												     *
     * @param caminhoFinal Caminho de ficheiros de resultados finais      	 *
     * @param caminhoInicial Localização do ficheiros de dados iniciais      *
     *************************************************************************/
    public static void modoNaoInterativo(String[] args, float h, float n, float s, float sDias, int dias, int option, String caminhoFinal, String caminhoInicial) {

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
        s = n - 1;
        sDias = n - 1;

        if (option != 1 && option != 2 || h <= 0 || h >= 1 || n <= 0 || dias <= 0) {
            mensagemErro(1);
            System.exit(0);
        }

        if (!((args[0].substring(args[0].length() - 4, args[0].length())).equals(".csv"))) {
            mensagemErro(2);
            System.exit(0);
        }

        int linhas = checkNumberOfLines(caminhoInicial);

        // Matrix para colocar os valores
        float[][] matrix = new float[linhas][4];
        String[] nomes = readFile(matrix, linhas, caminhoInicial);

        int indexPess = 0;

        while (indexPess < linhas) {

            switch (option) {
                case 1:
                    Euler(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, indexPess);
                    break;
                case 2:
                    Runge_Kutta(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, indexPess);
                    break;
                default:
                    mensagemErro(3);
                    break;
            }
            indexPess++;
        }
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
        }
    }

    /*************************************************************************
     *Função gnuplot                                                         *
     *************************************************************************
     * @param caminhoFinalGnu                                                *             
     *************************************************************************/
    public static void gnuplot(String caminhoFinalGnu, int dias) {

        String caminhoPng = caminhoFinalGnu.substring(0, caminhoFinalGnu.length() - 4);
        String[] g = {"-e", "set term png size 1200, 800",
                "-e", "set output '" + caminhoPng + ".png'",
                "-e", "replot"
        };

        String[] s = {"LAPR1FinalProject/gnuplot/bin/gnuplot.exe",
                "-e", "set datafile separator ';'",
                "-e", "plot '" + caminhoFinalGnu + "' u 1:2 w l title 'S' lc rgb '#0000f8' lw 2,'" + caminhoFinalGnu + "' u 1:3 w l title 'I' lc rgb '#8b0000' lw 2,'" + caminhoFinalGnu + "' u 1:4 w l title 'R' lc rgb '#00a600' lw 2",
                "-e", "set xlabel 'Dias' font ',16'",
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
            System.out.print("Deseja guardar o gráfico? |1- Sim| |0- Não|");
            int ans = scanner.nextInt();
            rt.exec("taskkill /im gnuplot_qt.exe");
            prc.destroy();
            while (ans != 0 && ans != 1) {
                mensagemErro(6);
                ans = scanner.nextInt();
            }

            if (ans == 1) {
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
}