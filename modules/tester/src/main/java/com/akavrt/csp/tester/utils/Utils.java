package com.akavrt.csp.tester.utils;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.metadata.ProblemMetadata;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * User: akavrt
 * Date: 11.04.13
 * Time: 14:30
 */
public class Utils {

    public static String extractProblemName(Problem problem, String path) {
        String problemName = null;
        ProblemMetadata metadata = problem.getMetadata();
        if (metadata != null) {
            problemName = metadata.getName();
        }

        if (com.akavrt.csp.utils.Utils.isEmpty(problemName)) {
            File problemFile = new File(path);
            problemName = FilenameUtils.removeExtension(problemFile.getName());
        }

        return problemName;
    }

}
