package storm.example3

import backtype.storm.spout.SpoutOutputCollector
import backtype.storm.task.TopologyContext
import backtype.storm.topology.base.BaseRichSpout
import backtype.storm.topology.OutputFieldsDeclarer
import backtype.storm.tuple.Fields
import backtype.storm.tuple.Values
import backtype.storm.utils.Utils

import groovy.transform.CompileStatic


@CompileStatic
class RandomSentenceSpout extends BaseRichSpout {

    SpoutOutputCollector collector

    Random rand

    final List sentences = [
            'the quick brown fox jumps over the lazy dog',
            'make everything as simple as possible but no simpler',
            'only two professions refer to customers as users',
            'groovy is the only reason to use the JVM',
            'but erlang is better than yer lang'
    ]

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {

        this.collector = collector
        this.rand = new Random()
    }

    @Override
    public void nextTuple() {

        // Limit to ~10 sentences/sec
        Utils.sleep(100)
        collector.emit(new Values(sentences[rand.nextInt(sentences.size())]))
    }

    @Override
    public void ack(Object id ) {
        // Nothing to do
    }

    @Override
    public void fail(Object id) {
        // Nothing to do
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declare(new Fields('sentence'))
    }

    public String getTopologyId() {
        return getClass().getName()
    }

}