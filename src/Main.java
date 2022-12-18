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

	//Variaveis globais
    public static float t = 0;
	public static int dias;
    public static float populacao;
	public static int metodo;
	public static String ficheiroInicial = "ficheiroSIR.csv";
	public static String ficheiroFinal = "ficheiroResultado.csv";
	public static float h;
    public static float s = populacao - 1;
    public static float i = 1;
    public static float r = 0;
    public static float sn = populacao - 1;
    public static float in = 1;
    public static float rn = 0;
    public static float taxaProp = 0.002f;              //β
    public static float taxaRej = 0.01f;                //γ
    public static float taxaPop = 0.6f;                 //ρ
    public static float taxaReI = 0;                    //α
    
    public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
	

		if(args.length == 0){
			//modo iterativo
			System.out.println(" Valor de h?");
			h = scanner.nextFloat();
			System.out.println(" Valor da população?");
			populacao = scanner.nextFloat();
			System.out.println(" Número de dias?");
			dias = scanner.nextInt();
			System.out.println(" -----------------------MENU-----------------------");
			System.out.println("| 1 - Método de Euler				   |");
			System.out.println("| 2 - Método de Runge-Kutta de 4ª ordem		   |");
			System.out.println("| 3 - Sair					   |");
			System.out.println(" --------------------------------------------------");
			 metodo = scanner.nextInt();
			switch (metodo) {
				case 1:
					Euler();
					break;
				case 2:
					Runge_Kutta();
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
		 * teste para os parametros -> java -jar lapr1_1dm_grupo02.jar ficheiroSIR.csv -m 1 -p 0.10 -t 1000 -d 30 ficheiroResultado.csv
		 */

		ficheiroInicial = args[0];
		metodo = Integer.valueOf(args[2]);
		h = Float.valueOf(args[4]);
		populacao = Float.valueOf(args[6]);
		dias = Integer.valueOf(args[8]);
        ficheiroFinal = args[9];
		
		int linhas = 0;

		// Chamar a função checkNumberOfLines
		try{
		linhas = checkNumberOfLines(ficheiroInicial);
		} catch(FileNotFoundException e1){
			e1.printStackTrace();
		}

		// Matrix para colocar os valores 
		String[][] matrix = new String[linhas-1][4];
		
		// Chamar a função readFile
		try{
		 matrix = readFile(ficheiroInicial,matrix);	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
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
	 * @param float suscetíveis        										 *
	 *************************************************************************/
	public static float functionS(float t, float s){
		return -taxaProp * s * i;
	}
	
	/*************************************************************************
	 *Função Sistema EDOs       										     *
	 *************************************************************************
	 * @param float T dias          										 *
	 * @param float I número de Infetados       							 *
	 *************************************************************************/
	public static float functionI(float t, float i){
		return taxaPop * taxaProp * s * i - taxaRej * i + taxaReI * r;
	}
	
	/*************************************************************************
	 *Função Sistema EDOs       										     *
	 *************************************************************************
	 * @param float T dias          										 *
	 * @param float R número de recuperados        							 *
	 *************************************************************************/
	public static float functionR(float t, float r){
		return taxaRej * i - taxaReI * r + (1 - taxaPop) * taxaProp * s * i;
	}
	
	/*************************************************************************
	 *Função de Euler     													 *
	 *************************************************************************/
	public static void Euler(){

		DecimalFormat frmt = new DecimalFormat();
		int index = 0;
		float[][] resultados = new float[dias][5];
		System.out.println("Valor de S" + (index) + ": " + frmt.format(sn));
		System.out.println("Valor de I" + (index) + ": " + frmt.format(in));
		System.out.println("Valor de R" + (index) + ": " + frmt.format(rn));
		System.out.println("Valor de N: " + frmt.format(sn + in + rn));

		while(index < dias){
			sn = s + h * functionS((t + index * h), s);
			s = sn;
			in = i + h * functionI((t + index * h), i);
			i = in;
			rn = r + h * functionR((t + index * h), r);
			r = rn;
			System.out.println("Valor de S" + (index) + ": " + frmt.format(sn));
			System.out.println("Valor de I" + (index) + ": " + frmt.format(in));
			System.out.println("Valor de R" + (index) + ": " + frmt.format(rn));
			System.out.println("Valor de N: " + frmt.format(sn + in + rn));

			resultados[index][0] = i;
			resultados[index][1] = sn;
			resultados[index][2] = in;
			resultados[index][3] = rn;
			resultados[index][4] = sn+in+rn;
			index++;
		}

		try {
			printFile("src/ficheiroResultado.csv", resultados, dias);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*************************************************************************
	 *Função de Runger_Kutta     											 *
	 *************************************************************************/
	public static void Runge_Kutta(){

		DecimalFormat frmt = new DecimalFormat();
		int index = 0;
		float[][] resultados = new float[dias][5];
		System.out.println("Valor de S" + (index) + ": " + frmt.format(sn));
		System.out.println("Valor de I" + (index) + ": " + frmt.format(in));
		System.out.println("Valor de R" + (index) + ": " + frmt.format(rn));
		System.out.println("Valor de N: " + frmt.format(sn + in + rn));
		
		while(index < dias){
			float Sk1 = h * functionS(t,s);
			float Sk2 = h * functionS((t + h/2), (s + Sk1/2));
			float Sk3 = h * functionS((t + h/2), (s + Sk2/2));
			float Sk4 = h * functionS((t + h), (s + Sk3));
			float Sk = (Sk1 + 2 * Sk2 + 2 * Sk3 + Sk4)/6;
			sn = s + Sk;
			s = sn;

			float Ik1 = h * functionI(t,i);
			float Ik2 = h * functionI((t + h/2), (i + Ik1/2));
			float Ik3 = h * functionI((t + h/2), (i + Ik2/2));
			float Ik4 = h * functionI((t + h), (i + Ik3));
			float Ik = (Ik1 + 2 * Ik2 + 2 * Ik3 + Ik4)/6;
			in = i + Ik;
			i = in;

			float Rk1 = h * functionR(t,r);
			float Rk2 = h * functionR((t + h/2), (r + Rk1/2));
			float Rk3 = h * functionR((t + h/2), (r + Rk2/2));
			float Rk4 = h * functionR((t + h), (r + Rk3));
			float Rk = (Rk1 + 2 * Rk2 + 2 * Rk3 + Rk4)/6;
			rn = r + Rk;
			r = rn;
			t += h;

			System.out.println("Valor de S" + (i) + ": " + frmt.format(sn));
			System.out.println("Valor de I" + (i) + ": " + frmt.format(in));
			System.out.println("Valor de R" + (i) + ": " + frmt.format(rn));
			System.out.println("Valor de N: " + frmt.format(sn + in + rn));

			resultados[index][0] = index;
			resultados[index][1] = sn;
			resultados[index][2] = in;
			resultados[index][3] = rn;
			resultados[index][4] = sn+in+rn;
			index++;
			}

		try {
			printFile("src/ficheiroResultado.csv", resultados, dias);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}