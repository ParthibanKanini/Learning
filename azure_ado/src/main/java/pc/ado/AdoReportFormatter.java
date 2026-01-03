package pc.ado;

import java.util.List;

/**
 * Handles presentation/formatting of ADO data. Separates display logic from
 * business logic.
 */
public class AdoReportFormatter {

    /**
     * Displays team iterations and their details.
     */
    public void displayTeamIterations(List<Iteration> iterations, AdoApiClient apiClient) {
        System.out.println("\n--- Team Iterations ---");

        for (Iteration iteration : iterations) {
            displayIterationHeader(iteration);
            displayIterationCapacities(iteration.getId(), apiClient);
        }
    }

    /**
     * Displays iteration header information.
     */
    private void displayIterationHeader(Iteration iteration) {
        System.out.println("\n\n Iteration : " + iteration.getName());
        System.out.println("--------------------");
        System.out.println("  Start Date: " + iteration.getStartDate());
        System.out.println("  Finish Date: " + iteration.getFinishDate());
    }

    /**
     * Displays team members with capacity for a specific iteration.
     */
    private void displayIterationCapacities(String iterationId, AdoApiClient apiClient) {
        try {
            List<TeamMemberCapacity> capacities = apiClient.getIterationCapacities(iterationId);
            System.out.println("  --- Team Members with Capacity > 0 ---");

            for (TeamMemberCapacity capacity : capacities) {
                System.out.println("   " + capacity.getDisplayName() + " - " + capacity.getCapacityPerDay());
            }
        } catch (Exception e) {
            System.err.println("Error occurred while fetching iteration capacities: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays team header.
     */
    public void displayTeamHeader(String teamName) {
        System.out.println("  << " + teamName + "  >> ");
    }
}
