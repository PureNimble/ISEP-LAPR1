//Podemos usar

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.io.PrintWriter;
import java.text.DecimalFormat;
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
         * args[9] -> caminho do ficheiroResultado.csv
         *
         * teste para os parametros ->  java -jar lapr1_1dm_grupo02.jar ficheiroSIR.csv -m 1 -p 0.10 -t 1000 -d 30 .csv
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
    }

    /*************************************************************************
     * Função para verificar o número de linhas do ficheiro csv              *
     *************************************************************************
     * @param String caminho_ficheiro                                        *
     * @return linhas = numero de linhas                                     *
     *************************************************************************/
    public static int checkNumberOfLines(String caminho_ficheiro) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(caminho_ficheiro));
        scanner = new Scanner(new File(caminho_ficheiro));

        int linhas = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().length() > 0) {
                linhas++;
            }
        }

        return linhas;
    }

    /*************************************************************************
     * Função para ler os valores dos dados no ficheiroSIR.csv  		     *
     *************************************************************************
     * @param String caminho_ficheiro 										 *
     * @param float[][] matrix = matrix com os dados 				     	 *
	 * @param String[] nomes = vetor com os nomes 				     	 	 *
	 * @return matrix = matrix com os dados 				     	 		 *
     *************************************************************************/
    public static float[][] readFile(String caminho_ficheiro, float[][] matrix, String[] nomes) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(caminho_ficheiro));

        int lineNumber = 0;

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            String[] values = line.split(";");

            if (lineNumber != 0) {
                nomes[lineNumber - 1] = values[0];

                for (int j = 1; j < 5; j++) {
                    matrix[lineNumber - 1][j - 1] = Float.valueOf(values[j].replace(",", "."));
                }
            }
            lineNumber++;
        }
        scanner.close();

        return matrix;
    }

    /*************************************************************************
     *Função para escrever os dados do sistema em ficheiroResultado.csv      *
     *************************************************************************
     * @param String caminho_ficheiro = onde está o ficheiro final           *
     * @param float[][] resultados [dias][4] = matriz com a informação final *
     * @param int dias = limite de dias                                      *
     *************************************************************************/
    public static void printFile(String caminho_ficheiro, float resultados[][], int dias) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(new File(caminho_ficheiro));    // Criar o ficheiro tests.csv

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
     * @param float t dias          										 *
     * @param float s suscetíveis        									 *
     * @param float taxaProp β        										 *
     * @param float inf infetados        									 *
     *************************************************************************/
    public static float functionS(float t, float s, float taxaProp, float inf) {
        return -taxaProp * s * inf;
    }

    /*************************************************************************
     *Função Sistema EDOs       										     *
     *************************************************************************
     * @param float t dias          										 *
     * @param float inf número de Infetados       							 *
     * @param float taxaPop ρ       							             *
     * @param float taxaProp β       							             *
     * @param float taxaRej γ     							                 *
     * @param float taxaReI α                                                *
     * @param float s                                                        *
     * @param float rec número recuperados                                   *
     *************************************************************************/
    public static float functionI(float t, float inf, float taxaPop, float taxaProp, float taxaRej, float taxaReI, float s, float rec) {
        return taxaPop * taxaProp * s * inf - taxaRej * inf + taxaReI * rec;
    }

    /*************************************************************************
     *Função Sistema EDOs       										     *
     *************************************************************************
     * @param float T dias          										 *
     * @param float R número de recuperados        							 *
     * @param float taxaRej γ     							                 *
     * @param float taxaReI α                                                *
     * @param float taxaPop ρ       							             *
     * @param float taxaProp β       							             *
     * @param float inf número de Infetados       							 *
     * @param float s                                                        *
     *************************************************************************/
    public static float functionR(float t, float rec, float taxaRej, float taxaReI, float taxaPop, float taxaProp, float inf, float s) {
        return taxaRej * inf - taxaReI * rec + (1 - taxaPop) * taxaProp * s * inf;
    }

    /*************************************************************************
     *Função de Euler     											         *
     *************************************************************************
     * @param int dias número de dias          								 *
     * @param float h step        							         		 *
     * @param float[][] matrix 												 *
     * @param int linhas número de linhas 									 *
     * @param float n valor da população  									 *
     * @param float s n-1 												 	 *
     * @param float sDias num de dias 										 *
     * @param String caminhoFinal ficheiro de resultados finais      		 *
     * @param String[] nomes												 *
     * @param int a															 *
     *************************************************************************/
    public static String Euler(int dias, float h, float[][] matrix, int linhas, float n, float s, float sDias, String caminhoFinal, String[] nomes, int a) {

        float taxaProp = matrix[a][0];
        float taxaRej = matrix[a][1];
        float taxaPop = matrix[a][2];
        float taxaReI = matrix[a][3];
        float iDias = 1;
        float rDias = 0;
        float inf = 1;
        float rec = 0;
        float t = 0;
        DecimalFormat frmt = new DecimalFormat("#.##");
        int i = 0;
        float[][] resultados = new float[dias + 1][5];
        resultados[i][0] = i;
        resultados[i][1] = s;
        resultados[i][2] = inf;
        resultados[i][3] = rec;
        resultados[i][4] = n;

        System.out.println("Valor de S" + (i) + ": " + frmt.format(sDias));
        System.out.println("Valor de I" + (i) + ": " + frmt.format(iDias));
        System.out.println("Valor de R" + (i) + ": " + frmt.format(rDias));
        System.out.println("Valor de N: " + frmt.format(sDias + iDias + rDias));

        while (i < dias) {

            for (float j = 0; j < 1; j += h) {

                sDias = s + h * functionS((t + i * h), s, taxaProp, inf);
                iDias = inf + h * functionI((t + i * h), inf, taxaPop, taxaProp, taxaRej, taxaReI, s, rec);
                rDias = rec + h * functionR((t + i * h), rec, taxaRej, taxaReI, taxaPop, taxaProp, inf, s);
                s = sDias;
                inf = iDias;
                rec = rDias;
            }
            System.out.println("Valor de S" + (i) + ": " + frmt.format(sDias));
            System.out.println("Valor de I" + (i) + ": " + frmt.format(iDias));
            System.out.println("Valor de R" + (i) + ": " + frmt.format(rDias));
            System.out.println("Valor de N: " + frmt.format(sDias + iDias + rDias));
            i++;

            resultados[i][0] = i;
            resultados[i][1] = sDias;
            resultados[i][2] = iDias;
            resultados[i][3] = rDias;
            resultados[i][4] = sDias + iDias + rDias;
        }

        String caminhoFinalGnu = caminhoFinal + nomes[a] + "m1" + "p" + String.valueOf(h).replace(".", "") + "t" + (int)n + "d" + dias + ".csv";
        try {
            printFile(caminhoFinalGnu, resultados, dias);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return caminhoFinalGnu;
    }

    /*************************************************************************
     *Função de Runge_kutta     											 *
     *************************************************************************
     * @param int dias número de dias          								 *
     * @param float h step        							         		 *
     * @param float[][] matrix 												 *
     * @param int linhas número de linhas 									 *
     * @param float n valor da população  								     *
     * @param float s n-1 												 	 *
     * @param floats Dias num de dias 										 *
     * @param String caminhoFinal ficheiro de resultados finais      		 *
     * @param String[] nomes												 *
     * @param int a															 *
     *************************************************************************/
    public static String Runge_Kutta(int dias, float h, float[][] matrix, int linhas, float n, float s, float sDias, String caminhoFinal, String[] nomes, int a) {

        float taxaProp = matrix[a][0];
        float taxaRej = matrix[a][1];
        float taxaPop = matrix[a][2];
        float taxaReI = matrix[a][3];
        float iDias = 1;
        float rDias = 0;
        float inf = 1;
        float rec = 0;
        float t = 0;
        DecimalFormat frmt = new DecimalFormat("#.##");
        int i = 0;
        float[][] resultados = new float[dias + 1][5];
        resultados[i][0] = i;
        resultados[i][1] = s;
        resultados[i][2] = inf;
        resultados[i][3] = rec;
        resultados[i][4] = n;
        System.out.println("Valor de S" + (i) + ": " + frmt.format(sDias));
        System.out.println("Valor de I" + (i) + ": " + frmt.format(iDias));
        System.out.println("Valor de R" + (i) + ": " + frmt.format(rDias));
        System.out.println("Valor de N: " + frmt.format(sDias + iDias + rDias));

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
            System.out.println("Valor de S" + (i) + ": " + frmt.format(sDias));
            System.out.println("Valor de I" + (i) + ": " + frmt.format(iDias));
            System.out.println("Valor de R" + (i) + ": " + frmt.format(rDias));
            System.out.println("Valor de N: " + frmt.format(sDias + iDias + rDias));
            i++;
            resultados[i][0] = i;
            resultados[i][1] = sDias;
            resultados[i][2] = iDias;
            resultados[i][3] = rDias;
            resultados[i][4] = sDias + iDias + rDias;
        }

        String caminhoFinalGnu = caminhoFinal + nomes[a] + "m2" + "p" + String.valueOf(h).replace(".", "") + "t" + (int)n + "d" + dias + ".csv";
        try {
            printFile(caminhoFinalGnu, resultados, dias);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return caminhoFinalGnu;
    }

    public static int repeated(String caminhoInicial) {
        int linhas = 0;
        // Chamar a função checkNumberOfLines
        try {
            linhas = checkNumberOfLines(caminhoInicial);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return linhas;
    }

    public static String[] repeatRead(float[][] matrix, int linhas, String caminhoInicial) {

        String[] nomes = new String[linhas - 1];

        // Chamar a função readFile
        try {
            matrix = readFile(caminhoInicial, matrix, nomes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return nomes;
    }

    public static int mSwitch(int option, int dias, float h, float[][] matrix, int linhas, float n, float s, float sDias, String caminhoFinal, String[] nomes, int a) {

        switch (option) {
            case 1:
                Euler(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, a);
                break;
            case 2:
                Runge_Kutta(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, a);
                break;
            default:
                mensagemErro(3);
                break;
        }
        return a;
    }
	/*************************************************************************
     *Função Modo Interativo    											 *
     *************************************************************************
     * @param float h step        							         		 *
     * @param float n valor da população  									 *
     * @param float s n-1 												 	 *
     * @param float sDias num de dias 										 *
     * @param int dias													     *
     * @param int option													 *
     * @param String caminhoFinal ficheiro de resultados finais      		 *
     * @param String caminhoInicial ficheiro SIR     		                 *
     *************************************************************************/
	public static void modoInterativo(float h, float n, float s, float sDias, int dias, int option, String caminhoFinal, String caminhoInicial){
		int linhas = repeated(caminhoInicial);
         // Matrix para colocar os valores
        float[][] matrix = new float[linhas - 1][4];
        String[] nomes = repeatRead(matrix, linhas, caminhoInicial);
        int[] indices = new int[linhas - 1];
        int[] metodos = new int[linhas - 1];

        //Modo iterativo
        int counter = 0;
        int countergrafic = 0;

        while (counter < linhas - 1 && option != 0) {

        	System.out.println("Selecione uma pessoa");

            for (int i = 0; i < linhas - 1; i++) {
                System.out.println(i + 1 + " - |" + nomes[i] + "|");
            }

            int a = scanner.nextInt() - 1;

            while (((a < 0) || (a >= linhas - 1) || (indices[a] == 1))) {

                if ((a < 0) || (a >= linhas - 1)) {
        	        mensagemErro(4);
                } else {
                    System.out.println(nomes[a] + " já foi selecionado/a");
                    mensagemErro(5);
                }
                a = scanner.nextInt() - 1;
            }

            indices[a]++;
            do{
                System.out.println(" Valor de h? (Ex.: 0,1)");
                h = scanner.nextFloat();
            }
            while(h <= 0 || h > 1);

            System.out.println(" Valor da população? (Ex.: 1000)");
            n = scanner.nextFloat();
            s = n - 1;
            sDias = n - 1;

            System.out.println(" Número de dias? (Ex.: 30)");
            dias = scanner.nextInt();

			System.out.println(" -----------------------MENU-----------------------");
			System.out.println("| 1 - Método de Euler				   |");
			System.out.println("| 2 - Método de Runge-Kutta de 4ª ordem		   |");
			System.out.println(" --------------------------------------------------");
			option = scanner.nextInt();

            while(option != 2 && option != 1){
                mensagemErro(1);
                option = scanner.nextInt();
            }

            if(option == 1){
                Euler(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, a);
                metodos[a] = 1;
            } else{
                Runge_Kutta(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, a);
                metodos[a] = 2;
            }

            counter++;
            if(counter != linhas-1){
                System.out.println("Deseja Procurar mais nomes? |1-Sim| |0-Não|");
                option = scanner.nextInt();
    
                while (option != 1 && option != 0) {

                    mensagemErro(6);
                    option = scanner.nextInt();
                }
            }
        }
        if (counter == linhas - 1) {

            System.out.println("Já percorreu todas as pessoas");
        }
        System.out.println("Deseja converter os resultados em gráfico? |1-Sim| |0-Não|");
            option = scanner.nextInt();
            while (option!= 1 && option!= 0) {
                mensagemErro(6);
                option = scanner.nextInt();
            }
        while(countergrafic < counter && option != 0){
            System.out.println("Deseja fazer o gráfico de quem?");
            for (int i = 0; i < linhas - 1; i++) {
                if(indices[i] == 1){
                    System.out.println(i+1 + " - |" + nomes[i] + "|");
                }
            }
            int pess = scanner.nextInt() - 1;

            while(pess >= linhas-1 || pess < 0 || indices[pess] == 0 || metodos[pess] != 2 && metodos[pess] != 1){
                    mensagemErro(4);
                pess = scanner.nextInt() - 1;
            }
            String caminhoFinalGnu = caminhoFinal + nomes[pess] + "m" + metodos[pess] + "p" + String.valueOf(h).replace(".", "") + "t" + (int)n + "d" + dias + ".csv";
            gnuplot(caminhoFinalGnu);
            metodos[pess] = 3;
            countergrafic++;
            if(countergrafic != counter){
                System.out.println("Deseja fazer um novo gráfico? |1- Sim| |0- Não|");
                option = scanner.nextInt();
                while (option!= 1 && option!= 0) {
                    mensagemErro(6);  
                    option = scanner.nextInt();      
                }
            }
        }
        if(countergrafic == counter){
            System.out.println("Todos os gráficos já foram concluidos");
        }
        scanner.close();
	}
	public static void modoNaoInterativo(String[] args, float h, float n, float s, float sDias, int dias, int option, String caminhoFinal, String caminhoInicial){

        if(args.length != 10){
            mensagemErro(1);
            System.exit(0);
        }

        for(int b = 1; b<8; b+=2) {
            if (args[b].equals("-m")) {
                option = Integer.valueOf(args[b+1]);
            }
            if (args[b].equals("-p")) {
                h = Float.valueOf(args[b+1]);
            }
            if (args[b].equals("-t")) {
                n = Float.valueOf(args[b+1]);
            }
            if (args[b].equals("-d")) {
                dias = Integer.valueOf(args[b+1]);
            }
        }

		if(!((args[9].substring(args[9].length() - 4, args[9].length())).equals(".csv") && ((args[0].substring(args[0].length() - 4, args[0].length())).equals(".csv")))){
			mensagemErro(2);
            System.exit(0);
		}

		caminhoInicial = args[0];
        caminhoInicial = "LAPR1FinalProject/" + caminhoInicial;
        caminhoFinal = args[9].substring(0, args[9].length() - 4);
        caminhoFinal = "LAPR1FinalProject/" + caminhoFinal;
        s = n - 1;
        sDias = n - 1;

        int linhas = repeated(caminhoInicial);
        // Matrix para colocar os valores
        float[][] matrix = new float[linhas - 1][4];
        String[] nomes = repeatRead(matrix, linhas, caminhoInicial);

        int a = 0;
        while (a < linhas - 1) {
            mSwitch(option, dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, a);
            a++;
        }
	}
    public static void mensagemErro(int valor) {
        // 0 = tudo bem
        // 1 = erro na estrutura do ficheiro
        // 2 = tem demasiadas casas decimais
        switch (valor) {
            case 1:
                System.out.println("\n***************************************************************************************");
                System.out.println("\n           -> Erro na estrutura do ficheiro de input. Verifique se está correto <-     ");
                System.out.println("\n***************************************************************************************");
                break;

            case 2:
                System.out.println(
                        "\n*********************************************************************************************");
                System.out.println(
                        "\n					-> Tipo de ficheiro inválido (deveria ser do tipo .csv) <-					");
                System.out.println(
                        "**********************************************************************************************");
                break;
            case 3:
                System.out.println("\n********************************************************");
                System.out.println("\n           -> Método inválido/inexistente <-          ");
                System.out.println("\n********************************************************");
                break;
            case 4:
                System.out.println("\n********************************************************");
                System.out.println("\n           -> Opção inválida/inexistente <-            ");
                System.out.println("\n********************************************************");
                break;
            case 5:
                System.out.println("\n********************************************************");
                System.out.println("\n           -> Escolha uma pessoa diferente <-            ");
                System.out.println("\n********************************************************");
                break;
            case 6:
                System.out.println("\n********************************************************");
                System.out.println("\n           -> Opção inválida <-                           ");
                System.out.println("\n     Opções disponíveis |1-Sim| |0-Não|                   ");
                System.out.println("\n********************************************************");
                break;
            }
        }
    public static void gnuplot(String caminhoFinalGnu){
        String caminhoPng = caminhoFinalGnu.substring(0, caminhoFinalGnu.length()-4);
        String[] g = {"C:/Program Files/gnuplot/bin/gnuplot",
        "-e", "set datafile separator ';'",
        "-e", "plot '" + caminhoFinalGnu + "' u 1:2 w l title 'S','" + caminhoFinalGnu + "' u 1:3 w l title 'I','" + caminhoFinalGnu + "' u 1:4 w l title 'R'",
        "-e", "set xlabel 'Dias'",
        "-e", "set ylabel 'N'",
        "-e", "set grid",
        "-e", "set term png size 1200, 700",
        "-e", "set output '" + caminhoPng + ".png'",
        "-e", "replot"
        };

        String[] s = {"C:/Program Files/gnuplot/bin/gnuplot",
            "-e", "set datafile separator ';'",
            "-e", "plot '" + caminhoFinalGnu + "' u 1:2 w l title 'S','" + caminhoFinalGnu + "' u 1:3 w l title 'I','" + caminhoFinalGnu + "' u 1:4 w l title 'R'",
            "-e", "set xlabel 'Dias'",
            "-e", "set ylabel 'N'"
        };
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(s);
            System.out.print("Deseja guardar o gráfico? |1- Sim| |0- Não|");
            int ans = scanner.nextInt();
            while(ans != 0 && ans != 1){
                mensagemErro(6);
                ans = scanner.nextInt();
            }
            if(ans == 1){
                rt.exec(g);
            }
            InputStream stdin = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null)
                System.err.println("gnuplot:"+line);
            int exitVal = proc.waitFor();
            if (exitVal != 0){
                    System.out.println("gnuplot Process exitValue: " + exitVal);
            }
            proc.getInputStream().close();
            proc.getOutputStream().close();
            proc.getErrorStream().close();
        } catch (Exception e) {
            System.err.println("Fail: " + e);
        }
    }
}