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
		//System.out.println("我被执行了");
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
				        	    // System.out.println("我是numeric,值为问号");
				    	           double a=0;
				    	           it.setValue(attribute,a);
				    	           
				           }else{
				        	  // System.out.println("我是numeric,值正常");
			   	                   double a =Double.parseDouble(value);
			   	                 it.setValue(attribute,a);
				           }
			    
		    		     
		           }else{
		        	               try {
		        	            	       
		        		                    it.setValue(attribute,value);
		        		                   
					               } catch (Exception e) {
					            	   System.out.println("出错");
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
		 //设定布尔值
		boolean tag=false;
		//实例化BestInstant对象
		BestInstant BI=new BestInstant();
		//从磁盘读入文件
		File inputFile=BI.readFile();
		//判断文件合法性
		while(!inputFile.exists()){
		   inputFile=BI.readFile();
		}
		
	
		//C:\\等待校验\\Surveillance.csv
		//E:\\等待校验\\weather.arff
		String name=inputFile.getName().split("\\.")[1];
		
		Instances instancesTrain=null;
		switch(name){
		   case "arff":
			    //创建数据加载器
	    		ArffLoader atf = new ArffLoader();
	    		//加载数组
	    		atf.setFile(inputFile);
	            //获取数据集
	            instancesTrain = atf.getDataSet();  
			   break;
		   case "csv" :
			     //创建数据加载器
			     CSVLoader loader = new CSVLoader();
			     //加载文件
			     loader.setSource(inputFile);
			     //获取数据集
			     instancesTrain = loader.getDataSet();
			    // System.out.println(instancesTrain);
			     break;
		 
		}
		// 将对象写到流里
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(instancesTrain);
        // 从流里读出来
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        Instances it=(Instances) oi.readObject();
        //初始化BI
		System.out.println("初始训练集为：");
		System.out.println(instancesTrain);
        BI.findBestInstant(instancesTrain);
        //进行全排列，创建数据集,训练
	    BI.permutation(BI.AttributeString, BI.fv, BI.attributes,0);
	    //在内存中获取最好的数据集
		Instances best=BI.returnBestInstances();
		//Test test=new Test();
		//test.permutationInstances(instancesTrain, best);
		//System.out.println(test.permutationInstances(instancesTrain, best));
		System.out.println("最终训练集为:");
		System.out.println(instancesTrain);
		System.out.println("我定义的深拷贝数据集");
		System.out.println(it);
		
		
		
	}
	
	
	
	
	
	
	
}
