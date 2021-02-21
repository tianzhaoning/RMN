package attributeChange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;

public class AttributeChange {
    public  ArrayList<String> attr;//存放属性
    public  ArrayList<HashMap<String,Object>>data;//存放数据
    public void  initAttr(){
    	String n1="temperature";
    	String n2="outlook";
    	String n3="humidity";
    	String n4="windy";
    	String n5="play";
    	int i;
    	int j;
    	
    }
    public  void changeAttr(int i,int j){
          this.attr=new ArrayList();
          String altitude1=this.attr.get(j);
		  String altitude2=this.attr.get(i);
		  attr.set(j,altitude2);
		  attr.set(i,altitude1);
        
    }
	public  void getdata(String file) throws IOException{  //file为源地址
		  
		  //从磁盘读入文件
		  File inputFile=new File(file);
		  ArffLoader loader=new ArffLoader();
	      loader.setSource(inputFile);
	      
	      
	      //获取数据集
	      Instances dataset=loader.getDataSet();
	      Instance inst;
	      int n=dataset.numInstances();
	      inst=loader.getNextInstance(dataset);
	      inst.numAttributes();
	      //遍历并取出并存入
	      while((inst=loader.getNextInstance(dataset))!=null){
	    	  HashMap<String,Object>map=new HashMap();//HashMap存放属性名称及数值
	    	  for(int m=0;m<attr.size();m++){
	    		  String name=attr.get(m);//属性的字符串类型
	    		  Attribute attrname=dataset.attribute(name);//通过字符串类型变成Attrbute类型
	    		  Object obj=inst.value(attrname);//通过Attribute类型获取值obj
	    		  map.put(name,obj);//存入指定的数据结构
	    	  }
	    	  this.data.add(map);
	      }
			
	}
	
	public void createarff(String sourcefile,String targerfile) throws IOException{ //file为将要存放的地址
		  
		//从磁盘读入文件
		  File inputFile=new File(sourcefile);
		  ArffLoader loader=new ArffLoader();
	      loader.setSource(inputFile);
	    //获取数据集
	      Instances dataset=loader.getDataSet();
	      Instance inst;
	      //创建属性集
	      FastVector  attributes =new FastVector();
	      //将源文件的属性放入属性集
	      for(int m=0;m<attr.size();m++){
    		  String name=attr.get(m);//属性的字符串类型
    		  Attribute attrname=dataset.attribute(name);//通过字符串类型变成Attrbute类型
    		  attributes.addElement(attrname);
    	  }
		  //创建新的数据集
	       Instances instances=new Instances("Test",attributes ,0);
	      for(int i=0;i<data.size();i++){
	    	  HashMap<String,Object> map=data.get(i);
	    	  double[] obj=new double[dataset.numAttributes()];
	    	  for(int j=0;j<dataset.numAttributes();j++){
	    		  String key=attr.get(j);
	    		  obj[j]=(double) map.get(key);
	    		  
	    	   }
	    	  instances.add(new DenseInstance(1.0,obj));    	  
	    	  
	      }
	      //生成arff
		
	}
	
	public static void main(String[] args) {
		
		AttributeChange at=new AttributeChange();
	    
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
