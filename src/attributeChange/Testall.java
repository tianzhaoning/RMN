package attributeChange;

import java.io.IOException;

public class Testall {
     public static void main(String[] args) throws Exception {
    	    System.out.println("开始");
    		String path="C:\\test\\grub-damage.arff";
        	Permutation pt=new Permutation();
        	pt.setLengthandSize(path);
        	pt.init(path);
        	pt.permutation(pt.attributes, 0);
        	
        	
        	
        	FindBestInstant fbi=new FindBestInstant();
        	
        	fbi.init(pt.total);
        	
    	    for(int i=1;i<pt.total+1;i++){
    	    	String filename="C:\\test\\"+i+"_result.arff";
    	    	System.out.print("正在读取"+filename);
    	    	int set=pt.confirmrow(filename);
    	    	
    	    	try{
    	        fbi.testInstant(filename, set, i-1);
    	            System.out.println("第"+i+"号文件正常运行");
    	            
    	    	}catch(Exception e){
    	    		System.out.println("第"+i+"号文件无法运行");
    	    	   
    	    	}
    	        
    	    }
    	    int best=fbi.findBest();
     	    System.out.println("最好的文件号为"+best+"正确率为"+fbi.correctrate[best-1]);
     	    System.out.println("源文件的正确率为"+fbi.correctrate[0]);
    	 
    	 
	}
	
 
	
	
	
	
	
	
	
	
	
	
	
	
	
}
