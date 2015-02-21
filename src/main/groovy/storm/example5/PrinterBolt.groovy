package storm.example5


import backtype.storm.task.OutputCollector
import backtype.storm.task.TopologyContext
import backtype.storm.topology.OutputFieldsDeclarer
import backtype.storm.topology.base.BaseRichBolt
import groovy.util.logging.Slf4j
import twitter4j.Status

@Slf4j
class PrinterBolt extends BaseRichBolt {
    OutputCollector outputCollector

    @Override
    void prepare(Map config, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector
    }

    @Override
    void execute(backtype.storm.tuple.Tuple input) {
        //log.debug input.fields
        final Status status = (Status) input.getValue(0);
        final String client = status.getSource();
        log.debug client
        outputCollector.ack(input)
    }

    @Override
    void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}