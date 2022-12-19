//Podemos usar
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.lang.Runtime;

/*	2 Modos disponíveis:
	-> Modo interativo     -> (java -jar lapr1_1dm_grupo02.jar)
	-> Modo não interativo -> (java -jar lapr1_1dm_grupo02.jar ficheiroSIR.csv -m X -p Y -t Z -d K ficheiroResultado.csv)
 */
public class Main {
    public static void main(String[] args){
		float h;
		float n;
		float s;
		float sDias;
		int dias;
		int option;
		String caminhoFinal = "src/ficheiroResultado";
		String caminhoInicial = "src/ficheiroSIR.csv";

		if (args.length == 0) {

			int linhas = repeated(caminhoInicial);
			// Matrix para colocar os valores 
			float[][] matrix = new float[linhas-1][4];
			String[] nomes = repeatRead(matrix, linhas, caminhoInicial);

			//Modo iterativo
			int a = 0;
			Scanner scanner = new Scanner(System.in);
			while(a < linhas-1){
				System.out.println("|" + nomes[a] +"|");
				System.out.println(" Valor de h?");
				h = scanner.nextFloat();

				System.out.println(" Valor da população?");
				n = scanner.nextFloat();
				s = n - 1;
				sDias = n - 1;

				System.out.println(" Número de dias?");
				dias = scanner.nextInt();
				System.out.println(" -----------------------MENU-----------------------");
				System.out.println("| 1 - Método de Euler				   |");
				System.out.println("| 2 - Método de Runge-Kutta de 4ª ordem		   |");
				System.out.println(" --------------------------------------------------");
				option = scanner.nextInt();
				mSwitch(option, dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, a);
				a++;
			}
			scanner.close();
		}else{
			caminhoInicial = "src/"+ args[0];
			caminhoFinal = "src/"+ args[9].substring(0, args[9].length()-4);
			option = Integer.valueOf(args[2]); // metdo a usar (1-Euler, 2-RK4)
			h = Float.valueOf(args[4]);
			n = Float.valueOf(args[6]);
			dias = Integer.valueOf(args[8]);
			s  = n - 1;
			sDias = n - 1;

			int linhas = repeated(caminhoInicial);
			// Matrix para colocar os valores 
			float[][] matrix = new float[linhas-1][4];
			String[] nomes = repeatRead(matrix, linhas, caminhoInicial);
			
			int a = 0;
			while(a < linhas-1){
				mSwitch(option, dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, a);
				a++;
			}
		}
		//modo não iterativo
		/*
		 * Estrutura do modo não interativo ->
		 * 
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
		 * teste para os parametros ->  java -jar lapr1_1dm_grupo02.jar ficheiroSIR.csv -m 1 -p 0.10 -t 1000 -d 30 ficheiroResultado.csv
		 */	
	}
	/*************************************************************************
	 * Função para verificar o número de linhas do ficheiro csv              *
	 *************************************************************************
	 * @param String caminho_ficheiro                                        *
	 * @return linhas = numero de linhas                                     *
	 *************************************************************************/
	public static int checkNumberOfLines(String caminho_ficheiro) throws FileNotFoundException {

		Scanner scanner = new Scanner(new File(caminho_ficheiro));

        int linhas = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.trim().length() > 0) {
				linhas++;
			}
		}
        scanner.close();

        return linhas;
	}
	
	/*************************************************************************
	 * Função para ler os valores dos dados no ficheiroSIR.csv  		     *
	 *************************************************************************
	 * @param String caminho_ficheiro 										 *	
	 * @param String[][] matrix						 						 *				
	 * @return matrix[][]	= matriz com os dados 				     		 *
	 *************************************************************************/
	public static float[][] readFile(String caminho_ficheiro,float[][] matrix, String[] nomes)throws FileNotFoundException {

		Scanner scanner = new Scanner(new File(caminho_ficheiro));

		int lineNumber = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] values = line.split(";");
			if (lineNumber != 0) {
				nomes[lineNumber-1] = values[0];
				for (int j = 1; j < 5; j++) {
					matrix[lineNumber - 1][j - 1] = Float.valueOf(values[j].replace(",","."));
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
	public static void printFile(String caminho_ficheiro,float resultados[][], int dias) throws FileNotFoundException {

		PrintWriter pw = new PrintWriter(new File(caminho_ficheiro));	// Criar o ficheiro tests.csv

		pw.print("Dia;S;I;R;N\n");	// Print do cabeçalho

		for(int i = 0;i < dias;i++){
			pw.print((int)(resultados[i][0]) + ";");
			for(int j = 1;j < 5;j++){
				pw.print(String.valueOf(resultados [i][j]).replace(".",","));
				if(j<4){
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
	public static float functionS(float t, float s, float taxaProp, float inf){
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
	public static float functionI(float t, float inf, float taxaPop, float taxaProp,float taxaRej, float taxaReI, float s, float rec){
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
	public static float functionR(float t, float rec, float taxaRej, float taxaReI, float taxaPop, float taxaProp, float inf, float s){
		return taxaRej * inf - taxaReI * rec + (1 - taxaPop) * taxaProp * s * inf;
	}
	
	/*************************************************************************
	 *Função de Euler     											         *
	 *************************************************************************
	 * @param int dias número de dias          								 *
	 * @param float h step        							         		 *
	 * @param float[][] matrix 												 *
	 * @param linhas número de linhas 										 *
	 * @param n valor da população  										 *
	 * @param s n-1 												 		 *
	 * @param sDias num de dias 											 *
	 * @param String caminhoFinal ficheiro de resultados finais      		 *
	 * @param String[] nomes												 *
	 * @param int a															 *
	 *************************************************************************/
	public static void Euler(int dias, float h, float[][] matrix, int linhas, float n, float s, float sDias, String caminhoFinal, String[] nomes, int a){
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
		float[][] resultados = new float[dias+1][5];
		resultados[i][0] = i;
		resultados[i][1] = s;
		resultados[i][2] = inf;
		resultados[i][3] = rec;
		resultados[i][4] = n;
		System.out.println("Valor de S" + (i) + ": " + frmt.format(sDias));
		System.out.println("Valor de I" + (i) + ": " + frmt.format(iDias));
		System.out.println("Valor de R" + (i) + ": " + frmt.format(rDias));
		System.out.println("Valor de N: " + frmt.format(sDias + iDias + rDias));

		while(i < dias){
			for (float j = 0; j < 1; j+=h){
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
			resultados[i][4] = sDias+iDias+rDias;
		}

		try {
			printFile(caminhoFinal + nomes[a] + ".csv", resultados, dias);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*************************************************************************
	 *Função de Runge_kutta     											 *
	 *************************************************************************
	 * @param int dias número de dias          								 *
	 * @param float h step        							         		 *
	 * @param float[][] matrix 												 *
	 * @param linhas número de linhas 										 *
	 * @param n valor da população  										 *
	 * @param s n-1 												 		 *
	 * @param sDias num de dias 											 *
	 * @param String caminhoFinal ficheiro de resultados finais      		 *
	 * @param String[] nomes												 *
	 * @param int a															 *
	 *************************************************************************/
	public static void Runge_Kutta(int dias, float h, float[][] matrix, int linhas, float n, float s, float sDias, String caminhoFinal, String[] nomes, int a){
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
		float[][] resultados = new float[dias+1][5];
		resultados[i][0] = i;
		resultados[i][1] = s;
		resultados[i][2] = inf;
		resultados[i][3] = rec;
		resultados[i][4] = n;
		System.out.println("Valor de S" + (i) + ": " + frmt.format(sDias));
		System.out.println("Valor de I" + (i) + ": " + frmt.format(iDias));
		System.out.println("Valor de R" + (i) + ": " + frmt.format(rDias));
		System.out.println("Valor de N: " + frmt.format(sDias + iDias + rDias));
		
		while(i < dias){
			for (float j = 0; j < 1; j+=h){

				float Sk1 = h * functionS(t, s, taxaProp, inf);
				float Ik1 = h * functionI(t, inf, taxaPop, taxaProp, taxaRej, taxaReI, s, rec);
				float Rk1 = h * functionR(t, rec, taxaRej, taxaReI, taxaPop, taxaProp, inf, s);

				float Sk2 = h * functionS((t + h/2), (s + Sk1/2), taxaProp, (inf + Ik1/2));
				float Ik2 = h * functionI((t + h/2), (inf + Ik1/2), taxaPop, taxaProp, taxaRej, taxaReI, (s + Sk1/2), (rec + Rk1/2));
				float Rk2 = h * functionR((t + h/2), (rec + Rk1/2), taxaRej, taxaReI, taxaPop, taxaProp, (inf + Ik1/2), (s + Sk1/2));

				float Sk3 = h * functionS((t + h/2), (s + Sk2/2), taxaProp, (inf + Ik2/2));
				float Ik3 = h * functionI((t + h/2), (inf + Ik2/2), taxaPop, taxaProp, taxaRej, taxaReI, (s + Sk2/2), (rec + Rk2/2));
				float Rk3 = h * functionR((t + h/2), (rec + Rk2/2), taxaRej, taxaReI, taxaPop, taxaProp, (inf + Ik2/2), (s + Sk2/2));

				float Sk4 = h * functionS((t + h), (s + Sk3), taxaProp, (inf + Ik3));
				float Ik4 = h * functionI((t + h), (inf + Ik3), taxaPop, taxaProp, taxaRej, taxaReI, (s + Sk3), (rec + Rk3));
				float Rk4 = h * functionR((t + h), (rec + Rk3), taxaRej, taxaReI, taxaPop, taxaProp, (inf + Ik3), (s + Sk3));
				
				float Sk = (Sk1 + 2 * Sk2 + 2 * Sk3 + Sk4)/6;	
				float Ik = (Ik1 + 2 * Ik2 + 2 * Ik3 + Ik4)/6;		
				float Rk = (Rk1 + 2 * Rk2 + 2 * Rk3 + Rk4)/6;
				
				
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
			resultados[i][4] = sDias+iDias+rDias;
			}

		try {
			printFile(caminhoFinal + nomes[a] + ".csv", resultados, dias);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static int repeated(String caminhoInicial){
		int linhas = 0;
		// Chamar a função checkNumberOfLines
		try{
			linhas = checkNumberOfLines(caminhoInicial);
			} catch(FileNotFoundException e){
				e.printStackTrace();
			}
	    return linhas;
    }
	public static String[] repeatRead(float[][] matrix, int linhas, String caminhoInicial){

		String[] nomes = new String[linhas-1];
			
		// Chamar a função readFile
		try{
		matrix = readFile(caminhoInicial, matrix, nomes);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return nomes;
	}
	public static int mSwitch(int option, int dias, float h, float[][] matrix, int linhas, float n, float s, float sDias, String caminhoFinal, String[] nomes, int a){
		switch (option) {
			case 1:
				Euler(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, a);
				break;
			case 2:
				Runge_Kutta(dias, h, matrix, linhas, n, s, sDias, caminhoFinal, nomes, a);
				break;
			default:
				System.out.print("Opção inválida/inexistente");
				break;
		}
		return a;
	}
}