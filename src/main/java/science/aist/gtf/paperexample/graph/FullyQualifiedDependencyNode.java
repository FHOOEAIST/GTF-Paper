package science.aist.gtf.paperexample.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>Node in the graph that represents a dependency without its version.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FullyQualifiedDependencyNode implements Node {
    private String groupId;
    private String artifactId;

    @Override
    public String getKey() {
        return groupId + ":" + artifactId;
    }
}
