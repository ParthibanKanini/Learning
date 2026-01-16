package pc.ado.formatter;

import java.util.List;

import pc.ado.dto.Iteration;

/**
 * Interface for formatting Iteration data in different output formats.
 */
public interface IterationFormatter {
    
    /**
     * Formats iterations to the desired output format.
     * 
     * @param iterations list of iterations to format
     * @return formatted string
     */
    String format(List<Iteration> iterations);
}
