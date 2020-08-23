//package org.apache.spark.metrics.source.bla;
//
//import com.codahale.metrics.Gauge;
//import com.codahale.metrics.MetricRegistry;
//import org.apache.spark.metrics.source.Source;
//import org.apache.spark.sql.streaming.StreamingQueryProgress;
//
//
//public class StreamingSource implements Source {
//    private final String appName;
//    private final MetricRegistry metricRegistry = new MetricRegistry();
//    private final Progress progress = new Progress();
//
//    public StreamingSource(String appName) {
//        this.appName = appName;
//        register("batchId", progress::getBatchId());
//        register("numInputRows", progress::getNumInputRows);
//        register("inputRowsPerSecond", progress::getInputRowsPerSecond);
//        register("processedRowsPerSecond", progress::getProcessedRowsPerSecond);
//    }
//
//    private void register(String name, Gauge metric) {
//        metricRegistry.register(MetricRegistry.name(name), metric);
//    }
//
//    @Override
//    public String sourceName() {
//        return String.format("%s.streaming", appName);
//    }
//
//
//    @Override
//    public MetricRegistry metricRegistry() {
//        return metricRegistry;
//    }
//
//    public void updateProgress(StreamingQueryProgress queryProgress) {
//        progress.setBatchId(queryProgress.batchId());
//        progress.setNumInputRows(queryProgress.numInputRows());
//        progress.setInputRowsPerSecond(queryProgress.inputRowsPerSecond());
//        progress.setProcessedRowsPerSecond(queryProgress.processedRowsPerSecond());
//    }
//
//    private static class Progress {
//
//        private long batchId = -1;
//        private long numInputRows = 0;
//        private double inputRowsPerSecond = 0;
//        private double processedRowsPerSecond = 0;
//
//        public long getBatchId() {
//            return batchId;
//        }
//
//        public void setBatchId(long batchId) {
//            this.batchId = batchId;
//        }
//
//        public long getNumInputRows() {
//            return numInputRows;
//        }
//
//        public void setNumInputRows(long numInputRows) {
//            this.numInputRows = numInputRows;
//        }
//
//        public double getInputRowsPerSecond() {
//            return inputRowsPerSecond;
//        }
//
//        public void setInputRowsPerSecond(double inputRowsPerSecond) {
//            this.inputRowsPerSecond = inputRowsPerSecond;
//        }
//
//        public double getProcessedRowsPerSecond() {
//            return processedRowsPerSecond;
//        }
//
//        public void setProcessedRowsPerSecond(double processedRowsPerSecond) {
//            this.processedRowsPerSecond = processedRowsPerSecond;
//        }
//    }
//}
