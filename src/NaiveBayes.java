import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;



public class NaiveBayes 
{
	
	static TreeMap<String, Integer> Ham_Map = new TreeMap<String, Integer>();
	static TreeMap<String, Integer> Spam_Map = new TreeMap<String, Integer>();
	
	static HashMap<String, Double> HamLikelihood = new HashMap<String, Double>();
	static HashMap<String, Double> SpamLikelihood = new HashMap<String, Double>();
	
	static int spam_total=0;
	static int ham_total=0;
	static Set<String> Vocabularyset = new HashSet<String>();


	public NaiveBayes(TreeMap<String,Integer> my_ham_wordmap,TreeMap<String, Integer> spam_wordmap, Set<String> distinct_vocab)
	{

		Spam_Map = spam_wordmap;
		Ham_Map = my_ham_wordmap;
		Vocabularyset = distinct_vocab;

	}
	public int train_doc(int i)
	{
		int h_totalterms =0;
		for(Entry<String, Integer> enter: Ham_Map.entrySet())
		{
			h_totalterms = h_totalterms + enter.getValue();
		}

		int s_totalterms =0;
		for(Entry<String, Integer> enter: Spam_Map.entrySet())
		{
			s_totalterms = s_totalterms + enter.getValue();
		}
		
		
		for(String v : Vocabularyset)
		{

			if(Spam_Map.containsKey(v))
			{
				double SpamLikely = (Spam_Map.get(v)+1.0)/(s_totalterms+Vocabularyset.size()+1.0);
				double SpamLogLikely = Math.log(SpamLikely);
				SpamLikelihood.put(v, SpamLogLikely);
			}			
		}
		for(String v : Vocabularyset)
		{

			if(Ham_Map.containsKey(v)){

				double HamLikely = (Ham_Map.get(v)+1.0)/(h_totalterms+Vocabularyset.size()+1.0);
				double HamLogLikely = Math.log(HamLikely);
				HamLikelihood.put(v, HamLogLikely);
			}
		
		}
		spam_total = s_totalterms;
		ham_total = h_totalterms;
		
		return 1;
	}

	public int test_doc(File input_file, double HamPrior_probability, double SpamPrior_probability) throws Exception 
	{

		double Sprobab_current= 0.0;
		double Hprobab_current = 0.0;
		Scanner scanner = new Scanner(input_file);
		while(scanner.hasNext())
		{
			String line = scanner.nextLine();
		

			for(String s : line.toLowerCase().split(" "))
			{
					
					if(SpamLikelihood.containsKey(s))
					{
						Sprobab_current= Sprobab_current+ SpamLikelihood.get(s);
					}
					else
					{

						Sprobab_current= Sprobab_current+ Math.log(1.0 / (spam_total + Vocabularyset.size()+1.0)) ;

					}
					if(HamLikelihood.containsKey(s))
					{
						Hprobab_current = Hprobab_current + HamLikelihood.get(s);
					}
					else
					{
						Hprobab_current = Hprobab_current +  Math.log( 1.0 / (ham_total + Vocabularyset.size()+1.0));
					}

				
			}
		}
		scanner.close();
		Sprobab_current= Sprobab_current+ SpamPrior_probability;
		Hprobab_current = Hprobab_current + SpamPrior_probability;

		if(Sprobab_current> Hprobab_current)
		{
			return 1; // spam
		}

		else
		{
			return 0;
		}
	
	}

	public int test_doc(File input_file, double HamPrior_probability, double SpamPrior_probability, Set<String> stopword_list, String tofilter) throws Exception 
	{

		double Sprobab_current= 0.0;
		double Hprobab_current = 0.0;
		Scanner scanner = new Scanner(input_file);
		while(scanner.hasNext())
		{
			String line = scanner.nextLine();
            if(tofilter.equals("yes") )
            {
            	for(String s : line.toLowerCase().split(" "))
            	{     
            		if(!stopword_list.contains(s))
            		{
    					if(SpamLikelihood.containsKey(s))
    					{
    						Sprobab_current= Sprobab_current+ SpamLikelihood.get(s);
    					}
    					else
    					{

    						Sprobab_current= Sprobab_current+ Math.log(1.0 / (spam_total + Vocabularyset.size()+1.0)) ;

    					}
    					if(HamLikelihood.containsKey(s))
    					{
    						Hprobab_current = Hprobab_current + HamLikelihood.get(s);
    					}
    					else
    					{
    						Hprobab_current = Hprobab_current +  Math.log( 1.0 / (ham_total + Vocabularyset.size()+1.0));
    					}
            		}
    			}
            }
            else
            {
            	for(String s : line.toLowerCase().split(" "))
            	{
            			
    					if(SpamLikelihood.containsKey(s))
    					{
    						Sprobab_current= Sprobab_current+ SpamLikelihood.get(s);
    					}else
    					{

    						Sprobab_current= Sprobab_current+ Math.log(1.0 / (spam_total + Vocabularyset.size()+1.0)) ;

    					}
    					if(HamLikelihood.containsKey(s))
    					{
    						Hprobab_current = Hprobab_current + HamLikelihood.get(s);
    					}
    					else
    					{
    						Hprobab_current = Hprobab_current +  Math.log( 1.0 / (ham_total + Vocabularyset.size()+1.0));
    					}

    				}	
    			}
            
		}
		scanner.close();
		Sprobab_current= Sprobab_current+ SpamPrior_probability;
		Hprobab_current = Hprobab_current + SpamPrior_probability;

		if(Sprobab_current> Hprobab_current)
		{
			return 1; // spam
		}

		else
		{
			return 0;
		}

	}


}


