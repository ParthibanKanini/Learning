package pc.ado.dto;

/**
 * Data Transfer Object for team iteration information.
 */
public class Iteration {

    private final String id;
    private final String name;
    private final String startDate;
    private final String finishDate;

    public Iteration(String id, String name, String startDate, String finishDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.finishDate = finishDate;
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
}
