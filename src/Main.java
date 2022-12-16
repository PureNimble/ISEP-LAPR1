import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
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
		

		char funcao = args[0].charAt(0);		
		float h = Float.parseFloat(args[1]);	
		int N = Integer.parseInt(args[2]);		
		int dias = Integer.parseInt(args[3]);
		
		// matrix para colocar os valores 
		float[][] matrix = new float[dias][5];
	
		// Chamar a função PrintFile
		try{
		 matrix = readFile("caminho/do/ficheiro.csv",dias,matrix);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Chamar a função PrintFile
		try {
			printFile("teste.csv",matrix,dias);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/*************************************************************************
	 * Função para ler os valores dos dados no ficheiroSIR.csv *		     *
	 *************************************************************************
	 * @param string caminho_ficheiro 										 *	
	 * 									 									 *				
	 * @return matrix[][]	= matriz com os dados 						     *
	 *************************************************************************/

	public static float[][] readFile(String caminho_ficheiro,int dias,float[][] matrix)throws FileNotFoundException {
		

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
}