package attributeChange;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class FileTrans {
public static void main(String[] args) throws IOException {
	 //load csv
    CSVLoader loader = new CSVLoader();
    loader.setSource(new File("C:\\等待校验\\buddymove_holidayiq.csv"));
    Instances csv = loader.getDataSet();
    System.out.println(csv);
    System.out.println("load csv successfully.");

    //save arff
    ArffSaver saver = new ArffSaver();
    saver.setInstances(csv);
    try {
		saver.setFile(new File("C:\\等待校验\\buddymove_holidayiq.arff"));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    saver.writeBatch();


	
	
	
}
}
