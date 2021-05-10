package science.aist.gtf.paperexample.verification;

import science.aist.gtf.verification.syntactic.constraint.Constraint;
import science.aist.gtf.verification.syntactic.constraint.ConstraintError;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>A constraint, that evaluates if there are dependencies with the same group_id + artifact_id but different version.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class DependencyNodeArtifactQualifierConstraint implements Constraint<String> {

    public static final ConstraintError DUPLICATED_DEPENDENCY_CONSTRAINT_ERROR = new ConstraintError("Duplicated Dependency");

    private final Set<String> dependencies = new HashSet<>();

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public boolean isBreakingConstraint() {
        return false;
    }

    @Override
    public ConstraintError apply(String dependencyNode, Field field) {
        if (dependencies.contains(dependencyNode))
            return DUPLICATED_DEPENDENCY_CONSTRAINT_ERROR;
        dependencies.add(dependencyNode);
        return ConstraintError.NoError;
    }
}
