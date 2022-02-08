package science.aist.gtf.paperexample.graph;

import lombok.Getter;

/**
 * <p>Node in the graph that represents a dependency.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
@Getter
public class FullyQualifiedVersionDependencyNode implements Node {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String artifactQualifier;

    public FullyQualifiedVersionDependencyNode(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        artifactQualifier = groupId + ":" + artifactId;
    }

    @Override
    public String getKey() {
        return groupId + ":" + artifactId + ":" + version;
    }
}
