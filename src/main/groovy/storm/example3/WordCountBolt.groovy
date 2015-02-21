package storm.example3

import backtype.storm.Config
import backtype.storm.Constants
import backtype.storm.topology.BasicOutputCollector
import backtype.storm.topology.OutputFieldsDeclarer
import backtype.storm.topology.base.BaseBasicBolt
import backtype.storm.tuple.Fields
import backtype.storm.tuple.Tuple
import backtype.storm.tuple.Values

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Slf4j
@CompileStatic
class WordCountBolt extends BaseBasicBolt {

    Map<String, Long> counts = [:]

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {

        if (isTickTuple(tuple)) {
            log.info("Current counts: " + counts)

        } else {
            String word = tuple.getStringByField('word')
            Long count = counts[word] ?: 0
            counts[word] = ++count
            collector.emit(new Values(word, count))
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declare(new Fields('word', 'count'))
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {

        // Log the current counts every second
        Config conf = new Config()
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS as String, 1)
        return conf
    }

    private boolean isTickTuple(Tuple tuple) {

        boolean cond1 = Constants.SYSTEM_COMPONENT_ID.equals(tuple?.getSourceComponent())
        boolean cond2 = Constants.SYSTEM_TICK_STREAM_ID.equals(tuple?.getSourceStreamId())
        return cond1 && cond2
    }

    public String getTopologyId() {
        return getClass().getName()
    }

}