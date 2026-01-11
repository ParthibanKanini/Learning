package pc.ado.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for team iteration information.
 */
public class Iteration {

    private final String projName;
    private final String teamName;
    private final String id;
    private final String name;
    private final String startDate;
    private final String finishDate;
    private final List<TeamMemberAllocation> allocations;
    private final List<WorkItem> workItems;
    private final List<PullRequest> pullRequests;

    public Iteration(String projName, String teamName, String id, String name, String startDate, String finishDate) {
        this.projName = projName;
        this.teamName = teamName;
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.allocations = new ArrayList<>();
        this.workItems = new ArrayList<>();
        this.pullRequests = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public String getProjName() {
        return projName;
    }

    public String getTeamName() {
        return teamName;
    }

    public List<TeamMemberAllocation> getAllocations() {
        return allocations;
    }

    public void addAllocation(TeamMemberAllocation allocation) {
        allocations.add(allocation);
    }

    public List<WorkItem> getWorkItems() {
        return workItems;
    }

    public void addWorkItem(WorkItem workItem) {
        workItems.add(workItem);
    }

    public List<PullRequest> getPullRequests() {
        return pullRequests;
    }

    public void addPullRequest(PullRequest pullRequest) {
        pullRequests.add(pullRequest);
    }

}
