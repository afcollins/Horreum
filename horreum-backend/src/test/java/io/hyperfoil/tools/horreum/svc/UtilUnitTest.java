package io.hyperfoil.tools.horreum.svc;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.formatters.PatternFormatter;
import org.jboss.logmanager.handlers.OutputStreamHandler;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UtilUnitTest {

    public static class StringHandler extends OutputStreamHandler {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        public StringHandler() {
            setOutputStream(baos);
            this.setFormatter(new PatternFormatter("%m"));
            this.setLevel(Level.ALL);
            this.setAutoFlush(true);
            LogContext.getLogContext().getLogger("").addHandler(this);
        }

        public String getLog() {
            return baos.toString();
        }

        @Override
        public void close() throws SecurityException {
            LogContext.getLogContext().getLogger("").removeHandler(this);
            super.close();
            try {
                baos.close();
            } catch (IOException e) {
                /* meh */}
        }
    }

    @Test
    public void findJsonPath() throws UnsupportedEncodingException {
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        ObjectNode metrics = root.putObject("metrics");
        ArrayNode jobSummary = metrics.putArray("jobSummary");
        ArrayNode values = JsonNodeFactory.instance.arrayNode();
        ObjectNode valuesWrapper = JsonNodeFactory.instance.objectNode();
        valuesWrapper.put("values", values);
        jobSummary.add(valuesWrapper);

        ObjectNode gcSummary = JsonNodeFactory.instance.objectNode();
        String gcEnd = "2024-09-05T18:16:36.803661713Z";
        gcSummary.put("endTimestamp", gcEnd);
        String gcStart = "2024-09-05T18:14:42.798970958Z";
        gcSummary.put("timestamp", gcStart);
        values.add(gcSummary);
        ObjectNode gcJobConfig = gcSummary.putObject("jobConfig");
        gcJobConfig.put("name", "garbage-collection");

        ObjectNode cdv2Summary = JsonNodeFactory.instance.objectNode();
        String cdv2End = "2024-09-05T18:14:42.797624736Z";
        cdv2Summary.put("endTimestamp", cdv2End);
        String cdv2Start = "2024-09-05T17:49:02.915410546Z";
        cdv2Summary.put("timestamp", cdv2Start);
        values.add(cdv2Summary);
        ObjectNode cdv2JobConfig = cdv2Summary.putObject("jobConfig");
        cdv2JobConfig.put("name", "cluster-density-v2");

        assertEquals(Util.findJsonPath(root,
                "$.metrics.jobSummary[0].values[?(@.jobConfig.name != 'garbage-collection')].timestamp"), cdv2Start);
        assertEquals(Util.findJsonPath(root,
                "$.metrics.jobSummary[0].values[?(@.jobConfig.name != 'garbage-collection')].endTimestamp"), cdv2End);
    }

}
