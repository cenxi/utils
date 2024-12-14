package common.utils.maven;

import common.utils.JarUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.List;

/**
 * @author fengxi
 * @className MavenUtil
 * @description
 * @date 2025年02月28日 16:37
 */
@Slf4j
public class MavenUtil {

    //todo 给sh文件加可执行权限

    public static int run(String pom, List<String> goals) {
        return run(pom, goals, (s) -> {
            log.info(s);
        });
    }

    public static int run(String pom, List<String> goals,InvocationOutputHandler invocationOutputHandler) {
        String mvnHome = JarUtil.getJarDir() + File.separator + "apache-maven-3.8.6";
        String localRepository = mvnHome + File.separator + ".m2/repository";
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(mvnHome));
        invoker.setLocalRepositoryDirectory(new File(localRepository));
        invoker.setOutputHandler(invocationOutputHandler);

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pom));
        request.setGoals(goals);
        InvocationResult res = null;
        try {
            res = invoker.execute(request);
        } catch (MavenInvocationException e) {
            log.error("", e);
            return -1;
        }
        return res.getExitCode();
    }
}
