package attributeChange;

import java.io.File;
import java.io.BufferedReader;    
import java.io.FileReader;
import java.util.ArrayList;    
import java.io.BufferedWriter;    
import java.io.File;    
import java.io.FileNotFoundException;    
import java.io.FileWriter;    
import java.io.IOException;   

public class Process {
	public void TestRead (String FilePath) {
		try {
    		
            BufferedReader reader = new BufferedReader(new FileReader(FilePath));
            
            //跳过第一行信息  
            reader.readLine();
            String line = null;
            line = reader.readLine();
            String item[] = line.split(",");
            
            //记录Jref，导出结果时用于创建文件名
            String JrefName = item[0]; 
            String path = "D:\\ResearchData\\Result\\" + JrefName + "_result.csv" ;
           

            //三个不定长list,存放三列信息
            ArrayList<String> Year = new ArrayList<>();
            ArrayList<Integer> ISSNCount = new ArrayList<>(); 
            ArrayList<Integer> ALLCount = new ArrayList<>();
            Year.add(item[1]);
            int ISSNNum = Integer.parseInt(item[2]);
            int ALLNum = Integer.parseInt(item[3]);
            ISSNCount.add(ISSNNum);
            ALLCount.add(ALLNum);
            
            //读取原数据
            while((line=reader.readLine()) != null){    
                 String row[] = line.split(",");	//CSV格式文件为逗号分隔符文件，这里每一行按逗号切分  
     
                 ISSNNum = Integer.parseInt(row[2]);
                 ALLNum = Integer.parseInt(row[3]);
                 Year.add(row[1]);
                 ISSNCount.add(ISSNNum);
                 ALLCount.add(ALLNum);
            }
            //导出影响力因子
            File StorePath = new File(path); 
            // 新建一个CSV数据文件   
            BufferedWriter bw = new BufferedWriter(new FileWriter(StorePath,false));
            bw.write("Year,ImpactFactor");
            for (int i = 2; i < Year.size(); i++) {
            	double ImpactFactor = (double)(ALLCount.get(i-1) + ALLCount.get(i-2))/(ISSNCount.get(i-1) + ISSNCount.get(i-2));
            	String res = Year.get(i) + "," + ImpactFactor;
            	bw.newLine(); 
            	bw.write(res);    
                  
            }
            //清空缓存区，关闭数据流
            bw.flush();
            bw.close();
        }
    	catch (FileNotFoundException e){

            System.out.println("没有找到指定文件");

        }catch (IOException e){

            System.out.println("文件读写出错");

        }
    	catch (Exception e) {    
             e.printStackTrace();    
         }
	}
	public static void main(String[] args) {
		String FolderPath = "D:\\ResearchData\\SubTable";
		File TargetFolder = new File(FolderPath);
		String[] FileList = TargetFolder.list();
		Process a = new Process();
		int count = 0;
		for (int i = 0; i < FileList.length; i++) {
			String FilePath = FolderPath + "\\" + FileList[i];
			a.TestRead(FilePath);
			count ++;
		}
		System.out.println("一共处理了"+ count +"文件");
	}
}

