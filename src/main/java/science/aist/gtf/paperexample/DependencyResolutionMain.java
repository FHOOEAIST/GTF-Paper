package science.aist.gtf.paperexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.io.FileUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import science.aist.gtf.paperexample.domain.Dependency;
import science.aist.gtf.paperexample.domain.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static science.aist.gtf.paperexample.Utils.mkdirs;

/**
 * <p>Method, that does the transitiv dependency resolution on the extracted dependencies.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class DependencyResolutionMain {

    private static final String REMOTE_REPOSITORY_TYPE = "default";
    private static final RemoteRepository central = new RemoteRepository.Builder("central", REMOTE_REPOSITORY_TYPE, "https://repo1.maven.org/maven2/").build();
    private static final RemoteRepository jcenter = new RemoteRepository.Builder("jcenter", REMOTE_REPOSITORY_TYPE, "https://jcenter.bintray.com/").build();
    private static final RemoteRepository google = new RemoteRepository.Builder("google", REMOTE_REPOSITORY_TYPE, "https://maven.google.com/").build();

    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        return locator.getService(RepositorySystem.class);
    }


    private static RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        var localRepo = new LocalRepository("target/local-repo");
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        return session;
    }

    public static void main(String[] args) {
        var repoSystem = newRepositorySystem();
        var session = newSession(repoSystem);


        var mapper = new ObjectMapper();

        mkdirs("jsons");
        var errors = new File("jsons/_errors.txt");
        var files = Objects.requireNonNull(new File("dependencies").list());
        try (ProgressBar pb = new ProgressBarBuilder().setTaskName("Crawling data").setInitialMax(files.length).setStyle(ProgressBarStyle.ASCII).build()) {
            for (var dependencyFile : files) {
                pb.step();
                pb.setExtraMessage(dependencyFile);
                var repository = new Repository();
                repository.setGithubUser(dependencyFile.split("_")[0]);
                repository.setRepoName(dependencyFile.split("_")[1]);
                try {
                    var dependencies = FileUtils.readFileToString(new File("dependencies/" + dependencyFile));
                    var dependenciesSplit = dependencies.split("\n");
                    for (String dependencyStr : dependenciesSplit) {
                        extractDependencies(repoSystem, session, errors, dependencyFile, repository, dependencyStr);
                    }
                    mapper.writeValue(new File("jsons/" + dependencyFile + ".json"), repository);
                } catch (Exception e) {
                    writeError(errors, dependencyFile + " " + e.getMessage());
                }
            }
        }
    }

    private static void extractDependencies(RepositorySystem repoSystem, RepositorySystemSession session, File errors, String dependencyFile, Repository repository, String dependencyStr) {
        try {
            var dependency = new org.eclipse.aether.graph.Dependency(new DefaultArtifact(dependencyStr), "compile");
            var aistDependency = Dependency.fromArtifact(dependency.getArtifact());
            createDependencyTree(repoSystem, session, errors, dependencyFile, dependencyStr, dependency, aistDependency);
            repository.getDependencies().add(aistDependency);
        } catch (Exception e) {
            writeError(errors, dependencyFile + " " + dependencyStr + " " + e.getMessage());
        }
    }

    private static void createDependencyTree(RepositorySystem repoSystem, RepositorySystemSession session, File errors, String dependencyFile, String dependencyStr, org.eclipse.aether.graph.Dependency dependency, Dependency aistDependency) {
        try {
            var collectRequest = new CollectRequest();
            collectRequest.setRoot(dependency);
            collectRequest.addRepository(google);
            collectRequest.addRepository(jcenter);
            collectRequest.addRepository(central);
            DependencyNode node = repoSystem.collectDependencies(session, collectRequest).getRoot();
            var dependencyRequest = new DependencyRequest();
            dependencyRequest.setRoot(node);
            makeTree(node.getChildren(), aistDependency);
        } catch (Exception e) {
            writeError(errors, dependencyFile + " " + dependencyStr + " " + e.getMessage());
            aistDependency.setError(e.getMessage());
        }
    }

    static void writeError(File error, String e) {
        try {
            FileUtils.writeStringToFile(error, e + "\n", true);
        } catch (IOException ioException) {
            // ignore
        }
    }

    static void makeTree(List<DependencyNode> items, Dependency parent) {
        if (items == null) return;
        for (DependencyNode item : items) {
            var dep = Dependency.fromArtifact(item.getArtifact());
            makeTree(item.getChildren(), dep);
            parent.getChildren().add(dep);
        }
    }

}
