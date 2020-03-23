kafka-console-consumer --bootstrap-server localhost:9092 --topic CountWord123 \
      --from-beginning --formatter kafka.tools.DefaultMessageFormatter \
      --property print.key=true --property print.value=true \
      --property key.deserialzer=org.apache.kafka.common.serialization.StringDeserializer \
      --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer