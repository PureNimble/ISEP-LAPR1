import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args){
		

		try {
			usingPrintWriter();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void usingPrintWriter() throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File("src/teste.csv"));
		StringBuffer csvHeader = new StringBuffer("");
		csvHeader.append("Dia;S;I;R;N\n");
		StringBuffer csvData = new StringBuffer("");
		pw.write(csvHeader.toString());
		csvData.append("John"+';'+"24"+';'+"Engineer"+'\n');
		pw.write(csvData.toString());
		pw.close();
		}
}