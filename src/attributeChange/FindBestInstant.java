package attributeChange;

import java.io.File;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class FindBestInstant {
	    public double correctrate[];
	    public int testlength;
	    //初始化
	    public void init(int length){
	    	this.testlength=length;
	    	this.correctrate=new double[this.testlength];
	    	for(int i=0;i<this.testlength;i++){
	    		correctrate[i]=0;
	    	}
	    }
	    public void testInstant(String file,int set,int filenumber) throws Exception{
	    	
	    	 //从磁盘读入训练文件
            File inputFile = new File(file);
            
            //创建arff加载
            ArffLoader atf = new ArffLoader();
            
            //arff读入
            atf.setFile(inputFile);
            
            //获取数据集
            Instances instancesTrain = atf.getDataSet();
            
            //设置分类属性所在行号，第一行为0
            instancesTrain.setClassIndex(set);
            
            //建立分类器
            Classifier classifier1;
            
            // mn算法
            classifier1 = (Classifier) Class.forName("weka.classifiers.MN.M_N").newInstance();
            
            //读入数据集
            classifier1.buildClassifier(instancesTrain);
            
            //创建评估对象
            Evaluation eval = new Evaluation(instancesTrain);
            
            //测试
            eval.crossValidateModel(classifier1, instancesTrain,10,new Random(1));
            
            //结果
            double correctrate=1-eval.errorRate();
	     	this.correctrate[filenumber]=correctrate;
	    }
	    
	    
	    //找到最好的,文件号从1开始,数组从0开始
	    public int findBest(){
	    	  double initnumber=0;
	    	  int set=0;
	    	  for(int i=0;i<this.testlength;i++){
	    		     if(this.correctrate[i]>initnumber){
	    		    	 initnumber=this.correctrate[i];
	    		    	 set=i;
	    		    	 
	    		    }
	    		
	    	  }
			return ++set;
	    	
	    }
	public static void main(String[] args) throws Exception {
		
		FindBestInstant fbi=new FindBestInstant();
		fbi.init(6);
		System.out.println(1111);
		fbi.testInstant("C:\\test\\4_result.arff", 0, 3);
		System.out.println(1111);
		for(int i=0;i<fbi.testlength;i++){
			System.out.print(fbi.correctrate[i]+" ");
			
			
		}
		System.out.println(fbi.findBest());
	}
	   
	  
        
	
}
