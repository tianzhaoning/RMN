package attributeChange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Scanner;



import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

public class BestInstant {
	//
	               int i=0;
	               //��ֵΪ���ԣ���Ӧ���ַ�������Ϊ��Ӧ���Ե�ȫ����ֵ
	
                   public HashMap<String, String[]>store=new HashMap<String,String[]>();
	                
	                //����������Ե�����
	                public String[]AttributeString;
	                
	                
	                //list��������
	                public ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	                
	                //��������
	                int length;
	                
	                //��������
	                int width;
	                
	                
	                //����
	                public FastVector fv=new FastVector();
	                
	                //
	                public HashMap<Instances,Double>conflunce=new HashMap<Instances,Double>();
	                
	                //
	                public Instances bestInstances;
	                //
	                public double bestAccurate=0;
	                
	                //
	                public ArrayList<DataSetStore> dst=new ArrayList<DataSetStore>();
	                
	                
	                
	                
	          public Instances findBestInstant(Instances instances){
	               // System.out.println("��Ҫȫ���������Ϊ");
	               // System.out.println(instances);
	                	
	                //��ȡ���ݼ�
	    	        Instances its = instances;
	    	      
	    	        //�����������
	    	        int numAttribute=its.numAttributes();
	    	        this.length=numAttribute;
	    	   
	    	       //��ȡ����������
	    	        int numInstances=its.numInstances();
	    	        this.width=numInstances;
	    	 
	    	        
	    	        //���������ַ�������ռ�
	    	        this.AttributeString=new String[numAttribute];
	    	        
	    	    
	    	        
	    	        //��ȡ��һ������
	    	        Instance it=its.instance(0);
	    	    
	    	        //�����������鸳ֵ
	    	        for(int i=0;i<numAttribute;i++){
	    	        	
	    	            //��ȡ����
	    	           Attribute attr=it.attribute(i);
	    	           
	    	          //�������鸳ֵ
	    	           this.attributes.add(attr);
	    	           this.fv.addElement(attr);
	    	           
	    	            //������תΪ�ַ�������
	    	           String stringattr=attr.toString();
	    	           
	    	           //�ַ������鸳ֵ 
	    	           AttributeString[i]=stringattr;
	    	           
	    	           
	    	           
	    	           
	    	           //����map�еĿյ��ַ�������
	    	           String mapString[]=new String[numInstances];
	    	           
	    	           //���������Ƽ���Ӧȡֵ����store��
	    	           store.put(stringattr, mapString);
	    	           
	    	        }
	    	  
     
                    for(int i=0;i<numInstances;i++){
                    	//��ȡ���ݣ����в���
	    	           it=its.instance(i);


	    	           //���в���
	    	           for(int j=0;j<numAttribute;j++){
	    	        	
	    	        	   
	    	        	   //��ȡ��������
	    	        	   String attributeName=this.AttributeString[j];
	    	        	
	    	        	   //�����������ƻ������
	    	        	   String storeString[]=store.get(attributeName);
	    	        	    
	    	        	   //��ȡ��i�У���j�����Զ�Ӧ��ֵ
	    	        	   String value=it.toString(it.attribute(j));
	    	        	  
	    	        
	    	        	   //���Դ����i��
	    	        	   storeString[i]=value;
	    	        	   
	    	           }
	    	        }
                 return its;
	     }
	                
	                
	   public void test(){
		   Iterator it=this.store.entrySet().iterator();
		   while(it.hasNext()){
			   
		  Entry entry=(Entry) it.next();
		  String key=(String) entry.getKey();
		  System.out.println(key);
		  String values[]=(String[]) entry.getValue();
			  for(int i=0;i<values.length;i++){
				  
				  System.out.println(values[i]);
			  }
			   
			   
			   
			   
			   
			   
		   }
	   }          
	 public  Instances creatInstances(String[] attributeString,FastVector fv,ArrayList<Attribute> at){
		// System.out.println("��Ҫ��������������Ϊ");
		// System.out.println(at);
		 Instances instances = new Instances("ssss"+this.i,fv,this.width);
		 
		 for(int j=0;j<this.width;j++){
			 
		 
		   Instance instance = new DenseInstance(this.length);
		   
		    		
		    		
		    		
		    		
		    		
		     for(int i=0;i<this.length;i++){
			 
		    	// System.out.println("��"+j+"�� ��"+i+"��");
		    	   String value=this.store.get(attributeString[i])[j];
		           if(at.get(i).type()==Attribute.NUMERIC){
				          if(value.equals("?")){
				        	    // System.out.println("����numeric,ֵΪ�ʺ�");
				    	           double a=0;
				    	           instance.setValue(at.get(i),a);
				           }else{
				        	  // System.out.println("����numeric,ֵ����");
			   	                   double a =Double.parseDouble(value);
			   	                  instance.setValue(at.get(i),a);
				           }
			    
		    		     
		           }else{
		        	               try {
		        	            	        
		        	            	      
		        		                     instance.setValue(at.get(i),value);
					               } catch (Exception e) {
					            	   System.out.println("��"+j+"�д���");
					            	   System.out.println("������");
					            	   System.out.print("��������:");		        	            	 
	        	            	         System.out.println(at.get(i).toString());
	        	            	         System.out.print("�����±�:");		        	            	 
                                           System.out.println(at.get(i).index());
                                           System.out.print("����ֵ��");
                                           System.out.println(value);
						                   e.printStackTrace();
						                   
					               }
	                 
		  	       }
		           
	      } 
		     
		     
		     
		    
		 instances.add(instance);
		 }
		// this.i=this.i+1; 
		 //System.out.println(instances);
		 return instances;
			
	 }             
	    //            
	 public void permutation(String[] attributeString,FastVector fv,ArrayList<Attribute> at,int begin) throws Exception{
    	 
    
		 if(begin==this.length-2){
			// System.out.println("��ʼ");
    		// for(int j=0;j<this.length;j++){
    			// System.out.println(j);
    			// System.out.print(attributeString[j]+",");
    		   //  System.out.println();
    		// }
    		// for(int j=0;j<this.length;j++){
    		//	 System.out.println(j);
    		//	 System.out.print(fv.elementAt(j)+",");
    		//	 System.out.println();
    		// }
    		// for(int j=0;j<this.length;j++){
    		//	System.out.println(j);
    		///	 System.out.print(at.get(j)+",");
    		//	 System.out.println();
    		// }
			// System.out.println("����ʱ�������");
			// System.out.println(at);
			Instances instancesTrain=this.creatInstances(attributeString, fv, at);
			////System.out.println("ѵ��ʱ�������");
			//System.out.println(at);
			this.trainFile(instancesTrain,attributeString,fv,at);	
			 DataSetStore dstt=new DataSetStore();
	        
    		 
    	 }else{
    		 for(int i=begin;i<this.length-1;i++){
    			 
    			 //ȫ�����ַ�������
    			 String using=attributeString[begin];
    			 attributeString[begin]=attributeString[i];
    			 attributeString[i]=using;
    			 
    			 
    			 //ȫ����FastVector
    			Object id=fv.elementAt(begin);
    		    fv.setElementAt(fv.elementAt(i), begin);
    		    fv.setElementAt(id, i);
    			 
    			 
    			 //ȫ����ArrayList
    			 Attribute attr=at.get(begin);
    			 at.set(begin, at.get(i));
    			 at.set(i, attr);
    			 
    			 
    			 
    			 
    			 permutation(attributeString,fv,at,begin+1);
    			 
    			 //���� 
    			 using=attributeString[begin];
    			 attributeString[begin]=attributeString[i];
    			 attributeString[i]=using;
    			 
    			 
    			//����
    			 id=fv.elementAt(begin);
    			 fv.setElementAt(fv.elementAt(i), begin);
    			 fv.setElementAt(id, i);
    			 
    			//����
    		     attr=at.get(begin);
    		     at.set(begin, at.get(i));
    			 at.set(i, attr);
    			 
    		 }		 
    	 }
     }               
	                
    
	 
	 
	 
	 
	 
