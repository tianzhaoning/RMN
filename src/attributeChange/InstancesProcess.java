package attributeChange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class InstancesProcess {
public static void main(String[] args) throws IOException {
	 //CSVLoader loader = new CSVLoader();
	 //  loader.setSource(new File("C:\\3.csv"));
	 //  Instances csv = loader.getDataSet();
	//  System.out.println(csv);
	    //  ArffSaver saver = new ArffSaver();
       // saver.setInstances(csv);
       // try {
       //     saver.setFile(new File("C:\\Users\\name\\Desktop\\3.arff"));
       //     saver.writeBatch();
      //  } catch (IOException e) {
      //      e.printStackTrace();
      //  }

    String sourceFilePath=new String("C:\\income_evaluation.csv");
	String targetFilePath=new String("C:\\3.csv");
    InstancesProcess IP=new InstancesProcess();
    IP.init(sourceFilePath, targetFilePath);
     
   }




  public void init(String sourceFilePath,String targetFilePath ) throws IOException{
	//����·������Դ�ļ�
	FileReader file=new FileReader(sourceFilePath);
	//��ȡԴ�ļ�
    BufferedReader reader = new BufferedReader(file);
    //����·������Ŀ���ļ�
	File StorePath = new File(targetFilePath);
	//��ʼ��Ŀ���ļ���ʼд����
	BufferedWriter bw = new BufferedWriter(new FileWriter(StorePath,false));
	//��ʼ��ȡ
	String line;
   
	int k=0;
	while((line=reader.readLine())!=null){
	 k++;
	    String newline=new String();
	    //ȥ˫����
	   
		
	    line= line.replace("\"","");
	    line=line.replace("\'","");
	    
	    line=line.replace(" ", "");
	
	    
	    String item[] = line.split(",");
	   	for(int j=0;j<item.length;j++){
	   		String val=item[j];
	   	    if(val.equals("?")){
	   	    	item[j]="1";
	   	    }
	   	 
	   	}
	    for(int i=0;i<item.length;i++){
	      if(i<item.length-1){
	     	    newline+=item[i]+",";
	        	}else{
	        	newline+=item[i];	
	        	}
	    }
	   
		
	    
	     
	     
	    System.out.println("����д���С�������������");
	    System.out.println(newline);
	    if(k<=10000){
	     bw.write(newline);
	     bw.newLine();
	    } 
     
	}
	bw.flush();
	bw.close();
	System.out.println("������");
  }
   
   
   
   
   
   
   
 }



















