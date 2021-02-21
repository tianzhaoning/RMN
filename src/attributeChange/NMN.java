package attributeChange;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

public class NMN {
     public NMN() throws Exception{
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
    	
            //初始化BI
            BI.findBestInstant(instancesTrain);
            //进行全排列，创建数据集,训练
    	    BI.permutation(BI.AttributeString, BI.fv, BI.attributes,0);
    	    //在内存中获取最好的数据集
    		Instances best=BI.returnBestInstances();
    		//设置分类属性所在行号，第一行为0
            best.setClassIndex(best.numAttributes()-1);
            //建立分类器
            Classifier classifier1;
            // mn算法
            classifier1 = (Classifier) Class.forName("weka.classifiers.MN.M_N").newInstance();
            //读入数据集
            classifier1.buildClassifier(best);
            //创建评估对象
            Evaluation eval = new Evaluation(best);
            //测试  
            System.out.println("最终测试");
            eval.crossValidateModel(classifier1, best,10,new Random(1));
            //得到结果
            double correctrate=1-eval.errorRate();
            //eval.numInstances();
            //输出结果
    		System.out.println(correctrate);
    	
     }     
	
	public static void main(String[] args) throws Exception {
                  NMN nmn=new NMN();
                //C:\\等待校验\\data_banknote_authentication.arff
                //C:\\等待校验\\nursery.arff
          		//C:\\等待校验\\abalone.arff
          		
          		//
          		//C:\\等待校验\\cmc.arff
          		
                  //C:\\等待校验\\weather.arff 3 6 7 10 12
                  
	//C:\\Users\\name\\Desktop\\1.arff
                  //C:\\Users\\name\\Desktop\\2.arff  C:\\2.csv
	
	
                //C:\\等待校验\\weather.arff
                //E:\\等待校验\\balloons.arff
	
	
	
	
	
	
	
	
	
}
}
