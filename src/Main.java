//Podemos usar
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.text.DecimalFormat;

/*	2 Modos disponíveis:
	-> Modo interativo     -> (java -jar lapr1_1dm_grupo02.jar)
	-> Modo não interativo -> (java -jar lapr1_1dm_grupo02.jar ficheiroSIR.csv -m X -p Y -t Z -d K ficheiroResultado.csv)
 */
public class Main {
    public static void main(String[] args){

		float taxaProp = 0.002f;              //β
    	float taxaRej = 0.01f;                //γ
    	float taxaPop = 0.6f;                 //ρ
    	float taxaReI = 0f;                    //α
		float h;
		float N;
		float I = 1;
		float R = 0;
		float T = 0;
		float In = 1;
		float Rn = 0;
		int n;
		String caminhoFinal = "src/ficheiroResultado.csv";
		String caminhoInicial = "src/ficheiroSIR.csv";

		Scanner scanner = new Scanner(System.in);
		System.out.println(" Valor de h?");
		h = scanner.nextFloat();

		System.out.println(" Valor da população?");
		N = scanner.nextFloat();
		float S = N - 1;
		float Sn = N - 1;

		System.out.println(" Número de dias?");
		n = scanner.nextInt();

		if(args.length == 0){
			//modo iterativo
			System.out.println(" -----------------------MENU-----------------------");
			System.out.println("| 1 - Método de Euler				   |");
			System.out.println("| 2 - Método de Runge-Kutta de 4ª ordem		   |");
			System.out.println("| 3 - Sair					   |");
			System.out.println(" --------------------------------------------------");
			int option = scanner.nextInt();
			switch (option) {
				case 1:
					Euler(n, h, taxaProp, taxaRej, taxaPop, taxaReI, I, N, S, Sn, R, T, In, Rn, caminhoFinal);
					break;
				case 2:
					Runge_Kutta(n, h, taxaProp, taxaRej, taxaPop, taxaReI, I, N, S, Sn, R, T, In, Rn, caminhoFinal);
					break;
				case 3:
					System.exit(0);
				default:
					System.out.print("Opção inválida/inexistente");
					break;
			}
			scanner.close();
		}
		//modo não iterativo
		for (int i = 0; i < args.length; i++) {
			System.out.println("Argument " + i + ": " + args[i]);
		}
		
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
		 * teste para os parametros ->
		 * ->aqui
		 */
         
         
		//char funcao = args[0].charAt(0);		
		//float h = Float.parseFloat(args[1]);	
		//int n = Integer.parseInt(args[2]);		
		//int dias = Integer.parseInt(args[3]);

		int linhas = 0;
		
		// Chamar a função para ir buscar o ficheiro.jar
		try {
			getJarFile(args);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		// Chamar a função checkNumberOfLines
		try{
		linhas = checkNumberOfLines(caminhoInicial);
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
		// matrix para colocar os valores 
		String[][] matrix = new String[linhas-1][4];
		
		// Chamar a função readFile
		try{
		 matrix = readFile(caminhoInicial, matrix);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Print da matrix
		for (int i = 0; i < linhas-1; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print("["+matrix[i][j]+"]" + " ");
			}
			System.out.println("\n");
		}
	}

	/*************************************************************************
	 * Função para abrir o ficheiro .jar                                     *
	 *************************************************************************
	 * @param String[] caminho_ficheiro                                      *
	 * @return linhas = número de linhas                                     *
	 *************************************************************************/
	public static void getJarFile(String[] caminho_ficheiro) throws FileNotFoundException {

		if (caminho_ficheiro.length < 1) {
			System.out.println("Error: No .jar file specified.");
			return;
		}
		String jarFileName = caminho_ficheiro[0];

		File jarFile = new File(jarFileName);
		if (!jarFile.exists()) {
			System.out.println("Error: .jar file does not exist.");
			return;
		}
		if (!jarFile.canRead()) {
			System.out.println("Error: .jar file is not readable.");
			return;
		}

		String jarFilePath = jarFile.getAbsolutePath();
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
	public static String[][] readFile(String caminho_ficheiro,String[][] matrix)throws FileNotFoundException {

		Scanner scanner = new Scanner(new File(caminho_ficheiro));

		int lineNumber = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] values = line.split(";");
			if (lineNumber != 0) {
				for (int j = 1; j < 5; j++) {
					matrix[lineNumber - 1][j - 1] = values[j];
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
	 * @param float T dias          										 *
	 * @param float S suscetíveis        									 *
	 * @param float taxaProp β        										 *
	 *************************************************************************/
	public static float functionS(float T, float S, float taxaProp, float I){
		return -taxaProp * S * I;
	}
	
	/*************************************************************************
	 *Função Sistema EDOs       										     *
	 *************************************************************************
	 * @param float T dias          										 *
	 * @param float I número de Infetados       							 *
	 * @param float taxaPop ρ       							             *
	 * @param float taxaProp β       							             *
	 * @param float taxaRej γ     							                 *
	 * @param float taxaReI α                                                *
	 *************************************************************************/
	public static float functionI(float T, float I, float taxaPop, float taxaProp,float taxaRej, float taxaReI, float S, float R){
		return taxaPop * taxaProp * S * I - taxaRej * I + taxaReI * R;
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
	 *************************************************************************/
	public static float functionR(float T, float R, float taxaRej, float taxaReI, float taxaPop, float taxaProp, float I, float S){
		return taxaRej * I - taxaReI * R + (1 - taxaPop) * taxaProp * S * I;
	}
	
	/*************************************************************************
	 *Função de Euler     											         *
	 * @param int n dias          										     *
	 * @param float h espaçamento        							         *
	 * @param float taxaProp β       							             *
	 * @param float taxaRej γ      							            	 *
	 * @param float taxaPop ρ        							         	 *
	 * @param float taxaReI α                                                *
	 * @param String caminhoFinal ficheiro de resultados finais              *
	 *************************************************************************/
	public static void Euler(int n, float h, float taxaProp, float taxaRej, float taxaPop, float taxaReI, float I, float N, float S, float Sn, float R, float T, float In, float Rn, String caminhoFinal){
		
		DecimalFormat frmt = new DecimalFormat("#.##");
		int i = 0;
		float[][] resultados = new float[n][5];
		System.out.println("Valor de S" + (i) + ": " + frmt.format(Sn));
		System.out.println("Valor de I" + (i) + ": " + frmt.format(In));
		System.out.println("Valor de R" + (i) + ": " + frmt.format(Rn));
		System.out.println("Valor de N: " + frmt.format(Sn + In + Rn));

		while(i < n){
			for (float j = 0; j < 1; j+=h){
				Sn = S + h * functionS((T + i * h), S, taxaProp, I);
				In = I + h * functionI((T + i * h), I, taxaPop, taxaProp, taxaRej, taxaReI, S, R);
				Rn = R + h * functionR((T + i * h), R, taxaRej, taxaReI, taxaPop, taxaProp, I, S);
				S = Sn;
				I = In;
				R = Rn;
			}
			System.out.println("Valor de S" + (i) + ": " + frmt.format(Sn));
			System.out.println("Valor de I" + (i) + ": " + frmt.format(In));
			System.out.println("Valor de R" + (i) + ": " + frmt.format(Rn));
			System.out.println("Valor de N: " + frmt.format(Sn + In + Rn));

			resultados[i][0] = i;
			resultados[i][1] = Sn;
			resultados[i][2] = In;
			resultados[i][3] = Rn;
			resultados[i][4] = Sn+In+Rn;
			i++;
		}

		try {
			printFile(caminhoFinal, resultados, n);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*************************************************************************
	 *Função de Runge_kutta     											 *
	 *************************************************************************
	 * @param int n dias          										     *
	 * @param float h espaçamento        							         *
	 * @param float taxaProp β       							             *
	 * @param float taxaRej γ      							            	 *
	 * @param float taxaPop ρ        							         	 *
	 * @param float taxaReI α        							         	 *
	 * @param String caminhoFinal ficheiro de resultados finais      		 *
	 *************************************************************************/
	public static void Runge_Kutta(int n, float h, float taxaProp, float taxaRej, float taxaPop, float taxaReI, float I, float N, float S, float Sn, float R, float T, float In, float Rn, String caminhoFinal){

		DecimalFormat frmt = new DecimalFormat("#.##");
		int i = 0;
		float[][] resultados = new float[n][5];
		System.out.println("Valor de S" + (i) + ": " + frmt.format(Sn));
		System.out.println("Valor de I" + (i) + ": " + frmt.format(In));
		System.out.println("Valor de R" + (i) + ": " + frmt.format(Rn));
		System.out.println("Valor de N: " + frmt.format(Sn + In + Rn));
		
		while(i < n){
			for (float j = 0; j < 1; j+=h){
				float Sk1 = h * functionS(T, S, taxaProp, I);
				float Sk2 = h * functionS((T + h/2), (S + Sk1/2), taxaProp, I);
				float Sk3 = h * functionS((T + h/2), (S + Sk2/2), taxaProp, I);
				float Sk4 = h * functionS((T + h), (S + Sk3), taxaProp, I);
				float Sk = (Sk1 + 2 * Sk2 + 2 * Sk3 + Sk4)/6;
				

				float Ik1 = h * functionI(T, I, taxaPop, taxaProp, taxaRej, taxaReI, S, R);
				float Ik2 = h * functionI((T + h/2), (I + Ik1/2), taxaPop, taxaProp, taxaRej, taxaReI, S, R);
				float Ik3 = h * functionI((T + h/2), (I + Ik2/2), taxaPop, taxaProp, taxaRej, taxaReI, S, R);
				float Ik4 = h * functionI((T + h), (I + Ik3), taxaPop, taxaProp, taxaRej, taxaReI, S, R);
				float Ik = (Ik1 + 2 * Ik2 + 2 * Ik3 + Ik4)/6;

				float Rk1 = h * functionR(T, R, taxaRej, taxaReI, taxaPop, taxaProp, I, S);
				float Rk2 = h * functionR((T + h/2), (R + Rk1/2), taxaRej, taxaReI, taxaPop, taxaProp, I, S);
				float Rk3 = h * functionR((T + h/2), (R + Rk2/2), taxaRej, taxaReI, taxaPop, taxaProp, I, S);
				float Rk4 = h * functionR((T + h), (R + Rk3), taxaRej, taxaReI, taxaPop, taxaProp, I, S);
				float Rk = (Rk1 + 2 * Rk2 + 2 * Rk3 + Rk4)/6;
				Sn = S + Sk;
				In = I + Ik;
				Rn = R + Rk;
				T += h;
				S = Sn;
				I = In;
				R = Rn;
			}
			System.out.println("Valor de S" + (i) + ": " + frmt.format(Sn));
			System.out.println("Valor de I" + (i) + ": " + frmt.format(In));
			System.out.println("Valor de R" + (i) + ": " + frmt.format(Rn));
			System.out.println("Valor de N: " + frmt.format(Sn + In + Rn));

			resultados[i][0] = i;
			resultados[i][1] = Sn;
			resultados[i][2] = In;
			resultados[i][3] = Rn;
			resultados[i][4] = Sn+In+Rn;
			i++;
			}

		try {
			printFile(caminhoFinal, resultados, n);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}