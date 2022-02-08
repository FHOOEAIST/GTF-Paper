package science.aist.gtf.paperexample.graph;

import lombok.*;

/**
 * <p>Node in the graph that represents a project.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ProjectNode implements Node {

    private String userName;
    private String repoName;

    @Override
    public String getKey() {
        return userName + ":" + repoName;
    }
}
