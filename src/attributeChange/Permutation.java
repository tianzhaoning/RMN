package attributeChange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class Permutation {
	     int total=0;
	     
	     int filename=1;//���ڴ����ļ���
         HashMap<String,String[]>dataset=new HashMap();
         String[] attributes;
         int length=0;//���Ը���
         int size=0;//��������
        
         
         
         //���ָ�����е��ļ�
         public void createFile(String[]args) throws IOException{
        	 //�����ļ�,����Ϊn��,ָ��һ���ļ���
    		 String path = "C:\\test\\" + filename + "_result.arff" ;
    		 File StorePath = new File(path);
    		 BufferedWriter bw = new BufferedWriter(new FileWriter(StorePath,false));
    		 //д���ļ���
    		 String firstline="@relation "+filename+"_result";
    		 bw.write(firstline);
             bw.newLine();
             //д������
             for (int i=0; i<args.length; i++) {
             	String writeline=args[i];
             	bw.write(writeline);
             	bw.newLine(); 
             }
             String data="@data";
             bw.write(data);
             bw.newLine();
             //д������
             
             for(int j=0;j<this.size;j++){
            	 for(int m=0;m<args.length;m++){
            		 String attributename=args[m];
            		 String nowdataset[]=dataset.get(attributename);
            		 String name=nowdataset[j];
            		 if(m==args.length-1){
            			 bw.write(name);
            			 bw.newLine();
            		 }else{
            		bw.write(name+",");
            		 }
            		 
            		 
            	 }
            	 
             }
            // ��ջ��������ر�������
             bw.flush();
             bw.close();
             this.filename++;
        	 
        	 
         }
         
         
         
         
         //���������ȫ����
         
         public void permutation(String[]args,int begin) throws IOException{
        	 if(begin==args.length-2){
        		 for(int i=0;i<args.length;i++){
        			 //System.out.print(args[i]+",");
        		 }
        		 System.out.println("");
        		 System.out.println("������");
        		 this.createFile(args);
                
        		 
        	 }else{
        		 for(int i=begin;i<args.length-1;i++){
        			 String using=args[begin];
        			 args[begin]=args[i];
        			 args[i]=using;
        			 permutation(args,begin+1);
        			 using=args[begin];
        			 args[begin]=args[i];
        			 args[i]=using;
        		 }		 
        	 }
         }
         
         
         
         
         
         
         
         //�������Բ��������������Լ�����������
         public void setLengthandSize(String file) throws IOException{
        	 
        	 //�����ļ�
        	 File inputFile=new File(file);
   		     ArffLoader loader=new ArffLoader();
   	         loader.setSource(inputFile);
   	         //��ȡ���ݼ�
   	         Instances dataset=loader.getDataSet();
   	         //��ȡ��������
   	          int size=dataset.numInstances();
   	          
   	         //��ȡ�������� 
   	          int length=dataset.numAttributes();
   	          
   	          
        	 //��������
        	 this.length=length;
        	 //��������
        	 this.size=size;
        	 
        	 this.total=this.recurrence(this.length-1);
        	 System.out.println(this.total);
        	
         }
         
         
         
         
         
         
         
        //�����ļ����������Լ����ݼ�
         public void init(String FilePath) throws IOException{
        	 FileReader file=new FileReader(FilePath);
        	 
        	//��ȡ�ļ�
             BufferedReader reader = new BufferedReader(file);
            //������һ��
             
            reader.readLine();
            
          //��ʼ��attributes
            this.attributes=new String[this.length];
            String line=null;
            
             for(int i=0;i<this.length;i++){
            	  
            	 line = reader.readLine(); 
            	 
            	 this.attributes[i]=line;	 
        
            		 
            	 this.dataset.put(line, new String[this.size]);
            	 
             }
             //����@data��һ��
             
             line =reader.readLine();
             
             //��ʼ��dataset
             for(int k=0;k<this.size;k++){
            	 line =reader.readLine();
            	 
            	 //��Ҫ���ݾ����ļ������޸�
            	String item[] = line.split(",");
            	for(int j=0;j<item.length;j++){
            		String attr=this.attributes[j];
            		String[]content=dataset.get(attr);
            	    content[k]=item[j];
            	}
             }
          }
       
         
        //ȷ������������������
        public int confirmrow(String filename) throws IOException{
        	
        	 FileReader file=new FileReader(filename);
        	 
         	  //��ȡ�ļ�
             BufferedReader reader = new BufferedReader(file);
             reader.readLine();
             String target=this.attributes[this.length-1];
             String now=reader.readLine();
             int set=0;
        	 while(!now.equals(target)){
        		 now=reader.readLine();
        		 set++;
        	}
             
        	return set;
        	
        }
        
         
        public  int recurrence(int num){
    		if(num<=1)
    			return 1;
    		else
    			return num*recurrence(num-1);
    	}
        	
        	
        	
        	
        	
        	
        	
       
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
     
         
         
       public static void main(String[] args) throws IOException {
		
    	String path="C:\\test\\artificial.arff";
    	Permutation pt=new Permutation();
    	pt.setLengthandSize(path);
    	pt.init(path);
    	//Iterator it=pt.dataset.entrySet().iterator();
    	//while(it.hasNext()){
    	//	Entry<String,String[]> entry=(Entry<String, String[]>) it.next();
    	//	String key=entry.getKey();
    	//	String[]values=entry.getValue();
    	//	System.out.println(key);
    	//	for(int i=0;i<values.length;i++){
    	//		System.out.print(values[i]+" ");
    			
    			
    	//	}
    	//	System.out.println();
    	//}
    	pt.permutation(pt.attributes, 0);
    	int number=pt.confirmrow("C:\\test\\2_result.arff");
    	System.out.println(number);
    	
    	
    	
    	
	}
         
         
         
}    
         
         
         
         
         
         
         
         
         
         