	 public void trainFile(Instances instancesTrain,String[] attributeString1,FastVector fv1,ArrayList<Attribute> at1) throws Exception{
		
		 
		 //���÷������������кţ���һ��Ϊ0
         instancesTrain.setClassIndex(this.length-1);
         
         //����������
         Classifier classifier1;
         
         // mn�㷨
         classifier1 = (Classifier) Class.forName("weka.classifiers.MN.M_N").newInstance();
         
         //�������ݼ�
         classifier1.buildClassifier(instancesTrain);
         
         //������������
         Evaluation eval = new Evaluation(instancesTrain);
         
         //����  
         System.out.println("���ڲ�����");
        // System.out.println("���Ե�����Ϊ");
        // System.out.println(instancesTrain);
         eval.crossValidateModel(classifier1, instancesTrain,10,new Random(1));
       
         //���
         double correctrate=1-eval.errorRate();
         DataSetStore dstt=new DataSetStore();
         dstt.accurate=correctrate;
        
         
         
         
         dstt.at=new ArrayList<Attribute>();
         for(int time1=0;time1<at1.size();time1++){
        	 dstt.at.add(at1.get(time1));
        	 
         }
         
         
         dstt.attributeString=new String[attributeString1.length];
         for(int time2=0;time2<attributeString1.length;time2++){
        	 dstt.attributeString[time2]=attributeString1[time2];
        	 
         }
         dstt.fv=new FastVector();
         for(int time3=0;time3<fv1.size();time3++){
        	 dstt.fv.addElement(fv1.elementAt(time3));
        	 
        }
        // System.out.println("���յ�������");
        // System.out.println(at1);
        
       
         this.dst.add(dstt);
         
        // System.out.println("�洢������");
        // System.out.println(this.dst.get(this.dst.size()-1).at);
    
		 
		 
	 }
	                
