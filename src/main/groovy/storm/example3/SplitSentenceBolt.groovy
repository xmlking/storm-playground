package storm.example3

import backtype.storm.topology.BasicOutputCollector
import backtype.storm.topology.OutputFieldsDeclarer
import backtype.storm.topology.base.BaseBasicBolt
import backtype.storm.tuple.Fields
import backtype.storm.tuple.Tuple
import backtype.storm.tuple.Values

import groovy.transform.CompileStatic


@CompileStatic
class SplitSentenceBolt extends BaseBasicBolt {

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declare(new Fields('word'))
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {

        String sentence = tuple.getStringByField('sentence')
        sentence.split().each { String word ->
            collector.emit(new Values(word))
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {

        return null
    }

    public String getTopologyId() {
        return getClass().getName()
    }

}