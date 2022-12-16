import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args){
		/*
		 * Estrutura do modo não interativo ->			java-jar nome programa.jar ficheiroSIR.csv-m X-p Y-t Z-d K ficheiroResultado.csv
		 * 
		 *	X -> Função      | (1-Euler,2-RK4)
		 *  Y -> h           |condição (0<h<1)    
         *  Z -> população(N)| 0 <  N
		 *  K -> Número      | 0 < dias   
		 */

		char m = args[0].charAt(0);	//1 e 2
		float p = Float.parseFloat(args[1]);
		int t = Integer.parseInt(args[2]);	//num de população
		int d = Integer.parseInt(args[3]);	//num de dias
		try {
			usingPrintWriter(m, p, t, d);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void usingPrintWriter(char m, float p, int t, int d) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File("src/teste.csv"));
		StringBuffer csvHeader = new StringBuffer("");
		csvHeader.append("Dia;S;I;R;N\n");
		StringBuffer csvData = new StringBuffer("");
		pw.write(csvHeader.toString());
		csvData.append(m + ';'+ p +';'+ t + ';' + d + '\n');
		pw.write(csvData.toString());
		pw.close();
		}
}