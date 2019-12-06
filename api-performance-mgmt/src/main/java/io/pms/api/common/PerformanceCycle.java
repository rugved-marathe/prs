/**
 * 
 */
package io.pms.api.common;

/**
 *This enum represents the performance cycles.
 */
public enum PerformanceCycle {
	APRIL, OCTOBER;
	
	 
	    private static PerformanceCycle[] vals = values();
	    public PerformanceCycle previous()
	    {
	    	int index = (this.ordinal()+1) % vals.length;
	    	if(index<0)
	    		index += vals.length;
	        return vals[index];
	    }
}
