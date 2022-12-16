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
	

		char funcao = args[0].charAt(0);	//1 e 2
		float h = Float.parseFloat(args[1]);
		int N = Integer.parseInt(args[2]);	//num de população
		int dias = Integer.parseInt(args[3]);	//num de dias
		try {
			usingPrintWriter(funcao, h, N, dias);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void usingPrintWriter(char m, float p, int t, int d) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File("teste.csv"));
		StringBuffer csvHeader = new StringBuffer("");
		csvHeader.append("Dia;S;I;R;N\n");
		StringBuffer csvData = new StringBuffer("");
		pw.write(csvHeader.toString());
		csvData.append(m + ';'+ p +';'+ t + ';' + d + '\n');
		pw.write(csvData.toString());
		pw.close();
		}
}