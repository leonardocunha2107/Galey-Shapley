import java.util.*;
public class StableMatching implements StableMatchingInterface {

	PriorityQueue<Integer> umm_queue;
	int[][] men_pref;
	int[] next_prop;
	int[] umm;
	int[] umw;
	int[][] women_pref;
	int n,m,w;
	int n_umm; //number of unmarried men
	PriorityQueue<Integer> [] women_marriages;
	int[][] mar;
	
	
	void init_data_struc(
		    int[] menGroupCount,
		    int[] womenGroupCount,
		    int[][] menPrefs,
		    int[][] womenPrefs
		  ) {
		n=0;
		for(int i=0;i<menGroupCount.length;i++) n+=menGroupCount[i];			
		n_umm=n;
		
		umm=menGroupCount;
		umw=womenGroupCount;
		w=womenGroupCount.length;
		m=menGroupCount.length;
		men_pref=menPrefs;
		women_marriages=new PriorityQueue[w];
		women_pref =new int[w][m];
		next_prop=new int[m];
		
		for(int i=0;i<w;i++)
			for(int j=0;j<m;j++) {
				women_pref[i][womenPrefs[i][j]]=j;}
				
		
		Comparator<Integer> comp = (a,b) -> {
			return umm[b]-umm[a];
		};
		
		umm_queue= new PriorityQueue<Integer>(m,comp);
		for (int i=0;i<m;i++) umm_queue.add(i);
		
		for(int i=0;i<w;i++) {
			int aux=i;
			comp = (a,b) -> {
				return women_pref[aux][b]-women_pref[aux][a];
			};
			women_marriages[i]=new PriorityQueue<Integer>(5,comp);
		}
		mar=new int[m][w];
	}
	
	void proposes(int man){		
		int woman=men_pref[man][next_prop[man]];
		while(!women_marriages[woman].isEmpty()&&umm[man]>umw[woman]) {
			int head=women_marriages[woman].peek();
			int slack=umm[man]-umw[woman];
			if(women_pref[woman][head]<=women_pref[woman][man]) break;
			//disengages part
			boolean goes_back=false;
			if(umm[head]==0) goes_back=true;
			if(mar[head][woman]>slack) {  
				mar[head][woman]-=slack;
				umm[head]+=slack;
				umw[woman]+=slack;
				n_umm+=slack;
			}
			else {
				umm[head]+=mar[head][woman];
				umw[woman]+=mar[head][woman];
				n_umm+=mar[head][woman];
				mar[head][woman]=0;
				women_marriages[woman].remove();
				if(men_pref[head][next_prop[head]]==woman) next_prop[head]++;

			}
			if(goes_back) umm_queue.add(head);
			
		} 
		if(umw[woman]==0) {
			next_prop[man]++;
			return;
		}
		if(umw[woman]>umm[man]) {
			mar[man][woman]+=umm[man];
			n_umm-=umm[man];
			umw[woman]-=umm[man];
			umm[man]=0;
		}
		if(umm[man]>=umw[woman]) {
			mar[man][woman]+=umw[woman];
			n_umm-=umw[woman];
			umm[man]-=umw[woman];
			umw[woman]=0;
		}
		women_marriages[woman].remove(man);
		women_marriages[woman].add(man);
		umm_queue.remove();

		if(umm[man]>0) umm_queue.add(man);
	}
	
	public int[][] constructStableMatching (
		    int[] menGroupCount,
		    int[] womenGroupCount,
		    int[][] menPrefs,
		    int[][] womenPrefs
		  ){
		if(menGroupCount.length==0)
			return new int[0][0];
		init_data_struc(menGroupCount,womenGroupCount,menPrefs,womenPrefs);
		while(n_umm>0) {
			proposes(umm_queue.peek());
			}
		return mar;
	}
}
