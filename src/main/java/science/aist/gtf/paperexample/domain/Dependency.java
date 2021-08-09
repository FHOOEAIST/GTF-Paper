package science.aist.gtf.paperexample.domain;

import lombok.*;
import org.eclipse.aether.artifact.Artifact;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Represents a project dependency</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@RequiredArgsConstructor
public class Dependency {

    @EqualsAndHashCode.Exclude
    private final Set<Dependency> children = new HashSet<>();
    @NonNull
    private String version;
    @NonNull
    private String groupId;
    @NonNull
    private String artifactId;
    @NonNull
    private String scope;

    @Setter
    @EqualsAndHashCode.Exclude
    private String error;

    /**
     * Creates the dependency from an artifact.
     *
     * @param artifact The artifact.
     * @return The resulting dependency.
     */
    public static Dependency fromArtifact(Artifact artifact) {
        return new Dependency(artifact.getVersion(), artifact.getGroupId(), artifact.getArtifactId(), "");
    }

    /**
     * Create the dependency from a eclipse dependency.
     *
     * @param dependency The dependency.
     * @return The resulting dependency.
     */
    public static Dependency fromDependency(org.eclipse.aether.graph.Dependency dependency) {
        var artifact = dependency.getArtifact();
        return new Dependency(artifact.getVersion(), artifact.getGroupId(), artifact.getArtifactId(), dependency.getScope());
    }

    /**
     * Returns the dependency as a string in the format {groupId}:{artifactId}:{version}
     *
     * @return The dependency string.
     */
    public String toDependencyString() {
        return groupId + ":" + artifactId;// + ":" + version;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        var sb = new StringBuilder();
        sb.append("\n");
        sb.append(" ".repeat(indent));
        sb.append(groupId).append(':').append(artifactId).append(':').append(version);
        if (!children.isEmpty()) {
            sb.append(" [");
        }
        sb.append(children.stream().map(c -> c.toString(indent + 2)).collect(Collectors.joining(",")));
        if (!children.isEmpty()) {
            sb.append('\n').append(" ".repeat(indent)).append(']');
        }
        return sb.toString();
    }
}