	public Instances returnBestInstances(){
		   
		  Instances bestInstances = null;
		  double bestAccurate=0;
		  int number = 0;
		  //System.out.println(this.dst.size());
	      for(int i=0;i<this.dst.size();i++){
	    	 // System.out.println("111111111111");
	    	//  System.out.println(this.dst.get(i).accurate);
	        //  System.out.println(this.dst.get(i).at);
	         
	    	  if(this.dst.get(i).accurate>bestAccurate){
	    		   bestAccurate=this.dst.get(i).accurate;
	    		  number=i;
	    	 }
	    	  
	      }
	      
	      
	      System.out.println("��õ��������Ϊ"+number);
          System.out.println("����Ϊ");
	      System.out.println(bestAccurate);
	     // System.out.println(this.dst.get(number).attributeString);
	     // System.out.println( this.dst.get(number).fv);
	     // System.out.println(this.dst.get(number).at);
	      bestInstances=this.creatInstances(this.dst.get(number).attributeString, this.dst.get(number).fv,this.dst.get(number).at);  
		 // System.out.println(bestInstances);
		
	    this.setBestInstances(bestInstances);
		return bestInstances;
		
	}                
	                
	              
	                
	                
	public Instances getBestInstances() {
		return bestInstances;
	}


	public void setBestInstances(Instances bestInstances) {
		this.bestInstances = bestInstances;
	}


	public File readFile(){
		File file = null;
		Scanner scan=new Scanner(System.in);
		System.out.println("�������ļ�����");
		String filename=scan.nextLine();
		file=new File(filename);
	    return file;
		
		
		
	}
	
	public Instance permutationInstance(Instance instance){
		System.out.println("�ұ�ִ����");
		System.out.println(instance);
		HashMap<String,String>datasource=new HashMap<String, String>();
		for(int j=0;j<instance.numAttributes();j++){
			String name=instance.attribute(j).toString();
			System.out.println(name);
			String value=instance.toString(j);
			System.out.println(value);
			datasource.put(name, value);
		}
		
		
		Instance orderInstance=this.getBestInstances().instance(0);
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
		        	            	       System.out.println("�ұ�ִ����");
		        		                    it.setValue(attribute,value);
		        		                   
					               } catch (Exception e) {
					            	   System.out.println("����");
					            	      // this.test();
						                  
					               }
	                 
		  	       }
		           
	      }
		
		
		
	   System.out.println(it);	
		
		return it;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
		
		boolean tag=false;
		
		BestInstant BI=new BestInstant();
		
		File inputFile=BI.readFile();
		while(!inputFile.exists()){
		   inputFile=BI.readFile();
		}
		
		
		
		
		
		//File inputFile = new File("C:\\�ȴ�У��\\diabetes.arff");
		//C:\\�ȴ�У��\\data_banknote_authentication.arff
		//C:\\�ȴ�У��\\abalone.arff
		//C:\\�ȴ�У��\\balloons.arff
		//C:\\�ȴ�У��\\balance-scale.arff
		//C:\\�ȴ�У��\\cmc.arff
		//C:\\�ȴ�У��\\weather.arff	
        //����arff����
        ArffLoader atf = new ArffLoader();
        
         atf.setFile(inputFile);
        
        //��ȡ���ݼ�
        Instances instancesTrain = atf.getDataSet();
        Instance instance=instancesTrain.instance(0);
       
        
		BI.findBestInstant(instancesTrain);
		//BI.creatInstances(BI.AttributeString, BI.fv, BI.attributes);
		BI.permutation(BI.AttributeString, BI.fv, BI.attributes,0);
		Instances best=BI.returnBestInstances();
		//System.out.println("ȫ���������");
		//System.out.println("����ȫ���е�һ�е�����");
		System.out.println(BI.permutationInstance(instance));
		
		
		
	   
		
		//for(int i=0;i<BI.dst.size();i++){
		//	System.out.println(BI.dst.get(i).at);
		//}
		//System.out.println(best);
		//ArffSaver saver = new ArffSaver();
	    //saver.setInstances(best);
		//saver.setFile(new File("C:\\sss.arff"));
		//saver.setDestination(new File("C:\\�ȴ�У��\\sss.arff"));
		//try {
		//	saver.writeBatch();
		//} catch (Exception e) {
		//	System.out.println("����ʧ��");
		//}
		
		
	
	
		
		
		
	}

	
	
	
}
