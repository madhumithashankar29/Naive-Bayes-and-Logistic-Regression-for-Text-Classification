import java.util.HashSet;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;



public class NaiveBayesClassification
{

	public static TreeMap <String, Integer> Spam_WordMap= new TreeMap<String, Integer>();
	public static TreeMap<String, Integer> Ham_WordMap = new TreeMap<String, Integer>();
	
	public static Set<String> VocabDistinct = new HashSet<String>();

	public static Set<String> List_StopWords = new HashSet<String>();

	public static void main(String[] args) throws Exception
	{
		String location_dir = args[0];
		String filter = args[1];
		
		File Stop_Words = new File(location_dir+"/stopwords.txt");
		File Spam_Train_Dir= new File(location_dir+"/train/spam");
		File Ham_Train_Dir = new File(location_dir+"/train/ham");
		File Spam_Test_Dir = new File(location_dir+"/test/spam");
		File Ham_Test_Dir = new File(location_dir+"/test/ham");
		
		
		String[] special_sym = {"!","#","%","^","&","*","(",")","!", ":",".","{","}", "[","]",">","<","?","/", "*","~", "@"};
	
		Distinct_Add(Spam_Train_Dir);
		Distinct_Add(Ham_Train_Dir);
	
		for(String s1: special_sym)
		{
			VocabDistinct.remove(s1);
		}
		
		
		Scanner s=null;
		try 
		{
			s = new Scanner(Stop_Words);
		} 
		catch (FileNotFoundException e) 
		{	
			e.printStackTrace();
		}
		while(s.hasNext())
		{
			String sw = s.next();
			List_StopWords.add(sw);
		}
		s.close();
		if(filter.equals("yes"))
		{
			//System.out.println("Removing stop words....");
			for(String str : List_StopWords)
			{
				if(VocabDistinct.contains(str))
				{
					VocabDistinct.remove(str);
				}
			}
		}
		Spam_getHashmap(Spam_Train_Dir);
		Ham_getHashmap(Ham_Train_Dir);

		for(String s1: special_sym)
		{
			if(Spam_WordMap.containsKey(s1) )
			{
				Spam_WordMap.remove(s1);
				
			}
			if(Ham_WordMap.containsKey(s1) )
			{
				Ham_WordMap.remove(s1);
				
			}
		}
		if(filter.equals("yes"))
		{

			for(String stopword : List_StopWords)
			{
				if(Spam_WordMap.containsKey(stopword) )
				{
					Spam_WordMap.remove(stopword);
				}
				if(Ham_WordMap.containsKey(stopword) )
				{
					Ham_WordMap.remove(stopword);
				}
			}
		}
		
		NaiveBayes NB = new NaiveBayes(Ham_WordMap,Spam_WordMap, VocabDistinct);
		NB.train_doc(1);
		// Priors//
		double SpamPrior_probability = 
				1.0*(Spam_Train_Dir.listFiles().length)/(  Spam_Train_Dir.listFiles().length + Ham_Train_Dir.listFiles().length ) ;

		double priorHam_probability = 1.0 - SpamPrior_probability;

		double l_SpamPrior_probability = Math.log(SpamPrior_probability);
		double l_HamPrior_probablity = Math.log(priorHam_probability);
		double Spam_numCorrect=0;
		int i = 0;
		for(File file: Spam_Test_Dir.listFiles())
		{
			i = i +1;
			if(NB.test_doc(file, l_HamPrior_probablity, l_SpamPrior_probability, List_StopWords,filter) == 1)
			{
				Spam_numCorrect = Spam_numCorrect + 1.0;
			}
		}
		
		if(filter.equals("yes"))
		{
			System.out.println("-----------Naive Bayes---------");
			System.out.println("Accuracy after removal of Stop Words(%):");
			
		}
		else
		{
			System.out.println("-----------Naive Bayes---------");
			System.out.println("Accuracy with out removing Stop Words(%): ");
		}
		System.out.println();
		double Accuracy_Spam = Spam_numCorrect/i; 
				
		double num_correct_ham =0;
		int j=0;
		for(File file: Ham_Test_Dir.listFiles())
		{
			j=j+1;
			if(NB.test_doc(file, priorHam_probability, SpamPrior_probability,List_StopWords,filter) == 0)
			{
				num_correct_ham = num_correct_ham + 1.0;
			}
		}
		System.out.println();
		double ham_accuracy = num_correct_ham/j; 
		System.out.println("-> "+ ham_accuracy*100);
		System.out.println();
		
	}
	private static void Distinct_Add(File Spam_Train_Dir) throws Exception 
	{
		for(File file: Spam_Train_Dir.listFiles())
		{
			Scanner scanner = new Scanner(file);
			while(scanner.hasNext())
			{
				String line = scanner.nextLine();
				for(String s : line.toLowerCase().trim().split(" "))
				{
					if(!s.isEmpty())
					{
						VocabDistinct.add(s);
					}
				}
			}
			scanner.close();

		}
	}
	private static void Spam_getHashmap(File Spam_Train_Dir) throws Exception 
	{
		for(File file: Spam_Train_Dir.listFiles())
		{
			Scanner sc = new Scanner(file);
			while(sc.hasNext())
			{
				String line = sc.nextLine();
				for(String s: line.toLowerCase().trim().split(" "))
				{
					if(!s.isEmpty())
					{	
						if(Spam_WordMap.containsKey(s))
						{
							Spam_WordMap.put(s, Spam_WordMap.get(s)+1);
						}
						else
						{
							Spam_WordMap.put(s, 1);
						}
					}
				}
			}
			sc.close();
		}

	}


	private static void Ham_getHashmap(File Ham_Train_Dir) throws Exception 
	{
		for(File file: Ham_Train_Dir.listFiles())
		{
			Scanner sc = new Scanner(file);
			while(sc.hasNext())
			{
				String line = sc.nextLine();
				for(String s: line.toLowerCase().trim().split(" "))
				{
					if(!s.isEmpty())
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
			}
			sc.close();
		}
	}


}
