package it.unibz.thesis.factorization;

import it.unibz.thesis.tests.data.Preference;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Factorization {
	
	private int max_features = 8; 
	
//	static final int MAX_FEATURES = 8;            // Number of features to use 
	private static final int MIN_EPOCHS = 120;           // Minimum number of epochs per feature
	
	private int number_of_movies;

	private static final float MIN_IMPROVEMENT = 0.0001f;        // Minimum improvement required to continue current feature
	private static final float INIT = 0.1f;           // Initialization value for features
	private static final float LRATE = 0.001f;        // Learning rate parameter
	private static final float K = 0.015f;         // Regularization parameter used to minimize over-fitting


	private int             m_nRatingCount;                                 // Current number of loaded ratings
	private Data            m_aRatings[];                        // Array of ratings data
	private Movie           m_aMovies[];                          // Array of movie metrics
	private Customer        m_aCustomers[];                    // Array of customer metrics
	private float           m_aMovieFeatures[][];     // Array of features by movie (using floats to save space)
	private float           m_aCustFeatures[][];   // Array of features by customer (using floats to save space)
	private Map<Integer, Integer> m_mCustIds = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> m_mMovieIds = new HashMap<Integer, Integer>();
    
    
	private Collection<Preference> preferences;
    
    
    
    
    
  //-------------------------------------------------------------------
  // Initialization
  //-------------------------------------------------------------------
    public Factorization(Collection<Preference> preferences) {
    	
    	this.preferences = preferences;
                
	}    
    
    public Factorization(Collection<Preference> preferences, int features) {
    	
    	this.preferences = preferences;
    	this.max_features = features;
                
	}    
    
    public void initialize() {    	
    	loadArrays();    	
        calcMetrics();
        calcFeatures();
    }
    
	 //-------------------------------------------------------------------
	 // Calculations - This Paragraph contains all of the relevant code
	 //-------------------------------------------------------------------

	 //
	 // CalcMetrics
	 // - Loop through the history and pre-calculate metrics used in the training 
	 // - Also re-number the customer id's to fit in a fixed array
	 //
	 private void calcMetrics()
	 {
		 /*
			if (m_aCustomers[custId] == null) {
				m_aCustomers[custId] = new Customer();
			}
			
			if (m_aMovies[movieId] == null) {
				m_aMovies[movieId] = new Movie();
			}
			

		    
		    */
	
	     Integer mid, cid;	 
	
	     // Process each row in the training set
	     for (int i=0; i<m_nRatingCount; i++)
	     {    	 

	         Data rating = m_aRatings[i];
	         
	         int movieId = rating.MovieId;
	         mid = m_mMovieIds.get(movieId);
	         if (mid == null) {	        	 
	        	 mid = m_mMovieIds.size();
	        	 m_mMovieIds.put(movieId, mid);
	        	 Movie movie = new Movie();
	 		     movie.RatingCount = 0;
			     movie.RatingSum = 0;
	        	 m_aMovies[mid] = movie;
	         }
	         
	         rating.MovieId = mid;
	         
	         m_aMovies[mid].RatingCount++;
	         m_aMovies[mid].RatingSum += rating.Rating;
	         
	         // Add customers (using a map to re-number id's to array indexes)
	         cid = m_mCustIds.get(rating.CustId);         
	         if (cid == null)
	         {
	             cid = m_mCustIds.size();
	
	             // Reserve new id and add lookup
	             m_mCustIds.put(rating.CustId, cid);
	
	             // Store off old sparse id for later
	             Customer customer = new Customer();
	             customer.CustomerId = rating.CustId;
	             customer.RatingCount = 0;
	             customer.RatingSum = 0;
	             
	             m_aCustomers[cid] = customer; 

	         }
	
	         // Swap sparse id for compact one
	         rating.CustId = cid;
	
	         m_aCustomers[cid].RatingCount++;
	         m_aCustomers[cid].RatingSum += rating.Rating;
	         
	         
	         
	     }
	     
	     // Do a follow-up loop to calc movie averages
	     for (int i=0; i<number_of_movies; i++)
	     {
	         Movie movie = m_aMovies[i];
	         movie.RatingAvg = movie.RatingSum / (1.0 * movie.RatingCount);
	         movie.PseudoAvg = (3.23 * 25 + movie.RatingSum) / (25.0 + movie.RatingCount);
	     }
	 }
	 
	 //
	// CalcFeatures
	// - Iteratively train each feature on the entire data set
	// - Once sufficient progress has been made, move on
	//
	private void calcFeatures()
	{
	    int f, e, i, custId, cnt = 0;
	    Data rating;
	    double err, p, sq, rmse_last = 0, rmse = 2.0;
	    int movieId;
	    float cf, mf;

	    for (f=0; f<max_features; f++)
	    {
	        // Keep looping until you have passed a minimum number 
	        // of epochs or have stopped making significant progress
	        for (e=0; (e < MIN_EPOCHS) || (rmse <= rmse_last - MIN_IMPROVEMENT); e++)
	        {
	            cnt++;
	            sq = 0;
	            rmse_last = rmse;

	            for (i=0; i<m_nRatingCount; i++)
	            {
	                rating = m_aRatings[i];
	                movieId = rating.MovieId;
	                custId = rating.CustId;

	                // Predict rating and calc error
	                p = predictRating(movieId, custId, f, rating.Cache, true);
	                err = (1.0 * rating.Rating - p);
	                sq += err*err;
	                
	                // Cache off old feature values
	                cf = m_aCustFeatures[f][custId];
	                mf = m_aMovieFeatures[f][movieId];

	                // Cross-train the features
	                m_aCustFeatures[f][custId] += (float)(LRATE * (err * mf - K * cf));
	                m_aMovieFeatures[f][movieId] += (float)(LRATE * (err * cf - K * mf));
	            }
	            
	            rmse = Math.sqrt(sq/m_nRatingCount);
	                  
//	            System.out.println("     <set x='%d' y='%f' />",cnt,rmse);
	        }

	        // Cache off old predictions
	        for (i=0; i<m_nRatingCount; i++)
	        {
	            rating = m_aRatings[i];
	            rating.Cache = (float) predictRating(rating.MovieId, rating.CustId, f, rating.Cache, false);
	        }            
	    }
	}
	
	//
	// PredictRating
	// - During training there is no need to loop through all of the features
	// - Use a cache for the leading features and do a quick calculation for the trailing
	// - The trailing can be optionally removed when calculating a new cache value
	//
	private double predictRating(int movieId, int custId, int feature, float cache, boolean bTrailing)
	{
	    // Get cached value for old features or default to an average
	    double sum = (cache > 0) ? cache : 1; //m_aMovies[movieId].PseudoAvg; 

	    // Add contribution of current feature
	    sum += m_aMovieFeatures[feature][movieId] * m_aCustFeatures[feature][custId];
	    if (sum > 5) sum = 5;
	    if (sum < 1) sum = 1;

	    // Add up trailing defaults values
	    if (bTrailing)
	    {
	        sum += (max_features-feature-1) * (INIT * INIT);
	        if (sum > 5) sum = 5;
	        if (sum < 1) sum = 1;
	    }

	    return sum;
	}


	//
	// PredictRating
	// - This version is used for calculating the final results
	// - It loops through the entire list of finished features
	//
	public Double predictRating(int movieId, int custId)
	{
		Integer cid = m_mCustIds.get(custId);
		Integer mid = m_mMovieIds.get(movieId);
		
		if (cid == null || mid == null) return null;
		
		movieId = mid;
		custId = cid;
		
	    double sum = 1; //m_aMovies[movieId].PseudoAvg;

	    for (int f=0; f<max_features; f++) 
	    {
	        sum += m_aMovieFeatures[f][movieId] * m_aCustFeatures[f][custId];
	        if (sum > 5) sum = 5;
	        if (sum < 1) sum = 1;
	    }

	    return sum;
	}

    
	//
	// ProcessFile
	// - Load a history file in the format:
	//
	//   <MovieId>:
	//   <CustomerId>,<Rating>
	//   <CustomerId>,<Rating>
	//   ...
	private void loadArrays()
	{
		
		m_nRatingCount = 0;
	    m_aRatings = new Data[preferences.size()];                        // Array of ratings data
	    
	    Set<Integer> movieIds = new HashSet<Integer>();
	    Set<Integer> customerIds = new HashSet<Integer>();
		
		for (Preference pref : preferences) {			
			int movieId = pref.getItemId();
			int custId = pref.getUserId();
			int rating = (int) pref.getValue();

			movieIds.add(movieId);
			customerIds.add(custId);
			
		    if (m_aRatings[m_nRatingCount] == null) {
		    	m_aRatings[m_nRatingCount] = new Data();
		    }
		    
	        m_aRatings[m_nRatingCount].MovieId = (short)movieId;
	        m_aRatings[m_nRatingCount].CustId = custId;
	        m_aRatings[m_nRatingCount].Rating = (byte) rating;
	        m_aRatings[m_nRatingCount].Cache = 0;
	        m_nRatingCount++;
		}
		
		int number_of_customers = customerIds.size();
		number_of_movies = movieIds.size();
		
		m_aMovies = new Movie[number_of_movies];                          // Array of movie metrics
	    m_aCustomers = new Customer[number_of_customers];                    // Array of customer metrics
	    m_aMovieFeatures = new float[max_features][number_of_movies];     // Array of features by movie (using floats to save space)
	    m_aCustFeatures = new float[max_features][number_of_customers];   // Array of features by customer (using floats to save space)
	            
        for (int f=0; f<max_features; f++)
        {
            for (int i=0; i<number_of_movies; i++) m_aMovieFeatures[f][i] = (float)INIT;
            for (int i=0; i<number_of_customers; i++) m_aCustFeatures[f][i] = (float)INIT;
        }
				
	}

}
