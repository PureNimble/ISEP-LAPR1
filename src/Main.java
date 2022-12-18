
//Podemos usar
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;
import java.io.PrintWriter;
import java.text.DecimalFormat;


public class Main {

	public static float Sn = 0;
    public static float In = 0;
    public static float Rn = 0;
    public static float T = 1;
    public static float N = 1000;
    public static float S = N - 1;
    public static float I = 1;
    public static float R = 0;
    public static float taxaProp = 0.02f;              //β
    public static float taxaPop = 0.7f;               //ρ
    public static float taxaRej = 0.001f;               //γ
    public static float taxaReI = 0.009f;               //α
    public static float h = 0.1f;
    public static float n = 3;
    public static void main(String[] args){
		/*
		 * Estrutura do modo não interativo ->			java -jar nome_programa.jar ficheiroSIR.csv -m X -p Y -t Z -d K ficheiroResultado.csv
		 * 
		 *	-m -> Função         | (1-Euler,2-RK4)
		 *  -p -> h              |condição (0<h<1)    
         *  -t -> população(N)   | 0 <  N
		 *  -d -> Número de dias | 0 < dias 
		 * 
		 * teste para os parametros -> java Main.java "1" "0.1" "1000" "10"  
		 */
	
		//char funcao = args[0].charAt(0);		
		//float h = Float.parseFloat(args[1]);	
		//int n = Integer.parseInt(args[2]);		
		//int dias = Integer.parseInt(args[3]);

		//Função para ir buscar o ficheiro.jar
		int linhas=0;
		
		try {
			getJarFile(args);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		

		try{
		linhas = checkNumberOfLines("exemplo_parametros_modelo.csv");
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
		// matrix para colocar os valores 
		String[][] matrix = new String[linhas-1][4];
		
		// Chamar a função PrintFile
		try{
		 matrix = readFile("exemplo_parametros_modelo.csv",matrix);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		for (int i = 0; i < linhas-1; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print("["+matrix[i][j]+"]" + " ");
			}
			System.out.println("\n");
		}
		/* 
		// Chamar a função PrintFile
		try {
			printFile("teste.csv",matrix,dias);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		*/
	}

	/*************************************************************************
	 * Função para abrir o ficheiro .jar                                     *
	 *************************************************************************
	 * @param string caminho_ficheiro                                        *
	 *                                                                       *
	 * @return linhas = numero de linhas                                     *
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
	 * @param string caminho_ficheiro                                        *
	 *                                                                       *
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
	 * @param string caminho_ficheiro 										 *	
	 * 									 									 *				
	 * @return matrix[][]	= matriz com os dados 						     *
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
	 * @param string caminho_ficheiro = onde está o ficheiro final           *
	 * @param int dados [dias][5] = matriz com a informação final       	 *
	 * @param int dias = limite de dias                                      *
	 *************************************************************************/

	public static void printFile(String caminho_ficheiro,float resultados[][],int dias) throws FileNotFoundException {

		PrintWriter pw = new PrintWriter(new File("teste.csv"));		// Criar o ficheiro tests.csv

		StringBuffer csvHeader = new StringBuffer("");						// Criar uma string para dar print ao cabeçalho

		csvHeader.append("Dia;S;I;R;N\n");									// Escrever o cabeçalho das colunas
		StringBuffer csvData = new StringBuffer("");

		pw.write(csvHeader.toString());

		for(int i =0;i<dias;i++){
			for(int j=0;j<5;j++){
				csvData.append(resultados [i][j] + ';');
			}
		pw.write(csvData.toString());
		pw.close();
		}
	}
	public static float functionS(float T, float S){
		return (-taxaProp * S * I);
	}
	
	public static float functionI(float T, float S){
		return ((taxaPop * taxaProp * S * I) - (taxaRej * I) + (taxaReI * R));
	}
	
	public static float functionR(float T, float S){
		return ((taxaRej * I) - (taxaReI * R) + (1 - taxaPop) * (taxaProp * S * I));
	}
	
	public static void Euler(){
	DecimalFormat frmt = new DecimalFormat();
	int i = 0;
	while(i < n){
			Sn = S + h * functionS(T + i * h, S);
			S = Sn;
			In = I + h * functionI(T + i * h, I);
			I = In;
			Rn = R + h * functionR(T + i * h, R);
			R = Rn;
			i++;
			System.out.println("Valor de S" + (i) + ": " + frmt.format(Sn));
			System.out.println("Valor de I" + (i) + ": " + frmt.format(In));
			System.out.println("Valor de R" + (i) + ": " + frmt.format(Rn));
		} 
	}
	public static void Runge_Kutta(){
	DecimalFormat frmt = new DecimalFormat();
	int i = 0;
	while(i < n){
			float Sk1 = h * functionS(T,S);
			float Sk2 = h * functionS(T + h/2, S + Sk1/2);
			float Sk3 = h * functionS(T + h/2, S + Sk2/2);
			float Sk4 = h * functionS(T + h, S + Sk3);
			float Sk = (Sk1 + 2 * Sk2 + 2 * Sk3 + Sk4)/6;~
			Sn = S + Sk;
			S = Sn;
	
			float Ik1 = h * functionI(T,I);
			float Ik2 = h * functionI(T + h/2, I + Ik1/2);
			float Ik3 = h * functionI(T + h/2, I + Ik2/2);
			float Ik4 = h * functionI(T + h, I + Ik3);
			float Ik = (Ik1 + 2 * Ik2 + 2 * Ik3 + Ik4)/6;
			In = I + Ik;
			I = In;
	
			float Rk1 = h * functionR(T,R);
			float Rk2 = h * functionR(T + h/2, R + Rk1/2);
			float Rk3 = h * functionR(T + h/2, R + Rk2/2);
			float Rk4 = h * functionR(T + h, R + Rk3);
			float Rk = (Rk1 + 2 * Rk2 + 2 * Rk3 + Rk4)/6;
			Rn = R + Rk;
			R = Rn;
	
			i++;
			T = T + h;
	
			System.out.println("Valor de S" + (i) + ": " + frmt.format(Rn));
			System.out.println("Valor de I" + (i) + ": " + frmt.format(In));
			System.out.println("Valor de R" + (i) + ": " + frmt.format(Rn));
		}
	}
}