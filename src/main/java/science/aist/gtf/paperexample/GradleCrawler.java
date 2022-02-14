/*
 * Copyright (c) 2022 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.gtf.paperexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static science.aist.gtf.paperexample.Utils.cast;
import static science.aist.gtf.paperexample.Utils.mkdirs;

/**
 * <p>Crawls the gradle files from github.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class GradleCrawler {

    private static final String DATA_FOLDER = "data/";

    private final ObjectMapper mapper = new ObjectMapper();
    private final ProxyClient client = new ProxyClient();

    private List<Map<String, String>> extractApps() throws IOException {
        return Utils.<List<Map<String, String>>>cast(
                        mapper.readValue(GradleCrawler.class.getResourceAsStream("/fdroid.json"), Map.class).get("apps")
                ).stream()
                .filter(app -> {
                    String sourceCode = app.get("sourceCode");
                    return sourceCode != null && sourceCode.startsWith("https://github.com");
                })
                .collect(Collectors.toList());
    }

    private Map<String, Object> getGradleEntriesMap(String currentDir, String userNameAndRepo, String branch) throws IOException {
        var githubListFilesEndpoint = "https://api.github.com/repos/" + userNameAndRepo + "/git/trees/" + branch + "?recursive=1";
        var content = client.get(githubListFilesEndpoint);
        Files.writeString(Path.of(currentDir + "github_meta_data.json"), content);
        return cast(mapper.readValue(content, Map.class));
    }

    private List<Map<String, String>> getGradleEntries(String currentDir, String userNameAndRepo) throws IOException {
        var map = getGradleEntriesMap(currentDir, userNameAndRepo, "master");

        if (map.containsKey("message") && map.get("message").equals("Not Found")) {
            map = getGradleEntriesMap(currentDir, userNameAndRepo, "main");
        }

        return cast(map.get("tree"));
    }

    private List<UrlPath> extraGradleUrls(List<Map<String, String>> entries) {
        return entries.stream()
                .map(Utils::<Map<String, String>>cast)
                .filter(x -> x.get("path").contains("gradle"))
                .filter(x -> x.get("type").equals("blob"))
                .map(x -> new UrlPath(x.get("path"), x.get("url")))
                .collect(Collectors.toList());
    }

    private void requestAndSaveUrlPaths(Iterable<UrlPath> paths, String currentDir) {
        for (UrlPath urlpath : paths) {
            try {
                var gradleApiContent = client.get(urlpath.url);
                var gradleFileContentBase64 = (String) mapper.readValue(gradleApiContent, Map.class).get("content");
                var fileContent = new String(Base64.decodeBase64(gradleFileContentBase64));
                mkdirs(currentDir + urlpath.path, true);
                Files.writeString(Path.of(currentDir + urlpath.path), fileContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void execute() throws IOException {
        var apps = extractApps();
        try (ProgressBar pb = new ProgressBarBuilder().setTaskName("Crawling data").setInitialMax(apps.size()).setStyle(ProgressBarStyle.ASCII).build()) {
            for (var app : apps) {
                pb.step();
                try {
                    var userNameAndRepo = app.get("sourceCode").substring("https://github.com/".length());
                    var currentDir = DATA_FOLDER + userNameAndRepo + "/";
                    pb.setExtraMessage(userNameAndRepo);
                    mkdirs(currentDir);

                    Files.writeString(Path.of(currentDir + "fdroid_meta_data.json"), mapper.writeValueAsString(app));

                    var entries = getGradleEntries(currentDir, userNameAndRepo);

                    var gradleUrls = extraGradleUrls(entries);
                    requestAndSaveUrlPaths(gradleUrls, currentDir);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class UrlPath {
        private final String path;
        private final String url;

        public UrlPath(String path, String url) {
            this.path = path;
            this.url = url;
        }
    }
}
