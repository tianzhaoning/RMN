package attributeChange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

public class Test {
	public Instances permutationInstances(Instances instances,Instances thebest){
		//System.out.println("�ұ�ִ����");
		//System.out.println(instance);
		BestInstant BI=new BestInstant();
		BI.findBestInstant(thebest);
		Instances changeInstances=new Instances("test",BI.fv,instances.numInstances());
		for(int k=0;k<instances.numInstances();k++){
			Instance instance=instances.instance(k);
			
		HashMap<String,String>datasource=new HashMap<String, String>();
		for(int j=0;j<instance.numAttributes();j++){
			String name=instance.attribute(j).toString();
			System.out.println(name);
			String value=instance.toString(j);
			System.out.println(value);
			datasource.put(name, value);
		}
		
		
		Instance orderInstance=thebest.instance(0);
		Instance it=new DenseInstance(instance.numAttributes());
		  for(int i=0;i<orderInstance.numAttributes();i++){
			       
			       
			       
				    Attribute attribute=orderInstance.attribute(i);
				    System.out.println(attribute);
				    String name=attribute.toString();
				    String value=datasource.get(name);
				    System.out.println(value);
		    	
		            if(attribute.type()==Attribute.NUMERIC){
				          if(value.equals("?")){
				        	    // System.out.println("����numeric,ֵΪ�ʺ�");
				    	           double a=0;
				    	           it.setValue(attribute,a);
				    	           
				           }else{
				        	  // System.out.println("����numeric,ֵ����");
			   	                   double a =Double.parseDouble(value);
			   	                 it.setValue(attribute,a);
				           }
			    
		    		     
		           }else{
		        	               try {
		        	            	       
		        		                    it.setValue(attribute,value);
		        		                   
					               } catch (Exception e) {
					            	   System.out.println("����");
					            	      // this.test();
						                  
					               }
	                 
		  	       }
		           
	      }
		// Instances bit=this.thebest;
	     //bit.add(it);
	     //it=bit.instance(this.thebest.numInstances()-1);
	       System.out.println(it);
	       changeInstances.add(it);
		}
		return changeInstances;
	}
	
	
	public static void main(String[] args) throws Exception {
		 //�趨����ֵ
		boolean tag=false;
		//ʵ����BestInstant����
		BestInstant BI=new BestInstant();
		//�Ӵ��̶����ļ�
		File inputFile=BI.readFile();
		//�ж��ļ��Ϸ���
		while(!inputFile.exists()){
		   inputFile=BI.readFile();
		}
		
	
		//C:\\�ȴ�У��\\Surveillance.csv
		//E:\\�ȴ�У��\\weather.arff
		String name=inputFile.getName().split("\\.")[1];
		
		Instances instancesTrain=null;
		switch(name){
		   case "arff":
			    //�������ݼ�����
	    		ArffLoader atf = new ArffLoader();
	    		//��������
	    		atf.setFile(inputFile);
	            //��ȡ���ݼ�
	            instancesTrain = atf.getDataSet();  
			   break;
		   case "csv" :
			     //�������ݼ�����
			     CSVLoader loader = new CSVLoader();
			     //�����ļ�
			     loader.setSource(inputFile);
			     //��ȡ���ݼ�
			     instancesTrain = loader.getDataSet();
			    // System.out.println(instancesTrain);
			     break;
		 
		}
		// ������д������
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(instancesTrain);
        // �����������
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        Instances it=(Instances) oi.readObject();
        //��ʼ��BI
		System.out.println("��ʼѵ����Ϊ��");
		System.out.println(instancesTrain);
        BI.findBestInstant(instancesTrain);
        //����ȫ���У��������ݼ�,ѵ��
	    BI.permutation(BI.AttributeString, BI.fv, BI.attributes,0);
	    //���ڴ��л�ȡ��õ����ݼ�
		Instances best=BI.returnBestInstances();
		//Test test=new Test();
		//test.permutationInstances(instancesTrain, best);
		//System.out.println(test.permutationInstances(instancesTrain, best));
		System.out.println("����ѵ����Ϊ:");
		System.out.println(instancesTrain);
		System.out.println("�Ҷ����������ݼ�");
		System.out.println(it);
		
		
		
	}
	
	
	
	
	
	
	
}
