package science.aist.gtf.paperexample.graph;

import lombok.Builder;

/**
 * <p>Node in the graph that represents a project.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
@Builder
public class ProjectNode implements Node {

    private final String userName;
    private final String repoName;

    @Override
    public String getKey() {
        return userName + ":" + repoName;
    }
}
