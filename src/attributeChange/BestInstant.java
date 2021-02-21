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
	               //键值为属性，对应的字符串数组为相应属性的全部列值
	
                   public HashMap<String, String[]>store=new HashMap<String,String[]>();
	                
	                //存放所有属性的名称
	                public String[]AttributeString;
	                
	                
	                //list属性数组
	                public ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	                
	                //属性总数
	                int length;
	                
	                //数据条数
	                int width;
	                
	                
	                //向量
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
	               // System.out.println("将要全排序的样本为");
	               // System.out.println(instances);
	                	
	                //获取数据集
	    	        Instances its = instances;
	    	      
	    	        //获得属性总数
	    	        int numAttribute=its.numAttributes();
	    	        this.length=numAttribute;
	    	   
	    	       //获取总属性条数
	    	        int numInstances=its.numInstances();
	    	        this.width=numInstances;
	    	 
	    	        
	    	        //申请属性字符串数组空间
	    	        this.AttributeString=new String[numAttribute];
	    	        
	    	    
	    	        
	    	        //获取第一行数据
	    	        Instance it=its.instance(0);
	    	    
	    	        //对于属性数组赋值
	    	        for(int i=0;i<numAttribute;i++){
	    	        	
	    	            //获取属性
	    	           Attribute attr=it.attribute(i);
	    	           
	    	          //属性数组赋值
	    	           this.attributes.add(attr);
	    	           this.fv.addElement(attr);
	    	           
	    	            //将属性转为字符串类型
	    	           String stringattr=attr.toString();
	    	           
	    	           //字符串数组赋值 
	    	           AttributeString[i]=stringattr;
	    	           
	    	           
	    	           
	    	           
	    	           //创建map中的空的字符串数组
	    	           String mapString[]=new String[numInstances];
	    	           
	    	           //将属性名称及对应取值放入store中
	    	           store.put(stringattr, mapString);
	    	           
	    	        }
	    	  
     
                    for(int i=0;i<numInstances;i++){
                    	//获取数据，逐行操作
	    	           it=its.instance(i);


	    	           //逐列操作
	    	           for(int j=0;j<numAttribute;j++){
	    	        	
	    	        	   
	    	        	   //获取属性名称
	    	        	   String attributeName=this.AttributeString[j];
	    	        	
	    	        	   //根据属性名称获得数组
	    	        	   String storeString[]=store.get(attributeName);
	    	        	    
	    	        	   //获取第i行，第j列属性对应的值
	    	        	   String value=it.toString(it.attribute(j));
	    	        	  
	    	        
	    	        	   //属性存入第i行
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
		// System.out.println("将要创建的属性排列为");
		// System.out.println(at);
		 Instances instances = new Instances("ssss"+this.i,fv,this.width);
		 
		 for(int j=0;j<this.width;j++){
			 
		 
		   Instance instance = new DenseInstance(this.length);
		   
		    		
		    		
		    		
		    		
		    		
		     for(int i=0;i<this.length;i++){
			 
		    	// System.out.println("第"+j+"行 第"+i+"列");
		    	   String value=this.store.get(attributeString[i])[j];
		           if(at.get(i).type()==Attribute.NUMERIC){
				          if(value.equals("?")){
				        	    // System.out.println("我是numeric,值为问号");
				    	           double a=0;
				    	           instance.setValue(at.get(i),a);
				           }else{
				        	  // System.out.println("我是numeric,值正常");
			   	                   double a =Double.parseDouble(value);
			   	                  instance.setValue(at.get(i),a);
				           }
			    
		    		     
		           }else{
		        	               try {
		        	            	        
		        	            	      
		        		                     instance.setValue(at.get(i),value);
					               } catch (Exception e) {
					            	   System.out.println("第"+j+"行错误");
					            	   System.out.println("出错了");
					            	   System.out.print("属性名称:");		        	            	 
	        	            	         System.out.println(at.get(i).toString());
	        	            	         System.out.print("属性下标:");		        	            	 
                                           System.out.println(at.get(i).index());
                                           System.out.print("属性值：");
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
			// System.out.println("开始");
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
			// System.out.println("创建时候的排列");
			// System.out.println(at);
			Instances instancesTrain=this.creatInstances(attributeString, fv, at);
			////System.out.println("训练时候的排列");
			//System.out.println(at);
			this.trainFile(instancesTrain,attributeString,fv,at);	
			 DataSetStore dstt=new DataSetStore();
	        
    		 
    	 }else{
    		 for(int i=begin;i<this.length-1;i++){
    			 
    			 //全排列字符串数组
    			 String using=attributeString[begin];
    			 attributeString[begin]=attributeString[i];
    			 attributeString[i]=using;
    			 
    			 
    			 //全排列FastVector
    			Object id=fv.elementAt(begin);
    		    fv.setElementAt(fv.elementAt(i), begin);
    		    fv.setElementAt(id, i);
    			 
    			 
    			 //全排列ArrayList
    			 Attribute attr=at.get(begin);
    			 at.set(begin, at.get(i));
    			 at.set(i, attr);
    			 
    			 
    			 
    			 
    			 permutation(attributeString,fv,at,begin+1);
    			 
    			 //换回 
    			 using=attributeString[begin];
    			 attributeString[begin]=attributeString[i];
    			 attributeString[i]=using;
    			 
    			 
    			//换回
    			 id=fv.elementAt(begin);
    			 fv.setElementAt(fv.elementAt(i), begin);
    			 fv.setElementAt(id, i);
    			 
    			//换回
    		     attr=at.get(begin);
    		     at.set(begin, at.get(i));
    			 at.set(i, attr);
    			 
    		 }		 
    	 }
     }               
	                
    
	 
	 
	 
	 
	 
	 public void trainFile(Instances instancesTrain,String[] attributeString1,FastVector fv1,ArrayList<Attribute> at1) throws Exception{
		
		 
		 //设置分类属性所在行号，第一行为0
         instancesTrain.setClassIndex(this.length-1);
         
         //建立分类器
         Classifier classifier1;
         
         // mn算法
         classifier1 = (Classifier) Class.forName("weka.classifiers.MN.M_N").newInstance();
         
         //读入数据集
         classifier1.buildClassifier(instancesTrain);
         
         //创建评估对象
         Evaluation eval = new Evaluation(instancesTrain);
         
         //测试  
         System.out.println("正在测试中");
        // System.out.println("测试的样例为");
        // System.out.println(instancesTrain);
         eval.crossValidateModel(classifier1, instancesTrain,10,new Random(1));
       
         //结果
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
        // System.out.println("接收到的排列");
        // System.out.println(at1);
        
       
         this.dst.add(dstt);
         
        // System.out.println("存储的排列");
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
	      
	      
	      System.out.println("最好的样本序号为"+number);
          System.out.println("精度为");
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
		System.out.println("请输入文件名：");
		String filename=scan.nextLine();
		file=new File(filename);
	    return file;
		
		
		
	}
	
	public Instance permutationInstance(Instance instance){
		System.out.println("我被执行了");
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
		        	            	       System.out.println("我被执行了");
		        		                    it.setValue(attribute,value);
		        		                   
					               } catch (Exception e) {
					            	   System.out.println("出错");
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
		
		
		
		
		
		//File inputFile = new File("C:\\等待校验\\diabetes.arff");
		//C:\\等待校验\\data_banknote_authentication.arff
		//C:\\等待校验\\abalone.arff
		//C:\\等待校验\\balloons.arff
		//C:\\等待校验\\balance-scale.arff
		//C:\\等待校验\\cmc.arff
		//C:\\等待校验\\weather.arff	
        //创建arff加载
        ArffLoader atf = new ArffLoader();
        
         atf.setFile(inputFile);
        
        //获取数据集
        Instances instancesTrain = atf.getDataSet();
        Instance instance=instancesTrain.instance(0);
       
        
		BI.findBestInstant(instancesTrain);
		//BI.creatInstances(BI.AttributeString, BI.fv, BI.attributes);
		BI.permutation(BI.AttributeString, BI.fv, BI.attributes,0);
		Instances best=BI.returnBestInstances();
		//System.out.println("全部生成完毕");
		//System.out.println("我是全排列第一行的数据");
		System.out.println(BI.permutationInstance(instance));
		
		
		
	   
		
		//for(int i=0;i<BI.dst.size();i++){
		//	System.out.println(BI.dst.get(i).at);
		//}
		//System.out.println(best);
		//ArffSaver saver = new ArffSaver();
	    //saver.setInstances(best);
		//saver.setFile(new File("C:\\sss.arff"));
		//saver.setDestination(new File("C:\\等待校验\\sss.arff"));
		//try {
		//	saver.writeBatch();
		//} catch (Exception e) {
		//	System.out.println("生成失败");
		//}
		
		
	
	
		
		
		
	}

	
	
	
}
