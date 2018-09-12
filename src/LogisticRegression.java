import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set; 
import java.util.TreeMap;
import java.io.File;
import java.util.HashMap;


public class LogisticRegression 
{
	static HashMap<String,HashMap<String,Integer>> LR_Spam = new HashMap<String, HashMap<String,Integer>>();
	static HashMap<String, HashMap<String, Integer>> LR_Ham = new HashMap<String, HashMap<String,Integer>>();
	static HashMap<String, Integer> LR_SpamMap = new HashMap<String, Integer>();
	static HashMap<String, Integer> LR_HamMap = new HashMap<String, Integer>();
	
	HashMap<String, Double>ExpectedWeights_Sum = new HashMap<String, Double>();

	double LR_LearningRate = 0.0 ;
	double LR_Lambda = 0; 
	int LR_Iterations=0;
	String Location_Dir = new String();
	static Set<String> VocabDistinct = new HashSet<String>();
	static double W0 = 0.1;
	static Set<String> Ham_FileSet = new HashSet<String>();
	static Set<String> Spam_FileSet = new HashSet<String>();
	static Set<String> FileSet = new HashSet<String>();

	static HashMap<String, Double> Wordmap_wt = new HashMap<String, Double>();
	static HashMap<String, Double> new_Wordmap_wt = new HashMap<String, Double>();

	public LogisticRegression(String LocatonDir, Set<String> Vocab_Distinct,HashMap<String, HashMap<String, Integer>> Filemap_Ham,HashMap<String, HashMap<String, Integer>> Filemap_Spam,HashMap<String, Integer> Wordmap_Ham,HashMap<String, Integer> Wordmap_Spam, double LearningRate_eta, double Lambda, int NumIterationa, Set<String> File_All_set2, Set<String> File_Spam_set2, Set<String> File_Ham_set2)
	{
		Location_Dir = LocatonDir;
		
		LR_Ham = Filemap_Ham;
		LR_Spam = Filemap_Spam;
		
		LR_HamMap = Wordmap_Ham;
		LR_SpamMap = Wordmap_Spam;
		
		VocabDistinct = Vocab_Distinct;
		
		LR_LearningRate = LearningRate_eta;
		LR_Lambda = Lambda; 
		LR_Iterations= NumIterationa;
	
		FileSet = File_All_set2;
		Spam_FileSet = File_Spam_set2;
		Ham_FileSet = File_Ham_set2;
	}

	public void train_doc() 
	{
	   	
		for(String s:VocabDistinct)
		{
			double d =  (Math.random() * (1 -(-1))) + (-1);
			Wordmap_wt.put(s, d); // assign random weights
		}
		int counter = 0;
		for(int i =0 ; i<1; i++)
		{
			//System.out.println("check point "+ counter++);
			for(String Current_Word : VocabDistinct)
			{
				double Delta_ErrorWt = 0;
				for(String filename : FileSet)
				{
					double File_Class;
					int count_Current_Word = getCount_Current_Word(filename, Current_Word);
					if(Spam_FileSet.contains(filename))
					{
						File_Class = 1; //spam
					}
					else
					{
						File_Class = 0;//ham
					}
					double File_Sigmoid = Compute_WeightofFile(filename);
					double error = (File_Class - File_Sigmoid);
					Delta_ErrorWt = Delta_ErrorWt + count_Current_Word*error;
				}
				
				double new_weight_forWord = Wordmap_wt.get(Current_Word) + LR_LearningRate*Delta_ErrorWt -(LR_LearningRate*LR_Lambda*Wordmap_wt.get(Current_Word));
				Wordmap_wt.put(Current_Word, new_weight_forWord);
			}
		}
	
	}

	
	
	private int getCount_Current_Word(String filename, String Current_Word) 
	{
		int count = 0;
		if(Ham_FileSet.contains(filename))
		{
			try 
			{
				for(Entry<String, Integer> word_count: LR_Ham.get(filename).entrySet())
				{
					if(word_count.getKey().equals(Current_Word))
					{
						count = word_count.getValue();
						return count;
					}
				}
			}
			catch (Exception e) 
			{
				
				e.printStackTrace();
			}
		}
		else if(Spam_FileSet.contains(filename))
		{
			try 
			{
				for(Entry<String, Integer> word_count: LR_Spam.get(filename).entrySet())
				{
					if(word_count.getKey().equals(Current_Word))
					{
						count = word_count.getValue();
						return count;
					}
				}
			}
			catch (Exception e) 
			{
				
				e.printStackTrace();
			}
		}
		return 0;
	}

	
	
	private double Compute_WeightofFile(String filename)
	{

		if(Ham_FileSet.contains(filename))
		{
			double Sum_Weight = W0;
			try
			{
				for(Entry<String, Integer> values_map: LR_Ham.get(filename).entrySet())
				{
					Sum_Weight = Sum_Weight + Wordmap_wt.get( values_map.getKey() )  * values_map.getValue();
				}	
			}
			catch(Exception E)
			{
				System.out.println( E);
				
			}
			return (Sigmod(Sum_Weight) );
		}

		else{
			double Sum_Weight1 = W0;
			try
			{
				for(Entry<String, Integer> values_map1: LR_Spam.get(filename).entrySet())
				{
					Sum_Weight1 = Sum_Weight1 + Wordmap_wt.get( values_map1.getKey() )  * values_map1.getValue();
			    }	
			}
			catch(Exception e)
			{
				System.out.println("..");
			}

			return (Sigmod(Sum_Weight1) );
		}


	}

	private double Sigmod(double Sum_Weight) 
	{
		if(Sum_Weight>100)
		{
			return 1.0;
		}
		else if(Sum_Weight<-100)
		{

			return 0.0;
		}
		else
		{
			return (1.0 /(1.0+ Math.exp(-2*Sum_Weight)));
		}
	}

	public HashMap<String, Integer> getmyWordsinthisfile(String filename) 
	{

		if(Ham_FileSet.contains(filename))
		{
			return LR_Ham.get(filename);
		}
		else
		{
			return LR_Spam.get(filename);
		}

	}

	public int test_doc(HashMap<String, Integer> testmap) 
	{
		double Sum_Test = 0;
		for(Entry<String, Integer> ent :testmap.entrySet())
		{
			if(Wordmap_wt.containsKey(ent.getKey()))
			{
				Sum_Test = Sum_Test + (Wordmap_wt.get(ent.getKey())* ent.getValue());
			}
		}
		Sum_Test = Sum_Test+W0;
		if(Sum_Test>=0)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}

}