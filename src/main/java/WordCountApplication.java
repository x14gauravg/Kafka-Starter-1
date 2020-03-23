import com.gauravg.serde.AJsonSerializer;
import com.gauravg.serde.AppSerdes;
import com.gauravg.types.A;
import com.gauravg.types.C;
import jdk.nashorn.internal.runtime.WithObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;


import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public class WordCountApplication {

    private static String STATE_STORE_NAME = "Mystore123";

    public static void main(String arg[]){
//
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,1000);
//        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG,10*1024*1024L);
//
//
//        StreamsBuilder builder = new StreamsBuilder();
//        KStream<String, String> textTopic = builder.stream("TextTopic");
//        textTopic.foreach((k,v) -> System.out.println("Key= " + k + " Value= " + v));
//
//        textTopic.flatMapValues(text -> Arrays.asList(text.toLowerCase().split("\\W+")))
//                .foreach((k,v) -> System.out.println("Key= " + k + " Value= " + v));
//
//
//        KTable<String, Long> count = textTopic.flatMapValues(text -> Arrays.asList(text.toLowerCase().split("\\W+")))
//                .groupBy((key, value) -> value)
//                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
//
//        count.toStream().print(Printed.<String,Long>toSysOut().withLabel("KT1"));
//
//
//
//        count.toStream().to("CountWord123", Produced.with(Serdes.String(),Serdes.Long()));
//
//
//
//        // count.toStream().to("CountTopic");
//
//        KafkaStreams streams = new KafkaStreams(builder.build(),props);
//        streams.start();

        new WordCountApplication().tryTopics();

    }

    public void tryTopics(){

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "transform-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG,10*1024*1024L);


        produceValue();

        StreamsBuilder builder = new StreamsBuilder();


        KStream<String, A> aTopic = builder.stream("ATopic", Consumed.with(Serdes.String(), AppSerdes.A()));

        StoreBuilder aStore = Stores.keyValueStoreBuilder(Stores.inMemoryKeyValueStore(WordCountApplication.STATE_STORE_NAME), Serdes.String(), Serdes.Integer());
        System.out.println("STORE : "+aStore.name());

        builder.addStateStore(aStore);

  //      aTopic.foreach((k,v) -> System.out.println("Key :  " + v.getKey() + "   "+ k + ",  Value : "+ v.getValue()));

        KStream<String, C> cStream = aTopic.transformValues(() -> new ValueTransformer<A, C>() {

            private KeyValueStore<String,Integer> kvStore;

            @Override
            public void init(ProcessorContext processorContext) {
                System.out.println("INIT: "+ processorContext.applicationId());
                this.kvStore = (KeyValueStore<String, Integer>) processorContext.getStateStore(WordCountApplication.STATE_STORE_NAME);
                System.out.println("KV STORE : "+ kvStore);

            }

            @Override
            public C transform(A a) {
  //              System.out.println("Key :  " + a.getKey() + "   "+ ",  Value : "+ a.getValue());
                System.out.println(a);

                C c = new C();
                c.setKey(a.getKey());

                if(kvStore.get(a.getKey())==null){
                    kvStore.put(a.getKey(),a.getValue());
                    c.setTotal(a.getValue());
                }else {
                    int rev = kvStore.get(a.getKey()).intValue() + a.getValue();
                    kvStore.put(a.getKey(), rev);
                    c.setTotal(rev);
                }
                return c;
            }

            @Override
            public void close() {

            }
        }, WordCountApplication.STATE_STORE_NAME);

        cStream.to("CTopic",Produced.with(Serdes.String(),AppSerdes.C()));

       builder.stream("CTopic", Consumed.with(Serdes.String(), AppSerdes.C()))
               .foreach((k,v)-> System.out.println("Key= " + v.getKey() + " Value= " + v.getTotal()));




        KafkaStreams streams = new KafkaStreams(builder.build(),props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down");
            streams.close();
        }));

    }

    private void produceValue(){

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG,"Transform");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AJsonSerializer.class.getName());

        KafkaProducer<String,A> prod = new KafkaProducer(props);

        for(int i=0;i<100;++i) {


            A a = new A();
            Random rand = new Random();

            int randomNum = rand.nextInt((3 - 1) + 1) + 1;

            switch(randomNum) {
                case 1:
                    a.setKey("A");
                    break;

                case 2:
                    a.setKey("B");
                    break;

                case 3:
                    a.setKey("C");
                    break;

                default:
                    a.setKey("D");
            }

            a.setValue(rand.nextInt((1000-1)+1)+1);

            prod.send(new ProducerRecord<>("ATopic", a.getKey(), a));

        }

    }

}
