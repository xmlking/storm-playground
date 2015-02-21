package storm.example3

import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.StormSubmitter
import backtype.storm.generated.StormTopology
import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.Fields

import groovy.transform.CompileStatic


@CompileStatic
class WordCountTopology {

    public static void main(String[] args) {

        final RandomSentenceSpout sentenceSpout = new RandomSentenceSpout()
        final SplitSentenceBolt splitSentence = new SplitSentenceBolt()
        final WordCountBolt wordCounter = new WordCountBolt()
        final Fields byWord = new Fields('word')

        final String spoutId = sentenceSpout.getTopologyId()
        final String splitId = splitSentence.getTopologyId()
        final String countId = wordCounter.getTopologyId()

        final StormTopology topology = new TopologyBuilder().with {

            setSpout (spoutId, sentenceSpout, 2)
            setBolt  (splitId, splitSentence, 4).shuffleGrouping (spoutId)
            setBolt  (countId, wordCounter  , 6).fieldsGrouping  (splitId, byWord)

            createTopology()
        }

        final Config conf = new Config()
        conf.debug = true

        if (args) {
            StormSubmitter.submitTopology(args[0], conf, topology)

        } else {
            final LocalCluster cluster = new LocalCluster()
            cluster.submitTopology('word-count', conf, topology)
            Thread.sleep(10000)
            cluster.shutdown()
        }

    }

}