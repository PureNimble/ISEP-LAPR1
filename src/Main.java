import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args){
		
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
	//compile by > javac nome do programa.java run by > java nome do ficheiro.java e parametros á frente (com espaços)
}