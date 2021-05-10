package science.aist.gtf.paperexample.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents a Github Repository with its dependencies.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
@Getter
public class Repository {
    private final List<Dependency> dependencies = new ArrayList<>();
    @Setter
    private String githubUser;
    @Setter
    private String repoName;
}
