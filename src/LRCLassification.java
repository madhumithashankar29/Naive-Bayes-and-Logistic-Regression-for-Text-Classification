import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.io.File;
import java.io.FileNotFoundException;



public class LRCLassification
{

	public static HashMap<String, Integer> Spam_WordMap = new HashMap<String, Integer>();
	public static HashMap<String, Integer> Ham_WordMap = new HashMap<String, Integer>();
	public static Set<String> VocabDistinct = new HashSet<String>();

	static Set<String> FileSet_Spam = new HashSet<String>();
	static Set<String>FileSet_Ham = new HashSet<String>();
    static Set<String> FileSet = new HashSet<String>();
	public static Set<String> List_Stop_Words = new HashSet<String>();

	public static HashMap<String,HashMap<String, Integer> >FileMap_Spam = new HashMap<String, HashMap<String,Integer>>();	
	public static HashMap<String,HashMap<String, Integer> >FileMap_Ham = new HashMap<String, HashMap<String,Integer>>();



	public static void main(String[] args) throws Exception 
	{
		String Location_Dir = args[0];
		String option = args[1];
		double LearningRate_eta= Double.parseDouble(args[2]);
		double Lambda = Double.parseDouble(args[3]);
		int numOfiterations = Integer.parseInt(args[4]);

		File Spam_test_Dir = new File(Location_Dir+"/test/spam");
		File Ham_test_Dir = new File(Location_Dir+"/test/ham");  
		File Spam_train_Dir = new File(Location_Dir+"/train/spam");
		File Ham_train_Dir = new File(Location_Dir+"/train/ham"); 
        File Stop_Words = new File(Location_Dir+"/stopwords.txt");

		Distinct_add(Spam_train_Dir);
		Distinct_add(Ham_train_Dir);

		Scanner s3=null;
		try 
		{
			s3 = new Scanner(Stop_Words);
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		while(s3.hasNext()){
			String sw = s3.next();
			List_Stop_Words.add(sw);
	}
	s3.close();

	if(option.equals("yes"))
	{
		System.out.println("Removing stop words...");
		for(String str : List_Stop_Words)
		{
			if(VocabDistinct.contains(str))
			{
				VocabDistinct.remove(str);
			}
		}

	}
	Spam_getHashmap(Spam_train_Dir);
	Ham_getHashmap(Ham_train_Dir);
	LogisticRegression LR = new LogisticRegression(Location_Dir, VocabDistinct,FileMap_Ham,FileMap_Spam,Ham_WordMap,Spam_WordMap, LearningRate_eta, Lambda, numOfiterations,FileSet,FileSet_Spam,FileSet_Ham);
	LR.train_doc();
	int Spam_TestFiles_num = 0;
	int count_s = 0 ;
	for(File testfile : Spam_test_Dir.listFiles())
	{
		Spam_TestFiles_num = Spam_TestFiles_num+1;
		HashMap<String, Integer> testmap = new HashMap<String, Integer>();
		Scanner sc = new Scanner(testfile);
		while(sc.hasNext())
		{
			String line = sc.nextLine();
			for(String s: line.toLowerCase().trim().split(" "))
			{
				s = s.replaceAll("[^a-zA-Z]+", "");
				if(testmap.containsKey(s))
				{
					testmap.put(s, testmap.get(s)+1);
				}
				else
				{
					testmap.put(s, 1);
				}
			}	
		}
		sc.close();
	
		if(option.equals("yes"))
		{
		    for(String stopword: List_Stop_Words)
		    {
				stopword = stopword.replaceAll("[^a-zA-Z]+", "");
				if(testmap.containsKey(stopword))
				{
					testmap.remove(stopword);
				}
			}
		}
		
		int spam = LR.test_doc(testmap);
		if(spam == 1)
		{
			count_s++;
		}
	}

	double spam_acc = ( (double)count_s / (double)Spam_TestFiles_num)*100;
	
	System.out.println();
	
	int count_h = 0 ;
	int Ham_Testfiles_num = Ham_test_Dir.listFiles().length;
	for(File testfile : Ham_test_Dir.listFiles())
	{
		HashMap<String, Integer> testmap = new HashMap<String, Integer>();
		Scanner sc = new Scanner(testfile);
		while(sc.hasNext())
		{
			String line = sc.nextLine();
			for(String s: line.toLowerCase().trim().split(" "))
			{
				s = s.replaceAll("[^a-zA-Z]+", "");
					if(testmap.containsKey(s))
					{
    					testmap.put(s, testmap.get(s)+1);
					}
					else
					{
						testmap.put(s, 1);
					}
			}	
		}
		sc.close();
		int ham = LR.test_doc(testmap);
		if(ham == 0)
		{
			count_h++;
		}
	}


	double ham_acc = ( (double)count_h / (double)Ham_Testfiles_num)*100;
	System.out.println("Accuracy "+ ham_acc);

	}
	private static void Distinct_add(File Spam_train_Dir) throws Exception 
	{
		for(File file: Spam_train_Dir.listFiles())
		{
			Scanner scanner = new Scanner(file);
			while(scanner.hasNext())
			{
				String line = scanner.nextLine();
				for(String s : line.toLowerCase().trim().split(" "))
				{
					s = s.replaceAll("[^a-zA-Z]+", "");
					if(!s.isEmpty())
					{
						VocabDistinct.add(s);
					}
				}
			}
			scanner.close();

		}
	}
	
	private static void Ham_getHashmap(File Ham_train_Dir) throws Exception 
	{
		for(File file: Ham_train_Dir.listFiles())
		{
			HashMap<String, Integer> Vocab_Ham = new HashMap<String, Integer>();
			FileSet_Ham.add(file.getName());
			FileSet.add(file.getName());
			Scanner sc = new Scanner(file);
			while(sc.hasNext())
			{
				String line = sc.nextLine();
				for(String s: line.toLowerCase().trim().split(" "))
				{
					s = s.replaceAll("[^a-zA-Z]+", "");
					if(!s.isEmpty())
					{
						if(VocabDistinct.contains(s))
						{
							if(Ham_WordMap.containsKey(s))
							{
								Ham_WordMap.put(s, Ham_WordMap.get(s)+1);
							}
							else
							{
								Ham_WordMap.put(s, 1);
							}

						}	
					}	

					if(!s.isEmpty())
					{
						if(VocabDistinct.contains(s))
						{
							if(Vocab_Ham.containsKey(s))
							{
								Vocab_Ham.put(s, Vocab_Ham.get(s)+1);
							}
							else
							{
								Vocab_Ham.put(s, 1);
							}
						}
					}
					FileMap_Ham.put(file.getName(), Vocab_Ham);
				}
			}
			sc.close();
		}
	}

	private static void Spam_getHashmap(File Spam_train_Dir) throws Exception 
	{
		for(File file: Spam_train_Dir.listFiles())
		{
			HashMap<String, Integer> Vocab_Spam = new HashMap<String, Integer>();
			FileSet_Spam.add(file.getName());
			FileSet.add(file.getName());
			Scanner sc = new Scanner(file);
			while(sc.hasNext())
			{
				String line = sc.nextLine();
				for(String s: line.toLowerCase().trim().split(" "))
				{
					s = s.replaceAll("[^a-zA-Z]+", "");
					if(VocabDistinct.contains(s))
					{
						if(Spam_WordMap.containsKey(s))
						{
							Spam_WordMap.put(s, Spam_WordMap.get(s)+1);
						}
						else
						{
							Spam_WordMap.put(s, 1);
						}
						if(Vocab_Spam.containsKey(s))
						{
							Vocab_Spam.put(s, Vocab_Spam.get(s)+1);
						}
						else
						{
							Vocab_Spam.put(s, 1);
						}
					}

					FileMap_Spam.put(file.getName(), Vocab_Spam);
				}
			}
			sc.close();
		}

	}





}


