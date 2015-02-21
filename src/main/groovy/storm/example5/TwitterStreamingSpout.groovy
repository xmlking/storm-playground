package storm.example5

import backtype.storm.Config
import backtype.storm.spout.SpoutOutputCollector
import backtype.storm.task.TopologyContext
import backtype.storm.topology.OutputFieldsDeclarer
import backtype.storm.topology.base.BaseRichSpout
import backtype.storm.tuple.Fields
import backtype.storm.tuple.Values
import backtype.storm.utils.Utils;
import groovy.util.logging.Slf4j
import twitter4j.FilterQuery
import twitter4j.StallWarning
import twitter4j.Status
import twitter4j.StatusDeletionNotice
import twitter4j.StatusListener
import twitter4j.TwitterStream
import twitter4j.TwitterStreamFactory
import twitter4j.conf.ConfigurationBuilder

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

@Slf4j
class TwitterStreamingSpout extends BaseRichSpout {
    SpoutOutputCollector spoutOutputCollector
    TwitterStream stream
    BlockingQueue<Status> queue = new LinkedBlockingQueue<Status>(1000)

    public TwitterStreamingSpout(Config config) {
        // Tell Storm we want to use com.esotericsoftware.kryo.serializers.FieldSerializer
        // to serialize twitter4j Status objects
        config.registerSerialization(Status)
    }

    @Override
    void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("status"))
    }

    @Override
    void open(Map config, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.spoutOutputCollector = spoutOutputCollector
        TwitterStreamFactory fact = new TwitterStreamFactory(
                new ConfigurationBuilder()
                        .setUser((String)config[TWITTER_USERNAME_KEY])
                        .setPassword((String)config[TWITTER_PASSWORD_KEY]).build());

        String[] terms = ((String)config[TWITTER_FILTER_KEY]).split(",")

        stream = fact.getInstance()
        stream.addListener(new QueuingStatusListener())
        stream.filter(new FilterQuery().track(terms))
    }

    @Override
    public void close() {
        stream.shutdown();
    }

//    @Override
//    void nextTuple() {
//        spoutOutputCollector.emit(new Values(queue.take()))
//    }
    @Override
    public void nextTuple() {
        Status ret = queue.poll();
        if(ret==null) {
            Utils.sleep(50);
        } else {
            spoutOutputCollector.emit(new Values(ret));
        }
    }

    class QueuingStatusListener implements StatusListener {
        @Override
        void onStatus(Status status) {
            if (!queue.offer(status)) {
                log.warn("Queue is full, dropping status: ${status.text}")
            }
        }

        @Override
        void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            // do nothing
        }

        @Override
        void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            // do nothing
            log.warn "numberOfLimitedStatuses: ${numberOfLimitedStatuses}"
        }

        @Override
        void onScrubGeo(long userId, long upToStatusId) {
            // do nothing
        }

        @Override
        void onStallWarning(StallWarning warning) {
            // do nothing
            log.warn warning
        }

        @Override
        void onException(Exception ex) {
            // do nothing
            log.error ex
        }
    }

    static final String TWITTER_USERNAME_KEY = 'twitter.username'
    static final String TWITTER_PASSWORD_KEY = 'twitter.password'
    static final String TWITTER_FILTER_KEY = 'twitter.filterTerms'
}